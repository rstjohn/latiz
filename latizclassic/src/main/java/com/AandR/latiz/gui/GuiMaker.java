package com.AandR.latiz.gui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.jdom.Element;
import org.jdom.JDOMException;

import com.AandR.beans.fileExplorerPanel.FileExplorerPanel;
import com.AandR.beans.imagePlotPanel.CinemaPanel;
import com.AandR.beans.jEditPanel.JEditTabsPanel;
import com.AandR.beans.latFileExplorerPanel.LatFilePlotPanel;
import com.AandR.gui.ConsolePanel;
import com.AandR.gui.HeaderPanel;
import com.AandR.gui.ui.GuiHelper;
import com.AandR.gui.ui.IndicatorIconPanel;
import com.AandR.gui.ui.InfiniteProgressPanel;
import com.AandR.gui.ui.JButtonX;
import com.AandR.gui.ui.JComboBoxButton;
import com.AandR.gui.ui.JTabbedPaneX;
import com.AandR.gui.ui.LineBorderX;
import com.AandR.gui.ui.TabOrderChangedListener;
import com.AandR.io.FileChooser;
import com.AandR.io.SortedProperties;
import com.AandR.io.XmlFile;
import com.AandR.latiz.core.GlobalParameterMap;
import com.AandR.latiz.core.LatizSystem;
import com.AandR.latiz.core.LatizSystemUtilities;
import com.AandR.latiz.core.PluginManager;
import com.AandR.latiz.core.PropertiesManager;
import com.AandR.latiz.dev.AbstractPlugin;
import com.AandR.latiz.gui.prefs.PreferencesDialog;
import com.AandR.latiz.gui.prefs.PreferencesListener;
import com.AandR.latiz.interfaces.DisplayableInterface;
import com.AandR.latiz.listeners.ConnectionPanelListener;
import com.AandR.latiz.pluginWizard.PluginWizard;
import com.AandR.latiz.resources.Resources;
import com.AandR.latiz.swing.LNumberField;
import com.AandR.network.RemoteApplicationUpdateDialog;

/**
 * @author Dr. Richard St. John
 * @version $Revision$, $Date$
 */
public class GuiMaker extends JFrame {

    public static final String ACTION_RUN = "ACTION_RUN";
    public static final String ACTION_STOP = "ACTION_STOP_SIMULATION";
    public static final String ACTION_PLUGIN_WIZARD = "ACTION_PLUGIN_WIZARD";
    public static final String ACTION_PLUGIN_MANAGER = "ACTION_PLUGIN_MANAGER";
    public static final String ACTION_EXPAND_X = "ACTION_EXPAND_X";
    public static final String ACTION_EXPAND_Y = "ACTION_EXPAND_Y";
    public static final String ACTION_CONTRACT_X = "ACTION_CONTRACT_X";
    public static final String ACTION_CONTRACT_Y = "ACTION_CONTRACT_Y";
    public static final String ACTION_PALETTE_ZOOM = "ACTION_PALETTE_ZOOM";
    private static final String ACTION_UPDATES = "ACTION_UPDATES";
    private static final String ACTION_ABOUT = "ACTION_ABOUT";
    private static final String ACTION_FIND_PLUGINS = "ACTION_FIND_PLUGINS";
    private static final String ACTION_PREFERENCES = "ACTION_PREFERENCES";
    private static final String ACTION_CALCULATOR = "ACTION_CALCULATOR";
    private static final String ACTION_LOAD = "ACTION_LOAD";
    private static final String ACTION_SAVE = "ACTION_SAVE";
    private static final String ACTION_EXIT = "ACTION_EXIT";
    private static final String ACTION_GOTO_FIRST = "ACTION_GOTO_FIRST";
    private static final String ACTION_GOTO_PREVIOUS = "ACTION_GOTO_PREVIOUS";
    private static final String ACTION_GOTO_NEXT = "ACTION_GOTO_NEXT";
    private static final String ACTION_PLUGIN_CHOOSER = "ACTION_PATH_CHOOSER";
    private static final String ACTION_PERSPECTIVE_LATIZ = "ACTION_PERSPECTIVE_LATIZ";
    private static final String ACTION_PERSPECTIVE_LATFILE = "ACTION_PERSPECTIVE_LATFILE";
    private static final String ACTION_PERSPECTIVE_CINEMA = "ACTION_PERSPECTIVE_CINEMA";
    private static final String ACTION_PERSPECTIVE_TEXT_EDITOR = "ACTION_PERSPECTIVE_TEXT_EDITOR";
    private static final String ACTION_PERSPECTIVE_HUD = "ACTION_PERSPECTIVE_HUD";
    public static final String SAVE = "SAVE";
    public static final String SAVE_AS = "SAVE_AS";
    public static final String SAVE_TO_WORKSPACE = "SAVE_TO_WORKSPACE";
    private ActionListener toolbarListener;
    private IndicatorIconPanel indicatorPanel;
    //private File currentFile;
    private GlobalParametersPanel globalsPanel;
    private HashSet<String> pluginIOConnectionSet;
    private HeadUpDisplayPanel headUpDisplayPanel;
    private InfiniteProgressPanel glassPane;
    private JList connectionSelectionList;
    private JMenuBar menu;
    private JPanel cardsCenter, emptyLatPanel, emptyCinema, emptyTextEditor;
    private JPanel connectionPanel, palettePanel, connectionSelectionPanel;
    private JSplitPane paletteSplitter, centerSplitter, splitterConsole, splitterFileTree, propertiesSplitter;
    private JTabbedPaneX inputTabs;
    private JComboBoxButton runButton, stopButton;
    private LatFileTreePanel latFileTreePanel;
    private LatFilePlotPanel latFilePlotPanel = null;
    private LNumberField stopTimeField;
    private MenuListener menuListener;
    private Palette palette;
    private PaletteListener paletteListener;
    private PluginsTreePanel pluginTreePanel;
    private String currentDirectory;
    private JTabbedPaneX fileTabs;
    private FileExplorerPanel fileExplorerPanel;

    public GuiMaker(ActionListener toolbarListener) {
        super("Latiz");
        this.toolbarListener = toolbarListener;
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        initialize();
        setContentPane(createContentPane());
        setJMenuBar(createMenu());
        setGlassPane(glassPane = new InfiniteProgressPanel("", 0));
        pack();
        recallProperties();
        setVisible(true);
    }

