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
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;

//import org.apache.tools.ant.Project;
//import org.apache.tools.ant.taskdefs.Delete;
/**
 * @author Aaron Masino and Dr. Richard St. John
 * @version $Revision: 1.6 $, $Date: 2007/09/26 16:07:59 $
 */
public class FileSystem {
    public static final int LINUX = 0;
    public static final int WINDOWS = 1;
    public static final int DEFAULT = 2;
    private static int wait_msec = 2;

    public static void moveDirectoryContents(String sourceDirectory, String destinationDirectory) throws IOException {
        File source = new File(sourceDirectory);
        File[] sourceFiles = source.listFiles();
        new File(destinationDirectory).mkdirs();

        String newDest, parent;
        for (int i = 0; i < sourceFiles.length; i++) {
            if (sourceFiles[i].isDirectory()) {
                parent = sourceFiles[i].getParent();
                newDest = sourceFiles[i].getPath().substring(parent.length(), sourceFiles[i].getPath().length());
                moveDirectoryContents(sourceFiles[i].getPath(), destinationDirectory + newDest);
                continue;
            }
            moveFile(sourceFiles[i], destinationDirectory);
        }
    }

    /**
     * returns true if f1 is a subdirectory of f2
     * @param f1
     * @param f2
     * @return
     */
    public static boolean isChildOf(File f1, File[] f2) {
        for (int i = 0; i < f2.length; i++) {
            if (isChildOf(f1, f2[i])) {
                return true;
            }
        }
        return false;
    }

