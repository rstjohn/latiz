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

import java.awt.event.ActionEvent;
import java.io.File;
import java.net.MalformedURLException;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JMenuItem;
import org.openide.filesystems.FileChooserBuilder;
import org.openide.nodes.AbstractNode;
import org.openide.util.Exceptions;
import org.openide.util.actions.Presenter;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author rstjohn
 */
public class ShortcutRootNode extends AbstractNode {

    public ShortcutRootNode(ShortcutChildren children) {
        super(children, Lookups.singleton(children));
    }

    @Override
    public Action[] getActions(boolean arg0) {
        return new Action[] {
            new AddShortcutAction(),
        };
    }

    /**
     *
     */
    private class AddShortcutAction extends AbstractAction implements Presenter.Popup {

        public void actionPerformed(ActionEvent e) {

            File file = new FileChooserBuilder(ShortcutRootNode.class)
                    .setDirectoriesOnly(true)
                    .setTitle("Choose directory to save as shortcut")
                    .setApproveText("Select")
                    .showOpenDialog();
            if(file==null) return;
            try {
                FileExplorerTopComponent fe = FileExplorerTopComponent.findInstance();
                fe.addShortcut(file.toURI().toURL());
            } catch (MalformedURLException ex) {
                Exceptions.printStackTrace(ex);
            }
        }

        public JMenuItem getPopupPresenter() {
            JMenuItem item = new JMenuItem("Add Shortcut...");
            item.addActionListener(this);
            return item;
        }
    }
}
