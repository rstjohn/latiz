package com.latizTech.jedit;

import java.awt.BorderLayout;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;

import com.AandR.library.gui.DropEvent;
import com.AandR.library.gui.DropListener;
import com.AandR.library.gui.JPanelWithDropSupport;
import com.latizTech.jedit.resources.Resources;
import com.latizTech.jedit.tokenMarkers.CCTokenMarker;
import com.latizTech.jedit.tokenMarkers.FortranTokenMarker;
import com.latizTech.jedit.tokenMarkers.JavaTokenMarker;
import com.latizTech.jedit.tokenMarkers.MakefileTokenMarker;
import com.latizTech.jedit.tokenMarkers.MatlabTokenMarker;
import com.latizTech.jedit.tokenMarkers.PropertiesTokenMarker;
import com.latizTech.jedit.tokenMarkers.TeXTokenMarker;
import com.latizTech.jedit.tokenMarkers.XMLTokenMarker;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.nodes.Node;

/**
 * @author Dr. Richard St. John
 * @version $Revision: 1.6 $, $Date: 2007/09/12 21:06:36 $
 */
public class JEditPanel extends JPanelWithDropSupport implements DropTargetListener, Serializable {

    private ImageIcon greenLed = Resources.createIcon("greenled16.png");
    private ImageIcon redLed = Resources.createIcon("redled16.png");
    private int lineWidth = 180;
    private boolean hasTextChanged = false;
    private ArrayList<DropListener> dropListeners;
    private JEditTextArea textArea;
    private JLabel numberOfLinesLabel, fileInfoLabel;
    private SyntaxSelectionListener syntaxSelectionListener;

    public JEditPanel() {
        this(null);
    }

    public JEditPanel(TextEditorProperties properties) {
        setLayout(new BorderLayout());
        numberOfLinesLabel = new JLabel();
        dropListeners = new ArrayList<DropListener>();

        JEditTextAreaListener editListener = new JEditTextAreaListener();
        syntaxSelectionListener = new SyntaxSelectionListener();

        textArea = new JEditTextArea();
        textArea.setBorder(new LineBorder(textArea.getBackground()));
        DefaultInputHandler inputHandler = new DefaultInputHandler();
        inputHandler.addDefaultKeyBindings();
        inputHandler.addJEditListener(editListener);
        inputHandler.addKeyBinding("C+SPACE", new CodeCompletionKeyAction());

        textArea.setInputHandler(inputHandler);
        textArea.addEditListener(editListener);
        textArea.addCaretListener(editListener);
        textArea.setRightClickPopup(createPopupMenu());
        textArea.getPainter().setEOLMarkersPainted(false);
        textArea.getPainter().setInvalidLinesPainted(false);

        add(textArea, BorderLayout.CENTER);
        add(makeInfoPanel(), BorderLayout.SOUTH);
    }

    public void addJEditListener(JEditListener listener) {
        ((DefaultInputHandler) textArea.getInputHandler()).addJEditListener(listener);
    }

