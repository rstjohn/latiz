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
package com.AandR.workspacePanel;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.nodes.PropertySupport;
import org.openide.nodes.Sheet;

public final class FileNode extends AbstractNode {

    static String PROP_FULL_PATH = "space";
    static String IS_HIDDEN = "is hidden";
    private File file;

    private FileNode(File f) {
        super(new FileKids(f));
        file = f;
        setName(f.getName());
        getHandle();
    }


    public static Node files() {
        return files(null);
    }


    public static Node files(File dir) {
        AbstractNode n = new AbstractNode(new FileKids(dir));
        n.setName("Root");
        return n;
    }


    public static class FileKids extends Children.Keys<File> {

        File file;

        public FileKids(File file) {
            this.file = file;
        }

        @Override
        protected void addNotify() {
            if (file == null) {
                File[] arr = File.listRoots();
                if (arr.length == 1) {
                    arr = arr[0].listFiles();
                }
                setKeys(arr);
            } else {
                File[] arr = file.listFiles();
                if (arr != null) {
                    setKeys(arr);
                }
            }
        }

        @Override
        public Node[] createNodes(File f) {
            FileNode n = new FileNode(f);
            return new Node[]{n};
        }

        @Override
        public Node[] getNodes(boolean arg0) {
            return super.getNodes(arg0);
        }
    }

    @Override
    protected Sheet createSheet() {
        Sheet s = super.createSheet();
        Sheet.Set ss = s.get(Sheet.PROPERTIES);
        if (ss == null) {
            ss = Sheet.createPropertiesSet();
            s.put(ss);
        }
        ss.put(new FullPathProperty(file));
        ss.put(new IsHiddenProperty(file));
        return s;
    }

    private class FullPathProperty extends PropertySupport.ReadOnly<String> {

        File file;

        public FullPathProperty(File file) {
            super(FileNode.PROP_FULL_PATH, String.class, "Full path", "Complete path is shown");
            this.file = file;
        }

        public String getValue() throws IllegalAccessException, InvocationTargetException {
            return file.getAbsolutePath();
        }
    }


    /**
     *
     */
    private class IsHiddenProperty extends PropertySupport.ReadOnly<String> {

        File file;

        public IsHiddenProperty(File file) {
            super(FileNode.IS_HIDDEN, String.class, "Is hidden", "Is hidden status is shown");
            this.file = file;
        }

        public String getValue() throws IllegalAccessException, InvocationTargetException {
            return String.valueOf(file.isHidden());
        }
    }

}