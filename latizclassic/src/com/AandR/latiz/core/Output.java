/**
 * 
 */
package com.AandR.latiz.core;

import java.util.ArrayList;
import java.util.TreeSet;


import com.AandR.latiz.dev.AbstractPlugin;
import com.AandR.latiz.interfaces.LatFileInterface;

/**
 * @author Aaron Masino
 * @version Jan 18, 2008 2:38:12 PM <br>
 *
 * Comments:
 *
 */
public class Output extends TransferableData {

    private AbstractPlugin outputPlugin;
    private ArrayList<Input> observers;
    private String latFileVariableKey;
    private boolean isRecallableRequired = false;
    private Object recallableValue = null;
    private boolean isOutputUpdated = false;
    private TreeSet<Input> inputsAlreadyNotified;
    private boolean continueWarningNotifications = false;
    private Input outputRequestInitiator = null;
    private LatFileInterface latFileInterface = null;
    private boolean isOutputRequestSatisfied = true;

    public Output(String key, Class valueClass, Class[] genericClasses, boolean isUsingGenrics, AbstractPlugin outputPlugin) {
        super(key, valueClass, genericClasses, isUsingGenrics);
        this.outputPlugin = outputPlugin;
        observers = new ArrayList<Input>();
        this.latFileVariableKey = key;
        inputsAlreadyNotified = new TreeSet<Input>(new InputComparator());
    }

    public Output(String key, Class valueClass, Class[] genericClasses, boolean isUsingGenrics, boolean isRecallableRequired, AbstractPlugin ouputPlugin) {
        super(key, valueClass, genericClasses, isUsingGenrics);
        this.outputPlugin = ouputPlugin;
        observers = new ArrayList<Input>();
        this.latFileVariableKey = key;
        inputsAlreadyNotified = new TreeSet<Input>(new InputComparator());
        if (isRecallableRequired) {
            recallableValue = LatizUtility.cloneObject(value);
        }
        this.isRecallableRequired = isRecallableRequired;
    }

    public Output(String key, Class valueClass, Class[] genericClasses, boolean isUsingGenrics, String toolTipText, AbstractPlugin outputPlugin) {
        super(key, valueClass, genericClasses, isUsingGenrics, toolTipText);
        this.outputPlugin = outputPlugin;
        observers = new ArrayList<Input>();
        this.latFileVariableKey = key;
        inputsAlreadyNotified = new TreeSet<Input>(new InputComparator());
    }

    public Output(String key, Class valueClass, Class[] genericClasses, boolean isUsingGenrics, String toolTipText, boolean isRecallableRequired, AbstractPlugin outputPlugin) {
        super(key, valueClass, genericClasses, isUsingGenrics, toolTipText);
        this.outputPlugin = outputPlugin;
        observers = new ArrayList<Input>();
        this.latFileVariableKey = key;
        inputsAlreadyNotified = new TreeSet<Input>(new InputComparator());
        if (isRecallableRequired) {
            recallableValue = LatizUtility.cloneObject(value);
        }
        this.isRecallableRequired = isRecallableRequired;
    }

    public Output(String key, Class valueClass, Class[] genericClasses, boolean isUsingGenrics, String toolTipText, String latFileVariableKey, AbstractPlugin outputPlugin) {
        super(key, valueClass, genericClasses, isUsingGenrics, toolTipText);
        this.outputPlugin = outputPlugin;
        observers = new ArrayList<Input>();
        this.latFileVariableKey = latFileVariableKey;
        inputsAlreadyNotified = new TreeSet<Input>(new InputComparator());
    }

    public Output(String key, Class valueClass, Class[] genericClasses, boolean isUsingGenrics, String toolTipText, String latFileVariableKey, boolean isRecallableRequired, AbstractPlugin outputPlugin) {
        super(key, valueClass, genericClasses, isUsingGenrics, toolTipText);
        this.outputPlugin = outputPlugin;
        this.latFileVariableKey = latFileVariableKey;
        this.isRecallableRequired = isRecallableRequired;
        observers = new ArrayList<Input>();
        if (isRecallableRequired) {
            recallableValue = LatizUtility.cloneObject(value);
        }
        inputsAlreadyNotified = new TreeSet<Input>(new InputComparator());
    }

    public void updateOutput(Object value, EventManagerInterface eventManagerInterface) {
        this.setValue(value);
        this.timeOfLastUpdate = eventManagerInterface.currentTime();
        this.indexOfLastUpdate += 1;
        this.isOutputUpdated = true;
        updateObserversValues();
        this.continueWarningNotifications = false;
        if (latFileInterface == null) {
            eventManagerInterface.appendDataToLatFile(outputPlugin, this, value);
        } else {
            eventManagerInterface.appendDataToLatFile(outputPlugin, this, latFileInterface);
        }
    }

