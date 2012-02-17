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

import java.util.TreeSet;
/**
 * @author Dr. Richard St. John
 * @version $Revision$, $Date$
 */

public class TrfParameter {
  
  private long aparams;
  
  private int ptype, nobjs, rowCount, colCount;
  
  private String pname, type;
  
  private TreeSet<String> runNames;
  
  private Object value;
  
  
  public TrfParameter() {
    runNames = new TreeSet<String>();
  }
  
  
  public TrfParameter(String pname, int ptype) {
    this.pname = pname;
    this.ptype = ptype;
  }
  
  
  public void setInformation(String type, int rows, int cols, Object value) {
    this.type = type;
    this.nobjs = rows*cols;
    this.value = value;
    this.rowCount = rows;
    this.colCount = cols;
  }

  
  public String getType() {
    return type;
  }

  
  public void setType(String ptype) {
    this.type = ptype;
  }


  public Object getValue() {
    return value;
  }


  public void setValue(Object value) {
    this.value = value;
  }

  
  public String getName() {
    return pname;
  }

  
  public void setPname(String pname) {
    this.pname = pname;
  }

  
  public int getPtype() {
    return ptype;
  }

  
  public void setPtype(int ptype) {
    this.ptype = ptype;
  }

  public int getNobjs() {
    return nobjs;
  }

  public void setNobjs(int nobjs) {
    this.nobjs = nobjs;
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


  public long getAparams() {
    return aparams;
  }


  public void setAparams(long aparams) {
    this.aparams = aparams;
  }


  public TreeSet<String> getRunNames() {
    return runNames;
  }
}
