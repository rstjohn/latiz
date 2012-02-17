package com.AandR.latiz.gui;

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
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeModel;

/**
 * @author Dr. Richard St. John
 * @version $Revision$, $Date$
 */
public class DragAndDropTree extends JTree {

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
    }

    private class DragAndDropListener implements DragGestureListener, DragSourceListener {

        private Cursor selectCursor(int action) {
            return (action == DnDConstants.ACTION_MOVE) ? DragSource.DefaultMoveDrop : DragSource.DefaultCopyDrop;
        }

        public void dragGestureRecognized(DragGestureEvent event) {
            String pluginName = ((DefaultMutableTreeNode) getSelectionPath().getLastPathComponent()).getUserObject().toString();
            String[] string = new String[]{pluginName};
            try {
                dragSource.startDrag(event, DragSource.DefaultCopyNoDrop, new TransferrableLabel(string), this);
            } catch (Exception e) {
            }
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

        public void dropActionChanged(DragSourceDragEvent dsde) {
        }

        public void dragDropEnd(DragSourceDropEvent dsde) {
        }
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

        @Override
        public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
            return string;
        }

        @Override
        public DataFlavor[] getTransferDataFlavors() {
            return flavors.clone();
        }

        @Override
        public boolean isDataFlavorSupported(DataFlavor flavor) {
            return flavor.isFlavorJavaFileListType() || (flavor.getRepresentationClass() == java.lang.String.class);
        }
    }
}
