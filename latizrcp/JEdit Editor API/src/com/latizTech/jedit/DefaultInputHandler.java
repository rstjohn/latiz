package com.latizTech.jedit;

/*
 * DefaultInputHandler.java - Default implementation of an input handler
 * Copyright (C) 1999 Slava Pestov
 *
 * You may use and modify this package for any purpose. Redistribution is
 * permitted, in both source and binary form, provided that this notice
 * remains intact in all source distributions of this package.
 */
import com.AandR.library.gui.FontChooser;
import javax.swing.JFileChooser;
import javax.swing.KeyStroke;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.undo.CannotUndoException;

import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.*;
import java.awt.Toolkit;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.StringTokenizer;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;

/**
 * The default input handler. It maps sequences of keystrokes into actions
 * and inserts key typed events into the text area.
 * @author Slava Pestov
 * @version $Id: DefaultInputHandler.java,v 1.5 2007/09/15 02:33:25 stjohnr Exp $
 */
@SuppressWarnings("unchecked")
public class DefaultInputHandler extends InputHandler {

    /**
     * If this client property is set to Boolean.TRUE on the text area, the home/end keys will support 'smart' BRIEF-like behaviour (one press = start/end of line, two presses = start/end of viewscreen,
     * three presses = start/end of document). By default, this property is not set.
     */
    public static final int TAB_LENGTH = 4;
    public static final String HOME_END_PROPERTY = "InputHandler.homeEnd";
    public static final ActionListener BACKSPACE = new backspace();
    public static final ActionListener BACKSPACE_WORD = new backspace_word();
    public static final ActionListener DELETE = new delete();
    public static final ActionListener DELETE_WORD = new delete_word();
    public static final ActionListener DELETE_LINE = new delete_line();
    public static final ActionListener COPY_UP = new copy_up();
    public static final ActionListener COPY_DOWN = new copy_down();
    public static final ActionListener MOVE_DOWN = new move_down();
    public static final ActionListener MOVE_UP = new move_up();
    public static final ActionListener NEW_LINE_ABOVE = new new_line_above();
    public static final ActionListener NEW_LINE_BELOW = new new_line_below();
    public static final ActionListener FONT_BIGGER = new font_bigger();
    public static final ActionListener FONT_SMALLER = new font_smaller();
    public static final ActionListener FONT_CHOOSER = new font_chooser();
    public static final ActionListener UNDO = new undo();
    public static final ActionListener REDO = new redo();
    public static final ActionListener GOTO_LINE = new goto_line();
    public static final ActionListener SCROLL_UP = new scroll_up();
    public static final ActionListener SCROLL_DOWN = new scroll_down();
    public final ActionListener OPEN = new open_file();
    public final ActionListener SAVE = new save_file();
    public final ActionListener SAVE_AS = new save_file_as();
    public static final ActionListener SELECT_ALL = new select_all();
    public static final ActionListener COPY = new copy();
    public static final ActionListener CUT = new cut();
    public static final ActionListener PASTE = new paste();
    public static final ActionListener PASTE_SPECIAL = new paste_special();
    public static final ActionListener REFORMAT_LINE = new reformat_line();
    public static final ActionListener FIND_REPLACE = new find_replace();
    public static final ActionListener END = new end(false);
    public static final ActionListener DOCUMENT_END = new document_end(false);
    public static final ActionListener SELECT_END = new end(true);
    public static final ActionListener SELECT_DOC_END = new document_end(true);
    public static final ActionListener INSERT_BREAK = new insert_break();
    public static final ActionListener INSERT_TAB = new insert_tab();
    public static final ActionListener HOME = new home(false);
    public static final ActionListener DOCUMENT_HOME = new document_home(false);
    public static final ActionListener SELECT_HOME = new home(true);
    public static final ActionListener SELECT_DOC_HOME = new document_home(true);
    public static final ActionListener NEXT_CHAR = new next_char(false);
    public static final ActionListener NEXT_LINE = new next_line(false);
    public static final ActionListener NEXT_PAGE = new next_page(false);
    public static final ActionListener NEXT_WORD = new next_word(false);
    public static final ActionListener SELECT_NEXT_CHAR = new next_char(true);
    public static final ActionListener SELECT_NEXT_LINE = new next_line(true);
    public static final ActionListener SELECT_NEXT_PAGE = new next_page(true);
    public static final ActionListener SELECT_NEXT_WORD = new next_word(true);
    public static final ActionListener OVERWRITE = new overwrite();
    public static final ActionListener PREV_CHAR = new prev_char(false);
    public static final ActionListener PREV_LINE = new prev_line(false);
    public static final ActionListener PREV_PAGE = new prev_page(false);
    public static final ActionListener PREV_WORD = new prev_word(false);
    public static final ActionListener SELECT_PREV_CHAR = new prev_char(true);
    public static final ActionListener SELECT_PREV_LINE = new prev_line(true);
    public static final ActionListener SELECT_PREV_PAGE = new prev_page(true);
    public static final ActionListener SELECT_PREV_WORD = new prev_word(true);
    public static final ActionListener TOGGLE_RECT = new toggle_rect();
    // Default action
    public static final ActionListener INSERT_CHAR = new insert_char();
    //LaTex Specific Added Actions
    public static final ActionListener PARAN_STRAIGHT = new paran_straight();
    public static final ActionListener PARAN_CURVED = new paran_curved();
    public static final ActionListener PARAN_CURLY = new paran_curly();
    public static final ActionListener FRAC = new frac();
    public static final ActionListener MATH = new math();
    public static final ActionListener INTEGRAL = new integral();
    public static final ActionListener REFERENCE = new reference();
    public static final ActionListener SYMBOL = new symbol();
    private ArrayList<JEditListener> listeners;

    /**
     * Creates a new input handler with no key bindings defined.
     */
    public DefaultInputHandler() {
        listeners = new ArrayList<JEditListener>();
        bindings = currentBindings = new Hashtable<KeyStroke, Object>();
    }

    public void addJEditListener(JEditListener listener) {
        listeners.add(listener);
    }

