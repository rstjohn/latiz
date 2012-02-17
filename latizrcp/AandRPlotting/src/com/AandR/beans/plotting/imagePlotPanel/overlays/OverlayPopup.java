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

import java.awt.Color;
import java.awt.event.ActionListener;

import javax.swing.Icon;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;


/**
 * @author Dr. Richard St. John
 * @version $Revision: 1.1 $, $Date: 2007/05/25 00:12:31 $
 */
public class OverlayPopup extends JPopupMenu {
  public static final String ACTION_REMOVE_OVERLAY = "REMOVE_OVERLAY";
  public static final String ACTION_EDIT_OVERLAY = "EDIT_OVERLAY";
  public static final String ACTION_CENTER_OVERLAY = "CENTER_OVERLAY";
  
  private ActionListener actionListener;

  public OverlayPopup(ActionListener actionListener) {
    this.actionListener = actionListener;
    createPopupMenu();

  }
  
  private void createPopupMenu() {
    add(createPopupMenuItem("Remove", null, null, actionListener, ACTION_REMOVE_OVERLAY));
    add(createPopupMenuItem("Edit", null, null, actionListener, ACTION_EDIT_OVERLAY));
    addSeparator();
    add(createPopupMenuItem("Center", null, null, actionListener, ACTION_CENTER_OVERLAY));
  }
  
  private JMenuItem createPopupMenuItem(String label, Icon icon, KeyStroke keyStroke, ActionListener al, String actionCommand) {
    JMenuItem menuItem = new JMenuItem(label, icon);
    menuItem.setAccelerator(keyStroke);
    menuItem.addActionListener(al);
    menuItem.setActionCommand(actionCommand);
    menuItem.setBackground(Color.WHITE);
    menuItem.setFont(menuItem.getFont().deriveFont(10.0f));
    menuItem.setEnabled(true);
    return menuItem;
  }
}
