package com.AandR.latiz.dev;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

import javax.swing.tree.DefaultMutableTreeNode;

import com.AandR.beans.latFileExplorerPanel.LatFileConstants;
import com.AandR.latiz.interfaces.LatFileInterface;

import ncsa.hdf.hdf5lib.H5;
import ncsa.hdf.hdf5lib.HDF5Constants;
import ncsa.hdf.hdf5lib.exceptions.HDF5Exception;
import ncsa.hdf.hdf5lib.exceptions.HDF5LibraryException;
import ncsa.hdf.object.Attribute;
import ncsa.hdf.object.Dataset;
import ncsa.hdf.object.Datatype;
import ncsa.hdf.object.FileFormat;
import ncsa.hdf.object.Group;
import ncsa.hdf.object.HObject;
import ncsa.hdf.object.h5.H5CompoundDS;
import ncsa.hdf.object.h5.H5Datatype;
import ncsa.hdf.object.h5.H5File;
import ncsa.hdf.object.h5.H5ScalarDS;

/**
 * @author Dr. Richard St. John
 * @version $Revision$, $Date$
 */
public class HDF5Worker {

  public static int initialIndex = 0;

  public static HashMap<String,Integer> counterMap = new HashMap<String, Integer>();

  private static double[] gridpointSpacing = new double[] {1.0, 1.0}; 

  public static FileFormat h5File;

  private static int dataType = LatFileConstants.ARRAY_DOUBLE;

  private static int[] gridDims;


  public static int getGroupID(int fid, String latizSystemName, String pluginName) throws HDF5LibraryException, NullPointerException {
    return H5.H5Gopen(fid, "/" + latizSystemName + "/" + pluginName);
  }


