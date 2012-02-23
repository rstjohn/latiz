package com.AandR.latiz.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.util.Enumeration;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.JDialog;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.border.EmptyBorder;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreePath;


import com.AandR.beans.fileExplorerPanel.FileNode;
import com.AandR.beans.fileExplorerPanel.FileSystemModel;
import com.AandR.beans.fileExplorerPanel.FileSystemTree;
import com.AandR.beans.fileExplorerPanel.FileTreeFilter;
import com.AandR.gui.OptionsDialog;
import com.AandR.gui.ui.JButtonX;
import com.AandR.io.AsciiFile;
import com.AandR.io.FileSystem;
import com.AandR.latiz.resources.Resources;

/**
 * @author Dr. Richard St. John
 * @version $Revision$, $Date$
 */
public class WorkspaceTreePanel extends JPanel {

    private FileSystemTree workspaceTree;
    private ImageIcon icon;
    private FileSystemModel treeModel;
    private WorkspaceTreeListener workspaceTreeListener;
    private String workspaceDir;
    private FileTreeFilter filter;

    public WorkspaceTreePanel() {
        setLayout(new BorderLayout());
        initialize();
        bindActionsToTree();
        expandAllRows();

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(workspaceTree, BorderLayout.CENTER);

        JScrollPane scroller = new JScrollPane(panel);
        scroller.setBorder(null);

        setLayout(new BorderLayout());
        add(scroller);
    }

    private void initialize() {
        workspaceDir = System.getProperty("user.home") + File.separator + ".AandRcreations" + File.separator + "latiz" + File.separator + "Workspaces";
        icon = Resources.createIcon("latizIcon22.png");
        filter = new FileTreeFilter();
        filter.setFileFilter("*.latiz");
        workspaceTree = new FileSystemTree();
        workspaceTree.addMouseListener(workspaceTreeListener);

        treeModel = new FileSystemModel(new File(workspaceDir), filter);

        workspaceTree.setModel(treeModel);
        workspaceTree.setShowsRootHandles(true);
        workspaceTree.setCellRenderer(new TreeRenderer());
        workspaceTree.addMouseListener(workspaceTreeListener = new WorkspaceTreeListener());
    }

    private void bindActionsToTree() {
        ActionMap actionMap = workspaceTree.getActionMap();
        InputMap inputMap = workspaceTree.getInputMap();
        actionMap.put("Refresh", new AbstractAction("Refresh") {

            public void actionPerformed(ActionEvent evt) {
                refreshTree();
            }
        });
        inputMap.put(KeyStroke.getKeyStroke("F5"), "Refresh");
    }

    /**
     *
     *
     */
    public void refreshTree() {
        TreePath[] selectedPaths = workspaceTree.getSelectionPaths();
        Enumeration<TreePath> expandedPaths = workspaceTree.getExpandedDescendants(new TreePath(treeModel.getRootNode()));

        treeModel = new FileSystemModel(new File(workspaceDir), filter);
        workspaceTree.setModel(treeModel);
        while (expandedPaths != null && expandedPaths.hasMoreElements()) {
            workspaceTree.expandPath(expandedPaths.nextElement());
        }
        workspaceTree.setSelectionPaths(selectedPaths);
    }

    public void expandAllRows() {
        for (int i = 0; i < workspaceTree.getRowCount(); i++) {
            workspaceTree.expandRow(i);
        }
    }

    public FileSystemTree getTree() {
        return workspaceTree;
    }

    /**
     *
     * @author Dr. Richard St. John
     * @version $Revision$, $Date$
     */
    private class TreeRenderer extends DefaultTreeCellRenderer {

