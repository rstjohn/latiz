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
 * @author Dr. Richard St. John
 * @version $Revision: 1.1 $, $Date: 2007/05/25 00:12:25 $
 */
public class SpecialFunctions {

  public SpecialFunctions() {
  }

  /**
   * 
   * @param s
   * @return
   */
  public static double factorial(int s) {
    if (s < 0 || s > 17) {
      System.out.println("Factorial argument must be > 0 and < 18");
      return Double.NaN;
    }
    double[] a = { 1.0, 1.0, 2.0, 6.0, 24.0, 120.0, 720.0, 5040.0, 40320.0, 362880.0, 3628800.0, 39916800.0, 479001600.0, 6227020800.0, 87178291200.0, 1307674368000.0, 20922789888000.0,
        355687428096000.0 };
    return a[s];
  }

  /**
   * This routine computes a 2D Gaussian with a given standard deviation and center given by (x0,y0).
   * 
   * @param x
   * @param y
   * @param x0
   * @param y0
   * @param stdev
   *            Standard Deviation of the Gaussian
   * @return
   */
  public static double gaussian(double x, double y, double x0, double y0, double stdev) {
    double denom = 2.0 * stdev * stdev;
    double normalization = Math.PI * denom;
    double arg = (x - x0) * (x - x0) + (y - y0) * (y - y0);

    return Math.exp(-arg / denom) / normalization;
  }

  /**
   * Error Function, taken from Numerical Recipes.
   * 
   * @param x
   *            Argument of the error function of type double.
   * @return double y = erf(x).
   */
  public static double erf(double x) {
    return Math.signum(x) * gammap(0.5, x * x);
  }

  /**
   * Error Function, taken from Numerical Recipes.
   * 
   * @param x
   *            Argument of the error function of type float.
   * @return float y = erf(x).
   */
  public static float erf(float x) {
    return (float) erf((double) x);
  }

  /**
   * Complimentary Error Function, Numerical Recipes
   * 
   * @param x
   *            Argument of the complimentary error function of type double.
   * @return double y = erfc(x).
   */
  public static double erfc(double x) {
    double y = 0.0;
    if (x < 0.0)
      y = 1.0 + gammap(0.50, x * x);
    else
      y = gammaq(0.50, x * x);
    return y;
  }

  /**
   * Complimentary Error Function, Numerical Recipes
   * 
   * @param x
   *            Argument of the complimentary error function of type float.
   * @return float y = erfc(x).
   */
  public static float erfc(float x) {
    return (float) erfc((double) x);
  }

  /**
   * Log of Gamma Function, Numerical Recipes. Used for computing error function
   * 
   * @param x
   * @return type double
   */
  public static double gammln(double x) {
    if (x <= 0) {
      System.out.println("Only valid for x > 0");
      return 0.0;
    }
    double[] cof = { 76.18009172947146, -86.50532032941677, 24.01409824083091, -1.231739572450155, .1208650973866179e-2, -.5395239384953000e-5 };

    double stp = 2.5066282746310005;
    double ser = 1.000000000190015;
    double y = x;
    double tmp = x + 5.5;

    tmp = (x + 0.5) * Math.log(tmp) - tmp;

    for (int i = 0; i < cof.length; i++) {
      y = y + 1;
      ser = ser + cof[i] / y;
    }
    return tmp + Math.log(stp * ser / x);
  }

  /**
   * Gamma Function from Numerical Recipes. Numerical recipes computes the ln of gamma. This method exponentiates the Numerical Recipe method.
   * 
   * @param x
   * @return type double
   */
  public static double gamma(double x) {
    return Math.exp(gammln(x));
  }

