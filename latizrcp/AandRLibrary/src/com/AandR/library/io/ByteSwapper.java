package com.AandR.library.io;

/*
 * (C) 2004 - Geotechnical Software Services
 * 
 * This code is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public 
 * License as published by the Free Software Foundation; either 
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This code is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public 
 * License along with this program; if not, write to the Free 
 * Software Foundation, Inc., 59 Temple Place - Suite 330, Boston, 
 * MA  02111-1307, USA.
 */

/**
 * Utility class for doing byte swapping (i.e. conversion between
 * little-endian and big-endian representations) of different data types.
 * Byte swapping is typically used when data is read from a stream 
 * delivered by a system of different endian type as the present one.
 * 
 * @author <a href="mailto:jacob.dreyer@geosoft.no">Jacob Dreyer</a>
 * @author Dr. Richard St. John
 * @version $Revision: 1.2 $, $Date: 2007/06/12 13:57:18 $
 */
public class ByteSwapper {

  /**
   * Byte swap a single short value.
   * 
   * @param value  Value to byte swap.
   * @return       Byte swapped representation.
   */
  public static short swap(short value) {
    int b1 = value & 0xff;
    int b2 = (value >> 8) & 0xff;

    return (short) (b1 << 8 | b2 << 0);
  }

  /**
   * Byte swap a single int value.
   * 
   * @param value  Value to byte swap.
   * @return       Byte swapped representation.
   */
  public static int swap(int value) {
    int b1 = (value >> 0) & 0xff;
    int b2 = (value >> 8) & 0xff;
    int b3 = (value >> 16) & 0xff;
    int b4 = (value >> 24) & 0xff;

    return b1 << 24 | b2 << 16 | b3 << 8 | b4 << 0;
  }

  /**
   * Byte swap a single long value.
   * 
   * @param value  Value to byte swap.
   * @return       Byte swapped representation.
   */
  public static long swap(long value) {
    long b1 = (value >> 0) & 0xff;
    long b2 = (value >> 8) & 0xff;
    long b3 = (value >> 16) & 0xff;
    long b4 = (value >> 24) & 0xff;
    long b5 = (value >> 32) & 0xff;
    long b6 = (value >> 40) & 0xff;
    long b7 = (value >> 48) & 0xff;
    long b8 = (value >> 56) & 0xff;

    return b1 << 56 | b2 << 48 | b3 << 40 | b4 << 32 | b5 << 24 | b6 << 16 | b7 << 8 | b8 << 0;
  }

  /**
   * Byte swap a single float value.
   * 
   * @param value  Value to byte swap.
   * @return       Byte swapped representation.
   */
  public static float swap(float value) {
    int intValue = Float.floatToRawIntBits(value);
    intValue = swap(intValue);
    return Float.intBitsToFloat(intValue);
  }

  /**
   * Byte swap a single double value.
   * 
   * @param value  Value to byte swap.
   * @return       Byte swapped representation.
   */
  public static double swap(double value) {
    long longValue = Double.doubleToRawLongBits(value);
    longValue = swap(longValue);
    return Double.longBitsToDouble(longValue);
  }

  /**
   * Byte swap an array of shorts. The result of the swapping
   * is put back into the specified array.
   *
   * @param array  Array of values to swap
   */
  public static void swap(short[] array) {
    for (int i = 0; i < array.length; i++)
      array[i] = swap(array[i]);
  }

  /**
   * Byte swap an array of ints. The result of the swapping
   * is put back into the specified array.
   * 
   * @param array  Array of values to swap
   */
  public static void swap(int[] array) {
    for (int i = 0; i < array.length; i++)
      array[i] = swap(array[i]);
  }

  /**
   * Byte swap an array of longs. The result of the swapping
   * is put back into the specified array.
   * 
   * @param array  Array of values to swap
   */
  public static void swap(long[] array) {
    for (int i = 0; i < array.length; i++)
      array[i] = swap(array[i]);
  }

  /**
   * Byte swap an array of floats. The result of the swapping
   * is put back into the specified array.
   * 
   * @param array  Array of values to swap
   */
  public static void swap(float[] array) {
    for (int i = 0; i < array.length; i++)
      array[i] = swap(array[i]);
  }

