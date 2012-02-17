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

import java.awt.GraphicsEnvironment;
import java.awt.LayoutManager;
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
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.JPanel;

/**
 * @author Aaron Masino
 * @version $Revision: 1.2 $, $Date: 2007/08/01 01:42:00 $
 */
public class JPanelWithDropSupport extends JPanel implements DropTargetListener {

  private ArrayList<DropListener> observers;


  /**
   *
   */
  public JPanelWithDropSupport() {
    if(!GraphicsEnvironment.isHeadless())
      new DropTarget(this, this);
    observers=new ArrayList<DropListener>();
  }


  /**
   * @param layout
   */
  public JPanelWithDropSupport(LayoutManager layout) {
    super(layout);
    if(!GraphicsEnvironment.isHeadless())
      new DropTarget(this, this);
    observers=new ArrayList<DropListener>();
  }


  /**
   * @param isDoubleBuffered
   */
  public JPanelWithDropSupport(boolean isDoubleBuffered) {
    super(isDoubleBuffered);
    if(!GraphicsEnvironment.isHeadless())
      new DropTarget(this, this);
    observers=new ArrayList<DropListener>();
  }


  /**
   * @param layout
   * @param isDoubleBuffered
   */
  public JPanelWithDropSupport(LayoutManager layout, boolean isDoubleBuffered) {
    super(layout, isDoubleBuffered);
    if(!GraphicsEnvironment.isHeadless())
      new DropTarget(this, this);
    observers=new ArrayList<DropListener>();
  }


  public void addDropListener(DropListener dl) {
    observers.add(dl);
  }


  public void removeDropListener(DropListener dl) {
    int index = observers.indexOf(dl);
    if(index != -1)observers.remove(index);
  }


  private void notifyDropListeners(DropTargetDropEvent dtde) {
    try {
      Transferable t = dtde.getTransferable();
      if (t.isDataFlavorSupported(DataFlavor.stringFlavor)) {
        dtde.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
        String[] files = (String[])t.getTransferData(DataFlavor.stringFlavor);
        Arrays.sort(files);
        for(int i=0; i<observers.size(); i++) {
          observers.get(i).dropAction(new DropEvent(files, dtde.getDropTargetContext(), dtde.getLocation(), dtde.getDropAction(), dtde.getSourceActions()));
        }
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


  public void dragExit(DropTargetEvent dte) {}
  public void dragOver(DropTargetDragEvent dtde) {}
  public void dropActionChanged(DropTargetDragEvent dtde) {}
  public void dragEnter(DropTargetDragEvent dtde) {
    dtde.acceptDrag(DnDConstants.ACTION_COPY);
  }


  public void drop(DropTargetDropEvent dtde) {
   notifyDropListeners(dtde);
  }
}
