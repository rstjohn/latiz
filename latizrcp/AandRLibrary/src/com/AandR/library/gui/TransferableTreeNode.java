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
package com.AandR.library.gui;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.util.Arrays;

import javax.swing.tree.DefaultMutableTreeNode;

/**
 * @author Dr. Richard St. John
 * @version $Revision: 1.1 $, $Date: 2007/08/30 15:48:13 $
 */
public class TransferableTreeNode implements Transferable {

    public static final DataFlavor NODE_FLAVOR = new DataFlavor(DefaultMutableTreeNode.class, "Default Mutable Tree Node");
    static DataFlavor[] flavors = {NODE_FLAVOR};
    private Object node;

    public TransferableTreeNode(Object node) {
        super();
        this.node = node;
    }

    public synchronized DataFlavor[] getTransferDataFlavors() {
        return flavors;
    }

    public boolean isDataFlavorSupported(DataFlavor flavor) {
        return Arrays.asList(flavors).contains(flavor);
    }

    public synchronized Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException {
        if (flavor.equals(NODE_FLAVOR)) {
            return node;
        } else {
            throw new UnsupportedFlavorException(flavor);
        }
    }
}

