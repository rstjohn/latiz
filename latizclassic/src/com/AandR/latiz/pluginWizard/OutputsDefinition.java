package com.AandR.latiz.pluginWizard;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Properties;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;


import com.AandR.gui.ui.JButtonX;
import com.AandR.gui.ui.JToolbarButton;
import com.AandR.latiz.gui.InputConnector;
import com.AandR.latiz.gui.OutputConnector;
import com.AandR.latiz.resources.Resources;

/**
 * @author Dr. Richard St. John
 * @version $Revision$, $Date$
 */
public class OutputsDefinition extends WizardPanel {

    private static final String ACTION_REFRESH = "ACTION_REFRESH";
    private static final String ACTION_ADD_SHORTCUT = "ACTION_ADD_SHORTCUT";
    private static final String ACTION_REMOVE_INPUT = "ACTION_REMOVE_SHORTCUT";
    private static final String ACTION_MOVE_UP = "ACTION_MOVE_UP";
    private static final String ACTION_MOVE_DOWN = "ACTION_MOVE_DOWN";
    private DefaultListModel listModel;
    private OutputsDataMapListener outputsDataMapListener;
    private JComboBox comboType;
    private JList list;
    private JTextArea fieldTooltip;
    private JTextField fieldName;
    private LinkedHashMap<String, String> defaultOutputsMap;
    private LinkedHashSet<OutputObject> ioSet;

    public OutputsDefinition(Properties props) {
        initialize();
        list.setSelectedIndex(0);
        setLayout(new BorderLayout());
        add(createPanel());
    }

