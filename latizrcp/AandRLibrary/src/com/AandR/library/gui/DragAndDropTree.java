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

import java.awt.Cursor;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;
import java.io.IOException;
import java.io.Serializable;

import javax.swing.JTree;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;


/**
 * @author Dr. Richard St. John
 * @version $Revision$, $Date$
 */
public class DragAndDropTree extends JZebraTree {

  private DragSource dragSource;

  private DataFlavor flavors[] = {DataFlavor.javaFileListFlavor, DataFlavor.stringFlavor};

  public DragAndDropTree() {
    dragSource = DragSource.getDefaultDragSource();
    dragSource.createDefaultDragGestureRecognizer(this, DnDConstants.ACTION_COPY_OR_MOVE, new DragAndDropListener());
  }

  public DragAndDropTree(TreeModel treeModel) {
    super(treeModel);
    dragSource = DragSource.getDefaultDragSource();
    dragSource.createDefaultDragGestureRecognizer(this, DnDConstants.ACTION_COPY_OR_MOVE, new DragAndDropListener());
    dragSource.createDefaultDragGestureRecognizer(this, DnDConstants.ACTION_COPY_OR_MOVE, new DragAndDropListener());
  }


  private class DragAndDropListener implements DragGestureListener, DragSourceListener {

    private Cursor selectCursor(int action) {
      return (action == DnDConstants.ACTION_MOVE) ? DragSource.DefaultMoveDrop : DragSource.DefaultCopyDrop;
    }


    public void dragGestureRecognized(DragGestureEvent event) {
      TreePath path = getSelectionPath();
      if(path==null) return;
      String pathString = "";
      for(int i=0; i<path.getPathCount(); i++) {
        pathString += path.getPath()[i].toString() + "/";
      }
      pathString = pathString.substring(0, pathString.length()-1);

      //String pluginName = ((DefaultMutableTreeNode)getSelectionPath().getLastPathComponent()).getUserObject().toString();
      //String[] string = new String[] {pluginName};
      String[] string = new String[] {pathString};
      try {
        dragSource.startDrag(event, DragSource.DefaultCopyNoDrop, new TransferrableLabel(string), this);
      } catch (Exception e) {}
    }


    public void dragEnter(DragSourceDragEvent dsde) {
      dsde.getDragSourceContext().setCursor(selectCursor(dsde.getDropAction()));
    }


    public void dragExit(DragSourceEvent dse) {
      dse.getDragSourceContext().setCursor(DragSource.DefaultCopyNoDrop);
    }


    public void dragOver(DragSourceDragEvent dsde) {
      dsde.getDragSourceContext().setCursor(selectCursor(dsde.getDropAction()));
    }

    public void dropActionChanged(DragSourceDragEvent dsde) {}
    public void dragDropEnd(DragSourceDropEvent dsde) {}
  }

/**
 *
 * @author Dr. Richard St. John
 * @version $Revision$, $Date$
 */
  private class TransferrableLabel implements Transferable, Serializable {

    private String[] string;

    public TransferrableLabel(String[] string) {
      this.string = string;
    }

    public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
      return string;
    }

    public DataFlavor[] getTransferDataFlavors() {
      return flavors.clone();
    }

    public boolean isDataFlavorSupported(DataFlavor flavor) {
      return flavor.isFlavorJavaFileListType() || (flavor.getRepresentationClass() == java.lang.String.class);
    }
  }
}
