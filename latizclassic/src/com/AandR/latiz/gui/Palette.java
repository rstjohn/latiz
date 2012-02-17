package com.AandR.latiz.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.TreeMap;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.border.Border;

import org.jdom.Element;

import com.AandR.beans.fileExplorerPanel.FileNode;
import com.AandR.beans.fileExplorerPanel.FileSystemModel;
import com.AandR.beans.fileExplorerPanel.FileSystemTree;
import com.AandR.gui.OptionsDialog;
import com.AandR.gui.dropSupport.DropEvent;
import com.AandR.gui.dropSupport.DropListener;
import com.AandR.gui.dropSupport.JPanelWithDropSupport;
import com.AandR.gui.ui.JButtonX;
import com.AandR.gui.ui.LineBorderX;
import com.AandR.io.ClassPathHacker;
import com.AandR.io.FileChooser;
import com.AandR.io.XmlFile;
import com.AandR.latiz.core.LatizSystem;
import com.AandR.latiz.core.LatizSystemUtilities;
import com.AandR.latiz.core.PluginComparator;
import com.AandR.latiz.core.PluginManager;
import com.AandR.latiz.core.PropertiesManager;
import com.AandR.latiz.dev.AbstractPlugin;
import com.AandR.latiz.interfaces.ParentPluginInterface;
import com.AandR.latiz.listeners.PalettePanelListener;
import com.AandR.latiz.listeners.PluginChangedListener;
import com.AandR.latiz.resources.Resources;

/**
 * 
 * @author Dr. Richard St. John
 * @version $Rev$, $Date$
 */
public class Palette extends JPanelWithDropSupport {

    public static final Color COLOR = Color.WHITE;
    public static final Color GRID_COLOR = new Color(230, 230, 230);
    public static final int GRID_LINE_SPACING = 20;
    public static final int GRID_LINES = 0;
    public static final int GRID_DOTS_1PT = 1;
    public static final int GRID_DOTS_2PT = 2;
    public static final int GRID_DOTS_3PT = 3;
    private int gridLineSpacing = GRID_LINE_SPACING;
    private int gridType = GRID_LINES;
    private float currentScaleFactor;
    private AbstractPlugin selectedPlugin;
    private ArrayList<AbstractPlugin> plugins, selectedPlugins;
    private ArrayList<PalettePanelListener> observers;
    private Color gridColor = GRID_COLOR, paletteColor = COLOR;
    private File workspaceFile;
    private HashMap<String, Connector> pluginToPluginConnectorMap;
    private HashMap<String, HashMap<String, Connector>> pluginOutgoingConnectorMaps;
    private HashSet<LatizSystem> latizSystems;
    private JMenu connectToMenu, removeFromMenu;
    private JMenuItem removeConnectionMenuItem;
    private JPopupMenu popupMenuPlugin, popupMenuPalette, popupMultipleSelection;
    private PaletteListener paletteListener;
    private String currentDirectory;

    public Palette(int w, int h) {
        PropertiesManager props = PropertiesManager.getInstanceOf();
        paletteColor = Color.decode(props.getProperty(PropertiesManager.PALETTE_BACKGROUND));
        gridColor = Color.decode(props.getProperty(PropertiesManager.PALETTE_LINE_COLOR));
        gridLineSpacing = Integer.parseInt(props.getProperty(PropertiesManager.PALETTE_LINE_SPACING));
        gridType = Integer.parseInt(props.getProperty(PropertiesManager.PALETTE_GRID_TYPE));
        currentDirectory = System.getProperty("user.home");
        currentScaleFactor = 1f;
        setLayout(null);
        setSize(new Dimension(w, h));
        setPreferredSize(new Dimension(w, h));
        setBounds(0, 0, w, h);
        setBackground(paletteColor);
        observers = new ArrayList<PalettePanelListener>();
        paletteListener = new PaletteListener();
        addMouseListener(paletteListener);
        addMouseMotionListener(paletteListener);
        addDropListener(paletteListener);
        createPluginPopupMenu();
        createPalettePopupMenu();
        createMultiplePopupMenu();
        plugins = new ArrayList<AbstractPlugin>();
        selectedPlugins = new ArrayList<AbstractPlugin>();
        pluginToPluginConnectorMap = new HashMap<String, Connector>();
        pluginOutgoingConnectorMaps = new HashMap<String, HashMap<String, Connector>>();
        latizSystems = new HashSet<LatizSystem>();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        int w = getWidth();
        int h = getHeight();

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        scaleGraphicsContext(g2);
        h = (int) (h / currentScaleFactor);
        w = (int) (w / currentScaleFactor);

        g2.setColor(gridColor);
        if (gridType > 0) {
            int dotSize = gridType;
            for (int j = 0; j < h; j += gridLineSpacing) {
                for (int i = 0; i < w; i += gridLineSpacing) {
                    g2.drawOval(i, j, dotSize, dotSize);
                    g2.fillOval(i, j, dotSize, dotSize);
                }
            }
        } else {
            for (int j = 0; j < h; j += gridLineSpacing) {
                g2.drawLine(0, j, w, j);
            }

            for (int j = 0; j < w; j += gridLineSpacing) {
                g2.drawLine(j, 0, j, h);
            }
        }
        repaintConnections();
    }

    /**
     *
     * @param g2
     * @throws IllegalArgumentException
     */
    public void scaleGraphicsContext(Graphics2D g2) throws IllegalArgumentException {
        int currentTranslationX = 0;
        int currentTranslationY = 0;
        if (currentScaleFactor > 0.0f) {
            g2.scale(currentScaleFactor, currentScaleFactor);
            g2.translate(currentTranslationX, currentTranslationY);
        } else {
            throw new IllegalArgumentException("An attempt was made to scale with a height or width value of 0");
        }
    }

