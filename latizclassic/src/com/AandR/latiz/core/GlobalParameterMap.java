package com.AandR.latiz.core;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Comparator;
import java.util.TreeMap;

import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import com.AandR.latiz.resources.Resources;

/**
 * @author Dr. Richard St. John
 * @version $Revision$, $Date$
 */
public class GlobalParameterMap extends TreeMap<String, GlobalVariable> {

    private static GlobalParameterMap instanceOf = new GlobalParameterMap();
    private Color c1 = new Color(240, 240, 250);
    private Color c2 = new Color(250, 250, 250);
    private Color c3 = new Color(245, 245, 245);
    private DefaultTableModel tableModel;
    private JPopupMenu tablePopup;
    private JTable table;
    private String currentKey;
    private TableModelListener tableModelListener;
    private PopupListener popupListener;

    private GlobalParameterMap() {
        super(new Comparator<String>() {

            public int compare(String o1, String o2) {
                return o1.compareToIgnoreCase(o2);
            }
        });
        initialize();
    }

    public static GlobalParameterMap getInstanceOf() {
        return instanceOf;
    }

    private void initialize() {
//    table = new JTable(tableModel = new DefaultTableModel(new String[] {"Name", "Value", "Trash"}, 70)) {
        table = new JTable(tableModel = new DefaultTableModel(new String[]{"Name", "Value"}, 70)) {

            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int rowIndex, int vColIndex) {
                Component c = super.prepareRenderer(renderer, rowIndex, vColIndex);

                if (rowIndex % 2 == 1 && !isCellSelected(rowIndex, vColIndex)) {
                    c.setBackground(c1);
                } else {
                    c.setBackground(c2);
                }

                if (isCellSelected(rowIndex, vColIndex)) {
                    c.setBackground(getSelectionBackground());
                    c.setForeground(Color.BLUE.darker());
                } else {
                    c.setForeground(UIManager.getColor("Table.forground"));
                }
                return c;
            }
        };

        table.addMouseListener(new TableListener());
        tableModelListener = new TableModelListener() {

            public void tableChanged(TableModelEvent e) {
                if (e.getType() == TableModelEvent.UPDATE) {
                    if (currentKey != null) {
                        remove(currentKey);
                    }
                    int row = table.getEditingRow();
                    if (row < 0) {
                        return;
                    }

                    Object cellObject = table.getValueAt(row, 0);
                    if (cellObject == null) {
                        return;
                    }

                    String label = cellObject.toString();

                    Object valueObject = table.getValueAt(row, 1);
                    String value = valueObject == null ? "" : valueObject.toString();
                    put(label, new GlobalVariable(label, value));
                    refreshTable();
                }
            }
        };
        tableModel.addTableModelListener(tableModelListener);

        //new ButtonColumn(table, 2);
        //table.getColumnModel().getColumn(2).setPreferredWidth(20);
        table.setBackground(c3);

        popupListener = new PopupListener();
        tablePopup = new JPopupMenu();
        tablePopup.add(createMenuItem("Delete Global", Resources.createIcon("cancel16.png"), "DELETE"));
    }

    public GlobalVariable put(String key, String value) {
        GlobalVariable g = super.put(key, new GlobalVariable(key, value));
        refreshTable();
        return g;
    }

    public void clearGlobals() {
        clear();
        refreshTable();
    }

    private void refreshTable() {
        tableModel.removeTableModelListener(tableModelListener);
        for (int i = 0; i < table.getRowCount(); i++) {
            tableModel.setValueAt("", i, 0);
            tableModel.setValueAt("", i, 1);
        }

        int rowCount = 0;
        for (String key : keySet()) {
            tableModel.setValueAt(key, rowCount, 0);
            tableModel.setValueAt(get(key).getValue(), rowCount, 1);
            rowCount++;
        }
        tableModel.addTableModelListener(tableModelListener);
    }

    public JTable getTable() {
        return table;
    }

    public void saveToLatFile(int fid) {
        System.out.println("Save to latFile");
    }

    private void removeGlobalFromTable() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            return;
        }

        Object o = table.getValueAt(selectedRow, 0);
        if (o == null) {
            return;
        }

        String label = o.toString();
        //tableModel.fireEditingStopped();
        remove(label);
        refreshTable();
    }

    private JMenuItem createMenuItem(String label, ImageIcon icon, String actionCommand) {
        JMenuItem menuItem = new JMenuItem(label, icon);
        menuItem.setActionCommand(actionCommand);
        menuItem.addActionListener(popupListener);
        return menuItem;
    }

    /**
     *
     * @author Dr. Richard St. John
     * @version $Revision$, $Date$
     */
    private class PopupListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            String command = e.getActionCommand();
            if (command.equalsIgnoreCase("DELETE")) {
                removeGlobalFromTable();
            }
        }
    }

    /**
     *
     * @author Dr. Richard St. John
     * @version $Revision$, $Date$
     */
    private class TableListener extends MouseAdapter {

        @Override
        public void mouseClicked(MouseEvent e) {
            int row = table.getSelectedRow();
            if (row < 0) {
                return;
            }

            int rowAtPoint = table.rowAtPoint(e.getPoint());
            if (e.getButton() == 3 && row == rowAtPoint) {
                tablePopup.show(table, e.getX(), e.getY());
            }

            Object selectedCell = table.getValueAt(row, 0);
            if (selectedCell == null) {
                return;
            }

            currentKey = selectedCell.toString();
        }
    }
    /**
     *
     * @author Dr. Richard St. John
     * @version $Revision: 1.1 $, $Date: 2007/05/25 00:17:03 $
    private class ButtonColumn extends AbstractCellEditor implements TableCellRenderer, TableCellEditor, ActionListener {

    private JButton renderButton, editButton;

    private String text;

    private JTable table;

    public ButtonColumn(JTable table, int column) {
    super();
    this.table = table;
    renderButton = new JButton();
    renderButton.setFocusPainted(false);
    renderButton.setBorderPainted(false);
    renderButton.setContentAreaFilled(false);
    renderButton.setToolTipText("Remove Global Variable");
    renderButton.setIcon(Resources.createIcon("delete16.png"));

    editButton = new JButton();
    editButton.setFocusPainted(false);
    editButton.setBorderPainted(false);
    editButton.setContentAreaFilled(false);
    editButton.addActionListener(this);
    editButton.setToolTipText("Remove Global Variable");
    editButton.setIcon(Resources.createIcon("delete16.png"));

    TableColumnModel columnModel = table.getColumnModel();
    columnModel.getColumn(column).setCellRenderer(this);
    columnModel.getColumn(column).setCellEditor(this);
    }

    
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
    renderButton.setName((value == null) ? "" : value.toString());
    renderButton.setBackground(Color.blue);
    return renderButton;
    }

    
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
    text = (value == null) ? "" : value.toString();
    editButton.setName(text);
    editButton.setBackground(Color.blue);
    return editButton;
    }

    
    public Object getCellEditorValue() {
    return text;
    }

    
    public void actionPerformed(ActionEvent e) {
    int selectedRow = table.getSelectedRow();
    String label = table.getValueAt(selectedRow, 0).toString();
    fireEditingStopped();
    remove(label);
    refreshTable();
    }
    }
     */
}
