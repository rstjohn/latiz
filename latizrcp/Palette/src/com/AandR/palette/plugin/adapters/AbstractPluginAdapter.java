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
package com.AandR.palette.plugin.adapters;

import com.AandR.latiz.core.lookup.LatizLookup;
import com.AandR.library.utility.CloneWorker;
import com.AandR.library.utility.FastByteArrayOutputStream;
import com.AandR.palette.plugin.AbstractPlugin;
import com.AandR.palette.plugin.IPluginIOChanged;
import com.AandR.palette.plugin.data.Input;
import com.AandR.palette.plugin.data.Output;
import com.AandR.palette.runtime.PluginRuntimeEvent;
import com.AandR.palette.runtime.exceptions.InputNotConnectedException;
import com.AandR.palette.runtime.exceptions.KeyNotFoundException;
import com.AandR.palette.runtime.exceptions.NullValueException;
import com.AandR.palette.runtime.exceptions.PluginRuntimeException;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;

/**
 *
 * @author Aaron Masino
 */
public abstract class AbstractPluginAdapter extends AbstractPlugin implements IPluginConvenience {

    @Override
    public void setUpRun() {
    }

    @Override
    public void tearDownRun() {
    }

    /**
     * Cancels any events scheduled for this plugin for times up to and
     * including latestScheduledTime
     *
     * @param latestScheduledTime
     */
    @Override
    public void cancelEvents(double latestScheduledTime) {
        ArrayList<PluginRuntimeEvent> events = getRuntimeManager().getAllEvents(this);
        if (events == null || events.size() == 0) {
            return;
        }
        for (PluginRuntimeEvent e : events) {
            if (e.getScheduledTime() <= latestScheduledTime) {
                getRuntimeManager().cancelEvent(e);
            }
        }
    }

    @Override
    public void updateOutput(String outputKey, Object value) throws KeyNotFoundException, NullValueException, InputNotConnectedException {
        getIOutputInputHandler().updateOutput(outputKey, value);
    }

    @Override
    public Object getInput(String inputKey) throws KeyNotFoundException, NullValueException,
            InputNotConnectedException {
        return getIOutputInputHandler().getInput(inputKey);
    }

    @Override
    public Object getInput(String inputKey, boolean isInputUpdateAcknowledged) throws KeyNotFoundException,
            NullValueException, InputNotConnectedException {
        return getIOutputInputHandler().getInput(inputKey, isInputUpdateAcknowledged);
    }

    @Override
    public Object getRecallableInput(String inputKey, double time) throws KeyNotFoundException,
            NullValueException, InputNotConnectedException {
        return getIOutputInputHandler().getRecallableInput(inputKey, time);
    }

    @Override
    public ArrayList<Object> getRecallableInput(String inputKey, double time, int count) throws PluginRuntimeException {
        return getIOutputInputHandler().getRecallableInput(inputKey, time, count);
    }

    @Override
    public Object getOutput(String outputKey) throws KeyNotFoundException {
        return getIOutputInputHandler().getOutput(outputKey);
    }

    @Override
    public Object getRecallableOutput(String outputKey, double time) throws PluginRuntimeException, KeyNotFoundException, NullValueException {
        return getIOutputInputHandler().getRecallableOutput(outputKey, time);
    }

    @Override
    public ArrayList<Object> getRecallableOutput(String outputKey, double time, int count) throws PluginRuntimeException, KeyNotFoundException, NullValueException {
        return getIOutputInputHandler().getRecallableOutput(outputKey, time, count);
    }

    /*
     * sets isStatePreserved for the inputKey
     */
    @Override
    public void setInputStatePreserved(String inputKey, boolean isStatePreserved) throws KeyNotFoundException {
        Input input = getInputDataMap().get(inputKey);
        if (input == null) {
            String message = "Input key : " + inputKey + " not found in method setInputStatePreserved.";
            throw new KeyNotFoundException(this, inputKey, message);
        }
        input.setStatePreserved(isStatePreserved);
    }

    /*
     * sets isStatePreserved for the outputKey
     */
    @Override
    public void setOutputStatePreserved(String outputKey, boolean isStatePreserved) throws KeyNotFoundException {
        Output output = getOutputDataMap().get(outputKey);
        if (output == null) {
            String message = "Output key : " + outputKey + " not found in method setOuputStatePreserved.";
            throw new KeyNotFoundException(this, outputKey, message);
        }
        output.setStatePreserved(isStatePreserved);
    }

