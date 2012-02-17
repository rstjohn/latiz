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
package com.AandR.beans.plotting.readers;

import com.AandR.beans.plotting.LinePlotPanel.LinePlotPanel;
import com.AandR.beans.plotting.data.AbstractNavigatableData;
import com.AandR.beans.plotting.data.LatNavigatableData;
import com.AandR.beans.plotting.imagePlotPanel.ImagePlotPanel;
import com.AandR.beans.plotting.latExplorer.LatFileReader;
import com.AandR.beans.plotting.latExplorer.LatFileRunTrace;
import com.AandR.beans.plotting.latExplorer.LatFileTree;
import com.AandR.beans.plotting.latExplorer.TrfConstants;
import com.AandR.beans.plotting.latExplorer.TrfDataObject;
import com.AandR.library.gui.DropEvent;
import com.AandR.library.math.MatrixMath;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.dnd.DropTarget;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;

import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.SpinnerListModel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import ncsa.hdf.object.Dataset;
import ncsa.hdf.object.Group;
import ncsa.hdf.object.HObject;
import net.miginfocom.swing.MigLayout;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.ImageUtilities;

/**
 * @author Aaron Masino
 * @version Sep 18, 2008 3:27:44 PM <br>
 *
 * Comments:
 *
 */
public class DefaultHDF5Reader extends AbstractDataReader {

    private static final int AMPLITUDE_PLOT = 10;
    private static final int PHASE_WRAPPED_PLOT = 20;
    private static final int INTENSITY_PLOT = 30;
    private static final int PHASE_PISTON_REMOVED_PLOT = 40;
    private static final int PHASE_UNWRAPPED_PLOT = 41;
    private static final int REAL_PART_PLOT = 50;
    private static final int IMAG_PART_PLOT = 60;
    private static final String PLOT_AMPLITUDE = "PLOT_AMPLITUDE";
    private static final String PLOT_INTENSITY = "PLOT_INTENSITY";
    private static final String PLOT_PHASE_WRAPPED = "PLOT_PHASE_WRAPPED";
    private static final String PLOT_PHASE_UNWRAPPED = "PLOT_PHASE_UNWRAPPED";
    private static final String PLOT_PHASE_PISTON_REMOVED = "PLOT_PHASE_PISTON_REMOVED";
    private static final String PLOT_REAL_PART = "PLOT_REAL_PART";
    private static final String PLOT_IMAGINARY_PART = "PLOT_IMAGINARY_PART";
    public int requestedPlotType;
    private ChangeListener rowChangeListener,  colChangeListener;
    private Dimension imagePanelDimension,  linePanelDimension;
    private JList timesList;
    private JPanel informationPanel;
    private JPopupMenu popupComplex;
    private JTextField fieldAlias;
    private JSpinner spinnerRows,  spinnerCols;
    private JSplitPane splitter;
    private LatFileReader latFileReader;
    private LatFileTree tree;
    private TimesListListener timesListener;

    public DefaultHDF5Reader() {
        icon = new ImageIcon(ImageUtilities.loadImage("com/AandR/beans/plotting/resources/hdf5.gif"));
    }

