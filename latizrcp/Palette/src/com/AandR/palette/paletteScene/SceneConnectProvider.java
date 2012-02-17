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
package com.AandR.palette.paletteScene;

import com.AandR.latiz.core.lookup.LatizLookup;
import com.AandR.palette.plugin.IPluginConnection;
import com.AandR.palette.plugin.AbstractPlugin;
import java.awt.Point;
import org.netbeans.api.visual.action.ConnectProvider;
import org.netbeans.api.visual.action.ConnectorState;
import org.netbeans.api.visual.widget.Scene;
import org.netbeans.api.visual.widget.Widget;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.openide.util.Lookup;

/**
 *
 * @author alex
 */
public class SceneConnectProvider implements ConnectProvider {

    private PaletteScene scene;
    
    private PluginNode source, target;
    

    public SceneConnectProvider(PaletteScene scene){
        this.scene=scene;
    }

    
    public boolean isSourceWidget(Widget sourceWidget) {
        PluginNode object = (PluginNode) scene.findObject(sourceWidget);
        source = scene.isNode(object) ? object : null;
        return source != null;
    }


    public ConnectorState isTargetWidget(Widget sourceWidget, Widget targetWidget) {
        Object targetObject = scene.findObject(targetWidget);
        target = scene.isNode(targetObject) ? (PluginNode)targetObject : null;

        if(target==null)
            return ConnectorState.REJECT;

        if(!(targetObject instanceof PluginNode))
            return ConnectorState.REJECT;
        
        if(source.getName().equals(target.getName()))
            return ConnectorState.REJECT;

        // Are they already connected?
        ConnectorEdge edge = new ConnectorEdge(source.getName() + ">" + target.getName());
        Collection currentEdges = scene.findEdgesBetween(source, (PluginNode)targetObject);
        if(currentEdges !=null && currentEdges.contains(edge))
            return ConnectorState.REJECT;

        return ConnectorState.ACCEPT;
    }

    
    public boolean hasCustomTargetWidgetResolver(Scene scene) {
        return false;
    }


    public Widget resolveTargetWidget(Scene scene, Point sceneLocation) {
        return null;
    }


    public void createConnection(Widget sourceWidget, Widget targetWidget) {
        PluginNode sourceNode = (PluginNode) scene.findObject(sourceWidget);
        PluginNode targetNode = (PluginNode) scene.findObject(targetWidget);

        ConnectorEdge edge = new ConnectorEdge(sourceNode.getName() +">" + targetNode.getName());
        scene.addEdge(edge);
        scene.setEdgeSource(edge, source);
        scene.setEdgeTarget(edge, target);
        scene.validate();
        Set<PluginNode> selectedNodes = new HashSet<PluginNode>();
        selectedNodes.add(sourceNode);
        scene.setFocusedObject(sourceNode);
        scene.setSelectedObjects(selectedNodes);
        notifyConnectionMade(sourceNode.getPlugin(), targetNode.getPlugin());
    }


    protected void notifyConnectionMade(AbstractPlugin source, AbstractPlugin target) {
        Lookup.Result<IPluginConnection> pcis = LatizLookup.getDefault().lookupResult(IPluginConnection.class);
        for(IPluginConnection pci : pcis.allInstances()) {
            pci.connectionMade(scene, source, target);
        }
    }
}
