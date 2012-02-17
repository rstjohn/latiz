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

import com.AandR.library.io.ByteSwapper;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

/**
 * @author Dr. Richard St. John
 * @version $Revision$, $Date$
 */
public class TrfReader {
  
  private DefaultTreeModel model;
  
  private File trfFile;
  
  private LinkedHashMap<String, TreeSet<TrfParameter>> parameterMaps;
  
  private RandomAccessFile f;

  private TreeMap<String, TreeSet<TrfVariable>> variableMap;

  private LinkedHashMap<String, TrfRunHeader> runMap;

    
  public TrfReader() {
    runMap = new LinkedHashMap<String, TrfRunHeader>();
    variableMap = new TreeMap<String, TreeSet<TrfVariable>>(new StringComparator());
  }


  public void loadFile(File trfFile, DefaultTreeModel model) {
    this.model = model;
    this.trfFile = trfFile;
    try {
      f = new RandomAccessFile(trfFile, "r");
      loadRunHeaders();
      traceParameters();
      traceVariables();
      addTreeNodes(trfFile, model);
      runMap.clear();
      variableMap.clear();
      parameterMaps.clear();
      f.close();
    } catch(Exception e) {}
  }


  private void addTreeNodes(File trfFile, DefaultTreeModel treeModel) {
    DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode) treeModel.getRoot();

    // Add this file's content at the bottom of the tree
    DefaultMutableTreeNode thisFileNode = new DefaultMutableTreeNode(new TrfFileGroup(trfFile.getPath()));
    treeModel.insertNodeInto(thisFileNode, rootNode, rootNode.getChildCount());

