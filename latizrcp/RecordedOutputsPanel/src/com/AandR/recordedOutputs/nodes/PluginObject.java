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

import com.AandR.palette.plugin.AbstractPlugin;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author rstjohn
 */
public class PluginObject {
    
    private final AbstractPlugin plugin;
    private List<OutputDataObject> outputDataList;

    public PluginObject(AbstractPlugin plugin) {
        this.plugin = plugin;
        outputDataList = new ArrayList<OutputDataObject>();
    }

    public List<OutputDataObject> getOutputDataList() {
        return outputDataList;
    }

    public AbstractPlugin getPlugin() {
        return plugin;
    }
}