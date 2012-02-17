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
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.AandR.library.math;

/**
 *
 * @author Aaron Masino
 */
public class ArrayMath {

    /**
     * Reshapes a 1-dimensional array into a consistent two-dimensional array.
     * @param A the array to reshape
     * @param m the dimension of the first rank of the 2-dimensional array.
     * @param n the dimension of the second rank of the 2-dimensional array.
     * @return a two-dimension array of size double[m][n].
     */
    public static double[][] reshape(double[] A, int m, int n) {
        if (A.length != m * n) {
            System.err.println("Inconsistent Shape");
        }

        double[][] x = new double[m][n];
        for (int i = 0; i < m; i++) {
            System.arraycopy(A, i * m, x[i], 0, n);
        }
        return x;
    }

    public static int[] reshape(int[][] A) {
        int m = A.length;
        int n = A[0].length;
        int[] x = new int[A.length * A[0].length];
        for (int i = 0; i < m; i++) {
            System.arraycopy(A[i], 0, x, i * n, n);
        }
        return x;
    }

    public static float[] reshape(float[][] A) {
        int m = A.length;
        int n = A[0].length;
        float[] x = new float[A.length * A[0].length];
        for (int i = 0; i < m; i++) {
            System.arraycopy(A[i], 0, x, i * n, n);
        }
        return x;
    }

    public static long[] reshape(long[][] A) {
        int m = A.length;
        int n = A[0].length;
        long[] x = new long[A.length * A[0].length];
        for (int i = 0; i < m; i++) {
            System.arraycopy(A[i], 0, x, i * n, n);
        }
        return x;
    }

    public static double[] reshape(double[][] A) {
        int m = A.length;
        int n = A[0].length;
        double[] x = new double[A.length * A[0].length];
        for (int i = 0; i < m; i++) {
            System.arraycopy(A[i], 0, x, i * n, n);
        }
        return x;
    }

    /**
     *
     * @param dimension
     * @return
     */
    public static double[] createZeroArray(int dimension) {
        double[] vector = new double[dimension];
        for (int i = 0; i < dimension; i++) {
            vector[i] = 0;
        }
        return vector;
    }

    /**
     *
     * @param imageSeries
     * @return
    private static float[][] average(float[][][] imageSeries) {
    float[][] a = new float[imageSeries[0].length][imageSeries[0][0].length];
    int seriesLength = imageSeries.length;
    int i, j, k;
    for(j=0; j<imageSeries[0][0].length; j++) {
    for(i=0; i<imageSeries[0].length; i++) {
    for(k=0; k<imageSeries.length; k++) {
    a[i][j] += imageSeries[k][i][j];
    }
    a[i][j] = a[i][j]/seriesLength;
    }
    }
    return a;
    }
     */
    /**
     * Taken from Matlab: used to determine the equivalent single index corresponding to a given set of subscript values. Currently, it only works for
     * rank 2 matrices.
     * @param sizes
     * @param i       the first index
     * @param j       the 2nd index
     * @return        the index
     */
    public static int sub2ind(int[] sizes, int i, int j) {
        int result = j * sizes[1] + i;
//  if(result>sizes[0]*sizes[1]) throw new IndexOutOfBoundsException("Out of range subscript");
        return result;
    }

    /**
     *
     * @param sizes
     * @param i
     * @param j
     * @return
     */
    public static int[] sub2ind(int[] sizes, int[] i, int[] j) {
        if (i.length != j.length) {
            throw new IndexOutOfBoundsException("Out of range subscript");
        }
        int[] ii = new int[i.length];
        for (int k = 0; k < i.length; k++) {
            ii[k] = sub2ind(sizes, i[k], j[k]);
        }
        return ii;
    }

    /**
     * the element-wise multiplication of a scalar times a 1-dimensional array
     * @param A       The array
     * @param scalar  the scalar
     * @return the element-wise multiplication of a scalar times a 1-dimensional array
     */
    public static double[] multiply(double[] A, double scalar) {
        double[] B = new double[A.length];
        for (int j = 0; j < A.length; j++) {
            B[j] = A[j] * scalar;
        }
        return B;
    }

    /**
     * the element-wise multiplication of a scalar times a 1-dimensional array
     * @param A       The array
     * @param scalar  the scalar
     * @return the element-wise multiplication of a scalar times a 1-dimensional array
     */
    public static float[] multiply(float[] A, float scalar) {
        float[] B = new float[A.length];
        for (int j = 0; j < A.length; j++) {
            B[j] = A[j] * scalar;
        }
        return B;
    }

    /**
     * Adds to arrays
     * @param a 1st array
     * @param b 2nd array
     * @return  the resultant array
     */
    public static double[] add(double[] a, double[] b) {
        double[] c = new double[a.length];
        for (int i = 0; i < a.length; i++) {
            c[i] = a[i] + b[i];
        }
        return c;
    }

    /**
     * Adds to arrays
     * @param a 1st array
     * @param b 2nd array
     * @return  the resultant array
     */
    public static float[] add(float[] a, float[] b) {
        float[] c = new float[a.length];
        for (int i = 0; i < a.length; i++) {
            c[i] = a[i] + b[i];
        }
        return c;
    }

    /**
     * Subtracts two arrays
     * @param a 1st array
     * @param b 2nd array
     * @return  the resultant array
     */
    public static double[] subtract(double[] a, double[] b) {
        double[] c = new double[a.length];
        for (int i = 0; i < a.length; i++) {
            c[i] = a[i] - b[i];
        }
        return c;
    }

    /**
     * Subtracts two arrays
     * @param a 1st array
     * @param b 2nd array
     * @return  the resultant array
     */
    public static float[] subtract(float[] a, float[] b) {
        float[] c = new float[a.length];
        for (int i = 0; i < a.length; i++) {
            c[i] = a[i] - b[i];
        }
        return c;
    }

    /**
     *
     * @param a
     * @param b
     * @return
     */
    public static double[] multiplyElementWise(double[] a, double[] b) {
        double[] c = new double[a.length];
        for (int i = 0; i < a.length; i++) {
            c[i] = a[i] * b[i];
        }
        return c;
    }

    /**
     *
     * @param a
     * @param b
     * @return
     */
    public static float[] multiplyElementWise(float[] a, float[] b) {
        float[] c = new float[a.length];
        for (int i = 0; i < a.length; i++) {
            c[i] = a[i] * b[i];
        }
        return c;
    }

    /**
     *
     * @param a
     * @param b
     * @return
     */
    public static float[] divisionElementWise(float[] a, float[] b) {
        float[] c = new float[a.length];
        for (int i = 0; i < a.length; i++) {
            if (b[i] != 0) {
                c[i] = a[i] / b[i];
            } else {
                c[i] = Float.NaN;
            }
        }
        return c;
    }

    /**
     *
     * @param a
     * @param b
     * @return
     */
    public static double[] divisionElementWise(double[] a, double[] b) {
        double[] c = new double[a.length];
        for (int i = 0; i < a.length; i++) {
            if (b[i] != 0) {
                c[i] = a[i] / b[i];
            } else {
                c[i] = Double.NaN;
            }
        }
        return c;
    }

    /**
     * Performs a dot-product of two arrays
     * @param a
     * @param b
     * @return the scalar value resulting from the dot-product
     */
    public static double dotProduct(double[] a, double[] b) {
        double c = 0;
        for (int i = 0; i < a.length; i++) {
            c += a[i] * b[i];
        }
        return c;
    }
}
