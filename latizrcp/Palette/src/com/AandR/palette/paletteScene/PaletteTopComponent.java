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
package com.AandR.palette.paletteScene;

import com.AandR.latiz.core.cookies.NewDocumentCookie;
import com.AandR.latiz.core.cookies.SaveAsCookie;
import com.AandR.latiz.core.lookup.LatizLookup;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import net.miginfocom.swing.MigLayout;
import org.openide.cookies.OpenCookie;
import org.openide.cookies.SaveCookie;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.CookieSet;
import org.openide.nodes.Node;
import org.openide.util.NbBundle;
import org.openide.windows.IOProvider;
import org.openide.windows.TopComponent;

public class PaletteTopComponent extends PaletteEditor {

    private PaletteClearedImpl paletteClearedImpl;
    private PaletteSavedImpl paletteSavedImpl;
    private PaletteSelectionNode paletteSelectionNode;
    private File seriliazedFile;

    public PaletteTopComponent() {
        paletteClearedImpl = new PaletteClearedImpl();
        paletteSelectionNode = new PaletteSelectionNode();
        paletteSavedImpl = new PaletteSavedImpl();
        
        setActivatedNodes(new Node[]{paletteSelectionNode});
        setLayout(new MigLayout("ins 0, fill"));
        add(scenePanel, "push, grow");
        setName(NbBundle.getMessage(PaletteTopComponent.class, "CTL_PaletteTopComponent"));
    }

    @Override
    protected void componentActivated() {
        super.componentActivated();
        paletteSelectionNode.isSelected(true);
    }

    @Override
    protected void componentDeactivated() {
        super.componentDeactivated();
        paletteSelectionNode.isSelected(false);
    }

    @Override
    public void componentOpened() {
        super.componentOpened();
        LatizLookup.getDefault().addToLookup(paletteClearedImpl);
        LatizLookup.getDefault().addToLookup(paletteSavedImpl);
    }

    @Override
    public void componentClosed() {
        LatizLookup.getDefault().removeFromLookup(paletteClearedImpl);
        LatizLookup.getDefault().removeFromLookup(paletteSavedImpl);
        super.componentClosed();
    }

    @Override
    protected void componentShowing() {
        if(seriliazedFile!=null && seriliazedFile.exists()) {
            try {
                setName(seriliazedFile.getPath());
                setDisplayName(seriliazedFile.getName());
                scenePanel.getScene().loadWorkspace(new FileInputStream(seriliazedFile), false);
                scenePanel.setCurrentFile(seriliazedFile);
            } catch (Exception ex) {
            }
            seriliazedFile=null;
            return;
        }
    }

    @Override
    public int getPersistenceType() {
        return TopComponent.PERSISTENCE_NEVER;
    }

    @Override
    public void readExternal(ObjectInput arg0) throws IOException, ClassNotFoundException {
        super.readExternal(arg0);
        String n = (String) arg0.readObject();
        String dn = (String) arg0.readObject();
        seriliazedFile = (File) arg0.readObject();
    }

    @Override
    public void writeExternal(ObjectOutput arg0) throws IOException {
        super.writeExternal(arg0);
        arg0.writeObject(getName());
        arg0.writeObject(getDisplayName());
        arg0.writeObject(scenePanel.getCurrentFile());
    }

    /**
     *
     */
    public class PaletteSelectionNode extends AbstractNode {

        private OpenCookie openCookieImpl;
        private SaveCookie saveCookieImpl;
        private SaveAsCookie saveAsCookieImpl;
        private NewDocumentCookie newDocumentCookieImpl;

        public PaletteSelectionNode() {
            super(Children.LEAF);
            openCookieImpl = new OpenCookie() {
                public void open() {
                    scenePanel.actionLoad();
                }
            };

            saveCookieImpl = new SaveCookie() {
                public void save() throws IOException {
                    scenePanel.actionSave();
                }
            };

            saveAsCookieImpl = new SaveAsCookie() {
                public void save() throws IOException {
                    scenePanel.actionSaveAs();
                }
            };

            newDocumentCookieImpl = new NewDocumentCookie() {
                public void createDocument() {
                    IOProvider.getDefault().getIO("Output", false).getOut().println("Create New Palette");
                }
            };
        }

        public void isSelected(boolean isSelected) {
            CookieSet cookieSet = getCookieSet();
            if (isSelected) {
                //If the text is modified we implement SaveCookie and add the implementation to the cookieset:
                cookieSet.assign(OpenCookie.class, openCookieImpl);
                cookieSet.assign(SaveCookie.class, saveCookieImpl);
                cookieSet.assign(SaveAsCookie.class, saveAsCookieImpl);
                cookieSet.assign(NewDocumentCookie.class, newDocumentCookieImpl);
            } else {
                //Otherwise, we make no assignment and the SaveCookie is not made available:
                cookieSet.assign(OpenCookie.class);
                cookieSet.assign(SaveCookie.class);
                cookieSet.assign(SaveAsCookie.class);
                cookieSet.assign(NewDocumentCookie.class);
            }
        }
    }

    /**
     * 
     */
    private class PaletteClearedImpl implements IPaletteCleared {

        public void paletteCleared(String paletteName) {
            if(!paletteName.equals(PaletteTopComponent.this.getName())) {
                return;
            }
            String name = PaletteUtilities.createNewPaletteName("Untitled");
            setName(name);
            setDisplayName(name);
        }
    }

    /**
     * 
     */
    private class PaletteSavedImpl implements IPaletteSaved {
        public void save(String oldName, File newFile) {
            if(!getName().equals(oldName)) {
                return;
            }
            setName(newFile.getPath());
            setDisplayName(newFile.getName());
        }
    }
}