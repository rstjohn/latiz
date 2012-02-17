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
package com.latizTech.jedit;

import bsh.EvalError;
import bsh.Interpreter;
import com.latizTech.jedit.resources.Resources;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collection;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.TreeSet;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.text.BadLocationException;
import org.openide.windows.IOProvider;
import org.openide.windows.WindowManager;

/**
 * 
 * @author  <a href="mailto://rstjohn@mza.com">Dr. Richard St. John</a>
 * @company <a href="http://www.mza.com">MZA Associates Corporation</a>
 */
public class CodeCompletionDialog extends JDialog {

    private static CodeCompletionDialog instance;
    private String filterString;
    private DefaultListModel listModel;
    private JList list;
    private ListListener listListener;
    private String currentURL;
    private TreeSet<ClassItem> classTreeSet;
    private TreeSet<Field> fieldsTreeSet;
    private TreeSet<Method> methodsTreeSet;
    private Interpreter interpreter;

    public CodeCompletionDialog() {
        super(WindowManager.getDefault().getMainWindow(), false);
        initialize();
    }

    public CodeCompletionDialog(JDialog rootFrame) {
        super(rootFrame, false);
        initialize();
    }

    public CodeCompletionDialog(JFrame rootFrame) {
        super(rootFrame, false);
        initialize();
    }

    public static CodeCompletionDialog getDefault() {
        if (instance == null) {
            instance = new CodeCompletionDialog();
        }
        return instance;
    }

    private void initialize() {
        setAlwaysOnTop(true);
        setUndecorated(true);
        setMinimumSize(new Dimension(300, 220));

        classTreeSet = new TreeSet<ClassItem>(new ClassComparator());
        fieldsTreeSet = new TreeSet<Field>(new FieldComparator());
        methodsTreeSet = new TreeSet<Method>(new MethodComparator());

        listListener = new ListListener();
        listModel = new DefaultListModel();
        list = new JList(listModel);
        list.addMouseListener(listListener);
        list.addKeyListener(listListener);
        list.setCellRenderer(new ReflectListRender());
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setContentPane(new JScrollPane(list));
        pack();
        //setLocation(getCaretPoint());
    }

    private Point getCaretPoint(JEditTextArea textArea) {
        int caretLocation = textArea.getCaretPosition();
        int currentLine = textArea.getLineOfOffset(caretLocation);
        int offsetOfLineStart = textArea.getLineStartOffset(currentLine);

        int x = textArea.offsetToX(currentLine, caretLocation - offsetOfLineStart);
        int y = textArea.lineToY(currentLine);
        Point p = new Point(x, y + 25);
        SwingUtilities.convertPointToScreen(p, textArea);
        return p;
    }

    public void start(JEditTextArea textArea) {
        listListener.setTextArea(textArea);
        interpreter = new Interpreter();

        String currentLine = readCurrentLine(textArea);

        // Find and substring to the first occurence of .
        int i0 = currentLine.indexOf(".");
        if (i0 == -1) {
            parseBuildPath(currentLine);
            setLocation(getCaretPoint(textArea));
            setVisible(true);
            return;
        }

        boolean isNested = false;
        String subString = currentLine.substring(0, i0);
        if (currentLine.substring(i0 + 1).contains(".")) {
            isNested = true;
        }

        // Find word
        String[] split = subString.split("[\t =]");
        String word = split[split.length - 1];

        // Get class for word
        Class firstClass = null;
        try {
            interpreter.eval(parseImportStatements(textArea));
            firstClass = (Class) interpreter.eval(word + ".class");
        } catch (EvalError e1) {
            firstClass = findClassFor(word, textArea);
            if (firstClass == null) {
                listModel.clear();
                listModel.addElement("No class information found.");
                setLocation(getCaretPoint(textArea));
                setVisible(true);
                return;
            }
        }

        filterString = null;

        Class lastClass = null;
        String phrase = word + currentLine.substring(i0);
        if (isNested) {
            if (phrase.endsWith(".")) {
                phrase += " ";
            }
            String[] methodSplit = phrase.split("[.]");
            lastClass = firstClass;
            for (int i = 1; i < methodSplit.length - 1; i++) {
                lastClass = findMatchingSignature(lastClass, methodSplit[i]);
            }
            filterString = phrase.endsWith(".") ? null : methodSplit[methodSplit.length - 1];
        } else {
            lastClass = firstClass;
            filterString = phrase.substring(phrase.indexOf(".") + 1);
        }
        parse(lastClass);
        refreshList(filterString);
        setLocation(getCaretPoint(textArea));
        setVisible(true);
    }

