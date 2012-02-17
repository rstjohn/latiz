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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.dnd.DragSource;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

import javax.swing.Icon;
import javax.swing.JDialog;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.event.MouseInputAdapter;


/**
 *
 * @author Dr. Richard St. John
 * @version $Revision: 1.2 $, $Date: 2007/09/15 02:37:32 $
 */
public class JTabbedPaneX extends JTabbedPane {

    private int dragIndex = -1;

    private int mouseOverIndex = -1;

    private boolean isDetachable = false;

    private boolean alwaysOnTop = true;

    private ArrayList<TabOrderChangedListener> tabOrderChangedListeners;

    private MouseHandler tabListener;


    public JTabbedPaneX() {
        this(TOP);
    }


    public JTabbedPaneX(int tabPlacement) {
        this(tabPlacement, WRAP_TAB_LAYOUT);
    }


    public JTabbedPaneX(int tabPlacement, int tabLayoutPolicy) {
        super(tabPlacement, tabLayoutPolicy);
        setUI(new CWTabbedPaneUI());
        tabOrderChangedListeners = new ArrayList<TabOrderChangedListener>();
        tabListener = new MouseHandler();
        addMouseListener(tabListener);
        addMouseMotionListener(tabListener);
        addKeyListener(tabListener);
    }


    @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        super.paintComponent(g);
        if (mouseOverIndex == -1) {
            return;
        }
        Color borderColor = new Color(255, 255, 255, 100);
        Rectangle rect = getUI().getTabBounds(this, mouseOverIndex);
        Stroke currentStroke = g2.getStroke();
        Stroke s = new BasicStroke(1);
        g2.setStroke(s);
        g2.setColor(Color.GRAY);
        g2.drawRoundRect(rect.x + 3, rect.y - 4, rect.width - 6, rect.height - 16, 8, 8);
        g2.setColor(borderColor);
        g2.fillRoundRect(rect.x + 3, rect.y - 4, rect.width - 6, rect.height - 16, 8, 8);
        g2.setStroke(new BasicStroke(2));
        g2.setColor(Color.GRAY);
        g2.drawLine(rect.x + 14, rect.height, rect.x + rect.width - 10, rect.height);
        g2.setStroke(currentStroke);
    }


    private void dropTab(int dragIndex, int tabIndex) {
        String title = getTitleAt(dragIndex);
        Icon icon = getIconAt(dragIndex);
        Component component = getComponentAt(dragIndex);
        String toolTipText = getToolTipTextAt(dragIndex);

        remove(dragIndex);
        insertTab(title, icon, component, toolTipText, tabIndex);
        notifyTabOrderChanged(title, dragIndex, tabIndex);
    }


    public void addTabOrderChangedListener(TabOrderChangedListener listener) {
        int index = tabOrderChangedListeners.indexOf(listener);
        if (index != -1) {
            tabOrderChangedListeners.add(listener);
        }
    }


    public void removeTabOrderChangedListener(TabOrderChangedListener listener) {
        int index = tabOrderChangedListeners.indexOf(listener);
        if (index != -1) {
            tabOrderChangedListeners.remove(index);
        }
    }


    public ArrayList<TabOrderChangedListener> getTabOrderChangedListeners() {
        return tabOrderChangedListeners;
    }


    private void notifyTabOrderChanged(String tabLabel, int oldIndex, int newIndex) {
        for (int i = 0; i < tabOrderChangedListeners.size(); i++) {
            tabOrderChangedListeners.get(i).tabOrderChanged(tabLabel, oldIndex, newIndex);
        }
    }


    public void setTabIndex(String title, int index) {
        int dragIndex = getTabIndex(title);
        Icon icon = getIconAt(dragIndex);
        Component component = getComponentAt(dragIndex);
        String toolTipText = getToolTipTextAt(dragIndex);

        remove(dragIndex);
        insertTab(title, icon, component, toolTipText, index);
    }


    public int getTabIndex(String title) {
        for (int i = 0; i < getTabCount(); i++) {
            if (getTitleAt(i).equals(title)) {
                return i;
            }
        }
        return -1;
    }


    public int getTabIndex(int x, int y) {
        return getUI().tabForCoordinate(this, x, y);
    }


    public boolean isDetachable() {
        return isDetachable;
    }


    public void setDetachable(boolean isDetachable) {
        this.isDetachable = isDetachable;
    }


    public void setAlwaysOnTop(boolean alwaysOnTop) {
        this.alwaysOnTop = alwaysOnTop;
    }


    public boolean isAlwaysOnTop() {
        return alwaysOnTop;
    }


    /**
     *
     * @author Dr. Richard St. John
     * @version $Revision: 1.2 $, $Date: 2007/09/15 02:37:32 $
     */
    private class MouseHandler extends MouseInputAdapter implements KeyListener {

        private int dividerLocation = -1;


        @Override
        public void mouseDragged(MouseEvent e) {
            if (dragIndex != -1) {
                mouseOverIndex = getTabIndex(e.getX(), e.getY());
                setCursor(mouseOverIndex != -1 ? DragSource.DefaultMoveDrop : DragSource.DefaultMoveNoDrop);
                repaint();
            }
        }


        @Override
        public void mouseClicked(MouseEvent e) {
            if (!isDetachable || e.getClickCount() != 2) {
                return;
            }
            final Container parent = getParent();

            final int tabIndex = getTabIndex(e.getX(), e.getY());
            String title = tabIndex == -1 ? getName() : getTitleAt(tabIndex);

            final JDialog d = new JDialog((JDialog) null, title, false);
            d.addWindowListener(new WindowAdapter() {

                @Override
                public void windowClosing(WindowEvent e) {
                    d.setVisible(false);
                    d.setAlwaysOnTop(alwaysOnTop);
                    Container rootPane = d.getContentPane();
                    if (tabIndex == -1) {
                        parent.add(JTabbedPaneX.this);
                        if (parent instanceof JSplitPane) {
                            ((JSplitPane) parent).setDividerLocation(dividerLocation);
                        }
                    } else {
                        addTab(d.getTitle(), rootPane);
                    }
                    revalidate();
                    repaint();
                    d.dispose();
                }
            });

            if (tabIndex == -1) {
                if (parent instanceof JSplitPane) {
                    dividerLocation = ((JSplitPane) parent).getDividerLocation();
                }
                d.setContentPane(JTabbedPaneX.this);
            } else {
                d.getContentPane().add(getComponentAt(tabIndex));
            }
            revalidate();
            repaint();
            d.pack();
            d.setLocationRelativeTo(null);
            d.setVisible(true);
        }


        @Override
        public void mousePressed(MouseEvent e) {
            if (!e.isPopupTrigger() && e.getButton() == MouseEvent.BUTTON1) {
                int tabIndex = getTabIndex(e.getX(), e.getY());
                if (tabIndex != -1) {
                    dragIndex = tabIndex;
                }
            }
        }


        @Override
        public void mouseReleased(MouseEvent e) {
            if (!e.isPopupTrigger() && e.getButton() == MouseEvent.BUTTON1) {

                if (dragIndex != -1) {
                    setCursor(Cursor.getDefaultCursor());
                    int tabIndex = getTabIndex(e.getX(), e.getY());
                    if (tabIndex != -1 && tabIndex != dragIndex) {
                        dropTab(dragIndex, tabIndex);
                        setSelectedIndex(tabIndex);
                    }
                    mouseOverIndex = -1;
                }
                repaint();
            }
        }


        public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() != KeyEvent.VK_ESCAPE) {
                return;
            }
            dragIndex = -1;
            setCursor(Cursor.getDefaultCursor());
            mouseOverIndex = -1;
            repaint();
        }


        public void keyReleased(KeyEvent e) {
        }


        public void keyTyped(KeyEvent e) {
        }
    }
}
