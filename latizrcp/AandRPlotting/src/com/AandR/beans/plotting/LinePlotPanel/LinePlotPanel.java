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
package com.AandR.beans.plotting.LinePlotPanel;

import com.AandR.beans.plotting.imagePlotPanel.DataTablePanel;
import com.AandR.library.gui.DropEvent;
import com.AandR.library.gui.DropListener;
import com.AandR.library.gui.TransferableTreeNode;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.RenderingHints;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;

import java.util.TreeMap;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JSplitPane;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.LogarithmicAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.axis.StandardTickUnitSource;
import org.jfree.chart.axis.TickUnits;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.LegendTitle;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.RectangleEdge;

/**
 * @author Dr. Richard St. John
 * @version $Revision$, $Date$
 */
public class LinePlotPanel extends JPanel {

    public static final int SCATTER_PLOT = 0;
    public static final int LINE_PLOT = 1;
    private static final String ACTION_HIDE_LINES = "ACTION_HIDE_LINES";
    private static final String ACTION_SHOW_SYMBOLS = "ACTION_SHOW_SYMBOLS";
    private static final String ACTION_LOG_LINEAR = "ACTION_LOG_LINEAR";
    private static final String ACTION_LOG_LOG = "ACTION_LOG_LOG";
    private static final String ACTION_LINEAR_LOG = "ACTION_LINEAR_LOG";
    private static final String ACTION_LINEAR_LINEAR = "ACTION_LINEAR_LINEAR";
    private static final String ACTION_EXPORT_PNG = "ACTION_EXPORT_PNG";
    private boolean isSymbolVisible,  isLineVisible;
    private ArrayList<DropListener> dropListeners;
    private ChartPanel chartPanel;
    private DataTablePanel dataPanel;
    private TreeMap<String, XYSeries> plotsMap;
    private JSplitPane centerSplitter;
    private ScatterPanelListener plotPanelListener;
    private TickUnits defaultTickUnits;
    private XYSeriesCollection plotSeries;

    public LinePlotPanel(int type) {
        if (type == SCATTER_PLOT) {
            isSymbolVisible = true;
            isLineVisible = false;
        } else {
            isSymbolVisible = false;
            isLineVisible = true;
        }
        initialize();
        new DropTarget(this, plotPanelListener);
        setLayout(new BorderLayout());
        add(centerSplitter, BorderLayout.CENTER);
    }

    public LinePlotPanel() {
        this(LINE_PLOT);
    }

    private void initialize() {
        plotsMap = new TreeMap<String, XYSeries>();
        plotSeries = new XYSeriesCollection();
        dropListeners = new ArrayList<DropListener>();

        plotPanelListener = new ScatterPanelListener();

        centerSplitter = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        centerSplitter.setTopComponent(createDataPanel());
        centerSplitter.setBottomComponent(createChartPanel());
        centerSplitter.setDividerLocation(0);
    }

    public void setPlotTitle(String title) {
        chartPanel.getChart().setTitle(title);
    }

    public String getPlotTitle() {
        return chartPanel.getChart().getTitle().getText();
    }

