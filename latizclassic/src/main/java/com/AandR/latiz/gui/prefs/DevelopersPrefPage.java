package com.AandR.latiz.gui.prefs;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import com.AandR.gui.ui.JToolbarButton;
import com.AandR.latiz.core.PropertiesManager;
import com.AandR.latiz.resources.Resources;

/**
 * @author Dr. Richard St. John
 * @version $Revision: 1.4 $, $Date: 2007/10/10 00:32:03 $
 */
public class DevelopersPrefPage extends AbstractPreferencePage {

    private JTextField fieldUsername, fieldDevFolder;

    public DevelopersPrefPage() {
        super("Developers");
        setPropPanel(createDevelopersPanel());
    }

    private JComponent createDevelopersPanel() {
        PropertiesManager props = PropertiesManager.getInstanceOf();
        String username = props.getProperty(PropertiesManager.DEVELOPERS_AUTHOR);
        final String projectFolder = props.getProperty(PropertiesManager.DEVELOPERS_PROJECT_FOLDER);

        fieldUsername = new JTextField(username, 30);
        fieldDevFolder = new JTextField(projectFolder, 30);

        JToolbarButton browseButton = new JToolbarButton(Resources.createIcon("find16.png"));
        browseButton.setPreferredSize(new Dimension(25, 25));
        browseButton.setMinimumSize(new Dimension(25, 25));
        browseButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                JFileChooser chooser = new JFileChooser(projectFolder);
                chooser.setDialogType(JFileChooser.CUSTOM_DIALOG);
                chooser.setDialogTitle("Choose Plugin Development Project Folder");
                chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                chooser.setApproveButtonText("Select");
                if (chooser.showDialog(getPropPanel(), "Select") == JFileChooser.CANCEL_OPTION) {
                    return;
                }
                String devFolder = chooser.getSelectedFile().getPath();
                fieldDevFolder.setText(devFolder);
            }
        });

        JLabel folderLabel = new JLabel("Plugin Project Folder: ");
        JPanel folderPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        folderPanel.add(folderLabel);
        folderPanel.add(fieldDevFolder);
        folderPanel.add(browseButton);

        JLabel userLabel = new JLabel("Specify Username: ");
        userLabel.setPreferredSize(folderLabel.getPreferredSize());
        JPanel usernamePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        usernamePanel.add(userLabel);
        usernamePanel.add(fieldUsername);

        JPanel panelGenerals = new JPanel(new GridLayout(2, 1, 5, 5));
        panelGenerals.setBorder(new TitledBorder("User Options"));
        panelGenerals.add(usernamePanel);
        panelGenerals.add(folderPanel);

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(panelGenerals, BorderLayout.NORTH);
        return panel;
    }

    public void fireAcceptAction() {
        PropertiesManager props = PropertiesManager.getInstanceOf();
        props.setProperty(PropertiesManager.DEVELOPERS_AUTHOR, fieldUsername.getText());
        props.setProperty(PropertiesManager.DEVELOPERS_PROJECT_FOLDER, fieldDevFolder.getText());
    }
}
