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

/**
 * @author Dr. Richard St. John
 * @version $Revision: 1.1 $, $Date: 2007/08/14 01:05:31 $
 */
public class EllipticalAnnulusShape extends Overlay {
  
  private double obsWidth = 2, obsHeight = 2;

  private Color obsColor;

  
  public EllipticalAnnulusShape(int xPos, int yPos, int width, int height) {
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
    EllipticalAnnulusShapeEditor.getInstanceOf().getFieldPenWidth().setText(String.valueOf(penWidth));
    EllipticalAnnulusShapeEditor.getInstanceOf().getColorChooser().setColor(shapeColor);
    EllipticalAnnulusShapeEditor.getInstanceOf().getFieldUpperLeftX().setText(String.valueOf(getXPos()));
    EllipticalAnnulusShapeEditor.getInstanceOf().getFieldUpperLeftY().setText(String.valueOf(getYPos()));
    EllipticalAnnulusShapeEditor.getInstanceOf().getFieldWidth().setText(String.valueOf(getWidth()));
    EllipticalAnnulusShapeEditor.getInstanceOf().getFieldHeight().setText(String.valueOf(getHeight()));
    EllipticalAnnulusShapeEditor.getInstanceOf().getFieldCenterWidth().setText(String.valueOf(obsWidth));
    EllipticalAnnulusShapeEditor.getInstanceOf().getFieldCenterHeight().setText(String.valueOf(obsHeight));
    
    EllipticalAnnulusShapeEditor.getInstanceOf().getSliderTrans().setValue(255-transparency);
    EllipticalAnnulusShapeEditor.getInstanceOf().setVisible(true);

    if(EllipticalAnnulusShapeEditor.getInstanceOf().isCancelled()) return;

    penWidth = Double.parseDouble(EllipticalAnnulusShapeEditor.getInstanceOf().getFieldPenWidth().getText());
    shapeColor = EllipticalAnnulusShapeEditor.getInstanceOf().getColorChooser().getColor();
    transparency = 255 - EllipticalAnnulusShapeEditor.getInstanceOf().getSliderTrans().getValue();
    shapeColor = new Color(shapeColor.getRed(), shapeColor.getGreen(), shapeColor.getBlue());
    obsColor = new Color(shapeColor.getRed(), shapeColor.getGreen(), shapeColor.getBlue(), transparency);
    try {
      double xPos = Double.parseDouble(EllipticalAnnulusShapeEditor.getInstanceOf().getFieldUpperLeftX().getText()); 
      double yPos = Double.parseDouble(EllipticalAnnulusShapeEditor.getInstanceOf().getFieldUpperLeftY().getText());
      double width = Double.parseDouble(EllipticalAnnulusShapeEditor.getInstanceOf().getFieldWidth().getText());
      double height = Double.parseDouble(EllipticalAnnulusShapeEditor.getInstanceOf().getFieldHeight().getText());
      obsWidth = Double.parseDouble(EllipticalAnnulusShapeEditor.getInstanceOf().getFieldCenterWidth().getText());
      obsHeight = Double.parseDouble(EllipticalAnnulusShapeEditor.getInstanceOf().getFieldCenterHeight().getText());
      setPosition(xPos, yPos);
      setWidth(width);
      setHeight(height);
    } catch(NumberFormatException e) {
      System.out.println("Invalid Number In The Input Field");
    }
  }

    
  public void paintItem(Graphics2D g2) {
    g2.setColor(shapeColor);
    g2.setStroke(new BasicStroke((float)penWidth));
    Stroke oldStroke = g2.getStroke();
    Shape shape = null;
    double left, top, width, height;
    
//  Paint Aperture
    left = getXPos() - penWidth/2.0;
    top = getYPos() - penWidth/2.0;
    width = getWidth() + penWidth;
    height = getHeight() + penWidth;
    
    double centerX = left + width/2.0;
    double centerY = top + height/2.0;
    
    shape = new Ellipse2D.Double(left, top, width, height);
    g2.draw(shape);
    
//  Paint Obscuration    
    g2.setColor(obsColor);
    left = centerX - obsWidth/2f;
    top = centerY - obsHeight/2f;
    width = obsWidth;
    height = obsHeight;
    shape = new Ellipse2D.Double(left, top, width, height);
    g2.fill(shape);

//  Reset Stroke.    
    g2.setStroke(oldStroke);
  }
}
