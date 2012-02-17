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
package com.AandR.palette.plugin;

import com.AandR.palette.model.AbstractPaletteModel;
import com.AandR.palette.paletteScene.PaletteScene;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.lookup.Lookups;
import org.openide.windows.Mode;
import org.openide.windows.WindowManager;

/**
 * @author Dr. Richard St. John
 * @version $Revision: 1.3 $, $Date: 2007/08/04 19:46:30 $
 */
public class PluginUtilities {

    private static PluginUtilities instanceOf;

    public static PluginUtilities getDefault() {
        if (instanceOf == null) {
            instanceOf = new PluginUtilities();
        }
        return instanceOf;
    }
    private int pluginNameCounter = 1;

    public AbstractPlugin instantiate(String pluginUniqueID, String pluginName, AbstractPaletteModel paletteModelImpl) {
        FileObject pluginFileObject = FileUtil.getConfigRoot().getFileObject(pluginUniqueID);

        String pluginPath = pluginFileObject.getPath();

        Lookup lkp = Lookups.forPath(pluginFileObject.getParent().getPath());
        Lookup.Template<AbstractPlugin> t = new Lookup.Template<AbstractPlugin>(AbstractPlugin.class);
        Lookup.Result<AbstractPlugin> result = lkp.lookup(t);
        String thisPath;
        AbstractPlugin p = null;
        for (Lookup.Item<AbstractPlugin> item : result.allItems()) {
            thisPath = item.getId() + ".instance";
            if (thisPath.equals(pluginPath)) {
                PluginKey pk = new PluginKey(pluginPath);
                pk.setIconPath((String) pluginFileObject.getAttribute("iconPath"));
                pk.setDefaultName(pluginFileObject.getName());
                pk.setDate((String)pluginFileObject.getAttribute("date"));
                pk.setAuthor((String)pluginFileObject.getAttribute("author"));
                pk.setClassName((String)pluginFileObject.getAttribute("instanceClass"));
                try {
                    p = item.getType().newInstance();
                } catch (Exception ex) {
                    Exceptions.printStackTrace(ex);
                }
                p.setName(pluginName);
                p.setPluginKey(pk);
                p.setPaletteModelImpl(paletteModelImpl);
                if (p instanceof IParameterPanel) {
                    ParameterContainer parameterPanel = ((IParameterPanel)p).createParametersPanel();
                    if (parameterPanel != null) {
                        parameterPanel.setPlugin(p);
                        p.setParameterContainer(parameterPanel);
                        Mode mode = WindowManager.getDefault().findMode("parameterPanel");
                        if (mode != null) {
                            mode.dockInto(parameterPanel);
                        }
                        parameterPanel.open();
                        parameterPanel.requestActive();
                    }
                }
                break;
            }
        }
        return p;
    }

    public AbstractPlugin instantiate(String pluginUniqueID, AbstractPaletteModel paletteModelImpl) {
        FileObject pluginFileObject = FileUtil.getConfigRoot().getFileObject(pluginUniqueID);

        String pluginPath = pluginFileObject.getPath();

        Lookup lkp = Lookups.forPath(pluginFileObject.getParent().getPath());
        Lookup.Template<AbstractPlugin> t = new Lookup.Template<AbstractPlugin>(AbstractPlugin.class);
        Lookup.Result<AbstractPlugin> result = lkp.lookup(t);
        String thisPath;
        AbstractPlugin p = null;
        for (Lookup.Item<AbstractPlugin> item : result.allItems()) {
            thisPath = item.getId() + ".instance";
            if (thisPath.equals(pluginPath)) {
                PluginKey pk = new PluginKey(pluginPath);
                pk.setIconPath((String) pluginFileObject.getAttribute("iconPath"));
                pk.setDefaultName(pluginFileObject.getName());
                try {
                    p = item.getType().newInstance();
                } catch (Exception ex) {
                    Exceptions.printStackTrace(ex);
                }
                p.setName(getPluginName(pk.getDefaultName(), paletteModelImpl));
                p.setPluginKey(pk);
                p.setPaletteModelImpl(paletteModelImpl);
                if (p instanceof IParameterPanel) {
                    ParameterContainer parameterPanel = ((IParameterPanel)p).createParametersPanel();
                    if (parameterPanel != null) {
                        parameterPanel.setPlugin(p);
                        p.setParameterContainer(parameterPanel);
                        Mode mode = WindowManager.getDefault().findMode("parameterPanel");
                        if (mode != null) {
                            mode.dockInto(parameterPanel);
                        }
                        parameterPanel.open();
                        parameterPanel.requestActive();
                    }
                }
                break;
            }
        }
        return p;
    }

