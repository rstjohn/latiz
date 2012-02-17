package com.AandR.latiz.pluginWizard;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileFilter;

import com.AandR.gui.ui.JButtonX;
import com.AandR.latiz.resources.Resources;

/**
 * @author Dr. Richard St. John
 * @version $Revision$, $Date$
 */
public class SourceCodeDefinition extends WizardPanel {

    private DefaultListModel jarsListModel;
    private JList jarsList;
    private JTextField fieldPackageName, fieldClassName, fieldJarName;
    private String currentDirectory;

    public SourceCodeDefinition() {
        setLayout(new BorderLayout(5, 5));
        add(createContentPanel(), BorderLayout.CENTER);
    }

    private JPanel createContentPanel() {
        fieldPackageName = new JTextField();
        fieldClassName = new JTextField();
        fieldJarName = new JTextField();
        jarsList = new JList(jarsListModel = new DefaultListModel());
        jarsList.setCellRenderer(new JarCellRenderer());

        JPanel packagePanel = new JPanel(new BorderLayout());
        packagePanel.add(createLabel("Java Package Name:"), BorderLayout.WEST);
        packagePanel.add(fieldPackageName, BorderLayout.CENTER);

        JPanel classPanel = new JPanel(new BorderLayout());
        classPanel.add(createLabel("Plugin Class Name:"), BorderLayout.WEST);
        classPanel.add(fieldClassName, BorderLayout.CENTER);

        JPanel jarNamePanel = new JPanel(new BorderLayout());
        jarNamePanel.add(createLabel("Plugin Jar File Name:"), BorderLayout.WEST);
        jarNamePanel.add(fieldJarName, BorderLayout.CENTER);

        ActionListener jarsListener = new JarsListener();

        Dimension bs = new Dimension(100, 24);
        JButtonX addButton = new JButtonX("Add Jars...");
        addButton.addActionListener(jarsListener);
        addButton.setPreferredSize(bs);

        JButtonX removeButton = new JButtonX("Remove");
        removeButton.addActionListener(jarsListener);
        removeButton.setPreferredSize(bs);

        JPanel buttonPanelGrid = new JPanel(new GridLayout(2, 1, 5, 5));
        buttonPanelGrid.add(addButton);
        buttonPanelGrid.add(removeButton);

        JPanel buttonPanel = new JPanel(new BorderLayout());
        buttonPanel.add(buttonPanelGrid, BorderLayout.NORTH);

        JTextArea classPathArea = new JTextArea(4, 5);
        classPathArea.setEditable(false);
        JPanel jarPanel = new JPanel(new GridLayout(1, 1));
        jarPanel.add(new JScrollPane(jarsList));

        JPanel northPanel = new JPanel(new GridLayout(3, 1, 5, 5));
        northPanel.add(packagePanel);
        northPanel.add(classPanel);
        northPanel.add(jarNamePanel);

        JPanel centerPanel = new JPanel(new BorderLayout(5, 5));
        centerPanel.setBorder(new CompoundBorder(
                new EmptyBorder(13, 0, 0, 0),
                new TitledBorder("JARs on the build path")));
        centerPanel.add(buttonPanel, BorderLayout.WEST);
        centerPanel.add(jarPanel, BorderLayout.CENTER);

        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(new EmptyBorder(5, 10, 5, 10));
        panel.add(northPanel, BorderLayout.NORTH);
        panel.add(centerPanel, BorderLayout.CENTER);
        return panel;
    }

    public String getMessageLabel() {
        return "This page gathers information about where the source code and dependencies are located. "
                + "JAR files placed on the build path will be copied to this plugin's folder.";
    }

    public String getMessageTitle() {
        return "Source Code Definitions";
    }

    public JTextField getFieldPackageName() {
        return fieldPackageName;
    }

    public void setFieldPackageName(JTextField fieldPackageName) {
        this.fieldPackageName = fieldPackageName;
    }

    public JTextField getFieldClassName() {
        return fieldClassName;
    }

    public void setFieldClassName(JTextField fieldClassName) {
        this.fieldClassName = fieldClassName;
    }

    public JTextField getFieldJarName() {
        String jarName = fieldJarName.getText();
        if (!jarName.endsWith(".jar")) {
            fieldJarName.setText(jarName + ".jar");
        }
        return fieldJarName;
    }

    public DefaultListModel getJarsListModel() {
        return jarsListModel;
    }

    public void setFieldJarName(JTextField fieldJarName) {
        this.fieldJarName = fieldJarName;
    }

    private JLabel createLabel(String label) {
        JLabel jlabel = new JLabel(label);
        jlabel.setPreferredSize(new Dimension(130, 24));
        return jlabel;
    }

    /**
     *
     * @author Dr. Richard St. John
     * @version $Revision$, $Date$
     */
    private class JarsListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            String command = ((JButtonX) e.getSource()).getText();
            if (command.startsWith("Add Jars")) {
                actionAdd();
            } else if (command.startsWith("Remove")) {
                actionRemove();
            }
        }

        private void actionAdd() {
            if (currentDirectory == null) {
                currentDirectory = System.getProperty("user.home");
            }
            JFileChooser fileChooser = new JFileChooser(currentDirectory);
            fileChooser.setMultiSelectionEnabled(true);
            fileChooser.setFileFilter(new FileFilter() {

                public boolean accept(File f) {
                    return f.isDirectory() || f.getName().endsWith(".jar") || f.getName().endsWith(".zip");
                }

                public String getDescription() {
                    return "JAR Files (*.jar, *.zip)";
                }
            });
            int returnVal = fileChooser.showOpenDialog(SourceCodeDefinition.this);
            if (returnVal == JFileChooser.CANCEL_OPTION) {
                return;
            }

            File[] selectedFiles = fileChooser.getSelectedFiles();
            currentDirectory = selectedFiles[0].getParent();
            for (File f : selectedFiles) {
                jarsListModel.addElement(f);
            }
        }

        private void actionRemove() {
            Object[] selectedItems = jarsList.getSelectedValues();
            if (selectedItems == null || selectedItems.length < 1) {
                return;
            }
            for (Object o : selectedItems) {
                jarsListModel.removeElement(o);
            }
        }
    }

    /**
     *
     * @author Dr. Richard St. John
     * @version $Revision$, $Date$
     */
    private class JarCellRenderer extends DefaultListCellRenderer {

        private ImageIcon javaIcon = Resources.createIcon("jar16.png");
        private ImageIcon javaMissingIcon = new ImageIcon("jarMissing.gif");

        @Override
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean selected, boolean focus) {
            super.getListCellRendererComponent(list, value, index, selected, focus);
            File f = (File) value;
            setIcon(f.exists() ? javaIcon : javaMissingIcon);
            return this;
        }
    }
}
