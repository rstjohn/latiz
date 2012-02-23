/**
 * 
 */
package com.AandR.latiz.core;

import com.AandR.latiz.dev.AbstractPlugin;

/**
 * @author Aaron Masino
 * @version Jan 18, 2008 1:58:59 PM <br>
 *
 * Comments:
 *
 */
public class Event {

    private double scheduledTime;
    private AbstractPlugin plugin;
    private String descriptor;

    public Event() {
    }

    /**
     *
     * @param description
     * @param timeFromNow
     */
    public Event(String description, double scheduledTime, AbstractPlugin plugin) {
        this.descriptor = description;
        this.scheduledTime = scheduledTime;
        this.plugin = plugin;
    }

    public String getDescriptor() {
        return descriptor;
    }

    public double getScheduledTime() {
        return scheduledTime;
    }

    public AbstractPlugin getPlugin() {
        return plugin;
    }

    public void setDescriptor(String descriptor) {
        this.descriptor = descriptor;
    }

    public void setScheduledTime(double scheduledTime) {
        this.scheduledTime = scheduledTime;
    }

    public void setPlugin(AbstractPlugin plugin) {
        this.plugin = plugin;
    }
}