    /**
     * sets the number of previous input values that will be stored to count for the inputKey
     * @param inputKey
     * @param count
     * @throws KeyNotFoundException
     */
    @Override
    public void setInputMaxRecall(String inputKey, int count) throws KeyNotFoundException {
        Input input = getInputDataMap().get(inputKey);
        if (input == null) {
            String message = "Intput key : " + inputKey + " not found in method setInputMaxRecall.";
            throw new KeyNotFoundException(this, inputKey, message);
        }
        input.setMaxTimesRecall(count);
    }

    /**
     * sets the number of previous output values that will be stored to count for the outputKey
     * @param outputKey
     * @param count
     * @throws KeyNotFoundException
     */
    @Override
    public void setOutputMaxRecall(String outputKey, int count) throws KeyNotFoundException {
        Output output = getOutputDataMap().get(outputKey);
        if (output == null) {
            String message = "Output key : " + outputKey + " not found in method setOutputMaxRecall.";
            throw new KeyNotFoundException(this, outputKey, message);
        }
        output.setMaxTimesRecall(count);
    }

    @Override
    public void warnAllOutputObservers() {
        getReceiveHandler().warnAllOutputObservers();
    }

    @Override
    public void warnOutputObservers(String outputKey) throws KeyNotFoundException {
        getReceiveHandler().warnOutputObservers(outputKey);
    }

    /**
     *
     * @param outputKey
     * @return true if the output is connected.
     * @throws KeyNotFoundException
     */
    @Override
    public boolean isOutputConnectToAnyInputs(String outputKey) throws KeyNotFoundException {
        Output output = getOutputDataMap().get(outputKey);
        if (output == null) {
            throw new KeyNotFoundException(this, outputKey, "Output key : " + outputKey + " not found in method getInput.");
        }
        return output.getObservers().size() > 0;
    }

    /**
     *
     * @param inputKey
     * @return true if the output is connected.
     * @throws KeyNotFoundException
     */
    @Override
    public boolean isInputConnectedToOutput(String inputKey) throws KeyNotFoundException {
        Input input = getInputDataMap().get(inputKey);
        if (input == null) {
            throw new KeyNotFoundException(this, inputKey, "Input key : " + inputKey + " not found in method getInput.");
        }
        return (input.getConnectedOutput() != null);
    }

    /**
     *
     * @param inputKey
     * @return true if the last update to this input has already been acknowledged.
     * @throws KeyNotFoundException
     */
    @Override
    public boolean isInputUpdateAcknowledged(String inputKey) throws KeyNotFoundException {
        if (getInputDataMap().get(inputKey) == null) {
            throw new KeyNotFoundException(this, inputKey, "Input key : " + inputKey + " not found in method getInput.");
        }
        return getInputDataMap().get(inputKey).isInputUpdateAcknowledged();
    }

    /**
     * Convenience method for scheduling an event in the system
     * @param description
     * @param timeFromNow
     */
    @Override
    public void scheduleEvent(String description, double timeFromNow) {
        double now = getRuntimeManager().currentTime();
        PluginRuntimeEvent thisEvent = new PluginRuntimeEvent(description, now + timeFromNow, this);
        thisEvent.setScheduledTime(now + timeFromNow);
        getRuntimeManager().scheduleEvent(thisEvent);
    }

    /**
     *
     * @param key
     * @param valueClass
     * @param genericClasses
     * @param toolTipText
     */
    public void addNewInput(String key, Class valueClass, Class[] genericClasses, String toolTipText) {
        boolean isUsingGenerics = true;
        if (genericClasses == null || genericClasses.length == 0) {
            isUsingGenerics = false;
        }
        getInputDataMap().put(key, new Input(key, valueClass, genericClasses, isUsingGenerics, toolTipText, this));
    }

    /**
     *
     * @param key
     * @param valueClass
     * @param genericClasses
     */
    public void addNewInput(String key, Class valueClass, Class[] genericClasses) {
        boolean isUsingGenerics = true;
        if (genericClasses == null || genericClasses.length == 0) {
            isUsingGenerics = false;
        }
        getInputDataMap().put(key, new Input(key, valueClass, genericClasses, isUsingGenerics, this));
    }

    /**
     *
     * @param key
     * @param valueClass
     */
    public void addNewInput(String key, Class valueClass) {
        addNewInput(key, valueClass, (Class[]) null);
    }

    /**
     *
     * @param key
     * @param valueClass
     * @param toolTipText
     */
    public void addNewInput(String key, Class valueClass, String toolTipText) {
        addNewInput(key, valueClass, (Class[]) null, toolTipText);
    }