    private void initialize() {
        currentDirectory = System.getProperty("user.home");

        headUpDisplayPanel = new HeadUpDisplayPanel();

        menuListener = new MenuListener();

        pluginIOConnectionSet = new HashSet<String>();

        indicatorPanel = new IndicatorIconPanel(Resources.createIcon("agt_runit.png"), 5);
        indicatorPanel.setPreferredSize(new Dimension(23, 23));
        indicatorPanel.setMaximumSize(new Dimension(23, 23));
        indicatorPanel.setToolTipText("Timer is running.");

        palette = new Palette(2000, 1200);
        palettePanel = new JPanel(new BorderLayout());
        palettePanel.add(createToolbarPanel(), BorderLayout.NORTH);
        palettePanel.add(new JScrollPane(palette), BorderLayout.CENTER);

        InputsTabListener inputsTabListener = new InputsTabListener();
        inputTabs = new JTabbedPaneX(JTabbedPaneX.TOP, JTabbedPaneX.SCROLL_TAB_LAYOUT);
        inputTabs.addTabOrderChangedListener(inputsTabListener);
        inputTabs.addChangeListener(inputsTabListener);

        connectionPanel = new JPanel(new GridLayout(1, 1));
        connectionPanel.setBorder(new EmptyBorder(5, 10, 10, 2));

        connectionSelectionPanel = new JPanel(new GridLayout(1, 1));
        connectionSelectionList = new JList(new DefaultListModel());
        connectionSelectionList.setCellRenderer(new AvailablePluginRenderer());

        paletteListener = new PaletteListener();
        palette.addPalettePanelListener(paletteListener);
        palette.addPalettePanelListener(headUpDisplayPanel);
        connectionSelectionList.addListSelectionListener(paletteListener);

        cardsCenter = new JPanel(new CardLayout());

        latFileTreePanel = new LatFileTreePanel();

        emptyLatPanel = new JPanel();
        emptyCinema = new JPanel();
        emptyTextEditor = new JPanel();
    }

    private JPanel createToolbarPanel() {
        JPanel panel = new JPanel();//new FlowLayout(FlowLayout.LEFT, 20, 0));
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        panel.add(runButton = createComboBoxButton(Resources.createIcon("run24.png"), "Execute the active system.", ACTION_RUN, toolbarListener));
        panel.add(Box.createHorizontalStrut(7));
        panel.add(stopButton = createComboBoxButton(Resources.createIcon("stop24.png"), "Stop active system.", ACTION_STOP, toolbarListener));
        panel.add(Box.createHorizontalStrut(3));

        JPanel stopPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        stopPanel.add(new JLabel("Stop Time"));
        stopTimeField = new LNumberField("-1", 5);
        stopPanel.add(stopTimeField);
        panel.add(stopPanel);
        panel.add(Box.createHorizontalGlue());
        panel.add(indicatorPanel);
        return panel;
    }

    private JComboBoxButton createComboBoxButton(ImageIcon icon, String label, String actionCommand, ActionListener al) {
        JComboBoxButton button = new JComboBoxButton(icon);
        button.setActionCommand(actionCommand);
        button.addActionListener(al);
        return button;
    }

    private JMenuBar createMenu() {
        menu = new JMenuBar();

//  File Menu
        JMenu fileMenu = new JMenu("File");
        fileMenu.add(createMenuItem("Load", ACTION_LOAD));
        fileMenu.add(createMenuItem("Save", ACTION_SAVE));
        fileMenu.add(createMenuItem("Exit", ACTION_EXIT));

//  Edit Menu
        JMenu editMenu = new JMenu("Edit");
        editMenu.add(createMenuItem("Find Plugin", ACTION_FIND_PLUGINS));
        editMenu.add(createMenuItem("Preferences", ACTION_PREFERENCES));

//  Palette Menu
        JMenu paletteMenu = new JMenu("Palette");
        JMenu zoomMenu = new JMenu("Zoom Level");
        zoomMenu.add(createMenuItem("25%", ACTION_PALETTE_ZOOM));
        zoomMenu.add(createMenuItem("50%", ACTION_PALETTE_ZOOM));
        zoomMenu.add(createMenuItem("75%", ACTION_PALETTE_ZOOM));
        zoomMenu.add(createMenuItem("100%", ACTION_PALETTE_ZOOM));
        zoomMenu.add(createMenuItem("125%", ACTION_PALETTE_ZOOM));
        zoomMenu.add(createMenuItem("150%", ACTION_PALETTE_ZOOM));
        paletteMenu.add(zoomMenu);
        paletteMenu.add(createMenuItem("Expand Width", ACTION_EXPAND_X));
        paletteMenu.add(createMenuItem("Expand Height", ACTION_EXPAND_Y));
        paletteMenu.add(createMenuItem("Shrink Width", ACTION_CONTRACT_X));
        paletteMenu.add(createMenuItem("Shrink Height", ACTION_CONTRACT_Y));

//  Tools Menu
        JMenu toolsMenu = new JMenu("Tools");
        toolsMenu.add(createMenuItem("Calculator", ACTION_CALCULATOR));

//  Perspectives Menu
        JMenu perspectivesMenu = new JMenu("Perspective");
        perspectivesMenu.add(createMenuItem("Palette", ACTION_PERSPECTIVE_LATIZ));
        perspectivesMenu.add(createMenuItem("Head-up Display", ACTION_PERSPECTIVE_HUD));
        perspectivesMenu.add(createMenuItem("LatFile Explorer", ACTION_PERSPECTIVE_LATFILE));
        perspectivesMenu.add(createMenuItem("Cinema", ACTION_PERSPECTIVE_CINEMA));
        perspectivesMenu.add(createMenuItem("Text Editor", ACTION_PERSPECTIVE_TEXT_EDITOR));

//  Options Menu    
        JMenu optionsMenu = new JMenu("Options");
        optionsMenu.add(createMenuItem("Plugin Manager", ACTION_PLUGIN_MANAGER));
        optionsMenu.add(createMenuItem("Plugin Wizard", ACTION_PLUGIN_WIZARD));

//  Help Menu
        JMenu helpMenu = new JMenu("Help");
        helpMenu.add(createMenuItem("Find and Install Updates...", ACTION_UPDATES));
        helpMenu.add(createMenuItem("About...", ACTION_ABOUT));

        menu.add(fileMenu);
        menu.add(editMenu);
        menu.add(paletteMenu);
        menu.add(toolsMenu);
        menu.add(optionsMenu);
        menu.add(perspectivesMenu);
        menu.add(helpMenu);
        return menu;
    }

    private JPanel createConnectionSelectionPanel() {
        JScrollPane scroller = new JScrollPane(connectionSelectionList);
        scroller.setPreferredSize(new Dimension(210, 100));
        connectionSelectionPanel.add(scroller);

        JPanel northPanel = new JPanel(new BorderLayout());
        northPanel.add(new HeaderPanel("Available Connections", ""), BorderLayout.NORTH);
        northPanel.add(new JSeparator(), BorderLayout.CENTER);

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(northPanel, BorderLayout.NORTH);
        panel.add(scroller, BorderLayout.CENTER);
        panel.setBorder(new CompoundBorder(new EmptyBorder(2, 0, 2, 2), new LineBorderX()));
        return panel;
    }

