package com.AandR.latiz.pluginWizard;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.io.File;
import java.net.URISyntaxException;

import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.border.TitledBorder;

import com.AandR.latiz.resources.Resources;

/**
 * @author Dr. Richard St. John
 * @version $Revision$, $Date$
 */
public class TemplateChooserPanel extends WizardPanel {

    private JRadioButton radioScheduler, radioLazy, radioDiligent, radioOther;
    private JCheckBox checkWorkspace, checkDisplay;

    public TemplateChooserPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.add(createTemplatePanel(), BorderLayout.NORTH);
        panel.add(createOptionalsPanel(), BorderLayout.CENTER);

        setLayout(new BorderLayout());
        add(new JScrollPane(panel), BorderLayout.NORTH);
    }

    private JPanel createTemplatePanel() {
        ButtonGroup bg = new ButtonGroup();
        bg.add(radioScheduler = new JRadioButton("Plugin will be responsible for scheduling event.", true));
        bg.add(radioDiligent = new JRadioButton("Plugin will always update its output when acknowledging modified intpus."));
        bg.add(radioLazy = new JRadioButton("Plugin will update outputs only when output is requested."));
        bg.add(radioOther = new JRadioButton("Other"));

        JPanel panel = new JPanel(new GridLayout(4, 1, 5, 5));
        panel.add(radioScheduler);
        panel.add(radioDiligent);
        panel.add(radioLazy);
        panel.add(radioOther);
        panel.setBorder(new TitledBorder("Plugin Template"));
        return panel;
    }

    private JPanel createOptionalsPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 1, 5, 5));
        panel.add(checkWorkspace = new JCheckBox("Load / Save workspace inputs?", true));
        panel.add(checkDisplay = new JCheckBox("Plugin has displayable data."));
        panel.setBorder(new TitledBorder("Optionals"));
        return panel;
    }

    public String getOptionalsSourceCode() {
        String s = "";
        if (checkDisplay.isSelected()) {
            s += getDisplayString();
        }
        if (checkWorkspace.isSelected()) {
            s += getSaveWorkspaceString();
        }
        return s;
    }

    public String getImportsString() {
        String s = "";
        if (checkWorkspace.isSelected()) {
            s += "import org.jdom.Element;\n\n";
        }
        return s;
    }

    private String getSaveWorkspaceString() {
        String s = "";
        s += "  public Element createWorkspaceParameters() {\n";
        s += "    Element parameterElement = new Element(\"parameter\");\n";
        s += "    return parameterElement;\n";
        s += "  }\n\n\n";
        s += "  public void loadSavedWorkspaceParameters(Element e) {\n";
        s += "    if(e==null) return;\n";
        s += "    Element element = e.getChild(\"parameter\");\n";
        s += "  }\n";
        return s;
    }

    private String getDisplayString() {
        String s = "";
        s += "  public void paintPluginPanel() {\n";
        s += "  }\n\n\n";
        return s;
    }

    public File getAdapterFile() {
        try {
            if (radioScheduler.isSelected()) {
                return new File(Resources.class.getResource("adapters/SchedulerAdapter.txt").toURI());
            } else if (radioDiligent.isSelected()) {
                return new File(Resources.class.getResource("adapters/DiligentAdapter.txt").toURI());
            } else if (radioLazy.isSelected()) {
                return new File(Resources.class.getResource("adapters/ProcastinatorAdapter.txt").toURI());
            } else {
                return new File(Resources.class.getResource("adapters/GeneralAdapter.txt").toURI());
            }
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getMessageLabel() {
        return "Helps the user chose a template.";
    }

    public String getMessageTitle() {
        return "Plugin Responsibility Chooser";
    }
}
