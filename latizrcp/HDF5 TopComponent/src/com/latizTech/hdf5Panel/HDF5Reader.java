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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;
import ncsa.hdf.object.Attribute;
import ncsa.hdf.object.Group;
import ncsa.hdf.object.HObject;
import ncsa.hdf.object.h5.H5CompoundDS;
import ncsa.hdf.object.h5.H5File;

/**
 *
 * @author rstjohn
 */
public class HDF5Reader {

    private static final int GRID_SIZE_MEMBER = 2;
    private static final int GRID_SPACING_MEMBER = 3;
    private static final int TIME_MEMBER = 0;
    private static final int DATA_MEMBER = 1;

    @SuppressWarnings(value="unchecked")
    public static HashMap<String, Object> getDataSetProperties(HObject hobj) {
        HashMap<String, Object> map = new HashMap<String, Object>();

        if (hobj instanceof Group) {
            System.out.println("Group Dropped --- nothing to do.");
            return null;
        }

        List<Attribute> metadata;
        H5File h5File = null;
        try {
            h5File = (H5File) hobj.getFileFormat();
            H5CompoundDS dataset = (H5CompoundDS) h5File.get(hobj.getFullName());
            h5File.open();

            dataset.init();
            metadata = dataset.getMetadata();
            if (metadata.size() == 1 && metadata.get(0).getName().equals("TYPE")) {
                map.put("DataType", ((int[]) metadata.get(0).getValue())[0]);
            }
            dataset.setMemberSelection(false);
            dataset.selectMember(GRID_SIZE_MEMBER);

            int[] grid;
            long[] startDim = dataset.getStartDims();
            startDim[0] = 0;
            startDim[1] = 0;

            long[] selected = dataset.getSelectedDims();
            selected[0] = 1;
            selected[1] = 1;

            grid = (int[]) ((Vector) dataset.read()).get(0);
            map.put("GridSize", grid);

            dataset.setMemberSelection(false);
            dataset.selectMember(GRID_SPACING_MEMBER);

            startDim = dataset.getStartDims();
            startDim[0] = 0;
            startDim[1] = 0;

            selected = dataset.getSelectedDims();
            selected[0] = 1;
            selected[1] = 1;

            float[] gridSpacing = (float[]) ((Vector) dataset.read()).get(0);
            map.put("GridSpacing", new double[] {gridSpacing[0], gridSpacing[1]});
            
            h5File.close();
            return map;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @SuppressWarnings(value="unchecked")
    public static HashMap<String, Object> loadVariable(HObject hobj) throws OutOfMemoryError, Exception {
        if (hobj instanceof Group) {
            System.out.println("Group Dropped --- nothing to do.");
            return null;
        }
        H5File h5File = (H5File) hobj.getFileFormat();

        H5CompoundDS dataset = (H5CompoundDS) h5File.get(hobj.getFullName());
        h5File = (H5File) dataset.getFileFormat();
        h5File.open();

        dataset.init();

        // Determine whether data is complex or real.
        List<Attribute> metadata;
        metadata = hobj.getMetadata();
        int dataTypeFlag = ((int[]) metadata.get(0).getValue())[0];
        int factor = dataTypeFlag == LatFileConstants.ARRAY_COMPLEX ? 2 : 1;

        int rank = dataset.getRank();
        if (rank > 2) {
            System.out.println("Dimensions larger than 2 are not supported.");
            return null;
        }

        // Get all available times
        dataset.setMemberSelection(false);
        dataset.selectMember(TIME_MEMBER);

        long[] selected = dataset.getSelectedDims();
        selected[1] = 1;

        long[] startDim = dataset.getStartDims();
        startDim[0] = 0;
        startDim[1] = 0;

        double[] times = (double[]) ((Vector) dataset.read()).get(0);

        int dataid = dataset.open();

        // Define output data
        ArrayList<double[]> outData = new ArrayList<double[]>();

        // Declare return data
        double[][] data = null;

        // Declare intermediate parameters
        int[] dataDims;

        // Declare raw data stream
        //byte[] rawBytes;
        long[] start;

        for (int i = 0; i < times.length; i++) {

            // Define array dimensions.
            dataset.setMemberSelection(false);
            dataset.selectMember(GRID_SIZE_MEMBER);

            startDim = dataset.getStartDims();
            startDim[0] = 0;
            startDim[1] = 0;

            selected = dataset.getSelectedDims();
            selected[0] = 1;
            selected[1] = 1;

            dataDims = (int[]) ((Vector) dataset.read()).get(0);
            int nx = dataDims[0];
            int ny = factor * dataDims[1];

            // Define the memory space to read a chunk.
            selected = dataset.getSelectedDims();
            selected[0] = 1;

            // Define the offset at which to start the read.
            start = dataset.getStartDims();
            start[0] = i;
            start[1] = 0;

            // Read the raw data.
            //rawBytes = dataset.readBytes();
            dataset.setMemberSelection(false);
            dataset.selectMember(DATA_MEMBER);
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
                //return objectData;
                return null;
            }

            if (dataDims[0] > 1) {
                dataset.close(dataid);
                h5File.close();
                HashMap<String, Object> xyData = new HashMap<String, Object>();
                xyData.put("x", times);
                xyData.put("y", data);
                return xyData;
            } else {
                outData.add(data[0]);
            }
        }

        double[][] o = new double[outData.size()][];
        for (int i = 0; i < outData.size(); i++) {
            o[i] = outData.get(i);
        }
        dataset.close(dataid);
        h5File.close();
        HashMap<String, Object> xyData = new HashMap<String, Object>();
        xyData.put("x", times);
        xyData.put("y", o);
        return xyData;
    }

    @SuppressWarnings(value="unchecked")
    public static HashMap<String, Object> loadVariable(HObject hobj, double[] selectedTimes) throws OutOfMemoryError, Exception {
        if (hobj instanceof Group) {
            System.out.println("Group Dropped --- nothing to do.");
            return null;
        }
        H5File h5File = (H5File) hobj.getFileFormat();

        H5CompoundDS dataset = (H5CompoundDS) h5File.get(hobj.getFullName());
        h5File = (H5File) dataset.getFileFormat();
        h5File.open();

        dataset.init();

        int rank = dataset.getRank();
        if (rank > 2) {
            System.out.println("Dimensions larger than 2 are not supported.");
            return null;
        }

        // Get all available times
        dataset.setMemberSelection(false);
        dataset.selectMember(TIME_MEMBER);

        long[] selected = dataset.getSelectedDims();
        selected[1] = 1;

        long[] startDim = dataset.getStartDims();
        startDim[0] = 0;
        startDim[1] = 0;

        double[] times = (double[]) ((Vector) dataset.read()).get(0);

        int dataid = dataset.open();

        // Define output data
        ArrayList<double[]> outData = new ArrayList<double[]>();

        // Declare return data
        double[][] data = null;

        // Declare intermediate parameters
        int[] dataDims;

        // Declare raw data stream
        //byte[] rawBytes;
        long[] start;

        List<Attribute> metadata;
        for (int i = 0; i < selectedTimes.length; i++) {

            // Determine whether data is complex or real.
            metadata = dataset.getMetadata();
            int dataTypeFlag = ((int[]) metadata.get(0).getValue())[0];
            int factor = dataTypeFlag == LatFileConstants.ARRAY_COMPLEX ? 2 : 1;

            // Define array dimensions.
            dataset.setMemberSelection(false);
            dataset.selectMember(GRID_SIZE_MEMBER);

            startDim = dataset.getStartDims();
            startDim[0] = 0;
            startDim[1] = 0;

            selected = dataset.getSelectedDims();
            selected[0] = 1;
            selected[1] = 1;

            dataDims = (int[]) ((Vector) dataset.read()).get(0);
            int nx = dataDims[0];
            int ny = factor * dataDims[1];

            // Define the memory space to read a chunk.
            selected = dataset.getSelectedDims();
            selected[0] = 1;

            // Define the offset at which to start the read.
            start = dataset.getStartDims();
            start[0] = Arrays.binarySearch(times, selectedTimes[i]);
            start[1] = 0;

            // Read the raw data.
            //rawBytes = dataset.readBytes();
            dataset.setMemberSelection(false);
            dataset.selectMember(DATA_MEMBER);
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
                //return objectData;
                return null;
            }
            
            if (dataDims[0] > 1) {
                dataset.close(dataid);
                h5File.close();
                HashMap<String, Object> xyData = new HashMap<String, Object>();
                xyData.put("x", selectedTimes);
                xyData.put("y", data);
                return xyData;
            } else {
                outData.add(data[0]);
            }
        }

        double[][] o = new double[outData.size()][];
        for (int i = 0; i < outData.size(); i++) {
            o[i] = outData.get(i);
        }
        dataset.close(dataid);
        h5File.close();
        HashMap<String, Object> xyData = new HashMap<String, Object>();
        xyData.put("x", selectedTimes);
        xyData.put("y", o);
        return xyData;
    }

