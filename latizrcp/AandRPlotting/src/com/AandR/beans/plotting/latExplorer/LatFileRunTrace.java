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
 * @author Dr. Richard St. John
 * @version $Revision$, $Date$
 */
public class LatFileRunTrace {

  private int rowCount, colCount, parameterCount, realizationCount, iterationCount, type;
              
  private int[] selectedParameters, selectedRealizations, selectedColumns;
  
  private boolean addToSavedMap;
  
  private long[][] selectedDims;
  
  private double[] selectedTimes;
  
  private String alias, varName, dataDesc;
              
  
  public LatFileRunTrace() {
    rowCount = colCount = 1;
    addToSavedMap = false;
    selectedParameters = new int[] {0};
    selectedColumns = new int[] {0};
    selectedRealizations = new int[] {0};
    selectedDims = new long[1][2];
    type = -1;
  }
    

  public void setDims(int rowCount, int colCount) {
    this.rowCount = rowCount;
    this.colCount = colCount;
  }
  
  
  public void setDims(long[] dims) {
    if(dims==null) return;
    if(dims.length==1) {
      this.rowCount = 1;
      this.colCount = (int)dims[0];
    } else {
      this.rowCount = (int)dims[0];
      this.colCount = (int)dims[1];
    }
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

  
  public boolean isAddToSavedMap() {
    return addToSavedMap;
  }


  public void setAddToSavedMap(boolean addToSavedMap) {
    this.addToSavedMap = addToSavedMap;
  }

  
  public double[] getSelectedTimes() {
    return selectedTimes;
  }

  
  public void setSelectedTimes(double[] selectedTimes) {
    this.selectedTimes = selectedTimes;
  }

  
  public String getAlias() {
    return alias;
  }

  
  public void setAlias(String alias) {
    this.alias = alias;
  }

  
  public String getVarName() {
    return varName;
  }

  
  public void setVarName(String varName) {
    this.varName = varName;
  }


  public String getDataDesc() {
    return dataDesc;
  }


  public void setDataDesc(String dataDesc) {
    this.dataDesc = dataDesc;
  }


  public long[][] getSelectedDims() {
    return selectedDims;
  }


  public void setSelectedDims(long[][] selectedDims) {
    this.selectedDims = selectedDims;
  }


  public int getParameterCount() {
    return parameterCount;
  }


  public void setParameterCount(int parameterCount) {
    this.parameterCount = parameterCount;
  }


  public int getRealizationCount() {
    return realizationCount;
  }


  public void setRealizationCount(int realizationCount) {
    this.realizationCount = realizationCount;
  }


  public int getIterationCount() {
    return iterationCount;
  }


  public void setIterationCount(int iterationCount) {
    this.iterationCount = iterationCount;
  }


  public int[] getSelectedParameters() {
    return selectedParameters;
  }


  public void setSelectedParameters(int[] selectedParameters) {
    this.selectedParameters = selectedParameters;
  }


  public int[] getSelectedRealizations() {
    return selectedRealizations;
  }


  public void setSelectedRealizations(int[] selectedRealizations) {
    this.selectedRealizations = selectedRealizations;
  }


  public int[] getSelectedColumns() {
    return selectedColumns;
  }


  public void setSelectedColumns(int[] selectedColumns) {
    this.selectedColumns = selectedColumns;
  }


  public int getType() {
    return type;
  }


  public void setType(int type) {
    this.type = type;
  }
}
