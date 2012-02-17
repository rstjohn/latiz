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
package com.AandR.library.gui;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;


/**
 * @author Dr. Richard St. John
 * @version $Revision: 1.4 $, $Date: 2007/09/15 15:57:06 $
 */
public class FontChooser extends JDialog implements ActionListener {
  
  private boolean cancelled = false;
  
  private FontChooserPanel fontChooserPanel;

  public FontChooser(Font font) {
    super(new Frame(), "Font Chooser", true);
    cancelled = false;
    fontChooserPanel = new FontChooserPanel(font);
    setContentPane(createContent(font));
    pack();
    setVisible(true);
  }
  
  /**
   * Returns the selected font.
   *
   * @return the font.
   */
  public Font getSelectedFont() {
      return this.fontChooserPanel.getSelectedFont();
  }

  /**
   * Returns the panel that is the user interface.
   *
   * @param font  the font.
   *
   * @return the panel.
   */
  private JPanel createContent(Font font) {
      final JPanel content = new JPanel(new BorderLayout());
      content.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
      if (font == null) {
          font = new Font("Dialog", 10, Font.PLAIN);
      }
      content.add(this.fontChooserPanel);

      final JPanel buttons = createButtonPanel();
      buttons.setBorder(BorderFactory.createEmptyBorder(4, 0, 0, 0));
      content.add(buttons, BorderLayout.SOUTH);

      return content;
  }

  
  public boolean isCancelled() {
    return cancelled;
  }

  /**
   * Builds and returns the user interface for the dialog.  This method is shared among the
   * constructors.
   *
   * @return the button panel.
   */
  protected JPanel createButtonPanel() {
    
    JButton okButton = new JButton("OK");
    okButton.setActionCommand("okButton");
    okButton.addActionListener(this);
    
    JButton cancelButton = new JButton("Cancel");
    cancelButton.setActionCommand("cancelButton");
    cancelButton.addActionListener(this);

    JPanel rightButtonPanel = new JPanel(new GridLayout(1, 2, 3, 3));
    rightButtonPanel.add(okButton);
    rightButtonPanel.add(cancelButton);
    
    JPanel buttonPanel = new JPanel(new BorderLayout());
    buttonPanel.setBorder(BorderFactory.createEmptyBorder(4, 4, 2, 2));
    buttonPanel.add(fontChooserPanel.getPreviewLabel());
    buttonPanel.add(rightButtonPanel, BorderLayout.EAST);
    return buttonPanel;
  }
  
  public void scrollToFont() {
    
  }
  

  /**
   * Handles clicks on the standard buttons.
   *
   * @param event  the event.
   */
  public void actionPerformed(final ActionEvent event) {
    final String command = event.getActionCommand();
    if (command.equals("okButton")) {
      cancelled = false;
      setVisible(false);
    } else if (command.equals("cancelButton")) {
      cancelled = true;
      setVisible(false);
    }
  }

  
  public FontChooserPanel getFontChooserPanel() {
    return fontChooserPanel;
  }
  
}
