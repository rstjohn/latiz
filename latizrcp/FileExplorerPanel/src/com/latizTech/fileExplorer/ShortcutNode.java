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

import java.awt.Image;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.SwingUtilities;
import org.openide.actions.DeleteAction;
import org.openide.actions.MoveDownAction;
import org.openide.actions.MoveUpAction;
import org.openide.actions.RenameAction;
import org.openide.filesystems.FileChooserBuilder;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.util.Exceptions;
import org.openide.util.actions.Presenter;
import org.openide.util.actions.SystemAction;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author stjohnr
 */
public class ShortcutNode extends AbstractNode {

    private ShortcutObject shortcutObject;

    public ShortcutNode(ShortcutObject shortcutObject) {
        super(Children.LEAF, Lookups.fixed());
        this.shortcutObject = shortcutObject;
    }

    public ShortcutObject getShortcutObject() {
        return shortcutObject;
    }

    @Override
    public Action[] getActions(boolean arg0) {

        return new Action[]{
                    new NewAction(),
                    null,
                    SystemAction.get(DeleteAction.class),
                    SystemAction.get(RenameAction.class),
                    null,
                    SystemAction.get(MoveUpAction.class),
                    SystemAction.get(MoveDownAction.class),
                    null,
                    new IconAction(),
                    new SetAsDefaultAction()
                };
    }

    @Override
    public Image getIcon(int arg0) {
        return ShortcutIcon.ICONS[shortcutObject.iconIndex];
    }

    @Override
    public boolean canRename() {
        return true;
    }

    @Override
    public void setName(String name) {
        shortcutObject.alias = name;
        SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                FileExplorerTopComponent fe = FileExplorerTopComponent.findInstance();
                fe.refreshList();
            }
        });
    }

    @Override
    public boolean canDestroy() {
        return true;
    }

    @Override
    public void destroy() throws IOException {
        SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                FileExplorerTopComponent fe = FileExplorerTopComponent.findInstance();
                fe.removeShortcut(shortcutObject);
            }
        });
        super.destroy();
    }

    @Override
    public String getHtmlDisplayName() {
        if (shortcutObject.isDefault) {
            return "<B><I>" + shortcutObject.alias + "</I></B>";
        } else {
            return null;
        }
    }

    @Override
    public String getDisplayName() {
        return shortcutObject.alias;
    }

    public File getFile() {
        return shortcutObject.file;
    }


    /*
    @Override
    protected Sheet createSheet() {
    Sheet sheet = Sheet.createDefault();
    Sheet.Set set = Sheet.createPropertiesSet();
    sheet.put(set);
    return sheet;
    }
     * */
    /**
     *
     */
    private class IconAction extends AbstractAction implements Presenter.Popup {

        public void actionPerformed(ActionEvent e) {
            JMenuItem item = (JMenuItem) e.getSource();
            shortcutObject.iconIndex = Integer.parseInt(item.getName());
            SwingUtilities.invokeLater(new Runnable() {

                public void run() {
                    FileExplorerTopComponent fe = FileExplorerTopComponent.findInstance();
                    fe.refreshList();
                }
            });
        }

        public JMenuItem getPopupPresenter() {
            JMenu iconMenu = new JMenu("Set Icon");
            Image[] images = ShortcutIcon.ICONS;
            JMenuItem thisMenuItem;
            for (int i = 0; i < images.length; i++) {
                thisMenuItem = new JMenuItem(new ImageIcon(ShortcutIcon.ICONS[i]));
                thisMenuItem.setName(String.valueOf(i));
                thisMenuItem.addActionListener(this);
                iconMenu.add(thisMenuItem);
            }
            return iconMenu;
        }
    }

    /**
     * 
     */
    private class SetAsDefaultAction extends AbstractAction implements Presenter.Popup {

        public void actionPerformed(ActionEvent e) {
            SwingUtilities.invokeLater(new Runnable() {

                public void run() {
                    FileExplorerTopComponent fe = FileExplorerTopComponent.findInstance();
                    fe.setAsDefault(shortcutObject);
                }
            });
        }

        public JMenuItem getPopupPresenter() {
            JMenuItem item = new JMenuItem("Set as Default");
            item.addActionListener(this);
            return item;
        }
    }

    /**
     * 
     */
    private class NewAction extends AbstractAction implements Presenter.Popup {

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
