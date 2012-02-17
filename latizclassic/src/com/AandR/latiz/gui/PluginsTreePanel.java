package com.AandR.latiz.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.border.EmptyBorder;
import javax.swing.text.Position;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import com.AandR.latiz.resources.Resources;

/**
 * @author Dr. Richard St. John
 * @version $Revision$, $Date$
 */
public class PluginsTreePanel extends JPanel {

    private TreeSet<String> parents;
    private DragAndDropTree tree;
    private DefaultMutableTreeNode root;
    private ImageIcon pluginIcon;
    private Set<PluginKey> pluginKeySet;
    private JScrollPane scroller;

    public PluginsTreePanel(Set<PluginKey> keySet) {
        this.pluginKeySet = keySet;
        initialize();

        createParents();
        addChildrenToTree();

        scroller = new JScrollPane(tree);
        scroller.setBorder(null);
        setLayout(new BorderLayout());
        add(scroller, BorderLayout.CENTER);
    }

    private void initialize() {
        pluginIcon = Resources.createIcon("plugin22.png");
        parents = new TreeSet<String>();
        root = new DefaultMutableTreeNode("Available Plugins");
        tree = new DragAndDropTree(new DefaultTreeModel(root));
        tree.setShowsRootHandles(true);
        tree.setCellRenderer(new TreeRenderer());
        tree.addMouseListener(new TreeMouseListener());
    }

    public void setPluginKeySet(Set<PluginKey> keySet) {
        this.pluginKeySet = keySet;
        ((DefaultTreeModel) tree.getModel()).setRoot(root = new DefaultMutableTreeNode("Available Plugins"));
        createParents();
        addChildrenToTree();
    }

    public Set<PluginKey> getPluginKeySet() {
        return pluginKeySet;
    }

    private void createParents() {
        parents.clear();
        for (PluginKey key : pluginKeySet) {
            parents.add(key.getParentID());
        }
        for (String p : parents) {
            createPluginTreeParent(p);
        }
        expandAllRows();
    }

    private void addChildrenToTree() {
        for (PluginKey key : pluginKeySet) {
            addPluginToTree(key.getParentID(), key);
        }
        expandAllRows();
    }

    public void createPluginTreeParent(String parentID) {
        DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
        model.insertNodeInto(new DefaultMutableTreeNode(parentID), root, root.getChildCount());
    }

    private void expandAllRows() {
        for (int i = 0; i < tree.getRowCount(); i++) {
            tree.expandRow(i);
        }
    }

    public DragAndDropTree getTree() {
        return tree;
    }

    private void addPluginToTree(String parent, PluginKey pluginKey) {
        TreePath thisPath = tree.getNextMatch(parent, 0, Position.Bias.Forward);
        if (thisPath == null) {
            return;
        }
        DefaultMutableTreeNode thisNode = (DefaultMutableTreeNode) thisPath.getLastPathComponent();
        DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
        model.insertNodeInto(new DefaultMutableTreeNode(pluginKey), thisNode, thisNode.getChildCount());
    }

    /**
     *
     * @author Dr. Richard St. John
     * @version $Revision$, $Date$
     */
    private class TreeMouseListener extends MouseAdapter implements ActionListener {

        private JPopupMenu popup;

        public TreeMouseListener() {
            popup = new JPopupMenu();
            popup.add(createMenuItem("Properties", Resources.createIcon("stock-preferences22.png"), "Show plugin properties dialog."));
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            if (e.getButton() != 3) {
                return;
            }

            int selRow = tree.getRowForLocation(e.getX(), e.getY());
            if (selRow < 0) {
                return;
            }

            tree.setSelectionPath(tree.getPathForLocation(e.getX(), e.getY()));

            if (selRow == 0) {
                // Root context menu, if necessary.
            } else {
                popup.show(tree, e.getX(), e.getY());
            }
        }

        private JMenuItem createMenuItem(String label, Icon icon, String tooltip) {
            JMenuItem item = new JMenuItem(label, icon);
            item.addActionListener(this);
            item.setActionCommand(label);
            return item;
        }

        public void actionPerformed(ActionEvent e) {
            String command = e.getActionCommand();
            if (command.equals("Properties")) {
                DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) tree.getSelectionPath().getLastPathComponent();
                PluginPropertiesDialog pluginPropertiesDialog = new PluginPropertiesDialog((PluginKey) selectedNode.getUserObject());
            }
        }
    }

    /**
     *
     * @author Dr. Richard St. John
     * @version $Revision$, $Date$
     */
    private class TreeRenderer extends DefaultTreeCellRenderer {

        @Override
        public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
            super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
            if (leaf) {
                setIcon(pluginIcon);
            } else {
                setIcon(null);
            }
            setText(node.getUserObject().toString());
            setBorder(new EmptyBorder(2, 0, 2, 0));
            return this;
        }
    }
}
