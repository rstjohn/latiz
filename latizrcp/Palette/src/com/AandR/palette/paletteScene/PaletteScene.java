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
import com.AandR.latiz.pluginPanel.PluginLeaf;
import com.AandR.latizOptions.palette.LatizPaletteOptionsPanelController;
import com.AandR.palette.cookies.PaletteSelectionCookie;
import com.AandR.palette.cookies.PluginSelectionCookie;
import com.AandR.palette.dataWriter.DefaultSavedOutputsImpl;
import com.AandR.palette.globals.GlobalVariable;
import com.AandR.palette.model.DefaultPaletteModel;
import com.AandR.palette.paletteScene.menus.NodeMenu;
import com.AandR.palette.paletteScene.menus.EdgeMenu;
import com.AandR.palette.plugin.IPluginNameChange;
import com.AandR.palette.plugin.IPluginsAdded;
import com.AandR.palette.plugin.IPluginsRemoved;
import com.AandR.palette.plugin.AbstractPlugin;
import com.AandR.palette.plugin.ParameterContainer;
import com.AandR.palette.plugin.PluginUtilities;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.TexturePaint;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.prefs.Preferences;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.netbeans.api.visual.action.AcceptProvider;
import org.netbeans.api.visual.action.ActionFactory;
import org.netbeans.api.visual.action.ConnectorState;
import org.netbeans.api.visual.action.MoveProvider;
import org.netbeans.api.visual.action.SelectProvider;
import org.netbeans.api.visual.action.TextFieldInplaceEditor;
import org.netbeans.api.visual.action.WidgetAction;
import org.netbeans.api.visual.anchor.AnchorFactory;
import org.netbeans.api.visual.anchor.AnchorShape;
import org.netbeans.api.visual.anchor.AnchorShapeFactory;
import org.netbeans.api.visual.anchor.PointShape;
import org.netbeans.api.visual.graph.GraphScene;
import org.netbeans.api.visual.router.Router;
import org.netbeans.api.visual.router.RouterFactory;
import org.netbeans.api.visual.widget.ConnectionWidget;
import org.netbeans.api.visual.widget.LabelWidget;
import org.netbeans.api.visual.widget.LayerWidget;
import org.netbeans.api.visual.widget.Widget;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.filesystems.FileObject;
import org.openide.util.Exceptions;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;
import org.openide.util.NbPreferences;

/**
 *
 * @author stjohnr
 */
public class PaletteScene extends GraphScene<PluginNode, ConnectorEdge> {

    public static final DataFlavor DATAOBJECT_FLAVOR = new DataFlavor(org.openide.nodes.Node.class, "DataObjectFlavor");
//    private int pluginNameCounter = 1;
    private boolean dataSavingRequested = false;
    private List<String> ioConnections;
    private HashMap<String, LinkedHashMap<String, DefaultSavedOutputsImpl>> savedOutputsMap;
    private File currentFile;
    private DefaultPaletteModel defaultPaletteModel;
    private LayerWidget mainLayer,  connectionLayer,  interractionLayer,  backgroundLayer;
    private LinkedHashMap<String, GlobalVariable> globalsMap;
    private PluginSelectionCookieImpl pluginSelectionCookie;
    private Router router = RouterFactory.createFreeRouter();
    private String name,  displayName;
    private WidgetAction connectAction, /*reconnectAction,*/  moveControlPointAction,  editorAction,  multiMoveAction;
    private WidgetAction selectAction = ActionFactory.createSelectAction(new ObjectSelectProvider());

    public PaletteScene(DefaultPaletteModel defaultPaletteModel) {
        this.defaultPaletteModel = defaultPaletteModel;
        ioConnections = this.defaultPaletteModel.getConnections();
        savedOutputsMap = this.defaultPaletteModel.getSavedOutputsMap();
        globalsMap = this.defaultPaletteModel.getGlobalsMap();

        pluginSelectionCookie = new PluginSelectionCookieImpl();
        addChild(backgroundLayer = new LayerWidget(this));
        addChild(mainLayer = new LayerWidget(this));
        addChild(connectionLayer = new LayerWidget(this));
        addChild(interractionLayer = new LayerWidget(this));

        connectAction = ActionFactory.createExtendedConnectAction(interractionLayer, new SceneConnectProvider(this));
        //reconnectAction = ActionFactory.createReconnectAction(new SceneReconnectProvider(this));
        moveControlPointAction = ActionFactory.createFreeMoveControlPointAction();
        editorAction = ActionFactory.createInplaceEditorAction(new LabelTextFieldEditor());

        getActions().addAction(ActionFactory.createAcceptAction(new DropAcceptProvider()));
        getActions().addAction(selectAction);

        getActions().addAction(ActionFactory.createZoomAction());
        getActions().addAction(ActionFactory.createPanAction());

        getActions().addAction(ActionFactory.createRectangularSelectAction(this, backgroundLayer));

        initGrids();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        defaultPaletteModel.setName(name);
//        for(AbstractPlugin p : getPluginsMap().values()) {
//            p.setName(name);
//        }
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
        defaultPaletteModel.setDisplayName(displayName);
    }

