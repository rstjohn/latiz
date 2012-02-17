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
package com.AandR.beans.plotting.latExplorer;

import com.AandR.beans.plotting.LinePlotPanel.LinePlotPanel;
import com.AandR.beans.plotting.data.AbstractNavigatableData;
import com.AandR.beans.plotting.data.LatNavigatableData;
import com.AandR.beans.plotting.data.SimpleNavigatableData;
import com.AandR.beans.plotting.data.TrfNavigatableData;
import com.AandR.beans.plotting.imagePlotPanel.ImagePlotPanel;
import com.AandR.beans.plotting.scrollableDesktop.JDesktopPaneWithDropSupport;
import com.AandR.library.gui.DropEvent;
import com.AandR.library.gui.DropListener;
import com.AandR.library.math.MatrixMath;
import com.AandR.library.math.OpticsMath;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultMutableTreeNode;

import ncsa.hdf.object.Attribute;
import ncsa.hdf.object.Dataset;
import ncsa.hdf.object.Group;
import ncsa.hdf.object.HObject;
import ncsa.hdf.object.h5.H5File;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;

/**
 * @author Dr. Richard St. John
 * @version $Revision$, $Date$
 */
public class LatFilePlotPanel extends JPanel {

    public static final int ASCII_DATA = 1;
    public static final int FIELD_DATA = 2;
    public static final int COMPLEX_DATA = 3;
    private static final int AMPLITUDE_PLOT = 10;
    private static final int PHASE_WRAPPED_PLOT = 20;
    private static final int INTENSITY_PLOT = 30;
    private static final int PHASE_PISTON_REMOVED_PLOT = 40;
    private static final int PHASE_UNWRAPPED_PLOT = 41;
    private static final int REAL_PART_PLOT = 50;
    private static final int IMAG_PART_PLOT = 60;
    private static final String ACTION_CLOSE = "CLOSE";
    private static final String PLOT_AMPLITUDE = "PLOT_AMPLITUDE";
    private static final String PLOT_INTENSITY = "PLOT_INTENSITY";
    private static final String PLOT_POWER = "PLOT_POWER";
    private static final String PLOT_PHASE_WRAPPED = "PLOT_PHASE_WRAPPED";
    private static final String PLOT_PHASE_UNWRAPPED = "PLOT_PHASE_UNWRAPPED";
    private static final String PLOT_PHASE_PISTON_REMOVED = "PLOT_PHASE_PISTON_REMOVED";
    private static final String PLOT_REAL_PART = "PLOT_REAL_PART";
    private static final String PLOT_IMAGINARY_PART = "PLOT_IMAGINARY_PART";
    private int requestedPlotType;
    private boolean showLatTree;
//    private AcsFileSeriesInterface acsFileSeriesInterface;
    private Dimension imagePanelDimension,  linePanelDimension;
    private HDF5PlotPanelInterface hdf5PlotPanelInterface;
    private JDesktopPaneWithDropSupport plotPanel;
    private JPopupMenu popupComplex;
    private LatFileExplorerPanel latExplorerPanel;
    private PlotPanelInterface selectedPlotPanelInterface;
    private TrfPlotPanelInterface trfPlotPanelInterface;

    public LatFilePlotPanel() {
        this(null, true);
    }

    public LatFilePlotPanel(JMenuBar menuBar) {
        this(menuBar, true);
    }

    public LatFilePlotPanel(JMenuBar menuBar, boolean showLatTree) {
        this.showLatTree = showLatTree;
        initialize();
        setLayout(new BorderLayout());
        add(createContentPane(), BorderLayout.CENTER);
        if (menuBar != null) {
            registerMenuBar(menuBar);
        }
    }

    public void registerMenuBar(JMenuBar menuBar) {
        plotPanel.registerMenuBar(menuBar);
    }

    public JMenu getMenu() {
        return plotPanel.getMenu();
    }

