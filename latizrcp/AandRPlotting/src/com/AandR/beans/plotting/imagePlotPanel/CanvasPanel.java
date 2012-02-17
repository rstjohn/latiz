/**
 *  Copyright 2010 Latiz Technologies, LLC
 *
 *  This file is part of Latiz.
 *
 *  Latiz is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Latiz is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Latiz.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.AandR.beans.plotting.imagePlotPanel;

import com.AandR.beans.plotting.data.AbstractNavigatableData;
import com.AandR.beans.plotting.data.SimpleNavigatableData;
import com.AandR.beans.plotting.imagePlotPanel.colormap.AbstractColorMap;
import com.AandR.beans.plotting.imagePlotPanel.colormap.ColorMapChooser;
import com.AandR.beans.plotting.imagePlotPanel.colormap.ColorMapFactory;
import com.AandR.beans.plotting.imagePlotPanel.overlays.ArrowOverlay;
import com.AandR.beans.plotting.imagePlotPanel.overlays.BasicShape;
import com.AandR.beans.plotting.imagePlotPanel.overlays.BasicShapeEditor;
import com.AandR.beans.plotting.imagePlotPanel.overlays.EllipticalAnnulusShape;
import com.AandR.beans.plotting.imagePlotPanel.overlays.Overlay;
import com.AandR.beans.plotting.imagePlotPanel.overlays.OverlayPopup;
import com.AandR.beans.plotting.imagePlotPanel.overlays.TextBox;
import com.AandR.library.math.MoreMath;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ToolTipManager;
import net.miginfocom.swing.MigLayout;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.util.ImageUtilities;

/**
 * @author Dr. Richard St. John
 * @version $Revision: 1.11 $, $Date: 2007/10/09 17:39:22 $
 */
public class CanvasPanel extends ImagePanel implements ActionListener {

    public static final int RANGE_AUTO_SCALE = 0;
    public static final int RANGE_SERIES_RANGE = 1;
    public static final int RANGE_GLOBAL_RANGE = 2;
    public static final int RANGE_USER_RANGE = 3;
    public static final int INITIAL_SCALE_NONE = 0;
    public static final int INITIAL_SCALE_ONE_TO_ONE = 1;
    public static final int INITIAL_SCALE_WIDTH = 2;
    public static final int INITIAL_SCALE_HEIGHT = 3;
    private boolean isToolTipVisible;
    private boolean isLogPlot = false;
    private boolean isLogPlotRequested = false;
    private boolean isAutoScaled = true;
    private boolean isSeriesRangeComputed = false;
    private boolean isTableDataViewable = true;
    private double dataMin,  dataMax,  logDataMin,  logDataMax,  linDataMin,  linDataMax,  gridSpacingX,  gridSpacingY;
    private double seriesMin,  seriesMax;
    private double currentVerticalLocation = 0,  currentHorizontalLocation = 0,  currentRotation = 0;
    private double[][] data;
    private int minMaxSelection = RANGE_AUTO_SCALE;
    private AbstractColorMap colorMap = ColorMapFactory.createColorMap(ColorMapFactory.JET);
    private AbstractNavigatableData abstractNavigatableData;
    private ArrayList<CanvasPanelListener> observers;
    private BufferedImage linBuffer,  logBuffer;
    private CanvasListener canvasListener;
    private HashSet<Overlay> overlays;
    private JPopupMenu popupMenu;
    private String currentDirectory;

    public CanvasPanel(int canvasWidth, int canvasHeight) {
        super(canvasWidth, canvasHeight);
        isToolTipVisible = ToolTipManager.sharedInstance().isEnabled();
        gridSpacingX = gridSpacingY = 1;
        currentDirectory = System.getProperty("user.home");
        canvasListener = new CanvasListener();
        observers = new ArrayList<CanvasPanelListener>();
        setToolTipText("No File Loaded");
        addBindings();
        createPopupMenu();

        addMouseListener(canvasListener);
        addMouseMotionListener(canvasListener);
        addMouseWheelListener(canvasListener);
        addKeyListener(canvasListener);

        overlays = new HashSet<Overlay>();

        this.canvasWidth = canvasWidth;
        this.canvasHeight = canvasHeight;
        setMinimumSize(new Dimension(canvasWidth, canvasHeight));
    }

    private void createPopupMenu() {
        popupMenu = new JPopupMenu();
        popupMenu.add(createPopupMenuItem("Toggle Log Plot", null, KeyStroke.getKeyStroke(KeyEvent.VK_L, 0)));
        popupMenu.add(createPopupMenuItem("Slice Here", null, KeyStroke.getKeyStroke(KeyEvent.VK_C, 10)));
        popupMenu.addSeparator();

        JMenu navigateMenu = new JMenu("Navigate");
        navigateMenu.add(createPopupMenuItem("View First Frame", null, KeyStroke.getKeyStroke(KeyEvent.VK_F, 2)));
        navigateMenu.add(createPopupMenuItem("View Previous Frame", null, KeyStroke.getKeyStroke(KeyEvent.VK_P, 2)));
        navigateMenu.add(createPopupMenuItem("Choose Frame To View", null, KeyStroke.getKeyStroke(KeyEvent.VK_C, 2)));
        navigateMenu.add(createPopupMenuItem("View Next Frame", null, KeyStroke.getKeyStroke(KeyEvent.VK_N, 2)));
        navigateMenu.add(createPopupMenuItem("View Last Frame", null, KeyStroke.getKeyStroke(KeyEvent.VK_L, 2)));
        popupMenu.add(navigateMenu);
        popupMenu.addSeparator();

        popupMenu.add(createPopupMenuItem("Set Zoom Level", null, KeyStroke.getKeyStroke(KeyEvent.VK_Z, 2)));
        popupMenu.add(createPopupMenuItem("Set Min/Max", null, KeyStroke.getKeyStroke(KeyEvent.VK_R, 2)));
        popupMenu.add(createPopupMenuItem("Set Physical Extent", null, KeyStroke.getKeyStroke(KeyEvent.VK_E, 2)));
        popupMenu.addSeparator();
        popupMenu.add(createPopupMenuItem("Recenter on Viewport", null, KeyStroke.getKeyStroke(KeyEvent.VK_C, 0)));
        popupMenu.addSeparator();
        popupMenu.add(createPopupMenuItem("Set Colormap", null, KeyStroke.getKeyStroke(KeyEvent.VK_M, 2)));
        popupMenu.addSeparator();

        JMenu overlayMenu = new JMenu("Overlays");
        overlayMenu.add(createPopupMenuItem("Add Text Overlay", null, KeyStroke.getKeyStroke(KeyEvent.VK_T, 2)));
        overlayMenu.add(createPopupMenuItem("Add Shape Overlay", null, KeyStroke.getKeyStroke(KeyEvent.VK_O, 2)));
        overlayMenu.add(createPopupMenuItem("Add Annulus Overlay", null, KeyStroke.getKeyStroke(KeyEvent.VK_U, 2)));
        overlayMenu.add(createPopupMenuItem("Add Arrow Overlay", null, KeyStroke.getKeyStroke(KeyEvent.VK_A, 2)));
        popupMenu.add(overlayMenu);
        popupMenu.addSeparator();

        JMenu pngMenu = new JMenu("To PNG");
        pngMenu.add(createPopupMenuItem("Export Original Image", null, KeyStroke.getKeyStroke(KeyEvent.VK_S, 10)));
        pngMenu.add(createPopupMenuItem("Export Viewport Image", null, KeyStroke.getKeyStroke(KeyEvent.VK_S, 2)));
        pngMenu.addSeparator();
        pngMenu.add(createPopupMenuItem("Export Viewport Series", null, KeyStroke.getKeyStroke(KeyEvent.VK_S, 8)));

        JMenu pdfMenu = new JMenu("To PDF");
        pdfMenu.add(createPopupMenuItem("Export Original Image", null, KeyStroke.getKeyStroke(KeyEvent.VK_P, KeyEvent.SHIFT_DOWN_MASK | KeyEvent.CTRL_DOWN_MASK)));
        pdfMenu.add(createPopupMenuItem("Export Viewport Image", null, KeyStroke.getKeyStroke(KeyEvent.VK_P, KeyEvent.ALT_DOWN_MASK | KeyEvent.CTRL_DOWN_MASK)));

        JMenu exportMenu = new JMenu("Export");
        exportMenu.add(pngMenu);
        exportMenu.add(pdfMenu);
        popupMenu.add(exportMenu);
        popupMenu.addSeparator();

        JMenu losslessMenu = new JMenu("Lossless Modifications");
        losslessMenu.add(createPopupMenuItem("Flip Horizontally", null, KeyStroke.getKeyStroke(KeyEvent.VK_H, 10)));
        losslessMenu.add(createPopupMenuItem("Flip Vertically", null, KeyStroke.getKeyStroke(KeyEvent.VK_V, 10)));
        losslessMenu.addSeparator();
        losslessMenu.add(createPopupMenuItem("Rotate +90", null, KeyStroke.getKeyStroke(KeyEvent.VK_R, 10)));
        losslessMenu.add(createPopupMenuItem("Rotate -90", null, KeyStroke.getKeyStroke(KeyEvent.VK_L, 10)));
        popupMenu.add(losslessMenu);
    }

