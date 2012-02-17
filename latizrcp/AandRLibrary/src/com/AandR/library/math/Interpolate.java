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
 *
 * @author Dr. Richard St. John
 * @version $Revision: 1.1 $, $Date: 2007/05/25 00:12:25 $
 */
public class Interpolate {

    public Interpolate() {
    }

    /**
     * 1-D Linear Interpolation without any error checking
     * @param x is a 1-dimensional array of x values
     * @param y is a 1-dimensional array of y values, y(x)
     * @param xi is the x-value to interpolate
     * @param errCheck is a flag to specify that no error checking will occur. This can be any integer.
     * @return the interpolated y-value (yi).
     */
    public static double linearWithoutErrorChecking(double x[], double y[], double xi) {
        int i = 0;
        while (x[i] <= xi) {
            if (x[i] == xi) {
                return y[i];
            }
            i++;
        }
        i--;
        return y[i + 1] + (xi - x[i + 1]) * (y[i + 1] - y[i]) / (x[i + 1] - x[i]);
    }

    /**
     * 1-D Linear Interpolation with error checking
     * @param x is a 1-dimensional array of x values
     * @param y is a 1-dimensional array of y values, y(x)
     * @param xi is the x-value to interpolate
     * @return the interpolated y-value (yi).
     */
    public static double linear(double x[], double y[], double xi) {

        if (x.length != y.length) {
            System.out.println("Length of x must equal length of y");
            return 0.0;
        }

        if (xi < x[0] || xi > x[x.length - 1]) {
            System.out.println("Interpolated Value out of bounds");
            return 0.0;
        }
        return linearWithoutErrorChecking(x, y, xi);
    }

    /**
     * 2-D Linear Interpolation without Error Checking
     * @param x is a 1-dimensional array of x values
     * @param y is a 1-dimensional array of y values
     * @param z is a 2-dimensional array of z values, z(x,y)
     * @param xi is the x-value to interpolate
     * @param yi is the x-value to interpolate
     * @param isChecked is a flag to specify that no error checking will occur. This can be any boolean.
     * @return the interpolated z-value (zi).
     */
    public static double linearWithoutErrorChecking(double x[], double y[], double z[][], double xi, double yi) {

//  Find x value of the Grid onto which to interpolate
        int i = 0;
        while ((x[i] <= xi)) {
            i++;
        }
        i--;

//  Find y value of the Grid onto which to interpolate
        int j = 0;
        while ((y[j] <= yi)) {
            j++;
        }
        j--;

        double xslope = (xi - x[i]) / (x[i + 1] - x[i]);
        double yslope = (yi - y[j]) / (y[j + 1] - y[j]);

        double zdiff1 = z[i + 1][j] - z[i][j];
        double zdiff2 = z[i + 1][j + 1] - z[i][j + 1];

        double a = zdiff1 * xslope + z[i][j];
        double b = zdiff2 * xslope + z[i][j + 1];

        return (b - a) * yslope + a;
    }

    /**
     * 2-D Linear Interpolation with Error Checking
     * @param x is a 1-dimensional array of x values
     * @param y is a 1-dimensional array of y values
     * @param z is a 2-dimensional array of z values, z(x,y)
     * @param xi is the x-value to interpolate
     * @param yi is the x-value to interpolate
     * @param isChecked is a flag to specify that no error checking will occur. This can be any boolean.
     * @return the interpolated z-value (zi).
     */
    public static double linear(double x[], double y[], double z[][], double xi, double yi) {

        if (x.length * y.length != z.length * z[0].length) {
            System.out.println("Array Dimensions Are Mismatched");
            return 0.0;
        }

        if (xi < x[0] || xi > x[x.length - 1] || yi < y[0] || yi > y[y.length - 1]) {
            System.out.println("Interpolated Value out of bounds");
            return 0.0;
        }
        return linearWithoutErrorChecking(x, y, z, xi, yi);
    }

