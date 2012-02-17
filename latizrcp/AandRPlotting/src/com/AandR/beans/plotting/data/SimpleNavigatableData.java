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
package com.AandR.beans.plotting.data;


/**
 * @author Dr. Richard St. John
 * @version $Revision$, $Date$
 * This class is used where a simple non-navigatable data is needed.
 */
public class SimpleNavigatableData extends AbstractNavigatableData {

  private double[][] data;
  

  /**
   * 
   */
  public SimpleNavigatableData(double[][] data) {
    this.data = data;
  }

  
  public double[][] getDatasetAt(int index) {
    return data;
  }
  
  
  public double[][] getFirstDataset() {
    return data;
  }

  
  public double[][] getLastDataset() {
    return data;
  }
  

  public double[][] getNextDataset() {
    return data;
  }
  

  public double[][] getPreviousDataset() {
    return data;
  }
  
  
  public boolean hasNext() {
    return false;
  }


  public int getCurrentIndex() {
    return 0;
  }


  public int getNumberOfFrames() {
    return 1;
  }


  public String getID() {
    return null;
  }
  
  public String getName() {
    return "";
  }


  public void setCurrentIndex(int index) {
  }


  public double[][] getData() {
    return data;
  }


  public void setData(double[][] data) {
    this.data = data;
  }
}
