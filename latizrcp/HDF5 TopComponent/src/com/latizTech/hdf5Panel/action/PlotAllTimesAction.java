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
package com.latizTech.hdf5Panel.action;

import com.latizTech.hdf5Panel.DatasetNode;
import com.latizTech.hdf5Panel.LatFilePlotter;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CookieAction;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

public final class PlotAllTimesAction extends CookieAction {

    private static PlotAllTimesAction action;

    private PlotAllTimesAction() {
    }

    protected void performAction(Node[] activatedNodes) {
        TopComponent propertiesComponent = WindowManager.getDefault().findTopComponent("properties");
        if (!propertiesComponent.isOpened()) {
            WindowManager.getDefault().findMode("properties").dockInto(propertiesComponent);
            propertiesComponent.open();
        }

        DatasetNode datasetNode = activatedNodes[0].getLookup().lookup(DatasetNode.class);
        int[] dataDim = datasetNode.getDim();
        if(dataDim[0]==1) {
            if(dataDim[1]==1) {
                LatFilePlotter.plotSingleLine(datasetNode);
            } else {
                LatFilePlotter.plotMultipleLines(datasetNode);
            }
        } else {
            LatFilePlotter.plotImageData(datasetNode);
        }
    }

    public static PlotAllTimesAction getDefault() {
        if (action == null) {
            action = new PlotAllTimesAction();
        }
        return action;
    }

    protected int mode() {
        return CookieAction.MODE_EXACTLY_ONE;
    }

    public String getName() {
        return NbBundle.getMessage(PlotAllTimesAction.class, "CTL_PlotAllTimesAction");
    }

    protected Class[] cookieClasses() {
        return new Class[]{DatasetNode.class};
    }

    @Override
    protected String iconResource() {
        return "com/latizTech/hdf5Panel/action/chart.png";
    }

    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }

    @Override
    protected boolean asynchronous() {
        return false;
    }
}