    public DefaultPaletteModel getDefaultPaletteModel() {
        return defaultPaletteModel;
    }

    @Override
    protected Widget attachNodeWidget(PluginNode node) {
        PluginWidget widget = new PluginWidget(this, node);
        widget.revalidate();
        widget.repaint();
        //widget.setImage(node.getImage());
        //widget.setLabel(Long.toString(node.hashCode()));

        //double-click, the event is consumed while double-clicking only:
        widget.getLabelWidget().getActions().addAction(editorAction);

        //single-click, the event is not consumed:
        widget.getActions().addAction(createSelectAction());
        widget.getActions().addAction(new PluginSelectAction(this));

        widget.getActions().addAction(connectAction);

        widget.getActions().addAction(ActionFactory.createResizeAction());

        //mouse-dragged, the event is consumed while mouse is dragged:
        widget.getActions().addAction(ActionFactory.createMoveAction(null, new MultiMoveProvider()));
        //widget.getActions().addAction(ActionFactory.createAlignWithMoveAction(mainLayer, interractionLayer, null));

        //mouse-over, the event is consumed while the mouse is over the widget:
        widget.getActions().addAction(createWidgetHoverAction());

        mainLayer.addChild(widget);
        widget.getActions().addAction(ActionFactory.createPopupMenuAction(new NodeMenu(this)));
        validate();
        return widget;
    }

    @Override
    protected Widget attachEdgeWidget(ConnectorEdge arg0) {
        ConnectionWidget connection = new ConnectionWidget(this);

        Preferences pref = NbPreferences.forModule(LatizPaletteOptionsPanelController.class);
        Color lineColor = Color.decode(pref.get("lineColor", "#" + Integer.toHexString(Color.BLACK.getRGB()).substring(1)));
        connection.setLineColor(lineColor);

        Float lineWidth = NbPreferences.forModule(LatizPaletteOptionsPanelController.class).getFloat("lineWidth", 2f);
        connection.setStroke(new BasicStroke(lineWidth));

        connection.setRouter(router);
        connection.setToolTipText("Double-click for Add/Remove Control Point");

        //AnchorShape arrowHead = AnchorShapeFactory.createArrowAnchorShape(60, 20);
        AnchorShape arrowHead = AnchorShapeFactory.createTriangleAnchorShape(20, true, false, 20);

        connection.setTargetAnchorShape(arrowHead);
        //connection.setTargetAnchorShape(AnchorShape.TRIANGLE_FILLED);
        connection.setControlPointShape(PointShape.SQUARE_FILLED_BIG);
        connection.setEndPointShape(PointShape.SQUARE_FILLED_BIG);
        connectionLayer.addChild(connection);

        WidgetAction.Chain actionChain = connection.getActions();
//        actionChain.addAction(reconnectAction);
        actionChain.addAction(createSelectAction());
        actionChain.addAction(new ConnectionSelectAction(this));
        actionChain.addAction(ActionFactory.createAddRemoveControlPointAction());
        actionChain.addAction(moveControlPointAction);
        actionChain.addAction(ActionFactory.createPopupMenuAction(new EdgeMenu(this)));
        return connection;
    }

    @Override
    protected void attachEdgeSourceAnchor(ConnectorEdge edge, PluginNode oldSourceNode, PluginNode sourceNode) {
        ConnectionWidget widget = (ConnectionWidget) findWidget(edge);
        Widget sourceNodeWidget = findWidget(sourceNode);
        widget.setSourceAnchor(sourceNodeWidget != null ? AnchorFactory.createFreeRectangularAnchor(sourceNodeWidget, true) : null);
    }

