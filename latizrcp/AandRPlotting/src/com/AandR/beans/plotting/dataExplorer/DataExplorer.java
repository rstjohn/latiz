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
package com.AandR.beans.plotting.dataExplorer;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileInputStream;
import java.util.List;

import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;


import ncsa.hdf.object.Attribute;
import ncsa.hdf.object.Group;
import ncsa.hdf.object.h5.H5File;
import net.miginfocom.swing.MigLayout;

import com.AandR.beans.plotting.readers.AbstractDataReader;
import com.AandR.beans.plotting.readers.DataReaderException;
import com.AandR.beans.plotting.readers.DefaultHDF5Reader;
import com.AandR.beans.plotting.readers.HelcomesHDF5Reader;
import com.AandR.beans.plotting.scrollableDesktop.JDesktopPaneWithDropSupport;
import com.AandR.library.gui.DropEvent;
import com.AandR.library.gui.DropListener;
import com.AandR.library.gui.JListWithDropSupport;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.Exceptions;

/**
 * @author Aaron Masino
 * @version Sep 18, 2008 2:35:00 PM <br>
 *
 * Comments:
 *
 */
public class DataExplorer extends JPanel {

    public static final int VERTICAL = JSplitPane.VERTICAL_SPLIT;
    public static final int HORIZONTAL = JSplitPane.HORIZONTAL_SPLIT;
    private static final String ACTION_HUD = "SWITCH-TO-HUD";
    private static final String ACTION_PALETTE = "SWITCH-TO-PALETTE";
    private static final String PALETTE_ID = "Palette";
    private int orientation;
    private JDesktopPaneWithDropSupport plotPanel;
    private JListWithDropSupport fileList;
    private JPanel defaultBottomPanel;
    private JSplitPane fileListSplitter,  splitter,  plotSplitter;

    public DataExplorer() {
        initialize();
        setLayout(new MigLayout("", "", "0[]"));
        add(createContentPane(), "push, grow");
    }

    public void initialize() {
        orientation = VERTICAL;

        defaultBottomPanel = new JPanel(new MigLayout());
        defaultBottomPanel.add(new JLabel("No Files Selected"));

        DefaultListModel model = new DefaultListModel();
        model.addElement("<Drop Files Here>");
        fileList = new JListWithDropSupport(model);
        fileList.setCellRenderer(new ListRenderer());

        FileListListener fileListListener = new FileListListener();
        fileList.addDropListener(fileListListener);
        fileList.addMouseListener(fileListListener);
        fileList.addListSelectionListener(fileListListener);

        PlotPanelListener plotPanelListener = new PlotPanelListener();
        plotPanel = new JDesktopPaneWithDropSupport();
        plotPanel.addDropListener(plotPanelListener);
        plotPanel.setPreferredSize(new Dimension(500, 500));
        plotPanel.registerMenuBar();
    }

    private JComponent createContentPane() {
        fileListSplitter = new JSplitPane(orientation, true);
        fileListSplitter.setDividerSize(8);
        fileListSplitter.setTopComponent(createFileListPanel());
        fileListSplitter.setBottomComponent(defaultBottomPanel);
        int dividerLocation = orientation == HORIZONTAL ? 500 : 400;
        fileListSplitter.setDividerLocation(dividerLocation);
        fileListSplitter.setOneTouchExpandable(true);

        plotSplitter = new JSplitPane();
        plotSplitter.setDividerSize(8);
        plotSplitter.setLeftComponent(fileListSplitter);
        plotSplitter.setRightComponent(plotPanel);

        return plotSplitter;
    }

