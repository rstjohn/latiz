/**
 * 
 */
package com.AandR.latiz.core;

import java.awt.GraphicsEnvironment;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeSet;


import com.AandR.gui.OptionsDialog;
import com.AandR.gui.ui.JButtonX;
import com.AandR.latiz.dev.AbstractPlugin;
import com.AandR.latiz.dev.HDF5Worker;
import com.AandR.latiz.gui.Connector;
import com.AandR.latiz.gui.LatFileTreeDataNode;
import com.AandR.latiz.gui.LatFileTreePanel;

/**
 * @author Aaron Masino
 * @version Jan 18, 2008 4:18:40 PM <br>
 * 
 * Comments:
 * 
 */
public class EventManager implements EventManagerInterface {

    private static final String LAT_FILE_ROOT = "Available Outputs";
    private boolean isSaveRequired;
    private double time;
    private TreeSet<Event> events;
    private LatFileTreePanel latFileTreePanel;
    private boolean isStopTimeIndefinite;
    private boolean isSimulationCancelRequested;
    private TreeSet<AbstractPlugin> plugins;
    private HashMap<Output, Input> outputRequestInitiatorsMap;
    private OutputRequestChainValidator outputRequestChainValidator;

    //private HashMap<String, Boolean> chainValidationMap;
    public EventManager() {
        events = new TreeSet<Event>(new EventComparator());
        time = 0;
        outputRequestInitiatorsMap = new HashMap<Output, Input>();
        outputRequestChainValidator = new OutputRequestChainValidator();
    }

    public void scheduleEvent(Event event) {
        //System.out.println("Scheduling Event : "+event.getPlugin().getName()+" : "+event.getDescriptor()+ " : "+event.getScheduledTime());
        events.add(event);
    }

    public void executeLatizSystem(TreeSet<AbstractPlugin> plugins, double stopTime,
            HashMap<String, HashMap<String, Connector>> pluginOutgoingConnectorMaps) {
        this.plugins = plugins;
        isSimulationCancelRequested = false;
        if (stopTime < 0) {
            isStopTimeIndefinite = true;
        } else {
            isStopTimeIndefinite = false;
        }

        if (isSaveRequired = latFileTreePanel.isLatFileSaveRequired()) {
            boolean success = createLatizDataFile();
            if (!success) {
                if (GraphicsEnvironment.isHeadless()) {
                    System.out.println("Latiz System Execution Failure: Latiz Output File Error. The file may be open.");
                } else {
                    OptionsDialog od = new OptionsDialog(null, "Latiz System Execution Failure", OptionsDialog.ERROR_ICON);
                    od.showDialog("<html>Latiz Output File Error. The file may be open.<BR> System execution halted.</html>", 0);
                }
                return;
            }
        }
        initializePlugins(pluginOutgoingConnectorMaps);
        Event e = events.pollFirst();
        if (e != null) {
            time = e.getScheduledTime();
        } else {
            return;
        }

        while (e != null && (time <= stopTime || isStopTimeIndefinite) && !isSimulationCancelRequested) {
            //System.out.println("$$$$$$$$$$$$$ firing event : " +e.getDescriptor()+" notification at "+time+" for plugin"+e.getPlugin().getName());
            e.getPlugin().receiveEventNotification(e);
            if (events.isEmpty()) {
                e = null;
            } else {
                e = events.pollFirst();
                time = e.getScheduledTime();
            }
        }
        System.gc();
        // System.out.println("done executing");
    }

    private void initializePlugins(HashMap<String, HashMap<String, Connector>> pluginOutgoingConnectorMaps) {
        for (AbstractPlugin p : plugins) {
            p.addEventManagerInterface(this);
            p.setFirstEventNotification(true);
            p.setFirstModifiedInputNotification(true);
            p.setFirstOutputRequestNotification(true);
            p.getInputsDataMap().clear();
            p.getOutputsDataMap().clear();
            p.initializeOutputs();
            p.initializeInputs();

        }

        //must perform registration AFTER the maps have been cleared and reset in loop above
        for (AbstractPlugin p : plugins) {
            registerAllPluginOutputObservers(pluginOutgoingConnectorMaps.get(p.getName()));
            p.scheduleInitialEvent();
        }
        outputRequestChainValidator.clearValidOutputRequestChainMaps();
    }

