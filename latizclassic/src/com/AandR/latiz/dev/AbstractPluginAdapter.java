/**
 * 
 */
package com.AandR.latiz.dev;

import javax.swing.JComponent;

import org.jdom.Element;

import com.AandR.gui.dropSupport.DropEvent;
import com.AandR.gui.dropSupport.DropListener;

/**
 * @author Aaron Masino
 * @version Oct 30, 2007 3:46:48 PM <br>
 * 
 * Comments:
 * 
 */
public abstract class AbstractPluginAdapter extends AbstractPlugin implements DropListener {

    public AbstractPluginAdapter() {
        initializeAdapter();
    }

    private void initializeAdapter() {
        addDropListener(this);
    }

    public JComponent createParametersPanel() {
        return null;
    }

    public Element createWorkspaceParameters() {
        return null;
    }

    public void loadSavedWorkspaceParameters(Element e) {
    }

    public void dropAction(DropEvent dropEvent) {
    }

    public void paintPluginPanel() {
    }
}
