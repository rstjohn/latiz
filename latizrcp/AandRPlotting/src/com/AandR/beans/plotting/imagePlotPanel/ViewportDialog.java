/**
 *  Copyright 2010 Latiz Technologies, LLC
 *
 *  This file is part of Latiz.
 *
 *  Latiz is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Latiz is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Latiz.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.AandR.beans.plotting.imagePlotPanel;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JDialog;

/**
 * @author Dr. Richard St. John
 * @version $Revision: 1.3 $, $Date: 2007/09/15 15:57:14 $
 */
public class ViewportDialog extends JDialog implements ActionListener {

    private JTextField fieldViewportX;
    private JTextField fieldViewportY;
    private int defaultWidth;
    private int defaultHeight;
    private int viewportWidth;
    private int viewportHeight;

    /**
     * @param owner
     * @param title
     * @param modal
     */
    public ViewportDialog() {
        super(new JFrame(), "Viewport Dialog", true);
        setContentPane(createContentPane());
        pack();
        centerOnScreen();
        setVisible(false);
    }

    private JPanel createContentPane() {
        fieldViewportX = new JTextField(15);
        fieldViewportX.setName("Set Viewport X: ");
        JLabel labelViewportX = new JLabel(fieldViewportX.getName());

        JPanel panelViewportX = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelViewportX.add(labelViewportX);
        panelViewportX.add(fieldViewportX);

        fieldViewportY = new JTextField(15);
        fieldViewportY.setName("Set Viewport Y: ");
        JLabel labelViewportY = new JLabel(fieldViewportY.getName());

        JPanel panelViewportY = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelViewportY.add(labelViewportY);
        panelViewportY.add(fieldViewportY);

        Dimension labelDim = labelViewportX.getPreferredSize();
        labelViewportY.setPreferredSize(labelDim);

        JPanel panelRange = new JPanel();
        panelRange.setLayout(new BoxLayout(panelRange, BoxLayout.Y_AXIS));
        panelRange.add(panelViewportX);
        panelRange.add(panelViewportY);

        JPanel panelButton = new JPanel();
        panelButton.add(createButton("Accept", this, "ACCEPT"));
        panelButton.add(createButton("Cancel", this, "CANCEL"));

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(panelRange, BorderLayout.NORTH);
        panel.add(panelButton, BorderLayout.SOUTH);

        return panel;
    }

    public void showDialog(int width, int height) {
        defaultWidth = width;
        defaultHeight = height;
        fieldViewportX.setText(String.valueOf(defaultWidth));
        fieldViewportY.setText(String.valueOf(defaultHeight));
        setVisible(true);
    }

    private JButton createButton(String title, ActionListener al, String actionCommand) {
        JButton button = new JButton(title);
        button.addActionListener(al);
        button.setActionCommand(actionCommand);
        return button;
    }

    private void centerOnScreen() {
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension screenSize = toolkit.getScreenSize();
        int x = (screenSize.width - getWidth()) / 2;
        int y = (screenSize.height - getHeight()) / 2;
        setLocation(x, y);
    }

    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        if (command.equalsIgnoreCase("ACCEPT")) {
            viewportWidth = Integer.parseInt(fieldViewportX.getText());
            viewportHeight = Integer.parseInt(fieldViewportY.getText());
            defaultWidth = viewportWidth;
            defaultHeight = viewportHeight;
        } else if (command.equalsIgnoreCase("CANCEL")) {
            viewportWidth = defaultWidth;
            viewportHeight = defaultHeight;
        }
        setVisible(false);
    }

    /**
     *
     * @return
     */
    public JTextField getFieldViewportX() {
        return fieldViewportX;
    }

    /**
     *
     * @return
     */
    public JTextField getFieldViewportY() {
        return fieldViewportY;
    }

    public int getViewportHeight() {
        return viewportHeight;
    }

    public int getViewportWidth() {
        return viewportWidth;
    }
}
