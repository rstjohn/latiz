package com.AandR.latiz.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.HierarchyBoundsListener;
import java.awt.event.HierarchyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import com.AandR.gui.ui.LineBorderX;
import com.AandR.latiz.core.Input;
import com.AandR.latiz.core.Output;
import com.AandR.latiz.dev.AbstractPlugin;
import com.AandR.latiz.listeners.ConnectionPanelListener;
import com.AandR.latiz.resources.Resources;

/**
 * @author Dr. Richard St. John
 * @version $Revision$, $Date$
 */
public class ConnectionPanel extends JPanel {

    private boolean isConnected = false;
    private ArrayList<InputConnector> inputConnectionMap;
    private ArrayList<OutputConnector> outputConnectionMap;
    private ArrayList<String> connections;
    private ArrayList<ConnectionPanelListener> connectionPanelListeners;
    private InputConnector selectedIncomingConnector;
    private JLabel labelP1, labelP2;
    private JScrollPane scroll;
    private JPanel connectionPanel, connectionGridPanel;
    private OutputConnector selectedOutgoingConnector;
    private String parentName, nameP2, nameP1;

    public ConnectionPanel() {
        super();
        addComponentListener(new ComponentListener() {

            public void componentShown(ComponentEvent e) {
            }

            public void componentHidden(ComponentEvent e) {
            }

            public void componentMoved(ComponentEvent e) {
            }

            public void componentResized(ComponentEvent e) {
                if (e.getComponent() == null || scroll == null) {
                    return;
                }
                revalidate();
                repaint();
                if (getParent() != null) {
                    getParent().validate();
                    getParent().repaint();
                }
            }
        });
    }

    public ConnectionPanel(AbstractPlugin p1, AbstractPlugin p2) {
        this();
        inputConnectionMap = new ArrayList<InputConnector>();
        outputConnectionMap = new ArrayList<OutputConnector>();
        connectionPanelListeners = new ArrayList<ConnectionPanelListener>();
        connections = new ArrayList<String>();
        createContent(p1, p2);
    }

    private void createContent(AbstractPlugin p1, AbstractPlugin p2) {
        nameP1 = p1.getName();
        nameP2 = p2.getName();
        parentName = p1.getName();

        labelP1 = new JLabel(p1.getName(), Resources.createIcon("connect_no.png"), JLabel.LEFT);
        labelP1.setToolTipText(p1.getName());
        labelP1.setOpaque(true);
        labelP1.setBorder(new LineBorderX());

        labelP2 = new JLabel(p2.getName(), Resources.createIcon("connect_no.png"), JLabel.LEFT);
        labelP2.setToolTipText(p2.getName());
        labelP2.setOpaque(true);
        labelP2.setBorder(new LineBorderX());

        setLayout(new BorderLayout());
        add(createConnectionPanel(p1, p2), BorderLayout.CENTER);

    }

    private JPanel createConnectionPanel(AbstractPlugin p1, AbstractPlugin p2) {
        IoConnectionPanelListener listener = new IoConnectionPanelListener();

        ScrollListener scrollListener = new ScrollListener();
        scroll = new JScrollPane(createInputConnections(p1, p2));
        scroll.getVerticalScrollBar().addAdjustmentListener(scrollListener);
        scroll.getHorizontalScrollBar().addAdjustmentListener(scrollListener);
        scroll.addHierarchyBoundsListener(scrollListener);
        scroll.setBorder(null);
        scroll.addMouseListener(listener);
        scroll.addMouseMotionListener(listener);

        connectionPanel = new JPanel(new BorderLayout());
        connectionPanel.add(scroll);

        return connectionPanel;
    }

