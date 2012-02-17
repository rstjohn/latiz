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

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 *
 * @author Aaron Masino
 */
public class CloneWorker {

    public static Object cloneObject(Object o) throws IOException, ClassNotFoundException {
    return deserializeObject(serializeObject(o));
  }


  public static Object deserializeObject(FastByteArrayOutputStream fbos) throws IOException, ClassNotFoundException {
		Object o;
		ObjectInputStream in = new ObjectInputStream(fbos.getInputStream());
		o = in.readObject();
		in.close();
		return o;
  }

  public static FastByteArrayOutputStream serializeObject(Object orig) throws IOException {
		  // Write the object out to a byte array
		  FastByteArrayOutputStream fbos = new FastByteArrayOutputStream();
		  ObjectOutputStream out = new ObjectOutputStream(fbos);
		  out.writeObject(orig);
		  out.flush();
		  out.close();
		  return fbos;
  }

}