    private Component createFileListPanel() {
        JPanel panel = new JPanel(new MigLayout("insets 5", "", ""));
        panel.add(new JLabel("List of Available Files", JLabel.CENTER), ", pushx, growx, wrap");
        panel.add(new JScrollPane(fileList), "push, grow, w :150:");
        return panel;
    }

//  public Element saveProperties() {
//    Element e = new Element("window");
//    e.addContent(new Element("fileListSplitter").setAttribute("div", String.valueOf(fileListSplitter.getDividerLocation())));
//    e.addContent(new Element("fileTreeSplitter").setAttribute("div", String.valueOf(fileTreeSplitter.getDividerLocation())));
//    e.addContent(new Element("plotSplitter").setAttribute("div", String.valueOf(plotSplitter.getDividerLocation())));
//    e.addContent(new Element("splitter").setAttribute("div", String.valueOf(splitter.getDividerLocation())));
//    return e;
//  }
//  public void loadProperties(Element e) {
//    Element elem = e.getChild("fileListSplitter");
//    if(elem!=null) fileListSplitter.setDividerLocation(Integer.parseInt(elem.getAttributeValue("div")));
//
//    elem = e.getChild("fileTreeSplitter");
//    if(elem!=null) fileTreeSplitter.setDividerLocation(Integer.parseInt(elem.getAttributeValue("div")));
//
//    elem = e.getChild("plotSplitter");
//    if(elem!=null) plotSplitter.setDividerLocation(Integer.parseInt(elem.getAttributeValue("div")));
//
//    elem = e.getChild("splitter");
//    if(elem!=null) splitter.setDividerLocation(Integer.parseInt(elem.getAttributeValue("div")));
//  }
    /**
     *
     * @author Dr. Richard St. John
     * @version $Revision$, $Date$
     */
    private class FileListListener extends MouseAdapter implements ActionListener, DropListener, ListSelectionListener {

        private JPopupMenu popupForFileNodes;

        public FileListListener() {
            popupForFileNodes = new JPopupMenu();
            JMenuItem removeFileMenu = new JMenuItem("Remove this file");
            removeFileMenu.setActionCommand("ACTION_REMOVE_FILE");
            removeFileMenu.addActionListener(this);
            popupForFileNodes.add(removeFileMenu);
        }

        public void dropAction(DropEvent dropEvent) {
            File file = new File(((String[]) dropEvent.getDroppedItem())[0]);
            if (file.isDirectory()) {
                NotifyDescriptor nd = new NotifyDescriptor.Message("<HTML>The directory:<BR><BR><B>" + file.getPath() + "</B><BR><BR>is not a valid HDF5, TRF, or ACS-H5 file.</HTML>");
                DialogDisplayer.getDefault().notify(nd);
                return;
            }

            DefaultListModel model = (DefaultListModel) fileList.getModel();

            if (!model.isEmpty() && model.getElementAt(0).toString().equals("<Drop Files Here>")) {
                model.remove(0);
            }
            maybeLoadFile(file);
        }

        @Override
        public void mousePressed(MouseEvent e) {
            if (e.getButton() == 3) {
                popupForFileNodes.show(fileList, e.getX(), e.getY());
                fileList.setSelectedIndex(fileList.locationToIndex(e.getPoint()));
            }
            return;
        }

        private void maybeLoadFile(File file) {
            FileInputStream stream = null;
            String magicHeader = "";
            try {
                byte[] b = new byte[8];
                stream = new FileInputStream(file);
                stream.read(b);
                magicHeader = new String(b);
            } catch (Exception e1) {
                e1.printStackTrace();
            }

            AbstractDataReader dataReader = null;
            if (magicHeader.equalsIgnoreCase("#!MZATRF")) {
                System.out.println("Trf file dropped");
            } else if (magicHeader.substring(1).startsWith("HDF")) {
                dataReader = getHDF5DataReader(file.getPath());
            } else {
                NotifyDescriptor nd = new NotifyDescriptor.Message("<HTML>The dropped file:<BR><BR><B>" + file.getPath() + "</B><BR><BR>is not a valid HDF5, TRF, or ACS-H5 file.</HTML>");
                DialogDisplayer.getDefault().notify(nd);
                return;
            }

            dataReader.setDataExplorerInterface(new DataReaderListener());
            dataReader.setFile(file);

            try {
                dataReader.initialize(file);
            } catch (DataReaderException e) {
                Exceptions.attachMessage(e, "Data Explorer Error: Error initializing data reader.");
                return;
            }
            ((DefaultListModel) fileList.getModel()).addElement(dataReader);
            fileList.setSelectedValue(dataReader, true);
        }

