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

import com.AandR.beans.plotting.data.SimpleNavigatableData;
import com.AandR.library.gui.DropListener;
import com.AandR.library.math.MoreMath;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.KeyStroke;
import javax.swing.border.LineBorder;
import net.miginfocom.swing.MigLayout;
import org.openide.util.Exceptions;

/**
 * @author Dr. Richard St. John
 * @version $Revision: 1.3 $, $Date: 2007/09/05 18:33:50 $
 */
public class ImagePlotPanel extends JPanel {

    private static final String ACTION_SET_VIEWPORT = "SET_VIEWPORT";
    private static final String ACTION_AUTO_SET_VIEWPORT = "AUTO_SET_VIEWPORT";
    private CanvasPanel canvas;
    private ColorBarPanel colorBarPanel;
    private DataPanel dataPanel;
    private JSplitPane centerSplitter,  leftSplitter;
    private JPanel previewPanel,  imagePlotPanel;
    private ThumbnailPanel thumbnailPanel;
    private ViewportDialog viewportDialog;
    private ViewportDialogListener viewportDialogListener;

    public ImagePlotPanel() {
        initialize();
        setLayout(new MigLayout("ins 0", "[]2[]2", "[top]2[top]4"));
        add(centerSplitter, "spany, push, grow");
        add(thumbnailPanel, "gaptop 2, wrap");
        add(colorBarPanel, "pushy, growy, wrap push");
    }

