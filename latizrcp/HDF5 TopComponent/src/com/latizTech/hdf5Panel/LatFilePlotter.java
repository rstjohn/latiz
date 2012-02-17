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
package com.latizTech.hdf5Panel;

import com.AandR.beans.plotting.LinePlotPanel.LinePlotPanel;
import com.AandR.beans.plotting.imagePlotPanel.ImagePlotPanel;
import com.AandR.library.math.MatrixMath;
import java.util.HashMap;
import org.openide.util.Exceptions;

/**
 *
 * @author rstjohn
 */
public class LatFilePlotter {

    public static void plotImageData(DatasetNode datasetNode) {
        datasetNode.getDataType();
        if (datasetNode.getDataType() == LatFileConstants.ARRAY_COMPLEX) {
            //requestedPlotType = 0;
            //popupComplex.show(invoker, pt.x, pt.y);
        } else {
            final ImagePlotPanel imagePlotPanel = new ImagePlotPanel();
            imagePlotPanel.getCanvas().setNavigatableData(new LatFileNavigatableData(datasetNode));
            //imagePlotPanel.setPreferredSize(imagePanelDimension);

            ImagePlotTopComponent ptc = new ImagePlotTopComponent(datasetNode.getName(), imagePlotPanel);
            ptc.setName(datasetNode.getDataset().getFileFormat().getFilePath() + ":" + datasetNode.getDataset().getFullName());
            ptc.setDisplayName(datasetNode.getName());
            ptc.setToolTipText(datasetNode.getDataset().getFileFormat().getFilePath() + ":" + datasetNode.getDataset().getFullName());
            ptc.open();
            ptc.requestActive();
//            DialogDescriptor dd = new DialogDescriptor(imagePlotPanel, "Plot");
//            Dialog dialog = DialogDisplayer.getDefault().createDialog(dd);
//            dialog.setVisible(true);
        }
    }

    public static void plotImageData(DatasetNode datasetNode, double[] selectedTimes) {
        datasetNode.getDataType();
        if (datasetNode.getDataType() == LatFileConstants.ARRAY_COMPLEX) {
            //requestedPlotType = 0;
            //popupComplex.show(invoker, pt.x, pt.y);
        } else {
            final ImagePlotPanel imagePlotPanel = new ImagePlotPanel();
            imagePlotPanel.getCanvas().setNavigatableData(new LatFileNavigatableData(datasetNode, selectedTimes));
            //imagePlotPanel.setPreferredSize(imagePanelDimension);

            ImagePlotTopComponent ptc = new ImagePlotTopComponent(datasetNode.getName(), imagePlotPanel);
            ptc.setName(datasetNode.getDataset().getFileFormat().getFilePath() + ":" + datasetNode.getDataset().getFullName());
            ptc.setDisplayName(datasetNode.getName());
            ptc.setToolTipText(datasetNode.getDataset().getFileFormat().getFilePath() + ":" + datasetNode.getDataset().getFullName());
            ptc.open();
            ptc.requestActive();
//            DialogDescriptor dd = new DialogDescriptor(imagePlotPanel, "Plot");
//            Dialog dialog = DialogDisplayer.getDefault().createDialog(dd);
//            dialog.setVisible(true);
        }
    }

