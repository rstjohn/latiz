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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.JTable;
import javax.swing.ProgressMonitorInputStream;

/**
 * This class reads and writes ascii files. A string delimiter can be set or use the default ", " delimiter.
 * This class can be subclassed to read data files with a know file structure. 
 * @author Dr. Richard St. John
 * @version $Revision: 1.2 $, $Date: 2007/08/01 01:42:36 $
 */
public class AsciiFile {

    private String delimiter = ", ";

    public AsciiFile() {
        delimiter = ", ";
    }

    public AsciiFile(String delimiter) {
        this.delimiter = delimiter;
    }

    public ArrayList<String> readFile(File fileToRead, String firstLineStartsWith, int numberOfRowsToRead) throws IOException {

//  Read in the file
        FileInputStream ASCIIfile = new FileInputStream(fileToRead);
        ProgressMonitorInputStream progressIn = new ProgressMonitorInputStream(null, "Reading File: " + fileToRead.getPath(), ASCIIfile);
        InputStreamReader ASCIIFileReader = new InputStreamReader(progressIn);
        BufferedReader dataIn = new BufferedReader(ASCIIFileReader);

        String thisLine;
        ArrayList<String> linesInFile = new ArrayList<String>();

        boolean isFirstLineFound = false;
        int counter = 0;
        System.out.println(firstLineStartsWith);
        while (((thisLine = dataIn.readLine()) != null)) {
            if (thisLine.trim().startsWith(firstLineStartsWith.trim())) {
                isFirstLineFound = true;
            }
            if (isFirstLineFound) {
                if (++counter > numberOfRowsToRead) {
                    break;
                }
                linesInFile.add(thisLine);
            }
        }
        dataIn.close();
        ASCIIFileReader.close();
        return linesInFile;
    }

    public static void addLine(File fileToRead, String lineStartsWith, String newLine) throws IOException {
        String[] linesInFile = readLinesInFile(fileToRead);
        String[] newLines = new String[linesInFile.length + 1];
        int j = 0;
        for (int i = 0; i < linesInFile.length; i++) {
            newLines[j++] = linesInFile[i];
            if (linesInFile[i].trim().startsWith(lineStartsWith.trim())) {
                newLines[j++] = newLine;
            }
        }
        write(fileToRead, newLines);
    }

    public static boolean replaceLine(File fileToRead, String lineStartsWith, String replacementLine) throws IOException {
        String[] linesInFile = readLinesInFile(fileToRead);
        boolean isLineFound = false;
        for (int i = 0; i < linesInFile.length; i++) {
            if (linesInFile[i].trim().startsWith(lineStartsWith.trim())) {
                linesInFile[i] = replacementLine;
                isLineFound = true;
            }
        }
        write(fileToRead, linesInFile);
        return isLineFound;
    }

    public static void replaceText(File fileToRead, String stringToReplace, String replacement) throws IOException {

//  Read in the file
        FileInputStream file = new FileInputStream(fileToRead);
        ProgressMonitorInputStream progressIn = new ProgressMonitorInputStream(null, "Reading File: " + fileToRead.getPath(), file);
        BufferedReader dataIn = new BufferedReader(new InputStreamReader(progressIn));

        String thisLine;
        String contents = "";
        while (((thisLine = dataIn.readLine()) != null)) {
            if (!thisLine.contains(stringToReplace)) {
                contents += thisLine + "\n";
            } else {
                contents += thisLine.replace(stringToReplace, replacement) + "\n";
            }
        }
        dataIn.close();
        file.close();
        progressIn.close();
        write(fileToRead, new String[]{contents});
    }

    /**
     * Returns the first line in the ascii file whose line starts with <b>lineStartsWith</b>
     * @param fileToRead
     * @param lineStartsWith
     * @return
     * @throws IOException
     */
    public static String findFirstOccurence(File fileToRead, String lineStartsWith) throws IOException {
        return findOccurence(fileToRead, lineStartsWith, 1);
    }

