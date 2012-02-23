package com.AandR.latiz.swing;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.text.ParseException;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;

import org.nfunk.jep.JEP;


import com.AandR.gui.ui.JButtonX;
import com.AandR.latiz.core.GlobalParameterMap;
import com.AandR.latiz.core.GlobalVariable;

/**
 * @author Dr. Richard St. John
 * @version $Revision$, $Date$
 */
public class LNumberField extends JTextField {

    public static final int INTEGER = 0;
    public static final int FLOAT = 1;
    public static final int DOUBLE = 2;
    public static final int LONG = 3;

    /**
     *
     */
    public LNumberField() {
        initialize();
    }

    /**
     * @param text
     */
    public LNumberField(String text) {
        super(text);
        initialize();
    }

    /**
     * @param columns
     */
    public LNumberField(int columns) {
        super(columns);
        initialize();
    }

    /**
     * @param text
     * @param columns
     */
    public LNumberField(String text, int columns) {
        super(text, columns);
        initialize();
    }

    private void initialize() {
        addMouseListener(new LTextFieldListener());
    }

    /**
     *
     * @param type Use constants INTEGER, FLOAT, LONG, of DOUBLE
     * @return
     * @throws ParseException
     *
     */
    public Number parse() throws ParseException {
        String text = getText().trim();
        if (text.length() == 0) {
            throw new ParseException("Parse Exception in LNumberField", 0);
        }

        if (text.startsWith("=")) {
            return parseEquation(text);
        }

        GlobalVariable g = GlobalParameterMap.getInstanceOf().get(text);
        if (g != null) {
            return parseGlobalVariable(g);
        }

        Number number = null;
        try {
            number = new Double(text);
        } catch (NumberFormatException nfe) {
            throw new ParseException("Parse Exception in LNumberField", 0);
        }
        return number;
    }

    /**
     *
     * @param type Use constants INTEGER, FLOAT, LONG, of DOUBLE
     * @return
     * @throws ParseException
     */
    @Deprecated
    public Number parse(int type) throws ParseException {
        return parse();
    }

    /**
     *
     * @param s
     * @return
     */
    private Number parseEquation(String s) {
        JEP myParser = new JEP();
        myParser.initFunTab(); // clear the contents of the function table
        myParser.initSymTab(); // clear the contents of the symbol table
        myParser.addStandardFunctions();
        myParser.addStandardConstants();
//        myParser.setAllowAssignment(true);
        myParser.setImplicitMul(true);
        myParser.setTraverse(false);

        GlobalParameterMap globals = GlobalParameterMap.getInstanceOf();
        Number value;
        String valueString;
        for (String key : globals.keySet()) {
            if (s.contains(key)) {
                valueString = globals.get(key).getValue();
                if (valueString.trim().startsWith("=")) {
                    value = parseEquation(valueString);
                } else {
                    value = new Double(globals.get(key).getValue());
                }
                myParser.addVariable(key, value.doubleValue());
            }
        }
        myParser.parseExpression(s.substring(1));
        Double y = new Double(myParser.getValueAsObject().toString());
        return y;
    }

    private Number parseGlobalVariable(GlobalVariable g) {
        if (g.isLoop()) {
            return new Double(g.parseLoop()[0]);
        }

        String value = g.getValue();
        if (value.startsWith("=")) {
            return parseEquation(value);
        }

        return new Double(value);
    }

    /**
     *
     * @author Dr. Richard St. John
     * @version $Revision$, $Date$
     */
    private class LTextFieldListener implements ActionListener, MouseListener {

        private JPopupMenu popupMenu;
        private JTextField fieldValue;
        private JTextField fieldName;
        private JDialog addDialog;
        private boolean addToGlobals;

        public LTextFieldListener() {
        }

        private void createPopupMenu(String label) {
            popupMenu = new JPopupMenu();
            popupMenu.add(createMenuItem("<html>Add <B><I>" + label + "</I></B> to Global Parameters</html>", "ADD"));

            JMenu retrieveMenu = new JMenu("Get From Global Parameters");
            for (String key : GlobalParameterMap.getInstanceOf().keySet()) {
                retrieveMenu.add(createMenuItem(key + "=" + GlobalParameterMap.getInstanceOf().get(key).getValue(), "RETRIEVE"));
            }
            popupMenu.add(retrieveMenu);
        }

        private JMenuItem createMenuItem(String label, String actionCommand) {
            JMenuItem item = new JMenuItem(label);
            item.addActionListener(this);
            item.setActionCommand(actionCommand);
            return item;
        }

        public void actionPerformed(ActionEvent e) {
            String command = e.getActionCommand();
            if (command.equalsIgnoreCase("ADD")) {
                actionAdd();
            } else if (command.equalsIgnoreCase("RETRIEVE")) {
                setText(((JMenuItem) e.getSource()).getText().split("=")[0]);
            }
        }

        private void actionAdd() {
            addDialog = new JDialog((JDialog) null, true);
            addDialog.setContentPane(createAddToContentPane());
            addDialog.pack();
            addDialog.setLocationRelativeTo(null);
            addDialog.setVisible(true);
            if (addToGlobals) {
                GlobalParameterMap.getInstanceOf().put(fieldName.getText(), fieldValue.getText());
            }
        }

        private Container createAddToContentPane() {
            fieldName = new JTextField(getText(), 15);
            fieldValue = new JTextField("", 15);

            JPanel namePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            JLabel label = new JLabel("Variable Name:");
            namePanel.add(label);
            namePanel.add(fieldName);

            JPanel valuePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            JLabel valueLabel = new JLabel("Value:");
            valueLabel.setPreferredSize(label.getPreferredSize());
            valuePanel.add(valueLabel);
            valuePanel.add(fieldValue);

            JPanel northPanel = new JPanel(new GridLayout(2, 1, 0, 0));
            northPanel.add(namePanel);
            northPanel.add(valuePanel);

            JButtonX okButton = new JButtonX("OK");
            okButton.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    addToGlobals = true;
                    addDialog.dispose();
                }
            });

            JButtonX cancelButton = new JButtonX("Cancel");
            cancelButton.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    addToGlobals = false;
                    addDialog.dispose();
                }
            });

            JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 5, 5));
            buttonPanel.add(okButton);
            buttonPanel.add(cancelButton);

            JPanel southPanel = new JPanel();
            southPanel.add(buttonPanel);

            JPanel panel = new JPanel(new BorderLayout());
            panel.add(northPanel, BorderLayout.CENTER);
            panel.add(southPanel, BorderLayout.SOUTH);
            return panel;
        }

        public void mouseClicked(MouseEvent e) {
        }

        public void mouseEntered(MouseEvent e) {
        }

        public void mouseExited(MouseEvent e) {
        }

        public void mousePressed(MouseEvent e) {
        }

        public void mouseReleased(MouseEvent e) {
            if (e.getButton() == 3) {
                createPopupMenu(getText());
                popupMenu.show(LNumberField.this, e.getX(), e.getY());
            }
        }
    }
}
