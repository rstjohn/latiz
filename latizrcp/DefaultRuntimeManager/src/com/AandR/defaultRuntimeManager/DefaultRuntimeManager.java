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
package com.AandR.defaultRuntimeManager;

import com.AandR.palette.model.AbstractPaletteModel;
import com.AandR.palette.model.DefaultPaletteModel;
import com.AandR.palette.plugin.AbstractPlugin;
import com.AandR.palette.plugin.data.Input;
import com.AandR.palette.plugin.data.Output;
import com.AandR.palette.runtime.IRuntimeManager;
import com.AandR.palette.runtime.IRuntimeObserver;
import com.AandR.palette.runtime.PluginRuntimeEvent;
import com.AandR.palette.runtime.PluginRuntimeEventComparator;
import com.AandR.palette.runtime.exceptions.PluginRuntimeException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import org.jdom.Element;

/**
 *
 * @author Aaron Masino
 */
public class DefaultRuntimeManager implements IRuntimeManager {

    private double virtualTime;
    private boolean isSimulationCancelRequested;
    private TreeSet<PluginRuntimeEvent> events;
    private DefaultPaletteModel paletteModel;
    private Collection<AbstractPlugin> plugins;
    private int currentRunNumber = 0;
    private double stopTime;
    private OutputRequestChainValidator outputRequestChainValidator;
    private HashMap<Output, Input> outputRequestInitiatorsMap;

    public DefaultRuntimeManager() {
        events = new TreeSet<PluginRuntimeEvent>(new PluginRuntimeEventComparator());
        virtualTime = 0;
        outputRequestInitiatorsMap = new HashMap<Output, Input>();
        outputRequestChainValidator = new OutputRequestChainValidator();
    }

    public void initializeOutputObservers() {
        if (plugins == null) {
            return;
        }
        for (AbstractPlugin p : plugins) {
            for (Output o : p.getOutputDataMap().values()) {
                o.setIOutputObserversList(paletteModel.getOutputObservers(p.getName(), o.getKey()));
            }
        }
    }

    public void initializePluginsReceiveHandler() {
        if (plugins == null) {
            return;
        }
        DefaultReceiverHandler thisHandler;
        for (AbstractPlugin p : plugins) {
            thisHandler = new DefaultReceiverHandler();
            thisHandler.setAbstractPlugin(p);
            p.setReceiveHandler(thisHandler);
        }
    }

    public void initializePluginOutputInputHandler() {
        if (plugins == null) {
            return;
        }
        DefalutIOHandler thisHandler;
        for (AbstractPlugin p : paletteModel.getPlugins().values()) {
            thisHandler = new DefalutIOHandler();
            thisHandler.setAbstractPlugin(p);
            p.setIOutputInputHandler(thisHandler);
        }
    }

    public void scheduleEvent(PluginRuntimeEvent event) {
        // System.out.println("Scheduling Event : "+event.getPlugin().getName()+
        // " : "+event.getDescriptor()+ " : "+event.getScheduledTime());
        events.add(event);
    }

    public double currentTime() {
        return virtualTime;
    }

    public PluginRuntimeEvent getNextEvent(AbstractPlugin p) {
        for (PluginRuntimeEvent e : events) {
            if (e.getPlugin().getName().equals(p.getName())) {
                return e;
            }
        }
        return null;
    }

    public ArrayList<PluginRuntimeEvent> getAllEvents(AbstractPlugin p) {
        ArrayList<PluginRuntimeEvent> eventList = new ArrayList<PluginRuntimeEvent>();
        for (PluginRuntimeEvent e : events) {
            if (e.getPlugin().getName().equals(p.getName())) {
                eventList.add(e);
            }
        }
        return eventList;
    }

    public void cancelEvent(PluginRuntimeEvent e) {
        // System.out.println("Cancelling Event : "+e.getPlugin().getName()+" : "
        // +e.getDescriptor()+ " : "+e.getScheduledTime());
        events.remove(e);
    }

    private void clearEventsAndCancel() {
        events.clear();
        isSimulationCancelRequested = true;
    }

