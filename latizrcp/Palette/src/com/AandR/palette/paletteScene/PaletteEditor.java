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

import com.AandR.latiz.core.lookup.LatizLookup;
import com.AandR.palette.cookies.PaletteSelectionCookie;
import com.AandR.palette.model.PaletteModelStateCookie;
import com.AandR.palette.paletteScene.actions.PaletteCounter;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import org.jdom.JDOMException;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;
import org.openide.windows.TopComponent;

/**
 *
 * @author rstjohn
 */
public class PaletteEditor extends TopComponent {

    protected PaletteScenePanel scenePanel;
    protected PaletteSelectionCookie paletteSelectionCookieImpl;
    protected PaletteModelStateCookie paletteModelStateCookie;

    public PaletteEditor() {
        scenePanel = new PaletteScenePanel();
        paletteModelStateCookie = new PaletteModelStateCookie(scenePanel.getPaletteModel());
        paletteSelectionCookieImpl = new PaletteSelectionCookie() {
            public PaletteEditor getActivePalette() {
                return PaletteEditor.this;
            }
        };
    }

    public PaletteScenePanel getScenePanel() {
        return scenePanel;
    }

    @Override
    public void setName(String name) {
        super.setName(name);
        scenePanel.setName(name);
        setToolTipText(name);
    }

    @Override
    public void setDisplayName(String name) {
        super.setDisplayName(name);
        scenePanel.setDisplayName(name);
    }

    @Override
    public void setHtmlDisplayName(String name) {
        super.setHtmlDisplayName(name);
        scenePanel.setDisplayName(name);
    }

    @Override
    public Image getIcon() {
        return ImageUtilities.loadImage("com/AandR/palette/paletteScene/paletteIcon.gif");
    }

    @Override
    public int getPersistenceType() {
        return PERSISTENCE_NEVER;
    }

    @Override
    protected void componentOpened() {
        TopComponent.getRegistry().addPropertyChangeListener(scenePanel);
        LatizLookup.getDefault().addToLookup(paletteModelStateCookie);
    }

    @Override
    protected void componentClosed() {
        TopComponent.getRegistry().removePropertyChangeListener(scenePanel);
        LatizLookup.getDefault().removeFromLookup(paletteSelectionCookieImpl);
        LatizLookup.getDefault().removeFromLookup(paletteModelStateCookie);
        Lookup.Result<IPaletteClosed> src = LatizLookup.getDefault().lookupResult(IPaletteClosed.class);
        for (IPaletteClosed psc : src.allInstances()) {
            psc.closed(this);
        }
        PaletteCounter pcounter = LatizLookup.getDefault().lookup(PaletteCounter.class);
        pcounter.setCount(0);
        setName("null" + String.valueOf(Math.random()));
        setDisplayName("null" + String.valueOf(Math.random()));
        scenePanel = null;
    }

    @Override
    protected void componentActivated() {
        PaletteSelectionCookie psc = LatizLookup.getDefault().lookup(PaletteSelectionCookie.class);
        if (psc == null || !psc.equals(paletteSelectionCookieImpl)) {
            LatizLookup.getDefault().removeAllFromLookup(PaletteSelectionCookie.class);
            LatizLookup.getDefault().addToLookup(paletteSelectionCookieImpl);
        }
    }

    @Override
    protected void componentDeactivated() {
    }

    public void loadWorkspace(File file) throws IOException, JDOMException {
        scenePanel.getScene().loadWorkspace(file);
    }

    public void loadWorkspace(InputStream inputStream) {
        scenePanel.getScene().loadWorkspace(inputStream);
    }

    public void clearPalette(){
        scenePanel.actionClearPalette();
    }
}
