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
package com.AandR.palette.paletteScene;

import com.AandR.latiz.core.lookup.LatizLookup;
import com.AandR.palette.globals.GlobalVariable;
import com.AandR.library.io.XmlFile;
import com.AandR.palette.cookies.PluginSelectionCookie;
import com.AandR.palette.dataWriter.AbstractDataWriter;
import com.AandR.palette.dataWriter.DefaultSavedOutputsImpl;
import com.AandR.palette.paletteScene.menus.SceneMainMenu;
import com.AandR.palette.plugin.AbstractPlugin;
import com.AandR.palette.plugin.IPluginConnection;
import com.AandR.palette.model.DefaultPaletteModel;
import com.AandR.palette.model.AbstractPaletteModel;
import com.AandR.palette.plugin.ParameterContainer;
import com.AandR.palette.runtime.IRuntimeManager;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.SwingWorker;
import javax.swing.filechooser.FileNameExtensionFilter;
import net.miginfocom.swing.MigLayout;
import org.jdom.Element;
import org.netbeans.api.visual.action.ActionFactory;
import org.netbeans.api.visual.router.Router;
import org.netbeans.api.visual.router.RouterFactory;
import org.netbeans.api.visual.widget.BirdViewController;
import org.netbeans.api.visual.widget.ConnectionWidget;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.Exceptions;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;
import org.openide.util.NbPreferences;
import org.openide.windows.IOProvider;
import org.openide.windows.TopComponent;

/**
 *
 * @author rstjohn
 */
public class PaletteScenePanel extends JPanel implements PropertyChangeListener {

    private static final String ACTION_RUN = "ACTION_RUN";
    private static final String ACTION_STOP = "ACTION_STOP";
    private static final String ACTION_RUN_CONFIGURATION = "ACTION_RUN_CONFIGURATION";
    private DefaultModelWorker modelWorker;
    private NonThreadedDefaultModelWorker nonThreadedModelWorker;
    private DefaultPaletteModel paletteModel;
    private JButton runButton,  stopButton,  runConfigButton;
    private JTextField stopTimeField;
    private PaletteScene scene;
    private String displayName;
    private RunConfigurationPanel runConfigPanel;
    private AbstractDataWriter dataWriter;
    private boolean isRunningNonThreaded;

    public PaletteScenePanel() {
        LatizLookup.getDefault().addToLookup(new PluginConnectionImpl());
        initialize();
        createContentPane();
    }

    private void initialize() {
        paletteModel = new DefaultPaletteModel();
        paletteModel.setName(this.getName());

        ActionListener toolbarListener = new ToolbarListener();
        Icon runIcon = new ImageIcon(ImageUtilities.loadImage("com/AandR/palette/resources/run16.png"));
        runButton = createButton("Run", runIcon, ACTION_RUN, toolbarListener);

        Icon runConfigIcon = new ImageIcon(ImageUtilities.loadImage("com/AandR/palette/resources/runConfig16.png"));
        runConfigButton = createButton("Settings", runConfigIcon, ACTION_RUN_CONFIGURATION, toolbarListener);

        Icon stopIcon = new ImageIcon(ImageUtilities.loadImage("com/AandR/palette/resources/stop16.png"));
        stopButton = createButton("Stop", stopIcon, ACTION_STOP, toolbarListener);
        stopButton.setEnabled(false);

        stopTimeField = new JTextField("-1", 8);
        stopTimeField.setHorizontalAlignment(JTextField.RIGHT);

        scene = new PaletteScene(paletteModel);
        scene.getActions().addAction(ActionFactory.createPopupMenuAction(new SceneMainMenu(scene, new MenuListener())));
    }

    @Override
    public void setName(String name) {
        super.setName(name);
        scene.setName(name);
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
        scene.setDisplayName(displayName);
    }

    public AbstractPlugin getPlugin(String name) {
        return scene.getPluginsMap().get(name);
    }

