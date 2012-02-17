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
package com.AandR.palette.paletteScene;

import com.AandR.latiz.core.lookup.LatizLookup;
import com.AandR.palette.cookies.PluginSelectionCookie;
import com.AandR.palette.plugin.IPluginConnection;
import com.AandR.palette.plugin.IPluginsAdded;
import com.AandR.palette.plugin.AbstractPlugin;
import com.AandR.palette.plugin.IParameterPanel;
import com.AandR.palette.plugin.PluginUtilities;
import com.AandR.palette.plugin.hud.HudContainer;
import com.AandR.palette.plugin.hud.HudInterface;
import java.awt.Color;
import java.awt.Font;
import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.netbeans.api.visual.widget.Widget;

import org.jdom.Element;
import org.netbeans.api.visual.router.RouterFactory;
import org.netbeans.api.visual.widget.ConnectionWidget;
import org.netbeans.api.visual.widget.LabelWidget.Orientation;
import org.openide.util.Lookup;

/**
 * @author David Kaspar
 */
public class SceneSerializer {

    public static final String ATT_PLUGIN_NAME = "name";
    public static final String ATT_PLUGIN_ID = "id";
    public static final String ATT_PLUGIN_X = "x";
    public static final String ATT_PLUGIN_Y = "y";
    public static final String ATT_PLUGIN_HIDDEN = "hidden";
    public static final String ATT_HUD_VISIBLE = "hudVisible";
    public static final String ATT_EDGE_ID = "id";
    public static final String ATT_EDGE_SOURCE = "src";
    public static final String ATT_EDGE_TARGET = "targ";
    public static final String ATT_EDGE_ROUTER = "router";
    public static final String ATT_VERT_X = "x";
    public static final String ATT_VERT_Y = "y";

    public static void serialize(PaletteScene scene, Element rootElement) {
        Element pluginsElement = new Element("plugins");
        Element thisPluginElement;
        Point loc;
        AbstractPlugin plugin;
        for (PluginNode node : scene.getNodes()) {
            Widget widget = scene.findWidget(node);
            loc = widget.getPreferredLocation();

            plugin = node.getPlugin();
            thisPluginElement = new Element("plugin");
            thisPluginElement.setAttribute(ATT_PLUGIN_ID, plugin.getPluginKey().getUniqueID());
            thisPluginElement.setAttribute(ATT_PLUGIN_NAME, node.getName());
            thisPluginElement.setAttribute(ATT_PLUGIN_X, Integer.toString(loc.x));
            thisPluginElement.setAttribute(ATT_PLUGIN_Y, Integer.toString(loc.y));
            thisPluginElement.setAttribute(ATT_PLUGIN_HIDDEN, String.valueOf(plugin.isParameterPanelHidden()));
            boolean isHudOpen = plugin.getHudContainer()!=null && plugin.getHudContainer().isOpened();
            thisPluginElement.setAttribute(ATT_HUD_VISIBLE, String.valueOf(isHudOpen));

            if(plugin instanceof IParameterPanel) {
                Element parameterPanelElement = ((IParameterPanel)plugin).createWorkspaceParameters();
                if (parameterPanelElement != null) {
                    thisPluginElement.addContent(new Element("parameters").addContent(parameterPanelElement));
                }
            }
            pluginsElement.addContent(thisPluginElement);
        }
        rootElement.addContent(pluginsElement);

        Element connectionsElement = new Element("connections");
        Element thisConnectionElement;
        for (ConnectorEdge edge : scene.getEdges()) {
            thisConnectionElement = new Element("connection");
            thisConnectionElement.setAttribute(ATT_EDGE_ID, edge.getName());

            PluginNode sourceNode = scene.getEdgeSource(edge);
            if (sourceNode != null) {
                thisConnectionElement.setAttribute(ATT_EDGE_SOURCE, sourceNode.getName());
            }

            PluginNode targetNode = scene.getEdgeTarget(edge);
            if (targetNode != null) {
                thisConnectionElement.setAttribute(ATT_EDGE_TARGET, targetNode.getName());
            }

            ConnectionWidget cw = (ConnectionWidget) scene.findWidget(edge);
            thisConnectionElement.setAttribute(ATT_EDGE_ROUTER, cw.getRouter().getClass().getSimpleName());
            List<Point> pts = cw.getControlPoints();
            if (pts != null && !pts.isEmpty()) {
                Element vertsElement = new Element("verts");
                Element thisVertexElement;
                for (Point pt : cw.getControlPoints()) {
                    thisVertexElement = new Element("vert");
                    thisVertexElement.setAttribute(ATT_VERT_X, Integer.toString(pt.x));
                    thisVertexElement.setAttribute(ATT_VERT_Y, Integer.toString(pt.y));
                    vertsElement.addContent(thisVertexElement);
                }
                thisConnectionElement.addContent(vertsElement);
            }
            connectionsElement.addContent(thisConnectionElement);
        }
        rootElement.addContent(connectionsElement);

        // Save I/O connections
        Element ioConnectionsElement = new Element("ioConnections");
        for (String ioc : scene.getDefaultPaletteModel().getConnections()) {
            ioConnectionsElement.addContent(new Element("io").setAttribute("name", ioc));
        }
        rootElement.addContent(ioConnectionsElement);

        Element annotationsElement = new Element("annotations");
        Element thisAnnotation;
        AnnotationWidget aw;
        Font font;
        for(Widget w : scene.getChildren()) {
            if(!(w instanceof AnnotationWidget)) continue;
            aw = (AnnotationWidget)w;

            thisAnnotation = new Element("entry");
            thisAnnotation.setAttribute("text", aw.getLabel());

            font = aw.getFont();
            thisAnnotation.setAttribute("font", String.valueOf(font.getName()));
            thisAnnotation.setAttribute("fontSize", String.valueOf(font.getSize()));
            thisAnnotation.setAttribute("fontStyle", String.valueOf(font.getStyle()));

            loc = aw.getPreferredLocation();
            thisAnnotation.setAttribute("xLoc", String.valueOf(loc.x));
            thisAnnotation.setAttribute("yLoc", String.valueOf(loc.y));

            thisAnnotation.setAttribute("fore", "#"+Integer.toHexString(aw.getForeground().getRGB()).substring(1));
            thisAnnotation.setAttribute("back", "#"+Integer.toHexString(((Color) aw.getBackground()).getRGB()).substring(1));
            thisAnnotation.setAttribute("orient", aw.getOrientation().name());
            annotationsElement.addContent(thisAnnotation);
        }
        rootElement.addContent(annotationsElement);
    }

