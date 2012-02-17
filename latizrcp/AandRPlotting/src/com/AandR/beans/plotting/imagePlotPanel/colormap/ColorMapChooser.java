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
package com.AandR.beans.plotting.imagePlotPanel.colormap;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import org.openide.util.ImageUtilities;

/**
 * @author Dr. Richard St. John
 * @version $Revision: 1.4 $, $Date: 2007/09/15 15:57:11 $
 */
public class ColorMapChooser extends JDialog implements ActionListener {

    private ImageIcon[] images = new ImageIcon[10];
    private Integer[] intValue = new Integer[10];
    private JComboBox comboColor;
    private AbstractColorMap colorMap;
    private boolean isCancelled;
    private int selectedColormap;

    public ColorMapChooser(JFrame frame) {
        this(frame, 0);
    }

    public ColorMapChooser(JFrame frame, int selectedColormap) {
        super(frame, "Colormap Chooser", true);
        setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                isCancelled = true;
                dispose();
            }
        });
        this.selectedColormap = selectedColormap;
        initialize();
        setContentPane(createContentPane());
        pack();
        centerOnScreen();
        setResizable(false);
        setVisible(true);
    }

    private void initialize() {
        images[0] = createIcon("com/AandR/beans/plotting/imagePlotPanel/colormap/rainbow.png", "Rainbow");
        images[1] = createIcon("com/AandR/beans/plotting/imagePlotPanel/colormap/jet.png", "Jet");
        images[2] = createIcon("com/AandR/beans/plotting/imagePlotPanel/colormap/hot.png", "Hot Metal");
        images[3] = createIcon("com/AandR/beans/plotting/imagePlotPanel/colormap/copper.png", "Copper");
        images[4] = createIcon("com/AandR/beans/plotting/imagePlotPanel/colormap/red.png", "Red");
        images[5] = createIcon("com/AandR/beans/plotting/imagePlotPanel/colormap/green.png", "Green");
        images[6] = createIcon("com/AandR/beans/plotting/imagePlotPanel/colormap/blue.png", "Blue");
        images[7] = createIcon("com/AandR/beans/plotting/imagePlotPanel/colormap/grey.png", "Grey");
        images[8] = createIcon("com/AandR/beans/plotting/imagePlotPanel/colormap/igrey.png", "Inverse Grey");
        images[9] = createIcon("com/AandR/beans/plotting/imagePlotPanel/colormap/matlab.png", "Matlab");

        intValue[0] = new Integer(0);
        intValue[1] = new Integer(1);
        intValue[2] = new Integer(2);
        intValue[3] = new Integer(3);
        intValue[4] = new Integer(4);
        intValue[5] = new Integer(5);
        intValue[6] = new Integer(6);
        intValue[7] = new Integer(7);
        intValue[8] = new Integer(8);
        intValue[9] = new Integer(9);
        comboColor = new JComboBox(intValue);
        ComboBoxRenderer renderer = new ComboBoxRenderer();
        renderer.setPreferredSize(new Dimension(250, 28));
        renderer.setLayout(new GridLayout(6, 1));
        comboColor.setRenderer(renderer);
        comboColor.setSelectedIndex(selectedColormap);
    }

    public static AbstractColorMap getColorMap(int index) {
        return ColorMapFactory.createColorMap(index);
    }

    private Container createContentPane() {
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(createButton("OK", "ACCEPT"));
        buttonPanel.add(createButton("Cancel", "CANCEL"));

        JPanel colormapPanel = new JPanel(new BorderLayout(5, 10));
        colormapPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        colormapPanel.add(new JLabel("Select a colormap from the list"), BorderLayout.NORTH);
        colormapPanel.add(comboColor, BorderLayout.CENTER);
        colormapPanel.add(buttonPanel, BorderLayout.SOUTH);

        return colormapPanel;
    }

    private final JButton createButton(String title, String actionCommand) {
        JButton button = new JButton(title);
        button.addActionListener(this);
        button.setActionCommand(actionCommand);
        return button;
    }

    private final void centerOnScreen() {
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension screenSize = toolkit.getScreenSize();
        int x = (screenSize.width - getWidth()) / 2;
        int y = (screenSize.height - getHeight()) / 2;
        setLocation(x, y);
    }

    public ImageIcon createIcon(String path, String desc) {
        ImageIcon icon = new ImageIcon(ImageUtilities.loadImage(path));
        icon.setDescription(desc);
        return icon;
    }

    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        if (command.equalsIgnoreCase("CANCEL")) {
            isCancelled = true;
        } else if (command.equalsIgnoreCase("ACCEPT")) {
            isCancelled = false;
            colorMap = ColorMapFactory.createColorMap(comboColor.getSelectedIndex());
        }
        setVisible(false);
    }

    private class ComboBoxRenderer extends JLabel implements ListCellRenderer {

        public ComboBoxRenderer() {
            setOpaque(true);
            setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
            setHorizontalAlignment(LEFT);
            setVerticalAlignment(CENTER);
        }
        /*
         * This method finds the image and text corresponding
         * to the selected value and returns the label, set up
         * to display the text and image.
         */

        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {

//    Get the selected index. (The index param isn't always valid, so just use the value.)
            int selectedIndex = ((Integer) value).intValue();

            if (isSelected) {
                setBackground(list.getSelectionBackground());
                setForeground(list.getSelectionForeground());
            } else {
                setBackground(list.getBackground());
                setForeground(list.getForeground());
            }

//    Set the icon and text.  If icon was null, say so.
            ImageIcon icon = images[selectedIndex];
            String desc = images[selectedIndex].getDescription();
            setIcon(icon);
            if (icon != null) {
                setText(desc);
                setFont(list.getFont());
            } else {
            }

            return this;
        }
    }

    public AbstractColorMap getColorMap() {
        return colorMap;
    }

    public boolean isCancelled() {
        return isCancelled;
    }
}
