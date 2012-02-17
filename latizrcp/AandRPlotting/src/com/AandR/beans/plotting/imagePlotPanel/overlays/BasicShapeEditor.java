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
package com.AandR.beans.plotting.imagePlotPanel.overlays;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import javax.swing.colorchooser.AbstractColorChooserPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * @author Dr. Richard St. John
 * @version $Revision: 1.4 $, $Date: 2007/09/15 15:57:08 $
 */
public class BasicShapeEditor extends JDialog implements ActionListener {

    private boolean cancelled = false;
    private static BasicShapeEditor instanceOf = new BasicShapeEditor();
    private JCheckBox checkFilled;
    private JColorChooser colorChooser;
    private JRadioButton radioRectangle,  radioEllipse;
    private JSlider sliderTrans;
    private JTextField fieldUpperLeftX,  fieldUpperLeftY,  fieldWidth,  fieldHeight,  fieldPenWidth;
    private int transparency = 0;

    private BasicShapeEditor() {
        super(new Frame(), "Basic Shape Editor", true);
        initialize();
        setContentPane(createContent());
        pack();
    }

    private void initialize() {
        cancelled = false;
        radioRectangle = createRadioButton("Rectangle", false);
        radioEllipse = createRadioButton("Ellipse", false);
        fieldUpperLeftX = new JTextField(6);
        fieldUpperLeftY = new JTextField(6);
        fieldWidth = new JTextField(6);
        fieldHeight = new JTextField(6);

        fieldPenWidth = new JTextField(6);
        checkFilled = new JCheckBox("Fill Shape");
        checkFilled.setFont(checkFilled.getFont().deriveFont(10.0f));

        sliderTrans = new JSlider(0, 255, 0);
        sliderTrans.addChangeListener(new ChangeListener() {

            public void stateChanged(ChangeEvent e) {
                transparency = ((JSlider) e.getSource()).getValue();
            }
        });
        Dimension sliderDim = sliderTrans.getPreferredSize();
        sliderTrans.setPreferredSize(new Dimension(100, sliderDim.height));

        ButtonGroup shapeGroup = new ButtonGroup();
        shapeGroup.add(radioRectangle);
        shapeGroup.add(radioEllipse);
        colorChooser = new JColorChooser();
        colorChooser.setColor(Color.BLACK);
    }

    private Container createContent() {

        JPanel northPanel = new JPanel();
        northPanel.setLayout(new BoxLayout(northPanel, BoxLayout.Y_AXIS));
        northPanel.add(createTopPanel());
        northPanel.add(createMiddlePanel());

        JTabbedPane colorTabs = new JTabbedPane();
        colorTabs.add("Colors", findPanel(colorChooser, "javax.swing.colorchooser.DefaultHSBChooserPanel"));
        colorTabs.add("Swatches", findPanel(colorChooser, "javax.swing.colorchooser.DefaultSwatchChooserPanel"));
        colorTabs.add("Channels", findPanel(colorChooser, "javax.swing.colorchooser.DefaultRGBChooserPanel"));

        JPanel colorPanel = new JPanel();
        colorPanel.setBorder(BorderFactory.createTitledBorder("Choose Color"));
        colorPanel.add(colorTabs);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(colorPanel, BorderLayout.CENTER);

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(northPanel, BorderLayout.NORTH);
        panel.add(centerPanel, BorderLayout.CENTER);
        panel.add(createButtonPanel(), BorderLayout.SOUTH);
        return panel;
    }

    private JRadioButton createRadioButton(String label, boolean isSelected) {
        JRadioButton button = new JRadioButton(label, isSelected);
        button.setFont(button.getFont().deriveFont(10.0f));
        return button;
    }

    private JLabel createLabel(String label) {
        JLabel jlabel = new JLabel(label);
        jlabel.setFont(jlabel.getFont().deriveFont(10.0f));
        return jlabel;
    }

    public static JButton createButton(String label, ActionListener al, String actionCommand) {
        JButton button = new JButton(label);
        button.addActionListener(al);
        button.setActionCommand(actionCommand);
        return button;
    }

