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
package com.AandR.palette.plugin;

import com.AandR.palette.paletteScene.PaletteScene;

/**
 *
 * @author rstjohn
 */
public interface IPluginNameChange {
    public void nameWillChange(PaletteScene scene, AbstractPlugin p, String oldName, String newName);
    public void nameChanged(PaletteScene scene, AbstractPlugin p, String oldName, String newName);
}
