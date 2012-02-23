/**
 * 
 */
package com.AandR.latiz.core;

/**
 * @author Aaron Masino
 * @version Jan 18, 2008 2:19:01 PM <br>
 *
 * Comments:
 *
 */
public class TransferableData {

    private String key;
    private String toolTipText;
    protected Object value;
    protected double timeOfLastUpdate;
    protected int indexOfLastUpdate;
    protected Class[] genericClasses;
    protected boolean isUsingGernics;
    protected String valueTypeCanonicalName;
    protected Class valueClass;

    public TransferableData(String key, Class valueClass, Class[] genericClasses, boolean isUsingGenrics, String toolTipText) {
        this.key = key;
        this.toolTipText = toolTipText;
        this.genericClasses = genericClasses;
        this.isUsingGernics = isUsingGenrics;
        this.timeOfLastUpdate = Double.NaN;
        this.indexOfLastUpdate = 0;
        this.valueClass = valueClass;
        this.valueTypeCanonicalName = valueClass.getCanonicalName();
    }

    public TransferableData(String key, Class valueClass, Class[] genericClasses, boolean isUsingGenrics) {
        this.key = key;
        this.toolTipText = "";
        this.genericClasses = genericClasses;
        this.isUsingGernics = isUsingGenrics;
        this.timeOfLastUpdate = Double.NaN;
        this.indexOfLastUpdate = 0;
        this.valueClass = valueClass;
        this.valueTypeCanonicalName = valueClass.getCanonicalName();
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getToolTipText() {
        return toolTipText;
    }

    public void setToolTipText(String toolTipText) {
        this.toolTipText = toolTipText;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public int getIndexOfLastUpdate() {
        return indexOfLastUpdate;
    }

    public void setIndexOfLastUpdate(int indexOfLastUpdate) {
        this.indexOfLastUpdate = indexOfLastUpdate;
    }

    public double getTimeOfLastUpdate() {
        return timeOfLastUpdate;
    }

    public void setTimeOfLastUpdate(double timeOfLastUpdate) {
        this.timeOfLastUpdate = timeOfLastUpdate;
    }

    public final String[] getGenericClassCanonicalNames() {
        String[] names = new String[genericClasses.length];
        int cnt = 0;
        for (Class c : genericClasses) {
            names[cnt++] = c.getCanonicalName();
        }
        return names;
    }

    public final boolean isUsingGernics() {
        return isUsingGernics;
    }

    public final String getValueTypeCanonicalName() {
        return valueTypeCanonicalName;
    }

    public String getValueTypeSimpleName() {
        return valueTypeCanonicalName.substring(valueTypeCanonicalName.lastIndexOf(".") + 1);

    }

    public final Class getValueClass() {
        return valueClass;
    }

    public final Class[] getGenericClasses() {
        return genericClasses;
    }
}
