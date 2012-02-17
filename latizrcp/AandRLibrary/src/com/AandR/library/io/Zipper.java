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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import javax.swing.ProgressMonitorInputStream;

//import org.apache.tools.ant.Project;
//import org.apache.tools.ant.taskdefs.Delete;
//import org.apache.tools.ant.taskdefs.Zip;
//import org.apache.tools.ant.types.FileSet;
/**
 * @author Dr. Richard St. John
 * @version $Revision: 1.1 $, $Date: 2007/05/25 00:12:27 $
 */
public class Zipper extends ZipFile {

    /**
     * @throws IOException
     * @throws ZipException
     */
    public Zipper(File file) throws ZipException, IOException {
        super(file);
    }

    public String getFirstEntry() {
        TreeSet<String> entries = new TreeSet<String>();
        for (Enumeration e = entries(); e.hasMoreElements();) {
            entries.add(((ZipEntry) e.nextElement()).getName());
        }
        return entries.first().toString();
    }

    /**
     * Returnss an object array containing the names of the entries.
     * @return
     */
    public String[] getSortedEntries() {
        TreeSet<String> entrySet = new TreeSet<String>();
        for (Enumeration e = entries(); e.hasMoreElements();) {
            entrySet.add(((ZipEntry) e.nextElement()).getName());
        }

        String[] entries = new String[entrySet.size()];
        Iterator it = entrySet.iterator();
        int counter = 0;
        while (it.hasNext()) {
            entries[counter++] = it.next().toString();
        }
        return entries;
    }

    public byte[] readEntry(ZipEntry entry) throws IOException {
        InputStream inputStream = getInputStream(entry);

        BufferedInputStream in = null;
        byte[] buf = null; // output buffer
        int bufLen = 10000 * 1024;
        try {
            ProgressMonitorInputStream progressIn = new ProgressMonitorInputStream(null, "Reading File: " + getName(), inputStream);
            in = new BufferedInputStream(progressIn);
            buf = new byte[bufLen];
            byte[] tmp = null;
            int len = 0;
            int offset = 0;
            while ((len = in.read(buf, offset, bufLen)) != -1) {
                tmp = new byte[len];
                System.arraycopy(buf, 0, tmp, 0, len); // still need to do copy
            }
        } finally {
            if (in != null) {
                try {
                    inputStream.close();
                    in.close();
                } catch (Exception e) {
                    System.out.println("Cannot Close File");
                }
            }
        }
        return buf;
    }

    /**
     *
     * @param entry
     * @return
     * @throws IOException
     */
    public byte[] readEntry(String entry) throws IOException {
        InputStream inputStream = getInputStream(getEntry(entry));

        BufferedInputStream in = null;
        byte[] buf = null; // output buffer
        int bufLen = 10000 * 1024;
        try {
            ProgressMonitorInputStream progressIn = new ProgressMonitorInputStream(null, "Reading File: " + getName(), inputStream);
            in = new BufferedInputStream(progressIn);
            buf = new byte[bufLen];
            byte[] tmp = null;
            int len = 0;
            int offset = 0;
            while ((len = in.read(buf, offset, bufLen)) != -1) {
                tmp = new byte[len];
                System.arraycopy(buf, 0, tmp, 0, len); // still need to do copy
            }
        } finally {
            if (in != null) {
                try {
                    inputStream.close();
                    in.close();
                } catch (Exception e) {
                    System.out.println("Cannot Close File");
                }
            }
        }
        return buf;
    }

