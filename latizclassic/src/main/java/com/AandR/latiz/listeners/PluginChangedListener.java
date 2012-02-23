package com.AandR.latiz.listeners;

import com.AandR.latiz.dev.AbstractPlugin;

/**
 * @author Dr. Richard St. John
 * @version $Revision$, $Date$
 */
public interface PluginChangedListener {

    public void pluginNameChanged(String oldName, String newName);

    public void pluginOutputsChanged(AbstractPlugin p);

    public void pluginInputsChanged(AbstractPlugin p);

    public void pluginDisplayChanged(AbstractPlugin p);
}
