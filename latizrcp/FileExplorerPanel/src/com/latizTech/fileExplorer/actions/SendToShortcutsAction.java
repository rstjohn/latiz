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
package com.latizTech.fileExplorer.actions;

import com.latizTech.fileExplorer.FileExplorerTopComponent;
import java.util.Collection;
import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import org.openide.loaders.DataFolder;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
import org.openide.util.HelpCtx;
import org.openide.util.ImageUtilities;
import org.openide.util.Utilities;
import org.openide.util.actions.CallableSystemAction;

public final class SendToShortcutsAction extends CallableSystemAction {

    @Override
    public JMenuItem getPopupPresenter() {
        JMenuItem item = super.getPopupPresenter();
        ImageIcon icon = new ImageIcon(ImageUtilities.loadImage("com/latizTech/fileExplorer/resources/favorites.png"));
        item.setIcon(icon);
        return item;
    }

    @Override
    public String getName() {
        return "Send to Shortcuts";
    }

    @Override
    protected String iconResource() {
        return "com/latizTech/fileExplorer/resources/favorites.png";
    }

    @Override
    public void performAction() {
        Collection<? extends Node> nodes = Utilities.actionsGlobalContext().lookupAll(Node.class);
        if (nodes == null || nodes.isEmpty()) {
            return;
        }

        try {
            FileExplorerTopComponent fe = FileExplorerTopComponent.findInstance();
            if (fe == null) {
                return;
            }
            for (Node n : nodes) {
                DataFolder dataFolder = n.getLookup().lookup(DataFolder.class);
                if (dataFolder == null) {
                    continue;
                }
                fe.addShortcut(dataFolder.getPrimaryFile().getURL());
            }
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    @Override
    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }

    @Override
    protected boolean asynchronous() {
        return false;
    }
}
