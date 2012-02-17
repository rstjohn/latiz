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
package com.AandR.palette.dataWriter;

import com.AandR.palette.plugin.data.IOutputObserver;
import com.AandR.palette.model.AbstractPaletteModel;
import javax.swing.JComponent;
import org.jdom.Element;

/**
 *
 * @author Aaron Masino
 */
public interface AbstractDataWriter extends IOutputObserver{

    /**
     * called before simulation run is started. The dataWriter is expected to register with the selected observer in the HashMap found in the model,
     * though this is not enforced
     * @param model
     */
    abstract public void registerOutputObservation(AbstractPaletteModel model);

    /**
     * called just before the simulation run is started
     */
    abstract public void setUp();

    /**
     * called after a simulation run is completed
     */
    abstract public void tearDown();

    /**
     * return the GUI component used to change data writer settings
     * @return
     */
    abstract public JComponent getParameterPanel();

    /**
	 * Used to load the parameter inputs as stored in XML from a previously
	 * saved Latiz workspace
	 *
	 * @param e
	 */
	abstract public void loadSavedWorkspaceParameters(Element e);

	/**
	 * Used by Latiz to get a savable XML element with the parameter inputs
	 *
	 * @return e
	 */
	abstract public Element createWorkspaceParameters();

}
