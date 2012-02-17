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
package com.AandR.latizOptions.pluginWizard;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.prefs.Preferences;
import javax.swing.DefaultComboBoxModel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.NbPreferences;

/**
 *
 * @author rstjohn
 */
public class PluginWizardOptionsPanel extends javax.swing.JPanel {
    private PluginWizardOptionsPanelController controller;

    /** Creates new form PluginWizardOptionsPanel */
    public PluginWizardOptionsPanel(PluginWizardOptionsPanelController controller) {
        this.controller = controller;
        initComponents();
    }

    void load() {
        // TODO read settings and initialize GUI
        // Example:
        // someCheckBox.setSelected(Preferences.userNodeForPackage(PluginWizardPanel.class).getBoolean("someFlag", false));
        // or for org.openide.util with API spec. version >= 7.4:
        // someCheckBox.setSelected(NbPreferences.forModule(PluginWizardPanel.class).getBoolean("someFlag", false));
        // or:
        // someTextField.setText(SomeSystemOption.getDefault().getSomeStringProperty());
        Preferences pref = NbPreferences.forModule(PluginWizardOptionsPanelController.class);
        String author = pref.get("author", System.getProperty("user.name"));
        fieldAuthor.setText(author);

        // Developer's Folder
        DefaultComboBoxModel devModel = (DefaultComboBoxModel) comboDevFolders.getModel();
        String[] devFolders = PluginWizardOptionsPanelController.getPropertyList(pref, "devFolders", new String[]{System.getProperty("user.home")});
        devModel.removeAllElements();
        for (String folder : devFolders) {
            devModel.addElement(folder);
        }
        comboDevFolders.setSelectedItem(pref.get("defaultDevFolder", System.getProperty("user.name")));

        // Categories
        DefaultComboBoxModel catModel = (DefaultComboBoxModel) comboCategories.getModel();
        String[] categories = PluginWizardOptionsPanelController.getPropertyList(pref, "categories", PluginWizardOptionsPanelController.DEFAULT_CATEGORIES);
        catModel.removeAllElements();
        for (String category : categories) {
            catModel.addElement(category);
        }
        comboCategories.setSelectedItem(pref.get("defaultCategory", PluginWizardOptionsPanelController.DEFAULT_CATEGORIES[1]));

        // Package Bases
        DefaultComboBoxModel baseModel = (DefaultComboBoxModel) comboBases.getModel();
        String[] basePackages = PluginWizardOptionsPanelController.getPropertyList(pref, "basePackages", new String[]{"com.company.name"});
        baseModel.removeAllElements();
        for (String basePackage : basePackages) {
            baseModel.addElement(basePackage);
        }
        comboBases.setSelectedItem(pref.get("defaultBasePackage", "com.company.name"));

        // Input / Output Classes
        DefaultComboBoxModel ioClassesModel = (DefaultComboBoxModel) comboIOclasses.getModel();
        String[] ioClasses = PluginWizardOptionsPanelController.getPropertyList(pref, "ioClasses",
                new String[]{
                    "Number.class",
                    "Double.class", "double[].class", "double[][].class",
                    "Float.class", "float[].class", "float[][].class",
                    "Integer.class", "int.class", "int[].class", "int[][].class",
                    "File.class", "File[].class", "String.class", "String[].class"});
        ioClassesModel.removeAllElements();
        for (String ioClass : ioClasses) {
            ioClassesModel.addElement(ioClass);
        }
        comboIOclasses.setSelectedItem(pref.get("defaultIoClass", "Double.class"));

    }

