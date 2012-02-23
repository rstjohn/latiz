/**
 * 
 */
package com.AandR.latiz.core;

import java.util.Comparator;

/**
 * @author Aaron Masino
 * @version Jan 18, 2008 5:51:45 PM <br>
 *
 * Comments:
 *
 */
public class EventComparator implements Comparator<Event> {

    /* (non-Javadoc)
     * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
     */
    public int compare(Event e1, Event e2) {
        //first compare the time of the events. Will be zero if times are the same
        int value = Double.compare(e1.getScheduledTime(), e2.getScheduledTime());


        //if times are the same are the events from different plugins?
        if (value == 0) {

            if (e1 instanceof NotifyObserversEvent && !(e2 instanceof NotifyObserversEvent)) {
                return -1;
            }
            if (e2 instanceof NotifyObserversEvent && !(e1 instanceof NotifyObserversEvent)) {
                return 1;
            }

            value = e1.getPlugin().getName().compareTo(e2.getPlugin().getName());
        }

        //if times and plugins are the same is the descriptor different?
        if (value == 0) {
            value = e1.getDescriptor().compareTo(e2.getDescriptor());
        }
        return value;
    }
}
