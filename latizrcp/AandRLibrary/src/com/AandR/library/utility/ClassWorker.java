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

import java.util.HashMap;

/**
 *
 * @author Aaron Masino
 */
public class ClassWorker {

    private static final HashMap<Class, Class> converterMap = createPrimativeClassesConverterMap();

    public static final HashMap<Class, Class> createPrimativeClassesConverterMap() {
    HashMap<Class, Class> map = new HashMap<Class, Class>();
    map.put(double.class, Double.class);
    map.put(int.class, Integer.class);
    map.put(float.class, Float.class);
    map.put(byte.class, Byte.class);
    map.put(short.class, Short.class);
    map.put(long.class, Long.class);
    map.put(boolean.class, Boolean.class);
    map.put(char.class, Character.class);
    return map;
  }

  @SuppressWarnings("unchecked")
  public static final boolean checkClassAssignAbility(Class assignFromClass, Class assignToClass) {
    assignFromClass = converterMap.get(assignFromClass) == null ? assignFromClass : converterMap.get(assignFromClass);
    assignToClass = converterMap.get(assignToClass) == null ? assignToClass : converterMap.get(assignToClass);

    if (assignToClass.isAssignableFrom(assignFromClass))
      return true;
    else
      return false;

  }

}