    private void createPalettePopupMenu() {
        PopupListener l = new PopupListener();
        popupMenuPalette = new JPopupMenu("Palette Popup Menu");
        popupMenuPalette.add(createMenuItem("Show Lines", Resources.createIcon("showLines16.png"), "Show Lines", l));
        popupMenuPalette.add(createMenuItem("Fade Lines", Resources.createIcon("hideLines16.png"), "Fade Lines", l));
        popupMenuPalette.add(new JSeparator());
        popupMenuPalette.add(createMenuItem("Connect All", Resources.createIcon("connect_established.png"), "Connect All", l));
        popupMenuPalette.add(createMenuItem("Disconnect All", Resources.createIcon("disconnect22.png"), "Disconnect All", l));
        popupMenuPalette.add(new JSeparator());
        popupMenuPalette.add(createMenuItem("Iconify All", Resources.createIcon("identity.png"), "Iconify All", l));
        popupMenuPalette.add(createMenuItem("Display All Output", Resources.createIcon("identity.png"), "Display All Output", l));
        popupMenuPalette.add(new JSeparator());
        popupMenuPalette.add(createMenuItem("Clear Palette", null, "Clear Palette", l));
        popupMenuPalette.add(new JSeparator());
        popupMenuPalette.add(createMenuItem("Load", Resources.createIcon("fileopen.png"), "Load", l));
        popupMenuPalette.add(createMenuItem("Save", Resources.createIcon("filesave.png"), "Save", l));
        popupMenuPalette.add(createMenuItem("Save to Workspace", Resources.createIcon("filesave.png"), "Save to Workspace", l));
        popupMenuPalette.add(createMenuItem("Save As...", Resources.createIcon("filesaveas.png"), "Save As...", l));
    }

