/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jmatlink;

import java.util.ArrayList;

/**
 *
 * @author rstjohn
 */
public class MatLabObject {
    
    private long engHandle;
    private JMatLink jMatLink;
    private String name;
    private ArrayList<IMatlabWorkspaceObserver> observers;
    private String outputBuffer;

    public MatLabObject() {
        jMatLink = new JMatLink();
        observers = new ArrayList<IMatlabWorkspaceObserver>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public void eval(String commandString) {
        jMatLink.engEvalString(engHandle, commandString);
        outputBuffer = jMatLink.engGetOutputBuffer(engHandle);
        MatlabWorkspaceEvent e = new MatlabWorkspaceEvent();
        e.setCommand(commandString);
        e.setMatlabBuffer(outputBuffer);
        notifyObserversOfEval(e);
    }

    /**
     * Evaluates the given string and returns the output buffer.
     * @param commandString
     * @return
     */
    public String evalInBackground(String commandString) {
        String currentBuffer = outputBuffer;
        jMatLink.engEvalString(engHandle, commandString);
        String s = jMatLink.engGetOutputBuffer(engHandle);
        outputBuffer = currentBuffer;
        return s;
    }

    public String getOutputBuffer() {
        if (outputBuffer == null) {
            return jMatLink.engGetOutputBuffer(engHandle);
        } else {
            return outputBuffer;
        }
    }

    public void open() {
        engHandle = jMatLink.engOpenSingleUse();
        jMatLink.engOutputBuffer(engHandle);
    }

    public long getEngHandle() {
        return engHandle;
    }

    public JMatLink getJMatLink() {
        return jMatLink;
    }

    public void close() {
        jMatLink.engClose(engHandle);
    }

    public double[][] getArray(String var) {
        return jMatLink.engGetArray(engHandle, var);
    }

    public double getScalar(String var) {
        return jMatLink.engGetScalar(engHandle, var);
    }

    public void putArray(String var, double val) {
        jMatLink.engPutArray(engHandle, var, val);
        notifyObserversOfPut(var);
    }

    public void putArray(String var, double[] val) {
        jMatLink.engPutArray(engHandle, var, val);
        notifyObserversOfPut(var);
    }

    public void putArray(String var, double[][] val) {
        jMatLink.engPutArray(engHandle, var, val);
        notifyObserversOfPut(var);
    }

    public void setVisible(boolean visible) {
        jMatLink.engSetVisible(engHandle, visible);
    }

    private void notifyObserversOfPut(String putVar){
        MatlabWorkspaceEvent e = new MatlabWorkspaceEvent();
        e.setPutVariable(putVar);
        e.setjMatlinkCommandType(MatlabWorkspaceEvent.JM_COMMAND_TYPE_PUT);
        for(IMatlabWorkspaceObserver o : observers){
            o.notifyCommandExecuted(e);
        }
    }

    private void notifyObserversOfEval(MatlabWorkspaceEvent e){
        e.setjMatlinkCommandType(MatlabWorkspaceEvent.JM_COMMAND_TYPE_EVAL);
        for(IMatlabWorkspaceObserver o : observers){
            o.notifyCommandExecuted(e);
        }
    }

    public void registerObserver(IMatlabWorkspaceObserver o){
        observers.add(o);
    }

    public void deregisterObserver(IMatlabWorkspaceObserver o){
        observers.remove(o);
    }
}
