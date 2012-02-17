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

import com.AandR.jepLibrary.LatizJep;
import java.text.ParseException;
import java.util.Map;

/**
 *
 * @author rstjohn
 */
public class GlobalsUtilities {

    public static Object parse(Map<String, GlobalVariable> globalsMap, String text) {
        Object mapEntry = globalsMap.get(text);
        if (mapEntry == null) {
            return null;
        }
        Object o = parseGlobalEntry(globalsMap, globalsMap.get(text));
        return o;
    }

    private static Object parseGlobalEntry(Map<String, GlobalVariable> globalsMap, GlobalVariable gv) {
        LatizJep jep = new LatizJep();
        try {
            parseEntry(globalsMap, gv, jep);
        } catch (ParseException e) {
            return null;
        }
        return jep.getValueAsObject();
    }

    private static void parseEntry(Map<String, GlobalVariable> globalsMap, GlobalVariable gv, LatizJep jep) throws ParseException {
        jep.parseExpression(gv.getValue());
        if (jep.hasError()) {
            String thisError;
            String[] errorList = jep.getErrorInfo().split("\n");
            GlobalVariable mapEntry;
            for (int i = 0; i < errorList.length; i++) {
                thisError = errorList[i];
                if (!thisError.startsWith("Unrecognized symbol")) {
                    throw new ParseException("Unsupported equation", 0);
                }
                String[] var = thisError.split("\"");
                String thisVar = var[1];
                mapEntry = globalsMap.get(thisVar);
                if (mapEntry == null) {
                    throw new ParseException("Variable not found: " + thisVar, 0);
                }
                parseEntry(globalsMap, mapEntry, jep);
            }
            parseEntry(globalsMap, gv, jep);
        }
        Object o = jep.getValueAsObject();
        jep.addVariable(gv.getLabel(), o);
    }
}
