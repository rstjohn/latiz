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
import com.AandR.library.gui.DragAndDropTree;
import com.AandR.library.gui.DropEvent;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Vector;

import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import ncsa.hdf.hdf5lib.exceptions.HDF5Exception;
import ncsa.hdf.object.Dataset;
import ncsa.hdf.object.FileFormat;
import ncsa.hdf.object.Group;
import ncsa.hdf.object.h5.H5CompoundDS;
import ncsa.hdf.object.h5.H5File;
import ncsa.hdf.object.h5.H5ScalarDS;
import net.miginfocom.swing.MigLayout;
import org.openide.util.ImageUtilities;

/**
 * @author  <a href="mailto://rstjohn@mza.com">Dr. Richard St. John</a>
 * @company <a href="http://www.mza.com">MZA Associates Corporation</a>
 */
public class HelcomesHDF5Reader extends AbstractDataReader {

    private DragAndDropTree tree;
    private FileFormat h5file;
    private HashMap<DefaultMutableTreeNode, RunTrace> runTraceMap;
    private JList parametersList;
    private JPanel parameterPanel, informationPanel;

    public HelcomesHDF5Reader() {
        icon = new ImageIcon(ImageUtilities.loadImage("com/AandR/beans/plotting/resource/latizIcon22.png"));
        runTraceMap = new HashMap<DefaultMutableTreeNode, RunTrace>();
    }

    /**
     *
     */
    @SuppressWarnings(value = "unchecked")
    public void initialize(File file) throws DataReaderException {
        runTraceMap.clear();

        DefaultMutableTreeNode fileRoot;
        h5file = new H5File(file.getPath(), H5File.READ);
        try {
            h5file.createFile(file.getPath(), FileFormat.FILE_CREATE_OPEN);
            h5file.open();
            fileRoot = (DefaultMutableTreeNode) h5file.getRootNode();
        } catch (Exception e) {
            throw new DataReaderException(this, "Could not create HDF5 file.");
        }

        // Trace run
        Object userObject;
        DefaultMutableTreeNode thisNode;
        Enumeration<DefaultMutableTreeNode> nodeEnum = fileRoot.breadthFirstEnumeration();
        while (nodeEnum.hasMoreElements()) {
            thisNode = nodeEnum.nextElement();
            if (!thisNode.isLeaf()) {
                continue;
            }

            userObject = thisNode.getUserObject();
            if (!(userObject instanceof Dataset)) {
                continue;
            }

            ((Dataset) userObject).init();
            long[] dims = ((Dataset) userObject).getDims();
            runTraceMap.put(thisNode, new RunTrace(dims));
        }

        try {
            h5file.close();
        } catch (Exception e) {
            throw new DataReaderException(this, "Could not close HDF5 file.");
        }

        TreeListener treeListener = new TreeListener();
        parameterPanel = new JPanel(new MigLayout("fill", "", ""));
        parameterPanel.add(new JLabel("Select from parameters"), "pushx, growx, wrap");
        parametersList = new JList(new DefaultListModel());
        parametersList.addListSelectionListener(treeListener);
        parameterPanel.add(new JScrollPane(parametersList), "push, grow");

        informationPanel = new JPanel(new CardLayout());
        informationPanel.add(parameterPanel, "PARAMETER_PANEL");
        informationPanel.add(new JLabel("No Parameter Panel"), "EMPTY_PANEL");

        tree = new DragAndDropTree(new DefaultTreeModel(fileRoot));
        tree.setCellRenderer(new HDF5TreeRenderer());
        tree.addMouseListener(treeListener);
        tree.addTreeSelectionListener(treeListener);
    }

    public void acknowledgePlotRequested(DropEvent event) throws DataReaderException {
        TreePath selectedPath = tree.getSelectionPath();
        if (selectedPath == null) {
            return;
        }

        DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) selectedPath.getLastPathComponent();
        if (selectedNode == null) {
            return;
        }

        Object userObject = selectedNode.getUserObject();
        if (!(userObject instanceof H5CompoundDS)) {
            return;
        }