    public static String findOccurence(File fileToRead, String lineStartsWith, int occurrenceCount) throws IOException {

//  Read in the file
        FileInputStream ASCIIfile = new FileInputStream(fileToRead);
        ProgressMonitorInputStream progressIn = new ProgressMonitorInputStream(null, "Reading File: " + fileToRead.getPath(), ASCIIfile);
        InputStreamReader ASCIIFileReader = new InputStreamReader(progressIn);
        BufferedReader dataIn = new BufferedReader(ASCIIFileReader);

        String thisLine;
        int counter = 1;
        while (((thisLine = dataIn.readLine()) != null)) {
            if (thisLine.trim().startsWith(lineStartsWith.trim()) && counter == occurrenceCount) {
                ASCIIfile.close();
                dataIn.close();
                ASCIIFileReader.close();
                return thisLine.trim();
            }
        }
        return null;
    }

    public static String findLastOccurrence(File fileToRead, String lineStartsWith) throws IOException {

//  Read in the file
        FileInputStream ASCIIfile = new FileInputStream(fileToRead);
        ProgressMonitorInputStream progressIn = new ProgressMonitorInputStream(null, "Reading File: " + fileToRead.getPath(), ASCIIfile);
        InputStreamReader ASCIIFileReader = new InputStreamReader(progressIn);
        BufferedReader dataIn = new BufferedReader(ASCIIFileReader);

        String thisLine;
        ArrayList<String> linesInFile = new ArrayList<String>();

        while (((thisLine = dataIn.readLine()) != null)) {
            if (thisLine.trim().startsWith(lineStartsWith.trim())) {
                linesInFile.add(thisLine);
            }
        }
        dataIn.close();
        ASCIIFileReader.close();
        if (linesInFile.size() == 0) {
            return null;
        }

        Object[] lines = linesInFile.toArray();
        return (String) lines[lines.length - 1];
    }

    public ArrayList<String> readFile(File fileToRead, String firstLineStartsWith, String lastLineStartsWith) throws IOException {

//  Read in the file
        FileInputStream ASCIIfile = new FileInputStream(fileToRead);
        ProgressMonitorInputStream progressIn = new ProgressMonitorInputStream(null, "Reading File: " + fileToRead.getPath(), ASCIIfile);
        InputStreamReader ASCIIFileReader = new InputStreamReader(progressIn);
        BufferedReader dataIn = new BufferedReader(ASCIIFileReader);

        String thisLine;
        ArrayList<String> linesInFile = new ArrayList<String>();

        boolean isFirstLineFound = false;

        while (((thisLine = dataIn.readLine()) != null)) {
            if (thisLine.trim().startsWith(firstLineStartsWith.trim())) {
                isFirstLineFound = true;
            }
            if (isFirstLineFound) {
                linesInFile.add(thisLine);
            }
            if (thisLine.trim().startsWith(lastLineStartsWith.trim())) {
                break;
            }
        }
        dataIn.close();
        ASCIIFileReader.close();
        return linesInFile;
    }

    protected static ArrayList<String> readFile(File fileToRead) throws IOException {

//  Read in the file
        FileInputStream ASCIIfile = new FileInputStream(fileToRead);
        ProgressMonitorInputStream progressIn = new ProgressMonitorInputStream(null, "Reading File: " + fileToRead.getPath(), ASCIIfile);
        InputStreamReader ASCIIFileReader = new InputStreamReader(progressIn);
        BufferedReader dataIn = new BufferedReader(ASCIIFileReader);

        String thisLine;
        ArrayList<String> linesInFile = new ArrayList<String>();
        while (((thisLine = dataIn.readLine()) != null)) {
            linesInFile.add(thisLine);
        }
        ASCIIfile.close();
        progressIn.close();
        dataIn.close();
        ASCIIFileReader.close();
        return linesInFile;
    }

    /**
     * Reads a file line-by-line returning the contents in a string array.
     * @param file
     * @return
     * @throws IOException
     */
    public static String[] readLinesInFile(File fileToRead) throws IOException {
        ArrayList<String> linesInFile = readFile(fileToRead);

        String[] lines = new String[linesInFile.size()];
        for (int i = 0; i < linesInFile.size(); i++) {
            lines[i] = linesInFile.get(i);
        }
        return lines;
    }

    public static String[] readLines(InputStream stream) throws IOException {

//  Read in the file
        InputStreamReader ASCIIFileReader = new InputStreamReader(stream);
        BufferedReader dataIn = new BufferedReader(ASCIIFileReader);

        String thisLine;
        ArrayList<String> linesInFile = new ArrayList<String>();
        while (((thisLine = dataIn.readLine()) != null)) {
            linesInFile.add(thisLine);
        }
        dataIn.close();
        ASCIIFileReader.close();

        String[] lines = new String[linesInFile.size()];
        return linesInFile.toArray(lines);

    }

