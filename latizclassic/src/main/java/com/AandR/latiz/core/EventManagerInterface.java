/**
 * 
 */
package com.AandR.latiz.core;

import java.util.ArrayList;

import com.AandR.latiz.dev.AbstractPlugin;

/**
 * @author Aaron Masino
 * @version Jan 18, 2008 4:19:17 PM <br>
 *
 * Comments:
 *
 */
public interface EventManagerInterface {

    public void scheduleEvent(Event event);

    public double currentTime();

    public Event getNextEvent(AbstractPlugin p);

    public ArrayList<Event> getAllEvents(AbstractPlugin p);

    public void cancelEvent(Event e);

    public void appendDataToLatFile(AbstractPlugin plugin, Output output, Object data);

    public void notifyExecutionFailure(String pluginName);

    public void validateOutputRequest(Input input);
}