    private Container createContentPane() {
        JPanel c = new JPanel(new BorderLayout());
        c.add(createConnectionToolbar(), BorderLayout.NORTH);
        c.add(connectionPanel, BorderLayout.CENTER);
        c.add(createConnectionSelectionPanel(), BorderLayout.EAST);

        propertiesSplitter = new JSplitPane();
        propertiesSplitter.setLeftComponent(inputTabs);
        propertiesSplitter.setRightComponent(c);

        centerSplitter = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        centerSplitter.setTopComponent(createPalettePanel());
        centerSplitter.setBottomComponent(propertiesSplitter);

        cardsCenter.add(centerSplitter, "LATIZ");
        cardsCenter.add(emptyLatPanel, "TRF_EXPLORER");
        cardsCenter.add(emptyCinema, "CINEMA");
        cardsCenter.add(emptyTextEditor, "TEXT_EDITOR");
        cardsCenter.add(headUpDisplayPanel, "HUD");

        fileTabs = new JTabbedPaneX(JTabbedPaneX.TOP, JTabbedPaneX.SCROLL_TAB_LAYOUT);
        fileTabs.addTab("FileSystem", fileExplorerPanel = new FileExplorerPanel());
        fileTabs.addTab("Recorded Outputs", latFileTreePanel);

        splitterConsole = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitterConsole.setTopComponent(fileTabs);
        JTabbedPane consoleTabs = new JTabbedPane();
        consoleTabs.addTab("Console", Resources.createIcon("terminal16.png"), new ConsolePanel());
        consoleTabs.addTab("BeanShell", Resources.createIcon("beany16.png"), new BeanShellPanel());
        splitterConsole.setBottomComponent(consoleTabs);

        splitterFileTree = new JSplitPane();
        splitterFileTree.setLeftComponent(splitterConsole);
        splitterFileTree.setRightComponent(cardsCenter);
        splitterFileTree.setOneTouchExpandable(true);
        splitterFileTree.setDividerSize(8);

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(splitterFileTree);
        return panel;
    }

