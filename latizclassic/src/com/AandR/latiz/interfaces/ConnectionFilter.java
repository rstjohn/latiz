/**
 * 
 */
package com.AandR.latiz.interfaces;

import com.AandR.latiz.core.Output;

/**
 * @author Aaron Masino
 * @version Feb 28, 2008 4:06:45 PM <br>
 *
 * Comments:
 *
 */
public interface ConnectionFilter {

    public boolean acceptConnection(Output output);
}
