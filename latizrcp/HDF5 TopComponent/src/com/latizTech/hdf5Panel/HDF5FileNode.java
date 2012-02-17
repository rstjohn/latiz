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

import java.awt.Image;
import javax.swing.tree.DefaultMutableTreeNode;
import ncsa.hdf.object.HObject;
import org.openide.nodes.AbstractNode;
import org.openide.util.ImageUtilities;

/**
 *
 * @author rstjohn
 */
public class HDF5FileNode extends AbstractNode {
    static final Image ICON = ImageUtilities.loadImage("com/AandR/latiz/pluginPanel/folder_open.png");

    public HDF5FileNode(DefaultMutableTreeNode rootNode) {
        super(new HDF5Children(rootNode));
        setName(((HObject)rootNode.getUserObject()).getName());
        setDisplayName(getName());
        setIconBaseWithExtension("com/latizTech/hdf5Panel/resources/latiz.png");
    }
}
