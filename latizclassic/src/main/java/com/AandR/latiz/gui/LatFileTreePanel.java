package com.AandR.latiz.gui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;

import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.plaf.ColorUIResource;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import com.AandR.gui.ui.JButtonX;
import com.AandR.gui.ui.JToolbarButton;
import com.AandR.latiz.core.LatizSystem;
import com.AandR.latiz.core.Output;
import com.AandR.latiz.core.PropertiesManager;
import com.AandR.latiz.dev.AbstractPlugin;
import com.AandR.latiz.resources.Resources;

/**
 * @author Dr. Richard St. John
 * @version $Revision$, $Date$
 */
public class LatFileTreePanel extends JPanel {

    private int systemCount = 1;
    private CheckNode root;
    private DefaultTreeModel treeModel;
    private HashMap<AbstractPlugin, ArrayList<LatFileTreeDataNode>> selectedDataNodeMap;
    private JCheckBox checkboxSaveLatFile;
    private JLabel beginLabel, endLabel, periodLabel, maxLabel;
    private JLabel beginLabelUnits, endLabelUnits, periodLabelUnits, maxLabelUnits;
    private JPanel cards;
    private JRadioButton radioUserSpecified, radioWhenUpdated;
    private JTextField fieldFilename, fieldBegin, fieldEnd, fieldPeriod, fieldMax;
    private JTree tree;
    private LatFileTreeDataNode selectedDataNode;
    private TreeListener treeListener;

    //private HashMap<AbstractPlugin, ArrayList<LatFileTreeDataNode>> selectedDataNodes;
    public LatFileTreePanel() {
        initialize();
        setLayout(new BorderLayout(5, 5));
        add(createTreePanel(), BorderLayout.CENTER);
        add(createSouthPanel(), BorderLayout.SOUTH);
    }

    private void initialize() {
        selectedDataNodeMap = new HashMap<AbstractPlugin, ArrayList<LatFileTreeDataNode>>();

        tree = new JTree(treeModel = new DefaultTreeModel(root = new CheckNode(new PaletteNode("Available Palettes"))));
        tree.addMouseListener(treeListener = new TreeListener());
        tree.setShowsRootHandles(true);
        tree.setCellRenderer(new CheckRenderer());
        tree.setEditable(false);

        cards = new JPanel(new CardLayout());
        cards.add(new JPanel(), "EMPTY_PANEL");
        cards.add(createSaveOptionsPanel(), "OPTIONS_PANEL");
        CardLayout cl = (CardLayout) (cards.getLayout());
        cl.show(cards, "EMPTY_PANEL");
    }

    private Component createTreePanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        JScrollPane scroller = new JScrollPane(tree);
        panel.add(createNorthPanel(), BorderLayout.NORTH);
        panel.add(scroller, BorderLayout.CENTER);

