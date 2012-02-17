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
package com.AandR.beans.plotting.imagePlotPanel;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;

/**
 * @author Dr. Richard St. John
 * @version $Revision: 1.1 $, $Date: 2007/05/25 00:12:33 $
 */
public class ThumbnailPanel extends ImagePanel {

    private int rectX = 0,  rectY = 0,  rectWidth,  rectHeight;
    private int currentHorizontalLocation = 0,  currentVerticalLocation = 0;
    private ArrayList<CanvasPanelListener> canvasPanelListeners;

    public ThumbnailPanel(int canvasWidth, int canvasHeight) {
        super(canvasWidth, canvasHeight);
        this.canvasWidth = canvasWidth;
        this.canvasHeight = canvasHeight;
        rectWidth = canvasWidth;
        rectHeight = canvasHeight;
        canvasPanelListeners = new ArrayList<CanvasPanelListener>();
        ThumbnailListener thumbnailListener = new ThumbnailListener();
        addMouseListener(thumbnailListener);
        addMouseMotionListener(thumbnailListener);

        setMinimumSize(new Dimension(canvasWidth, canvasHeight));
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        setBackground(Color.GRAY);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        if (bufferedImage != null) {
            scaleGraphicsContext(g2);
            g2.drawImage(bufferedImage, 0, 0, null);

            int width = (int) (rectWidth / currentScaleFactor);
            int height = (int) (rectHeight / currentScaleFactor);
            float lineWidth = Math.max((float) canvasWidth, (float) canvasHeight) / 50.0f;
            g2.setStroke(new BasicStroke(lineWidth));
            g2.setColor(new Color(125, 125, 125, 90));
            g2.fillRect(rectX, rectY, width, height);
            g2.setColor(new Color(0, 0, 0, 75));
            g2.drawRect(rectX, rectY, width, height);
        }
    }

    public void setRectSize(int rectWidth, int rectHeight) {
        this.rectWidth = rectWidth;
        this.rectHeight = rectHeight;
        repaint();
    }

    public void setRectPos(int rectX, int rectY) {
        this.rectX = rectX;
        this.rectY = rectY;
        repaint();
    }

    public int getRectWidth() {
        return rectWidth;
    }

    public int getRectHeight() {
        return rectHeight;
    }

    public int getRectX() {
        return rectX;
    }

    public int getRectY() {
        return rectY;
    }

    public int getCurrentHorizontalLocation() {
        return currentHorizontalLocation;
    }

    public int getCurrentVerticalLocation() {
        return currentVerticalLocation;
    }

    public void addCanvasPanelListener(CanvasPanelListener listener) {
        canvasPanelListeners.add(listener);
    }

    public void removeCanvasPanelListener(CanvasPanelListener listener) {
        int index = canvasPanelListeners.indexOf(listener);
        if (index == -1) {
            return;
        }
        canvasPanelListeners.remove(index);
    }

    private void nodifyDragListeners() {
        for (int i = 0; i < canvasPanelListeners.size(); i++) {
            canvasPanelListeners.get(i).mouseDragged();
        }
    }

    private void nodifyMouseMoved() {
        for (int i = 0; i < canvasPanelListeners.size(); i++) {
            canvasPanelListeners.get(i).mouseMoved();
        }
    }

    private class ThumbnailListener implements MouseListener, MouseMotionListener {

        private boolean thumbCanDrag = false;
        private int thumbRectX = 0,  thumbRectY = 0,  startThumbFromX = 0,  startThumbFromY = 0;
        private int dragThumbFromX = 0,  dragThumbFromY = 0;

        public void mousePressed(MouseEvent e) {
            double thumbZoom = getCurrentScaleFactor();

            thumbRectX = getRectX();
            thumbRectY = getRectY();

            int rectWidth = (int) (getRectWidth() / thumbZoom);
            int rectHeight = (int) (getRectHeight() / thumbZoom);

            thumbCanDrag = false;
            int x = (int) (e.getX() / thumbZoom);
            int y = (int) (e.getY() / thumbZoom);

            if (x >= thumbRectX && x <= (thumbRectX + rectWidth) && y >= thumbRectY && y <= (thumbRectY + rectHeight)) {
                thumbCanDrag = true;
                startThumbFromX = getRectX();
                startThumbFromY = getRectY();

                dragThumbFromX = x - startThumbFromX;
                dragThumbFromY = y - startThumbFromY;
            } else {
                thumbCanDrag = false;
            }
        }

        public void mouseDragged(MouseEvent e) {
            if (thumbCanDrag) {
                double thumbZoom = getCurrentScaleFactor();
                int x = (int) (e.getX() / thumbZoom);
                int y = (int) (e.getY() / thumbZoom);

                startThumbFromX = x - dragThumbFromX;
                startThumbFromY = y - dragThumbFromY;

                currentHorizontalLocation = -startThumbFromX;
                currentVerticalLocation = -startThumbFromY;

                setRectPos(startThumbFromX, startThumbFromY);

                nodifyDragListeners();
            }
        }

        public void mouseMoved(MouseEvent e) {
            double thumbZoom = getCurrentScaleFactor();

            thumbRectX = getRectX();
            thumbRectY = getRectY();

            int rectWidth = (int) (getRectWidth() / thumbZoom);
            int rectHeight = (int) (getRectHeight() / thumbZoom);

            thumbCanDrag = false;
            int x = (int) (e.getX() / thumbZoom);
            int y = (int) (e.getY() / thumbZoom);

            if (bufferedImage != null && x >= thumbRectX && x <= (thumbRectX + rectWidth) && y >= thumbRectY && y <= (thumbRectY + rectHeight)) {
                thumbCanDrag = true;
                setCursor(new Cursor(Cursor.MOVE_CURSOR));
            } else {
                thumbCanDrag = false;
                setCursor(Cursor.getDefaultCursor());
            }
            nodifyMouseMoved();
        }

        public void mouseClicked(MouseEvent e) {
        }

        public void mouseReleased(MouseEvent e) {
        }

        public void mouseEntered(MouseEvent e) {
        }

        public void mouseExited(MouseEvent e) {
        }
    }
}
