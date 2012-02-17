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
package com.AandR.palette.globals;

import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;

/**
 *
 * @author rstjohn
 */
public class GlobalVariableNode extends AbstractNode {
    private final GlobalVariable global;

    public GlobalVariableNode(GlobalVariable global) {
        super(Children.LEAF);
        this.global = global;
        setName(global.getLabel());
        setIconBaseWithExtension("com/AandR/palette/resources/invisibleIcon.png");
        setDisplayName(global.getLabel());
    }
}
