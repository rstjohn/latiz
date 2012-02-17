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
package com.AandR.defaultHdf5.restricted;

import com.AandR.palette.dataWriter.AbstractDataWriter;
import com.AandR.palette.dataWriter.DefaultSavedOutputsImpl;
import com.AandR.palette.model.AbstractPaletteModel;
import com.AandR.palette.plugin.data.Output;
import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.logging.Logger;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import net.miginfocom.swing.MigLayout;
import org.jdom.Element;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.Exceptions;
import org.openide.util.ImageUtilities;

/**
 *
 * @author Aaron Masino
 */
public class DefaultHDF5DataWriter implements AbstractDataWriter {

    private File latFile;
    private JTextField fileField;
    private JPanel parameterPanel;
    private DecimalFormat runCountFormat;
    private AbstractPaletteModel model;

    public DefaultHDF5DataWriter() {
        fileField = new JTextField(System.getProperty("user.home") + "/Desktop/SaveTest.lat5", 20);
    }

    public void registerOutputObservation(AbstractPaletteModel model) {
        this.model = model;
        for (String pluginName : model.getSavedOutputsMap().keySet()) {
            for (String outputKey : model.getSavedOutputsMap().get(pluginName).keySet()) {
                model.registerOutputObserver(this, pluginName, outputKey);
            }
        }
    }

    public void setUp() {
        String fileName = fileField.getText();
        latFile = new File(fileName);
        //ArrayList<LinkedHashMap<String, String>> loops = paletteModel.getGlobalParameterMap().getIndependentLoops();
        ArrayList<LinkedHashMap<String, String>> loops = new ArrayList<LinkedHashMap<String, String>>();
        if (loops == null || loops.isEmpty()) {
            runCountFormat = new DecimalFormat("0");
        } else {
            StringBuilder s = new StringBuilder();
            for (int i = 0; i < String.valueOf(loops.size()).length(); i++) {
                s.append("0");
            }
            runCountFormat = new DecimalFormat(s.toString());
        }

        if (latFile.exists() && !GraphicsEnvironment.isHeadless()) {
            NotifyDescriptor nd = new NotifyDescriptor.Confirmation("Overwrite " + latFile.getPath(), "File Exists");
            Object ans = DialogDisplayer.getDefault().notify(nd);
            //overwrite.showDialog("<HTML>The file <B><I>" + latFile + "</I></B> already exists.<BR>Do you want to overwrite or cancel?", 0);
            if (ans==NotifyDescriptor.NO_OPTION) {
                latFile = null;
                return;
            }
        }

        // Create HDF5 tree structure.
        HashMap<String, LinkedHashMap<String, DefaultSavedOutputsImpl>> saveOutputsMap = model.getSavedOutputsMap();
        try {
            HDF5Worker.createFile(latFile.getPath());
            for(String pluginName : saveOutputsMap.keySet()) {
                HDF5Worker.createGroup(pluginName);
            }
            HDF5Worker.close();
        } catch (Exception e) {
            Logger.getLogger(DefaultHDF5DataWriter.class.getName()).warning("There are errors and unexpected behavior.");
        }
    }

    public void tearDown() {
        model.removeOutputObserver(this);
    }

    public JComponent getParameterPanel() {
        parameterPanel = new JPanel(new MigLayout());
        parameterPanel.add(new JLabel("File:"));
        parameterPanel.add(fileField, "pushx,growx");
        Icon icon = new ImageIcon(ImageUtilities.loadImage("com/AandR/defaultHdf5/resources/find.png"));
        JButton browseButton = new JButton(icon);
        browseButton.addActionListener(new BrowseButtonListener());
        parameterPanel.add(browseButton, "w 20!,h 20!");
        return parameterPanel;
    }

    public void notfyOutputUpdated(Output o) {
        double time = o.getTimeOfLastUpdate();
        if (time == Double.NaN) {
            return;
        }
        HashMap<String, LinkedHashMap<String, DefaultSavedOutputsImpl>> savedOutputsMap = model.getSavedOutputsMap();
        boolean isSaveNeeded = savedOutputsMap.get(o.getOutputPlugin().getName()).get(o.getKey()).isSaveRequested(o);
        if (!isSaveNeeded) {
            return;
        }

        //WRITE DATA TO FILE HERE
        o.setSaveCount(o.getSaveCount() + 1);
        try {
            HDF5Worker.appendData(o.getOutputPlugin().getName(), o.getKey(), o.getValue(), time);
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    public void loadSavedWorkspaceParameters(Element e) {
    }

    public Element createWorkspaceParameters() {
        return null;
    }

    /**
     * 
     */
    private class BrowseButtonListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            File f = new File(fileField.getText());
            JFileChooser jfc;
            if (f.exists()) {
                jfc = new JFileChooser(f.getParent());
            } else {
                jfc = new JFileChooser();
            }
            jfc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
            int option = jfc.showOpenDialog(parameterPanel);
            if (option == JFileChooser.APPROVE_OPTION) {
                f = jfc.getSelectedFile();
                fileField.setText(f.getPath());
            }
        }
    }
}
