package com.AandR.latiz.dev;

import com.AandR.beans.latFileExplorerPanel.LatFileConstants;

/**
 * @author Dr. Richard St. John
 * @version $Revision$, $Date$
 */
public class ComplexLatData extends LatDataObject {

    private Object data;

    public ComplexLatData(Object data, double dx, double dy) {
        super();
        this.data = data;
        this.dx = dx;
        this.dy = dy;
        if (data instanceof double[]) {
            dataType = LatFileConstants.VECTOR_COMPLEX;
            nx = 1;
            ny = ((double[]) data).length;
        } else if (data instanceof double[][]) {
            dataType = LatFileConstants.ARRAY_COMPLEX;
            nx = ((double[][]) data).length;
            ny = ((double[][]) data)[0].length;
        } else if (data instanceof float[]) {
            dataType = LatFileConstants.VECTOR_COMPLEX;
            nx = 1;
            ny = ((float[]) data).length;
        } else if (data instanceof float[][]) {
            dataType = LatFileConstants.ARRAY_COMPLEX;
            nx = ((float[][]) data).length;
            ny = ((float[][]) data)[0].length;
        } else if (data instanceof int[]) {
            dataType = LatFileConstants.VECTOR_COMPLEX;
            nx = 1;
            ny = ((int[]) data).length;
        } else if (data instanceof int[][]) {
            dataType = LatFileConstants.ARRAY_COMPLEX;
            nx = ((int[][]) data).length;
            ny = ((int[][]) data)[0].length;
        }
        ny /= 2;
    }

    public Object getData() {
        return data;
    }
}
