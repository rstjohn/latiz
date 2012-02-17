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
package com.AandR.palette.runtime;

import com.AandR.palette.plugin.AbstractPlugin;
import com.AandR.palette.plugin.data.Input;
import com.AandR.palette.runtime.exceptions.KeyNotFoundException;

/**
 *
 * @author Aaron Masino
 */
public interface IReceiveHandler {

    /**
	 * Performs general logic tasks associated with receiving an event notification. These actions
     * performed any plugin when receiving an event notifcation. This method should call the
     * plugins acknowledgeEventNotification method
	 * @param event
	 */
	 public void receiveEventNotification(PluginRuntimeEvent event);

     /**
	  *Performs general logic tasks associated with receiving a modified input notificaiton. These
      * actions are performed by any plugin when receiving an event notification. This method should
      * call the plugins acknowledgeModifiedInputsNotification method
	  * @param input
	  */
	 public void receiveInputsModifiedNotification(Input input);

     /**
      * Performs general logic tasks associated with receiving an output request notification. These
      * actions are performed by any plugin when receiving an output request notification. This
      * methoud should call the the plugins acknowledgeOutputRequestNotification method. The prefix
      * argument is passed in by the IGetIOValues class and can be used to determine if a request
      * chain is valid or has become circular.
      * @param input
      * @param prefix
      */
     public void receiveOutputRequestNotificaton(Input input, String prefix);

     /**
      * Used by procrastiantor plugins to warn all output observers that new output could be available
      */
     public void warnAllOutputObservers();

     /**
      * used by procrastinator plugins to warn the Output in the plugins outputDataMap corresponding to the
      * outputKey that new output could be available
      * @param outputKey
      * @throws KeyNotFoundException
      */
     public void warnOutputObservers(String outputKey) throws KeyNotFoundException;

     /**
      * sets the AbstractPlugin that this IReceiverHandler belongs to
      * @param plugin
      */
     public void setAbstractPlugin(AbstractPlugin plugin);
}
