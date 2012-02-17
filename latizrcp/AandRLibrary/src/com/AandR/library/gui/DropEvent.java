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

import java.awt.Point;
import java.awt.dnd.DropTargetContext;
import java.awt.dnd.DropTargetDropEvent;


/**
 * @author Dr. Richard St. John
 * @version $Revision: 1.1 $, $Date: 2007/05/25 00:12:26 $
 */
public class DropEvent extends DropTargetDropEvent {
  
  private Object item;
  
  /**
   * @param dtc
   * @param cursorLocn
   * @param dropAction
   * @param srcActions
   */
  public DropEvent(Object item, DropTargetContext dtc, Point cursorLocn, int dropAction, int srcActions) {
    super(dtc, cursorLocn, dropAction, srcActions);
    this.item = item;
  }
  
  public Object getDroppedItem() {
    return item;
  }
  
  public int getX() {
    return getLocation().x;
  }
  
  
  public int getY() {
    return getLocation().y;
  }
}
