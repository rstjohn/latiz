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
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JWindow;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;

import net.miginfocom.swing.MigLayout;

/**
 * @author  <a href="mailto://rstjohn@mza.com">Dr. Richard St. John</a>
 * @company <a href="http://www.mza.com">MZA Associates Corporation</a>
 */
public class JSearchField extends JTextField {

    private ActionListener linkListener;
    private RewardingInterface rewardingInterface;
    private JPanel resultsPanel;
    private JWindow window;
    private List<?> listToSearch;

    public JSearchField() {
        initialize();
    }

    public JSearchField(String text, List<?> listToSearch) {
        super(text);
        this.listToSearch = listToSearch;
        initialize();
    }

    public JSearchField(int columns, List<?> listToSearch) {
        super(columns);
        this.listToSearch = listToSearch;
        initialize();
    }

    public JSearchField(String text, int columns, List<?> listToSearch) {
        super(text, columns);
        this.listToSearch = listToSearch;
        initialize();
    }

    private void initialize() {
        setFocusTraversalKeysEnabled(false);
        rewardingInterface = new DefaultSearchRewardInterface();
        window = new JWindow();
        window.getContentPane().add(new JScrollPane(resultsPanel = new JPanel(new MigLayout("wrap 2", "", ""))));
        resultsPanel.setBackground(new Color(240, 240, 240));
        resultsPanel.setOpaque(true);
        SearchActionListener searchListener = new SearchActionListener();
        addCaretListener(searchListener);
        addFocusListener(searchListener);
        addAncestorListener(searchListener);
        addKeyListener(searchListener);
    }

    public void setListToSearch(List<Object> listToSearch) {
        this.listToSearch = listToSearch;
    }

    public void setLinkListener(ActionListener linkListener) {
        this.linkListener = linkListener;
    }

    public void setResultsVisible(boolean visible) {
        window.setVisible(visible);
    }

    public RewardingInterface getRewardingInterface() {
        return rewardingInterface;
    }

    public void setRewardingInterface(RewardingInterface rewardingInterface) {
        this.rewardingInterface = rewardingInterface;
    }

    /**
     *
     * @author  <a href="mailto://rstjohn@mza.com">Dr. Richard St. John</a>
     * @company <a href="http://www.mza.com">MZA Associates Corporation</a>
     */
    public static interface RewardingInterface {

        public Number computeReward(String request, Object o);
    }

    /**
     *
     * @author  <a href="mailto://rstjohn@mza.com">Dr. Richard St. John</a>
     * @company <a href="http://www.mza.com">MZA Associates Corporation</a>
     */
    private class DefaultSearchRewardInterface implements RewardingInterface {

        public Number computeReward(String request, Object o) {
            int metric = 0;
            String sl = o.toString().toLowerCase();
            if (sl.contains(request)) {
                metric++;
            }
            if (sl.startsWith(request)) {
                metric++;
            }
            if (sl.equals(request)) {
                metric++;
            }
            return metric;
        }
    }

    /**
     *
     * @author  <a href="mailto://rstjohn@mza.com">Dr. Richard St. John</a>
     * @company <a href="http://www.mza.com">MZA Associates Corporation</a>
     */
    private class SearchActionListener implements ActionListener, CaretListener, FocusListener, AncestorListener, KeyListener {

        public void actionPerformed(ActionEvent e) {
            TreeSet<SearchHit> hits = new TreeSet<SearchHit>(new Comparator<SearchHit>() {

                public int compare(SearchHit o1, SearchHit o2) {
                    int m1 = o1.getMetric();
                    int m2 = o2.getMetric();
                    if (m1 != m2) {
                        return -1 * Integer.valueOf(m1).compareTo(Integer.valueOf(m2));
                    } else {
                        return o1.getString().compareTo(o2.getString());
                    }
                }
            });

            String request = getText().trim().toLowerCase();
            if (request.length() > 0) {
                for (Object s : listToSearch) {
                    Number metric = rewardingInterface.computeReward(request, s);
                    if (metric.doubleValue() > 0) {
                        hits.add(new SearchHit(s.toString(), metric.intValue()));
                    }
                }
            }
            resultsPanel.removeAll();
            resultsPanel.revalidate();
            for (SearchHit hit : hits) {
                resultsPanel.add(new IconLabel(hit.getMetric()));
                JHyperLink link = new JHyperLink(hit.toString());
                link.addActionListener(linkListener);
                resultsPanel.add(link);
            }
            if (hits.size() == 0) {
                resultsPanel.add(new JLabel("No results found"));
            }
            resultsPanel.revalidate();
            resultsPanel.repaint();
        }

        public void caretUpdate(CaretEvent e) {
            if (!window.isVisible()) {
                window.setSize(new Dimension(getWidth(), 250));
                Point fieldLocation = getLocationOnScreen();
                window.setLocation(fieldLocation.x, fieldLocation.y + getHeight());
                window.setVisible(true);
                window.validate();
                window.repaint();
            }

            if (getText().trim().length() == 0) {
                window.setVisible(false);
            }

            actionPerformed(null);
        }

        public void focusGained(FocusEvent e) {
            if (getText().trim().length() == 0) {
                return;
            }
            window.setVisible(true);
        }

        public void focusLost(FocusEvent e) {
            if (window.hasFocus()) {
                return;
            }
            if (window.isVisible()) {
                window.setVisible(false);
            }
        }

        public void ancestorAdded(AncestorEvent event) {
            window.setVisible(false);
            transferFocus();
        }

        public void ancestorMoved(AncestorEvent event) {
            window.setVisible(false);
            transferFocus();
        }

        public void ancestorRemoved(AncestorEvent event) {
            window.setVisible(false);
            transferFocus();
        }

        public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                transferFocus();
            }
            if (e.getKeyCode() != KeyEvent.VK_TAB) {
                return;
            }
        }

        public void keyReleased(KeyEvent e) {
        }

        public void keyTyped(KeyEvent e) {
        }
    }

    /**
     *
     * @author  <a href="mailto://rstjohn@mza.com">Dr. Richard St. John</a>
     * @company <a href="http://www.mza.com">MZA Associates Corporation</a>
     */
    private class IconLabel extends JLabel {

        public IconLabel(int index) {
            super(String.valueOf(index));
            Color c;
            if (index == 3) {
                c = Color.GREEN.darker();
            } else if (index == 2) {
                c = Color.ORANGE.darker();
            } else {
                c = Color.RED;
            }
            setForeground(c);
        }
    }

    /**
     *
     * @author  <a href="mailto://rstjohn@mza.com">Dr. Richard St. John</a>
     * @company <a href="http://www.mza.com">MZA Associates Corporation</a>
     */
    private class SearchHit {

        private final String s;
        private final int metric;

        public SearchHit(String s, int metric) {
            this.s = s;
            this.metric = metric;
        }

        public String getString() {
            return s;
        }

        public int getMetric() {
            return metric;
        }

        @Override
        public String toString() {
            return s;
        }
    }
}