    private void parseBuildPath(String currentLine) {
        classTreeSet.clear();
        listModel.clear();

        String[] split = currentLine.split("[ \t]");
        String word = split[split.length - 1];
        if (word.length() < 2) {
            listModel.addElement("Too many matches. Try typing at least two characters.");
            list.setSelectedIndex(0);
            return;
        }

        URL[] url0 = ((URLClassLoader) ClassLoader.getSystemClassLoader()).getURLs();
        URL[] urls = new URL[url0.length + 1];

        try {
            urls[0] = new File(System.getProperty("java.home") + File.separator + "lib", "rt.jar").toURI().toURL();
        } catch (MalformedURLException e1) {
            e1.printStackTrace();
        }

        for (int i = 0; i < url0.length; i++) {
            urls[i + 1] = url0[i];
        }

        for (URL u : urls) {
            try {
                File file = new File(u.toURI());
                currentURL = file.getPath();

                filterString = word;
                if (file.isFile() && file.getName().endsWith(".jar")) {
                    traverseJarFile(file, word, listModel);
                } else if (file.isDirectory()) {
                    traverseDirectory(file, word, listModel);
                }
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }
        }

        if (classTreeSet.isEmpty()) {
            listModel.addElement("No class information found. Try to find a class.");
        } else {
            for (ClassItem ci : classTreeSet) {
                listModel.addElement(ci);
            }
        }
        list.setSelectedIndex(0);
    }

    private void traverseDirectory(File file, String word, DefaultListModel listModel) {
        File[] files = file.listFiles(new FileFilter() {

            public boolean accept(File pathname) {
                return pathname.isDirectory() || pathname.getName().endsWith(".class");
            }
        });

        for (File f : files) {
            if (f.isDirectory()) {
                traverseDirectory(f, word, listModel);
            }

            String filename = f.getName();
            if (!filename.startsWith(word)) {
                continue;
            }

            String className = filename.substring(0, filename.indexOf(".class"));
            if (className.contains("$")) {
                continue;
            }

            String path = f.getPath();
            String packageName = path.replace(currentURL + File.separator, "").replace(File.separator, ".");
            packageName = packageName.substring(0, packageName.lastIndexOf("."));
            classTreeSet.add(new ClassItem(className + " - " + packageName));
        //listModel.addElement(new ClassItem(className + " - " + packageName));
        }
    }

    private void traverseJarFile(File file, String word, DefaultListModel listModel) throws IOException {
        JarFile jar = new JarFile(file);
        Enumeration<JarEntry> je = jar.entries();
        JarEntry thisJarEntry;
        while (je.hasMoreElements()) {
            thisJarEntry = je.nextElement();
            if (thisJarEntry.isDirectory() || !thisJarEntry.getName().endsWith(".class")) {
                continue;
            }

            String thisEntryString = thisJarEntry.getName().replace("/", ".");

            String[] splitPackage = thisEntryString.split("[.]");

            String className = splitPackage[splitPackage.length - 2];

            if (!className.startsWith(word)) {
                continue;
            }
            if (className.contains("$")) {
                continue;
            }

            String label = className + " - " + thisEntryString.substring(0, thisEntryString.lastIndexOf(".class"));
            classTreeSet.add(new ClassItem(label));
        //listModel.addElement(new ClassItem(label));
        }
    }

