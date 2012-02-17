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

import com.AandR.palette.plugin.AbstractPlugin;
import com.AandR.palette.runtime.exceptions.InputNotConnectedException;
import com.AandR.palette.runtime.exceptions.KeyNotFoundException;
import com.AandR.palette.runtime.exceptions.NullValueException;
import com.AandR.palette.runtime.exceptions.PluginRuntimeException;
import java.util.ArrayList;

/**
 *
 * @author Aaron Masino
 */
public interface IOutputInputHandler {

    /**
     * gets the current value object of the Input that corresponds to the inputKey in the plugins
     * inputDataMap. Should make sure the input is up to date
     * @param inputKey
     * @param isInputUpdateAcknowledged
     * @return
     * @throws com.AandR.pluginManager.runtime.exceptions.KeyNotFoundException
     * @throws com.AandR.pluginManager.runtime.exceptions.NullValueException
     * @throws com.AandR.pluginManager.runtime.exceptions.InputNotConnectedException
     */
    public Object getInput(String inputKey, boolean isInputUpdateAcknowledged) throws KeyNotFoundException,
            NullValueException, InputNotConnectedException;

    /**
     * gets the current value object of the Input that corresponds to the inputKey in the plugins
     * inputDataMap. Should make sure the input is up to date
     * @param inputKey
     * @return
     * @throws com.AandR.pluginManager.runtime.exceptions.KeyNotFoundException
     * @throws com.AandR.pluginManager.runtime.exceptions.NullValueException
     * @throws com.AandR.pluginManager.runtime.exceptions.InputNotConnectedException
     */
    public Object getInput(String inputKey) throws KeyNotFoundException, NullValueException,
            InputNotConnectedException;

    /**
     * updates teh value object of the Output object in the plugins outputDataMap corresponding to the outputKey
     * @param outputKey
     * @param value
     * @throws com.AandR.pluginManager.runtime.exceptions.KeyNotFoundException
     */
    public void updateOutput(String outputKey, Object value) throws KeyNotFoundException;

    /**
     * returns the value object of the Input in the plugins inputDataMap corresponding to the inputKey
     * the value is that for the recallable time specified by time
     * @param inputKey
     * @param time
     * @return
     * @throws com.AandR.pluginManager.runtime.exceptions.KeyNotFoundException
     * @throws com.AandR.pluginManager.runtime.exceptions.NullValueException
     * @throws com.AandR.pluginManager.runtime.exceptions.InputNotConnectedException
     */
    public Object getRecallableInput(String inputKey, double time) throws KeyNotFoundException,
            NullValueException, InputNotConnectedException;

    /** used to get a list of previously stored inputs. Returns at most "count" stored inputs. The
     * inputs will be those whose times are closest to time without being over
     * @param inputKey
     * @param time
     * @param count
     * @return
     * @throws PluginRuntimeException
     */
    public ArrayList<Object> getRecallableInput(String inputKey, double time, int count) throws PluginRuntimeException;

    /**
     * returns the value Object from the Output in the plugins outputDataMap corresponding to the outputKey
     * @param outputKey
     * @return
     * @throws com.AandR.pluginManager.runtime.exceptions.KeyNotFoundException
     */
    public Object getOutput(String outputKey) throws KeyNotFoundException ;

    /**
     * used to get a previously stored output value
     * @param outputKey
     * @param time
     * @return
     * @throws com.AandR.pluginManager.runtime.exceptions.PluginRuntimeException
     * @throws com.AandR.pluginManager.runtime.exceptions.KeyNotFoundException
     * @throws com.AandR.pluginManager.runtime.exceptions.NullValueException
     */
    public Object getRecallableOutput(String outputKey, double time) throws PluginRuntimeException, KeyNotFoundException, NullValueException;

    /**
     * used to get a list of previously stored outputs. Returns at most "count" stored inputs. The
	  * inputs will be those whose times are closest to time without being over 
     * @param outputKey
     * @param time
     * @param count
     * @return
     * @throws com.AandR.pluginManager.runtime.exceptions.PluginRuntimeException
     * @throws com.AandR.pluginManager.runtime.exceptions.KeyNotFoundException
     * @throws com.AandR.pluginManager.runtime.exceptions.NullValueException
     */
    public ArrayList<Object> getRecallableOutput(String outputKey, double time, int count)  throws PluginRuntimeException, KeyNotFoundException, NullValueException;

    /**
     * sets the AbstractPlugin that this IOutputInputHandler belongs to
     * @param plugin
     */
    public void setAbstractPlugin(AbstractPlugin plugin);
}