    /**
     * Sets up the default key bindings.
     */
    public void addDefaultKeyBindings() {
        addKeyBinding("BACK_SPACE", BACKSPACE);

        addKeyBinding("C+BACK_SPACE", BACKSPACE_WORD);

        addKeyBinding("DELETE", DELETE);

        addKeyBinding("C+DELETE", DELETE_WORD);

        addKeyBinding("C+D", DELETE_LINE);

        addKeyBinding("AC+UP", COPY_UP);

        addKeyBinding("AC+DOWN", COPY_DOWN);

        addKeyBinding("A+UP", MOVE_UP);

        addKeyBinding("A+DOWN", MOVE_DOWN);

        addKeyBinding("C+UP", SCROLL_UP);

        addKeyBinding("C+DOWN", SCROLL_DOWN);

        addKeyBinding("CS+ENTER", NEW_LINE_ABOVE);

        addKeyBinding("S+ENTER", NEW_LINE_BELOW);

        addKeyBinding("C+A", SELECT_ALL);

//    addKeyBinding("C+SPACE", new CodeCompletionKeyAction());

        addKeyBinding("C+MINUS", FONT_SMALLER);

        addKeyBinding("C+EQUALS", FONT_BIGGER);

        addKeyBinding("C+PLUS", FONT_BIGGER);

        addKeyBinding("CS+F", FONT_CHOOSER);

        addKeyBinding("C+Z", UNDO);

        addKeyBinding("C+Y", REDO);

        addKeyBinding("C+L", GOTO_LINE);

        addKeyBinding("C+F", FIND_REPLACE);

        addKeyBinding("CS+A", SAVE_AS);

        addKeyBinding("C+S", SAVE);

        addKeyBinding("C+O", OPEN);

        addKeyBinding("C+C", COPY);

        addKeyBinding("C+V", PASTE);

        addKeyBinding("C+X", CUT);

        addKeyBinding("CS+V", PASTE_SPECIAL);

        addKeyBinding("C+F9", REFORMAT_LINE);

        addKeyBinding("ENTER", INSERT_BREAK);

        addKeyBinding("TAB", INSERT_TAB);

        addKeyBinding("INSERT", OVERWRITE);

        addKeyBinding("C+\\", TOGGLE_RECT);

        addKeyBinding("HOME", HOME);

        addKeyBinding("END", END);

        addKeyBinding("S+HOME", SELECT_HOME);

        addKeyBinding("S+END", SELECT_END);

        addKeyBinding("C+HOME", DOCUMENT_HOME);

        addKeyBinding("C+END", DOCUMENT_END);

        addKeyBinding("CS+HOME", SELECT_DOC_HOME);

        addKeyBinding("CS+END", SELECT_DOC_END);

        addKeyBinding("PAGE_UP", PREV_PAGE);

        addKeyBinding("PAGE_DOWN", NEXT_PAGE);

        addKeyBinding("S+PAGE_UP", SELECT_PREV_PAGE);

        addKeyBinding("S+PAGE_DOWN", SELECT_NEXT_PAGE);

        addKeyBinding("LEFT", PREV_CHAR);

        addKeyBinding("S+LEFT", SELECT_PREV_CHAR);

        addKeyBinding("C+LEFT", PREV_WORD);

        addKeyBinding("CS+LEFT", SELECT_PREV_WORD);

        addKeyBinding("RIGHT", NEXT_CHAR);

        addKeyBinding("S+RIGHT", SELECT_NEXT_CHAR);

        addKeyBinding("C+RIGHT", NEXT_WORD);

        addKeyBinding("CS+RIGHT", SELECT_NEXT_WORD);

        addKeyBinding("UP", PREV_LINE);

        addKeyBinding("S+UP", SELECT_PREV_LINE);

        addKeyBinding("DOWN", NEXT_LINE);

        addKeyBinding("S+DOWN", SELECT_NEXT_LINE);

        addKeyBinding("C+ENTER", REPEAT);

        addKeyBinding("C+[", PARAN_STRAIGHT);

        addKeyBinding("C+9", PARAN_CURVED);

        addKeyBinding("CS+[", PARAN_CURLY);

        addKeyBinding("A+f", FRAC);

        addKeyBinding("A+m", MATH);

        addKeyBinding("A+i", INTEGRAL);

        addKeyBinding("A+r", REFERENCE);

        addKeyBinding("A+e", new EquationKeyAction());

        addKeyBinding("A+s", SYMBOL);
    }

    /**
     * Adds a key binding to this input handler. The key binding is
     * a list of white space separated key strokes of the form
     * <i>[modifiers+]key</i> where modifier is C for Control, A for Alt,
     * or S for Shift, and key is either a character (a-z) or a field
     * name in the KeyEvent class prefixed with VK_ (e.g., BACK_SPACE)
     * @param keyBinding The key binding
     * @param action The action
     */
    public void addKeyBinding(String keyBinding, ActionListener action) {
        Hashtable<KeyStroke, Object> current = bindings;

        StringTokenizer st = new StringTokenizer(keyBinding);
        while (st.hasMoreTokens()) {
            KeyStroke keyStroke = parseKeyStroke(st.nextToken());
            if (keyStroke == null) {
                return;
            }

            if (st.hasMoreTokens()) {
                Object o = current.get(keyStroke);
                if (o instanceof Hashtable) {
                    current = (Hashtable) o;
                } else {
                    o = new Hashtable<Object, Object>();
                    current.put(keyStroke, o);
                    current = (Hashtable) o;
                }
            } else {
                current.put(keyStroke, action);
            }
        }
    }

    /**
     * Removes a key binding from this input handler. This is not yet
     * implemented.
     * @param keyBinding The key binding
     */
    public void removeKeyBinding(String keyBinding) {
        bindings.remove(keyBinding);
        //throw new InternalError("Not yet implemented");
    }

    /**
     * Removes all key bindings from this input handler.
     */
    public void removeAllKeyBindings() {
        bindings.clear();
    }

    /**
     * Returns a copy of this input handler that shares the same
     * key bindings. Setting key bindings in the copy will also
     * set them in the original.
     */
    public InputHandler copy() {
        return new DefaultInputHandler(this);
    }

