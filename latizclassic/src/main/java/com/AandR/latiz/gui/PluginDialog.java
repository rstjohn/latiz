package com.AandR.latiz.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.border.TitledBorder;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import com.AandR.gui.ui.JButtonX;
import com.AandR.gui.ui.JToolbarButton;
import com.AandR.latiz.core.PluginManager;
import com.AandR.latiz.resources.Resources;

/**
 * @author Dr. Richard St. John
 * @version $Revision: 1.2 $, $Date: 2007/08/01 01:28:46 $
 */
public class PluginDialog extends JDialog implements ActionListener {

    private boolean isCancelled = true;
    private PluginsTreePanel activeTreePanel, inactiveTreePanel;

    public PluginDialog() {
        super(new JFrame(), "Plugin Manager", true);
        setContentPane(createContentPane());
        pack();
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension dialog = getSize();
        int xLoc = (screen.width - dialog.width) / 2;
        int yLoc = ((int) (.80 * screen.height) - dialog.height) / 2;
        setLocation(xLoc, yLoc);
        setVisible(true);
    }

    private JPanel createContentPane() {
        Dimension listDim = new Dimension(250, 300);

        inactiveTreePanel = new PluginsTreePanel(PluginManager.getInstanceOf().getInactivePlugins().keySet());
        inactiveTreePanel.setPreferredSize(listDim);
        inactiveTreePanel.setMinimumSize(listDim);
        TitledBorder border = new TitledBorder("Inactive Plugins");
        JPanel availablePanel = new JPanel(new BorderLayout());
        availablePanel.setBorder(border);
        availablePanel.add(inactiveTreePanel, BorderLayout.CENTER);

        activeTreePanel = new PluginsTreePanel(PluginManager.getInstanceOf().getPlugins().keySet());
        activeTreePanel.setPreferredSize(listDim);
        activeTreePanel.setMinimumSize(listDim);
        TitledBorder loadedBorder = new TitledBorder("Active Plugins");
        JPanel loadedPanel = new JPanel(new BorderLayout());
        loadedPanel.setBorder(loadedBorder);
        loadedPanel.add(activeTreePanel, BorderLayout.CENTER);

        JPanel listPanel = new JPanel();
        listPanel.add(availablePanel);
        listPanel.add(createButtonPanel());
        listPanel.add(loadedPanel);

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(listPanel, BorderLayout.CENTER);
        panel.add(createBottomPanel(), BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createBottomPanel() {
        JButtonX okButton = new JButtonX("Accept", Resources.createIcon("accept22.png"));
        okButton.addActionListener(this);
        okButton.setActionCommand("ACCEPT");

        JButtonX cancelButton = new JButtonX("Cancel", Resources.createIcon("cancel22.png"));
        cancelButton.addActionListener(this);
        cancelButton.setActionCommand("CANCEL");

        JPanel panel = new JPanel();
        panel.add(okButton);
        panel.add(cancelButton);
        return panel;
    }

    private JPanel createButtonPanel() {
        JToolbarButton addButton = new JToolbarButton(Resources.createIcon("greenRightArrow22.png"));
        addButton.setPreferredSize(new Dimension(33, 33));
        addButton.setToolTipText("Add To Currently Loaded Plugins");
        addButton.addActionListener(this);
        addButton.setActionCommand("ADD");

        JToolbarButton removeButton = new JToolbarButton(Resources.createIcon("redLeftArrow22.png"));
        removeButton.setPreferredSize(new Dimension(33, 33));
        removeButton.setToolTipText("Remove Currently Loaded Plugins");
        removeButton.addActionListener(this);
        removeButton.setActionCommand("REMOVE");

        JPanel panel = new JPanel(new GridLayout(2, 1));
        panel.add(addButton);
        panel.add(removeButton);
        return panel;
    }

    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        if (command.equalsIgnoreCase("ADD")) {
            actionAdd();
        } else if (command.equalsIgnoreCase("REMOVE")) {
            actionRemove();
        } else if (command.equalsIgnoreCase("CANCEL")) {
            actionCancel();
        } else if (command.equalsIgnoreCase("ACCEPT")) {
            actionAccept();
        }
    }

    private void actionCancel() {
        isCancelled = true;
        this.dispose();
    }

    private void actionAccept() {
        try {
            updatePluginVisibility();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updatePluginVisibility() throws FileNotFoundException, IOException {
        Properties props = new Properties();
        File propFile;
        for (PluginKey key : activeTreePanel.getPluginKeySet()) {
            propFile = key.getPropFile();
            props.load(new FileInputStream(propFile));
            props.setProperty("active", "true");
            props.store(new FileOutputStream(propFile), null);
        }

        for (PluginKey key : inactiveTreePanel.getPluginKeySet()) {
            propFile = key.getPropFile();
            props.load(new FileInputStream(propFile));
            props.setProperty("active", "false");
            props.store(new FileOutputStream(propFile), null);
        }
        isCancelled = false;
        dispose();
    }

    private void actionAdd() {
        JTree inactiveTree = inactiveTreePanel.getTree();
        TreePath[] paths = inactiveTree.getSelectionPaths();
        DefaultMutableTreeNode thisNode;
        DefaultTreeModel inactiveModel = (DefaultTreeModel) inactiveTree.getModel();
        for (int i = 0; i < paths.length; i++) {
            thisNode = (DefaultMutableTreeNode) paths[i].getLastPathComponent();
            if (!thisNode.isLeaf() || thisNode.isRoot()) {
                continue;
            }
            PluginKey thisKey = PluginManager.getInstanceOf().findInactiveKey(thisNode.getUserObject().toString());
            PluginManager.getInstanceOf().setActive(thisKey);
            inactiveModel.removeNodeFromParent(thisNode);
        }
        inactiveTreePanel.setPluginKeySet(PluginManager.getInstanceOf().getInactivePlugins().keySet());
        activeTreePanel.setPluginKeySet(PluginManager.getInstanceOf().getPlugins().keySet());
    }

    private void actionRemove() {
        JTree activeTree = activeTreePanel.getTree();
        TreePath[] paths = activeTree.getSelectionPaths();
        DefaultMutableTreeNode thisNode;
        DefaultTreeModel activeModel = (DefaultTreeModel) activeTree.getModel();
        for (int i = 0; i < paths.length; i++) {
            thisNode = (DefaultMutableTreeNode) paths[i].getLastPathComponent();
            if (!thisNode.isLeaf() || thisNode.isRoot()) {
                continue;
            }
            activeModel.removeNodeFromParent(thisNode);
            PluginKey thisKey = PluginManager.getInstanceOf().findKey(thisNode.toString());
            PluginManager.getInstanceOf().setInactive(thisKey);
        }
        inactiveTreePanel.setPluginKeySet(PluginManager.getInstanceOf().getInactivePlugins().keySet());
        activeTreePanel.setPluginKeySet(PluginManager.getInstanceOf().getPlugins().keySet());
    }

    public boolean isCancelled() {
        return isCancelled;
    }
}
