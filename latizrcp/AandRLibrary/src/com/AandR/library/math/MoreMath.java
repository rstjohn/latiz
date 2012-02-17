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
 * <p>
 * A Java class for expanding the built-in Java Math class.
 * <p>
 * @author Dr. Richard St. John
 * @author Dr. Aaron J. Masino
 * @version $Revision: 1.2 $, $Date: 2007/07/25 12:20:35 $
 */

public class MoreMath {

	public static final int COLUMN_WISE_COMPUTATION = 0;

	public static final int ROW_WISE_COMPUTATION = 1;

	public static final int WHOLE_DATA_SET_COMPUTATION = 2;

	/**
	 * 
	 * @param data
	 * @return
	 */
	public static int[] findPeakLocation(double[][] data) {
		double max = Double.NEGATIVE_INFINITY;
		int nx = data.length, ny = data[0].length;
		int[] peakLoc = new int[2];
		int x, y;
		for (y = 0; y < ny; y++) {
			for (x = 0; x < nx; x++) {
				if (data[x][y] > max) {
					max = data[x][y];
					peakLoc[0] = x;
					peakLoc[1] = y;
				}
			}
		}
		return peakLoc;
	}

	/**
	 * 
	 * @param data
	 * @return
	 */
	public static int[] findPeakLocation(float[][] data) {
		float max = Float.NEGATIVE_INFINITY;
		int nx = data.length, ny = data[0].length;
		int[] peakLoc = new int[2];
		int x, y;
		for (y = 0; y < ny; y++) {
			for (x = 0; x < nx; x++) {
				if (data[x][y] > max) {
					max = data[x][y];
					peakLoc[0] = x;
					peakLoc[1] = y;
				}
			}
		}
		return peakLoc;
	}

	/**
	 * Normalizes the given array so that all values are between -1 and 1 based on the 
	 * element of "values" with the largets magnitude
	 * @param values
	 * @return
	 */
	public static void normalizeValues(double[] values) {
		double maxValue = 0;
		for (int i = 0; i < values.length; i++) {
			if (Math.abs(values[i]) > maxValue) maxValue = Math.abs(values[i]);
		}
		if (maxValue == 0) return;
		for (int i = 0; i < values.length; i++)
			values[i] /= maxValue;
		return;
	}

	public static final double[][] log(double[][] data) {
		double[][] logData = new double[data.length][data[0].length];
		int i, j;
		for (j = 0; j < data[0].length; j++) {
			for (i = 0; i < data.length; i++) {
				logData[i][j] = Math.log(data[i][j]);
			}
		}
		return logData;
	}

	/**
	 * 
	 * @param data
	 * @return
	 */
	public static final float[][] log(float[][] data) {
		float[][] logData = new float[data.length][data[0].length];
		int i, j;
		for (j = 0; j < data[0].length; j++) {
			for (i = 0; i < data.length; i++) {
				logData[i][j] = (float) Math.log(data[i][j]);
			}
		}
		return logData;
	}

	/**
	 * Reverse the items in a double array.
	 * @param values The array to be reversed. The reversed array is returned in the original array.
	 */
	public static void reverseArray(double[] values) {
		int left = 0;
		int right = values.length - 1;
		double temp;
		while (left < right) {
			temp = values[left];
			values[left] = values[right];
			values[right] = temp;
			left++;
			right--;
		}
	}

	/**
	 * Tests the value "number" to see if it is an integer power of 2
	 * @param number
	 * @return
	 */
	public static boolean isNumberIntegerPowerOfTwo(double number) {
		return (MoreMath.isInteger(MoreMath.logBaseA(number, 2)));
	}

	/**
	 * returns the square of the argument, x
	 * @param x
	 * @return
	 */
	public static double square(double x) {
		return x * x;
	}

	/**
	 * returns the square of the argument, x
	 * @param x
	 * @return
	 */
	public static float square(float x) {
		return x * x;
	}

	/**
	 * Finds the maximum value in a 1-dimension array.
	 * @param x is the double[] array to find the maximum.
	 * @return the maximum value of x.
	 */
	public static int max(int[] x) {
		int maxVal = x[0];
		for (int i = 1; i < x.length; i++) {
			maxVal = x[i] > maxVal ? x[i] : maxVal;
		}
		return maxVal;
	}
	
