/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jmatlink;

/**
 *
 * @author amasino
 */
public interface IMatlabWorkspaceObserver {
    public void notifyCommandExecuted(MatlabWorkspaceEvent event);
}
