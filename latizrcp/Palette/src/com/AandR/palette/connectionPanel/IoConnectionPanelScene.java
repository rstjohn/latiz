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
package com.AandR.palette.connectionPanel;

import com.AandR.latizOptions.connectionPanel.ConnectionPanelOptionsController;
import com.AandR.palette.paletteScene.PaletteScene;
import com.AandR.palette.plugin.AbstractPlugin;
import com.AandR.palette.plugin.data.Input;
import com.AandR.palette.plugin.data.Output;
import java.awt.Color;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.prefs.PreferenceChangeEvent;
import java.util.prefs.PreferenceChangeListener;
import java.util.prefs.Preferences;
import org.netbeans.api.visual.action.ActionFactory;
import org.netbeans.api.visual.action.WidgetAction;
import org.netbeans.api.visual.anchor.AnchorFactory;
import org.netbeans.api.visual.anchor.AnchorShape;
import org.netbeans.api.visual.anchor.PointShape;
import org.netbeans.api.visual.graph.GraphScene;
import org.netbeans.api.visual.layout.Layout;
import org.netbeans.api.visual.layout.LayoutFactory;
import org.netbeans.api.visual.widget.ConnectionWidget;
import org.netbeans.api.visual.widget.LayerWidget;
import org.netbeans.api.visual.widget.Widget;
import org.openide.util.NbPreferences;

/**
 *
 * @author rstjohn
 */
public class IoConnectionPanelScene extends GraphScene<TransferableDataNode, TransferableDataEdge> implements PreferenceChangeListener {

    private LayerWidget connectionLayer,  interactionLayer;
    private PaletteScene paletteScene;
    private Widget targetWidgets,  sourceWidgets,  noTargetWidgets,  noSourceWidgets;

    public IoConnectionPanelScene() {
        initialize();
        setOpaque(false);
        Preferences pref = NbPreferences.forModule(ConnectionPanelOptionsController.class);
        pref.addPreferenceChangeListener(this);
        createContentPane();
    }

    public IoConnectionPanelScene(AbstractPlugin source, AbstractPlugin target) {
        this();
        createConnectionPanelScene(source, target);
    }

    private void createContentPane() {
        setMaximumBounds(new Rectangle(0, 0, Integer.MAX_VALUE, Integer.MAX_VALUE));
        LayerWidget mainLayer = new LayerWidget(this);
        mainLayer.setLayout(LayoutFactory.createHorizontalFlowLayout(LayoutFactory.SerialAlignment.LEFT_TOP, 50));

        addChild(mainLayer);
        addChild(connectionLayer);
        addChild(interactionLayer);

        mainLayer.addChild(sourceWidgets);
        mainLayer.addChild(targetWidgets);

    }

    private void initialize() {
        connectionLayer = new LayerWidget(this);
        interactionLayer = new LayerWidget(this);

        targetWidgets = new Widget(this);
        targetWidgets.setLayout(LayoutFactory.createVerticalFlowLayout(LayoutFactory.SerialAlignment.LEFT_TOP, 5));
        sourceWidgets = new Widget(this);
        sourceWidgets.setLayout(LayoutFactory.createVerticalFlowLayout(LayoutFactory.SerialAlignment.LEFT_TOP, 5));

        noTargetWidgets = new NoConnectionWidget(this, "No inputs", "This plugin does not have any exposed inputs");
        noSourceWidgets = new NoConnectionWidget(this, "No outputs", "This plugin does not have any exposed outputs");
    }

