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
import com.AandR.palette.runtime.IOutputInputHandler;
import com.AandR.palette.runtime.exceptions.InputNotConnectedException;
import com.AandR.palette.runtime.exceptions.KeyNotFoundException;
import com.AandR.palette.runtime.exceptions.NullValueException;
import com.AandR.palette.runtime.exceptions.PluginRuntimeException;
import java.io.IOException;
import java.util.ArrayList;

/**
 *
 * @author Aaron Masino
 */
public class DefalutIOHandler implements IOutputInputHandler {

    private AbstractPlugin plugin;

    /**
	  *
	  * @param inputKey
	  * @param isInputUpdateAcknowledged
	  * @return the input object
	  * @throws KeyNotFoundException
	  * @throws NullValueException
	  * @throws InputNotConnectedException
	  */
    @Override
	 public Object getInput(String inputKey, boolean isInputUpdateAcknowledged) throws KeyNotFoundException,
	 NullValueException, InputNotConnectedException {
		 Input input = plugin.getInputDataMap().get(inputKey);
		 if (input == null){
			 throw new KeyNotFoundException(plugin, inputKey, "Input key : " + inputKey + " not found in method getInput.");
         }
		 if (!isInputConnectedToOutput(inputKey))
			 throw new InputNotConnectedException(plugin, inputKey, "Input key : " + inputKey
					 + " not connected to an Output in method getInput.");
		
		 Output outputToRequest = input.getConnectedOutput();

		 boolean outputUpdated = outputToRequest.isOutputUpdated();
		try{
         outputUpdated = outputUpdated || outputToRequest.getTimeOfLastUpdate() == getCurrentTime();
		 if (outputUpdated) {
			 outputToRequest.addInputToAlreadyNotified(input);
			 input.setInputUpdateAcknowledged(isInputUpdateAcknowledged);
			 if (input.getValue(getCurrentTime()) == null)
				 throw new NullValueException(plugin, inputKey, "Input key : " + inputKey
						 + " is connected to an output whose value is null in method getInput.");
			 return input.getValue(getCurrentTime());
		 }
		 outputToRequest.getOutputPlugin().getReceiveHandler().receiveOutputRequestNotificaton(input, "");
		 input.setInputUpdateAcknowledged(isInputUpdateAcknowledged);
		 if (input.getValue(getCurrentTime()) == null)
			 throw new NullValueException(plugin, inputKey, "Input key : " + inputKey
					 + " is connected to an output whose value is null in method getInput.");
		 return input.getValue(getCurrentTime());
        }catch(IOException e){
            plugin.throwRuntimeException(e);
            return null;
        }catch(ClassNotFoundException e){
            plugin.throwRuntimeException(e);
            return null;
        }
	 }

    /**
	  *
	  * @param inputKey
	  * @return the input object
	  * @throws KeyNotFoundException
	  * @throws NullValueException
	  * @throws InputNotConnectedException
	  */
    @Override
	 public Object getInput(String inputKey) throws KeyNotFoundException, NullValueException,
	 InputNotConnectedException {
		 return getInput(inputKey, true);
	 }

    /**
	  *
	  * @param outputKey
	  * @param value
	  * @throws KeyNotFoundException
	  */
    @Override
	 public void updateOutput(String outputKey, Object value) throws KeyNotFoundException {
        try {
            Output o = plugin.getOutputDataMap().get(outputKey);
            if (o == null) {
                String message = "Output key : " + outputKey + " not found in method updateOutput.";
                throw new KeyNotFoundException(plugin, outputKey, message);
            } else {
                o.updateOutput(value, plugin.getRuntimeManager().currentTime());
            }
        } catch (IOException ex) {
            plugin.throwRuntimeException(ex);
        } catch (ClassNotFoundException ex) {
            plugin.throwRuntimeException(ex);
        }
	 }

     /**
	  * used to get a previously stored input value.
	  *
	  * @param inputKey
	  * @param time
	  * @return
	  * @throws PluginRuntimeException
	  */
    @Override
	 public Object getRecallableInput(String inputKey, double time) throws KeyNotFoundException,
	 NullValueException, InputNotConnectedException {
        try {
            if (time > getCurrentTime()) {
                plugin.throwRuntimeException(new PluginRuntimeException(plugin, "Invalid Request Time"));
            }
            Input input = plugin.getInputDataMap().get(inputKey);
            if (input == null) {
                throw new KeyNotFoundException(plugin, inputKey, "Input key : " + inputKey + " not found in method getRecallableInput.");
            }
            if (!isInputConnectedToOutput(inputKey)) {
                throw new InputNotConnectedException(plugin, inputKey, "Input key : " + inputKey + " not connected to an Output in method getInput.");
            }
            Object o = input.getValue(time);
            if (o == null) {
                throw new NullValueException(plugin, inputKey, "Input key : " + inputKey + " has a null value in getRecallableInput");
            }
            return o;
        } catch (IOException ex) {
            plugin.throwRuntimeException(ex);
            return null;
        } catch (ClassNotFoundException ex) {
            plugin.throwRuntimeException(ex);
            return null;
        }
	 }

