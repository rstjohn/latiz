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

import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collection;
import java.util.Comparator;
import java.util.TreeSet;
import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;
import net.miginfocom.swing.MigLayout;
import org.openide.util.Exceptions;
import org.openide.util.ImageUtilities;

/**
 *
 * @author rstjohn
 */
public class LineNumberPanel extends JPanel {

    private JTextPane area;
    private int start, end;
    private TreeSet<Integer> divList;
    private JEditTextArea editor;

    public LineNumberPanel(JEditTextArea editor) {
        this.editor = editor;
        start = end = -1;
        setLayout(new MigLayout("ins 0 4 0 6", "[20::]", ""));
        add(area = new JTextPane(), "align right");
        area.setBorder(null);
        final StyledDocument doc = area.getStyledDocument();
        addStylesToDocument(doc);
        area.setAlignmentY(1f);
        area.setEditable(false);
        area.setOpaque(false);
        area.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.getButton() == 3) {
                    showPopup(e);
                    return;
                }
                setBreakPoint(e.getPoint());
            }
        });
        divList = new TreeSet<Integer>(new Comparator<Integer>() {
            public int compare(Integer o1, Integer o2) {
                return o1.compareTo(o2);
            }
        });
    }

    public TreeSet<Integer> getDivLineNumbers() {
        return divList;
    }

    public void setDivLineNumbers(Collection<Integer> lineNumbers) {
        divList.clear();
        divList.addAll(lineNumbers);
        rewriteNumbers(start, end);
    }

    public String serialize() {
        StringBuilder sb = new StringBuilder();
        for(Integer i : divList) {
            sb = sb.append(i).append(";");
        }
        return sb.toString();
    }

    public void setLineNumberRange(int start, int end) {
        this.start = start;
        this.end = end;
        rewriteNumbers(start, end);
    }

    void lineInserted(int lineNumber) {
        TreeSet<Integer> localDivList = new TreeSet<Integer>(new Comparator<Integer>() {
            public int compare(Integer o1, Integer o2) {
                return o1.compareTo(o2);
            }
        });

        for(Integer div : divList) {
            if (div <= lineNumber) {
                localDivList.add(div);
            } else {
                localDivList.add(div+1);
            }
        }
        setDivLineNumbers(localDivList);
    }

    void linesInserted(int startLine, int endLine) {
        int diff = endLine - startLine + 1;
        TreeSet<Integer> localDivList = new TreeSet<Integer>(new Comparator<Integer>() {
            public int compare(Integer o1, Integer o2) {
                return o1.compareTo(o2);
            }
        });
        for(Integer div : divList) {
            if (div <= startLine) {
                localDivList.add(div);
            } else if(div >= endLine) {
                localDivList.add(div+diff);
            }
        }
        setDivLineNumbers(localDivList);
    }

    void lineRemoved(int lineNumber) {
        TreeSet<Integer> localDivList = new TreeSet<Integer>(new Comparator<Integer>() {
            public int compare(Integer o1, Integer o2) {
                return o1.compareTo(o2);
            }
        });
        
        for(Integer div : divList) {
            if (div > lineNumber) {
                localDivList.add(div-1);
            } else {
                localDivList.add(div);
            }
        }
        setDivLineNumbers(localDivList);
    }

    void linesRemoved(int startLine, int endLine) {
        int diff = endLine - startLine + 1;
        TreeSet<Integer> localDivList = new TreeSet<Integer>(new Comparator<Integer>() {
            public int compare(Integer o1, Integer o2) {
                return o1.compareTo(o2);
            }
        });

        for(Integer div : divList) {
            if (div <= startLine) {
                localDivList.add(div);
            } else if(div >= endLine) {
                localDivList.add(div-diff);
            }
        }
        setDivLineNumbers(localDivList);
    }

    private void setBreakPoint(Point pt) {
        int loc = area.viewToModel(pt);
        StyledDocument doc = area.getStyledDocument();

        Element elem = doc.getParagraphElement(loc);
        int startBreak = elem.getStartOffset();
        int endBreak = elem.getEndOffset() - 1;
        try {
            int line = Integer.parseInt(doc.getText(startBreak, endBreak - startBreak).trim());
            if (divList.contains(line)) {
                divList.remove((Integer) line);
            } else {
                divList.add((Integer) line);
            }
            rewriteNumbers(start, end);
            editor.requestFocusInWindow();
        } catch (BadLocationException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    private void removeAllDivs() {
        divList.clear();
        rewriteNumbers(start, end);
    }

    public void setTextFont(Font font) {
        super.setFont(font);
        applyFontToStyle(font);
    }

    public void updateLineNumbers(int start, int end) {
        if (this.start == start && this.end == end) return;
        rewriteNumbers(start, end);
        this.start = start;
        this.end = end;
    }

    private void rewriteNumbers(int start, int end) {
        area.setText("");
        StyledDocument doc = area.getStyledDocument();
        try {
            Style divStyle = doc.getStyle("div");
            Style regStyle = doc.getStyle("regular");
            for (int i = start; i <= end; i++) {
                doc.insertString(doc.getLength(), String.valueOf(i), divList.contains(i) ? divStyle : regStyle);
                doc.insertString(doc.getLength(), "\n", regStyle);
            }
        } catch (Exception ex) {}
    }

    private void applyFontToStyle(Font font) {
        Style s = area.getStyle("regular");
        StyleConstants.setFontFamily(s, font.getFamily());
        StyleConstants.setFontSize(s, font.getSize());

        s = area.getStyle("div");
        StyleConstants.setFontFamily(s, font.getFamily());
        StyleConstants.setFontSize(s, font.getSize());
        int size = Math.min(font.getSize(), 13);
        Image img = ImageUtilities.loadImage("com/latizTech/jedit/resources/div.png", false);
        ImageIcon icon = new ImageIcon(img.getScaledInstance(size, size, Image.SCALE_DEFAULT));
        StyleConstants.setIcon(s, icon);

        rewriteNumbers(start, end);
    }

    private void addStylesToDocument(StyledDocument doc) {
        Style def = StyleContext.getDefaultStyleContext().getStyle(StyleContext.DEFAULT_STYLE);

        Style regular = doc.addStyle("regular", def);
        StyleConstants.setAlignment(regular, StyleConstants.ALIGN_RIGHT);
        StyleConstants.setForeground(regular, Color.GRAY);
        StyleConstants.setBold(regular, false);
        area.setParagraphAttributes(regular, true);

        Style divStyle = doc.addStyle("div", def);
        Image img = ImageUtilities.loadImage("com/latizTech/jedit/resources/div.png", false);
        ImageIcon icon = new ImageIcon(img);
        StyleConstants.setIcon(divStyle, icon);
    }

    private void showPopup(final MouseEvent mouseEvent) {
        JMenuItem addItem = new JMenuItem("Add Div Here");
        addItem.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                setBreakPoint(mouseEvent.getPoint());
            }
        });
        JMenuItem removeAllItem = new JMenuItem("Remove All Divs");
        removeAllItem.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                removeAllDivs();
            }
        });
        JPopupMenu popup = new JPopupMenu();
        popup.add(addItem);
        popup.add(new JSeparator());
        popup.add(removeAllItem);
        popup.show(this, mouseEvent.getX(), mouseEvent.getY());
    }
}
