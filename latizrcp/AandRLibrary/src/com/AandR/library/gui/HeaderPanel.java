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
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

import javax.swing.border.LineBorder;
import net.miginfocom.swing.MigLayout;

/**
 * Some of this code was taken from "Swing Hacks", O'Reilly
 * @author Dr. Richard St. John
 * @version $Revision: 1.3 $, $Date: 2007/09/07 16:29:42 $
 */
public class HeaderPanel extends JPanel {
  public static final int ICON_COGS = 0;
  public static final int ICON_SUSE = 1;

  private Icon icon;

  private JLabel titleLabel, messageLabel;

  private String message, title;

  public HeaderPanel(String title, String message, int icon) {
    setLayout(new MigLayout());
    this.title = title;
    this.message = message;
    if(icon==ICON_COGS)
      this.icon = new ImageIcon(getClass().getResource("settings.png"));
    else if(icon==ICON_SUSE)
      this.icon = new ImageIcon(getClass().getResource("SuSEconf32.png"));

    createPanel();
  }

  public HeaderPanel(String title, String message) {
    this(title,message,null);
  }

  public HeaderPanel(String title, String message, Icon icon) {
    super(new MigLayout());
    this.icon = icon;
    this.title = title;
    this.message = message;
    createPanel();
  }

  private void createPanel() {
    JPanel titlePanel = new JPanel(new MigLayout());
    titlePanel.setOpaque(false);
    titlePanel.setBorder(new EmptyBorder(5, 0, 5, 0));

    if(title!=null) {
      titleLabel = new JLabel(title);
      titleLabel.setMinimumSize(new Dimension(20,20));
      Font font = titleLabel.getFont().deriveFont(Font.BOLD);
      titleLabel.setFont(font);
      titleLabel.setBorder(new EmptyBorder(0, 12, 0, 0));
      titlePanel.add(titleLabel, "pushx, growx, wrap");
    }

    if(message!=null) {
      messageLabel = new JLabel(message);
      messageLabel.setMinimumSize(new Dimension(20,20));
      titlePanel.add(messageLabel, "pushx, growx, wrap");
      Font font = messageLabel.getFont().deriveFont(Font.PLAIN);
      messageLabel.setFont(font);
      messageLabel.setBorder(new EmptyBorder(0, 24, 0, 0));
    }

    JLabel iconLabel = new JLabel(icon);
    iconLabel.setBorder(new EmptyBorder(0, 0, 0, 12));

    add(titlePanel, "dock center");
    add(iconLabel, "dock east");
    //setBorder(new LineBorder(Color.BLACK));
  }


  public void setTitleText(String text) {
    title = text;
    titleLabel.setText(text);
  }


  public void setMessageText(String text) {
    message = text;
    messageLabel.setText(text);
  }


    @Override
  public void paintComponent(Graphics g) {
    super.paintComponent(g);

    Color controlColor = UIManager.getColor("Panel.background");
    int width = getWidth();
    int height = getHeight();

    Graphics2D g2 = (Graphics2D) g;
    Paint storedPaint = g2.getPaint();
    int startX = icon == null ? 24 : icon.getIconWidth();
    g2.setPaint(new GradientPaint(startX, 0, Color.WHITE, width-10, height, controlColor, true));
    g2.fillRoundRect(0, 0, width, height, 10, 10);
    g2.setPaint(controlColor.darker().darker());
    g2.drawRoundRect(0, 0, width-1, height-1, 12, 12);
    g2.setPaint(storedPaint);
  }
}
