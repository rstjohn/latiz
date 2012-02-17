package com.AandR.latiz.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.TreeMap;

import com.AandR.gui.SplashScreen;
import com.AandR.io.SortedProperties;
import com.AandR.latiz.dev.AbstractPlugin;
import com.AandR.latiz.gui.PluginKey;

/**
 * @author Dr. Richard St. John
 * @version $Revision: 1.3 $, $Date: 2007/08/04 19:46:30 $
 */
public class PluginManager {

    private static PluginManager instanceOf = new PluginManager();
    private TreeMap<PluginKey, Class<AbstractPlugin>> plugins, inactivePlugins;
    private File pluginDirectory;
    private SortedProperties props;

    private PluginManager() {
        props = new SortedProperties();

        plugins = new TreeMap<PluginKey, Class<AbstractPlugin>>(new Comparator<PluginKey>() {

            public int compare(PluginKey a, PluginKey b) {
                return a.getId().compareToIgnoreCase(b.getId());
            }
        });
        inactivePlugins = new TreeMap<PluginKey, Class<AbstractPlugin>>(new Comparator<PluginKey>() {

            public int compare(PluginKey a, PluginKey b) {
                return a.getId().compareToIgnoreCase(b.getId());
            }
        });
        pluginDirectory = new File(System.getProperty("user.home") + File.separator + ".AandRcreations" + File.separator + "latiz" + File.separator + "plugins");
        pluginDirectory.mkdirs();
    }

    public static PluginManager getInstanceOf() {
        return instanceOf;
    }

    public TreeMap<PluginKey, Class<AbstractPlugin>> getPlugins() {
        return plugins;
    }

    public void setInactive(PluginKey key) {
        Class<AbstractPlugin> plugin = plugins.get(key);
        plugins.remove(key);
        inactivePlugins.put(key, plugin);
    }

    public void setActive(PluginKey key) {
        Class<AbstractPlugin> plugin = inactivePlugins.get(key);
        inactivePlugins.remove(key);
        plugins.put(key, plugin);
    }

    public TreeMap<PluginKey, Class<AbstractPlugin>> getInactivePlugins() {
        return inactivePlugins;
    }

    public File findPropertiesFile(AbstractPlugin p) {
        for (PluginKey key : plugins.keySet()) {
            if (key.getClassName().equals(p.getClass().getName())) {
                return key.getPropFile();
            }
        }

        for (PluginKey key : inactivePlugins.keySet()) {
            if (key.getClassName().equals(p.getClass().getName())) {
                return key.getPropFile();
            }
        }
        return null;
    }

    public PluginKey findKey(String id) {
        Object[] keys = plugins.keySet().toArray();
        for (int i = 0; i < keys.length; i++) {
            if (id.equalsIgnoreCase(keys[i].toString())) {
                return (PluginKey) keys[i];
            }
        }
        return null;
    }

    public PluginKey findInactiveKey(String id) {
        Object[] keys = inactivePlugins.keySet().toArray();
        for (int i = 0; i < keys.length; i++) {
            if (id.equalsIgnoreCase(keys[i].toString())) {
                return (PluginKey) keys[i];
            }
        }
        return null;
    }

    public ArrayList<File> getPropertyFiles() {
        ArrayList<File> propFiles = new ArrayList<File>();
        propFiles.addAll(getPropertyFiles(pluginDirectory));
        return propFiles;
    }

    public ArrayList<File> getPropertyFiles(File[] directoriesToScan) {
        ArrayList<File> propFiles = new ArrayList<File>();
        for (File file : directoriesToScan) {
            propFiles.addAll(getPropertyFiles(file));
        }
        return propFiles;
    }

    public ArrayList<File> getPropertyFiles(File directoryToScan) {
        ArrayList<File> propFiles = new ArrayList<File>();
        File pluginDir = directoryToScan;
        File thisPluginFile;
        String[] dirList = pluginDir.list();
        for (String dir : dirList) {
            thisPluginFile = new File(pluginDir + File.separator + dir + File.separator + "plugin.properties");
            if (!thisPluginFile.exists()) {
                continue;
            }
            propFiles.add(thisPluginFile);
        }
        return propFiles;
    }

    public void registerPlugins(SplashScreen splash, File directory) throws FileNotFoundException, IOException, ClassNotFoundException {
        if (!directory.exists()) {
            return;
        }

        Class<AbstractPlugin> thisClass = null;
        boolean isActive;

        File thisPluginDirectory, thisPluginFile, thisJarFile;
        File[] dirList = directory.listFiles();
        for (File f : dirList) {
            thisPluginDirectory = new File(directory, f.getName());

            File pluginSubDirectory = new File(thisPluginDirectory, "plugins");
            if (pluginSubDirectory.exists()) {
                registerPlugins(splash, pluginSubDirectory);
            }

            thisPluginFile = new File(thisPluginDirectory, "plugin.properties");
            if (!thisPluginFile.exists()) {
                continue;
            }

            props.load(new FileInputStream(thisPluginFile));
            thisJarFile = new File(thisPluginDirectory, props.getProperty("jarFile"));
            URLClassLoader classLoader = new URLClassLoader(new URL[]{thisJarFile.toURI().toURL()});

            String parentID = props.getProperty("parentID", "Available Plugins");
            PluginKey thisKey = new PluginKey(thisPluginFile, parentID, props.getProperty("id"));
            if (splash != null) {
                (splash.getMessageLabel()).setText("Registering plugin: " + thisKey);
            }
            String pluginClass = props.getProperty("pluginClass");
            thisKey.setClassName(pluginClass);
            thisKey.setClassPath(parseClassPath(props.getProperty("classPath")));
            isActive = Boolean.parseBoolean(props.getProperty("active"));
            thisClass = (Class<AbstractPlugin>) classLoader.loadClass(pluginClass);
            if (isActive) {
                plugins.put(thisKey, thisClass);
            } else {
                inactivePlugins.put(thisKey, thisClass);
            }
        }
    }

    public void registerPlugins() throws FileNotFoundException, IOException, ClassNotFoundException {
        registerPlugins(null, pluginDirectory);
    }

    public void registerPlugins(SplashScreen splash) throws FileNotFoundException, IOException, ClassNotFoundException {
        registerPlugins(splash, pluginDirectory);
    }

    public File[] parseClassPath(String pathString) {
        if (pathString == null || pathString.trim().equals("")) {
            return null;
        }
        String[] paths = pathString.split(";");
        File[] filePaths = new File[paths.length];

        File dir;
        String thisPath;
        for (int i = 0; i < paths.length; i++) {
            dir = pluginDirectory;
            thisPath = paths[i];

            if (thisPath.startsWith("./") || thisPath.startsWith(".\\")) {
                thisPath = thisPath.substring(2);
            }

            // Resolve relative paths
            while (thisPath.startsWith("../")) {
                dir = dir.getParentFile();
                thisPath = thisPath.substring(thisPath.indexOf("../") + 3);
            }

            // Resolve relative paths
            while (thisPath.startsWith("..\\")) {
                dir = dir.getParentFile();
                thisPath = thisPath.substring(thisPath.indexOf("..\\") + 3);
            }

            filePaths[i] = new File(dir, thisPath);
        }
        return filePaths;
    }

    public File getPluginDirectory() {
        return pluginDirectory;
    }
}
