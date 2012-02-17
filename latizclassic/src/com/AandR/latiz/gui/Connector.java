package com.AandR.latiz.gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;


import com.AandR.latiz.core.Connection;
import com.AandR.latiz.core.PropertiesManager;
import com.AandR.latiz.dev.AbstractPlugin;
import com.AandR.latiz.interfaces.ParentPluginInterface;

/**
 * @author Dr. Richard St. John
 * @version $Revision$, $Date$
 */
public final class Connector extends JPanel {

    public static final int TOP = 10;
    public static final int LEFT = 20;
    public static final int BOTTOM = 30;
    public static final int RIGHT = 40;
    public static final Color COLOR = Color.BLUE;
    public static final float LINE_WIDTH = 2f;
    private boolean showOrdering = false;
    private int arrowSize = 10, arrowPosition = LEFT;
    private float lineWidth = LINE_WIDTH;
    private AbstractPlugin outputSendingPlugin, inputReceivingPlugin;
    private ConnectionPanel connectionPanel;
    private Color connectorColor = COLOR;
    private String name, index;

    public Connector() {
        PropertiesManager props = PropertiesManager.getInstanceOf();
        index = "1";
        connectorColor = Color.decode(props.getProperty(PropertiesManager.CONNECTOR_LINE_COLOR));
        lineWidth = Float.parseFloat(props.getProperty(PropertiesManager.CONNECTOR_LINE_WEIGHT));
        setOpaque(false);
        setBounds(0, 0, 50, 50);
        setPreferredSize(new Dimension(50, 50));
    }

    public Connector(AbstractPlugin p1, AbstractPlugin p2) {
        this();
        setConnection(p1, p2);
    }

    public void setConnection(AbstractPlugin p1, AbstractPlugin p2) {
        ConnectorListener connectorListener = new ConnectorListener();

        outputSendingPlugin = p1;
        outputSendingPlugin.initializeInputs();
        outputSendingPlugin.initializeOutputs();

        inputReceivingPlugin = p2;
        inputReceivingPlugin.addParentPluginInterface(connectorListener);
        inputReceivingPlugin.initializeInputs();
        inputReceivingPlugin.initializeOutputs();

        connectionPanel = new ConnectionPanel(outputSendingPlugin, inputReceivingPlugin);
        name = this.outputSendingPlugin.getName() + ">" + this.inputReceivingPlugin.getName();
        Dimension p1Dim = p1.getPreferredSize();
        Dimension p2Dim = p2.getPreferredSize();
        Point p1Loc = p1.getLocation();
        Point p2Loc = p2.getLocation();
        int h = Math.max(p1Loc.y + p1Dim.height, p2Loc.y + p2Dim.height);
        setPreferredSize(new Dimension(Math.abs(p2Loc.x - (p1Loc.x + p1Dim.width)), h));
    }

    public void removeConnection() {
        outputSendingPlugin = null;
        inputReceivingPlugin = null;
        this.removeAll();
    }

    /**
     * Use this method to set the bounds for this line.  A call to this method will set the bounds and thus a repaint.
     */
    public void updateBounds() {

//  Set Bounds    
        Point locP1 = outputSendingPlugin.getLocation();
        Point locP2 = inputReceivingPlugin.getLocation();

        Dimension sizeP1 = outputSendingPlugin.getSize();
        Dimension sizeP2 = inputReceivingPlugin.getSize();
        int xMin = locP1.x < locP2.x ? locP1.x : locP2.x;
        int yMin = locP1.y < locP2.y ? locP1.y : locP2.y;

        int xMax = (locP1.x + sizeP1.width) < (locP2.x + sizeP2.width) ? (locP2.x + sizeP2.width) : (locP1.x + sizeP1.width);
        int yMax = (locP1.y + sizeP1.height) < (locP2.y + sizeP2.height) ? (locP2.y + sizeP2.height) : (locP1.y + sizeP1.height);
        setBounds(xMin - arrowSize, yMin - arrowSize, xMax - xMin + 2 * arrowSize, yMax - yMin + 2 * arrowSize);
    }

