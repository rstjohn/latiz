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
public class OutputConnectorWidget extends Widget {
    private int arrowWidth = 11;

    private int arrowHeight = 22;

    private int height = 25;

    private Color backgroundColor, foregroundColor;

    private Color borderColor = new Color(0,0,0);

    private ComponentWidget w;

    private MaleArrow arrowWidget;
    private JLabel label;

    public OutputConnectorWidget(Scene scene, String key, String className, String tooltip) {
        super(scene);

        Preferences pref = NbPreferences.forModule(ConnectionPanelOptionsController.class);
        backgroundColor = Color.decode(pref.get("sourceBackground", ConnectionPanelOptionsController.DEFAULT_SOURCE_BACKGROUND));
        foregroundColor = Color.decode(pref.get("sourceForeground", ConnectionPanelOptionsController.DEFAULT_FOREGROUND));

        label = new JLabel("<HTML>" + key + " <I>" + className + "</I></HTML>");
        label.setForeground(foregroundColor);
        
        w = new ComponentWidget(scene, label);
        w.setPreferredSize(new Dimension(130, height));
        w.setOpaque(true);
        w.setBorder(BorderFactory.createCompositeBorder(BorderFactory.createLineBorder(1, borderColor), BorderFactory.createEmptyBorder(0,5,0,0)));
        w.setBackground(backgroundColor);

        setLayout(LayoutFactory.createHorizontalFlowLayout());
        setBackground(backgroundColor);
        addChild(w);
        addChild(arrowWidget = new MaleArrow(scene));
        setBorder(BorderFactory.createEmptyBorder(1));
    }


    public Widget getArrowWidget() {
        return arrowWidget;
    }


    public void setBackgroundColor(Color color) {
        backgroundColor = color;
        setBackground(backgroundColor);
        label.setBackground(backgroundColor);
        w.setBackground(backgroundColor);
    }


    public void setForegroundColor(Color color) {
        foregroundColor = color;
        label.setForeground(foregroundColor);
    }


    /**
     * 
     */
    public class MaleArrow extends Widget {

        public MaleArrow(Scene scene) {
            super(scene);
        }


        @Override
        protected Rectangle calculateClientArea() {
            return new Rectangle(0,0,arrowWidth+2,arrowHeight+2);
        }


        @Override
        protected void paintWidget() {
            Graphics2D g = getGraphics();
            paintComponent(g);
        }


        public void paintComponent(Graphics g) {
            int w = arrowWidth;
            int h = arrowHeight;
            int s = 2;
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(backgroundColor);
            Polygon arrowHead = new Polygon();
            arrowHead.addPoint(s, 0);
            arrowHead.addPoint(s + w + 1, (h+1) / 2);
            arrowHead.addPoint(s, h+2);
            g2.fillPolygon(arrowHead);
            g2.setPaint(borderColor);
            g2.drawPolygon(arrowHead);
        }
    }
}
