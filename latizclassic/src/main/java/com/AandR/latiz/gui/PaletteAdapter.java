package com.AandR.latiz.gui;

import java.io.File;

import org.jdom.Element;


import com.AandR.latiz.dev.AbstractPlugin;
import com.AandR.latiz.listeners.PalettePanelListener;
import com.AandR.latiz.listeners.PluginChangedListener;

/**
 * @author Dr. Richard St. John
 * @version $Revision$, $Date$
 */
public class PaletteAdapter implements PalettePanelListener, PluginChangedListener {

    public void connectionMade(Connector c) {
    }

    public void connectionRemoved(Connector c) {
    }

    public void connectionWillBeRemoved(Connector c) {
    }

    public void mousePressed() {
    }

    public void mouseReleased() {
    }

    public void pluginDropped(AbstractPlugin p) {
    }

    public void pluginRemoved(AbstractPlugin p) {
    }

    public void pluginWillBeRemoved(AbstractPlugin p) {
    }

    public void pluginNameChanged(String oldName, String newName) {
    }

    public void pluginOutputsChanged(AbstractPlugin p) {
    }

    public void pluginInputsChanged(AbstractPlugin p) {
    }

    public void paletteLoaded(File file, Element latizRoot) {
    }

    public void paletteSaved(File file, Element latizRoot) {
    }

    public void paletteCleared() {
    }

    public void pluginDisplayChanged(AbstractPlugin p) {
    }
}
