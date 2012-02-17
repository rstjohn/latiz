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
package com.AandR.palette.dataWriter;

import com.AandR.palette.plugin.data.Output;
import java.math.BigDecimal;
import java.math.MathContext;
import org.jdom.Element;

/**
 *
 * @author Aaron Masino
 */
public class DefaultSavedOutputsImpl implements ISavedOutputs {

    private String datasetName;
    private String beginTime, endTime, period, maxIterationCount;
    private boolean selected,  isUserDefined;
    
    public DefaultSavedOutputsImpl() {
        isUserDefined = false;
    }
    
    public boolean isSaveRequested(Output output) {
		BigDecimal beginTimeBd, periodBd, normalizedStart;
		//LNumberField lnf = new LNumberField();
		Double endTimeDbl;

                BigDecimal timeOfLastUpdate = new BigDecimal(output.getTimeOfLastUpdate(), MathContext.DECIMAL32);
				//beginTimeBd = new BigDecimal(lnf.parse(dataNode.getBeginTime()).toString(), MathContext.DECIMAL32);
				beginTimeBd = new BigDecimal(Double.parseDouble(beginTime), MathContext.DECIMAL32);
				if (timeOfLastUpdate.compareTo(beginTimeBd) < 0) return false;

				if (endTime.equalsIgnoreCase("infinity")) {
					endTimeDbl = Double.POSITIVE_INFINITY;
				} else {
					//endTimeDbl = new Double(lnf.parse(dataNode.getEndTime()).toString());
					endTimeDbl = Double.parseDouble(endTime);
				}
				if (timeOfLastUpdate.doubleValue() > endTimeDbl.doubleValue()) return false;

				normalizedStart = timeOfLastUpdate.subtract(beginTimeBd, MathContext.DECIMAL32);
				if (normalizedStart.compareTo(BigDecimal.ZERO) < 0) return false;

				//periodBd = new BigDecimal(lnf.parse(dataNode.getPeriod()).toString(), MathContext.DECIMAL32);
				periodBd = new BigDecimal(Double.parseDouble(period), MathContext.DECIMAL32);
				boolean isPeriodPositive = (periodBd.compareTo(BigDecimal.ZERO) > 0);
				boolean isRemainderZero = true;
				if (isPeriodPositive)
					isRemainderZero = normalizedStart.remainder(periodBd, MathContext.DECIMAL32).compareTo(
							BigDecimal.ZERO) == 0;

				if (isPeriodPositive && !isRemainderZero) return false;
				return true;
    }

    public void loadSavedWorkspaceParameters(Element e) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Element createWorkspaceParameters() {
        throw new UnsupportedOperationException("Not supported yet.");
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
        return maxIterationCount;
    }

    public void setMaxIterationCount(String maxIterationCount) {
        this.maxIterationCount = maxIterationCount;
    }

    public boolean isUserDefined() {
        return isUserDefined;
    }

    public void setUserDefined(boolean isUserDefined) {
        this.isUserDefined = isUserDefined;
    }

    public String getDatasetName() {
        return datasetName;
    }

    public void setDatasetName(String datasetName) {
        this.datasetName = datasetName;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