    @Override
    public void setValue(Object value) {
        if (isRecallableRequired) {
            recallableValue = LatizUtility.cloneObject(value);
        }
        this.value = value;
    }

    public Object getRecallableValue() {
        return recallableValue;
    }

    public void updateObserversValues() {
        if (observers == null || observers.isEmpty()) {
            return;
        }
        for (Input in : observers) {
            if (observers.size() > 1) {//clone data
                Object newValue = LatizUtility.cloneObject(this.getValue());
                in.setValue(newValue);
            } else {
                in.setValue(value);
            }
            in.setIndexOfLastUpdate(indexOfLastUpdate);
            in.setTimeOfLastUpdate(timeOfLastUpdate);
            in.setInputUpdateAcknowledged(false);
        }
    }

    public void notifyObserversOfUpdate() {
        if (!isOutputUpdated) {
            return;
        }
        if (observers == null || observers.isEmpty()) {
            return;
        }
        /*if(outputRequestInitiator==null) {
        System.out.println("      -->-->-->--> "+getOutputPlugin().getName()+" ->"+getKey()+" notifying. request initiator = null");

        }else {
        System.out.println("      -->-->-->-->  "+getOutputPlugin().getName()+" ->"+getKey()+" notifying. request initiator = "+outputRequestInitiator.getInputPlugin().getName()+ " -> "+outputRequestInitiator.getKey());
        }*/

        for (Input input : observers) {
            if (!(inputsAlreadyNotified.contains(input) || input.equals(outputRequestInitiator))) {
                inputsAlreadyNotified.add(input);
                input.getInputPlugin().receiveInputsModifiedNotification(input);
                input.setTimeOfLastUpdate(timeOfLastUpdate);
                input.setIndexOfLastUpdate(indexOfLastUpdate);
            }
        }
        outputRequestInitiator = null;
        inputsAlreadyNotified.clear();
        isOutputUpdated = false;
        //System.out.println("      -->-->-->-->  "+getOutputPlugin().getName()+" ->"+getKey()+" done notifying");
    }

    public void notifyObserversOfWarning() {
        for (Input input : observers) {
            if (continueWarningNotifications) {
                input.getInputPlugin().receiveInputsModifiedNotification(input);
            } else {
                return;
            }
        }
    }

    public void addInputToAlreadyNotified(Input input) {
        inputsAlreadyNotified.add(input);
    }

    public boolean isInputAlreadyNotified(Input input) {
        if (inputsAlreadyNotified.contains(input)) {
            return true;
        } else {
            return false;
        }
    }

    public AbstractPlugin getOutputPlugin() {
        return outputPlugin;
    }

    public void setOutputPlugin(AbstractPlugin outputPlugin) {
        this.outputPlugin = outputPlugin;
    }

    public ArrayList<Input> getObservers() {
        return observers;
    }

    public TreeSet<AbstractPlugin> getPluginObservers() {
        if (observers == null || observers.isEmpty()) {
            return null;
        }
        TreeSet<AbstractPlugin> plugins = new TreeSet<AbstractPlugin>(new PluginComparator());
        for (Input in : observers) {
            plugins.add(in.getInputPlugin());
        }
        return plugins;
    }

    public void registerObserver(Input input) {
        observers.add(input);
    }

    public String getLatFileVariableKey() {
        return latFileVariableKey;
    }

    public void setLatFileVariableKey(String latFileVariableKey) {
        this.latFileVariableKey = latFileVariableKey;
    }

    public final boolean isRecallableRequired() {
        return isRecallableRequired;
    }

    public final boolean isOutputUpdated() {
        return isOutputUpdated;
    }

    public final void setOutputUpdated(boolean isUpdated) {
        this.isOutputUpdated = isUpdated;
    }

    public final boolean isContinueWarningNotifications() {
        return continueWarningNotifications;
    }

    public final void setContinueWarningNotifications(boolean continueWarning) {
        this.continueWarningNotifications = continueWarning;
    }

    public final void setOutputRequestInitiator(Input outputRequestInitiator) {
        this.outputRequestInitiator = outputRequestInitiator;
    }

    public final Input getOutputRequestInitiator() {
        return outputRequestInitiator;
    }

    public LatFileInterface getLatFileInterface() {
        return latFileInterface;
    }

    public void setLatFileInterface(LatFileInterface latFileInterface) {
        this.latFileInterface = latFileInterface;
    }

    public final boolean isOutputRequestSatisfied() {
        return isOutputRequestSatisfied;
    }

    public final void setOutputRequestSatisfied(boolean isOutputRequestSatisfied) {
        this.isOutputRequestSatisfied = isOutputRequestSatisfied;
    }
}