    public void notifyRuntimeFailure(PluginRuntimeException e) {
        // if (!isSimulationCancelRequested) new PluginRuntimeExceptionDialog(e);
        //TODO setup error dialog
        if (!isSimulationCancelRequested) {
            try {
                clearEventsAndCancel();
                deinitializePlugins();
                for (IRuntimeObserver iro : paletteModel.getRuntimeObservers()) {
                    iro.notifyRuntimeFailure(e);
                    iro.tearDownRun(this);
                }
                throw e;
            } catch (Exception ex) {
                Logger.getLogger(DefaultRuntimeManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    public void executePaletteModel(AbstractPaletteModel iPaletteModel) {
        paletteModel = (DefaultPaletteModel) iPaletteModel;
        plugins = paletteModel.getPlugins().values();
        isSimulationCancelRequested = false;
        stopTime = paletteModel.getStopTime();

        /* This is the correct methodology once the globals are in place
        GlobalParameterMap gpm = GlobalParameterMap.getInstanceOf();
        ArrayList<LinkedHashMap<String, String>> loops = gpm.getIndependentLoops();

        if (!GraphicsEnvironment.isHeadless() && loops != null) {
        boolean cont = showLoopCountDialog(loops.size());
        if (!cont) return;
        }

        updateCurrentRunNumber(1);
        if (loops == null || loops.isEmpty()) {
        beginRunProcess(stopTime, pluginOutgoingConnectorMaps);
        } else {
        for (LinkedHashMap<String, String> loopMap : loops) {
        for (String key : loopMap.keySet()) {
        gpm.put(key, new GlobalVariable(key, loopMap.get(key)));
        }
        if (isSimulationCancelRequested) break;
        beginRunProcess(stopTime, pluginOutgoingConnectorMaps);
        currentRunNumber++;
        }
        gpm.resetLoopEntries();
        // System.out.println("done executing");
        }
         */

        updateCurrentRunNumber(1);
        initializePlugins();
        notifyRuntimeObserversSetupRun();
        beginRunProcess();

        deinitializePlugins();
        notifyRuntimeObserversTearDown();
    }

    private void beginRunProcess() {
        virtualTime = 0;

        PluginRuntimeEvent e = events.pollFirst();
        if (e == null) {
            return;
        }

        virtualTime = e.getScheduledTime();
        while (e != null && (virtualTime <= stopTime || stopTime < 0) && !isSimulationCancelRequested) {
            // System.out.println("$$$$$$$$$$$$$ firing event : "
            // +e.getDescriptor
            // ()+" notification at "+time+" for plugin"+e.getPlugin
            // ().getName());
            e.getPlugin().getReceiveHandler().receiveEventNotification(e);
            if (events.size() == 0) {
                e = null;
            } else {
                e = events.pollFirst();
                virtualTime = e.getScheduledTime();
            }
        }
        deinitializePlugins();
    }

    public void initializePlugins() {
        initializePluginOutputInputHandler();
        initializePluginsReceiveHandler();
        for (AbstractPlugin p : plugins) {
            p.setRuntimeManager(this);
            p.setFirstEventNotification(true);
            p.setFirstModifiedInputNotification(true);
            p.setFirstOutputRequestNotification(true);

            Input thisInput;
            for (String key : p.getInputDataMap().keySet()) {
                thisInput = p.getInputDataMap().get(key);
                p.getInputDataMap().put(key, new Input(key, thisInput.getValueClass(), thisInput.getGenericClasses(), thisInput.isUsingGernics, thisInput.getToolTipText(), p));
            }
            Output thisOutput;
            for (String key : p.getOutputDataMap().keySet()) {
                thisOutput = p.getOutputDataMap().get(key);
                p.getOutputDataMap().put(key, new Output(key, thisOutput.getValueClass(), thisOutput.getGenericClasses(), thisOutput.isUsingGernics, thisOutput.getToolTipText(), p));
            }
        }
        initializeOutputObservers();

        // must perform Output Observer registration AFTER the maps have been cleared and
        // reset in loop above
        registerOutputObservers();

        //allow plugins to do any setup actions
        for (AbstractPlugin p : plugins) {
            p.setUpRun();
        }

        // must perform initial event scheduling AFTER registration so plugin can check for
        // isInputConnectedToOutput
        for (AbstractPlugin p : plugins) {
            try {
                p.scheduleInitialEvent();
            } catch (PluginRuntimeException e) {
                notifyRuntimeFailure(e);
            } catch (ParseException e) {
                notifyRuntimeFailure(new PluginRuntimeException(p, e.getMessage()));
            }
        }
        outputRequestChainValidator.clearValidOutputRequestChainMaps();
    }

    public void deinitializePlugins() {
        for (AbstractPlugin p : plugins) {
            p.tearDownRun();
            p.setReceiveHandler(null);
            p.setRuntimeManager(null);
            p.setIOutputInputHandler(null);
            for (Output o : p.getOutputDataMap().values()) {
                o.setIOutputObserversList(null);
            }
        }
    }

    public void registerOutputObservers() {
        Map<String, AbstractPlugin> pluginMap = paletteModel.getPlugins();
        List<String> connections = paletteModel.getConnections();
        String[] connectionSplit, sourceSplit, targetSplit;
        AbstractPlugin targetPlugin, sourcePlugin;
        String sourceOutputKey, targetInputKey;
        Input targetInput;
        Output sourceOutput;
        for (String thisConnection : connections) {
            //thisConnection=SourcePluginName::OutputKey>TargetPluginName::InputKey

            connectionSplit = thisConnection.split(">");

            sourceSplit = connectionSplit[0].split("::");
            sourcePlugin = pluginMap.get(sourceSplit[0]);
            sourceOutputKey = sourceSplit[1];

            targetSplit = connectionSplit[1].split("::");
            targetPlugin = pluginMap.get(targetSplit[0]);
            targetInputKey = targetSplit[1];

            targetInput = targetPlugin.getInputDataMap().get(targetInputKey);
            sourceOutput = sourcePlugin.getOutputDataMap().get(sourceOutputKey);
            sourceOutput.registerObserver(targetInput);
            targetInput.setConnectedOutput(sourceOutput);
        }
    }

    private void updateCurrentRunNumber(int number) {
        currentRunNumber = number;
        for (IRuntimeObserver iro : paletteModel.getRuntimeObservers()) {
            iro.setCurrentRunNumber(number);
        }
    }

    public void requestSimulationCancellation() {
        isSimulationCancelRequested = true;
        events.clear();
    }

    public void validateOutputRequest(Input input) {
        if (input.isInputChainValidated()) {
            return;
        } else {
            input.setInputChainValidated(true);
        }
        // System.out.println("Validating output request chain for : "+
        // requestedInput
        // .getInputPlugin().getName()+"->"+requestedInput.getKey());
        outputRequestChainValidator.validateOutputRequest(input);
    }

    public void notifyRuntimeObserversSetupRun() {
        for (IRuntimeObserver iro : paletteModel.getRuntimeObservers()) {
            iro.setUpRun(this);
        }
    }

    public void notifyRuntimeObserversTearDown() {
        for (IRuntimeObserver iro : paletteModel.getRuntimeObservers()) {
            iro.tearDownRun(this);
        }
    }

    public JComponent getParameterPanel() {
        JPanel p = new JPanel();
        p.add(new JLabel("Default Runtime Manager : No Settings"));
        return p;

    }

    public void loadSavedWorkspaceParameters(Element e) {
    }

    public Element createWorkspaceParameters() {
        return null;
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

        private final void validateChain(Input input, String branch) {
            AbstractPlugin inputPlugin = input.getInputPlugin();
            String inputString = inputPlugin.getName() + "->" + input.getKey();
            Output connectedOutput = input.getConnectedOutput();
            if (connectedOutput == null || connectedOutput.isOutputRequestSatisfied()) {
                branch += inputString;
                input.getValidOutputRequestChainMap().put(branch, true);
                // System.out.println(branch+"--**true**");
                branch += "%%";
                if (connectedOutput != null) {
                    connectedOutput.setOutputRequestSatisfied(false);
                }
            } else {
                branch += inputString;
                input.getValidOutputRequestChainMap().put(branch, false);
                // System.out.println(branch+"---**false**"+"---->"+
                // connectedOutput
                // .getOutputPlugin().getName()+"->"+connectedOutput.getKey());
                return;
            }
            outputRequestInitiatorsMap.put(input.getConnectedOutput(), input.getConnectedOutput().getOutputRequestInitiator());
            input.getConnectedOutput().setOutputRequestInitiator(input);

            // back check the inputs
            AbstractPlugin p = input.getConnectedOutput().getOutputPlugin();
            boolean b1, b2;

            for (Input i : p.getInputDataMap().values()) {
                connectedOutput = i.getConnectedOutput();
                if (connectedOutput == null) {
                    continue;
                }

                b1 = connectedOutput.isOutputUpdated();
                b2 = connectedOutput.getTimeOfLastUpdate() == currentTime();
                if (b1 || b2) {
                    continue;
                }

                if (connectedOutput.getOutputRequestInitiator() == null || !connectedOutput.getOutputRequestInitiator().equals(i)) {
                    validateChain(i, branch);
                }

            }
            input.getConnectedOutput().setOutputRequestSatisfied(true);
        }

        private void resetOutputChecks() {
            for (AbstractPlugin p : plugins) {
                for (Output o : p.getOutputDataMap().values()) {
                    o.setOutputRequestSatisfied(true);
                }
            }
        }

        private void clearValidOutputRequestChainMaps() {
            for (AbstractPlugin p : plugins) {
                for (Input i : p.getInputDataMap().values()) {
                    i.getValidOutputRequestChainMap().clear();
                    i.setInputChainValidated(false);
                }
            }
        }
    }
}
