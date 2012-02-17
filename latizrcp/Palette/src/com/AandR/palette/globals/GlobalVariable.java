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
package com.AandR.palette.globals;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Dr. Richard St. John
 * @version $Revision$, $Date$
 */
public class GlobalVariable implements Serializable {

    private String label,  value;
    private boolean isStringLoop, isPublic;

    public GlobalVariable(String label, String value, Boolean isPublic) {
        this.label = label;
        this.value = value;
        this.isPublic = isPublic == null ? false : isPublic.booleanValue();
    }

    public boolean isPublic() {
        return isPublic;
    }

    public void setPublic(boolean isPublic) {
        this.isPublic = isPublic;
    }

    public String getLabel() {
        return label; 
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public boolean isLoop() {
        return value.toLowerCase().trim().startsWith("loop");
    //return (thisValue.contains("[") && thisValue.contains("]"));
    }

    public String[] parseLoop() {
        if (!isLoop() || value == null) {
            return null;
        }

        String val = value.substring(value.indexOf("(")).trim();
        if (!val.endsWith(")")) {
            val += ")";
        }
        val = val.substring(1, val.length() - 1).trim();

        ArrayList<String> items = new ArrayList<String>();
        parseString(items, val);
        String[] returnString = new String[items.size()];
        return items.toArray(returnString);

    /*
    if(thisValue.contains("\"")) {
    isStringLoop = true;
    String strMatch_pattern = "\"([^(\")]*)([^(\")]*)\"";
    Pattern pattern = Pattern.compile(strMatch_pattern);
    Matcher matcher = pattern.matcher(new StringBuffer(thisValue));

    ArrayList<String> matches = new ArrayList<String>();
    while(matcher.find()) {
    matches.add(matcher.group(1));
    }
    String[] split = new String[matches.size()];
    return matches.toArray(split);
    } else {
    isStringLoop = false;
    String val = thisValue.substring(thisValue.indexOf("(")).trim();
    //String val = thisValue.trim();
    if(!val.endsWith(")")) val += ")";
    val = val.substring(1, val.length()-1);
    String[] vals = val.split(";");
    for(int i=0; i<vals.length; i++) {
    vals[i] = vals[i].trim();
    }
    return vals;
    }
     */
    }

    private void parseString(ArrayList<String> items, String s) {
        String[] split = s.split(",");
        int length = split.length;

        // Compute step constraints.
        String constraints = split[split.length - 1];
        ArrayList<String> steps = new ArrayList<String>();
        if (constraints.contains("[")) {
            length--;
            parseRegEx(steps, constraints, "[", "]");
            for (int i = steps.size(); i < length; i++) {
                steps.add("1");
            }
        } else {
            for (int i = 0; i < split.length; i++) {
                steps.add("1");
            }
        }

        // Parse values.
        String thisString;
        for (int i = 0; i < length; i++) {
            thisString = split[i].trim();
            if (thisString.contains("-")) {
                items.addAll(parseRange(thisString, Integer.parseInt(steps.get(i))));
            } else {
                items.add(thisString);
            }
        }
    }

    private void parseRegEx(ArrayList<String> items, String s, String s1, String s2) {
        Matcher matcher = Pattern.compile(createRegEx(s1, s2)).matcher(new StringBuffer(value));
        String thisMatch;
        while (matcher.find()) {
            thisMatch = matcher.group(1);
            if (thisMatch.length() == 0) {
                items.add("1");
            } else {
                items.add(matcher.group(1));
            }
        }
    }

    private String createRegEx(String s1, String s2) {
        if (s1.equals("[")) {
            s1 = "\\" + s1;
        }
        if (s2.equals("]")) {
            s2 = "\\" + s2;
        }
        return s1 + "([^(" + s2 + "\")]*)([^(" + s1 + ")]*)" + s2;
    }

    private Collection<? extends String> parseRange(String s, int step) {
        ArrayList<String> values = new ArrayList<String>();
        try {
            Integer.parseInt(s);
            values.add(s);
            return values;
        } catch (NumberFormatException e) {
        }

        String[] split = s.split("-");
        Number start = new Integer(split[0].trim());
        Number end = new Integer(split[1].trim());
        if (start.intValue() < end.intValue()) {
            int thisValue = start.intValue();
            while (thisValue <= end.intValue()) {
                values.add(String.valueOf(thisValue));
                thisValue += step;
            }
        } else {
            int thisValue = end.intValue();
            while (thisValue <= start.intValue()) {
                values.add(String.valueOf(thisValue));
                thisValue -= step;
            }
        }
        return values;
    }

    public boolean isStringLoop() {
        return isStringLoop;
    }

    public void setStringLoop(boolean isStringLoop) {
        this.isStringLoop = isStringLoop;
    }
}
