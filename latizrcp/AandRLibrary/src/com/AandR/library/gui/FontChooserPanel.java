/* ========================================================================
 * JCommon : a free general purpose class library for the Java(tm) platform
 * ========================================================================
 *
 * (C) Copyright 2000-2005, by Object Refinery Limited and Contributors.
 * 
 * Project Info:  http://www.jfree.org/jcommon/index.html
 *
 * This library is free software; you can redistribute it and/or modify it 
 * under the terms of the GNU Lesser General Public License as published by 
 * the Free Software Foundation; either version 2.1 of the License, or 
 * (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but 
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY 
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public 
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, 
 * USA.  
 *
 * [Java is a trademark or registered trademark of Sun Microsystems, Inc. 
 * in the United States and other countries.]
 * 
 * ---------------------
 * FontChooserPanel.java
 * ---------------------
 * (C) Copyright 2000-2004, by Object Refinery Limited.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   Arnaud Lelievre;
 *
 * $Id: FontChooserPanel.java,v 1.2 2007/06/27 14:00:03 stjohnr Exp $
 *
 * Changes (from 26-Oct-2001)
 * --------------------------
 * 26-Oct-2001 : Changed package to com.jrefinery.ui.*;
 * 14-Oct-2002 : Fixed errors reported by Checkstyle (DG);
 * 08-Sep-2003 : Added internationalization via use of properties resourceBundle (RFE 690236) (AL);
 * 21-Feb-2004 : The FontParameter of the constructor was never used (TM);
 */

package com.AandR.library.gui;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * A panel for choosing a font from the available system fonts - still a bit of a hack at the
 * moment, but good enough for demonstration applications.
 *
 * @author David Gilbert
 */
public class FontChooserPanel extends JPanel implements ListSelectionListener, ChangeListener {
  
  /** The font sizes that can be selected. */
  private DefaultListModel sizes, names;
  
  /** The list of fonts. */
  private JList fontlist;
  
  /** The list of sizes. */
  private JList sizelist;
  
  /** The checkbox that indicates whether the font is bold. */
  private JCheckBox bold;
  
  /** The checkbox that indicates whether or not the font is italic. */
  private JCheckBox italic;
  
  private Font selectedFont;
  
  private JLabel previewLabel = new JLabel("ABCDEFGabcdefg0123");
  
  /**
   * Standard constructor - builds a FontChooserPanel initialised with the specified font.
   *
   * @param font  the initial font to display.
   */
  public FontChooserPanel(final Font font) {
    final GraphicsEnvironment g = GraphicsEnvironment.getLocalGraphicsEnvironment();
    final String[] fonts = g.getAvailableFontFamilyNames();
    names = new DefaultListModel();
    for(int i=0; i<fonts.length; i++) {
      names.addElement(fonts[i]);
    }
    
    setLayout(new BorderLayout());
    final JPanel right = new JPanel(new BorderLayout());
    
    final JPanel fontPanel = new JPanel(new BorderLayout());
    fontPanel.setBorder(BorderFactory.createTitledBorder("Font"));
    fontlist = new JList(names);
    fontlist.addListSelectionListener(this);
    final JScrollPane fontpane = new JScrollPane(this.fontlist);
    fontpane.setWheelScrollingEnabled(true);
    fontpane.setBorder(BorderFactory.createEtchedBorder());
    fontPanel.add(fontpane);
    add(fontPanel);
    
    sizes = new DefaultListModel();
    for(int i=3; i<101; i++) {
      sizes.addElement(String.valueOf(i));
    }
    
    final JPanel sizePanel = new JPanel(new BorderLayout());
    sizePanel.setBorder(BorderFactory.createTitledBorder("Size"));
    sizelist = new JList(sizes);
    sizelist.addListSelectionListener(this);
    final JScrollPane sizepane = new JScrollPane(this.sizelist);
    sizepane.setBorder(BorderFactory.createEtchedBorder());
    sizepane.setWheelScrollingEnabled(true);
    sizePanel.add(sizepane);
    
    final JPanel attributes = new JPanel(new GridLayout(1, 2));
    bold = new JCheckBox("Bold");
    bold.addChangeListener(this);
    italic = new JCheckBox("Italic");
    italic.addChangeListener(this);
    attributes.add(this.bold);
    attributes.add(this.italic);
    attributes.setBorder(BorderFactory.createTitledBorder("Attributes"));
    
    right.add(sizePanel, BorderLayout.CENTER);
    right.add(attributes, BorderLayout.SOUTH);
    
    add(right, BorderLayout.EAST);
    
    setSelectedFont(font);
  }
  
  /**
   * Returns a Font object representing the selection in the panel.
   *
   * @return the font.
   */
  public Font getSelectedFont() {
    return new Font(getSelectedName(), getSelectedStyle(), getSelectedSize());
  }
  
  /**
   * Returns the selected name.
   *
   * @return the name.
   */
  public String getSelectedName() {
    return (String) this.fontlist.getSelectedValue();
  }
  
  /**
   * Returns the selected style.
   *
   * @return the style.
   */
  public int getSelectedStyle() {
    if (this.bold.isSelected() && this.italic.isSelected()) {
      return Font.BOLD + Font.ITALIC;
    }
    if (this.bold.isSelected()) {
      return Font.BOLD;
    }
    if (this.italic.isSelected()) {
      return Font.ITALIC;
    }
    else {
      return Font.PLAIN;
    }
  }
  
  /**
   * Returns the selected size.
   *
   * @return the size.
   */
  public int getSelectedSize() {
    final String selected = (String)sizelist.getSelectedValue();
    if (selected != null) {
      return Integer.parseInt(selected);
    }
    else {
      return 10;
    }
  }
  
  /**
   * Initializes the contents of the dialog from the given font
   * object.
   *
   * @param font the font from which to read the properties.
   */
  public void setSelectedFont (final Font font) {
    if (font == null) {
      throw new NullPointerException();
    }
    bold.setSelected(font.isBold());
    italic.setSelected(font.isItalic());
    
    final String fontName = font.getName();
    fontlist.clearSelection();
    for (int i = 0; i < names.size(); i++) {
      if (fontName.equals(names.getElementAt(i))) {
        fontlist.setSelectedIndex(i);
        break;
      }
    }
    fontlist.ensureIndexIsVisible(fontlist.getSelectedIndex());
    
    final String fontSize = String.valueOf(font.getSize());
    sizelist.clearSelection();
    for (int i = 0; i < sizes.size(); i++) {
      if (fontSize.equals(sizes.getElementAt(i).toString())) {
        this.sizelist.setSelectedIndex(i);
        break;
      }
    }
    sizelist.ensureIndexIsVisible(sizelist.getSelectedIndex());
  }
  
  public void valueChanged(ListSelectionEvent e) {
    selectedFont = new Font(getSelectedName(), getSelectedStyle(), getSelectedSize());
    previewLabel.setFont(selectedFont);
  }
  
  
  public JLabel getPreviewLabel() {
    return previewLabel;
  }
  
  public void stateChanged(ChangeEvent e) {
    selectedFont = new Font(getSelectedName(), getSelectedStyle(), getSelectedSize());
    previewLabel.setFont(selectedFont);
  }

  
  public JList getSizelist() {
    return sizelist;
  }
}
