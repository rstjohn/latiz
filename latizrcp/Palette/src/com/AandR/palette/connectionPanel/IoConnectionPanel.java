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

import com.AandR.latiz.core.lookup.LatizLookup;
import com.AandR.library.gui.HeaderPanel;
import com.AandR.palette.cookies.PluginSelectionCookie;
import com.AandR.palette.paletteScene.ConnectorEdge;
import com.AandR.palette.paletteScene.IPaletteClosed;
import com.AandR.palette.paletteScene.PaletteEditor;
import com.AandR.palette.paletteScene.PaletteScene;
import com.AandR.palette.paletteScene.PaletteScenePanel;
import com.AandR.palette.plugin.AbstractPlugin;
import com.AandR.palette.plugin.IConnectionSelected;
import com.AandR.palette.plugin.IPluginConnection;
import com.AandR.palette.plugin.IPluginIOChanged;
import com.AandR.palette.plugin.IPluginNameChange;
import com.AandR.palette.plugin.IPluginsRemoved;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import net.miginfocom.swing.MigLayout;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;

/**
 *
 * @author rstjohn
 */
public class IoConnectionPanel extends JPanel {

    private AbstractPlugin currentSourcePlugin;
    private IoConnectionPanelScene scene;
    private JLabel sourceLabel,  targetLabel;
    private JList availableConnectionsList;
    private JScrollPane sceneScrollPanel;
    private Lookup.Result<PluginSelectionCookie> pluginResult;
    private PaletteScenePanel activeScenePanel;
    private PluginSelectionListener pluginSelectionListener;
    private AvailableConnectionsList availableConnectionsListener;

    public IoConnectionPanel() {
        initialize();
        createContentPane();
    }

    private void initialize() {
        ImageIcon icon = new ImageIcon(ImageUtilities.loadImage("com/AandR/palette/resources/connect_no.png"));
        sourceLabel = new JLabel("", icon, SwingConstants.LEFT);
        sourceLabel.setBorder(new LineBorder(Color.BLACK));
        sourceLabel.setOpaque(true);

        targetLabel = new JLabel("", icon, SwingConstants.LEFT);
        targetLabel.setOpaque(true);
        targetLabel.setBorder(new LineBorder(Color.BLACK));

        sceneScrollPanel = new JScrollPane(new JPanel(new BorderLayout()));
        sceneScrollPanel.setBorder(new EmptyBorder(0, 0, 0, 0));

        availableConnectionsList = new JList(new DefaultListModel());
        availableConnectionsList.addListSelectionListener(availableConnectionsListener = new AvailableConnectionsList());
        availableConnectionsList.setCellRenderer(new AvailablePluginRenderer());

        scene = new IoConnectionPanelScene();

        LatizLookup.getDefault().addToLookup(new PluginConnectionImpl());
        LatizLookup.getDefault().addToLookup(new PluginNameChangeImpl());
        LatizLookup.getDefault().addToLookup(new PluginsRemovedImpl());
        LatizLookup.getDefault().addToLookup(new PluginIOChangedImpl());
        LatizLookup.getDefault().addToLookup(new ConnectionSelectedImpl());
        LatizLookup.getDefault().addToLookup(new ConnectionSelectedImpl());
        LatizLookup.getDefault().addToLookup(new PaletteClosedImpl());
    }

    private void createContentPane() {
        NavigatorListener navigatorListener = new NavigatorListener();
        setLayout(new MigLayout("", "[][]push[]", "[top]"));
        JButton prevButton = new JButton(new ImageIcon(ImageUtilities.loadImage("com/AandR/palette/resources/go-previous.png")));
        prevButton.setToolTipText("Move to previous plugin");
        prevButton.setActionCommand("previous");
        prevButton.addActionListener(navigatorListener);

        JButton nextButton = new JButton(new ImageIcon(ImageUtilities.loadImage("com/AandR/palette/resources/go-next.png")));
        nextButton.setToolTipText("Move to next plugin");
        nextButton.setActionCommand("next");
        nextButton.addActionListener(navigatorListener);

        JPanel p = new JPanel(new MigLayout("", "", ""));
        p.add(new HeaderPanel("Available Connections", null), "pushx, growx, wrap");
        p.add(new JScrollPane(availableConnectionsList), "push, grow");

        add(prevButton, "w 25!");
        add(nextButton, "w 25!, wrap");
        add(createPluginHeaderPanel(), "spanx, growx, wrap");

        add(sceneScrollPanel, "gap 5 0 5 5, spanx, push, grow, dock center");
        add(p, "pushy, grow, dock east");
    }

