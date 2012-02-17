package com.latizTech.jedit;

/*
 * InputHandler.java - Manages key bindings and executes actions Copyright (C) 1999 Slava Pestov You may use and modify this package for any purpose. Redistribution is permitted, in both source and
 * binary form, provided that this notice remains intact in all source distributions of this package.
 */

import javax.swing.JPopupMenu;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.EventObject;


/**
 * An input handler converts the user's key strokes into concrete actions. It also takes care of macro recording and action repetition.
 * <p>
 * This class provides all the necessary support code for an input handler, but doesn't actually do any key binding logic. It is up to the implementations of this class to do so.
 * @author Slava Pestov
 * @version $Id: InputHandler.java,v 1.11 2007/09/17 00:43:02 stjohnr Exp $
 * @see org.gjt.sp.jedit.textarea.DefaultInputHandler
 */
public abstract class InputHandler extends KeyAdapter {

  /**
   * If this client property is set to Boolean.TRUE on the text area, the home/end keys will support 'smart' BRIEF-like behaviour (one press = start/end of line, two presses = start/end of viewscreen,
   * three presses = start/end of document). By default, this property is not set.
   */
  public static final int tab_length = 4;
  
  public static final String SMART_HOME_END_PROPERTY = "InputHandler.homeEnd";
  
  protected static final ActionListener REPEAT = new repeat();
  
  /**
   * Adds the default key bindings to this input handler. This should not be called in the constructor of this input handler, because applications might load the key bindings from a file, etc.
   */
  public abstract void addDefaultKeyBindings();

  /**
   * Adds a key binding to this input handler.
   * @param keyBinding The key binding (the format of this is input-handler specific)
   * @param action The action
   */
  public abstract void addKeyBinding(String keyBinding, ActionListener action);

  /**
   * Removes a key binding from this input handler.
   * @param keyBinding The key binding
   */
  public abstract void removeKeyBinding(String keyBinding);

  /**
   * Removes all key bindings from this input handler.
   */
  public abstract void removeAllKeyBindings();

  /**
   * Grabs the next key typed event and invokes the specified action with the key as a the action command.
   * @param action The action
   */
  public void grabNextKeyStroke(ActionListener listener) {
    grabAction = listener;
  }

  /**
   * Returns if repeating is enabled. When repeating is enabled, actions will be executed multiple times. This is usually invoked with a special key stroke in the input handler.
   */
  public boolean isRepeatEnabled() {
    return repeat;
  }

  /**
   * Enables repeating. When repeating is enabled, actions will be executed multiple times. Once repeating is enabled, the input handler should read a number from the keyboard.
   */
  public void setRepeatEnabled(boolean repeat) {
    this.repeat = repeat;
  }

  /**
   * Returns the number of times the next action will be repeated.
   */
  public int getRepeatCount() {
    return (repeat ? Math.max(1, repeatCount) : 1);
  }

  /**
   * Sets the number of times the next action will be repeated.
   * @param repeatCount The repeat count
   */
  public void setRepeatCount(int repeatCount) {
    this.repeatCount = repeatCount;
  }

  /**
   * Returns the macro recorder. If this is non-null, all executed actions should be forwarded to the recorder.
   */
  public InputHandler.MacroRecorder getMacroRecorder() {
    return recorder;
  }
  
  
  /**
   * Sets the macro recorder. If this is non-null, all executed actions should be forwarded to the recorder.
   * @param recorder The macro recorder
   */
  public void setMacroRecorder(InputHandler.MacroRecorder recorder) {
    this.recorder = recorder;
  }

  /**
   * Returns a copy of this input handler that shares the same key bindings. Setting key bindings in the copy will also set them in the original.
   */
  public abstract InputHandler copy();

  /**
   * Executes the specified action, repeating and recording it as necessary.
   * @param listener The action listener
   * @param source The event source
   * @param actionCommand The action command
   */
  public void executeAction(ActionListener listener, Object source, String actionCommand) {
    // create event
    ActionEvent evt = new ActionEvent(source, ActionEvent.ACTION_PERFORMED, actionCommand);

    // don't do anything if the action is a wrapper
    // (like EditAction.Wrapper)
    if (listener instanceof Wrapper) {
      listener.actionPerformed(evt);
      return;
    }

    // remember old values, in case action changes them
    boolean _repeat = repeat;
    int _repeatCount = getRepeatCount();

    // execute the action
    if (listener instanceof InputHandler.NonRepeatable)
      listener.actionPerformed(evt);
    else {
      for (int i = 0; i < Math.max(1, repeatCount); i++)
        listener.actionPerformed(evt);
    }

    // do recording. Notice that we do no recording whatsoever
    // for actions that grab keys
    if (grabAction == null) {
      if (recorder != null) {
        if (!(listener instanceof InputHandler.NonRecordable)) {
          if (_repeatCount != 1)
            recorder.actionPerformed(REPEAT, String.valueOf(_repeatCount));

          recorder.actionPerformed(listener, actionCommand);
        }
      }

      // If repeat was true originally, clear it
      // Otherwise it might have been set by the action, etc
      if (_repeat) {
        repeat = false;
        repeatCount = 0;
      }
    }
  }

  /**
   * Returns the text area that fired the specified event.
   * @param evt The event
   */
  public static JEditTextArea getTextArea(EventObject evt) {
    if (evt != null) {
      Object o = evt.getSource();
      if (o instanceof Component) {
        // find the parent text area
        Component c = (Component) o;
        for (;;) {
          if (c instanceof JEditTextArea)
            return (JEditTextArea) c;
          else if (c == null)
            break;
          if (c instanceof JPopupMenu)
            c = ((JPopupMenu) c).getInvoker();
          else
            c = c.getParent();
        }
      }
    }

    // this shouldn't happen
    System.err.println("BUG: getTextArea() returning null");
    System.err.println("Report this to Slava Pestov <sp@gjt.org>");
    return null;
  }

  // protected members

  /**
   * If a key is being grabbed, this method should be called with the appropriate key event. It executes the grab action with the typed character as the parameter.
   */
  protected void handleGrabAction(KeyEvent evt) {
    // Clear it *before* it is executed so that executeAction()
    // resets the repeat count
    ActionListener _grabAction = grabAction;
    grabAction = null;
    executeAction(_grabAction, evt.getSource(), String.valueOf(evt.getKeyChar()));
  }

  // protected members
  protected ActionListener grabAction;

  protected boolean repeat;

  protected int repeatCount;

  protected InputHandler.MacroRecorder recorder;

  /**
   * If an action implements this interface, it should not be repeated. Instead, it will handle the repetition itself.
   */
  public interface NonRepeatable {
  }

  /**
   * If an action implements this interface, it should not be recorded by the macro recorder. Instead, it will do its own recording.
   */
  public interface NonRecordable {
  }

  /**
   * For use by EditAction.Wrapper only.
   * @since jEdit 2.2final
   */
  public interface Wrapper {
  }

  
  /**
   * Macro recorder.
   */
  public interface MacroRecorder {

    void actionPerformed(ActionListener listener, String actionCommand);
  }
  

  public static class repeat implements ActionListener, InputHandler.NonRecordable {
    public void actionPerformed(ActionEvent evt) {
      JEditTextArea textArea = getTextArea(evt);
      textArea.getInputHandler().setRepeatEnabled(true);
      String actionCommand = evt.getActionCommand();
      if (actionCommand != null) {
        textArea.getInputHandler().setRepeatCount(Integer.parseInt(actionCommand));
      }
    }
  }
}
