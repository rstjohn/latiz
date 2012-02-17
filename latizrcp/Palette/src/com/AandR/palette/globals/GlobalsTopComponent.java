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

import com.AandR.latiz.core.lookup.LatizLookup;
import com.AandR.palette.cookies.PaletteSelectionCookie;
import com.AandR.palette.paletteScene.IWorkspaceLoaded;
import com.AandR.palette.paletteScene.PaletteEditor;
import com.AandR.palette.paletteScene.PaletteScene;
import java.awt.BorderLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serializable;
import java.util.logging.Logger;
import org.jdom.Element;
import org.openide.util.NbBundle;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

public final class GlobalsTopComponent extends TopComponent implements PropertyChangeListener {

    private static GlobalsTopComponent instance;
    private static final String PREFERRED_ID = "GlobalsTopComponent";
    private GlobalsPanel globalsPanel;

    private GlobalsTopComponent() {
        setName(NbBundle.getMessage(GlobalsTopComponent.class, "CTL_GlobalsTopComponent"));
        setToolTipText(NbBundle.getMessage(GlobalsTopComponent.class, "HINT_GlobalsTopComponent"));
        LatizLookup.getDefault().addToLookup(new WorkspaceLoadedImpl());
        initialize();
    }

    private void initialize() {
        setLayout(new BorderLayout());
        globalsPanel = new GlobalsPanel();
        add(globalsPanel, BorderLayout.CENTER);
    }

    /**
     * Gets default instance. Do not use directly: reserved for *.settings files only,
     * i.e. deserialization routines; otherwise you could get a non-deserialized instance.
     * To obtain the singleton instance, use {@link #findInstance}.
     */
    public static synchronized GlobalsTopComponent getDefault() {
        if (instance == null) {
            instance = new GlobalsTopComponent();
        }
        return instance;
    }

    /**
     * Obtain the GlobalsTopComponent instance. Never call {@link #getDefault} directly!
     */
    public static synchronized GlobalsTopComponent findInstance() {
        TopComponent win = WindowManager.getDefault().findTopComponent(PREFERRED_ID);
        if (win == null) {
            Logger.getLogger(GlobalsTopComponent.class.getName()).warning(
                    "Cannot find " + PREFERRED_ID + " component. It will not be located properly in the window system.");
            return getDefault();
        }
        if (win instanceof GlobalsTopComponent) {
            return (GlobalsTopComponent) win;
        }
        Logger.getLogger(GlobalsTopComponent.class.getName()).warning(
                "There seem to be multiple components with the '" + PREFERRED_ID +
                "' ID. That is a potential source of errors and unexpected behavior.");
        return getDefault();
    }

    public GlobalsPanel getGlobalsPanel() {
        return globalsPanel;
    }

    @Override
    public int getPersistenceType() {
        return TopComponent.PERSISTENCE_ALWAYS;
    }

    @Override
    protected void componentActivated() {
    }

    @Override
    protected void componentDeactivated() {
        globalsPanel.updateGlobalsMap();
    }

    @Override
    public void componentOpened() {
        TopComponent.getRegistry().addPropertyChangeListener(this);
        PaletteSelectionCookie psc = LatizLookup.getDefault().lookup(PaletteSelectionCookie.class);
        if (psc == null) {
            return;
        }
        globalsPanel.setGlobalsMap(psc.getActivePalette().getScenePanel().getPaletteModel().getGlobalsMap());
    }

    @Override
    public void componentClosed() {
        TopComponent.getRegistry().removePropertyChangeListener(this);
    }

    /** replaces this in object stream */
    @Override
    public Object writeReplace() {
        return new ResolvableHelper();
    }

    @Override
    protected String preferredID() {
        return PREFERRED_ID;
    }

    public void propertyChange(PropertyChangeEvent evt) {
        if (!(evt.getNewValue() instanceof PaletteEditor)) {
            return;
        }

        if (!evt.getPropertyName().equals(TopComponent.getRegistry().PROP_ACTIVATED)) {
            return;
        }

        PaletteEditor pe = (PaletteEditor) evt.getNewValue();
        String displayName = pe.getDisplayName();
        setDisplayName("Globals-\"" + displayName + "\"");
        setToolTipText("Globals parameters for \"" + displayName + "\"");
        if(pe.getScenePanel()==null) {
            setDisplayName("Globals");
            setToolTipText("Globals parameters");
            return;
        }
        globalsPanel.setGlobalsMap(pe.getScenePanel().getPaletteModel().getGlobalsMap());
    }

    /**
     * 
     */
    final static class ResolvableHelper implements Serializable {

        private static final long serialVersionUID = 1L;

        public Object readResolve() {
            return GlobalsTopComponent.getDefault();
        }
    }

    private class WorkspaceLoadedImpl implements IWorkspaceLoaded {

        public void load(PaletteScene scene, Element root) {
            globalsPanel.setGlobalsMap(scene.getDefaultPaletteModel().getGlobalsMap());
        }
    }
}
