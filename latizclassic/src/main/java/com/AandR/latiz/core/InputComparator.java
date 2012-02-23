/**
 * 
 */
package com.AandR.latiz.core;

import java.util.Comparator;

/**
 * @author Aaron Masino
 * @version Jan 24, 2008 7:41:54 AM <br>
 *
 * Comments:
 *
 */
public class InputComparator implements Comparator<Input> {

    public int compare(Input inOne, Input inTwo) {
        String keyOne = inOne.getInputPlugin().getName() + "->" + inOne.getKey();
        String keyTwo = inTwo.getInputPlugin().getName() + "->" + inTwo.getKey();
        return keyOne.compareTo(keyTwo);
    }
}
