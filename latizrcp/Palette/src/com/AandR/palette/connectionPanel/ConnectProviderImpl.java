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
import org.netbeans.api.visual.action.ConnectProvider;
import org.netbeans.api.visual.action.ConnectorState;
import org.netbeans.api.visual.widget.Scene;
import org.netbeans.api.visual.widget.Widget;

/**
 *
 * @author rstjohn
 */
public class ConnectProviderImpl implements ConnectProvider {

    private IoConnectionPanelScene scene;
    private TransferableDataNode source,  target;

    public ConnectProviderImpl(IoConnectionPanelScene graphScene) {
        this.scene = graphScene;
    }

    public boolean isSourceWidget(Widget sourcWidget) {
        TransferableDataNode object = (TransferableDataNode) scene.findObject(sourcWidget);
        source = scene.isNode(object) ? object : null;
        return (source != null && (sourcWidget instanceof OutputConnectorWidget.MaleArrow));
    }

    public ConnectorState isTargetWidget(Widget sourceWidget, Widget targetWidget) {
        resetInputConnectors();

        if (!(targetWidget instanceof InputConnectorWidget.FemaleArrow)) {
            return ConnectorState.REJECT;
        }

        InputConnectorWidget.FemaleArrow arrowWidget = (FemaleArrow) targetWidget;

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
        for (String s : paletteScene.getDefaultPaletteModel().getConnections()) {
            if (s.split(">")[1].equals(targetNodeString)) {
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

    public void createConnection(Widget sourceWidget, Widget targetWidget) {
        TransferableDataNode sourceNode = (TransferableDataNode) scene.findObject(sourceWidget);
        TransferableDataNode targetNode = (TransferableDataNode) scene.findObject(targetWidget);
        TransferableDataEdge edge = new TransferableDataEdge(sourceNode, targetNode);
        scene.addEdge(edge);
        scene.setEdgeSource(edge, source);
        scene.setEdgeTarget(edge, target);
        scene.validate();
    }
}