    public static void extract(File zipFile, File toDir) {
        Zipper zipper = null;
        try {
            zipper = new Zipper(zipFile);
            zipper.extractAll(toDir.getPath());
        } catch (Exception ex) {
            Logger.getLogger(Zipper.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }
    }

    public File[] extractAll() throws IOException {
        return extractAll(null);
    }

    public File[] extractAll(String parentDirectory) throws IOException {
        String dir = null;
        if (parentDirectory == null || parentDirectory.trim().length() == 0) {
            dir = "";
        } else {
            dir = parentDirectory;
        }

        ArrayList<File> filesArray = new ArrayList<File>();
        Enumeration entries = entries();
        ZipEntry thisEntry = null;
        while (entries.hasMoreElements()) {
            thisEntry = (ZipEntry) entries.nextElement();

            if (thisEntry.isDirectory()) {
                (new File(dir, thisEntry.getName())).mkdirs();
                continue;
            }

            File zipEntryFile = new File(dir, thisEntry.getName());
            filesArray.add(zipEntryFile);
            extract(dir, thisEntry);
        }

        File[] files = new File[filesArray.size()];
        return filesArray.toArray(files);
    }

    public void extract(String parentDirectory, ZipEntry zipEntry) {
        String dir = null;
        if (parentDirectory == null || parentDirectory.trim().length() == 0) {
            dir = "";
        } else {
            dir = parentDirectory;
        }

        // Get the entry
        if (zipEntry.isDirectory()) {
            return;
        }

        InputStream input = null;
        try {
            input = getInputStream(zipEntry);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Create local file
        File outputFile = new File(dir, zipEntry.getName());
        OutputStream out;
        try {
            out = new FileOutputStream(outputFile);
        } catch (FileNotFoundException e) {
            outputFile.getParentFile().mkdirs();
            try {
                out = new FileOutputStream(outputFile);
            } catch (FileNotFoundException e1) {
                e1.printStackTrace();
                return;
            }
        }

        // Transfer bytes from the ZIP file to the output file
        try {
            byte[] buf = new byte[1024];
            int len;
            while ((len = input.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            // Close the streams
            out.close();
            input.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * @param entry
     * @throws IOException
     */
    public void extract(String entry) throws IOException {
        extract(null, getEntry(entry));
    }

    /**
     * Writes a zip file with the contents defined by an array of files.
     * @param zipFilename
     * @param filesToAdd
     * @throws IOException
     */
    public synchronized static void write(File zipFilename, File[] filesToAdd) throws IOException {

//  Create Buffer for reading files.
        int bufLen = 4096;
        byte[] buf = new byte[bufLen];

        ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(zipFilename));

//  Start adding files to zip.          
        for (int i = 0; i < filesToAdd.length; i++) {

            if (filesToAdd[i].isDirectory()) {
                continue;
            }

            FileInputStream in;
            in = new FileInputStream(filesToAdd[i]);

//    Add zip entry to output stream
            zipOut.putNextEntry(new ZipEntry(filesToAdd[i].getName()));

//    Transfer data
            int len;
            while ((len = in.read(buf)) > 0) {
                zipOut.write(buf, 0, len);
            }

//    Complete the entry        
            zipOut.closeEntry();
            in.close();
        }
        zipOut.close();
    }

    /**
     * zips files in the sourceDir that match the includes using the Ant API
     * if deleteFiles = true, files matching the includes are deleted after being zipped
     * @param sourceDir
     * @param destination
     * @param includes File name (not full path) of the files to include in the newly create zip file.
    public static void zip(File sourceDir, File destination, String[] includes, boolean deleteFiles) {
    if(!sourceDir.isDirectory())return;
    Project proj = new Project();
    Zip zip = new Zip();
    zip.setProject(proj);
    zip.setDestFile(destination);
    FileSet thisFileSet;

    for(int i=0; i<includes.length; i++) {
    thisFileSet = new FileSet();
    thisFileSet.setDir(sourceDir);
    thisFileSet.setIncludes(includes[i]);
    zip.addFileset(thisFileSet);
    }

    zip.execute();

    if(!deleteFiles)return;

    Delete delete = new Delete();
    delete.setProject(proj);
    for(int i=0; i<includes.length; i++) {
    thisFileSet = new FileSet();
    thisFileSet.setDir(sourceDir);
    thisFileSet.setIncludes(includes[i]);
    delete.addFileset(thisFileSet);
    }
    delete.execute();
    }
     */
    /**
     * zips files in the sourceDir that match the includes using the Ant API
     * if deleteFiles = true, files matching the includes are deleted after being zipped
     * @param sourceDir
     * @param destination
     * @param includes
     * @param deleteFiles
    public static void zip(File sourceDir, File destination, File[] includes, boolean deleteFiles) {
    if(!sourceDir.isDirectory())return;
    Project proj = new Project();
    Zip zip = new Zip();
    zip.setProject(proj);
    zip.setDestFile(destination);
    FileSet thisFileSet;

    for(int i=0; i<includes.length; i++) {
    thisFileSet = new FileSet();
    thisFileSet.setDir(sourceDir);
    thisFileSet.setIncludes(includes[i].getName());
    zip.addFileset(thisFileSet);
    }

    zip.execute();

    if(!deleteFiles)return;

    Delete delete = new Delete();
    delete.setProject(proj);
    for(int i=0; i<includes.length; i++) {
    thisFileSet = new FileSet();
    thisFileSet.setDir(sourceDir);
    thisFileSet.setIncludes(includes[i].getName());
    delete.addFileset(thisFileSet);
    }
    delete.execute();
    }
     * @throws IOException
     * @throws ZipException
     */
    public static void zip(File sourceDir, File destination, String[] includes, boolean deleteFiles) throws ZipException, IOException {
        File[] files = new File[includes.length];
        for (int i = 0; i < files.length; i++) {
            files[i] = new File(includes[i]);
        }
        zip(sourceDir, destination, files, deleteFiles);
    }

    public static void zip(File sourceDir, File destination, File[] includes, boolean deleteFiles) throws ZipException, IOException {
        ZipOutputStream out = new ZipOutputStream(new FileOutputStream(destination));

        // Create a buffer for reading the files
        byte[] buf = new byte[1024];

        for (int i = 0; i < includes.length; i++) {
            FileInputStream in = new FileInputStream(includes[i]);

            // Add ZIP entry to output stream.
            out.putNextEntry(new ZipEntry(RelativePath.getRelativePath(sourceDir.getParentFile(), includes[i])));

            // Transfer bytes from the file to the ZIP file
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }

            // Complete the entry
            out.closeEntry();
            in.close();
        }

        // Complete the ZIP file
        out.close();
    }

    public static void zipDirectory(File home, File dir2zip, ZipOutputStream zos) throws IOException {

        //get a listing of the directory content
        String[] dirList = dir2zip.list();
        byte[] readBuffer = new byte[2156];
        int bytesIn = 0;

        //loop through dirList, and zip the files
        for (int i = 0; i < dirList.length; i++) {
            File f = new File(dir2zip, dirList[i]);
            if (f.isDirectory()) {
                zipDirectory(home, f, zos);
                continue;
            }
            zos.putNextEntry(new ZipEntry(RelativePath.getRelativePath(home, f)));

            FileInputStream fis = new FileInputStream(f);
            while ((bytesIn = fis.read(readBuffer)) != -1) {
                zos.write(readBuffer, 0, bytesIn);
            }

            //close the Stream
            fis.close();
        }
    }
}