    /**
     * Bi-Quadratic interpolation given tabular data z(x[], y[]) at the point zi = z(xi, yi)
     * @param x
     * @param y
     * @param z
     * @param xi
     * @param yi
     * @return
     */
    public static double biQuadratic(double x[], double y[], double z[][], double xi, double yi) {
        if (x.length * y.length != z.length * z[0].length) {
            System.out.println("Array Dimensions Are Mismatched");
            return 0.0;
        }

        if (xi < x[0] || xi > x[x.length - 1] || yi < y[0] || yi > y[y.length - 1]) {
            System.out.println("Interpolated Value out of bounds");
            return 0.0;
        }

//  Find x value of the Grid onto which to interpolate
        int i = 0;
        while ((x[i] <= xi)) {
            i++;
        }
        i--;

//  Find y value of the Grid onto which to interpolate
        int j = 0;
        while ((y[j] <= yi)) {
            j++;
        }
        j--;
        double xm = biQuadAlgorithm(z[i - 1][j - 1], 2 * z[i - 1][j], z[i - 1][j + 1], yi - j);
        double x0 = biQuadAlgorithm(z[i][j - 1], 2 * z[i][j], z[i][j + 1], yi - j);
        double xp = biQuadAlgorithm(z[i + 1][j - 1], 2 * z[i + 1][j], z[i + 1][j + 1], yi - j);
        return 0.25 * biQuadAlgorithm(xm, x0 + x0, xp, xi - i);
    }

    private static double biQuadAlgorithm(double am, double a0, double ap, double x) {
        return a0 + x * (ap - am + x * (ap + am - a0));
    }

    /**
     * Bicubic interpolation
     * @param x
     * @param y
     * @param data
     * @return
     */
    public static double biCubic(double x, double y, double[][] data) {
        int nx = data.length;
        int ny = data[0].length;

        int i = Math.max(1, Math.min(nx - 3, (int) x)) - 1;
        int j = Math.max(1, Math.min(ny - 3, (int) y)) - 1;

        double xm = biCubicInterpAlgorithm(data[i - 1][j - 1], data[i - 1][j], data[i - 1][j + 1], data[i - 1][j + 2], y - j);
        double x0 = biCubicInterpAlgorithm(data[i][j - 1], data[i][j], data[i][j + 1], data[i][j + 2], y - j);
        double xp = biCubicInterpAlgorithm(data[i + 1][j - 1], data[i + 1][j], data[i + 1][j + 1], data[i + 1][j + 2], y - j);
        double x2 = biCubicInterpAlgorithm(data[i + 2][j - 1], data[i + 2][j], data[i + 2][j + 1], data[i + 2][j + 2], y - j);
        return biCubicInterpAlgorithm(xm, x0, xp, x2, x - i);
    }

    /**
     * Bicubic interpolation
     * @param x
     * @param y
     * @param data
     * @return
     */
    public static float biCubic(float x, float y, float[][] data) {
        int nx = data.length;
        int ny = data[0].length;

        int i = Math.max(1, Math.min(nx - 3, (int) x)) - 1;
        int j = Math.max(1, Math.min(ny - 3, (int) y)) - 1;

        float xm = biCubicInterpAlgorithm(data[i - 1][j - 1], data[i - 1][j], data[i - 1][j + 1], data[i - 1][j + 2], y - j);
        float x0 = biCubicInterpAlgorithm(data[i][j - 1], data[i][j], data[i][j + 1], data[i][j + 2], y - j);
        float xp = biCubicInterpAlgorithm(data[i + 1][j - 1], data[i + 1][j], data[i + 1][j + 1], data[i + 1][j + 2], y - j);
        float x2 = biCubicInterpAlgorithm(data[i + 2][j - 1], data[i + 2][j], data[i + 2][j + 1], data[i + 2][j + 2], y - j);
        return biCubicInterpAlgorithm(xm, x0, xp, x2, x - i);
    }

    private static double biCubicInterpAlgorithm(double val0, double val1, double val2, double val3, double x) {
        double a = val1;
        double b = 6.0 * val2 - 3.0 * val1 - 2.0 * val0 - val3;
        double c = 3.0 * (val2 + val0 - val1 - val1);
        double d = val3 - val0 + 3.0 * (val1 - val2);
        return a + x * (b + x * (c + x * d)) / 6.0;
    }

    private static float biCubicInterpAlgorithm(float val0, float val1, float val2, float val3, float x) {
        float a = val1;
        float b = 6.0f * val2 - 3.0f * val1 - 2.0f * val0 - val3;
        float c = 3.0f * (val2 + val0 - val1 - val1);
        float d = val3 - val0 + 3.0f * (val1 - val2);
        return a + x * (b + x * (c + x * d)) / 6.0f;
    }
}
