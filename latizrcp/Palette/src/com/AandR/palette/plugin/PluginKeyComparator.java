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
package com.AandR.palette.plugin;

import java.util.Comparator;


/**
 *
 * @author  <a href="mailto://rstjohn@mza.com">Dr. Richard St. John</a>
 * @company <a href="http://www.mza.com">MZA Associates Corporation</a>
 */
public class PluginKeyComparator implements Comparator<PluginKey> {
  public int compare(PluginKey a, PluginKey b) {
    StringBuffer nameA = new StringBuffer(a.getUniqueID());
    StringBuffer nameB = new StringBuffer(b.getUniqueID());
    int l = Math.min(nameA.length(), nameB.length());
    int indexOfDifference = -1;
    for(int i=0; i<l; i++) {
      if(nameA.charAt(i)!=nameB.charAt(i)) {
        indexOfDifference = i;
        break;
      }
    }
    if(indexOfDifference<1) return a.getUniqueID().compareTo(b.getUniqueID());

    String remainderA = nameA.substring(indexOfDifference);
    String remainderB = nameB.substring(indexOfDifference);
    if(remainderA.contains("/")) {
      return remainderB.contains("/") ? a.getUniqueID().compareTo(b.getUniqueID()) : -1;
    } else if(remainderB.contains("/")) {
      return 1;
    }

    return a.getUniqueID().compareTo(b.getUniqueID());
  }
}