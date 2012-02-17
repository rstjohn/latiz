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
package com.AandR.palette.plugin;

import com.AandR.library.utility.FastByteArrayOutputStream;
import com.AandR.palette.model.AbstractPaletteModel;
import com.AandR.palette.plugin.data.Input;
import com.AandR.palette.plugin.data.Output;
import com.AandR.palette.plugin.hud.HudContainer;
import com.AandR.palette.plugin.hud.HudInterface;
import com.AandR.palette.runtime.IOutputInputHandler;
import com.AandR.palette.runtime.IReceiveHandler;
import com.AandR.palette.runtime.IRuntimeManager;
import com.AandR.palette.runtime.PluginRuntimeEvent;
import com.AandR.palette.runtime.exceptions.PluginRuntimeException;
import java.text.ParseException;
import java.util.LinkedHashMap;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.windows.WindowManager;

/**
 *
 * @author stjohnr
 */
public abstract class AbstractPlugin {

    protected LinkedHashMap<String, Input> inputDataMap;
    protected LinkedHashMap<String, Output> outputDataMap;
    private AbstractPaletteModel paletteModelImpl;
    private IReceiveHandler receiveHandler;
    private IRuntimeManager runtimeManager;
    private IOutputInputHandler iOutputInputHandler;

//    private JComponent hudComponent;
    private PluginKey pluginKey;
    private String name;
    private ParameterContainer parameterContainer;
    private HudContainer hudContainer;
    private boolean isParameterPanelHidden = false;

    /**
     * Set to false after the first call to
     * {@link #acknowledgeModifiedInputsNotification(String)}
     * acknowledgeModifiedInputsNotification method.
     */
    protected boolean isFirstModifiedInputNotification = true;
    /**
     * Set to false after the first call to
     * {@link #acknowledgeEventNotification(PluginRuntimeEvent)}
     * acknowledgeEventNotification method.
     */
    protected boolean isFirstEventNotification = true;
    /**
     * Set to false after the first call to
     * {@link #acknowledgeOutputRequestNotification(String)}
     * acknowledgeOutputRequestNotification method.
     */
    protected boolean isFirstOutputRequestNotification = true;

    public AbstractPlugin() {
        inputDataMap = new LinkedHashMap<String, Input>();
        outputDataMap = new LinkedHashMap<String, Output>();
        initialize();
    }

    abstract protected void initializeInputs();

    abstract protected void initializeOutputs();

    /**
     *
     * @param event
     * @throws PluginRuntimeException
     * @throws ParseException
     */
    abstract public void acknowledgeEventNotification(PluginRuntimeEvent event) throws PluginRuntimeException, ParseException;

    /**
     *
     * @param outputKey
     * @throws PluginRuntimeException
     * @throws ParseException
     */
    abstract public void acknowledgeOutputRequestNotification(String outputKey) throws PluginRuntimeException, ParseException;

    /**
     *
     * @param inputKey
     * @throws PluginRuntimeException
     * @throws ParseException
     */
    abstract public void acknowledgeModifiedInputsNotification(String inputKey) throws PluginRuntimeException, ParseException;

    /**
     *
     * @throws PluginRuntimeException
     * @throws ParseException
     */
    abstract public void scheduleInitialEvent() throws PluginRuntimeException, ParseException;

    /**
     * This method is called by the runtime manager immediately before the first scheduled event.
     */
    abstract public void setUpRun();

    /**
     * This method is called by the runtime manager immediately after the last scheduled event.
     */
    abstract public void tearDownRun();

    /**
     * This method is called by the Input and Output classes to deserialize a plugin's Input or Output value
     * class for cloning. The method must be implemented in the plugin for any Input or Output value class that
     * is not part of the Java JRE classes.
     * @param fbos
     * @return
     */
    abstract public Object deserializeObject(FastByteArrayOutputStream fbos);

    public void initialize() {
        initializeInputs();
        initializeOutputs();
    }

    public void throwRuntimeException(Exception e) {
        PluginRuntimeException exception = (e instanceof PluginRuntimeException) ? (PluginRuntimeException) e
                : new PluginRuntimeException(this, getName()+" threw a runtime exception \n"+e.getMessage());
        runtimeManager.notifyRuntimeFailure(exception);
        return;
    }

    public AbstractPaletteModel getPaletteModelImpl() {
        return paletteModelImpl;
    }

    public void setPaletteModelImpl(AbstractPaletteModel paletteModelImpl) {
        this.paletteModelImpl = paletteModelImpl;
    }

    public ParameterContainer getParameterContainer() {
        return parameterContainer;
    }

    public void setParameterContainer(ParameterContainer parameterContainer) {
        this.parameterContainer = parameterContainer;
    }

    public HudContainer getHudContainer() {
        return hudContainer;
    }

    public void setHudContainer(HudContainer hudContainer) {
        this.hudContainer = hudContainer;
    }

    public void updateHudDisplay(final Object data) {
        if(hudContainer == null) {
            return;
        }

        if(!hudContainer.isHudVisible()) {
            hudContainer = null;
            return;
        }
        
        if (hudContainer.isPaused()) {
            return;
        }
        WindowManager.getDefault().invokeWhenUIReady(new Runnable() {

            public void run() {
                ((HudInterface) AbstractPlugin.this).repaintHudComponent(hudContainer, data);
            }
        });
    }