    private Class findMatchingSignature(Class cls, String methodString) {
        int paranIndex = methodString.indexOf("(");
        String thisMethodName;
        String methodName = methodString.substring(0, paranIndex == -1 ? methodString.length() - 1 : paranIndex);
        for (Method m : cls.getMethods()) {
            thisMethodName = m.getName();
            if (thisMethodName.equalsIgnoreCase(methodName)) {
                if (m.isBridge()) {
                    continue;
                }
                return m.getReturnType();
            }
        }
        return null;
    }

    private Class findClassFor(String var, JEditTextArea textArea) {
        String[] split = textArea.getText().split(var + "[\t =;]", 2);

        String className = split[0].substring(split[0].lastIndexOf(";") + 1).trim();
        Class cls;
        try {
            cls = (Class) interpreter.eval(className + ".class");
        } catch (EvalError e) {
            cls = null;
        }
        return cls;
    }

    private String parseImportStatements(JEditTextArea textArea) {
        int lineOffsetForLastImport = textArea.getLineOfOffset(textArea.getText().lastIndexOf("import "));
        if (lineOffsetForLastImport < 0) {
            return "";
        }
        String importString = textArea.getText(0, textArea.getLineEndOffset(lineOffsetForLastImport));
        return importString;
    }

    private String readCurrentLine(JEditTextArea textArea) {
        int currentLine = textArea.getLineOfOffset(textArea.getCaretPosition());
        int lineStart = textArea.getLineStartOffset(currentLine);
        int lineEnd = textArea.getCaretPosition();
        return textArea.getText(lineStart, lineEnd - lineStart);
    }

    /**
     *
     * @param s
     */
    private void parse(Class cls) {
        fieldsTreeSet.clear();
        methodsTreeSet.clear();
        listModel.clear();
        if (cls == null) {
            listModel.addElement("Nothing class found.");
            return;
        }

        for (Field f : cls.getFields()) {
            f.getModifiers();
            fieldsTreeSet.add(f);
        }

        // Add methods.
        methodsTreeSet.addAll(addMethodNames(cls));
    }

    private String methodSignature(Method m) {
        StringBuffer sb = new StringBuffer();
        sb.append(m.getName() + "(");
        Class[] params = m.getParameterTypes(); // avoid clone
        for (int j = 0; j < params.length; j++) {
            sb.append(getTypeName(params[j]));
            if (j < (params.length - 1)) {
                sb.append(",");
            }
        }
        sb.append(")");
        return sb.toString();
    }

    /**
     *
     * @param filter
     */
    public void refreshList(String filter) {

        if (filter == null || filter.trim().equals("") || filter.trim().equals(".")) {
            filter = null;
        }
        listModel.clear();
        if (filter != null) {
            filter = filter.toLowerCase();
        }
        for (Field f : fieldsTreeSet) {
            if (filter == null || f.getName().toLowerCase().startsWith(filter)) {
                listModel.addElement(f);
            }
        }

        for (Method m : methodsTreeSet) {
            if (filter == null || m.getName().toLowerCase().startsWith(filter)) {
                listModel.addElement(m);
            }
        }

        if (listModel.size() == 0) {
            listModel.addElement("No class information found.");
        }
        list.setSelectedIndex(0);
    }

    private Collection<Method> addMethodNames(Class cls) {
        LinkedHashMap<String, Method> methodMap = new LinkedHashMap<String, Method>();
        Method[] ms = cls.getMethods();
        Method oldMethod;
        String thisMethod;
        for (Method m : ms) {
            thisMethod = methodSignature(m);
            oldMethod = methodMap.get(thisMethod);
            if (oldMethod == null) {
                methodMap.put(thisMethod, m);
            } else {
                if (oldMethod.getReturnType().isAssignableFrom(m.getReturnType())) {
                    methodMap.remove(oldMethod);
                    methodMap.put(thisMethod, m);
                }
            }
        }
        return methodMap.values();
    }

    /**
     *
     * @author  <a href="mailto://rstjohn@mza.com">Dr. Richard St. John</a>
     * @company <a href="http://www.mza.com">MZA Associates Corporation</a>
     */
    private class MethodComparator implements Comparator<Method> {

