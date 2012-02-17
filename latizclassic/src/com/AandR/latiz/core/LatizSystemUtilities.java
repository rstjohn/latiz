/**
 * 
 */
package com.AandR.latiz.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;


import com.AandR.latiz.dev.AbstractPlugin;
import com.AandR.latiz.gui.Connector;

/**
 * @author Aaron Masino
 * @version Dec 1, 2007 2:49:17 PM <br>
 * 
 * Comments:
 * 
 */
public class LatizSystemUtilities {

    public static void connectionMade(HashSet<LatizSystem> latizSystems, Connector newConnector) {
        AbstractPlugin child = newConnector.getInputReceivingPlugin();
        AbstractPlugin parent = newConnector.getOutputSendingPlugin();
        LatizSystem parentSystem = null;
        LatizSystem childSystem = null;
        for (LatizSystem ls : latizSystems) {
            if (ls.contains(child)) {
                childSystem = ls;
            }
            if (ls.contains(parent)) {
                parentSystem = ls;
            }
        }
        if (parentSystem.equals(childSystem)) {
            return;
        }
        annexLatizSystem(latizSystems, childSystem, parentSystem);
        return;
    }

    public static void pluginRemoved(HashSet<LatizSystem> latizSystems, AbstractPlugin plugin,
            HashMap<String, HashMap<String, Connector>> pluginOutgoingConnectorMaps) {
        ArrayList<AbstractPlugin> plugins = new ArrayList<AbstractPlugin>();
        for (LatizSystem ls : latizSystems) {
            for (AbstractPlugin p : ls) {
                if (!p.equals(plugin)) {
                    plugins.add(p);
                }
            }
        }
        latizSystems.clear();
        LatizSystem newSystem;
        for (AbstractPlugin p : plugins) {
            newSystem = new LatizSystem(new PluginComparator());
            newSystem.add(p);
            latizSystems.add(newSystem);
        }

        for (HashMap<String, Connector> map : pluginOutgoingConnectorMaps.values()) {
            for (Connector c : map.values()) {
                connectionMade(latizSystems, c);
            }
        }
        return;
    }

    public static void connectionRemoved(HashSet<LatizSystem> latizSystems, Connector removedConnector,
            HashMap<String, HashMap<String, Connector>> pluginOutgoingConnectorMaps) {
        AbstractPlugin child = removedConnector.getInputReceivingPlugin();
        AbstractPlugin parent = removedConnector.getOutputSendingPlugin();
        boolean commonDescendants = LatizSystemUtilities.pluginsHaveCommonDescendents(latizSystems, child, parent,
                pluginOutgoingConnectorMaps);
        if (commonDescendants) {
            return;
        }

        ArrayList<AbstractPlugin> plugins = new ArrayList<AbstractPlugin>();
        for (LatizSystem ls : latizSystems) {
            for (AbstractPlugin p : ls) {
                plugins.add(p);
            }
        }
        latizSystems.clear();
        LatizSystem newSystem;
        for (AbstractPlugin p : plugins) {
            newSystem = new LatizSystem(new PluginComparator());
            newSystem.add(p);
            latizSystems.add(newSystem);
        }

        for (HashMap<String, Connector> map : pluginOutgoingConnectorMaps.values()) {
            for (Connector c : map.values()) {
                connectionMade(latizSystems, c);
            }
        }
        return;

    }

    private static void annexLatizSystem(HashSet<LatizSystem> allSystems, LatizSystem annexedSystem, LatizSystem system) {
        allSystems.remove(annexedSystem);
        allSystems.remove(system);
        for (AbstractPlugin p : annexedSystem) {
            system.add(p);
        }
        allSystems.add(system);

    }

    public static boolean pluginsHaveCommonDescendents(Set<LatizSystem> latizSystems, AbstractPlugin p1, AbstractPlugin p2,
            HashMap<String, HashMap<String, Connector>> pluginOutgoingConnectorMaps) {
        LatizSystem p1System = findLatizSystem(latizSystems, p1);
        LatizSystem p2System = findLatizSystem(latizSystems, p2);
        if (p1System != p2System) {
            return false;
        }

        TreeSet<AbstractPlugin> p1Descendents = new TreeSet<AbstractPlugin>(new PluginComparator());
        p1Descendents.addAll(LatizSystemUtilities.getDescendents(new TreeSet<AbstractPlugin>(new PluginComparator()), p1System, p1,
                pluginOutgoingConnectorMaps));
        TreeSet<AbstractPlugin> p2Descendents = new TreeSet<AbstractPlugin>(new PluginComparator());
        p2Descendents.addAll(LatizSystemUtilities.getDescendents(new TreeSet<AbstractPlugin>(new PluginComparator()), p2System, p2,
                pluginOutgoingConnectorMaps));
        boolean commonDescendents = false;
        for (AbstractPlugin p : p1Descendents) {
            if (p2Descendents.contains(p)) {
                commonDescendents = true;
            }
        }

        return commonDescendents;
    }

