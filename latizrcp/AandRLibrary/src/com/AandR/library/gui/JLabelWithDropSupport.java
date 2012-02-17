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

import javax.swing.ImageIcon;
import javax.swing.JLabel;


import java.awt.GraphicsEnvironment;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;


/**
 * @author Dr. Richard St. John
 * @version $Revision: 1.1 $, $Date: 2007/05/25 00:12:26 $
 */
public class JLabelWithDropSupport extends JLabel implements DropTargetListener, Serializable {

  private DropListener dropListener;

  /**
   * 
   */
  public JLabelWithDropSupport() {
    super();
    new DropTarget(this, this);
  }
  
  public JLabelWithDropSupport(ImageIcon icon) {
    super(icon);
    if(!GraphicsEnvironment.isHeadless())
      new DropTarget(this, this);
  }
  
  public JLabelWithDropSupport(String label) {
    super(label);
    if(!GraphicsEnvironment.isHeadless())
      new DropTarget(this, this);
  }
  
  public void addDropListener(DropListener dropListener) {
    this.dropListener = dropListener;
  }

  public void dragEnter(DropTargetDragEvent dtde) {}
  public void dragOver(DropTargetDragEvent dtde) {}
  public void dropActionChanged(DropTargetDragEvent dtde) {}
  public void dragExit(DropTargetEvent dte) {}

  public void drop(DropTargetDropEvent dtde) {
    try {
      Transferable t = dtde.getTransferable();
      if (t.isDataFlavorSupported(DataFlavor.stringFlavor)) {
        dtde.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
        String[] files = (String[])t.getTransferData(DataFlavor.stringFlavor);
        Arrays.sort(files);
        if(dropListener!=null)
          dropListener.dropAction(new DropEvent(files, dtde.getDropTargetContext(), dtde.getLocation(), dtde.getDropAction(), dtde.getSourceActions()));
        dtde.dropComplete(true);
        return;
      }
      dtde.rejectDrop();
      dtde.dropComplete(true);
    } catch(IOException ioe) {
      dtde.dropComplete(true);
    } catch(UnsupportedFlavorException e) {
      dtde.dropComplete(true);
    }
  }
}