    /**
     * Convenience method for adding new Output object to outputsDataMap
     * @param key
     * @param valueClass
     * @param genericClasses
     */
    public void addNewOutput(String key, Class valueClass, Class[] genericClasses) {
        boolean isUsingGenerics = true;
        if (genericClasses == null || genericClasses.length == 0) {
            isUsingGenerics = false;
        }
        Output o = new Output(key, valueClass, genericClasses, isUsingGenerics, this);
        o.setSavable(true);
        getOutputDataMap().put(key, o);
    }

    /**
     * Convenience method for adding new Output object to outputsDataMap
     * @param key
     * @param valueClass
     */
    public void addNewOutput(String key, Class valueClass) {
        addNewOutput(key, valueClass, (Class[]) null);
    }

    /**
     * Convenience method for adding new Output object to outputsDataMap
     * @param key
     * @param valueClass
     * @param genericClasses
     * @param toolTipText
     */
    public void addNewOutput(String key, Class valueClass, Class[] genericClasses, String toolTipText) {
        boolean isUsingGenerics = true;
        if (genericClasses == null || genericClasses.length == 0) {
            isUsingGenerics = false;
        }
        Output o = new Output(key, valueClass, genericClasses, isUsingGenerics, toolTipText, this);
        o.setSavable(true);
        getOutputDataMap().put(key, o);
    }

    /**
     * Convenience method for adding new Output object to outputsDataMap
     * @param key
     * @param valueClass
     * @param toolTipText
     */
    public void addNewOutput(String key, Class valueClass, String toolTipText) {
        addNewOutput(key, valueClass, (Class[]) null, toolTipText);
    }

    @Override
    public Object deserializeObject(FastByteArrayOutputStream fbos) {
        try {
            return CloneWorker.deserializeObject(fbos);
        } catch (Exception ex) {
            throwRuntimeException(ex);
            return null;
        }
    }

    /**
     * Convenience method for getting the current virtual time of the simulation
     * @return current simulation time.
     */
    @Override
    public double getCurrentTime() {
        return getRuntimeManager().currentTime();
    }

    /**
     * Convenience method for getting the most recent update time for given
     * input
     *
     * @throws KeyNotFoundException
     * @throws InputNotConnectedException
     */
    @Override
    public double getInputUpdateTime(String inputKey) throws KeyNotFoundException, InputNotConnectedException {
        Input input = getInputDataMap().get(inputKey);
        if (input == null) {
            throw new KeyNotFoundException(this, inputKey, "Input key : " + inputKey + " not found in method getInput.");
        }
        if (!isInputConnectedToOutput(inputKey)) {
            throw new InputNotConnectedException(this, inputKey, "Input key : " + inputKey + " not connected to an Output in method getInput.");
        }
        Output connectedOutput = input.getConnectedOutput();

        return connectedOutput.getTimeOfLastUpdate();
    }

    /**
     * Convenience method for getting the most recent update index for a given
     * input
     * @throws KeyNotFoundException
     * @throws InputNotConnectedException
     */
    @Override
    public int getInputUpdateIndex(String inputKey) throws KeyNotFoundException, InputNotConnectedException {
        Input input = getInputDataMap().get(inputKey);
        if (input == null) {
            throw new KeyNotFoundException(this, inputKey, "Input key : " + inputKey + " not found in method getInput.");
        }
        if (!isInputConnectedToOutput(inputKey)) {
            throw new InputNotConnectedException(this, inputKey, "Input key : " + inputKey + " not connected to an Output in method getInput.");
        }
        Output connectedOutput = input.getConnectedOutput();
        return connectedOutput.getIndexOfLastUpdate();
    }

    @Override
    public boolean isFirstModifiedInputNotification() {
        return super.isFirstModifiedInputNotification();
    }

    @Override
    public boolean isFirstEventNotification() {
        return super.isFirstEventNotification();
    }

    @Override
    public boolean isFirstOutputRequestNotification() {
        return super.isFirstOutputRequestNotification();
    }

    @Override
    public Object getGlobalValue(String globalVariableName) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void fireOutputsChanged(){
        Lookup.Result<IPluginIOChanged> result = LatizLookup.getDefault().getLookup().lookup(new Lookup.Template<IPluginIOChanged>(IPluginIOChanged.class));
        for(IPluginIOChanged ioc : result.allInstances()){
            ioc.pluginOutputsChanged(this);
        }
    }

    @Override
    public void fireInputsChanged(){
        Lookup.Result<IPluginIOChanged> result = LatizLookup.getDefault().getLookup().lookup(new Lookup.Template<IPluginIOChanged>(IPluginIOChanged.class));
        for(IPluginIOChanged ioc : result.allInstances()){
            ioc.pluginInputsChanged(this);
        }
    }
}
