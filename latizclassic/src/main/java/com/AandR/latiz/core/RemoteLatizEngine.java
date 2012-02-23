package com.AandR.latiz.core;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.jdom.Element;

import com.AandR.io.XmlFile;
import com.AandR.latiz.dev.AbstractPlugin;
import com.AandR.latiz.gui.Connector;
import com.AandR.latiz.gui.LatFileTreeDataNode;
import com.AandR.latiz.gui.LatFileTreePanel;
import com.AandR.latiz.gui.PluginKey;

/**
 * @author Dr. Richard St. John
 * @version $Revision$, $Date$
 */
public class RemoteLatizEngine {

    private ArrayList<AbstractPlugin> plugins;
    private HashSet<LatizSystem> latizSystems;
    private HashSet<String> pluginIOConnectionSet;
    private HashMap<String, Connector> pluginToPluginConnectorMap;
    private HashMap<String, HashMap<String, Connector>> pluginOutgoingConnectorMaps;
    private LatFileTreePanel latFileTreePanel;
    private Element workspaceRoot;

    public RemoteLatizEngine(String filename) {
        File workspaceFile = new File(filename);
        if (!workspaceFile.exists()) {
            return;
        }

        initialize();

        registerPlugins();

        workspaceRoot = readWorkspaceFile(workspaceFile);

        instatiatePlugins(workspaceRoot);

        createConnections(workspaceRoot);

        refreshGlobalParameters(workspaceRoot);

        initializeLatDataFile(workspaceRoot);

        actionRun();
    }

    private void initialize() {
        pluginIOConnectionSet = new HashSet<String>();
        pluginToPluginConnectorMap = new HashMap<String, Connector>();
        pluginOutgoingConnectorMaps = new HashMap<String, HashMap<String, Connector>>();
        plugins = new ArrayList<AbstractPlugin>();
        latizSystems = new HashSet<LatizSystem>();
    }

    private void actionRun() {
        SystemProcessStarter s = new SystemProcessStarter();
        for (LatizSystem l : latizSystems) {
            s.setLatizSystem(l);
            s.runProcess();
            break;
        }

    }

    private void createConnections(Element workspaceRoot) {
        AbstractPlugin p;
        Element e;
        String p1, p2, io1, io2;
        for (Object o : workspaceRoot.getChild("connections").getChildren("connection")) {
            e = (Element) o;
            p1 = e.getAttributeValue("p1");
            p2 = e.getAttributeValue("p2");
            Connector c = new Connector(LatizSystemUtilities.getPluginFromList(plugins, p1), LatizSystemUtilities.getPluginFromList(plugins, p2));
            pluginToPluginConnectorMap.put(p1 + ">" + p2, c);

            p = LatizSystemUtilities.getPluginFromList(plugins, p1);
            pluginOutgoingConnectorMaps.get(p.getName()).put(c.getName(), c);
            LatizSystemUtilities.connectionMade(latizSystems, c);


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
    }

    private void instatiatePlugins(Element workspaceRoot) {
        Element e;
        String id, name;
        AbstractPlugin p;
        for (Object o : workspaceRoot.getChild("plugins").getChildren("plugin")) {
            e = (Element) o;
            id = e.getAttributeValue("id");
            name = e.getAttributeValue("name");
            try {
                p = instantiatePlugin(id, name);
                p.createParametersPanel();
                p.loadSavedWorkspaceParameters(e.getChild("parameters"));
                p.initializeInputs();
                p.initializeOutputs();
            } catch (InstantiationException e1) {
                e1.printStackTrace();
            } catch (IllegalAccessException e1) {
                e1.printStackTrace();
            }
        }
    }

    private void initializeLatDataFile(Element workspaceRoot) {
        latFileTreePanel = new LatFileTreePanel();
        Element latFileElement = workspaceRoot.getChild("latizDataFile");
        if (latFileElement == null) {
            String defaultPath = PropertiesManager.getInstanceOf().getProperty(PropertiesManager.GENERAL_LAT_PATH);
            latFileTreePanel.setFileName(defaultPath + File.separator + "latizOutput.h5");
        } else {
            String latFileName = latFileElement.getAttributeValue("name");
            latFileTreePanel.setFileName(latFileName);

            boolean selected = Boolean.parseBoolean(latFileElement.getAttributeValue("use"));
            latFileTreePanel.getCheckboxSaveLatFile().setSelected(selected);

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
                    for (AbstractPlugin plugin : plugins) {
                        if (plugin.getName().equalsIgnoreCase(pluginName)) {
                            selectedDataNodesMap.put(plugin, dataNodes);
                        }
                    }
                }
                latFileTreePanel.setSelectedDataNodeMap(selectedDataNodesMap);
            }
        }
    }

    private Element readWorkspaceFile(File file) {
        Element workspaceRoot = null;
        try {
            workspaceRoot = XmlFile.readRootElement(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return workspaceRoot;
    }

    private void registerPlugins() {
        try {
            PluginManager.getInstanceOf().registerPlugins();
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
    }

    private AbstractPlugin instantiatePlugin(String id, String name) throws InstantiationException, IllegalAccessException {
        PluginKey thisKey = PluginManager.getInstanceOf().findKey(id);
        AbstractPlugin plugin = null;
        plugin = PluginManager.getInstanceOf().getPlugins().get(thisKey).newInstance();
        plugin.setPluginID(id);
        if (name == null || name.equalsIgnoreCase("")) {
            name = plugin.getName();
            setPluginName(plugin, name);
        } else {
            plugin.setName(name);
        }

        pluginOutgoingConnectorMaps.put(plugin.getName(), new HashMap<String, Connector>());
        plugins.add(plugin);

        LatizSystem newSystem = new LatizSystem(new PluginComparator());
        newSystem.add(plugin);
        latizSystems.add(newSystem);

        return plugin;
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

    private void refreshGlobalParameters(Element workspaceRoot) {
        GlobalParameterMap globals = GlobalParameterMap.getInstanceOf();
        Element e;
        for (Object o : workspaceRoot.getChild("globalParameters").getChildren("parameter")) {
            e = (Element) o;
            globals.put(e.getAttributeValue("var"), e.getAttributeValue("value"));
        }
    }

    private class SystemProcessStarter {

        private LatizSystem latizSystem;
        private String systemName;
        private EventManager em;

        private void setLatizSystem(LatizSystem ls) {
            latizSystem = ls;
            systemName = LatizSystemUtilities.getLatizSystemRunName(ls);
        }

        private void runProcess() {
            actionRun();
        }

        private void actionRun() {
            em = new EventManager();
            Number stopTime;
            stopTime = new Double(workspaceRoot.getAttributeValue("stopTime"));
            em.setLatFileTreePanel(latFileTreePanel);
            em.executeLatizSystem(latizSystem, stopTime.doubleValue(), pluginOutgoingConnectorMaps);
        }

        public final String getSystemName() {
            return systemName;
        }

        public final void setSystemName(String systemName) {
            this.systemName = systemName;
        }

        public final EventManager getEm() {
            return em;
        }
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        if (args.length != 1) {
            return;
        }
        RemoteLatizEngine remoteLatizEngine = new RemoteLatizEngine(args[0]);
    }
}
