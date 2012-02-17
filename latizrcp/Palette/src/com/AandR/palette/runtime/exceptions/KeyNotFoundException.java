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
package com.AandR.palette.runtime.exceptions;

import com.AandR.palette.plugin.AbstractPlugin;

/**
 *
 * @author Aaron Masino
 */
public class KeyNotFoundException extends InputOutputException{
    public KeyNotFoundException(AbstractPlugin p, String key) {
		super(p,key);
	}

	public KeyNotFoundException(AbstractPlugin p, String key, String message) {
		super(p,key,message);
	}

}
