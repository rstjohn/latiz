package com.AandR.latiz.dev;

import com.AandR.beans.latFileExplorerPanel.LatFileConstants;

/**
 * @author Dr. Richard St. John
 * @version $Revision$, $Date$
 */
abstract public class LatDataObject {

    public static final int APPEND = 0;
    public static final int OVERWRITE = 1;
    protected int dataType, nx, ny, writeMode;
    protected double dx, dy;
    protected String groupName;

    abstract public Object getData();

    public LatDataObject() {
        dx = dy = 1;
        groupName = null;
        dataType = LatFileConstants.ARRAY_DOUBLE;
        writeMode = APPEND;
    }

    public int getDataType() {
        return dataType;
    }

    /**
     * Use constants from LatFileConstants to specify the data type.
     * @param dataType
     */
    public void setDataType(int dataType) {
        this.dataType = dataType;
    }

    public int getNx() {
        return nx;
    }

    public void setNx(int nx) {
        this.nx = nx;
    }

    public int getNy() {
        return ny;
    }

    public void setNy(int ny) {
        this.ny = ny;
    }

    public double getDx() {
        return dx;
    }

    public void setDx(double dx) {
        this.dx = dx;
    }

    public double getDy() {
        return dy;
    }

    public void setDy(double dy) {
        this.dy = dy;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public int getWriteMode() {
        return writeMode;
    }

    public void setWriteMode(int writeMode) {
        this.writeMode = writeMode;
    }
}
