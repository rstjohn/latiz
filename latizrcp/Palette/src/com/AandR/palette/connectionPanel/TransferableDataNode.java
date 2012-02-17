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
package com.AandR.palette.connectionPanel;

import com.AandR.palette.plugin.data.TransferableData;

/**
 *
 * @author rstjohn
 */
public class TransferableDataNode {
    public static final int INPUT = 10;
    public static final int OUTPUT = 20;

    private int type;

    private TransferableData transferableData;

    private String alias, name, pluginName;

    
    public TransferableDataNode(String name, TransferableData transferableData, int type) {
        setName(name);
        this.transferableData = transferableData;
        this.type = type;
    }

    
    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
        String[] nameSplit = name.split("::");
        this.pluginName = nameSplit[0];
        this.alias = nameSplit[1];
    }


    public TransferableData getTransferableData() {
        return transferableData;
    }


    public void setTransferableData(TransferableData transferableData) {
        this.transferableData = transferableData;
    }


    public String getAlias() {
        return alias;
    }


    public void setAlias(String alias) {
        this.alias = alias;
    }

    
    int getType() {
        return type;
    }


    @Override
    public String toString() {
        return name;
    }
}
