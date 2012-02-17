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
package com.AandR.palette.connectionPanel;

import com.AandR.latiz.core.lookup.LatizLookup;
import com.AandR.palette.cookies.PluginSelectionCookie;
import com.AandR.palette.paletteScene.PaletteEditor;
import java.awt.BorderLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serializable;
import java.util.logging.Logger;
import org.openide.util.NbBundle;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;
//import org.openide.util.Utilities;

/**
 * Top component which displays something.
 */
public final class IoConnectionPanelTopComponent extends TopComponent implements PropertyChangeListener {

    private static IoConnectionPanelTopComponent instance;
    private static final String PREFERRED_ID = "IoConnectionPanelTopComponent";
    IoConnectionPanel connectionPanel;

    private IoConnectionPanelTopComponent() {
        setName(NbBundle.getMessage(IoConnectionPanelTopComponent.class, "CTL_IoConnectionPanelTopComponent"));
        setToolTipText(NbBundle.getMessage(IoConnectionPanelTopComponent.class, "HINT_IoConnectionPanelTopComponent"));
        setLayout(new BorderLayout());
        add(connectionPanel = new IoConnectionPanel());
//        LatizLookup.getDefault().addToLookup(new PluginIOChangedImpl());
//        LatizLookup.getDefault().addToLookup(new WorkspaceLoadedImpl());
//        LatizLookup.getDefault().addToLookup(new PaletteClosedImpl());
    }

    public static synchronized IoConnectionPanelTopComponent getDefault() {
        if (instance == null) {
            instance = new IoConnectionPanelTopComponent();
        }
        return instance;
    }

    /**
     * Obtain the IoConnectionPanelTopComponent instance. Never call {@link #getDefault} directly!
     */
    public static synchronized IoConnectionPanelTopComponent findInstance() {
        TopComponent win = WindowManager.getDefault().findTopComponent(PREFERRED_ID);
        if (win == null) {
            Logger.getLogger(IoConnectionPanelTopComponent.class.getName()).warning(
                    "Cannot find " + PREFERRED_ID + " component. It will not be located properly in the window system.");
            return getDefault();
        }
        if (win instanceof IoConnectionPanelTopComponent) {
            return (IoConnectionPanelTopComponent) win;
        }
        Logger.getLogger(IoConnectionPanelTopComponent.class.getName()).warning(
                "There seem to be multiple components with the '" + PREFERRED_ID +
                "' ID. That is a potential source of errors and unexpected behavior.");
        return getDefault();
    }

    @Override
    public int getPersistenceType() {
        return TopComponent.PERSISTENCE_ONLY_OPENED;
    }

    @Override
    public void componentOpened() {
        TopComponent.getRegistry().addPropertyChangeListener(this);
        connectionPanel.addLookupListener();
    }

    @Override
    public void componentClosed() {
        connectionPanel.removeLookupListener();
        TopComponent.getRegistry().removePropertyChangeListener(this);
    }

    public void propertyChange(PropertyChangeEvent evt) {
        if (!(evt.getNewValue() instanceof PaletteEditor)) {
            return;
        }

        if (!evt.getPropertyName().equals(TopComponent.getRegistry().PROP_ACTIVATED)) {
            return;
        }

        PaletteEditor pe = (PaletteEditor) evt.getNewValue();
        if (pe != null) {
            connectionPanel.setActiveScenePanel(pe.getScenePanel());
            PluginSelectionCookie psc = LatizLookup.getDefault().lookup(PluginSelectionCookie.class);
            if (psc != null) {
                connectionPanel.setSourcePlugin(psc.getSelectedPlugin());
            }
            return;
        }
        connectionPanel.showNullPanel();
        return;

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

    final static class ResolvableHelper implements Serializable {

        private static final long serialVersionUID = 1L;

        public Object readResolve() {
            return IoConnectionPanelTopComponent.getDefault();
        }
    }
}
