package com.AandR.latiz.gui.prefs;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


import com.AandR.gui.ColorChooser;
import com.AandR.gui.ui.JButtonX;
import com.AandR.latiz.core.PropertiesManager;
import com.AandR.latiz.gui.Connector;
import com.AandR.latiz.gui.Palette;

/**
 * @author Dr. Richard St. John
 * @version $Revision: 1.4 $, $Date: 2007/10/10 00:32:03 $
 */
public class PalettePrefPage extends AbstractPreferencePage implements ActionListener, ChangeListener {

    private int gridLineSpacing, gridType;
    private float lineWidth;
    private Color lineColor, gridLineColor = new Color(230, 230, 230), paletteColor = Color.WHITE;
    private LinePanel linePanel;
    private JSpinner weightSpinner;
    private PalettePanel palettePanel;
    private JSpinner spacingSpinner;
    private JComboBox comboGrid;

    public PalettePrefPage() {
        super("Palette Preferences");
        PropertiesManager props = PropertiesManager.getInstanceOf();
        paletteColor = Color.decode(props.getProperty(PropertiesManager.PALETTE_BACKGROUND));
        gridLineColor = Color.decode(props.getProperty(PropertiesManager.PALETTE_LINE_COLOR));
        gridLineSpacing = Integer.parseInt(props.getProperty(PropertiesManager.PALETTE_LINE_SPACING));
        gridType = Integer.parseInt(props.getProperty(PropertiesManager.PALETTE_GRID_TYPE));
        setPropPanel(createContentPanel());
    }

