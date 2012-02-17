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
package com.AandR.beans.plotting.latExplorer;

import com.AandR.library.gui.TransferableTreeNode;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;
import java.util.HashMap;

import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import ncsa.hdf.object.Dataset;
import ncsa.hdf.object.Group;
import ncsa.hdf.object.HObject;
import ncsa.hdf.object.h5.H5CompoundDS;
import ncsa.hdf.object.h5.H5ScalarDS;
import org.openide.util.ImageUtilities;

/**
 * @author Dr. Richard St. John
 * @version $Revision$, $Date$
 */
public class LatFileTree extends JTree {

    private DragSource dragSource;
    private HashMap<String, LatFileRunTrace> runTraces;
    private TreeListener treeListener;

    public LatFileTree(TreeModel model) {
        super(model);
        super.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        setCellRenderer(new LatTreeRenderer());
        treeListener = new TreeListener();
        setDragEnabled(false);
        dragSource = DragSource.getDefaultDragSource();
        dragSource.createDefaultDragGestureRecognizer(this, DnDConstants.ACTION_COPY_OR_MOVE, treeListener);
    }

    public LatFileTree(TreeNode root) {
        super(root);
        super.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        setCellRenderer(new LatTreeRenderer());
        treeListener = new TreeListener();
        setDragEnabled(false);
        dragSource = DragSource.getDefaultDragSource();
        dragSource.createDefaultDragGestureRecognizer(this, DnDConstants.ACTION_COPY_OR_MOVE, treeListener);
    }

    public void setRunTraces(HashMap<String, LatFileRunTrace> runTraces) {
        this.runTraces = runTraces;
    }

    /**
     *
     * @author Dr. Richard St. John
     * @version $Revision: 1.8 $, $Date: 2007/09/26 18:13:42 $
     */
    private class TreeListener implements DragGestureListener, DragSourceListener {

        private Cursor selectCursor(int action) {
            return (action == DnDConstants.ACTION_MOVE) ? DragSource.DefaultMoveDrop : DragSource.DefaultCopyDrop;
        }

        public void dragDropEnd(DragSourceDropEvent dsde) {
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

        public void dragGestureRecognized(DragGestureEvent event) {
            TreePath path = getSelectionPath();
            Point dragPoint = event.getDragOrigin();
            DefaultMutableTreeNode thisNode = null;

            if (path == null || path.getParentPath() == null || getRowForLocation(dragPoint.x, dragPoint.y) == -1) {
                return;
            }
            Object o = path.getLastPathComponent();
            if (!(o instanceof DefaultMutableTreeNode)) {
                return;
            }

            thisNode = (DefaultMutableTreeNode) o;
            try {
                event.startDrag(DragSource.DefaultCopyNoDrop, new TransferableTreeNode(thisNode), this);
            } catch (Exception e) {
            }
        }
    }

    /**
     *
     * @author Dr. Richard St. John
     * @version $Revision$, $Date$
     */
    private class LatTreeRenderer extends DefaultTreeCellRenderer {

        public LatTreeRenderer() {
        }

//  public Component g
        @Override
        public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
            super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
            setBorder(new EmptyBorder(3, 0, 1, 0));

            if (((DefaultMutableTreeNode) value).isRoot()) {
                setIcon(new ImageIcon(ImageUtilities.loadImage("com/AandR/beans/plotting/resources/exec.png")));
                return this;
            }

            DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
            Object userObject = node.getUserObject();

