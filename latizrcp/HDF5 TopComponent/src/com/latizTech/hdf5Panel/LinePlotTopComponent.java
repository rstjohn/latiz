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
package com.latizTech.hdf5Panel;

import com.AandR.beans.plotting.LinePlotPanel.LinePlotPanel;
import java.lang.reflect.InvocationTargetException;
import net.miginfocom.swing.MigLayout;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.nodes.PropertySupport;
import org.openide.nodes.Sheet;
import org.openide.windows.CloneableTopComponent;

/**
 *
 * @author rstjohn
 */
public class LinePlotTopComponent extends CloneableTopComponent {

    private LinePlotPanel linePlotPanel;
    private PlotTopComponentNode plotTopComponentNode;

    public LinePlotTopComponent(LinePlotPanel linePlotPanel) {
        this.linePlotPanel = linePlotPanel;
        plotTopComponentNode = new PlotTopComponentNode();
        setActivatedNodes(new Node[]{plotTopComponentNode});
        setLayout(new MigLayout());
        add(linePlotPanel, "push,, grow");
    }

    private class PlotTopComponentNode extends AbstractNode {

        public PlotTopComponentNode() {
            super(Children.LEAF);
        }

        @Override
        protected Sheet createSheet() {
            Sheet result = super.createSheet();
            Sheet.Set set = Sheet.createPropertiesSet();
            set.setDisplayName("Path Properties");

            Sheet.Set set2 = Sheet.createExpertSet();
            set2.setDisplayName("Data Properties");
            
            for(String key : linePlotPanel.getPlotsMap().keySet()) {
                set2.put(new BooleanProperty(key));
            }
            result.put(set);
            result.put(set2);
            return result;
        }
    }


    /**
     *
     */
    private class BooleanProperty extends PropertySupport.ReadWrite<Boolean> {

        private boolean isSelected;
        private String plotName;

        public BooleanProperty(String plotName) {
            super(plotName, Boolean.class, plotName, "This is a sample boolean property.");
            this.plotName = plotName;
            isSelected = true;
        }

        @Override
        public Boolean getValue() throws IllegalAccessException, InvocationTargetException {
            return isSelected;
        }

        @Override
        public void setValue(Boolean arg0) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
            isSelected = arg0;
            linePlotPanel.setPlotVisible(plotName, isSelected);
        }
    }
}