    private void createPluginPopupMenu() {
        removeConnectionMenuItem = new JMenuItem("Nothing", Resources.createIcon("cancel16.png"));
        removeConnectionMenuItem.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                String key = getConnectorKey(selectedPlugin);
                Connector c = pluginToPluginConnectorMap.get(key);
                pluginToPluginConnectorMap.remove(key);
                if (c == null) {
                    return;
                }
                c.removeConnection();
                c.repaint();
                repaint();
            }
        });

        connectToMenu = new JMenu("Connect To");
        connectToMenu.setIcon(Resources.createIcon("connect_established.png"));
        connectToMenu.add(removeConnectionMenuItem);

        removeFromMenu = new JMenu("Disconnect");
        removeFromMenu.setIcon(Resources.createIcon("disconnect22.png"));

        PopupListener l = new PopupListener();
        popupMenuPlugin = new JPopupMenu("Plugin Menu");
        popupMenuPlugin.add(connectToMenu);
        popupMenuPlugin.add(removeFromMenu);
        popupMenuPlugin.addSeparator();
        popupMenuPlugin.add(createMenuItem("Iconify", Resources.createIcon("identity.png"), "iconify", l));
        popupMenuPlugin.add(createMenuItem("Display Output", Resources.createIcon("identity.png"), "Display Output", l));
        popupMenuPlugin.add(createMenuItem("Remove", Resources.createIcon("delete16.png"), "Remove", l));
        popupMenuPlugin.addSeparator();
        popupMenuPlugin.add(createMenuItem("Properties", Resources.createIcon("stock-preferences22.png"), "Properties", l));
    }

    private void createMultiplePopupMenu() {
        MultipleSelectionPopupListener l = new MultipleSelectionPopupListener();
        popupMultipleSelection = new JPopupMenu();
        popupMultipleSelection.add(createMenuItem("Connect these", Resources.createIcon("connect_established.png"), "CONNECT_THESE", l));
        popupMultipleSelection.add(createMenuItem("Disconnect these", Resources.createIcon("disconnect22.png"), "DISCONNECT_THESE", l));
        popupMultipleSelection.addSeparator();
        popupMultipleSelection.add(createMenuItem("Remove these", Resources.createIcon("delete16.png"), "REMOVE_THESE", l));
    }

    public void fireMousePressed() {
        notifyMousePressed();
    }

    /**
     *
     * @return
     */
    public TreeMap<String, ConnectionPanel> getSelectedConnectionPanelMap() {
        TreeMap<String, ConnectionPanel> connectionPanelMap = new TreeMap<String, ConnectionPanel>();
        String selectedKey = selectedPlugin.getName();
        String[] keySplit;
        for (String connectorKey : pluginToPluginConnectorMap.keySet()) {
            keySplit = connectorKey.split(">");
            if (keySplit[0].equalsIgnoreCase(selectedKey)) {
                connectionPanelMap.put(connectorKey, pluginToPluginConnectorMap.get(connectorKey).getConnectionPanel());
            }
        }
        if (connectionPanelMap.size() == 0) {
            connectionPanelMap.put(selectedKey, new ConnectionPanel());
        }
        return connectionPanelMap;
    }

    private void refreshRemoveFromMenu(String selectedPlugin) {
        removeFromMenu.removeAll();
        String[] keySplit;
        for (String key : pluginToPluginConnectorMap.keySet()) {
            keySplit = key.split(">");
            if (selectedPlugin.equalsIgnoreCase(keySplit[0])) {
                removeFromMenu.add(createMenuItem(keySplit[1], Resources.createIcon("connect_no.png"), keySplit[1] + ".disconnect", new PopupListener()));
            }
        }
    }

    private void refreshConnectToMenu(String selectedPlugin) {
        connectToMenu.removeAll();
        connectToMenu.add(removeConnectionMenuItem);

        // Do not show plugins that that plugin is already connected to.
        ArrayList<String> alreadyConnectionList = new ArrayList<String>();
        String[] keySplit;
        for (String key : pluginToPluginConnectorMap.keySet()) {
            keySplit = key.split(">");
            if (selectedPlugin.equalsIgnoreCase(keySplit[0])) {
                alreadyConnectionList.add(keySplit[1]);
            }
        }

        for (AbstractPlugin p : plugins) {
            String label = p.getName();
            if (selectedPlugin.equalsIgnoreCase(label) || alreadyConnectionList.contains(label)) {
                continue;
            }
            connectToMenu.add(createMenuItem(label, Resources.createIcon("connect_no.png"), label + ".connect", new PopupListener()));
        }
    }

    private String getConnectorKey(AbstractPlugin p) {
        String[] thisKey;
        for (String key : pluginToPluginConnectorMap.keySet()) {
            thisKey = key.split(">");
            if (thisKey[0].equalsIgnoreCase(p.getName())) {
                return key;
            }
        }
        return null;
    }

    protected void repaintConnections() {
        for (AbstractPlugin p : plugins) {
            HashMap<String, Connector> thisOutgoingConnectorMap = pluginOutgoingConnectorMaps.get(p.getName());
            for (Connector c : thisOutgoingConnectorMap.values()) {
                c.updateBounds();
            }
        }
    }

    private JMenuItem createMenuItem(String label, ImageIcon icon, String actionCommand, ActionListener al) {
        JMenuItem item = new JMenuItem(label, icon);
        item.addActionListener(al);
        item.setActionCommand(actionCommand);
        return item;
    }

    public void addPalettePanelListener(PalettePanelListener palettePanelListener) {
        observers.add(palettePanelListener);
    }

    public void removePalettePanelListener(PalettePanelListener palettePanelListener) {
        int index = observers.indexOf(palettePanelListener);
        if (index > -1) {
            observers.remove(index);
        }
    }

    protected void nodifyMouseReleased() {
        for (PalettePanelListener listener : observers) {
            listener.mouseReleased();
        }
    }

    protected void notifyMousePressed() {
        for (PalettePanelListener listener : observers) {
            listener.mousePressed();
        }
    }

    protected void notifyPluginDropped(AbstractPlugin plugin) {
        for (PalettePanelListener listener : observers) {
            listener.pluginDropped(plugin);
        }
    }

    protected void notifyPluginRemoved(AbstractPlugin p) {
        for (PalettePanelListener listener : observers) {
            listener.pluginRemoved(p);
        }
    }

    protected void notifyPluginWillBeRemoved(AbstractPlugin p) {
        for (PalettePanelListener listener : observers) {
            listener.pluginWillBeRemoved(p);
        }
    }

    protected void notifyConnectionMade(Connector c) {
        for (PalettePanelListener listener : observers) {
            listener.connectionMade(c);
        }
    }

    protected void notifyConnectionRemoved(Connector c) {
        for (PalettePanelListener listener : observers) {
            listener.connectionRemoved(c);
        }
    }

    protected void notifyConnectionWillBeRemoved(Connector c) {
        for (PalettePanelListener listener : observers) {
            listener.connectionRemoved(c);
        }
    }

    protected void notifyPaletteCleared() {
        for (PalettePanelListener listener : observers) {
            listener.paletteCleared();
        }
    }

    protected void notifyPaletteLoaded(File file) {
        Element latizRoot = null;
        try {
            latizRoot = XmlFile.readRootElement(file);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        for (PalettePanelListener listener : observers) {
            listener.paletteLoaded(file, latizRoot);
        }
    }

    protected void notifyPaletteSaved(File file) {
        Element latizRoot = new Element("latizWorkspace");
        latizRoot.setAttribute("zoom", String.valueOf(getCurrentScaleFactor()));
        for (PalettePanelListener listener : observers) {
            listener.paletteSaved(workspaceFile, latizRoot);
        }
        try {
            XmlFile.write(file, latizRoot);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("ERROR while trying to save: " + file.getPath());
            return;
        }
        System.out.println(file.getPath() + " was saved.");
    }

    public void setSelectedPlugin(AbstractPlugin p) {
        selectedPlugin = p;
    }

    public AbstractPlugin getSelectedPlugin() {
        return selectedPlugin;
    }

    public ArrayList<AbstractPlugin> getPlugins() {
        return plugins;
    }

    public HashMap<String, Connector> getPluginToPluginConnectorMap() {
        return pluginToPluginConnectorMap;
    }

    public void setGridLineSpacing(int gridLineSpacing) {
        this.gridLineSpacing = gridLineSpacing;
    }

    public void setGridColor(Color gridColor) {
        this.gridColor = gridColor;
    }

    public void setPaletteColor(Color paletteColor) {
        this.paletteColor = paletteColor;
    }

    public void setGridType(int gridType) {
        this.gridType = gridType;
    }

    public float getCurrentScaleFactor() {
        return currentScaleFactor;
    }

    public void setCurrentScaleFactor(float currentScaleFactor) {
        this.currentScaleFactor = currentScaleFactor;
    }

    public ArrayList<Connector> getConnectors() {
        HashMap<String, Connector> connectors = pluginToPluginConnectorMap;
        if (connectors.isEmpty()) {
            ArrayList<Connector> connector = new ArrayList<Connector>();
            for (AbstractPlugin p : plugins) {
                Connector c = new Connector();
                c.setOutputSendingPlugin(p);
                connector.add(c);
            }
            return connector;
        }

        String firstKey = "";
        String thisOutputKey;
        for (String key : connectors.keySet()) {
            firstKey = key;
            thisOutputKey = key.split(">")[0];
            if (!hasInputConnection(thisOutputKey)) {
                break;
            }
        }

        ArrayList<Connector> c = new ArrayList<Connector>();
        c.add(connectors.get(firstKey));

        int n = connectors.size() - 1;
        String nextKey = firstKey;
        for (int i = 0; i < n; i++) {
            nextKey = findNextKey(nextKey);
            c.add(connectors.get(nextKey));
        }
        return c;
    }

    private boolean hasInputConnection(String outputKey) {
        for (String key : pluginToPluginConnectorMap.keySet()) {
            if (key.endsWith(outputKey)) {
                return true;
            }
        }
        return false;
    }

    private String findNextKey(String key) {
        String[] newKeySplit;
        String nextKey = null;
        String inputKey = key.split(">")[1];
        for (String newKey : pluginToPluginConnectorMap.keySet()) {
            newKeySplit = newKey.split(">");
            if (newKeySplit[0].equalsIgnoreCase(inputKey)) {
                nextKey = newKey;
                break;
            }
        }
        return nextKey;
    }

    public AbstractPlugin instantiatePlugin(String id, String name, Point location) throws InstantiationException, IllegalAccessException {
        PluginKey thisKey = PluginManager.getInstanceOf().findKey(id);
        if (thisKey == null) {
            return null;
        }
        try {
            File[] classPathJars = thisKey.getClassPath();
            //File[] classPathJars = null;
            if (classPathJars != null) {
                ClassPathHacker.addFiles(classPathJars);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        AbstractPlugin plugin = PluginManager.getInstanceOf().getPlugins().get(thisKey).newInstance();
        plugin.setPluginID(id);
        if (name == null || name.equalsIgnoreCase("")) {
            name = plugin.getName();
            setPluginName(plugin, name);
        } else {
            plugin.setName(name);
        }
        pluginOutgoingConnectorMaps.put(plugin.getName(), new HashMap<String, Connector>());
        plugins.add(plugin);
        plugin.setLocation(location);
        plugin.addPluginChangedListener(paletteListener);
        LatizSystem newSystem = new LatizSystem(new PluginComparator());
        newSystem.add(plugin);
        latizSystems.add(newSystem);
        add(plugin);
        validate();
        repaint();

        plugin.initializeInputs();
        plugin.initializeOutputs();

        notifyPluginDropped(plugin);
        return plugin;
    }

    public AbstractPlugin instantiatePlugin(String id, Point location) throws InstantiationException, IllegalAccessException {
        return instantiatePlugin(id, null, location);
    }

    private void setPluginName(AbstractPlugin p, String name) {
        for (AbstractPlugin thisPlugin : plugins) {
            if (thisPlugin.getName().equalsIgnoreCase(name)) {
                name += "_1";
                setPluginName(p, name);
                p.setName(name);
            }
        }
    }

    public final HashMap<String, HashMap<String, Connector>> getPluginOutgoingConnectorMaps() {
        return pluginOutgoingConnectorMaps;
    }

    public final void setPluginOutgoingConnectorMaps(HashMap<String, HashMap<String, Connector>> pluginOutgoingConnectorMaps) {
        this.pluginOutgoingConnectorMaps = pluginOutgoingConnectorMaps;
    }

    public final HashSet<LatizSystem> getLatizSystems() {
        return latizSystems;
    }

    public void clearPalette() {
        for (AbstractPlugin p : plugins) {
            if (pluginOutgoingConnectorMaps.get(p.getName()) != null) {
                pluginOutgoingConnectorMaps.get(p.getName()).clear();
            }
            Palette.this.remove(p);
            latizSystems.clear();
            notifyPluginRemoved(p);
        }
        plugins.clear();

        for (Connector c : pluginToPluginConnectorMap.values()) {
            Palette.this.remove(c);
            notifyConnectionRemoved(c);
        }
        pluginToPluginConnectorMap.clear();
        repaint();
        notifyPaletteCleared();
    }

    private void clearSelectedPlugins() {
        for (AbstractPlugin p : selectedPlugins) {
            p.setSelected(false);
            paintPluginBorder(p);
        }
        selectedPlugins.clear();
    }

    private void paintPluginBorder(AbstractPlugin p) {
        Border border = p.isSelected() ? new LineBorderX(Color.RED) : null;
        p.setBorder(border);
        if (p.getParent() != null) {
            p.getParent().validate();
            p.getParent().repaint();
        }
        p.repaint();
    }

    private void removeSelectedPlugin(AbstractPlugin selectedPlugin) {
        setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        notifyPluginWillBeRemoved(selectedPlugin);

        Palette.this.remove(selectedPlugin);
        for (AbstractPlugin p : LatizSystemUtilities.getChildren(latizSystems, selectedPlugin, pluginOutgoingConnectorMaps.get(selectedPlugin.getName()))) {
            p.removeParentPluginInterface(selectedPlugin);
        }

        plugins.remove(selectedPlugin);
        removeOutgoingConnector(selectedPlugin);

        for (ParentPluginInterface p : selectedPlugin.getParentPluginInterfaces()) {
            removeConnector(p.getParentPlugin(), selectedPlugin);
        }
        LatizSystemUtilities.pluginRemoved(latizSystems, selectedPlugin, pluginOutgoingConnectorMaps);
        notifyPluginRemoved(selectedPlugin);
    }

    private void removeConnector(AbstractPlugin parent, AbstractPlugin child) {
        if (parent == null) {
            return;
        }
        String[] splitKey;
        for (String key : pluginOutgoingConnectorMaps.get(parent.getName()).keySet()) {
            splitKey = key.split(">");
            if (splitKey[1].equalsIgnoreCase(child.getName())) {
                removeOutgoingConnector(key, pluginOutgoingConnectorMaps.get(parent.getName()));
                remove(pluginToPluginConnectorMap.get(key));
                pluginToPluginConnectorMap.remove(key);
                return;
            }
        }
    }

    private void removeOutgoingConnector(AbstractPlugin p) {
        if (p == null) {
            return;
        }
        HashMap<String, Connector> outgoingConnectorMap = pluginOutgoingConnectorMaps.get(p.getName());
        for (String key : outgoingConnectorMap.keySet()) {
            remove(pluginToPluginConnectorMap.get(key));
            pluginToPluginConnectorMap.remove(key);
        }
        pluginOutgoingConnectorMaps.get(p.getName()).clear();
        repaint();
    }

    public void removeOutgoingConnector(String connectionName, HashMap<String, Connector> map) {
        map.remove(map.get(connectionName).getName());
    }

    public void addOutgoingConnector(Connector c, HashMap<String, Connector> map) {
        map.put(c.getName(), c);
    }

    public File getWorkspaceFile() {
        return workspaceFile;
    }

    public void setWorkspaceFile(File workspaceFile) {
        this.workspaceFile = workspaceFile;
    }

    /**
     *
     * @author Dr. Richard St. John
     * @version $Revision$, $Date$
     */
    private class PaletteListener extends MouseAdapter implements DropListener, PluginChangedListener {

        private int startFromX = 0, startFromY = 0, dragFromX = 0, dragFromY = 0;
        private float currentTranslationX = 0, currentTranslationY = 0;

        @Override
        public void mouseClicked(MouseEvent e) {
            int x = (int) (e.getX() / currentScaleFactor);
            int y = (int) (e.getY() / currentScaleFactor);
            if (e.getButton() == 3) {
                if (plugins.isEmpty() || selectedPlugin == null) {
                    clearSelectedPlugins();
                    popupMenuPalette.show(e.getComponent(), e.getX(), e.getY());
                } else if (selectedPlugin.getBounds().contains(new Point(x, y))) {
                    if (selectedPlugins.isEmpty()) {
                        refreshPopupMenu();
                        popupMenuPlugin.show(e.getComponent(), e.getX(), e.getY());
                    } else {
                        popupMultipleSelection.show(e.getComponent(), e.getX(), e.getY());
                    }
                } else {
                    clearSelectedPlugins();
                    popupMenuPalette.show(e.getComponent(), e.getX(), e.getY());
                }
                if (selectedPlugin == null) {
                    return;
                }

                if (selectedPlugin.getParent() != null) {
                    selectedPlugin.getParent().validate();
                    selectedPlugin.getParent().repaint();
                }
                selectedPlugin.repaint();
                return;
            }

            if (e.isControlDown()) {
                if (selectedPlugin != null) {
                    selectedPlugin.setSelected(!selectedPlugin.isSelected());
                    paintPluginBorder(selectedPlugin);
                    selectedPlugins.add(selectedPlugin);
                }
            } else {
                clearSelectedPlugins();
            }
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            if (selectedPlugin != null) {
                if (selectedPlugin.getParent() != null) {
                    selectedPlugin.getParent().validate();
                    selectedPlugin.getParent().repaint();
                }
                selectedPlugin.repaint();
            }
            repaint();
            nodifyMouseReleased();
        }

        @Override
        public void mousePressed(MouseEvent e) {
            if (plugins == null) {
                return;
            }

            startFromX = (int) (currentTranslationX / currentScaleFactor);
            startFromY = (int) (currentTranslationY / currentScaleFactor);

            int x = (int) (e.getX() / currentScaleFactor);
            int y = (int) (e.getY() / currentScaleFactor);

            dragFromX = x - startFromX;
            dragFromY = y - startFromY;
            for (AbstractPlugin thisPlugin : plugins) {
                if (thisPlugin.getBounds().contains(new Point(x, y))) {
                    thisPlugin.setDraggable(true);
                    dragFromX = x - (int) (thisPlugin.getX());
                    dragFromY = y - (int) (thisPlugin.getY());
                    selectedPlugin = thisPlugin;
                    break;
                } else {
                    thisPlugin.setDraggable(false);
                }
            }

            // Create selection region.
            if (selectedPlugin == null) {
                return;
            }

            for (AbstractPlugin a : plugins) {
                a.setOpaque(false);
                a.repaint();
            }

            String selectedLabel = selectedPlugin.getName();
            for (AbstractPlugin thisPlugin : plugins) {
                if (thisPlugin.getName().equalsIgnoreCase(selectedLabel)) {
                    thisPlugin.setOpaque(true);
                    break;
                }
            }
            notifyMousePressed();
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            if (plugins == null) {
                return;
            }
            int x = (int) (e.getX() / currentScaleFactor);
            int y = (int) (e.getY() / currentScaleFactor);
            Point pt = new Point();
            for (AbstractPlugin thisPlugin : plugins) {
                if (!thisPlugin.isDraggable()) {
                    continue;
                }
                pt.x = x - dragFromX < 0 ? 0 : x - dragFromX;
                pt.y = y - dragFromY < 0 ? 0 : y - dragFromY;
                thisPlugin.setLocation(pt);
                break;
            }
            repaint();
        }

        @Override
        public void mouseMoved(MouseEvent e) {
            if (plugins == null) {
                return;
            }
            int x = (int) (e.getX() / currentScaleFactor);
            int y = (int) (e.getY() / currentScaleFactor);
            for (AbstractPlugin thisPlugin : plugins) {
                if (thisPlugin.getBounds().contains(new Point(x, y))) {
                    setCursor(new Cursor(Cursor.HAND_CURSOR));
                    thisPlugin.setDraggable(true);
                    break;
                } else {
                    setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                    thisPlugin.setDraggable(false);
                }
            }
        }

        private void refreshPopupMenu() {
            refreshConnectToMenu(selectedPlugin.getName());
            refreshRemoveFromMenu(selectedPlugin.getName());
        }

        public void dropAction(DropEvent dropEvent) {
            String s = ((String[]) dropEvent.getDroppedItem())[0];

            try {
                AbstractPlugin p = instantiatePlugin(s, dropEvent.getLocation());
                if (p == null) {
                    File file = new File(s);
                    if (isLatizFile(file)) {
                        notifyPaletteLoaded(file);
                        workspaceFile = file;
                    }
                }
            } catch (NullPointerException ne) {
                File file = new File(s);
                if (isLatizFile(file)) {
                    notifyPaletteLoaded(file);
                    workspaceFile = file;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private boolean isLatizFile(File file) {
            FileInputStream stream;
            OptionsDialog invalidDialog = new OptionsDialog(null, "File Format Error", OptionsDialog.ERROR_ICON);
            if (file.isDirectory()) {
                invalidDialog.showDialog("<HTML>The directory <BR><B>" + file.getPath() + "</B><BR>is not a valid latiz workspace file.", 0);
                return false;
            }
            try {
                stream = new FileInputStream(file);
                byte[] fileHead = new byte[100];
                stream.read(fileHead);
                String contents = new String(fileHead);
                if (!contents.contains("<latizWorkspace ")) {
                    invalidDialog.showDialog("<HTML>The dropped file <BR><B>" + file.getPath() + "</B><BR>is not a valid latiz workspace file.", 0);
                    return false;
                }
                return true;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return false;
        }

        public void pluginNameChanged(String oldName, String newName) {
            changeNameInConnectorMaps(pluginToPluginConnectorMap, oldName, newName);
            changeNameInOutgoingConnectorMaps(oldName, newName);
        }

        private void changeNameInOutgoingConnectorMaps(String oldName, String newName) {
            HashMap<String, Connector> newMap = null;
            for (String key : pluginOutgoingConnectorMaps.keySet()) {
                if (key.equals(oldName)) {
                    newMap = changeNameInConnectorMaps(pluginOutgoingConnectorMaps.get(key), oldName, newName);
                } else {
                    changeNameInConnectorMaps(pluginOutgoingConnectorMaps.get(key), oldName, newName);
                }
            }
            pluginOutgoingConnectorMaps.remove(oldName);
            if (newMap == null) {
                pluginOutgoingConnectorMaps.put(newName, new HashMap<String, Connector>());

            } else {
                pluginOutgoingConnectorMaps.put(newName, newMap);
            }
        }

        private HashMap<String, Connector> changeNameInConnectorMaps(HashMap<String, Connector> connectorMap, String oldName, String newName) {
            int size = connectorMap.size();
            if (size == 0) {
                return null;
            }
            String[] newKeys = new String[size];
            Connector[] newConnectors = new Connector[size];
            String[] keySplit;
            int i = 0;
            for (String key : connectorMap.keySet()) {
                keySplit = key.split(">");

                if (keySplit[0].equalsIgnoreCase(oldName)) {
                    newKeys[i] = newName + ">" + keySplit[1];
                } else if (keySplit[1].equalsIgnoreCase(oldName)) {
                    newKeys[i] = keySplit[0] + ">" + newName;
                } else {
                    newKeys[i] = key;
                }

                newConnectors[i] = connectorMap.get(key);
                newConnectors[i].setName(newKeys[i]);
                i++;
            }
            connectorMap.clear();
            for (int k = 0; k < size; k++) {
                connectorMap.put(newKeys[k], newConnectors[k]);
            }
            return connectorMap;
        }

        public void pluginInputsChanged(AbstractPlugin plugin) {
            HashMap<String, Connector> connectorMap;
            for (ParentPluginInterface ppi : plugin.getParentPluginInterfaces()) {
                connectorMap = pluginOutgoingConnectorMaps.get(ppi.getParentPlugin().getName());
                for (Connector c : connectorMap.values()) {
                    if (c.getInputReceivingPlugin().equals(plugin)) {
                        c.getConnectionPanel().notifyPluginInputsChanged(plugin, ppi.getParentPlugin());
                    }
                }
            }
        }

        public void pluginOutputsChanged(AbstractPlugin plugin) {
            HashMap<String, Connector> connectMap = pluginOutgoingConnectorMaps.get(plugin.getName());
            for (Connector c : connectMap.values()) {
                c.getConnectionPanel().notifyPluginOutputsChanged(c.getInputReceivingPlugin(), plugin);
            }
        }

        public void pluginDisplayChanged(AbstractPlugin p) {
        }
    }

    /**
     *
     * @author Dr. Richard St. John
     * @version $Revision$, $Date$
     */
    private class MultipleSelectionPopupListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            String command = e.getActionCommand();
            if (command.equalsIgnoreCase("REMOVE_THESE")) {
                actionRemoveThese();
            } else if (command.equalsIgnoreCase("CONNECT_THESE")) {
                actionConnectThese();
            } else if (command.equals("DISCONNECT_THESE")) {
                actionDisconnectThese();
            }

            clearSelectedPlugins();
        }

        private void actionConnectThese() {
            TreeMap<Double, AbstractPlugin> sortedMap = new TreeMap<Double, AbstractPlugin>();
            for (AbstractPlugin p : selectedPlugins) {
                sortedMap.put(new Double(p.getLocation().distance(0, 0)), p);
            }

            int selectedIndex = 0;
            AbstractPlugin[] plugins = new AbstractPlugin[sortedMap.size()];
            int i = 0;
            for (Double key : sortedMap.keySet()) {
                plugins[i] = sortedMap.get(key);
                if (plugins[i].equals(selectedPlugin)) {
                    selectedIndex = i;
                }
                i++;
            }

            for (i = 0; i < plugins.length - 1; i++) {
                selectedPlugin = plugins[i];
                String connectFrom = plugins[i].getName();
                String connectTo = plugins[i + 1].getName();
                String s = connectFrom + ">" + connectTo;
                if (pluginToPluginConnectorMap.keySet().contains(s)) {
                    continue;
                }

                Connector c = new Connector(plugins[i], plugins[i + 1]);
                pluginToPluginConnectorMap.put(s, c);
                HashMap<String, Connector> connectorMap = pluginOutgoingConnectorMaps.get(connectFrom);
                addOutgoingConnector(c, connectorMap);
                add(c);
                LatizSystemUtilities.connectionMade(latizSystems, c);
                repaint();
                notifyConnectionMade(c);
            }
            selectedPlugin = plugins[selectedIndex];
        }

        private void actionDisconnectThese() {

            HashMap<String, AbstractPlugin> connectionsToRemove = new HashMap<String, AbstractPlugin>();
            for (AbstractPlugin fromPlugin : selectedPlugins) {
                String connectFrom = fromPlugin.getName();

                HashMap<String, Connector> theseConnectors = pluginOutgoingConnectorMaps.get(connectFrom);
                for (String key : theseConnectors.keySet()) {
                    String[] split = key.split(">");

                    for (AbstractPlugin toPlugin : selectedPlugins) {
                        if (toPlugin.getName().equals(split[1])) {
                            connectionsToRemove.put(key, fromPlugin);
                            break;
                        }
                    }
                }
            }

            String[] split;
            for (String s : connectionsToRemove.keySet()) {
                split = s.split(">");
                String connectFrom = split[0];
                String connectTo = split[1];
                Connector c = pluginToPluginConnectorMap.get(connectFrom + ">" + connectTo);
                notifyConnectionWillBeRemoved(c);
                remove(c);
                String connectorName = connectFrom + ">" + connectTo;
                pluginToPluginConnectorMap.remove(connectorName);
                removeOutgoingConnector(connectorName, pluginOutgoingConnectorMaps.get(connectFrom));
                LatizSystemUtilities.getPluginFromList(plugins, connectTo).removeParentPluginInterface(connectionsToRemove.get(s));
                LatizSystemUtilities.connectionRemoved(latizSystems, c, pluginOutgoingConnectorMaps);
                repaint();
                notifyConnectionRemoved(c);
            }
        }

        private void actionRemoveThese() {
            for (AbstractPlugin thisPlugin : selectedPlugins) {
                removeSelectedPlugin(thisPlugin);
            }
        }
    }

    /**
     *
     * @author Dr. Richard St. John
     * @version $Revision$, $Date$
     */
    private class PopupListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            String command = e.getActionCommand();
            if (command.endsWith(".connect")) {
                connectSelectedPlugin(command.substring(0, command.lastIndexOf(".connect")));
            } else if (command.endsWith(".disconnect")) {
                disconnectSelectedPlugin(command.substring(0, command.lastIndexOf(".disconnect")));
            } else if (command.equalsIgnoreCase("Remove")) {
                removeSelectedPlugin(selectedPlugin);
            } else if (command.equalsIgnoreCase("iconify")) {
                iconifySelectedPlugin();
            } else if (command.equalsIgnoreCase("display output")) {
                displayOutput();
            } else if (command.equalsIgnoreCase("Fade Lines")) {
                actionSetLineColor(new Color(0, 0, 120, 30));
            } else if (command.equalsIgnoreCase("Show Lines")) {
                actionSetLineColor(Color.BLUE);
            } else if (command.equalsIgnoreCase("Disconnect All")) {
                actionDisconnectAll();
            } else if (command.equalsIgnoreCase("Connect All")) {
                actionConnectAll();
            } else if (command.equalsIgnoreCase("Clear Palette")) {
                clearPalette();
            } else if (command.equalsIgnoreCase("Properties")) {
                PluginPropertiesDialog pluginPropertiesDialog = new PluginPropertiesDialog(selectedPlugin);
            } else if (command.equalsIgnoreCase("Load")) {
                FileChooser chooser = new FileChooser(currentDirectory);
                int selections = chooser.showOpenDialog(Palette.this);
                if (selections == FileChooser.CANCEL_OPTION) {
                    return;
                }
                currentDirectory = chooser.getSelectedFile().getParent();
                notifyPaletteLoaded(chooser.getSelectedFile());
            } else if (command.equalsIgnoreCase("Save")) {
                actionSave();
            } else if (command.equalsIgnoreCase("Save to Workspace")) {
                actionSaveToWorkspace();
            } else if (command.equalsIgnoreCase("Save As...")) {
                actionSaveAs();
            } else if (command.equalsIgnoreCase("Iconify All")) {
                iconifyAllPlugins();
            } else if (command.equalsIgnoreCase("Display All Output")) {
                displayAllPluginOutputs();
            }
            clearSelectedPlugins();
        }

        private void actionSaveToWorkspace() {
            SaveToWorkspaceDialog saveToWorkspaceDialog = new SaveToWorkspaceDialog();
        }

        private void actionSaveAs() {
            FileChooser fileChooser = new FileChooser(currentDirectory);
            int choice = fileChooser.showSaveDialog(Palette.this);
            if (choice == JFileChooser.CANCEL_OPTION) {
                return;
            }
            currentDirectory = fileChooser.getSelectedFile().getParent();
            File file = fileChooser.getSelectedFile();
            if (!file.getName().endsWith(".latiz")) {
                file = new File(file.getPath() + ".latiz");
            }
            workspaceFile = file;
            notifyPaletteSaved(workspaceFile);
        }

        private void actionSave() {
            if (workspaceFile == null) {
                actionSaveAs();
                return;
            }
            notifyPaletteSaved(workspaceFile);
        }

        private void removeOutgoingConnector(String connectionName, HashMap<String, Connector> map) {
            map.remove(map.get(connectionName).getName());
        }

        private void addOutgoingConnector(Connector c, HashMap<String, Connector> map) {
            map.put(c.getName(), c);
        }

        private void disconnectSelectedPlugin(String disconnectFromName) {
            String connectFrom = selectedPlugin.getName();
            String connectTo = disconnectFromName;
            Connector c = pluginToPluginConnectorMap.get(connectFrom + ">" + connectTo);
            notifyConnectionWillBeRemoved(c);
            remove(c);
            String connectorName = connectFrom + ">" + connectTo;
            pluginToPluginConnectorMap.remove(connectorName);
            removeOutgoingConnector(connectorName, pluginOutgoingConnectorMaps.get(selectedPlugin.getName()));
            LatizSystemUtilities.getPluginFromList(plugins, connectTo).removeParentPluginInterface(selectedPlugin);
            LatizSystemUtilities.connectionRemoved(latizSystems, c, pluginOutgoingConnectorMaps);
            repaint();
            notifyConnectionRemoved(c);
        }

        private void connectSelectedPlugin(String connectToName) {
            String connectFrom = selectedPlugin.getName();
            String connectTo = connectToName;
            Connector c = new Connector(selectedPlugin, LatizSystemUtilities.getPluginFromList(plugins, connectTo));
            //remove(c);
            pluginToPluginConnectorMap.put(connectFrom + ">" + connectTo, c);
            addOutgoingConnector(c, pluginOutgoingConnectorMaps.get(selectedPlugin.getName()));
            add(c);
            //selectedPlugin.revalidate();
            LatizSystemUtilities.connectionMade(latizSystems, c);
            repaint();
            notifyConnectionMade(c);
        }

        private void actionSetLineColor(Color color) {
            for (Connector c : pluginToPluginConnectorMap.values()) {
                c.setConnectorColor(color);
                c.repaint();
            }
        }

        private void actionConnectAll() {
            TreeMap<Double, AbstractPlugin> sortedMap = new TreeMap<Double, AbstractPlugin>();
            for (AbstractPlugin p : plugins) {
                sortedMap.put(new Double(p.getLocation().distance(0, 0)), p);
            }

            AbstractPlugin[] plugins = new AbstractPlugin[sortedMap.size()];
            int i = 0;
            for (Double key : sortedMap.keySet()) {
                plugins[i] = sortedMap.get(key);
                pluginOutgoingConnectorMaps.get(plugins[i].getName()).clear();
                i++;
            }

            for (Connector c : pluginToPluginConnectorMap.values()) {
                remove(c);
            }

            Connector c;
            pluginToPluginConnectorMap.clear();
            String connectorName;
            for (i = 0; i < plugins.length - 1; i++) {
                c = new Connector(plugins[i], plugins[i + 1]);
                connectorName = plugins[i].getName() + ">" + plugins[i + 1].getName();
                pluginToPluginConnectorMap.put(connectorName, c);
                add(c);
                selectedPlugin = plugins[i];
                addOutgoingConnector(c, pluginOutgoingConnectorMaps.get(selectedPlugin.getName()));
                LatizSystemUtilities.connectionMade(latizSystems, c);
                notifyConnectionMade(c);
            }
            repaint();
        }

        private void actionDisconnectAll() {
            latizSystems.clear();
            LatizSystem newSystem;
            for (AbstractPlugin p : plugins) {
                newSystem = new LatizSystem(new PluginComparator());
                newSystem.add(p);
                latizSystems.add(newSystem);
            }

            Connector c;
            for (String key : pluginToPluginConnectorMap.keySet()) {
                c = pluginToPluginConnectorMap.get(key);
                remove(c);
            }
            pluginToPluginConnectorMap.clear();

            HashMap<String, Connector> outgoingConnectorMap;
            for (AbstractPlugin p : plugins) {
                outgoingConnectorMap = pluginOutgoingConnectorMaps.get(p.getName());
                for (Connector connector : outgoingConnectorMap.values()) {
                    notifyConnectionRemoved(connector);
                }
                outgoingConnectorMap.clear();
            }
            repaint();
        }

        private void iconifySelectedPlugin() {
            selectedPlugin.iconify();
        }

        private void iconifyAllPlugins() {
            for (AbstractPlugin p : plugins) {
                p.iconify();
            }
        }

        private void displayAllPluginOutputs() {
            for (AbstractPlugin p : plugins) {
                p.paintPluginPanel();
            }
        }

        private void displayOutput() {
            selectedPlugin.paintPluginPanel();
        }
    }

    /**
     *
     * @author Dr. Richard St. John
     * @version $Revision$, $Date$
     */
    private class SaveToWorkspaceDialog extends JDialog implements ActionListener, MouseListener {

        private File workspace;
        private FileSystemTree fileSystemTree;
        private JTextField nameField;

        public SaveToWorkspaceDialog() {
            super((JDialog) null, "Save To Workspace Folder");
            setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            setContentPane(this.createContentPane());
            pack();
            setLocationRelativeTo(Palette.this);
            setVisible(true);
        }

        private Container createContentPane() {
            String workspaceDir = System.getProperty("user.home") + File.separator + ".AandRcreations" + File.separator + "latiz" + File.separator + "Workspaces";
            workspace = new File(workspaceDir);
            new File(workspaceDir).mkdirs();
            JPanel namePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            namePanel.add(new JLabel("Workspace file name: "));
            nameField = new JTextField("name.latiz", 18);
            namePanel.add(nameField);

            fileSystemTree = new FileSystemTree();
            fileSystemTree.setModel(new FileSystemModel(new FileNode(workspace)));
            fileSystemTree.setSelectionRow(0);
            fileSystemTree.addMouseListener(this);

            JButtonX acceptButton = new JButtonX("Accept");
            acceptButton.setActionCommand("ACCEPT");
            acceptButton.addActionListener(this);

            JButtonX cancelButton = new JButtonX("Cancel");
            cancelButton.setActionCommand("CANCEL");
            cancelButton.addActionListener(this);

            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            buttonPanel.add(acceptButton);
            buttonPanel.add(cancelButton);

            JPanel southPanel = new JPanel(new GridLayout(2, 1, 0, 0));
            southPanel.add(namePanel);
            southPanel.add(buttonPanel);

            JPanel panel = new JPanel(new BorderLayout());
            panel.add(new JScrollPane(fileSystemTree), BorderLayout.CENTER);
            panel.add(southPanel, BorderLayout.SOUTH);
            return panel;
        }

        public void actionPerformed(ActionEvent e) {
            String command = e.getActionCommand();
            if (command.equalsIgnoreCase("ACCEPT")) {
                actionAccept();
            } else if (command.equalsIgnoreCase("CANCEL")) {
                dispose();
            }
        }

        private void actionAccept() {
            File selectedFile = fileSystemTree.getSelectedFile();
            File parent = selectedFile.isDirectory() ? selectedFile : selectedFile.getParentFile();

            String name = nameField.getText();
            if (!name.endsWith(".latiz")) {
                name += ".latiz";
            }
            File newFile = new File(parent, name);
            if (newFile.exists()) {
                OptionsDialog overwrite = new OptionsDialog(Palette.this, "Overwrite Dialog", new JButtonX[]{new JButtonX("Overwrite"), new JButtonX("Cancel")}, OptionsDialog.QUESTION_ICON);
                overwrite.showDialog("<HTML>Do you want to overwrite <B>" + newFile.getName() + "<B>?</HTML>", 0);
                if (overwrite.getSelectedButtonIndex() == 1) {
                    return;
                }
            }
            parent.mkdirs();
            try {
                newFile.createNewFile();
                workspaceFile = newFile;
                notifyPaletteSaved(workspaceFile);
                dispose();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }

        public void mouseClicked(MouseEvent e) {
        }

        public void mouseEntered(MouseEvent e) {
        }

        public void mouseExited(MouseEvent e) {
        }

        public void mouseReleased(MouseEvent e) {
        }

        public void mousePressed(MouseEvent e) {
            int row = fileSystemTree.getRowForLocation(e.getX(), e.getY());
            if (row < 0) {
                return;
            }
            fileSystemTree.setSelectionRow(row);
            File file = fileSystemTree.getSelectedFile();
            if (file.isFile()) {
                nameField.setText(file.getName());
            }
        }
    }
}