    public void initialize(File file) throws DataReaderException {
        imagePanelDimension = new Dimension(550, 400);
        linePanelDimension = new Dimension(600, 400);

        TreeListener treeListener = new TreeListener();
        tree = new LatFileTree(new DefaultTreeModel(new DefaultMutableTreeNode(file.getName())));
        tree.setBorder(new EmptyBorder(5, 5, 5, 5));
        tree.addMouseListener(treeListener);
        tree.addTreeSelectionListener(treeListener);

        latFileReader = new LatFileReader();
        try {
            latFileReader.loadFile(file, (DefaultTreeModel) tree.getModel());
            tree.setRunTraces(latFileReader.getRunTrace());
        } catch (Exception e) {
            throw new DataReaderException(this, "Error occurred while loading file: " + file.getPath());
        }

        rowChangeListener = new RowChangeListener();
        colChangeListener = new ColChangeListener();

        timesList = new JList(new DefaultListModel());
        timesList.addListSelectionListener(timesListener = new TimesListListener());

        fieldAlias = new JTextField("<alias>");
        fieldAlias.addKeyListener(new AliasFieldListener());


        ArrayList<Integer> initialList = new ArrayList<Integer>();
        initialList.add(1);
        spinnerRows = new JSpinner(new SpinnerListModel(initialList));
        spinnerRows.addChangeListener(rowChangeListener);
        spinnerRows.setEnabled(false);

        ArrayList<Integer> initialColList = new ArrayList<Integer>();
        initialColList.add(1);
        spinnerCols = new JSpinner(new SpinnerListModel(initialColList));
        spinnerCols.addChangeListener(colChangeListener);
        spinnerCols.setEnabled(false);

        informationPanel = new JPanel(new CardLayout());
        informationPanel.add(createInformationPanel(), "PARAMETER_PANEL");
        informationPanel.add(new JLabel("No Parameter Panel"), "EMPTY_PANEL");

        MenuActionListener menuActionListener = new MenuActionListener();
        popupComplex = new JPopupMenu();
        popupComplex.add(createMenuItem("Amplitude", PLOT_AMPLITUDE, menuActionListener));
        popupComplex.add(createMenuItem("Intensity", PLOT_INTENSITY, menuActionListener));
        popupComplex.add(new JSeparator());
        popupComplex.add(createMenuItem("Phase (Wrapped)", PLOT_PHASE_WRAPPED, menuActionListener));
        popupComplex.add(createMenuItem("<HTML>Phase (Unwrapped<SUP><B>*</B></SUP>)</HTML>", PLOT_PHASE_UNWRAPPED, menuActionListener));
        popupComplex.add(createMenuItem("Phase (Piston-removed)", PLOT_PHASE_PISTON_REMOVED, menuActionListener));
        popupComplex.add(new JSeparator());
        popupComplex.add(createMenuItem("Real Part", PLOT_REAL_PART, menuActionListener));
        popupComplex.add(createMenuItem("Imaginary Part", PLOT_IMAGINARY_PART, menuActionListener));
    }

    public void acknowledgePlotRequested(DropEvent event) throws DataReaderException {
        DropTarget dropTarget = (DropTarget) event.getSource();
        actionCreatePlot(dropTarget.getComponent(), event.getLocation());
    }

    public JComponent getParameterPanel() {
        if (splitter == null) {
            tree.expandRow(0);
            tree.setRootVisible(false);
            JPanel panel = new JPanel(new MigLayout("", "", ""));
            panel.add(new JScrollPane(tree), "push, grow, wrap");

            splitter = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
            splitter.setTopComponent(panel);
            splitter.setBottomComponent(informationPanel);
        }

        return splitter;
    }

    private JPanel createInformationPanel() {

        JPanel namePanel = new JPanel(new MigLayout("wrap 3", "", ""));
        namePanel.add(new JLabel("Alias: "));
        namePanel.add(fieldAlias, "spanx, w 50:100:250, pushx, growx, wrap");

        namePanel.add(new JLabel("Dimensions: "));
        namePanel.add(spinnerRows, "w 25:50:125, pushx, growx");
        namePanel.add(spinnerCols, "w 25:50:125, pushx, growx, wrap");

        JPanel timesPanel = new JPanel(new MigLayout("", "", ""));
        timesPanel.setBorder(new TitledBorder("Available Times"));
        timesPanel.add(new JScrollPane(timesList), "push, grow");

        JPanel panel = new JPanel(new MigLayout("wrap 1", "", ""));
        panel.add(namePanel, "growx");
        panel.add(timesPanel, "push, grow");
        return panel;
    }

