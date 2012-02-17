package com.AandR.latiz.gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.RenderingHints;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import com.AandR.latiz.core.Input;
import com.AandR.latiz.core.PropertiesManager;

/**
 * @author Dr. Richard St. John
 * @version $Revision$, $Date$
 */
public class InputConnector extends JPanel {

    public static int VALID_CONNECTION_COLOR = 0;
    public static int INVALID_CONNECTION_COLOR = 1;
    public static int HAS_CONNECTION_COLOR = 2;
    public static int DEFAULT_CONNECTION_COLOR = 3;
    public static Color COLOR = new Color(0, 154, 205);
    private boolean hasConnection = false;
    private ArrowHeadPanel arrowHeadPanel;
    private Color defaultBackgroundColor = COLOR, foregroundColor = Color.WHITE, connectedColor, backgroundColor;
    private Input input;
    private JLabel label;
    private String inputKey, tooltipString;

    public InputConnector(Input input) {
        this.input = input;
        setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
        setOpaque(false);

        PropertiesManager props = PropertiesManager.getInstanceOf();
        foregroundColor = Color.decode(props.getProperty(PropertiesManager.COLOR_CONNECTION_INCOMING_FOREGROUND));
        defaultBackgroundColor = Color.decode(props.getProperty(PropertiesManager.COLOR_CONNECTION_INCOMING_BACKGROUND));
        backgroundColor = Color.decode(props.getProperty(PropertiesManager.COLOR_CONNECTION_INCOMING_BACKGROUND));
        int r = defaultBackgroundColor.getRed();
        int g = defaultBackgroundColor.getGreen();
        int b = defaultBackgroundColor.getBlue();
        connectedColor = new Color(r, g, b, 128);

        inputKey = input.getKey();
        label = new JLabel("<html>" + inputKey + " <i>" + input.getValueTypeSimpleName() + "</i></html>");
        label.setOpaque(true);
        label.setBorder(new EmptyBorder(0, 5, 0, 0));
        label.setForeground(foregroundColor);
        label.setBackground(defaultBackgroundColor);

        FontMetrics fontMetrics = label.getFontMetrics(label.getFont());
        int height = 22;
        if (fontMetrics.stringWidth(inputKey + input.getValueTypeSimpleName()) > 130) {
            height = 35;
        }
        label.setPreferredSize(new Dimension(150, height));
        label.setMinimumSize(new Dimension(150, height));
        setMinimumSize(new Dimension(164, height));

        add(arrowHeadPanel = new ArrowHeadPanel(height));
        add(label);
    }

    @Override
    public void setToolTipText(String tooltip) {
        label.setToolTipText(tooltip);
        tooltipString = tooltip.replace("<HTML>", "").replace("</HTML>", "");
    }

    public void setConnectorColor(Color foreground, Color background) {
        defaultBackgroundColor = background;
        int r = defaultBackgroundColor.getRed();
        int g = defaultBackgroundColor.getGreen();
        int b = defaultBackgroundColor.getBlue();
        connectedColor = new Color(r, g, b, 128);

        label.setForeground(foregroundColor = foreground);
        label.setBackground(background);
        arrowHeadPanel.repaint();
    }

    public void setBackgroundColor(int colorFlag) {
        if (colorFlag == VALID_CONNECTION_COLOR) {
            backgroundColor = new Color(0, 180, 0);
        } else if (colorFlag == INVALID_CONNECTION_COLOR) {
            backgroundColor = Color.RED;
        } else if (hasConnection) {
            backgroundColor = connectedColor;
        } else {
            backgroundColor = defaultBackgroundColor;
        }
        label.setBackground(backgroundColor);
    }

    public JPanel getArrowHeadPanel() {
        return arrowHeadPanel;
    }

    public Point getDockLocation() {
        Point loc = getLocation();
        return new Point(loc.x, loc.y + 1);
    }

    public Input getInput() {
        return input;
    }

    public String getInputKey() {
        return inputKey;
    }

    public JLabel getLabel() {
        return label;
    }

    public void setParentName(String parentName) {
        if (parentName == null) {
            label.setToolTipText("<HTML>" + tooltipString + "</HTML>");
        } else {
            label.setToolTipText("<HTML>" + "Parent : " + parentName + "<HR>" + tooltipString + "</HTML>");
        }
    }

    public boolean hasConnection() {
        return hasConnection;
    }

    public void setHasConnection(boolean hasConnection) {
        this.hasConnection = hasConnection;
        setBackgroundColor(hasConnection ? HAS_CONNECTION_COLOR : DEFAULT_CONNECTION_COLOR);
        label.repaint();
    }

    /**
     *
     * @author Dr. Richard St. John
     * @version $Revision$, $Date$
     */
    private class ArrowHeadPanel extends JPanel {

        public ArrowHeadPanel(int arrowSize) {
            setOpaque(false);
            setPreferredSize(new Dimension(12, arrowSize));
        }

        @Override
        public void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            Dimension d = getPreferredSize();
            setOpaque(false);
            g2.setColor(backgroundColor);
            g2.fillRect(0, 0, d.width, d.height);
            Polygon arrowHead = new Polygon();
            arrowHead.addPoint(0, 0);
            arrowHead.addPoint(d.width - 1, d.height / 2 - 1);
            arrowHead.addPoint(0, d.height - 1);

            g2.setColor(hasConnection ? Color.RED : backgroundColor);
            g2.fillPolygon(arrowHead);

            g2.setColor(foregroundColor);
            g2.drawPolygon(arrowHead);

            g2.setStroke(new BasicStroke(2));
            g2.setColor(defaultBackgroundColor);
        }
    }
}
