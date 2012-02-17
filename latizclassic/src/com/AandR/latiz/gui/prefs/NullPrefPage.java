package com.AandR.latiz.gui.prefs;

import javax.swing.JPanel;

/**
 * @author Dr. Richard St. John
 * @version $Revision: 1.1 $, $Date: 2007/08/24 03:13:51 $
 */
public class NullPrefPage extends AbstractPreferencePage {

    public NullPrefPage(String treeLabel) {
        super(treeLabel, new JPanel());
    }

    public void fireAcceptAction() {
    }
}
