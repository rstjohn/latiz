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
package com.AandR.palette.paletteScene.actions;

import com.AandR.latiz.core.lookup.LatizLookup;
import com.AandR.palette.cookies.PaletteSelectionCookie;
import com.AandR.palette.paletteScene.PaletteEditor;
import com.AandR.palette.paletteScene.PaletteTopComponent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Set;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;


public final class NewPaletteSceneAction implements ActionListener {
    private int counter = 1;
    private String paletteName;

    public void actionPerformed(ActionEvent e) {
        PaletteCounter paletteCounter = LatizLookup.getDefault().lookup(PaletteCounter.class);
        if(paletteCounter==null) {
            paletteCounter = new PaletteCounter();
            LatizLookup.getDefault().addToLookup(paletteCounter);
        }
        if(paletteCounter.getCount()==1) return;
        String valueString = paletteCounter.getCount()==0 ? "" : "-" + paletteCounter.getCount();
        createNewPalette("Untitled" + valueString);
        paletteCounter.plusOne();
    }

    private void createNewPalette(String name) {
        paletteName = createNewPaletteName(name);
        final PaletteTopComponent palette = new PaletteTopComponent();
        palette.setName(paletteName);
        palette.setDisplayName(paletteName);
        palette.open();
        palette.requestActive();
        LatizLookup.getDefault().removeAllFromLookup(PaletteSelectionCookie.class);
        LatizLookup.getDefault().addToLookup(new PaletteSelectionCookie() {
            public PaletteEditor getActivePalette() {
                return palette;
            }
        });
    }

    private String createNewPaletteName(String name) {
        Set<TopComponent> reg = WindowManager.getDefault().getRegistry().getOpened();
        for (TopComponent tc2 : new ArrayList<TopComponent>(reg)) {
            if (tc2.getName().equals(name)) {
                int l = name.lastIndexOf("-");
                l = l<0 ? name.length()-1 : l;
                name = name.substring(0, l+1) + "-" + counter++;
                createNewPaletteName(name);
            }
        }
        return name;
    }

    public String getPaletteName() {
        return paletteName;
    }
}