    public void setConnectionsFor(AbstractPlugin sourcePlugin, AbstractPlugin targetPlugin) {
        setNoConnections();

        Map<String, Output> outputsDataMap = sourcePlugin.getOutputDataMap();
        if (outputsDataMap.size() < 1) {
//            sourceWidgets.addChild(noSourceWidgets);
            return;
        } else {
            String sourceName = sourcePlugin.getName();
            for (String alias : outputsDataMap.keySet()) {
                addNode(new TransferableDataNode(sourceName + "::" + alias, outputsDataMap.get(alias), TransferableDataNode.OUTPUT));
            }
        }

        Map<String, Input> inputsDataMap = targetPlugin.getInputDataMap();
        if (inputsDataMap.size() < 1) {
//            targetWidgets.addChild(noTargetWidgets);
        } else {
            String targetName = targetPlugin.getName();
            for (String alias : inputsDataMap.keySet()) {
                addNode(new TransferableDataNode(targetName + "::" + alias, inputsDataMap.get(alias), TransferableDataNode.INPUT));
            }
        }
        validate();
    }

    void clearScene() {
        for (Object o : new ArrayList<Object>(getObjects())) {
            if (!isNode(o)) {
                continue;
            }
            removeNodeWithEdges((TransferableDataNode)o);
        }
        validate();

    }

    public void setNoConnections() {
        clearScene();
    }

    @Override
    protected Widget attachNodeWidget(TransferableDataNode arg0) {
        WidgetAction connectAction = ActionFactory.createConnectAction(interactionLayer, new ConnectProviderImpl(this));
        Widget thisWidget;
        String label = arg0.getAlias();
        String className = arg0.getTransferableData().getValueClass().getSimpleName();
        if (arg0.getType() == TransferableDataNode.OUTPUT) {
            thisWidget = new OutputConnectorWidget(this, label, className, "Tooltip");
            ((OutputConnectorWidget) thisWidget).getArrowWidget().getActions().addAction(connectAction);
            sourceWidgets.addChild(thisWidget);
        } else {
            thisWidget = new InputConnectorWidget(this, label, className, "Tooltip");
            targetWidgets.addChild(thisWidget);
        }
        return thisWidget;
    }

    @Override
    protected void notifyNodeAdded(TransferableDataNode node, Widget widget) {
        if (!(widget instanceof InputConnectorWidget)) {
            return;
        }
        InputConnectorWidget thisInputConnectorWidget = (InputConnectorWidget) widget;
//        updateInputConnectors(node, thisInputConnectorWidget);
    }

//    private void updateInputConnectors(TransferableDataNode node, InputConnectorWidget widget) {
//        if(paletteScene == null) return;
//    }

    @Override
    protected void notifyEdgeAdded(TransferableDataEdge edge, Widget w) {
        TransferableDataNode targetNode = edge.getTargetNode();
        Widget inputWidget = findWidget(targetNode);
        if (!(inputWidget instanceof InputConnectorWidget)) {
            return;
        }
        InputConnectorWidget inputConnectorWidget = (InputConnectorWidget) inputWidget;
        inputConnectorWidget.setHasConnection(true);
//        updateInputConnectors(targetNode, inputConnectorWidget);
    }

    protected Widget attachEdgeWidget(TransferableDataEdge edge) {
        ConnectionWidget connection = new ConnectionWidget(this);
        WidgetAction reconnectAction = ActionFactory.createReconnectAction(new ReconnectProviderImpl(this));
        connection.getActions().addAction(reconnectAction);
        connection.setTargetAnchorShape(AnchorShape.TRIANGLE_FILLED);
        connection.setEndPointShape(PointShape.SQUARE_FILLED_BIG);
        connectionLayer.addChild(connection);

        List<String> ioConnections = paletteScene.getDefaultPaletteModel().getConnections();
        int index = ioConnections.indexOf(edge.toString());
        if (index == -1) {
            ioConnections.add(edge.toString());
        }
        return connection;
    }

    protected void attachEdgeSourceAnchor(TransferableDataEdge edge, TransferableDataNode oldSourceNode, TransferableDataNode sourceNode) {
        ConnectionWidget widget = (ConnectionWidget) findWidget(edge);
        Widget sourceNodeWidget = findWidget(sourceNode);
        widget.setSourceAnchor(sourceNodeWidget != null ? AnchorFactory.createDirectionalAnchor(sourceNodeWidget, AnchorFactory.DirectionalAnchorKind.HORIZONTAL) : null);
    }

