package com.AandR.latiz.gui;

import java.io.File;

/**
 * @author Dr. Richard St. John
 * @version $Revision: 1.2 $, $Date: 2007/08/04 19:46:30 $
 */
public class PluginKey {

    private File propFile;
    private String id, parentID, className;
    private File[] classPath;

    public PluginKey(File propFile, String parentID, String id) {
        this.propFile = propFile;
        this.id = id;
        this.parentID = parentID;
    }

    @Override
    public String toString() {
        return id;
    }

    public String getId() {
        return id;
    }

    public String getParentID() {
        return parentID;
    }

    public File getPropFile() {
        return propFile;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public File[] getClassPath() {
        return classPath;
    }

    public void setClassPath(File[] depends) {
        this.classPath = depends;
    }
}
