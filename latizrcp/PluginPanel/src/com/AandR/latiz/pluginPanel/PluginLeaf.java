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
package com.AandR.latiz.pluginPanel;

import java.awt.Image;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import org.openide.filesystems.FileObject;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.PropertySupport;
import org.openide.nodes.Sheet;
import org.openide.util.ImageUtilities;

/**
 *
 * @author rstjohn
 */
public class PluginLeaf extends AbstractNode implements Transferable {
    public static final DataFlavor DATA_FLAVOR = new DataFlavor(PluginLeaf.class, "pluginFlavor");
    static final Image ICON = ImageUtilities.loadImage("com/AandR/latiz/pluginPanel/pluginIcon.png");

    private FileObject fileObject;

    public PluginLeaf(FileObject f) {
        super(Children.LEAF);
        setDisplayName(f.getName());
        setName(f.getName());
        this.fileObject = f;
    }

    public FileObject getFileObject() {
        return fileObject;
    }

    @Override
    public Image getIcon(int arg0) {
        return ICON;
    }

    @Override
    protected Sheet createSheet() {
        Sheet result = super.createSheet();
        Sheet.Set set = Sheet.createPropertiesSet();

        String author = (String) fileObject.getAttribute("author");
        set.put(new AuthorProperty(author==null ? "<author>" : author));

        String date = (String) fileObject.getAttribute("date");
        set.put(new DateProperty(date==null ? "<date>" : date));

        String revision = (String)fileObject.getAttribute("rev");
        set.put(new RevisionProperty(revision==null ? "<revision>" : revision));

        String shortDesc = (String) fileObject.getAttribute("shortDesc");
        String longDesc = (String) fileObject.getAttribute("longDesc");
        set.put(new DescriptionProperty(shortDesc==null ? "<short>" : shortDesc, longDesc==null ? "<long>" : longDesc));
        
        result.put(set);
        return result;
    }

    @Override
    public Transferable drag() throws IOException {
        return this;
    }

    public DataFlavor[] getTransferDataFlavors() {
        return new DataFlavor[] {DATA_FLAVOR};
    }

    public boolean isDataFlavorSupported(DataFlavor flavor) {
        return flavor == DATA_FLAVOR;
    }

    public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
        if(flavor==DATA_FLAVOR) {
            return this;
        } else {
            throw new UnsupportedFlavorException(flavor);
        }
    }

    private static final class AuthorProperty extends PropertySupport.ReadOnly<String> {
        private String author;

        public AuthorProperty(String author) {
            super(author, String.class, "Author", author /*Long Description*/);
            this.author = author;
        }


        @Override
        public String getValue() throws IllegalAccessException, InvocationTargetException {
            return author;
        }
    }

    private static final class DateProperty extends PropertySupport.ReadOnly<String> {
        private String date;

        public DateProperty(String date) {
            super(date, String.class, "Date", date /*Long Description*/);
            this.date = date;
        }


        @Override
        public String getValue() throws IllegalAccessException, InvocationTargetException {
            return date;
        }
    }

    private static final class DescriptionProperty extends PropertySupport.ReadOnly<String> {
        private String shortDesc, longDesc;

        public DescriptionProperty(String shortDesc, String longDesc) {
            super(shortDesc, String.class, "Description", longDesc);
            this.shortDesc = shortDesc;
            this.longDesc = longDesc;
        }


        @Override
        public String getValue() throws IllegalAccessException, InvocationTargetException {
            return longDesc;
        }
    }

    private static final class RevisionProperty extends PropertySupport.ReadOnly<String> {
        private String revision;

        public RevisionProperty(String revision) {
            super(revision, String.class, "Revision", revision /*Long Description*/);
            this.revision = revision;
        }


        @Override
        public String getValue() throws IllegalAccessException, InvocationTargetException {
            return revision;
        }
    }

}