    void store() {
        // TODO store modified settings
        // Example:
        // Preferences.userNodeForPackage(PluginWizardPanel.class).putBoolean("someFlag", someCheckBox.isSelected());
        // or for org.openide.util with API spec. version >= 7.4:
        // NbPreferences.forModule(PluginWizardPanel.class).putBoolean("someFlag", someCheckBox.isSelected());
        // or:
        // SomeSystemOption.getDefault().setSomeStringProperty(someTextField.getText());
        Preferences pref = NbPreferences.forModule(PluginWizardOptionsPanelController.class);
        pref.put("author", fieldAuthor.getText().trim());

        // Developer's Folder
        DefaultComboBoxModel devModel = (DefaultComboBoxModel) comboDevFolders.getModel();
        String[] devFolders = new String[devModel.getSize()];
        for (int i = 0; i < devFolders.length; i++) {
            devFolders[i] = (String) devModel.getElementAt(i);
        }
        PluginWizardOptionsPanelController.setPropertyList(pref, "devFolders", devFolders);
        pref.put("defaultDevFolder", (String) comboDevFolders.getSelectedItem());

        // Categories
        DefaultComboBoxModel catModel = (DefaultComboBoxModel) comboCategories.getModel();
        String[] categories = new String[catModel.getSize()];
        for (int i = 0; i < categories.length; i++) {
            categories[i] = (String) catModel.getElementAt(i);
        }
        PluginWizardOptionsPanelController.setPropertyList(pref, "categories", categories);
        pref.put("defaultCategory", (String) comboCategories.getSelectedItem());

        // Package Bases
        DefaultComboBoxModel baseModel = (DefaultComboBoxModel) comboBases.getModel();
        String[] basePackages = new String[baseModel.getSize()];
        for (int i = 0; i < basePackages.length; i++) {
            basePackages[i] = (String) baseModel.getElementAt(i);
        }
        PluginWizardOptionsPanelController.setPropertyList(pref, "basePackages", basePackages);
        pref.put("defaultBasePackage", (String) comboBases.getSelectedItem());

        // Input / Output Classes
        DefaultComboBoxModel ioClassesModel = (DefaultComboBoxModel) comboIOclasses.getModel();
        String[] ioClasses = new String[ioClassesModel.getSize()];
        for (int i = 0; i < ioClasses.length; i++) {
            ioClasses[i] = (String) ioClassesModel.getElementAt(i);
        }
        PluginWizardOptionsPanelController.setPropertyList(pref, "ioClasses", ioClasses);
        pref.put("defaultIoClass", (String) comboIOclasses.getSelectedItem());
    }