        public int compare(Method o1, Method o2) {
            int sort = o1.getName().toLowerCase().compareTo(o2.getName().toLowerCase());
            if (sort != 0) {
                return sort;
            }

            String paramString1 = o1.getReturnType().getSimpleName();
            for (Class c : o1.getParameterTypes()) {
                paramString1 += c.getSimpleName() + ",";
            }

            String paramString2 = o2.getReturnType().getSimpleName();
            for (Class c : o2.getParameterTypes()) {
                paramString2 += c.getSimpleName() + ",";
            }
            return paramString1.compareTo(paramString2);
        }
    }

    /**
     *
     * @author  <a href="mailto://rstjohn@mza.com">Dr. Richard St. John</a>
     * @company <a href="http://www.mza.com">MZA Associates Corporation</a>
     */
    private class ClassComparator implements Comparator<ClassItem> {

        public int compare(ClassItem o1, ClassItem o2) {
            int result = o1.getClassName().compareToIgnoreCase(o2.getClassName());
            if(result==0) {
                result = o1.getPackageName().compareToIgnoreCase(o2.getPackageName());
            }
            return result;
        }
    }

    /**
     *
     * @author  <a href="mailto://rstjohn@mza.com">Dr. Richard St. John</a>
     * @company <a href="http://www.mza.com">MZA Associates Corporation</a>
     */
    private class FieldComparator implements Comparator<Field> {

        public int compare(Field o1, Field o2) {
            int sort = o1.getName().toLowerCase().compareTo(o2.getName().toLowerCase());
            if (sort == 0) {
                return o1.toString().toLowerCase().compareTo(o2.toString().toLowerCase());
            } else {
                return sort;
            }
        }
    }

    /**
     *
     * @author  <a href="mailto://rstjohn@mza.com">Dr. Richard St. John</a>
     * @company <a href="http://www.mza.com">MZA Associates Corporation</a>
     */
    private class ListListener extends MouseAdapter implements KeyListener {

        private JEditTextArea textArea;
        private int selectedIndex;

        public void setTextArea(JEditTextArea textArea) {
            this.textArea = textArea;
        }

        private void actionSelected() {
            int caretLoc = textArea.getCaretPosition();
            int filterLen = 0;
            if (filterString != null && !filterString.equals(" ")) {
                filterLen = filterString.length();
            }

            textArea.select(caretLoc - filterLen, caretLoc);
            textArea.setSelectedText(getString(list.getSelectedValue()));
            setVisible(false);
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            if (e.getClickCount() != 2) {
                return;
            }
            actionSelected();
        }

        private String getString(Object value) {
            if (value instanceof Method) {
                Method m = (Method) value;
                String s = m.getName() + "(";

                Class pvec[] = m.getParameterTypes();
                for (int j = 0; j < pvec.length; j++) {
                    s += pvec[j].getSimpleName() + ", ";
                }

                if (s.endsWith(", ")) {
                    s = s.substring(0, s.length() - 2);
                }

                s += ")";
                return s;
            } else if (value instanceof Field) {
                return ((Field) value).getName();
            } else if (value instanceof ClassItem) {
                ClassItem cls = (ClassItem) value;
                int start = textArea.getSelectionStart();
                int end = textArea.getSelectionEnd();
                String importString = "import " + cls.getPackageName() + "." + cls.getClassName() + ";\n";
                textArea.select(0, 0);
                textArea.setSelectedText(importString);
                textArea.select(start + importString.length(), end + importString.length());
                return cls.getClassName();
            }
            return "";
        }