    @Override
    public ArrayList<Object> getRecallableInput(String inputKey, double time, int count) throws PluginRuntimeException{
        try {
            if (time > getCurrentTime()) {
                plugin.throwRuntimeException(new PluginRuntimeException(plugin, "Invalid Request Time"));
            }
            Input input = plugin.getInputDataMap().get(inputKey);
            if (input == null) {
                throw new KeyNotFoundException(plugin, inputKey, "Input key : " + inputKey + " not found in method getRecallableInput.");
            }
            if (!isInputConnectedToOutput(inputKey)) {
                throw new InputNotConnectedException(plugin, inputKey, "Input key : " + inputKey + " not connected to an Output in method getInput.");
            }
            ArrayList<Object> o = input.getValue(time, count);
            if (o == null) {
                throw new NullValueException(plugin, inputKey, "Input key : " + inputKey + " has a null value in getRecallableInput");
            }
            return o;
        } catch (IOException ex) {
            plugin.throwRuntimeException(ex);
            return null;
        } catch (ClassNotFoundException ex) {
            plugin.throwRuntimeException(ex);
            return null;
        }
    }

    public Object getOutput(String outputKey) throws KeyNotFoundException {
        try {
            Output output = plugin.getOutputDataMap().get(outputKey);
            if (output == null) {
                String message = "Output key : " + outputKey + " not found in method getOutput.";
                throw new KeyNotFoundException(plugin, outputKey, message);
            }
            return output.getValue(getCurrentTime());
        } catch (IOException ex) {
            plugin.throwRuntimeException(ex);
            return null;
        } catch (ClassNotFoundException ex) {
            plugin.throwRuntimeException(ex);
            return null;
        }
	 }

    @Override
    public Object getRecallableOutput(String outputKey, double time) throws PluginRuntimeException, KeyNotFoundException, NullValueException {
        try {
            if (time > getCurrentTime()) {
                plugin.throwRuntimeException(new PluginRuntimeException(plugin, "Invalid Request Time"));
            }
            Output output = plugin.getOutputDataMap().get(outputKey);
            if (output == null) {
                String message = "Output key : " + outputKey + " not found in method getRecallabeOutput.";
                throw new KeyNotFoundException(plugin, outputKey, message);
            }
            Object o = output.getValue(time);
            if (o == null) {
                throw new NullValueException(plugin, outputKey, "Output key : " + outputKey + " has a null value in getRecallableOutput");
            }
            return o;
        } catch (IOException ex) {
            plugin.throwRuntimeException(ex);
            return null;
        } catch (ClassNotFoundException ex) {
            plugin.throwRuntimeException(ex);
            return null;
        }
	 }

     @Override
     public ArrayList<Object> getRecallableOutput(String outputKey, double time, int count)  throws PluginRuntimeException, KeyNotFoundException, NullValueException{
        try {
            if (time > getCurrentTime()) {
                plugin.throwRuntimeException(new PluginRuntimeException(plugin, "Invalid Request Time"));
            }
            Output output = plugin.getOutputDataMap().get(outputKey);
            if (output == null) {
                String message = "Output key : " + outputKey + " not found in method getRecallableOutput.";
                throw new KeyNotFoundException(plugin, outputKey, message);
            }
            ArrayList<Object> o = output.getValue(time, count);
            if (o == null) {
                throw new NullValueException(plugin, outputKey, "Output key : " + outputKey + " has a null value in getRecallableInput");
            }
            return o;
        } catch (IOException ex) {
            plugin.throwRuntimeException(ex);
            return null;
        } catch (ClassNotFoundException ex) {
            plugin.throwRuntimeException(ex);
            return null;
        }
	 }

    @Override
     public void setAbstractPlugin(AbstractPlugin plugin) {
        this.plugin = plugin;
    }

    /**
	  *
	  * @param inputKey
	  * @return true if the output is connected.
	  * @throws KeyNotFoundException
	  */
	 private boolean isInputConnectedToOutput(String inputKey) throws KeyNotFoundException {
		 Input input = plugin.getInputDataMap().get(inputKey);
		 if (input == null)
			 throw new KeyNotFoundException(plugin, inputKey, "Input key : " + inputKey + " not found in method getInput.");
		 return (input.getConnectedOutput() != null);
	 }

     private double getCurrentTime(){
         return plugin.getRuntimeManager().currentTime();
     }

}