    public AbstractPlugin instantiate(String pluginUniqueID, PaletteScene scene) {
        FileObject pluginFileObject = FileUtil.getConfigRoot().getFileObject(pluginUniqueID);

        String pluginPath = pluginFileObject.getPath();

        Lookup lkp = Lookups.forPath(pluginFileObject.getParent().getPath());
        Lookup.Template<AbstractPlugin> t = new Lookup.Template<AbstractPlugin>(AbstractPlugin.class);
        Lookup.Result<AbstractPlugin> result = lkp.lookup(t);
        String thisPath;
        AbstractPlugin p = null;
        for (Lookup.Item<AbstractPlugin> item : result.allItems()) {
            thisPath = item.getId() + ".instance";
            if (thisPath.equals(pluginPath)) {
                PluginKey pk = new PluginKey(pluginPath);
                pk.setAuthor((String) pluginFileObject.getAttribute("author"));
                pk.setDate((String) pluginFileObject.getAttribute("date"));
                pk.setIconPath((String) pluginFileObject.getAttribute("iconPath"));
                pk.setDefaultName(pluginFileObject.getName());
                try {
                    p = item.getType().newInstance();
                } catch (Exception ex) {
                    Exceptions.printStackTrace(ex);
                }
                p.setName(getPluginName(pk.getDefaultName(), scene));
                p.setPluginKey(pk);
                p.setPaletteModelImpl(scene.getDefaultPaletteModel());
                if (p instanceof IParameterPanel) {
                    ParameterContainer parameterPanel = ((IParameterPanel)p).createParametersPanel();
                    if (parameterPanel != null) {
                        parameterPanel.setPlugin(p);
                        p.setParameterContainer(parameterPanel);
                        Mode mode = WindowManager.getDefault().findMode("parameterPanel");
                        if (mode != null) {
                            mode.dockInto(parameterPanel);
                        }
                        parameterPanel.open();
                        parameterPanel.requestActive();
                    }
                }
                break;
            }
        }
        return p;
    }

    private String getPluginName(String name, AbstractPaletteModel paletteModelImpl) {
        for (String pluginName : paletteModelImpl.getPlugins().keySet()) {
            if (pluginName.equals(name)) {
                int l = name.lastIndexOf("-");
                l = l < 0 ? name.length() - 1 : l;
                name = name.substring(0, l + 1) + "-" + pluginNameCounter++;
                name = getPluginName(name, paletteModelImpl);
            }
        }
        return name;
    }

