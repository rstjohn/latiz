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

/**
 *
 * @author Aaron Masino
 */
public class FastByteArrayInputStream extends InputStream{
    /**
   * Our byte buffer
   */
  protected byte[] buf = null;


  /**
   * Number of bytes that we can read from the buffer
   */
  protected int count = 0;


  /**
   * Number of bytes that have been read from the buffer
   */
  protected int pos = 0;


  /**
   *
   * @param buf
   * @param count
   */
  public FastByteArrayInputStream(byte[] buf, int count) {
    this.buf = buf;
    this.count = count;
  }


    @Override
  public final int available() {
    return count - pos;
  }


  public final int read() {
    return (pos < count) ? (buf[pos++] & 0xff) : -1;
  }


    @Override
  public final int read(byte[] b, int off, int len) {
    if (pos >= count)
      return -1;

    if ((pos + len) > count)
      len = (count - pos);

    System.arraycopy(buf, pos, b, off, len);
    pos += len;
    return len;
  }


    @Override
  public final long skip(long n) {
    if ((pos + n) > count)
      n = count - pos;
    if (n < 0)
      return 0;
    pos += n;
    return n;
  }

}