    @Override
    protected void attachEdgeTargetAnchor(ConnectorEdge edge, PluginNode oldTargetNode, PluginNode targetNode) {
        ConnectionWidget widget = (ConnectionWidget) findWidget(edge);
        Widget targetNodeWidget = findWidget(targetNode);
        widget.setTargetAnchor(targetNodeWidget != null ? AnchorFactory.createFreeRectangularAnchor(targetNodeWidget, true) : null);
    }

    public LayerWidget getMainLayer() {
        return mainLayer;
    }

    public LayerWidget getConnectionLayer() {
        return connectionLayer;
    }

    public void initGrids() {
        Image sourceImage = ImageUtilities.loadImage("com/AandR/palette/resources/graph_lines_21.png"); // NOI18N
        int width = sourceImage.getWidth(null);
        int height = sourceImage.getHeight(null);
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = image.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.drawImage(sourceImage, 0, 0, null);
        g2.dispose();
        TexturePaint PAINT_BACKGROUND = new TexturePaint(image, new Rectangle(0, 0, width, height));
        setBackground(PAINT_BACKGROUND);
        repaint();
        revalidate(false);
        validate();
    }

    public Map<String, AbstractPlugin> getPluginsMap() {
        Collection<PluginNode> nodes = this.getNodes();
        Map<String, AbstractPlugin> plugins = new HashMap<String, AbstractPlugin>();
        AbstractPlugin thisPlugin;
        for (PluginNode node : nodes) {
            thisPlugin = node.getPlugin();
            plugins.put(thisPlugin.getName(), thisPlugin);
        }
        return plugins;
    }

    public void actionRemoveSelectedNodes() {
        ArrayList<AbstractPlugin> pluginsRemoved = new ArrayList<AbstractPlugin>();
        Set<?> objects = getSelectedObjects();
        removeTopComponents(objects);
        for (Object o : new ArrayList<Object>(objects)) {
            if (!isNode(o)) {
                continue;
            }
            removeNodeWithEdges((PluginNode) o);
            pluginsRemoved.add(((PluginNode) o).getPlugin());
        }
        revalidate();
        notifyPluginsRemoved(pluginsRemoved);
    }

    public void loadWorkspace(File file) throws IOException, JDOMException {
        notifyWorkspaceLoaded(file);
    }

    public void loadWorkspace(InputStream inputStream) {
        loadWorkspace(inputStream, true);
    }

    public void loadWorkspace(InputStream inputStream, boolean clearFirst) {
        if (clearFirst) {
            actionClearPalette();
        }
        Element rootElement = null;
        try {
            SAXBuilder builder = new SAXBuilder(false);
            rootElement = builder.build(inputStream).getRootElement();
            inputStream.close();
        } catch (Exception e) {
            Exceptions.printStackTrace(e);
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (Exception e) {
                Exceptions.printStackTrace(e);
            }
        }
        parseWorkspaceXml(rootElement);
    }