    private void initialize() {
        imagePanelDimension = new Dimension(500, 340);
        linePanelDimension = new Dimension(420, 300);
        MenuActionListener menuActionListener = new MenuActionListener();
        hdf5PlotPanelInterface = new HDF5PlotPanelInterface();
        trfPlotPanelInterface = new TrfPlotPanelInterface();
//        acsFileSeriesInterface = new AcsFileSeriesInterface();

        PlotPanelListener canvasListener = new PlotPanelListener();

        latExplorerPanel = new LatFileExplorerPanel(LatFileExplorerPanel.VERTICAL);
        latExplorerPanel.getTree().addMouseListener(canvasListener);
        latExplorerPanel.addH5PopupListener(canvasListener);

        plotPanel = new JDesktopPaneWithDropSupport();
        plotPanel.addDropListener(canvasListener);
        plotPanel.setPreferredSize(new Dimension(500, 500));

        popupComplex = new JPopupMenu();
        popupComplex.add(createMenuItem("Amplitude", PLOT_AMPLITUDE, menuActionListener));
        popupComplex.add(createMenuItem("Intensity", PLOT_INTENSITY, menuActionListener));
        popupComplex.add(new JSeparator());
        popupComplex.add(createMenuItem("Sum up intensity", PLOT_POWER, menuActionListener));
        popupComplex.add(new JSeparator());
        popupComplex.add(createMenuItem("Phase (Wrapped)", PLOT_PHASE_WRAPPED, menuActionListener));
        popupComplex.add(createMenuItem("<HTML>Phase (Unwrapped<SUP><B>*</B></SUP>)</HTML>", PLOT_PHASE_UNWRAPPED, menuActionListener));
        popupComplex.add(createMenuItem("Phase (Piston-removed)", PLOT_PHASE_PISTON_REMOVED, menuActionListener));
        popupComplex.add(new JSeparator());
        popupComplex.add(createMenuItem("Real Part", PLOT_REAL_PART, menuActionListener));
        popupComplex.add(createMenuItem("Imaginary Part", PLOT_IMAGINARY_PART, menuActionListener));
    }

    private JComponent createContentPane() {
        JSplitPane splitter = new JSplitPane();
        splitter.setOneTouchExpandable(true);
        splitter.setLeftComponent(latExplorerPanel);
        splitter.setRightComponent(plotPanel);
        splitter.setDividerSize(8);
        if (!showLatTree) {
            splitter.setDividerLocation(0);
        }

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(splitter, BorderLayout.CENTER);
        return panel;
    }

    private JMenuItem createMenuItem(String label, String actionCommand, ActionListener menuActionListener) {
        JMenuItem item = new JMenuItem(label);
        item.addActionListener(menuActionListener);
        item.setActionCommand(actionCommand);
        return item;
    }

    private void multipleLinePlot(String plotTitle, String[] labels, double[][] data) {
        LinePlotPanel linePlotPanel = new LinePlotPanel();
        linePlotPanel.addDropListener(new LinePlotListener(linePlotPanel));
        linePlotPanel.setPlotTitle(plotTitle);
        linePlotPanel.setData(labels, data);
        linePlotPanel.setPreferredSize(linePanelDimension);
        plotPanel.add(plotTitle, linePlotPanel);
    }

    private void actionOpenInTable(DefaultMutableTreeNode node) {
        Object userObject = node.getUserObject();
        if (userObject instanceof HObject) {
            hdf5PlotPanelInterface.openDataInTable(node);
        } else if (userObject instanceof TrfTreeObject) {
            trfPlotPanelInterface.openDataInTable(node);
        }
    }