    public static TreeSet<AbstractPlugin> getDescendents(TreeSet<AbstractPlugin> previousParents, LatizSystem parentSystem,
            AbstractPlugin parent, HashMap<String, HashMap<String, Connector>> pluginOutgoingConnectorMaps) {
        TreeSet<AbstractPlugin> descendents = new TreeSet<AbstractPlugin>(new PluginComparator());
        TreeSet<AbstractPlugin> nextDescendents;
        if (previousParents.contains(parent)) {
            return null;
        }
        for (AbstractPlugin c : LatizSystemUtilities.getChildren(parentSystem, parent, pluginOutgoingConnectorMaps.get(parent.getName()))) {
            descendents.add(c);
            previousParents.add(parent);
            nextDescendents = getDescendents(previousParents, parentSystem, c, pluginOutgoingConnectorMaps);
            if (nextDescendents != null) {
                for (AbstractPlugin g : nextDescendents) {
                    descendents.add(g);
                }
            }
        }
        return descendents;

    }

    public static String getLatizSystemRunName(LatizSystem latizSystem) {
        for (AbstractPlugin p : latizSystem) {
            if (p.getParentPluginInterfaces() == null || p.getParentPluginInterfaces().isEmpty()) {
                return p.getName();
            }
        }
        return null;
    }

    public static ArrayList<AbstractPlugin> getPluginsWithNoParents(ArrayList<AbstractPlugin> plugins) {
        ArrayList<AbstractPlugin> firstPlugins = new ArrayList<AbstractPlugin>();
        for (AbstractPlugin p : plugins) {
            if (p.getParentPluginInterfaces() == null || p.getParentPluginInterfaces().isEmpty()
                    || p.getParentPluginInterfaces().isEmpty()) {
                firstPlugins.add(p);
            }
        }
        return firstPlugins;
    }

    private static LatizSystem findLatizSystem(Set<LatizSystem> latizSystems, AbstractPlugin plugin) {
        for (LatizSystem ls : latizSystems) {
            if (ls.contains(plugin)) {
                return ls;
            }
        }
        return null;
    }

    public static ArrayList<AbstractPlugin> getChildren(Set<LatizSystem> latizSystems, AbstractPlugin parent,
            HashMap<String, Connector> pluginOutgoingConnectorMap) {
        LatizSystem parentSystem = findLatizSystem(latizSystems, parent);

        ArrayList<AbstractPlugin> children = new ArrayList<AbstractPlugin>();
        for (String s : pluginOutgoingConnectorMap.keySet()) {
            children.add(LatizSystemUtilities.getPluginFromList(parentSystem, s.split(">")[1]));
        }
        return children;
    }

    public static ArrayList<AbstractPlugin> getChildren(LatizSystem parentSystem, AbstractPlugin parent,
            HashMap<String, Connector> pluginOutgoingConnectorMap) {
        ArrayList<AbstractPlugin> children = new ArrayList<AbstractPlugin>();
        for (String s : pluginOutgoingConnectorMap.keySet()) {
            children.add(LatizSystemUtilities.getPluginFromList(parentSystem, s.split(">")[1]));
        }
        return children;
    }

    /**
     * returns plugin with the name "name" if found in the list
     *
     * @param plugins
     * @param name
     * @return
     */
    public static AbstractPlugin getPluginFromList(List<AbstractPlugin> ls, String name) {
        for (AbstractPlugin p : ls) {
            if (p.getName().equalsIgnoreCase(name)) {
                return p;
            }
        }
        return null;
    }

    public static AbstractPlugin getPluginFromList(Set<AbstractPlugin> plugins, String name) {
        for (AbstractPlugin p : plugins) {
            if (p.getName().equalsIgnoreCase(name)) {
                return p;
            }
        }
        return null;

    }
}
