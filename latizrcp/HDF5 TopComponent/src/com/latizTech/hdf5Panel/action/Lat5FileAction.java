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

import com.latizTech.hdf5Panel.HDF5TopComponent;
import com.latizTech.hdf5Panel.Lat5DataObject;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CookieAction;

public final class Lat5FileAction extends CookieAction {

    protected void performAction(Node[] activatedNodes) {
        Lat5DataObject lat5DataObject = activatedNodes[0].getLookup().lookup(Lat5DataObject.class);

        HDF5TopComponent htc = HDF5TopComponent.findInstance();
        htc.addFile(lat5DataObject.getPrimaryFile());
    }

    protected int mode() {
        return CookieAction.MODE_EXACTLY_ONE;
    }

    public String getName() {
        return NbBundle.getMessage(Lat5FileAction.class, "CTL_Lat5FileAction");
    }

    protected Class[] cookieClasses() {
        return new Class[]{Lat5DataObject.class};
    }

    @Override
    protected String iconResource() {
        return "com/latizTech/hdf5Panel/action/lat5File16.png";
    }

    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }

    @Override
    protected boolean asynchronous() {
        return false;
    }
}

