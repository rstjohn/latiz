package com.AandR.latiz.gui.prefs;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;


import com.AandR.gui.OptionsDialog;
import com.AandR.gui.ui.JButtonX;
import com.AandR.gui.ui.JToolbarButton;
import com.AandR.latiz.core.PropertiesManager;
import com.AandR.latiz.resources.Resources;

/**
 * @author Dr. Richard St. John
 * @version $Revision: 1.4 $, $Date: 2007/10/10 00:32:03 $
 */
public class RepositoryPrefPage extends AbstractPreferencePage implements ActionListener {

    private JComboBox repoDirCombo;

    public RepositoryPrefPage() {
        super("Repository");
        setPropPanel(createRepoPanel());
    }

    private JPanel createRepoPanel() {
        PropertiesManager props = PropertiesManager.getInstanceOf();
        String[] repos = props.getPropertyList(PropertiesManager.REPO_DIRS);
        String defaultRepo = props.getProperty(PropertiesManager.REPO_DEFAULT);
        repoDirCombo = new JComboBox(repos);
        repoDirCombo.setSelectedItem(defaultRepo);

        JPanel repoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        repoPanel.add(new JLabel("Choose:"));
        repoPanel.add(repoDirCombo);
        repoPanel.add(createButton("db_add.png", "ACTION_ADD", "Add new repository"));
        repoPanel.add(createButton("db_remove.png", "ACTION_REMOVE", "Add new repository"));

        JPanel p = new JPanel(new GridLayout(2, 1, 5, 5));
        p.add(repoPanel);
        return p;
    }

    private JToolbarButton createButton(String path, String command, String toolTip) {
        JToolbarButton button = new JToolbarButton(Resources.createIcon(path));
        button.setActionCommand(command);
        button.setPreferredSize(new Dimension(28, 28));
        button.addActionListener(this);
        return button;
    }

    public void fireAcceptAction() {
        String[] repos = new String[repoDirCombo.getItemCount()];
        for (int i = 0; i < repos.length; i++) {
            repos[i] = repoDirCombo.getItemAt(i).toString();
        }
        PropertiesManager props = PropertiesManager.getInstanceOf();
        props.setProperty(PropertiesManager.REPO_DEFAULT, repoDirCombo.getSelectedItem().toString());
        props.setPropertyList(PropertiesManager.REPO_DIRS, repos);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        if (command.equalsIgnoreCase("ACTION_ADD")) {
            addNewRepo();
        } else if (command.equalsIgnoreCase("ACTION_REMOVE")) {
            removeSelectedRepo();
        }
    }

    private void removeSelectedRepo() {
        repoDirCombo.removeItem(repoDirCombo.getSelectedItem());
        repoDirCombo.repaint();
    }

    private void addNewRepo() {
        OptionsDialog options = new OptionsDialog(null, "Add New Repository", new JButtonX[]{new JButtonX("Add"), new JButtonX("Cancel")}, OptionsDialog.QUESTION_ICON, true);
        options.showDialog("Specify the Repository Location", 0);
        if (options.getSelectedButtonIndex() == 1) {
            return;
        }

        String newRepo = options.getInput();
        repoDirCombo.insertItemAt(newRepo, 0);
        repoDirCombo.setSelectedIndex(0);
    }
}
