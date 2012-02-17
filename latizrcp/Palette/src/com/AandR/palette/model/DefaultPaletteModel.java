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
public class DefaultPaletteModel extends AbstractPaletteModel {

    private static final String allOutputs = "ALLOUTPUTS";
    private ArrayList<IRuntimeObserver> runtimeObservers;
    private HashMap<IOutputObserver, ArrayList<String>> outputObserverMap;
    private Map<String, AbstractPlugin> plugins;
    private HashMap<String, LinkedHashMap<String, DefaultSavedOutputsImpl>> savedOutputsMap;
    private double stopTime;
    private List<String> connections;
    private LinkedHashMap<String, GlobalVariable> globalsMap;

    public DefaultPaletteModel() {
        outputObserverMap = new HashMap<IOutputObserver, ArrayList<String>>();
        runtimeObservers = new ArrayList<IRuntimeObserver>();
        plugins = new HashMap<String, AbstractPlugin>();
        connections = new ArrayList<String>();
        globalsMap = new LinkedHashMap<String, GlobalVariable>();
        savedOutputsMap = new HashMap<String, LinkedHashMap<String, DefaultSavedOutputsImpl>>();
    }

    public Map<String, AbstractPlugin> getPlugins() {
        return plugins;
    }

    public List<String> getConnections() {
        return connections;
    }

    public void setStopTime(double time) {
        stopTime = time;
    }

    public double getStopTime() {
        return stopTime;
    }

    private ArrayList<String> getObserverList(IOutputObserver iOuputObserver) {
        ArrayList<String> thisList = outputObserverMap.get(iOuputObserver);
        if (thisList == null) {
            thisList = new ArrayList<String>();
            outputObserverMap.put(iOuputObserver, thisList);
        }
        return thisList;
    }

    public void registerOutputObserver(IOutputObserver iOutputObserver) {
        ArrayList<String> thisList = getObserverList(iOutputObserver);
        thisList.clear();
        thisList.add(allOutputs);
    }

    public void registerOutputObserver(IOutputObserver iOutputObserver, String pluginName, String outputKey) {
        ArrayList<String> thisList = getObserverList(iOutputObserver);
        if (thisList.size() == 0) {
            thisList.add(pluginName + "::" + outputKey);
        } else if (thisList.get(0).equals(allOutputs)) {
            return;
        } else {
            thisList.add(pluginName + "::" + outputKey);
        }
    }

    public void removeOutputObserver(IOutputObserver iOutputObserver) {
        outputObserverMap.remove(iOutputObserver);
    }

    public void notifyOutputUpdated(Output output) {
        ArrayList<String> observedOutputs;
        String outputKey = output.getOutputPlugin().getName() + "::" + output.getKey();
        for (IOutputObserver ioo : outputObserverMap.keySet()) {
            observedOutputs = outputObserverMap.get(ioo);
            for (String key : observedOutputs) {
                if (key.equals(allOutputs) || key.equals(outputKey)) {
                    ioo.notfyOutputUpdated(output);
                }
            }
        }
    }

    public List<IOutputObserver> getOutputObservers(String pluginName, String outputKey) {
        ArrayList<IOutputObserver> outputObservers = new ArrayList<IOutputObserver>();
        String key = pluginName + "::" + outputKey;
        ArrayList<String> observedOutputs;
        for (IOutputObserver ioo : outputObserverMap.keySet()) {  //<IOutputObserver, ArrayList<String>>
            observedOutputs = outputObserverMap.get(ioo);
            for (String nextKey : observedOutputs) {
                if (nextKey.equals(allOutputs) || nextKey.equals(key)) {
                    outputObservers.add(ioo);
                }
            }
        }
        return outputObservers;
    }

    public void registerRuntimeObserver(IRuntimeObserver iRuntimeObserver) {
        if (!runtimeObservers.contains(iRuntimeObserver)) {
            runtimeObservers.add(iRuntimeObserver);
        }
    }

    public void removeRuntimeObserver(IRuntimeObserver iRuntimeObserver) {
        runtimeObservers.remove(iRuntimeObserver);
    }

    public List<IRuntimeObserver> getRuntimeObservers() {
        return runtimeObservers;
    }

    public HashMap<String, LinkedHashMap<String, DefaultSavedOutputsImpl>> getSavedOutputsMap() {
        return savedOutputsMap;
    }
    
    public Map<IOutputObserver, ArrayList<String>> getOutputObservers() {
        return outputObserverMap;
    }

    public LinkedHashMap<String, GlobalVariable> getGlobalsMap() {
        return globalsMap;
    }
}
