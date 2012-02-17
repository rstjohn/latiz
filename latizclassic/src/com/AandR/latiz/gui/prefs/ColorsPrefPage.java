package com.AandR.latiz.gui.prefs;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;


import com.AandR.gui.ColorChooser;
import com.AandR.gui.ui.JButtonX;
import com.AandR.latiz.core.PropertiesManager;
import com.AandR.latiz.gui.InputConnector;
import com.AandR.latiz.gui.OutputConnector;

/**
 * @author Dr. Richard St. John
 * @version $Revision: 1.4 $, $Date: 2007/10/10 00:32:03 $
 */
public class ColorsPrefPage extends AbstractPreferencePage implements ActionListener {

    private Color outgoingBackgroundColor;
    private Color incomingBackgroundColor;
    private Color outgoingForegroundColor;
    private Color incomingForegroundColor;
    private JLabel outgoing;
    private JLabel incoming;

    public ColorsPrefPage() {
        super("Connection Panel");
        setPropPanel(createColorsPanel());
    }

    private JPanel createColorsPanel() {
        PropertiesManager props = PropertiesManager.getInstanceOf();
        outgoingBackgroundColor = Color.decode(props.getProperty(PropertiesManager.COLOR_CONNECTION_OUTGOING_BACKGROUND));
        outgoingForegroundColor = Color.decode(props.getProperty(PropertiesManager.COLOR_CONNECTION_OUTGOING_FOREGROUND));
        incomingBackgroundColor = Color.decode(props.getProperty(PropertiesManager.COLOR_CONNECTION_INCOMING_BACKGROUND));
        incomingForegroundColor = Color.decode(props.getProperty(PropertiesManager.COLOR_CONNECTION_INCOMING_FOREGROUND));

        Dimension labelDim = new Dimension(200, 24);
        outgoing = new JLabel("Outgoing Connector");
        outgoing.setPreferredSize(labelDim);
        outgoing.setOpaque(true);
        outgoing.setBackground(outgoingBackgroundColor);
        outgoing.setForeground(outgoingForegroundColor);
        outgoing.setBorder(new EmptyBorder(5, 10, 5, 10));
        JButtonX outgoingBackgroundButton = createButton("Background", "OUT_BACK", "");
        JButtonX outgoingForegroundButton = createButton("Foreground", "OUT_FORE", "");
        JButtonX outgoingDefaults = createButton("Default", "OUT_DEFAULT", "");
        JPanel panelOutgoing = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelOutgoing.add(outgoing);
        panelOutgoing.add(outgoingBackgroundButton);
        panelOutgoing.add(outgoingForegroundButton);
        panelOutgoing.add(outgoingDefaults);

        incoming = new JLabel("Incoming Connector");
        incoming.setPreferredSize(labelDim);
        incoming.setOpaque(true);
        incoming.setBackground(incomingBackgroundColor);
        incoming.setForeground(incomingForegroundColor);
        incoming.setBorder(new EmptyBorder(5, 10, 5, 10));
        JButtonX incomingBackgroundButton = createButton("Background", "IN_BACK", "");
        JButtonX incomingForegroundButton = createButton("Foreground", "IN_FORE", "");
        JButtonX incomingDefaults = createButton("Default", "IN_DEFAULT", "");
        JPanel panelIncoming = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelIncoming.add(incoming);
        panelIncoming.add(incomingBackgroundButton);
        panelIncoming.add(incomingForegroundButton);
        panelIncoming.add(incomingDefaults);

        JPanel p = new JPanel(new GridLayout(3, 1, 5, 5));
        p.add(panelOutgoing);
        p.add(panelIncoming);

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(p, BorderLayout.NORTH);
        return panel;
    }

    private JButtonX createButton(String label, String command, String toolTip) {
        JButtonX button = new JButtonX(label);
        button.setActionCommand(command);
        button.addActionListener(this);
        return button;
    }

    public void fireAcceptAction() {
        PropertiesManager props = PropertiesManager.getInstanceOf();
        props.setProperty(PropertiesManager.COLOR_CONNECTION_INCOMING_BACKGROUND, "#" + Integer.toHexString(incomingBackgroundColor.getRGB()).substring(1));
        props.setProperty(PropertiesManager.COLOR_CONNECTION_INCOMING_FOREGROUND, "#" + Integer.toHexString(incomingForegroundColor.getRGB()).substring(1));
        props.setProperty(PropertiesManager.COLOR_CONNECTION_OUTGOING_BACKGROUND, "#" + Integer.toHexString(outgoingBackgroundColor.getRGB()).substring(1));
        props.setProperty(PropertiesManager.COLOR_CONNECTION_OUTGOING_FOREGROUND, "#" + Integer.toHexString(outgoingForegroundColor.getRGB()).substring(1));
        props.saveProperties();
    }

    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        if (command.equalsIgnoreCase("IN_BACK")) {
            ColorChooser colorChooser = new ColorChooser(incomingBackgroundColor);
            incomingBackgroundColor = colorChooser.getSelectedColor();
        } else if (command.equalsIgnoreCase("IN_FORE")) {
            ColorChooser colorChooser = new ColorChooser(incomingForegroundColor);
            incomingForegroundColor = colorChooser.getSelectedColor();
        } else if (command.equalsIgnoreCase("IN_DEFAULT")) {
            incomingBackgroundColor = InputConnector.COLOR;
            incomingForegroundColor = Color.WHITE;
        } else if (command.equalsIgnoreCase("OUT_BACK")) {
            ColorChooser colorChooser = new ColorChooser(outgoingBackgroundColor);
            outgoingBackgroundColor = colorChooser.getSelectedColor();
        } else if (command.equalsIgnoreCase("OUT_FORE")) {
            ColorChooser colorChooser = new ColorChooser(outgoingForegroundColor);
            outgoingForegroundColor = colorChooser.getSelectedColor();
        } else if (command.equalsIgnoreCase("OUT_DEFAULT")) {
            outgoingBackgroundColor = OutputConnector.COLOR;
            outgoingForegroundColor = Color.WHITE;

        }
        incoming.setBackground(incomingBackgroundColor);
        incoming.setForeground(incomingForegroundColor);
        outgoing.setBackground(outgoingBackgroundColor);
        outgoing.setForeground(outgoingForegroundColor);
    }
}