            // HDF5 File - Dataset
            if (userObject instanceof Dataset) {
                HObject hobject = (HObject) userObject;
                LatFileRunTrace runTrace = runTraces.get(hobject.getFile() + ":" + hobject.getFullName());
                boolean isAddToSavedMap = runTrace.isAddToSavedMap();
                setForeground(isAddToSavedMap ? new Color(10, 180, 10) : UIManager.getColor("Tree.foreground"));
                setBorderSelectionColor(isAddToSavedMap ? new Color(0, 100, 0) : UIManager.getColor("Tree.selectionBorderColor"));
                setBackgroundSelectionColor(isAddToSavedMap ? new Color(220, 220, 220) : UIManager.getColor("Tree.selectionBackground"));

                if (userObject instanceof H5CompoundDS) {
                    setIcon(new ImageIcon(ImageUtilities.loadImage("com/AandR/beans/plotting/resources/table.gif")));
                } else if (userObject instanceof H5ScalarDS) {
                    setIcon(new ImageIcon(ImageUtilities.loadImage("com/AandR/beans/plotting/resources/dataset.gif")));
                }
            } // HDF5 File - Group
            else if (userObject instanceof Group) {
                Group group = (Group) userObject;

                if (((DefaultMutableTreeNode) node.getParent()).isRoot()) {
                    String filename = group.getFile();
                    if (filename.endsWith(".h5")) {
                        setIcon(new ImageIcon(ImageUtilities.loadImage("com/AandR/beans/plotting/resources/hdf5.gif")));
                    } else if (filename.endsWith(".lat5")) {
                        setIcon(new ImageIcon(ImageUtilities.loadImage("com/AandR/beans/plotting/resources/latizIcon22.png")));
                    } else {
                        setIcon(new ImageIcon(ImageUtilities.loadImage("com/AandR/beans/plotting/resources/folderYellow22.png")));
                    }
                } else {
                    setIcon(new ImageIcon(ImageUtilities.loadImage("com/AandR/beans/plotting/resources/folderYellow22.png")));
                }
            } // TRF File - Node
            else if (userObject instanceof TrfDataObject) {
                TrfDataObject tds = (TrfDataObject) userObject;
                boolean isAddToSavedMap = tds.isAddToSavedMap();
                setForeground(isAddToSavedMap ? new Color(10, 180, 10) : UIManager.getColor("Tree.foreground"));
                setBorderSelectionColor(isAddToSavedMap ? new Color(0, 100, 0) : UIManager.getColor("Tree.selectionBorderColor"));
                setBackgroundSelectionColor(isAddToSavedMap ? new Color(220, 220, 220) : UIManager.getColor("Tree.selectionBackground"));
                int type = tds.getType();
                switch (type) {
                    case TrfConstants.INT:
                        setIcon(new ImageIcon(ImageUtilities.loadImage("com/AandR/beans/plotting/resources/int.png")));
                        break;
                    case TrfConstants.INT_:
                        setIcon(new ImageIcon(ImageUtilities.loadImage("com/AandR/beans/plotting/resources/int.png")));
                        break;
                    case TrfConstants.FLOAT:
                        setIcon(new ImageIcon(ImageUtilities.loadImage("com/AandR/beans/plotting/resources/float.png")));
                        break;
                    case TrfConstants.DOUBLE:
                        setIcon(new ImageIcon(ImageUtilities.loadImage("com/AandR/beans/plotting/resources/double.png")));
                        break;
                    case TrfConstants.COMPLEX:
                        setIcon(new ImageIcon(ImageUtilities.loadImage("com/AandR/beans/plotting/resources/complex.png")));
                        break;
                    case TrfConstants.STRING:
                        setIcon(new ImageIcon(ImageUtilities.loadImage("com/AandR/beans/plotting/resources/string.png")));
                        break;
                    case TrfConstants.ARRAY_INT:
                        setIcon(new ImageIcon(ImageUtilities.loadImage("com/AandR/beans/plotting/resources/arrayInt.png")));
                        break;
                    case TrfConstants.ARRAY_FLOAT:
                        setIcon(new ImageIcon(ImageUtilities.loadImage("com/AandR/beans/plotting/resources/arrayFloat.png")));
                        break;
                    case TrfConstants.ARRAY_LONG:
                        setIcon(new ImageIcon(ImageUtilities.loadImage("com/AandR/beans/plotting/resources/arrayLong.png")));
                        break;
                    case TrfConstants.ARRAY_DOUBLE:
                        setIcon(new ImageIcon(ImageUtilities.loadImage("com/AandR/beans/plotting/resources/arrayDouble.png")));
                        break;
                    case TrfConstants.ARRAY_COMPLEX:
                        setIcon(new ImageIcon(ImageUtilities.loadImage("com/AandR/beans/plotting/resources/arracyComplex.png")));
                        break;
                    case TrfConstants.VECTOR_INT:
                        setIcon(new ImageIcon(ImageUtilities.loadImage("com/AandR/beans/plotting/resources/vectorInt.png")));
                        break;
                    case TrfConstants.VECTOR_FLOAT:
                        setIcon(new ImageIcon(ImageUtilities.loadImage("com/AandR/beans/plotting/resources/vectorFloat.png")));
                        break;
                    case TrfConstants.VECTOR_LONG:
                        setIcon(new ImageIcon(ImageUtilities.loadImage("com/AandR/beans/plotting/resources/vectorLong.png")));
                        break;
                    case TrfConstants.VECTOR_DOUBLE:
                        setIcon(new ImageIcon(ImageUtilities.loadImage("com/AandR/beans/plotting/resources/vectorDouble.png")));
                        break;
                    case TrfConstants.VECTOR_COMPLEX:
                        setIcon(new ImageIcon(ImageUtilities.loadImage("com/AandR/beans/plotting/resources/vectorComplex.png")));
                        break;
                    case TrfConstants.ARRAY_GRID_FLOAT:
                        setIcon(new ImageIcon(ImageUtilities.loadImage("com/AandR/beans/plotting/resources/gridFloat.png")));
                        break;
                    case TrfConstants.ARRAY_GRID_DOUBLE:
                        setIcon(new ImageIcon(ImageUtilities.loadImage("com/AandR/beans/plotting/resources/gridDouble.png")));
                        break;
                    case TrfConstants.ARRAY_GRID_COMPLEX:
                        setIcon(new ImageIcon(ImageUtilities.loadImage("com/AandR/beans/plotting/resources/gridComplex.png")));
                        break;
                    case TrfConstants.CHAR_ARRAY:
                        setIcon(new ImageIcon(ImageUtilities.loadImage("com/AandR/beans/plotting/resources/string.png")));
                        break;
                    case TrfConstants.BOOLEAN:
                        setIcon(new ImageIcon(ImageUtilities.loadImage("com/AandR/beans/plotting/resources/boolean.png")));
                        break;
                }
            } else if (userObject instanceof TrfFileGroup) {
                setIcon(new ImageIcon(ImageUtilities.loadImage("com/AandR/beans/plotting/resources/trfapp.png")));
            } else if (userObject instanceof TrfGroup) {
                if (value.toString().equalsIgnoreCase("Parameters")) {
                    setIcon(new ImageIcon(ImageUtilities.loadImage("com/AandR/beans/plotting/resources/params.png")));
                } else {
                    setIcon(new ImageIcon(ImageUtilities.loadImage("com/AandR/beans/plotting/resources/exec.png")));
                }
            }
            return this;
        }
    }
}
