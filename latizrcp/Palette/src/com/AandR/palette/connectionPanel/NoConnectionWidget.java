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

import java.awt.Color;
import java.awt.Dimension;
import javax.swing.JLabel;
import org.netbeans.api.visual.border.BorderFactory;
import org.netbeans.api.visual.layout.LayoutFactory;
import org.netbeans.api.visual.widget.ComponentWidget;
import org.netbeans.api.visual.widget.Scene;
import org.netbeans.api.visual.widget.Widget;


/**
 *
 * @author rstjohn
 */
public class NoConnectionWidget extends Widget {
    private int width = 143;
    private int height = 25;
    private Color backgroundColor, foregroundColor;
    private Color borderColor = new Color(0,0,0);
    private ComponentWidget w;
    private JLabel label;

    public NoConnectionWidget(Scene scene, String text, String tooltip) {
        super(scene);

        backgroundColor = new Color(232, 232, 232);
        foregroundColor = Color.DARK_GRAY;

        label = new JLabel(text);
        label.setForeground(foregroundColor);

        w = new ComponentWidget(scene, label);
        w.setPreferredSize(new Dimension(width, height));
        w.setOpaque(true);
        w.setBorder(BorderFactory.createCompositeBorder(BorderFactory.createLineBorder(1, borderColor), BorderFactory.createEmptyBorder(0,5,0,0)));
        w.setBackground(backgroundColor);

        setLayout(LayoutFactory.createHorizontalFlowLayout());
        setBackground(backgroundColor);
        addChild(w);
        setBorder(BorderFactory.createEmptyBorder(1));
    }
}