    public static double[] readVariableTimes(HObject hobj) {
        H5File h5File = null;
        try {
            h5File = (H5File) hobj.getFileFormat();
            H5CompoundDS dataset = (H5CompoundDS) h5File.get(hobj.getFullName());
            h5File.open();

            dataset.init();

            dataset.setMemberSelection(false);
            dataset.selectMember(TIME_MEMBER);

            long[] selected = dataset.getSelectedDims();
            selected[1] = 1;

            long[] startDim = dataset.getStartDims();
            startDim[0] = 0;
            startDim[1] = 0;

            double[] times = (double[]) ((Vector) dataset.read()).get(0);
            h5File.close();
            return times;
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static long[][] getDimensionsForSelectedTime(HObject hobj, int[] indices) {
        H5File h5File = null;
        try {
            h5File = (H5File) hobj.getFileFormat();
            H5CompoundDS dataset = (H5CompoundDS) h5File.get(hobj.getFullName());
            h5File.open();

            dataset.init();

            dataset.setMemberSelection(false);
            dataset.selectMember(GRID_SIZE_MEMBER);

            long[][] dims = new long[indices.length][2];
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
            h5File.close();
            return dims;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static double[] getGridSpacingForSelectedIndex(HObject hobj, int index) {
        H5File h5File = null;
        try {
            h5File = (H5File) hobj.getFileFormat();
            H5CompoundDS dataset = (H5CompoundDS) h5File.get(hobj.getFullName());
            h5File.open();

            dataset.init();
            dataset.setMemberSelection(false);
            dataset.selectMember(GRID_SPACING_MEMBER);

            long[] startDim = dataset.getStartDims();
            startDim[0] = index;
            startDim[1] = 0;

            long[] selected = dataset.getSelectedDims();
            selected[0] = 1;
            selected[1] = 1;

            float[] gridSpacing = (float[]) ((Vector) dataset.read()).get(0);
            return new double[]{gridSpacing[0], gridSpacing[1]};
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
