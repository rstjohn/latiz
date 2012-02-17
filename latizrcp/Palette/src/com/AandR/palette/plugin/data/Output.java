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
package com.AandR.palette.plugin.data;

import com.AandR.library.utility.FastByteArrayOutputStream;
import com.AandR.palette.plugin.AbstractPlugin;
import com.AandR.palette.plugin.PluginComparator;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

/**
 *
 * @author Aaron Masino
 */
public class Output extends TransferableData{

    private AbstractPlugin outputPlugin;
	private ArrayList<Input> observers;
	private boolean isOutputUpdated=false;
	private TreeSet<Input> inputsAlreadyNotified;
	private boolean continueWarningNotifications=false;
	private Input outputRequestInitiator=null;
	private boolean isOutputRequestSatisfied = true;
	private int saveCount=0;
    private List<IOutputObserver> iOutputObserversList;

	private boolean isSavable;

	/**
	 *
	 * @param key
	 * @param valueClass
	 * @param genericClasses
	 * @param isUsingGenrics
	 * @param outputPlugin
	 */
	public Output(String key, Class valueClass, Class[] genericClasses, boolean isUsingGenrics,AbstractPlugin outputPlugin) {
		super(key, valueClass, genericClasses, isUsingGenrics);
		this.outputPlugin=outputPlugin;
		observers = new ArrayList<Input>();
		inputsAlreadyNotified = new TreeSet<Input>(new InputComparator());
	}

	/**
	 *
	 * @param key
	 * @param valueClass
	 * @param genericClasses
	 * @param isUsingGenrics
	 * @param toolTipText
	 * @param outputPlugin
	 */
	public Output(String key, Class valueClass, Class[] genericClasses, boolean isUsingGenrics, String toolTipText, AbstractPlugin outputPlugin) {
		super(key, valueClass, genericClasses, isUsingGenrics,toolTipText);
		this.outputPlugin=outputPlugin;
		observers = new ArrayList<Input>();
		inputsAlreadyNotified = new TreeSet<Input>(new InputComparator());
	}

	/**
	 *
	 * @param value
	 * @throws PluginRuntimeException
	 */
	public void updateOutput(Object value, double currentTime) throws IOException, ClassNotFoundException{
		this.storeNewValue(value,currentTime);
		this.timeOfLastUpdate=currentTime;
		this.indexOfLastUpdate += 1;
		this.isOutputUpdated=true;
		updateObserverInformation(value,currentTime);
		this.continueWarningNotifications=false;
        for (IOutputObserver ioo : iOutputObserversList){
            ioo.notfyOutputUpdated(this);
        }
		/*
         if(savableDataInterface==null)
			runtimeDataWriter.saveData(outputPlugin, this, value);
		else
			runtimeDataWriter.saveData(outputPlugin, this, savableDataInterface);
         */
	}


	/**
     * updates information about this Output for each Input observer
	 * @throws PluginRuntimeException
	 *
	 */
	public void updateObserverInformation(Object o, double time) throws IOException, ClassNotFoundException{
		if(observers == null || observers.size()==0) {
            return;
        }
		for(Input in : observers) {
			in.setIndexOfLastUpdate(indexOfLastUpdate);
			in.setTimeOfLastUpdate(timeOfLastUpdate);
			in.setInputUpdateAcknowledged(false);
			in.storeNewValue(getValue(time),time);
		}
	}

	protected boolean isCloneRequired() {
		boolean b1 = observers.size()>1;
		return b1 || isStatePreserved;
	}

	/**
	 *notifies each Input observer that this Output has been updated
	 */
	public void notifyObserversOfUpdate() {
		if(!isOutputUpdated)return;
		if(observers==null || observers.size()==0)return;
		/*if(outputRequestInitiator==null) {
			System.out.println("      -->-->-->--> "+getOutputPlugin().getName()+" ->"+getKey()+" notifying. request initiator = null");

		}else {
			System.out.println("      -->-->-->-->  "+getOutputPlugin().getName()+" ->"+getKey()+" notifying. request initiator = "+outputRequestInitiator.getInputPlugin().getName()+ " -> "+outputRequestInitiator.getKey());
		}*/

		for(Input input : observers) {
			if(!(inputsAlreadyNotified.contains(input) || input.equals(outputRequestInitiator))) {
				inputsAlreadyNotified.add(input);
				input.getInputPlugin().getReceiveHandler().receiveInputsModifiedNotification(input);
				input.setTimeOfLastUpdate(timeOfLastUpdate);
				input.setIndexOfLastUpdate(indexOfLastUpdate);
			}
		}
		outputRequestInitiator=null;
		inputsAlreadyNotified.clear();
		isOutputUpdated=false;
		//System.out.println("      -->-->-->-->  "+getOutputPlugin().getName()+" ->"+getKey()+" done notifying");
	}


