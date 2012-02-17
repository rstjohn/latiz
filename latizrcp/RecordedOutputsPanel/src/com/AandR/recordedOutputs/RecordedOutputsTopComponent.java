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
package com.AandR.recordedOutputs;

import com.AandR.latiz.core.lookup.LatizLookup;
import com.AandR.palette.cookies.PaletteSelectionCookie;
import com.AandR.palette.cookies.PluginSelectionCookie;
import com.AandR.palette.paletteScene.PaletteEditor;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serializable;
import java.util.logging.Logger;
import net.miginfocom.swing.MigLayout;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.util.NbBundle;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

/**
 * Top component which displays something.
 */
final class RecordedOutputsTopComponent extends TopComponent implements PropertyChangeListener {

    private static final String PREFERRED_ID = "RecordedOutputsTopComponent";
    private static RecordedOutputsTopComponent instance;
    private Lookup.Result<PaletteSelectionCookie> paletteResult;
    private Lookup.Result<PluginSelectionCookie> pluginResult;
    private PluginSelectionListener pluginSelectionListener;
    private RecordedOutputTreePanel recordedOutputsTreePanel;
    private RecordedOutputsTopPanel recordedOutputTopPanel;

    private RecordedOutputsTopComponent() {
        setName(NbBundle.getMessage(RecordedOutputsTopComponent.class, "CTL_RecordedOutputsTopComponent"));
        setToolTipText(NbBundle.getMessage(RecordedOutputsTopComponent.class, "HINT_RecordedOutputsTopComponent"));
        initialize();
    }

    private void initialize() {
        setLayout(new MigLayout("ins 0"));
        recordedOutputsTreePanel = new RecordedOutputTreePanel();
        //add(recordedOutputsTreePanel, "push, grow");
        recordedOutputTopPanel = new RecordedOutputsTopPanel();
        add(recordedOutputTopPanel, "push, grow");
    }

    /**
     * Gets default instance. Do not use directly: reserved for *.settings files only,
     * i.e. deserialization routines; otherwise you could get a non-deserialized instance.
     * To obtain the singleton instance, use {@link #findInstance}.
     */
    public static synchronized RecordedOutputsTopComponent getDefault() {
        if (instance == null) {
            instance = new RecordedOutputsTopComponent();
        }
        return instance;
    }

    /**
     * Obtain the RecordedOutputsTopComponent instance. Never call {@link #getDefault} directly!
     */
    public static synchronized RecordedOutputsTopComponent findInstance() {
        TopComponent win = WindowManager.getDefault().findTopComponent(PREFERRED_ID);
        if (win == null) {
            Logger.getLogger(RecordedOutputsTopComponent.class.getName()).warning(
                    "Cannot find " + PREFERRED_ID + " component. It will not be located properly in the window system.");
            return getDefault();
        }
        if (win instanceof RecordedOutputsTopComponent) {
            return (RecordedOutputsTopComponent) win;
        }
        Logger.getLogger(RecordedOutputsTopComponent.class.getName()).warning(
                "There seem to be multiple components with the '" + PREFERRED_ID +
                "' ID. That is a potential source of errors and unexpected behavior.");
        return getDefault();
    }

    @Override
    public int getPersistenceType() {
        return TopComponent.PERSISTENCE_ALWAYS;
    }

    @Override
    protected void componentActivated() {
        recordedOutputsTreePanel.saveTreeExpansionState();
    }

    @Override
    protected void componentDeactivated() {
        recordedOutputTopPanel.updateSavedOutputsMap();
        recordedOutputsTreePanel.updateSavedOutputsMap();
        recordedOutputsTreePanel.restoreTreeExpansionState();
    }

    @Override
    public void componentOpened() {
        TopComponent.getRegistry().addPropertyChangeListener(this);
        pluginResult = LatizLookup.getDefault().lookup(new Lookup.Template<PluginSelectionCookie>(PluginSelectionCookie.class));
        if (pluginSelectionListener == null) {
            pluginSelectionListener = new PluginSelectionListener();
        }
        pluginResult.addLookupListener(pluginSelectionListener);
        pluginSelectionListener.resultChanged(null);
    }

    @Override
    public void componentClosed() {
        pluginResult.removeLookupListener(pluginSelectionListener);
        TopComponent.getRegistry().removePropertyChangeListener(this);
    }

    /** replaces this in object stream */
    @Override
    public Object writeReplace() {
        return recordedOutputsTreePanel.writeReplace();
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

        recordedOutputsTreePanel.saveTreeExpansionState();
        recordedOutputsTreePanel.setActiveScene(pe.getScenePanel().getScene());
        recordedOutputsTreePanel.restoreTreeExpansionState();
    }

    /**
     *
     */
    final static class ResolvableHelper implements Serializable {

        private static final long serialVersionUID = 1L;
        private boolean selected;
        private int dividerLocation;

        public ResolvableHelper(boolean selected, int dividerLocation) {
            this.selected = selected;
            this.dividerLocation = dividerLocation;
        }

        public Object readResolve() {
            RecordedOutputsTopComponent result = RecordedOutputsTopComponent.getDefault();
            result.recordedOutputsTreePanel.readResolve(selected, dividerLocation);
            return result;
        }
    }

    private class PluginSelectionListener implements LookupListener {

        @SuppressWarnings(value="unchecked")
        public void resultChanged(LookupEvent lookupEvent) {
            if (lookupEvent == null) {
                PluginSelectionCookie psc = LatizLookup.getDefault().lookup(PluginSelectionCookie.class);
                if (psc != null) {
                    recordedOutputsTreePanel.pluginSelectionChanged(psc.getSelectedPlugin());
                    recordedOutputTopPanel.pluginSelectionChanged(psc.getSelectedPlugin());
                }
                return;
            }

            Lookup.Result<PluginSelectionCookie> src = (Lookup.Result<PluginSelectionCookie>) lookupEvent.getSource();
            for (PluginSelectionCookie sc : src.allInstances()) {
                recordedOutputsTreePanel.pluginSelectionChanged(sc.getSelectedPlugin());
                recordedOutputTopPanel.pluginSelectionChanged(sc.getSelectedPlugin());
                return;
            }
        }
    }
}
