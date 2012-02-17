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
package com.latizTech.simplePlugin;

import com.AandR.palette.plugin.IParameterPanel;
import com.AandR.palette.plugin.ParameterContainer;
import java.text.ParseException;

import org.jdom.Element;

import com.AandR.palette.plugin.adapters.AbstractPluginAdapter;
import com.AandR.palette.plugin.hud.HudContainer;
import com.AandR.palette.plugin.hud.HudInterface;
import com.AandR.palette.runtime.PluginRuntimeEvent;
import com.AandR.palette.runtime.exceptions.PluginRuntimeException;
import com.AandR.palette.swing.LNumberField;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * @author Dr. Richard St. John
 * @version Thu Apr 09 14:43:41 EDT 2009
 */

public class SimplePlugin extends AbstractPluginAdapter implements IParameterPanel, HudInterface {
    private static final String KEY_INPUT_X = "x";

    private static final String KEY_OUTPUT_Y = "y";

    public SimplePlugin() {
    }

    @Override
    public void initializeInputs() {
        addNewInput(KEY_INPUT_X, Number.class);
        addNewInput("x2", Number.class);
        addNewInput("x3", Number.class);
        return;
    }

    @Override
    public void initializeOutputs() {
        addNewOutput(KEY_OUTPUT_Y, Double.class);
        addNewOutput("y1", Double.class);
        addNewOutput("y2", Double.class);
        return;
    }

    public void acknowledgeModifiedInputsNotification(String inputKey) throws PluginRuntimeException, ParseException {
    }

    public void acknowledgeOutputRequestNotification(String outputKey) throws PluginRuntimeException, ParseException {
    }

    public void scheduleInitialEvent() throws PluginRuntimeException, ParseException {
    }

    public void acknowledgeEventNotification(PluginRuntimeEvent event) throws PluginRuntimeException, ParseException {
    }

    public ParameterContainer createParametersPanel() {
        JPanel p = new JPanel();
        p.add(new JLabel("Input Field"));
        p.add(new LNumberField(this, "gv", 10));
        return new ParameterContainer(p);
    }

    public Element createWorkspaceParameters() {
        Element pe = new Element("parameter");
        //TODO use this JDOM-XML element as this panel's root save location.
        return pe;
    }

    public void loadSavedWorkspaceParameters(Element e) {
        if (e == null) return;
    }

    public HudContainer createHudComponent() {
        return new HudContainer(new JLabel("Hud Panel"));
    }

    public void repaintHudComponent(HudContainer source, Object data) {
    }

}
