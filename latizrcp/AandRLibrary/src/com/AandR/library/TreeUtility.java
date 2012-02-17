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
package com.AandR.library;

import java.util.StringTokenizer;

import javax.swing.JTree;
import javax.swing.tree.TreePath;

/**
 * @author <a href="mailto://rstjohn@mza.com">Dr. Richard St. John</a>
 * @company <a href="http://www.mza.com">MZA Associates Corporation</a>
 */
public class TreeUtility {

  // is path1 descendant of path2
  public static boolean isDescendant(TreePath path1, TreePath path2) {
    int count1 = path1.getPathCount();
    int count2 = path2.getPathCount();
    if (count1 <= count2)
      return false;
    while (count1 != count2) {
      path1 = path1.getParentPath();
      count1--;
    }
    return path1.equals(path2);
  }

  public static String getExpansionState(JTree tree, int row) {
    TreePath rowPath = tree.getPathForRow(row);
    StringBuffer buf = new StringBuffer();
    int rowCount = tree.getRowCount();
    for (int i = row; i < rowCount; i++) {
      TreePath path = tree.getPathForRow(i);
      if (i == row || isDescendant(path, rowPath)) {
        if (tree.isExpanded(path))
          buf.append("," + String.valueOf(i - row));
      } else
        break;
    }
    return buf.toString();
  }

  public static void restoreExpanstionState(JTree tree, int row, String expansionState) {
    StringTokenizer stok = new StringTokenizer(expansionState, ",");
    while (stok.hasMoreTokens()) {
      int token = row + Integer.parseInt(stok.nextToken());
      tree.expandRow(token);
    }
  }
}