    /**
     * Handle a key pressed event. This will look up the binding for
     * the key stroke and execute it.
     */
    @Override
    public void keyPressed(KeyEvent evt) {
        int keyCode = evt.getKeyCode();
        int modifiers = evt.getModifiers();

        for (int i = 0; i < listeners.size(); i++) {
            listeners.get(i).keyPressed(evt);
        }

        if (keyCode == KeyEvent.VK_CONTROL || keyCode == KeyEvent.VK_SHIFT || keyCode == KeyEvent.VK_ALT || keyCode == KeyEvent.VK_META) {
            return;
        }

        if ((modifiers & ~InputEvent.SHIFT_MASK) != 0 || evt.isActionKey() || keyCode == KeyEvent.VK_BACK_SPACE || keyCode == KeyEvent.VK_DELETE || keyCode == KeyEvent.VK_ENTER
                || keyCode == KeyEvent.VK_TAB || keyCode == KeyEvent.VK_ESCAPE) {
            if (grabAction != null) {
                handleGrabAction(evt);
                return;
            }

            KeyStroke keyStroke = KeyStroke.getKeyStroke(keyCode, modifiers);
            Object o = currentBindings.get(keyStroke);
            if (o == null) {
                // Don't beep if the user presses some
                // key we don't know about unless a
                // prefix is active. Otherwise it will
                // beep when caps lock is pressed, etc.
                if (currentBindings != bindings) {
                    Toolkit.getDefaultToolkit().beep();
                    // F10 should be passed on, but C+e F10
                    // shouldn't
                    repeatCount = 0;
                    repeat = false;
                    evt.consume();
                }
                currentBindings = bindings;
                return;
            } else if (o instanceof ActionListener) {
                currentBindings = bindings;

                executeAction(((ActionListener) o), evt.getSource(), null);

                evt.consume();
                return;
            } else if (o instanceof Hashtable) {
                currentBindings = (Hashtable) o;
                evt.consume();
                return;
            }
        }
    }

    /**
     * Handle a key typed event. This inserts the key into the text area.
     */
    @Override
    public void keyTyped(KeyEvent evt) {
        int modifiers = evt.getModifiers();
        char c = evt.getKeyChar();
        if (c != KeyEvent.CHAR_UNDEFINED && (modifiers & InputEvent.ALT_MASK) == 0) {
            if (c >= 0x20 && c != 0x7f) {
                KeyStroke keyStroke = KeyStroke.getKeyStroke(Character.toUpperCase(c));
                Object o = currentBindings.get(keyStroke);

                if (o instanceof Hashtable) {
                    currentBindings = (Hashtable) o;
                    return;
                } else if (o instanceof ActionListener) {
                    currentBindings = bindings;
                    executeAction((ActionListener) o, evt.getSource(), String.valueOf(c));
                    return;
                }

                currentBindings = bindings;

                if (grabAction != null) {
                    handleGrabAction(evt);
                    return;
                }

                // 0-9 adds another 'digit' to the repeat number
                if (repeat && Character.isDigit(c)) {
                    repeatCount *= 10;
                    repeatCount += (c - '0');
                    return;
                }

                executeAction(INSERT_CHAR, evt.getSource(), String.valueOf(evt.getKeyChar()));

                repeatCount = 0;
                repeat = false;
            }
        }
    }

    /**
     * Converts a string to a keystroke. The string should be of the
     * form <i>modifiers</i>+<i>shortcut</i> where <i>modifiers</i>
     * is any combination of A for Alt, C for Control, S for Shift
     * or M for Meta, and <i>shortcut</i> is either a single character,
     * or a keycode name from the <code>KeyEvent</code> class, without
     * the <code>VK_</code> prefix.
     * @param keyStroke A string description of the key stroke
     */
    public static KeyStroke parseKeyStroke(String keyStroke) {
        if (keyStroke == null) {
            return null;
        }
        int modifiers = 0;
        int index = keyStroke.indexOf('+');
        if (index != -1) {
            for (int i = 0; i < index; i++) {
                switch (Character.toUpperCase(keyStroke.charAt(i))) {
                    case 'A':
                        modifiers |= InputEvent.ALT_MASK;
                        break;
                    case 'C':
                        modifiers |= InputEvent.CTRL_MASK;
                        break;
                    case 'M':
                        modifiers |= InputEvent.META_MASK;
                        break;
                    case 'S':
                        modifiers |= InputEvent.SHIFT_MASK;
                        break;
                }
            }
        }
        String key = keyStroke.substring(index + 1);
        if (key.length() == 1) {
            char ch = Character.toUpperCase(key.charAt(0));
            if (modifiers == 0) {
                return KeyStroke.getKeyStroke(ch);
            } else {
                return KeyStroke.getKeyStroke(ch, modifiers);
            }
        } else if (key.length() == 0) {
            System.err.println("Invalid key stroke: " + keyStroke);
            return null;
        } else {
            int ch;

            try {
                ch = KeyEvent.class.getField("VK_".concat(key)).getInt(null);
            } catch (Exception e) {
                System.err.println("Invalid key stroke: " + keyStroke);
                return null;
            }

            return KeyStroke.getKeyStroke(ch, modifiers);
        }
    }
    // private members
    private Hashtable<KeyStroke, Object> bindings;
    private Hashtable<KeyStroke, Object> currentBindings;

    private DefaultInputHandler(DefaultInputHandler copy) {
        bindings = currentBindings = copy.bindings;
    }

    public static class backspace implements ActionListener {

        public void actionPerformed(ActionEvent evt) {
            JEditTextArea textArea = getTextArea(evt);
            if (!textArea.isEditable()) {
                textArea.getToolkit().beep();
                return;
            }
            if (textArea.getSelectionStart() != textArea.getSelectionEnd()) {
                textArea.setSelectedText("");
            } else {
                int caret = textArea.getCaretPosition();
                if (caret == 0) {
                    textArea.getToolkit().beep();
                    return;
                }
                try {
                    textArea.getDocument().remove(caret - 1, 1);
                } catch (BadLocationException bl) {
                    bl.printStackTrace();
                }
            }
        }
    }

    public static class backspace_word implements ActionListener {

        public void actionPerformed(ActionEvent evt) {
            JEditTextArea textArea = getTextArea(evt);
            int start = textArea.getSelectionStart();
            if (start != textArea.getSelectionEnd()) {
                textArea.setSelectedText("");
            }

            int line = textArea.getCaretLine();
            int lineStart = textArea.getLineStartOffset(line);
            int caret = start - lineStart;

            String lineText = textArea.getLineText(textArea.getCaretLine());

            if (caret == 0) {
                if (lineStart == 0) {
                    textArea.getToolkit().beep();
                    return;
                }
                caret--;
            } else {
                String noWordSep = (String) textArea.getDocument().getProperty("noWordSep");
                caret = TextUtilities.findWordStart(lineText, caret, noWordSep);
            }

            try {
                textArea.getDocument().remove(caret + lineStart, start - (caret + lineStart));
            } catch (BadLocationException bl) {
                bl.printStackTrace();
            }
        }
    }

    public static class delete implements ActionListener {

