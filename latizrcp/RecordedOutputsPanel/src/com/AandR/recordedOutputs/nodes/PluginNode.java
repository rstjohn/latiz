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
package com.AandR.recordedOutputs.nodes;

import com.AandR.recordedOutputs.actions.DeselectAllAction;
import com.AandR.recordedOutputs.actions.SelectAllAction;
import javax.swing.Action;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;

/**
 *
 * @author rstjohn
 */
public class PluginNode extends AbstractNode {
    private final PluginObject pluginObject;

    public PluginNode(PluginObject pluginObject) {
        super(Children.create(new OutputChildFactory(pluginObject), false));
        this.pluginObject = pluginObject;
        String name = pluginObject.getPlugin().getName();
        setName(name);
        setDisplayName(name);
        setIconBaseWithExtension("com/AandR/palette/plugin/plugin22.png");
    }

    public PluginObject getPluginObject() {
        return pluginObject;
    }

    @Override
    public Action[] getActions(boolean arg0) {
        return new Action[] {
            new SelectAllAction(this),
            new DeselectAllAction(this),
        };
    }
 }
