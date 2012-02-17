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
package com.AandR.palette.paletteScene.menus;

import com.AandR.latizOptions.connectionPanel.ConnectionPanelOptionsController;
import com.AandR.palette.plugin.AbstractPlugin;
import com.AandR.palette.plugin.PluginKey;
import java.awt.Color;
import java.io.IOException;
import java.util.Set;
import java.util.prefs.Preferences;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import net.miginfocom.swing.MigLayout;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.filesystems.Repository;
import org.openide.util.Exceptions;
import org.openide.util.ImageUtilities;
import org.openide.util.NbPreferences;

/**
 *
 * @author rstjohn
 */
public class PropertiesPanel extends javax.swing.JPanel {
    private AbstractPlugin plugin;
    private PluginKey pluginKey;

    /** Creates new form PropertiesPanel */
    public PropertiesPanel(AbstractPlugin plugin) {
        this.plugin = plugin;
        pluginKey = plugin.getPluginKey();
        JPanel ioPanel = new JPanel(new MigLayout("gap 2*unrel rel, flowy"));

        Preferences pref = NbPreferences.forModule(ConnectionPanelOptionsController.class);
        Color targetBackgroundColor = Color.decode(pref.get("targetBackground", ConnectionPanelOptionsController.DEFAULT_TARGET_BACKGROUND));
        Color foregroundColor = Color.decode(pref.get("sourceForeground", ConnectionPanelOptionsController.DEFAULT_FOREGROUND));

        JLabel label;
        Set<String> keys = plugin.getInputDataMap().keySet();
        String constraint;
        int size = keys.size();
        int index = 0;

        JLabel inputsLabel = new JLabel("Inputs");
        inputsLabel.setOpaque(true);
        inputsLabel.setBorder(new CompoundBorder(new LineBorder(Color.DARK_GRAY), new EmptyBorder(5, 5, 5, 5)));
        inputsLabel.setBackground(new Color(230,230,230));
        ioPanel.add(inputsLabel, "h 25!, w 130");
        for(String key : keys) {
            label = new JLabel("<HTML>  " + key + " <I>" + plugin.getInputDataMap().get(key).getValueTypeSimpleName() + "</I></HTML>");
            label.setOpaque(true);
            label.setBorder(new CompoundBorder(new LineBorder(Color.DARK_GRAY), new EmptyBorder(5, 5, 5, 5)));
            label.setForeground(foregroundColor);
            label.setBackground(targetBackgroundColor);
            constraint = "h 25!, w 130";
            if(index==size-1) {
                constraint += ", wrap";
            }
            ioPanel.add(label, constraint);
            index++;
        }

        JLabel outputsLabel = new JLabel("Outputs");
        outputsLabel.setOpaque(true);
        outputsLabel.setBorder(new CompoundBorder(new LineBorder(Color.DARK_GRAY), new EmptyBorder(5, 5, 5, 5)));
        outputsLabel.setBackground(new Color(230,230,230));
        ioPanel.add(outputsLabel, "h 25!, w 130");
        Color sourceBackgroundColor = Color.decode(pref.get("sourceBackground", ConnectionPanelOptionsController.DEFAULT_SOURCE_BACKGROUND));
        for(String key : plugin.getOutputDataMap().keySet()) {
            label = new JLabel("<HTML>  " + key + " <I>" + plugin.getOutputDataMap().get(key).getValueTypeSimpleName() + "</I></HTML>");
            label.setOpaque(true);
            label.setBorder(new CompoundBorder(new LineBorder(Color.DARK_GRAY), new EmptyBorder(5, 5, 5, 5)));
            label.setForeground(foregroundColor);
            label.setBackground(sourceBackgroundColor);
            constraint = "h 25!, w 130";

            ioPanel.add(label, constraint);
        }

        initComponents();
        ioScrollPane.setViewportView(ioPanel);
    }