        public void actionPerformed(ActionEvent evt) {
            JEditTextArea textArea = getTextArea(evt);

            if (!textArea.isEditable()) {
                textArea.getToolkit().beep();
                return;
            }

            if (textArea.getSelectionStart() != textArea.getSelectionEnd()) {
                textArea.setSelectedText("");
            } else {
                int caret = textArea.getCaretPosition();
                if (caret == textArea.getDocumentLength()) {
                    textArea.getToolkit().beep();
                    return;
                }
                try {
                    textArea.getDocument().remove(caret, 1);
                } catch (BadLocationException bl) {
                    bl.printStackTrace();
                }
            }
        }
    }

    public static class move_down implements ActionListener {

        public void actionPerformed(ActionEvent evt) {
            JEditTextArea textArea = getTextArea(evt);
            if (!textArea.isEditable()) {
                textArea.getToolkit().beep();
                return;
            }

            int caret = textArea.getCaretPosition();
            if (caret == textArea.getDocumentLength()) {
                textArea.getToolkit().beep();
                return;
            }

            int startLineNumber = textArea.getLineOfOffset(textArea.getSelectionStart());
            int endLineNumber = textArea.getLineOfOffset(textArea.getSelectionEnd());
            if (endLineNumber == textArea.getLineCount() - 2) {
                return;
            }

            int startPosition = textArea.getLineStartOffset(startLineNumber);
            int endPosition = textArea.getLineEndOffset(endLineNumber);
            String currentSelection = textArea.getText(startPosition, endPosition - startPosition);

            textArea.select(textArea.getLineEndOffset(endLineNumber + 1), textArea.getLineEndOffset(endLineNumber + 1));
            textArea.setSelectedText(currentSelection);
            textArea.select(startPosition, endPosition);
            textArea.setSelectedText("");

            startPosition = textArea.getLineStartOffset(startLineNumber + 1);
            endPosition = textArea.getLineEndOffset(endLineNumber + 1) - 1;
            textArea.select(startPosition, endPosition);
        }
    }

    public static class new_line_above implements ActionListener {

        public void actionPerformed(ActionEvent evt) {
            JEditTextArea textArea = getTextArea(evt);
            if (!textArea.isEditable()) {
                textArea.getToolkit().beep();
                return;
            }

            int caret = textArea.getCaretPosition();
            if (caret == textArea.getDocumentLength()) {
                textArea.getToolkit().beep();
                return;
            }
            int currentLineNumber = textArea.getLineOfOffset(textArea.getCaretPosition());
            int startPosition = textArea.getLineStartOffset(currentLineNumber);
            textArea.select(startPosition, startPosition);
            textArea.setSelectedText("\n");
            textArea.setCaretPosition(startPosition);
            textArea.getLineNumberPanel().lineInserted(currentLineNumber+1);
        }
    }

    public static class new_line_below implements ActionListener {

        public void actionPerformed(ActionEvent evt) {
            JEditTextArea textArea = getTextArea(evt);
            if (!textArea.isEditable()) {
                textArea.getToolkit().beep();
                return;
            }

            int len = textArea.getDocumentLength();
            int caret = textArea.getCaretPosition();
            int currentLineNumber = textArea.getLineOfOffset(caret);
            int lineEndOffset = textArea.getLineEndOffset(currentLineNumber);

            int startPosition, selectPosition;
            if (lineEndOffset > len) {
                startPosition = len;
                selectPosition = len + 1;
            } else {
                startPosition = textArea.getLineEndOffset(currentLineNumber);
                selectPosition = startPosition;
            }
            textArea.select(startPosition, startPosition);
            textArea.setSelectedText("\n");
            textArea.setCaretPosition(selectPosition);
            textArea.getLineNumberPanel().lineInserted(currentLineNumber+1);
        }
    }

    public static class move_up implements ActionListener {

        public void actionPerformed(ActionEvent evt) {
            JEditTextArea textArea = getTextArea(evt);
            if (!textArea.isEditable()) {
                textArea.getToolkit().beep();
                return;
            }

            int caret = textArea.getCaretPosition();
            if (caret == textArea.getDocumentLength()) {
                textArea.getToolkit().beep();
                return;
            }

            int startLineNumber = textArea.getLineOfOffset(textArea.getSelectionStart());
            if (startLineNumber == 0) {
                return;
            }

            int endLineNumber = textArea.getLineOfOffset(textArea.getSelectionEnd());
            int startPosition = textArea.getLineStartOffset(startLineNumber);
            int endPosition = textArea.getLineEndOffset(endLineNumber);
            String currentSelection = textArea.getText(startPosition, endPosition - startPosition);

            textArea.select(startPosition, endPosition);
            textArea.setSelectedText("");
            textArea.select(textArea.getLineStartOffset(startLineNumber - 1), textArea.getLineStartOffset(startLineNumber - 1));
            textArea.setSelectedText(currentSelection);

            startPosition = textArea.getLineStartOffset(startLineNumber - 1);
            endPosition = textArea.getLineEndOffset(endLineNumber - 1) - 1;
            textArea.select(startPosition, endPosition);
        }
    }

    public static class copy_up implements ActionListener {

        public void actionPerformed(ActionEvent evt) {
            JEditTextArea textArea = getTextArea(evt);
            if (!textArea.isEditable()) {
                textArea.getToolkit().beep();
                return;
            }

            int caret = textArea.getCaretPosition();
            if (caret == textArea.getDocumentLength()) {
                textArea.getToolkit().beep();
                return;
            }

            int startLineNumber = textArea.getLineOfOffset(textArea.getSelectionStart());
            int endLineNumber = textArea.getLineOfOffset(textArea.getSelectionEnd());
            int startPosition = textArea.getLineStartOffset(startLineNumber);
            int endPosition = textArea.getLineEndOffset(endLineNumber);
            String currentSelection = textArea.getText(startPosition, endPosition - startPosition);
            textArea.select(textArea.getLineStartOffset(startLineNumber), textArea.getLineStartOffset(startLineNumber));
            textArea.setSelectedText(currentSelection);
            textArea.select(startPosition, endPosition - 1);
            textArea.getLineNumberPanel().linesInserted(endLineNumber, endLineNumber + (endLineNumber - startLineNumber));
        }
    }

    public static class copy_down implements ActionListener {