    /**
     * Reads a file line-by-line returning the contents in a string.
     * @param file
     * @param newLineCharacter
     * @return
     * @throws IOException
     */
    public static String readLinesInFile(File fileToRead, String newLineCharacter) throws IOException {
        ArrayList<String> linesInFile = readFile(fileToRead);
        StringBuffer contents = new StringBuffer("");
        for (int i = 0; i < linesInFile.size() - 1; i++) {
            contents.append(linesInFile.get(i) + newLineCharacter);
        }
        contents.append(linesInFile.get(linesInFile.size() - 1));
        return contents.toString();
    }

    public static String readLinesInFile(InputStream stream) throws IOException {
        String[] text = readLines(stream);
        StringBuilder sb = new StringBuilder();
        for(String l : text) {
            sb.append(l).append("\n");
        }
        return sb.toString();
    }

    public static final void write(File file, double[][] dataToWrite, String delimiter, boolean append) throws IOException {

//  Read in the file
        FileWriter ASCIIfile = new FileWriter(file, append);
        BufferedWriter writer = new BufferedWriter(ASCIIfile);

//  Using Double precision data    
        String thisLine;
        for (int j = 0; j < dataToWrite.length; j++) {
            thisLine = "";
            for (int k = 0; k < dataToWrite[0].length; k++) {
                thisLine += String.valueOf(dataToWrite[j][k]) + delimiter;
            }
            thisLine += "\n";

            writer.write(thisLine);
        }
        writer.close();

    }

    public static final void write(File file, byte[] dataToWrite, String delimiter, boolean append) throws IOException {

//  Read in the file
        FileWriter ASCIIfile = new FileWriter(file, append);
        BufferedWriter writer = new BufferedWriter(ASCIIfile);

//  Using Double precision data    
        String thisLine;
        thisLine = "";
        for (int j = 0; j < dataToWrite.length; j++) {
            thisLine += String.valueOf(dataToWrite[j]) + delimiter;
        }
        thisLine += "\n";
        writer.write(thisLine);
        writer.close();

    }

    /**
     *
     * @param file
     * @param dataToWrite
     * @throws IOException
     */
    public void write(File file, double[][] dataToWrite) throws IOException {

//  Read in the file
        FileWriter ASCIIfile = new FileWriter(file);
        BufferedWriter writer = new BufferedWriter(ASCIIfile);

//  Using Double precision data    
        String thisLine;
        for (int j = 0; j < dataToWrite.length; j++) {
            thisLine = "";
            for (int k = 0; k < dataToWrite[0].length; k++) {
                thisLine += String.valueOf(dataToWrite[j][k]) + delimiter;
            }
            thisLine += "\n";

            writer.write(thisLine);
        }
        writer.close();
    }

    /**
     *
     * @param file
     * @param dataToWrite
     * @throws IOException
     */
    public void write(File file, float[][] dataToWrite) throws IOException {

//  Read in the file
        FileWriter ASCIIfile = new FileWriter(file);
        BufferedWriter writer = new BufferedWriter(ASCIIfile);

//  Using Double precision data    
        String thisLine;
        for (int j = 0; j < dataToWrite.length; j++) {
            thisLine = "";
            for (int k = 0; k < dataToWrite[0].length; k++) {
                thisLine += String.valueOf(dataToWrite[j][k]) + delimiter;
            }
            thisLine += "\n";

            writer.write(thisLine);
        }
        writer.close();
    }

    /**
     *
     * @param file
     * @param dataToWrite
     * @throws IOException
     */
    public static void write(File file, float[][] dataToWrite, String delimiter) throws IOException {

//  Read in the file
        BufferedWriter writer = new BufferedWriter(new FileWriter(file));

//  Using Double precision data    
        String thisLine;
        for (int j = 0; j < dataToWrite.length; j++) {
            thisLine = "";
            for (int k = 0; k < dataToWrite[0].length; k++) {
                thisLine += String.valueOf(dataToWrite[j][k]) + delimiter;
            }
            writer.write(thisLine + "\n");
        }
        writer.close();
    }

