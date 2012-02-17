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

import java.io.File;
import java.util.ArrayList;
import java.util.Set;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

/**
 *
 * @author rstjohn
 */
public class PaletteUtilities {
    private static int counter = 1;

    public static String createNewPaletteName(String name) {
        Set<TopComponent> reg = WindowManager.getDefault().getRegistry().getOpened();
        for (TopComponent tc2 : new ArrayList<TopComponent>(reg)) {
            if (tc2.getName().equals(name)) {
                int l = name.lastIndexOf("-");
                l = l < 0 ? name.length() - 1 : l;
                name = name.substring(0, l + 1) + "-" + counter++;
                createNewPaletteName(name);
            }
        }
        counter = 1;
        return name;
    }

    public static boolean isFileAlreadyLoaded(File file){
        String paletteName = file.getPath();
        for (TopComponent tc : WindowManager.getDefault().getRegistry().getOpened()) {
            if (tc.getName().equals(paletteName)) {
                tc.requestActive();
                return true;
            }
        }
        return false;
    }

}