    private Component createInputConnections(AbstractPlugin p1, AbstractPlugin p2) {
        int labelWidth = 165;
        int width = 200;
        int inset = 2;
        int n_out = p1.getOutputsDataMap().size();
        int n_in = p2.getInputsDataMap().size();

        OutputConnector outputConnector;
        InputConnector inputConnector;
        connectionGridPanel = new JPanel(null);

        Iterator<String> outputKeys = p1.getOutputsDataMap().keySet().iterator();
        Iterator<String> inputKeys = p2.getInputsDataMap().keySet().iterator();
        String outKey, inKey;
        Dimension d = labelP1.getPreferredSize();
        labelP1.setBounds(0, 0, labelWidth, d.height);
        connectionGridPanel.add(labelP1);
        HashMap<String, Output> thisOutputsDataMap = p1.getOutputsDataMap();

        int ho = 28;
        for (int i = 0; i < n_out; i++) {
            outKey = outputKeys.next();
            outputConnector = new OutputConnector(thisOutputsDataMap.get(outKey));
            outputConnector.setToolTipText(thisOutputsDataMap.get(outKey).getToolTipText());
            outputConnector.setLocation(0, ho);
            outputConnector.setSize(outputConnector.getPreferredSize());
            outputConnectionMap.add(outputConnector);
            connectionGridPanel.add(outputConnector);
            ho += outputConnector.getPreferredSize().height + 3;
        }

        d = labelP2.getPreferredSize();
        labelP2.setBounds(width, 0, labelWidth, d.height);
        connectionGridPanel.add(labelP2);
        HashMap<String, Input> thisInputsDataMap = p2.getInputsDataMap();

        int hi = 28;
        for (int i = 0; i < n_in; i++) {
            inKey = inputKeys.next();
            inputConnector = new InputConnector(thisInputsDataMap.get(inKey));
            inputConnector.setToolTipText(thisInputsDataMap.get(inKey).getToolTipText());
            inputConnector.setLocation(width, hi);
            inputConnector.setSize(inputConnector.getPreferredSize());
            inputConnectionMap.add(inputConnector);
            connectionGridPanel.add(inputConnector);
            hi += inputConnector.getPreferredSize().height + 4;
        }

        Point loc = connectionGridPanel.getLocation();
        connectionGridPanel.setBounds(loc.x, loc.y, width + labelWidth + inset, Math.max(hi, ho));
        connectionGridPanel.setPreferredSize(connectionGridPanel.getBounds().getSize());
        return connectionGridPanel;
    }

    public void notifyPluginInputsChanged(AbstractPlugin inputReceiver, AbstractPlugin outputSender) {
        inputConnectionMap.clear();
        outputConnectionMap.clear();
        removeAll();
        createContent(outputSender, inputReceiver);
        removeLostInputConnections();
        refreshConnections();
    }

    private void removeLostInputConnections() {
        ArrayList<String> lostConnections = new ArrayList<String>();
        for (String s : connections) {
            if (findInputConnector(s.split(">")[1]) == null) {
                lostConnections.add(s);
            }
        }
        for (String s : lostConnections) {
            connections.remove(s);
        }
    }

    public void notifyPluginOutputsChanged(AbstractPlugin inputReceiver, AbstractPlugin outputSender) {
        inputConnectionMap.clear();
        outputConnectionMap.clear();
        removeAll();
        createContent(outputSender, inputReceiver);
        removeLostOutputConnections();
        refreshConnections();
    }

    private void removeLostOutputConnections() {
        ArrayList<String> lostConnections = new ArrayList<String>();
        for (String s : connections) {
            if (findOutputConnector(s.split(">")[0]) == null) {
                lostConnections.add(s);
            }
        }
        for (String s : lostConnections) {
            connections.remove(s);
        }
    }

    public void addConnectionPanelListener(ConnectionPanelListener listener) {
        connectionPanelListeners.add(listener);
    }

    private void notifyConnectionMade(String ioConnectionName) {
        for (ConnectionPanelListener l : connectionPanelListeners) {
            l.ioConnectionMade(ioConnectionName);
        }
    }

    private void notifyConnectionRemoved(String ioConnectionName) {
        for (ConnectionPanelListener l : connectionPanelListeners) {
            l.ioConnectionRemoved(ioConnectionName);
        }
    }

    public void setOutgoingConnectorsColors(Color foreground, Color background) {
        for (OutputConnector o : outputConnectionMap) {
            o.setConnectorColor(foreground, background);
        }
    }

    public void setIncomingConnectorsColors(Color foreground, Color background) {
        for (InputConnector i : inputConnectionMap) {
            i.setConnectorColor(foreground, background);
        }
    }

    public void setOutgoingPluginLabel(String text) {
        labelP1.setText(text);
        labelP1.repaint();
    }

    public void setIncomingPluginLabel(String text) {
        labelP2.setText(text);
        labelP2.repaint();
    }

