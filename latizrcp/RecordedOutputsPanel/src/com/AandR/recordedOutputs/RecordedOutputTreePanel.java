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
package com.AandR.recordedOutputs;

import com.AandR.latiz.core.lookup.LatizLookup;
import com.AandR.library.TreeUtility;
import com.AandR.library.gui.JZebraTree;
import com.AandR.palette.cookies.PaletteSelectionCookie;
import com.AandR.palette.dataWriter.DefaultSavedOutputsImpl;
import com.AandR.palette.paletteScene.IPaletteCleared;
import com.AandR.palette.paletteScene.IWorkspaceLoaded;
import com.AandR.palette.paletteScene.PaletteScene;
import com.AandR.palette.plugin.AbstractPlugin;
import com.AandR.palette.plugin.IPluginsAdded;
import com.AandR.palette.plugin.IPluginsRemoved;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
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
import javax.swing.text.JTextComponent;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import net.miginfocom.swing.MigLayout;

import com.AandR.palette.swing.LNumberField;
import com.AandR.recordedOutputs.RecordedOutputsTopComponent.ResolvableHelper;
import java.awt.Image;
import java.util.Collections;
import java.util.Comparator;
import javax.swing.JButton;
import javax.swing.JToolBar;
import org.jdom.Element;
import org.openide.util.ImageUtilities;

/**
 * @author Dr. Richard St. John
 * @version $Revision$, $Date$
 */
public class RecordedOutputTreePanel extends JPanel implements IWorkspaceLoaded {
    private HashMap<AbstractPlugin, ArrayList<OutputsTreeDataNode>> selectedDataNodeMap;
    private HashMap<String, String> treeExpansionStateMaps;
    private JCheckBox checkboxSaveLatFile;
    private JLabel beginLabel,  endLabel,  periodLabel,  maxLabel;
    private JLabel beginLabelUnits,  endLabelUnits,  periodLabelUnits,  maxLabelUnits;
    private JPanel cards;
    private JRadioButton radioUserSpecified,  radioWhenUpdated;
    private JSplitPane splitterTree;
    private JZebraTree tree;
    private JTextField fieldBegin,  fieldEnd,  fieldPeriod,  fieldMax;
    private OutputsTreeDataNode selectedDataNode;
    private PaletteScene activeScene;
    private TreeListener treeListener;

    public RecordedOutputTreePanel() {
        LatizLookup.getDefault().addToLookup(this);
        LatizLookup.getDefault().addToLookup(new IPaletteCleared() {
            public void paletteCleared(String paletteName) {
                selectedDataNodeMap.clear();
                selectedDataNodeMap.clear();
                reloadTree();
            }
        });
        LatizLookup.getDefault().addToLookup(new IPluginsAdded() {
            public void pluginsAdded(PaletteScene scene, ArrayList<AbstractPlugin> plugins) {
                PaletteSelectionCookie psc = LatizLookup.getDefault().lookup(PaletteSelectionCookie.class);
                if(psc==null) return;
                activeScene = psc.getActivePalette().getScenePanel().getScene();
                reloadTree();
            }
        });
        LatizLookup.getDefault().addToLookup(new IPluginsRemoved() {
            public void removePlugins(PaletteScene scene, ArrayList<AbstractPlugin> pluginsRemoved) {
                PaletteSelectionCookie psc = LatizLookup.getDefault().lookup(PaletteSelectionCookie.class);
                if(psc==null) return;
                activeScene = psc.getActivePalette().getScenePanel().getScene();
                reloadTree();
            }
        });
        initialize();
        setLayout(new MigLayout("ins 0, fill"));
        add(createContentPane(), "push, grow, wrap");
    }

    public void load(PaletteScene scene, Element root) {
        activeScene = scene;
        updateSelectedDataNodeMap();
        //updateSelectedDataNodes();
        reloadTree();
    }

//    void pluginNameWillChange(GraphScene<PluginNode, ConnectorEdge> scene, AbstractPlugin p, String oldName, String newName) {
//        int index = pluginList.indexOf(p);
//        if (index > 0) {
//            pluginList.remove(p);
//        }
//        renamedDataNodeList = selectedDataNodeMap.get(p);
//        selectedDataNodeMap.remove(p);
//    }
//
//    void pluginNameChanged(GraphScene<PluginNode, ConnectorEdge> scene, AbstractPlugin plugin, String oldName, String newName) {
//        selectedDataNodeMap.put(plugin, renamedDataNodeList);
//        pluginList.add(plugin);
//        reloadTree();
//    }

