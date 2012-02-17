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

import java.util.Arrays;

/**
 *
 * @author Dr. Richard St. John
 * @author Dr. Aaron J. Masino
 * @version $Revision: 1.1 $, $Date: 2007/05/25 00:12:26 $
 */
public class Statistics {

    public static final int COLUMN_WISE_COMPUTATION = 0;
    public static final int ROW_WISE_COMPUTATION = 1;
    public static final int WHOLE_DATA_SET_COMPUTATION = 2;
    // constructor method for this class
    // Does nothing

    public Statistics() {
    }

    /**
     * Computes the mean of the array x.
     * @param x
     * @return double
     */
    public static double mean(double[] x) {
        if (isVectorNull(x)) {
            return 0;
        }
        double sum = 0;
        for (int i = 0; i < x.length; i++) {
            sum += x[i];
        }
        return sum / x.length;
    }

    public static double mean(int[] x) {
        double sum = 0;
        for (int i = 0; i < x.length; i++) {
            sum += x[i];
        }
        return sum / x.length;
    }

    /**
     * computes the column wise mean of the array x
     * @param x
     * @return
     */
    public static double[] mean(double[][] x) {
        double[] y = new double[x[0].length];
        double sum;
        double rows = x.length;
        for (int i = 0; i < x[0].length; i++) {
            sum = 0;
            for (int j = 0; j < rows; j++) {
                sum += x[j][i];
            }
            y[i] = sum / rows;
        }

        return y;
    }

    /**
     * returns the mean value(s) as computed column-wise, row-wise, or over
     * the entire data set as determined by the value of computationType
     * For ROW_WISE_COMPUTATION and WHOLE_DATA_SET_COMPUTATION, x may be jagged
     * @param x
     * @param computationType
     * @return
     */
    public static double[] mean(double[][] x, int computationType) {
        double sum;
        double[] y;
        switch (computationType) {
            case COLUMN_WISE_COMPUTATION:
                return Statistics.mean(x);

            case ROW_WISE_COMPUTATION:
                y = new double[x.length];
                double cols;
                for (int i = 0; i < x.length; i++) {
                    sum = 0;
                    cols = x[i].length;
                    for (int j = 0; j < cols; j++) {
                        sum += x[i][j];
                    }
                    y[i] = sum / cols;
                }

                return y;

            case WHOLE_DATA_SET_COMPUTATION:
                y = new double[1];
                sum = 0;
                double count = 0.0;
                for (int i = 0; i < x.length; i++) {
                    for (int j = 0; j < x[i].length; j++) {
                        sum += x[i][j];
                        count += 1.0;
                    }
                }
                if (count > 0) {
                    y[0] = sum / (count);
                    return y;
                }
        }

        return null;

    }

    /**
     * returns the mean value(s) as computed column-wise, row-wise, or over
     * the entire data set as determined by the value of computationType
     * For ROW_WISE_COMPUTATION and WHOLE_DATA_SET_COMPUTATION, x may be jagged
     * @param x
     * @param computationType
     * @return
     */
    public static double[] mean(float[][] x, int computationType) {
        double sum;
        double[] y;
        switch (computationType) {
            case COLUMN_WISE_COMPUTATION:
                y = new double[x[0].length];
                double rows = x.length;
                for (int i = 0; i < x[0].length; i++) {
                    sum = 0;
                    for (int j = 0; j < rows; j++) {
                        sum += x[j][i];
                    }
                    y[i] = sum / rows;
                }

                return y;

            case ROW_WISE_COMPUTATION:
                y = new double[x.length];
                double cols;
                for (int i = 0; i < x.length; i++) {
                    sum = 0;
                    cols = x[i].length;
                    for (int j = 0; j < cols; j++) {
                        sum += x[i][j];
                    }
                    y[i] = sum / cols;
                }

                return y;

            case WHOLE_DATA_SET_COMPUTATION:
                y = new double[1];
                sum = 0;
                double count = 0.0;
                for (int i = 0; i < x.length; i++) {
                    for (int j = 0; j < x[i].length; j++) {
                        sum += x[i][j];
                        count += 1.0;
                    }
                }
                y[0] = sum / (count);
                return y;
        }

        return null;

    }

