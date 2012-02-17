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
package com.latizTech.jedit.spell.swing;

import com.latizTech.jedit.spell.engine.SpellDictionaryHashMap;
import com.latizTech.jedit.spell.event.SpellCheckEvent;
import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

/** Implementation of a spell check dialog.
 *
 * @author Jason Height (jheight@chariot.net.au)
 */
public class JSpellDialog extends JDialog implements ActionListener, WindowListener {
  private JSpellForm form;
  private SpellCheckEvent event = null;

  public JSpellDialog(Frame owner, String title, boolean modal, SpellDictionaryHashMap dictionary) {
    super(owner, title, modal);
    form = new JSpellForm(dictionary);
    initialiseDialog();
  }

  public JSpellDialog(Dialog owner, String title, boolean modal) {
    super(owner, title, modal);
    initialiseDialog();
  }

  private void initialiseDialog() {
    getContentPane().add(form);
    form.addActionListener(this);
    addWindowListener(this);
    pack();
  }


  public void show(SpellCheckEvent e) {
    this.event = e;
    form.setSpellEvent(e);
    setVisible(true);
  }

  public void actionPerformed(ActionEvent e) {
    setVisible(false);
  }

  public void windowOpened(WindowEvent e) {
  }

  /** Cancel the event if the Dialog Close button is pressed*/
  public void windowClosing(WindowEvent e) {
    if (event != null)
      event.cancel();
  }

  public void windowClosed(WindowEvent e) {
  }

  public void windowIconified(WindowEvent e) {
  }

  public void windowDeiconified(WindowEvent e) {
  }

  public void windowActivated(WindowEvent e) {
  }

  public void windowDeactivated(WindowEvent e) {
  }
}
