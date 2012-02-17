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
package com.AandR.palette.globals;

import java.util.List;
import java.util.Map;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Node;

/**
 *
 * @author rstjohn
 */
public class GlobalVariableChildFactory extends ChildFactory<GlobalVariable> {
    
    private final Map<String, GlobalVariable> globalsMap;

    public GlobalVariableChildFactory(Map<String, GlobalVariable> globalsMap) {
        this.globalsMap = globalsMap;
    }

    @Override
    protected boolean createKeys(List<GlobalVariable> list) {
        if(list==null || list.isEmpty()) {
            list.add(new GlobalVariable("Value", "Variable", Boolean.FALSE));
            return true;
        }
        list.addAll(globalsMap.values());
        return true;
    }

    @Override
    protected Node createNodeForKey(GlobalVariable global) {
        return new GlobalVariableNode(global);
    }
}