    /**
     * returns true if f1 is a subdirectory of f2
     * @param f1
     * @param f2
     * @return
     */
    public static boolean isChildOf(File f1, File f2) {
        if (f1.getPath().equalsIgnoreCase(f2.getPath())) {
            return true;
        }
        String dir = f2.getPath();
        while ((f1 = f1.getParentFile()) != null) {
            if (f1.getPath().equalsIgnoreCase(dir)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Replaces all path separators with the specified path separator.
     * @param path The string containing the path to be converted.
     * @param type The integer flag specifying the file separator to use. Fields in this class can be used to set this flag.
     * @return
     */
    public static String pathSeparatorConversion(String path, int type) {
        int index, cumIndex = 0;
        char sep = File.separatorChar;
        switch (type) {
            case 0:
                sep = '/';
                break;
            case 1:
                sep = '\\';
                break;
            case 2:
                sep = File.separatorChar;
                break;
        }

//  Replace all Windows Separators
        String tempPath = new String(path);
        StringBuffer string = new StringBuffer(tempPath);
        while ((index = tempPath.indexOf('\\')) != -1) {
            cumIndex += index;
            string.setCharAt(cumIndex, sep);
            tempPath = tempPath.substring(index + 1, tempPath.length());
            cumIndex += 1;
        }

//  Replace all Linux Separators
        cumIndex = 0;
        tempPath = new String(string);
        while ((index = tempPath.indexOf('/')) != -1) {
            cumIndex += index;
            string.setCharAt(cumIndex, sep);
            tempPath = tempPath.substring(index + 1, tempPath.length());
            cumIndex += 1;
        }
        return string.toString();
    }

    public static void moveDirectory(String sourceDirectory, String destinationDirectory) throws IOException {
        String finalDestination = sourceDirectory.substring(sourceDirectory.lastIndexOf(File.separator, sourceDirectory.length()));
        finalDestination = destinationDirectory + finalDestination;
        moveDirectoryContents(sourceDirectory, finalDestination);
        try {
            Thread.sleep(wait_msec);
        } catch (Exception e) {
            e.printStackTrace();
        }

        deleteDirectory(sourceDirectory);
    }

    public static TreeMap<File, String> probeDirectory(String sourceDirectory, String mainOutputFileName) {
        return probeDirectory(sourceDirectory, mainOutputFileName, "");
    }

    public static TreeMap<File, String> probeDirectory(String[] sourceDirectory, String mainFileStartsWith, String mainFileEndsWith) {
        TreeMap<File, String> collection = new TreeMap<File, String>(new Comparator<File>() {

            public int compare(File file1, File file2) {
                return file1.getName().compareTo(file2.getName());
            }
        });
        for (int i = 0; i < sourceDirectory.length; i++) {
            collection.putAll(probeDirectory(sourceDirectory[i], mainFileStartsWith, mainFileEndsWith));
        }
        return collection;
    }

    public static TreeMap<File, String> probeDirectory(String sourceDirectory, String mainFileStartsWith, String mainFileEndsWith) {
        TreeMap<File, String> collection = new TreeMap<File, String>(new Comparator<File>() {

            public int compare(File file1, File file2) {
                return file1.getName().compareTo(file2.getName());
            }
        });
        boolean endsWith;
        File source = new File(sourceDirectory);
        File[] sourceFiles = source.listFiles();
        File thisFile;
        for (int i = 0; i < sourceFiles.length; i++) {
            thisFile = sourceFiles[i];
            if (thisFile.isDirectory()) {
                collection.putAll(probeDirectory(thisFile.getPath(), mainFileStartsWith, mainFileEndsWith));
                continue;
            }
            endsWith = thisFile.getName().endsWith(mainFileEndsWith);

            if (thisFile.getName().startsWith(mainFileStartsWith) && endsWith) {
                collection.put(thisFile, "mainFile");
            } else if (thisFile.getName().startsWith("p" + mainFileStartsWith) && endsWith) {
                collection.put(thisFile, "pFile");
            } else if (thisFile.getName().startsWith("cd" + mainFileStartsWith) && endsWith) {
                collection.put(thisFile, "cdFile");
            } else if (thisFile.getName().startsWith("cf" + mainFileStartsWith) && endsWith) {
                collection.put(thisFile, "cfFile");
            } //    Tracker Files
            else if (thisFile.getName().startsWith("ta" + mainFileStartsWith) && endsWith) {
                collection.put(thisFile, "taFile");
            } else if (thisFile.getName().startsWith("tb" + mainFileStartsWith) && endsWith) {
                collection.put(thisFile, "tbFile");
            } else if (thisFile.getName().startsWith("tc" + mainFileStartsWith) && endsWith) {
                collection.put(thisFile, "tcFile");
            } else if (thisFile.getName().startsWith("td" + mainFileStartsWith) && endsWith) {
                collection.put(thisFile, "tdFile");
            } else if (thisFile.getName().startsWith("te" + mainFileStartsWith) && endsWith) {
                collection.put(thisFile, "teFile");
            } else if (thisFile.getName().startsWith("tf" + mainFileStartsWith) && endsWith) {
                collection.put(thisFile, "tfFile");
            } else if (thisFile.getName().startsWith("tg" + mainFileStartsWith) && endsWith) {
                collection.put(thisFile, "tgFile");
            } else if (thisFile.getName().startsWith("th" + mainFileStartsWith) && endsWith) {
                collection.put(thisFile, "thFile");
            } else if (thisFile.getName().startsWith("ti" + mainFileStartsWith) && endsWith) {
                collection.put(thisFile, "tiFile");
            } else if (thisFile.getName().startsWith("tj" + mainFileStartsWith) && endsWith) {
                collection.put(thisFile, "tjFile");
            } //    WFS Files
            else if (thisFile.getName().startsWith("wa" + mainFileStartsWith) && endsWith) {
                collection.put(thisFile, "waFile");
            } else if (thisFile.getName().startsWith("wb" + mainFileStartsWith) && endsWith) {
                collection.put(thisFile, "wbFile");
            } else if (thisFile.getName().startsWith("wc" + mainFileStartsWith) && endsWith) {
                collection.put(thisFile, "wcFile");
            } else if (thisFile.getName().startsWith("wd" + mainFileStartsWith) && endsWith) {
                collection.put(thisFile, "wdFile");
            } else if (thisFile.getName().startsWith("we" + mainFileStartsWith) && endsWith) {
                collection.put(thisFile, "weFile");
            } else if (thisFile.getName().startsWith("wf" + mainFileStartsWith) && endsWith) {
                collection.put(thisFile, "wfFile");
            } else if (thisFile.getName().startsWith("wg" + mainFileStartsWith) && endsWith) {
                collection.put(thisFile, "wgFile");
            } else if (thisFile.getName().startsWith("wh" + mainFileStartsWith) && endsWith) {
                collection.put(thisFile, "whFile");
            } else if (thisFile.getName().startsWith("wi" + mainFileStartsWith) && endsWith) {
                collection.put(thisFile, "wiFile");
            } else if (thisFile.getName().startsWith("wj" + mainFileStartsWith) && endsWith) {
                collection.put(thisFile, "wjFile");
            }
        }
        return collection;
    }

    public static void copyDirectory(File sourceDirectory, File destinationDirectory) throws IOException {
        if (sourceDirectory.isFile() || destinationDirectory.isFile()) {
            return;
        }

        File finalDestinationFile = new File(destinationDirectory, sourceDirectory.getName());
        copyDirectoryContents(sourceDirectory.getPath(), finalDestinationFile.getPath());
    }

    public static void copyDirectory(String sourceDirectory, String destinationDirectory) throws IOException {
        String finalDestination = sourceDirectory.substring(sourceDirectory.lastIndexOf(File.separator, sourceDirectory.length()));
        finalDestination = destinationDirectory + finalDestination;
        copyDirectoryContents(sourceDirectory, finalDestination);
    }

    public static void copyDirectoryContents(String sourceDirectory, String destinationDirectory) throws IOException {
        File source = new File(sourceDirectory);
        File[] sourceFiles = source.listFiles();
        new File(destinationDirectory).mkdirs();

        String newDest, parent;
        for (int i = 0; i < sourceFiles.length; i++) {
            if (sourceFiles[i].isDirectory()) {
                parent = sourceFiles[i].getParent();
                newDest = sourceFiles[i].getPath().substring(parent.length(), sourceFiles[i].getPath().length());
                copyDirectoryContents(sourceFiles[i].getPath(), destinationDirectory + newDest);
                continue;
            }
            copyFile(sourceFiles[i], destinationDirectory + File.separator + sourceFiles[i].getName());
        }
    }

    public static synchronized void delete(File file) {
        if (file.isDirectory()) {
            deleteDirectory(file);
        } else {
            while (file.exists()) {
                deleteFile(file);
            }
        }
    }

    /*
     */
    public static boolean deleteFile(File file) {
        if (file == null) {
            return false;
        }
        boolean success = file.delete();
        return success;
    }

    public static synchronized boolean deleteFiles(File[] files) {
        boolean success = true;
        for (int i = 0; i < files.length; i++) {
            success = success && deleteFile(files[i]);
        }
        return success;
    }

    public final static synchronized boolean renameFile(File oldFile, File newFile) {
        return oldFile.renameTo(newFile);
    }

    public static void moveFiles(File[] files, String directory) {
        File thisFile, fileCopy;
        File dir = new File(directory);
        dir.mkdirs();
        for (int i = 0; i < files.length; i++) {
            thisFile = files[i];
            fileCopy = new File(dir, thisFile.getName());
            thisFile.renameTo(fileCopy);
        }
    }

    /*
    public static void deleteFilesWithAnt(File[] files) {
    Project proj = new Project();
    Delete delete = new Delete();
    delete.setProject(proj);
    for(int i=0; i<files.length; i++) {
    delete.setFile(files[i]);
    delete.execute();
    }
    }

    public static void deleteFileWithAnt(File file) {
    Project proj = new Project();
    Delete delete = new Delete();
    delete.setProject(proj);
    delete.setFile(file);
    delete.execute();
    }

    public static void deleteDirectoryWithAnt(File file) {
    Project proj = new Project();
    Delete delete = new Delete();
    delete.setProject(proj);
    delete.setDir(file);
    delete.execute();
    }


    public static synchronized void deleteWithAnt(File file) {
    if(file.isDirectory())
    deleteDirectoryWithAnt(file);
    else {
    while(file.exists()) {
    deleteFileWithAnt(file);
    }
    }
    }

     */
    public static boolean deleteDirectory(File dir) {
        boolean success;
        int i;
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (i = 0; i < children.length; i++) {
                success = deleteDirectory(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }

        // The directory is now empty so delete it
        return dir.delete();
    }

    public static boolean deleteDirectory(String directory) {
        return deleteDirectory(new File(directory));
    }

    public static boolean moveFile(File file, String newDirectory) throws IOException {
        copyFile(file, newDirectory, file.getName());
        deleteFile(file);
        return true;
    }

    public static void copyFile(InputStream stream, File destination) throws IOException {
        OutputStream out = new FileOutputStream(destination);
        byte[] buf = new byte[1024];
        int len;
        while ((len = stream.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
        stream.close();
        out.close();
    }

    public static void copyFileChannel(File srcFile, File dstFile) throws IOException {
        FileChannel src = new FileInputStream(srcFile).getChannel();
        FileChannel dst = new FileOutputStream(dstFile).getChannel();
        try {
            dst.transferFrom(src, 0, src.size());
        } finally {
            src.close();
            dst.close();
        }
    }

    public static void copyFile(File source, File destination) throws IOException {
        InputStream in = new FileInputStream(source);
        OutputStream out = new FileOutputStream(destination);

        // Transfer bytes from in to out
        byte[] buf = new byte[1024];
        int len;
        while ((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
        in.close();
        out.close();
    }

    public static void copyFile(File source, String newDirectory, String newFileName) throws IOException {
        new File(newDirectory).mkdirs();
        File destination = new File(newDirectory + File.separator + newFileName);
        copyFile(source, destination);
    }

    public static void copyFile(File source, String newFilePath) throws IOException {
        String newDirectory = newFilePath.substring(0, newFilePath.lastIndexOf(File.separator));
        String newFileName = newFilePath.substring(newFilePath.lastIndexOf(File.separator) + 1, newFilePath.length());
        copyFile(source, newDirectory, newFileName);
    }

    /**
     * Returns a list of files that vary by a numeric index. The first file in the series is used as the file template.
     * @param firstFile
     * @return
     */
    public static File[] getFileSeries(File firstFile) {
        int prefixLength = 0, numberLength = 0;
        String firstFileName = firstFile.getName();
        char[] thisChar = firstFileName.toCharArray();

//  Find Prefix Markers    
        for (int i = 0; i < thisChar.length; i++) {
            try {
                Integer.parseInt(String.valueOf(thisChar[i]));
                prefixLength = i;
                break;
            } catch (NumberFormatException ne) {
            }
        }

//  Find First Number Marker
        for (int i = prefixLength; i < thisChar.length; i++) {
            try {
                Integer.parseInt(String.valueOf(thisChar[i]));
                numberLength++;
            } catch (NumberFormatException ne) {
                break;
            }
        }

        String prefix = firstFileName.substring(0, prefixLength);
        String suffix = firstFileName.substring(prefixLength + numberLength, firstFileName.length());
        String number = firstFileName.substring(prefixLength, prefixLength + numberLength);
        int firstFileNumber, thisFileNumber;

        try {
            firstFileNumber = Integer.parseInt(number);
        } catch (NumberFormatException ne) {
            return new File[]{firstFile};
        }

        File[] listOfPossibleFiles = firstFile.getParentFile().listFiles();
        File thisFile = null;
        String thisFileName;
        ArrayList<File> listOfFiles = new ArrayList<File>();
        for (int i = 0; i < listOfPossibleFiles.length; i++) {
            thisFile = listOfPossibleFiles[i];
            thisFileName = thisFile.getName();
            if (thisFileName.startsWith(prefix) && thisFileName.endsWith(suffix) && thisFileName.length() == firstFileName.length()) {
                number = thisFileName.substring(prefixLength, prefixLength + numberLength);
                thisFileNumber = Integer.parseInt(number);
                if (thisFileNumber >= firstFileNumber) {
                    listOfFiles.add(thisFile);
                }
            }
        }

//  Put in File array
        File[] files = new File[listOfFiles.size()];
        for (int i = 0; i < listOfFiles.size(); i++) {
            files[i] = listOfFiles.get(i);
        }
        return files;
    }

    /**
     * Gets this directory contents that match the given extension.
     * @param directory
     * @param extenstion
     * @return
     */
    static public File[] getFileListing(File directory, final String extenstion) {
        if (!directory.isDirectory()) {
            return null;
        }
        File[] files = directory.listFiles(new FileFilter() {

            public boolean accept(File pathname) {
                return pathname.getPath().endsWith(extenstion);
            }
        });
        return files;
    }

    /**
     * Recursively walk a directory tree and return a List of all
     * Files found; the List is sorted using File.compareTo.
     *
     * @param aStartingDir is a valid directory, which can be read.
     */
    static public List<File> getFileListing(File aStartingDir) throws FileNotFoundException {
        if (aStartingDir == null) {
            throw new IllegalArgumentException("Directory May Not be Null");
        }
        List<File> result = new ArrayList<File>();

        File[] filesAndDirs = aStartingDir.listFiles();
        List<File> filesDirs = Arrays.asList(filesAndDirs);
        Iterator<File> filesIter = filesDirs.iterator();
        File file = null;
        while (filesIter.hasNext()) {
            file = filesIter.next();
            result.add(file); //always add, even if directory
            if (!file.isFile()) {
                //must be a directory
                //recursive call!
                List<File> deeperList = getFileListing(file);
                result.addAll(deeperList);
            }

        }
        Collections.sort(result);
        return result;
    }
}