    public void refreshConnections() {
        String[] key;
        OutputConnector out;
        InputConnector in;
        int i = 0;
        for (String s : this.connections) {
            key = s.split(">");
            out = findOutputConnector(key[0]);
            connectionGridPanel.setComponentZOrder(out.getArrowHeadPanel(), i++);
            connectionGridPanel.setComponentZOrder(out.getLinePanel(), i++);

            Point arrowOut = out.getArrowHeadPanel().getLocation();
            SwingUtilities.convertPointToScreen(arrowOut, this);

            in = findInputConnector(key[1]);
            in.setHasConnection(true);
            Point arrowIn = in.getDockLocation();
            out.getArrowHeadPanel().setLocation(arrowIn);
        }
        revalidate();
        repaint();
    }

    private void refreshZOrder() {
        OutputConnector out;
        String[] key;
        int i = 0;
        for (String s : this.connections) {
            key = s.split(">");
            out = findOutputConnector(key[0]);
            connectionGridPanel.setComponentZOrder(out.getArrowHeadPanel(), i++);
            connectionGridPanel.setComponentZOrder(out.getLinePanel(), i++);
        }
    }

    protected void setConnections(ArrayList<String> connections) {
        this.connections = connections;
    }

    private OutputConnector findOutputConnector(String key) {
        for (OutputConnector c : outputConnectionMap) {
            if (c.getOutputKey().equalsIgnoreCase(key)) {
                return c;
            }
        }
        return null;
    }

    public InputConnector findInputConnector(String key) {
        for (InputConnector c : inputConnectionMap) {
            if (c.getInputKey().equalsIgnoreCase(key)) {
                return c;
            }
        }
        return null;
    }

    public ArrayList<InputConnector> getInputConnectionMap() {
        return inputConnectionMap;
    }

    protected ArrayList<String> getConnections() {
        return connections;
    }

    /**
     *
     * @author Dr. Richard St. John
     * @version $Revision$, $Date$
     */
    private class IoConnectionPanelListener implements MouseListener, MouseMotionListener {

        private boolean isDraggedOverInputConnector(MouseEvent e) {
            Point p = e.getLocationOnScreen();
            Point loc;
            Rectangle r;
            JPanel arrowPanel;
            for (InputConnector o : inputConnectionMap) {
                arrowPanel = o.getArrowHeadPanel();
                loc = arrowPanel.getLocationOnScreen();
                r = new Rectangle(loc, new Dimension(arrowPanel.getWidth(), arrowPanel.getHeight()));
                if (r.contains(p)) {
                    setCursor(new Cursor(Cursor.MOVE_CURSOR));
                    return (isConnected = isValidConnection(o));
                } else {
                    o.setBackgroundColor(InputConnector.HAS_CONNECTION_COLOR);
                    setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                }
            }
            return false;
        }

        private boolean isValidConnection(InputConnector o) {
            if (o.hasConnection() && !o.equals(selectedIncomingConnector)) {
                o.setBackgroundColor(InputConnector.INVALID_CONNECTION_COLOR);
                return false;
            }

            boolean isValid = o.getInput().acceptConnectionToOutput(selectedOutgoingConnector.getOutput());
            isValid = isValid && (!o.hasConnection() || o.equals(selectedIncomingConnector));
            if (isValid) {
                o.setBackgroundColor(InputConnector.VALID_CONNECTION_COLOR);
            } else {
                o.setBackgroundColor(InputConnector.INVALID_CONNECTION_COLOR);
            }
            return isValid;
        }

        public void mouseClicked(MouseEvent e) {
        }

        public void mouseEntered(MouseEvent e) {
        }

        public void mouseExited(MouseEvent e) {
        }

        public void mouseReleased(MouseEvent e) {
            if (selectedOutgoingConnector == null) {
                return;
            }
            InputConnector inputConnector = findInputConnector(e.getLocationOnScreen());
            if (inputConnector != null) {
                inputConnector.setBackgroundColor(InputConnector.DEFAULT_CONNECTION_COLOR);
            } else {
                inputConnector = selectedIncomingConnector;
            }
            String ioConnection;
            if (isConnected) {
                if (selectedIncomingConnector != null) {
                    fireInputConnectorRemoved();
                }

                removePreviousConnection(selectedOutgoingConnector.getOutputKey());
                selectedOutgoingConnector.getArrowHeadPanel().setLocation(inputConnector.getDockLocation());

                connections.add(selectedOutgoingConnector.getOutputKey() + ">" + inputConnector.getInputKey());
                if (inputConnector != null && !inputConnector.hasConnection()) {
                    inputConnector.setHasConnection(true);
                    inputConnector.setParentName(parentName);
                    ioConnection = nameP1 + "::" + selectedOutgoingConnector.getOutputKey() + ">" + nameP2 + "::" + inputConnector.getInputKey();
                    notifyConnectionMade(ioConnection);
                }
            } else {
                selectedOutgoingConnector.resetConnector();
                fireInputConnectorRemoved();
            }
            isConnected = false;
            selectedOutgoingConnector = null;
            refreshZOrder();
            connectionGridPanel.validate();
            connectionGridPanel.repaint();
        }

