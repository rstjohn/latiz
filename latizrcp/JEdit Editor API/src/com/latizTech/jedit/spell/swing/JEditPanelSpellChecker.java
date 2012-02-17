/*
 * Jazzy - a Java library for Spell Checking Copyright (C) 2001 Mindaugas Idzelis Full text of license can be found in LICENSE.txt This library is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version. This library is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public
 * License for more details. You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street,
 * Fifth Floor, Boston, MA 02110-1301 USA
 */
package com.latizTech.jedit.spell.swing;

import com.latizTech.jedit.JEditPanel;
import com.latizTech.jedit.spell.autospell.AutoSpellEditorKit;
import com.latizTech.jedit.spell.engine.SpellDictionary;
import com.latizTech.jedit.spell.engine.SpellDictionaryCachedDichoDisk;
import com.latizTech.jedit.spell.engine.SpellDictionaryHashMap;
import com.latizTech.jedit.spell.event.JEditWordTokenizer;
import com.latizTech.jedit.spell.event.SpellCheckEvent;
import com.latizTech.jedit.spell.event.SpellCheckListener;
import com.latizTech.jedit.spell.event.SpellChecker;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Window;
import java.io.File;
import java.io.IOException;
import javax.swing.JEditorPane;
import javax.swing.SwingUtilities;
import javax.swing.text.Document;
import javax.swing.text.EditorKit;
import javax.swing.text.StyledEditorKit;

/**
 * This class spellchecks a JTextComponent throwing up a Dialog everytime it encounters a misspelled word.
 * @author Robert Gustavsson (robert@lindesign.se)
 */

public class JEditPanelSpellChecker implements SpellCheckListener {

  // private static final String COMPLETED="COMPLETED";
  private String dialogTitle = null;

  private SpellChecker spellCheck = null;

  private JSpellDialog dlg = null;

  private JEditPanel textComp = null;

  private SpellDictionary mainDict = null;

  // Constructor
  public JEditPanelSpellChecker(SpellDictionary dict) {
    this(dict, null, null);
  }

  // Convinient Constructors, for those lazy guys.
  public JEditPanelSpellChecker(String dictFile) throws IOException {
    this(dictFile, null);
  }

  public JEditPanelSpellChecker(String dictFile, String title) throws IOException {
    this(new SpellDictionaryHashMap(new File(dictFile)), null, title);
  }

  public JEditPanelSpellChecker(String dictFile, String phoneticFile, String title) throws IOException {
    this(new SpellDictionaryHashMap(new File(dictFile), new File(phoneticFile)), null, title);
  }

  public JEditPanelSpellChecker(SpellDictionary dict, SpellDictionary userDict, String title) {
    spellCheck = new SpellChecker(dict);
    // spellCheck.
    mainDict = dict;
    spellCheck.setCache();
    if (userDict != null)
      spellCheck.setUserDictionary(userDict);
    spellCheck.addSpellCheckListener(this);
    dialogTitle = title;
    // messages = ResourceBundle.getBundle("com.swabunga.spell.swing.messages", Locale.getDefault());
    // markHandler=new AutoSpellCheckHandler(spellCheck, messages);
  }

  // MEMBER METHODS

  /**
   * Set user dictionary (used when a word is added)
   */
  public void setUserDictionary(SpellDictionary dictionary) {
    if (spellCheck != null)
      spellCheck.setUserDictionary(dictionary);
  }

  private void setupDialog(JEditPanel textComp) {

    Component comp = SwingUtilities.getRoot(textComp);

    // Probably the most common situation efter the first time.
    if (dlg != null && dlg.getOwner() == comp)
      return;

    if (comp != null && comp instanceof Window) {
      if (comp instanceof Frame)
        dlg = new JSpellDialog((Frame) comp, dialogTitle, true, (SpellDictionaryHashMap)mainDict);
      if (comp instanceof Dialog)
        dlg = new JSpellDialog((Dialog) comp, dialogTitle, true);
      // Put the dialog in the middle of it's parent.
      if (dlg != null) {
        Window win = (Window) comp;
        int x = (int) (win.getLocation().getX() + win.getWidth() / 2 - dlg.getWidth() / 2);
        int y = (int) (win.getLocation().getY() + win.getHeight() / 2 - dlg.getHeight() / 2);
        dlg.setLocation(x, y);
      }
    } else {
      dlg = new JSpellDialog((Frame) null, dialogTitle, true, (SpellDictionaryHashMap)mainDict);
    }
  }

  /**
   * This method is called to check the spelling of a JEditTextArea.
   * @param textComp The JTextComponent to spellcheck.
   * @return Either SpellChecker.SPELLCHECK_OK, SpellChecker.SPELLCHECK_CANCEL or the number of errors found. The number of errors are those that are found BEFORE any corrections are made.
   */
  public synchronized int spellCheck(JEditPanel textComp) {
    setupDialog(textComp);
    this.textComp = textComp;
    int exitStatus = 0;
    JEditWordTokenizer tokenizer = new JEditWordTokenizer(textComp);

    exitStatus = spellCheck.checkSpelling(tokenizer);

    textComp.requestFocus();
    this.textComp = null;
    try {
      if (mainDict instanceof SpellDictionaryCachedDichoDisk)
        ((SpellDictionaryCachedDichoDisk) mainDict).saveCache();
    } catch (IOException ex) {
      System.err.println(ex.getMessage());
    }
//    textComp.getTextArea().setCaretPosition(0);
    return exitStatus;
  }

  /**
   * @param pane
   */
  public void startAutoSpellCheck(JEditorPane pane) {
    Document doc = pane.getDocument();
    pane.setEditorKit(new AutoSpellEditorKit((StyledEditorKit) pane.getEditorKit()));
    pane.setDocument(doc);
    // markHandler.addJEditorPane(pane);
  }

  /**
   * @param pane
   */
  public void stopAutoSpellCheck(JEditorPane pane) {
    EditorKit kit;
    Document doc;
    if (pane.getEditorKit() instanceof com.latizTech.jedit.spell.autospell.AutoSpellEditorKit) {
      doc = pane.getDocument();
      kit = ((com.latizTech.jedit.spell.autospell.AutoSpellEditorKit) pane.getEditorKit()).getStyledEditorKit();
      pane.setEditorKit(kit);
      pane.setDocument(doc);
    }
    // markHandler.removeJEditorPane(pane);
  }

  /**
   * 
   */
  public void spellingError(SpellCheckEvent event) {

    // java.util.List suggestions = event.getSuggestions();
    event.getSuggestions();
    int start = event.getWordContextPosition();
    int end = start + event.getInvalidWord().length();

    // Mark the invalid word in JEditTextArea
    textComp.requestFocus();
    textComp.getTextArea().setCaretPosition(0);
    textComp.getTextArea().select(start, end);
    dlg.show(event);
  }
}