    /**
     * returns the mean value(s) as computed column-wise, row-wise, or over
     * the entire data set as determined by the value of computationType
     * For ROW_WISE_COMPUTATION and WHOLE_DATA_SET_COMPUTATION, x may be jagged
     * @param x
     * @param computationType
     * @return
     */
    public static double[] mean(int[][] x, int computationType) {
        double sum = 0;
        double[] y;
        switch (computationType) {
            case COLUMN_WISE_COMPUTATION:
                y = new double[x[0].length];
                double rows = x.length;
                for (int i = 0; i < x[0].length; i++) {
                    sum = 0;
                    for (int j = 0; j < rows; j++) {
                        sum += x[j][i];
                    }
                    y[i] = sum / rows;
                }

                return y;

            case ROW_WISE_COMPUTATION:
                y = new double[x.length];
                double cols;
                for (int i = 0; i < x.length; i++) {
                    sum = 0;
                    cols = x[i].length;
                    for (int j = 0; j < cols; j++) {
                        sum += x[i][j];
                    }
                    y[i] = sum / cols;
                }

                return y;

            case WHOLE_DATA_SET_COMPUTATION:
                y = new double[1];
                sum = 0;
                double count = 0.0;
                for (int i = 0; i < x.length; i++) {
                    for (int j = 0; j < x[i].length; j++) {
                        sum += x[i][j];
                        count += 1.0;
                    }
                }
                y[0] = sum / (count);
                return y;
        }

        return null;

    }

    public static float mean(float[] x) {
        if (isVectorNull(x)) {
            return Float.NaN;
        }
        float sum = 0;
        for (int i = 0; i < x.length; i++) {
            sum += x[i];
        }
        return sum / x.length;
    }

    public static float mean(float[] x, float[] weights) {
        float sum = 0;
        float weightSum = 0;
        for (int i = 0; i < x.length; i++) {
            sum += x[i];
            weightSum += weights[i];
        }
        return sum / weightSum;
    }

    public static float mean(float[][] x) {
        if (isVectorNull(x)) {
            return Float.NaN;
        }
        float sum = 0;
        for (int i = 0; i < x.length; i++) {
            for (int j = 0; j < x[0].length; j++) {
                sum += x[i][j];
            }
        }
        return sum / (x.length * x[0].length);
    }

    /**
     * Computes the mean of a double array. It correctly computes the median for all size arrays: 0, even, or odd.
     * @param x The array from which to take the median.
     * @param isSorted True if the array does not need to be sorted.
     * @return The median
     */
    public static float median(float[] x, boolean isSorted) {
        if (!isSorted) {
            Arrays.sort(x);
        }

        if (x.length == 1) {
            return x[0];
        }
        int middleOfArray = x.length / 2;
        return x.length % 2 == 0 ? (x[middleOfArray] + x[middleOfArray + 1]) / 2 : x[middleOfArray];
    }

    /**
     * Computes the mean of a double array. It correctly computes the median for all size arrays: 0, even, or odd.
     * @param x The array from which to take the median.
     * @param isSorted True if the array does not need to be sorted.
     * @return The median
     */
    public static double median(double[] x, boolean isSorted) {
        if (!isSorted) {
            Arrays.sort(x);
        }

        if (x.length == 1) {
            return x[0];
        }
        int middleOfArray = x.length / 2;
        return x.length % 2 == 0 ? (x[middleOfArray] + x[middleOfArray + 1]) / 2 : x[middleOfArray];
    }

    /**
     * Computes the root mean square of the elements of the array x,
     * i.e. the square root of the average value of the squares of the elements of x
     * @param x
     * @return double
     */
    public static double rms(double[] x) {
        double sum = 0;
        for (int i = 0; i < x.length; i++) {
            sum += x[i] * x[i];
        }
        double d = x.length;
        return Math.sqrt(sum / d);
    }

    /**
     * computes the column wise rms of the array x
     * @param x
     * @return
     */
    public static double[] rms(double[][] x) {
        double[] y = new double[x[0].length];
        double sum;
        double rows = x.length;
        for (int i = 0; i < x[0].length; i++) {
            sum = 0;
            for (int j = 0; j < rows; j++) {
                sum += x[j][i] * x[j][i];
            }
            y[i] = Math.sqrt(sum / rows);
        }

        return y;
    }

    /**
     * computes teh rms of x
     * @param x
     * @return
     */
    public static float rms(float[] x) {
        if (isVectorNull(x)) {
            return Float.NaN;
        }
        float sum = 0;
        for (int i = 0; i < x.length; i++) {
            sum += x[i] * x[i];
        }
        float d = x.length;
        return (float) Math.sqrt(sum / d);
    }

