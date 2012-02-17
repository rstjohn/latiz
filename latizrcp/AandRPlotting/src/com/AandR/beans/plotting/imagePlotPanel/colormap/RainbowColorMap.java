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
package com.AandR.beans.plotting.imagePlotPanel.colormap;


/**
 * @author Dr. Richard St. John
 * @version $Revision: 1.1 $, $Date: 2007/05/25 00:12:32 $
 */
public class RainbowColorMap extends AbstractColorMap {
  public int[] getColorValue(int i) {
    int red, green, blue;
    if (i < 0) {
      red = 0;
      green = 0;
      blue = 255;
      return new int[]{red, green, blue};
    } else if (i < 64) {
      red = 0;
      green = 4*i+1;
      blue = 255;
      return new int[]{red, green, blue};
    } else if (i < 128) {
      red = 0;
      green = 255;
      blue = 255 - (i-64)*4;
      return new int[]{red, green, blue};
    } else if (i < 192) {
      red = (i-128)*4;
      green = 255;
      blue = 0;
      return new int[]{red, green, blue};
    } else if (i < 256) {
      red = 255;
      green = 255 - (i-192)*4;
      blue = 0;
      return new int[]{red, green, blue};
    } else {
      red = 255;
      green = 0;
      blue = 0;
      return new int[]{red, green, blue};
    }
  }
}
