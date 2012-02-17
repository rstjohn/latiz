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
package com.AandR.recordedOutputs.properties;

import com.AandR.recordedOutputs.nodes.OutputDataObject;
import java.lang.reflect.InvocationTargetException;
import org.openide.nodes.PropertySupport;

/**
 *
 * @author Aaron Masino
 */
public class EndTimeProperty extends PropertySupport.ReadWrite<Double> {

    private OutputDataObject outputData;

    public EndTimeProperty() {
        super("End Time", Double.class, "End Time", "Creates a variable alias");
    }

    public EndTimeProperty(OutputDataObject variable) {
        this();
        this.outputData = variable;
    }

    public Double getValue() throws IllegalAccessException, InvocationTargetException {
        return outputData.getEndTime();
    }

    @Override
    public void setValue(Double value) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        outputData.setEndTime(value);
    }
}