    private Component createConnectionToolbar() {
        JPanel toolbarPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        toolbarPanel.add(createComboBoxButton(Resources.createIcon("go-first.png"), "Go to starting plugin", ACTION_GOTO_FIRST, menuListener));
        toolbarPanel.add(createComboBoxButton(Resources.createIcon("go-previous.png"), "Select parent plugin", ACTION_GOTO_PREVIOUS, menuListener));
        toolbarPanel.add(createComboBoxButton(Resources.createIcon("go-next.png"), "Select parent plugin", ACTION_GOTO_NEXT, menuListener));

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(toolbarPanel, BorderLayout.CENTER);
        panel.add(new JSeparator(), BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createPalettePanel() {
        paletteSplitter = new JSplitPane();
        paletteSplitter.setLeftComponent(palettePanel);
        paletteSplitter.setRightComponent(createEastPaletteTabs());

        JPanel panel = new JPanel(new GridLayout(1, 1));
        panel.add(paletteSplitter);
        return panel;
    }

    private JTabbedPane createEastPaletteTabs() {
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Plugins", pluginTreePanel = new PluginsTreePanel(PluginManager.getInstanceOf().getPlugins().keySet()));
        tabs.addTab("Workspace", new WorkspaceTreePanel());
        tabs.addTab("Globals", globalsPanel = new GlobalParametersPanel());
        return tabs;
    }

    private JMenuItem createMenuItem(String label, String actionCommand) {
        JMenuItem item = new JMenuItem(label);
        item.addActionListener(menuListener);
        item.setActionCommand(actionCommand);
        return item;
    }

    public ArrayList<Connector> getConnectors() {
        return palette.getConnectors();
    }

    private void recallProperties() {
        PropertiesManager props = PropertiesManager.getInstanceOf();

        int width = Integer.parseInt(props.getProperty(PropertiesManager.FRAME_WIDTH));
        int height = Integer.parseInt(props.getProperty(PropertiesManager.FRAME_HEIGHT));

        int locX = Integer.parseInt(props.getProperty(PropertiesManager.FRAME_LOC_X));
        int locY = Integer.parseInt(props.getProperty(PropertiesManager.FRAME_LOC_Y));

        if (Boolean.parseBoolean(props.getProperty(PropertiesManager.FRAME_IS_MAXIMIZED)) || locX < -10000 || locY < -10000) {
            setExtendedState(Frame.MAXIMIZED_BOTH);
            locX = locY = 0;
        } else {
            setSize(width, height);
        }
        setLocation(locX, locY);

        int paletteWidth = Integer.parseInt(props.getProperty(PropertiesManager.FRAME_PALETTE_WIDTH));
        int paletteHeight = Integer.parseInt(props.getProperty(PropertiesManager.FRAME_PALETTE_HEIGHT));
        paletteSplitter.setDividerLocation(paletteWidth);
        centerSplitter.setDividerLocation(paletteHeight);

        int ftWidth = Integer.parseInt(props.getProperty(PropertiesManager.FRAME_FILETREE_WIDTH));
        int ftHeight = Integer.parseInt(props.getProperty(PropertiesManager.FRAME_FILETREE_HEIGHT));
        splitterFileTree.setDividerLocation(ftWidth);
        splitterConsole.setDividerLocation(ftHeight);

        int propsWidth = Integer.parseInt(props.getProperty(PropertiesManager.FRAME_PROPS_WIDTH));
        propertiesSplitter.setDividerLocation(propsWidth);
    }

    public void saveSettings() {
        SortedProperties props = PropertiesManager.getInstanceOf();
        props.setProperty(PropertiesManager.FRAME_IS_MAXIMIZED, String.valueOf(getExtendedState() == JFrame.MAXIMIZED_BOTH));
        if (getWidth() * getHeight() != 0) {
            props.setProperty(PropertiesManager.FRAME_WIDTH, String.valueOf(getWidth()));
            props.setProperty(PropertiesManager.FRAME_HEIGHT, String.valueOf(getHeight()));
        }
        props.setProperty(PropertiesManager.FRAME_LOC_X, String.valueOf(getLocationOnScreen().x));
        props.setProperty(PropertiesManager.FRAME_LOC_Y, String.valueOf(getLocationOnScreen().y));
        props.setProperty(PropertiesManager.FRAME_PALETTE_WIDTH, String.valueOf(paletteSplitter.getDividerLocation()));
        props.setProperty(PropertiesManager.FRAME_PALETTE_HEIGHT, String.valueOf(centerSplitter.getDividerLocation()));
        props.setProperty(PropertiesManager.FRAME_FILETREE_HEIGHT, String.valueOf(splitterConsole.getDividerLocation()));
        props.setProperty(PropertiesManager.FRAME_FILETREE_WIDTH, String.valueOf(splitterFileTree.getDividerLocation()));
        props.setProperty(PropertiesManager.FRAME_PROPS_WIDTH, String.valueOf(propertiesSplitter.getDividerLocation()));
        PropertiesManager.getInstanceOf().saveProperties();
        System.exit(0);
    }

    public Palette getPalette() {
        return palette;
    }

    public JButton getRunButton() {
        return runButton;
    }

    public IndicatorIconPanel getIndicatorPanel() {
        return indicatorPanel;
    }

    public LatFileTreePanel getLatFileTreePanel() {
        return latFileTreePanel;
    }

    public final JButton getStopButton() {
        return stopButton;
    }

    public final LNumberField getStopTimeField() {
        return stopTimeField;
    }

    public void loadWorkspace(File file, Element workspaceRoot) {
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        pluginIOConnectionSet.clear();
        palette.clearPalette();
        try {
            String zoomString = workspaceRoot.getAttributeValue("zoom");
            if (zoomString == null || zoomString.equalsIgnoreCase("")) {
                palette.setCurrentScaleFactor(1f);
            } else {
                palette.setCurrentScaleFactor(Float.parseFloat(workspaceRoot.getAttributeValue("zoom")));
            }

            String stopTime = workspaceRoot.getChildText("stopTime");
            if (stopTime == null || stopTime.equalsIgnoreCase("")) {
                stopTimeField.setText("0");
            } else {
                stopTimeField.setText(stopTime);
            }

            Element e;
            String name, id;
            Point loc;
            AbstractPlugin p;
            for (Object o : workspaceRoot.getChild("plugins").getChildren("plugin")) {
                e = (Element) o;
                id = e.getAttributeValue("id");
                name = e.getAttributeValue("name");
                loc = new Point(Integer.parseInt(e.getAttributeValue("xLoc")), Integer.parseInt(e.getAttributeValue("yLoc")));
                p = palette.instantiatePlugin(id, name, loc);
                p.loadSavedWorkspaceParameters(e.getChild("parameters"));
            }

            Element latFileElement = workspaceRoot.getChild("latizDataFile");
            if (latFileElement == null) {
                String defaultPath = PropertiesManager.getInstanceOf().getProperty(PropertiesManager.GENERAL_LAT_PATH);
                latFileTreePanel.setFileName(defaultPath + File.separator + "latizOutput.h5");
            } else {
                latFileTreePanel.setFileName(latFileElement.getAttributeValue("name"));
                latFileTreePanel.getCheckboxSaveLatFile().setSelected(Boolean.parseBoolean(latFileElement.getAttributeValue("use")));
                List pluginElements = latFileElement.getChildren("plugin");
                if (pluginElements != null) {
                    HashMap<AbstractPlugin, ArrayList<LatFileTreeDataNode>> selectedDataNodesMap = new HashMap<AbstractPlugin, ArrayList<LatFileTreeDataNode>>();
                    ArrayList<LatFileTreeDataNode> dataNodes;
                    List dataItemElements = null;
                    Element pluginElement;
                    String pluginName;
                    for (int i = 0; i < pluginElements.size(); i++) {
                        dataNodes = new ArrayList<LatFileTreeDataNode>();
                        pluginElement = (Element) pluginElements.get(i);
                        pluginName = pluginElement.getAttributeValue("name");
                        dataItemElements = pluginElement.getChildren("dataItem");
                        for (int j = 0; j < dataItemElements.size(); j++) {
                            Element thisDataElement = (Element) dataItemElements.get(j);
                            String dataName = thisDataElement.getAttributeValue("name");

                            LatFileTreeDataNode thisDataNode = new LatFileTreeDataNode(dataName, true);
                            try {
                                thisDataNode.setUserDefined(Boolean.parseBoolean(thisDataElement.getAttributeValue("userDefined")));
                                thisDataNode.setBeginTime(new Double(thisDataElement.getAttributeValue("beginTime")));
                                thisDataNode.setEndTime(new Double(thisDataElement.getAttributeValue("endTime")));
                                thisDataNode.setPeriod(new Double(thisDataElement.getAttributeValue("period")));
                                thisDataNode.setMaxFrameCount(new Integer(thisDataElement.getAttributeValue("maxFrames")));
                            } catch (Exception ex) {
                                thisDataNode.setUserDefined(false);
                                thisDataNode.setBeginTime(0);
                                thisDataNode.setEndTime(Double.POSITIVE_INFINITY);
                                thisDataNode.setPeriod(0);
                                thisDataNode.setMaxFrameCount(-1);
                            }

                            dataNodes.add(thisDataNode);
                        }
                        for (AbstractPlugin plugin : palette.getPlugins()) {
                            if (plugin.getName().equalsIgnoreCase(pluginName)) {
                                selectedDataNodesMap.put(plugin, dataNodes);
                            }
                        }
                    }
                    latFileTreePanel.setSelectedDataNodeMap(selectedDataNodesMap);
                }
            }

            String p1, p2, io1, io2;
            for (Object o : workspaceRoot.getChild("connections").getChildren("connection")) {
                e = (Element) o;
                p1 = e.getAttributeValue("p1");
                p2 = e.getAttributeValue("p2");
                Connector c = new Connector(LatizSystemUtilities.getPluginFromList(palette.getPlugins(), p1), LatizSystemUtilities.getPluginFromList(palette.getPlugins(), p2));
                palette.getPluginToPluginConnectorMap().put(p1 + ">" + p2, c);
                palette.add(c);
                palette.setSelectedPlugin(LatizSystemUtilities.getPluginFromList(palette.getPlugins(), p1));
                palette.getPluginOutgoingConnectorMaps().get(palette.getSelectedPlugin().getName()).put(c.getName(), c);
                LatizSystemUtilities.connectionMade(palette.getLatizSystems(), c);
                palette.notifyConnectionMade(c);

                ArrayList<String> ioConnections = new ArrayList<String>();
                for (Object io : e.getChildren("ioConnection")) {
                    e = (Element) io;
                    io1 = e.getAttributeValue("out");
                    io2 = e.getAttributeValue("in");
                    ioConnections.add(io1 + ">" + io2);
                    pluginIOConnectionSet.add(p1 + "::" + io1 + ">" + p2 + "::" + io2);
                }
                c.setIOconnections(ioConnections);
            }

            GlobalParameterMap globals = GlobalParameterMap.getInstanceOf();
            for (Object o : workspaceRoot.getChild("globalParameters").getChildren("parameter")) {
                e = (Element) o;
                globals.put(e.getAttributeValue("var"), e.getAttributeValue("value"));
            }

            palette.repaintConnections();
            palette.setSelectedPlugin(LatizSystemUtilities.getPluginsWithNoParents(palette.getPlugins()).get(0));
            palette.notifyMousePressed();

            updateIOconnections();
            latFileTreePanel.reloadTree(palette.getLatizSystems(), palette.getPluginOutgoingConnectorMaps());
            globalsPanel.setWorkspaceFile(file);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            setCursor(Cursor.getDefaultCursor());
        }
    }

    private void updateIOconnections() {
        resetIOconnections();
        String inputPlugin, thisKey;
        ArrayList<InputConnector> inputConnectorMap;
        for (String p2pName : palette.getPluginToPluginConnectorMap().keySet()) {
            inputPlugin = p2pName.split(">")[1];
            for (String ioName : pluginIOConnectionSet) {
                if (ioName.split(">")[1].split("::")[0].equalsIgnoreCase(inputPlugin)) {
                    inputConnectorMap = palette.getPluginToPluginConnectorMap().get(p2pName).getConnectionPanel().getInputConnectionMap();
                    thisKey = ioName.split(">")[1].split("::")[1];
                    for (InputConnector ic : inputConnectorMap) {
                        if (ic.getInputKey().equalsIgnoreCase(thisKey)) {
                            ic.setHasConnection(true);
                            ic.setParentName(ioName.split(">")[0].split("::")[0]);
                        }
                    }
                }
            }
        }
    }

    private void resetIOconnections() {
        for (Connector c : palette.getPluginToPluginConnectorMap().values()) {
            for (InputConnector ic : c.getConnectionPanel().getInputConnectionMap()) {
                ic.setHasConnection(false);
                ic.setParentName(null);
            }
        }
    }

    /**
     *
     * @author Dr. Richard St. John
     * @version $Revision$, $Date$
     */
    private class MenuListener implements ActionListener, PreferencesListener {

        public void actionPerformed(ActionEvent e) {
            String command = e.getActionCommand();
            if (command.equalsIgnoreCase(ACTION_UPDATES)) {
                actionFindAndInstallUpdates();
            } else if (command.equalsIgnoreCase(ACTION_ABOUT)) {
                actionAboutMenu();
            } else if (command.equalsIgnoreCase(ACTION_PLUGIN_WIZARD)) {
                actionPluginWizard();
            } else if (command.equalsIgnoreCase(ACTION_PLUGIN_MANAGER)) {
                actionPluginManager();
            } else if (command.equalsIgnoreCase(ACTION_PREFERENCES)) {
                actionShowPreferences();
            } else if (command.equalsIgnoreCase(ACTION_FIND_PLUGINS)) {
                FindPluginDialog dialog = new FindPluginDialog();
            } else if (command.equalsIgnoreCase(ACTION_SAVE)) {
                actionSaveWorkspace();
            } else if (command.equalsIgnoreCase(ACTION_LOAD)) {
                actionLoadWorkspace();
            } else if (command.equalsIgnoreCase(ACTION_EXIT)) {
                actionClose();
            } else if (command.equalsIgnoreCase(ACTION_CALCULATOR)) {
                new Calculator().setVisible(true);
            } else if (command.equalsIgnoreCase(GuiMaker.ACTION_PALETTE_ZOOM)) {
                actionZoom(((JMenuItem) e.getSource()).getText());
            } else if (command.equalsIgnoreCase(GuiMaker.ACTION_EXPAND_X)) {
                actionExpand(0, 100);
            } else if (command.equalsIgnoreCase(GuiMaker.ACTION_EXPAND_Y)) {
                actionExpand(1, 100);
            } else if (command.equalsIgnoreCase(GuiMaker.ACTION_CONTRACT_X)) {
                actionExpand(0, -100);
            } else if (command.equalsIgnoreCase(GuiMaker.ACTION_CONTRACT_Y)) {
                actionExpand(1, -100);
            } else if (command.equalsIgnoreCase(ACTION_GOTO_FIRST)) {
                actionGotoFirstPlugin();
            } else if (command.equalsIgnoreCase(ACTION_GOTO_PREVIOUS)) {
                actionGotoPreviousPlugin();
            } else if (command.equalsIgnoreCase(ACTION_GOTO_NEXT)) {
                actionGotoNextPlugin();
            } else if (command.equalsIgnoreCase(ACTION_PLUGIN_CHOOSER)) {
                actionGotoPlugin(((JMenuItem) e.getSource()).getText());
            } else if (command.equalsIgnoreCase(ACTION_PERSPECTIVE_LATIZ)) {
                showLatizCard();
            } else if (command.equalsIgnoreCase(ACTION_PERSPECTIVE_LATFILE)) {
                showLatFileCard();
            } else if (command.equalsIgnoreCase(ACTION_PERSPECTIVE_CINEMA)) {
                showCinemaCard();
            } else if (command.equalsIgnoreCase(ACTION_PERSPECTIVE_TEXT_EDITOR)) {
                showTextEditorCard();
            } else if (command.equalsIgnoreCase(ACTION_PERSPECTIVE_HUD)) {
                showHUDCard();
            }
        }

        private void showLatizCard() {
            unregisterMenus();
            ((CardLayout) cardsCenter.getLayout()).show(cardsCenter, "LATIZ");
            fileTabs.removeAll();
            fileTabs.addTab("FileSystem", fileExplorerPanel);
            fileTabs.addTab("Recorded Outputs", latFileTreePanel);
        }

        private void showCinemaCard() {
            unregisterMenus();
            CardLayout cl = (CardLayout) cardsCenter.getLayout();
            if (emptyCinema == null) {
                cl.show(cardsCenter, "CINEMA");
                return;
            }
            cl.removeLayoutComponent(emptyCinema);
            emptyCinema = null;
            cardsCenter.add(new CinemaPanel(), "CINEMA");
            cl.show(cardsCenter, "CINEMA");
            fileTabs.removeAll();
            fileTabs.addTab("FileSystem", fileExplorerPanel);
        }

        private void showLatFileCard() {
            unregisterMenus();
            CardLayout cl = (CardLayout) cardsCenter.getLayout();
            if (latFilePlotPanel != null) {
                menu.add(latFilePlotPanel.getMenu());
                cl.show(cardsCenter, "LAT EXPLORER");
                return;
            }
            cl.removeLayoutComponent(emptyLatPanel);
            emptyLatPanel = null;
            cardsCenter.add(latFilePlotPanel = new LatFilePlotPanel(menu), "LAT EXPLORER");
            menu.add(latFilePlotPanel.getMenu());
            cl.show(cardsCenter, "LAT EXPLORER");
            fileTabs.removeAll();
            fileTabs.addTab("FileSystem", fileExplorerPanel);
        }

        private void showTextEditorCard() {
            unregisterMenus();
            CardLayout cl = (CardLayout) cardsCenter.getLayout();
            if (emptyTextEditor == null) {
                cl.show(cardsCenter, "TEXT_EDITOR");
                return;
            }
            cl.removeLayoutComponent(emptyTextEditor);
            emptyTextEditor = null;
            cardsCenter.add(new JEditTabsPanel(), "TEXT_EDITOR");
            cl.show(cardsCenter, "TEXT_EDITOR");
            fileTabs.removeAll();
            fileTabs.addTab("FileSystem", fileExplorerPanel);
        }

        private void showHUDCard() {
            unregisterMenus();
            CardLayout cl = (CardLayout) cardsCenter.getLayout();
            cl.show(cardsCenter, "HUD");
            fileTabs.removeAll();
            fileTabs.addTab("Head-up Display", headUpDisplayPanel.getTopLeftComponent());
        }

        private void unregisterMenus() {
            if (latFilePlotPanel != null) {
                menu.remove(latFilePlotPanel.getMenu());
            }
        }

        private void actionZoom(String text) {
            palette.setCurrentScaleFactor(Float.parseFloat(text.replace("%", "")) / 100f);
            palette.repaint();
        }

        private void actionPluginWizard() {
            PluginWizard wizard = new PluginWizard();
            wizard.setLocationRelativeTo(null);
            wizard.setVisible(true);
        }

        private void actionPluginManager() {
            PluginDialog pluginDialog = new PluginDialog();
            pluginTreePanel.setPluginKeySet(PluginManager.getInstanceOf().getPlugins().keySet());
        }

        private void actionGotoPlugin(String name) {
            AbstractPlugin p = LatizSystemUtilities.getPluginFromList(palette.getPlugins(), name);
            if (p == null) {
                return;
            }
            palette.setSelectedPlugin(p);
            paletteListener.mousePressed();
        }

        private void actionGotoPreviousPlugin() {
            //TODO need to update this to allow a selection
            AbstractPlugin parent = palette.getSelectedPlugin().getParentPluginInterfaces().get(0).getParentPlugin();
            if (parent == null) {
                return;
            }
            palette.setSelectedPlugin(parent);
            paletteListener.mousePressed();
        }

        private void actionGotoNextPlugin() {
            HashMap<String, Connector> currentOutgoingConnectorMap = palette.getPluginOutgoingConnectorMaps().get(palette.getSelectedPlugin().getName());
            if (currentOutgoingConnectorMap.isEmpty()) {
                return;
            } else if (currentOutgoingConnectorMap.size() > 1) {
                JPopupMenu menu = new JPopupMenu("Path Chooser");
                Point loc = GuiMaker.this.getMousePosition();
                String[] keySplit;
                for (String key : currentOutgoingConnectorMap.keySet()) {
                    keySplit = key.split(">");
                    JMenuItem thisItem = new JMenuItem(keySplit[1], Resources.createIcon("plugin22.png"));
                    thisItem.setActionCommand(ACTION_PLUGIN_CHOOSER);
                    thisItem.addActionListener(this);
                    menu.add(thisItem);
                }
                menu.show(GuiMaker.this, loc.x, loc.y);
                return;
            } else {
                String[] keySplit;
                for (String key : currentOutgoingConnectorMap.keySet()) {
                    keySplit = key.split(">");
                    palette.setSelectedPlugin(LatizSystemUtilities.getPluginFromList(palette.getPlugins(), keySplit[1]));
                    paletteListener.mousePressed();
                }
            }
        }

        private void actionGotoFirstPlugin() {
            ArrayList<AbstractPlugin> firstPlugins = LatizSystemUtilities.getPluginsWithNoParents(palette.getPlugins());
            if (firstPlugins.isEmpty()) {
                return;
            }
            if (firstPlugins.size() == 1) {
                palette.setSelectedPlugin(firstPlugins.get(0));
                paletteListener.mousePressed();
                return;
            }
            JPopupMenu menu = new JPopupMenu("First Plugin Chooser");
            Point loc = GuiMaker.this.getMousePosition();
            for (AbstractPlugin p : firstPlugins) {
                JMenuItem thisItem = new JMenuItem(p.getName(), Resources.createIcon("plugin22.png"));
                thisItem.setActionCommand(ACTION_PLUGIN_CHOOSER);
                thisItem.addActionListener(this);
                menu.add(thisItem);
            }
            menu.show(GuiMaker.this, loc.x, loc.y);
            return;
        }

        private void actionClose() {
            saveSettings();
            System.exit(0);
        }

        private void actionExpand(int flag, int amount) {
            Dimension d = palette.getPreferredSize();
            if (flag == 0) {
                palette.setPreferredSize(new Dimension(d.width + amount, d.height));
            } else {
                palette.setPreferredSize(new Dimension(d.width, d.height + amount));
            }
            palette.revalidate();
        }

        private void actionSaveWorkspace() {
            Element latizRoot = new Element("latizWorkspace");
            paletteListener.paletteSaved(palette.getWorkspaceFile(), latizRoot);
        }

        private void actionLoadWorkspace() {
            FileChooser fileChooser = new FileChooser(currentDirectory);
            int choice = fileChooser.showOpenDialog(GuiMaker.this);
            if (choice == JFileChooser.CANCEL_OPTION) {
                return;
            }
            currentDirectory = fileChooser.getSelectedFile().getParent();
            Element latizRoot;
            try {
                latizRoot = XmlFile.readRootElement(fileChooser.getSelectedFile());
                loadWorkspace(fileChooser.getSelectedFile(), latizRoot);
            } catch (JDOMException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void preferencesUpdated() {
            PropertiesManager props = PropertiesManager.getInstanceOf();
            Color incomingBackground = Color.decode(props.getProperty(PropertiesManager.COLOR_CONNECTION_INCOMING_BACKGROUND));
            Color incomingForeground = Color.decode(props.getProperty(PropertiesManager.COLOR_CONNECTION_INCOMING_FOREGROUND));
            Color outgoingBackground = Color.decode(props.getProperty(PropertiesManager.COLOR_CONNECTION_OUTGOING_BACKGROUND));
            Color outgoingForeground = Color.decode(props.getProperty(PropertiesManager.COLOR_CONNECTION_OUTGOING_FOREGROUND));
            Color connectorColor = Color.decode(props.getProperty(PropertiesManager.CONNECTOR_LINE_COLOR));

            float lineWidth = Float.parseFloat(props.getProperty(PropertiesManager.CONNECTOR_LINE_WEIGHT));
            for (Connector c : palette.getPluginToPluginConnectorMap().values()) {
                c.setLineWidth(lineWidth);
                c.setConnectorColor(connectorColor);
                c.getConnectionPanel().setIncomingConnectorsColors(incomingForeground, incomingBackground);
                c.getConnectionPanel().setOutgoingConnectorsColors(outgoingForeground, outgoingBackground);
                c.repaint();
            }

            Color paletteColor = Color.decode(props.getProperty(PropertiesManager.PALETTE_BACKGROUND));
            Color gridColor = Color.decode(props.getProperty(PropertiesManager.PALETTE_LINE_COLOR));
            int gridSpacing = Integer.parseInt(props.getProperty(PropertiesManager.PALETTE_LINE_SPACING));
            int gridType = Integer.parseInt(props.getProperty(PropertiesManager.PALETTE_GRID_TYPE));
            palette.setPaletteColor(paletteColor);
            palette.setBackground(paletteColor);
            palette.setGridColor(gridColor);
            palette.setGridLineSpacing(gridSpacing);
            palette.setGridType(gridType);
            palette.repaint();

            String latPath = props.getProperty(PropertiesManager.GENERAL_LAT_PATH);
            String currentFilename = new File(latFileTreePanel.getFileName()).getName();
            latFileTreePanel.getFieldFilename().setText(latPath + File.separator + currentFilename);
        }

        private void actionShowPreferences() {
            PreferencesDialog preferencesDialog = new PreferencesDialog();
            preferencesDialog.addPreferencesListener(this);
        }

        private void actionFindAndInstallUpdates() {
            PropertiesManager.getInstanceOf().saveProperties();
            String repoDir = PropertiesManager.getInstanceOf().getProperty(PropertiesManager.REPO_DEFAULT);
            String repoApps = repoDir + "/apps";
            String repoLibs = repoDir + "/libs";

            String installDir = System.getProperty("user.dir");
            String installLib = installDir + "\\lib";

            HashMap<String, String> filesToCopy = new HashMap<String, String>();
            filesToCopy.put(repoApps + "/latiz.jar", installDir + "\\latiz.jar");
            filesToCopy.put(repoLibs + "/AandRLibrary.jar", installLib + "\\AandRLibrary.jar");
            filesToCopy.put(repoLibs + "/bsh.jar", installLib + "\\bsh.jar");
            filesToCopy.put(repoLibs + "/jcommon.jar", installLib + "\\jcommon.jar");
            filesToCopy.put(repoLibs + "/jdic.dll", installLib + "\\jdic.dll");
            filesToCopy.put(repoLibs + "/jdic.jar", installLib + "\\jdic.jar");
            filesToCopy.put(repoLibs + "/jdom.jar", installLib + "\\jdom.jar");
            filesToCopy.put(repoLibs + "/jep.jar", installLib + "\\jep.jar");
            filesToCopy.put(repoLibs + "/jfreechart.jar", installLib + "\\jfreechart.jar");
            filesToCopy.put(repoLibs + "/hdf-java/jhdf.jar", installLib + "\\jhdf.jar");
            filesToCopy.put(repoLibs + "/hdf-java/jhdfobj.jar", installLib + "\\jhdfobj.jar");
            filesToCopy.put(repoLibs + "/hdf-java/jhdf5.jar", installLib + "\\jhdf5.jar");
            filesToCopy.put(repoLibs + "/hdf-java/jhdf5obj.jar", installLib + "\\jhdf5obj.jar");
            filesToCopy.put(repoLibs + "/hdf-java/jhdf5.dll", installLib + "\\jhdf5.dll");
            filesToCopy.put(repoLibs + "/hdf-java/libjhdf5.so", installLib + "\\libjhdf5.so");
            RemoteApplicationUpdateDialog remoteApplicationUpdateDialog = new RemoteApplicationUpdateDialog("latiz.exe", filesToCopy);
        }

        private void actionAboutMenu() {
            glassPane.start();
            final JDialog aboutDialog = new JDialog(GuiMaker.this, "About", true);
            aboutDialog.setBackground(Color.WHITE);

            JButtonX okButton = new JButtonX("OK");
            okButton.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    aboutDialog.dispose();
                }
            });
            JPanel buttonPanel = new JPanel();
            buttonPanel.setOpaque(true);
            buttonPanel.setBackground(Color.WHITE);
            buttonPanel.add(okButton);

            JPanel centerPanel = new JPanel(new BorderLayout());
            centerPanel.setBackground(Color.WHITE);
            centerPanel.add(new JLabel(Resources.createIcon("credits.jpg")), BorderLayout.CENTER);
            centerPanel.add(buttonPanel, BorderLayout.SOUTH);

            JPanel panel = new JPanel(new BorderLayout(10, 10));
            panel.setBackground(Color.WHITE);
            panel.add(new JLabel(Resources.createIcon("logo.jpg")), BorderLayout.WEST);
            panel.add(centerPanel, BorderLayout.CENTER);

            aboutDialog.setContentPane(panel);
            aboutDialog.pack();
            GuiHelper.centerRelativeToParent(GuiMaker.this, aboutDialog, 0.2f, 0.1f);
            aboutDialog.setVisible(true);
            glassPane.stop();
        }
    }

    /**
     *
     * @author Dr. Richard St. John
     * @version $Revision$, $Date$
     */
    private class PaletteListener extends PaletteAdapter implements ListSelectionListener, ConnectionPanelListener {

        @Override
        public void mousePressed() {
            connectionPanel.removeAll();
            connectionPanel.setLayout(new GridLayout(1, 1));
            if (palette.getSelectedConnectionPanelMap().size() == 0) {
                return;
            }
            DefaultListModel model = (DefaultListModel) connectionSelectionList.getModel();
            model.clear();
            for (String connectionPanelKey : palette.getSelectedConnectionPanelMap().keySet()) {
                if (!connectionPanelKey.contains(">")) {
                    continue;
                }
                model.addElement(connectionPanelKey.split(">")[1]);
            }
            connectionPanel.add(palette.getSelectedConnectionPanelMap().firstEntry().getValue());
            connectionSelectionList.setSelectedIndex(0);
            connectionPanel.revalidate();
            connectionPanel.repaint();
            palette.revalidate();
            palette.repaint();
            setSelectedTabIndex(palette.getSelectedPlugin());
        }

        private void setSelectedTabIndex(AbstractPlugin p) {
            int index = inputTabs.indexOfTab(p.getName());
            if (index == -1) {
                return;
            }
            inputTabs.setSelectedIndex(index);
        }

        private void firePluginRelationshipChanged(AbstractPlugin p) {
            connectionPanel.removeAll();
            ArrayList<String> ioToRemove = new ArrayList<String>();
            for (String s : pluginIOConnectionSet) {
                String[] pSplit = s.split(">");
                if (pSplit[0].split("::")[0].equalsIgnoreCase(p.getName()) || pSplit[1].split("::")[0].equalsIgnoreCase(p.getName())) {
                    ioToRemove.add(s);
                }
            }
            pluginIOConnectionSet.removeAll(ioToRemove);
            mousePressed();
            updateIOconnections();
        }

        @Override
        public void connectionWillBeRemoved(Connector c) {
            if (c == null) {
                return;
            }
            resetIOconnections();
        }

        @Override
        public void connectionRemoved(Connector c) {
            if (c == null) {
                return;
            }
            //if(palette.getPluginOutgoingConnectorMaps().get(p.getName()).size()>0) return;
            firePluginRelationshipChanged(c.getOutputSendingPlugin());
            latFileTreePanel.updateAfterLatizSystemChange(palette.getLatizSystems());
        }

        @Override
        public void connectionMade(Connector c) {
            connectionPanel.removeAll();
            connectionPanel.setLayout(new GridLayout(1, 1));
            if (palette.getSelectedConnectionPanelMap().size() == 0) {
                return;
            }
            DefaultListModel model = (DefaultListModel) connectionSelectionList.getModel();
            model.clear();
            for (String connectionPanelKey : palette.getSelectedConnectionPanelMap().keySet()) {
                if (!connectionPanelKey.contains(">")) {
                    continue;
                }
                model.addElement(connectionPanelKey.split(">")[1]);
            }

            for (ConnectionPanel cp : palette.getSelectedConnectionPanelMap().values()) {
                cp.addConnectionPanelListener(this);
            }
            ConnectionPanel thisConnectionPanel = palette.getSelectedConnectionPanelMap().firstEntry().getValue();
            connectionPanel.add(thisConnectionPanel);
            connectionSelectionList.setSelectedIndex(0);
            connectionPanel.revalidate();
            connectionPanel.repaint();
            palette.revalidate();
            palette.repaint();
            latFileTreePanel.updateAfterLatizSystemChange(palette.getLatizSystems());
            updateIOconnections();
        }

        @Override
        public void pluginOutputsChanged(AbstractPlugin plugin) {
            LatizSystem latizSystem = null;
            for (LatizSystem ls : palette.getLatizSystems()) {
                if (ls.contains(plugin)) {
                    latizSystem = ls;
                    break;
                }
            }
            latFileTreePanel.updateAfterPluginOutputsChanged(latizSystem, plugin);
        }

        @Override
        public void pluginNameChanged(String oldName, String newName) {
            inputTabs.setTitleAt(inputTabs.indexOfTab(oldName), newName);
            mousePressed();
            latFileTreePanel.updateAfterPluginNameChange(oldName, newName);
        }

        @Override
        public void pluginDropped(AbstractPlugin p) {
            LatizSystem latizSystem = null;
            for (LatizSystem ls : palette.getLatizSystems()) {
                if (ls.contains(p)) {
                    latizSystem = ls;
                    break;
                }
            }
            latFileTreePanel.updateAfterPluginAdded(latizSystem, p);
            JComponent panel;
            if (p instanceof DisplayableInterface) {
                p.addPluginChangedListener(headUpDisplayPanel);
            }
            if ((panel = p.createParametersPanel()) == null) {
                return;
            }
            inputTabs.addTab(p.getName(), panel);
            p.addPluginChangedListener(this);
        }

        @Override
        public void pluginRemoved(AbstractPlugin p) {
            firePluginRelationshipChanged(p);
            latFileTreePanel.updateAfterLatizSystemChange(palette.getLatizSystems());
            int index = inputTabs.indexOfTab(p.getName());
            if (index != -1) {
                inputTabs.remove(index);
            }
        }

        @Override
        public void pluginWillBeRemoved(AbstractPlugin p) {
            resetIOconnections();
        }

        @Override
        public void paletteLoaded(File file, Element latizRoot) {
            loadWorkspace(file, latizRoot);
            latFileTreePanel.updateSelectedDataNodes();
            latFileTreePanel.setAllExpanded(true);
        }

        @Override
        public void paletteSaved(File file, Element latizRoot) {
            Element pluginsElement = new Element("plugins");
            Element thisPluginElement;
            Point loc;
            for (AbstractPlugin p : palette.getPlugins()) {
                loc = p.getLocation();
                thisPluginElement = new Element("plugin");
                thisPluginElement.setAttribute("id", p.getPluginID());
                thisPluginElement.setAttribute("name", p.getName());
                thisPluginElement.setAttribute("xLoc", String.valueOf(loc.x));
                thisPluginElement.setAttribute("yLoc", String.valueOf(loc.y));
                Element parameters = new Element("parameters");
                Element child = p.createWorkspaceParameters();
                if (child != null) {
                    parameters.addContent(p.createWorkspaceParameters());
                    thisPluginElement.addContent(parameters);
                }
                pluginsElement.addContent(thisPluginElement);
            }

            Element connectionsElement = new Element("connections");
            Element thisConnectionElement, thisIOConnectionElement;
            String[] keySplit, connectionSplit;
            for (Connector c : palette.getPluginToPluginConnectorMap().values()) {
                if (c.getName() == null) {
                    continue;
                }
                keySplit = c.getName().split(">");
                thisConnectionElement = new Element("connection");
                thisConnectionElement.setAttribute("p1", keySplit[0]);
                thisConnectionElement.setAttribute("p2", keySplit[1]);
                for (String connectionKey : c.getConnectionPanel().getConnections()) {
                    connectionSplit = connectionKey.split(">");
                    thisIOConnectionElement = new Element("ioConnection");
                    thisIOConnectionElement.setAttribute("out", connectionSplit[0]);
                    thisIOConnectionElement.setAttribute("in", connectionSplit[1]);
                    thisConnectionElement.addContent(thisIOConnectionElement);
                }
                connectionsElement.addContent(thisConnectionElement);
            }

            Element globalParamsElement = new Element("globalParameters");
            GlobalParameterMap globals = GlobalParameterMap.getInstanceOf();
            Element thisItem;
            for (String key : globals.keySet()) {
                thisItem = new Element("parameter");
                thisItem.setAttribute("var", key);
                thisItem.setAttribute("value", globals.get(key).getValue());
                globalParamsElement.addContent(thisItem);
            }

            Element latFileElement = new Element("latizDataFile");
            Element dataElement;
            latFileElement.setAttribute("name", latFileTreePanel.getFileName());
            latFileElement.setAttribute("use", String.valueOf(latFileTreePanel.isLatFileSaveRequired()));
            HashMap<AbstractPlugin, ArrayList<LatFileTreeDataNode>> selectedLatFileMap = latFileTreePanel.getSelectedLatFileMap();
            if (selectedLatFileMap != null) {
                ArrayList<LatFileTreeDataNode> theseSelectedItems;
                for (AbstractPlugin p : selectedLatFileMap.keySet()) {
                    thisItem = new Element("plugin");
                    thisItem.setAttribute("name", p.getName());
                    theseSelectedItems = selectedLatFileMap.get(p);
                    for (LatFileTreeDataNode dataItem : theseSelectedItems) {
                        dataElement = new Element("dataItem");
                        dataElement.setAttribute("name", dataItem.getDatasetName());
                        dataElement.setAttribute("userDefined", String.valueOf(dataItem.isUserDefined()));
                        dataElement.setAttribute("beginTime", dataItem.getBeginTime().toString());
                        dataElement.setAttribute("endTime", dataItem.getEndTime().toString());
                        dataElement.setAttribute("period", dataItem.getPeriod().toString());
                        dataElement.setAttribute("maxFrames", dataItem.getMaxFrameCount().toString());
                        thisItem.addContent(dataElement);
                    }
                    latFileElement.addContent(thisItem);
                }
            }

            latizRoot.addContent(new Element("stopTime").addContent(stopTimeField.getText()));
            latizRoot.addContent(latFileElement);
            latizRoot.addContent(pluginsElement);
            latizRoot.addContent(connectionsElement);
            latizRoot.addContent(globalParamsElement);
            globalsPanel.setWorkspaceFile(file);
        }

        @Override
        public void paletteCleared() {
            latFileTreePanel.reset();
        }

        public void valueChanged(ListSelectionEvent e) {
            Object selection = connectionSelectionList.getSelectedValue();
            if (selection == null) {
                return;
            }
            String selectedPlugin = palette.getSelectedPlugin().getName();
            String connectedTo = connectionSelectionList.getSelectedValue().toString();
            connectionPanel.removeAll();
            connectionPanel.setLayout(new GridLayout());
            connectionPanel.add(palette.getSelectedConnectionPanelMap().get(selectedPlugin + ">" + connectedTo));
            connectionPanel.revalidate();
            connectionPanel.repaint();
        }

        public void ioConnectionMade(String ioConnection) {
            pluginIOConnectionSet.add(ioConnection);
            updateIOconnections();
        }

        public void ioConnectionRemoved(String ioConnection) {
            pluginIOConnectionSet.remove(ioConnection);
            updateIOconnections();
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
            Border lineBorder = isSelected ? new LineBorderX(Color.GREEN) : new LineBorderX();
            setIcon(Resources.createIcon("connect_no.png"));
            setBorder(new CompoundBorder(new EmptyBorder(2, 2, 2, 2), lineBorder));
            setText(value.toString());
            return this;
        }
    }

    /**
     *
     * @author Dr. Richard St. John
     * @version $Revision$, $Date$
     */
    private class InputsTabListener implements ChangeListener, TabOrderChangedListener {

        public void stateChanged(ChangeEvent e) {
            int index = inputTabs.getSelectedIndex();
            if (index == -1) {
                return;
            }
            String title = ((JTabbedPaneX) e.getSource()).getTitleAt(index);
            palette.setSelectedPlugin(LatizSystemUtilities.getPluginFromList(palette.getPlugins(), title));
            palette.fireMousePressed();
        }

        public void tabOrderChanged(String tabText, int oldIndex, int newIndex) {
            ArrayList<AbstractPlugin> plugins = palette.getPlugins();
            AbstractPlugin thisPlugin = LatizSystemUtilities.getPluginFromList(palette.getPlugins(), tabText);

            boolean success = plugins.remove(thisPlugin);
            if (!success) {
                return;
            }
            plugins.add(newIndex, thisPlugin);
        }
    }
}
