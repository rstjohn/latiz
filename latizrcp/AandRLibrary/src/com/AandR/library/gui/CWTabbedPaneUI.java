package com.AandR.library.gui;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicTabbedPaneUI;
import java.awt.*;


/**
 * An implementation of the TabbedPaneUI that looks like the tabs that were used in the older versions of the
 * Codewarrior IDE.
 * <p/>
 * Copyright (C) 2005 by Jon Lipsky
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License. Y
 * ou may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software d
 * istributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
public class CWTabbedPaneUI extends BasicTabbedPaneUI {

    private static final Insets NO_INSETS = new Insets(0, 0, 0, 0);

    /**
     * The height that we want the button bar to be
     */
    private int buttonHeight = 20;

    /**
     * The (calculated) color of the button bar and the tabs
     */
    private Color tabColor = null;

    /**
     * The background color of the control
     */
    private Color background = null;

    /**
     * A color that is one step darker than the background color
     */
    private Color backgroundDarker1 = null;

    /**
     * A color that is two steps darker than the background color
     */
    private Color backgroundDarker2 = null;

    /**
     * Calculated colors to create a faded shadow
     */
    private Color[] fadeColors;

    /**
     * The number of pixels we want to use to create the shadow
     */
    private int fadeColorCount = 3;

    /**
     * The number of pixels the first tab should be inset from the left margin
     */
    private int leftInset = 12;

    // ------------------------------------------------------------------------------------------------------------------
    //  Custom installation methods
    // ------------------------------------------------------------------------------------------------------------------

    public static ComponentUI createUI(JComponent c) {
        return new CWTabbedPaneUI();
    }


    protected void installComponents() {
        super.installComponents();

        tabColor = tabPane.getBackground().darker().darker();

        background = tabPane.getBackground();
        backgroundDarker1 = tabPane.getBackground().darker();
        backgroundDarker2 = tabPane.getBackground().darker().darker();

        fadeColors = new Color[fadeColorCount];
        for (int i = 0; i < fadeColorCount; i++) {
            int rs = tabColor.getRed();
            int gs = tabColor.getGreen();
            int bs = tabColor.getBlue();

            int rt = background.getRed();
            int gt = background.getGreen();
            int bt = background.getBlue();

            int rn = Math.min(rs + (Math.abs(rt - rs) / 4 * i), 255);
            int gn = Math.min(gs + (Math.abs(gt - gs) / 4 * i), 255);
            int bn = Math.min(bs + (Math.abs(bt - bs) / 4 * i), 255);

            fadeColors[i] = new Color(rn, gn, bn);
        }
    }


    @Override
    protected void installDefaults() {
        super.installDefaults();
        tabAreaInsets = (Insets) UIManager.get("TabbedPane.cwTabAreaInsets");
        if (tabAreaInsets == null) {
            tabAreaInsets = new Insets(6, 30, 0, 10);
        }
        leftInset = tabAreaInsets.left;
        selectedTabPadInsets = new Insets(0, 0, 0, 0);
    }

    // ------------------------------------------------------------------------------------------------------------------
    //  Custom sizing methods
    // ------------------------------------------------------------------------------------------------------------------

