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
package com.AandR.palette.paletteScene.actions;

import com.AandR.latiz.core.lookup.LatizLookup;
import com.AandR.palette.cookies.PaletteSelectionCookie;
import com.AandR.palette.paletteScene.PaletteUtilities;
import java.io.File;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CookieAction;

public final class LoadWorkspaceAction extends CookieAction {

    protected void performAction(Node[] activatedNodes) {
        DataObject dataObject = activatedNodes[0].getLookup().lookup(DataObject.class);
        File file = FileUtil.toFile(dataObject.getPrimaryFile());
        if(PaletteUtilities.isFileAlreadyLoaded(file)){
            NotifyDescriptor nd = new NotifyDescriptor.Confirmation("<HTML>The file <b>" + file.getName() + "</b> is already open.</HTML>", "I/O Message", NotifyDescriptor.DEFAULT_OPTION);
            DialogDisplayer.getDefault().notify(nd);
            return;
        }

        NewPaletteSceneAction newPaletteAction = new NewPaletteSceneAction();
        newPaletteAction.actionPerformed(null);
        PaletteSelectionCookie psc = LatizLookup.getDefault().lookup(PaletteSelectionCookie.class);
        try {

            psc.getActivePalette().getScenePanel().getScene().loadWorkspace(file);
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    protected int mode() {
        return CookieAction.MODE_EXACTLY_ONE;
    }

    public String getName() {
        return NbBundle.getMessage(LoadWorkspaceAction.class, "CTL_LoadWorkspaceAction");
    }

    protected Class[] cookieClasses() {
        return new Class[]{DataObject.class};
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

