package com.AandR.latiz.listeners;

/**
 * @author Dr. Richard St. John
 * @version $Revision$, $Date$
 */
public interface ConnectionPanelListener {

    public void ioConnectionMade(String pluginIOconnectionName);

    public void ioConnectionRemoved(String pluginIOconnectionName);
}