    void pluginSelectionChanged(AbstractPlugin plugin) {
        TreeNode node = findTreeNode(plugin);
        TreePath path = getPath(node);
        if(path==null) {
            path = new TreePath(tree.getModel().getRoot());
        }
        tree.setSelectionPath(path);
        tree.scrollPathToVisible(path);
    }

//    void pluginsAdded(ArrayList<AbstractPlugin> plugins) {
//        for (AbstractPlugin p : plugins) {
//            if (p.getOutputDataMap().isEmpty()) {
//                continue;
//            }
//            pluginList.add(p);
//        }
//        reloadTree();
//    }
//
//    void pluginsRemoved(ArrayList<AbstractPlugin> plugins) {
//        pluginList.removeAll(plugins);
//        for (AbstractPlugin p : plugins) {
//            selectedDataNodeMap.remove(p);
//        }
//        reloadTree();
//    }

    void updateSavedOutputsMap() {
        if (activeScene == null) {
            return;
        }
        HashMap<String, LinkedHashMap<String, DefaultSavedOutputsImpl>> savedOutputsMap = activeScene.getDefaultPaletteModel().getSavedOutputsMap();
        savedOutputsMap.clear();
        DefaultSavedOutputsImpl thisOutput;
        ArrayList<OutputsTreeDataNode> dataItems;
        LinkedHashMap<String, DefaultSavedOutputsImpl> thisSavedData;
        for (AbstractPlugin p : selectedDataNodeMap.keySet()) {
            dataItems = selectedDataNodeMap.get(p);
            if (dataItems == null) {
                continue;
            }
            thisSavedData = new LinkedHashMap<String, DefaultSavedOutputsImpl>();
            for (OutputsTreeDataNode dataNode : dataItems) {
                String name = dataNode.getDatasetName();
                String beginTime = dataNode.getBeginTime();
                String endTime = dataNode.getEndTime();
                String maxCount = dataNode.getMaxIterationCount();
                String period = dataNode.getPeriod();
                thisOutput = new DefaultSavedOutputsImpl();
                thisOutput.setDatasetName(name);
                thisOutput.setBeginTime(beginTime);
                thisOutput.setEndTime(endTime);
                thisOutput.setPeriod(period);
                thisOutput.setMaxIterationCount(maxCount);
                thisSavedData.put(name, thisOutput);
            }
            savedOutputsMap.put(p.getName(), thisSavedData);
        }
    }

    private void initialize() {
        selectedDataNodeMap = new HashMap<AbstractPlugin, ArrayList<OutputsTreeDataNode>>();
        treeExpansionStateMaps = new HashMap<String, String>();

        tree = new JZebraTree(new DefaultTreeModel(new CheckTreeNode(new PaletteTreeNode("Available Plugins"))));
        tree.addMouseListener(treeListener = new TreeListener());
        tree.setShowsRootHandles(true);
        tree.setRowHeight(22);
        tree.setCellRenderer(new CheckRenderer());
        tree.setEditable(false);

        cards = new JPanel(new CardLayout());
        cards.add(new JPanel(), "EMPTY_PANEL");
        cards.add(createSaveOptionsPanel(), "OPTIONS_PANEL");
        CardLayout cl = (CardLayout) (cards.getLayout());
        cl.show(cards, "EMPTY_PANEL");
    }

    private Component createContentPane() {
        JButton expandButton = createToolbarButton("expandTree16.png", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setAllExpanded(tree, new TreePath(tree.getModel().getRoot()), true);
            }
        }, "Expand all nodes of the tree.");
        JButton collapseButton = createToolbarButton("collapseTree16.png", new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                setAllExpanded(tree, new TreePath(tree.getModel().getRoot()), false);
            }
        }, "Collapse all nodes of the tree.");
        JButton checkAllButton = createToolbarButton("check20.png", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setAllSelected(tree, true);
            }
        }, "Select all data nodes for saving.");
        JButton uncheckAllButton = createToolbarButton("uncheck20.png", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setAllSelected(tree, false);
            }
        }, "Unselect all data nodes for saving.");
        JToolBar toolbar = new JToolBar();
        toolbar.setLayout(new MigLayout("ins 0", "[25!]2[25!]2[25!]2[25!]push[]", ""));
        toolbar.setFloatable(false);
        toolbar.add(expandButton, "w 25!, h 25!, sg");
        toolbar.add(collapseButton, "sg");
        toolbar.add(checkAllButton, "sg");
        toolbar.add(uncheckAllButton, "sg, wrap");
