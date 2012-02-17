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
package com.AandR.beans.plotting.latExplorer;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import ncsa.hdf.hdf5lib.exceptions.HDF5Exception;
import ncsa.hdf.object.Attribute;
import ncsa.hdf.object.Dataset;
import ncsa.hdf.object.FileFormat;
import ncsa.hdf.object.Group;
import ncsa.hdf.object.HObject;
import ncsa.hdf.object.h5.H5CompoundDS;
import ncsa.hdf.object.h5.H5File;

/**
 * @author Dr. Richard St. John
 * @version $Revision$, $Date$
 */
public class LatFileReader {

    private static final int TIME_MEMBER = 0;
    private static final int DATA_MEMBER = 1;
    private static final int GRID_SIZE_MEMBER = 2;
    private static final int GRID_SPACING_MEMBER = 3;
    private HashMap<String, LatFileRunTrace> runTraces;
    private H5File h5File;

    public LatFileReader() {
        runTraces = new HashMap<String, LatFileRunTrace>();
    }

    public void loadFile(File hdf5File, DefaultTreeModel model) throws Exception {
        h5File = new H5File(hdf5File.getPath(), H5File.READ);
        h5File.createFile(hdf5File.getPath(), FileFormat.FILE_CREATE_OPEN);
        h5File.open();
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) model.getRoot();
        DefaultMutableTreeNode fileRoot = (DefaultMutableTreeNode) h5File.getRootNode();
        model.insertNodeInto(fileRoot, root, root.getChildCount());
        traceRun(model, fileRoot);
        h5File.close();
    }

    public void open() {
        try {
            h5File.open();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void close() {
        try {
            h5File.close();
        } catch (HDF5Exception e) {
            e.printStackTrace();
        }
    }

    public HObject get(HObject hobject) {
        try {
            return h5File.get(hobject.getFullName());
        } catch (Exception e) {
            return null;
        }
    }

    @SuppressWarnings(value = "unchecked")
    public List<Attribute> getMetadata(HObject hobject) {
        h5File = (H5File) hobject.getFileFormat();
        open();
        HObject obj = get(hobject);
        List<Attribute> attributes = null;
        try {
            attributes = obj.getMetadata();
        } catch (Exception e) {
            e.printStackTrace();
        }
        List<Attribute> newAtt = new ArrayList<Attribute>();
        newAtt.addAll(attributes);
        close();
        return newAtt;
    }

    public void updateRunTrace(DefaultTreeModel model) {
        open();
        DefaultMutableTreeNode fileRoot = (DefaultMutableTreeNode) h5File.getRootNode();
        traceRun(model, fileRoot);
        close();
    }

    @SuppressWarnings(value = "unchecked")
    protected void traceRun(DefaultTreeModel model, Object child) {
        int cc;
        cc = model.getChildCount(child);
        LatFileRunTrace thisTrace;
        List<Attribute> metadata;
        Object userObject;
        for (int i = 0; i < cc; i++) {
            Object thisChild = model.getChild(child, i);

            userObject = ((DefaultMutableTreeNode) thisChild).getUserObject();
            if (!(userObject instanceof HObject)) {
                continue;
            }

            HObject hobject = (HObject) ((DefaultMutableTreeNode) thisChild).getUserObject();
            String key = hobject.getFile() + ":" + hobject.getFullName();
            thisTrace = new LatFileRunTrace();
            thisTrace.setAlias(hobject.getName());
            thisTrace.setVarName(hobject.getName());
            if (model.isLeaf(thisChild)) {
                Dataset dataset = (Dataset) get(hobject);
                try {
                    dataset.init();
                    metadata = dataset.getMetadata();
                    if (metadata.size() > 2 && metadata.get(0).getName().equals("TYPE") && metadata.get(2).getName().equals("iterations")) {
                        thisTrace.setIterationCount(((int[]) metadata.get(2).getValue())[0]);
                        //} else if(metadata.size()>0 && metadata.get(0).getName().startsWith(attributeName)) {
                        //  float[] vals = (float[])metadata.get(0).getValue();
                        //  thisTrace.setType((int)vals[1]);
                        //} else if(metadata.size()>0 && metadata.get(0).getName().startsWith("[time,")) {
                        //  float[] vals = (float[])metadata.get(0).getValue();
                        //  thisTrace.setType((int)vals[1]);
                    } else if (metadata.size() == 1 && metadata.get(0).getName().equals("TYPE")) {  //TODO New compound data format for lat file.
                        thisTrace.setType(((int[]) metadata.get(0).getValue())[0]);
                    }
                    thisTrace.setDims(dataset.getDims());
                    if (dataset instanceof H5CompoundDS) {
                        H5CompoundDS ds = (H5CompoundDS) dataset;
                        ds.setMemberSelection(false);
                        ds.selectMember(1);
                        thisTrace.setDataDesc(ds.getSelectedMemberTypes()[0].getDatatypeDescription());
                    } else {
                        thisTrace.setDataDesc(dataset.getDatatype().getDatatypeDescription());
                    }
                    runTraces.put(key, thisTrace);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                thisTrace.setDims(0, 0);
                thisTrace.setDataDesc("Group");
                runTraces.put(key, thisTrace);
                traceRun(model, thisChild);
            }
        }
    }

    public double[] getGridSpacingForSelectedIndex(HObject hobj, int index) {
        H5CompoundDS dataset = (H5CompoundDS) get(hobj);
        open();
        dataset.init();
        dataset.setMemberSelection(false);
        dataset.selectMember(GRID_SPACING_MEMBER);

        long[] startDim = dataset.getStartDims();
        startDim[0] = index;
        startDim[1] = 0;

        long[] selected = dataset.getSelectedDims();
        selected[0] = 1;
        selected[1] = 1;

        try {
            float[] gridSpacing = (float[]) ((Vector) dataset.read()).get(0);
            close();
            return new double[]{gridSpacing[0], gridSpacing[1]};
        } catch (HDF5Exception e) {
            e.printStackTrace();
            close();
            return null;
        }
    }

    public long[][] getDimensionsForSelectedTime(HObject hobject, int[] indices) {
        H5CompoundDS dataset = (H5CompoundDS) get(hobject);
        h5File = (H5File) dataset.getFileFormat();
        open();
        dataset.init();

        dataset.setMemberSelection(false);
        dataset.selectMember(GRID_SIZE_MEMBER);

        long[][] dims = new long[indices.length][2];
        try {
            int[] grid;
            for (int i = 0; i < indices.length; i++) {

                long[] startDim = dataset.getStartDims();
                startDim[0] = indices[i];
                startDim[1] = 0;

                long[] selected = dataset.getSelectedDims();
                selected[0] = 1;
                selected[1] = 1;

                grid = (int[]) ((Vector) dataset.read()).get(0);
                dims[i][0] = grid[0];
                dims[i][1] = grid[1];
            }
            close();
        } catch (HDF5Exception e) {
            e.printStackTrace();
            return null;
        }
        return dims;
    }


    /*
    public long[][] getDimensionsForSelectedTime(HObject hobj, int[] indices) {
    List<Attribute> attList = getMetadata(hobj);
    if(attList == null || attList.size()<1) return null;

    long[][] dims = new long[indices.length][2];
    for(int i=0; i<indices.length; i++) {
    dims[i][0] = (long)((float[])attList.get(indices[i]).getValue())[2];
    dims[i][1] = (long)((float[])attList.get(indices[i]).getValue())[3];
    }
    return dims;
    }
     */
    public double[] readVariableTimes(HObject hobject) {
        H5CompoundDS dataset = (H5CompoundDS) get(hobject);
        h5File = (H5File) dataset.getFileFormat();
        open();
        dataset.init();

        dataset.setMemberSelection(false);
        dataset.selectMember(TIME_MEMBER);

        long[] selected = dataset.getSelectedDims();
        selected[1] = 1;

        long[] startDim = dataset.getStartDims();
        startDim[0] = 0;
        startDim[1] = 0;

        try {
            double[] times = (double[]) ((Vector) dataset.read()).get(0);
            close();
            return times;
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /*
    public float[] readVariableTimes(HObject hobject) {
    List<Attribute> attList = getMetadata(hobject);
    if(attList == null || attList.size()<1) return null;

    ArrayList<Float> timesList = new ArrayList<Float>();
    Attribute thisAtt;
    String name; 
    for(int i=0; i<attList.size(); i++) {
    thisAtt = attList.get(i);
    name = thisAtt.getName();
    if(name.startsWith(attributeName) || name.startsWith("[time,")) {
    timesList.add(((float[])thisAtt.getValue())[0]);
    }
    }
    int i=0;
    float[] times = new float[timesList.size()];
    for(Float time : timesList) {
    times[i++] = time;
    }
    return times;
    }
     */
    public double[][] loadAsciiData(Dataset dataset) throws OutOfMemoryError, Exception {
        LatFileRunTrace thisRunTrace = runTraces.get(dataset.getFile() + ":" + dataset.getFullName());

        h5File = (H5File) dataset.getFileFormat();
        dataset.init();
        long[] startDims = dataset.getStartDims();
        startDims[0] = 0;
        startDims[1] = 0;

        long[] selectedDims = dataset.getSelectedDims();
        int iterationCount = thisRunTrace.getIterationCount();
        selectedDims[0] = iterationCount;
        selectedDims[1] = 1;

        int[] columnIndices = thisRunTrace.getSelectedColumns();
        int[] parameterIndices = thisRunTrace.getSelectedParameters();
        int[] realizationIndices = thisRunTrace.getSelectedRealizations();

        double[][] data = new double[columnIndices.length * parameterIndices.length * realizationIndices.length][iterationCount];
        String[] labels = new String[data.length];
        float[] colData;
        int currentParameterIndex, currentRealizationIndex;
        int rowCount = 0;
        for (int i = 0; i < columnIndices.length; i++) {

            for (int j = 0; j < realizationIndices.length; j++) {
                currentRealizationIndex = realizationIndices[j];

                for (int k = 0; k < parameterIndices.length; k++) {
                    currentParameterIndex = parameterIndices[k];

                    startDims[0] = (currentParameterIndex * realizationIndices.length + currentRealizationIndex) * iterationCount;
                    startDims[1] = columnIndices[i];
                    colData = (float[]) dataset.read();
                    for (int c = 0; c < colData.length; c++) {
                        data[rowCount][c] = colData[c];
                    }
                    labels[rowCount] = "c" + (columnIndices[i] + 1) + "_r" + (realizationIndices[j] + 1) + "_p" + (parameterIndices[k] + 1);
                    rowCount++;
                }
            }
        }
        return data;
    }

    public double[] readVariableTimes(DefaultMutableTreeNode treeNode) {
        return readVariableTimes((HObject) treeNode.getUserObject());
    }

    public Object loadVariable(HObject hobject, LatFileRunTrace runTrace) throws OutOfMemoryError, Exception {
        if (runTrace == null) {
            return null;
        }

        if (hobject instanceof Group) {
            System.out.println("Group Dropped --- nothing to do.");
            return null;
        }

        Dataset dataset = (Dataset) hobject;
        if (runTrace.getSelectedTimes() == null) {
            return loadAsciiData(dataset);
        }

        return loadVariable(hobject, runTrace, runTrace.getSelectedTimes());
    }

    public Object loadVariable(HObject hobject, LatFileRunTrace runTrace, double[] selectedTimes) throws OutOfMemoryError, Exception {
        if (runTrace == null) {
            return null;
        }

        if (hobject instanceof Group) {
            System.out.println("Group Dropped --- nothing to do.");
            return null;
        }

        // Loop over each of the selected times.
        double[] allTimes = readVariableTimes(hobject);

        H5CompoundDS dataset = (H5CompoundDS) get(hobject);
        h5File = (H5File) dataset.getFileFormat();

        open();

        dataset.init();

        int rank = dataset.getRank();
        if (rank > 2) {
            System.out.println("Dimensions larger than 2 are not supported.");
            return null;
        }

        int dataid = dataset.open();

        // Define output data
        ArrayList<double[]> outData = new ArrayList<double[]>();

        // Declare return data
        double[][] data = null;

        // Declare intermediate parameters
        long[] dataDims, selected, start;

        // Declare raw data stream
        //byte[] rawBytes;

        dataset.setMemberSelection(false);
        dataset.selectMember(DATA_MEMBER);

        for (int i = 0; i < selectedTimes.length; i++) {

            // Determine whether data is complex or real.
            int factor = runTrace.getType() == LatFileConstants.ARRAY_COMPLEX ? 2 : 1;

            // Define array dimensions.
            dataDims = runTrace.getSelectedDims()[i];
            int nx = (int) dataDims[0];
            int ny = factor * (int) dataDims[1];

            // Define the memory space to read a chunk.
            selected = dataset.getSelectedDims();
            selected[0] = 1;

            // Define the offset at which to start the read.
            start = dataset.getStartDims();
            start[0] = Arrays.binarySearch(allTimes, selectedTimes[i]);
            start[1] = 0;

            // Read the raw data.
            //rawBytes = dataset.readBytes();
            Object objectData = ((Vector) dataset.read()).get(0);

            data = new double[nx][ny];
            int x, y, j = 0;
            if (objectData instanceof double[]) {
                double[] v = (double[]) objectData;
                for (y = 0; y < ny; y++) {
                    for (x = 0; x < nx; x++) {
                        data[x][y] = v[j++];
                    }
                }
            } else if (objectData instanceof float[]) {
                float[] v = (float[]) objectData;
                for (y = 0; y < ny; y++) {
                    for (x = 0; x < nx; x++) {
                        data[x][y] = v[j++];
                    }
                }
            } else if (objectData instanceof int[]) {
                int[] v = (int[]) objectData;
                for (y = 0; y < ny; y++) {
                    for (x = 0; x < nx; x++) {
                        data[x][y] = v[j++];
                    }
                }
            } else if (objectData instanceof byte[]) {
                return objectData;
            }
            /*
            data = new double[nx][ny];

            int x, y, j, dataOffset = 3;

            String dataDesc = dataset.getSelectedMemberTypes()[0].getDatatypeDescription();
            dataDesc = "64-bit floating";
            if(dataDesc.startsWith("32-bit integer")) {
            for(y=0; y<ny; y++) {
            for(x=0; x<nx; x++) {
            j = 4*dataOffset++;
            data[x][y] = ((rawBytes[j++]&0xFF)<<0) + ((rawBytes[j++]&0xFF)<<8) + ((rawBytes[j++]&0xFF)<<16) + ((rawBytes[j]&0xFF)<<24);
            }
            }
            } else if(dataDesc.startsWith("32-bit float")) {
            for(y=0; y<ny; y++) {
            for(x=0; x<nx; x++) {
            j = 4*dataOffset++;
            data[x][y] = Float.intBitsToFloat(((rawBytes[j++]&0xFF)<<0) + ((rawBytes[j++]&0xFF)<<8) + ((rawBytes[j++]&0xFF)<<16) + ((rawBytes[j]&0xFF)<<24));
            }
            }
            } else if(dataDesc.startsWith("64-bit integer")) {
            for(y=0; y<ny; y++) {
            for(x=0; x<nx; x++) {
            j = 8*dataOffset++;
            data[x][y] = 
            ((rawBytes[j++]&0xFFL)<<0L) + ((rawBytes[j++]&0xFFL)<<8L) + ((rawBytes[j++]&0xFFL)<<16L) + ((rawBytes[j++]&0xFFL)<<24L) +
            ((rawBytes[j++]&0xFFL)<<32L) + ((rawBytes[j++]&0xFFL)<<40L) + ((rawBytes[j++]&0xFFL)<<48L) + ((rawBytes[j]&0xFFL)<<56L);
            }
            }
            } else if(dataDesc.startsWith("64-bit float")) {
            for(y=0; y<ny; y++) {
            for(x=0; x<nx; x++) {
            j = 8*dataOffset++;
            data[x][y] = Double.longBitsToDouble(
            ((rawBytes[j++]&0xFFL)<<0L) + ((rawBytes[j++]&0xFFL)<<8L) + ((rawBytes[j++]&0xFFL)<<16L) + ((rawBytes[j++]&0xFFL)<<24L) +
            ((rawBytes[j++]&0xFFL)<<32L) + ((rawBytes[j++]&0xFFL)<<40L) + ((rawBytes[j++]&0xFFL)<<48L) + ((rawBytes[j]&0xFFL)<<56L));
            }
            }
            } else if(dataDesc.startsWith("8-bit character")) {
            return rawBytes;
            }
             */
            if (dataDims[0] > 1) {
                dataset.close(dataid);
                close();
                return data;
            } else {
                outData.add(data[0]);
            }
        }

        double[][] o = new double[outData.size()][];
        for (int i = 0; i < outData.size(); i++) {
            o[i] = outData.get(i);
        }
        dataset.close(dataid);
        close();
        return o;
    }

    public double[][] loadVariable(HObject hobject) throws OutOfMemoryError, Exception {
        double[][] data = null;
        if (hobject instanceof Group) {
            System.out.println("Group Dropped --- nothing to do.");
            return null;
        } else if (hobject instanceof Dataset) {
            Dataset dataset = (Dataset) get(hobject);
            h5File = (H5File) dataset.getFileFormat();

            dataset.init();

            long[] dims;
            int dataTypeSize = dataset.getDatatype().getDatatypeSize();
            Object dataObject = dataset.getData();

            // String data.
            if (dataObject instanceof String[]) {
                System.out.println(((String[]) dataset.getData())[0]);
            } // 32-bit data
            else if (dataTypeSize == 4) {
                if (dataObject instanceof float[]) {
                    dims = dataset.getDims();
                    float[] dd = (float[]) dataObject;
                    if (dims.length == 1) {
                        data = new double[1][(int) dims[0]];
                    } else if (dims.length == 2) {
                        data = new double[(int) dims[0]][(int) dims[1]];
                    } else {
                        data = null;
                        System.out.println("Dimensions larger than 2 are not supported.");
                        return null;
                    }

                    int count = 0;
                    for (int j = 0; j < data[0].length; j++) {
                        for (int i = 0; i < data.length; i++) {
                            data[i][j] = dd[count++];
                        }
                    }
                } else if (dataObject instanceof int[]) {
                    dims = dataset.getDims();
                    int[] dd = (int[]) dataObject;
                    if (dims.length == 1) {
                        data = new double[1][(int) dims[0]];
                    } else if (dims.length == 2) {
                        data = new double[(int) dims[0]][(int) dims[1]];
                    } else {
                        data = null;
                        System.out.println("Dimensions larger than 2 are not supported.");
                        return null;
                    }

                    int count = 0;
                    for (int j = 0; j < data[0].length; j++) {
                        for (int i = 0; i < data.length; i++) {
                            data[i][j] = dd[count++];
                        }
                    }
                }

                // 64-bit data
            } else if (dataTypeSize == 8) {
                if (dataObject instanceof double[]) {
                    dims = dataset.getDims();
                    if (dims.length == 1) {
                        data = new double[1][(int) dims[0]];
                    } else if (dims.length == 2) {
                        data = new double[(int) dims[0]][(int) dims[1]];
                    } else {
                        data = null;
                        System.out.println("Dimensions larger than 2 are not supported.");
                        return null;
                    }
                    double[] dd = (double[]) dataObject;
                    int count = 0;
                    for (int j = 0; j < data[0].length; j++) {
                        for (int i = 0; i < data.length; i++) {
                            data[i][j] = dd[count++];
                        }
                    }
                } else if (dataObject instanceof long[]) {
                    dims = dataset.getDims();
                    if (dims.length == 1) {
                        data = new double[1][(int) dims[0]];
                    } else if (dims.length == 2) {
                        data = new double[(int) dims[0]][(int) dims[1]];
                    } else {
                        data = null;
                        System.out.println("Dimensions larger than 2 are not supported.");
                        return null;
                    }
                    long[] dd = (long[]) dataObject;
                    int count = 0;
                    for (int i = 0; i < data.length; i++) {
                        for (int j = 0; j < data[0].length; j++) {
                            data[i][j] = dd[count++];
                        }
                    }
                }
            }
        }
        return data;
    }

    public void setH5File(H5File file) {
        h5File = file;
    }

    public H5File getH5File() {
        return h5File;
    }

    public HashMap<String, LatFileRunTrace> getRunTrace() {
        return runTraces;
    }

    @SuppressWarnings(value = "unchecked")
    public HObject findObject(FileFormat file, String path) {
        if (file == null || path == null) {
            return null;
        }

        if (!path.endsWith("/")) {
            path = path + "/";
        }

        DefaultMutableTreeNode theRoot = (DefaultMutableTreeNode) file.getRootNode();

        if (theRoot == null) {
            return null;
        } else if (path.equals("/")) {
            return (HObject) theRoot.getUserObject();
        }

        Enumeration<DefaultMutableTreeNode> local_enum = theRoot.breadthFirstEnumeration();
        DefaultMutableTreeNode theNode = null;
        HObject theObj = null;
        while (local_enum.hasMoreElements()) {
            theNode = local_enum.nextElement();
            theObj = (HObject) theNode.getUserObject();
            String fullPath = theObj.getFullName() + "/";
            if (path.equals(fullPath)) {
                break;
            }
        }

        return theObj;
    }
}