  /**
   * GCF From Numerical Recipes
   * 
   * @param a
   * @param x
   * @return
   */
  private static double gcf(double a, double x) {
    int itmax = 100;
    double eps = 3.0e-7;
    double fpmin = 1.0e-30;
    double b = x + 1.0f - a;
    double c = 1.0f / fpmin;
    double d = 1.0f / b;
    double h = d;

    double gln = gammln(a);

    for (int j = 0; j < itmax; j++) {
      double i = j;
      double an = -i * (i - a);
      b = b + 2;
      d = an * d + b;

      if (Math.abs(d) < fpmin)
        d = fpmin;
      c = b + an / c;

      if (Math.abs(c) < fpmin)
        c = fpmin;
      d = 1.0f / d;
      double del = d * c;
      h = h * del;
      if (Math.abs(del - 1) < eps)
        break;
    }

    return Math.exp(-x + a * Math.log(x) - gln) * h;
  }

  /**
   * GSER from Nuerical Recipes
   * 
   * @param a
   * @param x
   * @return
   */
  private static double gser(double a, double x) {
    if (x < 0.0)
      return 0.0;

    int itmax = 100;
    double eps = 3.0e-7;
    double ap, del, sum;

    double gln = gammln(a);

    ap = a;
    sum = 1.0 / a;
    del = sum;

    for (int i = 0; i < itmax; i++) {
      ap = ap + 1;
      del = del * x / ap;
      sum = sum + del;
      if (Math.abs(del) < Math.abs(sum * eps))
        break;
      // write error checking
    }
    return sum * Math.exp(-x + a * Math.log(x) - gln);
  }

  /**
   * Incomplete Gamma Function Q(a,x)
   * 
   * @param a
   * @param x
   * @return
   */
  public static double gammaq(double a, double x) {
    if (x < 0.0 || a <= 0.0)
      return 0.0;

    // Use Series Representation
    if (x < a + 1.0)
      return 1.0 - gser(a, x);

    // Use the Continued Fraction Representation
    else
      return gcf(a, x);
  }

  /**
   * Incomplete Gamma Function P(a,x)
   * 
   * @param a
   * @param x
   * @return
   */
  public static double gammap(double a, double x) {
    if (x < 0.0 || a <= 0.0)
      return 0.0;

    // Use Series Representation
    if (x < a + 1.0)
      return gser(a, x);

    // Use the Continued Fraction Representation
    else
      return 1.0 - gcf(a, x);
  }

