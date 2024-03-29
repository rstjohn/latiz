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
package com.AandR.library.utility;

import java.io.InputStream;
import java.io.OutputStream;

/**
 *
 * @author Aaron Masino
 */
public class FastByteArrayOutputStream extends OutputStream{

     /**
   * Buffer and size
   */
  protected byte[] buf = null;

  protected int size = 0;


  /**
   * Constructs a stream with buffer capacity size 5K
   */
  public FastByteArrayOutputStream() {
    this(5 * 1024);
  }


  /**
   * Constructs a stream with the given initial size
   */
  public FastByteArrayOutputStream(int initSize) {
    this.size = 0;
    this.buf = new byte[initSize];
  }


  /**
   * Ensures that we have a large enough buffer for the given size.
   */
  private void verifyBufferSize(int sz) {
    if (sz > buf.length) {
      byte[] old = buf;
      buf = new byte[Math.max(sz, 2 * buf.length)];
      System.arraycopy(old, 0, buf, 0, old.length);
      old = null;
    }
  }


  public int getSize() {
    return size;
  }


  /**
   * Returns the byte array containing the written data. Note that this array will almost always be larger than the amount of data actually written.
   */
  public byte[] getByteArray() {
    return buf;
  }


    @Override
  public final void write(byte b[]) {
    verifyBufferSize(size + b.length);
    System.arraycopy(b, 0, buf, size, b.length);
    size += b.length;
  }


    @Override
  public final void write(byte b[], int off, int len) {
    verifyBufferSize(size + len);
    System.arraycopy(b, off, buf, size, len);
    size += len;
  }


  public final void write(int b) {
    verifyBufferSize(size + 1);
    buf[size++] = (byte) b;
  }


  public void reset() {
    size = 0;
  }


  /**
   * Returns a ByteArrayInputStream for reading back the written data
   */
  public InputStream getInputStream() {
    return new FastByteArrayInputStream(buf, size);
  }

}
