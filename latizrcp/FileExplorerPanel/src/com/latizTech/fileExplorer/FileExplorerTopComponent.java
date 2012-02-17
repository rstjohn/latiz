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
package com.latizTech.fileExplorer;

import com.latizTech.fileExplorer.options.FileExplorerOptionsPanelController;
import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.logging.Logger;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import javax.swing.ActionMap;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.text.DefaultEditorKit;
import org.openide.util.NbBundle;
import org.openide.util.actions.SystemAction;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;
import org.openide.explorer.ExplorerManager;
import org.openide.explorer.ExplorerUtils;
import org.openide.loaders.DataObject;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;
import org.openide.util.NbPreferences;

public final class FileExplorerTopComponent extends TopComponent implements ExplorerManager.Provider, Lookup.Provider {
    private static FileExplorerTopComponent instance;
    private static final String PREFERRED_ID = "FileExplorerTopComponent";
    private boolean showHidden;
    private DefaultComboBoxModel model;
    private ExplorerManager mgr;
    private FilteredFileSystem fs;
    private ArrayList<ShortcutObject> shortcutsList;
    private String fileExtension = null;
    private ShortcutsPanel shortcutPanel;

    public FileExplorerTopComponent() {
        mgr = new ExplorerManager();
        ActionMap actionMap = getActionMap();
        actionMap.put(DefaultEditorKit.copyAction, ExplorerUtils.actionCopy(mgr));
        actionMap.put(DefaultEditorKit.cutAction, ExplorerUtils.actionCut(mgr));
        actionMap.put(DefaultEditorKit.pasteAction, ExplorerUtils.actionPaste(mgr));
        actionMap.put("delete", ExplorerUtils.actionDelete(mgr, true));
        associateLookup(ExplorerUtils.createLookup(mgr, actionMap));

        model = new DefaultComboBoxModel();
        String ext = NbPreferences.forModule(FileExplorerOptionsPanelController.class).get("fileFilters", "*.*;*.hdf;*.hdf5;*.java;*.lat;*.latiz;*.trf;*.bin");
        for (String o : ext.split(";")) {
            model.addElement(o.trim());
        }

        shortcutsList = new ArrayList<ShortcutObject>();
        shortcutsList.add(new ShortcutObject("Home", new File(System.getProperty("user.home")), true, 3));
        shortcutPanel = new ShortcutsPanel(shortcutsList);

        initComponents();
        setName(NbBundle.getMessage(FileExplorerTopComponent.class, "CTL_FileExplorerTopComponent"));
        setToolTipText(NbBundle.getMessage(FileExplorerTopComponent.class, "HINT_FileExplorerTopComponent"));
        setIcon(ImageUtilities.loadImage("com/latizTech/fileExplorer/resources/filesystem16.png"));
    }

    private void reloadFileSystem() {
        fs.setFilenameFilter(fileExtension);
        fs.setShowHidden(showHidden);
        fs.refresh(true);
    }

