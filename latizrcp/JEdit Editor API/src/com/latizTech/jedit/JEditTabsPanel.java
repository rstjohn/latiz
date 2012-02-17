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
package com.latizTech.jedit;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import com.AandR.library.gui.DropEvent;
import com.AandR.library.gui.DropListener;
import com.AandR.library.gui.JButtonWithDropSupport;
import com.AandR.library.gui.JLabelWithDropSupport;
import com.latizTech.jedit.resources.Resources;
import com.latizTech.jedit.tokenMarkers.TokenMarker;


/**
 * @author Dr. Richard St. John
 * @version $Revision: 1.7 $, $Date: 2007/09/15 02:35:25 $
 */
public class JEditTabsPanel extends JPanel {

  private static final String ACTION_NEW_TAB = "NEW_TAB";
  private static final String ACTION_RELOAD = "RELOAD";
  private static final String ACTION_OPEN = "OPEN";
  private static final String ACTION_NEW = "NEW";
  private static final String ACTION_SAVE = "SAVE";
  private static final String ACTION_SAVEAS = "SAVEAS";
  private static final String ACTION_BIGGER = "BIGGER";
  private static final String ACTION_SMALLER = "SMALLER";
  private static final String ACTION_FONT = "FONT";
  private static final String ACTION_COPY = "COPY";
  private static final String ACTION_CUT = "CUT";
  private static final String ACTION_PASTE = "PASTE";
  private static final String ACTION_UNDO = "UNDO";
  private static final String ACTION_REDO = "REDO";
  private static final String ACTION_FIND = "FIND";
  private static final String ACTION_VERTICAL_SPLIT = "ACTION_VERTICAL_SPLIT";
  private static final String ACTION_HORIZONTAL_SPLITS = "ACTION_HORIZONTAL_SPLITS";
  private static final String ACTION_REFORMAT_LINES = "ACTION_REFORMAT_LINES";

  private int tabCounter = 1, activeTabCount = 0;

  private JTabbedPane editorTabs;
  
  private JEditPanel editorWithFocus;
  
  private TextEditorListener textEditorListener;
  
  private TextEditorProperties properties;
  
  private TokenMarker tokenMarker;


  public JEditTabsPanel(File propertiesFile) {
    this(null, propertiesFile);
  }
  
  public JEditTabsPanel(TokenMarker tokenMarker, File propertiesFile) {
    super();
    properties = new TextEditorProperties(propertiesFile);
    properties.readPropertiesDocument();
    this.tokenMarker = tokenMarker;
    initialize();
    addTabs();
    setLayout(new BorderLayout());
    add(createButtonBar(), BorderLayout.NORTH);
    add(editorTabs, BorderLayout.CENTER);
  }
  
  private void addTabs() {
    activeTabCount = Integer.parseInt(properties.getProperty(TextEditorProperties.TAB_COUNT));
    for(int i=0; i<activeTabCount; i++) {
      addTab("Tab " +tabCounter++, createJEditPanel());
    }
  }

  private void initialize() {
    editorTabs = new JTabbedPane();
    editorTabs.setFont(editorTabs.getFont().deriveFont(10.0f));
    textEditorListener = new TextEditorListener();
  }

  public JEditPanel getEditorWithFocus() {
    return (JEditPanel)editorTabs.getSelectedComponent();
  }

  public JEditPanel getEditorAt(int index) {
    return (JEditPanel)editorTabs.getComponentAt(index);
  }
  

  public void updateEditorFont() {
    String fontName = properties.getProperty(TextEditorProperties.FONT_NAME); 
    String fontStyle = properties.getProperty(TextEditorProperties.FONT_STYLE); 
    String fontSize = properties.getProperty(TextEditorProperties.FONT_SIZE); 
    Font newFont= new Font(fontName, Integer.parseInt(fontStyle), Integer.parseInt(fontSize));
    
    int tabCount = editorTabs.getTabCount();
    JEditPanel thisEditor;
    for(int i=0; i<tabCount; i++) {
      thisEditor = getEditorAt(i);
      thisEditor.getTextArea().getPainter().setFont(newFont);
    }
  }

  
  public void addNewTab() {
    addTab("Tab "+tabCounter++, createJEditPanel());
    editorTabs.setSelectedIndex(editorTabs.getTabCount()-1);
  }
  
  
  private void addTab(String title, Component component) {
    editorTabs.addTab(null, component);
    editorTabs.setTabComponentAt(editorTabs.getTabCount()-1, createTabComponent(title));
  }

