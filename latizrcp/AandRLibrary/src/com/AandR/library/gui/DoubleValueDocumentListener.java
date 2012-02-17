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
package com.AandR.library.gui;

import java.awt.Color;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;

/**
 *
 */
public class DoubleValueDocumentListener implements DocumentListener, KeyListener, FocusListener {

    protected Color defaultColor;
    protected JTextField field;
    private String previousValue;

    public DoubleValueDocumentListener() {
    }

    public DoubleValueDocumentListener(JTextField field) {
        decorate(field);
    }

    public void decorate(JTextField field) {
        this.field = field;
        this.field.getDocument().addDocumentListener(this);
        this.field.addFocusListener(this);
        this.field.addKeyListener(this);
        this.previousValue = field.getText().trim();
        defaultColor = field.getForeground();
    }

    public void insertUpdate(DocumentEvent e) {
        updateField(e.getDocument());
    }

    public void removeUpdate(DocumentEvent e) {
        updateField(e.getDocument());
    }

    public void keyTyped(KeyEvent e) {
        if (e.getKeyChar() != '\n') return;
        field.setText(previousValue);
    }

    public void focusLost(FocusEvent e) {
        field.setText(previousValue);
    }
    // <editor-fold defaultstate="collapsed" desc="Unused implemented methods.">

    public void changedUpdate(DocumentEvent e) {
    }

    public void keyPressed(KeyEvent e) {
    }

    public void keyReleased(KeyEvent e) {
    }

    public void focusGained(FocusEvent e) {
    }// </editor-fold>

    protected void updateField(Document doc) {
        try {
            String s = doc.getText(0, doc.getLength());
            new Double(s);
            field.setForeground(defaultColor);
            previousValue = field.getText().trim();
        } catch (Exception ex) {
            field.setForeground(Color.RED);
        }
    }
}