        @Override
        public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
            super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
            FileNode node = (FileNode) value;
            if (!node.isLeaf() || node.getParent() == null) {
                setIcon(null);
            } else {
                setIcon(icon);
            }
            setBorder(new EmptyBorder(2, 0, 2, 0));
            return this;
        }
    }

    private class WorkspaceTreeListener implements ActionListener, MouseListener {

        private JPopupMenu popupRoot, popupCat, popup;

        public WorkspaceTreeListener() {
            popupRoot = new JPopupMenu();
            popupRoot.add(createMenuItem("Create New Category", Resources.createIcon("newFolder.png"), "NEW_CAT", "Create new category"));

            popupCat = new JPopupMenu();
            popupCat.add(createMenuItem("Rename", Resources.createIcon("rename16.png"), "RENAME", "Rename"));
            popupCat.add(createMenuItem("Create Subcategory", Resources.createIcon("newFolder.png"), "NEW_SUBCAT", "Create new category"));
            popupCat.add(createMenuItem("Remove this category", Resources.createIcon("cancel16.png"), "REMOVE_CAT", "Remove category"));

            popup = new JPopupMenu();
            popup.add(createMenuItem("View Source", null, "VIEW_SOURCE", "View Source"));
            popup.add(createMenuItem("Rename", Resources.createIcon("rename16.png"), "RENAME", "Rename"));
            popup.add(createMenuItem("Delete", Resources.createIcon("cancel16.png"), "REMOVE", "Remove workspace file."));
        }

        private JMenuItem createMenuItem(String label, Icon icon, String actionCommand, String tooltip) {
            JMenuItem item = new JMenuItem(label, icon);
            item.addActionListener(this);
            item.setActionCommand(actionCommand);
            return item;
        }

        public void mouseClicked(MouseEvent e) {
            refreshTree();
        }

        public void mouseEntered(MouseEvent e) {
            refreshTree();
        }

        public void mouseExited(MouseEvent e) {
        }

        public void mousePressed(MouseEvent e) {
        }

        public void mouseReleased(MouseEvent e) {
            if (e.getButton() != 3) {
                return;
            }

            int selRow = workspaceTree.getRowForLocation(e.getX(), e.getY());
            if (selRow < 0) {
                return;
            }

            workspaceTree.setSelectionPath(workspaceTree.getPathForLocation(e.getX(), e.getY()));

            if (selRow == 0) {
                popupRoot.show(workspaceTree, e.getX(), e.getY());
            } else {
                File selectedFile = workspaceTree.getSelectedFile();
                if (selectedFile.isDirectory()) {
                    popupCat.show(workspaceTree, e.getX(), e.getY());
                } else {
                    popup.show(workspaceTree, e.getX(), e.getY());
                }
            }
        }

        public void actionPerformed(ActionEvent e) {
            String command = e.getActionCommand();
            if (command.equalsIgnoreCase("REMOVE_CAT")) {
                actionRemoveCategory();
            } else if (command.equalsIgnoreCase("NEW_CAT")) {
                actionCreateCategory(workspaceDir);
            } else if (command.equalsIgnoreCase("NEW_SUBCAT")) {
                actionCreateCategory();
            } else if (command.equalsIgnoreCase("REMOVE")) {
                actionRemoveWorkspace();
            } else if (command.equalsIgnoreCase("RENAME")) {
                actionRename();
            } else if (command.equalsIgnoreCase("VIEW_SOURCE")) {
                actionViewSource();
            }
        }

        private void actionViewSource() {
            final File selectedFile = workspaceTree.getSelectedFile();
            if (selectedFile.isDirectory()) {
                return;
            }

            final JDialog dialog = new JDialog((JDialog) null);

            String fileContents = "";
            try {
                fileContents = AsciiFile.readLinesInFile(selectedFile, "\n");
            } catch (IOException e2) {
                e2.printStackTrace();
            }
            final JTextArea textArea = new JTextArea(fileContents, 30, 50);
            final String s = fileContents;

            JButtonX closeButton = new JButtonX("Close");
            closeButton.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    String textAreaContents = textArea.getText();
                    if (!textAreaContents.equals(s)) {
                        OptionsDialog o = new OptionsDialog(dialog, "File Modified", new JButtonX[]{new JButtonX("Accept"), new JButtonX("Cancel")}, OptionsDialog.QUESTION_ICON);
                        o.showDialog("File contents has changed. Save Changes", 0);
                        if (o.getSelectedButtonIndex() == 1) {
                            dialog.dispose();
                        } else {
                            try {
                                AsciiFile.write(selectedFile, new String[]{textAreaContents});
                            } catch (IOException e1) {
                                e1.printStackTrace();
                            }
                        }
                    }
                    dialog.dispose();

                }
            });
            JPanel buttonPanel = new JPanel();
            buttonPanel.add(closeButton);

            JPanel panel = new JPanel(new BorderLayout());
            panel.add(new JScrollPane(textArea), BorderLayout.CENTER);
            panel.add(buttonPanel, BorderLayout.SOUTH);

            dialog.setContentPane(panel);
            dialog.pack();
            dialog.setLocationRelativeTo(null);
            dialog.setVisible(true);
        }

        private void actionRename() {
            OptionsDialog optionsDialog = new OptionsDialog(WorkspaceTreePanel.this, "Rename Dialog:", new JButtonX[]{new JButtonX("Accept"), new JButtonX("Cancel")}, OptionsDialog.QUESTION_ICON, true);
            optionsDialog.showDialog("Rename to: ", 0);
            if (optionsDialog.getSelectedButtonIndex() == 1) {
                return;
            }

            File selectedFile = workspaceTree.getSelectedFile();
            String filename = optionsDialog.getInput();
            if (selectedFile.isFile() && !filename.endsWith(".latiz")) {
                filename += ".latiz";
            }

            File newFile = new File(selectedFile.getParent(), filename);
            if (newFile.exists()) {
                actionRename();
            } else {
                selectedFile.renameTo(newFile);
            }

            refreshTree();
        }

        private void actionRemoveWorkspace() {
            File[] selectedFiles = workspaceTree.getSelectedFiles();
            FileSystem.deleteFiles(selectedFiles);
        }

        private void actionCreateCategory() {
            File selectedFile = workspaceTree.getSelectedFile();
            actionCreateCategory(selectedFile.getPath());
        }

        private void actionCreateCategory(String dir) {
            OptionsDialog optionsDialog = new OptionsDialog(WorkspaceTreePanel.this, "Create Categroy", new JButtonX[]{new JButtonX("Accept"), new JButtonX("Cancel")}, OptionsDialog.QUESTION_ICON, true);
            optionsDialog.showDialog("Enter Category Name", 0);
            if (optionsDialog.getSelectedButtonIndex() == 1) {
                return;
            }

            File newCat = new File(dir, optionsDialog.getInput());
            if (!newCat.exists()) {
                newCat.mkdirs();
            } else {
                actionCreateCategory(dir);
            }
            refreshTree();
        }

        private void actionRemoveCategory() {
            File selectedFile = workspaceTree.getSelectedFile();
            FileSystem.deleteDirectory(selectedFile.getPath());
        }
    }
}
