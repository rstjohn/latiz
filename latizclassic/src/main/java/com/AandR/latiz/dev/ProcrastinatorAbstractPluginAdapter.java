/**
 * 
 */
package com.AandR.latiz.dev;

import com.AandR.latiz.core.Event;

/**
 * @author Aaron Masino
 * @version Jan 21, 2008 4:10:24 PM <br>
 *
 * Comments:
 * acknowledgeOutputRequestNotification(String ouputKey) is the key method to be overriden by a prosctinator
 *
 */
public abstract class ProcrastinatorAbstractPluginAdapter extends AbstractPluginAdapter {

    public void initializeInputs() {
    }

    public void initializeOutputs() {
    }

    protected boolean acknowledgeEventNotification(Event event) {
        return true;
    }

    public void scheduleInitialEvent() {
    }
}
