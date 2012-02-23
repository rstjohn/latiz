package com.AandR.latiz.swing;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.text.ParseException;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;


import com.AandR.gui.dropSupport.JTextFieldWithDropSupport;
import com.AandR.latiz.core.GlobalParameterMap;
import com.AandR.latiz.core.GlobalVariable;

/**
 * @author Dr. Richard St. John
 * @version $Revision$, $Date$
 */
public class LStringField extends JTextFieldWithDropSupport {

    /**
     *
     */
    public LStringField() {
        this("", 10);
        initialize();
    }

    /**
     * @param text
     */
    public LStringField(String text) {
        super(text);
        initialize();
    }

    /**
     * @param columns
     */
    public LStringField(int columns) {
        super(columns);
        initialize();
    }

    /**
     * @param text
     * @param columns
     */
    public LStringField(String text, int columns) {
        super(text, columns);
        initialize();
    }

    private void initialize() {
        addMouseListener(new LStringFieldListener());
    }

    /**
     *
     * @throws ParseException
     * @returns the text in the text field or the text found in the Global Map, giving presendence to the global map.
     * @throws ParseException
     */
    public String parse() throws ParseException {
        String text = getText().trim();
        if (text.length() == 0) {
            throw new ParseException("Parse Exception in LNumberField", 0);
        }

        GlobalVariable g = GlobalParameterMap.getInstanceOf().get(text);
        if (g != null) {
            return parseGlobalVariable(g);
        }

        return text;
    }

    private String parseGlobalVariable(GlobalVariable g) {
        String returnValue = g.getValue();
        if (g.isLoop()) {
            String[] loopValues = g.parseLoop();
            returnValue = loopValues[0];
        }
        return returnValue;
    }

    /**
     *
     * @author Dr. Richard St. John
     * @version $Revision$, $Date$
     */
    private class LStringFieldListener implements ActionListener, MouseListener {

        private JPopupMenu popupMenu;

        public LStringFieldListener() {
        }

        private void createPopupMenu(String label) {
            popupMenu = new JPopupMenu();
            popupMenu.add(createMenuItem("<html>Add <B><I>" + label + "</I></B> to Global Parameters</html>", "ADD"));

            JMenu retrieveMenu = new JMenu("Get From Global Parameters");
            for (String key : GlobalParameterMap.getInstanceOf().keySet()) {
                retrieveMenu.add(createMenuItem(key + "=" + GlobalParameterMap.getInstanceOf().get(key), "RETRIEVE"));
            }
            popupMenu.add(retrieveMenu);
        }

        private JMenuItem createMenuItem(String label, String actionCommand) {
            JMenuItem item = new JMenuItem(label);
            item.addActionListener(this);
            item.setActionCommand(actionCommand);
            return item;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            String command = e.getActionCommand();
            if (command.equalsIgnoreCase("ADD")) {
                actionAdd();
            } else if (command.equalsIgnoreCase("RETRIEVE")) {
                setText(((JMenuItem) e.getSource()).getText().split("=")[0]);
            }
        }

        private void actionAdd() {
            GlobalParameterMap.getInstanceOf().put(getText(), "");
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
            if (e.isPopupTrigger()) {
                createPopupMenu(getText());
                popupMenu.show(LStringField.this, e.getX(), e.getY());
            }
        }
    }
}
