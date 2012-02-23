package com.AandR.latiz.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTree;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;

import org.jdom.Element;

import com.AandR.gui.dropSupport.DropEvent;
import com.AandR.gui.dropSupport.DropListener;
import com.AandR.gui.dropSupport.JPanelWithDropSupport;
import com.AandR.gui.ui.JButtonX;
import com.AandR.gui.ui.JToolbarButton;
import com.AandR.latiz.dev.AbstractPlugin;
import com.AandR.latiz.interfaces.DisplayableInterface;
import com.AandR.latiz.interfaces.PerspectiveInterface;
import com.AandR.latiz.listeners.PalettePanelListener;
import com.AandR.latiz.listeners.PluginChangedListener;
import com.AandR.latiz.resources.Resources;

/**
 * @author Dr. Richard St. John
 * @version $Revision$, $Date$
 */
public class HeadUpDisplayPanel extends JPanel implements PluginChangedListener, PalettePanelListener, PerspectiveInterface {

    private boolean isPaused = true;
    private DragAndDropTree treePlugins;
    private HashMap<String, Integer> componentMap;
    private JDialog dialog;
    private JPanel plotPanel, rootPane, propertiesPanel;
    private JSpinner rowSpinner, colSpinner;
    private JButtonX pauseButton;
    private LinkedHashSet<AbstractPlugin> availablePlugins;

