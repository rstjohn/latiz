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
package com.latizTech.fileExplorer;

import java.io.File;
import java.io.Serializable;

/**
 * @author Dr. Richard St. John
 * @version $Revision: 1.1 $, $Date: 2007/09/04 15:12:58 $
 */
public class ShortcutObject implements Serializable {

    private static final long serialVersionUID = 453764088219157L;
    public int iconIndex;
    public boolean isDefault;
    public File file;
    public String alias;

    public ShortcutObject(String alias, File file, boolean isDefault, int iconIndex) {
        this.alias = alias;
        this.file = file;
        this.isDefault = isDefault;
        this.iconIndex = iconIndex;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean isDefault) {
        this.isDefault = isDefault;
    }

    @Override
    public String toString() {
        return alias;
    }
}