    private void actionComplexImageDropped(int plotType) {
        requestedPlotType = plotType;
        if (selectedPlotPanelInterface == trfPlotPanelInterface) {
            try {
                trfPlotPanelInterface.complexTrfDataDropped((TrfDataObject) latExplorerPanel.getSelectedNode().getUserObject());
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (selectedPlotPanelInterface == hdf5PlotPanelInterface) {
            hdf5PlotPanelInterface.complexDataDropped((HObject) latExplorerPanel.getSelectedNode().getUserObject());
//        } else if (selectedPlotPanelInterface == acsFileSeriesInterface) {
//            acsFileSeriesInterface.complexDataDropped();
        }
    }

    private void actionShowTotalPower() {
        if (!latExplorerPanel.getSelectedNode().isLeaf()) {
            return;
        }

        Object userObject = latExplorerPanel.getSelectedNode().getUserObject();
        try {
            if (userObject instanceof TrfDataObject) {
                String name = ((TrfDataObject) userObject).getAlias();
                NotifyDescriptor nd = new NotifyDescriptor.Message("<HTML>Total Power for <B>" + name + "</B> = " + OpticsMath.computeTotalPowerFromComplexField(latExplorerPanel.getData((TrfDataObject) userObject)) + "</HTML>");
                DialogDisplayer.getDefault().notify(nd);
            } else {
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public LatFileExplorerPanel getLatExplorerPanel() {
        return latExplorerPanel;
    }

    public JDesktopPaneWithDropSupport getPlotPanel() {
        return plotPanel;
    }

    /**
     *
     * @author Dr. Richard St. John
     * @version $Revision$, $Date$
     */
    private class PlotPanelListener implements DropListener, MouseListener, LatFileNodeListener {

        public void dropAction(DropEvent dropEvent) {
            Object droppedItem = dropEvent.getDroppedItem();
            if (droppedItem == null) {
                return;
            }

            // Tree node dropped
            Object object = null;
            if (droppedItem instanceof DefaultMutableTreeNode) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) droppedItem;

                Object userObject = node.getUserObject();
                if (userObject instanceof HObject) {
                    selectedPlotPanelInterface = hdf5PlotPanelInterface;
                    object = (DefaultMutableTreeNode) droppedItem;
                } else if (userObject instanceof TrfDataObject) {
                    selectedPlotPanelInterface = trfPlotPanelInterface;
                    object = (DefaultMutableTreeNode) droppedItem;
                }
            }

            // File dropped
//            if (droppedItem instanceof String[]) {
//                selectedPlotPanelInterface = acsFileSeriesInterface;
//                object = new File(((String[]) droppedItem)[0]);
//            }

            selectedPlotPanelInterface.treeNodeDropped(dropEvent, object);
        }

        public void mouseEntered(MouseEvent e) {
        }

        public void mouseExited(MouseEvent e) {
        }

        public void mousePressed(MouseEvent e) {
        }

        public void mouseReleased(MouseEvent e) {
        }

        public void mouseClicked(MouseEvent e) {
            if (e.getClickCount() != 2) {
                return;
            }

            DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) latExplorerPanel.getTree().getLastSelectedPathComponent();
            if (selectedNode == null) {
                return;
            }

            Object userObject = selectedNode.getUserObject();
            if (userObject instanceof Dataset) {
                selectedPlotPanelInterface = hdf5PlotPanelInterface;
                selectedPlotPanelInterface.treeNodeDoubleClicked(e, selectedNode);
            } else if (userObject instanceof TrfDataObject) {
                selectedPlotPanelInterface = trfPlotPanelInterface;
                selectedPlotPanelInterface.treeNodeDoubleClicked(e, selectedNode);
            }
        }

        public void saveOutputsChanged() {
        }

        public void itemSelected(String actionCommand, DefaultMutableTreeNode node) {
            if (actionCommand.equalsIgnoreCase("open data")) {
                actionOpenInTable(node);
            }
        }
    }

    /**
     *
     * @author Dr. Richard St. John
     * @version $Revision$, $Date$
     */
    private class LinePlotListener implements DropListener {

        private double[][] data;
        private double[] times;
        private long rowCount,  colCount;
        private String alias;
        private LinePlotPanel linePlotPanel;

        public LinePlotListener(LinePlotPanel linePlotPanel) {
            this.linePlotPanel = linePlotPanel;
        }

        public void dropAction(DropEvent dropEvent) {
            DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) latExplorerPanel.getTree().getLastSelectedPathComponent();
            if (selectedNode == null || !(selectedNode.getUserObject() instanceof Dataset)) {
                return;
            }

            HObject hObject = (HObject) selectedNode.getUserObject();
            LatFileReader reader = latExplorerPanel.getLatFileReader();

            LatFileRunTrace runTrace = latExplorerPanel.getLatFileReader().getRunTrace().get(hObject.getFile() + ":" + hObject.getFullName());

            alias = runTrace.getAlias();
            times = runTrace.getSelectedTimes();
            long[] dims = runTrace.getSelectedDims()[0];
            rowCount = dims[0];
            colCount = dims[1];

            data = null;
            try {
                data = (double[][]) reader.loadVariable((HObject) selectedNode.getUserObject(), runTrace);
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }

            JPopupMenu popupAddPlot = new JPopupMenu();
            popupAddPlot.add(createMenuItem("Add", "ADD_TO_PLOT", new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    addPlotData(rowCount, colCount, alias, times, data);
                }
            }));
            popupAddPlot.add(createMenuItem("Replace", "REPLACE_PLOT", new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    replacePlotData(rowCount, colCount, alias, times, data);
                }
            }));
            popupAddPlot.show(linePlotPanel, dropEvent.getX(), dropEvent.getY());

        //OptionsDialog dropDialog = new OptionsDialog(linePlotPanel, "Line Plot Drop Dialog", new JButtonX[] {new JButtonX("Add"), new JButtonX("Replace")}, OptionsDialog.QUESTION_ICON);
        //dropDialog.showDialog("Data already exists on this plot. Do you want to replace data or add data", 0);
        //int ans = dropDialog.getSelectedButtonIndex();
        //if(ans==0) {
        //} else {
        //}
        }

        private void addPlotData(long rowCount, long colCount, String alias, double[] times, double[][] data) {
            String currentTitle = linePlotPanel.getPlotTitle();
            if (rowCount * colCount == 1) {
                double[] t = new double[times.length];
                for (int i = 0; i < t.length; i++) {
                    t[i] = times[i];
                }
                linePlotPanel.setChartType(data.length == 1 ? LinePlotPanel.SCATTER_PLOT : LinePlotPanel.LINE_PLOT);
                linePlotPanel.addData(new String[]{alias}, t, MatrixMath.transpose(data));
            } else if (rowCount == 1) {
                linePlotPanel.setChartType(LinePlotPanel.LINE_PLOT);
                linePlotPanel.addData(new String[]{times.length == 1 ? "t=" + times[0] : alias}, new double[]{times[0]}, data);
            } else {
                String[] s = new String[times.length];
                for (int i = 0; i < times.length; i++) {
                    s[i] = "t=" + times[i];
                }
                linePlotPanel.addData(s, times, data);
            }
            linePlotPanel.setPlotTitle(currentTitle + ", " + alias);
            plotPanel.getSelectedFrame().setTitle(linePlotPanel.getPlotTitle());
        }

        private void replacePlotData(long rowCount, long colCount, String alias, double[] times, double[][] data) {
            linePlotPanel.clearChart();
            if (rowCount * colCount == 1) {
                double[] t = new double[times.length];
                for (int i = 0; i < t.length; i++) {
                    t[i] = times[i];
                }
                linePlotPanel.setChartType(data.length == 1 ? LinePlotPanel.SCATTER_PLOT : LinePlotPanel.LINE_PLOT);
                linePlotPanel.setData("time", new String[]{alias}, t, MatrixMath.transpose(data));
            } else if (rowCount == 1) {
                linePlotPanel.setChartType(LinePlotPanel.LINE_PLOT);
                linePlotPanel.setData(new String[]{times.length == 1 ? "t=" + times[0] : alias}, data);
            } else {
                String[] s = new String[times.length];
                for (int i = 0; i < times.length; i++) {
                    s[i] = "t=" + times[i];
                }
                linePlotPanel.setData(s, data);
            }
            linePlotPanel.setPlotTitle(alias);
            plotPanel.getSelectedFrame().setTitle(alias);
        }
    }

    /**
     *
     * @author Dr. Richard St. John
     * @version $Revision$, $Date$
     */
    private class MenuActionListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            String command = e.getActionCommand();
            if (command.equalsIgnoreCase(ACTION_CLOSE)) {
                actionClose();
            } else if (command.equalsIgnoreCase(PLOT_AMPLITUDE)) {
                actionComplexImageDropped(AMPLITUDE_PLOT);
            } else if (command.equalsIgnoreCase(PLOT_POWER)) {
                actionShowTotalPower();
            } else if (command.equalsIgnoreCase(PLOT_INTENSITY)) {
                actionComplexImageDropped(INTENSITY_PLOT);
            } else if (command.equalsIgnoreCase(PLOT_PHASE_WRAPPED)) {
                actionComplexImageDropped(PHASE_WRAPPED_PLOT);
            } else if (command.equalsIgnoreCase(PLOT_PHASE_UNWRAPPED)) {
                actionComplexImageDropped(PHASE_UNWRAPPED_PLOT);
            } else if (command.equalsIgnoreCase(PLOT_PHASE_PISTON_REMOVED)) {
                actionComplexImageDropped(PHASE_PISTON_REMOVED_PLOT);
            } else if (command.equalsIgnoreCase(PLOT_REAL_PART)) {
                actionComplexImageDropped(REAL_PART_PLOT);
            } else if (command.equalsIgnoreCase(PLOT_IMAGINARY_PART)) {
                actionComplexImageDropped(IMAG_PART_PLOT);
            }
        }

        private void actionClose() {
            System.exit(0);
        }
    }

    /**
     *
     * @author Dr. Richard St. John
     * @version $Revision$, $Date$
     */
