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

import com.AandR.palette.connectionPanel.InputConnectorWidget.FemaleArrow;
import com.AandR.palette.paletteScene.PaletteScene;
import com.AandR.palette.plugin.data.Input;
import com.AandR.palette.plugin.data.Output;
import java.awt.Point;
import java.util.List;
import org.netbeans.api.visual.action.ConnectorState;
import org.netbeans.api.visual.action.ReconnectProvider;
import org.netbeans.api.visual.widget.ConnectionWidget;
import org.netbeans.api.visual.widget.Scene;
import org.netbeans.api.visual.widget.Widget;
import org.openide.windows.IOProvider;

/**
 *
 * @author rstjohn
 */
public class ReconnectProviderImpl implements ReconnectProvider {

    private IoConnectionPanelScene scene;
//    private TransferableDataEdge edge;
    private TransferableDataNode source,  target;
    private Widget previousConnectionWidget;

    public ReconnectProviderImpl(IoConnectionPanelScene graphScene) {
        this.scene = graphScene;
    }

    public boolean isSourceWidget(Widget sourcWidget) {
        TransferableDataNode object = (TransferableDataNode) scene.findObject(sourcWidget);
        source = scene.isNode(object) ? object : null;
        return (source != null && (sourcWidget instanceof OutputConnectorWidget.MaleArrow));
    }

    public ConnectorState isTargetWidget(Widget sourceWidget, Widget targetWidget) {
        resetInputConnectors();

        if (!(targetWidget instanceof InputConnectorWidget)) {
            return ConnectorState.REJECT;
        }

        InputConnectorWidget arrowWidget = (InputConnectorWidget) targetWidget;

        Object targetObject = scene.findObject(targetWidget);
        target = scene.isNode(targetObject) ? (TransferableDataNode) targetObject : null;

        if (target == null || !(targetObject instanceof TransferableDataNode)) {
            return ConnectorState.REJECT_AND_STOP;
        }

        // Is the target data class assignable from source data class?
        Object sourceObject = scene.findObject(sourceWidget);
        if (!(sourceObject instanceof TransferableDataNode) || !(targetObject instanceof TransferableDataNode)) {
            return ConnectorState.REJECT_AND_STOP;
        }

        TransferableDataNode targetNode = (TransferableDataNode) targetObject;
            IOProvider.getDefault().getIO("Output", false).getOut().println(targetNode.toString() );
        TransferableDataNode sourceNode = (TransferableDataNode) sourceObject;
        if (targetNode.getTransferableData() instanceof Output) {
            return ConnectorState.REJECT_AND_STOP;
        }

        Input targetData = (Input) targetNode.getTransferableData();
        Output sourceData = (Output) sourceNode.getTransferableData();
        boolean success = targetData.acceptConnectionToOutput(sourceData);
        if (!success) {
            return ConnectorState.REJECT_AND_STOP;
        }

        // Does the target input have a connection?
        PaletteScene paletteScene = scene.getPaletteScene();
        String targetNodeString = targetNode.toString();
        for(String s : paletteScene.getDefaultPaletteModel().getConnections()) {
            if(s.split(">")[1].equals(targetNodeString)) {
                return ConnectorState.REJECT_AND_STOP;
            }
        }
        arrowWidget.setValidConnection(InputConnectorWidget.STATE_VALID_CONNECTION);
        return ConnectorState.ACCEPT;
    }

    public void resetInputConnectors() {
        Widget w;
        for (TransferableDataNode node : scene.getNodes()) {
            w = scene.findWidget(node);
            if (w instanceof InputConnectorWidget) {
                InputConnectorWidget fw = (InputConnectorWidget) w;
                fw.setValidConnection(InputConnectorWidget.STATE_NULL_CONNECTION);
            }
        }
        scene.validate();
    }

    public boolean hasCustomTargetWidgetResolver(Scene arg0) {
        return false;
    }

    public Widget resolveTargetWidget(Scene arg0, Point arg1) {
        return null;
    }

    public boolean isSourceReconnectable(ConnectionWidget cw) {
        return false;
    }

    public boolean isTargetReconnectable(ConnectionWidget cw) {
        return true;
    }