    public void addShortcut(URL urlToAdd) {
        try {
            File thisFile;
            thisFile = new File(urlToAdd.toURI());
            shortcutsList.add(new ShortcutObject(thisFile.getPath(), thisFile, false, 0));
            shortcutPanel.setShortcuts(shortcutsList);
        } catch (URISyntaxException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    public void removeShortcut(ShortcutObject shortcutObject) {
        shortcutsList.remove(shortcutObject);
        shortcutPanel.setShortcuts(shortcutsList);
    }

    public void refreshList() {
        shortcutPanel.setShortcuts(shortcutsList);
    }

    public void setAsDefault(ShortcutObject shortcutObject) {
        for (ShortcutObject o : shortcutsList) {
            o.isDefault = false;
        }
        shortcutObject.isDefault = true;
        shortcutPanel.setShortcuts(shortcutsList);
    }

    private File getDefaultShortcut() {
        if (shortcutsList.isEmpty()) {
            shortcutsList.add(new ShortcutObject("Home", new File(System.getProperty("user.home")), true, 2));
            return new File(System.getProperty("user.home"));
        }

        if (shortcutsList.isEmpty()) {
            return new File(System.getProperty("user.home"));
        }

        for (ShortcutObject o : shortcutsList) {
            if (o.isDefault) {
                return o.file;
            }
        }
        return shortcutsList.get(0).file;
    }

    void setRootDirectory(File dir) {
        try {
            if (fs == null) {
                fs = new FilteredFileSystem();
            }
            fs.setFilenameFilter(fileExtension);
            fs.setShowHidden(showHidden);
            fs.setRootDirectory(dir);
            DataObject dataObj = DataObject.find(fs.getRoot());
            Node rootNode = dataObj.getNodeDelegate();
            mgr.setRootContext(rootNode);
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        splitter = new javax.swing.JSplitPane();
        splitter.setTopComponent(shortcutPanel);
        beanTreeView = new org.openide.explorer.view.BeanTreeView();
        javax.swing.JLabel jLabel1 = new javax.swing.JLabel();
        javax.swing.JComboBox jComboBox1 = new javax.swing.JComboBox();
        model.setSelectedItem(model.getElementAt(0));
        javax.swing.JCheckBox jCheckBox1 = new javax.swing.JCheckBox();

        splitter.setDividerLocation(150);
        splitter.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);

        beanTreeView.setPreferredSize(new java.awt.Dimension(75, 150));
        splitter.setRightComponent(beanTreeView);

        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(FileExplorerTopComponent.class, "FileExplorerTopComponent.jLabel1.text")); // NOI18N

        jComboBox1.setModel(model);
        jComboBox1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                actionFilterComboClicked(evt);
            }
        });
        jComboBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                actionFilterList(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(jCheckBox1, org.openide.util.NbBundle.getMessage(FileExplorerTopComponent.class, "FileExplorerTopComponent.jCheckBox1.text")); // NOI18N
        jCheckBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                actionShowHidden(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(splitter, javax.swing.GroupLayout.DEFAULT_SIZE, 188, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jComboBox1, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jCheckBox1)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(splitter, javax.swing.GroupLayout.DEFAULT_SIZE, 311, Short.MAX_VALUE)
                .addGap(13, 13, 13)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jCheckBox1)))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void actionShowHidden(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_actionShowHidden
        JCheckBox check = (JCheckBox) evt.getSource();
        showHidden = check.isSelected();
        reloadFileSystem();
    }//GEN-LAST:event_actionShowHidden

    private void actionFilterList(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_actionFilterList
        JComboBox combobox = (JComboBox) evt.getSource();
        String s = combobox.getSelectedItem().toString();
        fileExtension = s.substring(s.lastIndexOf("."));
        if (fileExtension.equals(".*")) {
            fileExtension = null;
        }
        reloadFileSystem();
        combobox.setEditable(false);
    }//GEN-LAST:event_actionFilterList

    private void actionFilterComboClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_actionFilterComboClicked
        JComboBox combobox = (JComboBox) evt.getSource();
        if (evt.getClickCount() == 2) {
            combobox.setEditable(true);
        }
    }//GEN-LAST:event_actionFilterComboClicked

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private org.openide.explorer.view.BeanTreeView beanTreeView;
    private javax.swing.JSplitPane splitter;
    // End of variables declaration//GEN-END:variables
    /**
     * Gets default instance. Do not use directly: reserved for *.settings files only,
     * i.e. deserialization routines; otherwise you could get a non-deserialized instance.
     * To obtain the singleton instance, use {@link #findInstance}.
     */
    public static synchronized FileExplorerTopComponent getDefault() {
        if (instance == null) {
            instance = new FileExplorerTopComponent();
        }
        return instance;
    }

    /**
     * Obtain the FileExplorerTopComponent instance. Never call {@link #getDefault} directly!
     */
    public static synchronized FileExplorerTopComponent findInstance() {
        TopComponent win = WindowManager.getDefault().findTopComponent(PREFERRED_ID);
        if (win == null) {
            Logger.getLogger(FileExplorerTopComponent.class.getName()).warning(
                    "Cannot find " + PREFERRED_ID + " component. It will not be located properly in the window system.");
            return getDefault();
        }
        if (win instanceof FileExplorerTopComponent) {
            return (FileExplorerTopComponent) win;
        }
        Logger.getLogger(FileExplorerTopComponent.class.getName()).warning(
                "There seem to be multiple components with the '" + PREFERRED_ID +
                "' ID. That is a potential source of errors and unexpected behavior.");
        return getDefault();
    }

    @Override
    public int getPersistenceType() {
        return TopComponent.PERSISTENCE_ALWAYS;
    }

    @Override
    public void componentOpened() {
        recallShortcutPreferences();
    }

    @Override
    public void componentClosed() {
        saveShortcutPreferences();
    }

    void writeProperties(java.util.Properties p) {
        // better to version settings since initial version as advocated at
        // http://wiki.apidesign.org/wiki/PropertyFiles
        p.setProperty("version", "1.0");
        // TODO store your settings
    }

    Object readProperties(java.util.Properties p) {
        FileExplorerTopComponent singleton = FileExplorerTopComponent.getDefault();
        singleton.readPropertiesImpl(p);
        return singleton;
    }

    private void readPropertiesImpl(java.util.Properties p) {
        String version = p.getProperty("version");
        // TODO read your settings according to their version
    }

    public void saveShortcutPreferences() {
        String sep = "::";
        //Save shortcuts.
        Preferences prefs = Preferences.userNodeForPackage(FileExplorerTopComponent.class);
        StringBuilder sb;
        for(ShortcutObject so : shortcutsList) {
            sb = new StringBuilder();
            sb.append(so.getAlias()).append(sep);
            sb.append(so.isDefault()).append(sep);
            sb.append(so.iconIndex);
            prefs.put(so.getFile().getPath(), sb.toString());
        }
    }

    public void recallShortcutPreferences() {
        shortcutsList.clear();
        Preferences prefs = Preferences.userNodeForPackage(FileExplorerTopComponent.class);
        try {
            String[] keys = prefs.keys();
            if (keys == null || keys.length < 1) {
                shortcutsList.add(new ShortcutObject("Home", new File(System.getProperty("user.home")), true, 3));
            }
            for(String key : keys) {
                String shortcutDescriptor = prefs.get(key, "");
                String[] s = shortcutDescriptor.split("::");
                shortcutsList.add(new ShortcutObject(s[0], new File(key), Boolean.parseBoolean(s[1]), Integer.parseInt(s[2])));
            }
            refreshList();
        } catch (BackingStoreException ex) {}
        setRootDirectory(getDefaultShortcut());
    }

    @Override
    protected String preferredID() {
        return PREFERRED_ID;
    }

    public ExplorerManager getExplorerManager() {
        return mgr;
    }
}