    private void updateDimSpinner(long[] dims) {
        int rowCount = (int) dims[0];
        int colCount = (int) dims[1];
        int nobjs = rowCount * colCount;
        ArrayList<Integer> rowFactors = getFactors(nobjs);
        ArrayList<Integer> colFactors = new ArrayList<Integer>(rowFactors.size());
        if (nobjs == 0) {
            colFactors.add(0);
        } else {
            for (int i = rowFactors.size() - 1; i >= 0; i--) {
                colFactors.add(nobjs / rowFactors.get(i));
            }
        }

        spinnerRows.removeChangeListener(rowChangeListener);
        spinnerCols.removeChangeListener(colChangeListener);

        ArrayList<Integer> rowList = new ArrayList<Integer>();
        for (Integer i : rowFactors) {
            rowList.add(i);
        }
        ((SpinnerListModel) spinnerRows.getModel()).setList(rowList);
        spinnerRows.setValue(rowCount);

        ArrayList<Integer> colList = new ArrayList<Integer>();
        for (Integer i : colFactors) {
            colList.add(i);
        }
        ((SpinnerListModel) spinnerCols.getModel()).setList(colList);
        spinnerCols.setValue(colCount);

        spinnerCols.addChangeListener(colChangeListener);
        spinnerRows.addChangeListener(rowChangeListener);
    }

    private ArrayList<Integer> getFactors(int x) {
        ArrayList<Integer> factors = new ArrayList<Integer>();
        for (int num = 1; num < x / 2 + 1; num++) {
            if (x % num == 0) {
                factors.add(new Integer(num));
            }
        }
        factors.add(new Integer(x));
        return factors;
    }

    private JMenuItem createMenuItem(String label, String actionCommand, ActionListener menuActionListener) {
        JMenuItem item = new JMenuItem(label);
        item.addActionListener(menuActionListener);
        item.setActionCommand(actionCommand);
        return item;
    }

    private void actionCreatePlot(Component invoker, Point location) {
        DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
        if (selectedNode == null) {
            return;
        }

        Object userObject = selectedNode.getUserObject();
        if (userObject instanceof Group) {
            return;
        }

        Dataset dataset = (Dataset) selectedNode.getUserObject();
        LatFileRunTrace runTrace = latFileReader.getRunTrace().get(dataset.getFile() + ":" + dataset.getFullName());

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

        try {
            if (rowCount * colCount == 1) {
                plotSinglePoint(dataset);
            } else if (rowCount == 1) {
                plotMultipleLines(dataset);
            } else {
                plotImage(invoker, location, dataset);
            }
        } catch (Exception ex) {
        }
    }

    private void plotSinglePoint(Dataset dataset) throws OutOfMemoryError, Exception {
        LatFileRunTrace runTrace = latFileReader.getRunTrace().get(dataset.getFile() + ":" + dataset.getFullName());
        String alias = runTrace.getAlias();
        double[] times = runTrace.getSelectedTimes();

        double[][] data;
        if (times == null) {
            data = MatrixMath.transpose(latFileReader.loadVariable(dataset));
        } else {
            data = MatrixMath.transpose((double[][]) latFileReader.loadVariable(dataset, runTrace));
        }

        double[] t = new double[times.length];
        for (int i = 0; i < t.length; i++) {
            t[i] = times[i];
        }
        int plotType = data[0].length == 1 ? LinePlotPanel.SCATTER_PLOT : LinePlotPanel.LINE_PLOT;
        LinePlotPanel linePlotPanel = new LinePlotPanel(plotType);
        linePlotPanel.setPlotTitle(alias);
        linePlotPanel.setData("time", new String[]{alias}, t, data);
        linePlotPanel.setPreferredSize(linePanelDimension);
        dataExplorerInterface.drawPlotFrame(alias, linePlotPanel);
    }

