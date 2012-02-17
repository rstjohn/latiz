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

import com.AandR.latiz.core.lookup.LatizLookup;
import com.AandR.palette.cookies.PluginSelectionCookie;
import com.AandR.palette.plugin.AbstractPlugin;
import org.netbeans.api.visual.action.WidgetAction;
import org.netbeans.api.visual.action.WidgetAction.State;
import org.netbeans.api.visual.action.WidgetAction.WidgetMouseEvent;
import org.netbeans.api.visual.widget.Widget;

/**
 *
 * @author rstjohn
 */
public class PluginSelectAction extends WidgetAction.Adapter {

    private PaletteScene scene;

    public PluginSelectAction(PaletteScene scene){
        this.scene=scene;
    }

    @Override
    public State mousePressed(Widget w, WidgetMouseEvent arg1) {
        LatizLookup.getDefault().removeAllFromLookup(PluginSelectionCookie.class);
        final AbstractPlugin plugin = ((PluginNode) scene.findObject(w)).getPlugin();
        LatizLookup.getDefault().addToLookup(new PluginSelectionCookie() {
            public AbstractPlugin getSelectedPlugin() {
                return plugin;
            }
        });
        return State.REJECTED;
    }
}
