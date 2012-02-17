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
 */
public abstract interface NavigatableData {
  public boolean hasNext();
  public int getCurrentIndex();
  public int getNumberOfFrames();
  public double[][] getDatasetAt(int index);
  public double[][] getFirstDataset();
  public double[][] getPreviousDataset();
  public double[][] getNextDataset();
  public double[][] getLastDataset();
  public String getID();
  public String getName();
  public void setCurrentIndex(int index);
}
