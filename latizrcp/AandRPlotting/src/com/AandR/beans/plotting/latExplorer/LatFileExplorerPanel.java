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

import com.AandR.library.gui.DropEvent;
import com.AandR.library.gui.DropListener;
import com.AandR.library.gui.JTextFieldWithDropSupport;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
//import java.util.Vector;

import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
//import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerListModel;
import javax.swing.border.CompoundBorder;
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
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import ncsa.hdf.object.Attribute;
import ncsa.hdf.object.Dataset;
import ncsa.hdf.object.FileFormat;
import ncsa.hdf.object.Group;
import ncsa.hdf.object.HObject;
import ncsa.hdf.object.h5.H5File;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;

/**
 * @author Dr. Richard St. John
 * @version $Revision$, $Date$
 */
public class LatFileExplorerPanel extends JPanel {

    public static final int VERTICAL = JSplitPane.VERTICAL_SPLIT;
    public static final int HORIZONTAL = JSplitPane.HORIZONTAL_SPLIT;
    private int orientation;
    private boolean showAddToAvailableOutputs = true;
    private ArrayList<LatFileNodeListener> h5NodeListeners;
    private ChangeListener rowChangeListener,  colChangeListener;
    private File currentFile;
    private HashSet<Object> savedOutputsSet;
    private LatFileReader latFileReader;
    private LatFileTree tree;

    //private JCheckBox checkboxAddToOutputs;
    private JList timesList,  parametersList,  realizationsList,  columnsList;
    private JPanel cards;
    private JPopupMenu popupHdf,  popupTrf;
    private JSpinner spinnerRows,  spinnerCols;
    private JSplitPane treeSplitter;
    private JTextField fieldAlias;
    private JTextFieldWithDropSupport fieldFile;
    private ListSelectionListener timesListener,  columnsListener,  realizationsListener,  parametersListener;
    private TreeListener treeListener;
    private TrfReader trfReader;

    /**
     *
     * @param orientation
     */
    public LatFileExplorerPanel(int orientation) {
        this.orientation = orientation;
        initialize();
        setLayout(new BorderLayout());
        add(createContentPane());
        setMinimumSize(new Dimension(50, 10));
    }

    /**
     *
     */
    public LatFileExplorerPanel() {
        this(VERTICAL);
    }

    private void initialize() {
        h5NodeListeners = new ArrayList<LatFileNodeListener>();
        savedOutputsSet = new HashSet<Object>();
        treeListener = new TreeListener();

        popupHdf = createPopupHdf();
        popupTrf = createPopupTrf();

        tree = new LatFileTree(new DefaultMutableTreeNode("Available Data"));
        tree.setLargeModel(true);
        tree.setShowsRootHandles(true);
        int rowheight = 23 + (int) ((tree.getFont().getSize() - 12) * 0.5);
        tree.setRowHeight(rowheight);

        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        tree.setBorder(new EmptyBorder(2, 7, 0, 0));
        tree.addMouseListener(treeListener);
        tree.addTreeSelectionListener(treeListener);

        fieldFile = new JTextFieldWithDropSupport("<Drop HDF5 File Here>", 20);
        fieldFile.addDropListener(new FileFieldListener());

        rowChangeListener = new RowChangeListener();
        colChangeListener = new ColChangeListener();

        timesList = new JList(new DefaultListModel());
        timesList.addListSelectionListener(timesListener = new TimesListListener());

        fieldAlias = createTextField("<alias>");
        fieldAlias.addKeyListener(new AliasFieldListener());

        //fieldDataType = createTextField("<data type>");
        //fieldDataType.setEditable(false);

        //checkboxAddToOutputs = new JCheckBox("Add to available outputs");
        //checkboxAddToOutputs.setActionCommand("SAVE_TO_OUTPUTS_MAP");
        //checkboxAddToOutputs.addActionListener(treeListener);

        trfReader = new TrfReader();
        latFileReader = new LatFileReader();

    }

    /**
     *
     * @param hobject
     * @return
     */
    public LatFileRunTrace getRunTrace(HObject hobject) {
        return latFileReader.getRunTrace().get(hobject.getFile() + ":" + hobject.getFullName());
    }

    /**
     *
     * @param listener
     */
    public void addH5PopupListener(LatFileNodeListener listener) {
        h5NodeListeners.add(listener);
    }

    private void notifyH5PopupListener(String actionCommand, DefaultMutableTreeNode node) {
        for (LatFileNodeListener l : h5NodeListeners) {
            l.itemSelected(actionCommand, node);
        }
    }

    /**
     *
     * @param file
     */
    public void loadFile(File file) {
        FileInputStream stream = null;
        String magicHeader = "";
        try {
            byte[] b = new byte[8];
            stream = new FileInputStream(file);
            stream.read(b);
            magicHeader = new String(b);
        } catch (Exception e1) {
            e1.printStackTrace();
        }

        if (magicHeader.equalsIgnoreCase("#!MZATRF")) {
            loadTrfFile(file);
        } else if (magicHeader.substring(1).startsWith("HDF")) {
            try {
                loadHdf5File(file);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            NotifyDescriptor nd = new NotifyDescriptor.Message("<HTML>The dropped file:<BR><BR><B>" + file.getPath() + "</B><BR><BR>is not a valid HDF5, TRF, or ACS-H5 file.</HTML>");
            DialogDisplayer.getDefault().notify(nd);
            return;
        }
        fieldFile.setText(file.getPath());
        currentFile = file;
    }

    private void loadHdf5File(File hdf5File) throws Exception {
        if (latFileReader == null) {
            latFileReader = new LatFileReader();
        }

        // Load HDF5 file.
        fieldFile.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        latFileReader.loadFile(hdf5File, (DefaultTreeModel) tree.getModel());
        fieldFile.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        tree.setRunTraces(latFileReader.getRunTrace());

        // Hide root node.
        if (tree.isRootVisible()) {
            tree.expandRow(0);
            tree.setRootVisible(false);
        }

        // Get this HDF5 file's root node information (if it exists)
        DefaultMutableTreeNode fileRoot = (DefaultMutableTreeNode) latFileReader.getH5File().getRootNode();
        HObject rootObject = (HObject) fileRoot.getUserObject();
        tree.setSelectionPath(new TreePath(fileRoot.getPath()));
        CardLayout cl = (CardLayout) (cards.getLayout());
        cl.show(cards, "GROUP_INFORMATION");

        //List<Attribute> attributes = rootObject.getMetadata();
        List<Attribute> attributes = latFileReader.getMetadata(rootObject);
        if (attributes.size() != 3) {
            return;
        }

        // This node is an ACS-H5 file.
        if (attributes.get(0).getName().equals("ACS")) {
            refreshACSfileInformation(rootObject);
        }
    }

    private void loadTrfFile(File file) {
        if (trfReader == null) {
            trfReader = new TrfReader();
        }

        fieldFile.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        trfReader.loadFile(file, (DefaultTreeModel) tree.getModel());
        fieldFile.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));

