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
package com.AandR.recordedOutputs.nodes;

/**
 *
 * @author rstjohn
 */
public class OutputDataObject {
    private String name;
    private Double beginTime,  endTime,  period;
    private Integer maxIterationCount;
    private boolean selected, userDefined;

    public OutputDataObject(String name) {
        this.name = name;
        beginTime = Double.valueOf(0);
        endTime = Double.POSITIVE_INFINITY;
        period = Double.valueOf(0);
        maxIterationCount = Integer.valueOf(-1);
        selected = false;
        userDefined = false;
    }

    public String getName() {
        return name;
    }

    public void setDatasetName(String datasetName) {
        this.name = datasetName;
    }

    public Double getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(Double beginTime) {
        this.beginTime = beginTime;
    }

    public Double getEndTime() {
        return endTime;
    }

    public void setEndTime(Double endTime) {
        this.endTime = endTime;
    }

    public Integer getMaxIterationCount() {
        return maxIterationCount;
    }

    public void setMaxIterationCount(Integer maxIterationCount) {
        this.maxIterationCount = maxIterationCount;
    }

    public Double getPeriod() {
        return period;
    }

    public void setPeriod(Double period) {
        this.period = period;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public boolean isUserDefined() {
        return userDefined;
    }

    public void setUserDefined(boolean userDefined) {
        this.userDefined = userDefined;
    }
}