	/**
	 * Finds the maximum value in a 2-dimension array.
	 * @param x is the int[][] array in which to find the maximum.
	 * @return the maximum value of x.
	 */
	public static double max(int[][] x) {
		double maxVal = x[0][0];
		for (int i = 0; i < x.length; i++) {
			for (int j = 0; j < x[0].length; j++) {
				maxVal = x[i][j] > maxVal ? x[i][j] : maxVal;
			}
		}
		return maxVal;
	}

	/**
	 * Finds the maximum value in a 1-dimension array.
	 * @param x is the double[] array to find the maximum.
	 * @return the maximum value of x.
	 */
	public static double max(double[] x) {
		double maxVal = x[0];
		for (int i = 1; i < x.length; i++) {
			maxVal = x[i] > maxVal ? x[i] : maxVal;
		}
		return maxVal;
	}

	/**
	 * Finds the maximum value in a 2-dimension array.
	 * @param x is the double[][] array in which to find the maximum.
	 * @return the maximum value of x.
	 */
	public static double max(double[][] x) {
		double maxVal = x[0][0];
		for (int i = 0; i < x.length; i++) {
			for (int j = 0; j < x[0].length; j++) {
				maxVal = x[i][j] > maxVal ? x[i][j] : maxVal;
			}
		}
		return maxVal;
	}

	/**
	 * returns the maximum value(s) as computed column-wise, row-wise, or over
	 * the entire data set as determined by the value of computationType
	 * For ROW_WISE_COMPUTATION and WHOLE_DATA_SET_COMPUTATION, x may be jagged
	 * @param x
	 * @param computationType
	 * @return
	 */
	public static double[] max(double[][] x, int computationType) {
		double[] y;
		int rows = x.length;
		int cols;
		double max;
		switch(computationType) {
		case COLUMN_WISE_COMPUTATION : 
			cols = x[0].length;
			y = new double[cols];
			for(int i = 0; i<cols; i++) {
				max = x[0][i];
				for(int j=1; j<rows; j++) {
					max = x[j][i] > max ? x[j][i] : max;
				}
				y[i]=max;
			}
			return y;
			
		case ROW_WISE_COMPUTATION :
			y = new double[x.length];
			for(int i = 0; i<rows; i++) {
				max = x[i][0];
				for(int j=0; j<x[i].length; j++) {
					max = x[i][j] > max ? x[i][j] : max;
				}
				y[i]=max;
			}
			return y;
			
		case WHOLE_DATA_SET_COMPUTATION : 
			return new double[] {MoreMath.max(x)};
		}
		return null;
	}
	
	/**
	 * returns the maximum value(s) as computed column-wise, row-wise, or over
	 * the entire data set as determined by the value of computationType
	 * For ROW_WISE_COMPUTATION and WHOLE_DATA_SET_COMPUTATION, x may be jagged
	 * @param x
	 * @param computationType
	 * @return
	 */
	public static double[] max(float[][] x, int computationType) {
		double[] y;
		int rows = x.length;
		int cols;
		double max;
		switch(computationType) {
		case COLUMN_WISE_COMPUTATION : 
			cols = x[0].length;
			y = new double[cols];
			for(int i = 0; i<cols; i++) {
				max = x[0][i];
				for(int j=1; j<rows; j++) {
					max = x[j][i] > max ? x[j][i] : max;
				}
				y[i]=max;
			}
			return y;
			
		case ROW_WISE_COMPUTATION :
			y = new double[x.length];
			for(int i = 0; i<rows; i++) {
				max = x[i][0];
				for(int j=0; j<x[i].length; j++) {
					max = x[i][j] > max ? x[i][j] : max;
				}
				y[i]=max;
			}
			return y;
			
		case WHOLE_DATA_SET_COMPUTATION : 
			return new double[] {MoreMath.max(x)};
		}
		return null;
	}
	
