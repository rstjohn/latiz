package com.AandR.latiz.gui.prefs;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.border.EmptyBorder;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import com.AandR.gui.HeaderPanel;
import com.AandR.gui.ui.JButtonX;

/**
 * @author Dr. Richard St. John
 * @version $Revision: 1.5 $, $Date: 2007/09/15 15:57:24 $
 */
public class PreferencesDialog extends JDialog {

    private static final String ACTION_ACCEPT = "ACTION_ACCEPT";
    private static final String ACTION_CANCEL = "ACTION_CANCEL";
    private ArrayList<PreferencesListener> listeners;
    private DefaultTreeModel treeModel;
    private DefaultMutableTreeNode rootNode;
    private HashSet<AbstractPreferencePage> propSet;
    private JPanel cardsPanel;
    private JTree prefTree;
    private PrefListener prefListener;

    public PreferencesDialog() {
        super((JFrame) null, "Preferences");
        initialize();
        setContentPane(createContentPane());
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);
        showFirstItem();
        setVisible(true);
    }

    private void initialize() {
        listeners = new ArrayList<PreferencesListener>();

        prefListener = new PrefListener();
        cardsPanel = new JPanel(new CardLayout());
        propSet = new HashSet<AbstractPreferencePage>();

        NullPrefPage propPrefPage = new NullPrefPage("Properties");
        prefTree = new JTree(treeModel = new DefaultTreeModel(rootNode = new DefaultMutableTreeNode(propPrefPage)));
        prefTree.setBorder(new EmptyBorder(5, 8, 0, 0));
        prefTree.setShowsRootHandles(false);
        prefTree.setCellRenderer(new PreferencesCellRenderer());
        addToPropSet(propPrefPage);

        GeneralPrefPage generalProperties = new GeneralPrefPage();
        treeModel.insertNodeInto(new DefaultMutableTreeNode(generalProperties), rootNode, rootNode.getChildCount());
        addToPropSet(generalProperties);

        ColorsPrefPage colorProperties = new ColorsPrefPage();
        treeModel.insertNodeInto(new DefaultMutableTreeNode(colorProperties), rootNode, rootNode.getChildCount());
        addToPropSet(colorProperties);

        PalettePrefPage palettePrefPage = new PalettePrefPage();
        treeModel.insertNodeInto(new DefaultMutableTreeNode(palettePrefPage), rootNode, rootNode.getChildCount());
        addToPropSet(palettePrefPage);

        RepositoryPrefPage repoProperties = new RepositoryPrefPage();
        treeModel.insertNodeInto(new DefaultMutableTreeNode(repoProperties), rootNode, rootNode.getChildCount());
        addToPropSet(repoProperties);

        DevelopersPrefPage developersProperties = new DevelopersPrefPage();
        treeModel.insertNodeInto(new DefaultMutableTreeNode(developersProperties), rootNode, rootNode.getChildCount());
        addToPropSet(developersProperties);

        prefTree.expandRow(0);
        prefTree.addMouseListener(prefListener);
    }

    public void addPreferencesListener(PreferencesListener listener) {
        listeners.add(listener);
    }

    private void addToPropSet(AbstractPreferencePage prefPage) {
        propSet.add(prefPage);
        cardsPanel.add(prefPage.getPropPanel(), prefPage.getTreeLabel());
    }

    private void showFirstItem() {
        TreePath path = prefTree.getPathForRow(1);
        if (path == null) {
            return;
        }
        prefTree.setSelectionPath(path);
        try {
            AbstractPreferencePage thisPage = (AbstractPreferencePage) ((DefaultMutableTreeNode) path.getLastPathComponent()).getUserObject();
            CardLayout cl = (CardLayout) cardsPanel.getLayout();
            cl.show(cardsPanel, thisPage.getTreeLabel());
        } catch (NullPointerException ne) {
            return;
        }
    }

    private Container createContentPane() {
        JSplitPane topSplitter = new JSplitPane();
        topSplitter.setLeftComponent(createTreePanel());
        topSplitter.setRightComponent(createCardsPanel());

        HeaderPanel headerPanel = new HeaderPanel("Preference Dialog", "<html>Latiz allows users to customize its look and feel.  "
                + "Click on the tree to the left to customize the subcomponents.</html>");

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(topSplitter, BorderLayout.CENTER);
        panel.add(createButtonPanel(), BorderLayout.SOUTH);
        return panel;
    }

    private Component createTreePanel() {
        JScrollPane scroller = new JScrollPane(prefTree);
        scroller.setPreferredSize(new Dimension(200, 200));
        scroller.setMinimumSize(new Dimension(10, 10));
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(scroller);
        return panel;
    }

    private Component createCardsPanel() {
        JScrollPane scroller = new JScrollPane(cardsPanel);
        scroller.setMinimumSize(new Dimension(10, 10));
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(scroller);
        return panel;
    }

    private Component createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panel.add(createButton("OK", ACTION_ACCEPT));
        panel.add(createButton("Cancel", ACTION_CANCEL));
        panel.setBorder(new EmptyBorder(5, 0, 5, 5));
        return panel;
    }

    private JButton createButton(String label, String actionCommand) {
        JButton button = new JButtonX(label);
        button.setActionCommand(actionCommand);
        button.addActionListener(prefListener);
        button.setPreferredSize(new Dimension(80, 27));
        return button;
    }

    /**
     *
     * @author Dr. Richard St. John
     * @version $Revision$, $Date$
     */
    private class PreferencesCellRenderer extends DefaultTreeCellRenderer {

        @Override
        public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
            DefaultTreeCellRenderer label = (DefaultTreeCellRenderer) super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
            label.setIcon(null);
            label.setBorder(new EmptyBorder(1, 0, 1, 0));
            return label;
        }
    }

    /**
     *
     * @author Dr. Richard St. John
     * @version $Revision$, $Date$
     */
    private class PrefListener implements ActionListener, MouseListener {

        public void actionPerformed(ActionEvent e) {
            String command = e.getActionCommand();
            if (command.equalsIgnoreCase(ACTION_CANCEL)) {
                dispose();
            } else if (command.equalsIgnoreCase(ACTION_ACCEPT)) {
                actionAccept();
            }
        }

        private void actionAccept() {
            Iterator<AbstractPreferencePage> props = propSet.iterator();
            while (props.hasNext()) {
                props.next().fireAcceptAction();
            }

            Iterator<PreferencesListener> li = listeners.iterator();
            while (li.hasNext()) {
                li.next().preferencesUpdated();
            }
            dispose();
        }

        public void mouseClicked(MouseEvent e) {
        }

        public void mouseEntered(MouseEvent e) {
        }

        public void mouseExited(MouseEvent e) {
        }

        public void mouseReleased(MouseEvent e) {
        }

        public void mousePressed(MouseEvent e) {
            TreePath path = prefTree.getClosestPathForLocation(e.getX(), e.getY());
            if (path == null) {
                return;
            }
            prefTree.setSelectionPath(path);
            try {
                AbstractPreferencePage thisPage = (AbstractPreferencePage) ((DefaultMutableTreeNode) path.getLastPathComponent()).getUserObject();
                CardLayout cl = (CardLayout) cardsPanel.getLayout();
                cl.show(cardsPanel, thisPage.getTreeLabel());
            } catch (NullPointerException ne) {
                return;
            }
        }
    }
}