    /**
     *
     * @param file
     * @param dataToWrite
     * @throws IOException
     */
    public void write(File file, int[][] dataToWrite) throws IOException {

//  Read in the file
        FileWriter ASCIIfile = new FileWriter(file);
        BufferedWriter writer = new BufferedWriter(ASCIIfile);

//  Using Double precision data    
        String thisLine;
        for (int j = 0; j < dataToWrite.length; j++) {
            thisLine = "";
            for (int k = 0; k < dataToWrite[0].length; k++) {
                thisLine += String.valueOf(dataToWrite[j][k]) + delimiter;
            }
            thisLine += "\n";

            writer.write(thisLine);
        }
        writer.close();
    }

    /**
     *
     * @param file
     * @param dataToWrite
     * @param df
     * @throws IOException
     */
    public void write(File file, double[][] dataToWrite, DecimalFormat df) throws IOException {

//  Read in the file
        FileWriter ASCIIfile = new FileWriter(file);
        BufferedWriter writer = new BufferedWriter(ASCIIfile);

//  Using Double precision data    
        String thisLine;
        for (int j = 0; j < dataToWrite.length; j++) {
            thisLine = "";
            for (int k = 0; k < dataToWrite[0].length; k++) {
                thisLine += df.format(dataToWrite[j][k]) + delimiter;
            }
            thisLine += "\n";

            writer.write(thisLine);
        }
        writer.close();
    }

    /**
     *
     * @param file
     * @param dataToWrite
     * @param df
     * @throws IOException
     */
    public void write(File file, float[][] dataToWrite, DecimalFormat df) throws IOException {

//  Read in the file
        FileWriter ASCIIfile = new FileWriter(file);
        BufferedWriter writer = new BufferedWriter(ASCIIfile);

//  Using Double precision data    
        String thisLine;
        for (int j = 0; j < dataToWrite.length; j++) {
            thisLine = "";
            for (int k = 0; k < dataToWrite[0].length; k++) {
                thisLine += df.format(dataToWrite[j][k]) + delimiter;
            }
            thisLine += "\n";

            writer.write(thisLine);
        }
        writer.close();
    }

    /**
     *
     * @param file
     * @param dataToWrite
     * @param df
     * @throws IOException
     */
    public static void write(File file, float[][] dataToWrite, String delimiter, DecimalFormat df) throws IOException {

//  Read in the file
        BufferedWriter writer = new BufferedWriter(new FileWriter(file));

//  Using Double precision data    
        String thisLine;
        for (int j = 0; j < dataToWrite.length; j++) {
            thisLine = "";
            for (int k = 0; k < dataToWrite[0].length; k++) {
                thisLine += df.format(dataToWrite[j][k]) + delimiter;
            }
            writer.write(thisLine + "\n");
        }
        writer.close();
    }

    public static void write(File file, String[] header, String[][] data) throws IOException {
        write(file, header, data, ", ");
    }

    public static void write(File file, String[][] data, String delimiter) throws IOException {

//  Read in the file
        BufferedWriter writer = new BufferedWriter(new FileWriter(file));
        String thisLine;
        for (int j = 0; j < data.length; j++) {
            thisLine = "";
            for (int k = 0; k < data[0].length; k++) {
                thisLine += data[j][k] + delimiter;
            }
            writer.write(thisLine + "\n");
        }
        writer.close();
    }

    public static void write(File file, String[][] data, String delimiter, boolean append) throws IOException {

//  Read in the file
        FileWriter ASCIIfile = new FileWriter(file, append);
        BufferedWriter writer = new BufferedWriter(ASCIIfile);
        String thisLine;
        for (int j = 0; j < data.length; j++) {
            thisLine = "";
            for (int k = 0; k < data[0].length; k++) {
                thisLine += data[j][k] + delimiter;
            }
            writer.write(thisLine + "\n");
        }
        writer.close();
    }

    public static void write(File file, String[][] data) throws IOException {
        write(file, data, ", ");
    }

    public static void write(File file, String[] header, String[][] data, String delimiter) throws IOException {

//  Read in the file
        BufferedWriter writer = new BufferedWriter(new FileWriter(file));

        String headerRow = "";
        for (int i = 0; i < header.length; i++) {
            headerRow += header[i] + delimiter;
        }
        writer.write(headerRow + "\n");

//  Using Double precision data    
        String thisLine;
        for (int j = 0; j < data.length; j++) {
            thisLine = "";
            for (int k = 0; k < data[0].length; k++) {
                thisLine += data[j][k] + delimiter;
            }
            writer.write(thisLine + "\n");
        }
        writer.close();
    }