    private void initialize() {
        ImagePanelListener imageCanvasListener = new ImagePanelListener();

        viewportDialogListener = new ViewportDialogListener();
        viewportDialog = new ViewportDialog();

        canvas = new CanvasPanel(500, 500);
        canvas.setMinimumSize(new Dimension(20, 20));
        canvas.addCanvasPanelInterface(imageCanvasListener);

        canvas.getPopupMenu().insert(canvas.createPopupMenuItem("Set Viewport Size", null, KeyStroke.getKeyStroke(KeyEvent.VK_V, 2)), 13);
        canvas.getPopupMenu().insert(canvas.createPopupMenuItem("Set Viewport To Image Size", null, KeyStroke.getKeyStroke(KeyEvent.VK_V, 8)), 14);

//  Bind SetViewport    
        canvas.getActionMap().put("SetViewport", new AbstractAction("SetViewport") {

            public void actionPerformed(ActionEvent evt) {
                viewportDialogListener.actionSetViewport();
            }
        });
        canvas.getInputMap().put(KeyStroke.getKeyStroke("control V"), "SetViewport");

//  Bind SetViewport to Image Size
        canvas.getActionMap().put("SetViewportToImage", new AbstractAction("SetViewportToImage") {

            public void actionPerformed(ActionEvent evt) {
                viewportDialogListener.actionAutoSetViewport();
            }
        });
        canvas.getInputMap().put(KeyStroke.getKeyStroke("alt V"), "SetViewportToImage");


        imagePlotPanel = new JPanel(new MigLayout("ins 0"));
        imagePlotPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 5));
        imagePlotPanel.addComponentListener(imageCanvasListener);
        imagePlotPanel.add(canvas, "push, grow");

        thumbnailPanel = new ThumbnailPanel(128, 128);
        thumbnailPanel.addCanvasPanelListener(new ThumbnailPanelListener());

        colorBarPanel = new ColorBarPanel(128, 570);
        colorBarPanel.setColorBarWidth(30);
        colorBarPanel.setBorder(new LineBorder(Color.BLACK.brighter()));

        dataPanel = new DataPanel();
        dataPanel.setMinimumSize(new Dimension(0, 0));

        centerSplitter = new JSplitPane(JSplitPane.VERTICAL_SPLIT);

        leftSplitter = new JSplitPane();
        leftSplitter.setDividerSize(5);
        leftSplitter.setDividerLocation(0);

        JPanel emptyPanel = new JPanel();
        emptyPanel.setMinimumSize(new Dimension(0, 0));
        leftSplitter.setRightComponent(imagePlotPanel);
        leftSplitter.setLeftComponent(emptyPanel);

        centerSplitter.setTopComponent(dataPanel);
        centerSplitter.setBottomComponent(leftSplitter);
        centerSplitter.setDividerLocation(0);
    }

    public void clearData(){
        try {
            finalize();
        } catch (Throwable ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    public void addDropListener(DropListener listener) {
        getCanvas().addDropListener(listener);
    }

    /**
     * Gets the table data panel.
     * @return
     */
    public DataPanel getDataPanel() {
        return dataPanel;
    }

    public ThumbnailPanel getThumbnailPanel() {
        return thumbnailPanel;
    }

    public ColorBarPanel getColorBarPanel() {
        return colorBarPanel;
    }

    /**
     * Returns the split pane containing the data panel and image canvas.
     * @return
     */
    public JSplitPane getCenterSplitter() {
        return centerSplitter;
    }

    /**
     *
     * @return
     */
    public CanvasPanel getCanvas() {
        return canvas;
    }

    public JPanel getPreviewPanel() {
        return previewPanel;
    }

    @Override
    protected void finalize() throws Throwable {
        dataPanel.resetData(null);
        canvas.clearCanvas();
        System.gc();
    }

    /**
     * This private inner class is used to adjust the viewport.
     * @author Dr. Richard St. John
     * @version $Revision: 1.3 $, $Date: 2007/09/05 18:33:50 $
     */
    private class ViewportDialogListener implements ActionListener {

        private int xDim,  totalWidth,  divideWidth,  availableWidth;
        private int yDim,  totalHeight,  divideHeight,  availableHeight;

        public void actionPerformed(ActionEvent e) {
            String command = e.getActionCommand();
            if (command.equalsIgnoreCase(ACTION_SET_VIEWPORT)) {
                actionSetViewport();
            } else if (command.equalsIgnoreCase(ACTION_AUTO_SET_VIEWPORT)) {
                actionAutoSetViewport();
            }
        }

        private void actionAutoSetViewport() {
            xDim = (int) (canvas.getLinBuffer().getWidth() * canvas.getCurrentScaleFactor());
            yDim = (int) (canvas.getLinBuffer().getHeight() * canvas.getCurrentScaleFactor());
            resizeImagePanel();
        }

        private void actionSetViewport() {
            viewportDialog.showDialog(canvas.getWidth(), canvas.getHeight());
            xDim = viewportDialog.getViewportWidth();
            yDim = viewportDialog.getViewportHeight();
            resizeImagePanel();
        }

        private void computeAvailableSpace() {
            totalWidth = leftSplitter.getWidth();
            divideWidth = leftSplitter.getDividerSize();
            availableWidth = totalWidth - divideWidth - 11;

            totalHeight = centerSplitter.getHeight();
            divideHeight = centerSplitter.getDividerSize();
            availableHeight = totalHeight - divideHeight - 13;
        }

        private void resizeImagePanel() {
            computeAvailableSpace();
            resizeImagePanel(availableWidth - xDim, availableHeight - yDim);
        }

        private void resizeImagePanel(int width, int height) {
            leftSplitter.setDividerLocation(width);
            centerSplitter.setDividerLocation(height);
        }
    }

    /**
     * Implements CanvasPanelListener and ComponentListener.
     * @author Dr. Richard St. John
     * @version $Revision: 1.3 $, $Date: 2007/09/05 18:33:50 $
     */
    private class ImagePanelListener implements CanvasPanelListener, ComponentListener {

        private void updateThumbnailRectangle() {
            int locX = -canvas.getCurrentTranslationX();
            int locY = -canvas.getCurrentTranslationY();

            double maxImageDimension = Math.max(canvas.getLinBuffer().getWidth(), canvas.getLinBuffer().getHeight());
            double rectWidth = thumbnailPanel.getCanvasWidth() / canvas.getCurrentScaleFactor();
            rectWidth *= canvas.getWidth() / maxImageDimension;

            double rectHeight = thumbnailPanel.getCanvasHeight() / canvas.getCurrentScaleFactor();
            rectHeight *= canvas.getHeight() / maxImageDimension;

            thumbnailPanel.setRectSize((int) rectWidth, (int) rectHeight);
            thumbnailPanel.setRectPos(locX, locY);
        }

        public void bufferChanged() {
            if (canvas.isLogPlot()) {
                thumbnailPanel.setBufferedImage(canvas.getLogBuffer());
            } else {
                thumbnailPanel.setBufferedImage(canvas.getLinBuffer());
            }

            updateThumbnailRectangle();
            colorBarPanel.setColorMap(canvas.getColorMap());
        }

        public void dataChanged() {
            boolean isLoadTableRequested = canvas.isTableDataViewable();
            double widthThumb = thumbnailPanel.getCanvasWidth();
            double widthImage = Math.max(canvas.getLinBuffer().getWidth(), canvas.getLinBuffer().getHeight());
            thumbnailPanel.setCurrentScaleFactor(widthThumb / widthImage);

            if (canvas.isLogPlot()) {
                if (isLoadTableRequested) {
                    dataPanel.resetData(MoreMath.log(canvas.getData()));
                }
                thumbnailPanel.setBufferedImage(canvas.getLogBuffer());
            } else {
                if (isLoadTableRequested) {
                    dataPanel.resetData(canvas.getData());
                }
                thumbnailPanel.setBufferedImage(canvas.getLinBuffer());
            }
            colorBarPanel.setDataRange(canvas.getDataMin(), canvas.getDataMax());
        }

        public void mouseDragged() {
            int locX = -canvas.getCurrentTranslationX();
            int locY = -canvas.getCurrentTranslationY();
            thumbnailPanel.setRectPos(locX, locY);
        }

        public void mouseMoved() {
            int y = canvas.getSelectedYValue();
            int x = canvas.getSelectedXValue();
            dataPanel.getTable().setRowSelectionInterval(y, y);
            dataPanel.getTable().setColumnSelectionInterval(x, x);
            Rectangle rect = dataPanel.getTable().getCellRect(y, x, true);

            dataPanel.getTable().scrollRectToVisible(rect);
        }

        public void newDataLoaded() {
        }

        public void componentMoved(ComponentEvent e) {
        }

        public void componentShown(ComponentEvent e) {
        }

        public void componentHidden(ComponentEvent e) {
        }

        public void componentResized(ComponentEvent e) {
            if (canvas.getLinBuffer() != null) {
                updateThumbnailRectangle();
            }
        }
    }

    /**
     * Implements CanvasPanelListener
     * @author Dr. Richard St. John
     * @version $Revision: 1.3 $, $Date: 2007/09/05 18:33:50 $
     */
    private class ThumbnailPanelListener implements CanvasPanelListener {

        public void newDataLoaded() {
        }

        public void bufferChanged() {
        }

        public void dataChanged() {
        }

        public void mouseDragged() {
            canvas.translate(-thumbnailPanel.getRectX(), -thumbnailPanel.getRectY());
            canvas.setCurrentHorizontalLocation(-thumbnailPanel.getRectX());
            canvas.setCurrentVerticalLocation(-thumbnailPanel.getRectY());
        }

        public void mouseMoved() {
        }
    }
}

