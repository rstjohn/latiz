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
package com.AandR.beans.plotting.imagePlotPanel;

import com.AandR.beans.plotting.imagePlotPanel.colormap.AbstractColorMap;
import com.AandR.beans.plotting.imagePlotPanel.colormap.ColorMapFactory;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.text.DecimalFormat;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * @author Dr. Richard St. John
 * @version $Revision: 1.4 $, $Date: 2007/09/15 15:57:13 $
 */
public class ColorBarPanel extends ImagePanel implements MouseListener {

    private int colorBarWidth,  colorBarHeight;
    private int numberOfLevels = 7;
    private double dataMin,  dataMax;
    private AbstractColorMap colorMap = ColorMapFactory.createColorMap(ColorMapFactory.JET);

    public ColorBarPanel(int canvasWidth, int canvasHeight) {
        super(canvasWidth, canvasHeight);
        setOpaque(false);
        addMouseListener(this);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Dimension dim = super.getSize();
        canvasWidth = dim.width;
        canvasHeight = dim.height;
        colorBarWidth = this.canvasWidth / 3;
        colorBarHeight = this.canvasHeight - 20;
        setOpaque(true);
        setBackground(new Color(255, 255, 255));
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        scaleGraphicsContext(g2);
        createBufferedImage();
        g2.drawImage(bufferedImage, 4, 0, null);
        addTextLabels(g2, numberOfLevels);
    }

    private void addTextLabels(Graphics2D g2, int numberOfLabels) {
        double thisValue;
        double min = dataMin;
        double max = dataMax;
        double dx = 1.0 / (double) (numberOfLabels - 1);
        DecimalFormat ef = new DecimalFormat("0.000E00");
        DecimalFormat df = new DecimalFormat("0.0000");
        for (int i = 0; i < numberOfLabels; i++) {
            thisValue = max + dx * i * (min - max);
            if (Math.abs(thisValue) > 1e4) {
                addNumberToLabel(g2, i * colorBarHeight / (numberOfLabels - 1) + 10, ef.format((float) thisValue));
            } else if (Math.abs(thisValue) < 1e-4) {
                addNumberToLabel(g2, i * colorBarHeight / (numberOfLabels - 1) + 10, ef.format((float) thisValue));
            } else {
                addNumberToLabel(g2, i * colorBarHeight / (numberOfLabels - 1) + 10, df.format((float) thisValue));
            }
        }
    }

    private void addNumberToLabel(Graphics2D g2, int yLoc, String number) {
        g2.drawLine(colorBarWidth - 5, yLoc, colorBarWidth + 5, yLoc);
        g2.drawString(number, colorBarWidth + 8, yLoc + 5);
    }

    private void createBufferedImage() {
        bufferedImage = new BufferedImage(canvasWidth, canvasHeight, BufferedImage.TYPE_INT_ARGB);
        WritableRaster raster = bufferedImage.getRaster();

        int[] thisValue;
        int i, j;
        float df = 255.0f / (float) colorBarHeight;
        for (j = 0; j < canvasHeight; j++) {
            for (i = 0; i < canvasWidth; i++) {
                raster.setPixel(i, j, new int[]{0, 0, 0, 0});
            }
        }
        for (j = 0; j < colorBarHeight; j++) {
            thisValue = colorMap.getColorValue((int) (df * (j)));
            int[] pixel = new int[]{thisValue[0], thisValue[1], thisValue[2], 255};
            for (i = 0; i < colorBarWidth; i++) {
                raster.setPixel(i, colorBarHeight - j - 1 + 10, pixel);
            }
        }
    }

    public int getColorBarHeight() {
        return colorBarHeight;
    }

    public void setColorBarSize(int width, int height) {
        this.colorBarWidth = width;
        this.canvasWidth = Math.max(colorBarWidth, canvasWidth);

        this.colorBarHeight = height;
        this.canvasHeight = Math.max(colorBarHeight + 20, canvasHeight);
        repaint();
    }

    public void setColorBarHeight(int colorBarHeight) {
        this.colorBarHeight = colorBarHeight;
        this.canvasHeight = Math.max(colorBarHeight + 20, canvasHeight);
        repaint();
    }

    public int getColorBarWidth() {
        return colorBarWidth;
    }

    public void setColorBarWidth(int colorBarWidth) {
        this.colorBarWidth = colorBarWidth;
        this.canvasWidth = Math.max(colorBarWidth, canvasWidth);
        repaint();
    }

