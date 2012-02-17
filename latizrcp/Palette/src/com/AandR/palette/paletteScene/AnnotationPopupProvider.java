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
package com.AandR.palette.paletteScene;

import java.awt.Color;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import org.netbeans.api.visual.action.PopupMenuProvider;
import org.netbeans.api.visual.widget.LabelWidget.Orientation;
import org.netbeans.api.visual.widget.Widget;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;

/**
 *
 * @author rstjohn
 */
public class AnnotationPopupProvider extends JPopupMenu implements PopupMenuProvider {

    public AnnotationPopupProvider(final PaletteScene scene, final AnnotationWidget widget) {
        JMenuItem editTextMenu = new JMenuItem("Edit Text");
        editTextMenu.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                NotifyDescriptor.InputLine nd = new NotifyDescriptor.InputLine("New Text", "Change Text");
                DialogDisplayer.getDefault().notify(nd);
                String text = nd.getInputText();
                widget.setLabel(text);
            }
        });
        add(editTextMenu);
        add(new JSeparator());

        JMenu orientationMenu = new JMenu("Orientation");
        JMenuItem horizontalMenu = new JMenuItem("Horizontal");
        horizontalMenu.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                widget.setOrientation(Orientation.NORMAL);
            }
        });
        orientationMenu.add(horizontalMenu);

        JMenuItem verticalMenu = new JMenuItem("Vertical");
        verticalMenu.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                widget.setOrientation(Orientation.ROTATE_90);
            }
        });
        orientationMenu.add(verticalMenu);
        add(orientationMenu);

        JMenu fontStyleMenu = new JMenu("Font Style");
        JCheckBoxMenuItem plainFont = new JCheckBoxMenuItem("Plain", true);
        plainFont.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                widget.setFont(widget.getFont().deriveFont(Font.PLAIN));
            }
        });
        fontStyleMenu.add(plainFont);

        JCheckBoxMenuItem boldFont = new JCheckBoxMenuItem("Bold");
        boldFont.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                widget.setFont(widget.getFont().deriveFont(Font.BOLD));
            }
        });
        fontStyleMenu.add(boldFont);

        JCheckBoxMenuItem italicsFont = new JCheckBoxMenuItem("Italics");
        italicsFont.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                widget.setFont(widget.getFont().deriveFont(Font.ITALIC));
            }
        });
        fontStyleMenu.add(italicsFont);
        
        JCheckBoxMenuItem boldItalicsFont = new JCheckBoxMenuItem("Bold Italics");
        boldItalicsFont.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                widget.setFont(widget.getFont().deriveFont(Font.ITALIC + Font.BOLD));
            }
        });
        fontStyleMenu.add(boldItalicsFont);
        ButtonGroup bgStyle = new ButtonGroup();
        bgStyle.add(plainFont);
        bgStyle.add(boldFont);
        bgStyle.add(italicsFont);
        bgStyle.add(boldItalicsFont);

        add(fontStyleMenu);

        JMenu fontSizeMenu = new JMenu("Font Size");
        String[] fonts = new String[]{"6", "8", "10", "11", "12", "14", "16", "18", "20", "24", "32", "48", "64", "80"};
        ButtonGroup bg = new ButtonGroup();
        for (String fontSize : fonts) {
            final float size = Float.parseFloat(fontSize);
            JCheckBoxMenuItem mi = new JCheckBoxMenuItem(fontSize);
            bg.add(mi);
            if(fontSize.equals("11")) {
                mi.setSelected(true);
            }
            mi.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    widget.setFont(widget.getFont().deriveFont(size));
                }
            });
            fontSizeMenu.add(mi);
        }
        add(fontSizeMenu);

        Color[] colors = new Color[]{Color.BLACK, Color.BLUE, Color.CYAN, Color.DARK_GRAY, Color.GRAY, Color.GREEN, Color.LIGHT_GRAY, Color.MAGENTA, Color.ORANGE, Color.PINK, Color.RED, Color.WHITE, Color.YELLOW};
        JMenu foregroundColor = new JMenu("Foreground Color");
        for (Color color : colors) {
            JMenuItem mi = new JMenuItem();
            mi.setOpaque(true);
            mi.setBackground(color);
            mi.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    widget.setForeground(((JMenuItem) e.getSource()).getBackground());
                }
            });
            foregroundColor.add(mi);
        }
        add(foregroundColor);

        JMenu backgroundColor = new JMenu("Background Color");
        JMenuItem noBgMenu = new JMenuItem("None");
        noBgMenu.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                widget.setOpaque(false);
            }
        });
        backgroundColor.add(noBgMenu);

        for (Color color : colors) {
            JMenuItem mi = new JMenuItem();
            mi.setOpaque(true);
            mi.setBackground(color);
            mi.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    widget.setOpaque(true);
                    widget.setBackground(((JMenuItem) e.getSource()).getBackground());
                }
            });
            backgroundColor.add(mi);
        }
        add(backgroundColor);

        add(new JSeparator());
        JMenuItem removeItem = new JMenuItem("Remove this annotation");
        removeItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                scene.removeChild(widget);
                scene.validate();
            }
        });
        add(removeItem);
    }

    public JPopupMenu getPopupMenu(Widget arg0, Point arg1) {
        return this;
    }
}
