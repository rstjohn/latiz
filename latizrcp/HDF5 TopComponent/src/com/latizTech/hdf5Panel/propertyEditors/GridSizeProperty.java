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
package com.latizTech.hdf5Panel.propertyEditors;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.beans.PropertyChangeListener;
import java.beans.PropertyEditor;
import java.lang.reflect.InvocationTargetException;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import net.miginfocom.swing.MigLayout;
import org.openide.nodes.PropertySupport;

/**
 *
 * @author rstjohn
 */
public class GridSizeProperty extends PropertySupport.ReadOnly<String> {

    private int[] values;

    public GridSizeProperty(int[] values) {
        super("GridSize", String.class, "GridSize", "Number of grid points.");
        this.values = values;
    }

    @Override
    public String getValue() throws IllegalAccessException, InvocationTargetException {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < values.length - 1; i++) {
            sb.append(String.valueOf(values[i]) + ",");
        }
        sb.append(String.valueOf(values[values.length - 1]) + "]");
        return sb.toString();
    }

    @Override
    public PropertyEditor getPropertyEditor() {
        return new GridSizePropertyEditor(values);
    }


    /**
     *
     * @author rstjohn
     */
    public class GridSizePropertyEditor implements PropertyEditor {

        private int[] values;

        public GridSizePropertyEditor(int[] values) {
            this.values = values;
        }

        public void setValue(Object value) {
        }

        public Object getValue() {
            return null;
        }

        public boolean isPaintable() {
            return false;
        }

        public void paintValue(Graphics gfx, Rectangle box) {
        }

        public String getJavaInitializationString() {
            return null;
        }

        public String getAsText() {
            StringBuilder sb = new StringBuilder("[");
            for (int i = 0; i < values.length - 1; i++) {
                sb.append(String.valueOf(values[i]) + ",");
            }
            sb.append(String.valueOf(values[values.length - 1]) + "]");
            return sb.toString();
        }

        public void setAsText(String text) throws IllegalArgumentException {
        }

        public String[] getTags() {
            return null;
        }

        public Component getCustomEditor() {
            JPanel p = new JPanel(new MigLayout("wrap 2"));
            p.add(new JLabel("nx"));
            JTextField dxField = new JTextField(String.valueOf(values[0]));
            dxField.setEnabled(false);
            p.add(dxField, "pushx, growx");
            p.add(new JLabel("ny"));
            JTextField dyField = new JTextField(String.valueOf(values[1]));
            dyField.setEnabled(false);
            p.add(dyField, "pushx, growx");
            return p;
        }

        public boolean supportsCustomEditor() {
            return true;
        }

        public void addPropertyChangeListener(PropertyChangeListener listener) {
        }

        public void removePropertyChangeListener(PropertyChangeListener listener) {
        }
    }
}
