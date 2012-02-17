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
package com.AandR.palette.model;

import com.AandR.palette.dataWriter.DefaultSavedOutputsImpl;
import com.AandR.palette.globals.GlobalVariable;
import com.AandR.palette.plugin.AbstractPlugin;
import com.AandR.palette.plugin.data.IOutputObserver;
import com.AandR.palette.plugin.data.Output;
import com.AandR.palette.runtime.IRuntimeObserver;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Aaron Masino
 */
abstract public class AbstractPaletteModel {
    private String name, displayName;

    /**
     * returns a map of the pluginName, plugin in this model
     * @return
     */
    abstract public Map<String, AbstractPlugin> getPlugins();

    /**
     * returns a List of Strings of the form SourcePluginName::OutputName>TargetPluginName::InputName
     * @return
     */
    abstract public List<String> getConnections();

    /**
     * register the iRuntimeObserver as with this PaletteModel
     * @param iRuntimeObserver
     */
    abstract public void registerRuntimeObserver(IRuntimeObserver iRuntimeObserver);

    /**
     * remove iRuntimeObserver
     * @param iRuntimeObserver
     */
    abstract public void removeRuntimeObserver(IRuntimeObserver iRuntimeObserver);

    /**
     * returns the List this PaletteModel's runtime observers
     * @return
     */
    abstract public List<IRuntimeObserver> getRuntimeObservers();

    /**
     * Registers iPluginOutputObserver as an observer of all Output objects in the model
     * @param iPluginOutputObserver
     */
    abstract public void registerOutputObserver(IOutputObserver iOutputObserver);

    /**
     * Registers iPluginOutputObserver as an observer of the specified output
     * @param iOutputObserver
     * @param pluginName
     * @param outputKey
     */
    abstract public void registerOutputObserver(IOutputObserver iOutputObserver, String pluginName, String outputKey);

    /**
     * removes iOutputObserver as an observer from all Outputs
     * @param iPluginOutputObserver
     */
    abstract public void removeOutputObserver(IOutputObserver iOutputObserver);

    /**
     * Notifies all IPluginOutputObservers that output has been updated
     * @param output
     */
    abstract public void notifyOutputUpdated(Output output);

    /**
     * returns the List of observers of the given Output
     * @param pluginName
     * @param outputKey
     * @return
     */
    abstract public List<IOutputObserver> getOutputObservers(String pluginName, String outputKey);

    /**
     * Used to get the globals map.
     * @return
     */
    abstract public LinkedHashMap<String, GlobalVariable> getGlobalsMap();

    /**
     * provides the Map that can contains ISavedOutputs
     * @return
     */
    abstract public HashMap<String, LinkedHashMap<String, DefaultSavedOutputsImpl>> getSavedOutputsMap();

    /**
     * returns all output observers.
     * @return
     */
    abstract public Map<IOutputObserver, ArrayList<String>> getOutputObservers();

    /**
     * 
     * @return
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     *
     * @param displayName
     */
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    /**
     *
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * 
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

}