    /**
     * Writes a comma delimited file.
     * @param file
     * @param header String array of headers.
     * @param dataToWrite
     * @throws IOException
     */
    public static void write(File file, String[] header, double[][] dataToWrite) throws IOException {

//  Read in the file
        BufferedWriter writer = new BufferedWriter(new FileWriter(file));

        String headerRow = "";
        for (int i = 0; i < header.length; i++) {
            headerRow += header[i] + ", ";
        }
        writer.write(headerRow + "\n");

//  Using Double precision data    
        String thisLine;
        for (int j = 0; j < dataToWrite.length; j++) {
            thisLine = "";
            for (int k = 0; k < dataToWrite[0].length; k++) {
                thisLine += String.valueOf(dataToWrite[j][k]) + ", ";
            }
            writer.write(thisLine + "\n");
        }
        writer.close();
    }

    /**
     *
     * @param file
     * @param dataToWrite
     * @param df
     * @throws IOException
     */
    public void write(File file, int[][] dataToWrite, DecimalFormat df) throws IOException {

//  Read in the file
        FileWriter ASCIIfile = new FileWriter(file);
        BufferedWriter writer = new BufferedWriter(ASCIIfile);

//  Using Double precision data    
        String thisLine;
        for (int j = 0; j < dataToWrite.length; j++) {
            thisLine = "";
            for (int k = 0; k < dataToWrite[0].length; k++) {
                thisLine += df.format(dataToWrite[j][k]) + delimiter;
            }
            thisLine += "\n";

            writer.write(thisLine);
        }
        writer.close();
    }

    /**
     *
     * @param file
     * @param lines
     * @throws IOException
     */
    public static void write(File file, String[] lines) throws IOException {

//  Read in the file
        FileWriter ASCIIfile = new FileWriter(file);
        BufferedWriter writer = new BufferedWriter(ASCIIfile);

//  Using Double precision data    
        for (int i = 0; i < lines.length; i++) {
            writer.write(lines[i] + "\n");
        }
        writer.close();
        ASCIIfile.close();
    }

    /**
     *
     * @param file
     * @param lines
     * @throws IOException
     */
    public static void write(File file, String[] lines, boolean append) throws IOException {

//  Read in the file
        FileWriter ASCIIfile = new FileWriter(file, append);
        BufferedWriter writer = new BufferedWriter(ASCIIfile);

//  Using Double precision data    
        for (int i = 0; i < lines.length; i++) {
            writer.write(lines[i] + "\n");
        }
        writer.close();
        ASCIIfile.close();
    }

    /**
     * Creates new lines from the toString method on each entry of the Vector.
     * @param file
     * @param lines
     * @throws IOException
     */
    public void write(File file, Vector<String> lines) throws IOException {

//  Read in the file
        FileWriter ASCIIfile = new FileWriter(file);
        BufferedWriter writer = new BufferedWriter(ASCIIfile);

//  Using Double precision data    
        for (int i = 0; i < lines.size(); i++) {
            writer.write(lines.get(i).toString() + "\n");
        }
        writer.close();
    }

    /**
     * Returns the delimiter.
     * @return
     */
    public String getDelimiter() {
        return delimiter;
    }

    /**
     * Set the text file delimiter.
     * @param _delimiter
     */
    public void setDelimiter(String delimiter) {
        this.delimiter = delimiter;
    }

    public static void exportTableToTextFile(File file, JTable table, String delimiter) throws IOException {
        int rows = table.getRowCount();
        int cols = table.getColumnCount();
        StringBuilder currentRow;
        String[] lines = new String[rows];
        for (int j = 0; j < rows; j++) {
            currentRow = new StringBuilder();
            for (int k = 0; k < cols; k++) {
                try {
                    currentRow.append(table.getValueAt(j, k).toString() + delimiter);
                } catch (NullPointerException npe) {
                    currentRow.append(delimiter);
                    continue;
                }
            }
            lines[j] = currentRow.toString();
        }
        write(file, lines);
    }
}
