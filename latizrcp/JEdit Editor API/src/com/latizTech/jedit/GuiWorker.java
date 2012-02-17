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

import com.AandR.library.gui.JButtonWithDropSupport;
import java.awt.Dimension;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;


/**
 * @author Dr. Richard St. John
 * @version $Revision: 1.2 $, $Date: 2007/09/15 02:34:01 $
 */
public class GuiWorker {
  
/**
 * Create a JButton
 * @param icon
 * @param al
 * @param actionCommand
 * @param toolTip
 * @return
 */  
  public static JButton createToolbarButton(ImageIcon icon, ActionListener al, String actionCommand, String toolTip) {
    JButton button = new JButton(icon);
    button.setToolTipText(toolTip);
    button.addActionListener(al);
    button.setActionCommand(actionCommand);
    button.setPreferredSize(new Dimension(29,29));
    return button;
  }
  
  /**
   * Create a JButton
   * @param icon
   * @param al
   * @param actionCommand
   * @param toolTip
   * @return
   */  
  public static JButtonWithDropSupport createToolbarButtonWithDropSupport(ImageIcon icon, ActionListener al, String actionCommand, String toolTip) {
    JButtonWithDropSupport button = new JButtonWithDropSupport(icon);
    button.setToolTipText(toolTip);
    button.addActionListener(al);
    button.setActionCommand(actionCommand);
    button.setPreferredSize(new Dimension(29,29));
    return button;
  }
}