        public void actionPerformed(ActionEvent evt) {
            JEditTextArea textArea = getTextArea(evt);
            if (!textArea.isEditable()) {
                textArea.getToolkit().beep();
                return;
            }

            int caret = textArea.getCaretPosition();
            if (caret == textArea.getDocumentLength()) {
                textArea.getToolkit().beep();
                return;
            }

            int startLineNumber = textArea.getLineOfOffset(textArea.getSelectionStart());
            int endLineNumber = textArea.getLineOfOffset(textArea.getSelectionEnd());
            int startPosition = textArea.getLineStartOffset(startLineNumber);
            int endPosition = textArea.getLineEndOffset(endLineNumber);
            String currentSelection = textArea.getText(startPosition, endPosition - startPosition);

            textArea.select(textArea.getLineEndOffset(endLineNumber), textArea.getLineEndOffset(endLineNumber));
            textArea.setSelectedText(currentSelection);
            startPosition = textArea.getLineStartOffset(endLineNumber + 1);
            endPosition = startPosition + currentSelection.length() - 1;
            textArea.select(startPosition, endPosition);
            textArea.getLineNumberPanel().linesInserted(endLineNumber, endLineNumber + (endLineNumber - startLineNumber));
        }
    }

    public static class font_bigger implements ActionListener {

        public void actionPerformed(ActionEvent evt) {
            JEditTextArea textArea = getTextArea(evt);
            float currentFontSize = (float) textArea.painter.getFont().getSize();
            textArea.setTextFont(textArea.painter.getFont().deriveFont(++currentFontSize));
        }
    }

    public static class font_smaller implements ActionListener {

        public void actionPerformed(ActionEvent evt) {
            JEditTextArea textArea = getTextArea(evt);
            float currentFontSize = (float) textArea.painter.getFont().getSize();
            textArea.setTextFont(textArea.painter.getFont().deriveFont(--currentFontSize));
        }
    }

    public static class font_chooser implements ActionListener {

        public void actionPerformed(ActionEvent evt) {
            JEditTextArea textArea = getTextArea(evt);
            FontChooser fontChooser = new FontChooser(textArea.painter.getFont());
            if (fontChooser.isCancelled()) {
                return;
            }
            textArea.painter.setFont(fontChooser.getSelectedFont());
            textArea.setTextFont(fontChooser.getSelectedFont());
        }
    }

    public static class undo implements ActionListener {

        public void actionPerformed(ActionEvent evt) {
            JEditTextArea textArea = getTextArea(evt);
            try {
                if (textArea.getUndoManager().canUndo()) {
                    textArea.getUndoManager().undo();
                }
            } catch (CannotUndoException e) {
            }
        }
    }

    public static class redo implements ActionListener {

        public void actionPerformed(ActionEvent evt) {
            JEditTextArea textArea = getTextArea(evt);
            try {
                if (textArea.getUndoManager().canRedo()) {
                    textArea.getUndoManager().redo();
                }
            } catch (CannotUndoException e) {
            }
        }
    }

    public static class goto_line implements ActionListener {

        public void actionPerformed(ActionEvent evt) {
            JEditTextArea textArea = getTextArea(evt);

            NotifyDescriptor.InputLine nd = new NotifyDescriptor.InputLine("Goto Line", "Line Number");
            Object ansr = DialogDisplayer.getDefault().notify(nd);
            if (ansr == NotifyDescriptor.CANCEL_OPTION) {
                return;
            }
            String nameExtension = nd.getInputText();

            if ((nameExtension != null) && (nameExtension.trim().length() > 0)) {
                try {
                    int lineno = Integer.valueOf(nameExtension).intValue() - 1;
                    if (lineno <= textArea.getLineCount() && lineno > 0) {
                        Element elemen3 = textArea.getDocument().getDefaultRootElement();
                        Element elemen4 = elemen3.getElement(lineno);
                        textArea.select((elemen4.getStartOffset()), elemen4.getEndOffset() - 1);
                    }
                } catch (NumberFormatException e) {
                }
            }
        }
    }

    public static class scroll_up implements ActionListener {

        public void actionPerformed(ActionEvent evt) {
            JEditTextArea textArea = getTextArea(evt);
            int firstLine = textArea.getFirstLine();
            if (firstLine == 0) {
                return;
            }
            textArea.setFirstLine(firstLine - 1);
        }
    }

    public static class scroll_down implements ActionListener {

        public void actionPerformed(ActionEvent evt) {
            JEditTextArea textArea = getTextArea(evt);
            int firstLine = textArea.getFirstLine();
            int lineCount = textArea.getLineCount();
            int line = firstLine == (lineCount - 1) ? (lineCount - 1) : firstLine + 1;
            textArea.setFirstLine(line);
        }
    }

    public static class open_file implements ActionListener {

        public void actionPerformed(ActionEvent evt) {
            JEditTextArea textArea = getTextArea(evt);
            JFileChooser fc = new JFileChooser(textArea.getCurrentDirectory());
            int choice = fc.showOpenDialog(null);
            if (choice == JFileChooser.CANCEL_OPTION) {
                return;
            }
            textArea.setCurrentDirectory(fc.getCurrentDirectory().getPath());
            textArea.loadAsciiFile(fc.getSelectedFile());
            fc = null;
        }
    }

    public class save_file implements ActionListener {

        public void actionPerformed(ActionEvent evt) {
            JEditTextArea textArea = getTextArea(evt);
            textArea.saveAsciiFile();
        }
    }

    public static class copy implements ActionListener {

        public void actionPerformed(ActionEvent evt) {
            JEditTextArea textArea = getTextArea(evt);
            textArea.copy();
        }
    }

    public static class cut implements ActionListener {

        public void actionPerformed(ActionEvent evt) {
            JEditTextArea textArea = getTextArea(evt);
            textArea.cut();
        }
    }

    public static class paste implements ActionListener {

        public void actionPerformed(ActionEvent evt) {
            JEditTextArea textArea = getTextArea(evt);
            textArea.paste();
        }
    }

    public static class select_all implements ActionListener {

        public void actionPerformed(ActionEvent evt) {
            getTextArea(evt).selectAll();
        }
    }

    public static class reformat_line implements ActionListener {

        public void actionPerformed(ActionEvent evt) {
            JEditTextArea textArea = getTextArea(evt);
            String selectedText = textArea.getSelectedText();
            if (selectedText == null || selectedText.length() < 2) {
                return;
            }
            int lineWidth = 180;
            String newString = selectedText.replace('\n', ' ');
            StringTokenizer tokens = new StringTokenizer(newString);
            String thisToken;
            int charCount = 0;
            String modifiedString = "";
            while (tokens.hasMoreTokens()) {
                thisToken = tokens.nextToken();
                charCount += thisToken.length();
                modifiedString += thisToken + " ";
                if (charCount > lineWidth) {
                    modifiedString += "\n";
                    charCount = 0;
                }
            }
            textArea.setSelectedText(modifiedString);
        }
    }