	/**
	 * returns the maximum value(s) as computed column-wise, row-wise, or over
	 * the entire data set as determined by the value of computationType
	 * For ROW_WISE_COMPUTATION and WHOLE_DATA_SET_COMPUTATION, x may be jagged
	 * @param x
	 * @param computationType
	 * @return
	 */
	public static double[] max(int[][] x, int computationType) {
		double[] y;
		int rows = x.length;
		int cols;
		double max;
		switch(computationType) {
		case COLUMN_WISE_COMPUTATION : 
			cols = x[0].length;
			y = new double[cols];
			for(int i = 0; i<cols; i++) {
				max = x[0][i];
				for(int j=1; j<rows; j++) {
					max = x[j][i] > max ? x[j][i] : max;
				}
				y[i]=max;
			}
			return y;
			
		case ROW_WISE_COMPUTATION :
			y = new double[x.length];
			for(int i = 0; i<rows; i++) {
				max = x[i][0];
				for(int j=0; j<x[i].length; j++) {
					max = x[i][j] > max ? x[i][j] : max;
				}
				y[i]=max;
			}
			return y;
			
		case WHOLE_DATA_SET_COMPUTATION : 
			return new double[] {MoreMath.max(x)};
		}
		return null;
	}

	/**
	 * Finds the maximum value in a 1-dimension array.
	 * @param x is the double[] array to find the maximum.
	 * @return the maximum value of x.
	 */
	public static float max(float[] x) {
		float maxVal = x[0];
		for (int i = 0; i < x.length; i++) {
			if (x[i] > maxVal) maxVal = x[i];
		}
		return maxVal;
	}

	/**
	 * Finds the maximum value in a 2-dimension array.
	 * @param x is the double[][] array to find the maximum.
	 * @return the maximum value of x.
	 */
	public static float max(float[][] x) {
		float maxVal = x[0][0];
		for (int i = 0; i < x.length; i++) {
			for (int j = 0; j < x[0].length; j++) {
				if (x[i][j] > maxVal) maxVal = x[i][j];
			}
		}
		return maxVal;
	}

	public final static float max(float x, float y) {
		return (x >= y) ? x : y;
	}
	
	/**
	 * Finds the minimum value in a 1-dimension array.
	 * @param x is the int[] array to find the minimum.
	 * @return the minimum value of x.
	 */
	public static double min(int[] x) {
		double minVal = x[0];
		for (int i = 0; i < x.length; i++) {
			minVal = x[i] < minVal ? x[i] : minVal;
		}
		return minVal;
	}
	
	/**
	 * Finds the minimum value in a 2-dimension array.
	 * @param x is the int[][] array to find the minimum.
	 * @return the minimum value of x.
	 */
	public static double min(int[][] x) {
		double minVal = x[0][0];
		for (int i = 0; i < x[0].length; i++) {
			for (int j = 0; j < x.length; j++) {
				minVal = x[j][i] < minVal ? x[j][i] : minVal;
			}
		}
		return minVal;
	}

	/**
	 * Finds the minimum value in a 1-dimension array.
	 * @param x is the double[] array to find the minimum.
	 * @return the minimum value of x.
	 */
	public static double min(double[] x) {
		double minVal = x[0];
		for (int i = 0; i < x.length; i++) {
			minVal = x[i] < minVal ? x[i] : minVal;
		}
		return minVal;
	}

	/**
	 * Finds the minimum value in a 2-dimension array.
	 * @param x is the double[][] array to find the minimum.
	 * @return the minimum value of x.
	 */
	public static double min(double[][] x) {
		double minVal = x[0][0];
		for (int i = 0; i < x[0].length; i++) {
			for (int j = 0; j < x.length; j++) {
				minVal = x[j][i] < minVal ? x[j][i] : minVal;
			}
		}
		return minVal;
	}
	
	public final static float min(float x, float y) {
		return (x <= y) ? x : y;
	}

	/**
	 * Finds the minimum value in a a-dimension array.
	 * @param x is the double[] array to find the minimum.
	 * @return the minimum value of x.
	 * 
	 */
	public final static float min(float[] x) {
		float minVal = x[0];
		for (int i = 0; i < x.length; i++) {
			if (x[i] > minVal) minVal = x[i];
		}
		return minVal;
	}
	
	
	
	/**
	 * Finds the minimum value in a 2-dimension array.
	 * @param x is the double[][] array to find the minimum.
	 * @return the minimum value of x.
	 */
	public final static float min(float[][] x) {
		float minVal = x[0][0];
		for (int i = 0; i < x.length; i++) {
			for (int j = 0; j < x[0].length; j++) {
				if (x[i][j] < minVal) minVal = x[i][j];
			}
		}
		return minVal;
	}
	
