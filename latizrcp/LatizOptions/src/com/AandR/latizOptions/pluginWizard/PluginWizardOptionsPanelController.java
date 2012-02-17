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
package com.AandR.latizOptions.pluginWizard;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.prefs.Preferences;
import javax.swing.JComponent;
import org.netbeans.spi.options.OptionsPanelController;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;

public final class PluginWizardOptionsPanelController extends OptionsPanelController {
    private PluginWizardOptionsPanel panel;
    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
    private boolean changed;
    public static final String[] DEFAULT_CATEGORIES = new String[] {"File IO","Processors","Plotters"};

    @Override
    public void update() {
        getPanel().load();
        changed = false;
    }

    @Override
    public void applyChanges() {
        getPanel().store();
        changed = false;
    }

    @Override
    public void cancel() {
        // need not do anything special, if no changes have been persisted yet
    }

    @Override
    public boolean isValid() {
        return getPanel().valid();
    }

    @Override
    public boolean isChanged() {
        return changed;
    }

    @Override
    public HelpCtx getHelpCtx() {
        return null; // new HelpCtx("...ID") if you have a help set
    }

    @Override
    public JComponent getComponent(Lookup masterLookup) {
        return getPanel();
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener l) {
        pcs.addPropertyChangeListener(l);
    }

    @Override
    public void removePropertyChangeListener(PropertyChangeListener l) {
        pcs.removePropertyChangeListener(l);
    }

    private PluginWizardOptionsPanel getPanel() {
        if (panel == null) {
            panel = new PluginWizardOptionsPanel(this);
        }
        return panel;
    }

    void changed() {
        if (!changed) {
            changed = true;
            pcs.firePropertyChange(OptionsPanelController.PROP_CHANGED, false, true);
        }
        pcs.firePropertyChange(OptionsPanelController.PROP_VALID, null, null);
    }

    // Variables declaration - do not modify
    // End of variables declaration
    /**
     * Returns an array of property values as stored in the setPropertyList method.
     * @param listString
     * @return
     */
    public static String[] getPropertyList(Preferences pref, String key, String[] defaultList) {
        if (pref.get(key, "") == null || pref.get(key, "").trim().length() == 0) {
            setPropertyList(pref, key, defaultList);
            return defaultList;
        }

        StringTokenizer tokenizer = new StringTokenizer(pref.get(key, ""), "\r\n");
        ArrayList<String> items = new ArrayList<String>();
        while (tokenizer.hasMoreTokens()) {
            items.add(tokenizer.nextToken());
        }
        String[] list = new String[items.size()];
        items.toArray(list);
        return list;
    }

    /**
     * Sets the property to a list of strings. The list of strings are concatenated with \r\n delimiters.
     * @param key
     * @param list
     * @return
     */
    public static synchronized void setPropertyList(Preferences pref, String key, String[] list) {
        String listString = "";
        for (int i = 0; i < list.length; i++) {
            listString += list[i] + "\r\n";
        }
        pref.put(key, listString);
    }
}