    DefaultMutableTreeNode thisRunNode, parametersNode;
    TrfRunObject thisRunGroup;
    TrfParameterGroup thisTrfGroup;
    TrfDataObject thisTrfTreeNode;
    TreeSet<TrfParameter> thisParameterSet;
    for(String runName : runMap.keySet()) {
      thisRunGroup = new TrfRunObject(runName);
      thisRunGroup.setFile(trfFile);
      thisRunNode = new DefaultMutableTreeNode(thisRunGroup);
      treeModel.insertNodeInto(thisRunNode, thisFileNode, thisFileNode.getChildCount());

      // Add Parameters to Tree
      thisParameterSet = parameterMaps.get(runName);
      thisTrfGroup = new TrfParameterGroup("Parameters");
      thisTrfGroup.setFile(trfFile);
      parametersNode = new DefaultMutableTreeNode(thisTrfGroup); 
      treeModel.insertNodeInto(parametersNode, thisRunNode, thisRunNode.getChildCount());
      for(TrfParameter p : thisParameterSet) {
        thisTrfTreeNode = new TrfDataObject();
        thisTrfTreeNode.setTrfParameterData(runName, p);
        thisTrfTreeNode.setRowCount(p.getRowCount());
        thisTrfTreeNode.setColCount(p.getColCount());
        thisTrfTreeNode.setFile(trfFile);
        treeModel.insertNodeInto(new DefaultMutableTreeNode(thisTrfTreeNode), parametersNode, parametersNode.getChildCount());
      }

      // Add Output Variables to Tree
      for(TrfVariable thisTrace : variableMap.get(runName)) {
        thisTrfTreeNode = new TrfDataObject();
        thisTrfTreeNode.setTrfRunTraceData(runName, thisTrace);
        thisTrfTreeNode.setFile(trfFile);
        treeModel.insertNodeInto(new DefaultMutableTreeNode(thisTrfTreeNode), thisRunNode, thisRunNode.getChildCount());
      }
    }
  }

  
  public String readMgc() {
    try {
      byte[] recordBuffer = new byte[6];
      f.seek(13);
      f.read(recordBuffer, 0, 6);
      return new String(recordBuffer);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }


  private void loadRunHeaders() throws IOException {
    boolean endOfRuns = false;
    byte[] buf = new byte[4];
    int aNextRun = 19;
    f.seek(aNextRun);
    f.read(buf, 0, 4);

    long aParams, aVars;
    int runNameLength, len;
    String runName;
    while (!endOfRuns) {
      f.seek(aNextRun);
      buf = new byte[32];
      len = f.read(buf, 0, 32);
      if (len == -1 || aNextRun <= 0)
        break;
      aNextRun = (int) ByteSwapper.bytesToLong(buf, 0);
      aParams = ByteSwapper.bytesToLong(buf, 1);
      aVars = ByteSwapper.bytesToLong(buf, 2);
      runNameLength = (int) ByteSwapper.bytesToLong(buf, 3);

      buf = new byte[runNameLength];
      f.read(buf, 0, runNameLength);
      runName = new String(buf);
      runMap.put(runName, new TrfRunHeader(aNextRun, aParams, aVars));
    }
  }


  private void traceParameters() throws IOException {
    parameterMaps = new LinkedHashMap<String, TreeSet<TrfParameter>>();
    for (String runName : runMap.keySet()) {
      parameterMaps.put(runName, traceParam(runName));
    }
  }


  /**
   * Loads the parameters from the first run. If the data changes between runs, this method will need to be updated.
   * 
   * @throws IOException
   */
  private TreeSet<TrfParameter> traceParam(String runName) throws IOException {
    TreeSet<TrfParameter> parameterMap = new TreeSet<TrfParameter>(new Comparator<TrfParameter>() {
      public int compare(TrfParameter o1, TrfParameter o2) {
        return o1.getName().compareToIgnoreCase(o2.getName());
      }});

    String dimString;
    long aparams = runMap.get(runName).getAParams().longValue();
    byte[] buf;
    int size, ndim, len, nobjs;
    int[] dims;
    TrfParameter thisTrfParam;
    while (aparams > 0) {
      thisTrfParam = new TrfParameter();
      thisTrfParam.setAparams(aparams);

      f.seek(aparams);
      aparams = ByteSwapper.swap(f.readLong());
      int nl = (int) ByteSwapper.swap(f.readLong());
      buf = new byte[nl];
      f.read(buf, 0, nl);
      String pname = new String(buf);
      int typeSwitch = ByteSwapper.swap(f.readInt());
      thisTrfParam.setPname(pname);
      thisTrfParam.setPtype(typeSwitch);

      switch (typeSwitch) {

        case TrfConstants.FLOAT:
          thisTrfParam.setInformation("float", 1, 1, ByteSwapper.swap(f.readFloat()));
          break;

        case TrfConstants.SHORT:
          thisTrfParam.setInformation("short", 1, 1, ByteSwapper.swap(f.readShort()));
          break;

        case TrfConstants.INT:
          thisTrfParam.setInformation("int", 1, 1, ByteSwapper.swap(f.readInt()));
          break;

        case TrfConstants.INT_: 
          thisTrfParam.setInformation("int", 1, 1, ByteSwapper.swap(f.readInt()));
          break;

        case TrfConstants.LONG:
          thisTrfParam.setInformation("long", 1, 1, ByteSwapper.swap(f.readLong()));
          break;

        case TrfConstants.DOUBLE:
          thisTrfParam.setInformation("double", 1, 1, ByteSwapper.swap(f.readDouble()));
          break;

        case TrfConstants.COMPLEX:
          thisTrfParam.setInformation("complex", 1, 1, new double[] {ByteSwapper.swap(f.readFloat()), ByteSwapper.swap(f.readFloat())});
          break;

        case TrfConstants.BOOLEAN: 
          thisTrfParam.setInformation("bool", 1, 1, f.readBoolean());
          break;

        case TrfConstants.CHAR:
          thisTrfParam.setInformation("char", 1, 1, f.readChar());
          break;

        case TrfConstants.CHAR_ARRAY: 
          len = ByteSwapper.swap(f.readInt());
          buf = new byte[len];
          f.read(buf, 0, len);
          thisTrfParam.setInformation("char String", 1, 1, new String(buf));
          break;

        case TrfConstants.STRING: 
          len = ByteSwapper.swap(f.readInt());
          buf = new byte[len];
          f.read(buf, 0, len);
          String str = new String(buf).trim();
          thisTrfParam.setInformation("string", 1, 1, str);
          break;

        case TrfConstants.VECTOR_SHORT:
          size = ByteSwapper.swap(f.readInt());
          thisTrfParam.setInformation("Vector<short>", 1, size, "[1 x " + size + "]");
          break;

        case TrfConstants.VECTOR_INT:
          size = ByteSwapper.swap(f.readInt());
          thisTrfParam.setInformation("Vector<int>", 1, size, "[1 x " + size + "]");
          break;

        case TrfConstants.VECTOR_LONG:
          size = ByteSwapper.swap(f.readInt());
          thisTrfParam.setInformation("Vector<long>", 1, size, "[1 x " + size + "]");
          break;

        case TrfConstants.VECTOR_FLOAT:
          size = ByteSwapper.swap(f.readInt());
          thisTrfParam.setInformation("Vector<float>", 1, size, "[1 x " + size + "]");
          break;

        case TrfConstants.VECTOR_DOUBLE:
          size = ByteSwapper.swap(f.readInt());
          thisTrfParam.setInformation("Vector<double>", 1, size, "[1 x " + size + "]");
          break;

        case TrfConstants.VECTOR_COMPLEX:
          size = ByteSwapper.swap(f.readInt());
          thisTrfParam.setInformation("Vector<complex>", 1, size, "[1 x " + size + "]");
          break;

        case TrfConstants.ARRAY_SHORT: 
          dimString = "[";
          nobjs = 1;
          ndim = ByteSwapper.swap(f.readInt());
          dims = new int[ndim];
          for (int i = 0; i < ndim; i++) {
            dims[i] = ByteSwapper.swap(f.readInt()); 
            dimString += dims[i] + " x ";
            nobjs *= dims[i];
          }
          dimString = dimString.substring(0, dimString.length()-2) + "]";
          thisTrfParam.setInformation("Array<short>", dims[0], nobjs/dims[0], dimString);
          break;

        case TrfConstants.ARRAY_INT: 
          dimString = "[";
          nobjs = 1;
          ndim = ByteSwapper.swap(f.readInt());
          dims = new int[ndim];
          for (int i = 0; i < ndim; i++) {
            dims[i] = ByteSwapper.swap(f.readInt()); 
            dimString += dims[i] + " x ";
            nobjs *= dims[i];
          }
          dimString = dimString.substring(0, dimString.length()-2) + "]";
          thisTrfParam.setInformation("Array<int>", dims[0], nobjs/dims[0], dimString);
          break;

        case TrfConstants.ARRAY_LONG: 
          dimString = "[";
          nobjs = 1;
          ndim = ByteSwapper.swap(f.readInt());
          dims = new int[ndim];
          for (int i = 0; i < ndim; i++) {
            dims[i] = ByteSwapper.swap(f.readInt()); 
            dimString += dims[i] + " x ";
            nobjs *= dims[i];
          }
          dimString = dimString.substring(0, dimString.length()-2) + "]";
          thisTrfParam.setInformation("Array<long>", dims[0], nobjs/dims[0], dimString);
          break;

        case TrfConstants.ARRAY_FLOAT: 
          dimString = "[";
          nobjs = 1;
          ndim = ByteSwapper.swap(f.readInt());
          dims = new int[ndim];
          for (int i = 0; i < ndim; i++) {
            dims[i] = ByteSwapper.swap(f.readInt()); 
            dimString += dims[i] + " x ";
            nobjs *= dims[i];
          }
          dimString = dimString.substring(0, dimString.length()-2) + "]";
          thisTrfParam.setInformation("Array<float>", dims[0], nobjs/dims[0], dimString);
          break;

        case TrfConstants.ARRAY_DOUBLE:
          dimString = "[";
          nobjs = 1;
          ndim = ByteSwapper.swap(f.readInt());
          dims = new int[ndim];
          for (int i = 0; i < ndim; i++) {
            dims[i] = ByteSwapper.swap(f.readInt()); 
            dimString += dims[i] + " x ";
            nobjs *= dims[i];
          }
          thisTrfParam.setInformation("Array<double>", dims[0], nobjs/dims[0], dimString);
          break;

        case TrfConstants.ARRAY_COMPLEX:
          dimString = "";
          nobjs = 1;
          ndim = ByteSwapper.swap(f.readInt());
          dims = new int[ndim];
          for (int i = 0; i < ndim; i++) {
            dims[i] = ByteSwapper.swap(f.readInt()); 
            dimString += dims[i] + " x ";
            nobjs *= dims[i];
          }
          dimString = dimString.substring(0, dimString.length()-2) + "]";
          thisTrfParam.setInformation("Array<complex>", dims[0], nobjs/dims[0], dimString);
          break;

        case TrfConstants.ARRAY_GRID_SHORT: 
          /*
           nx = ByteSwapper.swap(raf.readInt());
           ny = ByteSwapper.swap(raf.readInt());
           dx = ByteSwapper.swap(raf.readFloat());
           dy = ByteSwapper.swap(raf.readFloat());
           minx = ByteSwapper.swap(raf.readFloat());
           maxx = ByteSwapper.swap(raf.readFloat());
           miny = ByteSwapper.swap(raf.readFloat());
           maxy = ByteSwapper.swap(raf.readFloat());
           */
          f.skipBytes(32);
          dimString = "[";
          nobjs = 1;
          ndim = ByteSwapper.swap(f.readInt());
          dims = new int[ndim];
          for (int i = 0; i < ndim; i++) {
            dims[i] = ByteSwapper.swap(f.readInt());
            dimString += dims[i] + " x ";
            nobjs *= dims[i];
          }
          dimString = dimString.substring(0, dimString.length()-2) + "]";
          thisTrfParam.setInformation("Grid<short>", dims[0], nobjs/dims[0], dimString);
          break;

        case TrfConstants.ARRAY_GRID_INT: 
          /*
           nx = ByteSwapper.swap(raf.readInt());
           ny = ByteSwapper.swap(raf.readInt());
           dx = ByteSwapper.swap(raf.readFloat());
           dy = ByteSwapper.swap(raf.readFloat());
           minx = ByteSwapper.swap(raf.readFloat());
           maxx = ByteSwapper.swap(raf.readFloat());
           miny = ByteSwapper.swap(raf.readFloat());
           maxy = ByteSwapper.swap(raf.readFloat());
           */
          f.skipBytes(32);
          dimString = "[";
          nobjs = 1;
          ndim = ByteSwapper.swap(f.readInt());
          dims = new int[ndim];
          for (int i = 0; i < ndim; i++) {
            dims[i] = ByteSwapper.swap(f.readInt());
            dimString += dims[i] + " x ";
            nobjs *= dims[i];
          }
          dimString = dimString.substring(0, dimString.length()-2) + "]";
          thisTrfParam.setInformation("Grid<int>", dims[0], nobjs/dims[0], dimString);
          break;

        case TrfConstants.ARRAY_GRID_FLOAT: 
          /*
           int nx = ByteSwapper.swap(raf.readInt());
           int ny = ByteSwapper.swap(raf.readInt());
           float dx = ByteSwapper.swap(raf.readFloat());
           float dy = ByteSwapper.swap(raf.readFloat());
           float minx = ByteSwapper.swap(raf.readFloat());
           float maxx = ByteSwapper.swap(raf.readFloat());
           float miny = ByteSwapper.swap(raf.readFloat());
           float maxy = ByteSwapper.swap(raf.readFloat());
           */
          f.skipBytes(32);
          dimString = "[";
          nobjs = 1;
          ndim = ByteSwapper.swap(f.readInt());
          dims = new int[ndim];
          for (int i = 0; i < ndim; i++) {
            dims[i] = ByteSwapper.swap(f.readInt());
            dimString += dims[i] + " x ";
            nobjs *= dims[i];
          }
          dimString = dimString.substring(0, dimString.length()-2) + "]";
          thisTrfParam.setInformation("Grid<float>", dims[0], nobjs/dims[0], dimString);
          break;

        case TrfConstants.ARRAY_GRID_LONG: 
          /*
           nx = ByteSwapper.swap(raf.readInt());
           ny = ByteSwapper.swap(raf.readInt());
           dx = ByteSwapper.swap(raf.readFloat());
           dy = ByteSwapper.swap(raf.readFloat());
           minx = ByteSwapper.swap(raf.readFloat());
           maxx = ByteSwapper.swap(raf.readFloat());
           miny = ByteSwapper.swap(raf.readFloat());
           maxy = ByteSwapper.swap(raf.readFloat());
           */
          f.skipBytes(32);
          dimString = "[";
          nobjs = 1;
          ndim = ByteSwapper.swap(f.readInt());
          dims = new int[ndim];
          for (int i = 0; i < ndim; i++) {
            dims[i] = ByteSwapper.swap(f.readInt());
            dimString += dims[i] + " x ";
            nobjs *= dims[i];
          }
          dimString = dimString.substring(0, dimString.length()-2) + "]";
          thisTrfParam.setInformation("Grid<long>", dims[0], nobjs/dims[0], dimString);
          break;

        case TrfConstants.ARRAY_GRID_DOUBLE: 
          /*
           nx = ByteSwapper.swap(raf.readInt());
           ny = ByteSwapper.swap(raf.readInt());
           dx = ByteSwapper.swap(raf.readFloat());
           dy = ByteSwapper.swap(raf.readFloat());
           minx = ByteSwapper.swap(raf.readFloat());
           maxx = ByteSwapper.swap(raf.readFloat());
           miny = ByteSwapper.swap(raf.readFloat());
           maxy = ByteSwapper.swap(raf.readFloat());
           */
          f.skipBytes(32);
          dimString = "[";
          nobjs = 1;
          ndim = ByteSwapper.swap(f.readInt());
          dims = new int[ndim];
          for (int i = 0; i < ndim; i++) {
            dims[i] = ByteSwapper.swap(f.readInt());
            dimString += dims[i] + " x ";
            nobjs *= dims[i];
          }
          dimString = dimString.substring(0, dimString.length()-2) + "]";
          thisTrfParam.setInformation("Grid<double>", dims[0], nobjs/dims[0], dimString);
          break;

        case TrfConstants.ARRAY_GRID_COMPLEX: 
          /*
          int nx = ByteSwapper.swap(raf.readInt());
          int ny = ByteSwapper.swap(raf.readInt());
          float dx = ByteSwapper.swap(raf.readFloat());
          float dy = ByteSwapper.swap(raf.readFloat());
          float minx = ByteSwapper.swap(raf.readFloat());
          float maxx = ByteSwapper.swap(raf.readFloat());
          float miny = ByteSwapper.swap(raf.readFloat());
          float maxy = ByteSwapper.swap(raf.readFloat());
           */
          f.skipBytes(32);
          dimString = "[";
          nobjs = 1;
          ndim = ByteSwapper.swap(f.readInt());
          dims = new int[ndim];
          for (int i = 0; i < ndim; i++) {
            dims[i] = ByteSwapper.swap(f.readInt());
            dimString += dims[i] + " x ";
            nobjs *= dims[i];
          }
          dimString = dimString.substring(0, dimString.length()-2) + "]";
          thisTrfParam.setInformation("Grid<complex>", dims[0], nobjs/dims[0], dimString);
          break;

        default: 
          thisTrfParam.setInformation("Unknown", 0, 0, null);
        break;
      }
      parameterMap.add(thisTrfParam);
    }
    return parameterMap;
  }


  /**
   * This method traces out the run. It primarily creates a new instance of TrfRunTrace class, setting its properties.
   * 
   * @throws IOException
   */
  private void traceVariables() throws IOException {

    byte[] buf = null;
    TrfVariable thisTrfVariable;
    TreeSet<TrfVariable> thisRunTraceMap;
    for (String runName : runMap.keySet()) {
      long anextvars = runMap.get(runName).getAVars().longValue();
      thisRunTraceMap = new TreeSet<TrfVariable>(new VariableComparator());
      while (anextvars > 0) {
        thisTrfVariable = new TrfVariable();
        f.seek(anextvars);
        anextvars = ByteSwapper.swap(f.readLong());
        thisTrfVariable.setANextVars((int) anextvars);
        thisTrfVariable.setANextDb(ByteSwapper.swap(f.readLong()));

//      Read Variable Name
        int nl = (int) ByteSwapper.swap(f.readLong());
        buf = new byte[nl];
        f.read(buf, 0, nl);
        String varName = new String(buf);
        thisTrfVariable.setVarName(varName);

//      Read Junk.
        f.readLong();

//      Read id and type name
        int id = ByteSwapper.swap(f.readInt());
        thisTrfVariable.setId(id);
        nl = ByteSwapper.swap(f.readInt());
        buf = new byte[nl];
        f.read(buf, 0, nl);
        thisTrfVariable.setTypeName(new String(buf));

//      Read Variable Description
        nl = ByteSwapper.swap(f.readInt());
        buf = new byte[nl];
        f.read(buf, 0, nl);
        thisTrfVariable.setDescription(new String(buf));

//      Read next Block (flags, type, and nobjs)
        thisTrfVariable.setFlags(ByteSwapper.swap(f.readInt()));
        thisTrfVariable.setType(ByteSwapper.swap(f.readInt()));
        thisTrfVariable.setNobjs(ByteSwapper.swap(f.readInt()));

//      Read initial tag and delta tag.
        thisTrfVariable.setInitialTag(ByteSwapper.swap(f.readFloat()));
        thisTrfVariable.setDeltaTag(ByteSwapper.swap(f.readFloat()));
        thisRunTraceMap.add(thisTrfVariable);
      }
      variableMap.put(runName, thisRunTraceMap);
    }
  }


  public void printVariableView() {
    try {
      f = new RandomAccessFile(trfFile, "r");
      loadRunHeaders();
      traceParameters();
      traceVariables();
    } catch (IOException e) {
      e.printStackTrace();
    }
    
    // Parse Variables
    TreeMap<String,TrfVariable> varNew = new TreeMap<String, TrfVariable>();
    TreeSet<TrfVariable> vars;
    String thisVarName;
    for(String runName : variableMap.keySet()) {
      vars = variableMap.get(runName);
      for(TrfVariable tfv : vars) {
        thisVarName = tfv.getVarName();
        if(varNew.keySet().contains(thisVarName)) {
          varNew.get(thisVarName).getRunNames().add(runName);
          continue;
        } else {
          tfv.getRunNames().add(runName);
          varNew.put(thisVarName, tfv);
        }
      }
    }

    // Print variables and runs
    for(TrfVariable trv : varNew.values()) {
      System.out.println(trv.getVarName());
      for(String runName : trv.getRunNames()) {
        System.out.println("    " + runName);
      }
    }
    
    // Parse Parameters
    TreeMap<String, TrfParameter> parNew = new TreeMap<String, TrfParameter>();
    TreeSet<TrfParameter> pars;
    String thisParName;
    for(String runName : parameterMaps.keySet()) {
      pars = parameterMaps.get(runName);
      for(TrfParameter tfv : pars) {
        thisParName = tfv.getName();
        if(parNew.keySet().contains(thisParName)) {
          parNew.get(thisParName).getRunNames().add(runName);
          continue;
        } else {
          tfv.getRunNames().add(runName);
          parNew.put(thisParName, tfv);
        }
      }
    }

    // Print parameters and runs
    for(TrfParameter param : parNew.values()) {
      System.out.println(param.getName());
      for(String runName : param.getRunNames()) {
        System.out.println("    " + runName);
      }
    }
    
    
    runMap.clear();
    variableMap.clear();
    parameterMaps.clear();
  }


  /**
   * Use this method to get the times for a particular variable.
   * 
   * @param varName
   * @return
   * @throws IOException
   */
  public float[] readVaribaleTimes(TrfDataObject trfTreeNode) throws IOException {
    LinkedHashSet<Float> times = new LinkedHashSet<Float>();
    long anextdb = trfTreeNode.getANextDb();
    int countin;
    int dataSize = trfTreeNode.getType() == TrfConstants.DOUBLE ? 8 : 4;
    RandomAccessFile raf = new RandomAccessFile(trfTreeNode.getFile(), "r");
    while (anextdb > 0) {
      raf.seek(anextdb);
      anextdb = ByteSwapper.swap(raf.readLong());
      raf.skipBytes(16); // skip bss, minTime, and maxTime (long, float, float)

      countin = ByteSwapper.swap(raf.readInt()); // counts
      for (int i = 0; i < countin; i++) {
        times.add(ByteSwapper.swap(raf.readFloat()));
        raf.skipBytes(trfTreeNode.getNobjs() * dataSize); // Skip actual data storage
      }
    }
    raf.close();
    float[] t = new float[times.size()];
    int i = 0;
    for (Float time : times) {
      t[i] = time.floatValue();
      i++;
    }
    return t;
  }
  
  
  /**
   * Zero-based indices of the runs to load. 
   * @param file
   * @param runIndex
   * @param variableName
   * @param times
   * @return
   * @throws IOException
   */
  public ArrayList<double[][]> loadVariable(File file, int[] runIndex, String variableName, float[] times) throws IOException {
    String[] allRuns = new String[runMap.keySet().size()];
    allRuns = runMap.keySet().toArray(allRuns);
    
    String[] runNames = new String[runIndex.length];
    for(int i=0; i<runIndex.length; i++) {
      runNames[i] = allRuns[runIndex[i]];
    }
    return loadVariable(file, runNames, variableName, times);
  }
  
  
  /**
   * 
   * @param file
   * @param runNames
   * @param variableName
   * @param times
   * @return
   * @throws IOException
   */
  public ArrayList<double[][]> loadVariable(File file, String[] runNames, String variableName, float[] times) throws IOException {
    if(model == null) {
      model = new DefaultTreeModel(new DefaultMutableTreeNode("Trf-files"));
      loadFile(file, model);
    }
    ArrayList<double[][]> output = new ArrayList<double[][]>();
    TrfDataObject thisTrfDataObject;
    for(int i=0; i<runNames.length; i++) {
      thisTrfDataObject = findTreeNodeForVariable(file, runNames[i], variableName);
      if(thisTrfDataObject==null) throw new IOException("'/" + runNames[i] + "/" + variableName + "' was not found in the file " + file);
      
      output.add(loadVariable(thisTrfDataObject, times));
    }
    return output;
  }


  /**
   * Returns and {@link ArrayList} for each {@link TrfDataObject} in {@code trfTreeNodes} for all available times.
   * @param trfTreeNodes The data to read.
   * @return An {@link ArrayList} of {@link double[][]}, one for each {@link TrfDataObject}.
   * @throws IOException
   */
  public ArrayList<double[][]> loadVariable(TrfDataObject[] trfTreeNodes) throws IOException {
    if(trfTreeNodes==null || trfTreeNodes.length<1) return null;
    ArrayList<double[][]> output = new ArrayList<double[][]>();
    for(TrfDataObject t : trfTreeNodes) {
      output.add(loadVariable(t, readVaribaleTimes(t)));
    }
    return output;
  }


  /**
   * Returns and {@link ArrayList} for each {@link TrfDataObject} in {@code trfTreeNodes} for the given {@code times}.
   * @param trfTreeNodes The data to read.
   * @param times The times at which to read the given variable
   * @return An {@link ArrayList} of {@link double[][]}, one for each {@link TrfDataObject}.
   * @throws IOException
   */
  public ArrayList<double[][]> loadVariable(TrfDataObject[] trfTreeNodes, float[] times) throws IOException {
    if(trfTreeNodes==null || trfTreeNodes.length<1) return null;
    ArrayList<double[][]> output = new ArrayList<double[][]>();
    for(TrfDataObject t : trfTreeNodes) {
      output.add(loadVariable(t, times));
    }
    return output;
  }


  /**
   * 
   * @param runName
   * @param varName
   * @param times
   * @return
   * @throws IOException
   */
  public double[][] loadVariable(TrfDataObject trfTreeNode, float[] times) throws IOException {
    long anextdb = trfTreeNode.getANextDb();
    float currentTime, time;
    int countin, timeCount = 0;
    double[][] data = new double[times.length][];
    int dataSize = trfTreeNode.getType() == TrfConstants.DOUBLE ? 8 : 4;
    RandomAccessFile raf = new RandomAccessFile(trfTreeNode.getFile(), "r");
    while (anextdb > 0) {
      raf.seek(anextdb);
      anextdb = ByteSwapper.swap(raf.readLong());
      raf.skipBytes(16); // skip bss, minTime, and maxTime (long, float, float)

      countin = ByteSwapper.swap(raf.readInt()); // counts
      for (int i = 0; i < countin; i++) {
        currentTime = ByteSwapper.swap(raf.readFloat());
        if (timeCount >= times.length)
          return data;

        time = times[timeCount];

        // Skip this time's data
        if (currentTime < time) {
          raf.skipBytes(trfTreeNode.getNobjs() * dataSize);
          continue;
        } else if (currentTime > time) {
          break;
        }

        // Store the data at this time step.
        byte[] buf;
        int j, len;
        int dataOffset = 0;
        switch (trfTreeNode.getType()) {
          case TrfConstants.INT: {
            dataOffset = 0;
            len = trfTreeNode.getNobjs();
            data[timeCount] = new double[len];
            buf = new byte[dataSize * len];
            raf.read(buf, 0, buf.length);
            for (int x = 0; x < data[timeCount].length; x++) {
              j = dataSize * dataOffset++;
              data[timeCount][x] = ((buf[j++] & 0xFF) << 0) + ((buf[j++] & 0xFF) << 8) + ((buf[j++] & 0xFF) << 16) + ((buf[j] & 0xFF) << 24);
            }
            timeCount++;
            break;
          }

          case TrfConstants.FLOAT: {
            dataOffset = 0;
            len = trfTreeNode.getNobjs();
            data[timeCount] = new double[len];
            buf = new byte[dataSize * len];
            raf.read(buf, 0, buf.length);
            for (int x = 0; x < data[timeCount].length; x++) {
              j = dataSize * dataOffset++;
              data[timeCount][x] = Float.intBitsToFloat(((buf[j++] & 0xFF) << 0) + ((buf[j++] & 0xFF) << 8) + ((buf[j++] & 0xFF) << 16) + ((buf[j] & 0xFF) << 24));
            }
            timeCount++;
            break;
          }

          case TrfConstants.LONG: {
            dataOffset = 0;
            len = trfTreeNode.getNobjs();
            data[timeCount] = new double[len];
            buf = new byte[dataSize * len];
            raf.read(buf, 0, buf.length);
            for (int x = 0; x < data[timeCount].length; x++) {
              j = dataSize * dataOffset++;
              data[timeCount][x] = ((buf[j++] & 0xffL) << 0) + ((buf[j++] & 0xffL) << 8) + ((buf[j++] & 0xffL) << 16) + ((buf[j++] & 0xffL) << 24) + ((buf[j++] & 0xffL) << 32)
              + ((buf[j++] & 0xffL) << 40) + ((buf[j++] & 0xffL) << 48) + ((buf[j] & 0xffL) << 56);
            }
            timeCount++;
            break;
          }

          case TrfConstants.DOUBLE: {
            dataOffset = 0;
            len = trfTreeNode.getNobjs();
            data[timeCount] = new double[len];
            buf = new byte[dataSize * len];
            raf.read(buf, 0, buf.length);
            for (int x = 0; x < data[timeCount].length; x++) {
              j = dataSize * dataOffset++;
              data[timeCount][x] = Double.longBitsToDouble(((buf[j++] & 0xffL) << 0) + ((buf[j++] & 0xffL) << 8) + ((buf[j++] & 0xffL) << 16) + ((buf[j++] & 0xffL) << 24) + ((buf[j++] & 0xffL) << 32)
                      + ((buf[j++] & 0xffL) << 40) + ((buf[j++] & 0xffL) << 48) + ((buf[j] & 0xffL) << 56));
            }
            timeCount++;
            break;
          }

          case TrfConstants.COMPLEX: {
            len = 2 * trfTreeNode.getNobjs();
            data[timeCount] = new double[len];
            buf = new byte[dataSize * len];
            raf.read(buf, 0, buf.length);
            for (int x = 0; x < data[timeCount].length; x++) {
              j = dataSize * dataOffset++;
              data[timeCount][x] = Float.intBitsToFloat(((buf[j++] & 0xFF) << 0) + ((buf[j++] & 0xFF) << 8) + ((buf[j++] & 0xFF) << 16) + ((buf[j] & 0xFF) << 24));
            }
            timeCount++;
            break;
          }
        }
      }
    }
    raf.close();
    return data;
  }

  /**
   * 
   * @param runName
   * @param varName
   * @param rowCount
   * @param colCount
   * @param time
   * @return
   * @throws IOException
   */
  public double[][] loadVariable(TrfDataObject trfTreeNode, int rowCount, int colCount, float time) throws IOException {
    long anextdb = trfTreeNode.getANextDb();
    float currentTime;
    int countin;
    RandomAccessFile raf = new RandomAccessFile(trfTreeNode.getFile(), "r");
    while (anextdb > 0) {
      raf.seek(anextdb);
      anextdb = ByteSwapper.swap(raf.readLong());
      raf.skipBytes(16); // skip bss, minTime, and maxTime (long, float, float)

      countin = ByteSwapper.swap(raf.readInt()); // counts
      for (int i = 0; i < countin; i++) {
        currentTime = ByteSwapper.swap(raf.readFloat());

        // Skip this time's data
        if (currentTime < time) {
          raf.skipBytes(trfTreeNode.getNobjs() * 4);
          continue;
        } else if (currentTime > time) {
          break;
        }

        // Store the data at this time step.
        byte[] buf;
        int j, len;
        int dataOffset = 0;
        double[][] data = null;
        switch (trfTreeNode.getType()) {
          case TrfConstants.INT: {
            len = trfTreeNode.getNobjs();
            data = new double[rowCount][colCount];
            buf = new byte[4 * len];
            raf.read(buf, 0, buf.length);
            for (int x = 0; x < data[0].length; x++) {
              for (int y = 0; y < data.length; y++) {
                j = 4 * dataOffset++;
                data[y][x] = ((buf[j++] & 0xFF) << 0) + ((buf[j++] & 0xFF) << 8) + ((buf[j++] & 0xFF) << 16) + ((buf[j] & 0xFF) << 24);
              }
            }
            return data;
          }

          case TrfConstants.FLOAT: {
            len = trfTreeNode.getNobjs();
            data = new double[rowCount][colCount];
            buf = new byte[4 * len];
            raf.read(buf, 0, 4 * len);
            for (int x = 0; x < data[0].length; x++) {
              for (int y = 0; y < data.length; y++) {
                j = 4 * dataOffset++;
                data[y][x] = Float.intBitsToFloat(((buf[j++] & 0xFF) << 0) + ((buf[j++] & 0xFF) << 8) + ((buf[j++] & 0xFF) << 16) + ((buf[j] & 0xFF) << 24));
              }
            }
            return data;
          }

          case TrfConstants.LONG: {
            len = trfTreeNode.getNobjs();
            data = new double[rowCount][colCount];
            buf = new byte[8 * len];
            raf.read(buf, 0, 8 * len);
            for (int x = 0; x < data[0].length; x++) {
              for (int y = 0; y < data.length; y++) {
                j = 8 * dataOffset++;
                data[y][x] = ((buf[j++] & 0xFF) << 0) + ((buf[j++] & 0xFF) << 8) + ((buf[j++] & 0xFF) << 16) + ((buf[j] & 0xFF) << 24) + ((buf[j++] & 0xFF) << 32) + ((buf[j++] & 0xFF) << 40)
                + ((buf[j++] & 0xFF) << 48) + ((buf[j] & 0xFF) << 56);
              }
            }
            return data;
          }

          case TrfConstants.DOUBLE: {
            len = trfTreeNode.getNobjs();
            data = new double[rowCount][colCount];
            buf = new byte[8 * len];
            raf.read(buf, 0, 8 * len);
            for (int x = 0; x < data[0].length; x++) {
              for (int y = 0; y < data.length; y++) {
                j = 8 * dataOffset++;
                data[y][x] = Double.longBitsToDouble(((buf[j++] & 0xFF) << 0) + ((buf[j++] & 0xFF) << 8) + ((buf[j++] & 0xFF) << 16) + ((buf[j] & 0xFF) << 24) + ((buf[j++] & 0xFF) << 32)
                        + ((buf[j++] & 0xFF) << 40) + ((buf[j++] & 0xFF) << 48) + ((buf[j] & 0xFF) << 56));
              }
            }
            return data;
          }

          case TrfConstants.COMPLEX: {
            len = 2 * trfTreeNode.getNobjs();
            data = new double[rowCount][2*colCount];
            buf = new byte[4 * len];
            raf.read(buf, 0, 4 * len);
            for (int x = 0; x < data.length; x++) {
              for (int y = 0; y < data[0].length; y++) {
                j = 4 * dataOffset++;
                data[x][y] = Float.intBitsToFloat(((buf[j++] & 0xFF) << 0) + ((buf[j++] & 0xFF) << 8) + ((buf[j++] & 0xFF) << 16) + ((buf[j] & 0xFF) << 24));
              }
            }
            return data;
          }
        }
      }
    }
    raf.close();
    return null;
  }

  /**
   * 
   * @param runName
   * @param varName
   * @param time
   * @return
   * @throws IOException
   */
  public double[][] loadVariable(TrfDataObject trfTreeNode, float time) throws IOException {
    return loadVariable(trfTreeNode, 1, trfTreeNode.getNobjs(), time);
  }


  public Object loadParameter(TrfDataObject parameterNode) throws IOException {
    String paramName = parameterNode.getVarName();

    long aparams = parameterNode.getAparams();
    byte[] buf;
    int ndim, len, rows, cols, j, dataOffset;
    double[][] data;
    RandomAccessFile raf = new RandomAccessFile(parameterNode.getFile(), "r");
    while (aparams > 0) {
      raf.seek(aparams);

      aparams = ByteSwapper.swap(raf.readLong());
      int nl = (int) ByteSwapper.swap(raf.readLong());
      buf = new byte[nl];
      raf.read(buf, 0, nl);
      String pname = new String(buf);
      if(!pname.equalsIgnoreCase(paramName)) continue;

      int typeSwitch = ByteSwapper.swap(raf.readInt());
      switch (typeSwitch) {

        case TrfConstants.FLOAT:
          return new double[][] {{ByteSwapper.swap(raf.readFloat())}};

        case TrfConstants.SHORT:
          return new double[][] {{ByteSwapper.swap(raf.readShort())}};

        case TrfConstants.INT:
          return new double[][] {{ByteSwapper.swap(raf.readInt())}};

        case TrfConstants.INT_: 
          return new double[][] {{ByteSwapper.swap(raf.readInt())}};

        case TrfConstants.LONG:
          return new double[][] {{ByteSwapper.swap(raf.readLong())}};

        case TrfConstants.DOUBLE:
          return new double[][] {{ByteSwapper.swap(raf.readDouble())}};

        case TrfConstants.COMPLEX:
          return new double[][] {{ByteSwapper.swap(raf.readFloat()), ByteSwapper.swap(raf.readFloat())}};

        case TrfConstants.BOOLEAN: 
          return new Boolean(raf.readBoolean());

        case TrfConstants.CHAR:
          return String.valueOf(raf.readChar());

        case TrfConstants.CHAR_ARRAY: 
          len = ByteSwapper.swap(raf.readInt());
          buf = new byte[len];
          raf.read(buf, 0, len);
          return new String(buf).trim();

        case TrfConstants.STRING: 
          len = ByteSwapper.swap(raf.readInt());
          buf = new byte[len];
          raf.read(buf, 0, len);
          return new String(buf).trim();

        case TrfConstants.VECTOR_SHORT:
          raf.skipBytes(4); // size = ByteSwapper.swap(raf.readInt())
          rows = parameterNode.getRowCount();
          cols = parameterNode.getColCount();
          data = new double[rows][cols];
          for (int c=0; c<cols; c++) {
            for(int r=0; r<rows; r++) {
              data[r][c] = (ByteSwapper.swap(raf.readShort()));
            }
          }
          return data;

        case TrfConstants.VECTOR_INT:
          rows = parameterNode.getRowCount();
          cols = parameterNode.getColCount();
          j = dataOffset = 0;
          data = new double[rows][cols];
          buf = new byte[4*parameterNode.getNobjs()];
          raf.read(buf, 0, buf.length);
          for (int c=0; c<cols; c++) {
            for(int r=0; r<rows; r++) {
              j = 4*dataOffset++;
              data[r][c] = ((buf[j++] & 0xFF) << 0) + ((buf[j++] & 0xFF) << 8) + ((buf[j++] & 0xFF) << 16) + ((buf[j] & 0xFF) << 24);
            }
          }
          return data;

        case TrfConstants.VECTOR_FLOAT:
          raf.skipBytes(4); // size = ByteSwapper.swap(raf.readInt())
          rows = parameterNode.getRowCount();
          cols = parameterNode.getColCount();
          j = dataOffset = 0;
          data = new double[rows][cols];
          buf = new byte[4*parameterNode.getNobjs()];
          raf.read(buf, 0, buf.length);
          for (int c=0; c<cols; c++) {
            for(int r=0; r<rows; r++) {
              j = 4*dataOffset++;
              data[r][c] = Float.intBitsToFloat(((buf[j++] & 0xFF) << 0) + ((buf[j++] & 0xFF) << 8) + ((buf[j++] & 0xFF) << 16) + ((buf[j] & 0xFF) << 24));
            }
          }
          return data;

        case TrfConstants.VECTOR_LONG:
          raf.skipBytes(4); // size = ByteSwapper.swap(raf.readInt())
          rows = parameterNode.getRowCount();
          cols = parameterNode.getColCount();
          j = dataOffset = 0;
          data = new double[rows][cols];
          buf = new byte[8*parameterNode.getNobjs()];
          raf.read(buf, 0, buf.length);
          for (int c=0; c<cols; c++) {
            for(int r=0; r<rows; r++) {
              j = 8*dataOffset++;
              data[r][c] = ((buf[j++] & 0xffL) << 0) + ((buf[j++] & 0xffL) << 8) + ((buf[j++] & 0xffL) << 16) + ((buf[j++] & 0xffL) << 24) + ((buf[j++] & 0xffL) << 32)
              + ((buf[j++] & 0xffL) << 40) + ((buf[j++] & 0xffL) << 48) + ((buf[j] & 0xffL) << 56);
            }
          }
          return data;


        case TrfConstants.VECTOR_DOUBLE:
          raf.skipBytes(4); // size = ByteSwapper.swap(raf.readInt())
          rows = parameterNode.getRowCount();
          cols = parameterNode.getColCount();
          j = dataOffset = 0;
          data = new double[rows][cols];
          buf = new byte[8*parameterNode.getNobjs()];
          raf.read(buf, 0, buf.length);
          for (int c=0; c<cols; c++) {
            for(int r=0; r<rows; r++) {
              j = 8*dataOffset++;
              data[r][c] = Double.longBitsToDouble(((buf[j++] & 0xffL) << 0) + ((buf[j++] & 0xffL) << 8) + ((buf[j++] & 0xffL) << 16) + ((buf[j++] & 0xffL) << 24) + ((buf[j++] & 0xffL) << 32)
                      + ((buf[j++] & 0xffL) << 40) + ((buf[j++] & 0xffL) << 48) + ((buf[j] & 0xffL) << 56));
            }
          }
          return data;

        case TrfConstants.VECTOR_COMPLEX:
          raf.skipBytes(4); // size = ByteSwapper.swap(raf.readInt())
          rows = parameterNode.getRowCount();
          cols = parameterNode.getColCount();
          j = dataOffset = 0;
          data = new double[rows][2*cols];
          buf = new byte[8*parameterNode.getNobjs()];
          raf.read(buf, 0, buf.length);
          for(int r=0; r<rows; r++) {
            for (int c=0; c<cols; c++) {
              j = 8*dataOffset++;
              data[r][2*c]   = Float.intBitsToFloat(((buf[j++] & 0xFF) << 0) + ((buf[j++] & 0xFF) << 8) + ((buf[j++] & 0xFF) << 16) + ((buf[j++] & 0xFF) << 24));
              data[r][2*c+1] = Float.intBitsToFloat(((buf[j++] & 0xFF) << 0) + ((buf[j++] & 0xFF) << 8) + ((buf[j++] & 0xFF) << 16) + ((buf[j] & 0xFF) << 24));
            }
          }
          return data;

        case TrfConstants.ARRAY_SHORT: 
          ndim = ByteSwapper.swap(raf.readInt());
          rows = parameterNode.getRowCount();
          cols = parameterNode.getColCount();
          raf.skipBytes(ndim*4);
          data = new double[rows][cols];
          for (int c=0; c<cols; c++) {
            for(int r=0; r<rows; r++) {
              data[r][c] = ByteSwapper.swap(raf.readShort());
            }
          }
          return data;

        case TrfConstants.ARRAY_INT: 
          ndim = ByteSwapper.swap(raf.readInt());
          rows = parameterNode.getRowCount();
          cols = parameterNode.getColCount();
          raf.skipBytes(ndim*4);
          j = dataOffset = 0;
          data = new double[rows][cols];
          buf = new byte[4*parameterNode.getNobjs()];
          raf.read(buf, 0, buf.length);
          for (int c=0; c<cols; c++) {
            for(int r=0; r<rows; r++) {
              j = 4*dataOffset++;
              data[r][c] = ((buf[j++] & 0xFF) << 0) + ((buf[j++] & 0xFF) << 8) + ((buf[j++] & 0xFF) << 16) + ((buf[j] & 0xFF) << 24);
            }
          }
          return data;

        case TrfConstants.ARRAY_FLOAT: 
          ndim = ByteSwapper.swap(raf.readInt());
          rows = parameterNode.getRowCount();
          cols = parameterNode.getColCount();
          raf.skipBytes(ndim*4);
          j = dataOffset = 0;
          data = new double[rows][cols];
          buf = new byte[4*parameterNode.getNobjs()];
          raf.read(buf, 0, buf.length);
          for (int c=0; c<cols; c++) {
            for(int r=0; r<rows; r++) {
              j = 4*dataOffset++;
              data[r][c] = Float.intBitsToFloat(((buf[j++] & 0xFF) << 0) + ((buf[j++] & 0xFF) << 8) + ((buf[j++] & 0xFF) << 16) + ((buf[j] & 0xFF) << 24));
            }
          }
          return data;

        case TrfConstants.ARRAY_LONG: 
          ndim = ByteSwapper.swap(raf.readInt());
          rows = parameterNode.getRowCount();
          cols = parameterNode.getColCount();
          raf.skipBytes(ndim*4);
          j = dataOffset = 0;
          data = new double[rows][cols];
          buf = new byte[8*parameterNode.getNobjs()];
          raf.read(buf, 0, buf.length);
          for (int c=0; c<cols; c++) {
            for(int r=0; r<rows; r++) {
              j = 8*dataOffset++;
              data[r][c] = ((buf[j++] & 0xffL) << 0) + ((buf[j++] & 0xffL) << 8) + ((buf[j++] & 0xffL) << 16) + ((buf[j++] & 0xffL) << 24) 
              + ((buf[j++] & 0xffL) << 32) + ((buf[j++] & 0xffL) << 40) + ((buf[j++] & 0xffL) << 48) + ((buf[j] & 0xffL) << 56);
            }
          }
          return data;

        case TrfConstants.ARRAY_DOUBLE:
          ndim = ByteSwapper.swap(raf.readInt());
          rows = parameterNode.getRowCount();
          cols = parameterNode.getColCount();
          raf.skipBytes(ndim*4);
          j = dataOffset = 0;
          data = new double[rows][cols];
          buf = new byte[8*parameterNode.getNobjs()];
          raf.read(buf, 0, buf.length);
          for (int c=0; c<cols; c++) {
            for(int r=0; r<rows; r++) {
              j = 8*dataOffset++;
              data[r][c] = Double.longBitsToDouble(((buf[j++] & 0xffL) << 0) + ((buf[j++] & 0xffL) << 8) + ((buf[j++] & 0xffL) << 16) + ((buf[j++] & 0xffL) << 24) + 
                      ((buf[j++] & 0xffL) << 32) + ((buf[j++] & 0xffL) << 40) + ((buf[j++] & 0xffL) << 48) + ((buf[j] & 0xffL) << 56));
            }
          }
          return data;

        case TrfConstants.ARRAY_COMPLEX:
          System.out.println("++++++++++++++++++++++++++++++++++++++++++");
          System.out.println("This has not been optimized for speed");
          ndim = ByteSwapper.swap(raf.readInt());
          rows = parameterNode.getRowCount();
          cols = parameterNode.getColCount();
          raf.skipBytes(ndim*4);
          data = new double[rows][2*cols];
          for (int c=0; c<cols; c++) {
            for(int r=0; r<rows; r++) {
              data[r][2*c] = ByteSwapper.swap(raf.readFloat());
              data[r][2*c+1] = ByteSwapper.swap(raf.readFloat());
            }
          }
          return data;

        case TrfConstants.ARRAY_GRID_INT: 
          /*
           nx = ByteSwapper.swap(raf.readInt());
           ny = ByteSwapper.swap(raf.readInt());
           dx = ByteSwapper.swap(raf.readFloat());
           dy = ByteSwapper.swap(raf.readFloat());
           minx = ByteSwapper.swap(raf.readFloat());
           maxx = ByteSwapper.swap(raf.readFloat());
           miny = ByteSwapper.swap(raf.readFloat());
           maxy = ByteSwapper.swap(raf.readFloat());
           */
          raf.skipBytes(32);
          ndim = ByteSwapper.swap(raf.readInt());
          rows = parameterNode.getRowCount();
          cols = parameterNode.getColCount();
          raf.skipBytes(ndim*4);
          j = dataOffset = 0;
          data = new double[rows][cols];
          buf = new byte[4*parameterNode.getNobjs()];
          raf.read(buf, 0, buf.length);
          for (int c=0; c<cols; c++) {
            for(int r=0; r<rows; r++) {
              j = 4*dataOffset++;
              data[r][c] = ((buf[j++] & 0xFF) << 0) + ((buf[j++] & 0xFF) << 8) + ((buf[j++] & 0xFF) << 16) + ((buf[j] & 0xFF) << 24);
            }
          }
          return data;

        case TrfConstants.ARRAY_GRID_LONG: 
          /*
           nx = ByteSwapper.swap(raf.readInt());
           ny = ByteSwapper.swap(raf.readInt());
           dx = ByteSwapper.swap(raf.readFloat());
           dy = ByteSwapper.swap(raf.readFloat());
           minx = ByteSwapper.swap(raf.readFloat());
           maxx = ByteSwapper.swap(raf.readFloat());
           miny = ByteSwapper.swap(raf.readFloat());
           maxy = ByteSwapper.swap(raf.readFloat());
           */
          raf.skipBytes(32);
          ndim = ByteSwapper.swap(raf.readInt());
          rows = parameterNode.getRowCount();
          cols = parameterNode.getColCount();
          raf.skipBytes(ndim*4);
          j = dataOffset = 0;
          data = new double[rows][cols];
          buf = new byte[8*parameterNode.getNobjs()];
          raf.read(buf, 0, buf.length);
          for (int c=0; c<cols; c++) {
            for(int r=0; r<rows; r++) {
              j = 8*dataOffset++;
              data[r][c] = ((buf[j++] & 0xffL) << 0) + ((buf[j++] & 0xffL) << 8) + ((buf[j++] & 0xffL) << 16) + ((buf[j++] & 0xffL) << 24) + 
              ((buf[j++] & 0xffL) << 32) + ((buf[j++] & 0xffL) << 40) + ((buf[j++] & 0xffL) << 48) + ((buf[j] & 0xffL) << 56);
            }
          }
          return data;

        case TrfConstants.ARRAY_GRID_FLOAT: 
          /*
           int nx = ByteSwapper.swap(raf.readInt());
           int ny = ByteSwapper.swap(raf.readInt());
           float dx = ByteSwapper.swap(raf.readFloat());
           float dy = ByteSwapper.swap(raf.readFloat());
           float minx = ByteSwapper.swap(raf.readFloat());
           float maxx = ByteSwapper.swap(raf.readFloat());
           float miny = ByteSwapper.swap(raf.readFloat());
           float maxy = ByteSwapper.swap(raf.readFloat());
           */
          raf.skipBytes(32);
          ndim = ByteSwapper.swap(raf.readInt());
          rows = parameterNode.getRowCount();
          cols = parameterNode.getColCount();
          raf.skipBytes(ndim*4);
          j = dataOffset = 0;
          data = new double[rows][cols];
          buf = new byte[4*parameterNode.getNobjs()];
          raf.read(buf, 0, buf.length);
          for (int c=0; c<cols; c++) {
            for(int r=0; r<rows; r++) {
              j = 4*dataOffset++;
              data[r][c] = Float.intBitsToFloat(((buf[j++] & 0xFF) << 0) + ((buf[j++] & 0xFF) << 8) + ((buf[j++] & 0xFF) << 16) + ((buf[j] & 0xFF) << 24));
            }
          }
          return data;

        case TrfConstants.ARRAY_GRID_DOUBLE: 
          /*
           nx = ByteSwapper.swap(raf.readInt());
           ny = ByteSwapper.swap(raf.readInt());
           dx = ByteSwapper.swap(raf.readFloat());
           dy = ByteSwapper.swap(raf.readFloat());
           minx = ByteSwapper.swap(raf.readFloat());
           maxx = ByteSwapper.swap(raf.readFloat());
           miny = ByteSwapper.swap(raf.readFloat());
           maxy = ByteSwapper.swap(raf.readFloat());
           */
          raf.skipBytes(32);
          ndim = ByteSwapper.swap(raf.readInt());
          rows = parameterNode.getRowCount();
          cols = parameterNode.getColCount();
          raf.skipBytes(ndim*4);
          j = dataOffset = 0;
          data = new double[rows][cols];
          buf = new byte[8*parameterNode.getNobjs()];
          raf.read(buf, 0, buf.length);
          for (int c=0; c<cols; c++) {
            for(int r=0; r<rows; r++) {
              j = 8*dataOffset++;
              data[r][c] = Double.longBitsToDouble(((buf[j++] & 0xffL) << 0) + ((buf[j++] & 0xffL) << 8) + ((buf[j++] & 0xffL) << 16) + ((buf[j++] & 0xffL) << 24) + 
                      ((buf[j++] & 0xffL) << 32) + ((buf[j++] & 0xffL) << 40) + ((buf[j++] & 0xffL) << 48) + ((buf[j] & 0xffL) << 56));
            }
          }
          return data;

        case TrfConstants.ARRAY_GRID_COMPLEX: 
          /*
          int nx = ByteSwapper.swap(raf.readInt());
          int ny = ByteSwapper.swap(raf.readInt());
          float dx = ByteSwapper.swap(raf.readFloat());
          float dy = ByteSwapper.swap(raf.readFloat());
          float minx = ByteSwapper.swap(raf.readFloat());
          float maxx = ByteSwapper.swap(raf.readFloat());
          float miny = ByteSwapper.swap(raf.readFloat());
          float maxy = ByteSwapper.swap(raf.readFloat());
           */
          raf.skipBytes(32);
          ndim = ByteSwapper.swap(raf.readInt());
          raf.skipBytes(ndim*4);
          j = dataOffset = 0;
          rows = parameterNode.getRowCount();
          cols = parameterNode.getColCount();
          data = new double[rows][2*cols];
          buf = new byte[8*parameterNode.getNobjs()];
          raf.read(buf, 0, buf.length);
          for(int r=0; r<rows; r++) {
            for (int c=0; c<cols; c++) {
              j = 8*dataOffset++;
              data[r][2*c]   = Float.intBitsToFloat(((buf[j++] & 0xFF) << 0) + ((buf[j++] & 0xFF) << 8) + ((buf[j++] & 0xFF) << 16) + ((buf[j++] & 0xFF) << 24));
              data[r][2*c+1] = Float.intBitsToFloat(((buf[j++] & 0xFF) << 0) + ((buf[j++] & 0xFF) << 8) + ((buf[j++] & 0xFF) << 16) + ((buf[j] & 0xFF) << 24));
            }
          }
          return data;
      }
    }
    raf.close();
    return null;
  }
  

  /**
   * Returns the tree node that contains the given data object.
   */
  public TrfDataObject findTreeNodeForVariable(File file, String runName, String variable) {
    if (runName==null || variable==null) {
      return null;
    }

    DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode) model.getRoot();

    DefaultMutableTreeNode theNode = null;
    Object thisObject = null;
    TrfDataObject thisTrfDataObject;
    Enumeration local_enum = rootNode.breadthFirstEnumeration();
    while(local_enum.hasMoreElements()) {
      theNode = (DefaultMutableTreeNode)local_enum.nextElement();
      thisObject = theNode.getUserObject();
      if(thisObject == null || !(thisObject instanceof TrfDataObject)) continue;
      
      thisTrfDataObject = (TrfDataObject) thisObject;
      String thisFileName = thisTrfDataObject.getFile().getPath();
      if(!thisFileName.equals(file.getPath())) continue;
      if(thisTrfDataObject.getFullName().equals("/"+runName+"/"+variable)) return thisTrfDataObject;
    }
    return null;
  }


  /**
   * 
   * @author  <a href="mailto://rstjohn@mza.com">Dr. Richard St. John</a>
   * @company <a href="http://www.mza.com">MZA Associates Corporation</a>
   */
  private class VariableComparator implements Comparator<TrfVariable> {
    public int compare(TrfVariable o1, TrfVariable o2) {
      return o1.getVarName().compareToIgnoreCase(o2.getVarName());
    }
  }


  /**
   * 
   * @author  <a href="mailto://rstjohn@mza.com">Dr. Richard St. John</a>
   * @company <a href="http://www.mza.com">MZA Associates Corporation</a>
   */
  private class StringComparator implements Comparator<String> {
    public int compare(String o1, String o2) {
      return o1.compareToIgnoreCase(o2);
    }
  }
}
