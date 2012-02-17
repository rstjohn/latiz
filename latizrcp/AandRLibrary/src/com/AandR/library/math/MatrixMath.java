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
 * @version $Revision$, $Date$
 */
public class MatrixMath {


  /** Solve A*X = B
  @param B    right hand side
  @return     solution if A is square, least squares solution otherwise
   */

  public static final double[][] solve(double[][] A, double[][] B) {
    int m = A.length;
    int n = A[0].length;
    return (m == n ? (new LUDecomposition(A)).solve(B) : (new QRDecomposition(A)).solve(B));
  }


  public static final double[][] identiy(int m, int n) {
    double[][] I = new double[m][n];
    for(int j=0; j<n; j++) {
      for(int i=0; i<m; i++) {
        I[i][j] = (i==j) ? 1 : 0;
      }
    }
    return I;
  }


  public static final double[][] inverse(double[][] A) {
    int m=A.length;
    return solve(A,identiy(m, m));
  }


  /**
   * Copies a two-dimensional array of doubles. It uses the arrayCopy method
   * from {@link System} to do the copying.
   * @param   source the two-dimension array to copy. 
   * @return  a copy of a two-dimensional array of doubles
   */  
  public static double[][] copy(double[][] source) {
    double[][] dest = new double[source.length][source[0].length];
    for(int i=0; i<source.length; i++) {
      System.arraycopy(source[i], 0, dest[i], 0, source[0].length);
    }
    return dest;
  }


  /**
   * Copies a two-dimensional array. It uses the arrayCopy method
   * from {@link System} to do the copying.
   * @param   source the two-dimension array to copy. 
   * @return  a copy of a two-dimensional array.
   */  
  public static float[][] copy(float[][] source) {
    float[][] dest = new float[source.length][source[0].length];
    for(int i=0; i<source.length; i++) {
      System.arraycopy(source[i], 0, dest[i], 0, source[0].length);
    }
    return dest;
  }


  /**
   * 
   * @param source
   * @param xPos
   * @param xLength
   * @param yPos
   * @param yLength
   * @return
   */
  public static float[][] copy(float[][] source, int xPos, int xLength, int yPos, int yLength) {
    int xSize = Math.min(xLength, source.length-xPos);
    int ySize = Math.min(yLength, source[0].length-yPos);
    int xMax = Math.min(xLength+xPos, source.length);
    float[][] dest = new float[xSize][ySize];
    int counter=0;
    for(int i=xPos; i<xMax; i++) {
      System.arraycopy(source[i], yPos, dest[counter++], 0, Math.min(yLength, source[0].length-yPos));
    }
    return dest;
  }


  /**
   * 
   * @param source
   * @param xPos
   * @param xLength
   * @param yPos
   * @param yLength
   * @return
   */
  public static double[][] copy(double[][] source, int xPos, int xLength, int yPos, int yLength) {
    int xSize = Math.min(xLength, source.length-xPos);
    int ySize = Math.min(yLength, source[0].length-yPos);
    int xMax = Math.min(xLength+xPos, source.length);
    double[][] dest = new double[xSize][ySize];
    int counter=0;
    for(int i=xPos; i<xMax; i++) {
      System.arraycopy(source[i], yPos, dest[counter++], 0, Math.min(yLength, source[0].length-yPos));
    }
    return dest;
  }


  /**
   * Extracts a copy of the given 2-dimensional array
   * @param source      the array from which the extraction is done 
   * @param xLength     the number of elements to extract from the 1st rank
   * @param yLength     the number of elements to extract from the 2nd rank
   * @return            a copy of the given 2-dimensional array
   */
  public static float[][] extractFromArrayCenter(float[][] source, int xLength, int yLength) {
    int xPos = (source.length - xLength)/2;
    int yPos = (source[0].length - yLength)/2;
    return copy(source, xPos, xLength, yPos, yLength);
  }



  public static double[][] multiply(double[][] A, double scalar) {
    double[][] B = new double[A.length][A[0].length];
    for(int i=0; i<A[0].length; i++) {
      for(int j=0; j<A.length; j++) {
        B[j][i] = scalar*A[j][i];
      }
    }
    return B;
  }


  public static double[][] multiply(double[][] A, double[][] B) {
    int m=A.length;
    int n=A[0].length;
    int p=B[0].length;

    double[][] C = new double[A.length][B[0].length];
    double[] Ai, Bk, Ci;
    double Aik;
    for(int i=0; i<m; i++) {
      Ci = C[i];
      Ai = A[i];
      for(int k=0; k<n; k++) {
        Aik = Ai[k];
        Bk = B[k];
        for(int j=0; j<p; j++) {
          Ci[j] += Aik*Bk[j];
        }
      }
    }
    return C;
  }


  /**
   * 
   * @param data
   * @return
   */
  public static double[][] transposeInSitu(double[][] data) {
    int N = data.length; int M = data[0].length;
    if(N!=M) System.out.println("Cannot transpose in-situ a non-square array");
    double s;
    for(int n=0; n<N-1; n++) {
      for(int m=n+1; m<N; m++) {
        s = data[n][m];
        data[n][m] = data[m][n];
        data[m][n] = s;
      }
    }
    return data;
  }


  public static double[][] transpose(double[][] data) {
    double[][] output = new double[data[0].length][data.length];
    for(int j=0; j<data[0].length; j++) {
      for(int i=0; i<data.length; i++) {
        output[j][i] = data[i][j];
      }
    }
    return output;
  }



