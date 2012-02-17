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
package com.latizTech.jedit;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import javax.swing.text.BadLocationException;

/**
 * @author Dr. Richard St. John
 * @version $Revision: 1.5 $, $Date: 2007/09/15 15:57:13 $
 */
public class FindReplaceDialog extends JDialog implements ActionListener {

    private int start = 0;
    private int end = 0;
    private JEditTextArea textArea;
    private JTextField fieldFind, fieldReplace;
    private JButton buttonReplaceFind;
    private JButton buttonReplace;
    private JLabel infoLabel;
    private boolean found;
    private JRadioButton radioFromTop;
    private JRadioButton radioFromCursor;

    public FindReplaceDialog() {
        super(new Frame(), "Find/Replace Dialog");
        setModal(false);
        setAlwaysOnTop(true);
        setContentPane(createContentPanel());
        pack();
        Dimension screenDim = Toolkit.getDefaultToolkit().getScreenSize();
        int locX = (screenDim.width - this.getWidth()) / 2;
        int locY = (int) (.6 * (screenDim.height - this.getHeight()) / 2);
        setLocation(locX, locY);
    }

    public void setVisible(boolean isVisible, JEditTextArea textArea) {
        setVisible(isVisible);
        this.textArea = textArea;
        fieldFind.requestFocus();
        String selectedText = textArea.getSelectedText();
        if (selectedText != null && selectedText.length() > 0) {
            fieldFind.setText(selectedText);
        }
        infoLabel.setText("");
        found = false;
    }

    private Container createContentPanel() {
        JPanel panelFind = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelFind.add(createLabel("Find:", true));
        panelFind.add(fieldFind = createTextField(15));
        fieldFind.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                actionFind();
            }
        });

        JPanel panelReplace = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelReplace.add(createLabel("Replace With:", true));
        panelReplace.add(fieldReplace = createTextField(15));

        JPanel panelInfo = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelInfo.add(infoLabel = createLabel("", false));
        infoLabel.setForeground(Color.RED);

        JPanel panelFields = new JPanel(new GridLayout(3, 1));
        panelFields.add(panelFind);
        panelFields.add(panelReplace);
        panelFields.add(panelInfo);

        JPanel northPanel = new JPanel(new BorderLayout());
        northPanel.add(panelFields, BorderLayout.NORTH);
        northPanel.add(createOptionsPanel(), BorderLayout.SOUTH);

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(northPanel, BorderLayout.NORTH);
        panel.add(createButtonPanel(), BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createOptionsPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 2));
        panel.add(createDirectionPanel());
        panel.add(createScopePanel());
        return panel;
    }

    private JPanel createScopePanel() {
        JPanel panel = new JPanel(new GridLayout(2, 0));
        radioFromTop = createRadioButton("All", true);
        radioFromCursor = createRadioButton("Selected Lines", false);
        radioFromCursor.setEnabled(false);
        ButtonGroup directionGroup = new ButtonGroup();
        directionGroup.add(radioFromTop);
        directionGroup.add(radioFromCursor);
        TitledBorder border = new TitledBorder("Scope");
        panel.setBorder(border);
        panel.add(radioFromTop);
        panel.add(radioFromCursor);
        return panel;
    }

    private JPanel createDirectionPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 0));
        JRadioButton forward = createRadioButton("Forward", true);
        JRadioButton backward = createRadioButton("Backward", false);
        backward.setEnabled(false);
        ButtonGroup directionGroup = new ButtonGroup();
        directionGroup.add(forward);
        directionGroup.add(backward);
        TitledBorder border = new TitledBorder("Direction");
        panel.setBorder(border);
        panel.add(forward);
        panel.add(backward);
        return panel;
    }

    private JRadioButton createRadioButton(String label, boolean isSelected) {
        JRadioButton button = new JRadioButton(label, isSelected);
        return button;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 2, 5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
        panel.add(createButton("Find", "FIND", true));
        panel.add(buttonReplaceFind = createButton("Replace/Find", "REPLACE_FIND", false));
        panel.add(buttonReplace = createButton("Replace", "REPLACE", false));
        panel.add(createButton("Close", "CLOSE", true));
        return panel;
    }

    private JButton createButton(String label, String actionCommand, boolean isEnabled) {
        JButton button = new JButton(label);
        button.setEnabled(isEnabled);
        button.addActionListener(this);
        button.setActionCommand(actionCommand);
        return button;
    }

    private JTextField createTextField(int i) {
        JTextField field = new JTextField(i);
        return field;
    }

    private JLabel createLabel(String string, boolean isConstrained) {
        JLabel label = new JLabel(string);
        if (isConstrained) {
            Dimension dim = new Dimension(80, 20);
            label.setPreferredSize(dim);
        }
        return label;
    }

    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        if (command.equalsIgnoreCase("FIND")) {
            actionFind();
        } else if (command.equalsIgnoreCase("REPLACE")) {
            actionReplace();
        } else if (command.equalsIgnoreCase("REPLACE_FIND")) {
            actionReplaceFind();
        } else if (command.equalsIgnoreCase("CLOSE")) {
            actionClose();
        }
    }

    private void actionClose() {
        setButtonsEnabled(false);
        setVisible(false);
    }

    private void actionFind() {
        String findExpression = fieldFind.getText();
        if ((findExpression == null) || (findExpression.trim().length() <= 0)) {
            return;
        }
        infoLabel.setText("");
        int localStart = textArea.getText().indexOf(findExpression, textArea.getCaretPosition());
        if (localStart < 0) {
            Toolkit.getDefaultToolkit().beep();
            if (found) {
                infoLabel.setText("End Of File Reached.");
                found = false;
            } else {
                infoLabel.setText("String Not Found.");
            }
            setButtonsEnabled(false);
            textArea.setCaretPosition(0);
            return;
        }
        found = true;
        start = localStart;
        end = start + findExpression.length();
        textArea.select(start, end);
        setButtonsEnabled(true);
    }

    private void actionReplace() {
        String replacement;
        if (fieldReplace.getText() == null) {
            replacement = "";
        } else {
            replacement = fieldReplace.getText();
        }
        try {
            textArea.getDocument().replace(start, (end - start), replacement, textArea.getDocument().getDefaultRootElement().getAttributes());
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
        textArea.select(start, start + replacement.length());
    }

    private void actionReplaceFind() {
        actionReplace();
        actionFind();
    }

    private void setButtonsEnabled(boolean isEnabled) {
        buttonReplace.setEnabled(isEnabled);
        buttonReplaceFind.setEnabled(isEnabled);
    }
}
















