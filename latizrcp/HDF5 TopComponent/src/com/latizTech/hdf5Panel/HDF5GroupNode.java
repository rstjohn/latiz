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

import javax.swing.tree.DefaultMutableTreeNode;
import ncsa.hdf.object.HObject;
import org.openide.nodes.AbstractNode;

/**
 *
 * @author rstjohn
 */
public class HDF5GroupNode extends AbstractNode {

    public HDF5GroupNode(DefaultMutableTreeNode node) {
        super(new HDF5Children(node));
        setName(((HObject)node.getUserObject()).getFullName());
        setDisplayName(((HObject)node.getUserObject()).getName());
        setIconBaseWithExtension("com/latizTech/hdf5Panel/resources/group.png");
    }
}