    private void parseWorkspaceXml(Element workspaceRoot) {
        PaletteSelectionCookie psc = LatizLookup.getDefault().lookup(PaletteSelectionCookie.class);
        if (psc != null) {
            if (currentFile != null) {
                PaletteEditor ptc = psc.getActivePalette();
                ptc.setName(currentFile.getPath());
                ptc.setDisplayName(currentFile.getName());
            }
        }

        globalsMap.clear();
        Element ge = workspaceRoot.getChild("globalParameters");
        if (ge != null) {
            List globals = ge.getChildren();
            Element thisGlobal;
            String variable, value;
            Boolean isPublic;
            for (Object gv : globals) {
                thisGlobal = (Element) gv;
                variable = thisGlobal.getAttributeValue("var");
                value = thisGlobal.getAttributeValue("value");
                isPublic = Boolean.parseBoolean(thisGlobal.getAttributeValue("public"));
                globalsMap.put(variable, new GlobalVariable(variable, value, isPublic));
            }
        }

        SceneSerializer.deserialize(this, workspaceRoot);

        // Load Recorded outputs
        savedOutputsMap.clear();
        String pluginName, dataName;
        Element thisPluginElement, thisDataElement;
        LinkedHashMap<String, DefaultSavedOutputsImpl> dataMap;
        DefaultSavedOutputsImpl thisSavedOutput;
        Element roe = workspaceRoot.getChild("recordedOutputs");
        setDataSavingRequested(Boolean.parseBoolean(roe.getAttributeValue("use")));
        if (roe != null) {
            for (Object ev : roe.getChildren()) {
                thisPluginElement = (Element) ev;
                pluginName = thisPluginElement.getAttributeValue("name");
                dataMap = new LinkedHashMap<String, DefaultSavedOutputsImpl>();
                for (Object dv : thisPluginElement.getChildren()) {
                    thisDataElement = (Element) dv;
                    thisSavedOutput = new DefaultSavedOutputsImpl();
                    dataName = thisDataElement.getAttributeValue("name");
                    thisSavedOutput.setDatasetName(dataName);
                    thisSavedOutput.setUserDefined(Boolean.parseBoolean(thisDataElement.getAttributeValue("userDefined")));
                    thisSavedOutput.setBeginTime(thisDataElement.getAttributeValue("beginTime"));
                    thisSavedOutput.setEndTime(thisDataElement.getAttributeValue("endTime"));
                    thisSavedOutput.setPeriod(thisDataElement.getAttributeValue("period"));
                    thisSavedOutput.setMaxIterationCount(thisDataElement.getAttributeValue("maxFrames"));
                    dataMap.put(dataName, thisSavedOutput);
                }
                savedOutputsMap.put(pluginName, dataMap);
            }
        }

        // Notify providers
        Lookup.Result<IWorkspaceLoaded> results = LatizLookup.getDefault().lookupResult(IWorkspaceLoaded.class);
        for (IWorkspaceLoaded ips : results.allInstances()) {
            ips.load(PaletteScene.this, workspaceRoot);
        }
        validate();
   }

    void actionClearPalette() {
        for(Widget w : new ArrayList<Widget>(getChildren())) {
            if(w instanceof AnnotationWidget) {
                removeChild(w);
            }
        }
        ArrayList<AbstractPlugin> pluginsRemoved = new ArrayList<AbstractPlugin>();
        Set<?> objects = this.getObjects();
        removeTopComponents(objects);
        for (Object o : new ArrayList<Object>(objects)) {
            if (!isNode(o)) {
                continue;
            }
            removeNodeWithEdges((PluginNode) o);
            pluginsRemoved.add(((PluginNode) o).getPlugin());
        }
        revalidate();
        notifyPluginsRemoved(pluginsRemoved);
        currentFile = null;
        ioConnections.clear();
        savedOutputsMap.clear();
        notifyPaletteCleard(getName());
    }

    private void notifyPaletteCleard(String paletteName) {
        Lookup.Result<IPaletteCleared> result = LatizLookup.getDefault().lookupResult(IPaletteCleared.class);
        for (IPaletteCleared ipc : result.allInstances()) {
            ipc.paletteCleared(paletteName);
        }
    }

    public File getCurrentFile() {
        return currentFile;
    }

    public void setCurrentFile(File currentFile) {
        this.currentFile = currentFile;
    }

    private void notifyPluginsRemoved(ArrayList<AbstractPlugin> pluginsRemoved) {
        Lookup.Result<IPluginsRemoved> pris = LatizLookup.getDefault().lookupResult(IPluginsRemoved.class);
        for (IPluginsRemoved pri : pris.allInstances()) {
            pri.removePlugins(PaletteScene.this, pluginsRemoved);
        }
    }

    private void notifyPluginsAdded(ArrayList<AbstractPlugin> plugins) {
        Lookup.Result<IPluginsAdded> pluginsAdded = LatizLookup.getDefault().lookupResult(IPluginsAdded.class);
        for (IPluginsAdded ipa : pluginsAdded.allInstances()) {
            ipa.pluginsAdded(PaletteScene.this, plugins);
        }
    }

    private void notifyPluginSelected(PluginNode pluginNode) {
        AbstractPlugin plugin = pluginNode.getPlugin();
        pluginSelectionCookie.setSelectedPlugin(plugin);
        LatizLookup.getDefault().removeAllFromLookup(PluginSelectionCookie.class);
        LatizLookup.getDefault().addToLookup(pluginSelectionCookie);
    }

    public boolean isAlreadyLoaded(File file) {
        if (file.equals(currentFile)) {
            NotifyDescriptor nd = new NotifyDescriptor.Confirmation("<HTML>Do you want to reload " + file.getName() + "?</HTML>", "Reload File", NotifyDescriptor.YES_NO_OPTION);
            Object ans = DialogDisplayer.getDefault().notify(nd);
            return ans == NotifyDescriptor.NO_OPTION;
        }
        return PaletteUtilities.isFileAlreadyLoaded(file);
    }

