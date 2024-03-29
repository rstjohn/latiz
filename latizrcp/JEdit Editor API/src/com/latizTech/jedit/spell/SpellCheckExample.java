/*
Jazzy - a Java library for Spell Checking
Copyright (C) 2001 Mindaugas Idzelis
Full text of license can be found in LICENSE.txt

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
*/
package com.latizTech.jedit.spell;


import com.latizTech.jedit.spell.engine.SpellDictionary;
import com.latizTech.jedit.spell.engine.SpellDictionaryHashMap;
import com.latizTech.jedit.spell.event.SpellCheckEvent;
import com.latizTech.jedit.spell.event.SpellCheckListener;
import com.latizTech.jedit.spell.event.SpellChecker;
import com.latizTech.jedit.spell.event.StringWordTokenizer;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.List;

/** This class shows an example of how to use the spell checking capability.
 *
 * @author Jason Height (jheight@chariot.net.au)
 */
public class SpellCheckExample implements SpellCheckListener {

  private static String dictFile = "dict/english.0";
  private static String phonetFile = "dict/phonet.en";

  private SpellChecker spellCheck = null;


  public SpellCheckExample() {
    try {
      SpellDictionary dictionary = new SpellDictionaryHashMap(new File(dictFile), new File(phonetFile));

      spellCheck = new SpellChecker(dictionary);
      spellCheck.addSpellCheckListener(this);
      BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

      while (true) {
        System.out.print("Enter text to spell check: ");
        String line = in.readLine();

        if (line.length() <= 0)
          break;
        spellCheck.checkSpelling(new StringWordTokenizer(line));
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void spellingError(SpellCheckEvent event) {
    List suggestions = event.getSuggestions();
    if (suggestions.size() > 0) {
      System.out.println("MISSPELT WORD: " + event.getInvalidWord());
      for (Iterator suggestedWord = suggestions.iterator(); suggestedWord.hasNext();) {
        System.out.println("\tSuggested Word: " + suggestedWord.next());
      }
    } else {
      System.out.println("MISSPELT WORD: " + event.getInvalidWord());
      System.out.println("\tNo suggestions");
    }
    //Null actions
  }

  public static void main(String[] args) {
    new SpellCheckExample();
  }
}