        public void keyTyped(KeyEvent e) {
            char c = e.getKeyChar();
            switch (c) {
                case 'a':
                case 'b':
                case 'c':
                case 'd':
                case 'e':
                case 'f':
                case 'g':
                case 'h':
                case 'i':
                case 'j':
                case 'k':
                case 'l':
                case 'm':
                case 'n':
                case 'o':
                case 'p':
                case 'q':
                case 'r':
                case 's':
                case 't':
                case 'u':
                case 'v':
                case 'w':
                case 'x':
                case 'y':
                case 'z':
                case 'A':
                case 'B':
                case 'C':
                case 'D':
                case 'E':
                case 'F':
                case 'G':
                case 'H':
                case 'I':
                case 'J':
                case 'K':
                case 'L':
                case 'M':
                case 'N':
                case 'O':
                case 'P':
                case 'Q':
                case 'R':
                case 'S':
                case 'T':
                case 'U':
                case 'V':
                case 'W':
                case 'X':
                case 'Y':
                case 'Z':
                case '1':
                case '2':
                case '3':
                case '4':
                case '5':
                case '6':
                case '7':
                case '8':
                case '9':
                case '0':
                case '(':
                    alphaNumericTyped(c);
                    break;
                case '+':
                case '-':
                case '*':
                case '/':
                case '.':
                    passthrough(c, true);
                    break;
                case ' ':
                    if (!e.isControlDown()) {
                        passthrough(c, true);
                    } else {
                        if (selectedIndex != -1) {
                            list.setSelectedIndex(selectedIndex);
                        }
                    }
                    break;
                default:

            }
        }

        public void keyPressed(KeyEvent e) {
            if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_SPACE) {
                selectedIndex = list.getSelectedIndex();
            } else {
                selectedIndex = -1;
            }
        }

        public void keyReleased(KeyEvent e) {
            int code = e.getKeyCode();
            if (code == KeyEvent.VK_ESCAPE) {
                setVisible(false);
            } else if (code == KeyEvent.VK_ENTER) {
                actionSelected();
            } else if (code == KeyEvent.VK_BACK_SPACE) {
                backspaceTyped();
            }
        }

        private void passthrough(char c, boolean closeDialog) {
            SyntaxDocument doc = textArea.getDocument();
            int loc = textArea.getCaretPosition();
            try {
                doc.insertString(loc, String.valueOf(c), null);
            } catch (BadLocationException ex) {
            }
            if (closeDialog) {
                setVisible(false);
            }
        }

        private void backspaceTyped() {
            SyntaxDocument doc = textArea.getDocument();
            int loc = textArea.getCaretPosition();
            try {
                String s = textArea.getText(loc - 2, 1);
                doc.replace(loc - 1, 1, "", null);
                if (s==null || s.equals(" ") || s.equals("\n")) {
                    setVisible(false);
                    return;
                }
                start(textArea);
            } catch (BadLocationException ex) {}
        }