    private JPanel createPluginHeaderPanel() {
        JPanel p = new JPanel(new MigLayout("gap 0", "[]45[]push[]", ""));
        p.add(sourceLabel, "w 150!");
        p.add(targetLabel, "w 150!, wrap");
        return p;
    }

    public void updateConnectionPanel() {
        if (activeScenePanel == null) {
            return;
        }

        if (currentSourcePlugin == null) {
            sourceLabel.setText("Outputs");
            targetLabel.setText("Inputs");
            scene.setNoConnections();
            return;
        }

        String sourceName = currentSourcePlugin.getName();
        refreshAvailableConnectionsListFor(sourceName);

        if (availableConnectionsList.getModel().getSize() < 1) {
            sourceLabel.setText("Outputs");
            targetLabel.setText("Inputs");
            scene.setNoConnections();
            return;
        }
        AbstractPlugin targetPlugin = activeScenePanel.getPlugin(availableConnectionsList.getModel().getElementAt(0).toString());
        sourceLabel.setText(sourceName);
        targetLabel.setText(targetPlugin.getName());
        scene.setConnectionsFor(currentSourcePlugin, targetPlugin);

        JComponent view = scene.getView();
        sceneScrollPanel.setViewportView(view == null ? scene.createView() : view);

        for (String s : new ArrayList<String>(activeScenePanel.getPaletteModel().getConnections())) {
            scene.makeConnection(s);
        }
    }

    private void updateConnectionPanel(String sourceName, String targetName) {
        if (activeScenePanel == null) {
            return;
        }
        refreshAvailableConnectionsListFor(sourceName);

        if (availableConnectionsList.getModel().getSize() < 1) {
            sourceLabel.setText("Outputs");
            targetLabel.setText("Inputs");
            scene.setNoConnections();
            return;
        }
        AbstractPlugin sourcePlugin = activeScenePanel.getPlugin(sourceName);
        AbstractPlugin targetPlugin = activeScenePanel.getPlugin(targetName);
        sourceLabel.setText(sourceName);
        targetLabel.setText(targetName);
        scene.setConnectionsFor(sourcePlugin, targetPlugin);

        JComponent view = scene.getView();
        sceneScrollPanel.setViewportView(view == null ? scene.createView() : view);

        for (String s : new ArrayList<String>(activeScenePanel.getPaletteModel().getConnections())) {
            scene.makeConnection(s);
        }
    }

    private void refreshAvailableConnectionsListFor(String pluginName) {
        Collection<ConnectorEdge> edges = activeScenePanel.getScene().getEdges();

        TreeSet<String> availables = new TreeSet<String>();
        for (ConnectorEdge edge : edges) {
            if (!edge.getName().startsWith(pluginName + ">")) {
                continue;
            }
            availables.add(edge.getName().split(">")[1]);
        }

        DefaultListModel model = (DefaultListModel) availableConnectionsList.getModel();
        model.clear();
        for (String s : availables) {
            model.addElement(s);
        }
    }

    void addLookupListener() {
        pluginResult = LatizLookup.getDefault().lookup(new Lookup.Template<PluginSelectionCookie>(PluginSelectionCookie.class));
        pluginSelectionListener = new PluginSelectionListener();
        pluginResult.addLookupListener(pluginSelectionListener);
        pluginSelectionListener.resultChanged(null);
    }

    void removeLookupListener() {
        pluginResult.removeLookupListener(pluginSelectionListener);
        pluginResult = null;
    }

    void showNullPanel() {
        sourceLabel.setText("Outputs");
        targetLabel.setText("Inputs");
        scene.setNoConnections();
//        refreshConnectionsListFor(NULL_CONNECTION_PANEL);
//        ((CardLayout) cards.getLayout()).show(cards, NULL_CONNECTION_PANEL);
    }

    public void setActiveScenePanel(PaletteScenePanel paletteScenePanel) {
        activeScenePanel = paletteScenePanel;
        scene.setPaletteScene(activeScenePanel.getScene());
    }

    public PaletteScenePanel getActiveScenePanel() {
        return activeScenePanel;
    }

    public void setSourcePlugin(AbstractPlugin sourcePlugin) {
        this.currentSourcePlugin = sourcePlugin;
    }

    /**
     *
     */
    private class PluginSelectionListener implements LookupListener {