        // Hide root node.
        if (tree.isRootVisible()) {
            tree.expandRow(0);
            tree.setRootVisible(false);
        }
    }

    private void refreshACSfileInformation(HObject rootObject) throws Exception {
        parametersList.removeListSelectionListener(parametersListener);
        List<Attribute> attributes = latFileReader.getMetadata(rootObject);

        Attribute parameterAttribute = attributes.get(2);
        int parameterCount = ((int[]) parameterAttribute.getValue())[0];
        DefaultListModel model = (DefaultListModel) parametersList.getModel();
        model.clear();
        for (int i = 0; i < parameterCount; i++) {
            model.addElement(i + 1);
        }
        parametersList.setSelectedIndex(0);
        parametersList.addListSelectionListener(parametersListener);

        realizationsList.removeListSelectionListener(realizationsListener);
        Attribute realizationAttribute = attributes.get(1);
        int realizationCount = ((int[]) realizationAttribute.getValue())[0];
        model = (DefaultListModel) realizationsList.getModel();
        model.clear();
        for (int i = 0; i < realizationCount; i++) {
            model.addElement(i + 1);
        }
        realizationsList.setSelectedIndex(0);
        realizationsList.addListSelectionListener(realizationsListener);

        savedOutputsSet.clear();
        notifySaveOutputListeners();
    }

    /**
     *
     * @return
     */
    public DefaultMutableTreeNode getSelectedNode() {
        return (DefaultMutableTreeNode) tree.getSelectionPath().getLastPathComponent();
    }

    /**
     *
     * @return
     */
    public HashSet<Object> getSavedOutputsSet() {
        return savedOutputsSet;
    }

    /**
     *
     * @return
     */
    public JTextFieldWithDropSupport getFieldLatFile() {
        return fieldFile;
    }

    /**
     *
     * @param hobject
     * @param runTrace
     * @return
     * @throws OutOfMemoryError
     * @throws Exception
     */
    public Object getData(HObject hobject, LatFileRunTrace runTrace) throws OutOfMemoryError, Exception {
        return latFileReader.loadVariable(hobject, runTrace);
    }

    /**
     *
     * @param hobject
     * @param runTrace
     * @param selectedTimes
     * @return
     * @throws OutOfMemoryError
     * @throws Exception
     */
    public Object getData(HObject hobject, LatFileRunTrace runTrace, double[] selectedTimes) throws OutOfMemoryError, Exception {
        return latFileReader.loadVariable(hobject, runTrace, selectedTimes);
    }

    /**
     *
     * @param trfTreeNode
     * @param timeIndex
     * @return
     * @throws IOException
     */
    public double[][] getData(TrfDataObject trfTreeNode, int timeIndex) throws IOException {
        int rowCount = trfTreeNode.getRowCount();
        int colCount = trfTreeNode.getColCount();
        float[] times = trfTreeNode.getSelectedTimes();
        if (rowCount * colCount == 1) {
            return trfReader.loadVariable(trfTreeNode, times);
        } else if (rowCount == 1) {
            return trfReader.loadVariable(trfTreeNode, times);
        } else {
            return trfReader.loadVariable(trfTreeNode, rowCount, colCount, times[timeIndex]);
        }
    }

    /**
     *
     * @param trfTreeNode
     * @return
     * @throws IOException
     */
    public double[][] getData(TrfDataObject trfTreeNode) throws IOException {
        return getData(trfTreeNode, 0);
    }

    /**
     *
     * @param trfTreeNode
     * @param time
     * @return
     * @throws IOException
     */
    public double[][] getData(TrfDataObject trfTreeNode, float time) throws IOException {
        int timeIndex = -1;
        float[] times = trfTreeNode.getSelectedTimes();
        for (int i = 0; i < times.length; i++) {
            if (times[i] == time) {
                timeIndex = i;
                break;
            }
        }
        return getData(trfTreeNode, timeIndex);
    }

    /**
     *
     * @param trfTreeNode
     * @return
     * @throws IOException
     */
    public Object getParameterData(TrfDataObject trfTreeNode) throws IOException {
        return trfReader.loadParameter(trfTreeNode);
    }

    /**
     *
     * @return
     */
    public float[] getSelectedTimes() {
        Object[] o = timesList.getSelectedValues();
        float[] times = new float[o.length];
        for (int i = 0; i < times.length; i++) {
            times[i] = new Float(o[i].toString());
        }
        return times;
    }

    /**
     *
     * @return
     */
    public int[] getSelectedTimesIndices() {
        return timesList.getSelectedIndices();
    }

    /**
     *
     * @return
     */
    public File getCurrentFile() {
        return currentFile;
    }

    private Container createContentPane() {
        treeSplitter = new JSplitPane(orientation, true);
        treeSplitter.setLeftComponent(createTreePanel());
        treeSplitter.setRightComponent(createInformationPanel());
        int dividerLocation = orientation == HORIZONTAL ? 500 : 400;
        treeSplitter.setDividerLocation(dividerLocation);
        treeSplitter.setOneTouchExpandable(true);
        return treeSplitter;
    }

    private Component createTreePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(fieldFile, BorderLayout.NORTH);
        panel.add(new JScrollPane(tree), BorderLayout.CENTER);
        return panel;
    }

    /**
     * creates a popup menu for a right mouse click on a data object
     */
    private JPopupMenu createPopupHdf() {
        if (popupHdf != null) {
            popupHdf.removeAll();
        }
        JPopupMenu menu = new JPopupMenu();

        menu.add(createMenuItem("Open", null, KeyEvent.VK_O, "Open data"));
        menu.addSeparator();

//        JMenu newOjbectMenu = new JMenu("New");
//        menu.add(newOjbectMenu);
//        newOjbectMenu.add(createMenuItem("Group", Resources.createIcon("folderclose.gif"), -1, "Add group"));
//        newOjbectMenu.add(createMenuItem("Dataset", Resources.createIcon("dataset.gif"), -1, "Add dataset"));
//        newOjbectMenu.add(createMenuItem("Image", Resources.createIcon("image.gif"), -1, "Add image"));
//        newOjbectMenu.add(createMenuItem("Table", Resources.createIcon("table.gif"), -1, "Add table"));
//        newOjbectMenu.add(createMenuItem("Datatype", Resources.createIcon("datatype.gif"), -1, "Add datatype"));
//        newOjbectMenu.add(createMenuItem("Link", Resources.createIcon("link.gif"), -1, "Add link"));
//
//        menu.addSeparator();
//        menu.add(createMenuItem("Copy", null, KeyEvent.VK_C, "Copy object"));
//        menu.add(createMenuItem("Paste", null, KeyEvent.VK_P, "Paste object"));
//        menu.add(createMenuItem("Delete", null, KeyEvent.VK_D, "Cut object"));
//        menu.add(createMenuItem("Reload File", null, KeyEvent.VK_L, "RELOAD"));
        if (showAddToAvailableOutputs) {
            menu.add(createMenuItem("Add To Available Outputs", null, -1, "SAVE_HDF_TO_OUTPUTS"));
            menu.add(createMenuItem("Remove From Available Outputs", null, -1, "REMOVE_HDF_FROM_OUTPUTS"));
            menu.addSeparator();
        }
        menu.add(createMenuItem("Remove", null, -1, "Remove"));
        menu.addSeparator();
        menu.add(createMenuItem("Save to", null, KeyEvent.VK_S, "Save object to file"));
        //menu.add(createMenuItem("Rename", null, KeyEvent.VK_R, "Rename object"));

        menu.addSeparator();
        menu.add(createMenuItem("Show Properties", null, -1, "Show object properties"));
//  menu.add(createMenuItem("Show Properties As", null, -1, "Show object properties as"));
//  menu.addSeparator();
//  menu.add(createMenuItem("Close File", null, KeyEvent.VK_F, "Close file"));

        /*
        newOjbectMenu.getMenuComponent(1).setEnabled(false);
        newOjbectMenu.getMenuComponent(2).setEnabled(false);
        newOjbectMenu.getMenuComponent(3).setEnabled(false);
        newOjbectMenu.getMenuComponent(4).setEnabled(false);
        newOjbectMenu.getMenuComponent(5).setEnabled(false);

        menu.getComponent(4).setEnabled(false);
        menu.getComponent(5).setEnabled(false);
        menu.getComponent(10).setEnabled(false);
         */

        return menu;
    }

    private JPopupMenu createPopupTrf() {
        if (popupTrf != null) {
            popupTrf.removeAll();
        }
        JPopupMenu menu = new JPopupMenu();
        if (isShowAddToAvailableOutputs()) {
            menu.add(createMenuItem("Add To Available Outputs", null, -1, "SAVE_TRF_TO_OUTPUTS"));
            menu.add(createMenuItem("Remove From Available Outputs", null, -1, "REMOVE_TRF_FROM_OUTPUTS"));
            menu.add(new JSeparator());
        }
        menu.add(createMenuItem("Remove", null, -1, "Remove"));
        return menu;
    }

    private JMenuItem createMenuItem(String label, ImageIcon icon, int mnemonic, String actionCommand) {
        JMenuItem item = new JMenuItem(label, icon);
        if (mnemonic > 0) {
            item.setMnemonic(mnemonic);
        }
        item.addActionListener(treeListener);
        item.setActionCommand(actionCommand);
        return item;
    }

    private Component createImageInformationPanel() {
        JPanel namePanel = new JPanel(new BorderLayout());
        namePanel.add(createLabel("Variable Alias: "), BorderLayout.WEST);
        namePanel.add(fieldAlias, BorderLayout.CENTER);
        namePanel.setBorder(new EmptyBorder(5, 10, 0, 0));

        JPanel dimPanel = new JPanel(new BorderLayout());
        dimPanel.add(createLabel("Dimensions: "), BorderLayout.WEST);
        JPanel spinnerPanel = new JPanel(new GridLayout(1, 2, 5, 5));
        ArrayList<Integer> initialList = new ArrayList<Integer>();
        initialList.add(1);
        spinnerRows = new JSpinner(new SpinnerListModel(initialList));
        spinnerRows.addChangeListener(rowChangeListener);
        spinnerRows.setPreferredSize(new Dimension(100, 22));
        spinnerRows.setMinimumSize(new Dimension(100, 22));
        // spinnerRows.setEnabled(false);

        ArrayList<Integer> initialColList = new ArrayList<Integer>();
        initialColList.add(1);
        spinnerCols = new JSpinner(new SpinnerListModel(initialColList));
        spinnerCols.addChangeListener(colChangeListener);
        spinnerCols.setPreferredSize(new Dimension(100, 22));
        spinnerCols.setMinimumSize(new Dimension(100, 22));
        // spinnerCols.setEnabled(false);

        spinnerPanel.add(spinnerRows);
        spinnerPanel.add(spinnerCols);
        dimPanel.add(spinnerPanel, BorderLayout.CENTER);
        dimPanel.setBorder(new EmptyBorder(0, 10, 0, 0));

        JPanel northPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        northPanel.add(namePanel);
        northPanel.add(dimPanel);

        JPanel centerPanel = new JPanel(new BorderLayout(5, 5));
        JScrollPane scroller = new JScrollPane(timesList);
        scroller.setMinimumSize(new Dimension(10, 50));
        scroller.setBorder(new TitledBorder("Available Times"));
        centerPanel.add(scroller, BorderLayout.CENTER);

        JPanel p = new JPanel(new BorderLayout(5, 5));
        p.add(northPanel, BorderLayout.NORTH);
        p.add(centerPanel, BorderLayout.CENTER);
        return p;
    }

    private Component createGroupPanel() {
        return new JPanel();
    }

    private Component createAsciiInformationPanel() {
        parametersList = new JList(new DefaultListModel());
        parametersList.addListSelectionListener(parametersListener = new ListSelectionListener() {

            public void valueChanged(ListSelectionEvent e) {
                DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
                if (selectedNode == null || !selectedNode.isLeaf()) {
                    return;
                }
                HObject hobject = (HObject) selectedNode.getUserObject();
                LatFileRunTrace runTrace = latFileReader.getRunTrace().get(hobject.getFile() + ":" + hobject.getFullName());
                runTrace.setSelectedParameters(parametersList.getSelectedIndices());
            }
        });

        realizationsList = new JList(new DefaultListModel());
        realizationsList.addListSelectionListener(realizationsListener = new ListSelectionListener() {

            public void valueChanged(ListSelectionEvent e) {
                DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
                if (selectedNode == null || !selectedNode.isLeaf()) {
                    return;
                }
                HObject hobject = (HObject) selectedNode.getUserObject();
                LatFileRunTrace runTrace = latFileReader.getRunTrace().get(hobject.getFile() + ":" + hobject.getFullName());
                runTrace.setSelectedRealizations(realizationsList.getSelectedIndices());
            }
        });

        columnsList = new JList(new DefaultListModel());
        columnsList.addListSelectionListener(columnsListener = new ListSelectionListener() {

            public void valueChanged(ListSelectionEvent e) {
                DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
                if (selectedNode == null || !selectedNode.isLeaf()) {
                    return;
                }
                HObject hobject = (HObject) selectedNode.getUserObject();
                LatFileRunTrace runTrace = latFileReader.getRunTrace().get(hobject.getFile() + ":" + hobject.getFullName());
                runTrace.setSelectedColumns(columnsList.getSelectedIndices());
            }
        });

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        c.anchor = GridBagConstraints.FIRST_LINE_START;
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1.0;
        c.weighty = 1.0;

        c.gridx = 0;
        c.gridy = 0;
        panel.add(createListPanel("Realizations", realizationsList), c);

        c.gridx = 0;
        c.gridy = 1;
        panel.add(createListPanel("Parameters", parametersList), c);

        c.gridx = 1;
        c.gridy = 0;
        c.gridheight = 2;
        panel.add(createListPanel("Columns", columnsList), c);

        return panel;
    }

    private JPanel createListPanel(String label, JList list) {
        JPanel panel = new JPanel(new BorderLayout());
        JScrollPane scroller = new JScrollPane(list);
        scroller.setPreferredSize(new Dimension(100, 150));
        panel.add(scroller, BorderLayout.CENTER);
        panel.setBorder(new CompoundBorder(new EmptyBorder(5, 5, 0, 0), new TitledBorder(label)));
        return panel;
    }

    private Component createInformationPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        //if(orientation==HORIZONTAL) {
        //  panel.add(checkboxAddToOutputs, BorderLayout.NORTH);
        //}

        cards = new JPanel(new CardLayout());
        cards.add(createImageInformationPanel(), "IMAGE_INFORMATION");
        cards.add(createAsciiInformationPanel(), "ASCII_INFORMATION");
        cards.add(createGroupPanel(), "GROUP_INFORMATION");
        panel.add(cards, BorderLayout.CENTER);
        return panel;
    }

    private JTextField createTextField(String text) {
        JTextField field = new JTextField(text);
        field.setMinimumSize(new Dimension(120, 22));
        return field;
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setPreferredSize(new Dimension(100, 22));
        label.setMinimumSize(new Dimension(100, 22));
        return label;
    }

    /**
     * Returns a list of all user objects that traverses the subtree rooted
     * at this node in breadth-first order..
     * @param node the node to start with.
    private final List breadthFirstUserObjects(TreeNode node) {
    if (node == null) {
    return null;
    }

    Vector<Object> list = new Vector<Object>();
    DefaultMutableTreeNode theNode = null;
    Enumeration local_enum = ((DefaultMutableTreeNode) node).breadthFirstEnumeration();
    while (local_enum.hasMoreElements()) {
    theNode = (DefaultMutableTreeNode) local_enum.nextElement();
    list.add(theNode.getUserObject());
    }
    return list;
    }
     */
    /**
     * Returns the tree node that contains the given data object.
     */
    public TreeNode findTreeNode(HObject obj) {
        if (obj == null) {
            return null;
        }

        TreeNode theFileRoot = obj.getFileFormat().getRootNode();
        if (theFileRoot == null) {
            return null;
        }

        DefaultMutableTreeNode theNode = null;
        HObject theObj = null;
        Enumeration local_enum = ((DefaultMutableTreeNode) theFileRoot).breadthFirstEnumeration();
        while (local_enum.hasMoreElements()) {
            theNode = (DefaultMutableTreeNode) local_enum.nextElement();
            theObj = (HObject) theNode.getUserObject();
            if (theObj == null) {
                continue;
            } else if (theObj.equals(obj)) {
                return theNode;
            }
        }
        return null;
    }

    /*
    public TreePath findByName(String[] names) {
    return findNode(tree, new TreePath(latFileReader.getH5File().getRootNode()), names, 0, true);
    }

    private TreePath findNode(JTree tree, TreePath parent, Object[] nodes, int depth, boolean byName) {
    DefaultMutableTreeNode node = (DefaultMutableTreeNode)parent.getLastPathComponent();
    Object o = node;

    // If by name, convert node to a string
    if (byName) {
    o = o.toString();
    }

    // If equal, go down the branch
    if (o.equals(nodes[depth])) {
    // If at end, return match
    if (depth == nodes.length-1) {
    return parent;
    }

    // Traverse children
    if (node.getChildCount() >= 0) {
    for (Enumeration e=node.children(); e.hasMoreElements(); ) {
    DefaultMutableTreeNode n = (DefaultMutableTreeNode)e.nextElement();
    TreePath path = parent.pathByAddingChild(n);
    TreePath result = findNode(tree, path, nodes, depth+1, byName);
    // Found a match
    if (result != null) {
    return result;
    }
    }
    }
    }

    // No match at this branch
    return null;
    }
     */
    private void notifySaveOutputListeners() {
        for (LatFileNodeListener l : h5NodeListeners) {
            l.saveOutputsChanged();
        }
    }

    /**
     *
     * @return
     */
    public LatFileReader getLatFileReader() {
        return latFileReader;
    }

    /**
     *
     * @return
     */
    public TrfReader getTrfReader() {
        return trfReader;
    }

    /**
     *
     * @return
     */
    public LatFileTree getTree() {
        return tree;
    }

    /**
     *
     * @return
     */
    public boolean isShowAddToAvailableOutputs() {
        return showAddToAvailableOutputs;
    }

    /**
     *
     * @param showAddToAvailableOutputs
     */
    public void setShowAddToAvailableOutputs(boolean showAddToAvailableOutputs) {
        this.showAddToAvailableOutputs = showAddToAvailableOutputs;
        popupTrf = createPopupTrf();
        popupHdf = createPopupHdf();
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

    /**
     *
     * @author Dr. Richard St. John
     * @version $Revision$, $Date$
     */
    private class TreeListener implements ActionListener, MouseListener, TreeSelectionListener {

        public void actionPerformed(ActionEvent e) {
            String command = e.getActionCommand();
            if (command.equalsIgnoreCase("Cut object")) {
                actionDeleteHObject();
            } else if (command.equalsIgnoreCase("Add group")); else if (command.equals("Add dataset")) {
                addDataset();
            } else if (command.equals("Add image")) {
                addImage();
            } else if (command.equals("Add table")) {
                addTable();
            } else if (command.equals("Add datatype")) {
                addDatatype();
            } else if (command.equals("Add link")) {
                addLink();
            } else if (command.equals("Rename object")) {
                actionRenameObject();
            } else if (command.equals("Show object properties")) {
                actionShowMetaData();
            } else if (command.equals("Remove")) {
                actionRemoveFileFromTree();
            } else if (command.equalsIgnoreCase("RELOAD")) {
                latFileReader.updateRunTrace((DefaultTreeModel) tree.getModel());
            } else if (command.equalsIgnoreCase("SAVE_HDF_TO_OUTPUTS")) {
                DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
                if (selectedNode == null || !selectedNode.isLeaf()) {
                    return;
                }
                Object userObject = selectedNode.getUserObject();
                HObject hobject = (HObject) userObject;
                LatFileRunTrace runTrace = latFileReader.getRunTrace().get(hobject.getFile() + ":" + hobject.getFullName());
                runTrace.setAddToSavedMap(true);
                tree.repaint();
                savedOutputsSet.add(userObject);
            } else if (command.equals("REMOVE_HDF_FROM_OUTPUTS")) {
                DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
                if (selectedNode == null || !selectedNode.isLeaf()) {
                    return;
                }
                Object userObject = selectedNode.getUserObject();
                HObject hobject = (HObject) userObject;
                LatFileRunTrace runTrace = latFileReader.getRunTrace().get(hobject.getFile() + ":" + hobject.getFullName());
                runTrace.setAddToSavedMap(false);
                tree.repaint();
                savedOutputsSet.remove(userObject);
            } else if (command.equals("SAVE_TRF_TO_OUTPUTS")) {
                DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
                if (selectedNode == null || !selectedNode.isLeaf()) {
                    return;
                }
                Object userObject = selectedNode.getUserObject();
                TrfDataObject trfTreeNode = (TrfDataObject) userObject;
                trfTreeNode.setAddToSavedMap(true);
                tree.repaint();
                savedOutputsSet.add(userObject);
            } else if (command.equals("REMOVE_TRF_FROM_OUTPUTS")) {
                DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
                if (selectedNode == null || !selectedNode.isLeaf()) {
                    return;
                }
                Object userObject = selectedNode.getUserObject();
                TrfDataObject trfTreeNode = (TrfDataObject) userObject;
                trfTreeNode.setAddToSavedMap(false);
                tree.repaint();
                savedOutputsSet.remove(userObject);
            }


            notifySaveOutputListeners();
            notifyH5PopupListener(command, (DefaultMutableTreeNode) tree.getLastSelectedPathComponent());
        }

        private void actionRemoveFileFromTree() {
            TreePath[] selectedPaths = tree.getSelectionPaths();
            if (selectedPaths == null) {
                return;
            }

            DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
            for (TreePath treePath : selectedPaths) {
                DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) treePath.getLastPathComponent();
                TreeNode[] nodes = selectedNode.getPath();
                if (nodes.length < 1) {
                    return;
                }

                //Close File
                Object userObject = selectedNode.getUserObject();
                if (userObject instanceof HObject) {
                    HObject hobj = (HObject) userObject;
                    try {
                        hobj.getFileFormat().close();
                    } catch (Exception e) {
                        NotifyDescriptor nd = new NotifyDescriptor.Message("<HTML>The file <BR><B>" + hobj.getFile() + "<B><BR> could not be closed</HTML>");
                        DialogDisplayer.getDefault().notify(nd);
                    }
                }
                model.removeNodeFromParent((DefaultMutableTreeNode) nodes[1]);
            }

            DefaultMutableTreeNode root = (DefaultMutableTreeNode) model.getRoot();
            if (root.getChildCount() < 1) {
                tree.setRootVisible(true);
            }
        }

        private void actionShowMetaData() {
            DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
            Object userObject = selectedNode.getUserObject();
            if (!(userObject instanceof HObject)) {
                return;
            }

            HObject selectedObject = (HObject) userObject;
            List<Attribute> metaData = latFileReader.getMetadata(selectedObject);
            if (metaData == null) {
                return;
            }

            try {
                String attributesString = "";
                for (Attribute a : metaData) {
                    int rank = a.getRank();
                    long[] dims = a.getDataDims();

                    String openString, closeString;
                    if (dims[0] > 1) {
                        openString = "[";
                        closeString = "]";
                    } else {
                        openString = "";
                        closeString = "";
                    }
                    String valueString = openString;

                    Object value = a.getValue();
                    if (rank == 1) {
                        if (value instanceof int[]) {
                            for (int i = 0; i < dims[0]; i++) {
                                valueString += ((int[]) value)[i] + ",";
                            }
                        } else if (value instanceof float[]) {
                            for (int i = 0; i < dims[0]; i++) {
                                valueString += ((float[]) value)[i] + ",";
                            }
                        } else if (value instanceof double[]) {
                            for (int i = 0; i < dims[0]; i++) {
                                valueString += ((double[]) value)[i] + ",";
                            }
                        }

                        valueString = valueString.substring(0, valueString.lastIndexOf(",")) + closeString;
                    }
                    attributesString += a.getName() + " = " + valueString + "\n";
                }
                JDialog metaDialog = new JDialog((JDialog) null, "Metadata", false);
                JPanel panel = new JPanel(new BorderLayout());
                panel.add(new JLabel("Metadata for: " + selectedObject.getName()), BorderLayout.NORTH);
                panel.add(new JScrollPane(new JTextArea(attributesString)), BorderLayout.CENTER);
                metaDialog.setContentPane(panel);
                metaDialog.setSize(300, 200);
                metaDialog.setLocationRelativeTo(LatFileExplorerPanel.this);
                metaDialog.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
            }
        }

        private void actionRenameObject() {
            DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
            HObject selectedObject = (HObject) selectedNode.getUserObject();

            if (selectedObject == null) {
                return;
            }

            if ((selectedObject instanceof Group) && ((Group) selectedObject).isRoot()) {
                Toolkit.getDefaultToolkit().beep();
//                OptionsDialog d = new OptionsDialog(tree, "HDF Explorer", OptionsDialog.ERROR_ICON);
//                d.showDialog("Cannot rename the root.", 0);
                return;
            }

            boolean isH4 = selectedObject.getFileFormat().isThisType(FileFormat.getFileFormat(FileFormat.FILE_TYPE_HDF4));

            if (isH4) {
                Toolkit.getDefaultToolkit().beep();
//                OptionsDialog d = new OptionsDialog(tree, "HDF Explorer", OptionsDialog.ERROR_ICON);
//                d.showDialog("Cannot rename HDF4 object.", 0);
                return;
            }

            String oldName = selectedObject.getName();
//            OptionsDialog d = new OptionsDialog(tree, "Rename...", true);
//            d.showDialog("Rename \"" + oldName + "\" to:", 0);
//            String newName = d.getInput();
//
//            if (newName == null) {
//                return;
//            }

//            newName = newName.trim();
//            if ((newName == null) || (newName.length() == 0) || newName.equals(oldName)) {
//                return;
//            }
//
//            try {
//                selectedObject.setName(newName);
//                latFileReader.getH5File().reloadTree((Group) selectedObject);
//            } catch (Exception ex) {
//                Toolkit.getDefaultToolkit().beep();
//                d = new OptionsDialog(tree, "HDF Explorer", OptionsDialog.ERROR_ICON);
//                d.showDialog(ex.getMessage(), 0);
//            }
        }

        private void addTable() {
            System.out.println("================================");
            System.out.println("Feature not yet active.");
            System.out.println("================================");
        }

        private void addLink() {
            System.out.println("================================");
            System.out.println("Feature not yet active.");
            System.out.println("================================");
        }

        private void addDatatype() {
            System.out.println("================================");
            System.out.println("Feature not yet active.");
            System.out.println("================================");
        }

        private void addImage() {
            System.out.println("================================");
            System.out.println("Feature not yet active.");
            System.out.println("================================");
        }

        private void addDataset() {
            System.out.println("================================");
            System.out.println("Feature not yet active.");
            System.out.println("================================");
        }

        /**
         * Adds a new data object to the file.
         * @param newObject the new object to add.
         * @param parentGroup the parent group the object is to add to.
         * @throws Exception
         */
        public void addObject(HObject newObject, Group parentGroup)
                throws Exception {
            if ((newObject == null) || (parentGroup == null)) {
                return;
            }

            TreeNode pnode = findTreeNode(parentGroup);
            TreeNode newnode = null;
            if (newObject instanceof Group) {
                newnode = new DefaultMutableTreeNode(newObject) {

                    public static final long serialVersionUID = HObject.serialVersionUID;

                    @Override
                    public boolean isLeaf() {
                        return false;
                    }
                };
            } else {
                newnode = new DefaultMutableTreeNode(newObject);
            }

            ((DefaultTreeModel) tree.getModel()).insertNodeInto((DefaultMutableTreeNode) newnode, (DefaultMutableTreeNode) pnode, pnode.getChildCount());
        }

        private void actionDeleteHObject() {
            DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
            HObject selectedObject = (HObject) selectedNode.getUserObject();

            Group pGroup = null;
            if (selectedObject instanceof Group) {
                pGroup = (Group) selectedObject;
            } else {
                pGroup = (Group) ((DefaultMutableTreeNode) selectedNode.getParent()).getUserObject();
            }

            try {
                ((DefaultTreeModel) tree.getModel()).removeNodeFromParent(selectedNode);
                latFileReader.getH5File().delete(selectedObject);
                latFileReader.getH5File().reloadTree(pGroup);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public void mouseExited(MouseEvent e) {
        }

        public void mouseEntered(MouseEvent e) {
        }

        public void mouseClicked(MouseEvent e) {
        }

        public void mouseReleased(MouseEvent e) {
            DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
            if (selectedNode == null) {
                return;
            }

            Object userObject = selectedNode.getUserObject();
            if (e.isPopupTrigger()) {
                if (userObject instanceof HObject) {
                    popupHdf.show(tree, e.getX(), e.getY());
                } else {
                    for (int i = 1; i < popupTrf.getComponentCount(); i++) {
                        popupTrf.getComponent(i).setVisible(selectedNode.isLeaf());
                    }
                    popupTrf.show(tree, e.getX(), e.getY());
                }
            }
        }

        public void mousePressed(MouseEvent e) {
            if (e.getButton() == 3) {
                int row = tree.getRowForLocation(e.getX(), e.getY());
                int[] rows = tree.getSelectionRows();
                boolean isSelected = false;
                if (rows != null) {
                    for (int i = 0; i < rows.length; i++) {
                        if (row == rows[i]) {
                            isSelected = true;
                            break;
                        }
                    }
                }
                if (!isSelected) {
                    tree.setSelectionRow(row);
                }
            }
        }

        public void actionTreeNodeSelected(DefaultMutableTreeNode selectedNode) {
            Object userObject = selectedNode.getUserObject();
            if (userObject instanceof HObject) {
                HObject hobject = (HObject) userObject;
                fieldFile.setText(hobject.getFile());
                if (!(hobject instanceof Dataset)) {
                    CardLayout cl = (CardLayout) (cards.getLayout());
                    cl.show(cards, "GROUP_INFORMATION");
                    return;
                }

                boolean isAcsAsciiData = false;
                try {
                    latFileReader.setH5File((H5File) hobject.getFileFormat());
                    List<Attribute> attList = latFileReader.getMetadata(hobject);
                    if (attList.size() > 0 && ((int[]) attList.get(0).getValue())[0] == LatFilePlotPanel.ASCII_DATA) {
                        isAcsAsciiData = true;
                    }
                } catch (Exception e1) {
                }

                CardLayout cl = (CardLayout) (cards.getLayout());
                if (isAcsAsciiData) {
                    updateAsciiInformationPanel(selectedNode);
                    cl.show(cards, "ASCII_INFORMATION");
                } else {
                    updateImageInformationPanel(selectedNode);
                    cl.show(cards, "IMAGE_INFORMATION");
                }
            } else if (userObject instanceof TrfTreeObject) {
                fieldFile.setText(((TrfTreeObject) userObject).getFile().getPath());
                CardLayout cl = (CardLayout) (cards.getLayout());
                updateImageInformationPanel(selectedNode);
                cl.show(cards, "IMAGE_INFORMATION");
            } else {
                CardLayout cl = (CardLayout) (cards.getLayout());
                updateImageInformationPanel(selectedNode);
                cl.show(cards, "IMAGE_INFORMATION");
            }
        }

        private void updateAsciiInformationPanel(DefaultMutableTreeNode selectedNode) {
            HObject hobj = (HObject) selectedNode.getUserObject();
            latFileReader.setH5File((H5File) hobj.getFileFormat());
            LatFileRunTrace thisRunTrace = latFileReader.getRunTrace().get(hobj.getFile() + ":" + hobj.getFullName());
            if (thisRunTrace == null) {
                return;
            }

            columnsList.removeListSelectionListener(columnsListener);
            parametersList.removeListSelectionListener(parametersListener);
            realizationsList.removeListSelectionListener(realizationsListener);

            DefaultListModel listModel = (DefaultListModel) columnsList.getModel();
            listModel.clear();
            if (selectedNode.getUserObject() instanceof Group) {
                columnsList.addListSelectionListener(columnsListener);
                parametersList.addListSelectionListener(parametersListener);
                realizationsList.addListSelectionListener(realizationsListener);
                return;
            }

            // Update column selection list
            int colCount = thisRunTrace.getColCount();
            DefaultListModel model = (DefaultListModel) columnsList.getModel();
            model.clear();
            for (int i = 0; i < colCount; i++) {
                model.addElement((i + 1));
            }
            columnsList.setSelectedIndices(thisRunTrace.getSelectedColumns());
            columnsList.addListSelectionListener(columnsListener);

            realizationsList.setSelectedIndices(thisRunTrace.getSelectedRealizations());
            realizationsList.addListSelectionListener(realizationsListener);

            parametersList.setSelectedIndices(thisRunTrace.getSelectedParameters());
            parametersList.addListSelectionListener(parametersListener);

        //checkboxAddToOutputs.setSelected(thisRunTrace.isAddToSavedMap());
        }

        private void updateTrfOutputInformationPanel(TrfDataObject trfTreeNode) {
            if (trfTreeNode.getNodeType() == TrfDataObject.PARAMETER_NODE) {
                fieldAlias.setEditable(false);
            }

            float[] times = null;
            try {
                if (trfTreeNode.getNodeType() == TrfDataObject.VARIABLE_NODE) {
                    times = trfReader.readVaribaleTimes(trfTreeNode);
                    if (times.length == 0) {
                        ((DefaultListModel) timesList.getModel()).clear();
                        NotifyDescriptor nd = new NotifyDescriptor.Message("No data found in " + trfTreeNode.getVarName());
                        DialogDisplayer.getDefault().notify(nd);
                        return;
                    }
                } else {
                    times = new float[]{Float.NaN};
                }
            } catch (IOException e2) {
                e2.printStackTrace();
            }
            timesList.removeListSelectionListener(timesListener);
            DefaultListModel listModel = (DefaultListModel) timesList.getModel();
            listModel.clear();
            for (int i = 0; i < times.length; i++) {
                listModel.addElement(String.valueOf(times[i]));
            }

            float[] selectedTimes = trfTreeNode.getSelectedTimes();
            if (selectedTimes == null) {
                trfTreeNode.setSelectedTimes(selectedTimes = new float[]{times[0]});
            }

            int[] selectedIndices = new int[selectedTimes.length];
            for (int i = 0; i < selectedTimes.length; i++) {
                selectedIndices[i] = listModel.indexOf(String.valueOf(selectedTimes[i]));
            }
            timesList.setSelectedIndices(selectedIndices);
            timesList.addListSelectionListener(timesListener);

            int nobjs = trfTreeNode.getNobjs();
            ArrayList<Integer> rowFactors = getFactors(nobjs);
            ArrayList<Integer> colFactors = new ArrayList<Integer>(rowFactors.size());
            for (int i = rowFactors.size() - 1; i >= 0; i--) {
                colFactors.add(nobjs / rowFactors.get(i));
            }

            spinnerRows.removeChangeListener(rowChangeListener);
            spinnerCols.removeChangeListener(colChangeListener);

            ArrayList<Integer> rowList = new ArrayList<Integer>();
            for (Integer i : rowFactors) {
                rowList.add(i);
            }
            ((SpinnerListModel) spinnerRows.getModel()).setList(rowList);
            spinnerRows.setValue(trfTreeNode.getRowCount());

            ArrayList<Integer> colList = new ArrayList<Integer>();
            for (Integer i : colFactors) {
                colList.add(i);
            }
            ((SpinnerListModel) spinnerCols.getModel()).setList(colList);
            spinnerCols.setValue(trfTreeNode.getColCount());

            spinnerCols.addChangeListener(colChangeListener);
            spinnerRows.addChangeListener(rowChangeListener);

            String alias = trfTreeNode.getAlias();
            if (alias.equalsIgnoreCase("")) {
                alias = trfTreeNode.toString();
                alias = alias.substring(alias.lastIndexOf(".") + 1);
                trfTreeNode.setAlias(alias);
            }
            //checkboxAddToOutputs.setSelected(trfTreeNode.isAddToSavedMap());
            fieldAlias.setText(trfTreeNode.getAlias());
        }

        private void updateImageInformationPanel(DefaultMutableTreeNode selectedNode) {
            Object userObject = selectedNode.getUserObject();

            double[] allTimes, selectedTimes;
            if (userObject instanceof HObject) {
                HObject hobj = (HObject) selectedNode.getUserObject();

                latFileReader.setH5File((H5File) hobj.getFileFormat());
                LatFileRunTrace thisRunTrace = latFileReader.getRunTrace().get(hobj.getFile() + ":" + hobj.getFullName());
                if (thisRunTrace == null) {
                    return;
                }

                fieldAlias.setText(thisRunTrace.getAlias());

                allTimes = latFileReader.readVariableTimes(selectedNode);
                selectedTimes = thisRunTrace.getSelectedTimes();
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

                //checkboxAddToOutputs.setSelected(thisRunTrace.isAddToSavedMap());

                long[][] dims = latFileReader.getDimensionsForSelectedTime(hobj, timesList.getSelectedIndices());
                if (dims == null || dims.length < 1 || dims[0].length < 1) {
                    latFileReader.updateRunTrace((DefaultTreeModel) tree.getModel());
                    dims = latFileReader.getDimensionsForSelectedTime(hobj, timesList.getSelectedIndices());
                }
                thisRunTrace.setSelectedDims(dims);
                updateDimSpinner(dims[0]);
            } else if (userObject instanceof TrfDataObject) {
                TrfDataObject trfDataset = (TrfDataObject) userObject;
                updateTrfOutputInformationPanel(trfDataset);
            }
        }

        /**
         *
         */
        public void valueChanged(TreeSelectionEvent e) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
            if (node == null) {
                return;
            }
            actionTreeNodeSelected(node);
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
     * @author Dr. Richard St. John
     * @version $Revision$, $Date$
     */
    private class FileFieldListener implements DropListener {

        public void dropAction(DropEvent dropEvent) {
            File file = new File(((String[]) dropEvent.getDroppedItem())[0]);
            if (file.isDirectory()) {
                NotifyDescriptor nd = new NotifyDescriptor.Message("<HTML>The directory:<BR><BR><B>" + file.getPath() + "</B><BR><BR>is not a valid HDF5, TRF, or ACS-H5 file.</HTML>");
                DialogDisplayer.getDefault().notify(nd);
                return;
            }
            loadFile(file);
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
}