    private Component createTopPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 2));
        panel.add(createShapePanel());
        panel.add(createStrokePanel());
        return panel;
    }

    private Component createStrokePanel() {
        JPanel panelStroke = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelStroke.add(createLabel("Pen Width (pixels)"));
        panelStroke.add(fieldPenWidth);

        JPanel panelFilled = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelFilled.add(checkFilled);
        panelFilled.add(sliderTrans);

        JPanel panel = new JPanel(new GridLayout(2, 1));
        TitledBorder titledBorder = new TitledBorder("Pen Stroke");
        titledBorder.setTitleFont(titledBorder.getTitleFont().deriveFont(10.0f));
        panel.setBorder(titledBorder);
        panel.add(panelStroke);
        panel.add(panelFilled);
        return panel;
    }

    private Component createShapePanel() {
        JPanel panelShape = new JPanel(new GridLayout(2, 1));
        TitledBorder titledBorder = new TitledBorder("Choose Shape");
        titledBorder.setTitleFont(titledBorder.getTitleFont().deriveFont(10.0f));
        panelShape.setBorder(titledBorder);
        panelShape.add(radioRectangle);
        panelShape.add(radioEllipse);
        return panelShape;
    }

    private Component createLocationPanel() {
        JPanel panelField = new JPanel();
        panelField.add(fieldUpperLeftX);
        panelField.add(fieldUpperLeftY);

        JPanel panelLabel = new JPanel();
        panelLabel.add(createLabel("Specify the Upper Left Corner"));

        JPanel panel = new JPanel(new GridLayout(2, 1));
        TitledBorder titledBorder = new TitledBorder("Location");
        titledBorder.setTitleFont(titledBorder.getTitleFont().deriveFont(10.0f));
        panel.setBorder(titledBorder);
        panel.add(panelLabel);
        panel.add(panelField);
        return panel;
    }

    private Component createMiddlePanel() {
        JPanel panel = new JPanel(new GridLayout(1, 2));
        panel.add(createLocationPanel());
        panel.add(createDimensionPanel());
        return panel;
    }

    private Component createDimensionPanel() {
        JPanel panelField = new JPanel();
        panelField.add(fieldWidth);
        panelField.add(fieldHeight);

        JPanel panelLabel = new JPanel();
        panelLabel.add(createLabel("Specify the Width and Height"));

        JPanel panel = new JPanel(new GridLayout(2, 1));
        TitledBorder titledBorder = new TitledBorder("Dimension");
        titledBorder.setTitleFont(titledBorder.getTitleFont().deriveFont(10.0f));
        panel.setBorder(titledBorder);
        panel.add(panelLabel);
        panel.add(panelField);
        return panel;
    }

    private Component createButtonPanel() {
        JPanel panel = new JPanel();
        panel.add(createButton("Accept", this, "OK"));
        panel.add(createButton("Cancel", this, "CANCEL"));
        return panel;
    }

    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        if (command.equalsIgnoreCase("OK")) {
            cancelled = false;
            setVisible(false);
        } else if (command.equalsIgnoreCase("CANCEL")) {
            cancelled = true;
            setVisible(false);
        }
    }

    private AbstractColorChooserPanel findPanel(JColorChooser chooser, String name) {
        AbstractColorChooserPanel[] panels = chooser.getChooserPanels();

        for (int i = 0; i < panels.length; i++) {
            String clsName = panels[i].getClass().getName();

            if (clsName.equals(name)) {
                return panels[i];
            }
        }
        return null;
    }

    public void setSelectedShape(int shape) {
        if (shape == BasicShape.ELLIPSE) {
            radioEllipse.setSelected(true);
        } else {
            radioRectangle.setSelected(true);
        }
    }

    public Color getSelectedColor() {
        return colorChooser.getColor();
    }

    public int getSelectedShape() {
        if (radioEllipse.isSelected()) {
            return BasicShape.ELLIPSE;
        }
        return BasicShape.RECTANGLE;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public JColorChooser getColorChooser() {
        return colorChooser;
    }

    public static BasicShapeEditor getInstanceOf() {
        return instanceOf;
    }

    public JTextField getFieldHeight() {
        return fieldHeight;
    }

    public void setFieldHeight(JTextField fieldHeight) {
        this.fieldHeight = fieldHeight;
    }

    public JTextField getFieldUpperLeftX() {
        return fieldUpperLeftX;
    }

    public void setFieldUpperLeftX(JTextField fieldUpperLeftX) {
        this.fieldUpperLeftX = fieldUpperLeftX;
    }

    public JTextField getFieldUpperLeftY() {
        return fieldUpperLeftY;
    }

    public void setFieldUpperLeftY(JTextField fieldUpperLeftY) {
        this.fieldUpperLeftY = fieldUpperLeftY;
    }

    public JTextField getFieldWidth() {
        return fieldWidth;
    }

    public void setFieldWidth(JTextField fieldWidth) {
        this.fieldWidth = fieldWidth;
    }

    public JTextField getFieldPenWidth() {
        return fieldPenWidth;
    }

    public int getTransparency() {
        return transparency;
    }

    public JSlider getSliderTrans() {
        return sliderTrans;
    }

    public JCheckBox getCheckFilled() {
        return checkFilled;
    }
}
