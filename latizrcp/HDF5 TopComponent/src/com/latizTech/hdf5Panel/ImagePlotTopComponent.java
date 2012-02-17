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

import com.AandR.beans.plotting.imagePlotPanel.ImagePlotPanel;
import com.latizTech.hdf5Panel.propertyEditors.AliasProperty;
import com.latizTech.hdf5Panel.propertyEditors.EnumPropertyEditor;
import com.latizTech.hdf5Panel.propertyEditors.FullNameProperty;
import com.latizTech.hdf5Panel.propertyEditors.GridSizeProperty;
import com.latizTech.hdf5Panel.propertyEditors.PathProperty;
import java.beans.PropertyEditor;
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
public class ImagePlotTopComponent extends CloneableTopComponent {

    private ImagePlotPanel panel;
    private PlotTopComponentNode plotTopComponentNode;
    private String path;

    public ImagePlotTopComponent(String path, ImagePlotPanel panel) {
        plotTopComponentNode = new PlotTopComponentNode();
        setActivatedNodes(new Node[]{plotTopComponentNode});
        this.panel = panel;
        this.path = path;
        setLayout(new MigLayout());
        add(panel, "push, grow");
    }

    private void setAvailablePlots() {

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
            int k = path.lastIndexOf("/");
            set.put(new AliasProperty(path.substring(k+1)));
            set.put(new PathProperty(path.substring(0, k)));
            set.put(new FullNameProperty(path));

            Sheet.Set set2 = Sheet.createExpertSet();
            set2.setDisplayName("Data Properties");
            set2.put(new DataProperty("Minimum Value", "Minimum Value", panel.getCanvas().getDataMin()));
            set2.put(new DataProperty("Maximum Value", "Maximum Value", panel.getCanvas().getDataMax()));
            set2.put(new DataProperty("GridSpacing x (m)", "GridSpacing x", panel.getCanvas().getGridSpacingX()));
            set2.put(new DataProperty("GridSpacing y (m)", "GridSpacing y", panel.getCanvas().getGridSpacingY()));
            set2.put(new GridSizeProperty(panel.getCanvas().getGridSize()));

            double[] extent = panel.getCanvas().getPhysicalExtent();
            set2.put(new DataProperty("ImageSize x (m)", "ImageSize x (m)", extent[0]));
            set2.put(new DataProperty("ImageSize y (m)", "ImageSize y (m)", extent[1]));
            result.put(set);
            result.put(set2);
            return result;
        }
    }


    private class EnumProperty extends PropertySupport.ReadWrite<String> {

        private String currentValue;

        public EnumProperty() {
            super("Enum", String.class, "SampleEnum", "SampleEnum");
            currentValue = "Hello";
        }

        @Override
        public String getValue() throws IllegalAccessException, InvocationTargetException {
            return currentValue;
        }

        @Override
        public PropertyEditor getPropertyEditor() {
            return new EnumPropertyEditor();
        }

        @Override
        public void setValue(String arg0) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
            currentValue = arg0;
        }
    }

    private class DataProperty extends PropertySupport.ReadOnly<Double> {

        private double value;

        public DataProperty(String name, String desc, double value) {
            super(name, Double.class, desc, desc);
            this.value = value;
        }

        @Override
        public Double getValue() throws IllegalAccessException, InvocationTargetException {
            return value;
        }
    }
}
