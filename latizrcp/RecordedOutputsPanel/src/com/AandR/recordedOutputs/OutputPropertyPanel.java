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
package com.AandR.recordedOutputs;

import com.AandR.library.gui.DoubleValueDocumentListener;
import com.AandR.palette.plugin.PluginKey;
import com.AandR.recordedOutputs.nodes.OutputDataObject;
import com.AandR.recordedOutputs.nodes.PluginObject;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SwingConstants;
import org.openide.windows.IOProvider;

/**
 *
 * @author rstjohn
 * Created on Aug 28, 2009, 4:53:42 PM
 */
public class OutputPropertyPanel extends javax.swing.JPanel {
    public static final int CARD_OUTPUT = 1;
    public static final int CARD_EMPTY = 2;
    public static final int CARD_PLUGIN = 3;

    private OutputDataObject outputDataObject;

    public OutputPropertyPanel() {
        initComponents();
        decorateFields();
        setSaveOptionsEnabled(false);
    }

    public void setOutputDataObject(OutputDataObject outputDataObject) {
        this.outputDataObject = outputDataObject;
    }

    void fireNodeSelectionWillChange() {
        if(outputDataObject==null) return;
        outputDataObject.setBeginTime(new Double(beginTimeField.getText()));
    }

    public void showCard(int card) {
        String cardName = "EMPTY_PANEL";
        switch (card) {
            case CARD_OUTPUT:
                cardName = "OUTPUT_CARD";
                break;
            case CARD_EMPTY:
                cardName = "EMPTY_PANEL";
                break;
            case CARD_PLUGIN:
                cardName = "PLUGIN_CARD";
                break;
        }
        ((CardLayout)getLayout()).show(this, cardName);
    }
    
    public void updatePluginProperties(PluginObject pluginObject) {
        PluginKey key = pluginObject.getPlugin().getPluginKey();
        IOProvider.getDefault().getIO("Output", false).getOut().println(key);
        authorLabel.setText(key.getAuthor());
        dateLabel.setText(key.getDate());
        classLabel.setText(pluginObject.getPlugin().getClass().getName());
        nameLabel.setText(pluginObject.getPlugin().getName());
    }

    public void updateProperty() {
        beginTimeField.setText(outputDataObject.getBeginTime().toString());
        endTimeField.setText(outputDataObject.getEndTime().toString());
        periodField.setText(outputDataObject.getPeriod().toString());
        maxItField.setText(outputDataObject.getMaxIterationCount().toString());
    }

    private final void setSaveOptionsEnabled(boolean enabled) {
        Color bg = enabled ? Color.WHITE : new Color(240, 240, 240);
        beginTimeLabel.setEnabled(enabled);
        beginTimeField.setEnabled(enabled);
        beginTimeField.setBackground(bg);
        beginUnitsLabel.setEnabled(enabled);

        endTimeLabel.setEnabled(enabled);
        endTimeField.setEnabled(enabled);
        endTimeField.setBackground(bg);
        endUnitsLabel.setEnabled(enabled);

        periodLabel.setEnabled(enabled);
        periodField.setEnabled(enabled);
        periodField.setBackground(bg);
        periodUnitsLabel.setEnabled(enabled);

        maxItLabel.setEnabled(enabled);
        maxItField.setEnabled(enabled);
        maxItField.setBackground(bg);
        maxItUnitsLabel.setEnabled(enabled);
    }

    // <editor-fold defaultstate="collapsed" desc="JTextField decorated with document listeners">
    private void decorateFields() {
        new DoubleValueDocumentListener(beginTimeField) {

            @Override
            public void focusLost(FocusEvent e) {
                super.focusLost(e);
                updateBeginTime();
            }

            @Override
            public void keyTyped(KeyEvent e) {
                super.keyTyped(e);
                updateBeginTime();
            }
        };

        new DoubleValueDocumentListener(endTimeField) {

            @Override
            public void focusLost(FocusEvent e) {
                super.focusLost(e);
                updateEndTime();
            }

            @Override
            public void keyTyped(KeyEvent e) {
                super.keyTyped(e);
                updateEndTime();
            }
        };

        new DoubleValueDocumentListener(periodField) {

            @Override
            public void focusLost(FocusEvent e) {
                super.focusLost(e);
                updatePeriod();
            }

            @Override
            public void keyTyped(KeyEvent e) {
                super.keyTyped(e);
                updatePeriod();
            }
        };

        new DoubleValueDocumentListener(maxItField) {

            @Override
            public void focusLost(FocusEvent e) {
                super.focusLost(e);
                updateMaxIterationField();
            }

            @Override
            public void keyTyped(KeyEvent e) {
                super.keyTyped(e);
                updateMaxIterationField();
            }
        };

    }// </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Update GUI methods">
    private void updateBeginTime() {
        try {
            outputDataObject.setBeginTime(new Double(beginTimeField.getText().trim()));
        } catch (NumberFormatException nfe) {}
    }

