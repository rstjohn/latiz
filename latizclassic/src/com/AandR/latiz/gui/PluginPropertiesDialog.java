package com.AandR.latiz.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;

import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileFilter;

import com.AandR.gui.OptionsDialog;
import com.AandR.gui.ui.JButtonX;
import com.AandR.gui.ui.LineBorderX;
import com.AandR.io.ClassPathHacker;
import com.AandR.io.RelativePath;
import com.AandR.latiz.core.Input;
import com.AandR.latiz.core.Output;
import com.AandR.latiz.core.PluginManager;
import com.AandR.latiz.dev.AbstractPlugin;
import com.AandR.latiz.resources.Resources;

/**
 * @author Dr. Richard St. John
 * @version $Revision$, $Date$
 */
public class PluginPropertiesDialog extends JDialog {

    private DefaultListModel jarsListModel;
    private File propFile;
    private JList jarsList;
    private JTextField fieldPackageName, fieldJarName, fieldClassName;
    private Properties props;
    private String classPath;
    private JTextArea areaKeyword, areaComments;

    public PluginPropertiesDialog(PluginKey pluginKey) {
        setTitle("Properties for " + pluginKey.getId());
        setAlwaysOnTop(true);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                updateProperties();
                dispose();
            }
        });
        propFile = pluginKey.getPropFile();
        props = new Properties();
        try {
            props.load(new FileInputStream(propFile));
        } catch (Exception e) {
            e.printStackTrace();
        }
        setContentPane(createContentPane(null));
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public PluginPropertiesDialog(AbstractPlugin p) {
        setTitle("Properties for " + p.getName());
        setAlwaysOnTop(true);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                updateProperties();
                dispose();
            }
        });
        getPluginInformation(p);
        setContentPane(createContentPane(p));
        pack();
        setLocationRelativeTo(p);
        setVisible(true);
    }

    private void updateProperties() {
        classPath = createClassPathString();
        if (isSaveRequired()) {
            OptionsDialog d = new OptionsDialog(this, "Plugin Properties Changed", new JButtonX[]{new JButtonX("Save Changes"), new JButtonX("Do Not Save")}, OptionsDialog.WARNING_ICON);
            d.setAlwaysOnTop(true);
            d.showDialog("Do you want to save the changes to the properties?", 0);
            if (d.getSelectedButtonIndex() == 1) {
                return;
            }

            try {
                // Update classpath
                props.setProperty("classPath", classPath);
                File thisJarFile;
                for (int i = 0; i < jarsListModel.size(); i++) {
                    thisJarFile = (File) jarsListModel.get(i);
                    if (!ClassPathHacker.exists(thisJarFile)) {
                        ClassPathHacker.addFile((File) jarsListModel.get(i));
                    }
                }

                // Update keywords
                props.setProperty("keywords", areaKeyword.getText());

                // Update comments
                props.setProperty("comments", areaComments.getText());

                // Save properties file
                props.store(new FileOutputStream(propFile), null);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private boolean isSaveRequired() {
        boolean hasKeywordChanged = false;
        String keywords = props.getProperty("keywords");
        if (keywords != null) {
            hasKeywordChanged = !keywords.equals(areaKeyword.getText());
        }

        boolean hasCommentsChanged = false;
        String comments = props.getProperty("comments");
        if (comments != null) {
            hasCommentsChanged = !comments.equals(areaComments.getText());
        }

        boolean hasClassPathChanged = false;
        String oldClassPath = props.getProperty("classPath");
        if (oldClassPath == null || oldClassPath.equals("")) {
            if (classPath != null && !classPath.equals("")) {
                hasClassPathChanged = true;
            }
        } else {
            String path = props.getProperty("classPath");
            path = path.replace("\\", "/");
            if (path.startsWith("./")) {
                path = path.substring(2);
            }
            hasClassPathChanged = !path.equals(classPath);
        }
        return hasClassPathChanged || hasKeywordChanged || hasCommentsChanged;
    }

    private void getPluginInformation(AbstractPlugin p) {
        propFile = PluginManager.getInstanceOf().findPropertiesFile(p);
        props = new Properties();
        try {
            props.load(new FileInputStream(propFile));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Container createContentPane(AbstractPlugin p) {
        JTabbedPane tabs = new JTabbedPane(JTabbedPane.TOP, JTabbedPane.SCROLL_TAB_LAYOUT);

        JPanel ioPanel;
        if (p == null) {
            ioPanel = new JPanel();
            ioPanel.add(new JLabel("Inputs / Outputs are only available from an instance of this plugin"));
        } else {
            ioPanel = createIOPanel(p);
        }
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JScrollPane(ioPanel), BorderLayout.CENTER);
        tabs.addTab("Inputs/Outputs", panel);
        tabs.addTab("Source", createSourceDefinitionPanel());
        tabs.addTab("Definition", createPluginDefinitionPanel());
        tabs.addTab("Additionals", createAdditionInformationPanel());
        tabs.setSelectedIndex(p == null ? 1 : 0);
        return tabs;
    }

    private Component createSourceDefinitionPanel() {
        String classNameFull = props.getProperty("pluginClass");
        int index = classNameFull.lastIndexOf(".");
        String packageName = classNameFull.substring(0, index);
        String className = classNameFull.substring(index + 1);

        fieldPackageName = new JTextField(packageName);
        fieldPackageName.setEditable(false);

        fieldClassName = new JTextField(className);
        fieldClassName.setEditable(false);

        fieldJarName = new JTextField(props.getProperty("jarFile"));
        fieldJarName.setEditable(false);

        jarsList = new JList(jarsListModel = new DefaultListModel());
        jarsList.setCellRenderer(new JarCellRenderer());
        parseClassPathString();

        JPanel packagePanel = new JPanel(new BorderLayout());
        packagePanel.add(createLabel("Java Package Name:", 150), BorderLayout.WEST);
        packagePanel.add(fieldPackageName, BorderLayout.CENTER);

        JPanel classPanel = new JPanel(new BorderLayout());
        classPanel.add(createLabel("Plugin Class Name:", 150), BorderLayout.WEST);
        classPanel.add(fieldClassName, BorderLayout.CENTER);

        JPanel jarNamePanel = new JPanel(new BorderLayout());
        jarNamePanel.add(createLabel("Plugin Jar File Name:", 150), BorderLayout.WEST);
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
        JScrollPane scroller = new JScrollPane(jarsList);
        scroller.setPreferredSize(new Dimension(200, 100));
        jarPanel.add(scroller);

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

    private void parseClassPathString() {
        File[] jars = PluginManager.getInstanceOf().parseClassPath(props.getProperty("classPath"));
        if (jars == null) {
            return;
        }

        for (int i = 0; i < jars.length; i++) {
            jarsListModel.addElement(jars[i]);
        }
    }

    private String createClassPathString() {
        if (jarsListModel.size() < 1) {
            return "";
        }

        String classPathString = "";
        File referenceDirectory = PluginManager.getInstanceOf().getPluginDirectory();
        for (int i = 0; i < jarsListModel.size(); i++) {
            classPathString += RelativePath.getRelativePath(referenceDirectory, (File) jarsListModel.get(i)) + ";";
        }
        classPathString = classPathString.replace("\\", "/");
        return classPathString;
    }

    private JPanel createIOPanel(AbstractPlugin p) {
        int labelWidth = 165;
        int width = 185;
        int inset = 2;
        int n_out = p.getOutputsDataMap().size();
        int n_in = p.getInputsDataMap().size();

        JLabel labelP1 = new JLabel("Inputs", Resources.createIcon("connect_no.png"), JLabel.LEFT);
        labelP1.setPreferredSize(new Dimension(165, 24));
        labelP1.setMinimumSize(new Dimension(165, 24));
        labelP1.setToolTipText(p.getName());
        labelP1.setOpaque(true);
        labelP1.setBorder(new LineBorderX());

        JLabel labelP2 = new JLabel("Outputs", Resources.createIcon("connect_no.png"), JLabel.LEFT);
        labelP2.setPreferredSize(new Dimension(165, 24));
        labelP2.setMinimumSize(new Dimension(165, 24));
        labelP2.setToolTipText(p.getName());
        labelP2.setOpaque(true);
        labelP2.setBorder(new LineBorderX());

        OutputConnector outputConnector;
        InputConnector inputConnector;
        JPanel connectionGridPanel = new JPanel(null);
        connectionGridPanel.setBounds(0, 0, 2 * width, 100);
        connectionGridPanel.add(labelP1);
        connectionGridPanel.add(labelP2);

        Iterator<String> outputKeys = p.getOutputsDataMap().keySet().iterator();
        Iterator<String> inputKeys = p.getInputsDataMap().keySet().iterator();
        String outKey, inKey;
        Dimension d = labelP1.getPreferredSize();
        labelP1.setBounds(0, 0, labelWidth, d.height);
        connectionGridPanel.add(labelP1);
        HashMap<String, Output> thisOutputsDataMap = p.getOutputsDataMap();

        int ho = 28;
        for (int i = 0; i < n_out; i++) {
            outKey = outputKeys.next();
            outputConnector = new OutputConnector(thisOutputsDataMap.get(outKey));
            outputConnector.setToolTipText(thisOutputsDataMap.get(outKey).getToolTipText());
            outputConnector.setLocation(width, ho);
            outputConnector.setSize(outputConnector.getPreferredSize());
            connectionGridPanel.add(outputConnector);
            ho += outputConnector.getPreferredSize().height + 3;
        }

        d = labelP2.getPreferredSize();
        labelP2.setBounds(width, 0, labelWidth, d.height);
        connectionGridPanel.add(labelP2);
        HashMap<String, Input> thisInputsDataMap = p.getInputsDataMap();

        int hi = 28;
        for (int i = 0; i < n_in; i++) {
            inKey = inputKeys.next();
            inputConnector = new InputConnector(thisInputsDataMap.get(inKey));
            inputConnector.setLocation(0, hi);
            inputConnector.setSize(inputConnector.getPreferredSize());
            inputConnector.setToolTipText(thisInputsDataMap.get(inKey).getToolTipText());
            connectionGridPanel.add(inputConnector);
            hi += inputConnector.getPreferredSize().height + 4;
        }

        Point loc = connectionGridPanel.getLocation();
        connectionGridPanel.setBounds(loc.x, loc.y, width + labelWidth + inset, Math.max(hi, ho));
        connectionGridPanel.setPreferredSize(connectionGridPanel.getBounds().getSize());
        return connectionGridPanel;
    }

    private Component createPluginDefinitionPanel() {
        JPanel inputsPanel = new JPanel(new GridLayout(5, 1, 5, 5));
        inputsPanel.setBorder(new EmptyBorder(5, 10, 10, 10));
        inputsPanel.add(createInputPanel("Plugin Author:", createTextField(props.getProperty("author"))));
        inputsPanel.add(createInputPanel("Creation Date:", createTextField(props.getProperty("date"))));
        inputsPanel.add(createInputPanel("Category ID:", createTextField(props.getProperty("parentID"))));
        inputsPanel.add(createInputPanel("Plugin ID:", createTextField(props.getProperty("id"))));
        inputsPanel.add(createInputPanel("Description:", createTextField(props.getProperty("desc"))));

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(inputsPanel, BorderLayout.NORTH);
        return panel;
    }

    private JTextField createTextField(String s) {
        JTextField textField = new JTextField(s);
        textField.setEditable(false);
        return textField;
    }

    private Container createInputPanel(String label, JComponent field) {
        JLabel jlabel = new JLabel(label);
        jlabel.setPreferredSize(new Dimension(130, 20));
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(jlabel, BorderLayout.WEST);
        panel.add(field, BorderLayout.CENTER);
        return panel;
    }

    private Component createAdditionInformationPanel() {
        areaKeyword = new JTextArea(2, 15);
        areaKeyword.setText(props.getProperty("keywords"));
        JPanel keywordsLabelPanel = new JPanel(new BorderLayout());
        keywordsLabelPanel.add(createLabel("Keywords:", 80), BorderLayout.NORTH);
        JPanel keywordPanel = new JPanel(new BorderLayout());
        keywordPanel.add(keywordsLabelPanel, BorderLayout.WEST);
        keywordPanel.add(new JScrollPane(areaKeyword), BorderLayout.CENTER);

        areaComments = new JTextArea(2, 15);
        areaComments.setText(props.getProperty("comments"));
        JPanel commentsLabelPanel = new JPanel(new BorderLayout());
        commentsLabelPanel.add(createLabel("Comments:", 80), BorderLayout.NORTH);
        JPanel commentsPanel = new JPanel(new BorderLayout());
        commentsPanel.add(commentsLabelPanel, BorderLayout.WEST);
        commentsPanel.add(new JScrollPane(areaComments), BorderLayout.CENTER);

        JPanel centerPanel = new JPanel(new GridLayout(2, 1, 10, 10));
        centerPanel.add(keywordPanel);
        centerPanel.add(commentsPanel);

        JCheckBox checkRestrictedAccess = new JCheckBox("Restricted Access?", Boolean.parseBoolean(props.getProperty("isRestricted")));
        checkRestrictedAccess.setEnabled(false);
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.add(centerPanel, BorderLayout.CENTER);
        panel.add(checkRestrictedAccess, BorderLayout.SOUTH);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        return new JScrollPane(panel);
    }

    private JLabel createLabel(String label, int width) {
        JLabel jlabel = new JLabel(label);
        jlabel.setPreferredSize(new Dimension(width, 22));
        jlabel.setVerticalAlignment(SwingConstants.TOP);
        return jlabel;
    }

    /**
     *
     * @author Dr. Richard St. John
     * @version $Revision$, $Date$
     */
    private class JarsListener implements ActionListener {

        private String currentDirectory;

        public void actionPerformed(ActionEvent e) {
            String command = ((JButtonX) e.getSource()).getText();
            if (command.startsWith("Add Jars")) {
                actionAdd();
            } else if (command.startsWith("Remove")) {
                actionRemove();
            }
        }

        private void actionAdd() {
            createClassPathString();

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
            int returnVal = fileChooser.showOpenDialog(PluginPropertiesDialog.this);
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
        private ImageIcon javaMissingIcon = Resources.createIcon("jarMissing.png");

        public Component getListCellRendererComponent(JList list, Object value, int index, boolean selected, boolean focus) {
            super.getListCellRendererComponent(list, value, index, selected, focus);
            File f = (File) value;
            setIcon(f.exists() ? javaIcon : javaMissingIcon);
            return this;
        }
    }
}
