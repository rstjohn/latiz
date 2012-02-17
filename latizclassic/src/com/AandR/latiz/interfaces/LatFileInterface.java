package com.AandR.latiz.interfaces;

import com.AandR.latiz.dev.LatDataObject;

/**
 * @author Dr. Richard St. John
 * @version $Revision$, $Date$
 */
public interface LatFileInterface {

    public String[] getLatDataKeys();

    public LatDataObject getLatData(String key);
}
