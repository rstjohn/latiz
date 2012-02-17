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
package com.AandR.palette.connectionPanel;

import com.AandR.latizOptions.connectionPanel.ConnectionPanelOptionsController;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.util.prefs.Preferences;
import javax.swing.JLabel;
import org.netbeans.api.visual.border.BorderFactory;
import org.netbeans.api.visual.layout.LayoutFactory;
import org.netbeans.api.visual.widget.ComponentWidget;
import org.netbeans.api.visual.widget.Scene;
import org.netbeans.api.visual.widget.Widget;
import org.openide.util.NbPreferences;


/**
 *
 * @author rstjohn
 */
public class InputConnectorWidget extends Widget {
    public static final int STATE_VALID_CONNECTION = 0;
    public static final int STATE_INVALID_CONNECTION = 1;
    public static final int STATE_NULL_CONNECTION = 2;
    
    private Color COLOR = new Color(220, 150, 255);
    private Color backgroundColor = new Color(220, 150, 255);
    private Color connectedColor;
    private Color borderColor = new Color(0,0,0);
    private Color defaultBackgroundColor = COLOR;
    private Color foregroundColor = Color.WHITE;
    private int height = 25;

    private boolean hasConnection = false;

    private ComponentWidget w;

    private FemaleArrow arrow;

    private JLabel label;

    public InputConnectorWidget(Scene scene, String key, String className, String tooltip) {
        super(scene);

        Preferences pref = NbPreferences.forModule(ConnectionPanelOptionsController.class);
        backgroundColor = Color.decode(pref.get("targetBackground", ConnectionPanelOptionsController.DEFAULT_TARGET_BACKGROUND));
        foregroundColor = Color.decode(pref.get("targetForeground", ConnectionPanelOptionsController.DEFAULT_FOREGROUND));
        defaultBackgroundColor = backgroundColor;
        int r = defaultBackgroundColor.getRed();
        int g = defaultBackgroundColor.getGreen();
        int b = defaultBackgroundColor.getBlue();
        connectedColor = new Color(r, g, b, 128);

        label = new JLabel("<HTML>" + key + " <I>" + className + "</I></HTML>");
        label.setForeground(foregroundColor);

        w = new ComponentWidget(scene, label);
        w.setPreferredSize(new Dimension(130, height));
        w.setBorder(BorderFactory.createEmptyBorder(0,5,0,0));
        setBackground(backgroundColor);
        setOpaque(true);

        setLayout(LayoutFactory.createHorizontalFlowLayout());
        addChild(arrow = new FemaleArrow(scene));
        addChild(w);
        setBorder(BorderFactory.createLineBorder(1, borderColor));
    }


    public FemaleArrow getArrowWidget() {
        return arrow;
    }

    public void setValidConnection(int state) {
        arrow.setValidConnection(state);
    }


    public void setBackgroundColor(Color color) {
        backgroundColor = color;
        defaultBackgroundColor = backgroundColor;
        int r = defaultBackgroundColor.getRed();
        int g = defaultBackgroundColor.getGreen();
        int b = defaultBackgroundColor.getBlue();
        connectedColor = new Color(r, g, b, 128);
        setBackground(hasConnection ? connectedColor : backgroundColor);
        //label.setBackground(color);
    }


    public void setForegroundColor(Color color) {
        foregroundColor = color;
        label.setForeground(foregroundColor);
    }


    public boolean hasConnection() {
        return hasConnection;
    }


    public void setHasConnection(boolean hasConnection) {
        this.hasConnection = hasConnection;
        backgroundColor = this.hasConnection ? connectedColor : defaultBackgroundColor;
        //label.setBackground(backgroundColor);
        setBackground(backgroundColor);
        revalidate();
        getScene().validate();
    }


    /**
     * 
     */
    public class FemaleArrow extends Widget {

        public FemaleArrow(Scene scene) {
            super(scene);
        }


        public boolean hasConnection() {
            return hasConnection;
        }


        public void setHasConnection(boolean hasConnection) {
            InputConnectorWidget.this.setHasConnection(hasConnection);
        }


        public void setValidConnection(int state) {
            switch(state) {
                case STATE_NULL_CONNECTION:
                    backgroundColor = hasConnection ? connectedColor : defaultBackgroundColor;
                    break;
                case STATE_VALID_CONNECTION:
                    backgroundColor = Color.GREEN;
                    break;
                case STATE_INVALID_CONNECTION:
                    backgroundColor = Color.RED;
                    break;
            }
            this.getScene().validate();
        }

        
        /**
         *
         */
        @Override
        protected Rectangle calculateClientArea() {
            return new Rectangle(0,0,12,height);
        }


        @Override
        protected void paintWidget() {
            Graphics2D g = getGraphics();
            paintComponent(g);
        }


        public void paintComponent(Graphics g) {
            int w = 12;
            int h = height;

            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            setOpaque(true);
            g2.setColor(hasConnection ? connectedColor : backgroundColor);
            g2.fillRect(0, 0, w, h);
            Polygon arrowHead = new Polygon();
            arrowHead.addPoint(0, 0);
            arrowHead.addPoint(w-1, h/2-1);
            arrowHead.addPoint(0, h-1);

            g2.setColor(hasConnection ? Color.RED : backgroundColor);
            g2.fillPolygon(arrowHead);

            g2.setColor(foregroundColor);
            g2.drawPolygon(arrowHead);

            //g2.setStroke(new BasicStroke(2));
            g2.setColor(defaultBackgroundColor);
            //g2.drawImage(ImageUtilities.loadImage("com/AandR/resources/remove_exc.gif"), -9, 3, null);
        }
    }
}