        private void alphaNumericTyped(char c) {
            SyntaxDocument doc = textArea.getDocument();
            int loc = textArea.getCaretPosition();
            try {
                doc.insertString(loc, String.valueOf(c), null);
                start(textArea);
            } catch (BadLocationException ex) {}
        }
    }

    /**
     *
     * @author  <a href="mailto://rstjohn@mza.com">Dr. Richard St. John</a>
     * @company <a href="http://www.mza.com">MZA Associates Corporation</a>
     */
    private class ClassItem {

        private String s;

        public ClassItem(String s) {
            this.s = s;
        }

        @Override
        public String toString() {
            return s;
        }

        public ImageIcon getIcon() {
            ImageIcon icon = Resources.createIcon("class_public.png");
            //try {
            boolean isAbstract = false;
            String command = s.split("[-]")[1].trim() + ".class;";
            Class cls = null;
            try {
                cls = (Class) interpreter.eval(command);
            } catch (Exception ex) {
                return icon;
            } catch (NoClassDefFoundError eee) {
                return icon;
            }
            int mod = cls.getModifiers();

            String iconName = "";
            if (cls.isInterface()) {
                iconName += "interface";
            } else {
                iconName += "class";
                isAbstract = Modifier.isAbstract(mod);
            }

            if (Modifier.isPrivate(mod)) {
                iconName += "_private";
            } else if (Modifier.isProtected(mod)) {
                iconName += "_protected";
            } else {
                iconName += "_public";
            }
            if (isAbstract) {
                iconName += "_abstract";
            }
            icon = Resources.createIcon(iconName + ".png");
            //} catch (EvalError e) {}

            return icon;
        }

        public String getClassName() {
            return s.split("[-]")[0].trim();
        }

        public String getPackageName() {
            String p = s.split("[-]")[1].trim();
            return p.substring(0, p.lastIndexOf("."));
        }
    }

    /**
     *
     * @author  <a href="mailto://rstjohn@mza.com">Dr. Richard St. John</a>
     * @company <a href="http://www.mza.com">MZA Associates Corporation</a>
     */
    private class ReflectListRender extends DefaultListCellRenderer {

        private ImageIcon publicMethodIcon = Resources.createIcon("method_public.png");
        private ImageIcon publicAbstractMethodIcon = Resources.createIcon("method_public_abstract.png");
        private ImageIcon publicStaticMethodIcon = Resources.createIcon("method_public_static.png");

        @Override
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            label.setFont(label.getFont().deriveFont(11f));
            label.setBorder(new EmptyBorder(1, 4, 1, 0));

            if (value instanceof Field) {
                Field f = (Field) value;

                int mod = f.getModifiers();
                String iconFile = "field";
                if (Modifier.isPublic(mod)) {
                    iconFile += "_public";
                } else if (Modifier.isProtected(mod)) {
                    iconFile += "_protected";
                } else {
                    iconFile += "_private";
                }
                if (Modifier.isStatic(mod)) {
                    iconFile += "_static";
                }
                if (Modifier.isFinal(mod)) {
                    iconFile += "_final";
                }
                iconFile += ".png";

                label.setIcon(Resources.createIcon(iconFile));
                label.setText(f.getName() + "  " + f.getType().getSimpleName() + " - " + f.getDeclaringClass().getSimpleName());
            } else if (value instanceof Method) {
                Method m = (Method) value;
                String s = m.getName() + "(";

                Class pvec[] = m.getParameterTypes();
                for (int j = 0; j < pvec.length; j++) {
                    s += pvec[j].getSimpleName() + ", ";
                }

                if (s.endsWith(", ")) {
                    s = s.substring(0, s.length() - 2);
                }
                s += ")  " + m.getReturnType().getSimpleName() + " - " + m.getDeclaringClass().getSimpleName();

                int mod = m.getModifiers();
                String iconFile = "method";
                if (Modifier.isPublic(mod)) {
                    iconFile += "_public";
                } else if (Modifier.isProtected(mod)) {
                    iconFile += "_protected";
                } else {
                    iconFile += "_private";
                }
                if (Modifier.isStatic(mod)) {
                    iconFile += "_static";
                } else if (Modifier.isFinal(mod)) {
                    iconFile += "_abstract";
                }
                iconFile += ".png";

                ImageIcon icon;
                if (iconFile.equals("method_public.png")) {
                    icon = publicMethodIcon;
                } else if (iconFile.equals("method_public_abstract.png")) {
                    icon = publicAbstractMethodIcon;
                } else if (iconFile.equals("method_public_static.png")) {
                    icon = publicStaticMethodIcon;
                } else {
                    icon = Resources.createIcon(iconFile);
                }

                label.setIcon(icon);
                label.setText(s);
            } else if (value instanceof ClassItem) {
                ClassItem ci = (ClassItem) value;
                label.setIcon(ci.getIcon());
                label.setText(ci.toString());
            } else if (value instanceof String) {
                label.setIcon(null);
                label.setText(value.toString());
            }
            return label;
        }
    }

    static String getTypeName(Class type) {
        if (type.isArray()) {
            try {
                Class cl = type;
                int dimensions = 0;
                while (cl.isArray()) {
                    dimensions++;
                    cl = cl.getComponentType();
                }
                StringBuffer sb = new StringBuffer();
                sb.append(cl.getName());
                for (int i = 0; i < dimensions; i++) {
                    sb.append("[]");
                }
                return sb.toString();
            } catch (Throwable e) { /*FALLTHRU*/ }
        }
        return type.getName();
    }
}