    private void createContentPane() {
        JToolBar toolbar = new JToolBar();
        toolbar.setFloatable(false);
        toolbar.setLayout(new MigLayout("ins 0, gap 5", "", ""));
        toolbar.add(runButton);
        toolbar.add(stopButton);

        JPanel toolbarPanel = new JPanel(new MigLayout("ins 0", "[]10[][]push", "2[]"));
        toolbarPanel.add(toolbar);
        toolbarPanel.add(new JLabel("Stop Time"));
        toolbarPanel.add(stopTimeField, "");

        setLayout(new MigLayout("ins 0, fill"));
        add(toolbarPanel, "pushx, growx, wrap");
        toolbar.add(runConfigButton);
        add(new JScrollPane(scene.createView()), "push, grow");
    }

    private JButton createButton(String text, Icon icon, String actionCommand, ActionListener al) {
        JButton b = new JButton(text, icon);
        b.setActionCommand(actionCommand);
        b.addActionListener(al);
        return b;
    }

    @SuppressWarnings(value="unchecked")
    private void selectPluginNode(PluginNode pluginNode) {
        HashSet selectedObjects = new HashSet(scene.getSelectedObjects());
        if (selectedObjects.isEmpty() || selectedObjects.size() < 2 || !selectedObjects.contains(pluginNode)) {
            selectedObjects = new HashSet();
            selectedObjects.add(pluginNode);
        }
        scene.setSelectedObjects(selectedObjects);
        scene.setFocusedObject(null);
        scene.validate();
    }

