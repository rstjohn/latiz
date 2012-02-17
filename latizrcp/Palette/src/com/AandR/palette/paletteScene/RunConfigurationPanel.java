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
package com.AandR.palette.paletteScene;

import com.AandR.palette.dataWriter.AbstractDataWriter;
import com.AandR.palette.runtime.IRuntimeManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.HashMap;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import net.miginfocom.swing.MigLayout;
import org.jdom.Element;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Lookup;
import org.openide.util.Lookup.Item;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author Aaron Masino
 */
public class RunConfigurationPanel extends JPanel {

    private DefaultComboBoxModel dataWriterModel;
    private JComboBox dataWriterCombo;
    private HashMap<String, Lookup.Item<AbstractDataWriter>> dataWriters;
    private AbstractDataWriter dataWriterToUse;
    private String dataWriterToUseKey, runTimeManagerToUseKey;
    private JPanel dataWriterPanel;
    private JComponent dataWriterParameterPanel,runtimeManagerParameterPanel;
    private JPanel runTimePanel;
    private DefaultComboBoxModel runTimeModel;
    private JComboBox runTimeCombo;
    private IRuntimeManager runtimeManagerToUse;
    private HashMap<String, Item<IRuntimeManager>> iRuntimeManagers;

    private static final String DEFAULT_RUNTIME_MANAGER_ID = "Default Runtime Manager";
    private static final String DEFAULT_DATA_WRITER_ID = "Default HDF5 Data Writer";
    private JCheckBox dataSavingOnCheckBox;




    public RunConfigurationPanel() {
        setLayout(new MigLayout());
        JTabbedPane tabs = new JTabbedPane();

        runTimePanel = new JPanel(new MigLayout());
        runTimeModel = new DefaultComboBoxModel();
        runTimeCombo = new JComboBox(runTimeModel);
        runTimeCombo.addActionListener(new RunTimeSelectionListener());
        runTimePanel.add(runTimeCombo,"pushx, growx,wrap");
        poputlateRuntimeManagers();
        tabs.addTab("Runtime Managers", runTimePanel);

        dataWriterPanel = new JPanel(new MigLayout());
        dataWriterModel = new DefaultComboBoxModel();
        dataWriterCombo = new JComboBox(dataWriterModel);
        dataWriterCombo.addActionListener(new DataWriterSelectionListener());
        dataSavingOnCheckBox = new JCheckBox("Turn on data saving");
        dataSavingOnCheckBox.setSelected(false);

        dataWriterPanel.add(dataSavingOnCheckBox,"wrap");
        dataWriterPanel.add(dataWriterCombo, "pushx, growx, wrap");
        populateDataWriters();
        tabs.addTab("Data Writer", dataWriterPanel);

        add(tabs, "push,grow");

    }

    public AbstractDataWriter getDataWriterNewInstance() {
        try {
            AbstractDataWriter writer = dataWriters.get(dataWriterToUseKey).getType().newInstance();
            writer.loadSavedWorkspaceParameters(dataWriterToUse.createWorkspaceParameters());
            return writer;
        } catch (Exception ex) {
            return null;
        }
    }

    public void setDataWriter(String dataWriterId){
        dataWriterModel.setSelectedItem(dataWriterId);
    }

    public boolean isDataSavingOn(){
        return dataSavingOnCheckBox.isSelected();
    }



    private void populateDataWriters() {
        //Set a data writer
        Lookup lkpDataWriters = Lookups.forPath("DataWriter");
        Lookup.Template<AbstractDataWriter> dwt = new Lookup.Template<AbstractDataWriter>(AbstractDataWriter.class);
        Collection<? extends Lookup.Item<AbstractDataWriter>> dataWriterItems = lkpDataWriters.lookup(dwt).allItems();

        FileObject dataWriterFO = FileUtil.getConfigRoot().getFileObject("DataWriter");
        dataWriters = new HashMap<String, Lookup.Item<AbstractDataWriter>>();
        String thisKey;
        FileObject fo;
        for (Lookup.Item<AbstractDataWriter> item : dataWriterItems) {
            fo = dataWriterFO.getFileObject(item.getId().split("/")[1] + ".instance");
            thisKey = fo.getAttribute("id").toString();
            dataWriters.put(thisKey, item);
            dataWriterModel.addElement(thisKey);
        }

        for (String key : dataWriters.keySet()) {
            if (key.equals(DEFAULT_DATA_WRITER_ID)) {
                dataWriterModel.setSelectedItem(DEFAULT_DATA_WRITER_ID);
            }
        }
    }

