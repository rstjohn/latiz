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
package com.AandR.beans.plotting.imagePlotPanel.overlays;

import java.awt.Color;
import java.awt.Graphics2D;

/**
 * @author Dr. Richard St. John
 * @version $Revision: 1.3 $, $Date: 2007/08/14 01:07:33 $
 */
public class TextBox extends Overlay {
  
  private String string;
  
  private String actionCommand;
  
  public TextBox(String string) {
    this(string, 0 , 0);
  }

  public TextBox(String string, int xPos, int yPos) {
    shapeColor = Color.BLACK;
    setOverlayType(Overlay.TEXT_OVERLAY);
    this.string = string;
    setPosition(xPos, yPos);
    setWidth((int)(0.75*this.string.length()*getFont().getSize()));
    setHeight(getFont().getSize());
  }
  
  public double getLeft() {
    return getXPos();
  }
  
  public double getTop() {
    return getYPos();
  }
  
  public void fireOverlayListener() {
    TextBoxEditor.getInstanceOf().getColorChooser().setColor(shapeColor);
    TextBoxEditor.getInstanceOf().getFontChooserPanel().setSelectedFont(getFont());
    TextBoxEditor.getInstanceOf().getFieldString().setText(string);
    TextBoxEditor.getInstanceOf().setVisible(true);
    
    if(TextBoxEditor.getInstanceOf().isCancelled()) return;
    setString(TextBoxEditor.getInstanceOf().getFieldString().getText());
    shapeColor = TextBoxEditor.getInstanceOf().getSelectedColor();
    setFont(TextBoxEditor.getInstanceOf().getSelectedFont());
  }
  
  public String getString() {
    return string;
  }

  
  public void setString(String string) {
    this.string = string;
    setWidth((int)(0.75*this.string.length()*getFont().getSize()));
  }

  public void setActionCommand(String actionCommand) {
    this.actionCommand = actionCommand;
  }

  
  public String getActionCommand() {
    return actionCommand;
  }

  @Override
  public void paintItem(Graphics2D g2) {
    g2.drawString(string, (int)getXPos(), (int)(getYPos()+getHeight()));
  }
}