    private void plotMultipleLines(Dataset dataset) throws OutOfMemoryError, Exception {
        LatFileRunTrace runTrace = latFileReader.getRunTrace().get(dataset.getFile() + ":" + dataset.getFullName());
        String alias = runTrace.getAlias();
        double[] times = runTrace.getSelectedTimes();
        if (times == null) {
            Toolkit.getDefaultToolkit().beep();
            NotifyDescriptor nd = new NotifyDescriptor.Message("Unable to plot data because no time information was found.");
            DialogDisplayer.getDefault().notify(nd);
            return;
        }

        Object o = latFileReader.loadVariable(dataset, runTrace);
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
        LinePlotPanel linePlotPanel = new LinePlotPanel();
        linePlotPanel.setPlotTitle(alias);
        linePlotPanel.setData(s, data);
        linePlotPanel.setPreferredSize(linePanelDimension);
        dataExplorerInterface.drawPlotFrame(alias, linePlotPanel);
    }

    private void plotImage(Component invoker, Point pt, Dataset dataset) {
        LatFileRunTrace runTrace = latFileReader.getRunTrace().get(dataset.getFile() + ":" + dataset.getFullName());
        if (runTrace.getType() == TrfConstants.ARRAY_COMPLEX) {
            requestedPlotType = 0;
            popupComplex.show(invoker, pt.x, pt.y);
        } else {
            final ImagePlotPanel imagePlotPanel = new ImagePlotPanel();
            imagePlotPanel.getCanvas().setNavigatableData(new LatNavigatableData(latFileReader, dataset));
            imagePlotPanel.setPreferredSize(imagePanelDimension);
            dataExplorerInterface.drawPlotFrame(runTrace.getAlias(), imagePlotPanel);
        }
    }

    /**
     *
     * @author  <a href="mailto://rstjohn@mza.com">Dr. Richard St. John</a>
     * @company <a href="http://www.mza.com">MZA Associates Corporation</a>
     */
    private class TreeListener extends MouseAdapter implements ListSelectionListener, TreeSelectionListener {

        @Override
        public void mousePressed(MouseEvent e) {
            TreePath selectedPath = tree.getPathForLocation(e.getX(), e.getY());
            if (selectedPath == null) {
                return;
            }
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            if (e.getClickCount() != 2) {
                return;
            }
            actionCreatePlot(e.getComponent(), e.getPoint());
        }

        public void valueChanged(ListSelectionEvent e) {
            if (e.getValueIsAdjusting()) {
                return;
            }

            DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
            if (selectedNode == null) {
                return;
            }
        }

        public void valueChanged(TreeSelectionEvent e) {
            DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
            Object userObject = selectedNode.getUserObject();
            if (!(userObject instanceof HObject)) {
                return;
            }

            HObject hobj = (HObject) userObject;
            if (hobj instanceof Group) {
            } else if (userObject instanceof Dataset) {

                LatFileRunTrace thisRunTrace = latFileReader.getRunTrace().get(hobj.getFile() + ":" + hobj.getFullName());
                if (thisRunTrace == null) {
                    return;
                }

                fieldAlias.setText(thisRunTrace.getAlias());

                double[] allTimes = latFileReader.readVariableTimes(selectedNode);
                double[] selectedTimes = thisRunTrace.getSelectedTimes();
                if (selectedTimes == null) {
                    thisRunTrace.setSelectedTimes(selectedTimes = new double[]{allTimes[0]});
                }

                timesList.removeListSelectionListener(timesListener);
                DefaultListModel listModel = (DefaultListModel) timesList.getModel();
                listModel.clear();
                if (userObject instanceof Group || allTimes == null || allTimes.length < 1) {
                    timesList.addListSelectionListener(timesListener);
                    return;
                }

                for (int i = 0; i < allTimes.length; i++) {
                    listModel.addElement(String.valueOf(allTimes[i]));
                }

                int[] selectedIndices = new int[selectedTimes.length];
                for (int i = 0; i < selectedTimes.length; i++) {
                    selectedIndices[i] = listModel.indexOf(String.valueOf(selectedTimes[i]));
                }
                timesList.setSelectedIndices(selectedIndices);
                timesList.addListSelectionListener(timesListener);

                long[][] dims = latFileReader.getDimensionsForSelectedTime(hobj, timesList.getSelectedIndices());
                if (dims == null || dims.length < 1 || dims[0].length < 1) {
                    latFileReader.updateRunTrace((DefaultTreeModel) tree.getModel());
                    dims = latFileReader.getDimensionsForSelectedTime(hobj, timesList.getSelectedIndices());
                }
                thisRunTrace.setSelectedDims(dims);
                updateDimSpinner(dims[0]);
            }
        }
    }

