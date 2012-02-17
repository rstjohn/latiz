package com.latizTech.jedit;

import java.awt.BorderLayout;
import java.util.Comparator;
import java.util.Iterator;
import java.util.TreeSet;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.table.DefaultTableModel;
import org.netbeans.swing.etable.ETable;

/**
 * @author Dr. Richard St. John
 * @version $Revision: 1.1 $, $Date: 2007/09/17 00:15:32 $
 */
@SuppressWarnings("unchecked")
public class KeyboardMapPanel extends JDialog {

    private TreeSet<KeyboardShortcut> keyboardList;

    public KeyboardMapPanel() {
        super((JFrame) null, "Keyboard Map", false);
        addKeyboardShortcutsToList();
        setContentPane(createContentPane());
        pack();
        setVisible(true);
    }

    private JPanel createContentPane() {
        DefaultTableModel tableModel = new DefaultTableModel(40, 2);
        tableModel.setColumnIdentifiers(new String[]{"Key Stroke", "Description"});
        ETable table = new ETable(tableModel);
        Iterator<KeyboardShortcut> shortcuts = keyboardList.iterator();
        KeyboardShortcut thisShortcut = null;
        int row = 0;
        while (shortcuts.hasNext()) {
            thisShortcut = shortcuts.next();
            tableModel.setValueAt(thisShortcut.key, row, 0);
            tableModel.setValueAt(thisShortcut.desc, row++, 1);
        }
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    private void addKeyboardShortcutsToList() {
        keyboardList = new TreeSet<KeyboardShortcut>(new Comparator() {

            public int compare(Object o1, Object o2) {
                String map1 = ((KeyboardShortcut) o1).toString().toLowerCase();
                String map2 = ((KeyboardShortcut) o2).toString().toLowerCase();
                return map1.compareTo(map2);
            }
        });
        keyboardList.add(new KeyboardShortcut("CTRL+DELETE     ", "Delete Word                  "));
        keyboardList.add(new KeyboardShortcut("CTRL+D          ", "Delete Line                  "));
        keyboardList.add(new KeyboardShortcut("CTRL+ALT+UP     ", "Copy Line up                 "));
        keyboardList.add(new KeyboardShortcut("CTRL+ALT+DOWN   ", "Copy Line Down               "));
        keyboardList.add(new KeyboardShortcut("ALT+UP          ", "Move Line Up                 "));
        keyboardList.add(new KeyboardShortcut("ALT+DOWN        ", "Move Line Down               "));
        keyboardList.add(new KeyboardShortcut("CTRL+UP         ", "Scroll Up                    "));
        keyboardList.add(new KeyboardShortcut("CTRL+DOWN       ", "Scroll Down                  "));
        keyboardList.add(new KeyboardShortcut("CTRL+SHIFT+ENTER", "Insert New Line Above        "));
        keyboardList.add(new KeyboardShortcut("SHIFT+ENTER     ", "Insert New Line Below        "));
        keyboardList.add(new KeyboardShortcut("CTRL+A          ", "Select All                   "));
        keyboardList.add(new KeyboardShortcut("CTRL+-          ", "Decrease Font Size           "));
        keyboardList.add(new KeyboardShortcut("CTRL++          ", "Increase Font Size           "));
        keyboardList.add(new KeyboardShortcut("CTRL_SHIFT+F    ", "Choose Font                  "));
        keyboardList.add(new KeyboardShortcut("CTRL+L          ", "Goto Line                    "));
        keyboardList.add(new KeyboardShortcut("CTRL+F          ", "Find / Replace               "));
        keyboardList.add(new KeyboardShortcut("CTRL+F9         ", "Reformat Lines               "));
        keyboardList.add(new KeyboardShortcut("INSERT          ", "Overwrite Mode               "));
        keyboardList.add(new KeyboardShortcut("CTRL+\\         ", "Toggle Rectangle             "));
        keyboardList.add(new KeyboardShortcut("HOME            ", "Home                         "));
        keyboardList.add(new KeyboardShortcut("END             ", "End                          "));
        keyboardList.add(new KeyboardShortcut("SHIFT+HOME      ", "Select To Beginning of Line  "));
        keyboardList.add(new KeyboardShortcut("Shift+END       ", "Select To End of Current Line"));
        keyboardList.add(new KeyboardShortcut("CTRL+SHIFT+HOME ", "Select To Beginning of File  "));
        keyboardList.add(new KeyboardShortcut("CTRL+Shift+END  ", "Select To End of File        "));
        keyboardList.add(new KeyboardShortcut("CTRL+RIGHT      ", "Next Word                    "));
        keyboardList.add(new KeyboardShortcut("CTRL+LEFT       ", "Previous Word                "));
        keyboardList.add(new KeyboardShortcut("CTRL+[          ", "[]                           "));
        keyboardList.add(new KeyboardShortcut("CTRL+(          ", "()                           "));
        keyboardList.add(new KeyboardShortcut("CTRL+SHIFT+[    ", "{}                           "));
        keyboardList.add(new KeyboardShortcut("ALT+F           ", "fraction                     "));
        keyboardList.add(new KeyboardShortcut("ALT+M           ", "Inline Math Mode             "));
        keyboardList.add(new KeyboardShortcut("ALT+I           ", "Integral                     "));
        keyboardList.add(new KeyboardShortcut("ALT+R           ", "Reference                    "));
        keyboardList.add(new KeyboardShortcut("ALT+E           ", "Equation                     "));
        keyboardList.add(new KeyboardShortcut("ALT+S           ", "Symbol                       "));
    }

    /**
     * 
     */
    private class KeyboardShortcut {

        private String key;
        private String desc;

        public KeyboardShortcut(String key, String desc) {
            this.key = key;
            this.desc = desc;
        }

        @Override
        public String toString() {
            return desc;
        }
    }
}
