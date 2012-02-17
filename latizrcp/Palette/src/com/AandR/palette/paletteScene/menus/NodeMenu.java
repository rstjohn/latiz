/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
package com.AandR.palette.paletteScene.menus;

import com.AandR.palette.paletteScene.PaletteScene;
import com.AandR.palette.paletteScene.PluginNode;
import com.AandR.palette.plugin.AbstractPlugin;
import com.AandR.palette.plugin.IParameterPanel;
import com.AandR.palette.plugin.hud.HudInterface;
import java.awt.Dialog;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import org.netbeans.api.visual.action.PopupMenuProvider;
import org.netbeans.api.visual.border.BorderFactory;
import org.netbeans.api.visual.widget.Widget;
import org.openide.DialogDisplayer;
import org.openide.util.ImageUtilities;

/**
 *
 * @author alex
 */
public class NodeMenu implements PopupMenuProvider, ActionListener {

    private static final String ACTION_DELETE_NODE = "ACTION_DELETE_NODE"; // NOI18N
    private static final String ACTION_RESIZE_NODE = "ACTION_RESIZE_NODE"; // NOI18N
    private static final String ACTION_SHOW_HUD = "ACTION_SHOW_HUD"; // NOI18N
    private static final String ACTION_HIDE_PARAMETER_PANEL = "ACTION_HIDE_PARAMETER_PANEL";
    private JCheckBoxMenuItem hiddenItem;
    private JMenuItem hudItem;
    private JPopupMenu menu;
    private PaletteScene scene;
    private Point point;
    private String res = "com/AandR/palette/resources/";
    private Widget node;

    public NodeMenu(PaletteScene scene) {
        this.scene = scene;
        menu = new JPopupMenu("Node Menu");
        menu.add(createMenuItem("Delete Selected Node(s)", new ImageIcon(ImageUtilities.loadImage(res + "delete16.png")), ACTION_DELETE_NODE, this));
        menu.add(createMenuItem("Resize Node", null, ACTION_RESIZE_NODE, this));
        menu.add(new JSeparator());

        hiddenItem = new JCheckBoxMenuItem("Hide Parameter Panel", false);
        hiddenItem.addActionListener(this);
        hiddenItem.setActionCommand("ACTION_HIDE_PARAMETER_PANEL");
        menu.add(hiddenItem);
        //menu.add(new JSeparator());

        hudItem = new JMenuItem("Open HUD Component");
        hudItem.addActionListener(this);
        hudItem.setActionCommand(ACTION_SHOW_HUD);
        menu.add(hudItem);
        menu.add(new JSeparator());

        JMenuItem propertiesMenuItem = new JMenuItem("Properties...");
        propertiesMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                AbstractPlugin plugin = ((PluginNode) NodeMenu.this.scene.findObject(node)).getPlugin();
                Dialog dialog = DialogDisplayer.getDefault().createDialog(new PropertiesDialogDescriptor(plugin));
                dialog.setVisible(true);
            }
        });
        menu.add(propertiesMenuItem);
    }

    private JMenuItem createMenuItem(String label, ImageIcon icon, String actionCommand, ActionListener al) {
        JMenuItem item = new JMenuItem(label, icon);
        item.setActionCommand(actionCommand);
        item.addActionListener(al);
        return item;
    }

    public JPopupMenu getPopupMenu(Widget widget, Point point) {
        this.point = point;
        this.node = widget;
        AbstractPlugin plugin = ((PluginNode) scene.findObject(node)).getPlugin();
        hudItem.setEnabled(plugin instanceof HudInterface);

        // The follwing if check might belong in another class, but is here for convenience.
        if(plugin.getHudContainer()!=null && !plugin.getHudContainer().isOpened()) {
            plugin.setHudContainer(null);
        }
        hudItem.setText(plugin.getHudContainer() == null ? "Open HUD Component" : "Close HUD Component");
        hiddenItem.setEnabled(plugin instanceof IParameterPanel);
        hiddenItem.setSelected(plugin.isParameterPanelHidden());
        return menu;
    }

    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        if (command.equals(ACTION_DELETE_NODE)) {
            scene.actionRemoveSelectedNodes();
        } else if (command.equals(ACTION_RESIZE_NODE)) {
            actionResizeNode();
        } else if (command.equals(ACTION_SHOW_HUD)) {
            actionShowHUD();
        } else if (command.equals(ACTION_HIDE_PARAMETER_PANEL)) {
            actionHideParameterPanel();
        }
    }

    private void actionHideParameterPanel() {
        AbstractPlugin plugin = ((PluginNode) scene.findObject(node)).getPlugin();
        plugin.setParameterPanelHidden(hiddenItem.isSelected());
    }

    private void actionShowHUD() {
        AbstractPlugin plugin = ((PluginNode) scene.findObject(node)).getPlugin();
        if(!(plugin instanceof HudInterface)) return;
        plugin.updateHudContainerState();
//        plugin.setHudContainerVisible(hudItem.isSelected());
    }

//
//    private void actionRemoveSelectedNodes() {
//        ArrayList<AbstractPlugin> pluginsRemoved = new ArrayList<AbstractPlugin>();
//        Set objects = scene.getSelectedObjects();
//        removeTopComponents(objects);
//        for(Object o : new ArrayList(objects)) {
//            if(!scene.isNode(o)) continue;
//            scene.removeNodeWithEdges(o);
//            pluginsRemoved.add(((PluginNode)o).getPlugin());
//        }
//        scene.validate();
//        notifyPluginsRemoved(pluginsRemoved);
//    }
//
//    private void notifyPluginsRemoved(ArrayList<AbstractPlugin> pluginsRemoved) {
//        Lookup.Result<IPluginsRemoved> pris = LatizLookup.getDefault().lookupResult(IPluginsRemoved.class);
//        for(IPluginsRemoved pri : pris.allInstances()) {
//            pri.removePlugins(scene, pluginsRemoved);
//        }
//    }
//
    private void actionResizeNode() {
        node.setBorder(BorderFactory.createResizeBorder(5));
        scene.validate();
    }
}
