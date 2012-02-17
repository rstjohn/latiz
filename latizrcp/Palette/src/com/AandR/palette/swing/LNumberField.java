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
package com.AandR.palette.swing;

import com.AandR.jepLibrary.LatizJep;
import com.AandR.palette.globals.GlobalVariable;
import com.AandR.palette.globals.GlobalsTopComponent;
import com.AandR.palette.globals.GlobalsUtilities;
import com.AandR.palette.plugin.AbstractPlugin;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.ParseException;
import java.util.ArrayList;

import java.util.LinkedHashMap;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import net.miginfocom.swing.MigLayout;
import org.openide.util.Exceptions;

/**
 *
 * @author   <a href="mailto://rstjohn@mza.com">Dr. Richard St. John</a>
 * @company  <a href="http://www.mza.com">MZA Associates Corporation</a>
 *
 */
public class LNumberField extends JTextField {

    private ArrayList<LNumberFieldListener> listeners;
    private LinkedHashMap<String, GlobalVariable> globalsMap;

    public LNumberField() {
    }

    public void setPlugin(AbstractPlugin plugin) {
        initialize(plugin.getPaletteModelImpl().getGlobalsMap());
    }

    /**
     *
     */
    public LNumberField(AbstractPlugin plugin) {
        initialize(plugin.getPaletteModelImpl().getGlobalsMap());
    }

    /**
     * @param text
     */
    public LNumberField(AbstractPlugin plugin, String text) {
        super(text);
        initialize(plugin.getPaletteModelImpl().getGlobalsMap());
    }

    /**
     * @param text
     * @param columns
     */
    public LNumberField(AbstractPlugin plugin, String text, int columns) {
        super(text, columns);
        initialize(plugin.getPaletteModelImpl().getGlobalsMap());
    }

    public LNumberField(LinkedHashMap<String, GlobalVariable> globalsMap) {
        initialize(globalsMap);
    }

    /**
     * @param text
     */
    public LNumberField(LinkedHashMap<String, GlobalVariable> globalsMap, String text) {
        super(text);
        initialize(globalsMap);
    }

    /**
     * @param text
     * @param columns
     */
    public LNumberField(LinkedHashMap<String, GlobalVariable> globalsMap, String text, int columns) {
        super(text, columns);
        initialize(globalsMap);
    }

    private void initialize(LinkedHashMap<String, GlobalVariable> globalsMap) {
        this.globalsMap=globalsMap;
        listeners = new ArrayList<LNumberFieldListener>();
        addMouseListener(new LTextFieldListener());
    }

    public void addNumberFieldListener(LNumberFieldListener l) {
        listeners.add(l);
    }

    /**
     * Tries to parse a {@link Number} from an input field.  If the string starts with an <code>=</code> an equation
     * is parsed using {@link #parse(String)}.  Otherwise, the global parameters map is consulted.  If the string
     * exists in the {@link GlobalParameterMap}, its value is returned. Finally, if neither are found, the text is parsed as a {@link Double}
     * and returned.
     * @return The number if it was successfully parsed, null otherwise.
     * @throws ParseException
     *
     */
    public Double parse() throws ParseException {
        return parse(getText().trim());
    }

    /**
     * Tries to parse a {@link Number} from an input field.  If the string starts with an <code>=</code> an equation
     * is parsed using {@link #parse(String)}.  Otherwise, the global parameters map is consulted.  If the string
     * exists in the {@link GlobalParameterMap}, its value is returned. Finally, if neither are found, the text is parsed as a {@link Double}
     * and returned.
     * @return The number if it was successfully parsed, null otherwise.
     * @throws ParseException
     *
     */
    public Double parse(String text) throws ParseException {
        if (text.length() == 0) {
            throw new ParseException("Parse Exception in LNumberField", 0);
        }
        Double returnObject = null;
        try {
            returnObject = (Double) parseEntry(text);
        } catch (ClassCastException e) {
            Exceptions.attachMessage(e, "Vector entries in LNumberField are not yet supported");
        }
        return returnObject;
    }

    private Object parseEntry(String value) throws ParseException {
        LatizJep jep = new LatizJep();
        jep.parseExpression(value);
        if (jep.hasError() || jep.getValueAsObject() == null) {
            String thisError;
            String[] errorList = jep.getErrorInfo().split("\n");

            for (int i = 0; i < errorList.length; i++) {
                thisError = errorList[i];
                if (!thisError.startsWith("Unrecognized symbol")) {
                    throw new ParseException(thisError, 0);
                }
                String[] var = thisError.split("\"");
                Object o = GlobalsUtilities.parse(globalsMap, var[1]);
                jep.addVariable(var[1], o);
            }
            jep.parseExpression(value);
        }
        Object returnObject = jep.getValueAsObject();
        return returnObject;
    }

    private void notifyNumberFieldChanged() {
        for (LNumberFieldListener l : listeners) {
            l.numberFieldChanged();
        }
    }

    /**
     *
     * @author Dr. Richard St. John
     * @version $Revision$, $Date$
     */
    private class LTextFieldListener extends MouseAdapter implements ActionListener {

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
            for(String key : globalsMap.keySet()) {
              retrieveMenu.add(createMenuItem(key+"="+globalsMap.get(key).getValue(), "RETRIEVE"));
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
            if (command.equals("ADD")) {
                actionAdd();
            } else if (command.equals("RETRIEVE")) {
                setText(((JMenuItem) e.getSource()).getText().split("=")[0]);
                notifyNumberFieldChanged();
            }
        }

        private void actionAdd() {
            addDialog = new JDialog((JDialog) null, true);
            addDialog.setContentPane(createAddToContentPane());
            addDialog.pack();
            addDialog.setLocationRelativeTo(null);
            addDialog.setVisible(true);
            if (addToGlobals) {
                String key = fieldName.getText();
                String val = fieldValue.getText();

                //Here is where we add from text field to globals map
                globalsMap.put(key, new GlobalVariable(key, val, false));
                GlobalsTopComponent gtc = GlobalsTopComponent.findInstance();
                if(gtc!=null) {
                    gtc.getGlobalsPanel().refreshTable();
                }
            }
        }

        private Container createAddToContentPane() {
            JButton okButton = new JButton("OK");
            okButton.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    addToGlobals = true;
                    addDialog.dispose();
                }
            });

            JButton cancelButton = new JButton("Cancel");
            cancelButton.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    addToGlobals = false;
                    addDialog.dispose();
                }
            });

            JPanel p = new JPanel(new MigLayout());
            p.add(new JLabel("Variable Name:"));
            p.add(fieldName = new JTextField(getText(), 15), "growx, wrap");
            p.add(new JLabel("Value:"));
            p.add(fieldValue = new JTextField("", 15), "growx, wrap");
            p.add(okButton, "spanx, split 2, tag ok");
            p.add(cancelButton, "tag cancel");
            return p;
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            if (e.getButton() == 3) {
                createPopupMenu(getText());
                popupMenu.show(LNumberField.this, e.getX(), e.getY());
            }
        }
    }
}