    protected void attachEdgeTargetAnchor(TransferableDataEdge edge, TransferableDataNode oldTargetNode, TransferableDataNode targetNode) {
        ConnectionWidget widget = (ConnectionWidget) findWidget(edge);
        Widget targetNodeWidget = findWidget(targetNode);
        widget.setTargetAnchor(targetNodeWidget != null ? AnchorFactory.createDirectionalAnchor(targetNodeWidget, AnchorFactory.DirectionalAnchorKind.HORIZONTAL) : null);
    }

    public void createConnectionPanelScene(AbstractPlugin source, AbstractPlugin target) {
        targetWidgets.removeChildren();
        Map<String, Input> inputsDataMap = target.getInputDataMap();
        if (inputsDataMap.size() < 1) {
            targetWidgets.addChild(noTargetWidgets);
        } else {
            String targetName = target.getName();
            for (String alias : inputsDataMap.keySet()) {
                addNode(new TransferableDataNode(targetName + "::" + alias, inputsDataMap.get(alias), TransferableDataNode.INPUT));
            }
        }

        sourceWidgets.removeChildren();
        Map<String, Output> outputsDataMap = source.getOutputDataMap();
        if (outputsDataMap.size() < 1) {
            sourceWidgets.addChild(noSourceWidgets);
            return;
        } else {
            String sourceName = source.getName();
            for (String alias : outputsDataMap.keySet()) {
                addNode(new TransferableDataNode(sourceName + "::" + alias, outputsDataMap.get(alias), TransferableDataNode.OUTPUT));
            }
        }
        validate();
    }

    public void setPaletteScene(PaletteScene paletteScene) {
        this.paletteScene = paletteScene;
    }

    public PaletteScene getPaletteScene() {
        return paletteScene;
    }

    public void makeConnection(String connectionString) {
        String[] pluginSplit = connectionString.split(">");

        TransferableDataNode srcNode = null;
        TransferableDataNode trgNode = null;
        Collection<TransferableDataNode> ns = getNodes();
        for (TransferableDataNode n : ns) {
            if (n.getName().equals(pluginSplit[1]) && n.getType() == TransferableDataNode.INPUT) {
                trgNode = n;
                break;
            }
        }
        if (trgNode == null) {
            return;
        }

        for (TransferableDataNode n : ns) {
            if (n.getName().equals(pluginSplit[0])) {
                srcNode = n;
                break;
            }
        }

        if (srcNode == null) {
            ((InputConnectorWidget) findWidget(trgNode)).setHasConnection(true);
        } else {
            TransferableDataEdge edge = new TransferableDataEdge(srcNode, trgNode);
            addEdge(edge);
            setEdgeSource(edge, srcNode);
            setEdgeTarget(edge, trgNode);
        }
        validate();
    }

    public void preferenceChange(PreferenceChangeEvent evt) {
        String key = evt.getKey();
        Color newColor = Color.decode(evt.getNewValue());
        if (key.equals("targetBackground")) {
            for (Widget w : targetWidgets.getChildren()) {
                ((InputConnectorWidget) w).setBackgroundColor(newColor);
            }
            validate();
        } else if (key.equals("targetForeground")) {
            for (Widget w : targetWidgets.getChildren()) {
                ((InputConnectorWidget) w).setForegroundColor(newColor);
            }
            validate();
        } else if (key.equals("sourceBackground")) {
            for (Widget w : sourceWidgets.getChildren()) {
                ((OutputConnectorWidget) w).setBackgroundColor(newColor);
            }
            validate();
        } else if (key.equals("sourceForeground")) {
            for (Widget w : sourceWidgets.getChildren()) {
                ((OutputConnectorWidget) w).setForegroundColor(newColor);
            }
            validate();
        }
    }

    private class IOLayout implements Layout {

        public void layout(Widget arg0) {
        }

        public boolean requiresJustification(Widget arg0) {
            return false;
        }

        public void justify(Widget arg0) {
        }
    }
}