        @SuppressWarnings(value="unchecked")
        public void resultChanged(LookupEvent lookupEvent) {
            if (lookupEvent == null) {
                PluginSelectionCookie psc = LatizLookup.getDefault().lookup(PluginSelectionCookie.class);
                if (psc != null) {
                    currentSourcePlugin = psc.getSelectedPlugin();
                    updateConnectionPanel();
                    if (availableConnectionsList.getModel().getSize() > 0) {
                        setSelectedTarget(availableConnectionsList.getModel().getElementAt(0).toString());
                    }
                }
                return;
            }
            Lookup.Result<PluginSelectionCookie> src = (Lookup.Result<PluginSelectionCookie>) lookupEvent.getSource();
            Collection<PluginSelectionCookie> allInstances = (Collection<PluginSelectionCookie>) src.allInstances();
            if (allInstances.size() != 1) {
                currentSourcePlugin = null;
                sourceLabel.setText("Outputs");
                targetLabel.setText("Inputs");
                scene.setNoConnections();
//                showNullPanel();
                return;
            }

            for (PluginSelectionCookie psc : allInstances) {
                currentSourcePlugin = psc.getSelectedPlugin();
                updateConnectionPanel();
                if (availableConnectionsList.getModel().getSize() > 0) {
                    setSelectedTarget(availableConnectionsList.getModel().getElementAt(0).toString());
                }
                return;
            }
        }
    }

