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
package com.AandR.recordedOutputs.actions;

import com.AandR.recordedOutputs.nodes.OutputDataNode;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JMenuItem;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Node;
import org.openide.util.actions.Presenter;

/**
 *
 */
public class SelectAllAction extends AbstractAction implements Presenter.Popup {

    private final AbstractNode pluginNode;

    public SelectAllAction(AbstractNode pluginNode) {
        this.pluginNode = pluginNode;
    }

    public void actionPerformed(ActionEvent e) {
        for (Node node : pluginNode.getChildren().getNodes()) {
            if (!(node instanceof OutputDataNode)) return;
            OutputDataNode outputNode = (OutputDataNode) node;
            outputNode.getOutputData().setSelected(true);
        }
    }

    public JMenuItem getPopupPresenter() {
        JMenuItem item = new JMenuItem("Select All");
        item.addActionListener(this);
        return item;
    }
}
