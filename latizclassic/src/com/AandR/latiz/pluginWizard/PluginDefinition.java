package com.AandR.latiz.pluginWizard;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import com.AandR.gui.ui.JToolbarButton;
import com.AandR.latiz.core.PropertiesManager;
import com.AandR.latiz.resources.Resources;

/**
 * @author Dr. Richard St. John
 * @version $Revision$, $Date$
 */
public class PluginDefinition extends WizardPanel {

    private JTextField fieldDevFolder, fieldPluginID;
    private JTextField fieldAuthor, fieldDate;
    private JTextArea fieldDescription;
    private JComboBox comboParentID;

    public PluginDefinition() {
        fieldAuthor = new JTextField(System.getProperty("user.name"), 15);
        fieldDate = new JTextField(new Date().toString());

        comboParentID = new JComboBox(new String[]{
                    "Data Creators",
                    "File Readers",
                    "File Writers",
                    "Processors",
                    "Plotters",});
        comboParentID.setEditable(true);

        JPanel northPanel = new JPanel(new GridLayout(4, 1, 5, 5));
        northPanel.add(createInputPanel("Plugin Author:", fieldAuthor));
        northPanel.add(createInputPanel("Creation Date:", fieldDate));
        northPanel.add(createInputPanel("Category ID:", comboParentID));
        northPanel.add(createInputPanel("Plugin ID:", fieldPluginID = new JTextField()));

        fieldDescription = new JTextArea();
        JPanel descriptionPanel = new JPanel(new BorderLayout());
        descriptionPanel.add(new JScrollPane(fieldDescription), BorderLayout.CENTER);

        JLabel l = new JLabel("Description:");
        l.setPreferredSize(new Dimension(120, 20));

        JPanel descPanel = new JPanel(new BorderLayout());
        descPanel.add(l, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new BorderLayout(0, 5));
        centerPanel.add(descPanel, BorderLayout.WEST);
        centerPanel.add(descriptionPanel, BorderLayout.CENTER);

        JToolbarButton browseButton = new JToolbarButton(Resources.createIcon("find16.png"));
        browseButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                JFileChooser chooser = new JFileChooser(System.getProperty("user.home"));
                chooser.setDialogType(JFileChooser.CUSTOM_DIALOG);
                chooser.setDialogTitle("Choose Plugin Development Project Folder");
                chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                chooser.setApproveButtonText("Select");
                if (chooser.showDialog(PluginDefinition.this, "Select") == JFileChooser.CANCEL_OPTION) {
                    return;
                }
                String devFolder = chooser.getSelectedFile().getPath();
                fieldDevFolder.setText(devFolder);
            }
        });
        browseButton.setPreferredSize(new Dimension(24, 24));
        browseButton.setMinimumSize(new Dimension(24, 24));

        String devFolder = PropertiesManager.getInstanceOf().getProperty(PropertiesManager.DEVELOPERS_PROJECT_FOLDER);
        Container projectPanel = createInputPanel("Development Folder:", fieldDevFolder = new JTextField(devFolder));
        projectPanel.add(browseButton, BorderLayout.EAST);

        setLayout(new BorderLayout(5, 5));
        setBorder(new EmptyBorder(5, 10, 5, 10));
        add(northPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
        add(projectPanel, BorderLayout.SOUTH);
    }

    private JPanel createInputPanel(String label, JComponent field) {
        JLabel jlabel = new JLabel(label);
        jlabel.setPreferredSize(new Dimension(120, 20));
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(jlabel, BorderLayout.WEST);
        panel.add(field, BorderLayout.CENTER);
        return panel;
    }

    public JTextArea getFieldDescription() {
        return fieldDescription;
    }

    public void setFieldDescription(JTextArea fieldDescription) {
        this.fieldDescription = fieldDescription;
    }

    public JTextField getFieldDevFolder() {
        return fieldDevFolder;
    }

    public void setFieldDevFolder(JTextField fieldDevFolder) {
        this.fieldDevFolder = fieldDevFolder;
    }

    public JTextField getFieldPluginID() {
        return fieldPluginID;
    }

    public void setFieldPluginID(JTextField fieldPluginID) {
        this.fieldPluginID = fieldPluginID;
    }

    public JComboBox getComboParentID() {
        return comboParentID;
    }

    public void setComboParentID(JComboBox comboParentID) {
        this.comboParentID = comboParentID;
    }

    public String getMessageLabel() {
        return "Choose the options that will be used to generate the new plugin skeleton source code.";
    }

    public String getMessageTitle() {
        return "Plugin Skeleton Wizard";
    }
}
