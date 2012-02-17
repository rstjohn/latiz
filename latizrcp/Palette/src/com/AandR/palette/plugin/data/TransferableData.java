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
package com.AandR.palette.plugin.data;

import com.AandR.library.utility.CloneWorker;
import com.AandR.library.utility.FastByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.TreeSet;

/**
 *
 * @author Aaron Masino
 */
public abstract class TransferableData {

    public boolean isUsingGernics;

	protected double timeOfLastUpdate;

	protected int indexOfLastUpdate;

	protected Class valueClass;

	protected Class[] genericClasses;

	protected String key, toolTipText, valueTypeCanonicalName;

	protected TreeSet<ValueStorageObject> valueStorageObjects;

	protected boolean isStatePreserved;

	protected int maxTimesRecall;

	abstract protected boolean isCloneRequired();

    abstract protected Object deserializeObject(FastByteArrayOutputStream fbos);

	/**
	 *
	 * @param key
	 * @param valueClass
	 * @param genericClasses
	 * @param isUsingGenrics
	 * @param toolTipText
	 */
	public TransferableData(String key, Class valueClass, Class[] genericClasses, boolean isUsingGenrics, String toolTipText) {
		this.key=key;
		this.toolTipText = toolTipText;
		this.genericClasses=genericClasses;
		this.isUsingGernics=isUsingGenrics;
		this.timeOfLastUpdate=Double.NaN;
		this.indexOfLastUpdate=0;
		this.valueClass=valueClass;
		this.valueTypeCanonicalName=valueClass.getCanonicalName();
		maxTimesRecall=-1;
		valueStorageObjects = new TreeSet<ValueStorageObject>(new ValueStorageObjectComparator());
	}


	/**
	 *
	 * @param key
	 * @param valueClass
	 * @param genericClasses
	 * @param isUsingGenrics
	 */
	public TransferableData(String key, Class valueClass, Class[] genericClasses, boolean isUsingGenrics) {
		this(key, valueClass, genericClasses, isUsingGenrics, "");
	}


	public void storeNewValue(Object value, double time) throws IOException {
		if(maxTimesRecall<1) {
			valueStorageObjects.clear();
			valueStorageObjects.add(new ValueStorageObject(time,convertValue(value)));
		}else if(valueStorageObjects.size()<maxTimesRecall){
			valueStorageObjects.add(new ValueStorageObject(time,convertValue(value)));
		}else {
			valueStorageObjects.remove(valueStorageObjects.last());
			valueStorageObjects.add(new ValueStorageObject(time,convertValue(value)));
		}
	}

	private Object convertValue(Object value) throws IOException {
		if(isCloneRequired()) {
			FastByteArrayOutputStream fbos = CloneWorker.serializeObject(value);
			return fbos;
		}else {
			return value;
		}
	}

	public void setMaxTimesRecall(int maxTimesRecall) {
		this.maxTimesRecall = maxTimesRecall;
	}

	public int getMaxTimesRecall() {
		return maxTimesRecall;
	}

	public boolean isStatePreserved() {
		return isStatePreserved;
	}

	public void setStatePreserved(boolean isStatePreserved) {
		this.isStatePreserved = isStatePreserved;
	}

	/**
	 * returns an ArrayList of the stored values of size count. The first element is the value whose time is
	 * closest to time without being greater than time, i.e. the value that would be returned by getValue(time)
	 * the next element is the object whose time immediately precedes the first element. If count is greater than
	 * the available number of elements whose times are <= to the input time, then only the available elements will
	 * be returned
	 * @param time
	 * @param index
	 * @return
	 */
	public  ArrayList<Object> getValue(double time, int count) throws IOException, ClassNotFoundException {
		if(valueStorageObjects==null || valueStorageObjects.size()==0)return null;

		ArrayList<Object> valList = new ArrayList<Object>();
		Object o;
		if(maxTimesRecall<0) {
			o = valueStorageObjects.first().vsoObject;
			if(o instanceof FastByteArrayOutputStream) o = deserializeObject((FastByteArrayOutputStream)o);
			valList.add(o);
		}
		else {
			BigDecimal bdTime = new BigDecimal(time,MathContext.DECIMAL32);
			double minTime = valueStorageObjects.last().vsoTime;
			BigDecimal valTime = new BigDecimal(minTime,MathContext.DECIMAL32);
			if(valTime.compareTo(bdTime)>0)return null;
			else {
				int stored = 0;
				for(ValueStorageObject vo : valueStorageObjects) {
					valTime = new BigDecimal(vo.vsoTime);
					if(valTime.compareTo(bdTime)<1) {
						o=vo.vsoObject;
						if(o instanceof FastByteArrayOutputStream) o = deserializeObject((FastByteArrayOutputStream)o);
						valList.add(o);
						stored++;
						if(stored==count) break;
					}
				}
			}
		}
		return valList;
	}


