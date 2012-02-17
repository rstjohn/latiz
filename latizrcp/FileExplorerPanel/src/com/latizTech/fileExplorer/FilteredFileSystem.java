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
import java.io.FilenameFilter;
import org.openide.filesystems.LocalFileSystem;


/**
 *
 * @author stjohnr
 */
public class FilteredFileSystem extends LocalFileSystem {

    private boolean showHidden = false;

    private String extension;


    public FilteredFileSystem() {
        this(null, false);
    }


    public FilteredFileSystem(String extension, boolean showHidden) {
        this.extension = extension;
        this.showHidden = showHidden;
        list = new ListDecorator();
    }


    public void setFilenameFilter(String extension) {
        this.extension = extension;
    }


    public void setShowHidden(boolean showHidden) {
        this.showHidden = showHidden;
    }


    /**
     * 
     */
    class ListDecorator implements LocalFileSystem.List {
        public String[] children(String name) {
            File f = new File(getRootDirectory(), name);
            if (f.isDirectory()) {
                return f.list(new Filter());
            } else {
                return null;
            }
        }
    }

    /**
     *
     */
    private class Filter implements FilenameFilter {

        public boolean accept(File dir, String name) {
            File file = new File(dir, name);
            boolean isDirectory = file.isDirectory();
            boolean isMatch = extension == null || extension.equals("") || name.endsWith(extension);
            boolean isHiddenFile = file.isHidden() || file.getName().startsWith(".");
            if (isHiddenFile && !showHidden) {
                return false;
            }

            boolean success = isDirectory || isMatch;
            return success;
        }
    }
}
