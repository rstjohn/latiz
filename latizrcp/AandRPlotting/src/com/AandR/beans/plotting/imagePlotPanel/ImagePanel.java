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

import com.AandR.library.gui.DropEvent;
import com.AandR.library.gui.DropListener;
import com.AandR.library.gui.TransferableTreeNode;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;

import javax.swing.JPanel;

/**
 * In order to successfully extend this class, a paintComponent(Graphics g) method must be overridden.
 * @author Dr. Richard St. John
 * @version $Revision: 1.1 $, $Date: 2007/05/25 00:12:30 $
 */
public class ImagePanel extends JPanel implements DropTargetListener, Serializable {

    public static final String PNG_FORMAT = "png";
    public static final String JPG_FORMAT = "jpg";
    public static final int NEAREST_NEIGHBOR = AffineTransformOp.TYPE_NEAREST_NEIGHBOR;
    public static final int BILINEAR = AffineTransformOp.TYPE_BILINEAR;
    public static final int BICUBIC = AffineTransformOp.TYPE_BICUBIC;
    private ArrayList<DropListener> dropListeners;
    protected BufferedImage bufferedImage,  originalBufferedImage;
    protected int canvasWidth,  canvasHeight;
    protected double currentScaleFactor;
    protected int currentTranslationX,  currentTranslationY;
    private int interpolationMethod = AffineTransformOp.TYPE_NEAREST_NEIGHBOR;

    /**
     *
     * @param canvasWidth
     * @param canvasHeight
     */
    public ImagePanel(int canvasWidth, int canvasHeight) {
        super();
        dropListeners = new ArrayList<DropListener>();
        if (!GraphicsEnvironment.isHeadless()) {
            new DropTarget(this, this);
        }
        this.canvasWidth = canvasWidth;
        this.canvasHeight = canvasHeight;
        currentScaleFactor = 1.0f;
        currentTranslationX = 0;
        currentTranslationY = 0;
        setPreferredSize(new Dimension(canvasWidth, canvasHeight));
    }

