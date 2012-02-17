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

import com.AandR.recordedOutputs.nodes.OutputDataObject;
import com.AandR.latiz.core.lookup.LatizLookup;
import com.AandR.palette.cookies.PaletteSelectionCookie;
import com.AandR.palette.dataWriter.DefaultSavedOutputsImpl;
import com.AandR.palette.paletteScene.IPaletteCleared;
import com.AandR.palette.paletteScene.PaletteScene;
import com.AandR.palette.plugin.AbstractPlugin;
import com.AandR.palette.plugin.IPluginsAdded;
import com.AandR.palette.plugin.IPluginsRemoved;
import com.AandR.recordedOutputs.actions.DeselectAllAction;
import com.AandR.recordedOutputs.actions.SelectAllAction;
import com.AandR.recordedOutputs.nodes.OutputDataNode;
import com.AandR.recordedOutputs.nodes.PluginNode;
import com.AandR.recordedOutputs.nodes.PluginObject;
import com.AandR.recordedOutputs.nodes.RecordedOutputsRootNode;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyVetoException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JToolBar;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SwingConstants;
import org.jdom.Element;
import org.openide.explorer.ExplorerManager;
import org.openide.explorer.view.OutlineView;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.nodes.NodeOp;

/**
 *
 * @author rstjohn
 */
public class RecordedOutputsPanel extends javax.swing.JPanel implements ExplorerManager.Provider {
    private final ExplorerManager manager;
    private PaletteScene activeScene;
    private List<OutputDataObject> outputList;
    private Node rootNode;

    public RecordedOutputsPanel() {
        manager = new ExplorerManager();
        initComponents();
        
        LatizLookup.getDefault().addToLookup(this);
        LatizLookup.getDefault().addToLookup(new IPaletteCleared() {
            public void paletteCleared(String paletteName) {
//                selectedDataNodeMap.clear();
//                selectedDataNodeMap.clear();
                reloadTree();
            }
        });
        LatizLookup.getDefault().addToLookup(new IPluginsAdded() {
            public void pluginsAdded(PaletteScene scene, ArrayList<AbstractPlugin> plugins) {
                PaletteSelectionCookie psc = LatizLookup.getDefault().lookup(PaletteSelectionCookie.class);
                if(psc==null) return;
                activeScene = psc.getActivePalette().getScenePanel().getScene();
                reloadTree();
            }
        });
        LatizLookup.getDefault().addToLookup(new IPluginsRemoved() {
            public void removePlugins(PaletteScene scene, ArrayList<AbstractPlugin> pluginsRemoved) {
                PaletteSelectionCookie psc = LatizLookup.getDefault().lookup(PaletteSelectionCookie.class);
                if(psc==null) return;
                activeScene = psc.getActivePalette().getScenePanel().getScene();
                reloadTree();
            }
        });
        
        rootNode = new AbstractNode(Children.LEAF);
        rootNode.setName("root");
        rootNode.setDisplayName("root");
        view.expandNode(rootNode);
        manager.setRootContext(rootNode);
    }

    public void load(PaletteScene scene, Element root) {
        activeScene = scene;
//        updateSelectedDataNodeMap();
        reloadTree();
    }

    void updateSavedOutputsMap() {
        if (activeScene == null) return;
        
        HashMap<String, LinkedHashMap<String, DefaultSavedOutputsImpl>> savedOutputsMap = activeScene.getDefaultPaletteModel().getSavedOutputsMap();
        savedOutputsMap.clear();
        DefaultSavedOutputsImpl thisOutput;
        LinkedHashMap<String, DefaultSavedOutputsImpl> thisSavedData;
        OutputDataObject thisDataObject;
        String name;
        for(Node pluginNode : rootNode.getChildren().snapshot()) {

            thisSavedData = new LinkedHashMap<String, DefaultSavedOutputsImpl>();
            for(Node outputNode : pluginNode.getChildren().snapshot()) {
                if(!(outputNode instanceof OutputDataNode)) continue;

                thisDataObject = ((OutputDataNode) outputNode).getOutputData();
                if(!thisDataObject.isSelected()) continue;
                
                name = thisDataObject.getName();
                thisOutput = new DefaultSavedOutputsImpl();
                thisOutput.setDatasetName(name);
                thisOutput.setBeginTime(thisDataObject.getBeginTime().toString());
                thisOutput.setEndTime(thisDataObject.getEndTime().toString());
                thisOutput.setPeriod(thisDataObject.getPeriod().toString());
                thisOutput.setMaxIterationCount(thisDataObject.getMaxIterationCount().toString());
                thisSavedData.put(name, thisOutput);
            }
            if(thisSavedData.isEmpty()) continue;
            savedOutputsMap.put(pluginNode.getDisplayName(), thisSavedData);
        }
    }

    /**
     * Does a complete reload of the lat-file tree.
     * @param latizSystems
     * @param pluginOutgoingConnectorMaps
     */
    void reloadTree() {
        if(activeScene==null || activeScene.getPluginsMap().isEmpty()) return;
        
        Set<PluginObject> sortedPlugins = new TreeSet<PluginObject>(new Comparator<PluginObject>() {
            public int compare(PluginObject o1, PluginObject o2) {
                return o1.getPlugin().getName().compareToIgnoreCase(o2.getPlugin().getName());
            }
        });

        Set<String> pluginOutputs;
        PluginObject po;
        for(AbstractPlugin plugin : activeScene.getPluginsMap().values()) {
            po = new PluginObject(plugin);
            outputList = po.getOutputDataList();
            pluginOutputs = plugin.getOutputDataMap().keySet();
            if(pluginOutputs==null || pluginOutputs.isEmpty()) continue;
            
            for(String key : pluginOutputs) {
                outputList.add(new OutputDataObject(key));
            }
            sortedPlugins.add(po);
        }

        List<String[]> paths = new ArrayList<String[]>();
        TreeUtilities.saveExpansionState(view, rootNode, rootNode, paths);
        
        rootNode = new RecordedOutputsRootNode(sortedPlugins);
        manager.setRootContext(rootNode);
        view.expandNode(rootNode);
        TreeUtilities.recallExpansionState(view, rootNode, paths);
    }

