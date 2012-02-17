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
package com.AandR.palette.runtime;

import com.AandR.palette.model.AbstractPaletteModel;
import com.AandR.palette.runtime.exceptions.PluginRuntimeException;
import com.AandR.palette.plugin.AbstractPlugin;
import com.AandR.palette.plugin.data.Input;
import java.util.ArrayList;
import javax.swing.JComponent;
import org.jdom.Element;

/**
 *
 * @author Aaron Masino
 */
public interface IRuntimeManager {

    /**
     * Set the IPluginReceiveHandler for each AbstractPlugin
     */
    public void initializePluginsReceiveHandler();

    /**
     * Set the IOutputInputHandler for each AbstractPlugin
     */
    public void initializePluginOutputInputHandler();

    /**
     * Set the OutputObserversList in each Output. The list of observers is available from the AbstractPaletteModel
     */
    public void initializeOutputObservers();

    /**
     * starts the execution of the model
     * @param plugins
     * @param stopTime
     * @param pluginOutgoingConnectorMaps
     */
    public void executePaletteModel(AbstractPaletteModel iPaletteModel);

	/**
     *
     */
    public void requestSimulationCancellation();

    /**
     * Used by plugins to schedule events
     * @param event
     */
    public void scheduleEvent(PluginRuntimeEvent event);

    /**
     * used by plugins to get current time
     * @return
     */
	public double currentTime();

    /**
     * used by plugins to get the next event for the iput plugin
     * @param p
     * @return
     */
	public PluginRuntimeEvent getNextEvent(AbstractPlugin p);

    /**
     * used by a plugin to get all of its currently scheduled events
     * @param p
     * @return
     */
	public ArrayList<PluginRuntimeEvent> getAllEvents(AbstractPlugin p);

    /**
     * cancel the input event
     * @param e
     */
	public void cancelEvent(PluginRuntimeEvent e);

    /**
     * used to notify this IRuntimeManager that execution has failed. It should also notify all IRuntimeObservers
     * @param e
     */
	public void notifyRuntimeFailure(PluginRuntimeException e);

    /**
     * should notify all the IRuntimeMangers that run will begin. Should be called immeadiately before the
     * run is executed by the runtime manager, after all AbstractPlugins have been initialized
     */
    public void notifyRuntimeObserversSetupRun();

    /**
     * should notify all the IRuntimeManagers that run has finished. Should be called called
     * immeadiately after the run is completed, after all AbstractPlugins have been deinitialzied
     */
    public void notifyRuntimeObserversTearDown();

    /**
     * Used by IReceiveHandlers to ask validate the path when an handling a receiveOutputRequestNotification
     * Can be used to determine if a circular request path has occurred.
     * @param input
     */
	public void validateOutputRequest(Input input);

    /**
     * return the GUI component used to change runtime manager settings
     * @return
     */
    abstract public JComponent getParameterPanel();

    /**
	 * Used to load the parameter inputs as stored in XML from a previously
	 * saved Latiz workspace
	 *
	 * @param e
	 */
	abstract public void loadSavedWorkspaceParameters(Element e);

	/**
	 * Used by Latiz to get a savable XML element with the parameter inputs
	 *
	 * @return e
	 */
	abstract public Element createWorkspaceParameters();

}
