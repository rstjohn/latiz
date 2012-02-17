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
package com.AandR.latiz.pluginPanel;

import java.util.Comparator;
import java.util.TreeSet;
import org.openide.filesystems.FileObject;
import org.openide.nodes.Children;
import org.openide.nodes.Node;

/**
 *
 * @author rstjohn
 */
public class GroupChildren extends Children.Keys<FileObject> {
    private FileObject fileObject;

    public GroupChildren(FileObject fileObject) {
        this.fileObject = fileObject;
    }

    @Override
    protected void addNotify() {
        //TODO try sorting children based on alpha / folder-file.
        TreeSet<FileObject> treeSet = new TreeSet<FileObject>(new Comparator<FileObject>() {
            public int compare(FileObject o1, FileObject o2) {
                if((o1.isFolder() && o2.isFolder()) || (o1.isData() && o2.isData())) {
                    return o1.getName().compareToIgnoreCase(o2.getName());
                } else {
                    return o1.isFolder() ? -1 : 1;
                }
            }
        });
        for(FileObject fo : fileObject.getChildren()) {
            treeSet.add(fo);
        }
        setKeys(treeSet);
    }

    @Override
    protected Node[] createNodes(FileObject f) {
        if(f.isFolder()) {
            return new Node[] {new GroupNode(f)};
        } else {
            return new Node[] {new PluginLeaf(f)};
        }
    }
}
