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

import com.AandR.palette.plugin.AbstractPlugin;
import com.AandR.palette.plugin.data.Input;
import com.AandR.palette.plugin.data.Output;
import com.AandR.palette.plugin.data.OutputComparator;
import com.AandR.palette.runtime.IReceiveHandler;
import com.AandR.palette.runtime.NotifyObserversEvent;
import com.AandR.palette.runtime.PluginRuntimeEvent;
import com.AandR.palette.runtime.exceptions.KeyNotFoundException;
import java.util.ArrayList;
import java.util.TreeSet;

/**
 *
 * @author Aaron Masino
 */
public class DefaultReceiverHandler implements IReceiveHandler {

    private AbstractPlugin plugin;
    private TreeSet<Output> warnedOutputs = new TreeSet<Output>(new OutputComparator());
    private NotifyObserversEvent notifyObserversEvent;

    public DefaultReceiverHandler(){
        notifyObserversEvent = new NotifyObserversEvent();
    }

    public void receiveEventNotification(PluginRuntimeEvent event) {
        if (event instanceof NotifyObserversEvent) {
            // System.out.println("EEEEEEEEEEE-> " + getName() +
            // " received event notificaiton : "+event.getDescriptor());
            notifyOutputObservers();
        } else {
            // System.out.println("EEEEEEEEEEE-> " + getName() +
            // " received event notificaiton : "+event.getDescriptor());
            warnedOutputs.clear();
            try {
                plugin.acknowledgeEventNotification(event);
            } catch (Exception e) {
                plugin.throwRuntimeException(e);
                return;
            }
            if (plugin.isFirstEventNotification()) {
                plugin.setFirstEventNotification(false);
            }
            notifyOutputObservers();
            warnOutputObservers();
        // System.out.println("eeeeeeeeeee-> " + getName() +
        // " done event notificaiton");
        }
    }

    public void receiveInputsModifiedNotification(Input input) {
        // System.out.println("ININININININ->    " + getName() +
        // " recieved input notification");
        warnedOutputs.clear();
        try {
            plugin.acknowledgeModifiedInputsNotification(input.getKey());
        } catch (Exception e) {
            plugin.throwRuntimeException(e);
            return;
        }
        if (plugin.isFirstModifiedInputNotification()) {
            plugin.setFirstModifiedInputNotification(false);
        }
        notifyOutputObservers();
        warnOutputObservers();
    // System.out.println("ininininininin->    " + getName() +
    // " done input notification");
    }

    public void receiveOutputRequestNotificaton(Input input, String prefix) {
        Output connectedOutput = input.getConnectedOutput();
		 // System.out.println("OUTOUTOUTOUTOUT-> receiving output request of " +
		 // getName() + " -> " + connectedOutput.getKey() + " from "
		 // + input.getInputPlugin().getName() + " -> " + input.getKey());

		 // System.out.println("         ---set initiator  " + input.getKey());

		 connectedOutput.setOutputRequestInitiator(input);
		 try {
			 plugin.acknowledgeOutputRequestNotification(connectedOutput.getKey());
		 } catch (Exception e) {
             plugin.throwRuntimeException(e);
			 return;
		 }

		 if (!connectedOutput.isOutputUpdated())
			 checkForScheduledEvents();

		 if (!connectedOutput.isOutputUpdated()) {
			 if (prefix.equals(""))
                 plugin.getRuntimeManager().validateOutputRequest(input);
			 prefix += input.getInputPlugin().getName() + "->" + input.getKey() + "%%";
			 checkForNewInputs(prefix);
		 }

		 if (plugin.isFirstOutputRequestNotification())
             plugin.setFirstOutputRequestNotification(false);

		 if (connectedOutput.isOutputUpdated()) {
			 notifyObserversEvent.setScheduledTime(plugin.getRuntimeManager().currentTime());
			 plugin.getRuntimeManager().scheduleEvent(notifyObserversEvent);
		 } else {
			 connectedOutput.setOutputRequestInitiator(null);
			 // TODO modified during unit testing
			 // connectedOutput.setTimeOfLastUpdate(getCurrentTime());
		 }

		 // System.out.println("outoutoutoutout->" + getName() +
		 // " is done output request of  -> " + connectedOutput.getKey());
    }