    private JPanel makeInfoPanel() {
        fileInfoLabel = new JLabel("Untitled");
        fileInfoLabel.setIcon(greenLed);
        fileInfoLabel.setHorizontalAlignment(SwingConstants.LEFT);
        numberOfLinesLabel.setText("Lines: " + getTextArea().getLineCount());

        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.X_AXIS));
        infoPanel.add(fileInfoLabel);
        infoPanel.add(Box.createHorizontalGlue());
        infoPanel.add(numberOfLinesLabel);
        return infoPanel;
    }

    @Override
    public void addDropListener(DropListener dropListener) {
        dropListeners.add(dropListener);
    }

    @Override
    public void removeDropListener(DropListener dl) {
        int index = dropListeners.indexOf(dl);
        if (index != -1) {
            dropListeners.remove(index);
        }
    }

    private void nodifyDropListeners(DropEvent dropEvent) {
        for (int i = 0; i < dropListeners.size(); i++) {
            dropListeners.get(i).dropAction(dropEvent);
        }
    }

    @Override
    public void dragEnter(DropTargetDragEvent dtde) {
        dtde.acceptDrag(DnDConstants.ACTION_COPY);
    }

    @Override
    public void dragOver(DropTargetDragEvent dtde) {
    }

    @Override
    public void dropActionChanged(DropTargetDragEvent dtde) {
    }

    @Override
    public void dragExit(DropTargetEvent dte) {
    }

    @Override
    public void drop(DropTargetDropEvent dtde) {
        if (textArea.getText().length() > 0 && hasTextChanged) {
            boolean success = showConfirmDropDialog();
            if (!success) {
                return;
            }
        }
        try {
            Transferable t = dtde.getTransferable();
            if (t.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                dtde.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
                String[] files = (String[]) t.getTransferData(DataFlavor.stringFlavor);
                nodifyDropListeners(new DropEvent(files, dtde.getDropTargetContext(), dtde.getLocation(), dtde.getDropAction(), dtde.getSourceActions()));
                dtde.dropComplete(true);
                return;
            } else {
                try {
                    DataFlavor flavor = t.getTransferDataFlavors()[0];
                    Node n = (Node) t.getTransferData(flavor);
                    nodifyDropListeners(new DropEvent(n, dtde.getDropTargetContext(), dtde.getLocation(), dtde.getDropAction(), dtde.getSourceActions()));
                    dtde.dropComplete(true);
                    return;
                } catch (Exception e) {
                }
            }
            dtde.rejectDrop();
            dtde.dropComplete(true);
        } catch (IOException ioe) {
            dtde.dropComplete(true);
        } catch (UnsupportedFlavorException e) {
            dtde.dropComplete(true);
        }
    }

    public boolean showConfirmDropDialog() {
        if (textArea.getText().length() > 0 && hasTextChanged) {
            NotifyDescriptor.InputLine nd = new NotifyDescriptor.InputLine("Confirm Drop", "<HTML>The current contents have changed but have <B>NOT</B> been saved.<BR><BR><CENTER>Continue without saving?</CENTER></HTML>");
            Object ans = DialogDisplayer.getDefault().notify(nd);
            if (ans == NotifyDescriptor.CANCEL_OPTION) {
                return false;
            }
        }
        return true;
    }

    private final void updateFileInfoPanel(boolean hasTextChanged) {
        this.hasTextChanged = hasTextChanged;
        String label = "";
        String flyOver = "";
        fileInfoLabel.setIcon(hasTextChanged ? redLed : greenLed);

        if (textArea.getCurrentFile() != null) {
            label += textArea.getCurrentFile().getPath();
            flyOver += textArea.getCurrentFile().getName();
        } else {
            label += "Untitled";
            flyOver += "Untitled";
        }
        fileInfoLabel.setText(label);
        fileInfoLabel.setToolTipText(flyOver);
    }

    public void setCodeCompletionUsed(boolean codeCompletionUsed) {
        textArea.setCodeCompletionUsed(codeCompletionUsed);
    }

    private JPopupMenu createPopupMenu() {
        JPopupMenu popupMenu = new JPopupMenu();

        popupMenu.add(createPopupMenuItem("New", null, KeyStroke.getKeyStroke(KeyEvent.VK_N, 2)));
        popupMenu.add(createPopupMenuItem("Open...", null, KeyStroke.getKeyStroke(KeyEvent.VK_O, 2)));
        popupMenu.add(createPopupMenuItem("Save", null, KeyStroke.getKeyStroke(KeyEvent.VK_S, 2)));
        popupMenu.add(createPopupMenuItem("Save As...", null, KeyStroke.getKeyStroke(KeyEvent.VK_A, 3)));
        popupMenu.addSeparator();
        popupMenu.add(createPopupMenuItem("Copy", null, KeyStroke.getKeyStroke(KeyEvent.VK_C, 2)));
        popupMenu.add(createPopupMenuItem("Cut", null, KeyStroke.getKeyStroke(KeyEvent.VK_X, 2)));
        popupMenu.add(createPopupMenuItem("Paste", null, KeyStroke.getKeyStroke(KeyEvent.VK_V, 2)));
        popupMenu.add(createPopupMenuItem("Paste Special...", null, KeyStroke.getKeyStroke(KeyEvent.VK_V, 3)));
        popupMenu.add(createPopupMenuItem("Delete Selected Lines", null, KeyStroke.getKeyStroke(KeyEvent.VK_D, 2)));
        popupMenu.addSeparator();
        popupMenu.add(createPopupMenuItem("Undo", null, KeyStroke.getKeyStroke(KeyEvent.VK_Z, 2)));
        popupMenu.add(createPopupMenuItem("Redo", null, KeyStroke.getKeyStroke(KeyEvent.VK_Y, 2)));
        popupMenu.add(createPopupMenuItem("Copy Up", null, KeyStroke.getKeyStroke(KeyEvent.VK_UP, 10)));
        popupMenu.add(createPopupMenuItem("Copy Down", null, KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 10)));
        popupMenu.add(createPopupMenuItem("Move Up", null, KeyStroke.getKeyStroke(KeyEvent.VK_UP, 8)));
        popupMenu.add(createPopupMenuItem("Move Down", null, KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 8)));
        popupMenu.addSeparator();
        popupMenu.add(createPopupMenuItem("Change Font...", null, KeyStroke.getKeyStroke(KeyEvent.VK_F, 3)));
        popupMenu.add(createPopupMenuItem("Increase Font Size", null, KeyStroke.getKeyStroke(KeyEvent.VK_PLUS, 2)));
        popupMenu.add(createPopupMenuItem("Decrease Font Size", null, KeyStroke.getKeyStroke(KeyEvent.VK_MINUS, 2)));
        popupMenu.addSeparator();
        popupMenu.add(createSyntaxPopup());
        popupMenu.addSeparator();
        popupMenu.add(createPopupMenuItem("Find...", null, KeyStroke.getKeyStroke(KeyEvent.VK_F, 2)));
        popupMenu.add(createPopupMenuItem("Go To Line...", null, KeyStroke.getKeyStroke(KeyEvent.VK_L, 2)));
        return popupMenu;
    }

    private JMenu createSyntaxPopup() {
        JMenu menu = new JMenu("Syntax Highlighting");
        menu.add(createPopupMenuItem("None", null));
        menu.add(createPopupMenuItem("Java", null));
        menu.add(createPopupMenuItem("Bundles", null));
        menu.add(createPopupMenuItem("Matlab", null));
        menu.add(createPopupMenuItem("XML", null));
        menu.add(createPopupMenuItem("Fortran", null));
        menu.add(createPopupMenuItem("Namelist", null));
        menu.add(createPopupMenuItem("LaTex", null));
        menu.add(createPopupMenuItem("C++", null));
        return menu;
    }

    private JMenuItem createPopupMenuItem(String label, Icon icon) {
        JMenuItem menuItem = new JMenuItem(label, icon);
        menuItem.setActionCommand(label);
        menuItem.addActionListener(syntaxSelectionListener);
        menuItem.setEnabled(true);
        return menuItem;
    }

    private JMenuItem createPopupMenuItem(String label, Icon icon, KeyStroke keyStroke) {
        JMenuItem menuItem = new JMenuItem(label, icon);
        menuItem.addActionListener(new JEditTextAreaListener());
        menuItem.setAccelerator(keyStroke);
        menuItem.setEnabled(true);
        return menuItem;
    }

    public void updateSyntaxHighlighter(String syntax) {
        if (syntax.equalsIgnoreCase("None")) {
            textArea.setTokenMarker(null);
            textArea.repaint();
        } else if (syntax.equals("Matlab")) {
            textArea.setTokenMarker(new MatlabTokenMarker());
            textArea.repaint();
        } else if (syntax.equalsIgnoreCase("Java")) {
            textArea.setTokenMarker(new JavaTokenMarker());
            textArea.repaint();
        } else if (syntax.equals("Bundles")) {
            textArea.setTokenMarker(new PropertiesTokenMarker());
            textArea.repaint();
        } else if (syntax.equalsIgnoreCase("Namelist")) {
            textArea.setTokenMarker(new MakefileTokenMarker());
            textArea.repaint();
        } else if (syntax.equalsIgnoreCase("LaTex")) {
            textArea.setTokenMarker(new TeXTokenMarker());
            textArea.repaint();
        } else if (syntax.equalsIgnoreCase("XML")) {
            textArea.setTokenMarker(new XMLTokenMarker());
            textArea.repaint();
        } else if (syntax.equalsIgnoreCase("Fortran")) {
            textArea.setTokenMarker(new FortranTokenMarker());
            textArea.repaint();
        } else if (syntax.equalsIgnoreCase("C++")) {
            textArea.setTokenMarker(new CCTokenMarker());
            textArea.repaint();
        }
    }

    public JEditTextArea getTextArea() {
        return textArea;
    }

    public int getLineWidth() {
        return lineWidth;
    }

    public void setLineWidth(int lineWidth) {
        this.lineWidth = lineWidth;
    }

    /**
     *
     * @author Dr. Richard St. John
     * @version $Revision$, $Date$
     */
    private class JEditTextAreaListener implements ActionListener, JEditListener, CaretListener {

        public void actionPerformed(ActionEvent e) {
            JMenuItem item = (JMenuItem) e.getSource();
            int modifiers = item.getAccelerator().getModifiers();
            int keyCode = item.getAccelerator().getKeyCode();
            dispatchEvent(new KeyEvent(textArea, KeyEvent.KEY_PRESSED, 0, modifiers, keyCode, KeyEvent.CHAR_UNDEFINED));
        }

        public void fileChanged(boolean hasTextChanged) {
            updateFileInfoPanel(hasTextChanged);
        }

        public void caretUpdate(CaretEvent e) {
            final int caretPosition = textArea.getCaretPosition();
            int lineNumber = textArea.getDocument().getDefaultRootElement().getElementIndex(caretPosition);

            int columnStartOffset = textArea.getLineStartOffset(lineNumber);
            int columnNumber = caretPosition - columnStartOffset + 1;

            numberOfLinesLabel.setText("row: " + (lineNumber + 1) + " / " + textArea.getLineCount() + ", col: " + columnNumber);
            if (textArea.getOriginalFile() == null) {
                textArea.setOriginalFile("");
            }

            if (!textArea.getOriginalFile().equals(textArea.getText())) {
                updateFileInfoPanel(true);
            } else {
                updateFileInfoPanel(false);
            }
        }

        public void keyPressed(KeyEvent evt) {
            int selectionStart = textArea.getSelectionStart();
            int selectionEnd = textArea.getSelectionEnd();
            if(selectionStart==selectionEnd) {
                keyPressedWithNoSelectedText(evt);
            } else {
                keyPressedWithSelectedText(evt);
            }

            if (evt.getKeyCode() != KeyEvent.VK_SPACE) return;

            final int caretPosition = textArea.getCaretPosition();
            int lineNumber = textArea.getDocument().getDefaultRootElement().getElementIndex(caretPosition);
            int columnStartOffset = textArea.getLineStartOffset(lineNumber);
            int columnNumber = caretPosition - columnStartOffset + 1;

            if (columnNumber > lineWidth && evt.getKeyCode() == KeyEvent.VK_SPACE) {
                int len = textArea.getDocumentLength() - caretPosition;
                String preText = textArea.getText(0, caretPosition);
                String postText = textArea.getText(caretPosition, len);
                final String newText = preText + "\n" + postText;
                new Thread() {

                    @Override
                    public void run() {
                        textArea.setText(newText);
                        textArea.setCaretPosition(caretPosition + 1);
                    }
                }.start();
            }
        }

        private void keyPressedWithNoSelectedText(KeyEvent evt) {
            final int caretPosition = textArea.getCaretPosition();
            int lineNumber = textArea.getDocument().getDefaultRootElement().getElementIndex(caretPosition);
            
            int keycode = evt.getKeyCode();
            if (keycode == KeyEvent.VK_ENTER && !(evt.isAltDown() || evt.isControlDown() || evt.isShiftDown())) {
                textArea.getLineNumberPanel().lineInserted(lineNumber+1);
            }

            if (keycode == KeyEvent.VK_BACK_SPACE) {
                if(caretPosition==0) return;
                String s = textArea.getText(caretPosition-1, 1);
                if(s.equals("\n")) {
                    textArea.getLineNumberPanel().lineRemoved(lineNumber+1);
                }
            }

            if (keycode == KeyEvent.VK_DELETE) {
                if (caretPosition == 0) return;
                int lineEndOffset = textArea.getLineEndOffset(lineNumber);
                if ((caretPosition + 1) == lineEndOffset) {
                    textArea.getLineNumberPanel().lineRemoved(lineNumber+1);
                }
            }
        }

        private void keyPressedWithSelectedText(KeyEvent evt) {
            int code = evt.getKeyCode();
            if(evt.isActionKey() || code==KeyEvent.VK_SHIFT || code==KeyEvent.VK_ALT || code==KeyEvent.VK_CONTROL) return;
            if(evt.isAltDown() || evt.isControlDown()) return;
            int startLine = textArea.getSelectionStartLine();
            int endLine = textArea.getSelectionEndLine();
            if(startLine==endLine) return;
            
            int startLineOffset = textArea.getLineStartOffset(startLine);
            int selectionOffset = textArea.getSelectionStart();
            if(selectionOffset!=startLineOffset) {
                startLine++;
            }
            int end = textArea.getSelectionEndLine();
            textArea.getLineNumberPanel().linesRemoved(startLine, end - 1);
        }
    }

    /**
     *
     * @author Dr. Richard St. John
     * @version $Revision$, $Date$
     */
    private class SyntaxSelectionListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            updateSyntaxHighlighter(e.getActionCommand());
        }
    }

    /**
     * 
     */
    private class CodeCompletionKeyAction implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            if (textArea.isCodeCompletionUsed()) {
                CodeCompletionDialog.getDefault().start(textArea);
            }
        }
    }
}
