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

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;


/**
 * @author Dr. Richard St. John
 * @version $Revision: 1.1 $, $Date: 2007/05/25 00:12:33 $
 */
public class DataTablePanel extends JPanel {
  
  private JTable table;
  
  private DefaultTableModel tableModel;
  
  
  public DataTablePanel() {
    super(new BorderLayout());
    tableModel = new DefaultTableModel();
    table = new JTable(tableModel);
    table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
    table.setColumnSelectionAllowed(true);
    table.setCellSelectionEnabled(true);
    JScrollPane scroll = new JScrollPane(table);
    add(scroll, BorderLayout.CENTER);
  }
  
  
  public void resetData(String xHeader, String[] yHeaders, double[][] x, double[][] y) {
    String[] tableLabels = new String[yHeaders.length + 1];
    tableLabels[0] = xHeader;
    System.arraycopy(yHeaders, 0, tableLabels, 1, yHeaders.length);
    tableModel.setColumnIdentifiers(tableLabels);
    tableModel.setColumnCount(2*y.length);
    for(int j=0; j<y.length; j++) {
      tableModel.setRowCount(y[j].length);
      for(int i=0; i<y[j].length; i++) {
      tableModel.setValueAt(x[j][i], i, 2*j);
        tableModel.setValueAt(y[j][i], i, 2*j+1);
      }
    }
  }
    
  
  public void resetData(String xHeader, String[] yHeaders, double[] x, double[][] data) {
    String[] tableLabels = new String[yHeaders.length + 1];
    tableLabels[0] = xHeader;
    System.arraycopy(yHeaders, 0, tableLabels, 1, yHeaders.length);
    tableModel.setColumnIdentifiers(tableLabels);
    tableModel.setRowCount(data[0].length);
    tableModel.setColumnCount(data.length+1);
    for(int i=0; i<data[0].length; i++) {
      tableModel.setValueAt(x[i], i, 0);
      for(int j=0; j<data.length; j++) {
        tableModel.setValueAt(data[j][i], i, j+1);
      }
    }
  }
    
  
  public void resetData(String[] headers, double[][] data) {
    tableModel.setRowCount(data[0].length);
    tableModel.setColumnCount(data.length);
    tableModel.setColumnIdentifiers(headers);
    for(int i=0; i<data[0].length; i++) {
      for(int j=0; j<data.length; j++) {
        tableModel.setValueAt(data[j][i], i, j);
      }
    }
  }
  
  
  public void clearData() {
    table.setRowSelectionInterval(0, table.getRowCount()-1);
    int[] selectedRows = table.getSelectedRows();
    DefaultTableModel model = (DefaultTableModel) table.getModel();
    for(int i = selectedRows.length-1; i>=0; i--) {
      model.removeRow(selectedRows[i]);
    }
  }
  
  
  public void addData(String xHeader, double[] x, String[] yHeaders, double[][] data) {
    for(int i=0; i<tableModel.getColumnCount(); i++) {
      String colName = tableModel.getColumnName(i);
      for(int j=0; j<yHeaders.length; j++) {
        if(yHeaders[j].equals(colName)) return;
      }
    }
    
    Double[] xData = new Double[x.length];
    for(int i=0; i<x.length; i++) {
      xData[i] = x[i];
    }
    tableModel.addColumn(xHeader, xData);
    
    Double[] yData;
    for(int j=0; j<yHeaders.length; j++) {
      yData = new Double[data[j].length];
      for(int i=0; i<x.length; i++) {
        yData[i] = data[j][i];
      }
      tableModel.addColumn(yHeaders[j], yData);
    }
  }
  
  
  public JTable getTable() {
    return table;
  }
  
  
  public void packTableData() {
    for (int c=0; c<table.getColumnCount(); c++) {
      packColumn(table, c, 2);
    }
  }
  
  
  public DefaultTableModel getTableModel() {
    return tableModel;
  }
  
  
  public void packColumn(JTable table, int vColIndex, int margin) {
    DefaultTableColumnModel colModel = (DefaultTableColumnModel)table.getColumnModel();
    TableColumn col = colModel.getColumn(vColIndex);
    int width = 0;
    
    // Get width of column header
    TableCellRenderer renderer = col.getHeaderRenderer();
    if (renderer == null) {
      renderer = table.getTableHeader().getDefaultRenderer();
    }
    Component comp = renderer.getTableCellRendererComponent(table, col.getHeaderValue(), false, false, 0, 0);
    width = comp.getPreferredSize().width;
    
    // Get maximum width of column data
    for (int r=0; r<table.getRowCount(); r++) {
      renderer = table.getCellRenderer(r, vColIndex);
      comp = renderer.getTableCellRendererComponent(table, table.getValueAt(r, vColIndex), false, false, r, vColIndex);
      width = Math.max(width, comp.getPreferredSize().width);
    }
    
    // Add margin
    width += 2*margin;
    
    // Set the width
    col.setPreferredWidth(width);
  }  
}
