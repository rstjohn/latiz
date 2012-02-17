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
package com.AandR.latiz.core.lookup;

import java.util.Collection;
import org.openide.util.Lookup;
import org.openide.util.Lookup.Item;
import org.openide.util.Lookup.Result;
import org.openide.util.Lookup.Template;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;

/**
 *
 * @author rstjohn
 */
public class LatizLookup {

    private AbstractLookup lkp;

    private static LatizLookup instance;
    
    private InstanceContent instanceContent;

    private LatizLookup() {
        instanceContent = new InstanceContent();
        lkp = new AbstractLookup(instanceContent);
    }

    public void addToLookup(Object o) {
        instanceContent.add(o);
    }

    public void removeFromLookup(Object o) {
        instanceContent.remove(o);
    }

    public <T> void removeAllFromLookup(Class<T> t) {
        Lookup.Result<T> result = lkp.lookupResult(t);
        for(T r : result.allInstances()) {
            instanceContent.remove(r);
        }
    }

    public static LatizLookup getDefault() {
        if(instance==null) {
            instance = new LatizLookup();
        }
        return instance;
    }

    public Lookup getLookup() {
        return lkp;
    }

    public <T> T lookup(Class<T> t) {
        return lkp.lookup(t);
    }

    public <T> Result<T> lookupResult(Class<T> t) {
        return lkp.lookupResult(t);
    }

    public <T> Result<T> lookupResult(Template<T> t) {
        return lkp.lookup(t);
    }

    public <T> Collection<? extends T> lookupAll(Class<T> t) {
        return lkp.lookupAll(t);
    }

    public final <T> Item<T> lookupItem(Template<T> t) {
        return lkp.lookupItem(t);
    }

    public final <T> Result<T> lookup(Template<T> t) {
        return lkp.lookup(t);
    }
}
