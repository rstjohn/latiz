package com.AandR.latiz.dev;

/**
 * @author Dr. Richard St. John
 * @version $Revision$, $Date$
 */
public abstract class EventSchedulerAdapter extends AbstractPluginAdapter {

    public void initializeInputs() {
    }

    public void initializeOutputs() {
    }

    protected boolean acknowledgeModifiedInputsNotification(String inputKey) {
        return true;
    }

    protected boolean acknowledgeOutputRequestNotification(String outputKey) {
        return true;
    }
}
