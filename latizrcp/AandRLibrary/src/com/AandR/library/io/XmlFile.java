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
package com.AandR.library.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;

import java.io.OutputStream;
import java.util.List;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

/**
 * 
 * @author Dr. Richard St. John
 * @version $Revision: 1.1 $, $Date: 2007/05/25 00:12:27 $
 */
public class XmlFile {

    public XmlFile() {
    }

    /**
     * This method parses an xml document via SAXBuilder, returning a Document Class
     * @param file The file name with path of the XML document to read
     * @return JDOM Document Class
     * @throws IOException
     * @throws JDOMException
     */
    public static Document readDocument(File file) throws JDOMException, IOException {
        FileInputStream infile = new FileInputStream(file);
        SAXBuilder builder = new SAXBuilder(false);
        Document thisDocument = builder.build(infile);
        infile.close();
        return thisDocument;
    }

    /**
     *
     * @param inputStream
     * @return
     * @throws JDOMException
     * @throws IOException
     */
    public static Document readDocument(InputStream inputStream) throws JDOMException, IOException {
        SAXBuilder builder = new SAXBuilder(false);
        Document thisDocument = builder.build(inputStream);
        inputStream.close();
        return thisDocument;
    }

    /**
     *
     * @param file
     * @return
     * @throws IOException
     * @throws JDOMException
     */
    public static Element readRootElement(File file) throws JDOMException, IOException {
        return readDocument(file).getRootElement();
    }

    /**
     * Writes an XML document element with the given element as its root.
     * @param file
     * @param doc
     * @throws IOException
     */
    public static void write(File file, Document doc) throws IOException {
        XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
        FileWriter writer = new FileWriter(file);
        outputter.output(doc, writer);
        writer.close();
    }

    /**
     * Writes an XML document element with the given element as its root.
     * @param file
     * @param root
     * @throws IOException
     */
    public static void write(File file, Element root) throws IOException {
        write(file, new Document(root));
    }

    public static void write(OutputStream outputStream, Document doc) throws IOException {
        XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
        outputter.output(doc, outputStream);
        outputStream.close();
    }

    /**
     * Returns true is the elements are the same
     * @param a
     * @param b
     * @return
     */
    public static boolean compare(Element a, Element b) {
        boolean same = true;
        List aAttrs = a.getAttributes();
        List bAttrs = b.getAttributes();
        if (aAttrs.size() != bAttrs.size()) {
            return false;
        }

        Attribute aAttr, bAttr;
        for (int i = 0; i < aAttrs.size(); i++) {
            aAttr = (Attribute) aAttrs.get(i);
            bAttr = (Attribute) bAttrs.get(i);
            if(!aAttr.getValue().equals(bAttr.getValue())) return false;
        }

        List aChildren = a.getChildren();
        List bChildren = b.getChildren();
        if (aChildren.size() != bChildren.size()) {
            return false;
        }

        if (aChildren.size() == 0) {
            return a.getText().equals(b.getText());
        }

        Element childA, childB;
        for (int i = 0; i < aChildren.size(); i++) {
            childA = (Element) aChildren.get(i);
            childB = (Element) bChildren.get(i);
            if(childB == null) return false;

            same = compare(childA, childB);
            if (!same) {
                return false;
            }
        }

        return true;
    }
}