  public static void close() {
    try {
      h5File.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
  
  private static void initialize() {
	  counterMap.clear();
	  gridDims = null;
	  gridpointSpacing = new double[] {1.0,1.0};
	  initialIndex=0;
  }
  
  public static void createGroups(String filename, String latizSystemName, ArrayList<AbstractPlugin> plugins) throws Exception {
    initialize();
	h5File = new H5File(filename);
    h5File.createFile(filename, FileFormat.FILE_CREATE_DELETE);
    h5File.open();

    // Create root group
    Group root = h5File.createGroup("/"+latizSystemName, null);

    // Add LatFile attribute.
    HObject rootObject = (HObject) ((DefaultMutableTreeNode)h5File.getRootNode()).getUserObject();
    rootObject.writeMetadata(new Attribute("LAT", new H5Datatype(Datatype.CLASS_INTEGER, 4, -1, -1), new long[] {1}, new int[] {1}));

    String[] groupNames;
    Group pluginGroup;
    for(AbstractPlugin p : plugins) {
      groupNames = p.getLatFileVariableNames();
      if(groupNames==null) continue;

      // Create plugin group
      pluginGroup = h5File.createGroup(p.getName(), root);

      // initialize counter map.
      for(String dsName : groupNames) {
        counterMap.put(pluginGroup.getFullName() + "/" + dsName, initialIndex);
      }
    }
    h5File.close();
  }
  
  
  private static Group createGroup(String groupName, Group group) throws Exception {
    String[] groupSplit = groupName.split("/");
    String subGroupName = group.getFullName();
    Group g;
    Group g2 = group;
    h5File.open();
    for(int k=0; k<groupSplit.length; k++) {
      subGroupName += "/" + groupSplit[k];
      g = (Group) h5File.get(subGroupName);
      if(g == null) {
        g2 = h5File.createGroup(subGroupName, null);
      }
    }
    h5File.close();
    return g2;
  }


  public static boolean exists(String group) {
    try {
      h5File.open();
      HObject hobject = h5File.get(group);
      h5File.close();
      return hobject != null;
    } catch (Exception e) {
      return false;
    }
  }
  
  public static void createGroup(String latizSystemName, String pluginName) {
    try {
      String groupName = "/"+latizSystemName+"/"+pluginName; 
      h5File.open();
      if(!exists(groupName)) h5File.createGroup(groupName, null);
      h5File.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
  

  /**
   * The main method for writing data to the lat-file.  This method will choice the correct signature to use based on the 
   * data variable.
   * @param groupPath
   * @param datasetName
   * @param data
   * @param time
   */
  public static void appendData(String groupPath, String datasetName, Object data, double time) {
    try {
      if(data instanceof LatFileInterface) 
        appendData(groupPath, datasetName, (LatFileInterface)data, time);
      else if(data instanceof Integer) 
        appendData(groupPath, datasetName, new int[][] {{(Integer) data}}, time);
      else if(data instanceof int[]) 
        appendData(groupPath, datasetName, new int[][] {(int[])data}, time);
      else if(data instanceof int[][]) 
        appendData(groupPath, datasetName, (int[][])data, time);
      else if(data instanceof Float) 
        appendData(groupPath, datasetName, new float[][] {{(Float) data}}, time);
      else if(data instanceof float[]) 
        appendData(groupPath, datasetName, new float[][] {(float[])data}, time);
      else if(data instanceof float[][]) 
        appendData(groupPath, datasetName, (float[][])data, time);
      else if(data instanceof Integer) 
        appendData(groupPath, datasetName, new long[][] {{(Long) data}}, time);
      else if(data instanceof int[]) 
        appendData(groupPath, datasetName, new long[][] {(long[])data}, time);
      else if(data instanceof int[][]) 
        appendData(groupPath, datasetName, (long[][])data, time);
      else if(data instanceof Double) 
        appendData(groupPath, datasetName, new double[][] {{(Double)data}}, time);
      else if(data instanceof double[]) 
        appendData(groupPath, datasetName, new double[][] {(double[])data}, time);
      else if(data instanceof double[][]) 
        appendData(groupPath, datasetName, (double[][])data, time);
      else if(data instanceof byte[]) 
        appendData(groupPath, datasetName, (byte[])data, time);
      else if(data instanceof File) 
        appendData(groupPath, datasetName, new File[] {(File)data}, time);
      else if(data instanceof File[]) 
        appendData(groupPath, datasetName, (File[])data, time);
      else if(data instanceof String) 
        appendData(groupPath, datasetName, new String[] {(String)data}, time);
      else if(data instanceof String[]) 
        appendData(groupPath, datasetName, (String[])data,time);

      else {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
        objectOutputStream.writeObject(data);
        objectOutputStream.close();
        appendData(groupPath, datasetName, byteArrayOutputStream.toByteArray(), time);
      }
    } catch(Exception e) {
      e.printStackTrace();
    }
  }


  /**
   * Method to append double[][] data to the LatFile.
   * @param gid
   * @param dsName
   * @param data
   * @throws NullPointerException
   * @throws HDF5Exception
   */
  public static void appendData(String group, String dsName, byte[] data, double time) throws Exception {
    appendData(group, dsName, Datatype.CLASS_INTEGER, 1, 1, data.length, data, time);
  }


  /**
   * 
   * @param gid
   * @param dsName
   * @param data
   * @param time
   * @throws Exception
   */
  public static void appendData(String gid, String dsName, int data, double time) throws Exception {
    appendData(gid, dsName, new int[][] {{data}}, time);
  }


  /**
   * 
   * @param gid
   * @param dsName
   * @param data
   * @param time
   * @throws Exception
   */
  public static void appendData(String gid, String dsName, int[] data, double time) throws Exception {
    appendData(gid, dsName, new int[][] {data}, time);
  }

  /**
   * Method to append double[][] data to the LatFile.
   * @param gid
   * @param dsName
   * @param data
   * @throws NullPointerException
   * @throws HDF5Exception
   */
  public static void appendData(String group, String dsName, int[][] data, double time) throws Exception {
    appendData(group, dsName, Datatype.CLASS_INTEGER, 4, data.length, data[0].length, data, time);
  }


  /**
   * 
   * @param gid
   * @param dsName
   * @param data
   * @param time
   * @throws Exception
   */
  public static void appendData(String gid, String dsName, float data, double time) throws Exception {
    appendData(gid, dsName, new float[][] {{data}}, time);
  }


  /**
   * 
   * @param gid
   * @param dsName
   * @param data
   * @param time
   * @throws Exception
   */
  public static void appendData(String gid, String dsName, float[] data, double time) throws Exception {
    appendData(gid, dsName, new float[][] {data}, time);
  }


  /**
   * 
   * @param group
   * @param dsName
   * @param data
   * @param time
   * @throws Exception
   */
  public static void appendData(String group, String dsName, float[][] data, double time) throws Exception {
    appendData(group, dsName, Datatype.CLASS_FLOAT, 4, data.length, data[0].length, data, time);
  }


  /**
   * 
   * @param gid
   * @param dsName
   * @param data
   * @param time
   * @throws Exception
   */
  public static void appendData(String gid, String dsName, long data, double time) throws Exception {
    appendData(gid, dsName, new long[][] {{data}}, time);
  }


  /**
   * 
   * @param gid
   * @param dsName
   * @param data
   * @param time
   * @throws Exception
   */
  public static void appendData(String gid, String dsName, long[] data, double time) throws Exception {
    appendData(gid, dsName, new long[][] {data}, time);
  }


  /**
   * Method to append double[][] data to the LatFile.
   * @param group
   * @param dsName
   * @param data
   * @param time
   * @throws Exception
   */
  public static void appendData(String group, String dsName, long[][] data, double time) throws Exception {
    appendData(group, dsName, Datatype.CLASS_INTEGER, 8, data.length, data[0].length, data, time);
  }


  /**
   * 
   * @param gid
   * @param dsName
   * @param data
   * @param time
   * @throws Exception
   */
  public static void appendData(String gid, String dsName, double data, double time) throws Exception {
    appendData(gid, dsName, new double[][] {{data}}, time);
  }


  /**
   * Method to append double[] data to the LatFile.
   * @param gid
   * @param dsName
   * @param data
   * @throws NullPointerException
   * @throws HDF5Exception
   */
  public static void appendData(String group, String dsName, double[] data, double time) throws Exception {
    appendData(group, dsName, Datatype.CLASS_FLOAT, 8, 1, data.length, data, time);
  }


  /**
   * Method to append double[][] data to the LatFile.
   * @param group
   * @param dsName
   * @param data
   * @param time
   * @throws Exception
   */
  public static void appendData(String group, String dsName, double[][] data, double time) throws Exception {
    appendData(group, dsName, Datatype.CLASS_FLOAT, 8, data.length, data[0].length, data, time);
  }


  /**
   * Method to append double[][] data to the LatFile.
   * @param group
   * @param dsName
   * @param si
   * @param time
   * @throws Exception
   */
  public static void appendData(String group, String dsName, LatFileInterface si, double time) throws Exception {
    String[] keys = si.getLatDataKeys();
    LatDataObject latDataObject;
    String groupName, mapKey;
    String newGroup = group;
    Group g2 = (Group) h5File.get(group);

    for(int i=0; i<keys.length; i++) {
      latDataObject = si.getLatData(keys[i]);
      groupName = latDataObject.getGroupName();

      if(groupName!=null) {
        newGroup = group + "/" + groupName;
        Group g = (Group) h5File.get(newGroup);
        if(g==null) createGroup(groupName, g2);
      }
      mapKey = newGroup + "/" + keys[i];
      Integer index= counterMap.get(mapKey);
      if(index==null) counterMap.put(mapKey, initialIndex);
      gridpointSpacing = new double[] {latDataObject.getDx(), latDataObject.getDy()};
      gridDims  = new int[] {latDataObject.getNx(), latDataObject.getNy()};
      dataType = latDataObject.getDataType();
      if(latDataObject.getWriteMode()==LatDataObject.APPEND)
        appendData(newGroup, keys[i], latDataObject.getData(), time);
      else 
        writeData(newGroup, keys[i], latDataObject.getData(), time);
    }
  }


  public static void appendData(String gid, String dsName, File[] data, double time) {
    System.out.println("LatFile - File type under development");
    /*

    int dataspace_id, dataset_id, gid2;
    String[] split = gid.split("::");
    try {
      gid2 = H5.H5Gopen(Integer.parseInt(split[0]), split[1]);

      //Get Width of string
      int maxLen = data[0].getPath().getBytes().length;
      for(int i=1; i<data.length; i++) {
        maxLen = data[i].getPath().getBytes().length > maxLen ? data[i].getPath().getBytes().length : maxLen;
      }

      // Create the data space for the dataset.
      int tid = H5.H5Tcopy(HDF5Constants.H5T_C_S1);
      H5.H5Tset_size(tid, maxLen);
      dataspace_id = H5.H5Screate_simple (1, new long[] {data.length}, null);

      // Create a dataset in the proper group. 
      int count = counterMap.get(split[1] + "/" + dsName);
      dataset_id = H5Dcreate_wrap(gid2, dsName+"_"+count, tid, dataspace_id, HDF5Constants.H5P_DEFAULT);
      counterMap.put(split[1] + "/" + dsName, ++count);

      // Write the first dataset. 
      StringBuffer sb = new StringBuffer();
      for(int i=0; i<data.length; i++) {
        sb.append(String.format("%1$-"+maxLen+"s", data[i].getPath()));
      }

      // for large number of strings, use StringBuffer
      H5.H5Dwrite(dataset_id, tid, HDF5Constants.H5S_ALL, HDF5Constants.H5S_ALL, HDF5Constants.H5P_DEFAULT, sb.toString().getBytes());

      // Close tid
      H5.H5Tclose(tid);

      // Close the data space for the dataset. 
      H5.H5Sclose(dataspace_id);

      // Close the first dataset. 
      H5.H5Dclose(dataset_id);

      // Close groups.
      H5.H5Gclose(gid2);

    } catch (HDF5LibraryException e) {
      e.printStackTrace();
    } catch (NullPointerException e) {
      e.printStackTrace();
    } catch (HDF5Exception e) {
      e.printStackTrace();
    }
     */
  }

  public static void appendData(String gid, String dsName, String[] data, double time) {
    System.out.println("LatFile - String type under development");

    /*
    int dataspace_id, dataset_id, gid2;
    String[] split = gid.split("::");
    try {
      gid2 = H5.H5Gopen(Integer.parseInt(split[0]), split[1]);

      //Get Width of string
      int maxLen = data[0].getBytes().length;
      for(int i=1; i<data.length; i++) {
        maxLen = data[i].getBytes().length > maxLen ? data[i].getBytes().length : maxLen;
      }

      // Create the data space for the dataset.
      int tid = H5.H5Tcopy(HDF5Constants.H5T_C_S1);
      H5.H5Tset_size(tid, maxLen);
      dataspace_id = H5.H5Screate_simple (1, new long[] {data.length}, null);

      // Create a dataset in the proper group. 
      int count = counterMap.get(split[1] + "/" + dsName);
      dataset_id = H5Dcreate_wrap(gid2, dsName+"_"+count, tid, dataspace_id, HDF5Constants.H5P_DEFAULT);
      counterMap.put(split[1] + "/" + dsName, ++count);

      // Write the first dataset. 
      StringBuffer sb = new StringBuffer();
      for(int i=0; i<data.length; i++) {
        sb.append(String.format("%1$-"+maxLen+"s", data[i]));
      }

      // for large number of strings, use StringBuffer
      H5.H5Dwrite(dataset_id, tid, HDF5Constants.H5S_ALL, HDF5Constants.H5S_ALL, HDF5Constants.H5P_DEFAULT, sb.toString().getBytes());

      // Close tid
      H5.H5Tclose(tid);

      // Close the data space for the dataset. 
      H5.H5Sclose(dataspace_id);

      // Close the first dataset. 
      H5.H5Dclose(dataset_id);

      // Close groups.
      H5.H5Gclose(gid2);

    } catch (HDF5LibraryException e) {
      e.printStackTrace();
    } catch (NullPointerException e) {
      e.printStackTrace();
    } catch (HDF5Exception e) {
      e.printStackTrace();
    }
     */
  }


  /**
   * Method to append double[][] data to the LatFile.
   * @param gid
   * @param dsName
   * @param data
   * @throws NullPointerException
   * @throws HDF5Exception
   */
  private static void appendData(String group, String dsName, int dataTypeClass, int prec, int nx, int ny, Object data, double time) throws Exception {
    double dx = 1;
    double dy = 1;
    int nxLocal, nyLocal;
    if(gridDims != null && gridDims[0]*gridDims[1]!=0) {
      nxLocal = gridDims[0];
      nyLocal = gridDims[1];
      gridDims = null;
    } else {
      nxLocal = nx;
      nyLocal = ny;
    }
    
    if(gridpointSpacing != null) {
      dx = gridpointSpacing[0];
      dy = gridpointSpacing[1];
      gridpointSpacing = null;
    }
    
    String mapKey = group + "/" + dsName;
    Integer countIndex = counterMap.get(mapKey);
    if(countIndex==null) {
      countIndex = initialIndex;
      counterMap.put(mapKey, initialIndex);
    }
    
    // Open the HDF5 file.
    h5File.open();
    
    HObject hobject = h5File.get(group + "/" + dsName);

    // Create Dataset if hobject = null, otherwise get and open the dataset for chunking.
    if(hobject == null) {
      
      // Creating an unsigned 1-byte integer datatype
      long[] dims = new long[] {1, 1};
      long[] maxDims = new long[] {HDF5Constants.H5S_UNLIMITED, 1};
      long[] chunks = new long[] {1, 1};

      Group g = (Group) h5File.get(group);
      String[] names = new String[] {"time", "data", "(nx,ny)", "(dx,dy)"};
      Datatype[] types = new Datatype[4];
      types[0] = h5File.createDatatype(Datatype.CLASS_FLOAT, 8, Datatype.NATIVE, Datatype.NATIVE);
      types[1] = h5File.createDatatype(dataTypeClass, prec, Datatype.NATIVE, Datatype.NATIVE);
      types[2] = h5File.createDatatype(Datatype.CLASS_INTEGER, 4, Datatype.NATIVE, Datatype.NATIVE);
      types[3] = h5File.createDatatype(Datatype.CLASS_FLOAT, 4, Datatype.NATIVE, Datatype.NATIVE);
      
      int[] orders = new int[] {1, nx*ny, 2, 2};

      Vector v = new Vector();
      v.add(0, new double[] {time});
      v.add(1, data);
      v.add(2, new int[]   {nxLocal, nyLocal});
      v.add(3, new float[] {(float) dx, (float)dy});
      H5CompoundDS dataset = (H5CompoundDS)h5File.createCompoundDS(dsName, g, dims, maxDims, chunks, 0, names, types, orders, v);
      dataset.writeMetadata(new Attribute("TYPE", new H5Datatype(Datatype.CLASS_INTEGER, 4, Datatype.NATIVE, Datatype.NATIVE), new long[] {1}, new int[] {dataType}));
      
    } else {
      
      H5CompoundDS dataset = (H5CompoundDS) hobject;

      // Extend data (set appropriate space requirements)
      int did = dataset.open();
      H5.H5Dextend(did, new long[] {countIndex+1, 1});
      dataset.close(did);

      // Write data set
      dataset.init();
      long[] startDims = dataset.getStartDims();
      startDims[0] = countIndex;

      long[] selectedDims = dataset.getSelectedDims();
      selectedDims[0] = 1; 
      
      Vector v = new Vector();
      v.add(0, new double[] {time});
      v.add(1, data);
      v.add(2, new int[]   {nxLocal, nyLocal});
      v.add(3, new float[] {(float) dx, (float)dy});
      dataset.write(v);
    }
    
    //TODO add last modified attribute to root node. 
    h5File.close();
    counterMap.put(group + "/" + dsName, ++countIndex);
  }

  /**
   * The main method for writing data to the lat-file.  This method will choice the correct signature to use based on the 
   * data variable.
   * @param groupPath
   * @param datasetName
   * @param data
   * @param time
   */
  public static void writeData(String groupPath, String datasetName, Object data, double time) {
    try {
      if(data instanceof Integer) 
        writeData(groupPath, datasetName, new int[][] {{(Integer) data}}, time);
      else if(data instanceof int[]) 
        writeData(groupPath, datasetName, new int[][] {(int[])data}, time);
      else if(data instanceof int[][]) 
        writeData(groupPath, datasetName, (int[][])data, time);
      else if(data instanceof Float) 
        writeData(groupPath, datasetName, new float[][] {{(Float) data}}, time);
      else if(data instanceof float[]) 
        writeData(groupPath, datasetName, new float[][] {(float[])data}, time);
      else if(data instanceof float[][]) 
        writeData(groupPath, datasetName, (float[][])data, time);
      else if(data instanceof Integer) 
        writeData(groupPath, datasetName, new long[][] {{(Long) data}}, time);
      else if(data instanceof int[]) 
        writeData(groupPath, datasetName, new long[][] {(long[])data}, time);
      else if(data instanceof int[][]) 
        writeData(groupPath, datasetName, (long[][])data, time);
      else if(data instanceof Double) 
        writeData(groupPath, datasetName, new double[][] {{(Double)data}}, time);
      else if(data instanceof double[]) 
        writeData(groupPath, datasetName, new double[][] {(double[])data}, time);
      else if(data instanceof double[][]) 
        writeData(groupPath, datasetName, (double[][])data, time);
      else if(data instanceof byte[]) 
        writeData(groupPath, datasetName, (byte[])data, time);
      else if(data instanceof File) 
        writeData(groupPath, datasetName, new File[] {(File)data}, time);
      else if(data instanceof File[]) 
        writeData(groupPath, datasetName, (File[])data, time);
      else if(data instanceof String) 
        writeData(groupPath, datasetName, new String[] {(String)data}, time);
      else if(data instanceof String[]) 
        writeData(groupPath, datasetName, (String[])data,time);

      else {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
        objectOutputStream.writeObject(data);
        objectOutputStream.close();
        writeData(groupPath, datasetName, byteArrayOutputStream.toByteArray(), time);
      }
    } catch(Exception e) {
      e.printStackTrace();
    }
  }


  /**
   * Method to append double[][] data to the LatFile.
   * @param gid
   * @param dsName
   * @param data
   * @throws NullPointerException
   * @throws HDF5Exception
   */
  public static void writeData(String group, String dsName, byte[] data, double time) throws Exception {
    writeData(group, dsName, Datatype.CLASS_INTEGER, 1, 1, data.length, data, time);
  }


  /**
   * 
   * @param gid
   * @param dsName
   * @param data
   * @param time
   * @throws Exception
   */
  public static void writeData(String gid, String dsName, int data, double time) throws Exception {
    writeData(gid, dsName, new int[][] {{data}}, time);
  }


  /**
   * 
   * @param gid
   * @param dsName
   * @param data
   * @param time
   * @throws Exception
   */
  public static void writeData(String gid, String dsName, int[] data, double time) throws Exception {
    writeData(gid, dsName, new int[][] {data}, time);
  }

  /**
   * Method to append double[][] data to the LatFile.
   * @param gid
   * @param dsName
   * @param data
   * @throws NullPointerException
   * @throws HDF5Exception
   */
  public static void writeData(String group, String dsName, int[][] data, double time) throws Exception {
    writeData(group, dsName, Datatype.CLASS_INTEGER, 4, data.length, data[0].length, data, time);
  }


  /**
   * 
   * @param gid
   * @param dsName
   * @param data
   * @param time
   * @throws Exception
   */
  public static void writeData(String gid, String dsName, float data, double time) throws Exception {
    writeData(gid, dsName, new float[][] {{data}}, time);
  }


  /**
   * 
   * @param gid
   * @param dsName
   * @param data
   * @param time
   * @throws Exception
   */
  public static void writeData(String gid, String dsName, float[] data, double time) throws Exception {
    writeData(gid, dsName, new float[][] {data}, time);
  }


  /**
   * 
   * @param group
   * @param dsName
   * @param data
   * @param time
   * @throws Exception
   */
  public static void writeData(String group, String dsName, float[][] data, double time) throws Exception {
    writeData(group, dsName, Datatype.CLASS_FLOAT, 4, data.length, data[0].length, data, time);
  }


  /**
   * 
   * @param gid
   * @param dsName
   * @param data
   * @param time
   * @throws Exception
   */
  public static void writeData(String gid, String dsName, long data, double time) throws Exception {
    writeData(gid, dsName, new long[][] {{data}}, time);
  }


  /**
   * 
   * @param gid
   * @param dsName
   * @param data
   * @param time
   * @throws Exception
   */
  public static void writeData(String gid, String dsName, long[] data, double time) throws Exception {
    writeData(gid, dsName, new long[][] {data}, time);
  }


  /**
   * Method to append double[][] data to the LatFile.
   * @param group
   * @param dsName
   * @param data
   * @param time
   * @throws Exception
   */
  public static void writeData(String group, String dsName, long[][] data, double time) throws Exception {
    writeData(group, dsName, Datatype.CLASS_INTEGER, 8, data.length, data[0].length, data, time);
  }


  /**
   * 
   * @param gid
   * @param dsName
   * @param data
   * @param time
   * @throws Exception
   */
  public static void writeData(String gid, String dsName, double data, double time) throws Exception {
    writeData(gid, dsName, new double[][] {{data}}, time);
  }


  /**
   * Method to append double[] data to the LatFile.
   * @param gid
   * @param dsName
   * @param data
   * @throws NullPointerException
   * @throws HDF5Exception
   */
  public static void writeData(String group, String dsName, double[] data, double time) throws Exception {
    writeData(group, dsName, Datatype.CLASS_FLOAT, 8, 1, data.length, data, time);
  }


  /**
   * Method to append double[][] data to the LatFile.
   * @param group
   * @param dsName
   * @param data
   * @param time
   * @throws Exception
   */
  public static void writeData(String group, String dsName, double[][] data, double time) throws Exception {
    writeData(group, dsName, Datatype.CLASS_FLOAT, 8, data.length, data[0].length, data, time);
  }


  /**
   * Method to append double[][] data to the LatFile.
   * @param gid
   * @param dsName
   * @param data
   * @throws NullPointerException
   * @throws HDF5Exception
   */
  private static void writeData(String group, String dsName, int dataTypeClass, int prec, int nx, int ny, Object data, double time) throws Exception {
    double dx = 1;
    double dy = 1;
    int nxLocal, nyLocal;
    if(gridDims != null) {
      nxLocal = gridDims[0];
      nyLocal = gridDims[1];
      gridDims = null;
    } else {
      nxLocal = nx;
      nyLocal = ny;
    }
    
    if(gridpointSpacing != null) {
      dx = gridpointSpacing[0];
      dy = gridpointSpacing[1];
      gridpointSpacing = null;
    }
    
    String mapKey = group + "/" + dsName;
    Integer countIndex = counterMap.get(mapKey);
    if(countIndex==null) {
      countIndex = initialIndex;
      counterMap.put(mapKey, initialIndex);
    }
    
    // Open the HDF5 file.
    h5File.open();
    
    // The name of the new attribute
    String name = "attr_"+countIndex;

    // Creating an unsigned 1-byte integer datatype
    Datatype type = new H5Datatype(Datatype.CLASS_FLOAT, 4, Datatype.NATIVE, Datatype.NATIVE);

    // The value of the attribute
    float[] attributeValue = new float[] {(float)time, (float)dataType, (float)nxLocal, (float)nyLocal, (float)dx, (float)dy};

    HObject hobject = h5File.get(group + "/" + dsName);

    // Create Dataset if hobject = null, otherwise get and open the dataset for chunking.
    if(hobject == null) {
      Datatype dtype = h5File.createDatatype(dataTypeClass, prec, Datatype.NATIVE, Datatype.NATIVE);

      long[] dims = new long[] {1, nx*ny};
      long[] maxDims = new long[] {HDF5Constants.H5S_UNLIMITED, nx*ny};
      long[] chunks = new long[] {1, nx*ny};

      Group g = (Group) h5File.get(group);
      Dataset dataset = h5File.createScalarDS(dsName, g, dtype, dims, maxDims, chunks, 0, data);
      dataset.writeMetadata(new Attribute(name, type, new long[] {6}, attributeValue));

    } else {
      H5ScalarDS dataset = (H5ScalarDS) hobject;
      dataset.write(data);
      
      Attribute thisAttribute = (Attribute) dataset.getMetadata().get(0);
      thisAttribute.setValue(attributeValue);
      dataset.writeMetadata(thisAttribute);
    }

    h5File.close();
    counterMap.put(group + "/" + dsName, ++countIndex);
  }


  /**
   * A convience method to recreate a Java object from its byte array.  This method will fail if the object, represented by the byte array,
   * is not in Latiz's class path. To insure it is, users should place user extra jars in Latiz's userLib directory.
   * @param <T>
   * @param byteArray
   * @return
   * @throws IOException
   * @throws ClassNotFoundException
   */
  @SuppressWarnings("unchecked")
public static <T> T recreateObjectFromByteArray(byte[] byteArray) throws IOException, ClassNotFoundException {
    ByteArrayInputStream bais = new ByteArrayInputStream(byteArray);
    ObjectInputStream ois;
    ois = new ObjectInputStream(bais);
    T t = (T)ois.readObject();
    return t;
  }
}
