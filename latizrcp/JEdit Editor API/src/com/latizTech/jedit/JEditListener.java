package com.latizTech.jedit;

import java.awt.event.KeyEvent;


/**
 * @author Dr. Richard St. John
 * @version $Revision: 1.2 $, $Date: 2007/09/12 18:04:07 $
 */
public interface JEditListener {
  public void fileChanged(boolean hasTextChanged);
  public void keyPressed(KeyEvent evt);
}