    public static void plotMultipleLines(DatasetNode datasetNode) {
        HashMap<String, Object> xyData;
        try {
            xyData = HDF5Reader.loadVariable(datasetNode.getDataset());
            double[] t = (double[]) xyData.get("x");
            double[][] y = (double[][]) xyData.get("y");
            String[] s = new String[t.length];
            for (int i = 0; i < t.length; i++) {
                s[i] = "t=" + t[i];
            }
            //y = MatrixMath.transpose(y);
            LinePlotPanel linePlotPanel = new LinePlotPanel();
            linePlotPanel.setData(s, y);
            LinePlotTopComponent ptc = new LinePlotTopComponent(linePlotPanel);
            ptc.setName(datasetNode.getDataset().getFileFormat().getFilePath() + ":" + datasetNode.getDataset().getFullName());
            ptc.setDisplayName(datasetNode.getName());
            ptc.setToolTipText(datasetNode.getDataset().getFileFormat().getFilePath() + ":" + datasetNode.getDataset().getFullName());
            ptc.open();
            ptc.requestActive();
//            DialogDescriptor dd = new DialogDescriptor(linePlotPanel, "Plot");
//            Dialog dialog = DialogDisplayer.getDefault().createDialog(dd);
//            dialog.setVisible(true);
        } catch (OutOfMemoryError ex) {
            Exceptions.printStackTrace(ex);
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    public static void plotMultipleLines(DatasetNode datasetNode, double[] selectedTimes) {
        HashMap<String, Object> xyData;
        try {
            xyData = HDF5Reader.loadVariable(datasetNode.getDataset(), selectedTimes);
            double[] t = (double[]) xyData.get("x");
            double[][] y = (double[][]) xyData.get("y");
            String[] s = new String[selectedTimes.length];
            for (int i = 0; i < selectedTimes.length; i++) {
                s[i] = "t=" + selectedTimes[i];
            }
            //y = MatrixMath.transpose(y);
            LinePlotPanel linePlotPanel = new LinePlotPanel();
            linePlotPanel.setData(s, y);
            LinePlotTopComponent ptc = new LinePlotTopComponent(linePlotPanel);
            ptc.setName(datasetNode.getDataset().getFileFormat().getFilePath() + ":" + datasetNode.getDataset().getFullName());
            ptc.setDisplayName(datasetNode.getName());
            ptc.setToolTipText(datasetNode.getDataset().getFileFormat().getFilePath() + ":" + datasetNode.getDataset().getFullName());
            ptc.open();
            ptc.requestActive();
//            DialogDescriptor dd = new DialogDescriptor(linePlotPanel, "Plot");
//            Dialog dialog = DialogDisplayer.getDefault().createDialog(dd);
//            dialog.setVisible(true);
        } catch (OutOfMemoryError ex) {
            Exceptions.printStackTrace(ex);
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    public static void plotSingleLine(DatasetNode datasetNode) {
        HashMap<String, Object> xyData;
        try {
            xyData = HDF5Reader.loadVariable(datasetNode.getDataset());
            double[] t = (double[]) xyData.get("x");
            double[][] y = (double[][]) xyData.get("y");
            y = MatrixMath.transpose(y);
            LinePlotPanel linePlotPanel = new LinePlotPanel();
            linePlotPanel.setData("time (sec)", new String[]{datasetNode.getName()}, t, y);
            LinePlotTopComponent ptc = new LinePlotTopComponent(linePlotPanel);
            ptc.setName(datasetNode.getDataset().getFileFormat().getFilePath() + ":" + datasetNode.getDataset().getFullName());
            ptc.setDisplayName(datasetNode.getName());
            ptc.setToolTipText(datasetNode.getDataset().getFileFormat().getFilePath() + ":" + datasetNode.getDataset().getFullName());
            ptc.open();
            ptc.requestActive();
//            DialogDescriptor dd = new DialogDescriptor(linePlotPanel, "Plot");
//            Dialog dialog = DialogDisplayer.getDefault().createDialog(dd);
//            dialog.setVisible(true);
        } catch (OutOfMemoryError ex) {
            Exceptions.printStackTrace(ex);
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }
    }


    public static void plotSingleLine(DatasetNode datasetNode, double[] selectedTimes) {
        HashMap<String, Object> xyData;
        try {
            xyData = HDF5Reader.loadVariable(datasetNode.getDataset(), selectedTimes);
            double[] t = (double[]) xyData.get("x");
            double[][] y = (double[][]) xyData.get("y");
            y = MatrixMath.transpose(y);
            LinePlotPanel linePlotPanel = new LinePlotPanel();
            linePlotPanel.setData("time (sec)", new String[]{datasetNode.getName()}, t, y);
            LinePlotTopComponent ptc = new LinePlotTopComponent(linePlotPanel);
            ptc.setName(datasetNode.getDataset().getFileFormat().getFilePath() + ":" + datasetNode.getDataset().getFullName());
            ptc.setDisplayName(datasetNode.getName());
            ptc.setToolTipText(datasetNode.getDataset().getFileFormat().getFilePath() + ":" + datasetNode.getDataset().getFullName());
            ptc.open();
            ptc.requestActive();
//            DialogDescriptor dd = new DialogDescriptor(linePlotPanel, "Plot");
//            Dialog dialog = DialogDisplayer.getDefault().createDialog(dd);
//            dialog.setVisible(true);
        } catch (OutOfMemoryError ex) {
            Exceptions.printStackTrace(ex);
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }
    }
}
