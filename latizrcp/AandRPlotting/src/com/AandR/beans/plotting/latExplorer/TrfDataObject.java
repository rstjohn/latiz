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

/**
 * 
 * @author Dr. Richard St. John
 * @version $Revision$, $Date$
 */
public class TrfDataObject extends TrfTreeObject {

  public static final int PARAMETER_NODE = 0;
  public static final int VARIABLE_NODE = 1;
  
  private int rowCount, colCount, type, nodeType, nobjs;
  
  private long aNextDb, aparams;
  
  private boolean addToSavedMap;
  
  private float[] selectedTimes;
  
  private Object value;
  
  private String alias, runName, dataType, varName;
  
  
  public TrfDataObject() {
    super("");
  }

  
  public void setTrfParameterData(String runName, TrfParameter trfParam) {
    this.nodeType = PARAMETER_NODE;
    this.runName = runName;
    this.varName = trfParam.getName();
    this.dataType = trfParam.getType();
    this.type = trfParam.getPtype();
    this.alias = varName;
    this.nobjs = trfParam.getNobjs();
    this.colCount = trfParam.getNobjs();
    this.value = trfParam.getValue();
    double sq = Math.sqrt(colCount);
    if(colCount%sq==0) {
      colCount = rowCount = (int)sq;
    } else {
      rowCount = 1;
    }
    aparams = trfParam.getAparams();
    this.selectedTimes = new float[] {Float.NaN};
  }
  
  
  public void setTrfRunTraceData(String runName, TrfVariable runTrace) {
    this.nodeType = VARIABLE_NODE;
    this.runName = runName;
    this.dataType = runTrace.getDataType();
    this.varName = runTrace.getVarName();
    this.alias="";
    this.nobjs = runTrace.getNobjs();
    this.type = runTrace.getType();
    this.value = "";
    this.colCount = runTrace.getNobjs();
    double sq = Math.sqrt(colCount);
    if(colCount%sq==0) {
      colCount = rowCount = (int)sq;
    } else {
      rowCount = 1;
    }
    aNextDb = runTrace.getANextDb();
    selectedTimes = null;
  }
  
  
  public String getVarName() {
    return varName;
  }


  public String getDataType() {
    return dataType;
  }

  
  public String getFullName() {
    if(nodeType == VARIABLE_NODE)
      return "/" + runName + "/" + varName; 
    else
      return "/" + runName + "/Parameters/" + varName; 
  }
  
  
  public int getType() {
    return type;
  }

  
  public String toString() {
    return nodeType==PARAMETER_NODE ? varName + " = " + value.toString() : varName;
  }


  public int getRowCount() {
    return rowCount;
  }


  public void setRowCount(int rowCount) {
    this.rowCount = rowCount;
  }


  public int getColCount() {
    return colCount;
  }


  public void setColCount(int colCount) {
    this.colCount = colCount;
  }


  public float[] getSelectedTimes() {
    return selectedTimes;
  }


  public void setSelectedTimes(float[] selectedTimes) {
    this.selectedTimes = selectedTimes;
  }
  
  
  public int getNobjs() {
    return nobjs;
  }


  public String getAlias() {
    return alias;
  }


  public void setAlias(String alias) {
    this.alias = alias;
  }


  public String getRunName() {
    return runName;
  }


  public boolean isAddToSavedMap() {
    return addToSavedMap;
  }


  public void setAddToSavedMap(boolean addToSavedMap) {
    this.addToSavedMap = addToSavedMap;
  }
  

  public int getNodeType() {
    return nodeType;
  }
  

  public long getANextDb() {
    return aNextDb;
  }

  
  public Object getValue() {
    return value;
  }

  
  public long getAparams() {
    return aparams;
  }
}
