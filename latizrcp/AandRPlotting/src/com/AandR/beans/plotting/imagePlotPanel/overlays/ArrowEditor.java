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
package com.AandR.beans.plotting.imagePlotPanel.overlays;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import javax.swing.colorchooser.AbstractColorChooserPanel;

/**
 * @author Dr. Richard St. John
 * @version $Revision: 1.3 $, $Date: 2007/09/15 15:57:08 $
 */
public class ArrowEditor extends JDialog implements ActionListener {
  
  private boolean cancelled;
  
  private int rotation,  xLoc, yLoc;
  
  private double lineLength, arrowLength, arrowWidth, penWidth;
  
  private JColorChooser colorChooser;
  
  private JComboBox comboRotation;
  
  private JRadioButton radioRectangle, radioEllipse;
  
  private JTextField fieldPenWidth, fieldLineLength, fieldUpperLeftX, fieldUpperLeftY;

  private JTextField fieldArrowLength, fieldArrowWidth;
  
  public ArrowEditor() {
    super(new Frame(), "Line / Arrow Editor", true);
    initialize();
    setContentPane(createContentPane());
    pack();
  }
  
  private void initialize() {
    colorChooser = new JColorChooser();
    colorChooser.setColor(Color.BLACK);
    
    cancelled = true;
    radioRectangle = createRadioButton("Rectangle", false);
    radioEllipse = createRadioButton("Ellipse", false);
    fieldUpperLeftX = new JTextField(6);
    fieldUpperLeftY = new JTextField(6);
    fieldLineLength = new JTextField(6);
    fieldPenWidth = new JTextField("1",6);
    
    DefaultComboBoxModel listModel = new DefaultComboBoxModel(new String[] {"0", "45", "90", "135", "180", "225", "270", "315"});
    comboRotation = new JComboBox(listModel);
    comboRotation.setFont(comboRotation.getFont().deriveFont(10.0f));
    comboRotation.setPreferredSize(new Dimension(70,24));
    comboRotation.setEditable(true);
    
    ButtonGroup shapeGroup = new ButtonGroup();
    shapeGroup.add(radioRectangle);
    shapeGroup.add(radioEllipse);
  }

  private Container createContentPane() {
    
    JPanel northPanel = new JPanel();
    northPanel.setLayout(new BoxLayout(northPanel, BoxLayout.Y_AXIS));
    northPanel.add(createTopPanel());
    northPanel.add(createMiddlePanel());
    
    JTabbedPane colorTabs = new JTabbedPane();
    colorTabs.add("Colors", findPanel(colorChooser, "javax.swing.colorchooser.DefaultHSBChooserPanel"));
    colorTabs.add("Swatches", findPanel(colorChooser, "javax.swing.colorchooser.DefaultSwatchChooserPanel"));
    colorTabs.add("Channels", findPanel(colorChooser, "javax.swing.colorchooser.DefaultRGBChooserPanel"));
    
    JPanel colorPanel = new JPanel();
    colorPanel.setBorder(BorderFactory.createTitledBorder("Choose Color"));
    colorPanel.add(colorTabs);
    
    JPanel centerPanel = new JPanel(new BorderLayout());
    centerPanel.add(colorPanel, BorderLayout.CENTER);
    
    JPanel panel = new JPanel(new BorderLayout());
    panel.add(northPanel, BorderLayout.NORTH);
    panel.add(centerPanel, BorderLayout.CENTER);
    panel.add(createButtonPanel(), BorderLayout.SOUTH);
    return panel;
  }
  
  private JRadioButton createRadioButton(String label, boolean isSelected) {
    JRadioButton button = new JRadioButton(label, isSelected);
    button.setFont(button.getFont().deriveFont(10.0f));
    return button;
  }
  
  private JLabel createLabel(String label) {
    JLabel jlabel = new JLabel(label);
    jlabel.setFont(jlabel.getFont().deriveFont(10.0f));
    return jlabel;
  }
  
  public static JButton createButton(String label, ActionListener al, String actionCommand) {
    JButton button = new JButton(label);
    button.addActionListener(al);
    button.setActionCommand(actionCommand);
    return button;
  }
  
  private Component createTopPanel() {
    JPanel panel = new JPanel(new GridLayout(1,2));
    panel.add(createLocationPanel());
    panel.add(createStrokePanel());
    return panel;
  }
  
  private Component createStrokePanel() {
    JPanel panelStroke = new JPanel(new FlowLayout(FlowLayout.LEFT));
    TitledBorder titledBorder = new TitledBorder("Pen Stroke");
    titledBorder.setTitleFont(titledBorder.getTitleFont().deriveFont(10.0f));
    panelStroke.setBorder(titledBorder);
    
    panelStroke.add(createLabel("Pen Width (pixels)"));
    panelStroke.add(fieldPenWidth);
    return panelStroke;
  }
  
