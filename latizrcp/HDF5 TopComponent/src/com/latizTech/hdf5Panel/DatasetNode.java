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

import com.latizTech.hdf5Panel.action.PlotAllTimesAction;
import com.latizTech.hdf5Panel.propertyEditors.FieldOfViewProperty;
import com.latizTech.hdf5Panel.propertyEditors.GridSpacingProperty;
import com.latizTech.hdf5Panel.propertyEditors.AliasProperty;
import com.latizTech.hdf5Panel.propertyEditors.DataTypeProperty;
import com.latizTech.hdf5Panel.propertyEditors.EnumPropertyEditor;
import com.latizTech.hdf5Panel.propertyEditors.GridSizeProperty;
import com.latizTech.hdf5Panel.propertyEditors.FullNameProperty;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.beans.PropertyEditor;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import javax.swing.Action;
import ncsa.hdf.object.CompoundDS;
import ncsa.hdf.object.HObject;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.nodes.PropertySupport;
import org.openide.nodes.Sheet;

/**
 *
 * @author rstjohn
 */
public class DatasetNode extends AbstractNode implements Transferable {

    public static final DataFlavor DATA_FLAVOR = new DataFlavor(DatasetNode.class, "HDF5 Dataset");
    private static final int GRID_SIZE_MEMBER = 2;
    private static final int GRID_SPACING_MEMBER = 3;
    private int[] dim;
    private double[] gridSpacing;
    private HObject hobj;
    private Integer dataType;

    public DatasetNode(HObject hobj) {
        //super(Children.LEAF);
        super(Children.create(new TimeChildrenFactory(hobj), true));
        this.hobj = hobj;
        setName(hobj.getFullName());
        setDisplayName(hobj.getName());
        if (hobj instanceof CompoundDS) {
            setIconBaseWithExtension("com/latizTech/hdf5Panel/resources/table.gif");
        } else {
            setIconBaseWithExtension("com/latizTech/hdf5Panel/resources/dataset.gif");
        }
    }

    public int getDataType() {
        if(dataType==null) {
            HashMap<String, Object> propMap = HDF5Reader.getDataSetProperties(hobj);
            dataType = (Integer) propMap.get("DataType");
            dim = (int[]) propMap.get("GridSize");
            gridSpacing = (double[]) propMap.get("GridSpacing");
        }
        return dataType.intValue();
    }

    public int[] getDim() {
        if(dim==null) {
            HashMap<String, Object> propMap = HDF5Reader.getDataSetProperties(hobj);
            dataType = (Integer) propMap.get("DataType");
            dim = (int[]) propMap.get("GridSize");
            gridSpacing = (double[]) propMap.get("GridSpacing");
        }
        return dim;
    }

    public double[] getGridSpacing() {
        if(gridSpacing==null) {
            HashMap<String, Object> propMap = HDF5Reader.getDataSetProperties(hobj);
            dataType = (Integer) propMap.get("DataType");
            dim = (int[]) propMap.get("GridSize");
            gridSpacing = (double[]) propMap.get("GridSpacing");
        }
        return gridSpacing;
    }

    @Override
    protected Sheet createSheet() {
        Sheet result = super.createSheet();
        Sheet.Set set = Sheet.createPropertiesSet();
        set.setDisplayName("Path Properties");

        set.put(new AliasProperty(hobj.getName()));
        set.put(new FullNameProperty(hobj.getFullName()));

        Sheet.Set set2 = Sheet.createExpertSet();
        set2.setDisplayName("Data Properties");

        if(dim==null) {
            HashMap<String, Object> propMap = HDF5Reader.getDataSetProperties(hobj);
            dataType = (Integer) propMap.get("DataType");
            dim = (int[]) propMap.get("GridSize");
            gridSpacing = HDF5Reader.getGridSpacingForSelectedIndex(hobj, 0);
        }
        if(dataType==null) {
            HashMap<String, Object> propMap = HDF5Reader.getDataSetProperties(hobj);
            dataType = (Integer) propMap.get("DataType");
            dim = (int[]) propMap.get("GridSize");
            gridSpacing = HDF5Reader.getGridSpacingForSelectedIndex(hobj, 0);
        }
        set2.put(new DataTypeProperty(dataType));
        set2.put(new GridSizeProperty(dim));
        set2.put(new GridSpacingProperty(gridSpacing));

        double[] fov = new double[]{dim[0] * gridSpacing[0], dim[1] * gridSpacing[1]};
        set2.put(new FieldOfViewProperty(fov));

        set2.put(new BooleanProperty());
        set2.put(new EnumProperty());

        result.put(set);
        result.put(set2);
        return result;
    }

    @Override
    public Action[] getActions(boolean b) {
        return new Action[] {PlotAllTimesAction.getDefault()};
    }

    public String getPath() {
        return hobj.getPath();
    }

    @Override
    public Transferable drag() throws IOException {
        return this;
    }

    public DataFlavor[] getTransferDataFlavors() {
        return new DataFlavor[]{DATA_FLAVOR};
    }

    public boolean isDataFlavorSupported(DataFlavor flavor) {
        return flavor == DATA_FLAVOR;
    }

    public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
        if (flavor == DATA_FLAVOR) {
            return this;
        } else {
            throw new UnsupportedFlavorException(flavor);
        }
    }

    public HObject getDataset() {
        return hobj;
    }

    /**
     *
     */
    private class BooleanProperty extends PropertySupport.ReadWrite<Boolean> {

        private boolean isSelected;

        public BooleanProperty() {
            super("Sample", Boolean.class, "SampleBoolean", "This is a sample boolean property.");
            isSelected = false;
        }

        @Override
        public Boolean getValue() throws IllegalAccessException, InvocationTargetException {
            return isSelected;
        }

        @Override
        public void setValue(Boolean arg0) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
            isSelected = arg0;
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

    static class TimeChildrenFactory extends ChildFactory<Double> {
        private HObject hobj;

        public TimeChildrenFactory(HObject hobj) {
            this.hobj = hobj;
        }

        @Override
        protected boolean createKeys(List<Double> arg0) {
            double[] times = HDF5Reader.readVariableTimes(hobj);
            for(int i=0; i<times.length; i++) {
                arg0.add(times[i]);
            }
            return true;
        }

        @Override
        protected Node createNodeForKey(Double arg0) {
            return new TimeNode(arg0);
        }
    }
}