    void pluginSelectionChanged(AbstractPlugin plugin) {
        Node root = manager.getRootContext();
        Node nodeToSelect = NodeOp.findChild(root, plugin.getName());
        if (nodeToSelect != null) {
            try {
                manager.setSelectedNodes(new Node[]{nodeToSelect});
            } catch (PropertyVetoException ex) {
            }
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        view = new OutlineView("Available Plugins");
        view.getOutline().setRootVisible(false);
        JCheckBox enableDataSavingCheckBox = new JCheckBox();
        JToolBar toolbar = new JToolBar();
        JButton expandTreeButton = new JButton();
        JButton collapseTreeButton = new JButton();
        JButton checkAllButton = new JButton();
        JButton uncheckAllButton = new JButton();

        enableDataSavingCheckBox.setText("Enable Data Saving?");

        toolbar.setFloatable(false);
        toolbar.setRollover(true);

        expandTreeButton.setIcon(new ImageIcon(getClass().getResource("/com/AandR/recordedOutputs/resources/expandTree16.png"))); // NOI18N
        expandTreeButton.setFocusPainted(false);
        expandTreeButton.setFocusable(false);
        expandTreeButton.setHorizontalTextPosition(SwingConstants.CENTER);
        expandTreeButton.setVerticalTextPosition(SwingConstants.BOTTOM);
        expandTreeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                actionExpandNodes(evt);
            }
        });
        toolbar.add(expandTreeButton);

        collapseTreeButton.setIcon(new ImageIcon(getClass().getResource("/com/AandR/recordedOutputs/resources/collapseTree16.png"))); // NOI18N
        collapseTreeButton.setFocusPainted(false);
        collapseTreeButton.setFocusable(false);
        collapseTreeButton.setHorizontalTextPosition(SwingConstants.CENTER);
        collapseTreeButton.setVerticalTextPosition(SwingConstants.BOTTOM);
        collapseTreeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                actionCollapseNodes(evt);
            }
        });
        toolbar.add(collapseTreeButton);

        checkAllButton.setIcon(new ImageIcon(getClass().getResource("/com/AandR/recordedOutputs/resources/check20.png"))); // NOI18N
        checkAllButton.setFocusPainted(false);
        checkAllButton.setFocusable(false);
        checkAllButton.setHorizontalTextPosition(SwingConstants.CENTER);
        checkAllButton.setVerticalTextPosition(SwingConstants.BOTTOM);
        checkAllButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                actionSelectAll(evt);
            }
        });
        toolbar.add(checkAllButton);

        uncheckAllButton.setIcon(new ImageIcon(getClass().getResource("/com/AandR/recordedOutputs/resources/uncheck20.png"))); // NOI18N
        uncheckAllButton.setFocusPainted(false);
        uncheckAllButton.setFocusable(false);
        uncheckAllButton.setHorizontalTextPosition(SwingConstants.CENTER);
        uncheckAllButton.setVerticalTextPosition(SwingConstants.BOTTOM);
        uncheckAllButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                actionDeselectAll(evt);
            }
        });
        toolbar.add(uncheckAllButton);

        GroupLayout layout = new GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(enableDataSavingCheckBox)
                .addContainerGap())
            .addComponent(toolbar, GroupLayout.DEFAULT_SIZE, 190, Short.MAX_VALUE)
            .addComponent(view, GroupLayout.DEFAULT_SIZE, 190, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(Alignment.LEADING)
            .addGroup(Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(enableDataSavingCheckBox)
                .addPreferredGap(ComponentPlacement.RELATED)
                .addComponent(toolbar, GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(ComponentPlacement.RELATED)
                .addComponent(view, GroupLayout.DEFAULT_SIZE, 209, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void actionExpandNodes(ActionEvent evt) {//GEN-FIRST:event_actionExpandNodes
        for(Node node : manager.getRootContext().getChildren().getNodes()) {
            view.expandNode(node);
        }
    }//GEN-LAST:event_actionExpandNodes

    private void actionCollapseNodes(ActionEvent evt) {//GEN-FIRST:event_actionCollapseNodes
        for(Node node : manager.getRootContext().getChildren().getNodes()) {
            view.collapseNode(node);
        }
    }//GEN-LAST:event_actionCollapseNodes

    private void actionSelectAll(ActionEvent evt) {//GEN-FIRST:event_actionSelectAll
        for(Node node : rootNode.getChildren().getNodes()) {
            if(node instanceof PluginNode) {
                new SelectAllAction((PluginNode)node).actionPerformed(evt);
            }
        }
        view.revalidate();
        view.repaint();
    }//GEN-LAST:event_actionSelectAll

    private void actionDeselectAll(ActionEvent evt) {//GEN-FIRST:event_actionDeselectAll
        for(Node node : rootNode.getChildren().getNodes()) {
            if(node instanceof PluginNode) {
                new DeselectAllAction((PluginNode)node).actionPerformed(evt);
            }
        }
        view.revalidate();
        view.repaint();
    }//GEN-LAST:event_actionDeselectAll

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private OutlineView view;
    // End of variables declaration//GEN-END:variables

    public ExplorerManager getExplorerManager() {
        return manager;
    }
}
