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

import com.AandR.recordedOutputs.properties.BeginTimeProperty;
import com.AandR.recordedOutputs.properties.EndTimeProperty;
import org.openide.explorer.view.CheckableNode;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Sheet;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author rstjohn
 */
public class OutputDataNode extends AbstractNode {
    private final OutputDataObject outputData;

    public OutputDataNode(final OutputDataObject outputData) {
        super(Children.LEAF, Lookups.singleton(new CheckableNode() {

            public boolean isCheckable() {
                return true;
            }

            public boolean isCheckEnabled() {
                return true;
            }

            public Boolean isSelected() {
                return outputData.isSelected();
            }

            public void setSelected(Boolean selected) {
                outputData.setSelected(selected);
            }
        }));
        this.outputData = outputData;
        setName(outputData.getName());
        setDisplayName(outputData.getName());
        setIconBaseWithExtension("com/AandR/recordedOutputs/resources/invisibleIcon.png");
    }

    public OutputDataObject getOutputData() {
        return outputData;
    }

    @Override
    protected Sheet createSheet() {
        Sheet result = super.createSheet();
        Sheet.Set set = Sheet.createPropertiesSet();
        set.put(new BeginTimeProperty(outputData));
        set.put(new EndTimeProperty(outputData));
        result.put(set);
        return result;
    }
}