    private JPanel createContentPanel() {
        PropertiesManager props = PropertiesManager.getInstanceOf();
        lineColor = Color.decode(props.getProperty(PropertiesManager.CONNECTOR_LINE_COLOR));
        lineWidth = Float.parseFloat(props.getProperty(PropertiesManager.CONNECTOR_LINE_WEIGHT));

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(createInputsPanel(), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createInputsPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(createPalettePanel());
        panel.add(createConnectorLinePanel());
        return panel;
    }

    private JPanel createPalettePanel() {
        palettePanel = new PalettePanel();
        palettePanel.setLayout(new BorderLayout());
        palettePanel.setBorder(new TitledBorder("Palette Properties"));
        palettePanel.setPreferredSize(new Dimension(200, 200));

        spacingSpinner = new JSpinner(new SpinnerNumberModel(gridLineSpacing, 5, 50, 5));
        spacingSpinner.addChangeListener(new ChangeListener() {

            public void stateChanged(ChangeEvent e) {
                JSpinner spinner = (JSpinner) e.getSource();
                gridLineSpacing = Integer.parseInt(spinner.getValue().toString());
                palettePanel.repaint();
            }
        });

        comboGrid = new JComboBox(new String[]{"Lines", "Dots-1pt", "Dots-2pt", "Dots-3pt"});
        comboGrid.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                JComboBox box = (JComboBox) e.getSource();
                gridType = box.getSelectedIndex();
                palettePanel.repaint();
            }
        });

        JPanel buttonPanel = new JPanel(new GridLayout(8, 1, 3, 3));
        buttonPanel.add(createButton("Defaults", "PALETTE_DEFAULTS", ""));
        buttonPanel.add(createButton("Grid Type", "PALETTE_DEFAULTS", ""));
        buttonPanel.add(createButton("Grid Color", "GRID_COLOR", ""));
        buttonPanel.add(createButton("Palette Color", "PALETTE_COLOR", ""));
        buttonPanel.add(new JLabel("Grid Type"));
        buttonPanel.add(comboGrid);
        buttonPanel.add(new JLabel("Grid Spacing"));
        buttonPanel.add(spacingSpinner);

        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
        p.add(buttonPanel);

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(palettePanel, BorderLayout.CENTER);
        panel.add(p, BorderLayout.EAST);
        return panel;
    }

    private JPanel createConnectorLinePanel() {
        weightSpinner = new JSpinner(new SpinnerNumberModel(lineWidth, 0.50, 7.0, 0.50));
        weightSpinner.addChangeListener(this);
        weightSpinner.setPreferredSize(new Dimension(50, 25));

        JPanel weightPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        weightPanel.add(new JLabel("Specify Line Width:"));
        weightPanel.add(weightSpinner);
        weightPanel.add(createButton("Line Color", "LINE_COLOR", "Change line color"));
        weightPanel.add(createButton("Default", "DEFAULT", "Restore Default Color scheme"));

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new TitledBorder("Connector Line Properties"));
        panel.add(weightPanel, BorderLayout.CENTER);
        panel.add(createPreviewPanel(), BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createPreviewPanel() {
        JPanel previewPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        previewPanel.setBorder(new TitledBorder("Preview"));
        previewPanel.add(linePanel = new LinePanel());
        return previewPanel;
    }

    private JButtonX createButton(String label, String command, String toolTip) {
        JButtonX button = new JButtonX(label);
        button.setActionCommand(command);
        button.addActionListener(this);
        return button;
    }

    public void fireAcceptAction() {
        lineWidth = Float.valueOf(weightSpinner.getValue().toString());
        PropertiesManager props = PropertiesManager.getInstanceOf();
        props.setProperty(PropertiesManager.CONNECTOR_LINE_COLOR, "#" + Integer.toHexString(lineColor.getRGB()).substring(1));
        props.setProperty(PropertiesManager.CONNECTOR_LINE_WEIGHT, String.valueOf(lineWidth));
        props.setProperty(PropertiesManager.PALETTE_BACKGROUND, "#" + Integer.toHexString(paletteColor.getRGB()).substring(1));
        props.setProperty(PropertiesManager.PALETTE_LINE_COLOR, "#" + Integer.toHexString(gridLineColor.getRGB()).substring(1));
        props.setProperty(PropertiesManager.PALETTE_LINE_SPACING, String.valueOf(gridLineSpacing));
        props.setProperty(PropertiesManager.PALETTE_GRID_TYPE, String.valueOf(gridType));
        props.saveProperties();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        if (command.equalsIgnoreCase("LINE_COLOR")) {
            actionLineColor();
        } else if (command.equalsIgnoreCase("DEFAULT")) {
            actionRestoreDefaults();
        } else if (command.equalsIgnoreCase("GRID_COLOR")) {
            actionGridColor();
        } else if (command.equalsIgnoreCase("PALETTE_COLOR")) {
            actionPaletteColor();
        } else if (command.equalsIgnoreCase("PALETTE_DEFAULTS")) {
            actionPaletteDefaults();
        }
    }

    private void actionPaletteDefaults() {
        weightSpinner.setValue(lineWidth = Connector.LINE_WIDTH);
        spacingSpinner.setValue(gridLineSpacing = Palette.GRID_LINE_SPACING);
        comboGrid.setSelectedIndex(gridType = Palette.GRID_LINES);
        paletteColor = Palette.COLOR;
        gridLineColor = Palette.GRID_COLOR;
        palettePanel.repaint();
    }

    private void actionPaletteColor() {
        ColorChooser colorChooser = new ColorChooser(paletteColor);
        paletteColor = colorChooser.getSelectedColor();
        palettePanel.repaint();
    }

    private void actionGridColor() {
        ColorChooser colorChooser = new ColorChooser(gridLineColor);
        gridLineColor = colorChooser.getSelectedColor();
        palettePanel.repaint();
    }

    private void actionRestoreDefaults() {
        lineColor = Connector.COLOR;
        weightSpinner.setValue(lineWidth = 2f);
        linePanel.repaint();
    }

    public void stateChanged(ChangeEvent e) {
        lineWidth = Float.valueOf(weightSpinner.getValue().toString());
        linePanel.repaint();
    }

    private void actionLineColor() {
        ColorChooser colorChooser = new ColorChooser(lineColor);
        lineColor = colorChooser.getSelectedColor();
        linePanel.repaint();
    }

    private class PalettePanel extends JPanel {

        public PalettePanel() {
            setPreferredSize(new Dimension(200, 100));
            setOpaque(true);
        }

        @Override
        public void paintComponent(Graphics g) {
            g.setColor(paletteColor);
            setBackground(paletteColor);
            super.paintComponent(g);
            int w = getWidth();
            int h = getHeight();

            g.setColor(gridLineColor);
            if (gridType != Palette.GRID_LINES) {
                int dotSize = gridType;
                for (int j = 0; j < h; j += gridLineSpacing) {
                    for (int i = 0; i < w; i += gridLineSpacing) {
                        g.drawOval(i, j, dotSize, dotSize);
                        g.fillOval(i, j, dotSize, dotSize);
                    }
                }
            } else {
                for (int j = 0; j < h; j += gridLineSpacing) {
                    g.drawLine(0, j, w, j);
                }

                for (int j = 0; j < w; j += gridLineSpacing) {
                    g.drawLine(j, 0, j, h);
                }
            }
        }
    }

    /**
     *
     * @author Dr. Richard St. John
     * @version $Revision$, $Date$
     */
    private class LinePanel extends JPanel {

        private int arrowSize = 10;
        private int back = arrowSize / 2;

        public LinePanel() {
            setPreferredSize(new Dimension(200, 24));
        }

        private Polygon createArrowHead() {
            int w = getWidth();
            int yMid = getHeight() / 2;
            Point p1 = new Point(w - arrowSize, yMid);
            Point p2 = new Point(w - arrowSize - back, yMid - arrowSize);
            Point p3 = new Point(w, yMid);
            Point p4 = new Point(w - arrowSize - back, yMid + arrowSize);

            Polygon p = new Polygon();
            p.addPoint(p1.x, p1.y);
            p.addPoint(p2.x, p2.y);
            p.addPoint(p3.x, p3.y);
            p.addPoint(p4.x, p4.y);
            return p;
        }

        @Override
        public void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            super.paintComponent(g2);
            int x1 = 0, x2 = getWidth() - arrowSize;
            int y1 = getHeight() / 2;
            g2.setColor(lineColor);
            g2.setStroke(new BasicStroke(lineWidth));
            g2.drawLine(x1, y1, x2, y1);
            g2.fillPolygon(createArrowHead());
        }
    }
}
