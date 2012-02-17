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
package com.latizTech.hdf5Panel.propertyEditors;

import com.latizTech.hdf5Panel.LatFileConstants;
import java.lang.reflect.InvocationTargetException;
import org.openide.nodes.PropertySupport;

/**
 *
 * @author rstjohn
 */
public class DataTypeProperty extends PropertySupport.ReadOnly<String> {

    private String dataType;

    public DataTypeProperty(int dataType) {
        super("DataType", String.class, "DataType", "Data type.");
        this.dataType = getDataTypeFor(dataType);
    }

    @Override
    public String getValue() throws IllegalAccessException, InvocationTargetException {
        return dataType;
    }

    private String getDataTypeFor(int dataType) {
        switch(dataType) {
            case LatFileConstants.ARRAY_COMPLEX:
                return "Array complex";
            case LatFileConstants.ARRAY_DOUBLE:
                return "Array double";
            case LatFileConstants.ARRAY_FLOAT:
                return "Array float";
            case LatFileConstants.ARRAY_LONG:
                return "Array long";
            case LatFileConstants.ARRAY_INT:
                return "Array int";
        }
        return null;
    }
}
