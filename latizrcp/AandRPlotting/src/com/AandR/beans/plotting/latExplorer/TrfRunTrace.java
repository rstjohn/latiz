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

public class TrfRunTrace {

  private String varName, typeName, description;

  private int type, flags, nobjs, id, aNextVars, varNumber;
  
  private long aNextDb;

  private float initialTag, deltaTag;

  public TrfRunTrace() {
  }

  public long getANextDb() {
    return aNextDb;
  }

  public void setANextDb(long nextDb) {
    aNextDb = nextDb;
  }

  public int getANextVars() {
    return aNextVars;
  }

  public void setANextVars(int nextVars) {
    aNextVars = nextVars;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public int getFlags() {
    return flags;
  }

  public void setFlags(int flags) {
    this.flags = flags;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public int getNobjs() {
    return nobjs;
  }

  public void setNobjs(int nobjs) {
    this.nobjs = nobjs;
  }

  public int getType() {
    return type;
  }

  public void setType(int type) {
    this.type = type;
  }

  public String getTypeName() {
    return typeName;
  }

  public void setTypeName(String typeName) {
    this.typeName = typeName;
  }

  public String getVarName() {
    return varName;
  }

  public void setVarName(String varName) {
    this.varName = varName;
  }

  public int getVarNumber() {
    return varNumber;
  }

  public void setVarNumber(int varNumber) {
    this.varNumber = varNumber;
  }

  public float getDeltaTag() {
    return deltaTag;
  }

  public void setDeltaTag(float deltaTag) {
    this.deltaTag = deltaTag;
  }

  public float getInitialTag() {
    return initialTag;
  }

  public void setInitialTag(float initialTag) {
    this.initialTag = initialTag;
  }

  public String getDataType() {
    switch(type) {
      case TrfConstants.SHORT:  return "short";
      case TrfConstants.INT:    return "int";
      case TrfConstants.LONG:   return "long";
      case TrfConstants.FLOAT:  return "float";
      case TrfConstants.DOUBLE: return "double";
      case TrfConstants.COMPLEX: return "complex";
    }
    return "no data";
  }
}