    public static class paste_special implements ActionListener {

        public void actionPerformed(ActionEvent evt) {
            JEditTextArea textArea = getTextArea(evt);
            PasteSpecialDialog pasteSpeicalDialog = new PasteSpecialDialog();

            if (pasteSpeicalDialog.isCancelled()) {
                return;
            }

            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            Transferable contents = clipboard.getContents(null);
            String result = "";
            boolean hasTransferableText = (contents != null) && contents.isDataFlavorSupported(DataFlavor.stringFlavor);
            if (hasTransferableText) {
                try {
                    result = (String) contents.getTransferData(DataFlavor.stringFlavor);
                    StringBuilder newString = new StringBuilder();
                    StringTokenizer tokenizer = new StringTokenizer(result, pasteSpeicalDialog.getCopyDelimiter());
                    while (tokenizer.hasMoreTokens()) {
                        newString.append(tokenizer.nextToken());
                        if (tokenizer.hasMoreTokens()) {
                            newString.append(pasteSpeicalDialog.getPasteDelimiter());
                        }
                    }
                    textArea.select(textArea.getCaretPosition(), textArea.getCaretPosition());
                    textArea.setSelectedText(newString.toString());
                } catch (UnsupportedFlavorException ex) {
                    //highly unlikely since we are using a standard DataFlavor
                    System.out.println(ex);
                    ex.printStackTrace();
                } catch (IOException ex) {
                    System.out.println(ex);
                    ex.printStackTrace();
                }
            }
        }
    }

    public static class find_replace implements ActionListener {

        public void actionPerformed(ActionEvent evt) {
            JEditTextArea textArea = getTextArea(evt);
            textArea.getFindReplaceDialog().setVisible(true, textArea);
        }
    }

    public static class save_file_as implements ActionListener {

        public void actionPerformed(ActionEvent evt) {
            JEditTextArea textArea = getTextArea(evt);
            JFileChooser fileChooser = new JFileChooser(textArea.getCurrentDirectory());
            int choice = fileChooser.showSaveDialog(null);
            if (choice == JFileChooser.CANCEL_OPTION) {
                return;
            }
            textArea.setCurrentDirectory(fileChooser.getCurrentDirectory().getPath());
            textArea.saveAsciiFileAs(fileChooser.getSelectedFile());
        }
    }

    public static class delete_line implements ActionListener {

        public void actionPerformed(ActionEvent evt) {
            JEditTextArea textArea = getTextArea(evt);
            if (!textArea.isEditable()) {
                textArea.getToolkit().beep();
                return;
            }

            if (textArea.getSelectionStart() != textArea.getSelectionEnd()) {
                int startLineNumber = textArea.getSelectionStartLine();
                int endLineNumber = textArea.getSelectionEndLine();
                textArea.getLineNumberPanel().linesRemoved(startLineNumber + 1, endLineNumber);
                textArea.setSelectedText("");
            } else {

                int caret = textArea.getCaretPosition();
                if (caret == textArea.getDocumentLength()) {
                    textArea.getToolkit().beep();
                    return;
                }
                try {
                    int startLineNumber = textArea.getLineOfOffset(textArea.getSelectionStart());
                    int endLineNumber = textArea.getLineOfOffset(textArea.getSelectionEnd());
                    int startPosition = textArea.getLineStartOffset(startLineNumber);
                    int endPosition = textArea.getLineEndOffset(endLineNumber);
                    textArea.getDocument().remove(startPosition, endPosition - startPosition);
                    textArea.getLineNumberPanel().linesRemoved(startLineNumber + 1, endLineNumber + 1);
                } catch (BadLocationException bl) {
                    bl.printStackTrace();
                }
            }
        }
    }

    public static class delete_word implements ActionListener {

        public void actionPerformed(ActionEvent evt) {
            JEditTextArea textArea = getTextArea(evt);
            int start = textArea.getSelectionStart();
            if (start != textArea.getSelectionEnd()) {
                textArea.setSelectedText("");
            }

            int line = textArea.getCaretLine();
            int lineStart = textArea.getLineStartOffset(line);
            int caret = start - lineStart;

            String lineText = textArea.getLineText(textArea.getCaretLine());

            if (caret == lineText.length()) {
                if (lineStart + caret == textArea.getDocumentLength()) {
                    textArea.getToolkit().beep();
                    return;
                }
                caret++;
            } else {
                String noWordSep = (String) textArea.getDocument().getProperty("noWordSep");
                caret = TextUtilities.findWordEnd(lineText, caret, noWordSep);
            }

            try {
                textArea.getDocument().remove(start, (caret + lineStart) - start);
            } catch (BadLocationException bl) {
                bl.printStackTrace();
            }
        }
    }

    public static class end implements ActionListener {

        private boolean select;

        public end(boolean select) {
            this.select = select;
        }

        public void actionPerformed(ActionEvent evt) {
            JEditTextArea textArea = getTextArea(evt);

            int caret = textArea.getCaretPosition();

            int lastOfLine = textArea.getLineEndOffset(textArea.getCaretLine()) - 1;
            int lastVisibleLine = textArea.getFirstLine() + textArea.getVisibleLines();
            if (lastVisibleLine >= textArea.getLineCount()) {
                lastVisibleLine = Math.min(textArea.getLineCount() - 1, lastVisibleLine);
            } else {
                lastVisibleLine -= (textArea.getElectricScroll() + 1);
            }

            int lastVisible = textArea.getLineEndOffset(lastVisibleLine) - 1;
            int lastDocument = textArea.getDocumentLength();

            if (caret == lastDocument) {
                textArea.getToolkit().beep();
                return;
            } else if (!Boolean.TRUE.equals(textArea.getClientProperty(HOME_END_PROPERTY))) {
                caret = lastOfLine;
            } else if (caret == lastVisible) {
                caret = lastDocument;
            } else if (caret == lastOfLine) {
                caret = lastVisible;
            } else {
                caret = lastOfLine;
            }

            if (select) {
                textArea.select(textArea.getMarkPosition(), caret);
            } else {
                textArea.setCaretPosition(caret);
            }
        }
    }

    public static class document_end implements ActionListener {

