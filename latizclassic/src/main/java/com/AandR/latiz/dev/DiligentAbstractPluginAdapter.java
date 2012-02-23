/**
 * 
 */
package com.AandR.latiz.dev;

import com.AandR.latiz.core.Event;

/**
 * @author Aaron Masino
 * @version Jan 20, 2008 2:40:52 PM <br>
 * 
 * Comments:
 * 
 */
public abstract class DiligentAbstractPluginAdapter extends AbstractPluginAdapter {

    public void initializeInputs() {
    }

    public void initializeOutputs() {
    }

    protected boolean acknowledgeEventNotification(Event event) {
        return true;
    }

    protected boolean acknowledgeOutputRequestNotification(String outputKey) {
        return true;
    }

    public void scheduleInitialEvent() {
    }
}
