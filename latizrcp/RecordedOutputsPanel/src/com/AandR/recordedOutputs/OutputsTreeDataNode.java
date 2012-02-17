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
package com.AandR.recordedOutputs;

/**
 * @author Dr. Richard St. John
 * @version $Revision$, $Date$
 */
public class OutputsTreeDataNode {
    private String datasetName;
    private String beginTime,  endTime,  period,  maxInterationCount;
    private boolean selected,  isUserDefined;

    public OutputsTreeDataNode(String datasetName, boolean selected) {
        this.datasetName = datasetName;
        this.selected = selected;
        beginTime = "0";
        endTime = String.valueOf(Double.POSITIVE_INFINITY);
        period = "0";
        maxInterationCount = "-1";
        isUserDefined = false;
    }
    
    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean newValue) {
        selected = newValue;
    }

    public String getDatasetName() {
        return datasetName;
    }

    @Override
    public String toString() {
        //return getClass().getName() + "[" + datasetName + "/" + selected + "]";
        return datasetName;
    }

    public String getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(String beginTime) {
        this.beginTime = beginTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public String getMaxIterationCount() {
        return maxInterationCount;
    }

    public void setMaxIterationCount(String maxIterationCount) {
        this.maxInterationCount = maxIterationCount;
    }

    public boolean isUserDefined() {
        return isUserDefined;
    }

    public void setUserDefined(boolean isUserDefined) {
        this.isUserDefined = isUserDefined;
    }
}
