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

import com.AandR.library.gui.FontChooserPanel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.colorchooser.AbstractColorChooserPanel;


/**
 * @author Dr. Richard St. John
 * @version $Revision: 1.3 $, $Date: 2007/09/15 15:57:06 $
 */
public class TextBoxEditor extends JDialog implements ActionListener {
  
  private boolean cancelled = false;
  private FontChooserPanel fontChooserPanel;
  private JColorChooser colorChooser;
  private Font font;
  private String string;
  private JTextField fieldString;
  private static TextBoxEditor instanceOf = new TextBoxEditor();
  
  private TextBoxEditor() {
    super(new Frame(), "Font Chooser", true);
    initialize();
    setContentPane(createContent());
    pack();
  }
  
  private void initialize() {
    cancelled = false;
    fieldString = new JTextField();
    font = new Font("Dialog", Font.PLAIN, 12);
    fontChooserPanel = new FontChooserPanel(font);
    colorChooser = new JColorChooser();
    colorChooser.setColor(Color.BLACK);
  }

  private Container createContent() {
    JPanel panelString = new JPanel();
    panelString.setLayout(new BoxLayout(panelString, BoxLayout.X_AXIS));
    panelString.add(createLabel("Specify Text Overlay:  "));
    panelString.add(fieldString);
    
    JPanel northPanel = new JPanel(new BorderLayout());
    northPanel.add(panelString, BorderLayout.NORTH);
    northPanel.add(fontChooserPanel, BorderLayout.CENTER);
    
    JTabbedPane colorTabs = new JTabbedPane();
    colorTabs.add("Colors", findPanel(colorChooser, "javax.swing.colorchooser.DefaultHSBChooserPanel"));
    colorTabs.add("Swatches", findPanel(colorChooser, "javax.swing.colorchooser.DefaultSwatchChooserPanel"));
    colorTabs.add("Channels", findPanel(colorChooser, "javax.swing.colorchooser.DefaultRGBChooserPanel"));
    
    JPanel colorPanel = new JPanel();
    colorPanel.setBorder(BorderFactory.createTitledBorder("Choose Color"));
    colorPanel.add(colorTabs);
    
    JPanel centerPanel = new JPanel(new BorderLayout());
    centerPanel.add(colorPanel, BorderLayout.CENTER);
    centerPanel.add(fontChooserPanel.getPreviewLabel(), BorderLayout.SOUTH);
    
    JPanel panel = new JPanel(new BorderLayout());
    panel.add(northPanel, BorderLayout.NORTH);
    panel.add(centerPanel, BorderLayout.CENTER);
    panel.add(createButtonPanel(), BorderLayout.SOUTH);
    return panel;
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
  
  private Component createButtonPanel() {
    JPanel panel = new JPanel();
    panel.add(createButton("Accept", this, "OK"));
    panel.add(createButton("Cancel", this, "CANCEL"));
    return panel;
  }

  public void actionPerformed(ActionEvent e) {
    String command = e.getActionCommand();
    if(command.equalsIgnoreCase("OK")) {
      cancelled=false;
      setVisible(false);
    } else if(command.equalsIgnoreCase("CANCEL")) {
      cancelled=true;
      setVisible(false);
    }
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
  
  public Color getSelectedColor() {
    return colorChooser.getColor();
  }
  
  public Font getSelectedFont() {
    return fontChooserPanel.getSelectedFont();
  }

  
  public boolean isCancelled() {
    return cancelled;
  }

  
  public String getString() {
    return string;
  }

  public JTextField getFieldString() {
    return fieldString;
  }
  
  public void setString(String string) {
    this.string = string;
    fieldString.setText(this.string);
  }

  
  public JColorChooser getColorChooser() {
    return colorChooser;
  }

  
  public FontChooserPanel getFontChooserPanel() {
    return fontChooserPanel;
  }

  
  public static TextBoxEditor getInstanceOf() {
    return instanceOf;
  }
}
