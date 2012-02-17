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
public class InputsDefinition extends WizardPanel {

    private static final String ACTION_REFRESH = "ACTION_REFRESH";
    private static final String ACTION_ADD_SHORTCUT = "ACTION_ADD_SHORTCUT";
    private static final String ACTION_REMOVE_INPUT = "ACTION_REMOVE_SHORTCUT";
    private static final String ACTION_MOVE_UP = "ACTION_MOVE_UP";
    private static final String ACTION_MOVE_DOWN = "ACTION_MOVE_DOWN";
    private DefaultListModel listModel;
    private InputsDataMapListener inputsDataMapListener;
    private JComboBox comboType;
    private JList list;
    private JTextArea fieldTooltip;
    private JTextField fieldName;
    private LinkedHashMap<String, String> defaultInputsMap;
    private LinkedHashSet<InputObject> ioSet;

    public InputsDefinition(Properties props) {
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
        inputsDataMapListener = new InputsDataMapListener();
        ioSet = new LinkedHashSet<InputObject>();
        listModel = new DefaultListModel();
        list = new JList(listModel) {

            @Override
            public String getToolTipText(MouseEvent evt) {
                int index = locationToIndex(evt.getPoint());
                InputObject item = (InputObject) getModel().getElementAt(index);
                return item.tooltip;
            }
        };
        list.setCellRenderer(new InputsListRenderer());
        list.addMouseListener(inputsDataMapListener);

        String[] labels = new String[defaultInputsMap.size()];
        int i = 0;
        for (String label : defaultInputsMap.keySet()) {
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
        defaultInputsMap = new LinkedHashMap<String, String>();
        defaultInputsMap.put("Number", "Number.class");
        defaultInputsMap.put("Double", "Double.class");
        defaultInputsMap.put("double[]", "double[].class");
        defaultInputsMap.put("double[][]", "double[][].class");
        defaultInputsMap.put("Float", "Float.class");
        defaultInputsMap.put("float[]", "float[].class");
        defaultInputsMap.put("float[][]", "float[][].class");
        defaultInputsMap.put("Integer", "Integer.class");
        defaultInputsMap.put("int[]", "int[].class");
        defaultInputsMap.put("int[][]", "int[][].class");
        defaultInputsMap.put("File", "File.class");
        defaultInputsMap.put("File[]", "File[].class");
        defaultInputsMap.put("String", "String.class");
        defaultInputsMap.put("String[]", "String[].class");
    }

    private JPanel createListPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JLabel("Current Inputs Data Map"), BorderLayout.NORTH);
        JScrollPane scroll = new JScrollPane(list);
        scroll.setPreferredSize(new Dimension(150, 120));

