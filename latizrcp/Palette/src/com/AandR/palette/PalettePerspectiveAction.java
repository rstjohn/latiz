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
package com.AandR.palette;

import com.AandR.palette.paletteScene.PaletteTopComponent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;


public final class PalettePerspectiveAction implements ActionListener {

    public void actionPerformed(ActionEvent e) {
        WindowManager wm = WindowManager.getDefault();
        for(TopComponent tc : new ArrayList<TopComponent>(wm.getRegistry().getOpened())) {
            if(tc instanceof PaletteTopComponent) {
                continue;
            }
            tc.close();
        }

        FileObject perspectivesFolder = FileUtil.getConfigRoot().getFileObject("Perspectives");
        FileObject paletteFolder = perspectivesFolder.getFileObject("Palette");
        FileObject[] paletteTopComponents = paletteFolder.getChildren();
        TopComponent tc;
        for(int i=0; i<paletteTopComponents.length; i++) {
            tc = wm.findTopComponent(paletteTopComponents[i].getName());
            if (tc != null && !tc.isOpened()) {
                tc.open();
                if(tc instanceof PaletteTopComponent) {
                    tc.requestActive();
                }
            }
        }
    }
}
