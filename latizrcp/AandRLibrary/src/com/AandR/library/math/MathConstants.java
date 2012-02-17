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
package com.AandR.library.math;

/**
 * @author Aaron Masino
 * @version $Revision: 1.1 $, $Date: 2007/05/25 00:12:25 $
 */
public class MathConstants {
  /**
   * The value of Pi with double precision.
   */
  public final static double PI = 3.141592653589793;
  
  /**
   * The value of Pi/2 with double precision.
   */
  public final static double piOverTwo = PI/2d; 

  /**
   * The value of 2*Pi with double precision.
   */
  public final static double TWOPI = 6.283185307179586;

  /**
   * The value of exp(1) with double precision.
   */
  public final static double e = 2.718281828459045;

  /**
   * The speed of light in vacuum in meters per second.
   */
  public final static double c = 299792458; // speed of light m/s
  
  /**
   * The number of degrees per radian with double precision.
   */
  public final static double toDegrees = 180.0/PI;
  
  /**
   * The number of radians per degree with double precision.
   */
  public final static double toRadians = PI/180.0; 
  
  /**
   * Earth radius in meters.
  */
  public final static double earthRadius = 6378136.3; 
  
  /**
   * Planck's constant in units of Joule - sec
   */
  public final static double h = 6.626176e-34; 
  
/**
 * Temperature (K)
 */
  public static final double T0 = 300.0;  
  
/**
 * Atmospheric Refractivity constant at temperature T0 in units of (m^3 / J)
 */
  public static final double C_A = 4.7199944853e-9; //Atmospheric Refractivity constant at temperature T0 (m^3 / J)
}