        dataExplorerInterface.drawPlotFrame("Test Button", createLinePlotPanel((H5CompoundDS) userObject));
    }

    /**
     *
     */
    public JComponent getParameterPanel() {
        JScrollPane scroller = new JScrollPane(tree);
        scroller.setBorder(null);
        JPanel panel = new JPanel(new MigLayout("", "0[]0", "0[]0"));
        panel.add(scroller, "gaptop 10, gapleft 5, push, grow, wrap");

        JSplitPane splitter = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitter.setTopComponent(panel);
        splitter.setBottomComponent(informationPanel);

        return splitter;
    }

    /**
     *
     * @param dataset
     * @return
     * @throws DataReaderException
     */
    private LinePlotPanel createLinePlotPanel(H5CompoundDS dataset) throws DataReaderException {
        int[] selectedIndices = parametersList.getSelectedIndices();

        try {
            h5file.open();
        } catch (Exception e) {
            throw new DataReaderException(this, "Error opening HDF5 file.");
        }

        dataset.init();
        int dataid = dataset.open();

        long[] selected = dataset.getSelectedDims();
        selected[0] = 1;

        ArrayList<double[]> dataList = new ArrayList<double[]>();
        ArrayList<String> labelList = new ArrayList<String>();
        try {
            long[] start = dataset.getStartDims();
            start[1] = 0;
            for (int i = 0; i < selectedIndices.length; i++) {
                start[0] = selectedIndices[i];
                Object objectData = ((Vector) dataset.read()).get(1);
                dataList.add((double[]) objectData);
                labelList.add("p" + String.valueOf(selectedIndices[i] + 1));
            }
        } catch (HDF5Exception e) {
            throw new DataReaderException(this, "Error reading dataset.");
        }

        try {
            dataset.close(dataid);
            h5file.close();
        } catch (Exception e) {
            throw new DataReaderException(this, "Error closing HDF5 file.");
        }

        String[] labels = new String[labelList.size()];
        double[][] outData = new double[dataList.size()][];

        LinePlotPanel plotPanel = new LinePlotPanel();
        plotPanel.setData(labelList.toArray(labels), dataList.toArray(outData));

        return plotPanel;
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
            updateParameterPanel(selectedPath);
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            if (e.getClickCount() != 2) {
                return;
            }

            TreePath selectedPath = tree.getPathForLocation(e.getX(), e.getY());
            if (selectedPath == null) {
                return;
            }

            DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) selectedPath.getLastPathComponent();
            if (selectedNode == null) {
                return;
            }

            Object userObject = selectedNode.getUserObject();
            if (userObject instanceof H5CompoundDS) {
                H5CompoundDS ds = (H5CompoundDS) userObject;
                JPanel plotPanel;
                try {
                    plotPanel = createLinePlotPanel((H5CompoundDS) selectedNode.getUserObject());
                } catch (DataReaderException e1) {
                    e1.printStackTrace();
                    return;
                }
                dataExplorerInterface.drawPlotFrame(ds.getName(), plotPanel);
            }
        }

        public void valueChanged(ListSelectionEvent e) {
            if (e.getValueIsAdjusting()) {
                return;
            }

            TreePath selectedPath = tree.getSelectionPath();
            if (selectedPath == null) {
                return;
            }

            DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) selectedPath.getLastPathComponent();
            if (selectedNode == null) {
                return;
            }

            runTraceMap.get(selectedNode).selectedIndices = parametersList.getSelectedIndices();
        }

        public void valueChanged(TreeSelectionEvent e) {
            updateParameterPanel(e.getPath());
        }

        private void updateParameterPanel(TreePath selectedPath) {
            DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) selectedPath.getLastPathComponent();
            if (selectedNode == null) {
                return;
            }

            ((CardLayout) informationPanel.getLayout()).show(informationPanel, selectedNode.isLeaf() ? "PARAMETER_PANEL" : "EMPTY_PANEL");

            // Get the RunTrace for this node or create one if it does not exist.
            RunTrace selectedRunTrace = runTraceMap.get(selectedNode);
            if (selectedRunTrace == null) {
                return;
            }

            // Repopulate parameter list
            parametersList.removeListSelectionListener(this);
            DefaultListModel model = (DefaultListModel) parametersList.getModel();
            model.clear();
            for (int i = 0; i < selectedRunTrace.dims[0]; i++) {
                model.addElement((i + 1));
            }
            parametersList.setSelectedIndices(selectedRunTrace.selectedIndices);
            parametersList.addListSelectionListener(this);
        }
    }

    /**
     *
     * @author  <a href="mailto://rstjohn@mza.com">Dr. Richard St. John</a>
     * @company <a href="http://www.mza.com">MZA Associates Corporation</a>
     */
    private class HDF5TreeRenderer extends DefaultTreeCellRenderer {

        private ImageIcon iconTable, iconDataset, iconFolder;

        public HDF5TreeRenderer() {
            iconTable = new ImageIcon(ImageUtilities.loadImage("com/AandR/beans/plotting/resource/table.gif"));
            iconDataset = new ImageIcon(ImageUtilities.loadImage("com/AandR/beans/plotting/resource/dataset.gif"));
            iconFolder = new ImageIcon(ImageUtilities.loadImage("com/AandR/beans/plotting/resource/folderYellow22.png"));
        }

        @Override
        public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
            super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);

            DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
            Object userObject = node.getUserObject();

            // HDF5 File - Dataset
            if (userObject instanceof Dataset) {
                if (userObject instanceof H5CompoundDS) {
                    setIcon(iconTable);
                } else if (userObject instanceof H5ScalarDS) {
                    setIcon(iconDataset);
                } else if (userObject instanceof H5CompoundDS) {
                    setIcon(iconTable);
                }
            } // HDF5 File - Group
            else if (userObject instanceof Group) {
                setIcon(node.isRoot() ? icon : iconFolder);
            }
            return this;
        }
    }

    /**
     *
     * @author  <a href="mailto://rstjohn@mza.com">Dr. Richard St. John</a>
     * @company <a href="http://www.mza.com">MZA Associates Corporation</a>
     */
    private class RunTrace {

        public long[] dims;
        public int[] selectedIndices;

        public RunTrace(long[] dims) {
            this.dims = dims;
            this.selectedIndices = (dims == null || dims[0] * dims[1] == 0) ? new int[]{} : new int[]{0};
        }
    }
}
