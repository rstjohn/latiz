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
import com.latizTech.hdf5Panel.TimeNode;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CookieAction;

public final class PlotSelectedTimesAction extends CookieAction {
    
    public static PlotSelectedTimesAction plotAction;

    private PlotSelectedTimesAction() {
    }

    public static PlotSelectedTimesAction getDefault() {
        if(plotAction==null) {
            plotAction = new PlotSelectedTimesAction();
        }
        return plotAction;
    }

    protected void performAction(Node[] activatedNodes) {
        double[] selectedTimes = new double[activatedNodes.length];
        for(int i=0; i<activatedNodes.length; i++) {
            TimeNode timeLeaf = activatedNodes[i].getLookup().lookup(TimeNode.class);
            selectedTimes[i] = Double.parseDouble(timeLeaf.getName());
        }
        DatasetNode datasetNode = (DatasetNode) activatedNodes[0].getParentNode();

        int[] dataDim = datasetNode.getDim();
        if(dataDim[0]==1) {
            if(dataDim[1]==1) {
                LatFilePlotter.plotSingleLine(datasetNode, selectedTimes);
            } else {
                LatFilePlotter.plotMultipleLines(datasetNode, selectedTimes);
            }
        } else {
            LatFilePlotter.plotImageData(datasetNode, selectedTimes);
        }
    }

    protected int mode() {
        return CookieAction.MODE_ALL;
    }

    public String getName() {
        return NbBundle.getMessage(PlotSelectedTimesAction.class, "CTL_SomeAction");
    }

    protected Class[] cookieClasses() {
        return new Class[]{TimeNode.class};
    }

    @Override
    protected void initialize() {
        super.initialize();
        // see org.openide.util.actions.SystemAction.iconResource() Javadoc for more details
        putValue("noIconInMenu", Boolean.TRUE);
    }

    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }

    @Override
    protected boolean asynchronous() {
        return false;
    }
}

