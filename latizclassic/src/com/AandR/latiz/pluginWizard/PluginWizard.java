package com.AandR.latiz.pluginWizard;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.Properties;

import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.border.EmptyBorder;

import org.jdom.Element;


import com.AandR.gui.ui.JButtonX;
import com.AandR.gui.ui.JToolbarButton;
import com.AandR.io.AsciiFile;
import com.AandR.io.RelativePath;
import com.AandR.io.XmlFile;
import com.AandR.latiz.core.PluginManager;
import com.AandR.latiz.resources.Resources;

/**
 * @author Dr. Richard St. John
 * @version $Revision$, $Date$
 */
public class PluginWizard extends JDialog {

    private static final String ACTION_BACK = "ACTION_BACK";
    private static final String ACTION_NEXT = "ACTION_NEXT";
    private static final String ACTION_FINISH = "FINISH";
    private static final String ACTION_CANCEL = "CANCEL";
    private static final String ACTION_HELP = "HELP";
    private int panelCount = 0;
    private AdditionalInformation additionalInformation;
    private CardLayout cardLayout;
    private InputsDefinition inputsDefinition;
    private JButton backButton, nextButton;
    private JLabel messageLabel;
    private JPanel cards;
    private OutputsDefinition outputsDefinition;
    private PluginDefinition pluginDefinition;
    private Properties settings;
    private SourceCodeDefinition sourceCodeDefinition;
    private TemplateChooserPanel templateChooserPanel;
    private WizardListener wizardListener;

    public PluginWizard() {
        super((JDialog) null, "Plugin Skeleton Wizard", false);
        setAlwaysOnTop(true);
        cards = new JPanel(cardLayout = new CardLayout());
        settings = new Properties();
        wizardListener = new WizardListener();
        setContentPane(createContentPane());
        pack();
    }

    private Container createContentPane() {
        pluginDefinition = new PluginDefinition();
        sourceCodeDefinition = new SourceCodeDefinition();
        templateChooserPanel = new TemplateChooserPanel();
        inputsDefinition = new InputsDefinition(settings);
        outputsDefinition = new OutputsDefinition(settings);
        additionalInformation = new AdditionalInformation(settings);
        cards.add(pluginDefinition, "PAGE1");
        cards.add(sourceCodeDefinition, "PAGE2");
        cards.add(templateChooserPanel, "PAGE3");
        cards.add(inputsDefinition, "PAGE4");
        cards.add(outputsDefinition, "PAGE5");
        cards.add(additionalInformation, "PAGE6");
        cardLayout.show(cards, "PAGE1");
        JPanel panel = new JPanel(new BorderLayout(3, 3));
        panel.add(createNorthPanel(), BorderLayout.NORTH);
        panel.add(cards, BorderLayout.CENTER);
        panel.add(createSouthPanel(), BorderLayout.SOUTH);
        return panel;
    }

    private void setHeaderInformation(String title, String message) {
        String messageString = "<html><B>" + title + "</B><br>" + message + "</html>";
        messageLabel.setText(messageString);
    }

    private Container createNorthPanel() {
        String headerTitle = "Plugin Skeleton Wizard";
        String headerString = "Choose the options that will be used to generate the new plugin skeleton source code.";
        String messageString = "<html><B>" + headerTitle + "</B><br>" + headerString + "</html>";
        Color bg = Color.WHITE;
        messageLabel = new JLabel(messageString);
        messageLabel.setVerticalAlignment(JLabel.TOP);
        messageLabel.setPreferredSize(new Dimension(380, 145));
        messageLabel.setMaximumSize(new Dimension(380, 145));
        messageLabel.setBackground(bg);
        messageLabel.setBorder(new EmptyBorder(15, 20, 20, 10));

        JLabel iconLabel = new JLabel(Resources.createIcon("edit64.png"));
        iconLabel.setVerticalAlignment(JLabel.TOP);
        iconLabel.setBorder(new EmptyBorder(15, 0, 0, 10));

        JPanel messagePanel = new JPanel(new BorderLayout());
        messagePanel.setBackground(bg);
        messagePanel.setOpaque(true);
        messagePanel.add(messageLabel, BorderLayout.CENTER);
        messagePanel.add(iconLabel, BorderLayout.EAST);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(bg);
        panel.add(messagePanel, BorderLayout.NORTH);
        panel.add(new JSeparator(), BorderLayout.CENTER);
        return panel;
    }