    private class NavigatorListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            String command = e.getActionCommand();
            if (command.equals("previous")) {
                actionPreviousPlugin((JButton) e.getSource());
            } else if (command.equals("next")) {
                actionNextPlugin((JButton) e.getSource());
            }
        }

        private void actionPreviousPlugin(JButton button) {
            if (currentSourcePlugin == null) {
                return;
            }

            String sourceName = currentSourcePlugin.getName();
            ArrayList<String> connections = new ArrayList<String>();
//            for (String key : panelsMap.keySet()) {
//                if (key.endsWith(">" + sourceName)) {
//                    connections.add(key.split(">")[0]);
//                }
//            }

            if (connections.isEmpty()) {
                return;
            }

            if (connections.size() == 1) {
//                activeScenePanel.getScene().setSelectedPlugin(connections.get(0));
//                activeScenePanel.getScene().validate();
                return;
            }

            JPopupMenu popup = new JPopupMenu();
            JMenuItem thisItem;
            for (String name : connections) {
                thisItem = new JMenuItem(name);
                thisItem.addActionListener(new ActionListener() {

                    public void actionPerformed(ActionEvent e) {
                        activeScenePanel.getScene().setSelectedPlugin(((JMenuItem) e.getSource()).getText());
                        activeScenePanel.getScene().validate();
                    }
                });
                popup.add(thisItem);
            }
            popup.show(button, 10, 10);
            return;
        }

        private void actionNextPlugin(JButton button) {
            String targetPluginName;
            DefaultListModel model = (DefaultListModel) availableConnectionsList.getModel();
            if (model.getSize() <= 0) {
                return;
            } else if (model.getSize() == 1) {
                targetPluginName = model.get(0).toString();
            } else {
                JPopupMenu popup = new JPopupMenu();
                JMenuItem thisItem;
                for (int i = 0; i < model.getSize(); i++) {
                    thisItem = new JMenuItem(model.get(i).toString());
                    thisItem.addActionListener(new ActionListener() {

                        public void actionPerformed(ActionEvent e) {
                            activeScenePanel.getScene().setSelectedPlugin(((JMenuItem) e.getSource()).getText());
                            activeScenePanel.getScene().validate();
                        }
                    });
                    popup.add(thisItem);
                }
                popup.show(button, 10, 10);
                return;
            }
            activeScenePanel.getScene().setSelectedPlugin(targetPluginName);
            activeScenePanel.getScene().validate();
        }
    }

    /**
     *
     * @author Dr. Richard St. John
     * @version $Revision$, $Date$
     */
    private class AvailablePluginRenderer extends DefaultListCellRenderer {

        @Override
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            Border lineBorder = isSelected ? new LineBorder(Color.GREEN) : new EmptyBorder(1, 1, 1, 1);

            setIcon(new ImageIcon(ImageUtilities.loadImage("com/AandR/palette/resources/connect_no.png")));
            setBorder(new CompoundBorder(new EmptyBorder(2, 2, 2, 2), lineBorder));
            setText(value.toString());
            return this;
        }
    }

    /**
     *
     */
    private class AvailableConnectionsList implements ListSelectionListener {

        public void valueChanged(ListSelectionEvent e) {
            PluginSelectionCookie psc = LatizLookup.getDefault().lookup(PluginSelectionCookie.class);
            if (psc == null) {
                sourceLabel.setText("Outputs");
                targetLabel.setText("Inputs");
                scene.setNoConnections();
                return;
            }

            if (e.getValueIsAdjusting() || psc.getSelectedPlugin().getName() == null) {
                return;
            }
            Object selectedObject = availableConnectionsList.getSelectedValue();
            if (selectedObject == null) {
                return;
            }

            String targetName = availableConnectionsList.getSelectedValue().toString();
            updateConnectionPanel(currentSourcePlugin.getName(), targetName);
            setSelectedTarget(targetName);
        }
    }

    private void setSelectedTarget(String targetName) {
        availableConnectionsList.removeListSelectionListener(availableConnectionsListener);
        availableConnectionsList.setSelectedValue(targetName, true);
        availableConnectionsList.addListSelectionListener(availableConnectionsListener);
    }

    /**
     *
     */
    private class PluginConnectionImpl implements IPluginConnection {

        public void connectionMade(PaletteScene scene, AbstractPlugin scr, AbstractPlugin target) {
            updateConnectionPanel(scr.getName(), target.getName());
            setSelectedTarget(target.getName());
        }

        public void connectionRemoved(PaletteScene scene, AbstractPlugin sourcePlugin, AbstractPlugin targetPlugin) {
            List<String> ioConnections = activeScenePanel.getPaletteModel().getConnections();
            String[] pluginSplit;
            for (String ioConnection : new ArrayList<String>(ioConnections)) {
                pluginSplit = ioConnection.split(">");
                if (pluginSplit[0].startsWith(sourcePlugin.getName() + "::") && pluginSplit[1].startsWith(targetPlugin.getName() + "::")) {
                    ioConnections.remove(ioConnection);
                }
            }
        }
    }

    /**
     * 
     */
    private class PluginNameChangeImpl implements IPluginNameChange {

        public void nameWillChange(PaletteScene scene, AbstractPlugin p, String oldName, String newName) {
        }

        public void nameChanged(PaletteScene scene, AbstractPlugin p, String oldName, String newName) {
            List<String> ioConnections = activeScenePanel.getPaletteModel().getConnections();
            String[] pluginSplit;
            for (String ioConnection : new ArrayList<String>(ioConnections)) {
                pluginSplit = ioConnection.split(">");
                if (pluginSplit[0].startsWith(oldName + "::")) {
                    ioConnections.remove(ioConnection);
                    ioConnections.add(ioConnection.replace(oldName + "::", newName + "::"));
                } else if (pluginSplit[1].startsWith(oldName + "::")) {
                    ioConnections.remove(ioConnection);
                    ioConnections.add(ioConnection.replace(">" + oldName + "::", ">" + newName + "::"));
                }
            }
            sourceLabel.setText(newName);
        }
    }

    /**
     *
     */
    private class ConnectionSelectedImpl implements IConnectionSelected {

        public void selectionMade(PaletteScene scene, ConnectorEdge edge) {
            String[] split = edge.getName().split(">");
            updateConnectionPanel(split[0], split[1]);
            setSelectedTarget(split[1]);
        }
    }

    /**
     * 
     */
    private class PaletteClosedImpl implements IPaletteClosed {

        public void closed(PaletteEditor paletteEditor) {
            activeScenePanel = null;
            currentSourcePlugin = null;
            showNullPanel();
        }
    }

    /**
     *
     */
    private class PluginsRemovedImpl implements IPluginsRemoved {

        public void removePlugins(PaletteScene scene, ArrayList<AbstractPlugin> pluginsRemoved) {
            for (AbstractPlugin p : pluginsRemoved) {
                removeConnectionsFor(p.getName());
            }
            showNullPanel();
        }

        private void removeConnectionsFor(String pluginName) {
            List<String> connections = activeScenePanel.getPaletteModel().getConnections();
            for(String s : new ArrayList<String>(connections)) {
                if(s.startsWith(pluginName + "::")) {
                    connections.remove(s);
                } else if(s.split(">")[1].startsWith(pluginName + "::")) {
                    connections.remove(s);
                }
            }
        }
    }

    private class PluginIOChangedImpl implements IPluginIOChanged {
        public void pluginOutputsChanged(AbstractPlugin plugin) {
            Set<String> outputKeys = plugin.getOutputDataMap().keySet();
            List<String> connections = activeScenePanel.getPaletteModel().getConnections();
            String dataItem;
            for(String s : new ArrayList<String>(connections)) {
                if(!s.startsWith(plugin.getName() + "::")) {
                    continue;
                }
                dataItem = s.split(">")[0].split("::")[1];
                if(outputKeys.contains(dataItem)) {
                    connections.remove(s);
                }
            }
        }

        public void pluginInputsChanged(AbstractPlugin plugin) {
            Set<String> inputKeys = plugin.getInputDataMap().keySet();
            List<String> connections = activeScenePanel.getPaletteModel().getConnections();
            String dataItem;
            String right;
            for(String s : new ArrayList<String>(connections)) {
                right = s.split(">")[1];
                if(!right.startsWith(plugin.getName() + "::")) {
                    continue;
                }
                dataItem = right.split("::")[1];
                if(inputKeys.contains(dataItem)) {
                    connections.remove(s);
                }
            }
        }
    }
}

