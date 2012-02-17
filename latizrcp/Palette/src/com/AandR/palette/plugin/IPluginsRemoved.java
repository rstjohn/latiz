/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.AandR.palette.plugin;

import com.AandR.palette.paletteScene.PaletteScene;
import java.util.ArrayList;

/**
 *
 * @author rstjohn
 */
public interface IPluginsRemoved {
    public void removePlugins(PaletteScene scene, ArrayList<AbstractPlugin> pluginsRemoved);
}