    /**
     * computes the column wise rms of the array x
     * @param x
     * @return
     */
    public static double[] rms(float[][] x) {
        double[] y = new double[x[0].length];
        double sum;
        double rows = x.length;
        for (int i = 0; i < x[0].length; i++) {
            sum = 0;
            for (int j = 0; j < rows; j++) {
                sum += x[j][i] * x[j][i];
            }
            y[i] = Math.sqrt(sum / rows);
        }

        return y;
    }

    /**
     * computes teh rms of x
     * @param x
     * @return
     */
    public static float rms(int[] x) {
        float sum = 0;
        for (int i = 0; i < x.length; i++) {
            sum += x[i] * x[i];
        }
        float d = x.length;
        return (float) Math.sqrt(sum / d);
    }

    /**
     * computes the column wise rms of the array x
     * @param x
     * @return
     */
    public static double[] rms(int[][] x) {
        double[] y = new double[x[0].length];
        double sum;
        double rows = x.length;
        for (int i = 0; i < x[0].length; i++) {
            sum = 0;
            for (int j = 0; j < rows; j++) {
                sum += x[j][i] * x[j][i];
            }
            y[i] = Math.sqrt(sum / rows);
        }

        return y;
    }

    /**
     * returns the root mean square values(s) as computed column-wise, row-wise, or over
     * the entire data set as determined by the value of computationType
     * For ROW_WISE_COMPUTATION and WHOLE_DATA_SET_COMPUTATION, x may be jagged
     * @param x
     * @param computationType
     * @return
     */
    public static double[] rms(double[][] x, int computationType) {
        double sum;
        double[] y;
        switch (computationType) {
            case COLUMN_WISE_COMPUTATION:
                return Statistics.rms(x);

            case ROW_WISE_COMPUTATION:
                y = new double[x.length];
                double cols;
                for (int i = 0; i < x.length; i++) {
                    sum = 0;
                    cols = x[i].length;
                    for (int j = 0; j < cols; j++) {
                        sum += x[i][j] * x[i][j];
                    }
                    y[i] = Math.sqrt(sum / cols);
                }

                return y;

            case WHOLE_DATA_SET_COMPUTATION:
                y = new double[1];
                sum = 0;
                double count = 0.0;
                for (int i = 0; i < x.length; i++) {
                    for (int j = 0; j < x[i].length; j++) {
                        sum += x[i][j] * x[i][j];
                        count += 1.0;
                    }
                }
                if (count > 0) {
                    y[0] = Math.sqrt(sum / (count));
                    return y;
                }
        }

        return null;

    }

    /**
     * returns the root mean square values(s) as computed column-wise, row-wise, or over
     * the entire data set as determined by the value of computationType
     * For ROW_WISE_COMPUTATION and WHOLE_DATA_SET_COMPUTATION, x may be jagged
     * @param x
     * @param computationType
     * @return
     */
    public static double[] rms(float[][] x, int computationType) {
        double sum;
        double[] y;
        switch (computationType) {
            case COLUMN_WISE_COMPUTATION:
                return Statistics.rms(x);

            case ROW_WISE_COMPUTATION:
                y = new double[x.length];
                double cols;
                for (int i = 0; i < x.length; i++) {
                    sum = 0;
                    cols = x[i].length;
                    for (int j = 0; j < cols; j++) {
                        sum += x[i][j] * x[i][j];
                    }
                    y[i] = Math.sqrt(sum / cols);
                }

                return y;

            case WHOLE_DATA_SET_COMPUTATION:
                y = new double[1];
                sum = 0;
                double count = 0.0;
                for (int i = 0; i < x.length; i++) {
                    for (int j = 0; j < x[i].length; j++) {
                        sum += x[i][j] * x[i][j];
                        count += 1.0;
                    }
                }
                if (count > 0) {
                    y[0] = Math.sqrt(sum / (count));
                    return y;
                }
        }

        return null;
    }

    /**
     * returns the root mean square values(s) as computed column-wise, row-wise, or over
     * the entire data set as determined by the value of computationType
     * For ROW_WISE_COMPUTATION and WHOLE_DATA_SET_COMPUTATION, x may be jagged
     * @param x
     * @param computationType
     * @return
     */
    public static double[] rms(int[][] x, int computationType) {
        double sum;
        double[] y;
        switch (computationType) {
            case COLUMN_WISE_COMPUTATION:
                return Statistics.rms(x);

            case ROW_WISE_COMPUTATION:
                y = new double[x.length];
                double cols;
                for (int i = 0; i < x.length; i++) {
                    sum = 0;
                    cols = x[i].length;
                    for (int j = 0; j < cols; j++) {
                        sum += x[i][j] * x[i][j];
                    }
                    y[i] = Math.sqrt(sum / cols);
                }

                return y;

            case WHOLE_DATA_SET_COMPUTATION:
                y = new double[1];
                sum = 0;
                double count = 0.0;
                for (int i = 0; i < x.length; i++) {
                    for (int j = 0; j < x[i].length; j++) {
                        sum += x[i][j] * x[i][j];
                        count += 1.0;
                    }
                }
                if (count > 0) {
                    y[0] = Math.sqrt(sum / (count));
                    return y;
                }
        }

        return null;

    }

