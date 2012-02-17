package com.AandR.latiz.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.TreeMap;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.jdom.Element;

import com.AandR.gui.ui.JButtonX;
import com.AandR.io.AsciiFile;
import com.AandR.io.FileSystem;
import com.AandR.io.XmlFile;
import com.AandR.latiz.core.GlobalParameterMap;
import com.AandR.latiz.core.GlobalVariable;
import com.AandR.latiz.resources.Resources;

/**
 * @author Dr. Richard St. John
 * @version $Revision$, $Date$
 */
public class GlobalParametersPanel extends JPanel {

    private File workspaceFile;

    /**
     *
     */
    public GlobalParametersPanel() {
        initialize();
    }

    private void initialize() {
        GlobalListener globalListener = new GlobalListener();

        JButtonX meccaButton = new JButtonX("Jobs");
        meccaButton.setActionCommand("MECCA");
        meccaButton.addActionListener(globalListener);

        JButtonX clearButton = new JButtonX("Clear");
        clearButton.setActionCommand("CLEAR");
        clearButton.addActionListener(globalListener);

        JPanel northPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        northPanel.add(meccaButton);
        northPanel.add(clearButton);

        JScrollPane scroller = new JScrollPane(GlobalParameterMap.getInstanceOf().getTable());
        scroller.setPreferredSize(new Dimension(300, 350));
        JPanel tablePanel = new JPanel(new GridLayout(1, 1));
        tablePanel.add(scroller);

        setLayout(new BorderLayout());
        add(northPanel, BorderLayout.NORTH);
        add(tablePanel, BorderLayout.CENTER);
    }

    public void setWorkspaceFile(File workspaceFile) {
        this.workspaceFile = workspaceFile;
    }

