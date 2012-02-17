package com.AandR.latiz.gui.prefs;

import javax.swing.JComponent;
import javax.swing.JPanel;

/**
 * @author Dr. Richard St. John
 * @version $Revision: 1.1 $, $Date: 2007/08/24 03:13:52 $
 */
public abstract class AbstractPreferencePage {

    private JComponent propPanel;
    private String treeLabel;

    public abstract void fireAcceptAction();

    public AbstractPreferencePage(String treeLabel) {
        this.treeLabel = treeLabel;
    }

    public AbstractPreferencePage(String treeLabel, JPanel propPanel) {
        this.treeLabel = treeLabel;
        this.propPanel = propPanel;
    }

    @Override
    public String toString() {
        return treeLabel;
    }

    public JComponent getPropPanel() {
        return propPanel;
    }

    public void setPropPanel(JComponent propPanel) {
        this.propPanel = propPanel;
    }

    public String getTreeLabel() {
        return treeLabel;
    }
}
