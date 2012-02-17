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

import com.AandR.palette.plugin.AbstractPlugin;
import java.awt.Image;
import org.openide.util.ImageUtilities;

/**
 *
 * @author stjohnr
 */
public class PluginNode {

    private AbstractPlugin plugin;

    private static final Image DEFAULT_IMAGE = ImageUtilities.loadImage("com/AandR/palette/resources/defaultPlugin.png");

    private Image image;

    private String name, iconPath;

    public PluginNode(AbstractPlugin p) {
        this.plugin = p;
        this.name = p.getName();
        this.iconPath = p.getPluginKey().getIconPath();
        this.image = this.iconPath==null ? DEFAULT_IMAGE : ImageUtilities.loadImage(this.iconPath);
    }

    public void setName(String name) {
        this.name = name;
    }

    public AbstractPlugin getPlugin() {
        return plugin;
    }

    public Image getImage() {
        return image;
    }

    public String getName() {
        return name;
    }

    public String getClassType() {
        return plugin.getClass().getSimpleName();
    }
}