    private void notifyWorkspaceLoaded(File file) throws IOException, JDOMException {
        if (isAlreadyLoaded(file)) {
            return;
        }
        actionClearPalette();
        currentFile = file;
        FileInputStream inputStream = new FileInputStream(file);
        SAXBuilder builder = new SAXBuilder(false);
        Element rootElement = builder.build(inputStream).getRootElement();
        inputStream.close();
        parseWorkspaceXml(rootElement);
    }

    private void removeTopComponents(final Collection<?> objects) {
        ParameterContainer parameterContainer;
        for (Object o : new ArrayList<Object>(objects)) {
            if (!(o instanceof PluginNode)) {
                continue;
            }
            parameterContainer = ((PluginNode) o).getPlugin().getParameterContainer();
            if(parameterContainer==null) continue;
            parameterContainer.forceClose();
            parameterContainer.setName("null");
            parameterContainer = null;
        }
    }

    public boolean isDataSavingRequested() {
        return dataSavingRequested;
    }

    public void setDataSavingRequested(boolean dataSavingRequested) {
        this.dataSavingRequested = dataSavingRequested;
    }

    public void setSelectedPlugin(String pluginName) {
        Collection<PluginNode> nodes = getNodes();
        for (PluginNode pn : nodes) {
            if (pn.getPlugin().getName().equals(pluginName)) {
                HashSet<PluginNode> set = new HashSet<PluginNode>();
                set.add(pn);
                setSelectedObjects(set);
                notifyPluginSelected(pn);
                return;
            }
        }
    }

    /**
     *
     */
    private class ObjectSelectProvider implements SelectProvider {

        public boolean isAimingAllowed(Widget widget, Point localLocation, boolean invertSelection) {
            return false;
        }

        public boolean isSelectionAllowed(Widget widget, Point localLocation, boolean invertSelection) {
            return true;
        }

        public void select(Widget widget, Point localLocation, boolean invertSelection) {
            Object object = findObject(widget);
            if (object != null) {
                return;
            }
            LatizLookup.getDefault().removeAllFromLookup(PluginSelectionCookie.class);
        }
    }

    /**
     *
     */
    private class DropAcceptProvider implements AcceptProvider {

        public ConnectorState isAcceptable(Widget widget, Point point, Transferable transferable) {
            boolean isPluginFlavor = transferable.isDataFlavorSupported(PluginLeaf.DATA_FLAVOR);
            boolean isFileListFlavor = transferable.isDataFlavorSupported(DataFlavor.javaFileListFlavor);
            boolean isStringFlavor = transferable.isDataFlavorSupported(DataFlavor.stringFlavor);
            return isPluginFlavor || isFileListFlavor || isStringFlavor ? ConnectorState.ACCEPT : ConnectorState.REJECT;
        }