  private Component createLocationPanel() {
    JPanel panelField = new JPanel();
    panelField.add(fieldUpperLeftX);
    panelField.add(fieldUpperLeftY);
    
    JPanel panelLabel = new JPanel();
    panelLabel.add(createLabel("Specify the Upper Left Corner"));
    
    JPanel panel = new JPanel(new GridLayout(2,1));
    TitledBorder titledBorder = new TitledBorder("Location");
    titledBorder.setTitleFont(titledBorder.getTitleFont().deriveFont(10.0f));
    panel.setBorder(titledBorder);
    panel.add(panelLabel);
    panel.add(panelField);
    return panel;
  }
  
  private Component createMiddlePanel() {
    JPanel panel = new JPanel(new GridLayout(1,2));
    panel.add(createArrowPanel());
    panel.add(createLinePanel());
    return panel;
  }
  
  private Component createArrowPanel() {
    fieldArrowLength = new JTextField(6);
    fieldArrowWidth = new JTextField(6);

    JPanel arrowLengthPanel = new JPanel();
    arrowLengthPanel.add(createLabel("Length: "));
    arrowLengthPanel.add(fieldArrowLength);
    
    JPanel arrowWidthPanel = new JPanel();
    arrowWidthPanel.add(createLabel("Width: "));
    arrowWidthPanel.add(fieldArrowWidth);
    
    JPanel panel = new JPanel(new GridLayout(2,1));
    panel.setBorder(BorderFactory.createTitledBorder("Arrow Properties"));
    panel.add(arrowLengthPanel);
    panel.add(arrowWidthPanel);
    return panel;
  }
  
  private Component createLinePanel() {
    JPanel panelRotation = new JPanel();
    panelRotation.add(createLabel("Rotation: "));
    panelRotation.add(comboRotation);
    
    JPanel panelLength = new JPanel();
    panelLength.add(createLabel("Length: "));
    panelLength.add(fieldLineLength);
    
    JPanel panel = new JPanel(new GridLayout(2,1));
    panel.setBorder(BorderFactory.createTitledBorder("Line Properties"));
    panel.add(panelLength);
    panel.add(panelRotation);
    return panel;
  }
  
  private Component createButtonPanel() {
    JPanel panel = new JPanel();
    panel.add(createButton("Accept", this, "OK"));
    panel.add(createButton("Cancel", this, "CANCEL"));
    return panel;
  }

  public void actionPerformed(ActionEvent e) {
    String command = e.getActionCommand();
    if(command.equalsIgnoreCase("OK")) {
      actionAccept();
      setVisible(false);
      cancelled = false;
    } else if(command.equalsIgnoreCase("CANCEL")) {
      setVisible(false);
      cancelled = true;
    }
  }
  
  private void actionAccept() {
    rotation = -Integer.parseInt(comboRotation.getSelectedItem().toString());
    penWidth = Double.parseDouble(fieldPenWidth.getText());
    lineLength = Double.parseDouble(fieldLineLength.getText());
    arrowLength = Double.parseDouble(fieldArrowLength.getText());
    arrowWidth = Double.parseDouble(fieldArrowWidth.getText());
  }

  private AbstractColorChooserPanel findPanel(JColorChooser chooser, String name) {
    AbstractColorChooserPanel[] panels = chooser.getChooserPanels();

    for (int i=0; i<panels.length; i++) {
      String clsName = panels[i].getClass().getName();

      if (clsName.equals(name)) {
        return panels[i];
      }
    }
    return null;
  }
  
  
  public void setSelectedShape(int shape) {
    if(shape==BasicShape.ELLIPSE)
      radioEllipse.setSelected(true);
    else
      radioRectangle.setSelected(true);
  }
  
  
  public Color getSelectedColor() {
    return colorChooser.getColor();
  }
  
  
  public int getSelectedShape() {
    if(radioEllipse.isSelected())
      return BasicShape.ELLIPSE;
    return BasicShape.RECTANGLE;
  }
  
  
  public boolean isCancelled() {
    return cancelled;
  }

  
  public double getLineLength() {
    return lineLength;
  }

  
  public int getRotation() {
    return rotation;
  }

  
  public void setRotation(int rotation) {
    this.rotation = rotation;
    comboRotation.setSelectedItem(String.valueOf(this.rotation));
  }

  
  public int getXLoc() {
    return xLoc;
  }

  
  public void setXLoc(int loc) {
    xLoc = loc;
  }

  
  public int getYLoc() {
    return yLoc;
  }

  
  public void setYLoc(int loc) {
    yLoc = loc;
  }

  
  public double getPenWidth() {
    return penWidth;
  }

  
  public void setPenWidth(double penWidth) {
    this.penWidth = penWidth;
    fieldPenWidth.setText(String.valueOf(this.penWidth));
  }

  
  public double getArrowLength() {
    return arrowLength;
  }

  
  public double getArrowWidth() {
    return arrowWidth;
  }

  
  public void setArrowLength(double arrowLength) {
    this.arrowLength = arrowLength;
    fieldArrowLength.setText(String.valueOf(this.arrowLength));
  }

  
  public void setArrowWidth(double arrowWidth) {
    this.arrowWidth = arrowWidth;
    fieldArrowWidth.setText(String.valueOf(this.arrowWidth));
  }

  
  public void setLineLength(double lineLength) {
    this.lineLength = lineLength;
    fieldLineLength.setText(String.valueOf(this.lineLength));
  }
}
