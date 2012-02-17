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
import java.awt.geom.GeneralPath;


/**
 * @author Dr. Richard St. John
 * @version $Revision: 1.4 $, $Date: 2007/08/14 01:07:33 $
 */
public class ArrowOverlay extends Overlay {

  private double xLoc, yLoc, top, left;
  private double rotation;
  private double penWidth=1;
  private double lineLength, arrowLength, arrowWidth;
  private int bufferWidth, bufferHeight;

  public ArrowOverlay(int xLoc, int yLoc, int lineLength, int arrowLength, int arrowWidth, double rotation) {
    this.rotation = Math.toRadians(rotation);
    this.lineLength = lineLength;
    this.arrowLength = arrowLength;
    this.arrowWidth = arrowWidth;
    this.xLoc = xLoc;
    this.yLoc = (int)(yLoc-penWidth/2.0); 
    shapeColor = Color.BLACK;
    setPosition(this.xLoc, this.yLoc);
    setWidth(lineLength);
    setHeight(lineLength);
  }

  public double getLeft() {
    left = getXPos();
    return left;
  }

  public double getTop() {
    top = getYPos()-lineLength/2.0;
    return top;
  }

  public void setBufferSize(int width, int height) {
    bufferWidth = width;
    bufferHeight = height;
  }

  /* (non-Javadoc)
   * @see org.arc.plotting.Overlay#fireOverlayListener()
   */
  @Override
  public void fireOverlayListener() {
    ArrowEditor arrowEditor = new ArrowEditor();
    arrowEditor.setRotation(-(int)Math.toDegrees(rotation));
    arrowEditor.setPenWidth(penWidth);
    arrowEditor.setLineLength(lineLength);
    arrowEditor.setArrowLength(arrowLength);
    arrowEditor.setArrowWidth(arrowWidth);
    arrowEditor.setVisible(true);
    if(arrowEditor.isCancelled()) return;

    rotation = Math.toRadians(arrowEditor.getRotation());
    lineLength = arrowEditor.getLineLength();
    penWidth = arrowEditor.getPenWidth();
    arrowLength = arrowEditor.getArrowLength();
    arrowWidth = arrowEditor.getArrowWidth();
    setWidth((int)lineLength);
    setHeight((int)lineLength);
  }

  /* (non-Javadoc)
   * @see org.arc.plotting.Overlay#paintItem(java.awt.Graphics2D)
   */
  @Override
  public void paintItem(Graphics2D g2) {
    Stroke oldStroke = g2.getStroke();
    g2.setStroke(new BasicStroke((float)penWidth, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));
    double xPos = getXPos();
    double yPos = getYPos();
    double rot = rotation;
    g2.translate(xPos+lineLength/2, yPos+arrowWidth);
    g2.rotate(rot);
    Shape arrow = createArrow();
    g2.draw(arrow);
    g2.rotate(-rot);
    g2.translate(-xPos-lineLength/2, -yPos-arrowWidth);
    g2.setStroke(oldStroke);
//    top = (int)(yPos-lineLength);
//    top = (int)(yPos);
//    left = (int)xPos;
//    left = (int)(xPos-lineLength/2);
  }

  private Shape createArrow() {
    GeneralPath arrow = new GeneralPath();
    arrow.moveTo(-lineLength/2, 0);
    arrow.lineTo(lineLength/2, 0);
    arrow.moveTo(lineLength/2-arrowLength, arrowWidth);
    arrow.lineTo(lineLength/2, 0);
    arrow.lineTo(lineLength/2-arrowLength, -arrowWidth);
    return arrow;
  }
  
  public int getBufferWidth() {
    return bufferWidth;
  }


  public void setBufferWidth(int bufferWidth) {
    this.bufferWidth = bufferWidth;
  }


  public int getBufferHeight() {
    return bufferHeight;
  }


  public void setBufferHeight(int bufferHeight) {
    this.bufferHeight = bufferHeight;
  }
}