    private void poputlateRuntimeManagers(){
        //This should be moved to a runtime configuration manager to populate the selections
            Lookup lkp = Lookups.forPath("iRuntimeManager");
            Lookup.Template<IRuntimeManager> t = new Lookup.Template<IRuntimeManager>(IRuntimeManager.class);
            Collection<? extends Lookup.Item<IRuntimeManager>> items = lkp.lookup(t).allItems();

            FileObject iRuntimeManagerFO = FileUtil.getConfigRoot().getFileObject("iRuntimeManager");
            iRuntimeManagers = new HashMap<String, Lookup.Item<IRuntimeManager>>();
            String thisKey;
            FileObject fo;
            for (Lookup.Item<IRuntimeManager> item : items) {
                fo = iRuntimeManagerFO.getFileObject(item.getId().split("/")[1] + ".instance");
                thisKey = fo.getAttribute("id").toString();
                iRuntimeManagers.put(thisKey, item);
                runTimeModel.addElement(thisKey);
            }

            for(String key : iRuntimeManagers.keySet()){
                if(key.equals(DEFAULT_RUNTIME_MANAGER_ID)){
                    runTimeModel.setSelectedItem(DEFAULT_RUNTIME_MANAGER_ID);

                }
            }
    }

    public IRuntimeManager getRuntimeManagerNewInstance(){
        try {
            Element settings = runtimeManagerToUse.createWorkspaceParameters();
            IRuntimeManager manager = iRuntimeManagers.get(runTimeManagerToUseKey).getType().newInstance();
            manager.loadSavedWorkspaceParameters(runtimeManagerToUse.createWorkspaceParameters());
            return manager;
        } catch (Exception e) {
            return null;
        }
    }

    public void setRuntimeManager(String runtimeManagerId){
        runTimeModel.setSelectedItem(runtimeManagerId);
    }

    private class DataWriterSelectionListener implements ActionListener{

        public void actionPerformed(ActionEvent e) {
           JComboBox cb = (JComboBox)e.getSource();
           Object key = cb.getModel().getSelectedItem();
           
           if(dataWriterParameterPanel!=null)dataWriterPanel.remove(dataWriterParameterPanel);
           dataWriterToUse=null;
           dataWriterParameterPanel=null;

           dataWriterToUseKey = (String)key;
           dataWriterToUse = dataWriters.get(key).getInstance();
           dataWriterParameterPanel=dataWriterToUse.getParameterPanel();
           dataWriterPanel.add(dataWriterParameterPanel, "push, grow");
           dataWriterPanel.revalidate();
           dataWriterPanel.repaint();
        }
    }

    private class RunTimeSelectionListener implements ActionListener{

        public void actionPerformed(ActionEvent e) {
            JComboBox cb = (JComboBox)e.getSource();
           Object key = cb.getModel().getSelectedItem();

           if(runtimeManagerParameterPanel!=null)runTimePanel.remove(runtimeManagerParameterPanel);
           runtimeManagerToUse=null;
           runtimeManagerParameterPanel=null;

           runTimeManagerToUseKey = (String) key;
           runtimeManagerToUse=iRuntimeManagers.get(key).getInstance();
           runtimeManagerParameterPanel = runtimeManagerToUse.getParameterPanel();
           runTimePanel.add(runtimeManagerParameterPanel,"push,grow");
           runTimePanel.revalidate();
           runTimePanel.repaint();
        }
    }

}
