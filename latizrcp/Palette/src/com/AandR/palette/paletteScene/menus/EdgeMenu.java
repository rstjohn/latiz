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

import com.AandR.latiz.core.lookup.LatizLookup;
import com.AandR.palette.paletteScene.ConnectorEdge;
import com.AandR.palette.paletteScene.PaletteScene;
import com.AandR.palette.plugin.IPluginConnection;
import com.AandR.palette.paletteScene.PluginNode;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import org.netbeans.api.visual.action.PopupMenuProvider;
import org.netbeans.api.visual.widget.ConnectionWidget;
import org.netbeans.api.visual.widget.Widget;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.netbeans.api.visual.router.Router;
import org.netbeans.api.visual.router.RouterFactory;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;


/**
 *
 * @author alex
 */
public class EdgeMenu implements PopupMenuProvider, ActionListener {
    private static final String ADD_REMOVE_CP_ACTION = "addRemoveCPAction"; // NOI18N
    private static final String DELETE_ALL_CP_ACTION = "deleteAllCPAction"; // NOI18N
    private static final String DELETE_TRANSITION = "deleteTransition"; // NOI18N
    private static final String ACTION_ROUTER_POLICY = "ACTION_ROUTER_POLICY";

    private static final int ROUTER_POLICY_FREE = 0;
    private static final int ROUTER_POLICY_DIRECT = 1;
    private static final int ROUTER_POLICY_ORTHOGONAL = 2;

    private ConnectionWidget edge;

    private PaletteScene scene;
    
    private JCheckBoxMenuItem menuFree, menuDirect, menuOrtho;

    private JPopupMenu menu;

    private Point point;


    public EdgeMenu(PaletteScene scene) {
        this.scene = scene;
        ImageIcon cancelIcon = new ImageIcon(ImageUtilities.loadImage("com/AandR/palette/resources/cancel.png"));
        menu = new JPopupMenu("Transition Menu");
        menu.add(createMenuItem("Delete Connection", cancelIcon, DELETE_TRANSITION, this));
        menu.addSeparator();
        menu.add(createMenuItem("Add/Delete Control Point", null, ADD_REMOVE_CP_ACTION, this));
        menu.add(createMenuItem("Delete All Control Points", null, DELETE_ALL_CP_ACTION, this));
        menu.addSeparator();
        menu.add(createRouterMenu(this));
    }


    public JPopupMenu getPopupMenu(Widget widget, Point point) {
        if (widget instanceof ConnectionWidget) {
            this.edge = (ConnectionWidget) widget;
            updateRouterMenu();
            this.point = point;
            return menu;
        }
        return null;
    }


    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        if (command.equals(ADD_REMOVE_CP_ACTION)) {
            addRemoveControlPoint(point);
        } else if (command.equals(DELETE_TRANSITION)) {
            actionRemoveTransition();
        } else if (command.equals(DELETE_ALL_CP_ACTION)) {
            actionRemoveAllControlPoints();
        } else if(command.equals(ACTION_ROUTER_POLICY+"_FREE")) {
            actionRouterPolicy(ROUTER_POLICY_FREE);
        } else if(command.equals(ACTION_ROUTER_POLICY+"_DIRECT")) {
            actionRouterPolicy(ROUTER_POLICY_DIRECT);
        } else if(command.equals(ACTION_ROUTER_POLICY+"_ORTHO")) {
            actionRouterPolicy(ROUTER_POLICY_ORTHOGONAL);
        }
    }


    private void actionRemoveTransition() {
        ConnectorEdge thisEdge = (ConnectorEdge) scene.findObject(edge);
        PluginNode sourceNode = scene.getEdgeSource(thisEdge);
        PluginNode targetNode = scene.getEdgeTarget(thisEdge);
        scene.removeEdge(thisEdge);
        Lookup.Result<IPluginConnection> pcis = LatizLookup.getDefault().lookupResult(IPluginConnection.class);
        for(IPluginConnection pci : pcis.allInstances()) {
            pci.connectionRemoved(scene, sourceNode.getPlugin(), targetNode.getPlugin());
        }
    }


    private void updateRouterMenu() {
        menuFree.setSelected(false);
        menuDirect.setSelected(false);
        menuOrtho.setSelected(false);
        String name = edge.getRouter().getClass().getSimpleName();
        if(name.equals("FreeRouter")) {
            menuFree.setSelected(true);
        } else if(name.equals("OrthogonalSearchRouter")) {
            menuOrtho.setSelected(true);
        } else if(name.equals("DirectRouter")) {
            menuDirect.setSelected(true);
        }
    }


    private JMenu createRouterMenu(ActionListener al) {

        ButtonGroup bg = new ButtonGroup();
        
        menuFree = createCheckboxMenuItem("Free", ACTION_ROUTER_POLICY+"_FREE", al);
        menuFree.setSelected(true);
        menuDirect = createCheckboxMenuItem("Direct", ACTION_ROUTER_POLICY+"_DIRECT", al);
        menuOrtho = createCheckboxMenuItem("Orthogonal", ACTION_ROUTER_POLICY+"_ORTHO", al);

        bg.add(menuFree);
        bg.add(menuDirect);
        bg.add(menuOrtho);

        JMenu routerMenu = new JMenu("Connectors");
        routerMenu.add(menuFree);
        routerMenu.add(menuDirect);
        routerMenu.add(menuOrtho);
        return routerMenu;
    }


    private void actionRemoveAllControlPoints() {
        edge.setRouter(RouterFactory.createDirectRouter());
    }


    private void actionRouterPolicy(int policy) {
        
        Router router;
        switch (policy) {
            case ROUTER_POLICY_FREE:
                router = RouterFactory.createFreeRouter();
                break;
            case ROUTER_POLICY_DIRECT:
                router = RouterFactory.createDirectRouter();
                break;
            case ROUTER_POLICY_ORTHOGONAL:
                router = RouterFactory.createOrthogonalSearchRouter(scene.getMainLayer());
                break;
            default:
                router = RouterFactory.createFreeRouter();
        }
        edge.setRouter(router);
    }


    private void addRemoveControlPoint(Point localLocation) {
        ArrayList<Point> list = new ArrayList<Point>(edge.getControlPoints());
        double createSensitivity = 1.00, deleteSensitivity = 5.00;
        if (!removeControlPoint(localLocation, list, deleteSensitivity)) {
            Point exPoint = null;
            int index = 0;
            for (Point elem : list) {
                if (exPoint != null) {
                    Line2D l2d = new Line2D.Double(exPoint, elem);
                    if (l2d.ptLineDist(localLocation) < createSensitivity) {
                        list.add(index, localLocation);
                        break;
                    }
                }
                exPoint = elem;
                index++;
            }
        }
        edge.setControlPoints(list, false);
    }


    private boolean removeControlPoint(Point point, ArrayList<Point> list, double deleteSensitivity) {
        for (Point elem : list) {
            if (elem.distance(point) < deleteSensitivity) {
                list.remove(elem);
                return true;
            }
        }
        return false;
    }


    private JMenuItem createMenuItem(String label, ImageIcon icon, String actionCommand, ActionListener al) {
        JMenuItem item = new JMenuItem(label, icon);
        item.setActionCommand(actionCommand);
        item.addActionListener(al);
        return item;
    }


    private JCheckBoxMenuItem createCheckboxMenuItem(String label, String actionCommand, ActionListener al) {
        JCheckBoxMenuItem item = new JCheckBoxMenuItem(label);
        item.addActionListener(al);
        item.setActionCommand(actionCommand);
        return item;
    }
}