        panel.add(scroll, BorderLayout.CENTER);
        panel.add(createListButtonPanel(), BorderLayout.EAST);
        return panel;
    }

    private JPanel createInputsPanel() {
        JLabel idLabel = new JLabel("Input Name ID:");
        idLabel.setPreferredSize(new Dimension(40, 25));

        JLabel pathLabel = new JLabel("Input Object Type:");
        pathLabel.setPreferredSize(new Dimension(40, 25));
        JPanel pathPanel = new JPanel(new GridLayout(5, 1));
        pathPanel.add(idLabel);
        pathPanel.add(fieldName);
        pathPanel.add(pathLabel);
        pathPanel.add(comboType);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 5, 8, 5));
        buttonPanel.add(createRoundedButton("Add", Resources.createIcon("add22.png"), inputsDataMapListener, ACTION_ADD_SHORTCUT, "Add this shortcut to the list"));
        buttonPanel.add(createRoundedButton("Reload", Resources.createIcon("reload22.png"), inputsDataMapListener, ACTION_REFRESH, "Reload this shortcut"));

        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.add(pathPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(fieldTooltip), BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        panel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(8, 5, 5, 5), BorderFactory.createTitledBorder("DataMap Properties")));

        return panel;
    }

    public String parseLabel() {
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
        panel.add(createButton(Resources.createIcon("delete22.png"), inputsDataMapListener, ACTION_REMOVE_INPUT, "Remove Selected input"));
        panel.add(createButton(Resources.createIcon("upArrowGreen22.png"), inputsDataMapListener, ACTION_MOVE_UP, "Move Selected input Up"));
        panel.add(createButton(Resources.createIcon("downArrowGreen22.png"), inputsDataMapListener, ACTION_MOVE_DOWN, "Move Selected input Down"));
        return panel;
    }

    public String getIoDataMap() {
        String map = "  public void initializeInputs() {\n";
        String[] io;
        for (InputObject s : ioSet) {
            s.label = s.label.replace("<HTML>", "");
            s.label = s.label.replace("</I></HTML>", "");
            s.label = s.label.replace(" <I>", ",");
            io = s.label.split(",");
            if (s.tooltip == null) {
                map += "    addNewInput(\"" + io[0] + "\", " + defaultInputsMap.get(io[1]) + ");\n";
            } else {
                map += "    addNewInput(\"" + io[0] + "\", " + defaultInputsMap.get(io[1]) + ", \"" + s.tooltip + "\");\n";
            }
        }
        map += "    return;\n";
        map += "  }";
        return map;
    }

    public String getMessageLabel() {
        return "Define the inputs that this plugin can accept from other plugins. Provide a name and "
                + "data type for each input. These input names will appear in Latiz for making connections "
                + "between plugins. <BR><BR>Inputs that are modified by this plugin are considered mutable.";
    }

    public String getMessageTitle() {
        return "Inputs Definition Page";
    }

    /**
     *
     * @author Dr. Richard St. John
     * @version $Revision$, $Date$
     */
    private class InputsListRenderer extends DefaultListCellRenderer {

        @Override
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            InputObject input = (InputObject) value;
            JLabel label = new JLabel(value.toString());
            label.setOpaque(true);
            label.setBackground(InputConnector.COLOR);
            label.setForeground(Color.WHITE);
            label.setBorder(new EmptyBorder(0, 3, 0, 0));

            ImageIcon icon = null;
            JLabel iconLabel = new JLabel(icon);
            iconLabel.setOpaque(true);
            iconLabel.setBackground(OutputConnector.COLOR);
            iconLabel.setForeground(Color.WHITE);

            JPanel panel = new JPanel(new BorderLayout());
            panel.add(label, BorderLayout.CENTER);
            panel.add(iconLabel, BorderLayout.EAST);
            panel.setToolTipText(input.tooltip);
            panel.setOpaque(false);
            if (isSelected) {
                panel.setBorder(new LineBorder(OutputConnector.COLOR, 2));
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

        @Override
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
    private class InputsDataMapListener implements MouseListener, ActionListener {

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
                ioSet.add((InputObject) listModel.get(i));
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
                ioSet.add((InputObject) listModel.get(i));
            }
        }

        private void updateListFromSet() {
            listModel.clear();
            for (InputObject s : ioSet) {
                listModel.addElement(s);
            }
        }

        private void actionRefresh() {
            InputObject selectedItem = (InputObject) list.getSelectedValue();
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
            ioSet.add(new InputObject(parseLabel(), parseTooltip()));
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
            updateFromList((InputObject) list.getSelectedValue());
        }

        private void updateFromList(InputObject input) {
            String l = input.label;
            l = l.replace("<HTML>", "");
            l = l.replace("</I></HTML>", "");
            l = l.replace(" <I>", ",");
            String[] s = l.split(",");
            fieldName.setText(s[0]);
            comboType.setSelectedItem(s[1]);
            String tooltipString = input.tooltip;
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
    private class InputObject {

        private String label, tooltip;

        public InputObject(String label, String tooltip) {
            this.label = label;
            this.tooltip = tooltip;
        }

        @Override
        public String toString() {
            return label;
        }

        public String getTooltip() {
            return tooltip;
        }
    }
}