    private String getPluginName(String name, PaletteScene scene) {
        for (String pluginName : scene.getPluginsMap().keySet()) {
            if (pluginName.equals(name)) {
                int l = name.lastIndexOf("-");
                l = l < 0 ? name.length() - 1 : l;
                name = name.substring(0, l + 1) + "-" + pluginNameCounter++;
                name = getPluginName(name, scene);
            }
        }
        return name;
    }
    
//    private void registerPlugins() {
//        Lookup lkp = Lookups.forPath("plugins");
//        Lookup.Template<AbstractPlugin> t = new Lookup.Template<AbstractPlugin>(AbstractPlugin.class);
//        Collection<? extends Lookup.Item<AbstractPlugin>> items = lkp.lookup(t).allItems();
//
//        FileObject pluginsFileObject = Repository.getDefault().getDefaultFileSystem().getRoot().getFileObject("plugins");
//        //IOProvider.getDefault().getIO("Output", false).getOut().println("Number of Children: " + pluginsFileObject.getChildren().length);
//        PluginKey thisKey;
//        FileObject fo;
//        Object iconPath, defaultName;
//
//        //IOProvider.getDefault().getIO("Output", false).getOut().println("Number of plugins found: " + items.size());
//        for (Lookup.Item<AbstractPlugin> item : items) {
//            fo = pluginsFileObject.getFileObject(item.getId().split("/")[1] + ".instance");
//            thisKey = new PluginKey(fo.getAttribute("id").toString());
//            thisKey.setClassName(fo.getName().replace("-", "."));
//            //IOProvider.getDefault().getIO("Output", false).getOut().println("      " + thisKey.getUniqueID());
//
//            iconPath = fo.getAttribute("iconPath");
//            thisKey.setIconPath(iconPath==null ? null : iconPath.toString());
//
//            defaultName = fo.getAttribute("defaultName");
//            thisKey.setDefaultName(defaultName==null ? null : defaultName.toString());
//
//            plugins.put(thisKey, item);
//        }
//        FileObject pluginsFileObject = Repository.getDefault().getDefaultFileSystem().getRoot().getFileObject("plugins");
//        if(pluginsFileObject==null) return;
//        FileObject[] children = pluginsFileObject.getChildren();
//        PluginKey thisKey;
//        for(FileObject fo : children) {
//            thisKey = new PluginKey(fo.getAttribute("id").toString());
//            thisKey.setClassName(fo.getName().replace("-", "."));
//            plugins.add(thisKey);
//        }

//        if (!directory.exists()) {
//            return;
//        }
//
//        File thisPluginDirectory, thisPluginFile;
//        File[] dirList = directory.listFiles();
//        for (File f : dirList) {
//            thisPluginDirectory = new File(directory, f.getName());
//
//            File pluginSubDirectory = new File(thisPluginDirectory, "plugins");
//            if (pluginSubDirectory.exists()) {
//                registerPlugins(pluginSubDirectory);
//            }
//
//            thisPluginFile = new File(thisPluginDirectory, "plugin.properties");
//            if (!thisPluginFile.exists()) {
//                continue;
//            }
//
//            props.clear();
//            FileInputStream fis = new FileInputStream(thisPluginFile);
//            props.load(fis);
//            fis.close();
//
//            PluginKey thisKey = new PluginKey(thisPluginFile, props.getProperty("id"));
//            String pluginClass = props.getProperty("pluginClass");
//            thisKey.setJarFile(new File(thisPluginDirectory, props.getProperty("jarFile")));
//            thisKey.setClassName(pluginClass);
//            thisKey.setClassPath(parseClassPath(props.getProperty("classPath")));
//            if (Boolean.parseBoolean(props.getProperty("active"))) {
//                plugins.put(thisKey, pluginClass);
//            } else {
//                inactivePlugins.put(thisKey, pluginClass);
//            }
//        }
//    }

//    public AbstractPlugin instantiate(PaletteScene scene,PluginKey key) throws InstantiationException, IllegalAccessException {
//        AbstractPlugin p = (AbstractPlugin) plugins.get(key).getType().newInstance();
//        p.setName(key.getDefaultName());
//        p.setPluginKey(key);
//        p.setScene(scene);
//        return p;
    //String id = key.getUniqueID().replace(".", "-");
//
//        Lookup lkp = Lookups.forPath("plugins");
//        Lookup.Template<AbstractPlugin> t = new Lookup.Template(AbstractPlugin.class);
//        Collection<? extends Lookup.Item> items = lkp.lookup(t).allItems();
//
//        for(Lookup.Item item : items) {
//            if(item.getType().getCanonicalName().equals(key.getClassName())) {
//                return (AbstractPlugin) item.getInstance();
//            }
//        }
    //Collection<? extends Lookup.Item<AbstractPlugin.class>> items = results.allItems();
    //AbstractPlugin plugin = item.getInstance();
    //IOProvider.getDefault().getIO("Output", false).getOut().println(plugin.getName());
    //return plugin;
//        } catch (ClassNotFoundException ex) {
//            Exceptions.printStackTrace(ex);
//            return null;
//        }
//    }

//    public AbstractPlugin instantiate(PluginKey key) {
//
//        //Use the methodology if you want to load all items onto the Systems's class path
//        try {
//            ClassLoader l = Thread.currentThread().getContextClassLoader();
//            URLClassLoader urlcl = URLClassLoader.newInstance(new URL[]{key.getJarFile().toURI().toURL()}, l);
//            File[] classPathJars = key.getClassPath();
//            if (classPathJars != null) {
//                ClassPathHacker.addFiles(classPathJars, urlcl);
//            }
//            Class c = urlcl.loadClass(key.getClassName());
//            return (AbstractPlugin) c.newInstance();
//        } catch (MalformedURLException e) {
//            e.printStackTrace();
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//        } catch (InstantiationException e) {
//            e.printStackTrace();
//        } catch (IllegalAccessException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//        }
//        return null;
//    }
}
