package com.AandR.beans.plotting.scrollableDesktop;

import com.AandR.library.gui.*;
import java.awt.Color;
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
import java.util.ArrayList;

import javax.swing.JMenuBar;


/**
 * @author Dr. Richard St. John
 * @version $Revision$, $Date$
 */
public class JDesktopPaneWithDropSupport extends JScrollableDesktopPane implements DropTargetListener {

  private ArrayList<DropListener> dropListeners;
  

  public JDesktopPaneWithDropSupport(JMenuBar menu) {
    super(menu);
    setBackground(new Color(160,160,160));
    dropListeners = new ArrayList<DropListener>();
    if(!GraphicsEnvironment.isHeadless())
      new DropTarget(this, this);
  }
  
  
  public JDesktopPaneWithDropSupport() {
    setBackground(new Color(160,160,160));
    dropListeners = new ArrayList<DropListener>();
    if(!GraphicsEnvironment.isHeadless())
      new DropTarget(this, this);
  }
  

  public void addDropListener(DropListener dropListener) {
    dropListeners.add(dropListener);
  }
  
  
  private void notifyDropListeners(DropEvent dropEvent) {
    for(DropListener d : dropListeners) {
      d.dropAction(dropEvent);
    }
  }
  

  public void dragExit(DropTargetEvent dte) {}
  public void dragOver(DropTargetDragEvent dtde) {}
  public void dropActionChanged(DropTargetDragEvent dtde) {}
  public void dragEnter(DropTargetDragEvent dtde) {
    dtde.acceptDrag(DnDConstants.ACTION_COPY);
  }


  public void drop(DropTargetDropEvent dtde) {
    Object droppedItem = null;
    try {
      Transferable t = dtde.getTransferable();
      if (t.isDataFlavorSupported(DataFlavor.stringFlavor)) {
        dtde.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
        droppedItem = t.getTransferData(DataFlavor.stringFlavor);
        notifyDropListeners(new DropEvent(droppedItem, dtde.getDropTargetContext(), dtde.getLocation(), dtde.getDropAction(), dtde.getSourceActions()));
        dtde.dropComplete(true);
        return;
      } else if(t.isDataFlavorSupported(TransferableTreeNode.NODE_FLAVOR)) {
        dtde.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
        droppedItem = t.getTransferData(TransferableTreeNode.NODE_FLAVOR);
        notifyDropListeners(new DropEvent(droppedItem, dtde.getDropTargetContext(), dtde.getLocation(), dtde.getDropAction(), dtde.getSourceActions()));
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
