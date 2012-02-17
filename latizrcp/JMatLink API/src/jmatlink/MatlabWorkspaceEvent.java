/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jmatlink;

/**
 *
 * @author amasino
 */
public class MatlabWorkspaceEvent {
    String command;
    String jMatlinkCommandType;
    String putVariable;
    String matlabBuffer;

    public static final String JM_COMMAND_TYPE_PUT = "PUT";
    public static final String JM_COMMAND_TYPE_EVAL = "EVAL";

    public String getPutVariable() {
        return putVariable;
    }

    public void setPutVariable(String putVariable) {
        this.putVariable = putVariable;
    }

    public String getjMatlinkCommandtType() {
        return jMatlinkCommandType;
    }

    public void setjMatlinkCommandType(String jMatlinkCommand) {
        this.jMatlinkCommandType = jMatlinkCommand;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String getMatlabBuffer() {
        return matlabBuffer;
    }

    public void setMatlabBuffer(String matlabBuffer) {
        this.matlabBuffer = matlabBuffer;
    }
    
}
