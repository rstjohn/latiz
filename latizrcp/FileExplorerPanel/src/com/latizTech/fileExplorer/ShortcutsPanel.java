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

import java.awt.BorderLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import javax.swing.ActionMap;
import org.openide.explorer.ExplorerManager;
import org.openide.explorer.ExplorerUtils;
import org.openide.explorer.view.ListView;
import org.openide.nodes.Node;
import org.openide.windows.TopComponent;

/**
 *
 * @author stjohnr
 */
public class ShortcutsPanel extends TopComponent implements ExplorerManager.Provider, PropertyChangeListener {

    private final ExplorerManager mgr = new ExplorerManager();

    public ShortcutsPanel(ArrayList<ShortcutObject> shortcutList) {
        setLayout(new BorderLayout());
        ActionMap map = getActionMap();
        map.put("delete", ExplorerUtils.actionDelete(mgr, true));
        associateLookup(ExplorerUtils.createLookup(mgr, map));
        ListView view = new ListView();
        setShortcuts(shortcutList);
        add(view, BorderLayout.CENTER);
    }

    @Override
    public void addNotify() {
        super.addNotify();
        mgr.addPropertyChangeListener(this);
    }

    @Override
    public void removeNotify() {
        mgr.removePropertyChangeListener(this);
        super.removeNotify();
    }

    public void setShortcuts(ArrayList<ShortcutObject> shortcutsList) {
        ShortcutChildren children = new ShortcutChildren();
        children.setChildren(shortcutsList);
        mgr.setRootContext(new ShortcutRootNode(children));
    }

    public ExplorerManager getExplorerManager() {
        return mgr;
    }

    public void propertyChange(PropertyChangeEvent evt) {
        if (ExplorerManager.PROP_SELECTED_NODES.equals(evt.getPropertyName())) {
            Node[] selectedNodes = mgr.getSelectedNodes();
            if (selectedNodes.length < 1) {
                return;
            }

            FileExplorerTopComponent fe = FileExplorerTopComponent.findInstance();
            if (fe == null) {
                return;
            }

            ShortcutNode rootNode = (ShortcutNode) selectedNodes[0];
            fe.setRootDirectory(rootNode.getFile());
        }
    }
}