    private JPanel createPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(createListPanel(), BorderLayout.CENTER);
        panel.add(createInputsPanel(), BorderLayout.WEST);
        return panel;
    }

    private void initialize() {
        createDefaultInputTypes();
        outputsDataMapListener = new OutputsDataMapListener();
        ioSet = new LinkedHashSet<OutputObject>();
        listModel = new DefaultListModel();
        list = new JList(listModel);
        list.setCellRenderer(new OutputsListRenderer());
        list.addMouseListener(outputsDataMapListener);

        String[] labels = new String[defaultOutputsMap.size()];
        int i = 0;
        for (String label : defaultOutputsMap.keySet()) {
            labels[i++] = label;
        }
        comboType = new JComboBox(labels);
        comboType.setEditable(true);
        comboType.setBackground(Color.white);
        ComboBoxRenderer renderer = new ComboBoxRenderer();
        renderer.setPreferredSize(new Dimension(30, 30));
        fieldName = new JTextField(10);
        fieldTooltip = new JTextArea("<Create Tooltip>", 5, 10);
    }

    private void createDefaultInputTypes() {
        defaultOutputsMap = new LinkedHashMap<String, String>();
        defaultOutputsMap.put("Number", "Number.class");
        defaultOutputsMap.put("Double", "Double.class");
        defaultOutputsMap.put("double[]", "double[].class");
        defaultOutputsMap.put("double[][]", "double[][].class");
        defaultOutputsMap.put("Float", "Float.class");
        defaultOutputsMap.put("float[]", "float[].class");
        defaultOutputsMap.put("float[][]", "float[][].class");
        defaultOutputsMap.put("Integer", "Integer.class");
        defaultOutputsMap.put("int[]", "int[].class");
        defaultOutputsMap.put("int[][]", "int[][].class");
        defaultOutputsMap.put("File", "File.class");
        defaultOutputsMap.put("File[]", "File[].class");
        defaultOutputsMap.put("String", "String.class");
        defaultOutputsMap.put("String[]", "String[].class");
    }

    private JPanel createListPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JLabel("Current Outputs Data Map"), BorderLayout.NORTH);
        JScrollPane scroll = new JScrollPane(list);
        scroll.setPreferredSize(new Dimension(150, 120));

        panel.add(scroll, BorderLayout.CENTER);
        panel.add(createListButtonPanel(), BorderLayout.EAST);
        return panel;
    }

    private JPanel createInputsPanel() {
        JLabel idLabel = new JLabel("Output Name ID:");
        idLabel.setPreferredSize(new Dimension(40, 25));

        JLabel pathLabel = new JLabel("Output Object Type:");
        pathLabel.setPreferredSize(new Dimension(40, 25));
        JPanel pathPanel = new JPanel(new GridLayout(5, 1));
        pathPanel.add(idLabel);
        pathPanel.add(fieldName);
        pathPanel.add(pathLabel);
        pathPanel.add(comboType);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 5, 8, 5));
        buttonPanel.add(createRoundedButton("Add", Resources.createIcon("add22.png"), outputsDataMapListener, ACTION_ADD_SHORTCUT, "Add this shortcut to the list"));
        buttonPanel.add(createRoundedButton("Reload", Resources.createIcon("reload22.png"), outputsDataMapListener, ACTION_REFRESH, "Reload this shortcut"));

        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.add(pathPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(fieldTooltip), BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        panel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(8, 5, 5, 5), BorderFactory.createTitledBorder("DataMap Properties")));

        return panel;
    }

    private String parseLabel() {
        return "<HTML>" + fieldName.getText() + " <I>" + comboType.getSelectedItem().toString() + "</I></HTML>";
    }

    private String parseTooltip() {
        String s = fieldTooltip.getText().trim();
        if (s.equalsIgnoreCase("") || s.equalsIgnoreCase("<Create Tooltip>")) {
            return null;
        }

        String tip = s.replace("\n", "<BR>");
        return "<HTML>" + tip + "</HTML>";
    }

    private JButtonX createRoundedButton(String label, ImageIcon icon, ActionListener al, String actionCommand, String toolTip) {
        JButtonX button = new JButtonX(label, icon);
        button.setToolTipText(toolTip);
        button.setActionCommand(actionCommand);
        button.addActionListener(al);
        return button;
    }

    private JToolbarButton createButton(ImageIcon icon, ActionListener al, String actionCommand, String tooltip) {
        JToolbarButton button = new JToolbarButton(icon);
        button.addActionListener(al);
        button.setActionCommand(actionCommand);
        button.setToolTipText(tooltip);
        return button;
    }

    private Component createListButtonPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(createButton(Resources.createIcon("delete22.png"), outputsDataMapListener, ACTION_REMOVE_INPUT, "Remove Selected input"));
        panel.add(createButton(Resources.createIcon("upArrowGreen22.png"), outputsDataMapListener, ACTION_MOVE_UP, "Move Selected input Up"));
        panel.add(createButton(Resources.createIcon("downArrowGreen22.png"), outputsDataMapListener, ACTION_MOVE_DOWN, "Move Selected input Down"));
        return panel;
    }

    public String getIoDataMap() {
        String map = "  public void initializeOutputs() {\n";
        String[] io;
        for (OutputObject out : ioSet) {
            out.label = out.label.replace("<HTML>", "");
            out.label = out.label.replace("</I></HTML>", "");
            out.label = out.label.replace(" <I>", ",");
            io = out.label.split(",");
            if (out.tooltip == null) {
                map += "    addNewOutput(\"" + io[0] + "\", " + defaultOutputsMap.get(io[1]) + ");\n";
            } else {
                map += "    addNewOutput(\"" + io[0] + "\", " + defaultOutputsMap.get(io[1]) + ", \"" + out.tooltip + "\");\n";
            }
        }
        map += "    return;\n";
        map += "  }";
        return map;
    }

    public String getMessageLabel() {
        return "Define the outputs that this plugin can pass to other plugins. Provide a name and "
                + "data type for each output. These output names will appear in Latiz for making connections "
                + "between plugins.";
    }

    public String getMessageTitle() {
        return "Outputs Definition Page";
    }

    /**
     *
     * @author Dr. Richard St. John
     * @version $Revision$, $Date$
     */
    private class OutputsListRenderer extends DefaultListCellRenderer {

        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            OutputObject output = (OutputObject) value;
            JLabel label = new JLabel(value.toString());
            label.setHorizontalTextPosition(SwingConstants.LEFT);
            label.setOpaque(true);
            label.setBackground(OutputConnector.COLOR);
            label.setForeground(Color.WHITE);
            label.setBorder(new EmptyBorder(0, 3, 0, 0));

            JPanel panel = new JPanel(new BorderLayout());
            panel.add(label, BorderLayout.CENTER);
            panel.setToolTipText(output.tooltip);
            panel.setOpaque(false);
            if (isSelected) {
                panel.setBorder(new LineBorder(InputConnector.COLOR, 2));
            } else {
                panel.setBorder(new EmptyBorder(2, 2, 2, 2));
            }
            return panel;
        }
    }

    /**
     *
     * @author Dr. Richard St. John
     * @version $Revision$, $Date$
     */
    private class ComboBoxRenderer extends DefaultListCellRenderer {

        public ComboBoxRenderer() {
            setHorizontalAlignment(SwingConstants.CENTER);
            setVerticalAlignment(SwingConstants.CENTER);
        }

        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (isSelected) {
                setBackground(super.getBackground());
            } else {
                setBackground(Color.WHITE);
            }
            setIcon((ImageIcon) value);
            return this;
        }
    }

    /**
     *
     * @author Dr. Richard St. John
     * @version $Revision$, $Date$
     */
    private class OutputsDataMapListener implements MouseListener, ActionListener {

        private void moveSelectedItemsUp() {
            Object[] items = list.getSelectedValues();
            if (items.length < 1 || listModel.indexOf(items[0]) == 0) {
                return;
            }
            Object temp;
            int index;
            int[] selectedIndices = new int[items.length];
            for (int i = 0; i < items.length; i++) {
                index = listModel.indexOf(items[i]);
                if (index > 0) {
                    temp = listModel.getElementAt(index - 1);
                    listModel.setElementAt(listModel.getElementAt(index), index - 1);
                    listModel.setElementAt(temp, index);
                    selectedIndices[i] = index - 1;
                }
            }
            list.setSelectedIndices(selectedIndices);
            ioSet.clear();
            for (int i = 0; i < listModel.size(); i++) {
                ioSet.add((OutputObject) listModel.get(i));
            }
        }

        public void moveSelectedItemsToTop() {
            for (int i = 0; i < list.getSelectedIndices().length; i++) {
                moveSelectedItemsUp();
            }
        }

        public void moveSelectedItemsDown() {
            Object[] items = list.getSelectedValues();
            if (items.length < 1 || listModel.indexOf(items[items.length - 1]) == listModel.size() - 1) {
                return;
            }

            Object temp;
            int index;
            int[] selectedIndices = list.getSelectedIndices();

            for (int i = items.length - 1; i >= 0; i--) {
                index = listModel.indexOf(items[i]);
                if (index < listModel.size() - 1) {
                    temp = listModel.getElementAt(index + 1);
                    listModel.setElementAt(listModel.getElementAt(index), index + 1);
                    listModel.setElementAt(temp, index);
                    selectedIndices[i] = index + 1;
                }
            }
            list.setSelectedIndices(selectedIndices);
            ioSet.clear();
            for (int i = 0; i < listModel.size(); i++) {
                ioSet.add((OutputObject) listModel.get(i));
            }
        }

        private void updateListFromSet() {
            listModel.clear();
            for (OutputObject o : ioSet) {
                listModel.addElement(o);
            }
        }

        private void actionRefresh() {
            OutputObject selectedItem = (OutputObject) list.getSelectedValue();
            if (selectedItem == null) {
                return;
            }
            selectedItem.label = parseLabel();
            selectedItem.tooltip = parseTooltip();
            list.repaint();
        }

        private void actionRemove() {
            int selectedIndex = list.getSelectedIndex();
            Object[] o = list.getSelectedValues();
            for (int i = 0; i < o.length; i++) {
                ioSet.remove(o[i]);
            }
            updateListFromSet();
            list.setSelectedIndex(Math.min(ioSet.size() - 1, selectedIndex));
        }

        private void actionAdd() {
            ioSet.add(new OutputObject(parseLabel(), parseTooltip()));
            updateListFromSet();
            list.setSelectedIndex(ioSet.size() - 1);
        }

        public void actionPerformed(ActionEvent e) {
            String command = e.getActionCommand();
            if (command.equalsIgnoreCase(ACTION_MOVE_UP)) {
                moveSelectedItemsUp();
            } else if (command.equalsIgnoreCase(ACTION_ADD_SHORTCUT)) {
                actionAdd();
            } else if (command.equalsIgnoreCase(ACTION_MOVE_DOWN)) {
                moveSelectedItemsDown();
            } else if (command.equalsIgnoreCase(ACTION_REFRESH)) {
                actionRefresh();
            } else if (command.equalsIgnoreCase(ACTION_REMOVE_INPUT)) {
                actionRemove();
            }
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
            if (list.getSelectedValue() == null) {
                return;
            }
            updateFromList((OutputObject) list.getSelectedValue());
        }

        private void updateFromList(OutputObject output) {
            String l = output.label;
            l = l.replace("<HTML>", "");
            l = l.replace("</I></HTML>", "");
            l = l.replace(" <I>", ",");
            String[] s = l.split(",");
            fieldName.setText(s[0]);
            comboType.setSelectedItem(s[1]);
            String tooltipString = output.tooltip;
            if (tooltipString == null || tooltipString.equalsIgnoreCase("") || tooltipString.equalsIgnoreCase("<Create Tooltip>")) {
                fieldTooltip.setText("<Create Tooltip>");
            } else {
                tooltipString = tooltipString.replace("<HTML>", "");
                tooltipString = tooltipString.replace("</HTML>", "");
                tooltipString = tooltipString.replace("<BR>", "\n");
                fieldTooltip.setText(tooltipString);
            }
        }
    }

    /**
     *
     * @author Dr. Richard St. John
     * @version $Revision$, $Date$
     */
    private class OutputObject {

        private String label, tooltip;

        public OutputObject(String label, String tooltip) {
            this.label = label;
            this.tooltip = tooltip;
        }

        public String toString() {
            return label;
        }
    }
}