//        JButton browseButton = new JButton(new ImageIcon(ImageUtilities.loadImage("com/AandR/recordedOutputs/resources/find16.png")));
//        browseButton.setPreferredSize(new Dimension(22, 22));
//        browseButton.addActionListener(new ActionListener() {
//
//            public void actionPerformed(ActionEvent e) {
//                JFileChooser chooser = new JFileChooser(System.getProperty("user.home"));
//                int selection = chooser.showSaveDialog(null);
//                if (selection == JFileChooser.APPROVE_OPTION) {
//                    fieldFilename.setText(chooser.getSelectedFile().getPath());
//                }
//            }
//        });
//        //String defaultPath = PropertiesManager.getInstanceOf().getProperty(PropertiesManager.GENERAL_LAT_PATH);
        JPanel filenamePanel = new JPanel(new MigLayout("ins 0"));
//        String defaultPath = NbPreferences.forModule(RecordedOutputsOptionsPanelController.class).get("defaultDirectory", System.getProperty("user.home"));
        filenamePanel.add(checkboxSaveLatFile = new JCheckBox("Enable Data Saving?", true), "growx, gapright unrel");
//        filenamePanel.add(new JLabel("Filename:"));
//        filenamePanel.add(fieldFilename = new JTextField(defaultPath + File.separator + "latizOutput.lat5", 20), "pushx, growx");
//        filenamePanel.add(browseButton, "w 25!, h 25!, wrap");

        JPanel panel = new JPanel(new MigLayout("ins 0", "", "3[]"));
        panel.add(filenamePanel, "pushx, growx, wrap");
        panel.add(toolbar, "pushx, growx, wrap");
        panel.add(new JScrollPane(tree), "push, grow, wrap");
        splitterTree = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitterTree.setOneTouchExpandable(true);
        splitterTree.setDividerSize(8);
        splitterTree.setTopComponent(panel);
        splitterTree.setBottomComponent(cards);
        return splitterTree;
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

        JPanel parameterPanel = new JPanel(new MigLayout("", "[][][]", ""));
        parameterPanel.setBorder(new TitledBorder("Save Options"));

        parameterPanel.add(radioWhenUpdated, "spanx, wrap");
        parameterPanel.add(radioUserSpecified, "spanx, wrap");

        parameterPanel.add(beginLabel = createLabel("Begin Time:", radioUserSpecified.isSelected()));
        parameterPanel.add(fieldBegin = createInputField("BEGIN"), "pushx, growx");
        parameterPanel.add(beginLabelUnits = createUnitsLabel("sec", radioUserSpecified.isSelected()), "wrap");

        parameterPanel.add(endLabel = createLabel("End Time:", radioUserSpecified.isSelected()));
        parameterPanel.add(fieldEnd = createInputField("END"), "pushx, growx");
        parameterPanel.add(endLabelUnits = createUnitsLabel(" sec", radioUserSpecified.isSelected()), "wrap");

        parameterPanel.add(periodLabel = createLabel("Period:", radioUserSpecified.isSelected()));
        parameterPanel.add(fieldPeriod = createInputField("PERIOD"), "pushx, growx");
        parameterPanel.add(periodLabelUnits = createUnitsLabel(" sec", radioUserSpecified.isSelected()), "wrap");

        parameterPanel.add(maxLabel = createLabel("Do not exceed:", radioUserSpecified.isSelected()));
        parameterPanel.add(fieldMax = createInputField("MAX"), "pushx, growx");
        parameterPanel.add(maxLabelUnits = createUnitsLabel(" frames", radioUserSpecified.isSelected()), "wrap");
        return parameterPanel;
    }

    private JTextField createInputField(String name) {
        final JTextField field = new JTextField(8);
        field.setName(name);
        field.setEnabled(radioUserSpecified.isSelected());
        field.addKeyListener(treeListener);
//        field.addNumberFieldListener(new LNumberFieldListener() {
//            public void numberFieldChanged() {
//                String name = field.getName();
//                if (name.equals("BEGIN")) {
//                    selectedDataNode.setBeginTime(field.getText());
//                } else if (name.equals("END")) {
//                    selectedDataNode.setEndTime(field.getText());
//                } else if (name.equals("PERIOD")) {
//                    selectedDataNode.setPeriod(field.getText());
//                } else if (name.equals("MAX")) {
//                    selectedDataNode.setMaxIterationCount(field.getText());
//                }
//            }
//        });
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

    private JButton createToolbarButton(String iconName, ActionListener al, String tooltip) {
        Image im = ImageUtilities.loadImage("com/AandR/recordedOutputs/resources/" + iconName);
        JButton button = new JButton(new ImageIcon(im));
        button.addActionListener(al);
        button.setToolTipText(tooltip);
        return button;
    }

    private void create(Set<AbstractPlugin> plugins) {
        ((DefaultTreeModel)tree.getModel()).setRoot(new CheckTreeNode(new PaletteTreeNode("Available Plugins")));
        Set<String> varNames;
        CheckTreeNode thisPluginNode;
        MutableTreeNode root = (MutableTreeNode) tree.getModel().getRoot();
        DefaultTreeModel treeModel = (DefaultTreeModel)tree.getModel();
        for (AbstractPlugin p : plugins) {
            //varNames = parameterPanel.getSavableOutputKeys();
            varNames = p.getOutputDataMap().keySet();
            if (varNames == null || varNames.isEmpty()) {
                continue;
            }

            thisPluginNode = new CheckTreeNode(new PluginTreeNode(p));
            treeModel.insertNodeInto(thisPluginNode, root, root.getChildCount());

            ArrayList<OutputsTreeDataNode> outputNodes = selectedDataNodeMap.get(p);
            for (String varName : varNames) {
                CheckTreeNode checkNode = new CheckTreeNode(new OutputsTreeDataNode(varName, true));
                checkNode.setSelected(outputNodes != null && findOutputNode(outputNodes, varName));
                treeModel.insertNodeInto(checkNode, thisPluginNode, thisPluginNode.getChildCount());
            }
        }
    }

    private boolean findOutputNode(ArrayList<OutputsTreeDataNode> selectedNodes, String nodeNameToTest) {
        for (OutputsTreeDataNode node : selectedNodes) {
            if (node.getDatasetName().equals(nodeNameToTest)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Does a complete reload of the lat-file tree.
     * @param latizSystems
     * @param pluginOutgoingConnectorMaps
     */
    void reloadTree() {
        TreeSet<AbstractPlugin> sortedPlugins = new TreeSet<AbstractPlugin>(new Comparator<AbstractPlugin>() {
            public int compare(AbstractPlugin o1, AbstractPlugin o2) {
                return o1.getName().compareToIgnoreCase(o2.getName());
            }
        });
        if(activeScene==null || activeScene.getPluginsMap().isEmpty()) {
            return;
        }
        
        for (AbstractPlugin p : activeScene.getPluginsMap().values()) {
            sortedPlugins.add(p);
        }
        String expansionState = TreeUtility.getExpansionState(tree, 0);
        create(sortedPlugins);
        tree.revalidate();
        tree.repaint();
        TreeUtility.restoreExpanstionState(tree, 0, expansionState);
        tree.expandRow(0);
    }

    /**
     *
     * @param latizSystem
     * @param parameterPanel
     */
//    private void updateAfterPluginOutputsChanged(AbstractPlugin p) {
//        //String[] keys = parameterPanel.getSavableOutputKeys();
//        Set<String> keys = p.getOutputDataMap().keySet();
//        CheckTreeNode parent = null;
//        CheckTreeNode node = (CheckTreeNode) findTreeNode(p);
//
//        DefaultTreeModel treeModel = (DefaultTreeModel) tree.getModel();
//        CheckTreeNode root = (CheckTreeNode)treeModel.getRoot();
//        if (node != null) {
//            parent = (CheckTreeNode) node.getParent();
//            treeModel.removeNodeFromParent(node);
//            if (keys.isEmpty()) {
//                return;
//            }
//        } else {
//            parent = root;
//        }
//
//        treeModel.insertNodeInto(node = new CheckTreeNode(new PluginTreeNode(p)), parent, parent.getChildCount());
//        for (String key : keys) {
//            treeModel.insertNodeInto(new CheckTreeNode(new OutputsTreeDataNode(key, true)), (MutableTreeNode) node, node.getChildCount());
//        }
//        tree.expandPath(new TreePath(node.getPath()));
//    }

    /**
     * Returns the tree node that contains the given data object.
     */
    private TreeNode findTreeNode(AbstractPlugin p) {
        if (p == null) {
            return null;
        }

        CheckTreeNode theNode = null;
        Object userObject = null;
        Enumeration local_enum = ((CheckTreeNode)tree.getModel().getRoot()).breadthFirstEnumeration();
        while (local_enum.hasMoreElements()) {
            theNode = (CheckTreeNode) local_enum.nextElement();
            userObject = theNode.getUserObject();
            if (userObject == null || !(userObject instanceof PluginTreeNode)) {
                continue;
            }
            PluginTreeNode node = (PluginTreeNode) userObject;
            AbstractPlugin np = node.getPlugin();
            if (np.getName().equals(p.getName())) {
                return theNode;
            }
        }
        return null;
    }

    /**
     * Returns a TreePath containing the specified node.
     */
    @SuppressWarnings(value="unchecked")
    public TreePath getPath(TreeNode node) {
        List list = new ArrayList();

        // Add all nodes to list
        while (node != null) {
            list.add(node);
            node = node.getParent();
        }
        if(list.isEmpty()) {
            return null;
        }
        
        Collections.reverse(list);

        // Convert array of nodes to TreePath
        return new TreePath(list.toArray());
    }

    /**
     *
     * @param label
     * @return
     */
//    private TreeNode findTreeNode(String label) {
//        if (label == null) {
//            return null;
//        }
//
//        CheckTreeNode theNode = null;
//        Object userObject = null;
//        Enumeration local_enum = ((CheckTreeNode) root).breadthFirstEnumeration();
//        while (local_enum.hasMoreElements()) {
//            theNode = (CheckTreeNode) local_enum.nextElement();
//            userObject = theNode.getUserObject();
//            if (userObject == null || !(userObject instanceof PluginTreeNode)) {
//                continue;
//            }
//
//            PluginTreeNode node = (PluginTreeNode) userObject;
//            AbstractPlugin np = node.getPlugin();
//            if (np.getName().equals(label)) {
//                return theNode;
//            }
//        }
//        return null;
//    }

//    public void reset() {
//        ((DefaultTreeModel)tree.getModel()).setRoot(new CheckTreeNode(new PaletteTreeNode("Selected Palette")));
//    }

    public void setSelectedDataNodeMap(HashMap<AbstractPlugin, ArrayList<OutputsTreeDataNode>> selectedDataNodeMap) {
        this.selectedDataNodeMap = selectedDataNodeMap;
    }

    public void updateSelectedDataNodes() {
        DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
        CheckTreeNode rootNode = (CheckTreeNode) model.getRoot();
        Enumeration enums = rootNode.breadthFirstEnumeration();
        CheckTreeNode thisNode = null;
        CheckTreeNode thisDataNode = null;
        ArrayList<OutputsTreeDataNode> theseSelectedNodes;
        AbstractPlugin selectedPlugin = null;
        OutputsTreeDataNode ld = null;
        while (enums.hasMoreElements()) {
            thisNode = (CheckTreeNode) enums.nextElement();
            if (!(thisNode.getUserObject() instanceof PluginTreeNode)) {
                continue;
            }

            selectedPlugin = ((PluginTreeNode) thisNode.getUserObject()).getPlugin();
            theseSelectedNodes = selectedDataNodeMap.get(selectedPlugin);
            if (theseSelectedNodes == null) {
                continue;
            }

            // Loop over each data node
            for (int i = 0; i < thisNode.getChildCount(); i++) {
                thisDataNode = (CheckTreeNode) thisNode.getChildAt(i);

                ld = (OutputsTreeDataNode) thisDataNode.getUserObject();
                String datasetName = ld.getDatasetName();

                for (OutputsTreeDataNode selectedNode : theseSelectedNodes) {
                    if (selectedNode.getDatasetName().equals(datasetName)) {
                        thisDataNode.setSelected(true);
                        ld.setBeginTime(selectedNode.getBeginTime());
                        ld.setEndTime(selectedNode.getEndTime());
                        ld.setPeriod(selectedNode.getPeriod());
                        ld.setMaxIterationCount(selectedNode.getMaxIterationCount());
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
        DefaultTreeModel nodel = (DefaultTreeModel) tree.getModel();
        CheckTreeNode rootNode = (CheckTreeNode) nodel.getRoot();
        Enumeration enums = rootNode.breadthFirstEnumeration();
        CheckTreeNode thisNode = null;
        CheckTreeNode thisDataNode = null;
        while (enums.hasMoreElements()) {
            thisNode = (CheckTreeNode) enums.nextElement();
            if (thisNode.getUserObject() instanceof PluginTreeNode) {
                for (int i = 0; i < thisNode.getChildCount(); i++) {
                    thisDataNode = (CheckTreeNode) thisNode.getChildAt(i);
                    ((OutputsTreeDataNode) thisDataNode.getUserObject()).setSelected(selected);
                    nodel.nodeChanged(thisDataNode);
                }
            }
        }
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
        Enumeration enums = ((CheckTreeNode)tree.getModel().getRoot()).breadthFirstEnumeration();
        OutputsTreeDataNode thisDataNode = null;
        CheckTreeNode thisChildNode = null;
        CheckTreeNode thisNode = null;
        ArrayList<OutputsTreeDataNode> theseNodesSelectedDataNodes = null;
        while (enums.hasMoreElements()) {
            thisNode = (CheckTreeNode) enums.nextElement();
            if (!(thisNode.getUserObject() instanceof PluginTreeNode)) {
                continue;
            }

            theseNodesSelectedDataNodes = new ArrayList<OutputsTreeDataNode>();
            for (int i = 0; i < thisNode.getChildCount(); i++) {
                thisChildNode = (CheckTreeNode) thisNode.getChildAt(i);
                if (thisChildNode.isSelected()) {
                    thisDataNode = (OutputsTreeDataNode) thisChildNode.getUserObject();
                    theseNodesSelectedDataNodes.add(thisDataNode);
                }
            }
            if (theseNodesSelectedDataNodes.size() > 0) {
                selectedDataNodeMap.put(((PluginTreeNode) thisNode.getUserObject()).plugin, theseNodesSelectedDataNodes);
            }
        }
    }

//    public HashMap<AbstractPlugin, ArrayList<OutputsTreeDataNode>> getSelectedLatFileMap() {
//        return selectedDataNodeMap;
//    }

//    public String getFileName() {
//        String name = fieldFilename.getText().trim();
//        if (name == null || name.equals("")) {
//            fieldFilename.setText(System.getProperty("user.home" + File.separator + "latizOutput.lat5"));
//        }
//        if (!(name.endsWith(".h5") || name.endsWith(".lat5"))) {
//            name += ".lat5";
//            fieldFilename.setText(name);
//        }
//        return fieldFilename.getText();
//    }
//
//    public void setFileName(String filename) {
//        fieldFilename.setText(filename);
//    }
//
//    public JTextField getFieldFilename() {
//        return fieldFilename;
//    }

    public boolean isDataWritingRequired() {
        return (checkboxSaveLatFile.isSelected() && (selectedDataNodeMap.size() > 0));
    }

//    public JCheckBox getCheckboxSaveLatFile() {
//        return checkboxSaveLatFile;
//    }

//    public JSplitPane getSplitterTree() {
//        return splitterTree;
//    }

//    public PaletteScene getActiveScene() {
//        return activeScene;
//    }

    public void setActiveScene(PaletteScene activeScene) {
        if(activeScene==null) {
            selectedDataNodeMap.clear();
            ((DefaultTreeModel) tree.getModel()).setRoot(new CheckTreeNode(new PaletteTreeNode("Available Plugins")));
            return;
        }
        this.activeScene = activeScene;
        updateSelectedDataNodeMap();
        reloadTree();
    }

    private void updateSelectedDataNodeMap() {
        selectedDataNodeMap.clear();
        HashMap<String, LinkedHashMap<String, DefaultSavedOutputsImpl>> savedOutputsMap = activeScene.getDefaultPaletteModel().getSavedOutputsMap();
        OutputsTreeDataNode thisTreeDataNode;
        LinkedHashMap<String, DefaultSavedOutputsImpl> thisSavedData;
        for (String pluginName : savedOutputsMap.keySet()) {
            thisSavedData = savedOutputsMap.get(pluginName);
            ArrayList<OutputsTreeDataNode> outputNodes = new ArrayList<OutputsTreeDataNode>();
            for (String dataName : thisSavedData.keySet()) {
                thisTreeDataNode = new OutputsTreeDataNode(dataName, true);
                convertDataObjects(thisSavedData.get(dataName), thisTreeDataNode);
                outputNodes.add(thisTreeDataNode);
            }
            selectedDataNodeMap.put(activeScene.getPluginsMap().get(pluginName), outputNodes);
        }
    }

    private void convertDataObjects(DefaultSavedOutputsImpl savedOutputsData, OutputsTreeDataNode treeDataNode) {
        treeDataNode.setBeginTime(savedOutputsData.getBeginTime());
        treeDataNode.setEndTime(savedOutputsData.getEndTime());
        treeDataNode.setMaxIterationCount(savedOutputsData.getMaxIterationCount());
        treeDataNode.setPeriod(savedOutputsData.getPeriod());
        treeDataNode.setUserDefined(savedOutputsData.isUserDefined());
    }

    public void saveTreeExpansionState() {
        if(activeScene==null || tree==null) return;
        treeExpansionStateMaps.put(activeScene.getName(), TreeUtility.getExpansionState(tree, 0));
    }

    public void restoreTreeExpansionState() {
        if(activeScene==null) return;
        String state = treeExpansionStateMaps.get(activeScene.getName());
        if(state==null || state.length()==0) return;
        TreeUtility.restoreExpanstionState(tree, 0, state);
    }

    Object writeReplace() {
        int dividerLocation = splitterTree.getDividerLocation();
        boolean isSelected = checkboxSaveLatFile.isSelected();
        return new ResolvableHelper(isSelected, dividerLocation);
    }

    Object readResolve(boolean selected, int dividerLocation) {
        RecordedOutputsTopComponent result = RecordedOutputsTopComponent.getDefault();
        checkboxSaveLatFile.setSelected(selected);
        splitterTree.setDividerLocation(dividerLocation);
        return result;
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

            CheckTreeNode node = (CheckTreeNode) path.getLastPathComponent();
            if (node == null) {
                return;
            }


            Object userObject = node.getUserObject();
            if (!(userObject instanceof OutputsTreeDataNode)) {
                CardLayout cl = (CardLayout) (cards.getLayout());
                cl.show(cards, "EMPTY_PANEL");
                return;
            } else {
                CardLayout cl = (CardLayout) (cards.getLayout());
                cl.show(cards, "OPTIONS_PANEL");
            }
            selectedDataNode = (OutputsTreeDataNode) userObject;

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
            fieldBegin.setText(selectedDataNode.getBeginTime());
            fieldEnd.setText(selectedDataNode.getEndTime().toString());
            fieldPeriod.setText(selectedDataNode.getPeriod().toString());
            fieldMax.setText(selectedDataNode.getMaxIterationCount().toString());
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
            if (command.equals("WHEN_UPDATED")) {
                setSaveOptionsEnabled(false);
            } else if (command.equals("USER_DEFINED")) {
                setSaveOptionsEnabled(true);
            }
        }

        private final void setSaveOptionsEnabled(boolean enabled) {
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
            Object f = e.getSource();
            String s = "";
            JTextComponent tc;
            if (f instanceof LNumberField) {
                LNumberField field = (LNumberField) e.getSource();
                tc = field;
                try {
                    s = String.valueOf(field.parse());
                } catch (ParseException e1) {
                    tc.setForeground(Color.RED);
                }
            } else {
                JTextField textField = (JTextField) e.getSource();
                tc = textField;
                s = textField.getText();
            }
            if (s.trim().equals("")) {
                return;
            }

            try {
                Double.parseDouble(s);
                tc.setForeground(UIManager.getColor("TextField.foreground"));
            } catch (NumberFormatException ne) {
                tc.setForeground(Color.RED);
                return;
            }

            String name = tc.getName();
            String t = tc.getText();
            if (name.equals("BEGIN")) {
                selectedDataNode.setBeginTime(t);
            } else if (name.equals("END")) {
                selectedDataNode.setEndTime(t);
            } else if (name.equals("PERIOD")) {
                selectedDataNode.setPeriod(t);
            } else if (name.equals("MAX")) {
                selectedDataNode.setMaxIterationCount(t);
            }
        }
    }

    /**
     *
     * @author Dr. Richard St. John
     * @version $Revision$, $Date$
     */
    private class PluginTreeNode {

        private AbstractPlugin plugin;

        public PluginTreeNode(AbstractPlugin plugin) {
            this.plugin = plugin;
        }

        @Override
        public String toString() {
            return plugin.getName();
        }

        public ImageIcon getIcon() {
            Image img = ImageUtilities.loadImage("com/AandR/palette/plugin/plugin22.png");
            return new ImageIcon(img);
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
    private class PaletteTreeNode {

        private String name;

        public PaletteTreeNode(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }

        public ImageIcon getIcon() {
            Image img = ImageUtilities.loadImage("com/AandR/palette/plugin/plugin22.png");
            return new ImageIcon(img);
        }
    }

    /**
     *
     */
    private class CheckTreeNode extends DefaultMutableTreeNode {
        public final static int SINGLE_SELECTION = 0;
        public final static int DIG_IN_SELECTION = 4;
        protected int selectionMode;
        protected boolean isSelected;

        public CheckTreeNode() {
            this(null);
        }

        public CheckTreeNode(Object userObject) {
            this(userObject, true, false);
        }

        public CheckTreeNode(Object userObject, boolean allowsChildren, boolean isSelected) {
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
                    CheckTreeNode node = (CheckTreeNode) e.nextElement();
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

        private Color textForeground,  textBackground;
        protected JCheckBox check;
        protected TreeLabel label;
        private JLabel icon;
        private Component decor;

        public CheckRenderer() {
            setLayout(null);
            setOpaque(false);
            textForeground = UIManager.getColor("Tree.textForeground");
            //textBackground = UIManager.getColor("Tree.textBackground");
            textBackground = new Color(255, 255, 255, 0);
            List gradient = (List) UIManager.get("CheckBox.gradient");
            UIManager.put("CheckBox.gradient", gradient);
            check = new JCheckBox();
            check.setContentAreaFilled(false);

            icon = new JLabel(UIManager.getIcon("Tree.leafIcon"));
            add(label = new TreeLabel());
            check.setBackground(textBackground);
            label.setForeground(textForeground);
        }

        public Component getTreeCellRendererComponent(JTree tree, Object value, boolean isSelected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
            String stringValue = tree.convertValueToText(value, isSelected, expanded, leaf, row, hasFocus);
            setEnabled(tree.isEnabled());
            check.setSelected(((CheckTreeNode) value).isSelected());
            label.setFont(tree.getFont());
            label.setText(stringValue);
            label.setSelected(isSelected);
            label.setFocus(hasFocus);
            if (leaf) {
                decor = check;
                remove(icon);
                add(check);
            } else {
                Object userObject = ((CheckTreeNode) value).getUserObject();
                if (userObject instanceof PaletteTreeNode) {
                    icon.setIcon(((PaletteTreeNode) userObject).getIcon());
                    label.setBorder(new EmptyBorder(3, 2, 3, 0));
                } else if (userObject instanceof PluginTreeNode) {
                    icon.setIcon(((PluginTreeNode) userObject).getIcon());
                    label.setBorder(new EmptyBorder(3, 2, 3, 0));
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
            return new Dimension(d_check.width + d_label.width + 10, (d_check.height < d_label.height ? d_label.height : d_check.height));
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
                    //Color c = isSelected ? UIManager.getColor("Tree.selectionBackground") : UIManager.getColor("Tree.textBackground");
                    Color c = isSelected ? UIManager.getColor("Tree.selectionBackground") : new Color(255, 255, 255, 0);
                    g.setColor(c);
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
                retDimension = new Dimension(retDimension.width + 3, retDimension.height);
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
