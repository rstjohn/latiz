/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jmatlink;

/**
 *
 * @author Aaron Masino
 */
public class VariableNotFoundException extends Exception{
    private String variable;

    public VariableNotFoundException(String variable){
        this.variable=variable;
    }

    public String getVariable() {
        return variable;
    }
}
