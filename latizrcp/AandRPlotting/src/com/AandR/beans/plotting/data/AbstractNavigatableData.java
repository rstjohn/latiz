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
public abstract class AbstractNavigatableData implements NavigatableData {
  
  protected double dx, dy;

  public AbstractNavigatableData() {
    dx = dy = 1;
  }
  
  
  public double getDx() {
    return dx;
  }

  
  public void setDx(double dx) {
    this.dx = dx;
  }

  
  public double getDy() {
    return dy;
  }

  
  public void setDy(double dy) {
    this.dy = dy;
  } 
  
  
  public void setGridPointSpacing(double dx, double dy) {
    this.dx = dx;
    this.dy = dy;
  }
}
