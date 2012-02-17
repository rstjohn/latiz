/**
 * 
 */
package com.AandR.latiz.core;

import java.util.Comparator;
import java.util.TreeSet;

import com.AandR.latiz.dev.AbstractPlugin;

/**
 * @author Aaron Masino
 * @version Nov 27, 2007 7:11:45 PM <br>
 *
 * Comments:
 *
 */
public class LatizSystem extends TreeSet<AbstractPlugin> {

    private String name;

    /**
     * This constructor uses the default comparator <code>PluginComparator</code>.
     */
    public LatizSystem() {
        this(new PluginComparator());
    }

    public LatizSystem(Comparator<AbstractPlugin> comparator) {
        super(comparator);
    }

    @Override
    public boolean add(AbstractPlugin plugin) {
        name = plugin.getName();
        return super.add(plugin);
    }

    public final String getName() {
        return name;
    }

    public final void setName(String name) {
        this.name = name;
    }
}
