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
package com.latizTech.hdf5Panel;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import javax.swing.ActionMap;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.tree.DefaultMutableTreeNode;
import ncsa.hdf.object.FileFormat;
import ncsa.hdf.object.h5.H5File;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.explorer.ExplorerManager;
import org.openide.explorer.ExplorerUtils;
import org.openide.explorer.view.BeanTreeView;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.nodes.AbstractNode;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;
import org.openide.util.datatransfer.PasteType;
import org.openide.util.lookup.Lookups;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;
//import org.openide.util.Utilities;

/**
 * Top component which displays something.
 */
final public class HDF5TopComponent extends TopComponent implements ExplorerManager.Provider {

    private static HDF5TopComponent instance;
    /** path to the icon used by the component and its open action */
//    static final String ICON_PATH = "SET/PATH/TO/ICON/HERE";
    private static final String PREFERRED_ID = "HDF5TopComponent";
    private ArrayList<DefaultMutableTreeNode> filesList;
    private ExplorerManager mgr = new ExplorerManager();
    private FilesChildren filesChildren;

    @SuppressWarnings(value="unchecked")
    private HDF5TopComponent() {
        filesList = new ArrayList<DefaultMutableTreeNode>();

        ActionMap map = getActionMap();
        //map.put(DefaultEditorKit.copyAction, ExplorerUtils.actionCopy(mgr));
        //map.put(DefaultEditorKit.cutAction, ExplorerUtils.actionCut(mgr));
        //map.put(DefaultEditorKit.pasteAction, ExplorerUtils.actionPaste(mgr));
        associateLookup(ExplorerUtils.createLookup(mgr, map));

        initComponents();

        //beanTreeView.setDragSource(true);
        //beanTreeView.setDropTarget(true);

        setName(NbBundle.getMessage(HDF5TopComponent.class, "CTL_HDF5TopComponent"));
        setToolTipText(NbBundle.getMessage(HDF5TopComponent.class, "HINT_HDF5TopComponent"));
//        setIcon(Utilities.loadImage(ICON_PATH, true));
        filesList.clear();
        filesChildren = new FilesChildren(filesList);
        AbstractNode rootNode = new AbstractNode(filesChildren, Lookups.singleton(filesChildren)) {

            @Override
            public PasteType getDropType(Transferable t, int arg1, int arg2) {
                try {
                    List<File> fileList = (List<File>) t.getTransferData(DataFlavor.javaFileListFlavor);
                    final File droppedFile = fileList.get(0);
                    return new PasteType() {

                        @Override
                        public Transferable paste() throws IOException {
                            addFile(droppedFile);
                            return null;
                        }
                    };
                } catch (Exception ex) {
                    Exceptions.printStackTrace(ex);
                    return null;
                }
            }
        };

        rootNode.setDisplayName("Loaded files");
        rootNode.setIconBaseWithExtension("com/latizTech/hdf5Panel/resources/hdf5.gif");
        mgr.setRootContext(rootNode);
    }

    public void addFile(FileObject f) {
        addFile(FileUtil.toFile(f));
    }

    final void addFile(File fileToAdd) {
        FileInputStream stream = null;
        String magicHeader = "";
        try {
            byte[] b = new byte[8];
            stream = new FileInputStream(fileToAdd);
            stream.read(b);
            magicHeader = new String(b);
        } catch (Exception e1) {
            return;
        } finally {
            try {
                stream.close();
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        }

        if (magicHeader.substring(1).startsWith("HDF")) {
            try {
                filesChildren.addFileNode(getFileRootNode(fileToAdd.getPath()));
            } catch (Exception ex) {
                Exceptions.printStackTrace(ex);
            }
        } else {
            NotifyDescriptor nd = new NotifyDescriptor.Message("<HTML>The dropped file:<BR><BR><B>" + fileToAdd.getPath() + "</B><BR><BR>is not a valid HDF5 or LAT5 file.</HTML>");
            DialogDisplayer.getDefault().notify(nd);
            return;
        }
    }

    private DefaultMutableTreeNode getFileRootNode(String filePath) throws Exception {
        H5File h5File = new H5File(filePath, H5File.READ);
        h5File.createFile(h5File.getPath(), FileFormat.FILE_CREATE_OPEN);
        h5File.open();
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) h5File.getRootNode();
        h5File.close();
        return root;
    }

    public ExplorerManager getExplorerManager() {
        return mgr;
    }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        beanTreeView = new BeanTreeView();

        beanTreeView.setDragSource(false);

        GroupLayout layout = new GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(Alignment.LEADING)
            .addComponent(beanTreeView, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 168, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(Alignment.LEADING)
            .addComponent(beanTreeView, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 197, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private BeanTreeView beanTreeView;
    // End of variables declaration//GEN-END:variables

    public static synchronized HDF5TopComponent getDefault() {
        if (instance == null) {
            instance = new HDF5TopComponent();
        }
        return instance;
    }

    /**
     * Obtain the HDF5TopComponent instance. Never call {@link #getDefault} directly!
     */
    public static synchronized HDF5TopComponent findInstance() {
        TopComponent win = WindowManager.getDefault().findTopComponent(PREFERRED_ID);
        if (win == null) {
            Logger.getLogger(HDF5TopComponent.class.getName()).warning(
                    "Cannot find " + PREFERRED_ID + " component. It will not be located properly in the window system.");
            return getDefault();
        }
        if (win instanceof HDF5TopComponent) {
            return (HDF5TopComponent) win;
        }
        Logger.getLogger(HDF5TopComponent.class.getName()).warning(
                "There seem to be multiple components with the '" + PREFERRED_ID +
                "' ID. That is a potential source of errors and unexpected behavior.");
        return getDefault();
    }

    @Override
    public int getPersistenceType() {
        return TopComponent.PERSISTENCE_ALWAYS;
    }

    @Override
    public void componentOpened() {
    }

    @Override
    public void componentClosed() {
    }

    /** replaces this in object stream */
    @Override
    public Object writeReplace() {
        return new ResolvableHelper();
    }

    @Override
    protected String preferredID() {
        return PREFERRED_ID;
    }

    final static class ResolvableHelper implements Serializable {

        private static final long serialVersionUID = 1L;

        public Object readResolve() {
            return HDF5TopComponent.getDefault();
        }
    }
}