	/**
	 * returns the Object whose time is closest to the input argument time without being greater than the input time
	 * e.g. if input time is 0.5 and there are store values at 0.3, 0.4, the object stored at 0.4 is returned. If there is
	 * no time less than the input time requested, null is returned, e.g. if the input time is 0.5 and the stored times are
	 * 0.6, 0.7, then null is returned
	 * @param time
	 * @return
	 */
	public Object getValue(double time) throws IOException, ClassNotFoundException{
		if(valueStorageObjects==null || valueStorageObjects.size()==0)return null;
		Object o=null;
		if(maxTimesRecall<0)o=valueStorageObjects.first().vsoObject;
		else {
			BigDecimal bdTime = new BigDecimal(time,MathContext.DECIMAL32);
			double minTime = valueStorageObjects.last().vsoTime;
			BigDecimal valTime = new BigDecimal(minTime,MathContext.DECIMAL32);
			if(valTime.compareTo(bdTime)>0)return null;
			else {
				for(ValueStorageObject vo : valueStorageObjects) {
					valTime = new BigDecimal(vo.vsoTime);
					if(valTime.compareTo(bdTime)<1) {
						o=vo.vsoObject;
						break;
					}
				}
			}
		}
		if(o instanceof FastByteArrayOutputStream) return deserializeObject((FastByteArrayOutputStream)o);
		return o;
	}

    /**
     * returns the value Object for at the timeOfLastUpdate
     * @return
     * @throws java.io.IOException
     * @throws java.lang.ClassNotFoundException
     */
    public Object getValue()throws IOException, ClassNotFoundException{
        return getValue(timeOfLastUpdate);
    }

	/**
	 *
	 * @return the key used in the maps.
	 */
	public String getKey() {
		return key;
	}


	/**
	 *
	 * @param key
	 */
	public void setKey(String key) {
		this.key = key;
	}


	/**
	 *
	 * @return the tooltip text.
	 */
	public String getToolTipText() {
		return toolTipText;
	}


	/**
	 *
	 * @param toolTipText
	 */
	public void setToolTipText(String toolTipText) {
		this.toolTipText = toolTipText;
	}

	/**
	 *
	 * @return the index of last update.
	 */
	public int getIndexOfLastUpdate() {
		return indexOfLastUpdate;
	}


	/**
	 *
	 * @param indexOfLastUpdate
	 */
	public void setIndexOfLastUpdate(int indexOfLastUpdate) {
		this.indexOfLastUpdate = indexOfLastUpdate;
	}


	/**
	 *
	 * @return the simulated time of the last update.
	 */
	public double getTimeOfLastUpdate() {
		return timeOfLastUpdate;
	}


	/**
	 *
	 * @param timeOfLastUpdate
	 */
	public void setTimeOfLastUpdate(double timeOfLastUpdate) {
		this.timeOfLastUpdate = timeOfLastUpdate;
	}


	/**
	 *
	 * @return an array of string describing the generic classes.
	 */
	public final String[] getGenericClassCanonicalNames() {
		String[] names = new String[genericClasses.length];
		int cnt=0;
		for(Class c : genericClasses) {
			names[cnt++]=c.getCanonicalName();
		}
		return names;
	}


	/**
	 *
	 * @return true if generics are used.
	 */
	public final boolean isUsingGernics() {
		return isUsingGernics;
	}


	/**
	 *
	 * @return canonical name.
	 */
	public final String getValueTypeCanonicalName() {
		return valueTypeCanonicalName;
	}


	/**
	 *
	 * @return simple name.
	 */
	public String getValueTypeSimpleName() {
		return valueTypeCanonicalName.substring(valueTypeCanonicalName.lastIndexOf(".")+1);

	}


	/**
	 *
	 * @return the class of the transferable data.
	 */
	public final Class getValueClass() {
		return valueClass;
	}


	/**
	 *
	 * @return the generic classes.
	 */
	public final Class[] getGenericClasses() {
		return genericClasses;
	}

	private class ValueStorageObject {
		private double vsoTime;
		private Object vsoObject;

		public ValueStorageObject(double time, Object o) {
			vsoTime = time;
			vsoObject = o;
		}
	}

	private class ValueStorageObjectComparator implements Comparator<ValueStorageObject>{

		public int compare(ValueStorageObject o1, ValueStorageObject o2) {
			int value = Double.compare(o1.vsoTime, o2.vsoTime);
			return -value;
		}

	}

}
