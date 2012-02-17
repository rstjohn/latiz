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
package com.AandR.latizOptions.palette;

import java.awt.Color;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import javax.swing.JComponent;
import org.netbeans.spi.options.OptionsPanelController;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;

public final class LatizPaletteOptionsPanelController extends OptionsPanelController {
    public static final String DEFAULT_FOREGROUND = "#" + Integer.toHexString(Color.BLACK.getRGB()).substring(1);

    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);

    private boolean changed;

    private LatizPaletteOptionsPanel panel;


    public void update() {
        getPanel().load();
        changed = false;
    }


    public void applyChanges() {
        getPanel().store();
        changed = false;
    }


    public void cancel() {
        // need not do anything special, if no changes have been persisted yet
    }

    public boolean isValid() {
        return getPanel().valid();
    }


    public boolean isChanged() {
        return changed;
    }


    public HelpCtx getHelpCtx() {
        return null; // new HelpCtx("...ID") if you have a help set
    }


    public JComponent getComponent(Lookup masterLookup) {
        return getPanel();
    }


    public void addPropertyChangeListener(PropertyChangeListener l) {
        pcs.addPropertyChangeListener(l);
    }


    public void removePropertyChangeListener(PropertyChangeListener l) {
        pcs.removePropertyChangeListener(l);
    }


    private LatizPaletteOptionsPanel getPanel() {
        if (panel == null) {
            panel = new LatizPaletteOptionsPanel(this);
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
}
