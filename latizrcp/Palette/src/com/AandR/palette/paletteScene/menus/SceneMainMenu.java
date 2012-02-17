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

import com.AandR.palette.paletteScene.AnnotationWidget;
import com.AandR.palette.paletteScene.PaletteScene;
import java.awt.Point;
import java.awt.event.ActionEvent;
import org.netbeans.api.visual.action.PopupMenuProvider;
import org.netbeans.api.visual.widget.Widget;

import java.awt.event.ActionListener;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.ImageUtilities;


/**
 *
 * @author alex
 */
public class SceneMainMenu implements PopupMenuProvider {
    public static final String ACTION_CONNECT_ALL = "ACTION_CONNECT_ALL";
    public static final String ACTION_DISCONNECT_ALL = "ACTION_DISCONNECT_ALL";
    public static final String ACTION_CLEAR_PALETTE = "ACTION_CLEAR_PALETTE";
    public static final String ACTION_SAVE_SCENE = "ACTION_SAVE_SCENE";
    public static final String ACTION_SAVE_AS_SCENE = "ACTION_SAVE_SCENE_AS";
    public static final String ACTION_LOAD_SCENE = "ACTION_LOAD_SCENE";
    public static final String ACTION_SHOW_BIRD_VIEW = "ACTION_SHOW_BIRD_VIEW";
    public static final String ACTION_ROUTER_POLICY = "ACTION_ROUTER_POLICY";
    public static final String ACTION_CREATE_ANNOTATION = "ACTION_CREATE_ANNOTATION";

    public static final int ROUTER_POLICY_FREE = 0;
    public static final int ROUTER_POLICY_DIRECT = 1;
    public static final int ROUTER_POLICY_ORTHOGONAL = 2;

    private PaletteScene scene;

    private JPopupMenu popupMenu;

    private Point point;

    private String res = "com/AandR/palette/resources/";

    public SceneMainMenu(PaletteScene scene, ActionListener al) {
        this.scene = scene;
        popupMenu = new JPopupMenu("Scene Menu");
        popupMenu.add(createMenuItem("Connect All", new ImageIcon(ImageUtilities.loadImage(res + "connect16.png")), ACTION_CONNECT_ALL, al));
        popupMenu.add(createMenuItem("Disconnect All", new ImageIcon(ImageUtilities.loadImage(res + "disconnect16.png")), ACTION_DISCONNECT_ALL, al));
        popupMenu.add(new JSeparator());
        popupMenu.add(createRouterMenu(al));
        popupMenu.add(new JSeparator());
        popupMenu.add(createMenuItem("Clear Palette", new ImageIcon(ImageUtilities.loadImage(res + "delete16.png")), ACTION_CLEAR_PALETTE, al));
        //popupMenu.add(createMenuItem("Add New Node", null, ADD_NEW_NODE_ACTION, al));
        popupMenu.add(new JSeparator());
        popupMenu.add(createMenuItem("Load scene...", new ImageIcon(ImageUtilities.loadImage(res + "fileopen.png")), ACTION_LOAD_SCENE, al));
        popupMenu.add(createMenuItem("Save", new ImageIcon(ImageUtilities.loadImage(res + "filesave.png")), ACTION_SAVE_SCENE, al));
        popupMenu.add(createMenuItem("Save As...", new ImageIcon(ImageUtilities.loadImage(res + "filesaveas.png")), ACTION_SAVE_AS_SCENE, al));
        popupMenu.add(new JSeparator());

        JMenuItem overlayItem = createMenuItem("Annotate...", null, ACTION_CREATE_ANNOTATION, new MenuListener());
        //overlayItem.setEnabled(false);
        popupMenu.add(overlayItem);

        JMenuItem birdViewItem = createMenuItem("Show Bird View", null, ACTION_SHOW_BIRD_VIEW, al);
        birdViewItem.setEnabled(false);
        popupMenu.add(birdViewItem);
    }

    private JMenu createRouterMenu(ActionListener al) {
        ButtonGroup bg = new ButtonGroup();
        JCheckBoxMenuItem menuFree = createCheckboxMenuItem("Free", ACTION_ROUTER_POLICY+"_FREE", al);
        menuFree.setSelected(true);
        JCheckBoxMenuItem menuDirect = createCheckboxMenuItem("Direct", ACTION_ROUTER_POLICY+"_DIRECT", al);
        JCheckBoxMenuItem menuOrtho = createCheckboxMenuItem("Orthogonal", ACTION_ROUTER_POLICY+"_ORTHO", al);

        bg.add(menuFree);
        bg.add(menuDirect);
        bg.add(menuOrtho);

        JMenu menu = new JMenu("Connectors");
        menu.add(menuFree);
        menu.add(menuDirect);
        menu.add(menuOrtho);
        return menu;
    }

    private JCheckBoxMenuItem createCheckboxMenuItem(String label, String actionCommand, ActionListener al) {
        JCheckBoxMenuItem item = new JCheckBoxMenuItem(label);
        item.addActionListener(al);
        item.setActionCommand(actionCommand);
        return item;
    }

    private JMenuItem createMenuItem(String label, ImageIcon icon, String actionCommand, ActionListener al) {
        JMenuItem item = new JMenuItem(label, icon);
        item.addActionListener(al);
        item.setActionCommand(actionCommand);
        return item;
    }

    public JPopupMenu getPopupMenu(Widget widget, Point point) {
        this.point = point;
        return popupMenu;
    }

    /**
     * 
     */
    private class MenuListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            actionCreateAnnotation();
        }
        
        private void actionCreateAnnotation() {
            NotifyDescriptor.InputLine nd = new NotifyDescriptor.InputLine("Enter Text:", "Annotation Text");
            DialogDisplayer.getDefault().notify(nd);
            String text = nd.getInputText();
            AnnotationWidget aw = new AnnotationWidget(scene, text);
            aw.setPreferredLocation(point);
            scene.addChild(aw);
            scene.validate();
        }
    }
}
