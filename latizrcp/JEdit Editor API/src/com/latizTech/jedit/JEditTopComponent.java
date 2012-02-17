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
package com.latizTech.jedit;

import com.AandR.latiz.core.cookies.SaveAsCookie;
import com.AandR.library.gui.DropEvent;
import com.AandR.library.gui.DropListener;
import com.latizTech.jedit.tokenMarkers.TokenMarker;
import java.io.File;
import java.io.IOException;
import net.miginfocom.swing.MigLayout;
import org.openide.cookies.OpenCookie;
import org.openide.cookies.SaveCookie;
import org.openide.filesystems.FileChooserBuilder;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.CookieSet;
import org.openide.nodes.Node;
import org.openide.windows.TopComponent;

/**
 *
 * @author rstjohn
 */
public class JEditTopComponent extends TopComponent {

    private final SelectedEditorNode selectedEditorNode;
    private final JEditPanel editPanel;

    public JEditTopComponent() {
        selectedEditorNode = new SelectedEditorNode();
        setActivatedNodes(new Node[]{selectedEditorNode});
        setLayout(new MigLayout("ins 0"));
        editPanel = new JEditPanel();
        editPanel.addDropListener(new BasicEditDropListener());
        add(editPanel, "push, grow");
    }

    public JEditTopComponent(FileObject sourceFileObject) {
        this();
        loadFile(FileUtil.toFile(sourceFileObject));
    }

    public void loadFile(File file) {
        setName(file.getPath());
        setDisplayName(file.getName());
        setToolTipText(file.getPath());
        editPanel.getTextArea().loadAsciiFile(file);
    }

    @Override
    public int getPersistenceType() {
        return TopComponent.PERSISTENCE_NEVER;
    }

    @Override
    protected void componentActivated() {
        super.componentActivated();
        selectedEditorNode.isSelected(true);
    }

    @Override
    protected void componentDeactivated() {
        super.componentDeactivated();
        selectedEditorNode.isSelected(false);
    }

    public JEditPanel getEditPanel() {
        return editPanel;
    }

    public void setSyntaxHighlighting(TokenMarker tokenMarker) {
        editPanel.getTextArea().setTokenMarker(tokenMarker);
    }

    private class BasicEditDropListener implements DropListener {

        public void dropAction(DropEvent dropEvent) {
            Object droppedItem = dropEvent.getDroppedItem();
            if(droppedItem instanceof Node) {
                String filename = ((Node)droppedItem).getName();
                File file = new File(filename);
                if(file.exists()) {
                    loadFile(file);
                }
            }
        }
    }

    /**
     *
     */
    private class SelectedEditorNode extends AbstractNode {

        private final OpenCookie openCookieImpl;
        private final SaveCookie saveCookieImpl;
        private final SaveAsCookie saveAsCookieImpl;

        public SelectedEditorNode() {
            super(Children.LEAF);
            openCookieImpl = new OpenCookie() {
                public void open() {
                    File file = new FileChooserBuilder(JEditTopComponent.class)
                            .setFilesOnly(true)
                            .setTitle("Open File Dialog")
                            .setApproveText("Open")
                            .showOpenDialog();
                    if (file == null) return;

                    loadFile(file);
                }
            };

            saveCookieImpl = new SaveCookie() {

                public void save() throws IOException {
                    editPanel.getTextArea().saveAsciiFile();
                }
            };

            saveAsCookieImpl = new SaveAsCookie() {

                public void save() throws IOException {
                    File file = new FileChooserBuilder(JEditTopComponent.class)
                            .setFilesOnly(true)
                            .setTitle("Save File As Dialog")
                            .setApproveText("Save As")
                            .showSaveDialog();
                    if (file == null) return;
                    
                    editPanel.getTextArea().saveAsciiFileAs(file);
                    JEditTopComponent.this.setName(file.getPath());
                    JEditTopComponent.this.setDisplayName(file.getName());
                    JEditTopComponent.this.setToolTipText(file.getPath());
                }
            };
        }

        public void isSelected(boolean isSelected) {
            CookieSet cookieSet = getCookieSet();
            if (isSelected) {
                cookieSet.assign(OpenCookie.class, openCookieImpl);
                cookieSet.assign(SaveCookie.class, saveCookieImpl);
                cookieSet.assign(SaveAsCookie.class, saveAsCookieImpl);
            } else {
                cookieSet.assign(OpenCookie.class);
                cookieSet.assign(SaveCookie.class);
                cookieSet.assign(SaveAsCookie.class);
            }
        }
    }
}