    private void updateEndTime() {
        try {
            outputDataObject.setEndTime(new Double(endTimeField.getText().trim()));
        } catch (NumberFormatException nfe) {}
    }

    private void updatePeriod() {
        try {
            outputDataObject.setPeriod(new Double(periodField.getText().trim()));
        } catch (NumberFormatException nfe) {}
    }

    private void updateMaxIterationField() {
        try {
            outputDataObject.setMaxIterationCount(new Integer(maxItField.getText().trim()));
        } catch (NumberFormatException nfe) {}
    } //</editor-fold>

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        ButtonGroup buttonGroup1 = new ButtonGroup();
        jLabel10 = new JLabel();
        propertyPanel = new JPanel();
        whenUpdatedRadio = new JRadioButton();
        userDefinedRadio = new JRadioButton();
        beginTimeLabel = new JLabel();
        endTimeLabel = new JLabel();
        periodLabel = new JLabel();
        maxItLabel = new JLabel();
        beginTimeField = new JTextField();
        endTimeField = new JTextField();
        periodField = new JTextField();
        maxItField = new JTextField();
        beginUnitsLabel = new JLabel();
        endUnitsLabel = new JLabel();
        periodUnitsLabel = new JLabel();
        maxItUnitsLabel = new JLabel();
        jLabel6 = new JLabel();
        jSeparator2 = new JSeparator();
        emptyPanel = new JPanel();
        pluginPropertyPanel = new JPanel();
        jLabel1 = new JLabel();
        jSeparator1 = new JSeparator();
        JLabel jLabel2 = new JLabel();
        JLabel jLabel3 = new JLabel();
        JLabel jLabel4 = new JLabel();
        JLabel jLabel5 = new JLabel();
        authorLabel = new JLabel();
        dateLabel = new JLabel();
        classLabel = new JLabel();
        nameLabel = new JLabel();

        jLabel10.setText("jLabel10");

        setLayout(new CardLayout());

