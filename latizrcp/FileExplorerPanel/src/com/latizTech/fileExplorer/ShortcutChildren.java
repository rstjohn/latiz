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
package com.latizTech.fileExplorer;

import java.util.ArrayList;
import java.util.List;
import org.openide.nodes.Index;
import org.openide.nodes.Node;

/**
 *
 * @author stjohnr
 */
public class ShortcutChildren extends Index.ArrayChildren {

    private ArrayList<ShortcutObject> shortcutObjects;

    public ShortcutChildren() {
    }

    public void setChildren(ArrayList<ShortcutObject> children) {
        this.shortcutObjects = children;
    }

    @Override
    protected List<Node> initCollection() {
        ArrayList<Node> childrenNodes = new ArrayList<Node>();
        for (ShortcutObject o : shortcutObjects) {
            childrenNodes.add(new ShortcutNode(o));
        }
        return childrenNodes;
    }

    @Override
    public void moveDown(int arg0) {
        super.moveDown(arg0);
        ShortcutObject o = shortcutObjects.remove(arg0);
        shortcutObjects.add(arg0 + 1, o);
    }

    @Override
    public void moveUp(int arg0) {
        super.moveUp(arg0);
        ShortcutObject o = shortcutObjects.remove(arg0);
        shortcutObjects.add(arg0 - 1, o);
    }
}