    public void setDataRange(double dataMin, double dataMax) {
        this.dataMin = dataMin;
        this.dataMax = dataMax;
        repaint();
    }

    public int getNumberOfLevels() {
        return numberOfLevels;
    }

    public void setNumberOfLevels(int numberOfLevels) {
        this.numberOfLevels = numberOfLevels;
        repaint();
    }

    public AbstractColorMap getColorMap() {
        return colorMap;
    }

    public void setColorMap(AbstractColorMap colorMap) {
        this.colorMap = colorMap;
        repaint();
    }

    @Override
    public void setCanvasSize(int width, int height) {
        this.canvasWidth = width;
        this.canvasHeight = height;
        colorBarWidth = canvasWidth / 2;
        colorBarHeight = canvasHeight - 20;
        repaint();
    }

    public void mouseClicked(MouseEvent e) {
        if (e.getClickCount() == 2) {
            new PropertiesDialog(this);
        }
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

    public void mousePressed(MouseEvent e) {
    }

    public void mouseReleased(MouseEvent e) {
    }

    /**
     *
     * @author Dr. Richard St. John
     * @version $Revision$, $Date$
     */
    private class PropertiesDialog extends JDialog implements ActionListener {

        private JTextField fieldWidth,  fieldHeight,  fieldLevels;
        private JPanel colorBarPanel;

        public PropertiesDialog(JPanel colorBarPanel) {
            super(new Frame(), "Colorbar Properties", true);
            fieldWidth = new JTextField(String.valueOf(colorBarWidth), 4);
            fieldHeight = new JTextField(String.valueOf(colorBarHeight), 4);
            fieldLevels = new JTextField(String.valueOf(numberOfLevels), 4);
            this.colorBarPanel = colorBarPanel;
            setContentPane(createContentPane());
            pack();
            Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
            Dimension dialog = getSize();
            setLocation(screen.width - dialog.width - 10, screen.height / 2);
            setVisible(true);
        }

        private Container createContentPane() {

            JPanel panelSize = new JPanel();
            panelSize.add(new JLabel("Size: "));
            panelSize.add(fieldWidth);
            panelSize.add(fieldHeight);

            JPanel levelsPanel = new JPanel();
            levelsPanel.add(new JLabel("Levels"));
            levelsPanel.add(fieldLevels);

            JPanel panelInputs = new JPanel(new GridLayout(2, 0));
            panelInputs.add(panelSize);
            panelInputs.add(levelsPanel);

            JPanel panel = new JPanel(new BorderLayout());
            panel.add(panelInputs, BorderLayout.CENTER);
            panel.add(createButtonPanel(), BorderLayout.SOUTH);
            return panel;
        }

        private Container createButtonPanel() {
            JPanel panel = new JPanel();
            panel.add(createButton("Refresh", "REFRESH"));
            panel.add(createButton("Export", "EXPORT"));
            panel.add(createButton("Close", "CLOSE"));
            return panel;
        }

        private JButton createButton(String label, String actionCommand) {
            JButton button = new JButton(label);
            button.addActionListener(this);
            button.setActionCommand(actionCommand);
            return button;
        }

        public void actionPerformed(ActionEvent e) {
            String command = e.getActionCommand();
            if (command.equalsIgnoreCase("CLOSE")) {
                dispose();
            } else if (command.equalsIgnoreCase("REFRESH")) {
                try {
                    int width = Integer.parseInt(fieldWidth.getText());
                    int height = Integer.parseInt(fieldHeight.getText());
                    if (height > 550) {
                        throw new NumberFormatException();
                    }
                    setColorBarSize(width, height);
                    numberOfLevels = Integer.parseInt(fieldLevels.getText());
                    repaint();
                } catch (NumberFormatException ne) {
                    System.out.println("Invalid Colorbar Size.");
                    Toolkit.getDefaultToolkit().beep();
                }
            } else if (command.equalsIgnoreCase("EXPORT")) {
//                JFileChooser fc = new JFileChooser(currentDirectory);
//
//                if (fc.isCancelled()) {
//                    return;
//                }
//                currentDirectory = fc.getCurrentDirectory().getPath();
//                try {
//                    Image image = colorBarPanel.createImage(canvasWidth, colorBarHeight + 20);
//                    colorBarPanel.paint(image.getGraphics());
//                    ImageIO.write((BufferedImage) image, ImagePanel.PNG_FORMAT, fc.getSelectedFile());
//                    fc = null;
//                } catch (IOException e1) {
//                    e1.printStackTrace();
//                }
            }
        }
    }
}
