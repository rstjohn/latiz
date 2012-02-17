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
package com.AandR.findPlugin;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;
import java.util.StringTokenizer;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import net.miginfocom.swing.MigLayout;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.util.ImageUtilities;
import org.openide.windows.IOProvider;

public final class FindPluginAction implements ActionListener {
    private static final int TABLE_ROW_COUNT = 30;
    private FindPluginListener dialogListener;
    private HashMap<Integer, File> pluginHits;
    private JLabel labelName,  labelCategory;
    private JTable tableResults;
    private JTextArea areaComments,  areaKeywords;
    private JTextField fieldKeywords;

    public void actionPerformed(ActionEvent e) {
        initialize();
        DialogDescriptor dialog = new DialogDescriptor(createContentPane(), "Find Plugin Dialog...");
        DialogDisplayer.getDefault().createDialog(dialog).setVisible(true);
    }

    private void initialize() {
        dialogListener = new FindPluginListener();
        tableResults = new JTable(new DefaultTableModel(new String[]{"Plugin Name", "Author", "Date", "Description", "Access"}, TABLE_ROW_COUNT));
        tableResults.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        tableResults.getColumnModel().getColumn(4).setCellRenderer(new TableRenderer());
        tableResults.getColumnModel().getColumn(0).setPreferredWidth(148);
        tableResults.getColumnModel().getColumn(3).setPreferredWidth(248);
        tableResults.getColumnModel().getColumn(4).setPreferredWidth(43);
        tableResults.getSelectionModel().addListSelectionListener(dialogListener);

        fieldKeywords = new JTextField(35);
        fieldKeywords.setActionCommand("FIND");
        fieldKeywords.addActionListener(dialogListener);

        areaComments = new JTextArea(10, 20);
        areaComments.setEditable(false);

        areaKeywords = new JTextArea(10, 20);
        areaKeywords.setEditable(false);

        labelName = new JLabel("Name:");
        labelCategory = new JLabel("Category:");

        pluginHits = new HashMap<Integer, File>();
    }

    private Container createContentPane() {
        JButton findButton = new JButton("Find");
        findButton.setActionCommand("FIND");
        findButton.addActionListener(dialogListener);

        JPanel p = new JPanel(new MigLayout("", "[][]", "[][]"));
        p.add(new JLabel("Keyword"));
        p.add(fieldKeywords, "pushx, growx");
        p.add(findButton, "wrap");
        p.add(new JScrollPane(tableResults), "grow, span");

        JSplitPane splitter = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitter.setTopComponent(p);
        splitter.setBottomComponent(createDetailsPanel());
        return splitter;
    }

    private JPanel createDetailsPanel() {
        JScrollPane commentsScroller = new JScrollPane(areaComments);
        JPanel commentsPanel = new JPanel(new BorderLayout());
        commentsPanel.add(new JLabel("Comments"), BorderLayout.NORTH);
        commentsPanel.add(commentsScroller, BorderLayout.CENTER);

        JScrollPane keywordsScroller = new JScrollPane(areaKeywords);
        JPanel keywordsPanel = new JPanel(new BorderLayout());
        keywordsPanel.add(new JLabel("Keywords"), BorderLayout.NORTH);
        keywordsPanel.add(keywordsScroller, BorderLayout.CENTER);

        JPanel northPanel = new JPanel(new MigLayout("", "[][][][]", "[][][]"));
        northPanel.setBorder(new TitledBorder("Selected Plugin Details"));
        northPanel.add(new JLabel("Name:"));
        northPanel.add(labelName, "span, pushx, growx, wrap");
        northPanel.add(new JLabel("Category:"));
        northPanel.add(labelCategory, "span 3, pushx, growx, wrap");

        JPanel panel = new JPanel(new MigLayout("", "[][]", "[][]"));
        panel.add(northPanel, "span, growx, wrap");
        panel.add(commentsScroller, "push, grow");
        panel.add(keywordsScroller, "push, grow");

        return panel;
    }

    /**
     *
     * @author Dr. Richard St. John
     * @version $Revision$, $Date$
     */
    private class FindPluginListener implements ActionListener, ListSelectionListener {

        public void actionPerformed(ActionEvent e) {
            String command = e.getActionCommand();
            if (command.equals("FIND")) {
                try {
                    actionFindPlugins();
                } catch (Exception e1) {
                    //new ErrorDialog("Find Plugin Error", "Error occurred while searching for " + fieldKeywords.getText(), e1);
                }
            }
        }

        private void actionFindPlugins() throws FileNotFoundException, IOException {
            String keywords = fieldKeywords.getText();
            IOProvider.getDefault().getIO("Output", false).getOut().println("Keywords: " + keywords);
            if (keywords == null || keywords.trim().equals("") || keywords.equals("all")) {
                findAllPlugins();
            } else {
                findPlugins(getKeywords(fieldKeywords.getText()));
            }
        }

