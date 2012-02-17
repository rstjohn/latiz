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
package com.AandR.palette.plugin.data;

import com.AandR.palette.plugin.data.Input;
import java.util.Comparator;

/**
 *
 * @author Aaron Masino
 */
public class InputComparator implements Comparator<Input>{
    public int compare(Input inOne, Input inTwo) {
		String keyOne = inOne.getInputPlugin().getName()+"->"+inOne.getKey();
		String keyTwo = inTwo.getInputPlugin().getName()+"->"+inTwo.getKey();
		return keyOne.compareTo(keyTwo);
	}

}
