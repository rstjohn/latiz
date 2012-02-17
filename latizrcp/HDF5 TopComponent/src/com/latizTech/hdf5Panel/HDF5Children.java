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
package com.latizTech.hdf5Panel;

import java.util.ArrayList;
import java.util.Enumeration;
import javax.swing.tree.DefaultMutableTreeNode;
import ncsa.hdf.object.CompoundDS;
import ncsa.hdf.object.Group;
import ncsa.hdf.object.HObject;
import ncsa.hdf.object.ScalarDS;
import org.openide.nodes.Children;
import org.openide.nodes.Node;

/**
 *
 * @author rstjohn
 */
public class HDF5Children extends Children.Keys<DefaultMutableTreeNode> {
    private DefaultMutableTreeNode treeNode;
    

    public HDF5Children(DefaultMutableTreeNode treeNode) {
        this.treeNode = treeNode;
    }

    @Override
    @SuppressWarnings(value="unchecked")
    protected void addNotify() {
        HObject hobject = (HObject) treeNode.getUserObject();
        if(hobject instanceof Group) {
            ArrayList<DefaultMutableTreeNode> children = new ArrayList<DefaultMutableTreeNode>();
            for(Enumeration<DefaultMutableTreeNode> e = treeNode.children(); e.hasMoreElements(); ) {
                children.add(e.nextElement());
            }
            setKeys(children);
        }
    }


    @Override
    protected Node[] createNodes(DefaultMutableTreeNode treeNode) {
        HObject hobj = (HObject) treeNode.getUserObject();
        if(hobj instanceof Group) {
            return new Node[] {new HDF5GroupNode(treeNode)};
        } else {
            if(hobj instanceof CompoundDS) {
                
            } else if (hobj instanceof ScalarDS) {
                
            }
            return new Node[] {new DatasetNode(hobj)};
        }
    }
}