    public void actionLoad() {
        final JFileChooser chooser = new JFileChooser(NbPreferences.forModule(PaletteScenePanel.class).get("currentDirectory", System.getProperty("user.home")));
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Palette Workspace Files", "latiz", "lat5");
        chooser.setFileFilter(filter);
        JButton jumpButton = new JButton("Jump to Palette's Default Workspace Directory");
        jumpButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                String FS = File.separator;
                File workspaceDir = new File(System.getProperty("user.home") + FS + ".AandRcreations" + FS + "latiz" + FS + "perspectives" + FS + "com.AandR.palette.workspaces" + FS + "workspaces");
                if (!workspaceDir.exists()) {
                    return;
                }
                chooser.setCurrentDirectory(workspaceDir);
            }
        });

        chooser.add(jumpButton, BorderLayout.SOUTH);
        chooser.setDialogTitle("Open Scene...");
        chooser.setMultiSelectionEnabled(false);
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        if (chooser.showOpenDialog(scene.getView()) != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File selectedFile = chooser.getSelectedFile();
        NbPreferences.forModule(PaletteScenePanel.class).put("currentDirectory", selectedFile.getParent());
        try {
            scene.loadWorkspace(selectedFile);
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    public void actionSave() {
        if (scene.getCurrentFile() == null) {
            actionSaveAs();
            return;
        }
        notifyPaletteSaved(scene.getCurrentFile());
    }

    public void actionSaveAs() {
        final JFileChooser chooser = new JFileChooser(NbPreferences.forModule(PaletteScenePanel.class).get("currentDirectory", System.getProperty("user.home")));
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Palette Workspace Files", "latiz", "lat5");
        chooser.setFileFilter(filter);
        JButton jumpButton = new JButton("Jump to Palette's Default Workspace Directory");
        jumpButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                String FS = File.separator;
                File workspaceDir = new File(System.getProperty("user.home") + FS + ".AandRcreations" + FS + "latiz" + FS + "perspectives" + FS + "com.AandR.palette.workspaces" + FS + "workspaces");
                if (!workspaceDir.exists()) {
                    return;
                }
                chooser.setCurrentDirectory(workspaceDir);
            }
        });

        chooser.add(jumpButton, BorderLayout.SOUTH);
        chooser.setDialogTitle("Save Scene...");
        chooser.setMultiSelectionEnabled(false);
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        if (chooser.showSaveDialog(scene.getView()) != JFileChooser.APPROVE_OPTION) {
            return;
        }
        File selectedFile = chooser.getSelectedFile();
        if (!selectedFile.getName().endsWith(".latiz")) {
            selectedFile = new File(selectedFile.getParent(), selectedFile.getName() + ".latiz");
        }

        NbPreferences.forModule(PaletteScenePanel.class).put("currentDirectory", selectedFile.getParent());
        if (!selectedFile.exists()) {
            notifyPaletteSaved(selectedFile);
            return;
        }

        // File already exists.  Prompt the user to make a decision.
        NotifyDescriptor d = new NotifyDescriptor.Confirmation("<HTML><B>" + selectedFile.getName() + "</B> exists.<BR><BR>Do you want to overwrite the existing file?</HTML>", "File Exists Warning", NotifyDescriptor.YES_NO_CANCEL_OPTION);
        Object ans = DialogDisplayer.getDefault().notify(d);
        if (ans == NotifyDescriptor.YES_OPTION) {
            notifyPaletteSaved(chooser.getSelectedFile());
        } else if (ans == NotifyDescriptor.NO_OPTION) {
            actionSaveAs();
        }
        return;
    }

    void actionClearPalette() {
        scene.actionClearPalette();
    }

    public void actionRun(boolean runInNewThread) {
        if (runInNewThread) {
            actionRun();
        } else {
            isRunningNonThreaded = true;
            notifyPaletteModelStarted();
            paletteModel.setStopTime(Double.parseDouble(stopTimeField.getText()));

            Map<String, AbstractPlugin> pluginMap = paletteModel.getPlugins();
            pluginMap.clear();
            pluginMap.putAll(scene.getPluginsMap());

            if (runConfigPanel == null) {
                runConfigPanel = new RunConfigurationPanel();
            }
            if (runConfigPanel.isDataSavingOn()) {
                dataWriter = runConfigPanel.getDataWriterNewInstance();
                dataWriter.registerOutputObservation(paletteModel);
                dataWriter.setUp();
            }
            nonThreadedModelWorker = new NonThreadedDefaultModelWorker(runConfigPanel.getRuntimeManagerNewInstance(), paletteModel);
            try {
                nonThreadedModelWorker.run();
            } catch (Exception ex) {
                NotifyDescriptor nd = new NotifyDescriptor.Exception(ex);
                DialogDisplayer.getDefault().notify(nd);
            }
        }
    }

    public void actionRun() {
        isRunningNonThreaded = false;
        notifyPaletteModelStarted();
        paletteModel.setStopTime(Double.parseDouble(stopTimeField.getText()));

        Map<String, AbstractPlugin> pluginMap = paletteModel.getPlugins();
        pluginMap.clear();
        pluginMap.putAll(scene.getPluginsMap());

        if (runConfigPanel == null) {
            runConfigPanel = new RunConfigurationPanel();
        }
        if (runConfigPanel.isDataSavingOn()) {
            dataWriter = runConfigPanel.getDataWriterNewInstance();
            dataWriter.registerOutputObservation(paletteModel);
            dataWriter.setUp();
        }

        modelWorker = new DefaultModelWorker(runConfigPanel.getRuntimeManagerNewInstance(), paletteModel);
        modelWorker.execute();
    }

    public void actionStop() {
        if (isRunningNonThreaded) {
            nonThreadedModelWorker.cancel();
        } else {
            modelWorker.cancel();
            notifyPaletteModelStopped();
        }
    }

    void notifyPaletteModelStarted() {
        Lookup.Result<IPaletteModelRun> result = LatizLookup.getDefault().lookupResult(IPaletteModelRun.class);
        for (IPaletteModelRun ipc : result.allInstances()) {
            ipc.modelStarted(paletteModel);
        }
    }

    void notifyPaletteModelStopped() {
        Lookup.Result<IPaletteModelRun> result = LatizLookup.getDefault().lookupResult(IPaletteModelRun.class);
        for (IPaletteModelRun ipc : result.allInstances()) {
            ipc.modelStopped(paletteModel);
        }
    }

    private void notifyPaletteSaved(File file) {
        Element root = new Element("latizWorkspace");
        root.setAttribute("file", file.getPath());
        SceneSerializer.serialize(scene, root);
        scene.setCurrentFile(file);

        // Save globals
        Element ge = new Element("globalParameters");
        Element var;
        for (GlobalVariable gv : paletteModel.getGlobalsMap().values()) {
            var = new Element("global");
            var.setAttribute("var", gv.getLabel());
            var.setAttribute("value", gv.getValue());
            var.setAttribute("public", String.valueOf(gv.isPublic()));
            ge.addContent(var);
        }
        root.addContent(ge);

        // Save Recorded Outputs
        Element roe = new Element("recordedOutputs");
        roe.setAttribute("use", String.valueOf(scene.isDataSavingRequested()));
        Element thisSavedDataElement, thisDataElement;
        LinkedHashMap<String, DefaultSavedOutputsImpl> thisSavedDataMap;
        DefaultSavedOutputsImpl data;
        for (String pluginName : paletteModel.getSavedOutputsMap().keySet()) {
            thisSavedDataElement = new Element("plugin");
            thisSavedDataElement.setAttribute("name", pluginName);

            thisSavedDataMap = paletteModel.getSavedOutputsMap().get(pluginName);
            for (String dataName : thisSavedDataMap.keySet()) {
                data = thisSavedDataMap.get(dataName);
                thisDataElement = new Element("dataItem");
                thisDataElement.setAttribute("name", data.getDatasetName());
                thisDataElement.setAttribute("userDefined", String.valueOf(data.isUserDefined()));
                thisDataElement.setAttribute("beginTime", data.getBeginTime());
                thisDataElement.setAttribute("endTime", data.getEndTime());
                thisDataElement.setAttribute("period", data.getPeriod());
                thisDataElement.setAttribute("maxFrames", data.getMaxIterationCount());
                thisSavedDataElement.addContent(thisDataElement);
            }
            roe.addContent(thisSavedDataElement);
        }
        root.addContent(roe);

        Lookup.Result<IWorkspaceSaved> results = LatizLookup.getDefault().lookupResult(IWorkspaceSaved.class);
        for (IWorkspaceSaved ips : results.allInstances()) {
            ips.save(root);
        }
        try {
            XmlFile.write(file, root);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }

        String oldName = scene.getName();
        File currentFile = scene.getCurrentFile();
        Lookup.Result<IPaletteSaved> savedResults = LatizLookup.getDefault().lookupResult(IPaletteSaved.class);
        for (IPaletteSaved ips : savedResults.allInstances()) {
            ips.save(oldName, currentFile);
        }
        IOProvider.getDefault().getIO("Output", false).getOut().println(file.getPath() + " was saved successfully.");
    }

    @SuppressWarnings(value = "unchecked")
    public void propertyChange(PropertyChangeEvent evt) {
        if (!evt.getPropertyName().equals(TopComponent.getRegistry().PROP_ACTIVATED)) {
            return;
        }

        Object o = evt.getNewValue();
        if (o instanceof ParameterContainer) {
            ParameterContainer pluginTopComponent = (ParameterContainer) o;
            Collection<PluginNode> nodes = scene.getNodes();

            PluginNode pluginNode = null;
            for (PluginNode node : nodes) {
                if (pluginTopComponent.getName().equals(node.getName())) {
                    pluginNode = node;
                    break;
                }
            }

            if (pluginNode == null) {
                return;
            }

            selectPluginNode(pluginNode);

        } else if (o instanceof PaletteEditor) {
            PaletteEditor pe = (PaletteEditor) o;

            // Only need to reset the selected palette plugin if the user switched from one palette to another.
            if (!(evt.getOldValue() instanceof PaletteEditor)) {
                return;
            }

            LatizLookup.getDefault().removeAllFromLookup(PluginSelectionCookie.class);
            Set objects = pe.getScenePanel().getScene().getSelectedObjects();
            for (final Object obj : new ArrayList<Object>(objects)) {
                if (!(obj instanceof PluginNode)) {
                    continue;
                }
                LatizLookup.getDefault().addToLookup(new PluginSelectionCookie() {

                    public AbstractPlugin getSelectedPlugin() {
                        return ((PluginNode) obj).getPlugin();
                    }
                });
            }
        }
    }

    public void setCurrentFile(File file) {
        scene.setCurrentFile(file);
    }

    public File getCurrentFile() {
        return scene.getCurrentFile();
    }

    public DefaultPaletteModel getPaletteModel() {
        return paletteModel;
    }

    public PaletteScene getScene() {
        return scene;
    }

    /**
     *
     */
    private class ToolbarListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            String command = e.getActionCommand();
            if (command.equals(ACTION_RUN)) {
                actionRun();
            } else if (command.equals(ACTION_STOP)) {
                actionStop();
            } else if (command.equals(ACTION_RUN_CONFIGURATION)) {
                actionRunConfig();
            }
        }

        private void actionRunConfig() {
            if (runConfigPanel == null) {
                runConfigPanel = new RunConfigurationPanel();
            }
            DialogDisplayer.getDefault().createDialog(new DialogDescriptor(runConfigPanel, "Settings")).setVisible(true);
        }
    }

    /**
     *
     */
    private class MenuListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            String command = e.getActionCommand();
            if (command.equals(SceneMainMenu.ACTION_LOAD_SCENE)) {
                actionLoadScene();
            } else if (command.equals(SceneMainMenu.ACTION_SAVE_SCENE)) {
                actionSave();
            } else if (command.equals(SceneMainMenu.ACTION_SAVE_AS_SCENE)) {
                actionSaveAs();
            } else if (command.equals(SceneMainMenu.ACTION_SHOW_BIRD_VIEW)) {
                actionShowBirdView();
            } else if (command.equals(SceneMainMenu.ACTION_CLEAR_PALETTE)) {
                actionClearPalette();
            } else if (command.equals(SceneMainMenu.ACTION_DISCONNECT_ALL)) {
                actionDisconnectAll();
            } else if (command.equals(SceneMainMenu.ACTION_CONNECT_ALL)) {
                actionConnectAll();
            } else if (command.equals(SceneMainMenu.ACTION_ROUTER_POLICY + "_FREE")) {
                actionRouterPolicy(SceneMainMenu.ROUTER_POLICY_FREE);
            } else if (command.equals(SceneMainMenu.ACTION_ROUTER_POLICY + "_DIRECT")) {
                actionRouterPolicy(SceneMainMenu.ROUTER_POLICY_DIRECT);
            } else if (command.equals(SceneMainMenu.ACTION_ROUTER_POLICY + "_ORTHO")) {
                actionRouterPolicy(SceneMainMenu.ROUTER_POLICY_ORTHOGONAL);
            }
        }

        private void actionRouterPolicy(int policy) {
            Router router;
            switch (policy) {
                case SceneMainMenu.ROUTER_POLICY_FREE:
                    router = RouterFactory.createFreeRouter();
                    break;
                case SceneMainMenu.ROUTER_POLICY_DIRECT:
                    router = RouterFactory.createDirectRouter();
                    break;
                case SceneMainMenu.ROUTER_POLICY_ORTHOGONAL:
                    router = RouterFactory.createOrthogonalSearchRouter((scene).getMainLayer());
                    break;
                default:
                    router = RouterFactory.createFreeRouter();
            }
            for (ConnectorEdge edge : scene.getEdges()) {
                ((ConnectionWidget) scene.findWidget(edge)).setRouter(router);
            //((ConnectionWidget) scene.findWidget(edge)).setForeground(Color.RED);
            }
        }

        private void actionLoadScene() {
            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle("Load Scene...");
            chooser.setMultiSelectionEnabled(false);
            chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
//            Element root = null;
            if (chooser.showOpenDialog(scene.getView()) == JFileChooser.CANCEL_OPTION) {
                return;
            }

            // Load palette scene
            scene.actionClearPalette();
            try {
                scene.loadWorkspace(chooser.getSelectedFile());
            } catch (Exception ex) {
                Exceptions.printStackTrace(ex);
            }
            scene.validate();
        }

        private void actionShowBirdView() {
            BirdViewController bvc = scene.createBirdView();
            scene.createBirdView().show();
        }

        private void actionDisconnectAll() {
            for (ConnectorEdge edge : new ArrayList<ConnectorEdge>(scene.getEdges())) {
                scene.removeEdge(edge);
            }
            scene.validate();
        }

        private void actionConnectAll() {
            actionDisconnectAll();
            TreeMap<Double, PluginNode> sortedNodeMap = new TreeMap<Double, PluginNode>();
            Collection<PluginNode> nodes = scene.getNodes();
            for (PluginNode pn : nodes) {
                sortedNodeMap.put(scene.findWidget(pn).getPreferredLocation().distance(0, 0), pn);
            }

            int count = 0;
            PluginNode[] ns = new PluginNode[sortedNodeMap.size()];
            for (PluginNode pn : sortedNodeMap.values()) {
                ns[count++] = pn;
            }

            ConnectorEdge edge;
            for (int i = 0; i < ns.length - 1; i++) {
                edge = new ConnectorEdge(ns[i].getName() + ">" + ns[i + 1].getName());
                scene.addEdge(edge);
                scene.setEdgeSource(edge, ns[i]);
                scene.setEdgeTarget(edge, ns[i + 1]);
                notifyConnectionMade(ns[i].getPlugin(), ns[i + 1].getPlugin());
            }
            scene.validate();
        }

        protected void notifyConnectionMade(AbstractPlugin source, AbstractPlugin target) {
            Lookup.Result<IPluginConnection> pcis = LatizLookup.getDefault().lookupResult(IPluginConnection.class);
            for (IPluginConnection pci : pcis.allInstances()) {
                pci.connectionMade(scene, source, target);
            }
        }
    }

    public class NonThreadedDefaultModelWorker {

        private IRuntimeManager iRuntimeManager;
        private AbstractPaletteModel paletteModel;
        private Boolean isRunSuccesful;

        public NonThreadedDefaultModelWorker(IRuntimeManager irm, AbstractPaletteModel ipm) {
            iRuntimeManager = irm;
            paletteModel = ipm;
            isRunSuccesful = true;
        }

        protected Boolean run() throws Exception {
            IOProvider.getDefault().getIO("Output", false).getOut().println("Running NON Threaded");
            runButton.setEnabled(false);
            stopButton.setEnabled(true);
            iRuntimeManager.executePaletteModel(paletteModel);
            done();
            return isRunSuccesful;
        }

        protected void done() {
            runButton.setEnabled(true);
            stopButton.setEnabled(false);
            if (runConfigPanel.isDataSavingOn()) {
                dataWriter.tearDown();
            }
            notifyPaletteModelStopped();
        }

        public void cancel() {
            iRuntimeManager.requestSimulationCancellation();
            done();
        }
    }

    public class DefaultModelWorker extends SwingWorker<Boolean, Void> {

        private IRuntimeManager iRuntimeManager;
        private AbstractPaletteModel paletteModel;
        private Boolean isRunSuccesful;

        public DefaultModelWorker(IRuntimeManager irm, AbstractPaletteModel ipm) {
            iRuntimeManager = irm;
            paletteModel = ipm;
            isRunSuccesful = true;
        }

        @Override
        protected Boolean doInBackground() throws Exception {
            runButton.setEnabled(false);
            stopButton.setEnabled(true);
            iRuntimeManager.executePaletteModel(paletteModel);
            return isRunSuccesful;
        }

        @Override
        protected void done() {
            runButton.setEnabled(true);
            stopButton.setEnabled(false);
            if (runConfigPanel.isDataSavingOn()) {
                dataWriter.tearDown();
            }
            notifyPaletteModelStopped();
        }

        public void cancel() {
            iRuntimeManager.requestSimulationCancellation();
            super.cancel(true);
        }
    }

    /**
     *
     */
    private class PluginConnectionImpl implements IPluginConnection {

        public void connectionMade(PaletteScene scene, AbstractPlugin scr, AbstractPlugin target) {
        }

        public void connectionRemoved(PaletteScene scene, AbstractPlugin sourcePlugin, AbstractPlugin targetPlugin) {
            List<String> ioConnections = getPaletteModel().getConnections();
            String[] pluginSplit;
            for (String ioConnection : new ArrayList<String>(ioConnections)) {
                pluginSplit = ioConnection.split(">");
                if (pluginSplit[0].startsWith(sourcePlugin.getName() + "::") && pluginSplit[1].startsWith(targetPlugin.getName() + "::")) {
                    ioConnections.remove(ioConnection);
                }
            }
        }
    }
}
