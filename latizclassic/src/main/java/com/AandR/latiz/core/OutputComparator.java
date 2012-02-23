/**
 * 
 */
package com.AandR.latiz.core;

import java.util.Comparator;

/**
 * @author Aaron Masino
 * @version Jan 24, 2008 11:37:53 AM <br>
 *
 * Comments:
 *
 */
public class OutputComparator implements Comparator<Output> {

    public int compare(Output outOne, Output outTwo) {
        String keyOne = outOne.getOutputPlugin().getName() + "->" + outOne.getKey();
        String keyTwo = outOne.getOutputPlugin().getName() + "->" + outTwo.getKey();
        return keyOne.compareTo(keyTwo);
    }
}