        JSplitPane treeSplitter = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        treeSplitter.setOneTouchExpandable(true);
        treeSplitter.setDividerSize(10);
        treeSplitter.setTopComponent(panel);
        treeSplitter.setBottomComponent(cards);
        return treeSplitter;
    }

    private Component createSaveOptionsPanel() {
        radioWhenUpdated = new JRadioButton("When updated", true);
        radioWhenUpdated.addActionListener(treeListener);
        radioWhenUpdated.setActionCommand("WHEN_UPDATED");

        radioUserSpecified = new JRadioButton("User specified", false);
        radioUserSpecified.addActionListener(treeListener);
        radioUserSpecified.setActionCommand("USER_DEFINED");

        ButtonGroup bg = new ButtonGroup();
        bg.add(radioUserSpecified);
        bg.add(radioWhenUpdated);

        JPanel choicePanel = new JPanel(new GridLayout(2, 1, 0, 0));
        choicePanel.add(radioWhenUpdated);
        choicePanel.add(radioUserSpecified);

        JPanel beginPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        beginPanel.add(beginLabel = createLabel("Begin Time:", radioUserSpecified.isSelected()));
        beginPanel.add(fieldBegin = createInputField("BEGIN"));
        beginPanel.add(beginLabelUnits = createUnitsLabel(" sec", radioUserSpecified.isSelected()));

        JPanel endPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        endPanel.add(endLabel = createLabel("End Time:", radioUserSpecified.isSelected()));
        endPanel.add(fieldEnd = createInputField("END"));
        endPanel.add(endLabelUnits = createUnitsLabel(" sec", radioUserSpecified.isSelected()));

        JPanel periodPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        periodPanel.add(periodLabel = createLabel("Period:", radioUserSpecified.isSelected()));
        periodPanel.add(fieldPeriod = createInputField("PERIOD"));
        periodPanel.add(periodLabelUnits = createUnitsLabel(" sec", radioUserSpecified.isSelected()));

        JPanel maxPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        maxPanel.add(maxLabel = createLabel("Do not exceed:", radioUserSpecified.isSelected()));
        maxPanel.add(fieldMax = createInputField("MAX"));
        maxPanel.add(maxLabelUnits = createUnitsLabel(" frames", radioUserSpecified.isSelected()));

        JPanel inputsPanel = new JPanel(new GridLayout(4, 1, 0, 0));
        inputsPanel.setBorder(new EmptyBorder(0, 30, 0, 0));
        inputsPanel.add(beginPanel);
        inputsPanel.add(endPanel);
        inputsPanel.add(periodPanel);
        inputsPanel.add(maxPanel);

        JPanel p = new JPanel(new BorderLayout());
        p.add(choicePanel, BorderLayout.NORTH);
        p.add(inputsPanel, BorderLayout.CENTER);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new TitledBorder("Save Options"));
        panel.setMinimumSize(new Dimension(10, 10));
        panel.add(p, BorderLayout.NORTH);
        return panel;
    }

    private JTextField createInputField(String name) {
        JTextField field = new JTextField(5);
        field.setName(name);
        field.setEnabled(radioUserSpecified.isSelected());
        field.addKeyListener(treeListener);
        return field;
    }

    private JLabel createLabel(String text, boolean enabled) {
        JLabel label = new JLabel(text);
        label.setEnabled(enabled);
        label.setPreferredSize(new Dimension(100, 22));
        return label;
    }

    private JLabel createUnitsLabel(String text, boolean enabled) {
        JLabel label = new JLabel(text);
        label.setEnabled(enabled);
        return label;
    }

    private Component createNorthPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 1));
        panel.add(createToolbarPanel());
        panel.add(checkboxSaveLatFile = new JCheckBox("Enable Latiz Data File."));
        return panel;
    }

    private Component createToolbarPanel() {
        JToolbarButton expandButton = createToolbarButton("expandTree16.png", new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                setAllExpanded(tree, new TreePath(root), true);
            }
        });
        JToolbarButton collapseButton = createToolbarButton("collapseTree16.png", new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                setAllExpanded(tree, new TreePath(root), false);
            }
        });
        JToolbarButton checkAllButton = createToolbarButton("check20.png", new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                setAllSelected(tree, true);
            }
        });
        JToolbarButton uncheckAllButton = createToolbarButton("uncheck20.png", new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                setAllSelected(tree, false);
            }
        });

        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.add(expandButton);
        panel.add(collapseButton);
        panel.add(checkAllButton);
        panel.add(uncheckAllButton);
        return panel;
    }

    private JToolbarButton createToolbarButton(String iconName, ActionListener al) {
        JToolbarButton button = new JToolbarButton(Resources.createIcon(iconName));
        button.setPreferredSize(new Dimension(26, 26));
        button.addActionListener(al);
        return button;
    }

    private Component createSouthPanel() {
        JButtonX browseButton = new JButtonX(Resources.createIcon("find16.png"));
        browseButton.setPreferredSize(new Dimension(22, 22));
        browseButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                JFileChooser chooser = new JFileChooser(System.getProperty("user.home"));
                int selection = chooser.showSaveDialog(null);
                if (selection == JFileChooser.APPROVE_OPTION) {
                    fieldFilename.setText(chooser.getSelectedFile().getPath());
                }
            }
        });

        JPanel filePanel = new JPanel(new BorderLayout(5, 5));
        String defaultPath = PropertiesManager.getInstanceOf().getProperty(PropertiesManager.GENERAL_LAT_PATH);
        filePanel.add(fieldFilename = new JTextField(defaultPath + File.separator + "latizOutput.lat5", 20), BorderLayout.CENTER);
        filePanel.add(browseButton, BorderLayout.EAST);
        filePanel.setBorder(new EmptyBorder(0, 0, 0, 5));

        JPanel panel = new JPanel(new GridLayout(2, 1, 2, 2));
        panel.add(new JLabel("Latiz datafile name"));
        panel.add(filePanel);
        panel.setBorder(new EmptyBorder(2, 5, 2, 5));

        return panel;
    }

    private void create(HashSet<LatizSystem> latizSystems, HashMap<String, HashMap<String, Connector>> pluginOutgoingConnectorMaps) {
        reset();
        String[] varNames;
        CheckNode thisSystemNode, thisPluginNode;
        for (LatizSystem ls : latizSystems) {
            treeModel.insertNodeInto(thisSystemNode = new CheckNode(new SystemNode(ls, systemCount++)), root, root.getChildCount());

            for (AbstractPlugin p : ls) {
                varNames = p.getLatFileVariableNames();
                if (varNames == null || varNames.length < 1) {
                    continue;
                }

                thisPluginNode = new CheckNode(new PluginNode(p));
                treeModel.insertNodeInto(thisPluginNode, thisSystemNode, thisSystemNode.getChildCount());
                for (int i = 0; i < varNames.length; i++) {
                    treeModel.insertNodeInto(new CheckNode(new LatFileTreeDataNode(varNames[i], true)), thisPluginNode, thisPluginNode.getChildCount());
                }
            }
            if (thisSystemNode.getChildCount() < 1) {
                treeModel.removeNodeFromParent(thisSystemNode);
            }
        }
    }

    /**
     * Does a complete reload of the lat-file tree.
     * @param latizSystems
     * @param pluginOutgoingConnectorMaps
     */
    public void reloadTree(HashSet<LatizSystem> latizSystems, HashMap<String, HashMap<String, Connector>> pluginOutgoingConnectorMaps) {
        systemCount = 1;
        create(latizSystems, pluginOutgoingConnectorMaps);
        tree.expandRow(0);
        tree.repaint();
        tree.revalidate();
    }

    /**
     * Does a complete reload of the lat-file tree.
     * @param latizSystems
     * @param pluginOutgoingConnectorMaps
     */
    public void refreshTree(HashSet<LatizSystem> latizSystems, HashMap<String, HashMap<String, Connector>> pluginOutgoingConnectorMaps) {
        systemCount = 1;
        create(latizSystems, pluginOutgoingConnectorMaps);
        tree.expandRow(0);
        tree.repaint();
        tree.revalidate();
    }

    public void updateAfterPluginAdded(LatizSystem latizSystem, AbstractPlugin p) {
        HashMap<String, Output> outputsMap = p.getOutputsDataMap();
        if (outputsMap == null || outputsMap.size() < 1) {
            return;
        }

        TreeNode parent = findTreeNode(latizSystem);
        if (parent == null) {
            parent = new CheckNode(new SystemNode(latizSystem, systemCount++));
            treeModel.insertNodeInto((MutableTreeNode) parent, root, root.getChildCount());
        }
        CheckNode thisNode;
        treeModel.insertNodeInto(thisNode = new CheckNode(new PluginNode(p)), (CheckNode) parent, parent.getChildCount());
        for (String key : p.getLatFileVariableNames()) {
            treeModel.insertNodeInto(new CheckNode(new LatFileTreeDataNode(key, true)), thisNode, thisNode.getChildCount());
        }
        tree.expandPath(new TreePath(thisNode.getPath()));
    }

    /**
     *
     * @param latizSystem
     * @param p
     */
    public void updateAfterPluginOutputsChanged(LatizSystem latizSystem, AbstractPlugin p) {
        String[] keys = p.getLatFileVariableNames();
        CheckNode parent = null;
        CheckNode node = (CheckNode) findTreeNode(p);
        if (node != null) {
            parent = (CheckNode) node.getParent();
            if (parent.getChildCount() > 1) {
                treeModel.removeNodeFromParent(node);
                node = null;
            } else {
                treeModel.removeNodeFromParent(parent);
                parent = null;
            }
            //treeModel.removeNodeFromParent(parent.getChildCount()>1 ? node : parent);
            if (keys.length < 1) {
                return;
            }
        }

        if (parent == null) {
            parent = new CheckNode(new SystemNode(latizSystem, systemCount++));
            treeModel.insertNodeInto(parent, root, root.getChildCount());
        }
        treeModel.insertNodeInto(node = new CheckNode(new PluginNode(p)), parent, parent.getChildCount());
        for (String key : keys) {
            treeModel.insertNodeInto(new CheckNode(new LatFileTreeDataNode(key, true)), (MutableTreeNode) node, node.getChildCount());
        }
        tree.expandPath(new TreePath(node.getPath()));
    }

    public void updateAfterLatizSystemChange(HashSet<LatizSystem> latizSystems) {
        HashMap<String, CheckNode> treeNodeMap = new HashMap<String, CheckNode>();
        CheckNode thisNode;
        Object userObject;
        Enumeration<CheckNode> enums = root.breadthFirstEnumeration();
        while (enums.hasMoreElements()) {
            thisNode = enums.nextElement();
            userObject = thisNode.getUserObject();
            if (!(userObject instanceof PluginNode)) {
                continue;
            }

            treeNodeMap.put(((PluginNode) userObject).plugin.getName(), thisNode);
        }

        reset();
        systemCount = 1;
        CheckNode thisSystemNode, thisPluginNode;
        for (LatizSystem ls : latizSystems) {
            treeModel.insertNodeInto(thisSystemNode = new CheckNode(new SystemNode(ls, systemCount++)), root, root.getChildCount());

            for (AbstractPlugin plugin : ls) {
                String pluginName = plugin.getName();
                thisPluginNode = treeNodeMap.get(pluginName);
                if (thisPluginNode == null) {
                    continue;
                }
                thisSystemNode.add(thisPluginNode);
            }
            if (thisSystemNode.getChildCount() < 1) {
                treeModel.removeNodeFromParent(thisSystemNode);
            }
        }
        treeNodeMap.clear();

        setAllExpanded(true);
    }

    public void updateAfterPluginNameChange(String oldName, String newName) {
        CheckNode node = (CheckNode) findTreeNode(oldName);
        if (node == null) {
            return;
        }
        treeModel.reload(node);
        treeModel.nodeChanged(node);
    }

    private TreeNode findTreeNode(LatizSystem latizSystem) {
        if (latizSystem == null) {
            return null;
        }

        CheckNode theNode = null;
        Object userObject = null;
        Enumeration local_enum = ((CheckNode) root).breadthFirstEnumeration();
        while (local_enum.hasMoreElements()) {
            theNode = (CheckNode) local_enum.nextElement();
            userObject = theNode.getUserObject();
            if (userObject == null || !(userObject instanceof SystemNode)) {
                continue;
            }
            SystemNode node = (SystemNode) userObject;
            LatizSystem ls = node.getLatizSystem();
            if (ls.equals(latizSystem)) {
                return theNode;
            }
        }
        return null;
    }

    /**
     * Returns the tree node that contains the given data object.
     */
    private TreeNode findTreeNode(AbstractPlugin p) {
        if (p == null) {
            return null;
        }

        CheckNode theNode = null;
        Object userObject = null;
        Enumeration local_enum = ((CheckNode) root).breadthFirstEnumeration();
        while (local_enum.hasMoreElements()) {
            theNode = (CheckNode) local_enum.nextElement();
            userObject = theNode.getUserObject();
            if (userObject == null || !(userObject instanceof PluginNode)) {
                continue;
            }
            PluginNode node = (PluginNode) userObject;
            AbstractPlugin np = node.getPlugin();
            if (np.getName().equals(p.getName())) {
                return theNode;
            }
        }
        return null;
    }

    /**
     *
     * @param label
     * @return
     */
    private TreeNode findTreeNode(String label) {
        if (label == null) {
            return null;
        }

        CheckNode theNode = null;
        Object userObject = null;
        Enumeration local_enum = ((CheckNode) root).breadthFirstEnumeration();
        while (local_enum.hasMoreElements()) {
            theNode = (CheckNode) local_enum.nextElement();
            userObject = theNode.getUserObject();
            if (userObject == null || !(userObject instanceof PluginNode)) {
                continue;
            }

            PluginNode node = (PluginNode) userObject;
            AbstractPlugin np = node.getPlugin();
            if (np.getName().equals(label)) {
                return theNode;
            }
        }
        return null;
    }

    public void reset() {
        systemCount = 1;
        treeModel.setRoot(root = new CheckNode(new PaletteNode("Selected Palette")));
    }

    public void setSelectedDataNodeMap(HashMap<AbstractPlugin, ArrayList<LatFileTreeDataNode>> selectedDataNodeMap) {
        this.selectedDataNodeMap = selectedDataNodeMap;
    }

    public void updateSelectedDataNodes() {
        if (selectedDataNodeMap == null) {
            return;
        }
        setAllSelected(tree, false);
        DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
        CheckNode rootNode = (CheckNode) model.getRoot();
        Enumeration enums = rootNode.breadthFirstEnumeration();
        CheckNode thisNode = null;
        CheckNode thisDataNode = null;
        ArrayList<LatFileTreeDataNode> theseSelectedNodes;
        AbstractPlugin selectedPlugin = null;
        LatFileTreeDataNode ld = null;
        while (enums.hasMoreElements()) {
            thisNode = (CheckNode) enums.nextElement();
            if (!(thisNode.getUserObject() instanceof PluginNode)) {
                continue;
            }

            selectedPlugin = ((PluginNode) thisNode.getUserObject()).getPlugin();
            theseSelectedNodes = selectedDataNodeMap.get(selectedPlugin);
            if (theseSelectedNodes == null) {
                continue;
            }

            // Loop over each data node
            for (int i = 0; i < thisNode.getChildCount(); i++) {
                thisDataNode = (CheckNode) thisNode.getChildAt(i);

                ld = (LatFileTreeDataNode) thisDataNode.getUserObject();
                String datasetName = ld.getDatasetName();

                for (LatFileTreeDataNode selectedNode : theseSelectedNodes) {
                    if (selectedNode.getDatasetName().equals(datasetName)) {
                        thisDataNode.setSelected(true);
                        ld.setBeginTime(selectedNode.getBeginTime());
                        ld.setEndTime(selectedNode.getEndTime());
                        ld.setPeriod(selectedNode.getPeriod());
                        ld.setMaxFrameCount(selectedNode.getMaxFrameCount());
                        ld.setUserDefined(selectedNode.isUserDefined());
                        model.nodeChanged(thisDataNode);
                        break;
                    }
                }
            }
        }
        traverseTree();
    }

    private void setAllSelected(JTree tree, boolean selected) {
        DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
        CheckNode rootNode = (CheckNode) model.getRoot();
        Enumeration enums = rootNode.breadthFirstEnumeration();
        CheckNode thisNode = null;
        CheckNode thisDataNode = null;
        while (enums.hasMoreElements()) {
            thisNode = (CheckNode) enums.nextElement();
            if (thisNode.getUserObject() instanceof PluginNode) {
                for (int i = 0; i < thisNode.getChildCount(); i++) {
                    thisDataNode = (CheckNode) thisNode.getChildAt(i);
                    ((LatFileTreeDataNode) thisDataNode.getUserObject()).setSelected(selected);
                    model.nodeChanged(thisDataNode);
                }
            }
        }
    }

    public void setAllExpanded(boolean isExpanded) {
        setAllExpanded(tree, new TreePath(root), isExpanded);
    }

    private void setAllExpanded(JTree tree, TreePath parent, boolean expand) {
        TreeNode node = (TreeNode) parent.getLastPathComponent();
        if (node.getChildCount() >= 0) {
            for (Enumeration e = node.children(); e.hasMoreElements();) {
                TreeNode n = (TreeNode) e.nextElement();
                TreePath path = parent.pathByAddingChild(n);
                setAllExpanded(tree, path, expand);
            }
        }

        // Expansion or collapse must be done bottom-up
        if (expand) {
            tree.expandPath(parent);
        } else {
            tree.collapsePath(parent);
        }
    }

    /**
     * Traverses the entire tree, stopping at all plugins, to check if any of the children data nodes are selected.
     * If a data node is selected it is added to the hash map.
     * @return HashMap with the key being the abstract plugin and the value being an ArrayList of string (key) that is
     * used by the abstract plugin.
     */
    private void traverseTree() {
        selectedDataNodeMap.clear();
        Enumeration enums = root.breadthFirstEnumeration();
        LatFileTreeDataNode thisDataNode = null;
        CheckNode thisChildNode = null;
        CheckNode thisNode = null;
        ArrayList<LatFileTreeDataNode> theseNodesSelectedDataNodes = null;
        while (enums.hasMoreElements()) {
            thisNode = (CheckNode) enums.nextElement();
            if (!(thisNode.getUserObject() instanceof PluginNode)) {
                continue;
            }

            theseNodesSelectedDataNodes = new ArrayList<LatFileTreeDataNode>();
            for (int i = 0; i < thisNode.getChildCount(); i++) {
                thisChildNode = (CheckNode) thisNode.getChildAt(i);
                if (thisChildNode.isSelected()) {
                    thisDataNode = (LatFileTreeDataNode) thisChildNode.getUserObject();
                    theseNodesSelectedDataNodes.add(thisDataNode);
                }
            }
            if (theseNodesSelectedDataNodes.size() > 0) {
                selectedDataNodeMap.put(((PluginNode) thisNode.getUserObject()).plugin, theseNodesSelectedDataNodes);
            }
        }
    }

    public HashMap<AbstractPlugin, ArrayList<LatFileTreeDataNode>> getSelectedLatFileMap() {
        return selectedDataNodeMap;
    }

    public String getFileName() {
        String name = fieldFilename.getText().trim();
        if (name == null || name.equalsIgnoreCase("")) {
            fieldFilename.setText(System.getProperty("user.home" + File.separator + "latizOutput.lat5"));
        }
        if (!(name.endsWith(".h5") || name.endsWith(".lat5"))) {
            name += ".lat5";
            fieldFilename.setText(name);
        }
        return fieldFilename.getText();
    }

    public void setFileName(String filename) {
        fieldFilename.setText(filename);
    }

    public JTextField getFieldFilename() {
        return fieldFilename;
    }

    public boolean isLatFileSaveRequired() {
        return (checkboxSaveLatFile.isSelected() && (selectedDataNodeMap.size() > 0));
    }

    public JCheckBox getCheckboxSaveLatFile() {
        return checkboxSaveLatFile;
    }

    /**
     *
     * @author Dr. Richard St. John
     * @version $Revision$, $Date$
     */
    private class TreeListener extends MouseAdapter implements ActionListener, KeyListener {

        @Override
        public void mousePressed(MouseEvent e) {
            TreePath path = tree.getPathForLocation(e.getX(), e.getY());
            if (path == null) {
                return;
            }

            CheckNode node = (CheckNode) path.getLastPathComponent();
            if (node == null) {
                return;
            }


            Object userObject = node.getUserObject();
            if (!(userObject instanceof LatFileTreeDataNode)) {
                CardLayout cl = (CardLayout) (cards.getLayout());
                cl.show(cards, "EMPTY_PANEL");
                return;
            } else {
                CardLayout cl = (CardLayout) (cards.getLayout());
                cl.show(cards, "OPTIONS_PANEL");
            }
            selectedDataNode = (LatFileTreeDataNode) userObject;

            if (selectedDataNode.isUserDefined()) {
                radioUserSpecified.setSelected(true);
            } else {
                radioWhenUpdated.setSelected(true);
            }

            updateSaveOptionsPanel();
            if (!isCheckboxClicked(e.getPoint(), path)) {
                return;
            }

            node.setSelected(!node.isSelected);
            ((DefaultTreeModel) tree.getModel()).nodeChanged(node);
            selectedDataNode.setSelected(node.isSelected);
            traverseTree();
        }

        public void updateSaveOptionsPanel() {
            fieldBegin.setText(selectedDataNode.getBeginTime().toString());
            fieldEnd.setText(selectedDataNode.getEndTime().toString());
            fieldPeriod.setText(selectedDataNode.getPeriod().toString());
            fieldMax.setText(selectedDataNode.getMaxFrameCount().toString());
            radioUserSpecified.setSelected(selectedDataNode.isUserDefined());
            setSaveOptionsEnabled(selectedDataNode.isUserDefined());
        }

        private boolean isCheckboxClicked(Point p, TreePath path) {
            Rectangle pathBounds = tree.getPathBounds(path);
            pathBounds.width = 20;
            return pathBounds.contains(p);
        }

        public void actionPerformed(ActionEvent e) {
            String command = e.getActionCommand();
            if (command.equalsIgnoreCase("WHEN_UPDATED")) {
                setSaveOptionsEnabled(false);
            } else if (command.equalsIgnoreCase("USER_DEFINED")) {
                setSaveOptionsEnabled(true);
            }
        }

        private void setSaveOptionsEnabled(boolean enabled) {
            if (selectedDataNode == null) {
                return;
            }
            Color bg = enabled ? UIManager.getColor("TextArea.background") : new Color(240, 240, 240);
            selectedDataNode.setUserDefined(enabled);
            beginLabel.setEnabled(enabled);
            fieldBegin.setEnabled(enabled);
            fieldBegin.setBackground(bg);
            beginLabelUnits.setEnabled(enabled);

            endLabel.setEnabled(enabled);
            fieldEnd.setEnabled(enabled);
            fieldEnd.setBackground(bg);
            endLabelUnits.setEnabled(enabled);

            periodLabel.setEnabled(enabled);
            fieldPeriod.setEnabled(enabled);
            fieldPeriod.setBackground(bg);
            periodLabelUnits.setEnabled(enabled);

            maxLabel.setEnabled(enabled);
            fieldMax.setEnabled(enabled);
            fieldMax.setBackground(bg);
            maxLabelUnits.setEnabled(enabled);
        }

        public void keyTyped(KeyEvent e) {
        }

        public void keyPressed(KeyEvent e) {
        }

        public void keyReleased(KeyEvent e) {
            JTextField textField = (JTextField) e.getSource();
            String s = textField.getText();
            if (s.trim().equalsIgnoreCase("")) {
                return;
            }
            Number n;
            try {
                n = Double.parseDouble(s);
                textField.setForeground(UIManager.getColor("TextField.foreground"));
            } catch (NumberFormatException ne) {
                textField.setForeground(Color.RED);
                return;
            }

            String name = textField.getName();
            if (name.equals("BEGIN")) {
                selectedDataNode.setBeginTime(n);
            } else if (name.equalsIgnoreCase("END")) {
                selectedDataNode.setEndTime(n);
            } else if (name.equalsIgnoreCase("PERIOD")) {
                selectedDataNode.setPeriod(n);
            } else if (name.equalsIgnoreCase("MAX")) {
                selectedDataNode.setMaxFrameCount(n);
            }
        }
    }

    /**
     *
     * @author Dr. Richard St. John
     * @version $Revision$, $Date$
     */
    private class PluginNode {

        private AbstractPlugin plugin;

        public PluginNode(AbstractPlugin plugin) {
            this.plugin = plugin;
        }

        @Override
        public String toString() {
            return plugin.getName();
        }

        public ImageIcon getIcon() {
            return Resources.createIcon("plugin22.png");
        }

        public AbstractPlugin getPlugin() {
            return plugin;
        }
    }

    /**
     *
     * @author Dr. Richard St. John
     * @version $Revision$, $Date$
     */
    private class SystemNode {

        private int index;
        private LatizSystem latizSystem;

        public SystemNode(LatizSystem latizSystem, int index) {
            this.latizSystem = latizSystem;
            this.index = index;
        }

        @Override
        public String toString() {
            return "System " + index;
        }

        public ImageIcon getIcon() {
            return Resources.createIcon("latizIcon22.png");
        }

        public LatizSystem getLatizSystem() {
            return latizSystem;
        }
    }

    /**
     *
     * @author Dr. Richard St. John
     * @version $Revision$, $Date$
     */
    private class PaletteNode {

        private String name;

        public PaletteNode(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }

        public ImageIcon getIcon() {
            return Resources.createIcon("katomic.png");
        }
    }

    private final class CheckNode extends DefaultMutableTreeNode {

        public final static int SINGLE_SELECTION = 0;
        public final static int DIG_IN_SELECTION = 4;
        protected int selectionMode;
        protected boolean isSelected;

        public CheckNode() {
            this(null);
        }

        public CheckNode(Object userObject) {
            this(userObject, true, false);
        }

        public CheckNode(Object userObject, boolean allowsChildren, boolean isSelected) {
            super(userObject, allowsChildren);
            this.isSelected = isSelected;
            setSelectionMode(DIG_IN_SELECTION);
        }

        public void setSelectionMode(int mode) {
            selectionMode = mode;
        }

        public int getSelectionMode() {
            return selectionMode;
        }

        public void setSelected(boolean isSelected) {
            this.isSelected = isSelected;

            if ((selectionMode == DIG_IN_SELECTION) && (children != null)) {
                Enumeration e = children.elements();
                while (e.hasMoreElements()) {
                    CheckNode node = (CheckNode) e.nextElement();
                    node.setSelected(isSelected);
                }
            }
        }

        public boolean isSelected() {
            return isSelected;
        }
        // If you want to change "isSelected" by CellEditor,
    /*
        public void setUserObject(Object obj) { if (obj instanceof Boolean) {
         * setSelected(((Boolean)obj).booleanValue()); } else {
         * super.setUserObject(obj); } }
         */
    }

    /**
     *
     * @author Dr. Richard St. John
     * @version $Revision$, $Date$
     */
    private class CheckRenderer extends JPanel implements TreeCellRenderer {

        private Color textForeground, textBackground;
        protected JCheckBox check;
        protected TreeLabel label;
        private JLabel icon;
        private Component decor;

        public CheckRenderer() {
            setLayout(null);
            textForeground = UIManager.getColor("Tree.textForeground");
            textBackground = UIManager.getColor("Tree.textBackground");

            check = new JCheckBox();
            icon = new JLabel(UIManager.getIcon("Tree.leafIcon"));
            add(label = new TreeLabel());
            check.setBackground(textBackground);
            label.setForeground(textForeground);
        }

        public Component getTreeCellRendererComponent(JTree tree, Object value, boolean isSelected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
            String stringValue = tree.convertValueToText(value, isSelected, expanded, leaf, row, hasFocus);
            setEnabled(tree.isEnabled());
            check.setSelected(((CheckNode) value).isSelected());
            label.setFont(tree.getFont());
            label.setText(stringValue);
            label.setSelected(isSelected);
            label.setFocus(hasFocus);
            if (leaf) {
                decor = check;
                remove(icon);
                add(check);
            } else {
                Object userObject = ((CheckNode) value).getUserObject();
                if (userObject instanceof SystemNode) {
                    icon.setIcon(((SystemNode) userObject).getIcon());
                    icon.setBorder(new EmptyBorder(1, 0, 1, 0));
                } else if (userObject instanceof PaletteNode) {
                    icon.setIcon(((PaletteNode) userObject).getIcon());
                    icon.setBorder(new EmptyBorder(1, 0, 1, 0));
                } else if (userObject instanceof PluginNode) {
                    icon.setIcon(((PluginNode) userObject).getIcon());
                    icon.setBorder(new EmptyBorder(1, 0, 1, 0));
                }
                decor = icon;
                remove(check);
                add(icon);
            }
            return this;
        }

        @Override
        public Dimension getPreferredSize() {
            Dimension d_check = check.getPreferredSize();
            Dimension d_label = label.getPreferredSize();
            return new Dimension(d_check.width + d_label.width, (d_check.height < d_label.height ? d_label.height : d_check.height));
        }

        @Override
        public void doLayout() {
            Dimension d_check = decor.getPreferredSize();
            Dimension d_label = label.getPreferredSize();
            int y_check = 0;
            int y_label = 0;
            if (d_check.height < d_label.height) {
                y_check = (d_label.height - d_check.height) / 2;
            } else {
                y_label = (d_check.height - d_label.height) / 2;
            }
            decor.setLocation(0, y_check);
            decor.setBounds(0, y_check, d_check.width, d_check.height);
            label.setLocation(d_check.width + 2, y_label);
            label.setBounds(d_check.width + 2, y_label, d_label.width, d_label.height);
        }

        @Override
        public void setBackground(Color color) {
            if (color instanceof ColorUIResource) {
                color = null;
            }
            super.setBackground(color);
        }
    }

    /**
     *
     * @author Dr. Richard St. John
     * @version $Revision$, $Date$
     */
    private class TreeLabel extends JLabel {

        boolean isSelected, hasFocus;

        public TreeLabel() {
        }

        @Override
        public void setBackground(Color color) {
            if (color instanceof ColorUIResource) {
                color = null;
            }
            super.setBackground(color);
        }

        @Override
        public void paint(Graphics g) {
            String str;
            if ((str = getText()) != null) {
                if (0 < str.length()) {
                    if (isSelected) {
                        g.setColor(UIManager.getColor("Tree.selectionBackground"));
                    } else {
                        g.setColor(UIManager.getColor("Tree.textBackground"));
                    }
                    Dimension d = getPreferredSize();
                    int imageOffset = 0;
                    Icon currentI = getIcon();
                    if (currentI != null) {
                        imageOffset = currentI.getIconWidth() + Math.max(0, getIconTextGap() - 1);
                    }
                    g.fillRect(imageOffset, 0, d.width - 1 - imageOffset, d.height);
                    if (hasFocus) {
                        g.setColor(UIManager.getColor("Tree.selectionBorderColor"));
                        g.drawRect(imageOffset, 0, d.width - 1 - imageOffset, d.height - 1);
                    }
                }
            }
            super.paint(g);
        }

        @Override
        public Dimension getPreferredSize() {
            Dimension retDimension = super.getPreferredSize();
            if (retDimension != null) {
                retDimension = new Dimension(retDimension.width + 3,
                        retDimension.height);
            }
            return retDimension;
        }

        public void setSelected(boolean isSelected) {
            this.isSelected = isSelected;
        }

        public void setFocus(boolean hasFocus) {
            this.hasFocus = hasFocus;
        }
    }
}
