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
package com.AandR.palette.paletteScene;

import org.netbeans.api.visual.layout.LayoutFactory;
import org.netbeans.api.visual.widget.LabelWidget;
import org.netbeans.api.visual.widget.Scene;
import org.netbeans.api.visual.widget.general.IconNodeWidget;


/**
 *
 * @author stjohnr
 */
public class PluginWidget extends IconNodeWidget {

    public PluginWidget(Scene scene, PluginNode pluginNode) {
        super(scene);
        super.setImage(pluginNode.getImage());
        super.setLabel(pluginNode.getName());
        setLayout(LayoutFactory.createVerticalFlowLayout(LayoutFactory.SerialAlignment.CENTER, 3));

        getLabelWidget().setUseGlyphVector(false);
        LabelWidget lw = new LabelWidget(scene, pluginNode.getClassType());
        lw.setUseGlyphVector(true);
        addChild(0, lw);
        //setBorder(BorderFactory.createResizeBorder(5));
    }
}