  public static float[][] multiply(float[][] a, float[][] b) {
    float[][] c = new float[a.length][a[0].length];
    int i, j, k;
    for(j=0; j<a[0].length; j++) {
      for(i=0; i<a.length; i++) {
        for(k=0; k<a.length; k++) {
          c[i][j] += a[i][k]*b[k][j];
        }
      }
    }
    return c;
  }


  public static float[][] multiply(float[][] A, float scalar) {
    float[][] B = new float[A.length][A[0].length];
    for(int i=0; i<A[0].length; i++) {
      for(int j=0; j<A.length; j++) {
        B[j][i] = scalar*A[j][i];
      }
    }
    return B;
  }

  /**
   * 
   * @param a
   * @param b
   * @return
   */
  public static float[][] multiplyElementWise(float[][] a, float[][] b) {
    float[][] c = new float[a.length][a[0].length];
    int i, j;
    for(j=0; j<a[0].length; j++) {
      for(i=0; i<a.length; i++) {
        c[i][j] = a[i][j]*b[i][j];
      }
    }
    return c;
  }


  /**
   * Sets part of a 2-dimension array to a specified value.
   * @param A                   the 2-dimension array in which the values are stored
   * @param value               the value to store
   * @param fromIndexLeft       the starting left index
   * @param toIndexLeft         the ending left index    
   * @param fromIndexRight      the starting right index
   * @param toIndexRight        the ending right index
   * @param fromIndexBottom     the starting bottom index
   * @param toIndexBottom       the ending bottom index
   * @param fromIndexTop        the starting top index
   * @param toIndexTop          the stopping top index
   */
  public static void fillAnnulus(double[][] A, double value, 
          int fromIndexLeft, int toIndexLeft, int fromIndexRight, int toIndexRight,
          int fromIndexBottom, int toIndexBottom, int fromIndexTop, int toIndexTop) {

    int nx = A.length; 
    int ny = A[0].length;

    int i0 = Math.max(0,fromIndexLeft);
    nx = Math.min(nx-1,toIndexRight);

    int i1 = Math.min(toIndexLeft, nx);
    int i2 = Math.max(fromIndexRight, 1);

    int j0 = Math.max(0, fromIndexBottom);
    ny = Math.min(ny-1, toIndexTop);

    int j1 = Math.min(toIndexBottom, ny);
    int j2 = Math.max(fromIndexTop, 1);

//  Left and Right parts of annulus    
    for(int j=j0; j<=ny; j++) {
      for(int i=i0; i<=i1; i++) {
        A[i][j] = value;
      }
      for(int i=i2; i<=nx; i++) {
        A[i][j] = value;
      }
    }

//  Top and Bottom parts of annulus
    for(int i=i1; i<=i2; i++) {
      for(int j=j0; j<=j1; j++) {
        A[i][j] = value;
      }
      for(int j=j2; j<=ny; j++) {
        A[i][j] = value;
      }
    }
  }

  /**
   * Fills a rectangular subarray of the array A with the specified value.
   * @param A               the array in which the value is stored.
   * @param fromIndex0      the initial index in the 1st rank of the array
   * @param toIndex0        the final index in the 1st rank of the array
   * @param fromIndex1      the initial index in the 2nd rank of the array
   * @param toIndex1        the final index in the 2nd rank of the array
   * @param value           the value to store
   */
  public static void fill(float[][] A, float value, int fromIndex0, int toIndex0, int fromIndex1, int toIndex1) {
    int nx = A.length;
    int ny = A[0].length;

    int i0 = Math.max(0, fromIndex0);
    int i1 = Math.min(nx-1, toIndex0);

    int j0 = Math.max(0, fromIndex1);
    int j1 = Math.min(ny-1, toIndex1);
    for(int j=j0; j<=j1; j++) {
      for(int i=i0; i<=i1; i++) {
        A[i][j] = value;
      }
    }
  }


  /**
   * Fills an 2-dimensional array with the specified value 
   * @param A       the 2-dimensional array
   * @param value   the value
   */
  public static void fill(float[][] A, float value) {
    fill(A,value,0,A.length-1,0,A[0].length-1);
  }


  /**
   * Fills an 2-dimensional array with the specified value 
   * @param A       the 2-dimensional array
   * @param value   the value
   */
  public static void fill(double[][] A, double value) {
    fill(A,value,0,A.length-1,0,A[0].length-1);
  }


  /**
   * Fills a rectangular subarray of the array A with the specified value.
   * @param A               the array in which the value is stored.
   * @param fromIndex0      the initial index in the 1st rank of the array
   * @param toIndex0        the final index in the 1st rank of the array
   * @param fromIndex1      the initial index in the 2nd rank of the array
   * @param toIndex1        the final index in the 2nd rank of the array
   * @param value           the value to store
   */
  public static void fill(double[][] A, double value, int fromIndex0, int toIndex0, int fromIndex1, int toIndex1) {
    int nx = A.length;
    int ny = A[0].length;

    int i0 = Math.max(0, fromIndex0);
    int i1 = Math.min(nx-1, toIndex0);

    int j0 = Math.max(0, fromIndex1);
    int j1 = Math.min(ny-1, toIndex1);
    for(int j=j0; j<=j1; j++) {
      for(int i=i0; i<=i1; i++) {
        A[i][j] = value;
      }
    }
  }


  /**
   * 
   * @param data
   * @return
   */
  public final static float[][] convertToFloat(double[][] data) {
    float[][] fdata = new float[data.length][data[0].length];
    for(int i=0; i<fdata[0].length; i++) {
      for(int j=0; j<fdata.length; j++) {
        fdata[j][i] = (float)data[j][i];
      }
    }
    return fdata;
  }
}
