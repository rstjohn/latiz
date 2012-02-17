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

import java.awt.Image;
import javax.swing.ImageIcon;
import org.openide.filesystems.FileObject;
import org.openide.nodes.AbstractNode;
import org.openide.util.ImageUtilities;

/**
 *
 * @author rstjohn
 */
class GroupNode extends AbstractNode {
    static final Image ICON = ImageUtilities.loadImage("com/AandR/latiz/pluginPanel/folder_open.png");

    public GroupNode(FileObject f) {
        super(new GroupChildren(f));
        setName(f.getName());
        setDisplayName(f.getName());
    }

    @Override
    public Image getIcon(int arg0) {
        return ICON;
    }

    @Override
    public Image getOpenedIcon(int arg0) {
        return ICON;
    }
}