    public static Element deserialize(PaletteScene scene, Element rootElement) {
        HashMap<String, PluginNode> registeredNodeMap = new HashMap<String, PluginNode>();
        Element pluginsElement = rootElement.getChild("plugins");
        List plugins = pluginsElement.getChildren("plugin");

        ArrayList<AbstractPlugin> addedPlugins = new ArrayList<AbstractPlugin>();
        PluginUtilities pm = PluginUtilities.getDefault();
        String pluginID, pluginName, isHudVisible, isParameterPanelHidden;
        Element pluginElement;
        for (Object plugin : plugins) {
            pluginElement = (Element) plugin;
            pluginID = pluginElement.getAttributeValue(ATT_PLUGIN_ID);
            pluginName = pluginElement.getAttributeValue(ATT_PLUGIN_NAME);

            isHudVisible = pluginElement.getAttributeValue(ATT_HUD_VISIBLE);
            isHudVisible = isHudVisible==null ? "false" : isHudVisible;

            isParameterPanelHidden = pluginElement.getAttributeValue(ATT_PLUGIN_HIDDEN);
            isParameterPanelHidden = isParameterPanelHidden==null ? "false" : isParameterPanelHidden;

            AbstractPlugin p = pm.instantiate(pluginID, pluginName, scene.getDefaultPaletteModel());
            p.setParameterPanelHidden(Boolean.parseBoolean(isParameterPanelHidden));

            if (p instanceof IParameterPanel) {
                ((IParameterPanel) p).loadSavedWorkspaceParameters(pluginElement.getChild("parameters"));
            }

            if(p instanceof HudInterface && Boolean.parseBoolean(isHudVisible)) {
                actionShowHUD(p);
            }
            addedPlugins.add(p);

            PluginNode thisNode = new PluginNode(p);
            registeredNodeMap.put(thisNode.getName(), thisNode);
            Widget nodeWidget = scene.addNode(thisNode);
            int x = Integer.parseInt(pluginElement.getAttributeValue(ATT_PLUGIN_X));
            int y = Integer.parseInt(pluginElement.getAttributeValue(ATT_PLUGIN_Y));
            //nodeWidget.setPreferredLocation(new Point(x, y));
            nodeWidget.setPreferredLocation(new Point(x, y));
        }
        scene.revalidate();

        Lookup.Result<IPluginsAdded> pluginsAdded = LatizLookup.getDefault().lookupResult(IPluginsAdded.class);
        for (IPluginsAdded ipa : pluginsAdded.allInstances()) {
            ipa.pluginsAdded(scene, addedPlugins);
        }

        List connections = rootElement.getChild("connections").getChildren("connection");
        Element connectionElement;
        for (Object connection : connections) {
            connectionElement = (Element) connection;
            ConnectorEdge edge = new ConnectorEdge(connectionElement.getAttributeValue(ATT_EDGE_ID));
            PluginNode sourceNode = registeredNodeMap.get(connectionElement.getAttributeValue(ATT_EDGE_SOURCE));
            PluginNode targetNode = registeredNodeMap.get(connectionElement.getAttributeValue(ATT_EDGE_TARGET));

            // Create connection panels.
            Lookup.Result<IPluginConnection> pcis = LatizLookup.getDefault().lookupResult(IPluginConnection.class);
            for (IPluginConnection pci : pcis.allInstances()) {
                pci.connectionMade(scene, sourceNode.getPlugin(), targetNode.getPlugin());
            }

            scene.addEdge(edge);
            scene.setEdgeSource(edge, sourceNode);
            scene.setEdgeTarget(edge, targetNode);

            // Apply vertices
            ArrayList<Point> controlPoints = new ArrayList<Point>();
            List verts = connectionElement.getChild("verts").getChildren();
            Element vertElement;
            for (Object vert : verts) {
                vertElement = (Element) vert;
                int x = Integer.parseInt(vertElement.getAttributeValue(ATT_VERT_X));
                int y = Integer.parseInt(vertElement.getAttributeValue(ATT_VERT_Y));
                controlPoints.add(new Point(x, y));
            }

            ConnectionWidget cw = (ConnectionWidget) scene.findWidget(edge);
            String router = connectionElement.getAttributeValue(ATT_EDGE_ROUTER);
            if (router.equals("FreeRouter")) {
                cw.setRouter(RouterFactory.createFreeRouter());
            } else if (router.equals("DirectRouter")) {
                cw.setRouter(RouterFactory.createDirectRouter());
            } else if (router.equals("OrthogonalSearchRouter")) {
                cw.setRouter(RouterFactory.createOrthogonalSearchRouter(scene.getMainLayer()));
            }
            cw.setControlPoints(controlPoints, true);
        }

        // Deserialize I/O connections
        Element ioConnectionsElement = rootElement.getChild("ioConnections");
        if (ioConnectionsElement == null) {
            return rootElement;
        }
        List<String> ioConnections = scene.getDefaultPaletteModel().getConnections();
        ioConnections.clear();
        for (Object o : ioConnectionsElement.getChildren()) {
            ioConnections.add(((Element) o).getAttributeValue("name"));
        }

        // Annotations
        Element annotationsElement = rootElement.getChild("annotations");
        Element annotationElement;
        AnnotationWidget aw;
        if (annotationsElement != null) {
            for (Object o : annotationsElement.getChildren()) {
                annotationElement = (Element) o;
                aw = new AnnotationWidget(scene, annotationElement.getAttributeValue("text"));

                String fontName = annotationElement.getAttributeValue("font");
                int fontSize = Integer.parseInt(annotationElement.getAttributeValue("fontSize"));
                int fontStyle = Integer.parseInt(annotationElement.getAttributeValue("fontStyle"));
                Font font = new Font(fontName, fontStyle, fontSize);
                aw.setFont(font);

                int xLoc = Integer.parseInt(annotationElement.getAttributeValue("xLoc"));
                int yLoc = Integer.parseInt(annotationElement.getAttributeValue("yLoc"));
                aw.setPreferredLocation(new Point(xLoc, yLoc));
                aw.setForeground(Color.decode(annotationElement.getAttributeValue("fore")));
                aw.setBackground(Color.decode(annotationElement.getAttributeValue("back")));
                aw.setOrientation(Orientation.valueOf(annotationElement.getAttributeValue("orient")));
                scene.addChild(aw);
            }
        }
        scene.validate();

        // Removed all Plugin Selection Cookies.
        LatizLookup.getDefault().removeAllFromLookup(PluginSelectionCookie.class);

        return rootElement;
    }

    private static void actionShowHUD(AbstractPlugin plugin) {
        if(!(plugin instanceof HudInterface)) return;

        HudContainer hudContainer = plugin.getHudContainer();
        if (hudContainer == null) {
            hudContainer = ((HudInterface) plugin).createHudComponent();
            if(hudContainer==null) return;
            
            hudContainer.setName("hudEditor::" + plugin.getPaletteModelImpl().getName() + "::" + plugin.getName());
            hudContainer.setDisplayName(plugin.getPaletteModelImpl().getDisplayName()+"::"+plugin.getName());
            hudContainer.setToolTipText("hudEditor::" + plugin.getPaletteModelImpl().getName() + "::" + plugin.getName());
            plugin.setHudContainer(hudContainer);
        }

        if (!hudContainer.isOpened()) {
            hudContainer.open();
        }
    }
}
