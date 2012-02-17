package com.AandR.latiz.dev;

import com.AandR.beans.latFileExplorerPanel.LatFileConstants;

/**
 * @author Dr. Richard St. John
 * @version $Revision$, $Date$
 */
public class DefaultLatData extends LatDataObject {

    private Object data;

    public DefaultLatData(Object data) {
        this(data, 1.0, 1.0);
    }

    public DefaultLatData(Object data, double dx, double dy) {
        super();
        this.data = data;
        this.dx = dx;
        this.dy = dy;
        if (data instanceof double[]) {
            dataType = LatFileConstants.VECTOR_DOUBLE;
            nx = 1;
            ny = ((double[]) data).length;
        } else if (data instanceof double[][]) {
            dataType = LatFileConstants.ARRAY_DOUBLE;
            nx = ((double[][]) data).length;
            ny = ((double[][]) data)[0].length;
        } else if (data instanceof float[]) {
            dataType = LatFileConstants.VECTOR_FLOAT;
            nx = 1;
            ny = ((float[]) data).length;
        } else if (data instanceof float[][]) {
            dataType = LatFileConstants.ARRAY_FLOAT;
            nx = ((float[][]) data).length;
            ny = ((float[][]) data)[0].length;
        } else if (data instanceof int[]) {
            dataType = LatFileConstants.VECTOR_INT;
            nx = 1;
            ny = ((int[]) data).length;
        } else if (data instanceof int[][]) {
            dataType = LatFileConstants.ARRAY_INT;
            nx = ((int[][]) data).length;
            ny = ((int[][]) data)[0].length;
        } else if (data instanceof Integer) {
            dataType = LatFileConstants.INT;
            nx = ny = 1;
        } else if (data instanceof Float) {
            dataType = LatFileConstants.FLOAT;
            nx = ny = 1;
        } else if (data instanceof Double) {
            dataType = LatFileConstants.DOUBLE;
            nx = ny = 1;
        }
    }

    public Object getData() {
        return data;
    }
}
