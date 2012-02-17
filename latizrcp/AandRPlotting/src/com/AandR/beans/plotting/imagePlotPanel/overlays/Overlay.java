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
import java.awt.Font;
import java.awt.Graphics2D;

import javax.swing.JTextArea;


/**
 * @author Dr. Richard St. John
 * @version $Revision: 1.3 $, $Date: 2007/08/14 01:07:33 $
 */
public abstract class Overlay {
  public static final int TEXT_OVERLAY = 0;
  
  public static final int BASIC_SHAPE = 1;
  
  private int overlayType;
  
  protected int transparency = 255;
  
  protected Color shapeColor;
  
  protected double penWidth=1;
  
  private double xPos, yPos, width, height;
  
  private Font font=new JTextArea().getFont();
  
  public boolean isDraggable = false;
  
  protected boolean isFilled = false;
  
  public abstract void paintItem(Graphics2D g2);
  public abstract void fireOverlayListener();
  public abstract double getTop();
  public abstract double getLeft();
  
  
  public int getOverlayType() {
    return overlayType;
  }
  
  public void setOverlayType(int overlayType) {
    this.overlayType = overlayType;
  }
  
  
  public Font getFont() {
    return font;
  }

  
  public void setFont(Font font) {
    this.font = font;
  }

  
  public double getHeight() {
    return height;
  }

  
  public void setHeight(double height) {
    this.height = height;
  }

  
  public double getWidth() {
    return width;
  }

  
  public void setWidth(double width) {
    this.width = width;
  }

  
  public double getXPos() {
    return xPos;
  }

  
  public void setXPos(double pos) {
    xPos = pos;
  }
  
  public void setPosition(double xPos, double yPos) {
    this.xPos = xPos;
    this.yPos = yPos;
  }

  
  public double getYPos() {
    return yPos;
  }

  
  public void setYPos(double pos) {
    yPos = pos;
  }
  
  public boolean isFilled() {
    return isFilled;
  }
  
  public void setFilled(boolean isFilled) {
    this.isFilled = isFilled;
  }
  public Color getShapeColor() {
    return shapeColor;
  }
  public void setShapeColor(Color shapeColor) {
    this.shapeColor = shapeColor;
  }
  public double getPenWidth() {
    return penWidth;
  }
  public void setPenWidth(double penWidth) {
    this.penWidth = penWidth;
  }
}
