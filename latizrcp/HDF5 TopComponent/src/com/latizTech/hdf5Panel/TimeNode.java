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
package com.latizTech.hdf5Panel;

import com.latizTech.hdf5Panel.action.PlotSelectedTimesAction;
import java.awt.Image;
import javax.swing.Action;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.util.ImageUtilities;

/**
 *
 * @author rstjohn
 */
public class TimeNode extends AbstractNode {

    private Image clock;

    public TimeNode(Double time) {
        super(Children.LEAF);
        clock = ImageUtilities.loadImage("com/latizTech/hdf5Panel/resources/clock2.png");
        setName(time.toString());
        setDisplayName(time.toString());
    }

    @Override
    public Action[] getActions(boolean b) {
        return new Action[] {PlotSelectedTimesAction.getDefault()};
    }

    @Override
    public Image getIcon(int arg0) {
        return clock;
    }

    @Override
    public Image getOpenedIcon(int arg0) {
        return clock;
    }
}