    /**
     *
     */
    @Override
    public void paintComponent(Graphics g) {
        if (outputSendingPlugin == null || inputReceivingPlugin == null) {
            return;
        }

        Polygon arrowHeadPolygon = arrowHead(arrowPosition);
        Point lineEnd = new Point(arrowHeadPolygon.xpoints[0], arrowHeadPolygon.ypoints[2]);

        Point locP1 = outputSendingPlugin.getLocation();
        Point locP2 = inputReceivingPlugin.getLocation();

        Dimension sizeP1 = outputSendingPlugin.getSize();
        Dimension sizeP2 = inputReceivingPlugin.getSize();

        float xDiff = (float) ((locP2.x + sizeP2.width / 2) - (locP1.x + sizeP1.width / 2));
        float yDiff = (float) ((locP2.y + sizeP2.height / 2) - (locP1.y + sizeP1.height / 2));
        float slope = yDiff / xDiff;
        if (slope > -1f && slope < 1f) {
            arrowPosition = xDiff > 0 ? LEFT : RIGHT;
        } else {
            arrowPosition = yDiff > 0 ? TOP : BOTTOM;
        }

//  Convert to local coordinates    
        int xp1 = sizeP1.width / 2;
        int yp1 = sizeP1.height / 2;
        Point c1 = SwingUtilities.convertPoint(outputSendingPlugin, new Point(xp1, yp1), this);

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_VRGB);
        g2.setStroke(new BasicStroke(lineWidth));
        g2.setColor(connectorColor);
        g2.drawLine(c1.x, c1.y, lineEnd.x, lineEnd.y);
        g2.fillPolygon(arrowHeadPolygon);
        if (showOrdering) {
            paintNumber(g2, c1, lineEnd);
        }
    }

    private void paintNumber(Graphics2D g2, Point lineStart, Point lineEnd) {
        float xPad = 4f;
        float yPad = 2f;
        FontMetrics fm = g2.getFontMetrics();
        float width = fm.stringWidth(index) + 2f * xPad;
        float height = fm.getHeight() + 2f * yPad;

        width = Math.max(width, height);
        float xDiff = (float) (lineEnd.x - lineStart.x);
        float yDiff = (float) (lineEnd.y - lineStart.y);
        float m = yDiff / xDiff;

        Dimension sizeP1 = outputSendingPlugin.getPreferredSize();
        float xMid = 0;
        float yMid = 0;
        switch (arrowPosition) {
            case LEFT:
                xMid = (0.5f * (lineStart.x + sizeP1.width / 2f + lineEnd.x));
                yMid = (m * (xMid - lineStart.x) + lineStart.y);
                break;
            case RIGHT:
                xMid = (0.5f * (lineStart.x - sizeP1.width / 2f + lineEnd.x));
                yMid = (m * (xMid - lineStart.x) + lineStart.y);
                break;
            case TOP:
                yMid = (0.5f * (lineStart.y + sizeP1.height / 2f + lineEnd.y));
                xMid = (1f / m * (yMid - lineStart.y) + lineStart.x);
                break;
            case BOTTOM:
                yMid = (0.5f * (lineStart.y - sizeP1.height / 2f + lineEnd.y));
                xMid = (1f / m * (yMid - lineStart.y) + lineStart.x);
                break;
        }

        float xLoc = xMid - width / 2f;
        float yLoc = yMid - height / 2f;

        g2.setStroke(new BasicStroke(1f));
        g2.setColor(Color.WHITE);
        g2.fillOval((int) xLoc, (int) yLoc, (int) width, (int) height);
        g2.setColor(Color.BLACK);
        g2.drawOval((int) xLoc, (int) yLoc, (int) width, (int) height);
        g2.drawString(index, xMid - fm.stringWidth(index) / 2f, yMid + yPad / 2f + 2f);
    }

    /**
     *
     * @param position
     * @return
     */
    private Polygon arrowHead(int position) {
        Dimension sizeP2 = inputReceivingPlugin.getPreferredSize();
        int xMid = sizeP2.width / 2;
        int yMid = sizeP2.height / 2;
        int back = arrowSize / 2;
        Point p1 = null, p2 = null, p3 = null, p4 = null;
        switch (position) {
            case LEFT:
                p1 = new Point(-arrowSize, yMid);
                p2 = new Point(-arrowSize - back, yMid - arrowSize);
                p3 = new Point(0, yMid);
                p4 = new Point(-arrowSize - back, yMid + arrowSize);
                break;
            case RIGHT:
                p1 = new Point(2 * xMid + arrowSize, yMid);
                p2 = new Point(2 * xMid + arrowSize + back, yMid - arrowSize);
                p3 = new Point(2 * xMid, yMid);
                p4 = new Point(2 * xMid + arrowSize + back, yMid + arrowSize);
                break;
            case TOP:
                p1 = new Point(xMid, 0);
                p2 = new Point(xMid + arrowSize, -arrowSize - back);
                p3 = new Point(xMid, -arrowSize);
                p4 = new Point(xMid - arrowSize, -arrowSize - back);
                break;
            case BOTTOM:
                p1 = new Point(xMid, 2 * yMid);
                p2 = new Point(xMid + arrowSize, 2 * yMid + arrowSize + back);
                p3 = new Point(xMid, 2 * yMid + arrowSize);
                p4 = new Point(xMid - arrowSize, 2 * yMid + arrowSize + back);
                break;
        }
        Point arrow1 = SwingUtilities.convertPoint(inputReceivingPlugin, p1, this);
        Point arrow2 = SwingUtilities.convertPoint(inputReceivingPlugin, p2, this);
        Point arrow3 = SwingUtilities.convertPoint(inputReceivingPlugin, p3, this);
        Point arrow4 = SwingUtilities.convertPoint(inputReceivingPlugin, p4, this);

        Polygon p = new Polygon();
        p.addPoint(arrow1.x, arrow1.y);
        p.addPoint(arrow2.x, arrow2.y);
        p.addPoint(arrow3.x, arrow3.y);
        p.addPoint(arrow4.x, arrow4.y);
        return p;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
        String[] keySplit = name.split(">");
        if (keySplit.length == 0) {
            connectionPanel.setOutgoingPluginLabel(name);
            return;
        }
        connectionPanel.setOutgoingPluginLabel(keySplit[0]);
        connectionPanel.setIncomingPluginLabel(keySplit[1]);
        connectionPanel.revalidate();
        connectionPanel.repaint();
    }

    public void setIOconnections(ArrayList<String> ioConnections) {
        connectionPanel.setConnections(ioConnections);
        connectionPanel.refreshConnections();
    }

    public Connection getIOconnection() {
        Connection connection = new Connection();
        try {
            connection.setInputReceivingProcessor(inputReceivingPlugin);
        } catch (NullPointerException ne) {
        }
        try {
            connection.setOutputSendingProcessor(outputSendingPlugin);
        } catch (NullPointerException ne) {
        }

        String[] nameSplit;
        if (connectionPanel == null) {
            return connection;
        }
        for (String ioKey : connectionPanel.getConnections()) {
            nameSplit = ioKey.split(">");
            connection.put(nameSplit[1], nameSplit[0]); //Engine needs inputs->output. Connector class uses output->input.
        }
        return connection;
    }

    public ConnectionPanel getConnectionPanel() {
        return connectionPanel;
    }

    public AbstractPlugin getOutputSendingPlugin() {
        return outputSendingPlugin;
    }

    public AbstractPlugin getInputReceivingPlugin() {
        return inputReceivingPlugin;
    }

    public void setOutputSendingPlugin(AbstractPlugin outputSendingPlugin) {
        this.outputSendingPlugin = outputSendingPlugin;
    }

    public void setInputReceivingPlugin(AbstractPlugin inputReceivingPlugin) {
        this.inputReceivingPlugin = inputReceivingPlugin;
    }

    public Color getConnectorColor() {
        return connectorColor;
    }

    public void setConnectorColor(Color connectorColor) {
        this.connectorColor = connectorColor;
    }

    public int getArrowPosition() {
        return arrowPosition;
    }

    public void setArrowPosition(int arrowPosition) {
        this.arrowPosition = arrowPosition;
    }

    public void setLineWidth(float lineWidth) {
        this.lineWidth = lineWidth;
    }

    /**
     *
     * @author Dr. Richard St. John
     * @version $Revision$, $Date$
     */
    private class ConnectorListener implements ParentPluginInterface {

        public AbstractPlugin getParentPlugin() {
            return outputSendingPlugin;
        }

        public Connection getInputOutputConnection() {
            return getIOconnection();
        }

        public HashMap<String, String> getInputOutputMap() {
            Connection c = getIOconnection();
            HashMap<String, String> map = new HashMap<String, String>();
            for (String key : c.keySet()) {
                map.put(key, c.get(key));
            }
            return map;
        }

        public Connector getIncomingConnector() {
            return Connector.this;
        }
    }
}
