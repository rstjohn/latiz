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
package com.AandR.beans.plotting.data;

import com.AandR.beans.plotting.latExplorer.LatFileReader;
import com.AandR.beans.plotting.latExplorer.LatFileRunTrace;
import com.AandR.library.math.OpticsMath;
import java.util.Arrays;
import ncsa.hdf.object.HObject;

/**
 * @author Dr. Richard St. John
 * @version $Revision$, $Date$
 */
public class LatNavigatableData extends AbstractNavigatableData {
  private static final int AMPLITUDE_PLOT = 10;
  private static final int PHASE_WRAPPED_PLOT = 20;
  private static final int INTENSITY_PLOT = 30;
  private static final int PHASE_PISTON_REMOVED_PLOT = 40;
  private static final int PHASE_UNWRAPPED_PLOT = 41;
  private static final int REAL_PART_PLOT = 50;
  private static final int IMAG_PART_PLOT = 60;

  private int currentIndex = -1;

  private int plotType = -1;

  private double[] times;

  private HObject hobject;

  private LatFileReader latReader;

  private String plotLabel;


  public LatNavigatableData(LatFileReader reader, HObject hobject) {
    this(reader, hobject, -1);
  }

  
  public LatNavigatableData(LatFileReader latReader, HObject hobject, int requestedComplexPlotType) {
    this.latReader = latReader;
    this.hobject = hobject;
    LatFileRunTrace runTrace = latReader.getRunTrace().get(hobject.getFile() + ":" + hobject.getFullName());
    
    times = this.latReader.readVariableTimes(hobject);
    double[] selectedTimes = runTrace.getSelectedTimes();
    if(selectedTimes!=null && selectedTimes.length>0) {
      int startIndex = Arrays.binarySearch(times, selectedTimes[0]);
      double[] newTimes = new double[times.length-startIndex];
      int k=0;
      for(int i=startIndex; i<times.length; i++) {
        newTimes[k++] = times[i];
      }
      times = newTimes;
    }
    plotType = requestedComplexPlotType;
    plotLabel = "";
  }


  public double[][] getDatasetAt(int index) {
    currentIndex = index;
    return getData();
  }


  public double[][] getFirstDataset() {
    currentIndex = 0;
    return getData();
  }


  public double[][] getNextDataset() {
    currentIndex = currentIndex < times.length-1 ? currentIndex+1 : times.length-1;
    return getData();
  }


  public double[][] getPreviousDataset() {
    currentIndex = currentIndex < 2 ? 0 : currentIndex-1;
    return getData();
  }


  public double[][] getLastDataset() {
    currentIndex = times.length - 1;
    return getData();
  }


  public int getCurrentIndex() {
    return currentIndex;
  }


  public String getID() {
    LatFileRunTrace runTrace = latReader.getRunTrace().get(hobject.getFile() + ":" + hobject.getFullName());
    String id = runTrace.getAlias() + plotLabel + ": (time=" + times[getCurrentIndex()] + ")";  
    return id;
  }

  
  public String getName() {
    String fileName = hobject.getFile() + "." + hobject.getName();
    fileName = fileName.substring(0, fileName.lastIndexOf("."));
    fileName += plotLabel + ".t="+ times[getCurrentIndex()];
    return fileName;
  }
    

  public int getNumberOfFrames() {
    return times.length;
  }


  public boolean hasNext() {
    return currentIndex < times.length-1;
  }


  public void setCurrentIndex(int index) {
    currentIndex = index;
  }


  private final double[][] getData() {
    try {
      LatFileRunTrace runTrace = latReader.getRunTrace().get(hobject.getFile() + ":" + hobject.getFullName());
      double[][] data = (double[][])latReader.loadVariable(hobject, runTrace, new double[] {times[currentIndex]});
      double[] gridSpacing = latReader.getGridSpacingForSelectedIndex(hobject, currentIndex);
      if(gridSpacing!=null) setGridPointSpacing(gridSpacing[0], gridSpacing[1]);
      if(plotType!=-1)
        data = getDataFromComplexField(data);
      return data;
    } catch (OutOfMemoryError e) {
      e.printStackTrace();
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }


  /**
   * 
   * @param complexData
   * @return
   */
  private final double[][] getDataFromComplexField(double[][] complexData) {
    double[][] data = null;
    int jRe, jIm;
    switch(plotType) {
      case AMPLITUDE_PLOT:
        data = new double[complexData.length][complexData[0].length/2];
        for(int j=0; j<data[0].length; j++) {
          jRe = 2*j; jIm = jRe + 1; 
          for(int i=0; i<data.length; i++) {
            data[i][j] = Math.sqrt(complexData[i][jRe]*complexData[i][jRe] + complexData[i][jIm]*complexData[i][jIm]);
          }
        }
        plotLabel = " (Amplitude)";
        break;
      case INTENSITY_PLOT:
        data = new double[complexData.length][complexData[0].length/2];
        for(int j=0; j<data[0].length; j++) {
          jRe = 2*j; jIm = jRe + 1; 
          for(int i=0; i<data.length; i++) {
            data[i][j] = complexData[i][jRe]*complexData[i][jRe] + complexData[i][jIm]*complexData[i][jIm];
          }
        }
        plotLabel = " (Intensity)";
        break;
      case PHASE_WRAPPED_PLOT:
        data = OpticsMath.computePhase(complexData);
        plotLabel = " (Wrapped Phase)";
        break;
      case PHASE_UNWRAPPED_PLOT:
        data = OpticsMath.computeUnwrappedPhase(complexData);
        plotLabel = " (Unwrapped Phase)";
        break;
      case PHASE_PISTON_REMOVED_PLOT:
        data = OpticsMath.computePistonRemovedPhase(complexData);
        plotLabel = " (Piston-removed Phase)";
        break;
      case REAL_PART_PLOT:
        data = new double[complexData.length][complexData[0].length/2];
        for(int j=0; j<data[0].length; j++) {
          jRe = 2*j; jIm = jRe + 1; 
          for(int i=0; i<data.length; i++) {
            data[i][j] = complexData[i][jRe];
          }
        }
        plotLabel = " (Real part)";
        break;
      case IMAG_PART_PLOT:
        data = new double[complexData.length][complexData[0].length/2];
        for(int j=0; j<data[0].length; j++) {
          jRe = 2*j; jIm = jRe + 1; 
          for(int i=0; i<data.length; i++) {
            data[i][j] = complexData[i][jIm];
          }
        }
        plotLabel = " (Imaginary part)";
        break;
    }
    return data;
  }
}