    Icon getPluginIcon() {
        String path = pluginKey.getIconPath();
        if(path==null) {
            path = "com/AandR/palette/resources/defaultPlugin.png";
        }
        return new ImageIcon(ImageUtilities.loadImage(path));
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        javax.swing.JTabbedPane jTabbedPane1 = new javax.swing.JTabbedPane();
        javax.swing.JPanel generalPanel = new javax.swing.JPanel();
        ioScrollPane = new javax.swing.JScrollPane();
        labelIcon = new javax.swing.JLabel();
        javax.swing.JButton buttonIcon = new javax.swing.JButton();
        javax.swing.JPanel developersPanel = new javax.swing.JPanel();
        javax.swing.JLabel jLabel1 = new javax.swing.JLabel();
        javax.swing.JLabel jLabel3 = new javax.swing.JLabel();
        labelClass = new javax.swing.JLabel();
        javax.swing.JLabel jLabel4 = new javax.swing.JLabel();
        javax.swing.JButton buttonAuthor = new javax.swing.JButton();
        fieldAuthor = new javax.swing.JTextField();
        fieldDate = new javax.swing.JTextField();
        javax.swing.JButton buttonDate = new javax.swing.JButton();

        labelIcon.setIcon(getPluginIcon());
        labelIcon.setText(org.openide.util.NbBundle.getMessage(PropertiesPanel.class, "PropertiesPanel.labelIcon.text")); // NOI18N

        buttonIcon.setText(org.openide.util.NbBundle.getMessage(PropertiesPanel.class, "PropertiesPanel.buttonIcon.text")); // NOI18N
        buttonIcon.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                actionChangeIcon(evt);
            }
        });

        javax.swing.GroupLayout generalPanelLayout = new javax.swing.GroupLayout(generalPanel);
        generalPanel.setLayout(generalPanelLayout);
        generalPanelLayout.setHorizontalGroup(
            generalPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, generalPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(labelIcon, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(buttonIcon)
                .addGap(238, 238, 238))
            .addComponent(ioScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 359, Short.MAX_VALUE)
        );
        generalPanelLayout.setVerticalGroup(
            generalPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, generalPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(generalPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(buttonIcon)
                    .addComponent(labelIcon, javax.swing.GroupLayout.DEFAULT_SIZE, 34, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(ioScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 143, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab(org.openide.util.NbBundle.getMessage(PropertiesPanel.class, "PropertiesPanel.generalPanel.TabConstraints.tabTitle"), new javax.swing.ImageIcon(getClass().getResource("/com/AandR/palette/resources/connect16.png")), generalPanel); // NOI18N

        jLabel1.setText(org.openide.util.NbBundle.getMessage(PropertiesPanel.class, "PropertiesPanel.jLabel1.text")); // NOI18N

        jLabel3.setText(org.openide.util.NbBundle.getMessage(PropertiesPanel.class, "PropertiesPanel.jLabel3.text")); // NOI18N

        labelClass.setText(pluginKey.getClassName());

        jLabel4.setText(org.openide.util.NbBundle.getMessage(PropertiesPanel.class, "PropertiesPanel.jLabel4.text")); // NOI18N

        buttonAuthor.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/AandR/palette/paletteScene/menus/edit.png"))); // NOI18N
        buttonAuthor.setText(org.openide.util.NbBundle.getMessage(PropertiesPanel.class, "PropertiesPanel.buttonAuthor.text")); // NOI18N
        buttonAuthor.setToolTipText(org.openide.util.NbBundle.getMessage(PropertiesPanel.class, "PropertiesPanel.buttonAuthor.toolTipText")); // NOI18N
        buttonAuthor.setBorderPainted(false);
        buttonAuthor.setContentAreaFilled(false);
        buttonAuthor.setFocusPainted(false);
        buttonAuthor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                actionChangeAuthor(evt);
            }
        });

        fieldAuthor.setEditable(false);
        fieldAuthor.setText(pluginKey.getAuthor());

        fieldDate.setEditable(false);
        fieldDate.setText(pluginKey.getDate());

        buttonDate.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/AandR/palette/paletteScene/menus/edit.png"))); // NOI18N
        buttonDate.setText(org.openide.util.NbBundle.getMessage(PropertiesPanel.class, "PropertiesPanel.buttonDate.text")); // NOI18N
        buttonDate.setToolTipText(org.openide.util.NbBundle.getMessage(PropertiesPanel.class, "PropertiesPanel.buttonDate.toolTipText")); // NOI18N
        buttonDate.setBorderPainted(false);
        buttonDate.setContentAreaFilled(false);
        buttonDate.setFocusPainted(false);
        buttonDate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                actionChangeDate(evt);
            }
        });

        javax.swing.GroupLayout developersPanelLayout = new javax.swing.GroupLayout(developersPanel);
        developersPanel.setLayout(developersPanelLayout);
        developersPanelLayout.setHorizontalGroup(
            developersPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(developersPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(developersPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3)
                    .addComponent(jLabel1)
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(developersPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(developersPanelLayout.createSequentialGroup()
                        .addComponent(fieldDate, javax.swing.GroupLayout.DEFAULT_SIZE, 263, Short.MAX_VALUE)
                        .addGap(2, 2, 2))
                    .addComponent(labelClass, javax.swing.GroupLayout.DEFAULT_SIZE, 265, Short.MAX_VALUE)
                    .addGroup(developersPanelLayout.createSequentialGroup()
                        .addComponent(fieldAuthor, javax.swing.GroupLayout.DEFAULT_SIZE, 263, Short.MAX_VALUE)
                        .addGap(2, 2, 2)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(developersPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(buttonAuthor, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(buttonDate, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        developersPanelLayout.setVerticalGroup(
            developersPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(developersPanelLayout.createSequentialGroup()
                .addGroup(developersPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(developersPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(developersPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3)
                            .addComponent(labelClass))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(developersPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1)
                            .addComponent(fieldAuthor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(developersPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(fieldDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel4)))
                    .addGroup(developersPanelLayout.createSequentialGroup()
                        .addGap(31, 31, 31)
                        .addComponent(buttonAuthor, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(buttonDate, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(117, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab(org.openide.util.NbBundle.getMessage(PropertiesPanel.class, "PropertiesPanel.developersPanel.TabConstraints.tabTitle"), new javax.swing.ImageIcon(getClass().getResource("/com/AandR/palette/paletteScene/menus/developer.png")), developersPanel); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 224, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void actionChangeIcon(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_actionChangeIcon
        NotifyDescriptor nd = new NotifyDescriptor.Message("Under development");
        DialogDisplayer.getDefault().notify(nd);
    }//GEN-LAST:event_actionChangeIcon

    private void actionChangeAuthor(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_actionChangeAuthor
        NotifyDescriptor.InputLine nd = new NotifyDescriptor.InputLine("Enter author:", "Change Author Dialog");
        Object ans = DialogDisplayer.getDefault().notify(nd);
        if(ans==NotifyDescriptor.CANCEL_OPTION) {
            return;
        }
        FileObject pluginFileObject = FileUtil.getConfigRoot().getFileObject(pluginKey.getUniqueID());
        try {
            String newAuthor = nd.getInputText();
            pluginFileObject.setAttribute("author", newAuthor);
            pluginKey.setAuthor(newAuthor);
            fieldAuthor.setText(newAuthor);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }//GEN-LAST:event_actionChangeAuthor

    private void actionChangeDate(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_actionChangeDate
        NotifyDescriptor.InputLine nd = new NotifyDescriptor.InputLine("Enter new date:", "Change Date Dialog");
        Object ans = DialogDisplayer.getDefault().notify(nd);
        if(ans==NotifyDescriptor.CANCEL_OPTION) {
            return;
        }
        FileObject pluginFileObject = FileUtil.getConfigRoot().getFileObject(pluginKey.getUniqueID());
        try {
            pluginFileObject.setAttribute("date", nd.getInputText());
            pluginKey.setDate(pluginFileObject.getAttribute("date").toString());
            fieldDate.setText(pluginFileObject.getAttribute("date").toString());
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }//GEN-LAST:event_actionChangeDate


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField fieldAuthor;
    private javax.swing.JTextField fieldDate;
    private javax.swing.JScrollPane ioScrollPane;
    private javax.swing.JLabel labelClass;
    private javax.swing.JLabel labelIcon;
    // End of variables declaration//GEN-END:variables
}
