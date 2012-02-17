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
package com.AandR.workspacePanel;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.logging.Logger;
import javax.swing.ActionMap;
import javax.swing.text.DefaultEditorKit;
import net.miginfocom.swing.MigLayout;
import org.openide.explorer.ExplorerManager;
import org.openide.explorer.ExplorerUtils;
import org.openide.explorer.view.BeanTreeView;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.filesystems.LocalFileSystem;
import org.openide.loaders.DataObject;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.nodes.NodeNotFoundException;
import org.openide.nodes.NodeOp;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;
//import org.openide.util.Utilities;

/**
 * Top component which displays something.
 */
final class WorkspacePanelTopComponent extends TopComponent implements ExplorerManager.Provider {

    private static WorkspacePanelTopComponent instance;
    /** path to the icon used by the component and its open action */
//    static final String ICON_PATH = "SET/PATH/TO/ICON/HERE";

    private static final String PREFERRED_ID = "WorkspacePanelTopComponent";
    
    private ExplorerManager mgr;
    private BeanTreeView beanTreeView;

    private WorkspacePanelTopComponent() {
        initialize();
        setName(NbBundle.getMessage(WorkspacePanelTopComponent.class, "CTL_WorkspacePanelTopComponent"));
        setToolTipText(NbBundle.getMessage(WorkspacePanelTopComponent.class, "HINT_WorkspacePanelTopComponent"));
//        setIcon(Utilities.loadImage(ICON_PATH, true));

    }

    private void initialize() {
        mgr = new ExplorerManager();
        ActionMap map = getActionMap();
        map.put(DefaultEditorKit.copyAction, ExplorerUtils.actionCopy(mgr));
        //map.put(DefaultEditorKit.cutAction, ExplorerUtils.actionCut(mgr));
        map.put(DefaultEditorKit.pasteAction, ExplorerUtils.actionPaste(mgr));
        map.put("delete", ExplorerUtils.actionDelete(mgr, true));
        associateLookup(ExplorerUtils.createLookup(mgr, map));
        setOpaque(true);
        setLayout(new MigLayout("", "0[]0", "0[]0"));
        String FS = File.separator;
        String userDir = System.getProperty("user.home") + FS + ".AandRcreations" + FS + "latiz" + FS + "perspectives";
        File f = new File(userDir, "workspaces");
        if(!f.exists())f.mkdirs();
        f = FileUtil.normalizeFile(f); 
        FileObject fo = FileUtil.toFileObject(f);
        try {
            LocalFileSystem lfs = new LocalFileSystem();
            lfs.setRootDirectory(new File(userDir, "workspaces"));
            DataObject dataObj = DataObject.find(lfs.getRoot());
            //DataObject dataObj = DataObject.find(fo);
            Node rootNode = dataObj.getNodeDelegate();
            //Node rootNode = FileNode.files(new File(userDir, "workspaces"));
            mgr.setRootContext(rootNode);
            beanTreeView = new BeanTreeView();
            //TreeTableView beanTreeView = new TreeTableView();

            beanTreeView.setPopupAllowed(true);
            beanTreeView.setRootVisible(true);
            //ChoiceView beanTreeView = new ChoiceView();
            add(beanTreeView, "push, grow");
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
    }
    }

    /**
     * Gets default instance. Do not use directly: reserved for *.settings files only,
     * i.e. deserialization routines; otherwise you could get a non-deserialized instance.
     * To obtain the singleton instance, use {@link #findInstance}.
     */
    public static synchronized WorkspacePanelTopComponent getDefault() {
        if (instance == null) {
            instance = new WorkspacePanelTopComponent();
        }
        return instance;
    }

    /**
     * Obtain the WorkspacePanelTopComponent instance. Never call {@link #getDefault} directly!
     */
    public static synchronized WorkspacePanelTopComponent findInstance() {
        TopComponent win = WindowManager.getDefault().findTopComponent(PREFERRED_ID);
        if (win == null) {
            Logger.getLogger(WorkspacePanelTopComponent.class.getName()).warning(
                    "Cannot find " + PREFERRED_ID + " component. It will not be located properly in the window system.");
            return getDefault();
        }
        if (win instanceof WorkspacePanelTopComponent) {
            return (WorkspacePanelTopComponent) win;
        }
        Logger.getLogger(WorkspacePanelTopComponent.class.getName()).warning(
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
        ArrayList<String[]> paths = new ArrayList<String[]>();
        updateExpansionList(paths, mgr.getRootContext());
        return new ResolvableHelper(paths);
    }


    private void updateExpansionList(ArrayList<String[]> paths, Node contextNode) {
        Children children = contextNode.getChildren();
        Node root = mgr.getRootContext();
        Node[] nodes = children.getNodes();
        for(Node node : nodes) {
            if(beanTreeView.isExpanded(node)) {
                paths.add(NodeOp.createPath(node, root));
                updateExpansionList(paths, node);
            }
        }
    }


    @Override
    protected String preferredID() {
        return PREFERRED_ID;
    }

    
    public ExplorerManager getExplorerManager() {
        return mgr;
    }


    /**
     *
     */
    final static class ResolvableHelper implements Serializable {
        private static final long serialVersionUID = 1L;

        private ArrayList<String[]> paths;

        private ResolvableHelper(ArrayList<String[]> paths) {
            this.paths = paths;
        }
        

        public Object readResolve() {
            WorkspacePanelTopComponent result = WorkspacePanelTopComponent.getDefault();
            if(paths==null || paths.isEmpty()) return result;
            for(String[] path : paths) {
                try {
                    Node nodeToExpand = NodeOp.findPath(result.mgr.getRootContext(), path);
                    if(nodeToExpand != null)
                        result.beanTreeView.expandNode(nodeToExpand);
                } catch (NodeNotFoundException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
            return result;
        }
    }
}