  /**
   * generates the annular resonator functions (arfs)
   * @param index
   * @param r
   * @param q
   * @return
   */
  public static double arf(int index, double r, double q) {
    double z = 0.0;
    int iord = (int) Math.sqrt(index - 1.0);
    int inord = index - iord * iord - 1;
    int itrord = (2 * (inord % 2) - 1) * (int) ((inord + 1.0) / 2.0);
    int irord = (int) ((2 * iord - inord) / 2.0);

    switch (irord) {
      case 0:
        z = 1.0;
        break;
      case 1:
        z = -2.0 + 3.0 * r;
        break;
      case 2:
        z = 3.0 + r * (-12.0 + 10.0 * r);
        break;
      case 3:
        z = -4.0 + r * (30.0 + r * (-60.0 + 35.0 * r));
        break;
      case 4:
        z = 5.0 + r * (-60.0 + r * (210.0 + r * (-280.0 + 126.0 * r)));
        break;
      case 5:
        z = -6.0 + r * (105.0 + r * (-560.0 + r * (1260.0 + r * (-1260.0 + 462.0 * r))));
        break;
      case 6:
        z = 7.0 + r * (-168.0 + r * (1260.0 + r * (-4200.0 + r * (6930.0 + r * (-5544.0 + 1716.0 * r)))));
        break;
      case 7:
        z = -8.0 + r * (252.0 + r * (-2520.0 + r * (11550.0 + r * (-27720.0 + r * (36036.0 + r * (-24024.0 + 6435.0 * r))))));
        break;
      case 8:
        z = 9.0 + r * (-360.0 + r * (4620.0 + r * (-27720.0 + r * (90090.0 + r * (-168168.0 + r * (180180.0 + r * (-102960.0 + 24310.0 * r)))))));
        break;
      case 9:
        z = -10.0 + r * (495.0 + r * (-7920.0 + r * (60060.0 + r * (-252252.0 + r * (630630.0 + r * (-960960.0 + r * (875160.0 + r * (-437580.0 + 92378.0 * r))))))));
        break;
      case 10:
        z = 11.0 + r * (-660.0 + r * (12870.0 + r * (-120120.0 + r * (630630.0 + r * (-2018016.0 + r * (4084080.0 + r * (-5250960.0 + r * (4157010.0 + r * (-1847560.0 + 352716.0 * r)))))))));
        break;
      case 11:
        z = -12.0 + r * (858 + r * (-20020 + r * (225225 + r * (-1441440 + r * (5717712 + r * (-14702688 + r * (24942060 + r * (-27713400 + r * (19399380 + r * (-7759752 + r * 1352078))))))))));
    }
    if (itrord <= 0)
      return z * Math.cos(-itrord * q);
    else
      return z * Math.sin(itrord * q);
  }

  
  /**
   * Zernike polynomials.
   * @param index
   * @param r
   * @param q
   * @return
   */
  public static double zernike(int index, double r, double q) {

    double r2, p = 0.0;
    double sqrt2 = 1.4142135623730951;
    double sqrt3 = 1.7320508075688772;
    double sqrt5 = 2.23606797749979;
    double sqrt6 = 2.449489742783178;
    double sqrt8 = 2 * sqrt2;
    double sqrt10 = 3.1622776601683795;

    switch (index) {
      case 1:
        p = 1.;
        break;
      case 2:
        p = 2 * r * Math.cos(q);
        break;
      case 3:
        p = 2 * r * Math.sin(q);
        break;
      case 4:
        p = sqrt3 * (2 * r * r - 1.);
        break;
      case 5:
        p = sqrt6 * r * r * Math.cos(2. * q);
        break;
      case 6:
        p = sqrt6 * r * r * Math.sin(2. * q);
        break;
      case 7:
        p = sqrt8 * r * (3 * r * r - 2.) * Math.cos(q);
        break;
      case 8:
        p = sqrt8 * r * (3 * r * r - 2.) * Math.sin(q);
        break;
      case 9:
        p = sqrt8 * r * r * r * Math.cos(3. * q);
        break;
      case 10:
        p = sqrt8 * r * r * r * Math.sin(3. * q);
        break;
      case 11:
        p = sqrt5 * (6 * r * r * (r * r - 1) + 1);
        break;
      case 12:
        p = sqrt10 * (r * r * (4 * r * r - 3)) * Math.cos(2. * q);
        break;
      case 13:
        p = sqrt10 * (r * r * (4 * r * r - 3)) * Math.sin(2. * q);
        break;
      case 14:
        p = sqrt10 * r * r * r * r * Math.cos(4. * q);
        break;
      case 15:
        p = sqrt10 * r * r * r * r * Math.sin(4. * q);
        break;
      case 16:
        p = 2 * sqrt3 * r * (r * r * (10 * r * r - 12) + 3) * Math.cos(q);
        break;
      case 17:
        p = 2 * sqrt3 * r * (r * r * (10 * r * r - 12) + 3) * Math.sin(q);
        break;
      case 18:
        p = Math.sqrt(12.) * r * r * r * (5 * r * r - 4) * Math.cos(3. * q);
        break;
      case 19:
        p = 2 * sqrt3 * r * r * r * (5 * r * r - 4) * Math.sin(3. * q);
        break;
      case 20:
        p = 2 * sqrt3 * r * r * r * r * r * Math.cos(5. * q);
        break;
      case 21:
        p = 2 * sqrt3 * r * r * r * r * r * Math.sin(5. * q);
        break;
      case 22:
        p = Math.sqrt(7.) * (r * r * (r * r * (20 * r * r - 30) + 12) - 1);
        break;
      case 23:
        p = Math.sqrt(14.) * r * r * (r * r * (15 * r * r - 20) + 6) * Math.cos(2. * q);
        break;
      case 24:
        p = Math.sqrt(14.) * r * r * (r * r * (15 * r * r - 20) + 6) * Math.sin(2. * q);
        break;
      case 25:
        p = Math.sqrt(14.) * r * r * r * r * (6 * r * r - 5) * Math.cos(4. * q);
        break;
      case 26:
        p = Math.sqrt(14.) * r * r * r * r * (6 * r * r - 5) * Math.sin(4. * q);
        break;
      case 27:
        p = Math.sqrt(14.) * r * r * r * r * r * r * Math.cos(6. * q);
        break;
      case 28:
        p = Math.sqrt(14.) * r * r * r * r * r * r * Math.sin(6. * q);
        break;
      case 29:
        p = 4. * (r * (r * r * (r * r * (35 * r * r - 60) + 30) - 4)) * Math.cos(q);
        break;
      case 30:
        r2 = r * r;
        p = 4. * (r * (r2 * (r2 * (35 * r2 - 60) + 30) - 4)) * Math.sin(q);
        break;
      case 31:
        r2 = r * r;
        p = 4. * r * r2 * (r2 * (21 * r2 - 30) + 10) * Math.cos(3. * q);
        break;
      case 32:
        r2 = r * r;
        p = 4. * r * r2 * (r2 * (21 * r2 - 30) + 10) * Math.sin(3. * q);
        break;
      case 33:
        r2 = r * r;
        p = 4. * r * r2 * r2 * (7 * r2 - 6) * Math.cos(5. * q);
        break;
      case 34:
        r2 = r * r;
        p = 4. * r * r2 * r2 * (7 * r2 - 6) * Math.sin(5. * q);
        break;
      case 35:
        r2 = r * r;
        p = 4. * r * r2 * r2 * r2 * Math.cos(7. * q);
        break;
      case 36:
        r2 = r * r;
        p = 4. * r * r2 * r2 * r2 * Math.sin(7. * q);
        break;
      case 37:
        r2 = r * r;
        p = 3. * (r2 * (r2 * (r2 * (70 * r2 - 140) + 90) - 20) + 1);
        break;
      case 38:
        r2 = r * r;
        p = Math.sqrt(18.) * r2 * (r2 * (r2 * (56 * r2 - 105) + 60) - 10) * Math.cos(2. * q);
        break;
      case 39:
        r2 = r * r;
        p = Math.sqrt(18.) * r2 * (r2 * (r2 * (56 * r2 - 105) + 60) - 10) * Math.cos(2. * q);
        break;
      case 40:
        r2 = r * r;
        p = Math.sqrt(18.) * r2 * r2 * (r2 * (28 * r2 - 42) + 15) * Math.cos(4. * q);
        break;
      case 41:
        r2 = r * r;
        p = Math.sqrt(18.) * r2 * r2 * (r2 * (28 * r2 - 42) + 15) * Math.sin(4. * q);
        break;
      case 42:
        r2 = r * r;
        p = Math.sqrt(18.) * r2 * r2 * r2 * (8 * r2 - 7) * Math.cos(6. * q);
        break;
      case 43:
        r2 = r * r;
        p = Math.sqrt(18.) * r2 * r2 * r2 * (8 * r2 - 7) * Math.sin(6. * q);
        break;
      case 44:
        r2 = r * r;
        p = Math.sqrt(18.) * r2 * r2 * r2 * r2 * Math.cos(8. * q);
        break;
      case 45:
        r2 = r * r;
        p = Math.sqrt(18.) * r2 * r2 * r2 * r2 * Math.sin(8. * q);
        break;
//      case 46:
      /*              write(*,*) 'enter n, l: ';
      read(*,*) n, l
      k = abs(l)
      xnorm = Math.sqrt(.5/real(n+1))
      do is = 0, (n - k)/2
      denom = fact(is)*fact((n+k)/2-is)*fact((n-k)/2-is)
      coeff = (-1.)**is*fact(n-is)/denom
      p = p + coeff*r**(n-2*is)
      end do
      if(l>0) p = xnorm*p*Math.cos(k*q)
      if(l<0) p = xnorm*p*Math.sin(k*q)
       */
    }
    return p;
  }
}
