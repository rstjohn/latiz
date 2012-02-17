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
package com.AandR.recordedOutputs;

import com.AandR.palette.paletteScene.IWorkspaceLoaded;
import com.AandR.palette.paletteScene.PaletteScene;
import com.AandR.palette.plugin.AbstractPlugin;
import com.AandR.recordedOutputs.nodes.OutputDataNode;
import com.AandR.recordedOutputs.nodes.PluginNode;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import org.jdom.Element;
import org.openide.nodes.Node;

/**
 *
 * @author rstjohn
 */
public class RecordedOutputsTopPanel extends javax.swing.JPanel implements IWorkspaceLoaded {
    private final OutputPropertyPanel outputPropertyPanel;
    private final RecordedOutputsPanel recordedOutputsPanel;

    public RecordedOutputsTopPanel() {
        initComponents();
        recordedOutputsPanel = new RecordedOutputsPanel();
        recordedOutputsPanel.getExplorerManager().addPropertyChangeListener(propertyChangeListener);
        splitPane.setTopComponent(recordedOutputsPanel);
        scrollPane.setViewportView(outputPropertyPanel = new OutputPropertyPanel());
    }

    public void load(PaletteScene scene, Element root) {
        recordedOutputsPanel.load(scene, root);
    }

    public void updateSavedOutputsMap() {
        recordedOutputsPanel.updateSavedOutputsMap();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        splitPane = new javax.swing.JSplitPane();
        scrollPane = new javax.swing.JScrollPane();

        splitPane.setDividerLocation(200);
        splitPane.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        splitPane.setResizeWeight(0.5);
        splitPane.setBottomComponent(scrollPane);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(splitPane, javax.swing.GroupLayout.DEFAULT_SIZE, 164, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(splitPane, javax.swing.GroupLayout.DEFAULT_SIZE, 316, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane scrollPane;
    private javax.swing.JSplitPane splitPane;
    // End of variables declaration//GEN-END:variables

    private PropertyChangeListener propertyChangeListener = new PropertyChangeListener() {
        public void propertyChange(PropertyChangeEvent evt) {
            if(!evt.getPropertyName().equals("selectedNodes")) return;
            
            Node[] selectedNodes = recordedOutputsPanel.getExplorerManager().getSelectedNodes();
            if(selectedNodes==null || selectedNodes.length<1) return;

            Node selectedNode = selectedNodes[0];

            if(selectedNode instanceof PluginNode) {
                outputPropertyPanel.showCard(OutputPropertyPanel.CARD_PLUGIN);
                outputPropertyPanel.updatePluginProperties(((PluginNode)selectedNode).getPluginObject());
            } else if(selectedNode instanceof OutputDataNode) {
                outputPropertyPanel.showCard(OutputPropertyPanel.CARD_OUTPUT);
                outputPropertyPanel.fireNodeSelectionWillChange();
                outputPropertyPanel.setOutputDataObject(((OutputDataNode) selectedNode).getOutputData());
                outputPropertyPanel.updateProperty();
            } else {
                outputPropertyPanel.showCard(OutputPropertyPanel.CARD_EMPTY);
            }
        }
    };

    void pluginSelectionChanged(AbstractPlugin selectedPlugin) {
        recordedOutputsPanel.pluginSelectionChanged(selectedPlugin);
    }
}