    /**
     *
     * @author Dr. Richard St. John
     * @version $Revision$, $Date$
     */
    private class RowChangeListener implements ChangeListener {

        public void stateChanged(ChangeEvent e) {
            DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
            if (selectedNode == null || !selectedNode.isLeaf()) {
                return;
            }
            if (selectedNode.getUserObject() instanceof TrfDataObject) {
                TrfDataObject trfTreeNode = (TrfDataObject) selectedNode.getUserObject();

                Integer val = (Integer) spinnerRows.getValue();
                Integer prevVal = trfTreeNode.getRowCount();
                Integer newVal;
                if (val == prevVal) {
                    return;
                }
                spinnerCols.removeChangeListener(colChangeListener);
                if (prevVal < val) {
                    newVal = spinnerCols.getPreviousValue() == null ? 1 : (Integer) spinnerCols.getPreviousValue();
                } else if (prevVal > val) {
                    newVal = (spinnerCols.getNextValue() == null ? trfTreeNode.getNobjs() : (Integer) spinnerCols.getNextValue());
                } else {
                    newVal = val;
                }
                spinnerCols.setValue(newVal);
                spinnerCols.addChangeListener(colChangeListener);

                trfTreeNode.setColCount(newVal);
                trfTreeNode.setRowCount(val);
            } else if (selectedNode.getUserObject() instanceof HObject) {
            }
        }
    }

    /**
     *
     * @author Dr. Richard St. John
     * @version $Revision$, $Date$
     */
    private class ColChangeListener implements ChangeListener {

        public void stateChanged(ChangeEvent e) {
            DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
            if (selectedNode == null || !selectedNode.isLeaf()) {
                return;
            }
            if (selectedNode.getUserObject() instanceof TrfDataObject) {
                TrfDataObject trfTreeNode = (TrfDataObject) selectedNode.getUserObject();

                Integer val = (Integer) spinnerCols.getValue();
                Integer prevVal = trfTreeNode.getColCount();
                Integer newVal;
                if (val == prevVal) {
                    return;
                }
                spinnerRows.removeChangeListener(rowChangeListener);
                if (prevVal < val) {
                    newVal = spinnerRows.getPreviousValue() == null ? 1 : (Integer) spinnerRows.getPreviousValue();
                } else if (prevVal > val) {
                    newVal = spinnerRows.getNextValue() == null ? trfTreeNode.getNobjs() : (Integer) spinnerRows.getNextValue();
                } else {
                    newVal = val;
                }
                spinnerRows.setValue(newVal);
                spinnerRows.addChangeListener(rowChangeListener);

                trfTreeNode.setColCount(val);
                trfTreeNode.setRowCount(newVal);
            } else if (selectedNode.getUserObject() instanceof HObject) {
            }
        }
    }

    /**
     *
     * @author  <a href="mailto://rstjohn@mza.com">Dr. Richard St. John</a>
     * @company <a href="http://www.mza.com">MZA Associates Corporation</a>
     */
    private class TimesListListener implements ListSelectionListener {