    private Container createSouthPanel() {
        JPanel buttonPanel = new JPanel(new GridLayout(1, 4, 3, 3));
        buttonPanel.add(backButton = createButton("< Back", null, ACTION_BACK, "Back"));
        buttonPanel.add(nextButton = createButton("Next >", null, ACTION_NEXT, "Next"));
        buttonPanel.add(createButton("Finish", null, ACTION_FINISH, "Click to finish"));
        buttonPanel.add(createButton("Cancel", null, ACTION_CANCEL, "Click to cancel"));
        backButton.setEnabled(false);

        JToolbarButton helpButton = new JToolbarButton(Resources.createIcon("question22.png"));
        helpButton.setActionCommand(ACTION_HELP);
        helpButton.addActionListener(wizardListener);
        helpButton.setToolTipText("Help");
        helpButton.setPreferredSize(new Dimension(28, 28));

        JPanel southPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        southPanel.add(buttonPanel);
        southPanel.add(helpButton);

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JSeparator(), BorderLayout.CENTER);
        panel.add(southPanel, BorderLayout.SOUTH);
        return panel;
    }

    private JButtonX createButton(String label, Icon icon, String actionCommand, String tooltip) {
        JButtonX button = new JButtonX(label, icon);
        button.addActionListener(wizardListener);
        button.setActionCommand(actionCommand);
        button.setToolTipText(tooltip);
        return button;
    }

    /**
     *
     * @author Dr. Richard St. John
     * @version $Revision$, $Date$
     */
    private class WizardListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            String command = e.getActionCommand();
            if (command.equalsIgnoreCase(ACTION_FINISH)) {
                createSkeleton();
            } else if (command.equalsIgnoreCase(ACTION_BACK)) {
                actionBack();
            } else if (command.equalsIgnoreCase(ACTION_NEXT)) {
                actionNext();
            } else if (command.equalsIgnoreCase(ACTION_CANCEL)) {
                dispose();
            } else if (command.equalsIgnoreCase(ACTION_HELP)) {
                actionHelpDialog();
            }
        }

        private void actionHelpDialog() {
            new Thread() {

                @Override
                public void run() {
                    JDialog dialog = new JDialog((JDialog) null, "Plugin Wizard Help Dialog", false);
                    try {
                        dialog.setContentPane(new JScrollPane(new JEditorPane(Resources.class.getResource("help/PluginWizard.html"))));
                        dialog.pack();
                        dialog.setVisible(true);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }.start();
        }

        private void actionBack() {
            if (panelCount == 0) {
                return;
            }
            panelCount--;
            WizardPanel panel = (WizardPanel) cards.getComponent(panelCount);
            setHeaderInformation(panel.getMessageTitle(), panel.getMessageLabel());
            backButton.setEnabled(panelCount != 0);
            nextButton.setEnabled(panelCount != 5);
            cardLayout.previous(cards);
        }

        private void actionNext() {
            if (panelCount == 5) {
                return;
            }
            panelCount++;
            WizardPanel panel = (WizardPanel) cards.getComponent(panelCount);
            setHeaderInformation(panel.getMessageTitle(), panel.getMessageLabel());
            backButton.setEnabled(panelCount != 0);
            nextButton.setEnabled(panelCount != 5);
            cardLayout.next(cards);
        }

        private void createSkeleton() {
            File devFolder = new File(pluginDefinition.getFieldDevFolder().getText() + File.separator + sourceCodeDefinition.getFieldPackageName().getText().replace(".", File.separator));
            devFolder.mkdirs();
            try {
                createSkeletonClassFile(new File(devFolder, sourceCodeDefinition.getFieldClassName().getText() + ".java"));
                createPluginConfigurationFile(new File(devFolder, "plugin.properties"));
                createBuildScript(new File(devFolder, "build.xml"));
                dispose();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private void createBuildScript(File buildFile) throws IOException {
            String FS = File.separator;

            String pluginDir = "${user.home}" + FS + ".AandRcreations" + FS + "latiz" + FS + "plugins";
            Element pluginDirElement = new Element("property");
            pluginDirElement.setAttribute("name", "pluginDir");
            pluginDirElement.setAttribute("value", pluginDir);

            String devFolder = pluginDefinition.getFieldDevFolder().getText();
            String packageName = sourceCodeDefinition.getFieldPackageName().getText();

            Element project = new Element("project");
            project.setAttribute("name", "autoMakeJar");
            project.setAttribute("default", "makeJar");
            project.setAttribute("basedir", devFolder);

            Element target = new Element("target");
            target.setAttribute("name", "makeJar");

            Element mkdir = new Element("mkdir");
            mkdir.setAttribute("dir", "${pluginDir}" + FS + packageName);

            Element copy = new Element("copy");
            copy.setAttribute("file", packageName.replace(".", File.separator) + File.separator + "plugin.properties");
            copy.setAttribute("todir", "${pluginDir}" + FS + packageName);

            Element jar = new Element("jar");
            String jarName = sourceCodeDefinition.getFieldJarName().getText();
            jar.setAttribute("destfile", "${pluginDir}" + FS + packageName + File.separator + jarName);

            Element fileset = new Element("fileset");
            fileset.setAttribute("dir", ".");
            fileset.setAttribute("includes", packageName.replace(".", "/") + "/**");
            fileset.setAttribute("excludes", packageName.replace(".", "/") + "/*.xml");

            jar.addContent(fileset);

            target.addContent(mkdir);
            target.addContent(jar);
            target.addContent(copy);

            project.addContent(pluginDirElement);
            project.addContent(target);

            XmlFile.write(buildFile, project);
        }

        private void createPluginConfigurationFile(File widgetConfigFile) throws IOException {
            settings.setProperty("id", pluginDefinition.getFieldPluginID().getText());
            settings.setProperty("jarFile", sourceCodeDefinition.getFieldJarName().getText());
            settings.setProperty("desc", pluginDefinition.getFieldDescription().getText());
            settings.setProperty("pluginClass", sourceCodeDefinition.getFieldPackageName().getText() + "." + sourceCodeDefinition.getFieldClassName().getText());
            settings.setProperty("parentID", pluginDefinition.getComboParentID().getSelectedItem().toString());
            settings.setProperty("active", String.valueOf(true));
            settings.setProperty("author", additionalInformation.getFieldAuthor().getText());
            settings.setProperty("date", additionalInformation.getFieldDate().getText());
            settings.setProperty("keywords", additionalInformation.getAreaKeyword().getText());
            settings.setProperty("comments", additionalInformation.getAreaComments().getText());
            settings.setProperty("isRestricted", String.valueOf(additionalInformation.getCheckRestrictedAccess().isSelected()));
            settings.setProperty("classPath", createClassPathString());
            File propFile = new File(pluginDefinition.getFieldDevFolder().getText() + File.separator + sourceCodeDefinition.getFieldPackageName().getText().replace(".", File.separator) + File.separator + "plugin.properties");
            settings.store(new FileOutputStream(propFile), null);
        }

        private String createClassPathString() {
            DefaultListModel jarListModel = sourceCodeDefinition.getJarsListModel();
            if (jarListModel.size() < 1) {
                return "";
            }

            String classPathString = "";
            File referenceDirectory = PluginManager.getInstanceOf().getPluginDirectory();
            for (int i = 0; i < jarListModel.size(); i++) {
                classPathString += RelativePath.getRelativePath(referenceDirectory, (File) jarListModel.get(i)) + ";";
            }
            return classPathString;
        }

        private void createSkeletonClassFile(File newJavaClassFile) throws IOException, URISyntaxException {

            File templateAdapter = templateChooserPanel.getAdapterFile();
            String[] lines = AsciiFile.readLinesInFile(templateAdapter);

            lines[0] = lines[0].replace("$PACKAGE$", "package " + sourceCodeDefinition.getFieldPackageName().getText() + ";");
            lines[4] = lines[4].replace("$IMPORTS$", templateChooserPanel.getImportsString());
            for (int i = 1; i < lines.length; i++) {
                lines[i] = lines[i].replace("$USER$", System.getProperty("user.name"));
                lines[i] = lines[i].replace("$DATE$", new Date().toString());
                lines[i] = lines[i].replace("$COMMENTS$", additionalInformation.getAreaKeyword().getText().trim());
                lines[i] = lines[i].replace("$PLUGIN$", sourceCodeDefinition.getFieldClassName().getText());
                lines[i] = lines[i].replace("$INITIALIZE_INPUTS$", inputsDefinition.getIoDataMap());
                lines[i] = lines[i].replace("$INITIALIZE_OUTPUTS$", outputsDefinition.getIoDataMap());
                lines[i] = lines[i].replace("$OPTIONALS$", templateChooserPanel.getOptionalsSourceCode());
            }
            AsciiFile.write(newJavaClassFile, lines);
        }
    }
}