    /**
     *
     */
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
    }

    /**
     *
     * @param g2
     * @throws IllegalArgumentException
     */
    public void scaleGraphicsContext(Graphics2D g2) throws IllegalArgumentException {
        if (currentScaleFactor > 0.0f) {
            g2.scale(currentScaleFactor, currentScaleFactor);
            g2.translate(currentTranslationX, currentTranslationY);
        } else {
            throw new IllegalArgumentException("An attempt was made to scale with a height or width value of 0");
        }
    }

    /**
     *
     *
     */
    public void flipImageVertically() {
        AffineTransform tx = AffineTransform.getScaleInstance(1, -1);
        tx.translate(0, -bufferedImage.getHeight());
        AffineTransformOp op = new AffineTransformOp(tx, interpolationMethod);
        bufferedImage = op.filter(bufferedImage, null);
        repaint();
    }

    /**
     *
     *
     */
    public void flipImageHorizontally() {
        AffineTransform tx = AffineTransform.getScaleInstance(-1, 1);
        tx.translate(-bufferedImage.getWidth(), 0);
        AffineTransformOp op = new AffineTransformOp(tx, interpolationMethod);
        bufferedImage = op.filter(bufferedImage, null);
        repaint();
    }

    /**
     *
     * @param angleInDgrees
     */
    public void rotateImage(double angleInDgrees) {
        AffineTransform tx = new AffineTransform();
        tx.rotate(Math.toRadians(angleInDgrees), bufferedImage.getWidth() / 2., bufferedImage.getHeight() / 2.);
        AffineTransformOp op = new AffineTransformOp(tx, interpolationMethod);
        bufferedImage = op.filter(bufferedImage, null);
        repaint();
    }

    /**
     *
     * @param scale
     * @param bufferedImage
     */
    public void scaleBufferedImage(double scale, BufferedImage bufferedImage) {
        AffineTransform tx = new AffineTransform();
        tx.scale(scale, scale);
        AffineTransformOp op = new AffineTransformOp(tx, interpolationMethod);
        bufferedImage = op.filter(bufferedImage, null);
        repaint();
    }

    public int getInterpolationMethod() {
        return interpolationMethod;
    }

    public void setInterpolationMethod(int method) {
        if (method != AffineTransformOp.TYPE_NEAREST_NEIGHBOR && method != AffineTransformOp.TYPE_BICUBIC && method != AffineTransformOp.TYPE_BILINEAR) {
            interpolationMethod = AffineTransformOp.TYPE_NEAREST_NEIGHBOR;
        } else {
            interpolationMethod = method;
        }
    }

    /**
     *
     * @param scale
     * @param xTranslate
     * @param yTranslate
     * @return
     */
    public BufferedImage transformBufferedImage(double scale, double xTranslate, double yTranslate) {
        BufferedImage tempBuffer = new BufferedImage(canvasWidth, canvasHeight, BufferedImage.TYPE_INT_RGB);
        AffineTransform tx = new AffineTransform();
        tx.scale(scale, scale);
        tx.translate(xTranslate, yTranslate);
        AffineTransformOp op = new AffineTransformOp(tx, interpolationMethod);
        return op.filter(bufferedImage, tempBuffer);
    }

    /**
     *
     * @param scale
     * @param xTranslate
     * @param yTranslate
     * @return
     */
    public BufferedImage transformBufferedImage(double xScale, double yScale, double scale, double xTranslate, double yTranslate) {
        int newWidth = (int) (canvasWidth * xScale);
        int newHeight = (int) (canvasHeight * yScale);
        BufferedImage tempBuffer = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
        AffineTransform tx = new AffineTransform();
        tx.scale(scale, scale);
        tx.translate(xTranslate, yTranslate);
        AffineTransformOp op = new AffineTransformOp(tx, interpolationMethod);
        return op.filter(bufferedImage, tempBuffer);
    }

    /**
     *
     * @param scaleFactor
     */
    public void scaleImage(double scaleFactor) {
        currentScaleFactor = scaleFactor;
        repaint();
    }

    /**
     *
     * @param xTranslattion
     * @param yTranslation
     */
    public void translate(int xTranslattion, int yTranslation) {
        currentTranslationX = xTranslattion;
        currentTranslationY = yTranslation;
        repaint();
    }

    /**
     *
     * @param scaleFactor
     * @param xTranslate
     * @param yTranslate
     */
    public void transform(double scaleFactor, int xTranslate, int yTranslate) {
        currentScaleFactor = scaleFactor;
        currentTranslationX = xTranslate;
        currentTranslationY = yTranslate;
        repaint();
    }

    /**
     *
     * @return
     */
    public BufferedImage getBufferedImage() {
        return bufferedImage;
    }

    /**
     *
     * @param image
     */
    public void setBufferedImage(BufferedImage image) {
        bufferedImage = image;
        originalBufferedImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
        originalBufferedImage.setData(image.getData());
        repaint();
    }

    /**
     *
     * @return
     */
    public double getCurrentScaleFactor() {
        return currentScaleFactor;
    }

    /**
     *
     * @param canvasWidth
     * @param canvasHeight
     */
    public void setCanvasSize(int canvasWidth, int canvasHeight) {
        Dimension dim = new Dimension(canvasWidth, canvasHeight);
        setPreferredSize(dim);
        this.canvasWidth = canvasWidth;
        this.canvasHeight = canvasHeight;
        repaint();
    }

    /**
     *
     * @return
     */
    public int getCanvasHeight() {
        return canvasHeight;
    }

    /**
     *
     * @return
     */
    public int getCanvasWidth() {
        return canvasWidth;
    }

    public int getCurrentTranslationX() {
        return currentTranslationX;
    }

    public int getCurrentTranslationY() {
        return currentTranslationY;
    }

    public void setCurrentScaleFactor(double currentScaleFactor) {
        this.currentScaleFactor = currentScaleFactor;
    }

    public void addDropListener(DropListener dropListener) {
        dropListeners.add(dropListener);
    }

    public void dragEnter(DropTargetDragEvent dtde) {
        dtde.acceptDrag(DnDConstants.ACTION_COPY);
    }

    public void dragOver(DropTargetDragEvent dtde) {
    }

    public void dropActionChanged(DropTargetDragEvent dtde) {
    }

    public void dragExit(DropTargetEvent dte) {
    }

    public void drop(DropTargetDropEvent dtde) {
        Object droppedItem = null;
        try {
            Transferable t = dtde.getTransferable();
            if (t.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                dtde.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
                droppedItem = t.getTransferData(DataFlavor.stringFlavor);
                for (DropListener dropListener : dropListeners) {
                    dropListener.dropAction(new DropEvent(droppedItem, dtde.getDropTargetContext(), dtde.getLocation(), dtde.getDropAction(), dtde.getSourceActions()));
                }
                dtde.dropComplete(true);
                return;
            } else if (t.isDataFlavorSupported(TransferableTreeNode.NODE_FLAVOR)) {
                dtde.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
                droppedItem = t.getTransferData(TransferableTreeNode.NODE_FLAVOR);
                for (DropListener dropListener : dropListeners) {
                    dropListener.dropAction(new DropEvent(droppedItem, dtde.getDropTargetContext(), dtde.getLocation(), dtde.getDropAction(), dtde.getSourceActions()));
                }
                dtde.dropComplete(true);
                return;
            }
            dtde.rejectDrop();
            dtde.dropComplete(true);
        } catch (IOException ioe) {
            dtde.dropComplete(true);
        } catch (UnsupportedFlavorException e) {
            dtde.dropComplete(true);
        }
    }
}