    /**
     * Computes the standard deviation of the elements of the array x.
     * @param x
     * @return double
     */
    public static double stdev(double[] x) {
        if (isVectorNull(x)) {
            return 0;
        }
        double average = mean(x);
        double N = x.length;
        double sumsquares = 0;

        for (int i = 0; i < x.length; i++) {
            sumsquares += (x[i] - average) * (x[i] - average);
        }
        return Math.sqrt(sumsquares / (N - 1));

    }

    public static double stdev(int[] x) {

        double average = mean(x);
        double N = x.length;
        double sumsquares = 0;

        for (int i = 0; i < x.length; i++) {
            sumsquares += (x[i] - average) * (x[i] - average);
        }
        return Math.sqrt(sumsquares / (N - 1));

    }

    /**
     * computes the column wise stdev of the array x
     * @param x
     * @return
     */
    public static double[] stdev(double[][] x) {
        double[] y = new double[x[0].length];
        double[] avgs = Statistics.mean(x);
        double sumsq;
        double rows = x.length;
        for (int i = 0; i < x[0].length; i++) {
            sumsq = 0;
            for (int j = 0; j < rows; j++) {
                sumsq += (x[j][i] - avgs[i]) * (x[j][i] - avgs[i]);
            }
            y[i] = Math.sqrt(sumsq / (rows - 1));
        }

        return y;
    }

    /**
     * returns the standard deviation value(s) as computed column-wise, row-wise, or over
     * the entire data set as determined by the value of computationType
     * For ROW_WISE_COMPUTATION and WHOLE_DATA_SET_COMPUTATION, x may be jagged
     * @param x
     * @param computationType
     * @return
     */
    public static double[] stdev(double[][] x, int computationType) {
        double sumsq;
        double[] y;
        double[] avgs;
        switch (computationType) {
            case COLUMN_WISE_COMPUTATION:
                return Statistics.stdev(x);

            case ROW_WISE_COMPUTATION:
                y = new double[x.length];
                avgs = Statistics.mean(x, computationType);
                double cols;
                for (int i = 0; i < x.length; i++) {
                    sumsq = 0;
                    cols = x[i].length;
                    for (int j = 0; j < cols; j++) {
                        sumsq += (x[i][j] - avgs[i]) * (x[i][j] - avgs[i]);
                    }
                    y[i] = Math.sqrt(sumsq / (cols - 1));
                }
                return y;

            case WHOLE_DATA_SET_COMPUTATION:
                sumsq = 0;
                y = new double[1];
                double count = 0.0;
                double avg = Statistics.mean(x, computationType)[0];
                for (int i = 0; i < x.length; i++) {
                    for (int j = 0; j < x[i].length; j++) {
                        sumsq += (x[i][j] - avg) * (x[i][j] - avg);
                        count += 1.0;
                    }
                }
                y[0] = Math.sqrt(sumsq / (count - 1));
                return y;
        }

        return null;

    }

    /**
     * returns the standard deviation value(s) as computed column-wise, row-wise, or over
     * the entire data set as determined by the value of computationType
     * For ROW_WISE_COMPUTATION and WHOLE_DATA_SET_COMPUTATION, x may be jagged
     * @param x
     * @param computationType
     * @return
     */
    public static double[] stdev(float[][] x, int computationType) {
        double sumsq;
        double[] y;
        double[] avgs;
        switch (computationType) {
            case COLUMN_WISE_COMPUTATION:
                y = new double[x[0].length];
                avgs = Statistics.mean(x, computationType);
                double rows = x.length;
                for (int i = 0; i < x[0].length; i++) {
                    sumsq = 0;
                    for (int j = 0; j < rows; j++) {
                        sumsq += (x[j][i] - avgs[i]) * (x[j][i] - avgs[i]);
                    }
                    y[i] = Math.sqrt(sumsq / (rows - 1));
                }

                return y;

            case ROW_WISE_COMPUTATION:
                y = new double[x.length];
                avgs = Statistics.mean(x, computationType);
                double cols;
                for (int i = 0; i < x.length; i++) {
                    sumsq = 0;
                    cols = x[i].length;
                    for (int j = 0; j < cols; j++) {
                        sumsq += (x[i][j] - avgs[i]) * (x[i][j] - avgs[i]);
                    }
                    y[i] = Math.sqrt(sumsq / (cols - 1));
                }
                return y;

            case WHOLE_DATA_SET_COMPUTATION:
                sumsq = 0;
                y = new double[1];
                double count = 0.0;
                double avg = Statistics.mean(x, computationType)[0];
                for (int i = 0; i < x.length; i++) {
                    for (int j = 0; j < x[i].length; j++) {
                        sumsq += (x[i][j] - avg) * (x[i][j] - avg);
                        count += 1.0;
                    }
                }
                y[0] = Math.sqrt(sumsq / (count - 1));
                return y;
        }

        return null;

    }

