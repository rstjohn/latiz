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

import com.AandR.palette.runtime.exceptions.InputNotConnectedException;
import com.AandR.palette.runtime.exceptions.KeyNotFoundException;
import com.AandR.palette.runtime.exceptions.NullValueException;
import com.AandR.palette.runtime.exceptions.PluginRuntimeException;
import java.util.ArrayList;

/**
 *
 * @author Aaron Masino
 */
public interface IPluginConvenience {
    public void updateOutput(String outputKey, Object value) throws KeyNotFoundException , NullValueException,
		InputNotConnectedException  ;
	 public Object getInput(String inputKey) throws KeyNotFoundException, NullValueException,
		InputNotConnectedException  ;
	 public Object getInput(String inputKey, boolean isInputUpdateAcknowledged) throws KeyNotFoundException,
		NullValueException, InputNotConnectedException;
	 public Object getOutput(String outputKey)throws KeyNotFoundException ;
	 public void warnAllOutputObservers();
	 public void warnOutputObservers(String outputKey)throws KeyNotFoundException ;
	 public boolean isOutputConnectToAnyInputs(String outputKey) throws KeyNotFoundException;
	 public boolean isInputConnectedToOutput(String inputKey) throws KeyNotFoundException ;
	 public boolean isInputUpdateAcknowledged(String inputKey) throws KeyNotFoundException ;
	 public void scheduleEvent(String description, double timeFromNow);
	 public double getCurrentTime();
	 public double getInputUpdateTime(String inputKey)throws KeyNotFoundException, InputNotConnectedException ;
	 public int getInputUpdateIndex(String inputKey)throws KeyNotFoundException, InputNotConnectedException ;
	 public void cancelEvents(double latestScheduledTime);
	 public boolean isFirstModifiedInputNotification();
	 public boolean isFirstEventNotification();
	 public boolean isFirstOutputRequestNotification();
	 public Object getGlobalValue(String globalVariableName);
     public Object getRecallableInput(String inputKey, double time) throws KeyNotFoundException,
	 NullValueException, InputNotConnectedException;
     public ArrayList<Object> getRecallableInput(String inputKey, double time, int count) throws PluginRuntimeException;
     public Object getRecallableOutput(String outputKey, double time) throws PluginRuntimeException, KeyNotFoundException, NullValueException;
     public ArrayList<Object> getRecallableOutput(String outputKey, double time, int count)  throws PluginRuntimeException, KeyNotFoundException, NullValueException;
     public void setInputStatePreserved(String inputKey, boolean isStatePreserved) throws KeyNotFoundException;
     public void setOutputStatePreserved(String outputKey, boolean isStatePreserved) throws KeyNotFoundException;
     public void setInputMaxRecall(String inputKey, int count) throws KeyNotFoundException;
     public void setOutputMaxRecall(String outputKey, int count) throws KeyNotFoundException;
     public void fireOutputsChanged();
     public void fireInputsChanged();
}
