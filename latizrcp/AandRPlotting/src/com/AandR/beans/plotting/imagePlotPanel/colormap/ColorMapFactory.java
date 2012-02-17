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
 * @version $Revision: 1.1 $, $Date: 2007/05/25 00:12:33 $
 */
public class ColorMapFactory {
  public final static int RAINBOWSCALE = 0; 
  public final static int JET = 1;
  public final static int HOT = 2;
  public final static int COPPER = 3;
  public final static int RED = 4;
  public final static int GREEN = 5;
  public final static int BLUE = 6;  
  public final static int GREYSCALE = 7; 
  public final static int INVERSE_GREYSCALE = 8; 
  public final static int MATLAB = 9;

  public static AbstractColorMap createColorMap(int type) {
    switch(type) {
    case RAINBOWSCALE:
      return new RainbowColorMap();
    case JET:
      return new JetColorMap();
    case MATLAB:
      return new MatlabColoMap();
    case HOT:
      return new HotColorMap();
    case COPPER:
      return new CopperColorMap();
    case RED:
      return new RedColorMap();
    case GREEN:
      return new GreenColorMap();
    case BLUE:
      return new BlueColorMap();
    case INVERSE_GREYSCALE:
      return new InverseGreyScaleColorMap();
    case GREYSCALE:
      return new GreyScaleColorMap();
    }
    return null;
  }
}
