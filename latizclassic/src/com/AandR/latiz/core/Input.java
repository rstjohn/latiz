/**
 * 
 */
package com.AandR.latiz.core;

import java.util.HashMap;

import com.AandR.latiz.dev.AbstractPlugin;
import com.AandR.latiz.interfaces.ConnectionFilter;

/**
 * @author Aaron Masino
 * @version Jan 18, 2008 2:29:07 PM <br>
 *
 * Comments:
 *
 */
public class Input extends TransferableData {

    private Output connectedOutput;
    private AbstractPlugin inputPlugin;
    private boolean isInputUpdateAcknowledged;
    private ConnectionFilter connectionFilter;
    private boolean isInputChainValidated = false;
    private HashMap<String, Boolean> validOutputRequestChainMap = new HashMap<String, Boolean>();

    public Input(String key, Class valueClass, Class[] genericClasses, boolean isUsingGenrics, AbstractPlugin inputPlugin) {
        super(key, valueClass, genericClasses, isUsingGenrics);
        this.inputPlugin = inputPlugin;
    }

    public Input(String key, Class valueClass, Class[] genericClasses, boolean isUsingGenrics, String toolTipText, AbstractPlugin inputPlugin) {
        super(key, valueClass, genericClasses, isUsingGenrics, toolTipText);
        this.inputPlugin = inputPlugin;
    }

    public void setConnectedOutput(Output connectedOutput) {
        this.connectedOutput = connectedOutput;
    }

    public Output getConnectedOutput() {
        return connectedOutput;
    }

    public AbstractPlugin getInputPlugin() {
        return inputPlugin;
    }

    public void setInputPlugin(AbstractPlugin inputPlugin) {
        this.inputPlugin = inputPlugin;
    }

    @SuppressWarnings("unchecked")
    public boolean acceptConnectionToOutput(Output output) {
        if (output.isUsingGernics != this.isUsingGernics) {
            return false;
        }

        boolean accept;
        accept = LatizUtility.checkClassAssignAbility(output.getValueClass(), valueClass);
        if (this.isUsingGernics) {//need to check the genric list
            if (output.isUsingGernics) {
                accept = accept && checkGenerics(output.getGenericClasses());
            } else {
                accept = false;
            }
        }
        if (connectionFilter != null) {
            accept = accept && connectionFilter.acceptConnection(output);
        }
        return accept;
    }

    @SuppressWarnings("unchecked")
    private boolean checkGenerics(Class[] outputGenerics) {
        if (outputGenerics == null || genericClasses == null) {
            return false;
        }
        if (outputGenerics.length != genericClasses.length) {
            return false;
        }

        for (int i = 0; i < genericClasses.length; i++) {
            if (!LatizUtility.checkClassAssignAbility(outputGenerics[i], genericClasses[i])) {
                return false;
            }
        }
        return true;
    }

    public final boolean isInputUpdateAcknowledged() {
        return isInputUpdateAcknowledged;
    }

    public final void setInputUpdateAcknowledged(boolean isInputUpdateAcknowledged) {
        this.isInputUpdateAcknowledged = isInputUpdateAcknowledged;
    }

    public final ConnectionFilter getConnectionFilter() {
        return connectionFilter;
    }

    public final void setConnectionFilter(ConnectionFilter connectionFilter) {
        this.connectionFilter = connectionFilter;
    }

    public final HashMap<String, Boolean> getValidOutputRequestChainMap() {
        return validOutputRequestChainMap;
    }

    public final boolean isInputChainValidated() {
        return isInputChainValidated;
    }

    public final void setInputChainValidated(boolean isInputChainValidated) {
        this.isInputChainValidated = isInputChainValidated;
    }
}
