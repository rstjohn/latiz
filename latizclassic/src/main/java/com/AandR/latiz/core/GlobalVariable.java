package com.AandR.latiz.core;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Dr. Richard St. John
 * @version $Revision$, $Date$
 */
public class GlobalVariable {

    private String label, value;

    public GlobalVariable(String label, String value) {
        this.label = label;
        this.value = value;
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
        return value.toLowerCase().startsWith("$loop=");
    }

    public String[] parseLoop() {
        if (!isLoop() || value == null) {
            return null;
        }

        String strMatch_pattern = "\"([^(\")]*)([^(\")]*)\"";
        Pattern pattern = Pattern.compile(strMatch_pattern);
        Matcher matcher = pattern.matcher(new StringBuffer(value));

        ArrayList<String> matches = new ArrayList<String>();
        while (matcher.find()) {
            matches.add(matcher.group(1));
        }
        String[] split = new String[matches.size()];
        return matches.toArray(split);
    }
}
