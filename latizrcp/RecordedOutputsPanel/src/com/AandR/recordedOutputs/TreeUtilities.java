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
package com.AandR.recordedOutputs;

import java.util.List;
import org.openide.explorer.view.OutlineView;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.nodes.NodeNotFoundException;
import org.openide.nodes.NodeOp;

/**
 *
 * @author rstjohn
 */
public class TreeUtilities {

    /**
     * Used to get a list of expanded tree paths.
     * @param view The TreeView (BeanTreeView, TreeTableView, etc)
     * @param root Typically the explorerManger.getRootContext()
     * @param contextNode The selected node
     * @param paths
     */
    public static void saveExpansionState(OutlineView view, Node root, Node contextNode, List<String[]> paths) {
        Children children = contextNode.getChildren();
        Node[] nodes = children.getNodes();
        for (Node node : nodes) {
            if (view.isExpanded(node)) {
                paths.add(NodeOp.createPath(node, root));
                saveExpansionState(view, root, node, paths);
            }
        }
    }

    public static void recallExpansionState(OutlineView view, Node root, List<String[]> paths) {
        for (String[] pathSplit : paths) {
            try {
                Node nodeToExpand = NodeOp.findPath(root, pathSplit);
                if (nodeToExpand != null) {
                    view.expandNode(nodeToExpand);
                }
            } catch (NodeNotFoundException ex) {
            }
        }
    }

}