    private void registerAllPluginOutputObservers(HashMap<String, Connector> pluginConnectorMap) {
        if (pluginConnectorMap.isEmpty()) { // no children
            return;
        }

        Connection thisConnection;
        for (Connector c : pluginConnectorMap.values()) {
            thisConnection = c.getIOconnection();
            registerOutputObservers(thisConnection);
        }
        return;
    }

    private void registerOutputObservers(Connection c) {
        AbstractPlugin inputReceivingProcessor = c.getInputReceivingProcessor();
        AbstractPlugin outputSendingPlugin = c.getOutputSendingProcessor();
        String outputKey;
        Input thisInput;
        Output thisOutput;
        for (String inputKey : c.keySet()) {
            outputKey = c.get(inputKey);
            //System.out.println("Reg I\\O : "+ outputSendingPlugin.getName()+" : "+outputKey+" -> "+inputReceivingProcessor.getName()+" : "+inputKey);
            thisInput = inputReceivingProcessor.getInputsDataMap().get(inputKey);
            thisOutput = outputSendingPlugin.getOutputsDataMap().get(outputKey);
            thisOutput.registerObserver(thisInput);
            thisInput.setConnectedOutput(thisOutput);
        }
    }

    public double currentTime() {
        return time;
    }

    public Event getNextEvent(AbstractPlugin p) {
        for (Event e : events) {
            if (e.getPlugin().getName().equals(p.getName())) {
                return e;
            }
        }
        return null;
    }

    public ArrayList<Event> getAllEvents(AbstractPlugin p) {
        ArrayList<Event> eventList = new ArrayList<Event>();
        for (Event e : events) {
            if (e.getPlugin().getName().equals(p.getName())) {
                eventList.add(e);
            }
        }
        return eventList;
    }

    public void cancelEvent(Event e) {
        // System.out.println("Cancelling Event : "+e.getPlugin().getName()+" : "+e.getDescriptor()+ " : "+e.getScheduledTime());
        events.remove(e);
    }

