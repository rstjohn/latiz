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
public class Integration {

    /**
     * 2-D Trapezoidal Rule Integration with uniform, unit spacing
     *
     * @param y-value
     *            to integrate
     * @return
     */
    public static final double trapz(double[][] y) {
        int nx = y.length;
        int ny = y[0].length;

        double sumx = 0.0;
        for (int i = 1; i < nx - 1; i++) {
            sumx += (y[i][0] + y[i][nx - 1]);
        }

        double sumy = 0.0;
        for (int i = 1; i < ny - 1; i++) {
            sumy += (y[0][i] + y[nx - 1][i]);
        }

        double sumxy = 0.0;
        for (int j = 1; j < ny - 1; j++) {
            for (int i = 1; i < nx - 1; i++) {
                sumxy += y[i][j];
            }
        }
        return 0.25 * (y[0][0] + y[nx - 1][0] + y[0][ny - 1] + y[nx - 1][ny - 1]) + 0.5 * (sumx + sumy) + sumxy;
    }

    /**
     * 2-D Trapezoidal Rule Integration with uniform, unit spacing
     *
     * @param y-value
     *            to integrate
     * @return
     */
    public static final float trapz(float[][] y) {
        int nx = y.length;
        int ny = y[0].length;

        float sumx = 0.0f;
        for (int i = 1; i < nx - 1; i++) {
            sumx += (y[i][0] + y[i][nx - 1]);
        }

        float sumy = 0.0f;
        for (int i = 1; i < ny - 1; i++) {
            sumy += (y[0][i] + y[nx - 1][i]);
        }

        float sumxy = 0.0f;
        for (int j = 1; j < ny - 1; j++) {
            for (int i = 1; i < nx - 1; i++) {
                sumxy += y[i][j];
            }
        }
        return 0.25f * (y[0][0] + y[nx - 1][0] + y[0][ny - 1] + y[nx - 1][ny - 1]) + 0.5f * (sumx + sumy) + sumxy;
    }

    /**
     * 1-D Trapezoidal Rule Integration with uniform, unit spacing
     *
     * @param y-value
     *            to integrate
     */
    public static final double trapz(double y[]) {

        int n = y.length - 1;
        double sum = 0.0;

        for (int i = 1; i < n; i++) {
            sum = sum + y[i];
        }
        return (sum + 0.50 * (y[0] + y[n]));
    }

    /**
     * 1-D Trapezoidal Rule Integration with uniform, unit spacing
     *
     * @param y-value
     *            to integrate
     */
    public static final float trapz(float y[]) {
        int n = y.length - 1;
        float sum = 0.0f;

        for (int i = 1; i < n; i++) {
            sum = sum + y[i];
        }
        return (sum + 0.50f * (y[0] + y[n]));
    }

    /**
     * 1-D Trapezoidal Rule Integration with variable spacing
     *
     * @param x
     *            is the independent variable
     * @param y
     *            is the dependent variable
     * @return integral of y with spacing x.
     */
    public static final double trapz(double x[], double y[]) {

        double sum = 0.0;

        if (x.length != y.length) {
            System.out.println("Length of x must equal length of y");
        }

        for (int i = 1; i < x.length; i++) {
            sum = sum + (x[i] - x[i - 1]) * (y[i - 1] + y[i]);
        }
        return 0.50 * sum;
    }

    /**
     * 1-D Trapezoidal Rule Integration with variable spacing
     *
     * @param x
     *            is the independent variable
     * @param y
     *            is the dependent variable
     * @return integral of y with spacing x.
     */
    public static final float trapz(float x[], float y[]) {

        float sum = 0.0f;

        if (x.length != y.length) {
            System.out.println("Length of x must equal length of y");
        }

        for (int i = 1; i < x.length; i++) {
            sum = sum + (x[i] - x[i - 1]) * (y[i - 1] + y[i]);
        }
        return 0.50f * sum;
    }

    /**
     * 1-D Trapezoidal Rule For Cumulative Integration with unit spacing
     *
     * @param y
     * @return
     */
    public static final double[] cumtrapz(double y[]) {
        int n = y.length;
        double[] cumtrap = new double[n];

        cumtrap[0] = 0.0;
        for (int i = 1; i < n - 1; i++) {
            cumtrap[i] = cumtrap[i - 1] + 0.5 * (y[i] + y[i - 1]);
        }
        cumtrap[n - 1] = cumtrap[n - 2] + 0.5 * (y[n - 1] + y[n - 2]);
        return cumtrap;
    }

    /**
     * 1-D Trapezoidal Rule For Cumulative Integration with unit spacing
     *
     * @param y
     * @return
     */
    public static final float[] cumtrapz(float y[]) {
        int n = y.length;
        float[] cumtrap = new float[n];

        cumtrap[0] = 0.0f;
        for (int i = 1; i < n - 1; i++) {
            cumtrap[i] = cumtrap[i - 1] + 0.5f * (y[i] + y[i - 1]);
        }
        cumtrap[n - 1] = cumtrap[n - 2] + 0.5f * (y[n - 1] + y[n - 2]);
        return cumtrap;
    }

    /**
     * Uses Simpson's rule to integrate y(x) with uniform spacing
     *
     * @param y
     * @return The integrated value.
     */
    public static final double simpson(double[] y) {
        int n = y.length;
        if (n % 2 == 0) {
            System.out.println("SIMPSON RULE INTEGRATION ERROR: Must use Odd Number of Points");
            return 0.0;
        }
        double sumEven = 0.0;
        for (int i = 1; i < n - 1; i += 2) {
            sumEven += y[i];
        }
        sumEven = 4.0 * sumEven;

        double sumOdd = 0.0;
        for (int i = 2; i < n - 2; i += 2) {
            sumOdd += y[i];
        }
        sumOdd = 2.0 * sumOdd;

        return 0.3333333333333333 * (y[0] + sumEven + sumOdd + y[n - 1]);
    }
}