//    private class AcsFileSeriesInterface implements PlotPanelInterface {
//
//        private File currentFile;
//
//        public void openDataInTable(Object object) {
//        }
//
//        public void treeNodeDoubleClicked(MouseEvent mouseEvent, Object object) {
//        }
//
//        public void treeNodeDropped(DropEvent dropEvent, Object object) {
//            currentFile = (File) object;
//            File file = currentFile;
//            BinaryFile binaryFile = new BinaryFile();
//            boolean success = true;
//            int dataType = -1;
//            try {
//                dataType = binaryFile.readHeader(file);
//                if (dataType == -1) {
//                    dataType = binaryFile.readHeader(file, BinaryFile.FILE_TYPE_UNFORMATTED, BinaryFile.HEADER_NX_NY_DX_DY);
//                    if (dataType == -1) {
//                        success = false;
//                    }
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//            if (dataType == BinaryFile.COMPLEX_DATA) {
//                popupComplex.show(plotPanel, dropEvent.getX(), dropEvent.getY());
//            } else {
//                final ImagePlotPanel imagePlotPanel = new ImagePlotPanel();
//                imagePlotPanel.setPreferredSize(imagePanelDimension);
//                success &= imagePlotPanel.getCanvas().setNavigatableData(new AcsFileSeriesData(file));
//                if (!success) {
//                    OptionsDialog d = new OptionsDialog(LatFilePlotPanel.this, "File Format Error", OptionsDialog.ERROR_ICON);
//                    d.showDialog("<HTML><B>" + file.getPath() + "</B><BR><BR>is not a valid ACS Binary Image File.</HTML>", 0);
//                    return;
//                }
//                plotPanel.add(file.getName() + "-Series", imagePlotPanel);
//            }
//        }
//
//        public void treeNodePressed(MouseEvent e, Object object) {
//        }
//
//        public void complexDataDropped() {
//            final ImagePlotPanel imagePlotPanel = new ImagePlotPanel();
//            imagePlotPanel.getCanvas().setNavigatableData(new AcsFileSeriesData(currentFile, requestedPlotType));
//            imagePlotPanel.setPreferredSize(imagePanelDimension);
//            plotPanel.add(currentFile.getName() + "-Series", imagePlotPanel);
//        }
//    }

    /**
     *
     * @author Dr. Richard St. John
     * @version $Revision$, $Date$
     */
    private class HDF5PlotPanelInterface implements PlotPanelInterface {

        public void treeNodeDoubleClicked(MouseEvent e, Object object) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) object;
            plotAction(e.getPoint(), (HObject) node.getUserObject());
        }

        public void treeNodePressed(MouseEvent e, Object object) {
        }

        public void treeNodeDropped(DropEvent dropEvent, Object object) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) object;
            plotAction(dropEvent.getLocation(), (HObject) node.getUserObject());
        }

        private void plotAction(Point pt, HObject hobject) {
            if (hobject instanceof Group) {
                return;
            }

            Dataset dataset = (Dataset) hobject;
            latExplorerPanel.getLatFileReader().setH5File((H5File) dataset.getFileFormat());

            List<Attribute> attList = latExplorerPanel.getLatFileReader().getMetadata(hobject);
            try {
                if (attList.size() < 1) {
                    plotImageData(plotPanel, pt, dataset);
                    return;
                }

                Attribute att = attList.get(0);

                // ACS-H5 file node.
                if (att.getName().equals("TYPE")) {
                    int dataType = ((int[]) att.getValue())[0];
                    if (dataType == ASCII_DATA) {
                        plotAsciiData(dataset);
                    } else {
                        plotImageData(plotPanel, pt, dataset);
                    }
                } else {
                    plotImageData(plotPanel, pt, dataset);
                }
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }

        private void plotImageData(Component invoker, Point pt, Dataset dataset) throws OutOfMemoryError, Exception {
            LatFileReader reader = latExplorerPanel.getLatFileReader();
            LatFileRunTrace runTrace = reader.getRunTrace().get(dataset.getFile() + ":" + dataset.getFullName());

            long rowCount = 1;
            long colCount = 1;
            long[][] dims = runTrace.getSelectedDims();
            if (dims == null) {
                rowCount = dataset.getDims()[0];
                colCount = dataset.getDims()[1];
            } else {
                rowCount = dims[0][0];
                colCount = dims[0][1];
            }

            if (rowCount * colCount == 1) {
                singlePointDropped(dataset);
            } else if (rowCount == 1) {
                multipleLinesDropped(dataset);
            } else {
                if (runTrace.getType() == TrfConstants.ARRAY_COMPLEX) {
                    requestedPlotType = 0;
                    popupComplex.show(invoker, pt.x, pt.y);
                } else {
                    final ImagePlotPanel imagePlotPanel = new ImagePlotPanel();
                    imagePlotPanel.getCanvas().setNavigatableData(new LatNavigatableData(reader, dataset));
                    imagePlotPanel.setPreferredSize(imagePanelDimension);
                    plotPanel.add(runTrace.getAlias(), imagePlotPanel);
                }
            }
        }

        public void complexDataDropped(HObject hobject) {
            if (requestedPlotType == 0) {
                return;
            }
            final ImagePlotPanel imagePlotPanel = new ImagePlotPanel();
            AbstractNavigatableData navData = new LatNavigatableData(latExplorerPanel.getLatFileReader(), hobject, requestedPlotType);
            imagePlotPanel.getCanvas().setNavigatableData(navData);
            String plotID = navData.getID().substring(0, navData.getID().indexOf(": (time="));
            imagePlotPanel.setPreferredSize(imagePanelDimension);
            plotPanel.add(plotID, imagePlotPanel);
        }

        private void singlePointDropped(Dataset dataset) throws OutOfMemoryError, Exception {
            LatFileReader reader = latExplorerPanel.getLatFileReader();

            LatFileRunTrace runTrace = reader.getRunTrace().get(dataset.getFile() + ":" + dataset.getFullName());
            String alias = runTrace.getAlias();
            double[] times = runTrace.getSelectedTimes();

            double[][] data;
            if (times == null) {
                data = MatrixMath.transpose(reader.loadVariable(dataset));
            } else {
                data = MatrixMath.transpose((double[][]) reader.loadVariable(dataset, runTrace));
            }

            double[] t = new double[times.length];
            for (int i = 0; i < t.length; i++) {
                t[i] = times[i];
            }
            int plotType = data[0].length == 1 ? LinePlotPanel.SCATTER_PLOT : LinePlotPanel.LINE_PLOT;
            LinePlotPanel linePlotPanel = new LinePlotPanel(plotType);
            linePlotPanel.addDropListener(new LinePlotListener(linePlotPanel));
            linePlotPanel.setPlotTitle(alias);
            linePlotPanel.setData("time", new String[]{alias}, t, data);
            linePlotPanel.setPreferredSize(linePanelDimension);
            plotPanel.add(alias, linePlotPanel);
        }

        private void multipleLinesDropped(Dataset dataset) throws OutOfMemoryError, Exception {
            LatFileReader reader = latExplorerPanel.getLatFileReader();

            LatFileRunTrace runTrace = reader.getRunTrace().get(dataset.getFile() + ":" + dataset.getFullName());
            String alias = runTrace.getAlias();
            double[] times = runTrace.getSelectedTimes();
            if (times == null) {
                Toolkit.getDefaultToolkit().beep();
                NotifyDescriptor nd = new NotifyDescriptor.Message("Unable to plot data because no time information was found.");
                DialogDisplayer.getDefault().notify(nd);
                return;
            }

            Object o = reader.loadVariable(dataset, runTrace);
            double[][] data;
            if (o instanceof double[][]) {
                data = (double[][]) o;
            } else if (o instanceof byte[]) {
                byte[] byteData = (byte[]) o;
                data = new double[1][byteData.length];
                for (int i = 0; i < byteData.length; i++) {
                    data[0][i] = byteData[i];
                }
            } else {
                return;
            }
            String[] s = new String[times.length];
            for (int i = 0; i < times.length; i++) {
                s[i] = "t=" + times[i];
            }
            multipleLinePlot(alias, s, data);
        }

        private void plotAsciiData(Dataset dataset) throws OutOfMemoryError, Exception {
            LatFileRunTrace runTrace = latExplorerPanel.getLatFileReader().getRunTrace().get(dataset.getFile() + ":" + dataset.getFullName());

            dataset.init();
            long[] startDims = dataset.getStartDims();
            startDims[0] = 0;
            startDims[1] = 0;

            long[] selectedDims = dataset.getSelectedDims();
            int iterationCount = runTrace.getIterationCount();
            selectedDims[0] = iterationCount;
            selectedDims[1] = 1;

            int[] columnIndices = runTrace.getSelectedColumns();
            int[] parameterIndices = runTrace.getSelectedParameters();
            int[] realizationIndices = runTrace.getSelectedRealizations();

            double[][] data = new double[columnIndices.length * parameterIndices.length * realizationIndices.length][iterationCount];
            String[] labels = new String[data.length];
            float[] colData;
            int currentParameterIndex, currentRealizationIndex;
            int rowCount = 0;

            dataset.getFileFormat().open();
            for (int i = 0; i < columnIndices.length; i++) {

                for (int j = 0; j < realizationIndices.length; j++) {
                    currentRealizationIndex = realizationIndices[j];

                    for (int k = 0; k < parameterIndices.length; k++) {
                        currentParameterIndex = parameterIndices[k];

                        startDims[0] = (currentParameterIndex * realizationIndices.length + currentRealizationIndex) * iterationCount;
                        startDims[1] = columnIndices[i];
                        colData = (float[]) dataset.read();
                        for (int c = 0; c < colData.length; c++) {
                            data[rowCount][c] = colData[c];
                        }
                        labels[rowCount] = "c" + (columnIndices[i] + 1) + "_r" + (realizationIndices[j] + 1) + "_p" + (parameterIndices[k] + 1);
                        rowCount++;
                    }
                }
            }
            dataset.getFileFormat().open();
            multipleLinePlot(runTrace.getAlias(), labels, data);
        }

        public void openDataInTable(Object object) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) object;
            if (node == null || !node.isLeaf()) {
                return;
            }

            HObject hobject = (HObject) node.getUserObject();
            if (!(hobject instanceof Dataset)) {
                return;
            }

            Dataset dataset = (Dataset) hobject;
            dataset.init();

            int rank = dataset.getRank();
            if (rank > 2) {
                System.out.println("Rank > 2, not supported");
                return;
            }

            long[] dims = dataset.getDims();

            Object o = null;
            try {
                dataset.getFileFormat().open();
                o = dataset.read();
                dataset.getFileFormat().close();
            } catch (OutOfMemoryError e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }

            Number[][] data = null;
            String[] header = null;
            int k = 0;
            int rows = (int) dims[0];
            int cols = (int) dims[1];
            header = new String[cols];
            for (int i = 0; i < header.length; i++) {
                header[i] = "c" + (i + 1);
            }

            String datatype = dataset.getDatatype().getDatatypeDescription();
            if (datatype.startsWith("32-bit floating")) {
                float[] d = (float[]) o;
                data = new Float[rows][cols];
                for (int j = 0; j < rows; j++) {
                    for (int i = 0; i < cols; i++) {
                        data[j][i] = d[k++];
                    }
                }
            } else if (datatype.startsWith("64-bit floating")) {
                double[] d = (double[]) o;
                data = new Double[rows][cols];
                for (int j = 0; j < rows; j++) {
                    for (int i = 0; i < cols; i++) {
                        data[j][i] = d[k++];
                    }
                }
            } else if (datatype.startsWith("32-bit integer")) {
                int[] d = (int[]) o;
                data = new Integer[rows][cols];
                for (int j = 0; j < rows; j++) {
                    for (int i = 0; i < cols; i++) {
                        data[j][i] = d[k++];
                    }
                }
            } else if (datatype.startsWith("64-bit integer")) {
                long[] d = (long[]) o;
                data = new Long[rows][cols];
                for (int j = 0; j < rows; j++) {
                    for (int i = 0; i < cols; i++) {
                        data[j][i] = d[k++];
                    }
                }
            }
            JTable table = new JTable(new DefaultTableModel(data, header));
            table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
            table.setCellSelectionEnabled(true);
            table.setColumnSelectionAllowed(true);
            JPanel panel = new JPanel(new BorderLayout());
            panel.add(new JScrollPane(table));
            plotPanel.add(hobject.getFullName(), panel);
        }
    }

    /**
     *
     * @author Dr. Richard St. John
     * @version $Revision$, $Date$
     */
    private class TrfPlotPanelInterface implements PlotPanelInterface {

        public void treeNodeDoubleClicked(MouseEvent e, Object object) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) object;
            Object userObject = node.getUserObject();
            if (!(userObject instanceof TrfDataObject)) {
                return;
            }

            TrfDataObject trfTreeNode = (TrfDataObject) userObject;
            if (trfTreeNode.getNodeType() == TrfDataObject.VARIABLE_NODE) {
                TrfVariableDoubleClicked(latExplorerPanel.getTree(), e.getPoint(), trfTreeNode);
            } else if (trfTreeNode.getNodeType() == TrfDataObject.PARAMETER_NODE) {
                TrfParameterDoubleClicked(e.getPoint(), trfTreeNode);
            } else {
                return;
            }
        }

        public void treeNodeDropped(DropEvent dropEvent, Object object) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) object;
            Object userObject = node.getUserObject();
            if (!(userObject instanceof TrfDataObject)) {
                return;
            }

            TrfDataObject trfTreeNode = (TrfDataObject) userObject;
            if (trfTreeNode.getNodeType() == TrfDataObject.VARIABLE_NODE) {
                TrfVariableDoubleClicked(plotPanel, dropEvent.getLocation(), trfTreeNode);
            } else if (trfTreeNode.getNodeType() == TrfDataObject.PARAMETER_NODE) {
                TrfParameterDoubleClicked(dropEvent.getLocation(), trfTreeNode);
            } else {
                return;
            }
        }

        public void treeNodePressed(MouseEvent e, Object object) {
        }

        private void TrfParameterDoubleClicked(Point pt, TrfDataObject trfTreeNode) {
            String runName = trfTreeNode.getRunName();
            String alias = trfTreeNode.getAlias();
            int rowCount = trfTreeNode.getRowCount();
            int colCount = trfTreeNode.getColCount();
            Object data;

            int dataType = trfTreeNode.getType();
            if (dataType == TrfConstants.STRING) {
                JDialog o = new JDialog((JDialog) null, "Value for " + alias, false);
                o.setContentPane(new JScrollPane(new JTextArea(trfTreeNode.getValue().toString(), 5, 30)));
                o.pack();
                o.setLocationRelativeTo(latExplorerPanel);
                o.setVisible(true);
                return;
            }

            try {
                data = latExplorerPanel.getTrfReader().loadParameter(trfTreeNode);
                if (!(data instanceof double[][])) {
                    return;
                }

                if (rowCount == 1) {
                    double[][] x = (double[][]) data;
                    LinePlotPanel linePlotPanel = new LinePlotPanel(colCount == 1 ? LinePlotPanel.SCATTER_PLOT : LinePlotPanel.LINE_PLOT);
                    linePlotPanel.addDropListener(new LinePlotListener(linePlotPanel));
                    linePlotPanel.setPlotTitle(alias);
                    linePlotPanel.setData(new String[]{alias}, x);
                    linePlotPanel.setPreferredSize(linePanelDimension);
                    plotPanel.add(runName + ":" + alias, linePlotPanel);
                } else {
                    final ImagePlotPanel imagePlotPanel = new ImagePlotPanel();
                    imagePlotPanel.getCanvas().setNavigatableData(new SimpleNavigatableData((double[][]) data));
                    imagePlotPanel.setPreferredSize(imagePanelDimension);
                    plotPanel.add(trfTreeNode.getRunName() + ":" + trfTreeNode.getAlias(), imagePlotPanel);
                }
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }

        private void TrfVariableDoubleClicked(Component invoker, Point pt, TrfDataObject trfTreeNode) {
            int rowCount = trfTreeNode.getRowCount();
            int colCount = trfTreeNode.getColCount();
            try {
                if (rowCount * colCount == 1) {
                    singlePointDropped(trfTreeNode);
                } else if (rowCount == 1) {
                    multipleLinesDropped(trfTreeNode);
                } else {
                    if (trfTreeNode.getType() == TrfConstants.COMPLEX) {
                        requestedPlotType = 0;
                        popupComplex.show(invoker, pt.x, pt.y);
//          complexTrfDataDropped(trfTreeNode);
                    } else {
                        imagePlotDropped(trfTreeNode);
                    }
                }
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }

        private final void complexTrfDataDropped(TrfDataObject trfTreeNode) throws IOException {
            if (requestedPlotType == 0) {
                return;
            }
            final ImagePlotPanel imagePlotPanel = new ImagePlotPanel();
            AbstractNavigatableData navData = new TrfNavigatableData(latExplorerPanel.getTrfReader(), trfTreeNode, requestedPlotType);
            imagePlotPanel.getCanvas().setNavigatableData(navData);
            String plotID = navData.getID().substring(0, navData.getID().indexOf("@"));
            imagePlotPanel.setPreferredSize(imagePanelDimension);
            plotPanel.add(plotID, imagePlotPanel);
        }

        private void singlePointDropped(TrfDataObject trfTreeNode) throws IOException {
            String runName = trfTreeNode.getRunName();
            String alias = trfTreeNode.getAlias();
            float[] times = trfTreeNode.getSelectedTimes();
            double[][] data = MatrixMath.transpose(latExplorerPanel.getData(trfTreeNode));

            double[] t = new double[times.length];
            for (int i = 0; i < t.length; i++) {
                t[i] = (double) times[i];
            }

            int plotType = data[0].length == 1 ? LinePlotPanel.SCATTER_PLOT : LinePlotPanel.LINE_PLOT;
            LinePlotPanel linePlotPanel = new LinePlotPanel(plotType);
            linePlotPanel.addDropListener(new LinePlotListener(linePlotPanel));
            linePlotPanel.setPlotTitle(alias);
            linePlotPanel.setData("time", new String[]{alias}, t, data);
            linePlotPanel.setPreferredSize(linePanelDimension);
            plotPanel.add(runName + ":" + alias, linePlotPanel);
        }

        private void multipleLinesDropped(TrfDataObject trfTreeNode) throws IOException {
            String runName = trfTreeNode.getRunName();
            String alias = trfTreeNode.getAlias();
            float[] times = trfTreeNode.getSelectedTimes();
            double[][] data = latExplorerPanel.getTrfReader().loadVariable(trfTreeNode, times);
            String[] s = new String[times.length];
            for (int i = 0; i < times.length; i++) {
                s[i] = "t=" + times[i];
            }
            LinePlotPanel linePlotPanel = new LinePlotPanel();
            linePlotPanel.addDropListener(new LinePlotListener(linePlotPanel));
            linePlotPanel.setPlotTitle(alias);
            linePlotPanel.setData(s, data);
            linePlotPanel.setPreferredSize(linePanelDimension);
            plotPanel.add(runName + ":" + alias, linePlotPanel);
        }

        private final void imagePlotDropped(TrfDataObject trfTreeNode) throws IOException {
            final ImagePlotPanel imagePlotPanel = new ImagePlotPanel();
            imagePlotPanel.getCanvas().addDropListener(new DropListener() {

                public void dropAction(DropEvent dropEvent) {
                    TrfDataObject trfTreeNode = (TrfDataObject) dropEvent.getDroppedItem();
                    imagePlotPanel.getCanvas().setNavigatableData(new TrfNavigatableData(latExplorerPanel.getTrfReader(), trfTreeNode));
                    plotPanel.getSelectedFrame().setTitle(trfTreeNode.getRunName() + ":" + trfTreeNode.getAlias());
                }
            });
            imagePlotPanel.getCanvas().setNavigatableData(new TrfNavigatableData(latExplorerPanel.getTrfReader(), trfTreeNode));
            imagePlotPanel.setPreferredSize(imagePanelDimension);
            plotPanel.add(trfTreeNode.getRunName() + ":" + trfTreeNode.getAlias(), imagePlotPanel);
        }

        public void openDataInTable(Object object) {
        }
    }
}