    /**
     * returns the standard deviation value(s) as computed column-wise, row-wise, or over
     * the entire data set as determined by the value of computationType
     * For ROW_WISE_COMPUTATION and WHOLE_DATA_SET_COMPUTATION, x may be jagged
     * @param x
     * @param computationType
     * @return
     */
    public static double[] stdev(int[][] x, int computationType) {
        double sumsq;
        double[] y;
        double[] avgs;
        switch (computationType) {
            case COLUMN_WISE_COMPUTATION:
                y = new double[x[0].length];
                avgs = Statistics.mean(x, computationType);
                double rows = x.length;
                for (int i = 0; i < x[0].length; i++) {
                    sumsq = 0;
                    for (int j = 0; j < rows; j++) {
                        sumsq += (x[j][i] - avgs[i]) * (x[j][i] - avgs[i]);
                    }
                    y[i] = Math.sqrt(sumsq / (rows - 1));
                }
                return y;

            case ROW_WISE_COMPUTATION:
                y = new double[x.length];
                avgs = Statistics.mean(x, computationType);
                double cols;
                for (int i = 0; i < x.length; i++) {
                    sumsq = 0;
                    cols = x[i].length;
                    for (int j = 0; j < cols; j++) {
                        sumsq += (x[i][j] - avgs[i]) * (x[i][j] - avgs[i]);
                    }
                    y[i] = Math.sqrt(sumsq / (cols - 1));
                }
                return y;

            case WHOLE_DATA_SET_COMPUTATION:
                sumsq = 0;
                y = new double[1];
                double count = 0.0;
                double avg = Statistics.mean(x, computationType)[0];
                for (int i = 0; i < x.length; i++) {
                    for (int j = 0; j < x[i].length; j++) {
                        sumsq += (x[i][j] - avg) * (x[i][j] - avg);
                        count += 1.0;
                    }
                }
                y[0] = Math.sqrt(sumsq / (count - 1));
                return y;
        }

        return null;

    }

    public static float stdev(float[] x) {
        if (isVectorNull(x)) {
            return 0;
        }
        float average = mean(x);
        float N = x.length;
        float sumsquares = 0.0f;

        for (int i = 0; i < x.length; i++) {
            sumsquares += (x[i] - average) * (x[i] - average);
        }
        return (float) Math.sqrt(sumsquares / (N - 1));

    }

    public static float stdev(float[] x, float[] weights) {
        float average = mean(x, weights);
        float weightSum = 0;
        float sumsquares = 0.0f;

        for (int i = 0; i < x.length; i++) {
            sumsquares += (x[i] - average) * (x[i] - average);
            weightSum += weights[i];
        }
        return (float) Math.sqrt(sumsquares / (weightSum - 1));

    }

    public float stdev(float[] x, float valueToIgnore) {

        float N = x.length;
        for (int i = 0; i < N; i++) {
            if (x[i] == valueToIgnore) {
                N = N - 1;
            }
        }
        float[] newx = new float[(int) N];
        int count = 0;
        for (int i = 0; i < x.length; i++) {
            if (x[i] != valueToIgnore) {
                newx[count++] = x[i];
            }
        }
        return stdev(newx);
    }

    public static float stdev(float[][] x, float valueToIgnore) {

        int N = x.length * x[0].length;
        for (int i = 0; i < x.length; i++) {
            for (int j = 0; j < x[0].length; j++) {
                if (x[i][j] == valueToIgnore) {
                    N = N - 1;
                }

            }
        }
        float[] newx = new float[N];
        int count = 0;
        for (int i = 0; i < x.length; i++) {
            for (int j = 0; j < x[0].length; j++) {
                if (x[i][j] != valueToIgnore) {
                    newx[count++] = x[i][j];
                }
            }
        }
        return stdev(newx);

    }

