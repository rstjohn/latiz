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

import java.util.Comparator;

/**
 *
 * @author Aaron Masino
 */
public class PluginRuntimeEventComparator implements Comparator<PluginRuntimeEvent>{
    /**
   * Comparison based on scheduled time then plugin name.
   */
  public int compare(PluginRuntimeEvent e1, PluginRuntimeEvent e2) {
    //first compare the time of the events. Will be zero if times are the same
    int value = Double.compare(e1.getScheduledTime(), e2.getScheduledTime());


    //if times are the same are the events from different plugins?
    if(value==0) {

      if(e1 instanceof NotifyObserversEvent && !(e2 instanceof NotifyObserversEvent))return -1;
      if(e2 instanceof NotifyObserversEvent && !(e1 instanceof NotifyObserversEvent))return 1;

      value=e1.getPlugin().getName().compareTo(e2.getPlugin().getName());
    }

    //if times and plugins are the same is the descriptor different?
    if(value==0)value=e1.getDescriptor().compareTo(e2.getDescriptor());
    return value;
  }

}