        private void fireInputConnectorRemoved() {
            try {
                selectedIncomingConnector.setHasConnection(false);
                selectedIncomingConnector.setParentName(null);
                connections.remove(selectedOutgoingConnector.getOutputKey() + ">" + selectedIncomingConnector.getInputKey());
                String ioConnection = nameP1 + "::" + selectedOutgoingConnector.getOutputKey() + ">" + nameP2 + "::" + selectedIncomingConnector.getInputKey();
                notifyConnectionRemoved(ioConnection);
            } catch (NullPointerException npe) {
            }
        }

        private void removePreviousConnection(String outputKey) {
            if (connections.isEmpty()) {
                return;
            }
            String[] splitKey;
            for (String c : connections) {
                splitKey = c.split(">");
                if (splitKey[0].equalsIgnoreCase(outputKey)) {
                    connections.remove(c);
                    return;
                }
            }
        }

        private InputConnector findInputConnector(Point p) {
            Point loc;
            Rectangle r;
            JPanel arrowPanel;
            for (InputConnector o : inputConnectionMap) {
                arrowPanel = o.getArrowHeadPanel();
                loc = arrowPanel.getLocationOnScreen();
                r = new Rectangle(loc, new Dimension(arrowPanel.getWidth(), arrowPanel.getHeight()));
                if (r.contains(p)) {
                    return o;
                }
            }
            return null;
        }

        public void mousePressed(MouseEvent e) {
            if (selectedOutgoingConnector == null) {
                return;
            }
            selectedIncomingConnector = findInputConnector(e.getLocationOnScreen());
            connectionGridPanel.setComponentZOrder(selectedOutgoingConnector.getArrowHeadPanel(), 0);
            connectionGridPanel.setComponentZOrder(selectedOutgoingConnector.getLinePanel(), 1);
        }

        public void mouseDragged(MouseEvent e) {
            if (selectedOutgoingConnector == null) {
                return;
            }
            isConnected = false;
            isDraggedOverInputConnector(e);
            Point p = SwingUtilities.convertPoint(ConnectionPanel.this, e.getPoint(), connectionGridPanel);
            selectedOutgoingConnector.getArrowHeadPanel().setLocation(p);
        }

        public void mouseMoved(MouseEvent e) {
            Point p = e.getLocationOnScreen();
            Point loc;
            Rectangle r;
            JPanel arrowPanel;
            for (OutputConnector o : outputConnectionMap) {
                arrowPanel = o.getArrowHeadPanel();
                loc = arrowPanel.getLocationOnScreen();
                r = new Rectangle(loc, arrowPanel.getSize());
                if (r.contains(p)) {
                    setCursor(new Cursor(Cursor.HAND_CURSOR));
                    selectedOutgoingConnector = o;
                    return;
                } else {
                    setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                    selectedOutgoingConnector = null;
                }
            }
        }
    }

    /**
     *
     * @author Dr. Richard St. John
     * @version $Revision$, $Date$
     */
    private class ScrollListener implements AdjustmentListener, HierarchyBoundsListener {

        public void adjustmentValueChanged(AdjustmentEvent e) {
            scroll.repaint();
            if (getParent() != null) {
                getParent().validate();
                getParent().repaint();
            }
        }

        public void ancestorMoved(HierarchyEvent e) {
        }

        public void ancestorResized(HierarchyEvent e) {
            if (e.getComponent() == null || scroll == null) {
                return;
            }
            Dimension dim = e.getChanged().getSize();
            scroll.setSize(dim);
            scroll.setBounds(new Rectangle(dim));
            scroll.setPreferredSize(dim);
            scroll.revalidate();
            scroll.repaint();
            if (getParent() != null) {
                getParent().validate();
                getParent().repaint();
            }
        }
    }
}
