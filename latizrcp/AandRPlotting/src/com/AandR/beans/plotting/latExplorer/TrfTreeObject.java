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

import java.io.File;
import java.io.Serializable;

/**
 * @author Dr. Richard St. John
 * @version $Revision$, $Date$
 */
public class TrfTreeObject implements Serializable {

  private String label, name;
  
  private File file;


  public TrfTreeObject(String label) {
    this.label = label;
  }

  
  public String toString() {
    return label;
  }
  

  public File getFile() {
    return file;
  }
  

  public void setFile(File file) {
    this.file = file;
  }


  public String getName() {
    return name;
  }


  public void setName(String name) {
    this.name = name;
  }
}
