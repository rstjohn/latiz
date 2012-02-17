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
package com.AandR.palette.globals;

import java.lang.reflect.InvocationTargetException;
import org.openide.nodes.PropertySupport;

public class ValueProperty extends PropertySupport.ReadWrite<String> {
    private final GlobalVariable globalVariable;

    public ValueProperty() {
        super("Value", String.class, "Value", "Value Details");
        this.globalVariable = null;
    }

    public ValueProperty(GlobalVariable globalVariable) {
        super("Value", String.class, "Value", "Value Details");
        setValue("suppressCustomEditor", Boolean.TRUE);
        this.globalVariable = globalVariable;
    }

    @Override
    public String getValue() throws IllegalAccessException, InvocationTargetException {
        return globalVariable.getValue();
    }

    @Override
    public void setValue(String value) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        globalVariable.setValue(value);
    }
}