    public HeadUpDisplayPanel() {
        initialize();

        JToolbarButton undockButton = new JToolbarButton(Resources.createIcon("remove16.png"));
        undockButton.setToolTipText("Undock");
        undockButton.setPreferredSize(new Dimension(20, 20));
        undockButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                if (dialog == null) {
                    createUndockedDialog();
                }
                showInDialog();
            }
        });

        JPanel undockButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        undockButtonPanel.add(undockButton);

        JPanel gridsizePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        gridsizePanel.add(new JLabel("Rows"));
        gridsizePanel.add(rowSpinner);
        gridsizePanel.add(new JLabel("Cols"));
        gridsizePanel.add(colSpinner);
        gridsizePanel.add(pauseButton);

        JPanel northPanel = new JPanel(new BorderLayout());
        northPanel.add(gridsizePanel, BorderLayout.WEST);
        northPanel.add(undockButton, BorderLayout.EAST);

        JScrollPane scroller = new JScrollPane(plotPanel);
        rootPane = new JPanel(new BorderLayout());
        rootPane.add(northPanel, BorderLayout.NORTH);
        rootPane.add(scroller);

        setLayout(new BorderLayout());
        add(rootPane);
    }

    private void initialize() {
        availablePlugins = new LinkedHashSet<AbstractPlugin>();
        componentMap = new HashMap<String, Integer>();

        pauseButton = new JButtonX("OFF");
        pauseButton.setFocusable(false);
        pauseButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                isPaused = !isPaused;
                pauseButton.setText(isPaused ? "OFF" : "ON");
                setAllPauseButtons(isPaused);
            }
        });

        GridChangeListener gridChangeListener = new GridChangeListener();
        rowSpinner = new JSpinner(new SpinnerNumberModel(2, 1, 6, 1));
        rowSpinner.addChangeListener(gridChangeListener);
        colSpinner = new JSpinner(new SpinnerNumberModel(2, 1, 10, 1));
        colSpinner.addChangeListener(gridChangeListener);

        plotPanel = new JPanel(new GridLayout(2, 2, 5, 5));
        plotPanel.setBackground(Color.BLACK);
        plotPanel.add(new HUDcomponent());
        plotPanel.add(new HUDcomponent());
        plotPanel.add(new HUDcomponent());
        plotPanel.add(new HUDcomponent());

        treePlugins = new DragAndDropTree(new DefaultTreeModel(new DefaultMutableTreeNode("Plugins")));
        treePlugins.setCellRenderer(new PluginTreeRenderer());
        treePlugins.setShowsRootHandles(true);
        treePlugins.expandRow(0);

        propertiesPanel = new JPanel(new BorderLayout());
        propertiesPanel.setBorder(new TitledBorder("Head-up Display Properties"));
        propertiesPanel.add(new JLabel("Available Plugins"), BorderLayout.NORTH);
        propertiesPanel.add(new JScrollPane(treePlugins), BorderLayout.CENTER);
    }

    private void setAllPauseButtons(boolean isPaused) {
        Component[] components = plotPanel.getComponents();
        for (Component c : components) {
            ((HUDcomponent) c).setPaused(isPaused);
        }
    }

    private void createUndockedDialog() {
        dialog = new JDialog((JDialog) null, "Console Panel", false);
        dialog.setLocationRelativeTo(null);
        dialog.setAlwaysOnTop(true);
        dialog.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        dialog.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                rootPane = (JPanel) dialog.getContentPane();
                dialog.setVisible(false);
                add(rootPane);
                repaint();
            }
        });
    }

    private void showInDialog() {
        if (dialog.isVisible()) {
            rootPane = (JPanel) dialog.getContentPane();
            dialog.setVisible(false);
            add(rootPane);
            revalidate();
            repaint();
        } else {
            remove(rootPane);
            revalidate();
            repaint();
            dialog.setContentPane(rootPane);
            dialog.pack();
            dialog.setVisible(true);
        }
    }

    private void addPluginToTree(AbstractPlugin p) {
        if (!(p instanceof DisplayableInterface)) {
            return;
        }
        DefaultTreeModel model = (DefaultTreeModel) treePlugins.getModel();
        DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode) model.getRoot();
        model.insertNodeInto(new DefaultMutableTreeNode(p.getName()), rootNode, rootNode.getChildCount());
    }

    public Component getTopLeftComponent() {
        return propertiesPanel;
    }

    public void updateDisplay(String pluginName, Component component) {
        Integer componentIndex = componentMap.get(pluginName);
        if (componentIndex == null) {
            return;
        }

        HUDcomponent huDcomponent = (HUDcomponent) plotPanel.getComponent(componentIndex);
        huDcomponent.updateComponent(component);
    }

    public void pluginInputsChanged(AbstractPlugin p) {
    }

    public void pluginNameChanged(String oldName, String newName) {
    }

    public void pluginOutputsChanged(AbstractPlugin p) {
    }

    public synchronized void pluginDisplayChanged(final AbstractPlugin p) {
        if (isPaused) {
            return;
        }
        Integer index = componentMap.get(p.getName());
        if (index == null) {
            return;
        }
        if (((HUDcomponent) plotPanel.getComponent(index)).isPaused) {
            return;
        }
        try {
            SwingUtilities.invokeAndWait(new Runnable() {

                public void run() {
                    updateDisplay(p.getName(), ((DisplayableInterface) p).getDisplayableComponent());
                }
            });
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public void connectionMade(Connector c) {
    }

    public void connectionRemoved(Connector c) {
    }

    public void connectionWillBeRemoved(Connector c) {
    }

    public void mousePressed() {
    }

    public void mouseReleased() {
    }

    public void pluginWillBeRemoved(AbstractPlugin p) {
    }

    public void paletteCleared() {
        treePlugins.setModel(new DefaultTreeModel(new DefaultMutableTreeNode("Plugins")));
        HUDcomponent thisHudComponent;
        Component[] hudComponents = plotPanel.getComponents();
        for (Component c : hudComponents) {
            thisHudComponent = (HUDcomponent) c;
            thisHudComponent.actionRemoveHUDComponent();
        }
    }

    public void paletteLoaded(File file, Element latizRoot) {
        Element hudElement = latizRoot.getChild("HeadUpDisplay");
        int rows = Integer.parseInt(hudElement.getAttributeValue("rows"));
        int cols = Integer.parseInt(hudElement.getAttributeValue("cols"));
        rowSpinner.setValue(rows);
        colSpinner.setValue(cols);

        String pluginName;
        HUDcomponent thisHudComponent;
        int location;
        componentMap.clear();
        List<Element> items = hudElement.getChildren("item");
        for (Element e : items) {
            pluginName = e.getAttributeValue("pluginName");
            location = new Integer(e.getAttributeValue("location"));
            thisHudComponent = (HUDcomponent) plotPanel.getComponent(location);
            thisHudComponent.titleLabel.setText(pluginName);
            thisHudComponent.informationLabel.setText("");
            boolean isThisPaused = Boolean.parseBoolean(e.getAttributeValue("isPaused"));
            isPaused = isPaused & isThisPaused;
            thisHudComponent.setPaused(isThisPaused);
            thisHudComponent.updateComponent(((DisplayableInterface) findPlugin(pluginName)).getDisplayableComponent());
            componentMap.put(pluginName, location);
        }
        pauseButton.setText(isPaused ? "OFF" : "ON");
    }

    public void paletteSaved(File file, Element latizRoot) {
        Element hudElement = new Element("HeadUpDisplay");
        hudElement.setAttribute("rows", rowSpinner.getValue().toString());
        hudElement.setAttribute("cols", colSpinner.getValue().toString());
        Element thisHudElement;
        for (String key : componentMap.keySet()) {
            thisHudElement = new Element("item");
            thisHudElement.setAttribute("pluginName", key);
            thisHudElement.setAttribute("location", componentMap.get(key).toString());

            boolean paused = ((HUDcomponent) plotPanel.getComponent(componentMap.get(key))).isPaused;
            thisHudElement.setAttribute("isPaused", String.valueOf(paused));
            hudElement.addContent(thisHudElement);
        }
        latizRoot.addContent(hudElement);
    }

    public void pluginDropped(AbstractPlugin p) {
        availablePlugins.add(p);
        addPluginToTree(p);
    }

    private AbstractPlugin findPlugin(String name) {
        for (AbstractPlugin p : availablePlugins) {
            if (p.getName().equals(name)) {
                return p;
            }
        }
        return null;
    }

    public void pluginRemoved(AbstractPlugin p) {
        availablePlugins.remove(p);
    }

    /**
     *
     * @author Dr. Richard St. John
     * @version $Revision$, $Date$
     */
    private class HUDcomponent extends JPanelWithDropSupport implements ActionListener, /*ItemListener,*/ DropListener {

        private boolean isPaused = true;
        private Component component;
        private JLabel titleLabel, informationLabel;
        private JButtonX pauseButton;
        private String pluginName;

        public HUDcomponent() {
            addDropListener(this);

            pauseButton = new JButtonX("OFF");
            pauseButton.setFocusable(false);
            pauseButton.addActionListener(this);
            pauseButton.setActionCommand("PAUSE_BUTTON");
            //pauseButton.addItemListener(this);

            JButton closeButton = new JButton(Resources.createIcon("remove16.png"));
            closeButton.setToolTipText("Clear this panel");
            closeButton.setBorderPainted(false);
            closeButton.setContentAreaFilled(false);
            closeButton.setFocusPainted(false);
            closeButton.addActionListener(this);
            closeButton.setActionCommand("REMOVE");

            titleLabel = new JLabel("");
            JPanel toolButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            toolButtonPanel.add(pauseButton);
            toolButtonPanel.add(titleLabel);

            informationLabel = new JLabel("Drop Plugin Here", SwingConstants.CENTER);

            JPanel northEastPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            northEastPanel.add(closeButton);

            JPanel northPanel = new JPanel(new BorderLayout());
            northPanel.add(toolButtonPanel, BorderLayout.WEST);
            northPanel.add(northEastPanel, BorderLayout.EAST);
            northPanel.setBorder(new EmptyBorder(0, 5, 0, 5));

            super.setLayout(new BorderLayout());
            super.add(northPanel, BorderLayout.NORTH);
            if (component != null) {
                super.add(component, BorderLayout.CENTER);
            } else {
                super.add(informationLabel, BorderLayout.CENTER);
            }
        }

        public void updateComponent(Component component) {
            if (component == null) {
                return;
            }

            if (this.component != null) {
                remove(this.component);
            }
            this.component = component;
            add(this.component, BorderLayout.CENTER);
            revalidate();
            repaint();
        }

        protected void setPaused(boolean isPaused) {
            this.isPaused = isPaused;
            pauseButton.setText(isPaused ? "OFF" : "ON");
        }

        public void actionPerformed(ActionEvent e) {
            String command = e.getActionCommand();
            if (command.equals("REMOVE")) {
                actionRemoveHUDComponent();
            } else if (command.equals("PAUSE_BUTTON")) {
                actionPauseButtonPressed();
            }
        }

        private void actionPauseButtonPressed() {
            isPaused = !isPaused;
            pauseButton.setText(isPaused ? "OFF" : "ON");

            if (!isPaused) {
                HeadUpDisplayPanel.this.isPaused = false;
                HeadUpDisplayPanel.this.pauseButton.setText("ON");
            }
        }

        private void actionRemoveHUDComponent() {
            titleLabel.setText("");
            informationLabel.setText("Drop Plugin Here");
            if (component == null) {
                return;
            }
            componentMap.remove(pluginName);
            remove(component);
            revalidate();
            repaint();
            component = null;
        }

        public void dropAction(DropEvent dropEvent) {
            pluginName = ((String[]) dropEvent.getDroppedItem())[0];
            int index = plotPanel.getComponentZOrder(this);
            componentMap.put(pluginName, index);
            titleLabel.setText(pluginName);
            informationLabel.setText("");
            AbstractPlugin droppedPlugin = findPlugin(pluginName);
            if (droppedPlugin == null) {
                return;
            }
            updateComponent(((DisplayableInterface) droppedPlugin).getDisplayableComponent());
            plotPanel.revalidate();
            plotPanel.repaint();
        }
    }

    /**
     *
     * @author Dr. Richard St. John
     * @version $Revision$, $Date$
     */
    private class GridChangeListener implements ChangeListener {

        public void stateChanged(ChangeEvent e) {
            int componentCount = plotPanel.getComponentCount();

            int rows = (Integer) rowSpinner.getValue();
            int cols = (Integer) colSpinner.getValue();

            if (componentCount < rows * cols) {
                for (int i = componentCount - 1; i < rows * cols - 1; i++) {
                    plotPanel.add(new HUDcomponent());
                }
            } else if (componentCount > rows * cols) {
                for (int i = componentCount - 1; i >= rows * cols; i--) {
                    HUDcomponent hud = (HUDcomponent) plotPanel.getComponent(i);
                    if (hud.component != null) {
                        componentMap.remove(hud.pluginName);
                    }
                    plotPanel.remove(hud);
                }
            }
            plotPanel.setLayout(new GridLayout(rows, cols, 5, 5));
            plotPanel.revalidate();
            plotPanel.repaint();
        }
    }

    /**
     *
     * @author Dr. Richard St. John
     * @version $Revision$, $Date$
     */
    private class PluginTreeRenderer extends DefaultTreeCellRenderer {

        private Icon pluginIcon = Resources.createIcon("plugin22.png");

        @Override
        public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
            super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
            if (leaf) {
                setIcon(pluginIcon);
            } else {
                setIcon(null);
            }
            setText(node.getUserObject().toString());
            setBorder(new EmptyBorder(2, 0, 2, 0));
            return this;
        }
    }
}