    boolean valid() {
        // TODO check whether form is consistent and complete
        return true;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        JLabel jLabel1 = new JLabel();
        JLabel jLabel2 = new JLabel();
        JLabel jLabel3 = new JLabel();
        JLabel jLabel4 = new JLabel();
        JLabel jLabel5 = new JLabel();
        fieldAuthor = new JTextField();
        comboDevFolders = new JComboBox();
        comboCategories = new JComboBox();
        comboBases = new JComboBox();
        comboIOclasses = new JComboBox();
        JButton jButton1 = new JButton();
        JButton jButton2 = new JButton();
        JButton jButton3 = new JButton();
        JButton jButton4 = new JButton();
        JButton jButton5 = new JButton();
        JButton jButton6 = new JButton();
        JButton jButton7 = new JButton();
        JButton jButton8 = new JButton();

        jLabel1.setText("Author's Name:");

        jLabel2.setText("Development Folder:");

        jLabel3.setText("Categories:");

        jLabel4.setText("Package Base:");

        jLabel5.setText("Input/Output Objects:");

        jButton1.setIcon(new ImageIcon(getClass().getResource("/com/AandR/latizOptions/resources/db_add.png"))); // NOI18N
        jButton1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                addDevFolder(evt);
            }
        });

        jButton2.setIcon(new ImageIcon(getClass().getResource("/com/AandR/latizOptions/resources/db_remove.png"))); // NOI18N
        jButton2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                actionRemoveDevFolder(evt);
            }
        });

        jButton3.setIcon(new ImageIcon(getClass().getResource("/com/AandR/latizOptions/resources/db_add.png"))); // NOI18N
        jButton3.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                actionAddCategory(evt);
            }
        });

        jButton4.setIcon(new ImageIcon(getClass().getResource("/com/AandR/latizOptions/resources/db_remove.png"))); // NOI18N
        jButton4.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                actionRemoveCategory(evt);
            }
        });

        jButton5.setIcon(new ImageIcon(getClass().getResource("/com/AandR/latizOptions/resources/db_add.png"))); // NOI18N
        jButton5.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                actionAddPackage(evt);
            }
        });

        jButton6.setIcon(new ImageIcon(getClass().getResource("/com/AandR/latizOptions/resources/db_remove.png"))); // NOI18N
        jButton6.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                actionRemovePackage(evt);
            }
        });

        jButton7.setIcon(new ImageIcon(getClass().getResource("/com/AandR/latizOptions/resources/db_add.png"))); // NOI18N
        jButton7.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                actionAddIO(evt);
            }
        });

        jButton8.setIcon(new ImageIcon(getClass().getResource("/com/AandR/latizOptions/resources/db_remove.png"))); // NOI18N
        jButton8.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                actionRemoveIO(evt);
            }
        });

        GroupLayout layout = new GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(jLabel2)
                            .addComponent(jLabel3)
                            .addComponent(jLabel4))
                        .addGap(12, 12, 12))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addPreferredGap(ComponentPlacement.RELATED)))
                .addGroup(layout.createParallelGroup(Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(Alignment.LEADING)
                            .addComponent(comboIOclasses, 0, 240, Short.MAX_VALUE)
                            .addComponent(comboCategories, 0, 240, Short.MAX_VALUE)
                            .addComponent(comboDevFolders, 0, 240, Short.MAX_VALUE)
                            .addComponent(comboBases, 0, 240, Short.MAX_VALUE))
                        .addPreferredGap(ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(Alignment.LEADING, false)
                            .addGroup(Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(jButton7, GroupLayout.PREFERRED_SIZE, 26, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(ComponentPlacement.RELATED)
                                .addComponent(jButton8, GroupLayout.PREFERRED_SIZE, 26, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(ComponentPlacement.RELATED))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(Alignment.LEADING, false)
                                    .addComponent(jButton1, GroupLayout.PREFERRED_SIZE, 26, GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jButton3, GroupLayout.PREFERRED_SIZE, 26, GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(Alignment.LEADING)
                                    .addComponent(jButton2, GroupLayout.PREFERRED_SIZE, 26, GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jButton4, GroupLayout.PREFERRED_SIZE, 26, GroupLayout.PREFERRED_SIZE)))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jButton5, GroupLayout.PREFERRED_SIZE, 26, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jButton6, GroupLayout.PREFERRED_SIZE, 26, GroupLayout.PREFERRED_SIZE))))
                    .addComponent(fieldAuthor, GroupLayout.DEFAULT_SIZE, 304, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(Alignment.BASELINE)
                            .addComponent(jLabel1)
                            .addComponent(fieldAuthor, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(Alignment.BASELINE)
                            .addComponent(jLabel2)
                            .addComponent(comboDevFolders, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton1, GroupLayout.PREFERRED_SIZE, 22, GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jButton2, GroupLayout.PREFERRED_SIZE, 24, GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(Alignment.TRAILING)
                    .addGroup(layout.createParallelGroup(Alignment.BASELINE)
                        .addComponent(jLabel3)
                        .addComponent(comboCategories, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addComponent(jButton3, GroupLayout.PREFERRED_SIZE, 22, GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton4, GroupLayout.PREFERRED_SIZE, 24, GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(Alignment.TRAILING)
                    .addGroup(layout.createParallelGroup(Alignment.BASELINE)
                        .addComponent(jLabel4)
                        .addComponent(comboBases, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addComponent(jButton5, GroupLayout.PREFERRED_SIZE, 22, GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton6, GroupLayout.PREFERRED_SIZE, 24, GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(Alignment.TRAILING)
                    .addComponent(jButton8, GroupLayout.PREFERRED_SIZE, 24, GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createParallelGroup(Alignment.BASELINE)
                        .addComponent(comboIOclasses, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel5))
                    .addComponent(jButton7, GroupLayout.PREFERRED_SIZE, 22, GroupLayout.PREFERRED_SIZE))
                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void addDevFolder(ActionEvent evt) {//GEN-FIRST:event_addDevFolder
        Object selectedItem = comboDevFolders.getSelectedItem();
        String dir = selectedItem == null ? null : selectedItem.toString();
        JFileChooser chooser = new JFileChooser(dir);
        chooser.setDialogType(JFileChooser.CUSTOM_DIALOG);
        chooser.setDialogTitle("Choose Plugin Development Project Folder");
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setApproveButtonText("Select");
        if (chooser.showDialog(null, "Select") == JFileChooser.CANCEL_OPTION) {
            return;
        }
        String devFolder = chooser.getSelectedFile().getPath();
        ((DefaultComboBoxModel) comboDevFolders.getModel()).addElement(devFolder);
        comboDevFolders.setSelectedItem(devFolder);
    }//GEN-LAST:event_addDevFolder

    private void actionRemoveDevFolder(ActionEvent evt) {//GEN-FIRST:event_actionRemoveDevFolder
        //TODO Show message that last item cannot be removed.
        if (comboDevFolders.getModel().getSize() == 1) {
            return;
        }
        Object selectedObject = comboDevFolders.getSelectedItem();
        if (selectedObject != null) {
            ((DefaultComboBoxModel) comboDevFolders.getModel()).removeElement(selectedObject);
        }
    }//GEN-LAST:event_actionRemoveDevFolder

    private void actionAddCategory(ActionEvent evt) {//GEN-FIRST:event_actionAddCategory
        showAddInputDialog("Add New Category", "New Category", comboCategories);
    }//GEN-LAST:event_actionAddCategory

    private void actionRemoveCategory(ActionEvent evt) {//GEN-FIRST:event_actionRemoveCategory
        //TODO Show message that last item cannot be removed.
        if (comboCategories.getModel().getSize() == 1) {
            return;
        }
        Object selectedObject = comboCategories.getSelectedItem();
        if (selectedObject != null) {
            ((DefaultComboBoxModel) comboCategories.getModel()).removeElement(selectedObject);
        }
    }//GEN-LAST:event_actionRemoveCategory

    private void actionAddPackage(ActionEvent evt) {//GEN-FIRST:event_actionAddPackage
        showAddInputDialog("Add Base Package", "New Base Package", comboBases);
    }//GEN-LAST:event_actionAddPackage

    private void actionRemovePackage(ActionEvent evt) {//GEN-FIRST:event_actionRemovePackage
        //TODO Show message that last item cannot be removed.
        if (comboBases.getModel().getSize() == 1) {
            return;
        }
        Object selectedObject = comboBases.getSelectedItem();
        if (selectedObject != null) {
            ((DefaultComboBoxModel) comboBases.getModel()).removeElement(selectedObject);
        }
    }//GEN-LAST:event_actionRemovePackage

    private void actionAddIO(ActionEvent evt) {//GEN-FIRST:event_actionAddIO
        showAddInputDialog("Add Input / Output Class", "New Input / Output Class", comboIOclasses);
    }//GEN-LAST:event_actionAddIO

    private void actionRemoveIO(ActionEvent evt) {//GEN-FIRST:event_actionRemoveIO
        //TODO Show message that last item cannot be removed.
        if (comboIOclasses.getModel().getSize() == 1) {
            return;
        }
        Object selectedObject = comboIOclasses.getSelectedItem();
        if (selectedObject != null) {
            ((DefaultComboBoxModel) comboIOclasses.getModel()).removeElement(selectedObject);
        }
    }//GEN-LAST:event_actionRemoveIO


    private void showAddInputDialog(String title, String label, JComboBox combo) {
        NotifyDescriptor.InputLine d = new NotifyDescriptor.InputLine(label, title);
        if (DialogDisplayer.getDefault().notify(d) != NotifyDescriptor.OK_OPTION) {
            return;
        }

        String input = d.getInputText();
        if (input == null || input.trim().length() == 0) {
            return;
        }

        DefaultComboBoxModel model = (DefaultComboBoxModel) combo.getModel();
        if (model.getIndexOf(input) != -1) {
            return;
        }

        model.addElement(input);
        model.setSelectedItem(input);
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JComboBox comboBases;
    private JComboBox comboCategories;
    private JComboBox comboDevFolders;
    private JComboBox comboIOclasses;
    private JTextField fieldAuthor;
    // End of variables declaration//GEN-END:variables
}
