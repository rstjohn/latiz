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
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Line2D;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;

import com.AandR.latiz.core.Output;
import com.AandR.latiz.core.PropertiesManager;

/**
 * @author Dr. Richard St. John
 * @version $Revision$, $Date$
 */
public class OutputConnector extends JPanel {

    public static final Color COLOR = new Color(0, 0, 139);
    private static final int OFFSET_X = 0;
    private static final int OFFSET_Y = 0;
    private int arrowSize = 11, arrowSpacer = 2, dx, dy;
    private float lineWidth = 2f;
    private Color lineColor, backgroundColor, foregroundColor;
    private Dimension labelSize = new Dimension(150, 2 * arrowSize);
    private JLabel label;
    private JPanel arrowHeadPanel;
    private LinePanel linePanel;
    private Output output;
    private Point currentPoint;
    private String outputKey;
    private Timer timer;

    public OutputConnector(Output output) {
        this.output = output;
        setLayout(new FlowLayout(FlowLayout.LEFT, OFFSET_X, OFFSET_Y));
        setOpaque(false);

        PropertiesManager props = PropertiesManager.getInstanceOf();
        foregroundColor = Color.decode(props.getProperty(PropertiesManager.COLOR_CONNECTION_OUTGOING_FOREGROUND));
        backgroundColor = Color.decode(props.getProperty(PropertiesManager.COLOR_CONNECTION_OUTGOING_BACKGROUND));
        lineColor = backgroundColor;

        outputKey = output.getKey();
        label = new JLabel("<html> " + output.getKey() + " <i>" + output.getValueTypeSimpleName() + "</i></html>");
        label.setOpaque(true);
        label.setBorder(new EmptyBorder(0, 5, 0, 0));
        label.setForeground(foregroundColor);
        label.setBackground(backgroundColor);

        FontMetrics fontMetrics = label.getFontMetrics(label.getFont());
        int height = 22;
        if (fontMetrics.stringWidth(outputKey + output.getValueTypeSimpleName()) > 130) {
            height = 34;
        }
        int w = 150;
        label.setPreferredSize(new Dimension(w, height));
        label.setMinimumSize(new Dimension(w, height));

        linePanel = new LinePanel();

        arrowHeadPanel = new ArrowHeadPanel();
        arrowHeadPanel.setBounds(w + arrowSpacer, 1, arrowSize, 2 * arrowSize);

        add(label);
        add(linePanel);
        add(arrowHeadPanel);

        timer = new Timer(70, new AnimationListener());
        timer.setDelay(40);
    }

    public void setConnectorColor(Color foreground, Color background) {
        label.setForeground(foregroundColor = foreground);
        label.setBackground(backgroundColor = background);
        label.repaint();
        lineColor = backgroundColor;
        linePanel.repaint();
        arrowHeadPanel.repaint();
    }

    @Override
    public void setToolTipText(String tooltip) {
        label.setToolTipText(tooltip);
    }

    @Override
    public String getToolTipText() {
        return label.getToolTipText();
    }

    public JPanel getArrowHeadPanel() {
        return arrowHeadPanel;
    }

    public Rectangle getArrowHeadBounds() {
        return arrowHeadPanel.getBounds();
    }

    public String getOutputKey() {
        return outputKey;
    }

    public void resetConnector() {
        Rectangle r = OutputConnector.this.getBounds();
        currentPoint = arrowHeadPanel.getLocation();
        Point endingPoint = new Point(labelSize.width + arrowSpacer, r.y + 1);

        dx = (endingPoint.x - currentPoint.x) / 5;
        dy = (endingPoint.y - currentPoint.y) / 5;

        timer.start();
    }

    public JPanel getLinePanel() {
        return linePanel;
    }

    public final Output getOutput() {
        return output;
    }

    public JLabel getLabel() {
        return label;
    }

    /**
     *
     * @author Dr. Richard St. John
     * @version $Revision$, $Date$
     */
    private class LinePanel extends JPanel {

        public LinePanel() {
            setOpaque(false);
            setMinimumSize(new Dimension(1, 24));
            setPreferredSize(new Dimension(1, 24));
        }

        private void updateBounds() {
            Point center = getLocation();
            center.setLocation(center.x + 150, center.y + 12);
            Point locP1 = SwingUtilities.convertPoint(OutputConnector.this, center, this);
            Point locP2 = arrowHeadPanel.getLocation();

            Dimension sizeP1 = new Dimension(0, 1);
            Dimension sizeP2 = new Dimension(1, arrowHeadPanel.getPreferredSize().height);
            int xMin = locP1.x < locP2.x ? locP1.x : locP2.x;
            int yMin = locP1.y < locP2.y ? locP1.y : locP2.y;

            int xMax = (locP1.x + sizeP1.width) < (locP2.x + sizeP2.width) ? (locP2.x + sizeP2.width) : (locP1.x + sizeP1.width);
            int yMax = (locP1.y + sizeP1.height) < (locP2.y + sizeP2.height) ? (locP2.y + sizeP2.height) : (locP1.y + sizeP1.height);
            setBounds(xMin, yMin, xMax - xMin, yMax - yMin);
        }

        @Override
        public void paintComponent(Graphics g) {
            Point p1 = SwingUtilities.convertPoint(OutputConnector.this, new Point(labelSize.width, arrowSize), this);
            Point p2 = SwingUtilities.convertPoint(arrowHeadPanel, new Point(0, 10), this);

            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(lineColor);
            g2.setStroke(new BasicStroke(lineWidth));
            g2.draw(new Line2D.Double(p1, p2));
        }
    }

    /**
     *
     * @author Dr. Richard St. John
     * @version $Revision$, $Date$
     */
    private class ArrowHeadPanel extends JPanel {

        public ArrowHeadPanel() {
            int arrowSize = OutputConnector.this.arrowSize;
            setOpaque(false);
            setPreferredSize(new Dimension(arrowSize, 2 * arrowSize));
        }

        @Override
        public void setLocation(Point p) {
            super.setLocation(p);
            linePanel.setLocation(p);
            linePanel.updateBounds();
        }

        @Override
        public void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            Dimension d = getPreferredSize();
            g2.setColor(backgroundColor);
            Polygon arrowHead = new Polygon();
            arrowHead.addPoint(0, 0);
            arrowHead.addPoint(d.width + 1, d.height / 2);
            arrowHead.addPoint(0, d.height);
            g2.fillPolygon(arrowHead);
        }
    }

    /**
     * @author Dr. Richard St. John
     * @version $Revision$, $Date$
     */
    public class AnimationListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            currentPoint.translate(dx, dy);
            arrowHeadPanel.setLocation(currentPoint.x, currentPoint.y);
            Rectangle r = OutputConnector.this.getBounds();
            if ((arrowHeadPanel.getLocation().distance(new Point(labelSize.width + arrowSpacer, r.y + 1))) < 10) {
                arrowHeadPanel.setLocation(labelSize.width + arrowSpacer, r.y + 1);
                timer.stop();
            }
            revalidate();
            repaint();
        }
    }
}