        public void valueChanged(ListSelectionEvent e) {
            DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
            if (selectedNode == null || !selectedNode.isLeaf()) {
                return;
            }

            if (selectedNode.getUserObject() instanceof HObject) {
                HObject hobject = (HObject) selectedNode.getUserObject();
                Object[] selectedValues = timesList.getSelectedValues();
                double[] selectedTimes = new double[selectedValues.length];
                for (int i = 0; i < selectedTimes.length; i++) {
                    selectedTimes[i] = Double.parseDouble(selectedValues[i].toString());
                }
                LatFileRunTrace runTrace = latFileReader.getRunTrace().get(hobject.getFile() + ":" + hobject.getFullName());
                runTrace.setSelectedTimes(selectedTimes);

                long[][] dims = latFileReader.getDimensionsForSelectedTime(hobject, timesList.getSelectedIndices());
                runTrace.setSelectedDims(dims);
                updateDimSpinner(dims[0]);
            } else if (selectedNode.getUserObject() instanceof TrfDataObject) {
                TrfDataObject trfTreeNode = (TrfDataObject) selectedNode.getUserObject();
                Object[] selectedValues = timesList.getSelectedValues();
                float[] selectedTimes = new float[selectedValues.length];
                for (int i = 0; i < selectedTimes.length; i++) {
                    selectedTimes[i] = Float.parseFloat(selectedValues[i].toString());
                }
                trfTreeNode.setSelectedTimes(selectedTimes);
            }
        }
    }

    /**
     *
     * @author  <a href="mailto://rstjohn@mza.com">Dr. Richard St. John</a>
     * @company <a href="http://www.mza.com">MZA Associates Corporation</a>
     */
    private class AliasFieldListener implements KeyListener {

        public void keyPressed(KeyEvent e) {
        }

        public void keyTyped(KeyEvent e) {
        }

        public void keyReleased(KeyEvent e) {
            String s = fieldAlias.getText().trim();
            Object userObject = ((DefaultMutableTreeNode) tree.getLastSelectedPathComponent()).getUserObject();

            if (userObject instanceof HObject) {
                HObject hobject = (HObject) userObject;
                LatFileRunTrace thisRunTrace = latFileReader.getRunTrace().get(hobject.getFile() + ":" + hobject.getFullName());
                if (s.equalsIgnoreCase("") || s.equalsIgnoreCase("<alias>")) {
                    String varName = hobject.toString();
                    thisRunTrace.setAlias(varName);
                    return;
                }
                thisRunTrace.setAlias(s);
            } else if (userObject instanceof TrfDataObject) {
                TrfDataObject trfTreeNode = (TrfDataObject) userObject;
                if (s.equalsIgnoreCase("") || s.equalsIgnoreCase("<alias>")) {
                    String varName = trfTreeNode.toString();
                    varName = varName.substring(varName.lastIndexOf(".") + 1);
                    trfTreeNode.setAlias(varName);
                    return;
                }
                trfTreeNode.setAlias(s);
            }
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
            if (command.equalsIgnoreCase(PLOT_AMPLITUDE)) {
                actionComplexImageDropped(AMPLITUDE_PLOT);
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

        public void actionComplexImageDropped(int requestedPlotType) {
            if (requestedPlotType == 0) {
                return;
            }
            DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
            if (selectedNode == null) {
                return;
            }

            Object userObject = selectedNode.getUserObject();
            if (userObject instanceof Group) {
                return;
            }

            Dataset dataset = (Dataset) selectedNode.getUserObject();

            final ImagePlotPanel imagePlotPanel = new ImagePlotPanel();
            AbstractNavigatableData navData = new LatNavigatableData(latFileReader, dataset, requestedPlotType);
            imagePlotPanel.getCanvas().setNavigatableData(navData);
            String plotID = navData.getID().substring(0, navData.getID().indexOf(": (time="));
            imagePlotPanel.setPreferredSize(imagePanelDimension);
            dataExplorerInterface.drawPlotFrame(plotID, imagePlotPanel);
        }
    }
}