        public void accept(Widget widget, Point point, Transferable transferable) {
            if (transferable.isDataFlavorSupported(PluginLeaf.DATA_FLAVOR)) {
                try {
                    PluginLeaf pl = (PluginLeaf) transferable.getTransferData(PluginLeaf.DATA_FLAVOR);
                    FileObject pluginFileObject = pl.getFileObject();
                    String pluginPath = pluginFileObject.getPath();
                    AbstractPlugin p = PluginUtilities.getDefault().instantiate(pluginPath, PaletteScene.this);
                    PluginNode thisPluginNode = new PluginNode(p);
                    Widget w = PaletteScene.this.addNode(thisPluginNode);
                    w.setPreferredLocation(widget.convertLocalToScene(point));
                    HashSet<PluginNode> selectedPlugins = new HashSet<PluginNode>();
                    selectedPlugins.add(thisPluginNode);
                    PaletteScene.this.setSelectedObjects(selectedPlugins);
                    PaletteScene.this.setFocusedObject(thisPluginNode);
                    ArrayList<AbstractPlugin> addedPlugins = new ArrayList<AbstractPlugin>();
                    addedPlugins.add(p);
                    notifyPluginsAdded(addedPlugins);
                    PaletteScene.this.validate();
                    return;

                } catch (Exception ex) {
                    Exceptions.printStackTrace(ex);
                }
                return;
            }
            List<?> fileList = getFilesTransferable(transferable);
            if (fileList != null) {
                if (isLatizFile((File)fileList.get(0))) {
                    try {
                        notifyWorkspaceLoaded((File) fileList.get(0));
                    } catch (Exception ex) {
                        Exceptions.printStackTrace(ex);
                    }
                }
                return;
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

        private boolean isLatizFile(File file) {
            FileInputStream stream;
            if (file.isDirectory()) {
                return false;
            }
            try {
                stream = new FileInputStream(file);
                byte[] fileHead = new byte[100];
                stream.read(fileHead);
                String contents = new String(fileHead);
                if (!contents.contains("<latizWorkspace")) {
                    return false;
                }
                return true;
            } catch (Exception e) {
                return false;
            }
        }
    }

    /**
     *
     */
    private class LabelTextFieldEditor implements TextFieldInplaceEditor {

        public boolean isEnabled(Widget widget) {
            return true;
        }

        public String getText(Widget widget) {
            return ((LabelWidget) widget).getLabel();
        }

        public void setText(Widget widget, String text) {
            String oldText = getText(widget);
            notifyPluginNameWillChange(widget, oldText, text);
            ((LabelWidget) widget).setLabel(text);
            notifyPluginNameChanged(widget, oldText, text);
        }

        private void notifyPluginNameWillChange(Widget w, String oldName, String newName) {
            PluginNode node = (PluginNode) PaletteScene.this.findObject(w);
            AbstractPlugin plugin = node.getPlugin();
            Lookup.Result<IPluginNameChange> pcis = LatizLookup.getDefault().lookupResult(IPluginNameChange.class);
            for (IPluginNameChange pci : pcis.allInstances()) {
                pci.nameWillChange(PaletteScene.this, plugin, oldName, newName);
            }
        }

        private void notifyPluginNameChanged(Widget w, String oldName, String newName) {
            PluginNode node = (PluginNode) PaletteScene.this.findObject(w);
            node.setName(newName);

            AbstractPlugin plugin = node.getPlugin();
            plugin.setName(newName);

            String name;
            Collection<ConnectorEdge> edges = PaletteScene.this.findNodeEdges(node, true, true);
            for (ConnectorEdge edge : edges) {
                name = edge.getName();
                if (name.startsWith(oldName + ">")) {
                    edge.setName(name.replace(oldName + ">", newName + ">"));
                } else if (name.endsWith(">" + oldName)) {
                    edge.setName(name.replace(">" + oldName, ">" + newName));
                }
            }
            Lookup.Result<IPluginNameChange> pcis = LatizLookup.getDefault().lookupResult(IPluginNameChange.class);
            for (IPluginNameChange pci : pcis.allInstances()) {
                pci.nameChanged(PaletteScene.this, plugin, oldName, newName);
            }
        }
    }

    /**
     *
     */
    private class MultiMoveProvider implements MoveProvider {

        private HashMap<Widget, Point> originals = new HashMap<Widget, Point>();
        private Point original;

        public void movementStarted(Widget widget) {
            Object object = findObject(widget);
            if (isNode(object)) {
                for (Object o : getSelectedObjects()) {
                    if (isNode(o)) {
                        Widget w = findWidget(o);
                        if (w != null) {
                            originals.put(w, w.getPreferredLocation());
                        }
                    }
                }
            } else {
                originals.put(widget, widget.getPreferredLocation());
            }
        }

        public void movementFinished(Widget widget) {
            originals.clear();
            original = null;
        }

        public Point getOriginalLocation(Widget widget) {
            original = widget.getPreferredLocation();
            return original;
        }

        public void setNewLocation(Widget widget, Point location) {
            int dx = location.x - original.x;
            int dy = location.y - original.y;
            for (Map.Entry<Widget, Point> entry : originals.entrySet()) {
                Point point = entry.getValue();
                entry.getKey().setPreferredLocation(new Point(point.x + dx, point.y + dy));
            }
        }
    }

    class PluginSelectionCookieImpl implements PluginSelectionCookie {

        private AbstractPlugin selectedPlugin;

        public void setSelectedPlugin(AbstractPlugin selectedPlugin) {
            this.selectedPlugin = selectedPlugin;
        }

        public AbstractPlugin getSelectedPlugin() {
            return selectedPlugin;
        }
    }
}