        buttonGroup1.add(whenUpdatedRadio);
        whenUpdatedRadio.setSelected(true);
        whenUpdatedRadio.setText("When Updated");
        whenUpdatedRadio.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                actionWhenUpdated(evt);
            }
        });

        buttonGroup1.add(userDefinedRadio);
        userDefinedRadio.setText("User Defined");
        userDefinedRadio.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                actionUserDefined(evt);
            }
        });

        beginTimeLabel.setText("Begin Time");

        endTimeLabel.setText("End Time");

        periodLabel.setText("Period");

        maxItLabel.setText("Do Not Exceed");

        beginTimeField.setMaximumSize(new Dimension(200, 200));

        endTimeField.setMaximumSize(new Dimension(200, 200));

        periodField.setMaximumSize(new Dimension(200, 200));

        maxItField.setMaximumSize(new Dimension(200, 200));

        beginUnitsLabel.setText("sec");

        endUnitsLabel.setText("sec");

        periodUnitsLabel.setText("sec");

        maxItUnitsLabel.setText("frames");

        jLabel6.setFont(new Font("Tahoma", 1, 12));
        jLabel6.setText("Save Options");

        GroupLayout propertyPanelLayout = new GroupLayout(propertyPanel);
        propertyPanel.setLayout(propertyPanelLayout);
        propertyPanelLayout.setHorizontalGroup(
            propertyPanelLayout.createParallelGroup(Alignment.LEADING)
            .addGroup(propertyPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(propertyPanelLayout.createParallelGroup(Alignment.LEADING)
                    .addGroup(propertyPanelLayout.createSequentialGroup()
                        .addGroup(propertyPanelLayout.createParallelGroup(Alignment.LEADING)
                            .addComponent(whenUpdatedRadio)
                            .addComponent(userDefinedRadio))
                        .addContainerGap())
                    .addGroup(propertyPanelLayout.createSequentialGroup()
                        .addComponent(jSeparator2, GroupLayout.DEFAULT_SIZE, 183, Short.MAX_VALUE)
                        .addContainerGap())
                    .addGroup(propertyPanelLayout.createSequentialGroup()
                        .addComponent(jLabel6)
                        .addContainerGap(112, Short.MAX_VALUE))
                    .addGroup(propertyPanelLayout.createSequentialGroup()
                        .addGroup(propertyPanelLayout.createParallelGroup(Alignment.LEADING)
                            .addGroup(propertyPanelLayout.createSequentialGroup()
                                .addComponent(beginTimeLabel)
                                .addPreferredGap(ComponentPlacement.RELATED)
                                .addComponent(beginTimeField, GroupLayout.DEFAULT_SIZE, 58, Short.MAX_VALUE)
                                .addPreferredGap(ComponentPlacement.RELATED)
                                .addComponent(beginUnitsLabel)
                                .addGap(17, 17, 17))
                            .addGroup(propertyPanelLayout.createSequentialGroup()
                                .addComponent(endTimeLabel)
                                .addPreferredGap(ComponentPlacement.RELATED)
                                .addComponent(endTimeField, GroupLayout.DEFAULT_SIZE, 58, Short.MAX_VALUE)
                                .addPreferredGap(ComponentPlacement.RELATED)
                                .addComponent(endUnitsLabel)
                                .addGap(17, 17, 17))
                            .addGroup(propertyPanelLayout.createSequentialGroup()
                                .addComponent(periodLabel)
                                .addPreferredGap(ComponentPlacement.RELATED)
                                .addComponent(periodField, GroupLayout.DEFAULT_SIZE, 58, Short.MAX_VALUE)
                                .addPreferredGap(ComponentPlacement.RELATED)
                                .addComponent(periodUnitsLabel)
                                .addGap(17, 17, 17))
                            .addGroup(propertyPanelLayout.createSequentialGroup()
                                .addComponent(maxItLabel)
                                .addPreferredGap(ComponentPlacement.RELATED)
                                .addComponent(maxItField, GroupLayout.DEFAULT_SIZE, 58, Short.MAX_VALUE)
                                .addPreferredGap(ComponentPlacement.RELATED)
                                .addComponent(maxItUnitsLabel)))
                        .addGap(23, 23, 23))))
        );

        propertyPanelLayout.linkSize(SwingConstants.HORIZONTAL, new Component[] {beginTimeLabel, endTimeLabel, maxItLabel, periodLabel});

        propertyPanelLayout.setVerticalGroup(
            propertyPanelLayout.createParallelGroup(Alignment.LEADING)
            .addGroup(propertyPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel6)
                .addPreferredGap(ComponentPlacement.RELATED)
                .addComponent(jSeparator2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(ComponentPlacement.RELATED)
                .addComponent(whenUpdatedRadio)
                .addPreferredGap(ComponentPlacement.RELATED)
                .addComponent(userDefinedRadio)
                .addPreferredGap(ComponentPlacement.UNRELATED)
                .addGroup(propertyPanelLayout.createParallelGroup(Alignment.BASELINE)
                    .addComponent(beginTimeLabel)
                    .addComponent(beginTimeField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(beginUnitsLabel))
                .addPreferredGap(ComponentPlacement.RELATED)
                .addGroup(propertyPanelLayout.createParallelGroup(Alignment.BASELINE)
                    .addComponent(endTimeLabel)
                    .addComponent(endTimeField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(endUnitsLabel))
                .addPreferredGap(ComponentPlacement.RELATED)
                .addGroup(propertyPanelLayout.createParallelGroup(Alignment.BASELINE)
                    .addComponent(periodLabel)
                    .addComponent(periodField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(periodUnitsLabel))
                .addPreferredGap(ComponentPlacement.RELATED)
                .addGroup(propertyPanelLayout.createParallelGroup(Alignment.BASELINE)
                    .addComponent(maxItLabel)
                    .addComponent(maxItField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(maxItUnitsLabel))
                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        add(propertyPanel, "OUTPUT_CARD");

        GroupLayout emptyPanelLayout = new GroupLayout(emptyPanel);
        emptyPanel.setLayout(emptyPanelLayout);
        emptyPanelLayout.setHorizontalGroup(
            emptyPanelLayout.createParallelGroup(Alignment.LEADING)
            .addGap(0, 203, Short.MAX_VALUE)
        );
        emptyPanelLayout.setVerticalGroup(
            emptyPanelLayout.createParallelGroup(Alignment.LEADING)
            .addGap(0, 189, Short.MAX_VALUE)
        );

        add(emptyPanel, "EMPTY_PANEL");

        jLabel1.setFont(new Font("Tahoma", 1, 12));
        jLabel1.setText("Plugin Properties");

        jLabel2.setText("Author:");

        jLabel3.setText("Date:");

        jLabel4.setText("Class:");

        jLabel5.setText("Name:");

        authorLabel.setText("  ");

        dateLabel.setText("  ");

        classLabel.setText("  ");

        nameLabel.setText("  ");

        GroupLayout pluginPropertyPanelLayout = new GroupLayout(pluginPropertyPanel);
        pluginPropertyPanel.setLayout(pluginPropertyPanelLayout);
        pluginPropertyPanelLayout.setHorizontalGroup(
            pluginPropertyPanelLayout.createParallelGroup(Alignment.LEADING)
            .addGroup(pluginPropertyPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pluginPropertyPanelLayout.createParallelGroup(Alignment.LEADING)
                    .addComponent(jSeparator1, GroupLayout.DEFAULT_SIZE, 183, Short.MAX_VALUE)
                    .addComponent(jLabel1)
                    .addGroup(pluginPropertyPanelLayout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(ComponentPlacement.RELATED)
                        .addComponent(authorLabel, GroupLayout.DEFAULT_SIZE, 140, Short.MAX_VALUE))
                    .addGroup(pluginPropertyPanelLayout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addPreferredGap(ComponentPlacement.RELATED)
                        .addComponent(dateLabel, GroupLayout.DEFAULT_SIZE, 140, Short.MAX_VALUE))
                    .addGroup(pluginPropertyPanelLayout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addPreferredGap(ComponentPlacement.RELATED)
                        .addComponent(classLabel, GroupLayout.DEFAULT_SIZE, 140, Short.MAX_VALUE))
                    .addGroup(pluginPropertyPanelLayout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addPreferredGap(ComponentPlacement.RELATED)
                        .addComponent(nameLabel, GroupLayout.DEFAULT_SIZE, 140, Short.MAX_VALUE)))
                .addContainerGap())
        );

        pluginPropertyPanelLayout.linkSize(SwingConstants.HORIZONTAL, new Component[] {jLabel2, jLabel3, jLabel4, jLabel5});

        pluginPropertyPanelLayout.setVerticalGroup(
            pluginPropertyPanelLayout.createParallelGroup(Alignment.LEADING)
            .addGroup(pluginPropertyPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(ComponentPlacement.RELATED)
                .addComponent(jSeparator1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(ComponentPlacement.RELATED)
                .addGroup(pluginPropertyPanelLayout.createParallelGroup(Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(authorLabel))
                .addPreferredGap(ComponentPlacement.RELATED)
                .addGroup(pluginPropertyPanelLayout.createParallelGroup(Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(dateLabel))
                .addPreferredGap(ComponentPlacement.RELATED)
                .addGroup(pluginPropertyPanelLayout.createParallelGroup(Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(classLabel))
                .addPreferredGap(ComponentPlacement.RELATED)
                .addGroup(pluginPropertyPanelLayout.createParallelGroup(Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(nameLabel))
                .addContainerGap(75, Short.MAX_VALUE))
        );

        add(pluginPropertyPanel, "PLUGIN_CARD");
    }// </editor-fold>//GEN-END:initComponents

    private void actionWhenUpdated(ActionEvent evt) {//GEN-FIRST:event_actionWhenUpdated
        setSaveOptionsEnabled(false);
    }//GEN-LAST:event_actionWhenUpdated

    private void actionUserDefined(ActionEvent evt) {//GEN-FIRST:event_actionUserDefined
        setSaveOptionsEnabled(true);
    }//GEN-LAST:event_actionUserDefined

    //<editor-fold defaultstate="collapsed" desc="Variable declarations">
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JLabel authorLabel;
    private JTextField beginTimeField;
    private JLabel beginTimeLabel;
    private JLabel beginUnitsLabel;
    private JLabel classLabel;
    private JLabel dateLabel;
    private JPanel emptyPanel;
    private JTextField endTimeField;
    private JLabel endTimeLabel;
    private JLabel endUnitsLabel;
    private JLabel jLabel1;
    private JLabel jLabel10;
    private JLabel jLabel6;
    private JSeparator jSeparator1;
    private JSeparator jSeparator2;
    private JTextField maxItField;
    private JLabel maxItLabel;
    private JLabel maxItUnitsLabel;
    private JLabel nameLabel;
    private JTextField periodField;
    private JLabel periodLabel;
    private JLabel periodUnitsLabel;
    private JPanel pluginPropertyPanel;
    private JPanel propertyPanel;
    private JRadioButton userDefinedRadio;
    private JRadioButton whenUpdatedRadio;
    // End of variables declaration//GEN-END:variables
    //</editor-fold>
}