	/**
	 * returns the minimum value(s) as computed column-wise, row-wise, or over
	 * the entire data set as determined by the value of computationType
	 * For ROW_WISE_COMPUTATION and WHOLE_DATA_SET_COMPUTATION, x may be jagged
	 * @param x
	 * @param computationType
	 * @return
	 */
	public static double[] min(double[][] x, int computationType) {
		double[] y;
		int rows = x.length;
		int cols;
		double min;
		switch(computationType) {
		case COLUMN_WISE_COMPUTATION : 
			cols = x[0].length;
			y = new double[cols];
			for(int i = 0; i<cols; i++) {
				min = x[0][i];
				for(int j=1; j<rows; j++) {
					min = x[j][i] < min ? x[j][i] : min;
				}
				y[i]=min;
			}
			return y;
			
		case ROW_WISE_COMPUTATION :
			y = new double[x.length];
			for(int i = 0; i<rows; i++) {
				min = x[i][0];
				for(int j=0; j<x[i].length; j++) {
					min = x[i][j] < min ? x[i][j] : min;
				}
				y[i]=min;
			}
			return y;
			
		case WHOLE_DATA_SET_COMPUTATION : 
			return new double[] {MoreMath.min(x)};
		}
		return null;
	}
	
	/**
	 * returns the minimum value(s) as computed column-wise, row-wise, or over
	 * the entire data set as determined by the value of computationType
	 * For ROW_WISE_COMPUTATION and WHOLE_DATA_SET_COMPUTATION, x may be jagged
	 * @param x
	 * @param computationType
	 * @return
	 */
	public static double[] min(float[][] x, int computationType) {
		double[] y;
		int rows = x.length;
		int cols;
		double min;
		switch(computationType) {
		case COLUMN_WISE_COMPUTATION : 
			cols = x[0].length;
			y = new double[cols];
			for(int i = 0; i<cols; i++) {
				min = x[0][i];
				for(int j=1; j<rows; j++) {
					min = x[j][i] < min ? x[j][i] : min;
				}
				y[i]=min;
			}
			return y;
			
		case ROW_WISE_COMPUTATION :
			y = new double[x.length];
			for(int i = 0; i<rows; i++) {
				min = x[i][0];
				for(int j=0; j<x[i].length; j++) {
					min = x[i][j] < min ? x[i][j] : min;
				}
				y[i]=min;
			}
			return y;
			
		case WHOLE_DATA_SET_COMPUTATION : 
			return new double[] {MoreMath.min(x)};
		}
		return null;
	}

	/**
	 * returns the minimum value(s) as computed column-wise, row-wise, or over
	 * the entire data set as determined by the value of computationType
	 * For ROW_WISE_COMPUTATION and WHOLE_DATA_SET_COMPUTATION, x may be jagged
	 * @param x
	 * @param computationType
	 * @return
	 */
	public static double[] min(int[][] x, int computationType) {
		double[] y;
		int rows = x.length;
		int cols;
		double min;
		switch(computationType) {
		case COLUMN_WISE_COMPUTATION : 
			cols = x[0].length;
			y = new double[cols];
			for(int i = 0; i<cols; i++) {
				min = x[0][i];
				for(int j=1; j<rows; j++) {
					min = x[j][i] < min ? x[j][i] : min;
				}
				y[i]=min;
			}
			return y;
			
		case ROW_WISE_COMPUTATION :
			y = new double[x.length];
			for(int i = 0; i<rows; i++) {
				min = x[i][0];
				for(int j=0; j<x[i].length; j++) {
					min = x[i][j] < min ? x[i][j] : min;
				}
				y[i]=min;
			}
			return y;
			
		case WHOLE_DATA_SET_COMPUTATION : 
			return new double[] {MoreMath.min(x)};
		}
		return null;
	}

	public final static float[] minMax(int i0, int j0, int i1, int j1, float[][] x) {
		int i, j;
		float[] minMax = new float[] { Float.POSITIVE_INFINITY, Float.NEGATIVE_INFINITY };
		for (j = j0; j <= j1; j++) {
			for (i = i0; i <= i1; i++) {
				if (x[i][j] < minMax[0]) minMax[0] = x[i][j];
				else if (x[i][j] > minMax[1]) minMax[1] = x[i][j];
			}
		}
		return minMax;
	}

	public final static double[] minMax(double[] x) {
		double[] minMax = new double[2];
		minMax[0] = minMax[1] = x[0];
		for (int i = 1; i < x.length; i++) {
			minMax[0] = x[i] < minMax[0] ? x[i] : minMax[0];
			minMax[1] = x[i] > minMax[1] ? x[i] : minMax[1];
		}
		return minMax;
	}