    /**
     * Access to the method used to create the popup menu items used in the Canvas Popup Menu.
     * @param label
     * @param icon
     * @param keyStroke
     * @return
     */
    public JMenuItem createPopupMenuItem(String label, Icon icon, KeyStroke keyStroke) {
        JMenuItem menuItem = new JMenuItem(label, icon);
        menuItem.setAccelerator(keyStroke);
        menuItem.addActionListener(this);
        menuItem.setBackground(Color.WHITE);
        menuItem.setEnabled(true);
        return menuItem;
    }

    /**
     *
     */
    public void actionPerformed(ActionEvent e) {
        JMenuItem item = (JMenuItem) e.getSource();
        int modifiers = item.getAccelerator().getModifiers();
        int keyCode = item.getAccelerator().getKeyCode();
        dispatchEvent(new KeyEvent(this, KeyEvent.KEY_PRESSED, 0, modifiers, keyCode, KeyEvent.CHAR_UNDEFINED));
    }

    /**
     *
     */
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        setBackground(Color.GRAY);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        if (bufferedImage != null) {
            scaleGraphicsContext(g2);
            g2.drawImage(bufferedImage, 0, 0, null);
            drawTextBoxes(g2);
        }
    }

    public void setColorMap(AbstractColorMap colorMap) {
        this.colorMap = colorMap;
        canvasListener.updateCanvasImage(data);
    }

    public AbstractColorMap getColorMap() {
        return colorMap;
    }

    /**
     *
     */
    @Override
    public void scaleGraphicsContext(Graphics2D g2) throws IllegalArgumentException {
        if (currentScaleFactor > 0.0f) {
            g2.scale(currentScaleFactor, currentScaleFactor);
            g2.translate(currentTranslationX, currentTranslationY);
        } else {
            throw new IllegalArgumentException("An attempt was made to scale with a height or width value of 0");
        }
    }

    private void drawTextBoxes(Graphics2D g2) {
        Overlay thisOverlay;
        Iterator<Overlay> thisBox = overlays.iterator();
        while (thisBox.hasNext()) {
            thisOverlay = thisBox.next();
            g2.setFont(thisOverlay.getFont());
            g2.setColor(thisOverlay.getShapeColor());
            thisOverlay.paintItem(g2);
        }
    }

    /**
     *  Add action bindings
     *
     */
    private void addBindings() {

//  Bind First Frame    
        getActionMap().put("FirstFrame", new AbstractAction("FirstFrame") {

            public void actionPerformed(ActionEvent evt) {
                canvasListener.actionPlotFirstFrame();
            }
        });
        getInputMap().put(KeyStroke.getKeyStroke("control F"), "FirstFrame");

//  Bind Next Frame    
        getActionMap().put("NextFrame", new AbstractAction("NextFrame") {

            public void actionPerformed(ActionEvent evt) {
                canvasListener.actionPlotNextFrame();
            }
        });
        getInputMap().put(KeyStroke.getKeyStroke("control N"), "NextFrame");

//  Bind Choose Frame    
        getActionMap().put("ChooseFrame", new AbstractAction("ChooseFrame") {

            public void actionPerformed(ActionEvent evt) {
                canvasListener.actionChooseFrame();
            }
        });
        getInputMap().put(KeyStroke.getKeyStroke("control C"), "ChooseFrame");

//  Bind Previous Frame    
        getActionMap().put("PreviousFrame", new AbstractAction("PreviousFrame") {

            public void actionPerformed(ActionEvent evt) {
                canvasListener.actionPlotPreviousFrame();
            }
        });
        getInputMap().put(KeyStroke.getKeyStroke("control P"), "PreviousFrame");

//  Bind Last Frame    
        getActionMap().put("LastFrame", new AbstractAction("LastFrame") {

            public void actionPerformed(ActionEvent evt) {
                canvasListener.actionPlotLastFrame();
            }
        });
        getInputMap().put(KeyStroke.getKeyStroke("control L"), "LastFrame");

//  Bind Toggle Log Plot    
        getActionMap().put("LogPlot", new AbstractAction("LogPlot") {

            public void actionPerformed(ActionEvent evt) {
                canvasListener.actionToggleLogPlot();
            }
        });
        getInputMap().put(KeyStroke.getKeyStroke("L"), "LogPlot");

//  Bind SLice Plot
        getActionMap().put("SlicePlot", new AbstractAction("SlicePlot") {

            public void actionPerformed(ActionEvent evt) {
                canvasListener.actionSlicePlot();
            }
        });
        getInputMap().put(KeyStroke.getKeyStroke("control alt C"), "SlicePlot");


//  Bind Recenter On Viewport     
        getActionMap().put("RecenterOnViewport", new AbstractAction("RecenterOnViewport") {

            public void actionPerformed(ActionEvent evt) {
                canvasListener.actionCenterOnViewport();
            }
        });
        getInputMap().put(KeyStroke.getKeyStroke("C"), "RecenterOnViewport");

//  Bind Flip Horizontally     
        getActionMap().put("FlipHorz", new AbstractAction("FlipHorz") {

            public void actionPerformed(ActionEvent evt) {
                canvasListener.actionFlipHorizontally();
            }
        });
        getInputMap().put(KeyStroke.getKeyStroke("control alt H"), "FlipHorz");

//  Bind Flip Vertically     
        getActionMap().put("FlipVert", new AbstractAction("FlipVert") {

            public void actionPerformed(ActionEvent evt) {
                canvasListener.actionFlipVertically();
            }
        });
        getInputMap().put(KeyStroke.getKeyStroke("control alt V"), "FlipVert");

//  Bind Rotate Right     
        getActionMap().put("RotateRight", new AbstractAction("RotateRight") {

            public void actionPerformed(ActionEvent evt) {
                canvasListener.actionRotate(90.0f);
            }
        });
        getInputMap().put(KeyStroke.getKeyStroke("control alt R"), "RotateRight");

//  Bind Rotate Left     
        getActionMap().put("RotateLeft", new AbstractAction("RotateLeft") {

            public void actionPerformed(ActionEvent evt) {
                canvasListener.actionRotate(-90.0f);
            }
        });
        getInputMap().put(KeyStroke.getKeyStroke("control alt L"), "RotateLeft");

//  Bind Export Viewport     
        getActionMap().put("ExportViewport", new AbstractAction("ExportViewport") {

            public void actionPerformed(ActionEvent evt) {
                canvasListener.actionSaveImageFile(true);
            }
        });
        getInputMap().put(KeyStroke.getKeyStroke("control S"), "ExportViewport");

//  Bind Export Original     
        getActionMap().put("ExportOriginal", new AbstractAction("ExportOriginal") {

            public void actionPerformed(ActionEvent evt) {
                canvasListener.actionSaveImageFile(false);
            }
        });
        getInputMap().put(KeyStroke.getKeyStroke("control alt S"), "ExportOriginal");

//  Bind Zoom Dialog     
        getActionMap().put("ZoomDialog", new AbstractAction("ZoomDialog") {

            public void actionPerformed(ActionEvent evt) {
                canvasListener.actionShowZoomDialog();
            }
        });
        getInputMap().put(KeyStroke.getKeyStroke("control Z"), "ZoomDialog");

//  Bind export image series
        getActionMap().put("ExportSeries", new AbstractAction("ExportSeries") {

            public void actionPerformed(ActionEvent e) {
                canvasListener.actionExportSeries();
            }
        });
        getInputMap().put(KeyStroke.getKeyStroke("alt S"), "ExportSeries");


//  Bind export image to pdf
        getActionMap().put("ExportToPDF", new AbstractAction("ExportToPDF") {

            public void actionPerformed(ActionEvent e) {
                canvasListener.actionExportToPDF(false);
            }
        });
        getInputMap().put(KeyStroke.getKeyStroke("control shift P"), "ExportToPDF");


//  Bind export image to pdf
        getActionMap().put("ExportToPDF2", new AbstractAction("ExportToPDF2") {

            public void actionPerformed(ActionEvent e) {
                canvasListener.actionExportToPDF(true);
            }
        });
        getInputMap().put(KeyStroke.getKeyStroke("control alt P"), "ExportToPDF2");


//  Bind Range Dialog     
        getActionMap().put("RangeDialog", new AbstractAction("RangeDialog") {

            public void actionPerformed(ActionEvent evt) {
                canvasListener.actionShowRangeDialog();
            }
        });
        getInputMap().put(KeyStroke.getKeyStroke("control R"), "RangeDialog");

//  Bind View Extend Dialog     
        getActionMap().put("PhysicalExtentDialog", new AbstractAction("PhysicalExtentDialog") {

            public void actionPerformed(ActionEvent evt) {
                canvasListener.actionShowPhysicalDialog();
            }
        });
        getInputMap().put(KeyStroke.getKeyStroke("control E"), "PhysicalExtentDialog");

//  Bind Add Basic Shape Overlay     
        getActionMap().put("AddBasicShape", new AbstractAction("AddBasicShape") {

            public void actionPerformed(ActionEvent evt) {
                canvasListener.actionAddShapeOverlay();
            }
        });
        getInputMap().put(KeyStroke.getKeyStroke("control O"), "AddBasicShape");

//  Bind Add Basic Shape Overlay     
        getActionMap().put("AddAnnulusShape", new AbstractAction("AddAnnulusShape") {

            public void actionPerformed(ActionEvent evt) {
                canvasListener.actionAddAnnulusOverlay();
            }
        });
        getInputMap().put(KeyStroke.getKeyStroke("control U"), "AddAnnulusShape");

//  Bind Add Text Overlay     
        getActionMap().put("AddTextOverlay", new AbstractAction("AddTextOverlay") {

            public void actionPerformed(ActionEvent evt) {
                canvasListener.actionAddTextOverlay();
            }
        });
        getInputMap().put(KeyStroke.getKeyStroke("control T"), "AddTextOverlay");

//  Bind Arrow Overlay     
        getActionMap().put("AddArrowOverlay", new AbstractAction("AddArrowOverlay") {

            public void actionPerformed(ActionEvent evt) {
                canvasListener.actionAddArrowOverlay();
            }
        });
        getInputMap().put(KeyStroke.getKeyStroke("control A"), "AddArrowOverlay");

//  Bind Colormap Chooser
        getActionMap().put("ColorMapChooser", new AbstractAction("ColorMapChooser") {

            public void actionPerformed(ActionEvent e) {
                canvasListener.actionColormapChooser();
            }
        });
        getInputMap().put(KeyStroke.getKeyStroke("control M"), "ColorMapChooser");

//  Bind Refresh Action    
        getActionMap().put("ReloadFile", new AbstractAction("ReloadFile") {

            public void actionPerformed(ActionEvent e) {
                canvasListener.actionReloadFile();
            }
        });
        getInputMap().put(KeyStroke.getKeyStroke("F5"), "ReloadFile");
    }

    public boolean setNavigatableData(AbstractNavigatableData navigatableData) {
        this.abstractNavigatableData = navigatableData;
        canvasListener.updateCanvasData(data = abstractNavigatableData.getFirstDataset());
        if (data == null) {
            return false;
        }

        nodifyNewDataLoaded();
        return true;
    }

    public void clearCanvas() {
        setNavigatableData(new SimpleNavigatableData(new double[][]{{0}}));
    }

    public AbstractNavigatableData getNavigatableData() {
        return abstractNavigatableData;
    }

    public void plot(int index) {
        canvasListener.updateCanvasData(abstractNavigatableData.getDatasetAt(index));
    }

    public void plotNext() {
        canvasListener.actionPlotNextFrame();
    }

    /**
     *
     * @param dataLocal
     * @param isAutoScaled
     * @return
     */
    public BufferedImage convertDataToImage(double[][] data, double dataMin, double dataMax) {
        BufferedImage image = new BufferedImage(data.length, data[0].length, BufferedImage.TYPE_INT_RGB);
        WritableRaster raster = image.getRaster();

        double thisValue;
        int[] thisRowOfPixels = new int[3 * data.length];
        int[] thisPixelValue = new int[3];
        int i, j;
        double scale = 255.0 / (dataMax - dataMin);
        for (j = 0; j < data[0].length; j++) {
            for (i = 0; i < data.length; i++) {
                thisValue = scale * (data[i][j] - dataMin);
                //if(thisValue<0.0)
                //  thisValue = 0.0;
                //else if(thisValue>255.0)
                //  thisValue = 255.0;

                thisPixelValue = colorMap.getColorValue((int) thisValue);
                thisRowOfPixels[3 * i + 0] = thisPixelValue[0];
                thisRowOfPixels[3 * i + 1] = thisPixelValue[1];
                thisRowOfPixels[3 * i + 2] = thisPixelValue[2];
            }
            raster.setPixels(0, j, data.length, 1, thisRowOfPixels);
        }
        return image;
    }

    /**
     *
     * @param dataLocal
     * @param isAutoScaled
     * @return
    public BufferedImage convertDataToImage(float[][] dataLocal, boolean isAutoScaled) {

    if(isAutoScaled) {
    float[] range = MoreMath.minMax(dataLocal);
    dataMin = range[0]; dataMax = range[1];
    }

    BufferedImage image = new BufferedImage(dataLocal.length, dataLocal[0].length, BufferedImage.TYPE_INT_RGB);
    WritableRaster raster = image.getRaster();

    double thisValue;
    int i, j;
    double scale = 255.0 / (dataMax - dataMin);
    for(j=0; j<dataLocal[0].length; j++) {
    for(i=0; i<dataLocal.length; i++) {
    thisValue = scale*(dataLocal[i][j]-dataMin);
    if(thisValue<0.0)
    thisValue = 0.0;
    else if(thisValue>255.0f)
    thisValue = 255.0f;
    raster.setPixel(i, j, colorMap.getColorValue((int) thisValue));
    }
    }
    return image;
    }
     */
    public BufferedImage convertDataToImage(double[][] data) {
        double min = data[0][0];
        double max = data[0][0];
        for (int j = 0; j < data[0].length; j++) {
            for (int i = 0; i < data.length; i++) {
                min = data[i][j] < min ? data[i][j] : min;
                max = data[i][j] > max ? data[i][j] : max;
            }
        }
        dataMin = min;
        dataMax = max;
        return convertDataToImage(data, min, max);
    }

    private final double[] computeMinMax(double[][] data) {
        double[] minMax = new double[]{Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY};
        for (int j = 0; j < data[0].length; j++) {
            for (int i = 0; i < data.length; i++) {
                minMax[0] = data[i][j] < minMax[0] ? data[i][j] : minMax[0];
                minMax[1] = data[i][j] > minMax[1] ? data[i][j] : minMax[1];
            }
        }
        return minMax;
    }

    /*
     */
    public double[] computeSeriesMinMax(int stepSize) {
        double[][] dataLocal = abstractNavigatableData.getFirstDataset();
        double[] minMax = computeMinMax(dataLocal);
        double[] thisMinMax = isLogPlot ? new double[]{Math.log(minMax[0]), Math.log(minMax[1])} : minMax;
        if (thisMinMax[0] < minMax[0]) {
            minMax[0] = thisMinMax[0];
        }
        if (thisMinMax[1] > minMax[1]) {
            minMax[1] = thisMinMax[1];
        }

        int currentSkip = 1;
        while (abstractNavigatableData.hasNext()) {
            if (currentSkip < stepSize) {
                currentSkip++;
                continue;
            }
            dataLocal = abstractNavigatableData.getNextDataset();
            minMax = computeMinMax(dataLocal);
            thisMinMax = isLogPlot ? new double[]{Math.log(minMax[0]), Math.log(minMax[1])} : minMax;
            currentSkip = 1;
        }
        seriesMin = minMax[0];
        seriesMax = minMax[1];
        return minMax;
    }

    /**
     * Computes the maximum and minimum values from a dataLocal series.
     * @return
     * @throws LargeArrayException
     * @throws IOException
     */
    public double[] computeSeriesMinMax() {
        return computeSeriesMinMax(1);
    }

    /**
     * Returns the dataLocal minimum.
     * @return
     */
    public double getDataMin() {
        return isLogPlot ? logDataMin : linDataMin;
    }

    /**
     * Returns the maximum dataLocal value.
     * @return
     */
    public double getDataMax() {
        return isLogPlot ? logDataMax : linDataMax;
    }

    /**
     * Set the min and max dataLocal values.
     * @param dataMin
     * @param dataMax
     */
    public void setDataRange(double dataMin, double dataMax) {
        if (isLogPlot) {
            this.logDataMin = dataMin;
            this.logDataMax = dataMax;

            this.linDataMin = Math.exp(dataMin);
            this.linDataMax = Math.exp(dataMax);
        } else {
            this.linDataMin = dataMin;
            this.linDataMax = dataMax;

            this.logDataMin = Math.log(dataMin);
            this.logDataMax = Math.log(dataMax);
        }
    }

    /**
     * Returns the x-grid point spacing of the currently loaded dataLocal file.
     * @return
     */
    public double getGridSpacingX() {
        return gridSpacingX;
    }

    /**
     * Returns the y-grid point spacing of the currently loaded dataLocal file.
     * @return
     */
    public double getGridSpacingY() {
        return gridSpacingY;
    }

    /**
     * Sets the grid point spacing for the currently loaded dataLocal file.
     * @param dx
     * @param dy
     */
    public void setGridSpacing(double dx, double dy) {
        this.gridSpacingX = dx;
        this.gridSpacingY = dy;
    }

    /**
     *
     * @param logBuffer
     */
    public void setLogBuffer(BufferedImage logBuffer) {
        this.logBuffer = logBuffer;
    }

    /**
     *
     * @param linBuffer
     */
    public void setLinBuffer(BufferedImage linBuffer) {
        this.linBuffer = linBuffer;
    }

    /**
     * Get the currenlty loaded dataLocal.
     * @return
     */
    public double[][] getData() {
        return data;
    }

    /**
     *
     * @return
     */
    public double getSeriesMax() {
        return seriesMax;
    }

    /**
     *
     * @return
     */
    public double getSeriesMin() {
        return seriesMin;
    }

    /**
     *
     * @return
     */
    public boolean isSeriesRangeComputed() {
        return isSeriesRangeComputed;
    }

    public boolean isShowToolTip() {
        return canvasListener.showToolTip;
    }

    public void setShowToolTip(boolean showToolTip) {
        canvasListener.showToolTip = showToolTip;
    }

    /**
     *
     * @param isSeriesRangeComputed
     */
    public void setSeriesRangeComputed(boolean isSeriesRangeComputed) {
        this.isSeriesRangeComputed = isSeriesRangeComputed;
    }

    /**
     *
     * @return
     */
    public JPopupMenu getPopupMenu() {
        return popupMenu;
    }

    public void addCanvasPanelInterface(CanvasPanelListener canvasPanelListener) {
        observers.add(canvasPanelListener);
    }

    public void removeCanvasPanelInterface(CanvasPanelListener canvasPanelListener) {
        int index = observers.indexOf(canvasPanelListener);
        if (index > -1) {
            observers.remove(index);
        }
    }

    private void notifyBufferChanged() {
        for (int i = 0; i < observers.size(); i++) {
            observers.get(i).bufferChanged();
        }
    }

    private void nodifyNewDataLoaded() {
        for (int i = 0; i < observers.size(); i++) {
            observers.get(i).newDataLoaded();
        }
    }

    private void nodifyDataChanged() {
        for (int i = 0; i < observers.size(); i++) {
            observers.get(i).dataChanged();
        }
    }

    private void nodifyMouseDragged() {
        for (int i = 0; i < observers.size(); i++) {
            observers.get(i).mouseDragged();
        }
    }

    private void nodifyMouseMoved() {
        for (int i = 0; i < observers.size(); i++) {
            observers.get(i).mouseMoved();
        }
    }

    /**
     * Determines whether the current buffer is a log plot.
     * @return
     */
    public boolean isLogPlot() {
        return isLogPlot;
    }

    /**
     * Returns the current linear buffer.
     * @return
     */
    public BufferedImage getLinBuffer() {
        return linBuffer;
    }

    /**
     * Returns the buffer of the log of the currently loaded dataLocal.
     * @return
     */
    public BufferedImage getLogBuffer() {
        return logBuffer;
    }

    /**
     * Returns the x-index of the dataLocal that the mouse is currently over.
     * @return
     */
    public int getSelectedXValue() {
        return canvasListener.selectedX;
    }

    /**
     * Returns the y-index of the dataLocal that the mouse is currently over.
     * @return
     */
    public int getSelectedYValue() {
        return canvasListener.selectedY;
    }

    public void setInitialScaleFactor(int flag) {
        canvasListener.setInitialScaleFactor(flag);
    }

    /**
     *
     * @return
     */
    public int getMinMaxType() {
        return canvasListener.getMinMaxSelection();
    }

    /**
     *
     * @return
     */
    public boolean isAutoScaled() {
        return isAutoScaled;
    }

    /**
     *
     * @param isAutoScaled
     */
    public void setAutoScaled(boolean isAutoScaled) {
        this.isAutoScaled = isAutoScaled;
    }

    public int getPanelWidth() {
        return getWidth();
    }

    public int[] getGridSize() {
        return new int[] {data.length, data[0].length};
    }

    public double[] getPhysicalExtent() {
        return new double[] {data.length*getGridSpacingX(), data[0].length*getGridSpacingY()};
    }

    public int getPanelHeight() {
        return getHeight();
    }

    public double getCurrentHorizontalLocation() {
        return currentHorizontalLocation;
    }

    public double getCurrentVerticalLocation() {
        return currentVerticalLocation;
    }

    public double getCurrentRotation() {
        return currentRotation;
    }

    public void setCurrentRotation(double currentRotation) {
        this.currentRotation = currentRotation;
    }

    public void setCurrentHorizontalLocation(double currentHorizontalLocation) {
        this.currentHorizontalLocation = currentHorizontalLocation;
    }

    public void setCurrentVerticalLocation(double currentVerticalLocation) {
        this.currentVerticalLocation = currentVerticalLocation;
    }

    public boolean isTableDataViewable() {
        return isTableDataViewable;
    }

    public void setTableDataViewable(boolean isTableDataViewable) {
        this.isTableDataViewable = isTableDataViewable;
    }

    /**
     *
     * @author Dr. Richard St. John
     * @version $Revision: 1.11 $, $Date: 2007/10/09 17:39:22 $
     */
    private class CanvasListener implements MouseListener, MouseMotionListener, MouseWheelListener, KeyListener {

        private boolean showToolTip = false,  isHorizontalFlip = false,  isVerticalFlip = false;
        private double selectedZ;
        private int startFromX = 0,  startFromY = 0,  dragFromX = 0,  dragFromY = 0;
        private int selectedX,  selectedY;
        private Overlay selectedOverlay;
        private OverlayPopup overlayPopupMenu;
        private Point clickPoint = new Point(0, 0);

        private CanvasListener() {
            overlayPopupMenu = new OverlayPopup(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    String command = e.getActionCommand();
                    if (command.equalsIgnoreCase(OverlayPopup.ACTION_REMOVE_OVERLAY)) {
                        actionRemoveOverlay();
                    } else if (command.equalsIgnoreCase(OverlayPopup.ACTION_EDIT_OVERLAY)) {
                        selectedOverlay.fireOverlayListener();
                        if (!BasicShapeEditor.getInstanceOf().isCancelled()) {
                            repaint();
                        }

                    } else if (command.equalsIgnoreCase(OverlayPopup.ACTION_CENTER_OVERLAY)) {
                        actionCenterOverlay();
                    }
                }
            });
        }

        private void actionSlicePlot() {
            XYSeries horizSlice = new XYSeries("Horiontal Slice");
            double len = data.length;
            for (int i = 0; i < len; i++) {
                horizSlice.add((i - len / 2) * gridSpacingX, isLogPlot ? Math.log(data[i][selectedY]) : data[i][selectedY]);
            }

            XYSeries vertSlice = new XYSeries("Vertical Slice");
            len = data[0].length;
            for (int i = 0; i < len; i++) {
                vertSlice.add((i - len / 2) * gridSpacingY, isLogPlot ? Math.log(data[selectedX][i]) : data[selectedX][i]);
            }

            XYSeriesCollection seriesCollection = new XYSeriesCollection();
            seriesCollection.addSeries(horizSlice);
            seriesCollection.addSeries(vertSlice);
            JFreeChart slicePlot = ChartFactory.createXYLineChart(
                    "Sliced at (" + selectedX + ", " + selectedY + ")",
                    "distance (m)",
                    "Slice",
                    seriesCollection,
                    PlotOrientation.VERTICAL,
                    true,
                    true,
                    true);
                    
            DialogDescriptor dd = new DialogDescriptor(new ChartPanel(slicePlot), "Slice Plot");
            DialogDisplayer.getDefault().createDialog(dd).setVisible(true);
        }

        private void actionReloadFile() {
            canvasListener.updateCanvasData(data);
        }

        private void actionFlipHorizontally() {
            if (bufferedImage == null) {
                return;
            }
            isHorizontalFlip = !isHorizontalFlip;
            flipImageHorizontally();
            notifyBufferChanged();
        }

        private void actionFlipVertically() {
            if (bufferedImage == null) {
                return;
            }
            isVerticalFlip = !isVerticalFlip;
            flipImageVertically();
            notifyBufferChanged();
        }

        private void actionPlotFirstFrame() {
            if (bufferedImage == null || abstractNavigatableData == null) {
                return;
            }
            updateCanvasData(data = abstractNavigatableData.getFirstDataset());
        }

        private void actionPlotNextFrame() {
            if (bufferedImage == null || abstractNavigatableData == null) {
                return;
            }
            updateCanvasData(data = abstractNavigatableData.getNextDataset());
        }

        private void actionPlotPreviousFrame() {
            if (bufferedImage == null || abstractNavigatableData == null) {
                return;
            }
            updateCanvasData(data = abstractNavigatableData.getPreviousDataset());
        }

        private void actionPlotLastFrame() {
            if (bufferedImage == null || abstractNavigatableData == null) {
                return;
            }
            updateCanvasData(data = abstractNavigatableData.getLastDataset());
        }

        private void actionChooseFrame() {
            if (bufferedImage == null || abstractNavigatableData == null) {
                return;
            }
            int currentIndex = abstractNavigatableData.getCurrentIndex();
            String ans = JOptionPane.showInputDialog("Specify Frame Number To Plot", String.valueOf(currentIndex + 1));
            if (ans == null) {
                return;
            }
            try {
                int index = Integer.parseInt(ans);
                updateCanvasData(data = abstractNavigatableData.getDatasetAt(index));
            } catch (NumberFormatException e) {
                canvasListener.updateCanvasData(data);
            }
        }

        private void actionExportSeries() {
            if (bufferedImage == null) {
                return;
            }

//    Compute Min / Max      
            setAutoScaled(false);
            if (getMinMaxSelection() == RANGE_AUTO_SCALE) {
                setAutoScaled(true);
            } else if (getMinMaxSelection() == RANGE_SERIES_RANGE || getMinMaxSelection() == RANGE_GLOBAL_RANGE) {
                try {
                    //computeSeriesMinMax();
                    setDataRange(getSeriesMin(), getSeriesMax());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

//    Loop through files.
            File thisFile;
            while (abstractNavigatableData.hasNext()) {
                thisFile = new File(abstractNavigatableData.getName() + ".png");
                updateCanvasData(data = abstractNavigatableData.getNextDataset());
                actionSaveImageFile(thisFile, true);
            }
        }

        private void actionExportToPDF(boolean isViewportSizeUsed) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            Rectangle pageSize = null;
            try {
                if (isViewportSizeUsed) {
                    BufferedImage image = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
                    Graphics2D g2 = image.createGraphics();
                    paint(g2);
                    g2.dispose();
                    ImageIO.write(image, "png", baos);
                    pageSize = new Rectangle(image.getWidth(), image.getHeight());
                } else {
                    ImageIO.write(bufferedImage, "png", baos);
                    pageSize = new Rectangle(bufferedImage.getWidth(), bufferedImage.getHeight());
                }
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }

            File outputFile = new File(abstractNavigatableData.getName() + ".pdf");
//
//            // step 1: creation of a document-object
//            Document document = new Document(pageSize, 0f, 0f, 0f, 0f);
//            document.addAuthor(System.getProperty("user.name"));
//
//            // step 2:
//            // we create a writer that listens to the document
//            // and directs a PDF-stream to a file
//            try {
//                PdfWriter.getInstance(document, new FileOutputStream(outputFile));
//
//                // step 3: we open the document
//                document.open();
//
//                // step 4:
//                Image jpg = Image.getInstance(baos.toByteArray());
//                //document.newPage();
//                document.add(jpg);
//
//                // step 5: we close the document
//                document.close();
//
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//
//            OptionsDialog dialog = new OptionsDialog(CanvasPanel.this, "Export Finished", OptionsDialog.INFORMATION_ICON);
//            String filename = outputFile.getName();
//            String parent = outputFile.getParent();
//            dialog.showDialog("<HTML><center><I>" + filename + "</I><BR><BR>was successfully saved to<BR><BR><I>" + parent + "</I></center></HTML>", 0);
        }

        private void actionToggleLogPlot() {
            if (bufferedImage == null) {
                return;
            }
            isLogPlotRequested = true;

            isLogPlot = !isLogPlot;
            if (isLogPlot) {
                if (logBuffer == null) {
                    createLogBuffer(data);
                }
                setBufferedImage(logBuffer);
            } else {
                setBufferedImage(linBuffer);
            }
            nodifyDataChanged();
        }

        private void actionCenterOnViewport() {
            if (bufferedImage == null) {
                return;
            }
            int bufferWidth = (int) (bufferedImage.getWidth() * currentScaleFactor);
            int bufferHeight = (int) (bufferedImage.getHeight() * currentScaleFactor);

            currentHorizontalLocation = (int) ((getPanelWidth() - bufferWidth) / (2.0f * currentScaleFactor));
            currentVerticalLocation = (int) ((getPanelHeight() - bufferHeight) / (2.0f * currentScaleFactor));
            startFromX = (int) currentHorizontalLocation;
            startFromY = (int) currentVerticalLocation;

            transform(currentScaleFactor, (int) currentHorizontalLocation, (int) currentVerticalLocation);
            notifyBufferChanged();
        }

        private void actionRotate(double angle) {
            if (bufferedImage == null) {
                return;
            }
            currentRotation += angle;
            rotateImage(angle);
            notifyBufferChanged();
        }

        private void actionShowPhysicalDialog() {
            if (bufferedImage == null) {
                return;
            }
            new PhysicalExtentDialog();
        }

        private void actionShowRangeDialog() {
            if (bufferedImage == null) {
                return;
            }
            new RangeDialog();
        }

        private void actionAddTextOverlay() {
            overlays.add(new TextBox("New String", clickPoint.x, clickPoint.y));
            repaint();
        }

        private void actionAddAnnulusOverlay() {
            EllipticalAnnulusShape shape = new EllipticalAnnulusShape(clickPoint.x, clickPoint.y, 10, 10);
            overlays.add(shape);
            repaint();
        }

        private void actionAddShapeOverlay() {
            BasicShape shape = new BasicShape(BasicShape.ELLIPSE, clickPoint.x, clickPoint.y, 10, 10);
            overlays.add(shape);
            repaint();
        }

        private void actionAddArrowOverlay() {
            ArrowOverlay arrow = new ArrowOverlay(clickPoint.x, clickPoint.y, 10, 5, 5, 0f);
            overlays.add(arrow);
            repaint();
        }

        private void actionRemoveOverlay() {
            clickPoint.x = 0;
            clickPoint.y = 0;
            overlays.remove(selectedOverlay);
            repaint();
        }

        private void actionCenterOverlay() {
            double panelCenterX = (double) (getWidth() / 2.0f);
            double panelCenterY = (double) (getHeight() / 2.0f);

            double overlayRadiusX = (selectedOverlay.getWidth() / 2.0) * getCurrentScaleFactor();
            double overlayRadiusY = (selectedOverlay.getHeight() / 2.0) * getCurrentScaleFactor();

            double offsetX = getCurrentTranslationX() * getCurrentScaleFactor();
            double offsetY = getCurrentTranslationY() * getCurrentScaleFactor();

            double xPos = (panelCenterX - overlayRadiusX - offsetX) / getCurrentScaleFactor();
            double yPos = (panelCenterY - overlayRadiusY - offsetY) / getCurrentScaleFactor();

            selectedOverlay.setXPos(xPos + selectedOverlay.getPenWidth() / 2.0);
            selectedOverlay.setYPos(yPos + selectedOverlay.getPenWidth() / 2.0);
            repaint();
        }

        private void actionColormapChooser() {
            ColorMapChooser colorChooser = new ColorMapChooser(new JFrame());
            if (!colorChooser.isCancelled()) {
                if (abstractNavigatableData != null) {
                    setColorMap(colorChooser.getColorMap());
                    updateCanvasData(data);
                }
            }
            colorChooser = null;
        }

        private void updateCanvasData(double[][] data) {
            if (data == null) {
                return;
            }
            setCursor(new Cursor(Cursor.WAIT_CURSOR));
            updateCanvasImage(data);
            nodifyDataChanged();
            setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        }

        private final void createLogBuffer(double[][] data) {
            logBuffer = null;
            if (isAutoScaled) {
                logBuffer = convertDataToImage(MoreMath.log(data));
                logDataMin = dataMin;
                logDataMax = dataMax;
            } else {
                logBuffer = convertDataToImage(MoreMath.log(data), logDataMin, logDataMax);
            }
        }

        private final void createLinearBuffer(double[][] data) {
            linBuffer = null;
            if (isAutoScaled) {
                linBuffer = convertDataToImage(data);
                linDataMin = dataMin;
                linDataMax = dataMax;
            } else {
                linBuffer = convertDataToImage(data, linDataMin, linDataMax);
            }
        }

        private void updateCanvasImage(double[][] data) {
            if (abstractNavigatableData == null) {
                return;
            }

            createLinearBuffer(data);
            if (isLogPlotRequested || isLogPlot) {
                createLogBuffer(data);
            }

            if (isLogPlot) {
                setBufferedImage(logBuffer);
            } else {
                setBufferedImage(linBuffer);
            }

            if (isHorizontalFlip) {
                flipImageHorizontally();
            }
            if (isVerticalFlip) {
                flipImageVertically();
            }
            rotateImage(currentRotation);

            gridSpacingX = abstractNavigatableData.getDx();
            gridSpacingY = abstractNavigatableData.getDy();
            updateTooltipString();
            notifyBufferChanged();
        }

        private void updateTooltipString() {
            String physicalExtentX = formatNumber((double) getPanelWidth() * gridSpacingX / getCurrentScaleFactor());
            String physicalExtentY = formatNumber((double) getPanelHeight() * gridSpacingY / getCurrentScaleFactor());
            StringBuffer buffer = new StringBuffer("<html><table>");
            buffer.append("<tr><td>Value:</td><td><b>z(" + selectedX + ", " + selectedY + ")=" + formatNumber(selectedZ) + "</b></td></tr>");
            if (abstractNavigatableData != null) {
                buffer.append("<tr><td>Filename:</td><td><b>" + abstractNavigatableData.getID() + "</b></td></tr>");
            }
            int currentFrame = abstractNavigatableData.getCurrentIndex();
            int seriesLength = abstractNavigatableData.getNumberOfFrames();
            buffer.append("<tr><td>File Index:</td><td><b>" + (currentFrame + 1) + " of " + seriesLength + "</b></td></tr>");
            buffer.append("<tr><td>Min/Max:</td><td><b>(" + formatNumber(getDataMin()) + ", " + formatNumber(getDataMax()) + ")</b></td></tr>");
            buffer.append("<tr><td>Grid Spacing:</td><td><b>(" + formatNumber(gridSpacingX) + ", " + formatNumber(gridSpacingY) + ")</b></td></tr>");
            buffer.append("<tr><td>Mesh Size:</td><td><b>(" + bufferedImage.getWidth() + ", " + bufferedImage.getHeight() + ")</b></td></tr>");
            buffer.append("<tr><td>Viewport:</td><td><b>(" + getPanelWidth() + ", " + getPanelHeight() + ")</b></td></tr>");
            buffer.append("<tr><td>Physical Extent (m):</td><td><b>(" + physicalExtentX + ", " + physicalExtentY + ")</b></td></tr>");
            buffer.append("<tr><td>Zoom Level:</td><td><b>" + getCurrentScaleFactor() + "</b></td></tr>");
            buffer.append("</table></html>");
            setToolTipText(buffer.toString());
        }

        private String formatNumber(double x) {
            DecimalFormat df = (x > 1e5 || Math.abs(x) < 1e-5) ? new DecimalFormat("0.0000E00") : new DecimalFormat("0.0000");
            return df.format(x);
        }

        private void actionSaveImageFile(File imageFile, boolean isViewportSizeUsed) {
            if (bufferedImage == null) {
                return;
            }
            try {
                BufferedImage image = null;
                if (isViewportSizeUsed) {
                    image = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
                    Graphics2D g2 = image.createGraphics();
                    paint(g2);
                    g2.dispose();
                    ImageIO.write(image, "png", imageFile);
                } else {
                    ImageIO.write(bufferedImage, "png", imageFile);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void actionSaveImageFile(boolean isViewportSizeUsed) {
            if (bufferedImage == null) {
                return;
            }
            JFileChooser fc = new JFileChooser(currentDirectory);
            int ans = fc.showSaveDialog(null);
            if(ans==JFileChooser.CANCEL_OPTION) return;

            //FileChooser fc = new FileChooser(currentDirectory, "png", "PNG Files", JFileChooser.SAVE_DIALOG, true);
            currentDirectory = fc.getCurrentDirectory().getPath();
            File imageFile = fc.getSelectedFile();
            fc = null;
            actionSaveImageFile(imageFile, isViewportSizeUsed);
        }

        private void actionShowZoomDialog() {
            if (bufferedImage == null) {
                return;
            }
            new ZoomDialog();
        }

        private int getMinMaxSelection() {
            return minMaxSelection;
        }

        public void setInitialScaleFactor(int flag) {
            if (bufferedImage == null) {
                return;
            }
            if (flag == INITIAL_SCALE_ONE_TO_ONE) {
                currentScaleFactor = 1.0f;
            } else if (flag == INITIAL_SCALE_WIDTH) {
                currentScaleFactor = (double) getPanelWidth() / (double) bufferedImage.getWidth();
            } else if (flag == INITIAL_SCALE_HEIGHT) {
                currentScaleFactor = (double) getPanelHeight() / (double) bufferedImage.getHeight();
            }
            currentHorizontalLocation = 0;
            currentVerticalLocation = 0;
            startFromX = 0;
            startFromY = 0;
            transform(currentScaleFactor, (int) currentHorizontalLocation, (int) currentVerticalLocation);
            notifyBufferChanged();
            updateTooltipString();
        }

        public void mouseClicked(MouseEvent e) {
            requestFocus();
            try {
                boolean isOverlaySelected = false;
                int x = (int) (e.getX() / currentScaleFactor);
                int y = (int) (e.getY() / currentScaleFactor);
                Iterator<Overlay> boxes = overlays.iterator();
                Overlay thisOverlay = null;
                boolean isLeft, isRight, isTop, isBottom;
                while (boxes.hasNext()) {
                    thisOverlay = boxes.next();
                    isLeft = (x - currentTranslationX) >= thisOverlay.getLeft();
                    isRight = (x - currentTranslationX) <= (thisOverlay.getLeft() + thisOverlay.getWidth());
                    isTop = (y - currentTranslationY) >= thisOverlay.getTop();
                    isBottom = (y - currentTranslationY) <= (thisOverlay.getTop() + thisOverlay.getHeight());
                    if (isLeft && isRight && isTop && isBottom) {
                        isOverlaySelected = true;
                        selectedOverlay = thisOverlay;
                        break;
                    }
                }

                if (e.getClickCount() == 2) {
                    if (bufferedImage == null) {
                        return;
                    }
                    if (isOverlaySelected) {
                        selectedOverlay.fireOverlayListener();
                        repaint();
                    } else {
                        setInitialScaleFactor(INITIAL_SCALE_WIDTH);
                        if (e.isControlDown()) {
                            setInitialScaleFactor(INITIAL_SCALE_HEIGHT);
                        }
                        if (e.isShiftDown()) {
                            setInitialScaleFactor(INITIAL_SCALE_ONE_TO_ONE);
                        }
                        notifyBufferChanged();
                        updateTooltipString();
                    }
                }

                if (e.getButton() == 3) {
                    int xRef = (int) (e.getX() / currentScaleFactor) - (int) currentHorizontalLocation;
                    int yRef = (int) (e.getY() / currentScaleFactor) - (int) currentVerticalLocation;
                    clickPoint = new Point(xRef, yRef);
                    if (isOverlaySelected) {
                        overlayPopupMenu.show(e.getComponent(), e.getX(), e.getY());
                    } else {
                        popupMenu.show(e.getComponent(), e.getX(), e.getY());
                    }
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }

        public void mouseReleased(MouseEvent e) {
        }

        public void mouseEntered(MouseEvent e) {
            //requestFocus();
            ToolTipManager.sharedInstance().setEnabled(showToolTip);
        }

        public void mouseExited(MouseEvent e) {
            //transferFocus();
            ToolTipManager.sharedInstance().setEnabled(isToolTipVisible);
        }

        public void mousePressed(MouseEvent e) {
            startFromX = currentTranslationX;
            startFromY = currentTranslationY;

            int x = (int) (e.getX() / currentScaleFactor);
            int y = (int) (e.getY() / currentScaleFactor);

            dragFromX = x - startFromX;
            dragFromY = y - startFromY;
            selectedOverlay = null;

            Iterator<Overlay> boxes = overlays.iterator();
            Overlay thisOverlay = null;
            boolean isLeft, isRight, isBottom, isTop;
            while (boxes.hasNext()) {
                thisOverlay = boxes.next();
                isLeft = (x - currentTranslationX) >= thisOverlay.getLeft();
                isRight = (x - currentTranslationX) <= (thisOverlay.getLeft() + thisOverlay.getWidth());
                isTop = (y - currentTranslationY) >= thisOverlay.getTop();
                isBottom = (y - currentTranslationY) <= (thisOverlay.getTop() + thisOverlay.getHeight());
                if (isLeft && isRight && isTop && isBottom) {
                    thisOverlay.isDraggable = true;
                    dragFromX = x - (int) thisOverlay.getXPos();
                    dragFromY = y - (int) thisOverlay.getYPos();
                    selectedOverlay = thisOverlay;
                    break;
                } else {
                    thisOverlay.isDraggable = false;
                }
            }
        }

        public void mouseDragged(MouseEvent e) {
            int x = (int) (e.getX() / currentScaleFactor);
            int y = (int) (e.getY() / currentScaleFactor);

            boolean isImageDragged = true;
            Iterator<Overlay> items = overlays.iterator();
            Overlay thisOverlay = null;
            while (items.hasNext()) {
                thisOverlay = items.next();
                if (!thisOverlay.isDraggable) {
                    continue;
                }

                isImageDragged = false;
                thisOverlay.setXPos(x - dragFromX);
                thisOverlay.setYPos(y - dragFromY);
                repaint();
                break;
            }
            if (isImageDragged) {
                startFromX = x - dragFromX;
                currentHorizontalLocation = startFromX;

                startFromY = y - dragFromY;
                currentVerticalLocation = startFromY;

                transform(currentScaleFactor, (int) currentHorizontalLocation, (int) currentVerticalLocation);
                nodifyMouseDragged();
            }
        }

        public void mouseMoved(MouseEvent e) {
            if (e.isControlDown()) {
                return;
            }
            int x = (int) (e.getX() / currentScaleFactor);
            int y = (int) (e.getY() / currentScaleFactor);
            Iterator<Overlay> boxes = overlays.iterator();
            Overlay thisOverlay = null;
            boolean isLeft, isRight, isBottom, isTop;
            while (boxes.hasNext()) {
                thisOverlay = boxes.next();
                isLeft = (x - currentTranslationX) >= thisOverlay.getLeft();
                isRight = (x - currentTranslationX) <= (thisOverlay.getLeft() + thisOverlay.getWidth());
                isTop = (y - currentTranslationY) >= thisOverlay.getTop();
                isBottom = (y - currentTranslationY) <= (thisOverlay.getTop() + thisOverlay.getHeight());
                if (isLeft && isRight && isTop && isBottom) {
                    setCursor(new Cursor(Cursor.HAND_CURSOR));
                    thisOverlay.isDraggable = true;
                    break;
                } else {
                    setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                    thisOverlay.isDraggable = false;
                }
            }
            try {
                selectedX = (int) (e.getX() / getCurrentScaleFactor()) - (int) currentHorizontalLocation;
                selectedY = (int) (e.getY() / getCurrentScaleFactor()) - (int) currentVerticalLocation;
                double z;
                if (isLogPlot) {
                    z = Math.log(data[selectedX][selectedY]);
                } else {
                    z = data[selectedX][selectedY];
                }
                selectedZ = z;
                updateTooltipString();
                nodifyMouseMoved();
            } catch (Exception excep) {
            }
        }

        public void mouseWheelMoved(MouseWheelEvent e) {
            if (bufferedImage == null) {
                return;
            }
            currentScaleFactor = currentScaleFactor * (1 - e.getWheelRotation() / 20.0f);
            transform(currentScaleFactor, (int) currentHorizontalLocation, (int) currentVerticalLocation);
            notifyBufferChanged();
        }

        public void keyPressed(KeyEvent e) {
        }

        public void keyTyped(KeyEvent e) {
        }

        public void keyReleased(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_F2) {
                showToolTip = !showToolTip;
                ToolTipManager.sharedInstance().setEnabled(showToolTip);
            }
        }
    }

    /**
     *
     * @author Dr. Richard St. John
     * @version $Revision: 1.11 $, $Date: 2007/10/09 17:39:22 $
     */
    private class RangeDialog extends JDialog implements ActionListener {

        public static final String ACTION_RESTORE_DEFAULTS = "RANGE_DEFAULTS";
        public static final String ACTION_PREVIEW = "RANGE_PREVIEW";
        private JRadioButton radioFrameRange,  radioSeriesRange,  radioGridRange,  radioUserDefined;
        private JTextField fieldMax,  fieldMin;

        public RangeDialog() {
            super(new Frame(), "Range Dialog for Setting Minimum and Maximum", true);
            setContentPane(createContentPane());
            pack();
            centerOnScreen();
            setResizable(false);
            setVisible(true);
        }

        private JPanel createContentPane() {
            ButtonGroup rangeGroup = new ButtonGroup();
            rangeGroup.add(radioFrameRange = new JRadioButton("Frame Minimum / Maximum", minMaxSelection == RANGE_AUTO_SCALE));
            rangeGroup.add(radioSeriesRange = new JRadioButton("This Series Minimum / Maximum", minMaxSelection == RANGE_SERIES_RANGE));
            rangeGroup.add(radioGridRange = new JRadioButton("All Series Minimum / Maximum", minMaxSelection == RANGE_GLOBAL_RANGE));
            rangeGroup.add(radioUserDefined = new JRadioButton("User Defined Range", minMaxSelection == RANGE_USER_RANGE));

            radioFrameRange.setActionCommand("USER_DEFINED");
            radioFrameRange.addActionListener(this);
            radioSeriesRange.setActionCommand("USER_DEFINED");
            radioSeriesRange.addActionListener(this);
            radioGridRange.setActionCommand("USER_DEFINED");
            radioGridRange.addActionListener(this);
            radioUserDefined.setActionCommand("USER_DEFINED");
            radioUserDefined.addActionListener(this);

            JPanel panelType = new JPanel();
            panelType.setLayout(new BoxLayout(panelType, BoxLayout.Y_AXIS));
            panelType.setBorder(BorderFactory.createTitledBorder("Select Zoom Type"));
            panelType.add(radioFrameRange);
            panelType.add(radioSeriesRange);
            panelType.add(radioGridRange);
            panelType.add(radioUserDefined);

            fieldMin = new JTextField(String.valueOf(getDataMin()), 15);
            fieldMin.setName("Set Minimum Range: ");
            fieldMin.setEnabled(minMaxSelection == RANGE_USER_RANGE);
            JLabel labelRangeMin = new JLabel(fieldMin.getName());

            fieldMax = new JTextField(String.valueOf(getDataMax()), 15);
            fieldMax.setName("Set Maximum Range: ");
            fieldMax.setEnabled(minMaxSelection == RANGE_USER_RANGE);
            JLabel labelRangeMax = new JLabel(fieldMax.getName());

            Dimension labelDim = labelRangeMax.getPreferredSize();
            labelRangeMin.setPreferredSize(labelDim);
            labelRangeMin.setMinimumSize(labelDim);
            labelRangeMax.setPreferredSize(labelDim);
            labelRangeMax.setMinimumSize(labelDim);

            JPanel panelRange = new JPanel(new MigLayout());
            panelRange.add(labelRangeMin);
            panelRange.add(fieldMin, "wrap");
            panelRange.add(labelRangeMax);
            panelRange.add(fieldMax, "wrap");

            JPanel panelButton = new JPanel();
            panelButton.add(createButton("Preview", this, ACTION_PREVIEW));
            panelButton.add(createButton("Default", this, ACTION_RESTORE_DEFAULTS));
            panelButton.add(createButton("Close", this, "CLOSE"));

            JPanel panel = new JPanel(new MigLayout());
            panel.add(panelType, "pushx, growx, wrap");
            panel.add(panelRange, "pushx, growx, wrap");
            panel.add(panelButton);

            return panel;
        }

        private JButton createButton(String title, ActionListener al, String actionCommand) {
            JButton button = new JButton(title);
            button.addActionListener(al);
            button.setActionCommand(actionCommand);
            return button;
        }

        private void centerOnScreen() {
            Toolkit toolkit = Toolkit.getDefaultToolkit();
            Dimension screenSize = toolkit.getScreenSize();
            int x = (screenSize.width - getPanelWidth()) / 2;
            int y = (screenSize.height - getPanelHeight()) / 2;
            setLocation(x, y);
        }

        public void actionPerformed(ActionEvent e) {
            if (radioFrameRange.isSelected()) {
                minMaxSelection = RANGE_AUTO_SCALE;
            } else if (radioSeriesRange.isSelected()) {
                minMaxSelection = RANGE_SERIES_RANGE;
            } else if (radioGridRange.isSelected()) {
                minMaxSelection = RANGE_GLOBAL_RANGE;
            } else {
                minMaxSelection = RANGE_USER_RANGE;
            }

            String command = e.getActionCommand();
            if (command.equalsIgnoreCase("CLOSE")) {
                setVisible(false);
            } else if (command.equalsIgnoreCase("USER_DEFINED")) {
                if (radioUserDefined.isSelected()) {
                    fieldMin.setEnabled(true);
                    fieldMax.setEnabled(true);
                } else {
                    fieldMin.setEnabled(false);
                    fieldMax.setEnabled(false);
                }
            } else if (command.equalsIgnoreCase(ACTION_PREVIEW)) {
                actionRangePreview();
            } else if (command.equalsIgnoreCase(ACTION_RESTORE_DEFAULTS)) {
                isAutoScaled = true;
                radioFrameRange.setSelected(true);
                canvasListener.updateCanvasData(data);
            }
        }

        private void actionRangePreview() {
            if (radioUserDefined.isSelected()) {
                setDataRange(Double.parseDouble(fieldMin.getText()), Double.parseDouble(fieldMax.getText()));
                isAutoScaled = false;
            } else if (radioSeriesRange.isSelected()) {
                try {
                    isAutoScaled = false;
                    //computeSeriesMinMax();
                    setDataRange(getSeriesMin(), getSeriesMax());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                isAutoScaled = true;
            }
            canvasListener.updateCanvasData(data);
        }
    }

    /**
     * @author Dr. Richard St. John
     * @version $Revision: 1.11 $, $Date: 2007/10/09 17:39:22 $
     */
    private class PhysicalExtentDialog extends JDialog implements ActionListener {

        public static final String ACTION_PREVIEW = "PHYSICAL_EXTENT_PREVIEW";
        public static final String ACTION_DEFAULT = "PHYSICAL_EXTENT_DEFAULT";
        private JTextField fieldPhysicalExtentX;

        public PhysicalExtentDialog() {
            super(new JFrame(), "Physical Extent Dialog", true);
            setContentPane(createContentPane());
            pack();
            centerOnScreen();
            setResizable(false);
            setVisible(true);
        }

        private JPanel createContentPane() {
            fieldPhysicalExtentX = new JTextField(8);
            fieldPhysicalExtentX.setText(String.valueOf(getPanelWidth() * getGridSpacingX() / getCurrentScaleFactor()));

            JPanel panelExtent = new JPanel(new MigLayout());
            panelExtent.setBorder(BorderFactory.createTitledBorder("Set Desired Physical Extent"));
            panelExtent.add(createLabel("Horizontal Physical Extent (meters)"));
            panelExtent.add(fieldPhysicalExtentX, "pushx, growx");

            JPanel panelButton = new JPanel();
            panelButton.add(createButton("Default", this, ACTION_DEFAULT));
            panelButton.add(createButton("Preview", this, ACTION_PREVIEW));
            panelButton.add(createButton("Close", this, "CLOSE"));

            JPanel panel = new JPanel(new MigLayout());
            panel.add(panelExtent, "pushx, growx, wrap");
            panel.add(panelButton, "pushx, growx");

            return panel;
        }

        private JLabel createLabel(String label) {
            JLabel jlabel = new JLabel(label);
            jlabel.setFont(jlabel.getFont().deriveFont(10.0f));
            return jlabel;
        }

        private JButton createButton(String title, ActionListener al, String actionCommand) {
            JButton button = new JButton(title);
            button.addActionListener(al);
            button.setActionCommand(actionCommand);
            return button;
        }

        private void centerOnScreen() {
            Toolkit toolkit = Toolkit.getDefaultToolkit();
            Dimension screenSize = toolkit.getScreenSize();
            int x = (screenSize.width - getWidth()) / 2;
            int y = (screenSize.height - getHeight()) / 2;
            setLocation(x, y);
        }

        public void actionPerformed(ActionEvent e) {
            double extent = 0;
            String command = e.getActionCommand();
            if (command.equalsIgnoreCase("CLOSE")) {
                setVisible(false);
                return;
            } else if (command.equalsIgnoreCase(PhysicalExtentDialog.ACTION_PREVIEW)) {
                extent = Double.parseDouble(fieldPhysicalExtentX.getText());
            } else if (command.equalsIgnoreCase(PhysicalExtentDialog.ACTION_DEFAULT)) {
                extent = gridSpacingX * bufferedImage.getWidth();
                fieldPhysicalExtentX.setText(String.valueOf(extent));
            }
            currentScaleFactor = getPanelWidth() * gridSpacingX / extent;
            scaleImage(currentScaleFactor);
            notifyBufferChanged();
        }
    }

    /**
     * @author Dr. Richard St. John
     * @version $Revision: 1.11 $, $Date: 2007/10/09 17:39:22 $
     */
    private class ZoomDialog extends JDialog implements ActionListener {

        public static final String ACTION_PREVIEW = "ZOOM_PREVIEW";
        private JComboBox comboInterpMethod;
        private JTextField zoomField;

        public ZoomDialog() {
            super(new Frame(), "Zoom Level Dialog", true);
            setContentPane(createContentPane());
            pack();
            centerOnScreen();
            setResizable(false);
            setVisible(true);
        }

        private JPanel createContentPane() {
            zoomField = new JTextField(11);
            zoomField.setName("Zoom Level");
            zoomField.setText(String.valueOf(currentScaleFactor));

            comboInterpMethod = new JComboBox(new String[]{"Nearest Neighbor", "Bilinear", "Bicubic"});

            JPanel panel = new JPanel(new MigLayout());
            panel.add(new JLabel("Zoom Level"));
            panel.add(zoomField, "pushx, growx, wrap");
            panel.add(new JLabel("Interpolation"));
            panel.add(comboInterpMethod, "pushx, growx, wrap");
            panel.add(createButton("Preview", this, ACTION_PREVIEW), "spanx, split 2, tag ok");
            panel.add(createButton("Close", this, "CLOSE"), "tag cancel");

            return panel;
        }

        private JButton createButton(String title, ActionListener al, String actionCommand) {
            JButton button = new JButton(title);
            button.addActionListener(al);
            button.setActionCommand(actionCommand);
            return button;
        }

        private void centerOnScreen() {
            Toolkit toolkit = Toolkit.getDefaultToolkit();
            Dimension screenSize = toolkit.getScreenSize();
            int x = (screenSize.width - getWidth()) / 2;
            int y = (screenSize.height - getHeight()) / 2;
            setLocation(x, y);
        }

        public void actionPerformed(ActionEvent e) {
            String command = e.getActionCommand();
            if (command.equalsIgnoreCase("CLOSE")) {
                setVisible(false);
            } else if (command.equalsIgnoreCase(ACTION_PREVIEW)) {
                currentScaleFactor = Double.parseDouble(zoomField.getText());
                int interpMethod = 0;
                switch (comboInterpMethod.getSelectedIndex()) {
                    case 0:
                        interpMethod = AffineTransformOp.TYPE_NEAREST_NEIGHBOR;
                        break;
                    case 1:
                        interpMethod = AffineTransformOp.TYPE_BILINEAR;
                        break;
                    case 2:
                        interpMethod = AffineTransformOp.TYPE_BICUBIC;
                        break;
                }
                setInterpolationMethod(interpMethod);
                scaleImage(currentScaleFactor);
                setInterpolationMethod(AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
                notifyBufferChanged();
            }
        }
    }
}

