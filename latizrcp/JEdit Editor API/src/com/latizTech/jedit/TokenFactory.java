package com.latizTech.jedit;

import com.latizTech.jedit.tokenMarkers.JavaTokenMarker;
import com.latizTech.jedit.tokenMarkers.MatlabTokenMarker;
import com.latizTech.jedit.tokenMarkers.PropertiesTokenMarker;
import com.latizTech.jedit.tokenMarkers.TokenMarker;
import com.latizTech.jedit.tokenMarkers.XMLTokenMarker;

/**
 *
 * @author rstjohn
 */
public class TokenFactory {
    
    public static final String JAVA = "JAVA";
    public static final String BUNDLES = "BUNDLES";
    public static final String MATLAB = "MATLAB";
    public static final String XML = "XML";

    private TokenFactory() {
    }

    public static TokenMarker getTokenMarker(String tokenName) {
        if(tokenName.equals(JAVA)) {
            return new JavaTokenMarker();
        } else if(tokenName.equals(BUNDLES)) {
            return new PropertiesTokenMarker();
        } else if(tokenName.equals(MATLAB)) {
            return new MatlabTokenMarker();
        } else if(tokenName.equals(XML)) {
            return new XMLTokenMarker();
        } else {
            return null;
        }
    }
}