	/**
	 *notifies each Input observer that this output may be updated
	 */
	public void notifyObserversOfWarning() {
		for(Input input : observers) {
			if(continueWarningNotifications)
				input.getInputPlugin().getReceiveHandler().receiveInputsModifiedNotification(input);
			else
				return;
		}
	}

	/**
	 *
	 * @param input
	 */
	public void addInputToAlreadyNotified(Input input) {
		inputsAlreadyNotified.add(input);
	}


	/**
	 *
	 * @param input
	 * @return true if this output has already been notified.
	 */
	public boolean isInputAlreadyNotified(Input input) {
		if(inputsAlreadyNotified.contains(input))return true;
		else return false;
	}


	/**
	 *
	 * @return the plugin containing this output.
	 */
	public AbstractPlugin getOutputPlugin() {
		return outputPlugin;
	}


	/**
	 *
	 * @param outputPlugin
	 */
	public void setOutputPlugin(AbstractPlugin outputPlugin) {
		this.outputPlugin = outputPlugin;
	}


	/**
	 *
	 * @return a list of observers.
	 */
	public ArrayList<Input> getObservers() {
		return observers;
	}


	/**
	 *
	 * @return a sorted set of observers.
	 */
	public TreeSet<AbstractPlugin> getPluginObservers(){
		if(observers==null || observers.size()==0)return null;
		TreeSet<AbstractPlugin> plugins = new TreeSet<AbstractPlugin>(new PluginComparator());
		for(Input in : observers) {
			plugins.add(in.getInputPlugin());
		}
		return plugins;
	}


	/**
	 *
	 * @param input
	 */
	public void registerObserver(Input input) {
		observers.add(input);
	}

	/**
	 *
	 * @return true if this output is up-to-date.
	 */
	public final boolean isOutputUpdated() {
		return isOutputUpdated;
	}


	/**
	 *
	 * @param isUpdated
	 */
	public final void setOutputUpdated(boolean isUpdated) {
		this.isOutputUpdated = isUpdated;
	}


	/**
	 *
	 * @return ??
	 */
	public final boolean isContinueWarningNotifications() {
		return continueWarningNotifications;
	}


	/**
	 *
	 * @param continueWarning
	 */
	public final void setContinueWarningNotifications(boolean continueWarning) {
		this.continueWarningNotifications = continueWarning;
	}


	/**
	 *
	 * @param outputRequestInitiator
	 */
	public final void setOutputRequestInitiator(Input outputRequestInitiator) {
		this.outputRequestInitiator=outputRequestInitiator;
	}


	/**
	 *
	 * @return an output request initiator.
	 */
	public final Input getOutputRequestInitiator() {
		return outputRequestInitiator;
	}

	/**
	 *
	 * @return true is the output request was satisified.
	 */
	public final boolean isOutputRequestSatisfied() {
		return isOutputRequestSatisfied;
	}


	/**
	 *
	 * @param isOutputRequestSatisfied
	 */
	public final void setOutputRequestSatisfied(boolean isOutputRequestSatisfied) {
		this.isOutputRequestSatisfied = isOutputRequestSatisfied;
	}


	/**
	 *
	 * @return The number of times this output has been saved.
	 */
	public int getSaveCount() {
		return saveCount;
	}


	/**
	 * Sets the number of times this output has been saved.
	 * @param saveCount
	 */
	public void setSaveCount(int saveCount) {
		this.saveCount = saveCount;
	}


	public final boolean isSavable() {
		return isSavable;
	}


	public final void setSavable(boolean isSavable) {
		this.isSavable = isSavable;
	}

    public List<IOutputObserver> getIOutputObserversList() {
        return iOutputObserversList;
    }

    public void setIOutputObserversList(List<IOutputObserver> iOutputObserversList) {
        this.iOutputObserversList = iOutputObserversList;
    }

    @Override
    protected Object deserializeObject(FastByteArrayOutputStream fbos) {
        return outputPlugin.deserializeObject(fbos);
    }

}
