/**
 * 
 */
package com.AandR.latiz.interfaces;

import java.util.HashMap;


import com.AandR.latiz.core.Connection;
import com.AandR.latiz.dev.AbstractPlugin;
import com.AandR.latiz.gui.Connector;

/**
 * @author Aaron Masino
 * @version Oct 30, 2007 8:03:18 PM <br>
 *
 * Comments:
 *
 */
public interface ParentPluginInterface {

    public AbstractPlugin getParentPlugin();

    public Connection getInputOutputConnection();

    public HashMap<String, String> getInputOutputMap();

    public Connector getIncomingConnector();
}
