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
package com.AandR.palette.plugin.hud;

import com.AandR.latiz.core.lookup.LatizLookup;
import com.AandR.palette.ContainerTopComponent;
import com.AandR.palette.paletteScene.IPaletteCleared;
import com.AandR.palette.paletteScene.IPaletteClosed;
import com.AandR.palette.paletteScene.IPaletteSaved;
import com.AandR.palette.paletteScene.PaletteEditor;
import java.awt.Image;
import java.io.File;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import net.miginfocom.swing.MigLayout;
import org.openide.util.ImageUtilities;
import org.openide.windows.TopComponent;

/**
 *
 * @author rstjohn
 */
public class HudContainer extends ContainerTopComponent{
    private boolean isDefaultToolbarVisible = true;
    private boolean isHudVisible = false;
    private boolean isClosable = true;
    private JComponent mainPanel, userToolbar;
    private JToggleButton pauseButton;

    public HudContainer(JComponent mainPanel) {
        this(mainPanel, true, null);
    }

    public HudContainer(JComponent mainPanel, boolean isDefaultToolbarVisible) {
        this(mainPanel, isDefaultToolbarVisible, null);
    }

    public HudContainer(JComponent mainPanel, boolean isDefaultToolbarVisible, JComponent userToolbar) {
        putClientProperty("TopComponentAllowDockAnywhere", Boolean.TRUE);
        this.mainPanel=mainPanel;
        this.isDefaultToolbarVisible = isDefaultToolbarVisible;
        this.userToolbar=userToolbar;
        LatizLookup.getDefault().addToLookup(new PaletteSavedImpl());
        LatizLookup.getDefault().addToLookup(new PaletteClosedImpl());
        LatizLookup.getDefault().addToLookup(new PaletteClearedImpl());
        createContentPane();
    }

    @Override
    protected void componentOpened() {
        isHudVisible = true;
    }

    @Override
    protected void componentClosed() {
        isHudVisible = false;
    }

    @Override
    public boolean canClose() {
        return isClosable;
    }

    public boolean isClosable() {
        return isClosable;
    }

    public void setClosable(boolean isClosable) {
        this.isClosable = isClosable;
    }

    private void createContentPane() {
        setLayout(new MigLayout("ins 0", "[]", "[]0[]"));
        JPanel northPanel = new JPanel(new MigLayout());
        if(isDefaultToolbarVisible) {
            northPanel.add(createToolbar());
        }

        if(userToolbar!=null) {
            northPanel.add(userToolbar, "pushx, growx");
        }

        if(northPanel.getComponents().length>0) {
            add(northPanel, "pushx, growx, wrap");
        }
        add(mainPanel, "push, grow");
    }

    private JPanel createToolbar() {
        JPanel p = new JPanel(new MigLayout("ins 0"));
        JToggleButton playButton = new JToggleButton(new ImageIcon(ImageUtilities.loadImage("com/AandR/palette/plugin/hud/Play16.gif")), true);
        pauseButton = new JToggleButton(new ImageIcon(ImageUtilities.loadImage("com/AandR/palette/plugin/hud/Pause16.gif")), false);
        ButtonGroup bg = new ButtonGroup();
        bg.add(playButton);
        bg.add(pauseButton);

        p.add(playButton, "w 22!, h 22!");
        p.add(pauseButton, "w 22!, h 22!");
        //p.add(helpButton);
        return p;
    }

    public boolean isDefaultToolbarVisible() {
        return isDefaultToolbarVisible;
    }

    public JComponent getMainPanel() {
        return mainPanel;
    }

    public JComponent getUserToolbar() {
        return userToolbar;
    }

    public boolean isPaused() {
        if(pauseButton==null) {
            return false;
        }
        return pauseButton.isSelected();
    }

    public boolean isHudVisible() {
        return isHudVisible;
    }

    public void setHudVisible(boolean isHudVisible) {
        this.isHudVisible = isHudVisible;
    }

    @Override
    public Image getIcon() {
        return ImageUtilities.loadImage("com/AandR/palette/plugin/hud/hudIcon.png");
    }

    @Override
    public int getPersistenceType() {
        return TopComponent.PERSISTENCE_NEVER;
    }

    /**
     *
     */
    private class PaletteSavedImpl implements IPaletteSaved {
        public void save(String oldName, File newFile) {
            if(!getName().startsWith("hudEditor::" + oldName + "::")) {
                return;
            }
            String oldHudName = getName();
            setName("hudEditor::" + newFile.getPath() + "::" + oldHudName.split("::")[2]);
            setToolTipText(getName());
        }
    }

    private class PaletteClosedImpl implements IPaletteClosed {
        public void closed(PaletteEditor paletteEditor) {
            String paletteName = paletteEditor.getName();
            String myName = getName();
            if(myName.startsWith("hudEditor::" + paletteName + "::")) {
                close();
            }
        }
    }

    private class PaletteClearedImpl implements IPaletteCleared {
        public void paletteCleared(String paletteName) {
            String myName = getName();
            if(myName.startsWith("hudEditor::" + paletteName + "::")) {
                close();
            }
        }
    }
}
