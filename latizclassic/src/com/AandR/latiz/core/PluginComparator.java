/**
 * 
 */
package com.AandR.latiz.core;

import java.util.Comparator;

import com.AandR.latiz.dev.AbstractPlugin;

/**
 * @author Aaron Masino
 * @version Dec 2, 2007 12:29:28 PM <br>
 *
 * Comments:
 *
 */
@SuppressWarnings("hiding")
public class PluginComparator implements Comparator<AbstractPlugin> {

    /* (non-Javadoc)
     * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
     */
    public int compare(AbstractPlugin p1, AbstractPlugin p2) {
        return p1.getName().compareToIgnoreCase(p2.getName());
    }
}
