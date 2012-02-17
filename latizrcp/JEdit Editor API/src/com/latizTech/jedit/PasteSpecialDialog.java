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
package com.latizTech.jedit;

import com.latizTech.jedit.resources.Resources;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;

/**
 * 
 * @author Dr. Richard St. John
 * @version $Revision: 1.3 $, $Date: 2007/09/15 15:57:13 $
 */  
  public class PasteSpecialDialog extends JDialog {
    private static final String DELIMITER_COMMA = ",";
    private static final String DELIMITER_SEMI_COLON = ";";
    private static final String DELIMITER_SPACE = " ";
    private static final String DELIMITER_TAB = "\t";
    
    private String pasteDelimiter = "";
    private String copyDelimiter = "";
    
    private boolean isCancelled = false;

    public PasteSpecialDialog() {
      super(new JFrame(), "Paste Special", true);
      addWindowListener(new WindowAdapter() {
        public void windowClosing(WindowEvent e) {
          isCancelled = true;
          setVisible(false);
        }
      });
      setContentPane(createContentPane());
      setLocationRelativeTo(null);
      pack();
      Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
      Dimension panelSize = getSize();
      setLocation((int)((screen.getWidth()-panelSize.getWidth())/2.0), (int)((screen.getHeight()-panelSize.getHeight())/2.0));
      setVisible(true);
    }
    
    private JRadioButton createRadioButton(String label, boolean isSelected) {
      JRadioButton button = new JRadioButton(label, isSelected);
      button.setBackground(new Color(236,233,216));
      return button;
    }
    
    private JCheckBox createCheckBox(String label, boolean isSelected) {
      JCheckBox button = new JCheckBox(label, isSelected);
      button.setBackground(new Color(236,233,216));
      return button;
    }

    private Container createContentPane() {
      final JCheckBox checkCommaDelimiter = createCheckBox("Comma", true);
      final JCheckBox checkSemiColonDelimiter = createCheckBox("Semi-colon", true);
      final JCheckBox checkSpaceDelimiter = createCheckBox("Space", true);
      final JCheckBox checkTabDelimiter = createCheckBox("Tab", true);
      JPanel copyDelimiterPanel = new JPanel(new GridLayout(2,2,5,5));
      copyDelimiterPanel.setBorder(BorderFactory.createTitledBorder("Source Delimiter"));
      copyDelimiterPanel.add(checkCommaDelimiter);
      copyDelimiterPanel.add(checkSemiColonDelimiter);
      copyDelimiterPanel.add(checkSpaceDelimiter);
      copyDelimiterPanel.add(checkTabDelimiter);
      
      final JRadioButton radioCommaDelimited = createRadioButton("Comma", false);
      final JRadioButton radioSemiColonDelimited = createRadioButton("Semi-colon", true);
      final JRadioButton radioSpaceDelimited = createRadioButton("Space", false);
      final JRadioButton radioTabDelimited = createRadioButton("Tab", false);
      ButtonGroup radioGroup = new ButtonGroup();
      radioGroup.add(radioCommaDelimited);
      radioGroup.add(radioSemiColonDelimited);
      radioGroup.add(radioSpaceDelimited);
      radioGroup.add(radioTabDelimited);
      
      JPanel pasteDelimiterPanel = new JPanel(new GridLayout(2,2,5,5));
      pasteDelimiterPanel.setBorder(BorderFactory.createTitledBorder("Paste Delimiter"));
      pasteDelimiterPanel.add(radioCommaDelimited);
      pasteDelimiterPanel.add(radioSemiColonDelimited);
      pasteDelimiterPanel.add(radioSpaceDelimited);
      pasteDelimiterPanel.add(radioTabDelimited);
      
      JButton okButton = new JButton("OK");
      okButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          if(radioCommaDelimited.isSelected()) 
            pasteDelimiter = DELIMITER_COMMA;
          else if(radioSemiColonDelimited.isSelected())
            pasteDelimiter = DELIMITER_SEMI_COLON;
          else if(radioSpaceDelimited.isSelected())
            pasteDelimiter = DELIMITER_SPACE;
          else if(radioTabDelimited.isSelected())
            pasteDelimiter = DELIMITER_TAB;
          isCancelled = false;
          
          copyDelimiter = "";
          if(checkCommaDelimiter.isSelected())
            copyDelimiter += ",";
          if(checkSemiColonDelimiter.isSelected())
            copyDelimiter += ";";
          if(checkSpaceDelimiter.isSelected())
            copyDelimiter += " ";
          if(checkTabDelimiter.isSelected())
            copyDelimiter += "\t";
          setVisible(false);
        }
      });
      JButton cancelButton = new JButton("Cancel");
      cancelButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          isCancelled = true;
          setVisible(false);
        }
      });
      
      JPanel northPanel = new JPanel(new BorderLayout());
      northPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
      
      JLabel northLabel = new JLabel("<HTML><B><font size=+2>Paste Special</font></B><BR><BR><left>The <I>Source Delimiter</I> is the delimited for the string located on the clipboard.<BR><BR>The <I>Paste Delimiter</I> is the delimiter for the string to be pasted.</left></html>");
      northLabel.setIcon(Resources.createIcon("editpaste64.png"));
      northLabel.setIconTextGap(20);
      northLabel.setVerticalTextPosition(SwingConstants.TOP);
      northLabel.setOpaque(false);
      northLabel.setPreferredSize(new Dimension(350, 160));
      
      JPanel labelPanel = new JPanel(new BorderLayout());
      labelPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 10, 10));
      labelPanel.setBackground(Color.WHITE);
      labelPanel.add(northLabel);
      
      northPanel.add(labelPanel, BorderLayout.CENTER);
      northPanel.add(new JSeparator(), BorderLayout.SOUTH);
      
      JPanel centerPanel = new JPanel(new GridLayout(2,1,5,5));
      centerPanel.add(copyDelimiterPanel);
      centerPanel.add(pasteDelimiterPanel);
      
      JPanel buttonPanel = new JPanel(new GridLayout(1,2,5,5));
      buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));
      buttonPanel.add(okButton);
      buttonPanel.add(cancelButton);
      
      JPanel southPanel = new JPanel();
      southPanel.add(buttonPanel);
      
      JPanel panel = new JPanel(new BorderLayout());
      panel.add(northPanel, BorderLayout.NORTH);
      panel.add(centerPanel, BorderLayout.CENTER);
      panel.add(southPanel, BorderLayout.SOUTH);
      
      return panel;
    }

    
    public boolean isCancelled() {
      return isCancelled;
    }

    
    public String getCopyDelimiter() {
      return copyDelimiter;
    }

    
    public void setCopyDelimiter(String copyDelimiter) {
      this.copyDelimiter = copyDelimiter;
    }

    
    public String getPasteDelimiter() {
      return pasteDelimiter;
    }

    
    public void setPasteDelimiter(String pasteDelimiter) {
      this.pasteDelimiter = pasteDelimiter;
    }
  }
