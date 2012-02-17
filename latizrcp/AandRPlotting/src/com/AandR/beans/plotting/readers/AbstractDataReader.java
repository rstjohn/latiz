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
package com.AandR.beans.plotting.readers;

import com.AandR.beans.plotting.dataExplorer.DataExplorerInterface;
import java.io.File;

import javax.swing.Icon;
import javax.swing.JComponent;

import com.AandR.library.gui.DropEvent;

/**
 * @author  <a href="mailto://rstjohn@mza.com">Dr. Richard St. John</a>
 * @company <a href="http://www.mza.com">MZA Associates Corporation</a>
 */
public abstract class AbstractDataReader {

    protected DataExplorerInterface dataExplorerInterface;
    protected File file;
    protected Icon icon;

    /**
     * Perform any required activities associated with response to a file being dropped on the LatFileExplorer panel's file field
     * @param file
     * @throws DataReaderException
     */
    public abstract void initialize(File file) throws DataReaderException;

    /**
     * Called when a node in this reader's tree is dropped onto the LatFileExplorer's plot viewport.
     * Returned JComponent is displayed in an internal frame in the LatFileExplorer's plot viewport.
     * @param event
     * @throws DataReaderException
     */
    public abstract void acknowledgePlotRequested(DropEvent event) throws DataReaderException;

    public abstract JComponent getParameterPanel();

    public DataExplorerInterface getDataExplorerInterface() {
        return dataExplorerInterface;
    }

    public void setDataExplorerInterface(DataExplorerInterface dataExplorerInterface) {
        this.dataExplorerInterface = dataExplorerInterface;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public Icon getIcon() {
        return icon;
    }

    public void setIcon(Icon icon) {
        this.icon = icon;
    }

    @Override
    public String toString() {
        return file.getName();
    }
}