        private void findAllPlugins() throws FileNotFoundException, IOException {
//            clearTable();
//            pluginHits.clear();
//            int row = 0;
//            ArrayList<File> pluginList = PluginManager.getInstanceOf().getPropertyFiles();
//
//            int rowCount = pluginList.size() > TABLE_ROW_COUNT ? pluginList.size() : TABLE_ROW_COUNT;
//            ((DefaultTableModel) tableResults.getModel()).setRowCount(rowCount);
//            Properties props;
//            for (File f : pluginList) {
//                props = new Properties();
//                props.load(new FileInputStream(f));
//                tableResults.setValueAt(props.getProperty("id"), row, 0);
//                tableResults.setValueAt(props.getProperty("author"), row, 1);
//                tableResults.setValueAt(props.getProperty("date"), row, 2);
//                tableResults.setValueAt(props.getProperty("desc"), row, 3);
//                tableResults.setValueAt(props.getProperty("isRestricted"), row, 4);
//                pluginHits.put(row, f);
//                row++;
//            }
        }

        private void findPlugins(ArrayList<String> keywords) throws FileNotFoundException, IOException {
//            if (keywords == null) {
//                return;
//            }
//            clearTable();
//            pluginHits.clear();
//            int row = 0;
//            ArrayList<File> pluginList = PluginManager.getInstanceOf().getPropertyFiles();
//
//            int rowCount = pluginList.size() > TABLE_ROW_COUNT ? pluginList.size() : TABLE_ROW_COUNT;
//            ((DefaultTableModel) tableResults.getModel()).setRowCount(rowCount);
//            Properties props;
//            for (File f : pluginList) {
//                props = new Properties();
//                props.load(new FileInputStream(f));
//                ArrayList pluginKeywords = getKeywords(props.getProperty("keywords"));
//
//                if (pluginKeywords != null && containsKeyword(pluginKeywords, keywords)) {
//                    addToTable(props, row, f);
//                    row++;
//                    continue;
//                }
//
//                boolean match;
//                for (String keyword : keywords) {
//                    match = props.getProperty("id").toLowerCase().contains(keyword);
//                    match |= (props.getProperty("desc") != null && props.getProperty("desc").toLowerCase().contains(keyword));
//                    if (match) {
//                        addToTable(props, row, f);
//                        row++;
//                        break;
//                    }
//                }
//            }
        }

        private void addToTable(Properties props, int row, File f) {
            tableResults.setValueAt(props.getProperty("id"), row, 0);
            tableResults.setValueAt(props.getProperty("author"), row, 1);
            tableResults.setValueAt(props.getProperty("date"), row, 2);
            tableResults.setValueAt(props.getProperty("desc"), row, 3);
            tableResults.setValueAt(props.getProperty("isRestricted"), row, 4);
            pluginHits.put(row, f);
        }

        private void clearTable() {
            DefaultTableModel tm = (DefaultTableModel) tableResults.getModel();
            for (int row = 0; row < tm.getRowCount(); row++) {
                for (int col = 0; col < tm.getColumnCount(); col++) {
                    tm.setValueAt(null, row, col);
                }
            }
        }

        private boolean containsKeyword(ArrayList<String> list1, ArrayList<String> list2) {
            for (String s : list1) {
                if (list2.contains(s)) {
                    return true;
                }
            }
            return false;
        }

        private ArrayList<String> getKeywords(String s) {
            if (s == null || s.equals("")) {
                return null;
            }
            ArrayList<String> keywords = new ArrayList<String>();
            StringTokenizer tokens = new StringTokenizer(s);
            while (tokens.hasMoreTokens()) {
                keywords.add(tokens.nextToken().toLowerCase());
            }
            return keywords;
        }

        public void valueChanged(ListSelectionEvent e) {
            if (e.getValueIsAdjusting()) {
                return;
            }
            try {
                File f = pluginHits.get(tableResults.getSelectedRow());
                if (f == null) {
                    areaComments.setText("");
                    areaKeywords.setText("");
                    labelName.setText("<HTML>Name:</HTML>");
                    labelCategory.setText("<HTML>Categroy:</HTML>");
                    return;
                }
                Properties props = new Properties();
                props.load(new FileInputStream(f));
                areaComments.setText(props.getProperty("comments"));
                areaKeywords.setText(props.getProperty("keywords"));
                labelName.setText("<HTML>Name: \t<B>" + props.getProperty("id") + "</B></HTML>");
                labelCategory.setText("<HTML>Categroy: \t<B>" + props.getProperty("parentID") + "</B></HTML>");
            } catch (Exception e1) {
            }
        }
    }

    /**
     *
     * @author Dr. Richard St. John
     * @version $Revision$, $Date$
     */
    private class TableRenderer extends JPanel implements TableCellRenderer {

        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            ImageIcon icon;
            if (value == null || value.toString().trim().equals("")) {
                icon = null;
            } else if (value.toString().toLowerCase().equals("false")) {
                icon = new ImageIcon(ImageUtilities.loadImage("com/AandR/findPlugin/accept16.png"));
            } else {
                icon = new ImageIcon(ImageUtilities.loadImage("com/AandR/findPlugin/cancel16.png"));
            }

            JPanel panel = new JPanel(new BorderLayout());
            panel.setOpaque(false);
            panel.add(new JLabel(icon));
            return panel;
        }
    }
}