  private JPanel createTabComponent(String title) {
    final JPanel tabComponent = new JPanel();
    tabComponent.setLayout(new BoxLayout(tabComponent, BoxLayout.X_AXIS));
    tabComponent.setBorder(BorderFactory.createEmptyBorder(2, 0, 0, 0));
    tabComponent.setOpaque(false);

    JButton tabCloseButton = new JButton(Resources.createIcon("close10white.png"));
    tabCloseButton.setPreferredSize(new Dimension(12,12));
    tabCloseButton.setContentAreaFilled(false);
    tabCloseButton.setFocusable(false);
    tabCloseButton.setBorderPainted(false);
    tabCloseButton.setRolloverEnabled(true);
    tabCloseButton.setRolloverIcon(Resources.createIcon("close10.png"));
    tabCloseButton.setToolTipText("Close");
    tabCloseButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        int thisTabIndex = editorTabs.indexOfTabComponent(tabComponent);
        JEditPanel thisEditor = (JEditPanel)editorTabs.getComponentAt(thisTabIndex);
        if(!thisEditor.showConfirmDropDialog()) return;
        editorTabs.removeTabAt(thisTabIndex);
        properties.setProperty(TextEditorProperties.TAB_COUNT, String.valueOf(--activeTabCount));
        properties.saveProperties();
      }
    });

    final JLabelWithDropSupport tabLabel = new JLabelWithDropSupport(title);
    tabLabel.addDropListener(new DropListener() {
      public void dropAction(DropEvent dropEvent) {
        File file = new File(((String[])dropEvent.getDroppedItem())[0]);
        if(file.isDirectory()) return;
        int thisTabIndex = editorTabs.indexOfTabComponent(tabComponent);
        JEditPanel thisEditor = (JEditPanel)editorTabs.getComponentAt(thisTabIndex);
        if(!thisEditor.showConfirmDropDialog()) return;
        
        thisEditor.getTextArea().loadAsciiFile(file);
        ((JLabel)(dropEvent.getDropTargetContext()).getComponent()).setText(file.getName());
      }});
    tabLabel.setFont(tabLabel.getFont().deriveFont(10.0f));
    tabLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 14));

    tabComponent.add(tabLabel);
    tabComponent.add(tabCloseButton);
    return tabComponent;
  }

  public Font getSelectedEditorFont() {
    return ((JEditPanel)editorTabs.getSelectedComponent()).getTextArea().getFont();
  }

  public Font getSelectedEditorFontAt(int index) {
    return ((JEditPanel)editorTabs.getComponentAt(index)).getTextArea().getFont();
  }

  private JEditPanel createJEditPanel() {
    final JEditPanel textEditor = new JEditPanel(properties);
    textEditor.addJEditListener(new JEditListener() {
      public void fileChanged(boolean hasTextChanged) {}
      public void keyPressed(KeyEvent evt) {
        int keyCode = evt.getKeyCode();
        int modifiers = evt.getModifiers();
        
        int index = editorTabs.getSelectedIndex();
        if(keyCode==KeyEvent.VK_PAGE_DOWN && (modifiers & ~InputEvent.CTRL_DOWN_MASK)!=0) {
          editorTabs.setSelectedIndex(Math.min(++index, activeTabCount-1));
          ((JEditPanel)editorTabs.getSelectedComponent()).getTextArea().grabFocus();
        } else if(keyCode==KeyEvent.VK_PAGE_UP && (modifiers & ~InputEvent.CTRL_DOWN_MASK)!=0) {
          editorTabs.setSelectedIndex(Math.max(--index, 0));
          ((JEditPanel)editorTabs.getSelectedComponent()).getTextArea().grabFocus();
        }
      }
    });
    textEditor.getTextArea().setBorder(new LineBorder(getBackground()));
    textEditor.getTextArea().setTokenMarker(tokenMarker);
    textEditor.setMinimumSize(new Dimension(0,0));
    textEditor.addDropListener(new DropListener() {
      public void dropAction(DropEvent dropEvent) {
        textEditor.getTextArea().loadAsciiFile(new File(((String[])dropEvent.getDroppedItem())[0]));
      }});
    textEditor.getTextArea().addFocusListener(textEditorListener);
    
    String fontName = properties.getProperty(TextEditorProperties.FONT_NAME);
    String fontStyle = properties.getProperty(TextEditorProperties.FONT_STYLE);
    String fontSize = properties.getProperty(TextEditorProperties.FONT_SIZE);
    textEditor.getTextArea().getPainter().setFont(new Font(fontName, Integer.parseInt(fontStyle), Integer.parseInt(fontSize)));
    return textEditor;
  }

  private JPanel createButtonBar() {

    JPanel toolbar = new JPanel();
    toolbar.setLayout(new BoxLayout(toolbar, BoxLayout.X_AXIS));
    toolbar.add(GuiWorker.createToolbarButton(Resources.createIcon("reload16.png"), textEditorListener, ACTION_RELOAD, "Reload Files"));
    toolbar.add(createTabButton());
    toolbar.add(Box.createHorizontalStrut(25));
    toolbar.add(GuiWorker.createToolbarButton(Resources.createIcon("filenew16.png"), textEditorListener, ACTION_NEW, "New   ctrl+n"));
    toolbar.add(GuiWorker.createToolbarButton(Resources.createIcon("fileopen16.png"), textEditorListener, ACTION_OPEN, "Open   ctrl+o"));
    toolbar.add(GuiWorker.createToolbarButton(Resources.createIcon("filesave16.png"), textEditorListener, ACTION_SAVE, "Save   ctrl+s"));
    toolbar.add(GuiWorker.createToolbarButton(Resources.createIcon("filesaveas16.png"), textEditorListener, ACTION_SAVEAS, "Save As   ctrl+shift+a"));
    toolbar.add(GuiWorker.createToolbarButton(Resources.createIcon("fonts16.png"), textEditorListener, ACTION_FONT, "Change Font   ctrl+shift+f"));
    toolbar.add(GuiWorker.createToolbarButton(Resources.createIcon("fontsizeup16.png"), textEditorListener, ACTION_BIGGER, "Bigger Font   ctrl++"));
    toolbar.add(GuiWorker.createToolbarButton(Resources.createIcon("fontsizedown16.png"), textEditorListener, ACTION_SMALLER, "Smaller Font   ctrl+-"));
    toolbar.add(GuiWorker.createToolbarButton(Resources.createIcon("find16.png"), textEditorListener, ACTION_FIND, "Find...   ctrl+f"));
    toolbar.add(GuiWorker.createToolbarButton(Resources.createIcon("editcopy16.png"), textEditorListener, ACTION_COPY, "Copy   ctrl+c"));
    toolbar.add(GuiWorker.createToolbarButton(Resources.createIcon("editcut16.png"), textEditorListener, ACTION_CUT, "Cut   ctrl+x"));
    toolbar.add(GuiWorker.createToolbarButton(Resources.createIcon("editpaste16.png"), textEditorListener, ACTION_PASTE, "Paste   ctrl+v"));
    toolbar.add(GuiWorker.createToolbarButton(Resources.createIcon("undo16.png"), textEditorListener, ACTION_UNDO, "Undo   ctrl+z"));
    toolbar.add(GuiWorker.createToolbarButton(Resources.createIcon("redo16.png"), textEditorListener, ACTION_REDO, "Redo   ctrl+y"));
    toolbar.add(Box.createHorizontalStrut(25));
    toolbar.add(GuiWorker.createToolbarButton(Resources.createIcon("verticalSplit16.png"), textEditorListener, ACTION_HORIZONTAL_SPLITS, "Split Vertically"));
    toolbar.add(GuiWorker.createToolbarButton(Resources.createIcon("horizontalSplit16.png"), textEditorListener, ACTION_VERTICAL_SPLIT, "Split Horizontally"));
    toolbar.add(Box.createHorizontalStrut(25));
    toolbar.add(GuiWorker.createToolbarButton(Resources.createIcon("text_block16.png"), textEditorListener, ACTION_REFORMAT_LINES, "Reformat Lines"));
    toolbar.add(Box.createHorizontalGlue());
    toolbar.setBorder(new EmptyBorder(0,0,0,0));
    return toolbar;
  }

  private JButtonWithDropSupport createTabButton() {
    JButtonWithDropSupport button = GuiWorker.createToolbarButtonWithDropSupport(Resources.createIcon("newTab16.png"), textEditorListener, ACTION_NEW_TAB, "Add New Tab");
    button.addDropListener(new DropListener() {
      public void dropAction(DropEvent dropEvent) {
        File file = new File(((String[])dropEvent.getDroppedItem())[0]);
        if(file.isDirectory()) return;
        tabCounter++;
        addTab(file.getName(), createJEditPanel());
        editorTabs.setSelectedIndex(editorTabs.getTabCount()-1);
        ((JEditPanel)editorTabs.getSelectedComponent()).getTextArea().loadAsciiFile(file);
        properties.setProperty(TextEditorProperties.TAB_COUNT, String.valueOf(++activeTabCount));
        properties.saveProperties();
      }});
    return button;
  }

  private void actionReloadFiles() {
    int tabCount = editorTabs.getTabCount();
    JEditPanel thisEditor;
    for(int i=0; i<tabCount; i++) {
      thisEditor = (JEditPanel)editorTabs.getComponentAt(i); 
      if(thisEditor.getTextArea().getCurrentFile()==null) continue;
      int caretPos = thisEditor.getTextArea().getCaretPosition();
      thisEditor.getTextArea().loadAsciiFile(thisEditor.getTextArea().getCurrentFile());
      caretPos = Math.min(caretPos, thisEditor.getTextArea().getDocument().getLength());
      thisEditor.getTextArea().setCaretPosition(caretPos);
    }
  }

  /**
   * 
   * @param orientation
   */
  private void actionSplitPane(int orientation) {
    if(editorWithFocus==null) {
      editorWithFocus = (JEditPanel)editorTabs.getSelectedComponent();
    }
    
    JEditPanel oldEditor = createJEditPanel();
    if(editorWithFocus.getTextArea().getCurrentFile() != null) {
      oldEditor.getTextArea().setText(editorWithFocus.getTextArea().getText());
      oldEditor.getTextArea().setCurrentFile(editorWithFocus.getTextArea().getCurrentFile());
    }

    JSplitPane splitter = new JSplitPane(orientation);
    splitter.setLeftComponent(oldEditor);
    splitter.setRightComponent(createJEditPanel());

    editorWithFocus.removeAll();
    editorWithFocus.add(splitter);
    editorWithFocus.revalidate();
    if(orientation==JSplitPane.VERTICAL_SPLIT)
      splitter.setDividerLocation(editorWithFocus.getHeight()/2);
    else
      splitter.setDividerLocation(editorWithFocus.getWidth()/2);
    oldEditor.getTextArea().grabFocus();
  }


  private void setTabText(int k, String label) {
    ((JLabel)((JPanel)editorTabs.getTabComponentAt(k)).getComponent(0)).setText(label);
  }

  
  public JTabbedPane getEditorTabs() {
    return editorTabs;
  }


  public TextEditorProperties getProperties() {
    return properties;
  }


  /**
   * 
   * @author  <a href="mailto://rstjohn@mza.com">Dr. Richard St. John</a>
   * @company <a href="http://www.mza.com">MZA Associates Corporation</a>
   */
  private class TextEditorListener implements ActionListener, DropListener, FocusListener {

    public void actionPerformed(ActionEvent e) {
      String command = e.getActionCommand();
      if(command.equalsIgnoreCase(ACTION_NEW_TAB)) {
        addTab("Tab "+tabCounter++, createJEditPanel());
        properties.setProperty(TextEditorProperties.TAB_COUNT, String.valueOf(++activeTabCount));
        properties.saveProperties();
      } else if(command.equalsIgnoreCase(ACTION_RELOAD)) {
        actionReloadFiles();
      } else if(command.equalsIgnoreCase(ACTION_NEW)) {
        if(editorTabs.getTabCount()<1) {
          addTab("Tab "+tabCounter++, createJEditPanel());
          activeTabCount=1;
          properties.setProperty(TextEditorProperties.TAB_COUNT, String.valueOf(activeTabCount));
          properties.saveProperties();
        }
        JEditPanel textEditor = (JEditPanel)editorTabs.getSelectedComponent();
        JEditTextArea textArea = textEditor.getTextArea();
        textArea.dispatchEvent(new KeyEvent(textArea, KeyEvent.KEY_PRESSED, 0, InputEvent.CTRL_DOWN_MASK, KeyEvent.VK_N, KeyEvent.CHAR_UNDEFINED));
        textArea.dispatchEvent(new KeyEvent(textArea, KeyEvent.KEY_RELEASED, 0, InputEvent.CTRL_DOWN_MASK, KeyEvent.VK_N, KeyEvent.CHAR_UNDEFINED));
        setTabText(editorTabs.getSelectedIndex(), "File "+(editorTabs.getSelectedIndex()+1));
        textArea.requestFocus();
      } else if(command.equalsIgnoreCase(ACTION_OPEN)) {
        if(editorTabs.getTabCount()<1) {
          addTab("Tab "+tabCounter++, createJEditPanel());
          activeTabCount=1;
          properties.setProperty(TextEditorProperties.TAB_COUNT, String.valueOf(activeTabCount));
          properties.saveProperties();
        }
        JEditPanel textEditor = (JEditPanel)editorTabs.getSelectedComponent();
        JEditTextArea textArea = textEditor.getTextArea();
        textArea.dispatchEvent(new KeyEvent(textArea, KeyEvent.KEY_PRESSED, 0, InputEvent.CTRL_DOWN_MASK, KeyEvent.VK_O, KeyEvent.CHAR_UNDEFINED));
        textArea.dispatchEvent(new KeyEvent(textArea, KeyEvent.KEY_RELEASED, 0, InputEvent.CTRL_DOWN_MASK, KeyEvent.VK_O, KeyEvent.CHAR_UNDEFINED));
        if(textEditor.getTextArea().getCurrentFile()==null) return;
        setTabText(editorTabs.getSelectedIndex(), textEditor.getTextArea().getCurrentFile().getName());
        textArea.requestFocus();
      } else if(command.equalsIgnoreCase(ACTION_SAVE)) { 
        if(editorTabs.getTabCount()<1) return; 
        JEditPanel textEditor = (JEditPanel)editorTabs.getSelectedComponent();
        JEditTextArea textArea = textEditor.getTextArea();
        textArea.dispatchEvent(new KeyEvent(textArea, KeyEvent.KEY_PRESSED, 0, InputEvent.CTRL_DOWN_MASK, KeyEvent.VK_S, KeyEvent.CHAR_UNDEFINED));
        textArea.dispatchEvent(new KeyEvent(textArea, KeyEvent.KEY_RELEASED, 0, InputEvent.CTRL_DOWN_MASK, KeyEvent.VK_S, KeyEvent.CHAR_UNDEFINED));
        textArea.requestFocus();
      } else if(command.equalsIgnoreCase(ACTION_SAVEAS)) {
        if(editorTabs.getTabCount()<1) return; 
        JEditPanel textEditor = (JEditPanel)editorTabs.getSelectedComponent();
        JEditTextArea textArea = textEditor.getTextArea();
        textArea.dispatchEvent(new KeyEvent(textArea, KeyEvent.KEY_PRESSED, 0, InputEvent.CTRL_DOWN_MASK+InputEvent.SHIFT_DOWN_MASK, KeyEvent.VK_A, KeyEvent.CHAR_UNDEFINED));
        textArea.dispatchEvent(new KeyEvent(textArea, KeyEvent.KEY_RELEASED, 0, InputEvent.CTRL_DOWN_MASK+InputEvent.SHIFT_DOWN_MASK, KeyEvent.VK_A, KeyEvent.CHAR_UNDEFINED));
        setTabText(editorTabs.getSelectedIndex(), textEditor.getTextArea().getCurrentFile().getName());
        textArea.requestFocus();
      } else if(command.equalsIgnoreCase(ACTION_BIGGER)) {
        if(editorTabs.getTabCount()<1) return; 
        JEditPanel textEditor = (JEditPanel)editorTabs.getSelectedComponent();
        JEditTextArea textArea = textEditor.getTextArea();
        textArea.dispatchEvent(new KeyEvent(textArea, KeyEvent.KEY_PRESSED, 0, InputEvent.CTRL_DOWN_MASK, KeyEvent.VK_EQUALS, KeyEvent.CHAR_UNDEFINED));
        textArea.dispatchEvent(new KeyEvent(textArea, KeyEvent.KEY_RELEASED, 0, InputEvent.CTRL_DOWN_MASK, KeyEvent.VK_EQUALS, KeyEvent.CHAR_UNDEFINED));
        textArea.requestFocus();
      } else if(command.equalsIgnoreCase(ACTION_SMALLER)) {
        if(editorTabs.getTabCount()<1) return; 
        JEditPanel textEditor = (JEditPanel)editorTabs.getSelectedComponent();
        JEditTextArea textArea = textEditor.getTextArea();
        textArea.dispatchEvent(new KeyEvent(textArea, KeyEvent.KEY_PRESSED, 0, InputEvent.CTRL_DOWN_MASK, KeyEvent.VK_MINUS, KeyEvent.CHAR_UNDEFINED));
        textArea.dispatchEvent(new KeyEvent(textArea, KeyEvent.KEY_RELEASED, 0, InputEvent.CTRL_DOWN_MASK, KeyEvent.VK_MINUS, KeyEvent.CHAR_UNDEFINED));
        textArea.requestFocus();
      } else if(command.equalsIgnoreCase(ACTION_FONT)) {
        if(editorTabs.getTabCount()<1) return; 
        JEditPanel textEditor = (JEditPanel)editorTabs.getSelectedComponent();
        JEditTextArea textArea = textEditor.getTextArea();
        textArea.dispatchEvent(new KeyEvent(textArea, KeyEvent.KEY_PRESSED, 0, InputEvent.CTRL_DOWN_MASK+InputEvent.SHIFT_DOWN_MASK, KeyEvent.VK_F, KeyEvent.CHAR_UNDEFINED));
        textArea.dispatchEvent(new KeyEvent(textArea, KeyEvent.KEY_RELEASED, 0, InputEvent.CTRL_DOWN_MASK+InputEvent.SHIFT_DOWN_MASK, KeyEvent.VK_F, KeyEvent.CHAR_UNDEFINED));
        textArea.requestFocus();
      } else if(command.equalsIgnoreCase(ACTION_COPY)) {
        if(editorTabs.getTabCount()<1) return; 
        JEditPanel textEditor = (JEditPanel)editorTabs.getSelectedComponent();
        JEditTextArea textArea = textEditor.getTextArea();
        textArea.copy();
        textArea.requestFocus();
      } else if(command.equalsIgnoreCase(ACTION_CUT)) { 
        if(editorTabs.getTabCount()<1) return; 
        JEditPanel textEditor = (JEditPanel)editorTabs.getSelectedComponent();
        JEditTextArea textArea = textEditor.getTextArea();
        textArea.cut();
        textArea.requestFocus();
      } else if(command.equalsIgnoreCase(ACTION_PASTE)) { 
        if(editorTabs.getTabCount()<1) return; 
        JEditPanel textEditor = (JEditPanel)editorTabs.getSelectedComponent();
        JEditTextArea textArea = textEditor.getTextArea();
        textArea.paste();
        textArea.requestFocus();
      } else if(command.equalsIgnoreCase(ACTION_UNDO)) {
        if(editorTabs.getTabCount()<1) return; 
        JEditPanel textEditor = (JEditPanel)editorTabs.getSelectedComponent();
        JEditTextArea textArea = textEditor.getTextArea();
        textArea.dispatchEvent(new KeyEvent(textArea, KeyEvent.KEY_PRESSED, 0, InputEvent.CTRL_DOWN_MASK, KeyEvent.VK_Z, KeyEvent.CHAR_UNDEFINED));
        textArea.dispatchEvent(new KeyEvent(textArea, KeyEvent.KEY_RELEASED, 0, InputEvent.CTRL_DOWN_MASK, KeyEvent.VK_Z, KeyEvent.CHAR_UNDEFINED));
        textArea.requestFocus();
      } else if(command.equalsIgnoreCase(ACTION_REDO)) {
        if(editorTabs.getTabCount()<1) return; 
        JEditPanel textEditor = (JEditPanel)editorTabs.getSelectedComponent();
        JEditTextArea textArea = textEditor.getTextArea();
        textArea.dispatchEvent(new KeyEvent(textArea, KeyEvent.KEY_PRESSED, 0, InputEvent.CTRL_DOWN_MASK, KeyEvent.VK_Y, KeyEvent.CHAR_UNDEFINED));
        textArea.dispatchEvent(new KeyEvent(textArea, KeyEvent.KEY_RELEASED, 0, InputEvent.CTRL_DOWN_MASK, KeyEvent.VK_Y, KeyEvent.CHAR_UNDEFINED));
        textArea.requestFocus();
      } else if(command.equalsIgnoreCase(ACTION_FIND)) {
        if(editorTabs.getTabCount()<1) return; 
        JEditPanel textEditor = (JEditPanel)editorTabs.getSelectedComponent();
        JEditTextArea textArea = textEditor.getTextArea();
        textArea.dispatchEvent(new KeyEvent(textArea, KeyEvent.KEY_PRESSED, 0, InputEvent.CTRL_DOWN_MASK, KeyEvent.VK_F, KeyEvent.CHAR_UNDEFINED));
        textArea.dispatchEvent(new KeyEvent(textArea, KeyEvent.KEY_RELEASED, 0, InputEvent.CTRL_DOWN_MASK, KeyEvent.VK_F, KeyEvent.CHAR_UNDEFINED));
        textArea.requestFocus();
      } else if(command.equalsIgnoreCase(ACTION_VERTICAL_SPLIT)) {
        actionSplitPane(JSplitPane.VERTICAL_SPLIT);
      } else if(command.equalsIgnoreCase(ACTION_HORIZONTAL_SPLITS)) {
        actionSplitPane(JSplitPane.HORIZONTAL_SPLIT);
      } else if(command.equalsIgnoreCase(ACTION_REFORMAT_LINES)) {
        JEditPanel textEditor = (JEditPanel)editorTabs.getSelectedComponent();
        JEditTextArea textArea = textEditor.getTextArea();
        textArea.dispatchEvent(new KeyEvent(textArea, KeyEvent.KEY_PRESSED, 0, InputEvent.CTRL_DOWN_MASK, KeyEvent.VK_F9, KeyEvent.CHAR_UNDEFINED));
        textArea.dispatchEvent(new KeyEvent(textArea, KeyEvent.KEY_RELEASED, 0, InputEvent.CTRL_DOWN_MASK, KeyEvent.VK_F9, KeyEvent.CHAR_UNDEFINED));
        textArea.requestFocus();
      }
    }

    public void dropAction(DropEvent dropEvent) {
      File file = new File(((String[])dropEvent.getDroppedItem())[0]);
      if(file.isDirectory()) return;
      getEditorWithFocus().getTextArea().loadAsciiFile(file);
      setTabText(editorTabs.getSelectedIndex(), file.getName());
    }

    public void focusGained(FocusEvent e) {
      JEditTextArea area = (JEditTextArea)e.getSource();
      area.setBorder(new LineBorder(Color.GREEN));
      editorWithFocus = (JEditPanel)area.getParent();
    }

    public void focusLost(FocusEvent e) {
      JComponent area = (JComponent)e.getSource();
      area.setBorder(new LineBorder(getBackground()));
    }
  }
}
