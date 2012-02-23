package com.AandR.latiz.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
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
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;


import com.AandR.gui.ui.JButtonX;
import com.AandR.latiz.core.PluginManager;
import com.AandR.latiz.resources.Resources;

/**
 * @author Dr. Richard St. John
 * @version $Revision$, $Date$
 */
public class FindPluginDialog extends JDialog {

    private HashMap<Integer, File> pluginHits;
    private JLabel labelName, labelCategory;
    private JTable tableResults;
    private JTextArea areaComments, areaKeywords;
    private JTextField fieldKeywords;
    private FindPluginListener dialogListener;

    public FindPluginDialog() {
        super((JDialog) null, "Find Plugins Dialog", true);
        setMinimumSize(new Dimension(420, 30));
        initialize();
        setContentPane(createContentPane());
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void initialize() {
        dialogListener = new FindPluginListener();
        tableResults = new JTable(new DefaultTableModel(new String[]{"Plugin Name", "Author", "Date", "Description", "Access"}, 30));
        tableResults.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        tableResults.getColumnModel().getColumn(4).setCellRenderer(new TableRenderer());
        tableResults.getColumnModel().getColumn(0).setPreferredWidth(148);
        tableResults.getColumnModel().getColumn(3).setPreferredWidth(248);
        tableResults.getColumnModel().getColumn(4).setPreferredWidth(43);
        tableResults.getSelectionModel().addListSelectionListener(dialogListener);

        fieldKeywords = new JTextField(35);
        areaComments = new JTextArea(10, 20);
        areaComments.setEditable(false);

        areaKeywords = new JTextArea(10, 20);
        areaKeywords.setEditable(false);

        labelName = new JLabel("Name:");
        labelCategory = new JLabel("Category:");

        pluginHits = new HashMap<Integer, File>();
    }

    private Container createContentPane() {
        JPanel northPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        northPanel.add(new JLabel("Keyword"));
        northPanel.add(fieldKeywords);

        JButtonX findButton = new JButtonX("Find");
        findButton.setActionCommand("FIND");
        findButton.addActionListener(dialogListener);
        northPanel.add(findButton);

        JPanel tablePanel = new JPanel(new BorderLayout(5, 15));
        tablePanel.setBorder(new EmptyBorder(0, 5, 5, 5));
        tablePanel.add(new JSeparator(), BorderLayout.NORTH);
        tablePanel.add(new JScrollPane(tableResults), BorderLayout.CENTER);

        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.add(northPanel, BorderLayout.NORTH);
        panel.add(tablePanel, BorderLayout.CENTER);

        JSplitPane splitter = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitter.setTopComponent(panel);
        splitter.setBottomComponent(createDetailsPanel());
        return splitter;
    }

    private JPanel createDetailsPanel() {
        GridBagConstraints c = new GridBagConstraints();
        c.weightx = 1;
        c.weighty = 1;
        c.anchor = GridBagConstraints.FIRST_LINE_START;
        c.insets.set(2, 0, 2, 0);// c.fill = GridBagConstraints.BOTH;

        JPanel northPanel = new JPanel(new GridBagLayout());
        c.gridx = 0;
        c.gridy = 0;
        northPanel.add(labelName, c);

        c.gridx = 0;
        c.gridy = 1;
        northPanel.add(labelCategory, c);

        JScrollPane commentsScroller = new JScrollPane(areaComments);
        commentsScroller.setPreferredSize(new Dimension(150, 100));
        JPanel commentsPanel = new JPanel(new BorderLayout());
        commentsPanel.add(new JLabel("Comments"), BorderLayout.NORTH);
        commentsPanel.add(commentsScroller, BorderLayout.CENTER);

        JScrollPane keywordsScroller = new JScrollPane(areaKeywords);
        keywordsScroller.setPreferredSize(new Dimension(150, 100));
        JPanel keywordsPanel = new JPanel(new BorderLayout());
        keywordsPanel.add(new JLabel("Keywords"), BorderLayout.NORTH);
        keywordsPanel.add(keywordsScroller, BorderLayout.CENTER);

        JPanel centerPanel = new JPanel(new GridLayout(1, 2, 5, 5));
        centerPanel.setPreferredSize(new Dimension(600, 150));
        centerPanel.add(commentsPanel);
        centerPanel.add(keywordsPanel);

        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(new TitledBorder("Selected Plugin Details"));
        panel.add(northPanel, BorderLayout.NORTH);
        panel.add(centerPanel, BorderLayout.CENTER);
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
            if (command.equalsIgnoreCase("FIND")) {
                try {
                    actionFindPlugins();
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        }

        @SuppressWarnings("unchecked")
        private void actionFindPlugins() throws FileNotFoundException, IOException {
            clearTable();
            pluginHits.clear();
            int row = 0;
            ArrayList<File> pluginList = PluginManager.getInstanceOf().getPropertyFiles();
            ArrayList<String> keywords = getKeywords(fieldKeywords.getText());
            ((DefaultTableModel) tableResults.getModel()).setRowCount(pluginList.size());
            Properties props;
            for (File f : pluginList) {
                props = new Properties();
                props.load(new FileInputStream(f));
                ArrayList pluginKeywords = getKeywords(props.getProperty("keywords"));
                if (keywords == null || pluginKeywords == null) {
                    continue;
                }
                if (!containsKeyword(pluginKeywords, keywords)) {
                    continue;
                }
                tableResults.setValueAt(props.getProperty("id"), row, 0);
                tableResults.setValueAt(props.getProperty("author"), row, 1);
                tableResults.setValueAt(props.getProperty("date"), row, 2);
                tableResults.setValueAt(props.getProperty("desc"), row, 3);
                tableResults.setValueAt(props.getProperty("isRestricted"), row, 4);
                pluginHits.put(row, f);
                row++;

            }
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
            if (s == null || s.equalsIgnoreCase("")) {
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
                e1.printStackTrace();
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
            if (value == null || value.toString().trim().equalsIgnoreCase("")) {
                icon = null;
            } else if (value.toString().toLowerCase().equalsIgnoreCase("false")) {
                icon = Resources.createIcon("accept16.png");
            } else {
                icon = Resources.createIcon("cancel16.png");
            }

            JPanel panel = new JPanel(new BorderLayout());
            panel.setOpaque(false);
            panel.add(new JLabel(icon));
            return panel;
        }
    }
}
