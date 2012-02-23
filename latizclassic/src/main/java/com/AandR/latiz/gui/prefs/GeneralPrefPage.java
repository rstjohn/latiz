package com.AandR.latiz.gui.prefs;

import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;


import com.AandR.gui.ui.JButtonX;
import com.AandR.latiz.core.PropertiesManager;
import com.AandR.latiz.resources.Resources;

/**
 * @author Dr. Richard St. John
 * @version $Revision$, $Date$
 */
public class GeneralPrefPage extends AbstractPreferencePage implements ActionListener {

    private JTextField fieldPath;
    private PropertiesManager props;

    public GeneralPrefPage() {
        super("General");
        props = PropertiesManager.getInstanceOf();
        setPropPanel(createContentPanel());
    }

    private JPanel createContentPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(createLatFileSettingsPanel());
        return panel;
    }

    private Component createLatFileSettingsPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.add(new JLabel("Default Lat-File Directory:"));
        panel.add(fieldPath = new JTextField(props.getProperty(PropertiesManager.GENERAL_LAT_PATH), 25));

        JButtonX browseButton = new JButtonX(Resources.createIcon("find16.png"));
        browseButton.addActionListener(this);
        panel.add(browseButton);
        panel.setBorder(new TitledBorder("Lat File Properties"));
        return panel;
    }

    public void fireAcceptAction() {
        String s = fieldPath.getText();
        if (s.trim().equalsIgnoreCase("")) {
            return;
        }
        props.setProperty(PropertiesManager.GENERAL_LAT_PATH, s);
        props.saveProperties();
    }

    public void actionPerformed(ActionEvent e) {
        JFileChooser chooser = new JFileChooser(System.getProperty("user.home"));
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int selection = chooser.showOpenDialog(fieldPath);
        if (selection == JFileChooser.CANCEL_OPTION) {
            return;
        }
        fieldPath.setText(chooser.getSelectedFile().getPath());
    }
}