    /**
     *
     * @author Dr. Richard St. John
     * @version $Revision$, $Date$
     */
    private class GlobalListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            String command = e.getActionCommand();
            if (command.equalsIgnoreCase("MECCA")) {
                actionCreateMeccaJobs();
            } else if (command.equalsIgnoreCase("CLEAR")) {
                actionClearGlobals();
            }
        }

        private void actionCreateMeccaJobs() {
            if (workspaceFile == null) {
                return;
            }

            Element workspaceRoot;
            try {
                workspaceRoot = XmlFile.readRootElement(workspaceFile);
            } catch (Exception e1) {
                e1.printStackTrace();
                return;
            }

            Element globalsElement = workspaceRoot.getChild("globalParameters");
            if (globalsElement == null) {
                return;
            }

            TreeMap<String, GlobalVariable> globalMap = GlobalParameterMap.getInstanceOf();
            ArrayList<GlobalVariable> loops = new ArrayList<GlobalVariable>();

            GlobalVariable thisGlobal;
            for (String key : globalMap.keySet()) {
                thisGlobal = globalMap.get(key);
                if (thisGlobal.isLoop()) {
                    loops.add(thisGlobal);
                }
            }
            if (loops.size() == 0) {
                return;
            }

            // Compute number of independent loops
            int independentLoopCount = 1;
            LinkedHashMap<GlobalVariable, String[]> loopValues = new LinkedHashMap<GlobalVariable, String[]>();
            String[] thisLoopParse;
            for (int i = 0; i < loops.size(); i++) {
                thisGlobal = loops.get(i);
                thisLoopParse = thisGlobal.parseLoop();
                loopValues.put(thisGlobal, thisLoopParse);
                independentLoopCount *= thisLoopParse.length;
            }

            // Create MeccaJobs Folder
            File jobsFolder = new File(workspaceFile.getParentFile(), "MeccaJobs");
            jobsFolder.mkdirs();

            //
            DecimalFormat df = createCounterFormat(independentLoopCount);
            String newFileName;
            String fileName = workspaceFile.getName();
            int extensionIndex = fileName.lastIndexOf(".");

            int div, l;
            for (int k = 0; k < independentLoopCount; k++) {

                // Create Job Folder
                File jobFolder = new File(jobsFolder, fileName.substring(0, extensionIndex) + "Run" + df.format(k + 1));
                jobFolder.mkdirs();

                // Compute Index for each loop
                div = 1;
                for (GlobalVariable g : loopValues.keySet()) {
                    thisLoopParse = loopValues.get(g);

                    l = (k / div) % thisLoopParse.length;
                    setParameterElement(globalsElement, g.getLabel(), thisLoopParse[l]);
                    div = div * thisLoopParse.length;
                }

                // Write new workspace file to job folder
                newFileName = fileName.substring(0, extensionIndex) + "Run" + df.format(k + 1) + fileName.substring(extensionIndex);
                try {
                    XmlFile.write(new File(jobFolder, newFileName), workspaceRoot.getDocument());
                } catch (IOException e) {
                    e.printStackTrace();
                }

                // Write build.sh file to job folder
                writeBuildScript(jobFolder, newFileName);

                // Copy latiz to job folder
                copyLatizToJobFolder(jobFolder);
            }
        }

        private void writeBuildScript(File jobFolder, String newFileName) {
            String os = System.getProperty("os.name").toLowerCase();

            File buildScriptFile = new File(jobFolder, os.contains("windows") ? "build.bat" : "build.sh");
            try {
                FileSystem.copyFile(Resources.class.getResourceAsStream("build.sh"), buildScriptFile);
                AsciiFile.replaceText(buildScriptFile, "$WORKSPACE_FILE$", newFileName);
                AsciiFile.replaceText(buildScriptFile, "$JAVA_COMMAND$", os.contains("windows") ? "java" : "/usr/bin/java");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void copyLatizToJobFolder(File jobFolder) {
            String baseDir;
            if (System.getProperty("os.name").toLowerCase().contains("windows")) {
                baseDir = "C:\\Program Files\\MZA\\Latiz 1.0.0\\";
            } else {
                baseDir = "/opt/AandRcreations/Latiz/";
            }

            try {
                FileSystem.copyFile(new File(baseDir + "latiz.jar"), new File(jobFolder, "latiz.jar"));
                FileSystem.copyDirectory(new File(baseDir + File.separator + "lib"), jobFolder);
                FileSystem.copyDirectory(new File(baseDir + File.separator + "plugins"), jobFolder);
                FileSystem.copyDirectory(new File(baseDir + File.separator + "userLib"), jobFolder);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private DecimalFormat createCounterFormat(int n) {
            DecimalFormat df;
            if (n < 10) {
                df = new DecimalFormat("0");
            } else if (n < 100) {
                df = new DecimalFormat("00");
            } else if (n < 1000) {
                df = new DecimalFormat("000");
            } else {
                df = new DecimalFormat("0000");
            }
            return df;
        }

        private void setParameterElement(Element globalParametersElement, String variable, String value) {
            List<Element> parameterElements = globalParametersElement.getChildren("parameter");
            String thisVariable;
            for (Element e : parameterElements) {
                thisVariable = e.getAttributeValue("var");
                if (thisVariable.equalsIgnoreCase(variable)) {
                    e.getAttribute("value").setValue(value);
                    return;
                }
            }
        }

        /*
        private String[] parseGlobalLoop(String loopString) {
        String strMatch_pattern = "\"([^(\")]*)([^(\")]*)\"";
        Pattern pattern = Pattern.compile(strMatch_pattern);
        Matcher matcher = pattern.matcher(new StringBuffer(loopString));

        ArrayList<String> matches = new ArrayList<String>();
        while(matcher.find()) {
        matches.add(matcher.group(1));
        }
        String[] split = new String[matches.size()];
        return matches.toArray(split);
        }
         */
        private void actionClearGlobals() {
            GlobalParameterMap.getInstanceOf().clearGlobals();
        }
    }
}
