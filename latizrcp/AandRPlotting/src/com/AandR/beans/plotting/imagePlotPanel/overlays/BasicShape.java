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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;

/**
 * @author Dr. Richard St. John
 * @version $Revision: 1.4 $, $Date: 2007/09/04 15:36:30 $
 */
public class BasicShape extends Overlay {

  public static final int ELLIPSE = 0;
  public static final int RECTANGLE = 1;
  public static final int MASK_ELLIPSE = 2;
  private int shapeFlag=0;
  private double penWidth=1;

  public BasicShape(int shape, int xPos, int yPos, int width, int height) {
    this.shapeFlag = shape;
    shapeColor = new Color(0,0,0,255);
    setPosition(xPos, yPos);
    setHeight(height);
    setWidth(width);
  }

  public double getLeft() {
    return getXPos();
  }

  public double getTop() {
    return getYPos();
  }

  public void fireOverlayListener() {
    BasicShapeEditor.getInstanceOf().getFieldPenWidth().setText(String.valueOf(penWidth));
    BasicShapeEditor.getInstanceOf().setSelectedShape(shapeFlag);
    BasicShapeEditor.getInstanceOf().getColorChooser().setColor(shapeColor);
    BasicShapeEditor.getInstanceOf().getFieldUpperLeftX().setText(String.valueOf(getXPos()));
    BasicShapeEditor.getInstanceOf().getFieldUpperLeftY().setText(String.valueOf(getYPos()));
    BasicShapeEditor.getInstanceOf().getFieldWidth().setText(String.valueOf(getWidth()));
    BasicShapeEditor.getInstanceOf().getFieldHeight().setText(String.valueOf(getHeight()));
    BasicShapeEditor.getInstanceOf().getCheckFilled().setSelected(isFilled);
    BasicShapeEditor.getInstanceOf().getSliderTrans().setValue(255-transparency);
    BasicShapeEditor.getInstanceOf().setVisible(true);

    if(BasicShapeEditor.getInstanceOf().isCancelled()) return;

    penWidth = Double.parseDouble(BasicShapeEditor.getInstanceOf().getFieldPenWidth().getText());
    setShape(BasicShapeEditor.getInstanceOf().getSelectedShape());
    shapeColor = BasicShapeEditor.getInstanceOf().getColorChooser().getColor();
    isFilled = BasicShapeEditor.getInstanceOf().getCheckFilled().isSelected();
    transparency = 255 - BasicShapeEditor.getInstanceOf().getSliderTrans().getValue();
    shapeColor = new Color(shapeColor.getRed(), shapeColor.getGreen(), shapeColor.getBlue(), transparency);
    try {
      double xPos = Double.parseDouble(BasicShapeEditor.getInstanceOf().getFieldUpperLeftX().getText()); 
      double yPos = Double.parseDouble(BasicShapeEditor.getInstanceOf().getFieldUpperLeftY().getText());
      double width = Double.parseDouble(BasicShapeEditor.getInstanceOf().getFieldWidth().getText());
      double height = Double.parseDouble(BasicShapeEditor.getInstanceOf().getFieldHeight().getText());
      setPosition(xPos, yPos);
      setWidth(width);
      setHeight(height);
    } catch(NumberFormatException e) {
      System.out.println("Invalid Number In The Input Field");
    }
  }

  private void setShape(int shape) {
    this.shapeFlag = shape;
  }

  public void paintItem(Graphics2D g2) {
    g2.setStroke(new BasicStroke((float)penWidth));
    Stroke oldStroke = g2.getStroke();
    double left, top, width, height;
    if(isFilled) { 
      left = getXPos();
      top = getYPos();
      width = getWidth();
      height = getHeight();
    } else {
      left = getXPos() - penWidth/2f;
      top = getYPos() - penWidth/2f;
      width = getWidth() + penWidth;
      height = getHeight() + penWidth;
    }
    
    Shape shape = null;
    if(shapeFlag==MASK_ELLIPSE) {
      shape = new Ellipse2D.Double(left, top, width, height);
    } else if(shapeFlag==RECTANGLE) {
      shape = new Rectangle2D.Double(left, top, width, height);
    } else if(shapeFlag==ELLIPSE) {
      shape = new Ellipse2D.Double(left, top, width, height);
    }

    if(isFilled)
      g2.fill(shape);
    else
      g2.draw(shape);

//  Reset Stroke.    
    g2.setStroke(oldStroke);
  }
}
























