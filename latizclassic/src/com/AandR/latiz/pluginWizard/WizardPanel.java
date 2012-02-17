package com.AandR.latiz.pluginWizard;

import java.awt.LayoutManager;

import javax.swing.JPanel;

/**
 * @author Dr. Richard St. John
 * @version $Revision$, $Date$
 */
public abstract class WizardPanel extends JPanel {

    /**
     *
     */
    public WizardPanel() {
    }

    public WizardPanel(LayoutManager layout) {
        super(layout);
    }

    public abstract String getMessageTitle();

    public abstract String getMessageLabel();
}
