package com.AandR.latiz.core;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.ToolTipManager;
import javax.swing.WindowConstants;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;


import com.AandR.beans.fileExplorerPanel.FileExplorerPropertiesManager;
import com.AandR.beans.jEditPanel.TextEditorPropertiesManager;
import com.AandR.gui.OptionsDialog;
import com.AandR.gui.SplashScreen;
import com.AandR.gui.ui.DefaultUI;
import com.AandR.gui.ui.JButtonX;
import com.AandR.gui.ui.LineBorderX;
import com.AandR.latiz.gui.GuiMaker;
import com.AandR.latiz.resources.Resources;

public class Latiz {

    private GuiMaker gui;
    private HashMap<String, LatizSystem> latizSystemsMap;
    private HashMap<String, SystemProcessStarter> runningSystems;
    private ToolbarListener toolBarListener;

    public Latiz() {
        latizSystemsMap = new HashMap<String, LatizSystem>();
        runningSystems = new HashMap<String, SystemProcessStarter>();
        toolBarListener = new ToolbarListener();
        createAndShowGUI();
    }

    private void createAndShowGUI() {
        File updateScript = new File("./updateApplication.bat");
        if (updateScript.exists()) {
            try {
                Runtime.getRuntime().exec("cmd /C updateApplication.bat");
                System.exit(0);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        final SplashScreen splash = new SplashScreen(Resources.createIcon("latizSplash.jpg"), true);
        final JLabel label = splash.getMessageLabel();
        label.setBackground(new Color(46, 86, 137));
        label.setForeground(new Color(224, 238, 251));
        label.setBorder(new CompoundBorder(new LineBorderX(new Color(224, 238, 251)), new EmptyBorder(2, 8, 2, 0)));
        label.setText("Reading User's Settings");

        DefaultUI.load();
        ToolTipManager.sharedInstance().setInitialDelay(300);
        ToolTipManager.sharedInstance().setReshowDelay(200);
        ToolTipManager.sharedInstance().setDismissDelay(10000);
        ToolTipManager.sharedInstance().setEnabled(true);

        File settingsDir = new File(System.getProperty("user.home") + File.separator + ".AandRcreations" + File.separator + "latiz");
        FileExplorerPropertiesManager.setPropertiesFile(settingsDir);
        TextEditorPropertiesManager.setPropertiesFile(settingsDir);
        PropertiesManager.setPropertiesFile(settingsDir);
        PropertiesManager.getInstanceOf().readPropertiesDocument();

        File userLibDirectory = new File(settingsDir, "userLib");
        if (!userLibDirectory.exists()) {
            userLibDirectory.mkdirs();
        }

        try {
            PluginManager.getInstanceOf().registerPlugins(splash);
        } catch (ClassNotFoundException cnfe) {
        } catch (Exception e) {
            e.printStackTrace();
        }

        label.setText("Preparing to display Latiz");

        SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                gui = new GuiMaker(toolBarListener);
                gui.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
                gui.addWindowListener(new WindowAdapter() {

                    @Override
                    public void windowClosing(WindowEvent e) {
                        gui.saveSettings();
                        System.exit(0);
                    }
                });
            }
        });
        splash.dispose();
    }

    /**
     * @param args
     *            Comments:
     */
    public static void main(String[] args) {
        if (args.length == 1) {
            RemoteLatizEngine remoteLatizEngine = new RemoteLatizEngine(args[0]);
            return;
        }
        Latiz latiz = new Latiz();
    }

    /**
     *
     * @author Dr. Richard St. John
     * @version $Revision$, $Date$
     */
    private class ToolbarListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            String command = e.getActionCommand();
            if (command.equalsIgnoreCase(GuiMaker.ACTION_RUN)) {
                chooseLatizSystemAndRun();
            } else if (command.equalsIgnoreCase(GuiMaker.ACTION_STOP)) {
                chooseLatizSystemAndStop();
            } else if (command.equalsIgnoreCase(GuiMaker.ACTION_EXPAND_X)) {
                actionExpand(0, 100);
            } else if (command.equalsIgnoreCase(GuiMaker.ACTION_EXPAND_Y)) {
                actionExpand(1, 100);
            } else if (command.equalsIgnoreCase(GuiMaker.ACTION_CONTRACT_X)) {
                actionExpand(0, -100);
            } else if (command.equalsIgnoreCase(GuiMaker.ACTION_CONTRACT_Y)) {
                actionExpand(1, -100);
            } else if (command.equalsIgnoreCase("ACTION_SELECT_PATHS")) {
                String name = ((JMenuItem) e.getSource()).getText();
                if (name.equalsIgnoreCase("All Paths")) {
                    for (LatizSystem ls : gui.getPalette().getLatizSystems()) {
                        SystemProcessStarter s = new SystemProcessStarter();
                        s.setLatizSystem(ls);
                        runningSystems.put(s.getSystemName(), s);
                        s.runProcess();
                    }
                } else {
                    LatizSystem ls = latizSystemsMap.get(name);
                    SystemProcessStarter s = new SystemProcessStarter();
                    s.setLatizSystem(ls);
                    runningSystems.put(s.getSystemName(), s);
                    s.runProcess();
                }
            } else if (command.equalsIgnoreCase("ACTION_STOP_PATHS")) {
                String name = ((JMenuItem) e.getSource()).getText();
                ArrayList<SystemProcessStarter> systems = new ArrayList<SystemProcessStarter>();
                if (name.equalsIgnoreCase("Stop All")) {
                    for (SystemProcessStarter s : runningSystems.values()) {
                        systems.add(s);
                    }
                } else {
                    systems.add(runningSystems.get(name));
                }
                stopSystem(systems);
            }
        }

        public void notifySystemDone(SystemProcessStarter s) {
            runningSystems.remove(s.getSystemName());
        }

        private void stopSystem(final ArrayList<SystemProcessStarter> systems) {
            final JDialog dialog = new JDialog((JDialog) null, false);
            dialog.setTitle("Cancelling Simulation");
            dialog.setAlwaysOnTop(true);
            JPanel ceneterPanel = new JPanel(new GridLayout(1, 1, 1, 1));
            ceneterPanel.setBorder(new EmptyBorder(10, 15, 20, 15));
            String sims = "";
            for (SystemProcessStarter s : systems) {
                sims += "<li>" + s.getSystemName() + "</li>";
            }

            JLabel l1 = new JLabel("<HTML>Simulations cancelled successfully.<br>" + sims + "</HTML>");
            ceneterPanel.add(l1);

            JPanel buttonPanel = new JPanel();
            final JButtonX okButton = new JButtonX("OK");
            okButton.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    dialog.dispose();
                }
            });
            okButton.setEnabled(false);
            buttonPanel.add(okButton);

            JPanel p1 = new JPanel(new BorderLayout());
            p1.add(buttonPanel, BorderLayout.SOUTH);
            p1.add(ceneterPanel);
            dialog.setContentPane(p1);
            dialog.pack();
            dialog.setLocationRelativeTo(null);
            SwingUtilities.invokeLater(new Runnable() {

                public void run() {
                    dialog.setVisible(true);
                    for (SystemProcessStarter s : systems) {
                        s.getEm().requestSimulationCancellation();
                    }
                    okButton.setEnabled(true);
                }
            });
        }

        private void actionExpand(int flag, int amount) {
            Dimension d = gui.getPalette().getPreferredSize();
            if (flag == 0) {
                gui.getPalette().setPreferredSize(new Dimension(d.width + amount, d.height));
            } else {
                gui.getPalette().setPreferredSize(new Dimension(d.width, d.height + amount));
            }
            gui.getPalette().revalidate();
        }

        private void chooseLatizSystemAndRun() {
            HashSet<LatizSystem> latizSystems = gui.getPalette().getLatizSystems();
            latizSystemsMap.clear();
            if (latizSystems.isEmpty()) {
                return;
            }
            if (latizSystems.size() > 1) {
                JPopupMenu menu = new JPopupMenu("Run Path Chooser");
                Point loc = gui.getMousePosition();
                for (LatizSystem ls : latizSystems) {
                    latizSystemsMap.put(LatizSystemUtilities.getLatizSystemRunName(ls), ls);
                    JMenuItem thisItem = new JMenuItem(LatizSystemUtilities.getLatizSystemRunName(ls), Resources.createIcon("plugin22.png"));
                    thisItem.setActionCommand("ACTION_SELECT_PATHS");
                    thisItem.addActionListener(this);
                    menu.add(thisItem);
                }
                JMenuItem thisItem = new JMenuItem("All Paths", Resources.createIcon("plugin22.png"));
                thisItem.setActionCommand("ACTION_SELECT_PATHS");
                thisItem.addActionListener(this);
                menu.add(thisItem);
                menu.show(gui, loc.x, loc.y);
            } else {
                SystemProcessStarter s = new SystemProcessStarter();
                for (LatizSystem l : latizSystems) {
                    s.setLatizSystem(l);
                    runningSystems.put(s.getSystemName(), s);
                    s.runProcess();
                    break;
                }
            }

        }

        private void chooseLatizSystemAndStop() {
            if (runningSystems.isEmpty()) {
                return;
            }

            JPopupMenu menu = new JPopupMenu("Stop System Chooser");
            Point loc = gui.getMousePosition();
            for (SystemProcessStarter s : runningSystems.values()) {
                JMenuItem thisItem = new JMenuItem(s.getSystemName(), Resources.createIcon("plugin22.png"));
                thisItem.setActionCommand("ACTION_STOP_PATHS");
                thisItem.addActionListener(this);
                menu.add(thisItem);
            }
            JMenuItem thisItem = new JMenuItem("Stop All", Resources.createIcon("plugin22.png"));
            thisItem.setActionCommand("ACTION_STOP_PATHS");
            thisItem.addActionListener(this);
            menu.add(thisItem);
            menu.show(gui, loc.x, loc.y);
        }
    }

    private class SystemProcessStarter extends SwingWorker<Object, Void> {

        private LatizSystem latizSystem;
        private String systemName;
        private EventManager em;

        private void setLatizSystem(LatizSystem ls) {
            latizSystem = ls;
            systemName = LatizSystemUtilities.getLatizSystemRunName(ls);
        }

        private void runProcess() {
            this.execute();
        }

        private void actionRun() {
            gui.getIndicatorPanel().start();
            em = new EventManager();
            try {
                Number stopTime;
                stopTime = gui.getStopTimeField().parse();
                em.setLatFileTreePanel(gui.getLatFileTreePanel());
                em.executeLatizSystem(latizSystem, stopTime.doubleValue(), gui.getPalette().getPluginOutgoingConnectorMaps());
                gui.getIndicatorPanel().stop();
            } catch (ParseException e) {
                gui.getIndicatorPanel().stop();
                OptionsDialog options = new OptionsDialog(null, "Error", OptionsDialog.ERROR_ICON);
                options.showDialog("A valid numerical stop time time must be chosen. Zero is allowed.", 0);
            }
            toolBarListener.notifySystemDone(this);
        }

        @Override
        protected Object doInBackground() throws Exception {
            actionRun();
            return null;
        }

        public final String getSystemName() {
            return systemName;
        }

        public final void setSystemName(String systemName) {
            this.systemName = systemName;
        }

        public final EventManager getEm() {
            return em;
        }
    }
}