    public void reconnectingStarted(ConnectionWidget cw, boolean arg1) {
        target = null;
        Object object = scene.findObject(cw);
        TransferableDataEdge edge = scene.isEdge(object) ? (TransferableDataEdge) object : null;

        List<String> connections = scene.getPaletteScene().getDefaultPaletteModel().getConnections();
        int index = connections.indexOf(edge.toString());
        if(index!=-1) {
            connections.remove(index);
        }

        Widget o = cw.getTargetAnchor().getRelatedWidget();
        previousConnectionWidget = o;
        if (o instanceof InputConnectorWidget) {
            ((InputConnectorWidget) previousConnectionWidget).setHasConnection(false);
            ((InputConnectorWidget) previousConnectionWidget).setValidConnection(InputConnectorWidget.STATE_NULL_CONNECTION);
        } else {
            previousConnectionWidget = null;
        }
    }

    public void reconnectingFinished(ConnectionWidget cw, boolean arg1) {
        scene.validate();
    }

    public ConnectorState isReplacementWidget(ConnectionWidget cw, Widget widget, boolean isSource) {

        resetInputConnectors();

        if (!(widget instanceof InputConnectorWidget.FemaleArrow)) {
            return ConnectorState.REJECT_AND_STOP;
        }
        InputConnectorWidget.FemaleArrow arrowWidget = (FemaleArrow) widget;

        if (arrowWidget == ((InputConnectorWidget) previousConnectionWidget).getArrowWidget()) {
            arrowWidget.setValidConnection(InputConnectorWidget.STATE_VALID_CONNECTION);
            return ConnectorState.ACCEPT;
        }

        Object targetObject = scene.findObject(widget);
        target = scene.isNode(targetObject) ? (TransferableDataNode) targetObject : null;
        if (target == null || !(targetObject instanceof TransferableDataNode)) {
            return ConnectorState.REJECT_AND_STOP;
        }

        // Is the target data class assignable from source data class?
        Widget sourceWidget = cw.getSourceAnchor().getRelatedWidget();
        Object sourceObject = scene.findObject(sourceWidget);
        if (!(sourceObject instanceof TransferableDataNode) || !(targetObject instanceof TransferableDataNode)) {
            return ConnectorState.REJECT_AND_STOP;
        }
        TransferableDataNode targetNode = (TransferableDataNode) targetObject;
        TransferableDataNode sourceNode = (TransferableDataNode) sourceObject;
        if (targetNode.getTransferableData() instanceof Output) {
            return ConnectorState.REJECT_AND_STOP;
        }

        Input targetData = (Input) targetNode.getTransferableData();
        Output sourceData = (Output) sourceNode.getTransferableData();
        boolean success = targetData.acceptConnectionToOutput(sourceData);
        if (!success) {
            //newArrow.setValidConnection(InputConnectorWidget.STATE_INVALID_CONNECTION);
            return ConnectorState.REJECT_AND_STOP;
        }

        // Does the target input have a connection?
        PaletteScene paletteScene = scene.getPaletteScene();
        String targetNodeString = targetNode.toString();
        for(String s : paletteScene.getDefaultPaletteModel().getConnections()) {
            if(s.split(">")[1].equals(targetNodeString)) {
                return ConnectorState.REJECT_AND_STOP;
            }
        }

        //arrowWidget.setHasConnection(true);
        arrowWidget.setValidConnection(InputConnectorWidget.STATE_VALID_CONNECTION);
        return ConnectorState.ACCEPT;
    }

    public boolean hasCustomReplacementWidgetResolver(Scene scene) {
        return false;
    }

    public Widget resolveReplacementWidget(Scene scene, Point pt) {
        return null;
    }

    public void reconnect(ConnectionWidget cw, Widget widget, boolean arg2) {
        Object object = scene.findObject(cw);
        TransferableDataEdge edge = scene.isEdge(object) ? (TransferableDataEdge) object : null;

        // widget is null if nothing is connected
        if (widget == null) {
            scene.removeEdge(edge);
            scene.validate();
            return;
        }
        FemaleArrow arrow = (FemaleArrow) widget;
        arrow.setHasConnection(true);
        arrow.repaint();

        TransferableDataNode targetNode = (TransferableDataNode) scene.findObject(widget);
        edge.setTargetNode(targetNode);
        scene.setEdgeTarget(edge, targetNode);
        scene.getPaletteScene().getDefaultPaletteModel().getConnections().add(edge.toString());
        scene.validate();
    }
}
