package com.latizTech.jedit;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import java.util.Properties;

/**
 * @author Dr. Richard St. John
 * @version $Revision: 1.2 $, $Date: 2007/09/11 01:12:01 $
 */
public class TextEditorProperties extends Properties {
  public static final String FONT_NAME  = "FONT_NAME";
  public static final String FONT_STYLE = "FONT_STYLE";
  public static final String FONT_SIZE  = "FONT_SIZE";
  public static final String TAB_COUNT  = "TAB_COUNT";
  public static final String LINE_WIDTH = "LINE_WIDTH"; 

  private File propertiesFile;

  public TextEditorProperties(File propertiesFile) {
    super();
    this.propertiesFile = propertiesFile;
    setDefaultProperties();
  }


  public void readPropertiesDocument() {
    if(propertiesFile==null) return;
    try {
      load(new FileInputStream(propertiesFile));
    } catch (Exception e) {
      saveProperties();
      readPropertiesDocument();
    }
  }
  
  
  private void setDefaultProperties() {
    setProperty(FONT_NAME, "Courier New");
    setProperty(FONT_STYLE, "0");
    setProperty(FONT_SIZE, "16");
    setProperty(TAB_COUNT, "2");
    setProperty(LINE_WIDTH, "150");
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
}
