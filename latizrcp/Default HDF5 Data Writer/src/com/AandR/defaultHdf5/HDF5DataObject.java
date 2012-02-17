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
package com.AandR.defaultHdf5;

import com.AandR.defaultHdf5.HDF5DataConstants;

/**
 *
 * @author rstjohn
 */
abstract public class HDF5DataObject {

    /**Flag to set write mode to append.*/
    public static final int APPEND = 0;
    /**Flag to set write mode to overwrite.*/
    public static final int OVERWRITE = 1;
    protected int dataType,  nx,  ny,  writeMode;
    protected double dx,  dy;
    protected String groupName;

    abstract public Object getData();

    public HDF5DataObject() {
        dx = dy = 1;
        groupName = null;
        dataType = HDF5DataConstants.ARRAY_DOUBLE;
        writeMode = APPEND;
    }

    /**
     * {@link com.AandR.beans.plotting.latExplorer.HDF5DataConstants}
     * @return Flag that defines the type of data.
     */
    public int getDataType() {
        return dataType;
    }

    /**
     * Use constants from HDF5DataConstants to specify the data type. Values can be one of the values in
     * {@link com.AandR.beans.plotting.latExplorer.HDF5DataConstants} HDF5DataConstants.
     * @param dataType The data type.
     */
    public void setDataType(int dataType) {
        this.dataType = dataType;
    }

    /**
     * Number of data points in the x-direction.
     * @return The number of data point in the first rank of the data array.
     */
    public int getNx() {
        return nx;
    }

    /**
     * Number of data points in the x-direction.
     * @param nx The number of data point in the first rank of the data array.
     */
    public void setNx(int nx) {
        this.nx = nx;
    }

    /**
     * Number of data points in the y-direction.
     * @return The number of data point in the second rank of the data array.
     */
    public int getNy() {
        return ny;
    }

    /**
     *
     * Number of data points in the y-direction.
     * @param ny The number of data point in the second rank of the data array.
     */
    public void setNy(int ny) {
        this.ny = ny;
    }

    /**
     * The grid-point spacing.
     * @return The grid-point spacing in the x-direction.
     */
    public double getDx() {
        return dx;
    }

    /**
     * The grid-point spacing.
     * @param dx The grid-point spacing in the x-direction.
     */
    public void setDx(double dx) {
        this.dx = dx;
    }

    /**
     * The grid-point spacing.
     * @return The grid-point spacing in the y-direction.
     */
    public double getDy() {
        return dy;
    }

    /**
     * The grid-point spacing.
     * @param dy The grid-point spacing in the y-direction.
     */
    public void setDy(double dy) {
        this.dy = dy;
    }

    /**
     * Returns {@link #APPEND} or {@link #OVERWRITE}
     * @return The write mode.
     */
    public int getWriteMode() {
        return writeMode;
    }

    /**
     * Use either {@link #APPEND} or {@link #OVERWRITE}
     * @param writeMode The write mode.
     */
    public void setWriteMode(int writeMode) {
        this.writeMode = writeMode;
    }
}
