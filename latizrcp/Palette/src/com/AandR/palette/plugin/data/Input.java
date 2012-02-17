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

import com.AandR.library.utility.ClassWorker;
import com.AandR.library.utility.FastByteArrayOutputStream;
import com.AandR.palette.plugin.AbstractPlugin;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author Aaron Masino
 */
public class Input extends TransferableData{

    private boolean isInputUpdateAcknowledged = false;

	private boolean isInputChainValidated = false;

	private AbstractPlugin inputPlugin;

	private ArrayList<ConnectionListener> connectionListeners = new ArrayList<ConnectionListener>();

	private ConnectionFilter connectionFilter;

	private HashMap<String,Boolean> validOutputRequestChainMap=new HashMap<String, Boolean>();

	private Output connectedOutput;


	/**
	 *
	 * @param key
	 * @param valueClass
	 * @param genericClasses
	 * @param isUsingGenrics
	 * @param inputPlugin
	 */
	public Input(String key, Class valueClass, Class[] genericClasses, boolean isUsingGenrics, AbstractPlugin inputPlugin) {
		super(key, valueClass,genericClasses,isUsingGenrics);
		this.inputPlugin=inputPlugin;
	}


	/**
	 *
	 * @param key
	 * @param valueClass
	 * @param genericClasses
	 * @param isUsingGenrics
	 * @param toolTipText
	 * @param inputPlugin
	 */
	public Input(String key, Class valueClass, Class[] genericClasses, boolean isUsingGenrics, String toolTipText, AbstractPlugin inputPlugin) {
		super(key, valueClass, genericClasses,isUsingGenrics, toolTipText);
		this.inputPlugin=inputPlugin;
	}


	/**
	 *
	 * @param connectedOutput
	 */
	public void setConnectedOutput(Output connectedOutput) {
		this.connectedOutput = connectedOutput;
	}


	/**
	 *
	 * @return the {@code Output} connected to this input.
	 */
	public Output getConnectedOutput() {
		return connectedOutput;
	}


	/**
	 *
	 * @return the plugin associated with this input.
	 */
	public AbstractPlugin getInputPlugin() {
		return inputPlugin;
	}


	/**
	 *
	 * @param inputPlugin
	 */
	public void setInputPlugin(AbstractPlugin inputPlugin) {
		this.inputPlugin = inputPlugin;
	}


	/**
	 *
	 * @param output
	 * @return true if {@code output} connection is accepted.
	 */
	public boolean acceptConnectionToOutput(Output output) {
		if(output.isUsingGernics!=this.isUsingGernics)return false;

		boolean accept;
		accept = ClassWorker.checkClassAssignAbility(output.getValueClass(), valueClass);
		if(this.isUsingGernics) {//need to check the genric list
			if(output.isUsingGernics) {
				accept = accept && checkGenerics(output.getGenericClasses());
			}else accept=false;
		}
		if(connectionFilter!=null)accept = accept && connectionFilter.acceptConnection(output);
		return accept;
	}


	/**
	 *
	 * @param output
	 */
	public void notifyConnectionListeners(Output output) {
		for(ConnectionListener cl : connectionListeners)cl.receiveConnectionNotification(this,output);
	}


	/**
	 *
	 * @param c
	 */
	public void addConnectionListener(ConnectionListener c) {
		connectionListeners.add(c);
	}


	private boolean checkGenerics(Class[] outputGenerics) {
		if(outputGenerics==null || genericClasses==null)return false;
		if(outputGenerics.length!=genericClasses.length)return false;

		for(int i=0; i<genericClasses.length; i++) {
			if(!ClassWorker.checkClassAssignAbility(outputGenerics[i], genericClasses[i]))return false;
		}
		return true;
	}


	/**
	 *
	 * @return true if the input is up-to-date.
	 */
	public final boolean isInputUpdateAcknowledged() {
		return isInputUpdateAcknowledged;
	}


	/**
	 *
	 * @param isInputUpdateAcknowledged
	 */
	public final void setInputUpdateAcknowledged(boolean isInputUpdateAcknowledged) {
		this.isInputUpdateAcknowledged = isInputUpdateAcknowledged;
	}


	/**
	 *
	 * @return the connection filter
	 */
	public final ConnectionFilter getConnectionFilter() {
		return connectionFilter;
	}


	/**
	 *
	 * @param connectionFilter
	 */
	public final void setConnectionFilter(ConnectionFilter connectionFilter) {
		this.connectionFilter = connectionFilter;
	}


	/**
	 *
	 * @return a map of chain map.
	 */
	public final HashMap<String, Boolean> getValidOutputRequestChainMap() {
		return validOutputRequestChainMap;
	}


	/**
	 *
	 * @return true if the input chain has been validated.
	 */
	public final boolean isInputChainValidated() {
		return isInputChainValidated;
	}


	/**
	 *
	 * @param isInputChainValidated
	 */
	public final void setInputChainValidated(boolean isInputChainValidated) {
		this.isInputChainValidated = isInputChainValidated;
	}

	protected boolean isCloneRequired() {
		return isStatePreserved;
	}

    @Override
    protected Object deserializeObject(FastByteArrayOutputStream fbos) {
        return inputPlugin.deserializeObject(fbos);
    }
}
