/**
 * 
 */
package com.AandR.latiz.core;

import com.AandR.latiz.dev.AbstractPlugin;

/**
 * @author Aaron Masino
 * @version Jan 21, 2008 12:35:32 PM <br>
 *
 * Comments:
 *
 */
public class NotifyObserversEvent extends Event {

    public static final String notifyObserversEventDescription = "notifyObservers";

    public NotifyObserversEvent() {
        setDescriptor(notifyObserversEventDescription);
    }

    public NotifyObserversEvent(double time, AbstractPlugin plugin) {
        super(notifyObserversEventDescription, time, plugin);
    }
}
