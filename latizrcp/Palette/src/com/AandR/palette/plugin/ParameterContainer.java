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
package com.AandR.palette.plugin;

import com.AandR.latiz.core.lookup.LatizLookup;
import com.AandR.palette.ContainerTopComponent;
import com.AandR.palette.cookies.PluginSelectionCookie;
import com.AandR.palette.paletteScene.IPaletteClosed;
import com.AandR.palette.paletteScene.IPaletteSaved;
import com.AandR.palette.paletteScene.PaletteEditor;
import java.awt.Image;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.Random;
import javax.swing.JComponent;
import net.miginfocom.swing.MigLayout;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

/**
 *
 * @author stjohnr
 */
public class ParameterContainer extends ContainerTopComponent implements PropertyChangeListener {

    private boolean canClose = false;
    private AbstractPlugin plugin;
    private Lookup.Result pluginResult;
    private PaletteClosedImpl paletteClosedImpl;
    private PaletteSavedImpl paletteSavedImpl;
    private PluginSelectionListener pluginSelectionListener;

    public ParameterContainer() {
    }
    
    public ParameterContainer(JComponent parameterPanel) {
        pluginSelectionListener = new PluginSelectionListener();
        pluginResult = LatizLookup.getDefault().getLookup().lookup(new Lookup.Template<PluginSelectionCookie>(PluginSelectionCookie.class));
        pluginResult.addLookupListener(pluginSelectionListener);
        
        paletteClosedImpl = new PaletteClosedImpl();
        paletteSavedImpl = new PaletteSavedImpl();
        
        setLayout(new MigLayout());
        add(parameterPanel, "push, grow");
        TopComponent.getRegistry().addPropertyChangeListener(this);
    }

    public void setPlugin(AbstractPlugin plugin) {
        this.plugin = plugin;
        setName(plugin.getPaletteModelImpl().getName() + "::" + plugin.getName());
        setDisplayName(plugin.getName());
        setToolTipText(getName());
    }

    public AbstractPlugin getPlugin() {
        return plugin;
    }

    @Override
    public int getPersistenceType() {
        return PERSISTENCE_NEVER;
    }

    @Override
    public String preferredID() {
        return getName();
    }

    public void rename(String oldName, String newName) {
        String[] nameSplit = oldName.split("::");
        setName(nameSplit[0] + "::" + newName);
        setDisplayName(newName);
        setToolTipText(getName());
    }

    @Override
    public boolean canClose() {
        return canClose;
    }

    public boolean forceClose() {
        canClose = true;
        boolean success = close();
        canClose = false;
        return success;
    }

    @Override
    public void open() {
        if(plugin.isParameterPanelHidden()) return;
        super.open();
    }

    @Override
    protected void componentOpened() {
        if(plugin.isParameterPanelHidden()) return;
        LatizLookup.getDefault().addToLookup(paletteClosedImpl);
        LatizLookup.getDefault().addToLookup(paletteSavedImpl);
    }

    @Override
    protected void componentClosed() {
        LatizLookup.getDefault().removeFromLookup(paletteClosedImpl);
        LatizLookup.getDefault().removeFromLookup(paletteSavedImpl);
    }

    @Override
    public Image getIcon() {
        return ImageUtilities.loadImage("com/AandR/palette/plugin/parameterPanelIcon.gif");
    }

    public void propertyChange(PropertyChangeEvent evt) {
        if (!evt.getPropertyName().equals(TopComponent.getRegistry().PROP_ACTIVATED)) {
            return;
        }

        Object newObject = evt.getNewValue();
        if(!(newObject instanceof TopComponent)) {
            return;
        }

        TopComponent newTC = (TopComponent) newObject;
        boolean isEditor = WindowManager.getDefault().isEditorTopComponent(newTC);
        boolean isPaletteEditor = newTC instanceof PaletteEditor;
        boolean isContainerTopComponent = newTC instanceof ContainerTopComponent;
                
        if(isEditor && !isContainerTopComponent && !isPaletteEditor) {
            return;
        }

        if(!isEditor || isContainerTopComponent) {
            return;
        }

        if(!isPaletteEditor) {
            forceClose();
            return;
        }

        if (!getName().startsWith(((PaletteEditor) (evt.getNewValue())).getName() + "::")) {
            forceClose();
        } else {
            open();
        }
    }

    /**
     * 
     */
    @SuppressWarnings(value="unchecked")
    private class PluginSelectionListener implements LookupListener {
        public void resultChanged(LookupEvent lookupEvent) {
            if (lookupEvent == null) {
                return;
            }
            Lookup.Result<PluginSelectionCookie> src = (Lookup.Result<PluginSelectionCookie>) lookupEvent.getSource();
            for(PluginSelectionCookie psc : src.allInstances()) {
                AbstractPlugin plugin = psc.getSelectedPlugin();
                if (plugin == null || !plugin.getName().equals(ParameterContainer.this.plugin.getName()) || plugin.isParameterPanelHidden()) {
                    return;
                }
                requestActive();
            }
        }
    }

    /**
     * 
     */
    private class PaletteClosedImpl implements IPaletteClosed {
        private Random random = new Random();
        public void closed(PaletteEditor paletteEditor) {
            if (!getName().startsWith(paletteEditor.getName() + "::")) return;
            
            setName("null" + Long.toHexString(random.nextLong()));
            TopComponent.getRegistry().removePropertyChangeListener(ParameterContainer.this);
            LatizLookup.getDefault().removeFromLookup(paletteClosedImpl);
            pluginResult.removeLookupListener(pluginSelectionListener);
            pluginResult = null;
            pluginSelectionListener = null;
            plugin = null;
            forceClose();
        }
    }

    private class PaletteSavedImpl implements IPaletteSaved {
        public void save(String oldName, File newFile) {
            if(!getName().startsWith(oldName + "::")) {
                return;
            }
            setName(newFile.getPath() + "::" + plugin.getName());
            setToolTipText(getName());
        }
    }
}