    public static float stdev(float[][] x) {
        if (isVectorNull(x)) {
            return 0;
        }
        float average = mean(x);
        float N = x.length * x[0].length;
        float sumsquares = 0;

        for (int i = 0; i < x.length; i++) {
            for (int j = 0; j < x[0].length; j++) {
                sumsquares += (x[i][j] - average) * (x[i][j] - average);
            }
        }
        return (float) Math.sqrt(sumsquares / (N - 1));

    }

    /**
     * Computes the covariance of the arrays x and y.
     * @param x
     * @param y
     * @return double
     */
    public static double covariance(double[] x, double[] y) {
        if (isVectorNull(x) || isVectorNull(y)) {
            return 0;
        }
        if (x.length != y.length) {
            System.out.println("Error: Covariance arguements must be the same size.");
            return 0;
        }
        double xmean = mean(x);
        double ymean = mean(y);
        double sum = 0;
        for (int i = 0; i < x.length; i++) {
            sum += (x[i] - xmean) * (y[i] - ymean);
        }
        double d = (x.length - 1);
        return sum / d;
    }

    public static float covariance(float[] x, float[] y) {
        if (isVectorNull(x) || isVectorNull(y)) {
            return 0;
        }
        if (x.length != y.length) {
            System.out.println("Error: Covariance arguements must be the same size.");
            return 0;
        }
        float xmean = mean(x);
        float ymean = mean(y);
        float sum = 0;
        for (int i = 0; i < x.length; i++) {
            sum += (x[i] - xmean) * (y[i] - ymean);
        }
        float d = (x.length - 1);
        return sum / d;
    }

    public static float correlation(float[] a, float[] b) {
        return correlation(a, 0.0f, b, 0.0f);
    }

    public static float correlation(float[] a, float aMean, float[] b, float bMean) {
        float numerator = 0.0f;
        float ssqA = 0.0f;
        float ssqB = 0.0f;
        int length = Math.min(a.length, b.length);
        for (int i = 0; i < length; i++) {
            numerator += (a[i] - aMean) * (b[i] - bMean);
            ssqA += (a[i] - aMean) * (a[i] - aMean);
            ssqB += (b[i] - bMean) * (b[i] - bMean);
        }
        float denominator = (float) Math.sqrt(ssqA * ssqB);
        return numerator / denominator;
    }

    public static float correlation(float[][] a, float[][] b) {
        float numerator = Integration.trapz(MatrixMath.multiplyElementWise(a, b));
        float denominator = Integration.trapz(MatrixMath.multiplyElementWise(a, a)) * Integration.trapz(MatrixMath.multiplyElementWise(b, b));
        return numerator / (float) Math.sqrt(denominator);
    }

    public static float correlation(float[][] a, float[][] aRef, float[][] b, float[][] bRef) {
        float[][] A = new float[a.length][a[0].length];
        for (int j = 0; j < a[0].length; j++) {
            for (int i = 0; i < a.length; i++) {
                A[i][j] = a[i][j] - aRef[i][j];
            }
        }
        float[][] B = new float[b.length][b[0].length];
        for (int j = 0; j < b[0].length; j++) {
            for (int i = 0; i < b.length; i++) {
                B[i][j] = b[i][j] - bRef[i][j];
            }
        }
        float numerator = Integration.trapz(MatrixMath.multiplyElementWise(A, B));
        float denominator = Integration.trapz(MatrixMath.multiplyElementWise(A, A)) * Integration.trapz(MatrixMath.multiplyElementWise(B, B));
        return numerator / (float) Math.sqrt(denominator);
    }

    /**
     * Checks if the vector x is null. Returns true if the vector is null, false otherwise.
     * @param x
     * @return boolean
     */
    private static boolean isVectorNull(double[] x) {
        if (x == null) {
            System.out.println("Error in mean: Length of x is zero.");
            return true;
        } else {
            return false;
        }
    }

    private static boolean isVectorNull(float[] x) {
        if (x == null) {
            System.out.println("Error in mean: Length of x is zero.");
            return true;
        } else {
            return false;
        }
    }

    private static boolean isVectorNull(float[][] x) {
        if (x == null) {
            System.out.println("Error in mean: Length of x is zero.");
            return true;
        } else {
            return false;
        }
    }
}