  /**
   * Byte swap an array of doubles. The result of the swapping
   * is put back into the specified array.
   * 
   * @param array  Array of values to swap
   */
  public static void swap(double[] array) {
    for (int i = 0; i < array.length; i++)
      array[i] = swap(array[i]);
  }

  /**
   * Byte swap a 2-rank array, double[][]. The result of the swapping
   * is put back into the specified array.
   * 
   * @param array  Array of values to swap
   */
  public static void swap(double[][] array) {
    for (int j = 0; j < array[0].length; j++) {
      for(int i=0; i<array.length; i++) {
        array[i][j] = swap(array[i][j]);
      }
    }
  }

  /**
   * Byte swap a 2-rank array, float[][]. The result of the swapping
   * is put back into the specified array.
   * 
   * @param array  Array of values to swap
   */
  public static void swap(float[][] array) {
    for (int j = 0; j < array[0].length; j++) {
      for(int i=0; i<array.length; i++) {
        array[i][j] = swap(array[i][j]);
      }
    }
  }

  /**
   * Byte swap a 2-rank array, int[][]. The result of the swapping
   * is put back into the specified array.
   * 
   * @param array  Array of values to swap
   */
  public static void swap(int[][] array) {
    for (int j = 0; j < array[0].length; j++) {
      for(int i=0; i<array.length; i++) {
        array[i][j] = swap(array[i][j]);
      }
    }
  }
  
  public static double bytesToDouble(byte[] data, int dataIndexOffset) {
    return Double.longBitsToDouble(bytesToLong(data, dataIndexOffset));
  }
  
  /**
   * 
   * @param data 
   * @param dataIndexOffset 
   * @return 
   */
  public static float bytesToFloat(byte[] data, int dataIndexOffset) {        
    return Float.intBitsToFloat(bytesToInt(data,dataIndexOffset));
  } 
  
  /**
   * 
   * @param data The byte array from which to get the integer data.
   * @param dataIndexOffset The index at which to start reading byte information.
   * @return 
   */
  public static int bytesToInt(byte[] data, int dataIndexOffset) {        
    int i = 0, j = 4*dataIndexOffset;
    i += (data[j++] & 0xFF) << 0;
    i += (data[j++] & 0xFF) << 8;
    i += (data[j++] & 0xFF) << 16;
    i += (data[j] & 0xFF) << 24;
    return i;
  } 
  
  /**
   * 
   * @param data The byte array from which to get the integer data.
   * @param dataIndexOffset The index at which to start reading byte information.
   * @return 
   */
  public static long bytesToLong(byte[] data, int dataIndexOffset) {        
    long i = 0;
    int  j = 8*dataIndexOffset;
    i += (data[j++] & 0xFF) << 0;
    i += (data[j++] & 0xFF) << 8;
    i += (data[j++] & 0xFF) << 16;
    i += (data[j++] & 0xFF) << 24;
    i += (data[j++] & 0xFF) << 32;
    i += (data[j++] & 0xFF) << 40;
    i += (data[j++] & 0xFF) << 48;
    i += (data[j]   & 0xFF) << 56;
    return i;
  } 
  
  public static byte[] intToByteArray(int value) {
    value = ByteSwapper.swap(value);
    byte[] bytes = new byte[4];
    for( int i=0; i<bytes.length; ++i ) {
      int off = (bytes.length-i-1)*8;
      bytes[i] = (byte) ((value & (0xff << off)) >>> off);
    }    
    return bytes;
  }
  
  /**
   * 
   * @param data 
   * @param dataOffset 
   * @param nx 
   * @param ny 
   */
  public static double[][] bytesToFloatArray(byte[] data, int dataIndexOffset, int nx, int ny) {
    int x,y;
    double[][] out = new double[nx][ny];
    for(y=0; y<ny; y++) {
      for(x=0; x<nx; x++) {
        out[x][y] = bytesToFloat(data, dataIndexOffset++);
      }
    }
    return out;
  }
  
}