//  public int getTabRunCount(JTabbedPane pane) {
//    return 1;
//  }
    protected Insets getContentBorderInsets(int tabPlacement) {
        return NO_INSETS;
    }


    protected int calculateTabHeight(int tabPlacement, int tabIndex, int fontHeight) {
        if (tabPlacement == tabIndex) {
            return buttonHeight;
        } else {
            return buttonHeight + (buttonHeight / 2) + 4;
        }
    }


    protected int calculateTabWidth(int tabPlacement, int tabIndex, FontMetrics metrics) {
        Icon icon = getIconForTab(tabIndex);
        int iconWidth = 0;
        if (icon != null) {
            iconWidth += icon.getIconWidth() / 2 + textIconGap;
        }
        return super.calculateTabWidth(tabPlacement, tabIndex, metrics) + buttonHeight + iconWidth;
    }

    // ------------------------------------------------------------------------------------------------------------------
    //  Custom painting methods
    // ------------------------------------------------------------------------------------------------------------------

    protected void paintTabArea(Graphics g, int tabPlacement, int selectedIndex) {
        if (selectedIndex == -1) {
            return;
        }

        int tw = tabPane.getBounds().width;

        g.setColor(tabColor);
        g.fillRect(0, 0, tw, buttonHeight);
        g.draw3DRect(0, 0, leftInset - 1, buttonHeight, true);

        int x = rects[rects.length - 1].x + rects[rects.length - 1].width;
        g.draw3DRect(x, 0, tw - x - 1, buttonHeight, true);

        g.setColor(Color.black);
        g.drawLine(0, buttonHeight + 1, tw - 1, buttonHeight + 1);

        for (int i = 1; i <= fadeColorCount; i++) {
            g.setColor(fadeColors[fadeColorCount - 1]);
            g.drawLine(0, buttonHeight + 1 + i, tw - 1, buttonHeight + 1 + i);
        }

        try {
            super.paintTabArea(g, tabPlacement, selectedIndex);
        } catch (Exception e) {
        }
    }


    protected void paintTabBorder(Graphics g, int tabPlacement, int tabIndex, int tx, int ty, int tw, int th, boolean isSelected) {
        Graphics2D g2d = (Graphics2D) g;

        g2d.translate(tx, 0);

        if (isSelected) {
            int[] x = new int[3];
            int[] y = new int[3];

            g.setColor(tabColor);

            g.fillRect(0, 0, tw, buttonHeight);
            g.draw3DRect(0, 0, tw - 1, buttonHeight, true);
            g.fillRect(buttonHeight / 2, buttonHeight, tw - buttonHeight, buttonHeight / 2 + 1);

            // Left Polygon
            x[0] = 0;
            y[0] = buttonHeight;
            x[1] = buttonHeight / 2;
            y[1] = buttonHeight + (buttonHeight / 2);
            x[2] = buttonHeight / 2;
            y[2] = buttonHeight;
            g.fillPolygon(x, y, 3);

            // Right Polygon
            x[0] = tw;
            y[0] = buttonHeight;
            x[1] = tw - buttonHeight / 2;
            y[1] = buttonHeight + (buttonHeight / 2);
            x[2] = tw - buttonHeight / 2;
            y[2] = buttonHeight;
            g.fillPolygon(x, y, 3);

            g.setColor(backgroundDarker1);
            g.drawLine(0, buttonHeight, buttonHeight / 2, buttonHeight + (buttonHeight / 2));

            g.setColor(backgroundDarker2);
            g.drawLine(0, buttonHeight + 1, buttonHeight / 2, buttonHeight + (buttonHeight / 2) + 1);
            g.drawLine(buttonHeight / 2, buttonHeight + (buttonHeight / 2) + 1, tw - buttonHeight / 2, buttonHeight + (buttonHeight / 2) + 1);
            g.drawLine(tw - buttonHeight / 2, buttonHeight + (buttonHeight / 2), tw, buttonHeight);

            g.setColor(Color.black);
            g.drawLine(buttonHeight / 2 + 1, buttonHeight + (buttonHeight / 2) + 2, tw - buttonHeight / 2 - 1, buttonHeight + (buttonHeight / 2) + 2);
            g.drawLine(tw - buttonHeight / 2 - 1, buttonHeight + (buttonHeight / 2) + 2, tw, buttonHeight + 1);

            for (int i = 1; i <= fadeColorCount; i++) {
                g.setColor(fadeColors[i - 1]);
                g.drawLine(buttonHeight / 2 + 2 + i, buttonHeight + (buttonHeight / 2) + 2 + i, tw - buttonHeight / 2 - 1, buttonHeight + (buttonHeight / 2) + 2 + i);
                g.drawLine(tw - buttonHeight / 2 - 1, buttonHeight + (buttonHeight / 2) + 2 + i, tw, buttonHeight + 1 + i);
            }
        } else {
            g.setColor(tabColor);

            g.fillRect(0, 0, tw, buttonHeight);
            g.draw3DRect(0, 0, tw - 1, buttonHeight, true);

            g.setColor(Color.black);
            g.drawLine(0, buttonHeight + 1, tw - 1, buttonHeight + 1);

            for (int i = 1; i <= fadeColorCount; i++) {
                g.setColor(fadeColors[fadeColorCount - 1]);
                g.drawLine(0, buttonHeight + 1 + i, tw - 1, buttonHeight + 1 + i);
            }
        }

        g2d.translate(-1 * tx, 0);
    }


    protected void paintIcon(Graphics g, int tabPlacement, int tabIndex, Icon icon, Rectangle iconRect, boolean isSelected) {
        if (icon == null) {
            return;
        }
        int offsetX, offsetY;
        if (isSelected) {
            offsetY = 0;
        } else {
            offsetY = -14;
        }

        offsetX = 0;
        if (icon != null) {
            offsetX = (icon.getIconWidth() + textIconGap) / 2;
        }

        icon.paintIcon(tabPane, g, iconRect.x - offsetX, iconRect.y + offsetY);
    }


    protected void paintText(Graphics g, int tabPlacement, Font font, FontMetrics metrics, int tabIndex, String title, Rectangle textRect, boolean isSelected) {
        Rectangle r = rects[tabIndex];

        Graphics2D g2d = (Graphics2D) g;
        g2d.translate(r.x, 0);

        Icon icon = getIconForTab(tabIndex);
        int iconWidth = 0;
        if (icon != null) {
            iconWidth = icon.getIconWidth() / 2;
        }

        if (isSelected) {
            g.setColor(highlight);
            g.setFont(g.getFont().deriveFont(Font.BOLD));
            FontMetrics fm = g.getFontMetrics();
            g.drawString(title, (r.width / 2 + iconWidth - fm.stringWidth(title) / 2), buttonHeight / 2 + fm.getMaxDescent() + buttonHeight / 2 + 3);

        } else {
            FontMetrics fm = getFontMetrics();
            g.setColor(shadow);
            g.drawString(title, (r.width / 2 + iconWidth - fm.stringWidth(title) / 2) + 1, buttonHeight / 2 + fm.getMaxDescent() + 2);
        }

        g2d.translate(-1 * r.x, 0);
    }

    // ------------------------------------------------------------------------------------------------------------------
    //  Methods that we want to suppress the behaviour of the superclass
    // ------------------------------------------------------------------------------------------------------------------

    protected void paintTabBackground(Graphics g, int tabPlacement, int tabIndex, int x, int y, int w, int h, boolean isSelected) {
        // Do nothing
    }


    protected void paintFocusIndicator(Graphics g, int tabPlacement, Rectangle[] rects, int tabIndex, Rectangle iconRect, Rectangle textRect, boolean isSelected) {
        // Do nothing
    }


    protected void paintContentBorder(Graphics g, int tabPlacement, int selectedIndex) {
    }


    public int getLeftInset() {
        return leftInset;
    }


    public void setLeftInset(int leftInset) {
        this.leftInset = leftInset;
    }
}
