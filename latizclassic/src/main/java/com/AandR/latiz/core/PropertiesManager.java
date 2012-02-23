package com.AandR.latiz.core;

import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


import com.AandR.io.SortedProperties;
import com.AandR.latiz.gui.InputConnector;
import com.AandR.latiz.gui.OutputConnector;
import com.AandR.latiz.gui.Palette;

/**
 * @author Dr. Richard St. John
 * @version $Revision: 1.11 $, $Date: 2007/10/10 00:32:06 $
 */
public class PropertiesManager extends SortedProperties {

    public static final String COLOR_CONNECTION_INCOMING_BACKGROUND = "COLOR_CONNECTION_INCOMING_BACKGROUND";
    public static final String COLOR_CONNECTION_INCOMING_FOREGROUND = "COLOR_CONNECTION_INCOMING_FOREGROUND";
    public static final String COLOR_CONNECTION_OUTGOING_BACKGROUND = "COLOR_CONNECTION_OUTGOING_BACKGROUND";
    public static final String COLOR_CONNECTION_OUTGOING_FOREGROUND = "COLOR_CONNECTION_OUTGOING_FOREGROUND";
    public static final String CONNECTOR_LINE_COLOR = "CONNECTOR_LINE_COLOR";
    public static final String CONNECTOR_LINE_WEIGHT = "CONNECTOR_LINE_WEIGHT";
    public static final String FRAME_IS_MAXIMIZED = "FRAME_IS_MAXIMIZED";
    public static final String FRAME_WIDTH = "FRAME_WIDTH";
    public static final String FRAME_HEIGHT = "FRAME_HEIGHT";
    public static final String FRAME_LOC_X = "FRAME_LOC_X";
    public static final String FRAME_LOC_Y = "FRAME_LOC_Y";
    public static final String FRAME_FILETREE_WIDTH = "FRAME_FILETREE_WIDTH";
    public static final String FRAME_FILETREE_HEIGHT = "FRAME_FILETREE_HEIGHT";
    public static final String FRAME_PALETTE_WIDTH = "FRAME_PALETTE_WIDTH";
    public static final String FRAME_PALETTE_HEIGHT = "FRAME_PALETTE_HEIGHT";
    public static final String FRAME_PROPS_WIDTH = "FRAME_PROPS_WIDTH";
    public static final String GENERAL_LAT_PATH = "GENERAL_LAT_PATH";
    public static final String PALETTE_BACKGROUND = "PALETTE_BACKGROUND";
    public static final String PALETTE_LINE_COLOR = "PALETTE_LINE_COLOR";
    public static final String PALETTE_LINE_SPACING = "PALETTE_LINE_SPACING";
    public static final String PALETTE_GRID_TYPE = "PALETTE_GRID_TYPE";
    public static final String REPO_DIRS = "REPO_DIRS";
    public static final String REPO_DEFAULT = "REPO_DEFAULT";
    public static final String DEVELOPERS_PROJECT_FOLDER = "DEVELOPERS_PROJECT_FOLDER";
    public static final String DEVELOPERS_AUTHOR = "DEVELOPERS_AUTHOR";
    //public static final String DEVELOPERS_CATEGORY_IDS = "DEVELOPERS_CATEGORY_IDS";
    //public static final String DEVELOPERS_DEFAULT_TEMPLATE = "DEVELOPERS_DEFAULT_TEMPLATE";
    //public static final String DEVELOPERS_SAVABLE_INPUTS = "DEVELOPERS_SAVABLE_INPUTS";
    //public static final String DEVELOPERS_DISPLAYABLE_DATA = "DEVELOPERS_DISPLAYABLE_DATA";
    //public static final String DEVELOPERS_HUD_VIEWS = "DEVELOPERS_HUD_VIEW";
    private static PropertiesManager instanceOf = new PropertiesManager();
    private static File propertiesFile;

    private PropertiesManager() {
        super();
        setDefaults();
    }

    public static void setPropertiesFile(File propertiesFileDir) {
        propertiesFileDir.mkdirs();
        propertiesFile = new File(propertiesFileDir, "latiz.properties");
    }

    private void setDefaults() {
        setPropertyList(REPO_DIRS, new String[]{"https://svn.mza.com/svn/AandRcreations/repo", "https://svn.mza.com/svn/JupiterLib/AandRcreations/repo"});
        setProperty(REPO_DEFAULT, "https://svn.mza.com/svn/AandRcreations/repo");
        setProperty(FRAME_IS_MAXIMIZED, "true");
        setProperty(FRAME_WIDTH, "1500");
        setProperty(FRAME_HEIGHT, "800");
        setProperty(FRAME_LOC_X, "0");
        setProperty(FRAME_LOC_Y, "0");
        setProperty(FRAME_FILETREE_WIDTH, "300");
        setProperty(FRAME_FILETREE_HEIGHT, "500");
        setProperty(FRAME_PALETTE_WIDTH, "600");
        setProperty(FRAME_PALETTE_HEIGHT, "400");
        setProperty(FRAME_PROPS_WIDTH, "600");
        setProperty(COLOR_CONNECTION_OUTGOING_BACKGROUND, "#" + Integer.toHexString(OutputConnector.COLOR.getRGB()).substring(1));
        setProperty(COLOR_CONNECTION_OUTGOING_FOREGROUND, "#" + Integer.toHexString(Color.WHITE.getRGB()).substring(1));
        setProperty(COLOR_CONNECTION_INCOMING_BACKGROUND, "#" + Integer.toHexString(InputConnector.COLOR.getRGB()).substring(1));
        setProperty(COLOR_CONNECTION_INCOMING_FOREGROUND, "#" + Integer.toHexString(Color.WHITE.getRGB()).substring(1));
        setProperty(CONNECTOR_LINE_COLOR, "#" + Integer.toHexString(Color.BLUE.getRGB()).substring(1));
        setProperty(CONNECTOR_LINE_WEIGHT, "2.0f");
        setProperty(PALETTE_BACKGROUND, "#" + Integer.toHexString(Palette.COLOR.getRGB()).substring(1));
        setProperty(PALETTE_LINE_COLOR, "#" + Integer.toHexString(Palette.GRID_COLOR.getRGB()).substring(1));
        setProperty(PALETTE_LINE_SPACING, String.valueOf(Palette.GRID_LINE_SPACING));
        setProperty(PALETTE_GRID_TYPE, String.valueOf(Palette.GRID_LINES));
        setProperty(GENERAL_LAT_PATH, System.getProperty("user.home"));

        setProperty(DEVELOPERS_AUTHOR, System.getProperty("user.name"));
        setProperty(DEVELOPERS_PROJECT_FOLDER, System.getProperty("user.home") + File.separator + "LatizPlugins");
    }

    public void readPropertiesDocument() {
        try {
            load(new FileInputStream(propertiesFile));
        } catch (Exception e1) {
            saveProperties();
            readPropertiesDocument();
        }
    }

    public void saveProperties() {
        try {
            store(new FileOutputStream(propertiesFile), null);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static PropertiesManager getInstanceOf() {
        return instanceOf;
    }
}
