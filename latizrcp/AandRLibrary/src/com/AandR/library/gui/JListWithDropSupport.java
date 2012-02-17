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

import javax.swing.AbstractAction;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.KeyStroke;

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
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import org.openide.util.Exceptions;

/**
 * @author Dr. Richard St. John
 * @version $Revision: 1.2 $, $Date: 2007/08/14 01:09:16 $
 */
public class JListWithDropSupport extends JList implements DropTargetListener, Serializable {

    public static final String ACTION_MOVE_UP_KEY = "MoveUp";
    public static final String ACTION_MOVE_DOWN_KEY = "MoveDown";
    public static final String ACTION_REMOVE_ITEMS = "RemoveItems";
    private DropListener dropListener;
    private DefaultListModel listModel;

    /**
     *
     */
    public JListWithDropSupport() {
        super();
        if (!GraphicsEnvironment.isHeadless()) {
            new DropTarget(this, this);
        }
        initialize();
    }

    public JListWithDropSupport(Object[] listData) {
        super(listData);
        if (!GraphicsEnvironment.isHeadless()) {
            new DropTarget(this, this);
        }
        initialize();
    }

    public JListWithDropSupport(DefaultListModel listModel) {
        super(listModel);
        if (!GraphicsEnvironment.isHeadless()) {
            new DropTarget(this, this);
        }
        this.listModel = listModel;
        initialize();
    }

    private void initialize() {
        bindMoveUp();
        bindMoveDown();
        bindRemoveItems();
    }

    public void addDropListener(DropListener dropListener) {
        this.dropListener = dropListener;
    }

    public void dragEnter(DropTargetDragEvent dtde) {
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
                String[] fileNames = (String[]) t.getTransferData(DataFlavor.stringFlavor);
                Arrays.sort(fileNames);
                if (dropListener != null) {
                    dropListener.dropAction(new DropEvent(fileNames, dtde.getDropTargetContext(), dtde.getLocation(), dtde.getDropAction(), dtde.getSourceActions()));
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
            }

            dtde.rejectDrop();
            dtde.dropComplete(true);
        } catch (IOException ioe) {
            dtde.dropComplete(true);
        } catch (UnsupportedFlavorException e) {
            dtde.dropComplete(true);
        }
    }

    private void bindMoveUp() {
        getActionMap().put(ACTION_MOVE_UP_KEY, new AbstractAction(ACTION_MOVE_UP_KEY) {

            public void actionPerformed(ActionEvent evt) {
                moveUp();
            }
        });
        getInputMap().put(KeyStroke.getKeyStroke("alt UP"), ACTION_MOVE_UP_KEY);
    }

    private void bindMoveDown() {
        getActionMap().put(ACTION_MOVE_DOWN_KEY, new AbstractAction(ACTION_MOVE_DOWN_KEY) {

            public void actionPerformed(ActionEvent evt) {
                moveDown();
            }
        });
        getInputMap().put(KeyStroke.getKeyStroke("alt DOWN"), ACTION_MOVE_DOWN_KEY);
    }

    private void bindRemoveItems() {
        getActionMap().put(ACTION_REMOVE_ITEMS, new AbstractAction(ACTION_REMOVE_ITEMS) {

            public void actionPerformed(ActionEvent evt) {
                removeItems();
            }
        });
        getInputMap().put(KeyStroke.getKeyStroke("control DELETE"), ACTION_REMOVE_ITEMS);
    }

    private void moveUp() {
        Object[] items = getSelectedValues();
        if (items.length < 1 || listModel.indexOf(items[0]) == 0) {
            return;
        }
        Object temp;
        int index;
        int[] selectedIndices = new int[items.length];
        for (int i = 0; i < items.length; i++) {
            index = listModel.indexOf(items[i]);
            if (index > 0) {
                temp = listModel.getElementAt(index - 1);
                listModel.setElementAt(listModel.getElementAt(index), index - 1);
                listModel.setElementAt(temp, index);
                selectedIndices[i] = index - 1;
            }
        }
        setSelectedIndices(selectedIndices);
    }

    private void moveDown() {
        Object[] items = getSelectedValues();
        if (items.length < 1 || listModel.indexOf(items[items.length - 1]) == listModel.size() - 1) {
            return;
        }

        Object temp;
        int index;
        int[] selectedIndices = getSelectedIndices();

        for (int i = items.length - 1; i >= 0; i--) {
            index = listModel.indexOf(items[i]);
            if (index < listModel.size() - 1) {
                temp = listModel.getElementAt(index + 1);
                listModel.setElementAt(listModel.getElementAt(index), index + 1);
                listModel.setElementAt(temp, index);
                selectedIndices[i] = index + 1;
            }
        }
        setSelectedIndices(selectedIndices);
    }

    private void removeItems() {
        DefaultListModel model = (DefaultListModel) getModel();
        Object[] values = getSelectedValues();
        if (values == null || values.length == 0) {
            return;
        }
        for (int i = 0; i < values.length; i++) {
            model.removeElement(values[i]);
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
