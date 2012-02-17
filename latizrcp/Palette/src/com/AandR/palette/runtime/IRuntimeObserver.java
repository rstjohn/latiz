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
package com.AandR.palette.runtime;

/**
 *
 * @author Aaron Masino
 */
public interface IRuntimeObserver {

    /**
     * notify observers the run has failed
     * @param e
     */
    public void notifyRuntimeFailure(Exception e);

    /**
     * update the run number. This occurs when looping over parameters in a run.
     * @param runNumber
     */
    public void setCurrentRunNumber(int runNumber);

    /**
     * called immeadiately before the run is executed by the runtime manager, after all AbstractPlugins have been initialized
     * @param modelName
     */
    public void setUpRun(IRuntimeManager iRuntimeManager);

    /**
     * called immeadiately after the run is completed, after all AbstractPlugins have been deinitialzied
     * @param modelName
     */
    public void tearDownRun(IRuntimeManager iRuntimeManager);
}