        private boolean select;

        public document_end(boolean select) {
            this.select = select;
        }

        public void actionPerformed(ActionEvent evt) {
            JEditTextArea textArea = getTextArea(evt);
            if (select) {
                textArea.select(textArea.getMarkPosition(), textArea.getDocumentLength());
            } else {
                textArea.setCaretPosition(textArea.getDocumentLength());
            }
        }
    }

    public static class home implements ActionListener {

        private boolean select;

        public home(boolean select) {
            this.select = select;
        }

        public void actionPerformed(ActionEvent evt) {
            JEditTextArea textArea = getTextArea(evt);

            int caret = textArea.getCaretPosition();

            int firstLine = textArea.getFirstLine();

            int firstOfLine = textArea.getLineStartOffset(textArea.getCaretLine());
            int firstVisibleLine = (firstLine == 0 ? 0 : firstLine + textArea.getElectricScroll());
            int firstVisible = textArea.getLineStartOffset(firstVisibleLine);

            if (caret == 0) {
                textArea.getToolkit().beep();
                return;
            } else if (!Boolean.TRUE.equals(textArea.getClientProperty(HOME_END_PROPERTY))) {
                caret = firstOfLine;
            } else if (caret == firstVisible) {
                caret = 0;
            } else if (caret == firstOfLine) {
                caret = firstVisible;
            } else {
                caret = firstOfLine;
            }

            if (select) {
                textArea.select(textArea.getMarkPosition(), caret);
            } else {
                textArea.setCaretPosition(caret);
            }
        }
    }

    public static class document_home implements ActionListener {

        private boolean select;

        public document_home(boolean select) {
            this.select = select;
        }

        public void actionPerformed(ActionEvent evt) {
            JEditTextArea textArea = getTextArea(evt);
            if (select) {
                textArea.select(textArea.getMarkPosition(), 0);
            } else {
                textArea.setCaretPosition(0);
            }
        }
    }

    public static class insert_break implements ActionListener {

        public void actionPerformed(ActionEvent evt) {
            JEditTextArea textArea = getTextArea(evt);

            if (!textArea.isEditable()) {
                textArea.getToolkit().beep();
                return;
            }

            textArea.setSelectedText("\n");
        }
    }

    public static class insert_tab implements ActionListener {

        public void actionPerformed(ActionEvent evt) {
            JEditTextArea textArea = getTextArea(evt);

            if (!textArea.isEditable()) {
                textArea.getToolkit().beep();
                return;
            }

            String tab = "";
            for (int i = 0; i < TAB_LENGTH; i++) {
                tab += " ";
            }
            textArea.overwriteSetSelectedText(tab);
        }
    }

    public static class next_char implements ActionListener {

        private boolean select;

        public next_char(boolean select) {
            this.select = select;
        }

        public void actionPerformed(ActionEvent evt) {
            JEditTextArea textArea = getTextArea(evt);
            int caret = textArea.getCaretPosition();
            if (caret == textArea.getDocumentLength()) {
                textArea.getToolkit().beep();
                return;
            }

            if (select) {
                textArea.select(textArea.getMarkPosition(), caret + 1);
            } else {
                textArea.setCaretPosition(caret + 1);
            }
        }
    }

    public static class next_line implements ActionListener {

        private boolean select;

        public next_line(boolean select) {
            this.select = select;
        }

        public void actionPerformed(ActionEvent evt) {
            JEditTextArea textArea = getTextArea(evt);

            int caret = textArea.getCaretPosition();
            int line = textArea.getCaretLine();

            if (line == textArea.getLineCount() - 1) {
                textArea.getToolkit().beep();
                return;
            }

            int magic = textArea.getMagicCaretPosition();
            if (magic == -1) {
                magic = textArea.offsetToX(line, caret - textArea.getLineStartOffset(line));
            }

            caret = textArea.getLineStartOffset(line + 1) + textArea.xToOffset(line + 1, magic);
            if (select) {
                textArea.select(textArea.getMarkPosition(), caret);
            } else {
                textArea.setCaretPosition(caret);
            }
            textArea.setMagicCaretPosition(magic);
        }
    }

    public static class next_page implements ActionListener {

        private boolean select;

        public next_page(boolean select) {
            this.select = select;
        }

        public void actionPerformed(ActionEvent evt) {
            JEditTextArea textArea = getTextArea(evt);
            int lineCount = textArea.getLineCount();
            int firstLine = textArea.getFirstLine();
            int visibleLines = textArea.getVisibleLines();
            int line = textArea.getCaretLine();

            firstLine += visibleLines;

            if (firstLine + visibleLines >= lineCount - 1) {
                firstLine = lineCount - visibleLines;
            }

            textArea.setFirstLine(firstLine);

            int caret = textArea.getLineStartOffset(Math.min(textArea.getLineCount() - 1, line + visibleLines));
            if (select) {
                textArea.select(textArea.getMarkPosition(), caret);
            } else {
                textArea.setCaretPosition(caret);
            }
        }
    }

    public static class next_word implements ActionListener {

        private boolean select;

        public next_word(boolean select) {
            this.select = select;
        }

        public void actionPerformed(ActionEvent evt) {
            JEditTextArea textArea = getTextArea(evt);
            int caret = textArea.getCaretPosition();
            int line = textArea.getCaretLine();
            int lineStart = textArea.getLineStartOffset(line);
            caret -= lineStart;

            String lineText = textArea.getLineText(textArea.getCaretLine());

            if (caret == lineText.length()) {
                if (lineStart + caret == textArea.getDocumentLength()) {
                    textArea.getToolkit().beep();
                    return;
                }
                caret++;
            } else {
                String noWordSep = (String) textArea.getDocument().getProperty("noWordSep");
                caret = TextUtilities.findWordEnd(lineText, caret, noWordSep);
            }

            if (select) {
                textArea.select(textArea.getMarkPosition(), lineStart + caret);
            } else {
                textArea.setCaretPosition(lineStart + caret);
            }
        }
    }

    public static class overwrite implements ActionListener {

        public void actionPerformed(ActionEvent evt) {
            JEditTextArea textArea = getTextArea(evt);
            textArea.setOverwriteEnabled(!textArea.isOverwriteEnabled());
        }
    }

    public static class prev_char implements ActionListener {

        private boolean select;

        public prev_char(boolean select) {
            this.select = select;
        }

