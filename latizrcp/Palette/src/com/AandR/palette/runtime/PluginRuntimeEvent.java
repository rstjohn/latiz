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

/**
 *
 * @author Aaron Masino
 */
public class PluginRuntimeEvent {
    private double scheduledTime;

  private AbstractPlugin plugin;

  private String descriptor;

  /**
   *
   */
  public PluginRuntimeEvent() {}


  /**
   *
   * @param description
   * @param scheduledTime
   * @param plugin
   */
  public PluginRuntimeEvent(String description, double scheduledTime, AbstractPlugin plugin) {
    this.descriptor = description;
    this.scheduledTime=scheduledTime;
    this.plugin=plugin;
  }


  /**
   *
   * @return The descriptor
   */
  public String getDescriptor() {
    return descriptor;
  }


  /**
   *
   * @return the scheduled time
   */
  public double getScheduledTime() {
    return scheduledTime;
  }


  /**
   *
   * @return The abstract plugin for this event.
   */
  public AbstractPlugin getPlugin() {
    return plugin;
  }


  /**
   *
   * @param descriptor
   */
  public void setDescriptor(String descriptor) {
    this.descriptor = descriptor;
  }


  /**
   *
   * @param scheduledTime
   */
  public void setScheduledTime(double scheduledTime) {
    this.scheduledTime = scheduledTime;
  }


  /**
   *
   * @param plugin
   */
  public void setPlugin(AbstractPlugin plugin) {
    this.plugin = plugin;
  }

}