	public final static float[] minMax(float[][] x) {
		int i, j;
		float[] minMax = new float[] { Float.POSITIVE_INFINITY, Float.NEGATIVE_INFINITY };
		for (j = 0; j < x[0].length; j++) {
			for (i = 0; i < x.length; i++) {
				if (x[i][j] < minMax[0]) minMax[0] = x[i][j];
				else if (x[i][j] > minMax[1]) minMax[1] = x[i][j];
			}
		}
		return minMax;
	}

	public static double[] minMax(double[][] x) {
		int i, j;
		double[] minMax = new double[] { Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY };
		for (j = 0; j < x[0].length; j++) {
			for (i = 0; i < x.length; i++) {
				if (x[i][j] < minMax[0]) minMax[0] = x[i][j];
				else if (x[i][j] > minMax[1]) minMax[1] = x[i][j];
			}
		}
		return minMax;
	}

	public final static double mod(double x, double y) {
		double n = Math.floor(x / y);
		return x - n * y;
	}

	/**
	 * Logarith with Base 10.
	 * @param x is the double value of which to take the logarithm
	 * @return Returns double value.
	 */
	public static double log10(double x) {
		return Math.log(x) / 2.302585092994046;
	}

	/**
	 * Logarith with Base 10.
	 * @param x is the float value of which to take the logarithm
	 * @return Returns float value.
	 */
	public static float log10(float x) {
		return (float) Math.log(x) / 2.302585092994046f;
	}

	/**
	 * Logarith with Base a
	 * @param x
	 * @param a
	 * @return
	 */
	public static float logBaseA(float x, int a) {
		return (float) (Math.log(x) / Math.log((double) a));
	}

	/**
	 * Logarith with Base a
	 * @param x
	 * @param a
	 * @return
	 */
	public static double logBaseA(double x, int a) {
		return (Math.log(x) / Math.log((double) a));
	}

	/**
	 * Checks if given value is an integer
	 */
	public static boolean isInteger(double x) {
		return (x == (int) x);
	}

	/**
	 * Sign Function
	 * @param x
	 * @return -1 if x<0, else it returns +1.
	 */
	public static double sign(double x) {
		double y = 0.0;
		if (x < 0.0) y = -1.0;
		else y = 1.0;

		return y;
	}

	public static double[] centroid(double[][] p) {
		double centroidX = 0.0;
		double centroidY = 0.0;
		double totalPower = 0.0;

		int nx = p.length;
		int ny = p[0].length;

		double gridCenterX = nx / 2.0;
		double gridCenterY = ny / 2.0;

		for (int x = 0; x < nx; x++) {
			for (int y = 0; y < ny; y++) {
				centroidX = +(x - gridCenterX) * p[x][y];
				centroidY = +(y - gridCenterY) * p[x][y];
				totalPower = +p[x][y];
			}
		}
		if (totalPower > 0.0) {
			centroidX = centroidX / totalPower;
			centroidY = centroidY / totalPower;
		}
		;
		double[] centroidValue = { centroidX, centroidY };
		return centroidValue;
	}

	public static float[] centroid(float[][] p) {
		float centroidX = 0.0f;
		float centroidY = 0.0f;
		float totalPower = 0.0f;

		int nx = p.length;
		int ny = p[0].length;

		float gridCenterX = nx / 2.0f;
		float gridCenterY = ny / 2.0f;

		for (int x = 0; x < nx; x++) {
			for (int y = 0; y < ny; y++) {
				centroidX = +(x - gridCenterX) * p[x][y];
				centroidY = +(y - gridCenterY) * p[x][y];
				totalPower = +p[x][y];
			}
		}
		if (totalPower > 0.0) {
			centroidX = centroidX / totalPower;
			centroidY = centroidY / totalPower;
		}
		;
		float[] centroidValue = { centroidX, centroidY };
		return centroidValue;
	}

	/**
	 *  sqrt(a^2 + b^2) without under/overflow. 
	 *
	 */
	public final static double hypot(double a, double b) {
		double r;
		if (Math.abs(a) > Math.abs(b)) {
			r = b / a;
			r = Math.abs(a) * Math.sqrt(1 + r * r);
		} else if (b != 0) {
			r = a / b;
			r = Math.abs(b) * Math.sqrt(1 + r * r);
		} else {
			r = 0.0;
		}
		return r;
	}
}
