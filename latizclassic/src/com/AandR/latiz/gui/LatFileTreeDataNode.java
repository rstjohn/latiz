package com.AandR.latiz.gui;

/**
 * @author Dr. Richard St. John
 * @version $Revision$, $Date$
 */
public class LatFileTreeDataNode {

    private String datasetName;
    private Number beginTime, endTime, period, maxFrameCount;
    private boolean selected, isUserDefined;

    public LatFileTreeDataNode(String datasetName, boolean selected) {
        this.datasetName = datasetName;
        this.selected = selected;
        beginTime = 0;
        endTime = Double.POSITIVE_INFINITY;
        period = 0;
        maxFrameCount = -1;
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

    public Number getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(Number beginTime) {
        this.beginTime = beginTime;
    }

    public Number getEndTime() {
        return endTime;
    }

    public void setEndTime(Number endTime) {
        this.endTime = endTime;
    }

    public Number getPeriod() {
        return period;
    }

    public void setPeriod(Number period) {
        this.period = period;
    }

    public Number getMaxFrameCount() {
        return maxFrameCount;
    }

    public void setMaxFrameCount(Number maxFrameCount) {
        this.maxFrameCount = maxFrameCount;
    }

    public boolean isUserDefined() {
        return isUserDefined;
    }

    public void setUserDefined(boolean isUserDefined) {
        this.isUserDefined = isUserDefined;
    }
}