    public IReceiveHandler getReceiveHandler() {
        return receiveHandler;
    }

    public void setReceiveHandler(IReceiveHandler receiveHandler) {
        this.receiveHandler = receiveHandler;
        if (receiveHandler != null) {
            this.receiveHandler.setAbstractPlugin(this);
        }
    }

    public IRuntimeManager getRuntimeManager() {
        return runtimeManager;
    }

    public void setRuntimeManager(IRuntimeManager runtimeManager) {
        this.runtimeManager = runtimeManager;
    }

    public IOutputInputHandler getIOutputInputHandler() {
        return iOutputInputHandler;
    }

    public void setIOutputInputHandler(IOutputInputHandler iOutputInputHandler) {
        this.iOutputInputHandler = iOutputInputHandler;
    }

    public LinkedHashMap<String, Input> getInputDataMap() {
        return inputDataMap;
    }

    public LinkedHashMap<String, Output> getOutputDataMap() {
        return outputDataMap;
    }

    public boolean isFirstEventNotification() {
        return isFirstEventNotification;
    }

    public void setFirstEventNotification(boolean isFirstEventNotification) {
        this.isFirstEventNotification = isFirstEventNotification;
    }

    public boolean isFirstModifiedInputNotification() {
        return isFirstModifiedInputNotification;
    }

    public void setFirstModifiedInputNotification(boolean isFirstModifiedInputNotification) {
        this.isFirstModifiedInputNotification = isFirstModifiedInputNotification;
    }

    public boolean isFirstOutputRequestNotification() {
        return isFirstOutputRequestNotification;
    }

    public void setFirstOutputRequestNotification(boolean isFirstOutputRequestNotification) {
        this.isFirstOutputRequestNotification = isFirstOutputRequestNotification;
    }

    public void setName(String name) {
        this.name = name;
        if (parameterContainer != null) {
            parameterContainer.setName(paletteModelImpl.getName() + "::" + name);
            parameterContainer.setDisplayName(name);
        }
    }

    public String getName() {
        return name;
    }

    public PluginKey getPluginKey() {
        return pluginKey;
    }

    public void setPluginKey(PluginKey pluginKey) {
        this.pluginKey = pluginKey;
    }

    public boolean isParameterPanelHidden() {
        return isParameterPanelHidden;
    }

    public void setParameterPanelHidden(boolean isParameterPanelHidden) {
        this.isParameterPanelHidden = isParameterPanelHidden;
        if (this.isParameterPanelHidden) {
            if (parameterContainer != null && parameterContainer.isOpened()) {
                parameterContainer.forceClose();
            }
        } else {
            if (parameterContainer != null && !parameterContainer.isOpened()) {
                parameterContainer.open();
            }
        }
    }

    public void updateHudContainerState() {
        if(hudContainer == null) {
           openHudContainer();
        } else {
            closeHudContainer();
        }
    }

//    public boolean isHudContainerVisible() {
//        return isHudContainerVisible;
//    }

    private void openHudContainer() {
        hudContainer = ((HudInterface) this).createHudComponent();
        if (hudContainer == null) {
            String message = getName() + "'s HUD component is not displayable because it is NULL.";
            NotifyDescriptor nd = new NotifyDescriptor.Message(message, NotifyDescriptor.ERROR_MESSAGE);
            DialogDisplayer.getDefault().notify(nd);
            return;
        }
        hudContainer.setName("hudEditor::" + paletteModelImpl.getName() + "::" + getName());
        hudContainer.setDisplayName(paletteModelImpl.getDisplayName() + "::" + getName());
        hudContainer.setToolTipText(paletteModelImpl.getName() + "::" + getName());
        hudContainer.open();
    }

    private void closeHudContainer() {
        hudContainer.close();
        hudContainer = null;
    }

//    public void setHudContainerVisible(boolean isHudContainerVisible) {
//        this.isHudContainerVisible = isHudContainerVisible;
//        if(isHudContainerVisible) {
//            if(hudContainer==null) {
//                hudContainer = ((HudInterface) this).createHudComponent();
//                if (hudContainer == null) {
//                    String message = getName() + "'s HUD component is not displayable because it is NULL.";
//                    NotifyDescriptor nd = new NotifyDescriptor.Message(message, NotifyDescriptor.ERROR_MESSAGE);
//                    DialogDisplayer.getDefault().notify(nd);
//                    return;
//                }
//                hudContainer.setName("hudEditor::" + getScene().getName() + "::" + getName());
//                hudContainer.setDisplayName(getName());
//            }
//            if (hudContainer != null && !hudContainer.isOpened()) {
//                hudContainer.setHudVisible(true);
//                hudContainer.open();
//            }
//        } else {
//            if (hudContainer != null && hudContainer.isOpened()) {
//                hudContainer.setHudVisible(false);
//                hudContainer.close();
//            }
//        }
//    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final AbstractPlugin other = (AbstractPlugin) obj;
        if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 67 * hash + (this.name != null ? this.name.hashCode() : 0);
        return hash;
    }
}