     private void checkForNewInputs(String prefix) {
		 Output connectedOutput;
		 boolean isInputsFired = false;
		 Boolean isValidForOutputRequest;
		 String validationKey;
		 for (Input input : plugin.getInputDataMap().values()) {
			 connectedOutput = input.getConnectedOutput();
			 if (connectedOutput != null) {
				 if (connectedOutput.isOutputUpdated() || connectedOutput.getTimeOfLastUpdate() == plugin.getRuntimeManager().currentTime()) {// check
					 // if
					 // output
					 // is
					 // updated
					 if (!connectedOutput.isInputAlreadyNotified(input)
							 && (connectedOutput.getOutputRequestInitiator() == null || !connectedOutput
									 .getOutputRequestInitiator().equals(input))) {
						 connectedOutput.addInputToAlreadyNotified(input);
						 isInputsFired = true;
						 try {
							 plugin.acknowledgeModifiedInputsNotification(input.getKey());
						 } catch (Exception e) {
							 plugin.throwRuntimeException(e);
							 return;
						 }
						 if (plugin.isFirstModifiedInputNotification())
							 plugin.setFirstModifiedInputNotification(false);
					 }
				 } else if (connectedOutput.getOutputRequestInitiator() == null
						 || !connectedOutput.getOutputRequestInitiator().equals(input)) {
					 // see if output should be updated
					 validationKey = prefix + plugin.getName() + "->" + input.getKey();
					 isValidForOutputRequest = input.getValidOutputRequestChainMap().get(validationKey);
					 if (isValidForOutputRequest == null)
						 isValidForOutputRequest = true;
					 if (!isValidForOutputRequest) {
						 // System.out.println("input : " +
						 // input.getInputPlugin().getName()+" : " +
						 // input.getKey()+" not in valid chain.");
						 continue;
					 }
					 connectedOutput.getOutputPlugin().getReceiveHandler().receiveOutputRequestNotificaton(input, validationKey);
					 if (connectedOutput.isOutputUpdated() && !connectedOutput.isInputAlreadyNotified(input)) {
						 isInputsFired = true;
						 connectedOutput.addInputToAlreadyNotified(input);
						 try {
							 plugin.acknowledgeModifiedInputsNotification(input.getKey());
						 } catch (Exception e) {
                             plugin.throwRuntimeException(e);
							 return;
						 }
						 if (plugin.isFirstModifiedInputNotification())
                             plugin.setFirstModifiedInputNotification(false);
					 }
				 } else if (connectedOutput.getOutputRequestInitiator().equals(input)) {
					 // some how an ifninite loop got through undetected maybe?
					 System.out.println("ERROR ? : An undetected infinite loop has occurred.");
					 // eventManagerInterface.notifyExecutionFailure(getName());
					 return;
				 }
			 }
		 }
		 if (isInputsFired) {
			 notifyObserversEvent.setScheduledTime(plugin.getRuntimeManager().currentTime());
			 plugin.getRuntimeManager().scheduleEvent(notifyObserversEvent);
		 }
		 return;
	 }


	 private void checkForScheduledEvents() {
		 ArrayList<PluginRuntimeEvent> events = plugin.getRuntimeManager().getAllEvents(plugin);
		 if (events == null || events.size() == 0)
			 return;
		 boolean isEventsFired = false;
		 for (PluginRuntimeEvent e : events) {
			 if (e.getScheduledTime() <= plugin.getRuntimeManager().currentTime()) {
				 plugin.getRuntimeManager().cancelEvent(e);
				 try {
					 plugin.acknowledgeEventNotification(e);
				 } catch (Exception e1) {
                     plugin.throwRuntimeException(e1);
					 return;
				 }
				 if (plugin.isFirstEventNotification())
                     plugin.setFirstEventNotification(false);
				 isEventsFired = true;
			 }
		 }
		 if (isEventsFired) {
			 notifyObserversEvent.setScheduledTime(plugin.getRuntimeManager().currentTime());
			 plugin.getRuntimeManager().scheduleEvent(notifyObserversEvent);
		 }
	 }

     @Override
     public void warnAllOutputObservers() {
		 for (Output o : plugin.getOutputDataMap().values()) {
			 warnedOutputs.add(o);
		 }
	 }

     @Override
     public void warnOutputObservers(String outputKey) throws KeyNotFoundException {
		 if (plugin.getOutputDataMap().get(outputKey) == null) {
			 String message = "Output key : " + outputKey + " not found in method updateOutput.";
			 throw new KeyNotFoundException(plugin, outputKey, message);
		 }
		 warnedOutputs.add(plugin.getOutputDataMap().get(outputKey));
	 }

    private void notifyOutputObservers() {
        for (Output o : plugin.getOutputDataMap().values()) {
            o.setContinueWarningNotifications(false);
            o.notifyObserversOfUpdate();
        }
    }

    private void warnOutputObservers() {
        for (Output w : warnedOutputs) {
            w.setContinueWarningNotifications(true);
        }
        for (Output w : warnedOutputs) {
            w.notifyObserversOfWarning();
        }
    }


    public void setAbstractPlugin(AbstractPlugin plugin) {
        this.plugin = plugin;
        notifyObserversEvent.setPlugin(plugin);
    }
}