    public void setChartType(int type) {
        if (type == SCATTER_PLOT) {
            isSymbolVisible = true;
            isLineVisible = false;
        } else {
            isSymbolVisible = false;
            isLineVisible = true;
        }
        XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) chartPanel.getChart().getXYPlot().getRenderer();
        renderer.setBaseLinesVisible(isLineVisible);
        renderer.setBaseShapesVisible(isSymbolVisible);
    }

    public void setChartType(XYDataset dataset, int type) {
        if (type == SCATTER_PLOT) {
            isSymbolVisible = true;
            isLineVisible = false;
        } else {
            isSymbolVisible = false;
            isLineVisible = true;
        }
        XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) chartPanel.getChart().getXYPlot().getRendererForDataset(dataset);
        renderer.setBaseLinesVisible(isLineVisible);
        renderer.setBaseShapesVisible(isSymbolVisible);
    }

    public TreeMap<String, XYSeries> getPlotsMap() {
        return plotsMap;
    }

    public void clearChart() {
        plotSeries.removeAllSeries();
    }

    public void setData(String label, double[] x, double[] y) {
        setData("", new String[]{label}, x, new double[][]{y});
    }

    public void setData(String label, double[] y) {
        setData(new String[]{label}, new double[][]{y});
    }

    public void setData(String[] labels, double[][] y) {
        double[][] x = new double[y.length][];
        for (int i = 0; i < y.length; i++) {
            x[i] = new double[y[i].length];
            for (int j = 0; j < x[i].length; j++) {
                x[i][j] = j + 1;
            }
        }
        setData("x", labels, x, y);
    }

    public void setData(String xLabel, String[] yLabels, double[] x, double[][] y) {
        plotSeries.removeAllSeries();
        plotsMap.clear();
        XYSeries thisPlotSeries;
        for (int j = 0; j < y.length; j++) {
            thisPlotSeries = new XYSeries(yLabels[j]);
            for (int i = 0; i < y[0].length; i++) {
                thisPlotSeries.add(x[i], y[j][i]);
            }
            plotSeries.addSeries(thisPlotSeries);
            plotsMap.put(yLabels[j], thisPlotSeries);
        }
        chartPanel.getChart().getXYPlot().getDomainAxis().setLabel(xLabel);
        dataPanel.resetData(xLabel, yLabels, x, y);
    }

    public void setData(String xLabel, String[] legendLabels, double[][] x, double[][] y) {
        setData(xLabel, "y", legendLabels, x, y);
    }

    public void setData(String xLabel, String yLabel, String[] legendLabels, double[][] x, double[][] y) {
        plotSeries.removeAllSeries();
        plotsMap.clear();
        XYSeries thisPlotSeries;
        for (int j = 0; j < y.length; j++) {
            thisPlotSeries = new XYSeries(legendLabels[j]);
            for (int i = 0; i < y[j].length; i++) {
                thisPlotSeries.add(x[j][i], y[j][i]);
            }
            plotSeries.addSeries(thisPlotSeries);
            plotsMap.put(legendLabels[j], thisPlotSeries);
            setChartType(plotSeries, y[j].length == 1 ? SCATTER_PLOT : LINE_PLOT);
        }
        chartPanel.getChart().getXYPlot().getDomainAxis().setLabel(xLabel);
        chartPanel.getChart().getXYPlot().getRangeAxis().setLabel(yLabel);
        dataPanel.resetData(xLabel, legendLabels, x, y);
    }

    public void addData(String[] yLabels, double[] x, double[][] y) {
        XYSeries thisPlotSeries;
        for (int j = 0; j < y.length; j++) {
            thisPlotSeries = new XYSeries(yLabels[j]);
            for (int i = 0; i < y[0].length; i++) {
                thisPlotSeries.add(x[i], y[j][i]);
            }
            plotSeries.addSeries(thisPlotSeries);
            plotsMap.put(yLabels[j], thisPlotSeries);
        }
        dataPanel.addData("x", x, yLabels, y);
    }

    public void removeSeries(Comparable key) {
        XYSeries series = plotSeries.getSeries(key);
        plotSeries.removeSeries(series);
        plotsMap.remove(series.getKey().toString());
    }

    public void addDropListener(DropListener dropListener) {
        dropListeners.add(dropListener);
    }

    private void notifyDropListeners(DropEvent dropEvent) {
        for (DropListener d : dropListeners) {
            d.dropAction(dropEvent);
        }
    }

    public ChartPanel getChartPanel() {
        return chartPanel;
    }

    public void setPlotVisible(String label, boolean isVisible) {
        if(isVisible) {
            plotSeries.addSeries(plotsMap.get(label));
        } else {
            plotSeries.removeSeries(plotsMap.get(label));
        }
    }

    private JMenu createLinePropMenu() {
        JMenuItem symMenu = new JMenuItem("Show/Hide Symbols");
        symMenu.setActionCommand(ACTION_SHOW_SYMBOLS);
        symMenu.addActionListener(plotPanelListener);

        JMenuItem lineMenu = new JMenuItem("Show/Hide Lines");
        lineMenu.setActionCommand(ACTION_HIDE_LINES);
        lineMenu.addActionListener(plotPanelListener);


        JMenu menu = new JMenu("Line Properties");
        menu.add(symMenu);
        menu.add(lineMenu);
        return menu;
    }

    private JMenu createAxesPropMenu() {
        JMenu menu = new JMenu("Axes Scale");
        menu.add(createMenuItem("Log-Linear", ACTION_LOG_LINEAR));
        menu.add(createMenuItem("Log-Log", ACTION_LOG_LOG));
        menu.add(createMenuItem("Linear-Log", ACTION_LINEAR_LOG));
        menu.add(createMenuItem("Linear-Linear", ACTION_LINEAR_LINEAR));
        return menu;
    }

    private JMenu createExportMenu() {
        JMenu menu = new JMenu("Export");
        menu.add(createMenuItem("PNG", ACTION_EXPORT_PNG));
        return menu;
    }

    private JMenuItem createMenuItem(String label, String actionCommand) {
        JMenuItem menuItem = new JMenuItem(label);
        menuItem.addActionListener(plotPanelListener);
        menuItem.setActionCommand(actionCommand);
        return menuItem;
    }

    private ChartPanel createChartPanel() {
        JFreeChart xyChart = ChartFactory.createXYLineChart("f", "x", "y", plotSeries, PlotOrientation.VERTICAL, false, true, false);
        RenderingHints hints = xyChart.getRenderingHints();
        hints.put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        hints.put(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        xyChart.setBackgroundPaint(Color.WHITE);

        XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) xyChart.getXYPlot().getRenderer();
        renderer.setBaseLinesVisible(isLineVisible);
        renderer.setBaseShapesVisible(isSymbolVisible);

        LegendTitle legend = new LegendTitle(renderer);
        legend.setPosition(RectangleEdge.BOTTOM);
        xyChart.addLegend(legend);

        xyChart.getXYPlot().getRangeAxis().setStandardTickUnits(createTickUnits());
        //xyChart.getXYPlot().getRangeAxis().setStandardTickUnits(new StandardTickUnitSource());
        xyChart.getXYPlot().getRangeAxis().setAutoRangeMinimumSize(1.0e-45);

        chartPanel = new ChartPanel(xyChart);

        JPopupMenu popup = chartPanel.getPopupMenu();
        popup.remove(1);  // removes separator
        popup.remove(1);  // removes save as...
        popup.add(createLinePropMenu());
        popup.add(createAxesPropMenu());
        popup.addSeparator();
        popup.add(createExportMenu());
        return chartPanel;
    }

    private DataTablePanel createDataPanel() {
        dataPanel = new DataTablePanel();
        dataPanel.setMinimumSize(new Dimension(0, 0));
        return dataPanel;
    }

    private TickUnits createTickUnits() {
        defaultTickUnits = new TickUnits();
        DecimalFormat df0 = new DecimalFormat("0.00E00");
        DecimalFormat df2 = new DecimalFormat("0.000000");
        DecimalFormat df3 = new DecimalFormat("0.00000");
        DecimalFormat df4 = new DecimalFormat("0.0000");
        DecimalFormat df5 = new DecimalFormat("0.000");
        DecimalFormat df6 = new DecimalFormat("0.00");
        DecimalFormat df7 = new DecimalFormat("0.0");
        DecimalFormat df8 = new DecimalFormat("#,##0");
        DecimalFormat df9 = new DecimalFormat("0.00E00");

        // we can add the units in any order, the TickUnits collection will sort them...
        defaultTickUnits.add(new NumberTickUnit(1.0e-45, df0));
        defaultTickUnits.add(new NumberTickUnit(1.0e-44, df0));
        defaultTickUnits.add(new NumberTickUnit(1.0e-43, df0));
        defaultTickUnits.add(new NumberTickUnit(1.0e-42, df0));
        defaultTickUnits.add(new NumberTickUnit(1.0e-41, df0));
        defaultTickUnits.add(new NumberTickUnit(1.0e-40, df0));
        defaultTickUnits.add(new NumberTickUnit(1.0e-39, df0));
        defaultTickUnits.add(new NumberTickUnit(1.0e-38, df0));
        defaultTickUnits.add(new NumberTickUnit(1.0e-37, df0));
        defaultTickUnits.add(new NumberTickUnit(1.0e-36, df0));
        defaultTickUnits.add(new NumberTickUnit(1.0e-35, df0));
        defaultTickUnits.add(new NumberTickUnit(1.0e-34, df0));
        defaultTickUnits.add(new NumberTickUnit(1.0e-33, df0));
        defaultTickUnits.add(new NumberTickUnit(1.0e-32, df0));
        defaultTickUnits.add(new NumberTickUnit(1.0e-31, df0));
        defaultTickUnits.add(new NumberTickUnit(1.0e-30, df0));
        defaultTickUnits.add(new NumberTickUnit(1.0e-29, df0));
        defaultTickUnits.add(new NumberTickUnit(1.0e-28, df0));
        defaultTickUnits.add(new NumberTickUnit(1.0e-27, df0));
        defaultTickUnits.add(new NumberTickUnit(1.0e-26, df0));
        defaultTickUnits.add(new NumberTickUnit(1.0e-25, df0));
        defaultTickUnits.add(new NumberTickUnit(1.0e-24, df0));
        defaultTickUnits.add(new NumberTickUnit(1.0e-23, df0));
        defaultTickUnits.add(new NumberTickUnit(1.0e-22, df0));
        defaultTickUnits.add(new NumberTickUnit(1.0e-21, df0));
        defaultTickUnits.add(new NumberTickUnit(1.0e-20, df0));
        defaultTickUnits.add(new NumberTickUnit(1.0e-19, df0));
        defaultTickUnits.add(new NumberTickUnit(1.0e-18, df0));
        defaultTickUnits.add(new NumberTickUnit(1.0e-17, df0));
        defaultTickUnits.add(new NumberTickUnit(1.0e-16, df0));
        defaultTickUnits.add(new NumberTickUnit(1.0e-15, df0));
        defaultTickUnits.add(new NumberTickUnit(1.0e-14, df0));
        defaultTickUnits.add(new NumberTickUnit(1.0e-13, df0));
        defaultTickUnits.add(new NumberTickUnit(1.0e-12, df0));
        defaultTickUnits.add(new NumberTickUnit(1.0e-11, df0));
        defaultTickUnits.add(new NumberTickUnit(1.0e-10, df0));
        defaultTickUnits.add(new NumberTickUnit(1.0e-9, df0));
        defaultTickUnits.add(new NumberTickUnit(1.0e-8, df0));
        defaultTickUnits.add(new NumberTickUnit(1.0e-7, df0));
        defaultTickUnits.add(new NumberTickUnit(1.0e-6, df2));
        defaultTickUnits.add(new NumberTickUnit(1.0e-5, df3));
        defaultTickUnits.add(new NumberTickUnit(1.0e-4, df4));
        defaultTickUnits.add(new NumberTickUnit(0.001, df5));
        defaultTickUnits.add(new NumberTickUnit(0.01, df6));
        defaultTickUnits.add(new NumberTickUnit(0.1, df7));
        defaultTickUnits.add(new NumberTickUnit(1, df8));
        defaultTickUnits.add(new NumberTickUnit(10, df8));
        defaultTickUnits.add(new NumberTickUnit(100, df8));
        defaultTickUnits.add(new NumberTickUnit(1000, df8));
        defaultTickUnits.add(new NumberTickUnit(1e5, df9));
        defaultTickUnits.add(new NumberTickUnit(1e6, df9));
        defaultTickUnits.add(new NumberTickUnit(1e7, df9));
        defaultTickUnits.add(new NumberTickUnit(1e8, df9));
        defaultTickUnits.add(new NumberTickUnit(1e9, df9));
        defaultTickUnits.add(new NumberTickUnit(1e10, df9));
        defaultTickUnits.add(new NumberTickUnit(1e11, df9));
        defaultTickUnits.add(new NumberTickUnit(1e12, df9));
        defaultTickUnits.add(new NumberTickUnit(1e13, df9));
        defaultTickUnits.add(new NumberTickUnit(1e14, df9));
        defaultTickUnits.add(new NumberTickUnit(1e15, df9));
        defaultTickUnits.add(new NumberTickUnit(1e16, df9));
        defaultTickUnits.add(new NumberTickUnit(1e17, df9));
        defaultTickUnits.add(new NumberTickUnit(1e18, df9));
        defaultTickUnits.add(new NumberTickUnit(1e19, df9));
        defaultTickUnits.add(new NumberTickUnit(1e20, df9));
        defaultTickUnits.add(new NumberTickUnit(1e21, df9));
        defaultTickUnits.add(new NumberTickUnit(1e22, df9));
        defaultTickUnits.add(new NumberTickUnit(1e23, df9));
        defaultTickUnits.add(new NumberTickUnit(1e24, df9));
        defaultTickUnits.add(new NumberTickUnit(1e25, df9));
        defaultTickUnits.add(new NumberTickUnit(1e26, df9));
        defaultTickUnits.add(new NumberTickUnit(1e27, df9));
        defaultTickUnits.add(new NumberTickUnit(1e28, df9));
        defaultTickUnits.add(new NumberTickUnit(1e29, df9));
        defaultTickUnits.add(new NumberTickUnit(1e30, df9));
        defaultTickUnits.add(new NumberTickUnit(1e31, df9));
        defaultTickUnits.add(new NumberTickUnit(1e32, df9));
        defaultTickUnits.add(new NumberTickUnit(1e33, df9));
        defaultTickUnits.add(new NumberTickUnit(1e34, df9));
        defaultTickUnits.add(new NumberTickUnit(1e35, df9));
        defaultTickUnits.add(new NumberTickUnit(1e36, df9));
        defaultTickUnits.add(new NumberTickUnit(1e37, df9));
        defaultTickUnits.add(new NumberTickUnit(1e38, df9));
        defaultTickUnits.add(new NumberTickUnit(1e39, df9));
        defaultTickUnits.add(new NumberTickUnit(1e40, df9));
        defaultTickUnits.add(new NumberTickUnit(1e41, df9));
        defaultTickUnits.add(new NumberTickUnit(1e42, df9));
        defaultTickUnits.add(new NumberTickUnit(1e43, df9));
        defaultTickUnits.add(new NumberTickUnit(1e44, df9));
        defaultTickUnits.add(new NumberTickUnit(1e45, df9));

        defaultTickUnits.add(new NumberTickUnit(2.5e-45, df0));
        defaultTickUnits.add(new NumberTickUnit(2.5e-44, df0));
        defaultTickUnits.add(new NumberTickUnit(2.5e-43, df0));
        defaultTickUnits.add(new NumberTickUnit(2.5e-42, df0));
        defaultTickUnits.add(new NumberTickUnit(2.5e-41, df0));
        defaultTickUnits.add(new NumberTickUnit(2.5e-40, df0));
        defaultTickUnits.add(new NumberTickUnit(2.5e-39, df0));
        defaultTickUnits.add(new NumberTickUnit(2.5e-38, df0));
        defaultTickUnits.add(new NumberTickUnit(2.5e-37, df0));
        defaultTickUnits.add(new NumberTickUnit(2.5e-36, df0));
        defaultTickUnits.add(new NumberTickUnit(2.5e-35, df0));
        defaultTickUnits.add(new NumberTickUnit(2.5e-34, df0));
        defaultTickUnits.add(new NumberTickUnit(2.5e-33, df0));
        defaultTickUnits.add(new NumberTickUnit(2.5e-32, df0));
        defaultTickUnits.add(new NumberTickUnit(2.5e-31, df0));
        defaultTickUnits.add(new NumberTickUnit(2.5e-30, df0));
        defaultTickUnits.add(new NumberTickUnit(2.5e-29, df0));
        defaultTickUnits.add(new NumberTickUnit(2.5e-28, df0));
        defaultTickUnits.add(new NumberTickUnit(2.5e-27, df0));
        defaultTickUnits.add(new NumberTickUnit(2.5e-26, df0));
        defaultTickUnits.add(new NumberTickUnit(2.5e-25, df0));
        defaultTickUnits.add(new NumberTickUnit(2.5e-24, df0));
        defaultTickUnits.add(new NumberTickUnit(2.5e-23, df0));
        defaultTickUnits.add(new NumberTickUnit(2.5e-22, df0));
        defaultTickUnits.add(new NumberTickUnit(2.5e-21, df0));
        defaultTickUnits.add(new NumberTickUnit(2.5e-20, df0));
        defaultTickUnits.add(new NumberTickUnit(2.5e-19, df0));
        defaultTickUnits.add(new NumberTickUnit(2.5e-18, df0));
        defaultTickUnits.add(new NumberTickUnit(2.5e-17, df0));
        defaultTickUnits.add(new NumberTickUnit(2.5e-16, df0));
        defaultTickUnits.add(new NumberTickUnit(2.5e-15, df0));
        defaultTickUnits.add(new NumberTickUnit(2.5e-14, df0));
        defaultTickUnits.add(new NumberTickUnit(2.5e-13, df0));
        defaultTickUnits.add(new NumberTickUnit(2.5e-12, df0));
        defaultTickUnits.add(new NumberTickUnit(2.5e-11, df0));
        defaultTickUnits.add(new NumberTickUnit(2.5e-10, df0));
        defaultTickUnits.add(new NumberTickUnit(2.5e-9, df0));
        defaultTickUnits.add(new NumberTickUnit(2.5e-8, df0));
        defaultTickUnits.add(new NumberTickUnit(2.5e-7, df0));
        defaultTickUnits.add(new NumberTickUnit(2.5e-6, df0));
        defaultTickUnits.add(new NumberTickUnit(2.5e-5, df2));
        defaultTickUnits.add(new NumberTickUnit(2.5e-4, df3));
        defaultTickUnits.add(new NumberTickUnit(0.0025, df4));
        defaultTickUnits.add(new NumberTickUnit(0.025, df5));
        defaultTickUnits.add(new NumberTickUnit(0.25, df6));
        defaultTickUnits.add(new NumberTickUnit(2.5, df7));
        defaultTickUnits.add(new NumberTickUnit(25, df8));
        defaultTickUnits.add(new NumberTickUnit(250, df8));
        defaultTickUnits.add(new NumberTickUnit(2500, df8));
        defaultTickUnits.add(new NumberTickUnit(2.5e4, df9));
        defaultTickUnits.add(new NumberTickUnit(2.5e5, df9));
        defaultTickUnits.add(new NumberTickUnit(2.5e6, df9));
        defaultTickUnits.add(new NumberTickUnit(2.5e7, df9));
        defaultTickUnits.add(new NumberTickUnit(2.5e8, df9));
        defaultTickUnits.add(new NumberTickUnit(2.5e9, df9));
        defaultTickUnits.add(new NumberTickUnit(2.5e10, df9));
        defaultTickUnits.add(new NumberTickUnit(2.5e11, df9));
        defaultTickUnits.add(new NumberTickUnit(2.5e12, df9));
        defaultTickUnits.add(new NumberTickUnit(2.5e13, df9));
        defaultTickUnits.add(new NumberTickUnit(2.5e14, df9));
        defaultTickUnits.add(new NumberTickUnit(2.5e15, df9));
        defaultTickUnits.add(new NumberTickUnit(2.5e16, df9));
        defaultTickUnits.add(new NumberTickUnit(2.5e17, df9));
        defaultTickUnits.add(new NumberTickUnit(2.5e18, df9));
        defaultTickUnits.add(new NumberTickUnit(2.5e19, df9));
        defaultTickUnits.add(new NumberTickUnit(2.5e20, df9));
        defaultTickUnits.add(new NumberTickUnit(2.5e21, df9));
        defaultTickUnits.add(new NumberTickUnit(2.5e22, df9));
        defaultTickUnits.add(new NumberTickUnit(2.5e23, df9));
        defaultTickUnits.add(new NumberTickUnit(2.5e24, df9));
        defaultTickUnits.add(new NumberTickUnit(2.5e25, df9));
        defaultTickUnits.add(new NumberTickUnit(2.5e26, df9));
        defaultTickUnits.add(new NumberTickUnit(2.5e27, df9));
        defaultTickUnits.add(new NumberTickUnit(2.5e28, df9));
        defaultTickUnits.add(new NumberTickUnit(2.5e29, df9));
        defaultTickUnits.add(new NumberTickUnit(2.5e30, df9));
        defaultTickUnits.add(new NumberTickUnit(2.5e31, df9));
        defaultTickUnits.add(new NumberTickUnit(2.5e32, df9));
        defaultTickUnits.add(new NumberTickUnit(2.5e33, df9));
        defaultTickUnits.add(new NumberTickUnit(2.5e34, df9));
        defaultTickUnits.add(new NumberTickUnit(2.5e35, df9));
        defaultTickUnits.add(new NumberTickUnit(2.5e36, df9));
        defaultTickUnits.add(new NumberTickUnit(2.5e37, df9));
        defaultTickUnits.add(new NumberTickUnit(2.5e38, df9));
        defaultTickUnits.add(new NumberTickUnit(2.5e39, df9));
        defaultTickUnits.add(new NumberTickUnit(2.5e40, df9));
        defaultTickUnits.add(new NumberTickUnit(2.5e41, df9));
        defaultTickUnits.add(new NumberTickUnit(2.5e42, df9));
        defaultTickUnits.add(new NumberTickUnit(2.5e43, df9));
        defaultTickUnits.add(new NumberTickUnit(2.5e44, df9));
        defaultTickUnits.add(new NumberTickUnit(2.5e45, df9));

        defaultTickUnits.add(new NumberTickUnit(5.0e-45, df0));
        defaultTickUnits.add(new NumberTickUnit(5.0e-44, df0));
        defaultTickUnits.add(new NumberTickUnit(5.0e-43, df0));
        defaultTickUnits.add(new NumberTickUnit(5.0e-42, df0));
        defaultTickUnits.add(new NumberTickUnit(5.0e-41, df0));
        defaultTickUnits.add(new NumberTickUnit(5.0e-40, df0));
        defaultTickUnits.add(new NumberTickUnit(5.0e-39, df0));
        defaultTickUnits.add(new NumberTickUnit(5.0e-38, df0));
        defaultTickUnits.add(new NumberTickUnit(5.0e-37, df0));
        defaultTickUnits.add(new NumberTickUnit(5.0e-36, df0));
        defaultTickUnits.add(new NumberTickUnit(5.0e-35, df0));
        defaultTickUnits.add(new NumberTickUnit(5.0e-34, df0));
        defaultTickUnits.add(new NumberTickUnit(5.0e-33, df0));
        defaultTickUnits.add(new NumberTickUnit(5.0e-32, df0));
        defaultTickUnits.add(new NumberTickUnit(5.0e-31, df0));
        defaultTickUnits.add(new NumberTickUnit(5.0e-30, df0));
        defaultTickUnits.add(new NumberTickUnit(5.0e-29, df0));
        defaultTickUnits.add(new NumberTickUnit(5.0e-28, df0));
        defaultTickUnits.add(new NumberTickUnit(5.0e-27, df0));
        defaultTickUnits.add(new NumberTickUnit(5.0e-26, df0));
        defaultTickUnits.add(new NumberTickUnit(5.0e-25, df0));
        defaultTickUnits.add(new NumberTickUnit(5.0e-24, df0));
        defaultTickUnits.add(new NumberTickUnit(5.0e-23, df0));
        defaultTickUnits.add(new NumberTickUnit(5.0e-22, df0));
        defaultTickUnits.add(new NumberTickUnit(5.0e-21, df0));
        defaultTickUnits.add(new NumberTickUnit(5.0e-20, df0));
        defaultTickUnits.add(new NumberTickUnit(5.0e-19, df0));
        defaultTickUnits.add(new NumberTickUnit(5.0e-18, df0));
        defaultTickUnits.add(new NumberTickUnit(5.0e-17, df0));
        defaultTickUnits.add(new NumberTickUnit(5.0e-16, df0));
        defaultTickUnits.add(new NumberTickUnit(5.0e-15, df0));
        defaultTickUnits.add(new NumberTickUnit(5.0e-14, df0));
        defaultTickUnits.add(new NumberTickUnit(5.0e-13, df0));
        defaultTickUnits.add(new NumberTickUnit(5.0e-12, df0));
        defaultTickUnits.add(new NumberTickUnit(5.0e-11, df0));
        defaultTickUnits.add(new NumberTickUnit(5.0e-10, df0));
        defaultTickUnits.add(new NumberTickUnit(5.0e-9, df0));
        defaultTickUnits.add(new NumberTickUnit(5.0e-8, df0));
        defaultTickUnits.add(new NumberTickUnit(5.0e-7, df0));
        defaultTickUnits.add(new NumberTickUnit(5.0e-6, df2));
        defaultTickUnits.add(new NumberTickUnit(5.0e-5, df3));
        defaultTickUnits.add(new NumberTickUnit(5.0e-4, df4));
        defaultTickUnits.add(new NumberTickUnit(0.005, df5));
        defaultTickUnits.add(new NumberTickUnit(0.05, df6));
        defaultTickUnits.add(new NumberTickUnit(0.5, df7));
        defaultTickUnits.add(new NumberTickUnit(5, df8));
        defaultTickUnits.add(new NumberTickUnit(50, df8));
        defaultTickUnits.add(new NumberTickUnit(500, df8));
        defaultTickUnits.add(new NumberTickUnit(5000, df8));
        defaultTickUnits.add(new NumberTickUnit(5.0e4, df9));
        defaultTickUnits.add(new NumberTickUnit(5.0e5, df9));
        defaultTickUnits.add(new NumberTickUnit(5.0e6, df9));
        defaultTickUnits.add(new NumberTickUnit(5.0e7, df9));
        defaultTickUnits.add(new NumberTickUnit(5.0e8, df9));
        defaultTickUnits.add(new NumberTickUnit(5.0e9, df9));
        defaultTickUnits.add(new NumberTickUnit(5.0e10, df9));
        defaultTickUnits.add(new NumberTickUnit(5.0e11, df9));
        defaultTickUnits.add(new NumberTickUnit(5.0e12, df9));
        defaultTickUnits.add(new NumberTickUnit(5.0e13, df9));
        defaultTickUnits.add(new NumberTickUnit(5.0e14, df9));
        defaultTickUnits.add(new NumberTickUnit(5.0e15, df9));
        defaultTickUnits.add(new NumberTickUnit(5.0e16, df9));
        defaultTickUnits.add(new NumberTickUnit(5.0e17, df9));
        defaultTickUnits.add(new NumberTickUnit(5.0e18, df9));
        defaultTickUnits.add(new NumberTickUnit(5.0e19, df9));
        defaultTickUnits.add(new NumberTickUnit(5.0e20, df9));
        defaultTickUnits.add(new NumberTickUnit(5.0e21, df9));
        defaultTickUnits.add(new NumberTickUnit(5.0e22, df9));
        defaultTickUnits.add(new NumberTickUnit(5.0e23, df9));
        defaultTickUnits.add(new NumberTickUnit(5.0e24, df9));
        defaultTickUnits.add(new NumberTickUnit(5.0e25, df9));
        defaultTickUnits.add(new NumberTickUnit(5.0e26, df9));
        defaultTickUnits.add(new NumberTickUnit(5.0e27, df9));
        defaultTickUnits.add(new NumberTickUnit(5.0e28, df9));
        defaultTickUnits.add(new NumberTickUnit(5.0e29, df9));
        defaultTickUnits.add(new NumberTickUnit(5.0e30, df9));
        defaultTickUnits.add(new NumberTickUnit(5.0e31, df9));
        defaultTickUnits.add(new NumberTickUnit(5.0e32, df9));
        defaultTickUnits.add(new NumberTickUnit(5.0e33, df9));
        defaultTickUnits.add(new NumberTickUnit(5.0e34, df9));
        defaultTickUnits.add(new NumberTickUnit(5.0e35, df9));
        defaultTickUnits.add(new NumberTickUnit(5.0e36, df9));
        defaultTickUnits.add(new NumberTickUnit(5.0e37, df9));
        defaultTickUnits.add(new NumberTickUnit(5.0e38, df9));
        defaultTickUnits.add(new NumberTickUnit(5.0e39, df9));
        defaultTickUnits.add(new NumberTickUnit(5.0e40, df9));
        defaultTickUnits.add(new NumberTickUnit(5.0e41, df9));
        defaultTickUnits.add(new NumberTickUnit(5.0e42, df9));
        defaultTickUnits.add(new NumberTickUnit(5.0e43, df9));
        defaultTickUnits.add(new NumberTickUnit(5.0e44, df9));
        defaultTickUnits.add(new NumberTickUnit(5.0e45, df9));

        return defaultTickUnits;
    }

    /**
     *
     * @author Dr. Richard St. John
     * @version $Revision$, $Date$
     */
    private class ScatterPanelListener implements ActionListener, DropTargetListener {

        public void actionPerformed(ActionEvent e) {
            String command = e.getActionCommand();
            if (command.equalsIgnoreCase(ACTION_HIDE_LINES)) {
                actionHideLines();
            } else if (command.equalsIgnoreCase(ACTION_SHOW_SYMBOLS)) {
                actionShowSymbols();
            } else if (command.equalsIgnoreCase(ACTION_LOG_LINEAR)) {
                updateAxis(chartPanel.getChart().getXYPlot(), "y", "x", true, false);
            } else if (command.equalsIgnoreCase(ACTION_LOG_LOG)) {
                updateAxis(chartPanel.getChart().getXYPlot(), "y", "x", true, true);
            } else if (command.equalsIgnoreCase(ACTION_LINEAR_LOG)) {
                updateAxis(chartPanel.getChart().getXYPlot(), "y", "x", false, true);
            } else if (command.equalsIgnoreCase(ACTION_LINEAR_LINEAR)) {
                updateAxis(chartPanel.getChart().getXYPlot(), "y", "x", false, false);
            } else if (command.equalsIgnoreCase(ACTION_EXPORT_PNG)) {
                actionExportPNG();
            }
        }

        private void actionExportPNG() {
            JFileChooser chooser = new JFileChooser();
            int ans = chooser.showSaveDialog(LinePlotPanel.this);
            if (ans == JFileChooser.CANCEL_OPTION) {
                return;
            }
            File outputFile = chooser.getSelectedFile();

            try {
                ChartUtilities.saveChartAsPNG(outputFile, chartPanel.getChart(), chartPanel.getWidth(), chartPanel.getHeight());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        /**
         * This routine changes between linear axis and log axis
         * @param plot The plot being modified
         * @param rangeLabel The string label for the range axis
         * @param domainLabel The string label for the domain axis
         * @param isRangeLog Boolean flag to determine if the requested axis is logarithmic. True is log axis is
         * requested.
         * @return
         */
        private XYPlot updateAxis(XYPlot plot, String rangeLabel, String domainLabel, boolean isRangeLog, boolean isDomainLog) {
            if (isRangeLog) {
                plot.setRangeAxis(createRangeLogAxis(rangeLabel));
            } else {
                plot.setRangeAxis(new NumberAxis(rangeLabel));
                plot.getRangeAxis().setStandardTickUnits(defaultTickUnits);
            }

            if (isDomainLog) {
                plot.setDomainAxis(createDomainLogAxis(domainLabel));
            } else {
                plot.setDomainAxis(new NumberAxis(domainLabel));
            }

            return plot;
        }

        private LogarithmicAxis createDomainLogAxis(String label) {
            LogarithmicAxis domainAxisLog = new LogarithmicAxis(label);
            domainAxisLog.setExpTickLabelsFlag(true);
            domainAxisLog.setLog10TickLabelsFlag(false);
            return domainAxisLog;
        }

        private LogarithmicAxis createRangeLogAxis(String label) {
            LogarithmicAxis rangeAxisLog = new LogarithmicAxis(label);
            rangeAxisLog.setExpTickLabelsFlag(true);
            rangeAxisLog.setLog10TickLabelsFlag(false);
            rangeAxisLog.setAutoRangeMinimumSize(1.0e-16);
            rangeAxisLog.setStandardTickUnits(new StandardTickUnitSource());
            return rangeAxisLog;
        }

        private void actionHideLines() {
            isLineVisible = !isLineVisible;
            XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) chartPanel.getChart().getXYPlot().getRenderer();
            renderer.setBaseLinesVisible(isLineVisible);
        }

        private void actionShowSymbols() {
            isSymbolVisible = !isSymbolVisible;
            XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) chartPanel.getChart().getXYPlot().getRenderer();
            renderer.setBaseShapesVisible(isSymbolVisible);
            renderer.setUseOutlinePaint(true);
        }

        public void dragExit(DropTargetEvent dte) {
        }

        public void dragOver(DropTargetDragEvent dtde) {
        }

        public void dropActionChanged(DropTargetDragEvent dtde) {
        }

        public void dragEnter(DropTargetDragEvent dtde) {
            dtde.acceptDrag(DnDConstants.ACTION_COPY);
        }

        public void drop(DropTargetDropEvent dtde) {
            Object droppedItem = null;
            try {
                Transferable t = dtde.getTransferable();
                if (t.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                    dtde.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
                    droppedItem = t.getTransferData(DataFlavor.stringFlavor);
                    notifyDropListeners(new DropEvent(droppedItem, dtde.getDropTargetContext(), dtde.getLocation(), dtde.getDropAction(), dtde.getSourceActions()));
                    dtde.dropComplete(true);
                    return;
                } else if (t.isDataFlavorSupported(TransferableTreeNode.NODE_FLAVOR)) {
                    dtde.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
                    droppedItem = t.getTransferData(TransferableTreeNode.NODE_FLAVOR);
                    notifyDropListeners(new DropEvent(droppedItem, dtde.getDropTargetContext(), dtde.getLocation(), dtde.getDropAction(), dtde.getSourceActions()));
                    dtde.dropComplete(true);
                    return;
                }
                dtde.rejectDrop();
                dtde.dropComplete(true);
            } catch (IOException ioe) {
                dtde.dropComplete(true);
            } catch (UnsupportedFlavorException e) {
                dtde.dropComplete(true);
            }
        }
    }
}