        private AbstractDataReader getHDF5DataReader(String filename) {
            H5File h5File = new H5File(filename);
            try {
                h5File.open();
                Group g = (Group) h5File.get("/");
                int gid = g.open();
                List attList = g.getMetadata();
                if (attList.isEmpty()) {
                    g.close(gid);
                    h5File.close();
                    return new DefaultHDF5Reader();
                }

                Object attValue = ((Attribute) g.getMetadata().get(0)).getValue();
                g.close(gid);
                h5File.close();

                if (!(attValue instanceof String[])) {
                    return new DefaultHDF5Reader();
                }

                //TODO Search over all registered writers to find the appropriate registered reader.
                String id = ((String[]) attValue)[0];
                if (id.equals("HELCOMES HDF5 Writer")) {
                    return new HelcomesHDF5Reader();
                } else if (id.equals("Default HDF5 Writer (Latiz)")) {
                    return new DefaultHDF5Reader();
                } else {
                    return new DefaultHDF5Reader();
                }
            } catch (Exception e) {
                return new DefaultHDF5Reader();
            }
        }

        public void actionPerformed(ActionEvent e) {
            String command = e.getActionCommand();
            if (command.equals("ACTION_REMOVE_FILE")) {
                actionRemoveFile();
            }
        }

        private void actionRemoveFile() {
            int selectedIndex = fileList.getSelectedIndex();
            Object selectedItem = fileList.getSelectedValue();
            DefaultListModel model = (DefaultListModel) fileList.getModel();

            fileList.removeListSelectionListener(this);
            model.removeElement(selectedItem);
            if (model.isEmpty() || !(selectedItem instanceof AbstractDataReader)) {
                model.addElement("<Drop Files Here>");
                fileList.addListSelectionListener(this);
                fileListSplitter.setBottomComponent(defaultBottomPanel);
                return;
            }
            fileListSplitter.setBottomComponent(((AbstractDataReader) selectedItem).getParameterPanel());
            fileList.addListSelectionListener(this);
            selectedIndex--;
            fileList.setSelectedIndex(selectedIndex < 0 ? 0 : selectedIndex);
        }

        public void valueChanged(ListSelectionEvent e) {
            if (((DefaultListModel) fileList.getModel()).isEmpty()) {
                return;
            }

            Object value = fileList.getSelectedValue();
            if (value == null || !(value instanceof AbstractDataReader)) {
                return;
            }

            fileListSplitter.setBottomComponent(((AbstractDataReader) fileList.getSelectedValue()).getParameterPanel());
        }
    }

    /**
     *
     * @author Aaron Masino
     * @version Sep 18, 2008 3:09:35 PM <br>
     *
     * Comments:
     *
     */
    private class PlotPanelListener extends MouseAdapter implements DropListener {

        public void dropAction(DropEvent dropEvent) {
            AbstractDataReader reader = (AbstractDataReader) fileList.getSelectedValue();
            try {
                reader.acknowledgePlotRequested(dropEvent);
            } catch (DataReaderException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     *
     * @author  <a href="mailto://rstjohn@mza.com">Dr. Richard St. John</a>
     * @company <a href="http://www.mza.com">MZA Associates Corporation</a>
     */
    private class DataReaderListener implements DataExplorerInterface {

        public void drawPlotFrame(String title, JPanel panel) {
            plotPanel.add(title, panel);
        }
    }

    /**
     *
     * @author  <a href="mailto://rstjohn@mza.com">Dr. Richard St. John</a>
     * @company <a href="http://www.mza.com">MZA Associates Corporation</a>
     */
    private class ListRenderer extends DefaultListCellRenderer {

        @Override
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean selected, boolean hasFocus) {
            super.getListCellRendererComponent(list, value, index, selected, hasFocus);
            setBorder(new EmptyBorder(2, 5, 0, 0));
            if (value instanceof AbstractDataReader) {
                setIcon(((AbstractDataReader) value).getIcon());
                setToolTipText(((AbstractDataReader) value).getFile().getPath());
            } else {
                setToolTipText("Drop Files Here");
            }
            return this;
        }
    }
}
