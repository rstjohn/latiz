package com.AandR.latiz.listeners;

import java.io.File;

import org.jdom.Element;


import com.AandR.latiz.dev.AbstractPlugin;
import com.AandR.latiz.gui.Connector;

/**
 * @author Dr. Richard St. John
 * @version $Revision$, $Date$
 */
public interface PalettePanelListener {

    abstract public void mouseReleased();

    abstract public void mousePressed();

    abstract public void connectionMade(Connector c);

    abstract public void paletteCleared();

    abstract public void paletteLoaded(File file, Element latizRoot);

    abstract public void paletteSaved(File file, Element latizRoot);

    abstract public void connectionRemoved(Connector c);

    abstract public void connectionWillBeRemoved(Connector c);

    abstract public void pluginDropped(AbstractPlugin p);

    abstract public void pluginRemoved(AbstractPlugin p);

    abstract public void pluginWillBeRemoved(AbstractPlugin p);
}