        public void actionPerformed(ActionEvent evt) {
            JEditTextArea textArea = getTextArea(evt);
            int caret = textArea.getCaretPosition();
            if (caret == 0) {
                textArea.getToolkit().beep();
                return;
            }

            if (select) {
                textArea.select(textArea.getMarkPosition(), caret - 1);
            } else {
                textArea.setCaretPosition(caret - 1);
            }
        }
    }

    public static class prev_line implements ActionListener {

        private boolean select;

        public prev_line(boolean select) {
            this.select = select;
        }

        public void actionPerformed(ActionEvent evt) {
            JEditTextArea textArea = getTextArea(evt);
            int caret = textArea.getCaretPosition();
            int line = textArea.getCaretLine();

            if (line == 0) {
                textArea.getToolkit().beep();
                return;
            }

            int magic = textArea.getMagicCaretPosition();
            if (magic == -1) {
                magic = textArea.offsetToX(line, caret - textArea.getLineStartOffset(line));
            }

            caret = textArea.getLineStartOffset(line - 1) + textArea.xToOffset(line - 1, magic);
            if (select) {
                textArea.select(textArea.getMarkPosition(), caret);
            } else {
                textArea.setCaretPosition(caret);
            }
            textArea.setMagicCaretPosition(magic);
        }
    }

    public static class prev_page implements ActionListener {

        private boolean select;

        public prev_page(boolean select) {
            this.select = select;
        }

        public void actionPerformed(ActionEvent evt) {
            JEditTextArea textArea = getTextArea(evt);
            int firstLine = textArea.getFirstLine();
            int visibleLines = textArea.getVisibleLines();
            int line = textArea.getCaretLine();

            if (firstLine < visibleLines) {
                firstLine = visibleLines;
            }

            textArea.setFirstLine(firstLine - visibleLines);

            int caret = textArea.getLineStartOffset(Math.max(0, line - visibleLines));
            if (select) {
                textArea.select(textArea.getMarkPosition(), caret);
            } else {
                textArea.setCaretPosition(caret);
            }
        }
    }

    public static class prev_word implements ActionListener {

        private boolean select;

        public prev_word(boolean select) {
            this.select = select;
        }

        public void actionPerformed(ActionEvent evt) {
            JEditTextArea textArea = getTextArea(evt);
            int caret = textArea.getCaretPosition();
            int line = textArea.getCaretLine();
            int lineStart = textArea.getLineStartOffset(line);
            caret -= lineStart;

            String lineText = textArea.getLineText(textArea.getCaretLine());

            if (caret == 0) {
                if (lineStart == 0) {
                    textArea.getToolkit().beep();
                    return;
                }
                caret--;
            } else {
                String noWordSep = (String) textArea.getDocument().getProperty("noWordSep");
                caret = TextUtilities.findWordStart(lineText, caret, noWordSep);
            }

            if (select) {
                textArea.select(textArea.getMarkPosition(), lineStart + caret);
            } else {
                textArea.setCaretPosition(lineStart + caret);
            }
        }
    }

    public static class toggle_rect implements ActionListener {

        public void actionPerformed(ActionEvent evt) {
            JEditTextArea textArea = getTextArea(evt);
            textArea.setSelectionRectangular(!textArea.isSelectionRectangular());
        }
    }

    public static class insert_char implements ActionListener, InputHandler.NonRepeatable {

        public void actionPerformed(ActionEvent evt) {
            JEditTextArea textArea = getTextArea(evt);
            String str = evt.getActionCommand();
            int repeatCount = textArea.getInputHandler().getRepeatCount();

            if (textArea.isEditable()) {
                StringBuffer buf = new StringBuffer();
                for (int i = 0; i < repeatCount; i++) {
                    buf.append(str);
                }
                textArea.overwriteSetSelectedText(buf.toString());
            } else {
                textArea.getToolkit().beep();
            }
        }
    }

    public static class paran_straight implements ActionListener {

        public void actionPerformed(ActionEvent evt) {
            JEditTextArea textArea = getTextArea(evt);
            textArea.setSelectedText("\\left[\\right]");
            textArea.setCaretPosition(textArea.getCaretPosition() - 7);
        }
    }

    public static class paran_curved implements ActionListener {

        public void actionPerformed(ActionEvent evt) {
            JEditTextArea textArea = getTextArea(evt);
            textArea.setSelectedText("\\left(\\right)");
            textArea.setCaretPosition(textArea.getCaretPosition() - 7);
        }
    }

    public static class paran_curly implements ActionListener {

        public void actionPerformed(ActionEvent evt) {
            JEditTextArea textArea = getTextArea(evt);
            textArea.setSelectedText("\\left{\\right}");
            textArea.setCaretPosition(textArea.getCaretPosition() - 7);
        }
    }

    public static class frac implements ActionListener {

        public void actionPerformed(ActionEvent evt) {
            JEditTextArea textArea = getTextArea(evt);
            textArea.setSelectedText("\\frac{}{}");
            textArea.setCaretPosition(textArea.getCaretPosition() - 3);
        }
    }

    public static class math implements ActionListener {

        public void actionPerformed(ActionEvent evt) {
            JEditTextArea textArea = getTextArea(evt);
            textArea.setSelectedText("$$");
            textArea.setCaretPosition(textArea.getCaretPosition() - 1);
        }
    }

    public static class integral implements ActionListener {

        public void actionPerformed(ActionEvent evt) {
            JEditTextArea textArea = getTextArea(evt);
            textArea.setSelectedText("\\int\\limits_{}^{}");
            textArea.setCaretPosition(textArea.getCaretPosition() - 4);
        }
    }

    public static class reference implements ActionListener {

        public void actionPerformed(ActionEvent evt) {
            JEditTextArea textArea = getTextArea(evt);
            textArea.setSelectedText("\\ref{}");
            textArea.setCaretPosition(textArea.getCaretPosition() - 1);
        }
    }

    public static class symbol implements ActionListener {

        public void actionPerformed(ActionEvent evt) {
            JEditTextArea textArea = getTextArea(evt);
            textArea.setSelectedText("\\symbol[]{$$}");
            textArea.setCaretPosition(textArea.getCaretPosition() - 5);
        }
    }

    private class EquationKeyAction implements ActionListener {

        public void actionPerformed(ActionEvent evt) {
            JEditTextArea textArea = getTextArea(evt);
            String text = "\\begin{equation}\\label{}" + "\n\n" + "\\end{equation}";
            textArea.setSelectedText(text);
            textArea.setCaretPosition(textArea.getCaretPosition() - text.length() + 23);
        }
    }
}