    private boolean createLatizDataFile() {
        File latFile = new File(latFileTreePanel.getFileName());
        if (latFile.exists() && !GraphicsEnvironment.isHeadless()) {
            OptionsDialog overwrite = new OptionsDialog(null, "File Already Exists", new JButtonX[]{new JButtonX("Overwrite"), new JButtonX("Cancel")}, OptionsDialog.QUESTION_ICON);
            overwrite.showDialog("<HTML>The file <B><I>" + latFile + "</I></B> already exists.<BR>Do you want to overwrite or cancel?", 0);
            if (overwrite.getSelectedButtonIndex() == 1) {
                return false;
            }
        }

        ArrayList<AbstractPlugin> pluginList = new ArrayList<AbstractPlugin>();
        pluginList.addAll(latFileTreePanel.getSelectedLatFileMap().keySet());

        try {
            HDF5Worker.createGroups(latFileTreePanel.getFileName(), LAT_FILE_ROOT, pluginList);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public void appendDataToLatFile(AbstractPlugin plugin, Output output, Object data) {
        if (!isSaveRequired || !latFileTreePanel.getSelectedLatFileMap().containsKey(plugin)) {
            return;
        }

        HDF5Worker.createGroup(LAT_FILE_ROOT, plugin.getName());

        double timeOfLastUpdate = output.getTimeOfLastUpdate();
        for (LatFileTreeDataNode dataNode : latFileTreePanel.getSelectedLatFileMap().get(plugin)) {
            if (dataNode.getDatasetName().equals(output.getLatFileVariableKey())) {
                double period = dataNode.getPeriod().doubleValue();
                if (timeOfLastUpdate < dataNode.getBeginTime().floatValue()) {
                    continue;
                }
                if (timeOfLastUpdate > dataNode.getEndTime().floatValue()) {
                    continue;
                }
                double normalizedStart = timeOfLastUpdate - dataNode.getBeginTime().doubleValue();
                if (normalizedStart < 0) {
                    continue;
                }
                if (period > 0 && normalizedStart % period != 0) {
                    continue;
                }
                HDF5Worker.appendData("/" + LAT_FILE_ROOT + "/" + plugin.getName(), output.getLatFileVariableKey(), data, timeOfLastUpdate);
                return;
            }
        }
    }

    public void setLatFileTreePanel(LatFileTreePanel latFileTreePanel) {
        this.latFileTreePanel = latFileTreePanel;
    }

    public void notifyExecutionFailure(String pluginName) {
        if (GraphicsEnvironment.isHeadless()) {
            System.out.println("Latiz System Execution Failure: Latiz system failed on plugin " + pluginName + "\nSystem execution halted.");
        } else {
            OptionsDialog od = new OptionsDialog(null, "Latiz System Execution Failure", OptionsDialog.ERROR_ICON);
            od.showDialog("<html>Latiz system failed on plugin : <B>" + pluginName + "</B>.<BR> System execution halted.</html>", 0);
        }
        events.clear();
        isSimulationCancelRequested = true;
    }

    public void requestSimulationCancellation() {
        isSimulationCancelRequested = true;
        events.clear();
    }

    //input is the input being requested from a given plugin
    public void validateOutputRequest(Input requestedInput) {
        if (requestedInput.isInputChainValidated()) {
            return;
        } else {
            requestedInput.setInputChainValidated(true);
        }
        //System.out.println("Validating output request chain for : "+requestedInput.getInputPlugin().getName()+"->"+requestedInput.getKey());
        outputRequestChainValidator.validateOutputRequest(requestedInput);
    }

    private class OutputRequestChainValidator {

        public void validateOutputRequest(Input requestedInput) {
            outputRequestInitiatorsMap.clear();
            if (requestedInput.getConnectedOutput() == null) {
                return;
            }
            validateChain(requestedInput, "");
            for (Output o : outputRequestInitiatorsMap.keySet()) {
                o.setOutputRequestInitiator(outputRequestInitiatorsMap.get(o));
            }
            resetOutputChecks();
        }

        private void validateChain(Input input, String branch) {
            AbstractPlugin inputPlugin = input.getInputPlugin();
            String inputString = inputPlugin.getName() + "->" + input.getKey();
            Output connectedOutput = input.getConnectedOutput();
            if (connectedOutput == null || connectedOutput.isOutputRequestSatisfied()) {
                branch += inputString;
                input.getValidOutputRequestChainMap().put(branch, true);
                //	System.out.println(branch+"--**true**");
                branch += "%%";
                if (connectedOutput != null) {
                    connectedOutput.setOutputRequestSatisfied(false);
                }
            } else {
                branch += inputString;
                input.getValidOutputRequestChainMap().put(branch, false);
                //	System.out.println(branch+"---**false**"+"---->"+connectedOutput.getOutputPlugin().getName()+"->"+connectedOutput.getKey());
                return;
            }
            outputRequestInitiatorsMap.put(input.getConnectedOutput(), input.getConnectedOutput().getOutputRequestInitiator());
            input.getConnectedOutput().setOutputRequestInitiator(input);

            //back check the inputs
            AbstractPlugin p = input.getConnectedOutput().getOutputPlugin();
            boolean b1, b2;

            for (Input i : p.getInputsDataMap().values()) {
                connectedOutput = i.getConnectedOutput();
                if (connectedOutput == null) {
                    continue;
                }

                b1 = connectedOutput.isOutputUpdated();
                b2 = connectedOutput.getTimeOfLastUpdate() == currentTime();
                if (b1 || b2) {
                    continue;
                }

                if (connectedOutput.getOutputRequestInitiator() == null
                        || !connectedOutput.getOutputRequestInitiator().equals(i)) {
                    validateChain(i, branch);
                }

            }
            input.getConnectedOutput().setOutputRequestSatisfied(true);
        }

        private void resetOutputChecks() {
            for (AbstractPlugin p : plugins) {
                for (Output o : p.getOutputsDataMap().values()) {
                    o.setOutputRequestSatisfied(true);
                }
            }
        }

        private void clearValidOutputRequestChainMaps() {
            for (AbstractPlugin p : plugins) {
                for (Input i : p.getInputsDataMap().values()) {
                    i.getValidOutputRequestChainMap().clear();
                    i.setInputChainValidated(false);
                }
            }
        }
    }
}
