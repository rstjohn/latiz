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
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;

import java.util.List;
import javax.swing.JTextField;
import org.openide.util.Exceptions;

/**
 * @author Dr. Richard St. John
 * @version $Revision: 1.1 $, $Date: 2007/05/25 00:12:26 $
 */
public class JTextFieldWithDropSupport extends JTextField implements DropTargetListener, Serializable {

    private DropListener dropListener;

    public JTextFieldWithDropSupport() {
        this("", 10);
    }

    public JTextFieldWithDropSupport(int columns) {
        super(columns);
        if (!GraphicsEnvironment.isHeadless()) {
            new DropTarget(this, this);
        }
    }

    public JTextFieldWithDropSupport(String text) {
        super(text);
        if (!GraphicsEnvironment.isHeadless()) {
            new DropTarget(this, this);
        }
    }

    public JTextFieldWithDropSupport(String text, int columns) {
        super(text, columns);
        if (!GraphicsEnvironment.isHeadless()) {
            new DropTarget(this, this);
        }
    }

    public void addDropListener(DropListener dropListener) {
        this.dropListener = dropListener;
    }

    public void dragEnter(DropTargetDragEvent dtde) {
        dtde.acceptDrag(DnDConstants.ACTION_COPY);
    }

    public void dragOver(DropTargetDragEvent dtde) {
    }

    public void dropActionChanged(DropTargetDragEvent dtde) {
    }

    public void dragExit(DropTargetEvent dte) {
    }

    public void drop(DropTargetDropEvent dtde) {
        try {
            Transferable t = dtde.getTransferable();
            if (t.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                dtde.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
                String[] files = (String[]) t.getTransferData(DataFlavor.stringFlavor);
                Arrays.sort(files);
                if (dropListener != null) {
                    dropListener.dropAction(new DropEvent(files, dtde.getDropTargetContext(), dtde.getLocation(), dtde.getDropAction(), dtde.getSourceActions()));
                }
                dtde.dropComplete(true);
                return;
            }

            List<?> fileTransferable = getFilesTransferable(t);
            if(fileTransferable != null && fileTransferable.size()>0) {
                dtde.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
                String[] fileNames = new String[fileTransferable.size()];
                for(int i=0; i<fileNames.length; i++) {
                    fileNames[i] = ((File)fileTransferable.get(i)).getPath();
                }
                Arrays.sort(fileNames);
                if (dropListener != null) {
                    dropListener.dropAction(new DropEvent(fileNames, dtde.getDropTargetContext(), dtde.getLocation(), dtde.getDropAction(), dtde.getSourceActions()));
                }
                dtde.dropComplete(true);
                return;
            } else if (t.isDataFlavorSupported(TransferableTreeNode.NODE_FLAVOR)) {
                Object droppedItem = null;
                dtde.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
                droppedItem = t.getTransferData(TransferableTreeNode.NODE_FLAVOR);
                if (dropListener != null) {
                    dropListener.dropAction(new DropEvent(droppedItem, dtde.getDropTargetContext(), dtde.getLocation(), dtde.getDropAction(), dtde.getSourceActions()));
                }
                dtde.dropComplete(true);
                return;
            }
            dtde.rejectDrop();
            dtde.dropComplete(true);
        } catch (IOException ioe) {
            dtde.dropComplete(true);
        } catch (UnsupportedFlavorException e) {
            dtde.dropComplete(true);
        }
    }

    private List<?> getFilesTransferable(Transferable transferable) {
        if (!transferable.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
            return null;
        }
        try {
            return (List) transferable.getTransferData(DataFlavor.javaFileListFlavor);
        } catch (UnsupportedFlavorException ex) {
            Exceptions.printStackTrace(ex);
            return null;
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
            return null;
        }
    }
}
