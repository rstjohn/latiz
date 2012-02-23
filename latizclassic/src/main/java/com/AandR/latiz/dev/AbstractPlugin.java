/**
 * 
 */
package com.AandR.latiz.dev;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.TreeSet;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import org.jdom.Element;

import com.AandR.gui.dropSupport.JPanelWithDropSupport;
import com.AandR.latiz.core.Event;
import com.AandR.latiz.core.EventManagerInterface;
import com.AandR.latiz.core.Input;
import com.AandR.latiz.core.LatizUtility;
import com.AandR.latiz.core.NotifyObserversEvent;
import com.AandR.latiz.core.Output;
import com.AandR.latiz.core.OutputComparator;
import com.AandR.latiz.interfaces.LatFileInterface;
import com.AandR.latiz.interfaces.ParentPluginInterface;
import com.AandR.latiz.listeners.PluginChangedListener;
import com.AandR.latiz.resources.Resources;

/**
 * @author Aaron Masino
 * @version Oct 24, 2007 7:39:11 AM <br>
 *          AbstractProcessor <br>
 *          isSingleton: <br>
 *          isAbstract: <br>
 *          Comments: Abstract class for creating a component in Latiz
 * 
 */
public abstract class AbstractPlugin extends JPanelWithDropSupport {

    private boolean isDraggable = false;
    private boolean isSelected = false;
    protected boolean isFirstModifiedInputNotification = true;
    protected boolean isFirstEventNotification = true;
    protected boolean isFirstOutputRequestNotification = true;
    protected boolean hasDisplayableData = false;
    public boolean hasHUDdata = false;
    protected ArrayList<PluginChangedListener> pluginChangedListeners;
    protected ArrayList<ParentPluginInterface> parentPluginInterfaces;
    private HashMap<String, Event> eventMap;
    private EventManagerInterface eventManagerInterface;
    protected ImageIcon icon;
    private Input modifiedInput;
    private JLabel typeLabel, iconLabel;
    private JPanel iconPanel;
    private JTextField nameField;
    private LinkedHashMap<String, Input> inputsDataMap;
    private LinkedHashMap<String, Output> outputsDataMap;
    private NotifyObserversEvent notifyObserversEvent;
    private String pluginID;
    private TreeSet<Output> warnedOutputs = new TreeSet<Output>(new OutputComparator());

    // Abstract
    // methods------------------------------------------------------------------------------
    /**
     * This must be overridden. Comments: Place holder method to remind
     * developer to add input descriptions to inputsMap
     */
    abstract public void initializeInputs();

    /**
     * This must be overridden. Comments: Place holder method to remind
     * developer to add output descriptions to outputsMap
     */
    abstract public void initializeOutputs();

    abstract public void scheduleInitialEvent();

    abstract protected boolean acknowledgeEventNotification(Event event);

    abstract protected boolean acknowledgeOutputRequestNotification(String outputKey);

    abstract protected boolean acknowledgeModifiedInputsNotification(String inputKey);

    /**
     * If this method returns null, no input panel will be presented to the
     * user.
     *
     * @return JPanel userInputPanel Comments: Used to create a JPanel for any
     *         handle any user input required by the AbstractProcessor
     */
    abstract public JComponent createParametersPanel();

    /**
     * Comments: Display the output data in some format, such as a table, chart,
     * console output, or just do nothing This is used by the palette context
     * menu
     */
    abstract public void paintPluginPanel();

    /**
     * @param e
     *            Comments: Used to load the parameter inputs as stored in XML
     *            from a previously saved Latiz workspace
     */
    abstract public void loadSavedWorkspaceParameters(Element e);

    /**
     * @return e Comment: Used by Latiz to get a savable XML element with the
     *         parameter inputs
     */
    abstract public Element createWorkspaceParameters();

    // Concrete
    // methods------------------------------------------------------------------------------
    protected AbstractPlugin() {
        initialize();

        // GUI stuff -------------------
        setBackground(new Color(10, 10, 150, 20));
        setOpaque(false);
        setLayout(new BorderLayout());

        add(typeLabel, BorderLayout.NORTH);
        add(iconPanel, BorderLayout.CENTER);
        add(nameField, BorderLayout.SOUTH);
    }

    private void initialize() {
        inputsDataMap = new LinkedHashMap<String, Input>();
        outputsDataMap = new LinkedHashMap<String, Output>();
        pluginChangedListeners = new ArrayList<PluginChangedListener>();
        parentPluginInterfaces = new ArrayList<ParentPluginInterface>();
        eventMap = new HashMap<String, Event>();

        notifyObserversEvent = new NotifyObserversEvent();
        notifyObserversEvent.setPlugin(this);

        typeLabel = new JLabel("", SwingConstants.CENTER);

        iconPanel = new JPanel(new GridLayout(1, 1));
        iconPanel.setOpaque(false);
        iconPanel.add(iconLabel = new JLabel("", SwingConstants.CENTER));

        PluginListener pluginListener = new PluginListener();
        nameField = new JTextField("", 10);
        nameField.addMouseListener(pluginListener);
        nameField.addActionListener(pluginListener);
        nameField.addFocusListener(pluginListener);
        nameField.setEnabled(false);
    }

    /**
     * Used by Latiz main program
     */
    public void addEventManagerInterface(EventManagerInterface emi) {
        eventManagerInterface = emi;
    }

    protected void setLatFileInterface(String outputKey, LatFileInterface latFileInterface) {
        outputsDataMap.get(outputKey).setLatFileInterface(latFileInterface);
    }

    public HashMap<String, Input> getInputsDataMap() {
        return inputsDataMap;
    }

    public void clearInputs() {
        inputsDataMap.clear();
    }

    public HashMap<String, Output> getOutputsDataMap() {
        return outputsDataMap;
    }

    public void clearOutputs() {
        outputsDataMap.clear();
    }

    private void notifyOutputObservers() {
        for (Output o : outputsDataMap.values()) {
            o.setContinueWarningNotifications(false);
            o.notifyObserversOfUpdate();
        }
    }

    private void warnOutputObservers() {
        for (Output w : warnedOutputs) {
            w.setContinueWarningNotifications(true);
        }
        for (Output w : warnedOutputs) {
            w.notifyObserversOfWarning();
        }
    }

    public void receiveEventNotification(Event event) {
        if (event instanceof NotifyObserversEvent) {
            //   System.out.println("EEEEEEEEEEE-> " + getName() + " received event notificaiton : "+event.getDescriptor());
            notifyOutputObservers();
        } else {
            //   System.out.println("EEEEEEEEEEE-> " + getName() + " received event notificaiton : "+event.getDescriptor());
            warnedOutputs.clear();
            if (!acknowledgeEventNotification(event)) {
                eventManagerInterface.notifyExecutionFailure(getName());
                return;
            }
            if (isFirstEventNotification) {
                isFirstEventNotification = false;
            }
            notifyOutputObservers();
            warnOutputObservers();
            //   System.out.println("eeeeeeeeeee-> " + getName() + " done event notificaiton");
        }
    }

    public void receiveInputsModifiedNotification(Input input) {
//    System.out.println("ININININININ->    " + getName() + " recieved input notification");
        warnedOutputs.clear();
        if (input.getConnectedOutput().isOutputUpdated()) {
            modifiedInput = input;
        }
        if (!acknowledgeModifiedInputsNotification(input.getKey())) {
            eventManagerInterface.notifyExecutionFailure(getName());
            return;
        }
        if (isFirstModifiedInputNotification) {
            isFirstModifiedInputNotification = false;
        }
        modifiedInput = null;
        notifyOutputObservers();
        warnOutputObservers();
        modifiedInput = null;
        //  System.out.println("ininininininin->    " + getName() + " done input notification");
    }

    public void receiveOutputRequestNotificaton(Input input, String prefix) {
        Output connectedOutput = input.getConnectedOutput();
        //  System.out.println("OUTOUTOUTOUTOUT-> receiving output request of " + getName() + " -> " + connectedOutput.getKey() + " from "
        //  		+ input.getInputPlugin().getName() + " -> " + input.getKey());

//    System.out.println("         ---set initiator  " + input.getKey());

        connectedOutput.setOutputRequestInitiator(input);

        if (!acknowledgeOutputRequestNotification(connectedOutput.getKey())) {
            eventManagerInterface.notifyExecutionFailure(getName());
            return;
        }

        if (!connectedOutput.isOutputUpdated()) {
            checkForScheduledEvents();
        }

        if (!connectedOutput.isOutputUpdated()) {
            if (prefix.equals("")) {
                eventManagerInterface.validateOutputRequest(input);
            }
            prefix += input.getInputPlugin().getName() + "->" + input.getKey() + "%%";
            checkForNewInputs(prefix);
        }

        if (isFirstOutputRequestNotification) {
            isFirstOutputRequestNotification = false;
        }

        if (connectedOutput.isOutputUpdated()) {
            notifyObserversEvent.setScheduledTime(eventManagerInterface.currentTime());
            eventManagerInterface.scheduleEvent(notifyObserversEvent);
        } else {
            connectedOutput.setOutputRequestInitiator(null);
            connectedOutput.setTimeOfLastUpdate(getCurrentTime());
        }

        //  System.out.println("outoutoutoutout->" + getName() + " is done output request of  -> " + connectedOutput.getKey());
    }

    private void checkForNewInputs(String prefix) {
        Output connectedOutput;
        boolean isInputsFired = false;
        Boolean isValidForOutputRequest;
        String validationKey;
        for (Input input : inputsDataMap.values()) {
            connectedOutput = input.getConnectedOutput();
            if (connectedOutput != null) {
                if (connectedOutput.isOutputUpdated() || connectedOutput.getTimeOfLastUpdate() == getCurrentTime()) {// check if output is
                    // updated
                    if (!connectedOutput.isInputAlreadyNotified(input)
                            && (connectedOutput.getOutputRequestInitiator() == null || !connectedOutput.getOutputRequestInitiator().equals(
                            input))) {
                        connectedOutput.addInputToAlreadyNotified(input);
                        isInputsFired = true;
                        acknowledgeModifiedInputsNotification(input.getKey());
                        if (isFirstModifiedInputNotification) {
                            isFirstModifiedInputNotification = false;
                        }
                    }
                } else if (connectedOutput.getOutputRequestInitiator() == null
                        || !connectedOutput.getOutputRequestInitiator().equals(input)) {
                    // see if output should be updated
                    validationKey = prefix + getName() + "->" + input.getKey();
                    isValidForOutputRequest = input.getValidOutputRequestChainMap().get(validationKey);
                    if (isValidForOutputRequest == null) {
                        isValidForOutputRequest = true;
                    }
                    if (!isValidForOutputRequest) {
                        //  System.out.println("input : " + input.getInputPlugin().getName()+" : " + input.getKey()+" not in valid chain.");
                        continue;
                    }
                    connectedOutput.getOutputPlugin().receiveOutputRequestNotificaton(input, validationKey);
                    if (connectedOutput.isOutputUpdated() && !connectedOutput.isInputAlreadyNotified(input)) {
                        isInputsFired = true;
                        connectedOutput.addInputToAlreadyNotified(input);
                        acknowledgeModifiedInputsNotification(input.getKey());
                        if (isFirstModifiedInputNotification) {
                            isFirstModifiedInputNotification = false;
                        }
                    }
                } else if (connectedOutput.getOutputRequestInitiator().equals(input)) {
                    //some how an ifninite loop got through undetected maybe?
                    System.out.println("ERROR ? : An undetected infinite loop has occurred.");
                    //eventManagerInterface.notifyExecutionFailure(getName());
                    return;
                }
            }
        }
        if (isInputsFired) {
            notifyObserversEvent.setScheduledTime(eventManagerInterface.currentTime());
            eventManagerInterface.scheduleEvent(notifyObserversEvent);
        }
        return;
    }

    private void checkForScheduledEvents() {
        ArrayList<Event> events = eventManagerInterface.getAllEvents(this);
        if (events == null || events.isEmpty()) {
            return;
        }
        boolean isEventsFired = false;
        for (Event e : events) {
            if (e.getScheduledTime() <= eventManagerInterface.currentTime()) {
                eventManagerInterface.cancelEvent(e);
                acknowledgeEventNotification(e);
                if (isFirstEventNotification) {
                    isFirstEventNotification = false;
                }
                isEventsFired = true;
            }
        }
        if (isEventsFired) {
            notifyObserversEvent.setScheduledTime(eventManagerInterface.currentTime());
            eventManagerInterface.scheduleEvent(notifyObserversEvent);
        }
    }

    protected void updateOutput(String outputKey, Object value) {
        outputsDataMap.get(outputKey).updateOutput(value, eventManagerInterface);
    }

    protected Object getInput(String inputKey) {
        return getInput(inputKey, true);
    }

    protected Object getInput(String inputKey, boolean isInputUpdateAcknowledged) {
        Input input = inputsDataMap.get(inputKey);
        if (input.getConnectedOutput() == null) {
            return null;
        }
        if (modifiedInput != null && input.getKey().equals(modifiedInput.getKey())) {
            input.setInputUpdateAcknowledged(isInputUpdateAcknowledged);
            return input.getValue();
        }

        Output outputToRequest = input.getConnectedOutput();

        boolean outputUpdated = outputToRequest.isOutputUpdated();
        outputUpdated = outputUpdated || outputToRequest.getTimeOfLastUpdate() == getCurrentTime();
        if (outputUpdated) {
            outputToRequest.addInputToAlreadyNotified(input);
            input.setInputUpdateAcknowledged(isInputUpdateAcknowledged);
            return input.getValue();
        }
        outputToRequest.getOutputPlugin().receiveOutputRequestNotificaton(input, "");
        input.setInputUpdateAcknowledged(isInputUpdateAcknowledged);

        return input.getValue();
    }

    protected Object getInputClone(String inputKey) {
        return getInputClone(inputKey, true);
    }

    protected Object getInputClone(String inputKey, boolean isInputUpdateAcknowledged) {
        Input input = inputsDataMap.get(inputKey);
        if (input.getConnectedOutput() == null) {
            return null;
        }
        if (modifiedInput != null && input.getKey().equals(modifiedInput.getKey())) {
            input.setInputUpdateAcknowledged(isInputUpdateAcknowledged);
            return LatizUtility.cloneObject(input.getValue());
        }

        Output outputToRequest = input.getConnectedOutput();

        boolean outputUpdated = outputToRequest.isOutputUpdated();
        outputUpdated = outputUpdated || outputToRequest.getTimeOfLastUpdate() == getCurrentTime();
        if (outputUpdated) {
            outputToRequest.addInputToAlreadyNotified(input);
            input.setInputUpdateAcknowledged(isInputUpdateAcknowledged);
            return LatizUtility.cloneObject(input.getValue());
        }
        outputToRequest.getOutputPlugin().receiveOutputRequestNotificaton(input, "");
        input.setInputUpdateAcknowledged(isInputUpdateAcknowledged);

        return LatizUtility.cloneObject(input.getValue());
    }

    protected Object getOutput(String outputKey) {
        Output output = outputsDataMap.get(outputKey);
        return output.isRecallableRequired() ? output.getRecallableValue() : output.getValue();
    }

    protected void warnAllOutputObservers() {
        for (Output o : outputsDataMap.values()) {
            warnedOutputs.add(o);
        }
    }

    protected void warnOutputObservers(String outputKey) {
        warnedOutputs.add(outputsDataMap.get(outputKey));
    }

    protected boolean isOutputConnectToAnyInputs(String outputKey) {
        Output output = outputsDataMap.get(outputKey);
        return output.getObservers().size() > 0;
    }

    protected boolean isInputConnectedToOutput(String inputKey) {
        Input input = inputsDataMap.get(inputKey);
        return (input.getConnectedOutput() != null);
    }

    protected boolean isInputUpdateAcknowledged(String inputKey) {
        return inputsDataMap.get(inputKey).isInputUpdateAcknowledged();
    }

    /**
     * Convenience method for scheduling an event in the system
     *
     * @param description
     * @param timeFromNow
     */
    protected void scheduleEvent(String description, double timeFromNow) {
        double now = eventManagerInterface.currentTime();

        Event thisEvent = eventMap.get(description + String.valueOf(timeFromNow));
        if (thisEvent == null) {
            thisEvent = new Event(description, now + timeFromNow, this);
            eventMap.put(description + String.valueOf(timeFromNow), thisEvent);
        }
        thisEvent.setScheduledTime(now + timeFromNow);
        eventManagerInterface.scheduleEvent(thisEvent);
    }

    /**
     * Convenience method for adding new Input object ot inputsDataMap
     */
    protected void addNewInput(String key, Class valueClass, Class[] genericClasses, String toolTipText) {
        boolean isUsingGenerics = true;
        if (genericClasses == null || genericClasses.length == 0) {
            isUsingGenerics = false;
        }
        inputsDataMap.put(key, new Input(key, valueClass, genericClasses, isUsingGenerics, toolTipText, this));
    }

    /**
     * Convenience method for adding new Input object ot inputsDataMap
     */
    protected void addNewInput(String key, Class valueClass, Class[] genericClasses) {
        boolean isUsingGenerics = true;
        if (genericClasses == null || genericClasses.length == 0) {
            isUsingGenerics = false;
        }
        inputsDataMap.put(key, new Input(key, valueClass, genericClasses, isUsingGenerics, this));
    }

    protected void addNewInput(String key, Class valueClass) {
        addNewInput(key, valueClass, (Class[]) null);
    }

    protected void addNewInput(String key, Class valueClass, String toolTipText) {
        addNewInput(key, valueClass, (Class[]) null, toolTipText);
    }

    /**
     * Convenience method for adding new Output object ot outputsDataMap
     */
    protected void addNewOutput(String key, Class valueClass, Class[] genericClasses) {
        boolean isUsingGenerics = true;
        if (genericClasses == null || genericClasses.length == 0) {
            isUsingGenerics = false;
        }
        outputsDataMap.put(key, new Output(key, valueClass, genericClasses, isUsingGenerics, this));
    }

    protected void addNewOutput(String key, Class valueClass) {
        addNewOutput(key, valueClass, (Class[]) null);
    }

    /**
     * Convenience method for adding new Output object ot outputsDataMap
     */
    protected void addNewOutput(String key, Class valueClass, Class[] genericClasses, boolean isRecallableRequired) {
        boolean isUsingGenerics = true;
        if (genericClasses == null || genericClasses.length == 0) {
            isUsingGenerics = false;
        }
        outputsDataMap.put(key, new Output(key, valueClass, genericClasses, isUsingGenerics, isRecallableRequired, this));
    }

    /**
     * Convenience method for adding new Output object ot outputsDataMap
     */
    protected void addNewOutput(String key, Class valueClass, Class[] genericClasses, String toolTipText) {
        boolean isUsingGenerics = true;
        if (genericClasses == null || genericClasses.length == 0) {
            isUsingGenerics = false;
        }
        outputsDataMap.put(key, new Output(key, valueClass, genericClasses, isUsingGenerics, toolTipText, this));
    }

    protected void addNewOutput(String key, Class valueClass, String toolTipText) {
        addNewOutput(key, valueClass, (Class[]) null, toolTipText);
    }

    /**
     * Convenience method for adding new Output object ot outputsDataMap
     */
    protected void addNewOutput(String key, Class valueClass, Class[] genericClasses, String toolTipText, boolean isRecallbeRequired) {
        boolean isUsingGenerics = true;
        if (genericClasses == null || genericClasses.length == 0) {
            isUsingGenerics = false;
        }
        outputsDataMap.put(key, new Output(key, valueClass, genericClasses, isUsingGenerics, toolTipText, isRecallbeRequired, this));
    }

    /**
     * Convenience method for adding new Output object ot outputsDataMap
     */
    protected void addNewOutput(String key, Class valueClass, Class[] genericClasses, String toolTipText, String latFileVariableKey) {
        boolean isUsingGenerics = true;
        if (genericClasses == null || genericClasses.length == 0) {
            isUsingGenerics = false;
        }
        outputsDataMap.put(key, new Output(key, valueClass, genericClasses, isUsingGenerics, toolTipText, latFileVariableKey, this));
    }

    /**
     * Convenience method for adding new Output object ot outputsDataMap
     */
    protected void addNewOutput(String key, Class valueClass, Class[] genericClasses, String toolTipText, String latFileVariableKey, boolean isRecallbeRequired) {
        boolean isUsingGenerics = true;
        if (genericClasses == null || genericClasses.length == 0) {
            isUsingGenerics = false;
        }
        outputsDataMap.put(key, new Output(key, valueClass, genericClasses, isUsingGenerics, toolTipText, latFileVariableKey, isRecallbeRequired, this));
    }

    /**
     * Convenience method for getting the current virtual time of the simulation
     */
    protected double getCurrentTime() {
        return eventManagerInterface.currentTime();
    }

    /**
     * Convenience method for getting the most recent update time for given
     * input
     */
    protected double getInputUpdateTime(String inputKey) {
        Input input = inputsDataMap.get(inputKey);
        Output connectedOutput = input.getConnectedOutput();
        return connectedOutput.getTimeOfLastUpdate();
    }

    /**
     * Convenience method for getting the most recent update index for a given
     * input
     */
    protected int getInputUpdateIndex(String inputKey) {
        Input input = inputsDataMap.get(inputKey);
        Output connectedOutput = input.getConnectedOutput();
        return connectedOutput.getIndexOfLastUpdate();
    }

    /**
     * @return Comment: Used by Latiz to request the list of names of the output
     *         variables that the plugin can save to the Latiz Data File. The
     *         user selects which variables to save from this list.
     */
    public String[] getLatFileVariableNames() {
        String[] latFileVars = new String[outputsDataMap.size()];
        int i = 0;
        for (Output o : outputsDataMap.values()) {
            latFileVars[i++] = o.getLatFileVariableKey();
        }
        return latFileVars;
    }

    /**
     *
     * @param ppi
     */
    public void addParentPluginInterface(ParentPluginInterface ppi) {
        parentPluginInterfaces.add(ppi);
    }

    /**
     *
     * @return
     */
    public ArrayList<ParentPluginInterface> getParentPluginInterfaces() {
        return parentPluginInterfaces;
    }

    /**
     *
     * @param parent
     */
    public void removeParentPluginInterface(AbstractPlugin parent) {
        int index = -1; // = parentPluginInterfaces.indexOf(parent);
        ParentPluginInterface p;
        for (int i = 0; i < parentPluginInterfaces.size(); i++) {
            p = parentPluginInterfaces.get(i);
            if (p.getParentPlugin().getName().equals(parent.getName())) {
                index = i;
            }
        }
        if (index != -1) {
            parentPluginInterfaces.remove(index);
        }
    }

    // RSS Methods -------------------------------------------------------
    public boolean isSelected(Point pt) {
        return getBounds().contains(pt);
    }

    public void init(String desc) {
        init(desc, Resources.createIcon("defaultPlugin.png"));
    }

    public void init(String desc, ImageIcon icon) {
        setIcon(icon);
        typeLabel.setText(desc);
        setName(desc);
        setSize(getPreferredSize());
    }

    public void iconify() {
        JLabel label = new JLabel(icon);
        iconPanel.removeAll();
        iconPanel.add(label);
        iconPanel.setSize(label.getSize());
        iconPanel.revalidate();
        iconPanel.repaint();
        setSize(getPreferredSize());
        if (getParent() != null) {
            getParent().validate();
            getParent().repaint();
        }
    }

    public void addPluginChangedListener(PluginChangedListener l) {
        if (l == null) {
            return;
        }
        pluginChangedListeners.add(l);
    }

    @Override
    public void setName(String name) {
        super.setName(name);
        nameField.setText(name);
        nameField.setToolTipText(name);
    }

    public JLabel getIcon() {
        return iconLabel;
    }

    public void setIcon(ImageIcon icon) {
        this.icon = icon;
        this.iconLabel.setIcon(icon);
    }

    public JPanel getIconPanel() {
        return iconPanel;
    }

    public void setIconPanel(JPanel iconPanel) {
        this.iconPanel = iconPanel;
    }

    public JTextField getNameField() {
        return nameField;
    }

    public String getPluginID() {
        return pluginID;
    }

    public void setPluginID(String pluginID) {
        this.pluginID = pluginID;
    }

    private void notifyPluginNameChanged(String oldName, String newName) {
        for (PluginChangedListener l : pluginChangedListeners) {
            l.pluginNameChanged(oldName, newName);
        }
    }

    protected void notifyPluginOutputsChanged() {
        outputsDataMap.clear();
        initializeOutputs();
        for (PluginChangedListener pl : pluginChangedListeners) {
            pl.pluginOutputsChanged(this);
        }
    }

    protected void notifyPluginInputsChanged() {
        inputsDataMap.clear();
        initializeInputs();
        for (PluginChangedListener pl : pluginChangedListeners) {
            pl.pluginInputsChanged(this);
        }
    }

    protected void notifyPluginDisplayChanged() {
        for (PluginChangedListener pl : pluginChangedListeners) {
            pl.pluginDisplayChanged(this);
        }
    }

    public boolean isDraggable() {
        return isDraggable;
    }

    public void setDraggable(boolean isDraggable) {
        this.isDraggable = isDraggable;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

    public final void setFirstEventNotification(boolean isFirstEventNotification) {
        this.isFirstEventNotification = isFirstEventNotification;
    }

    public final void setFirstModifiedInputNotification(boolean isFirstModifiedInputNotification) {
        this.isFirstModifiedInputNotification = isFirstModifiedInputNotification;
    }

    public final void setFirstOutputRequestNotification(boolean isFirstOutputRequestNotification) {
        this.isFirstOutputRequestNotification = isFirstOutputRequestNotification;
    }

    /**
     *
     * @author Dr. Richard St. John
     * @version $Revision$, $Date$
     */
    private class PluginListener extends MouseAdapter implements ActionListener, FocusListener {

        private void setPluginName() {
            String oldName = getName();
            String newName = nameField.getText();
            setName(newName);
            notifyPluginNameChanged(oldName, newName);
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            if (e.getClickCount() != 2) {
                return;
            }
            nameField.setEnabled(true);
            nameField.requestFocusInWindow();
            nameField.selectAll();
        }

        public void actionPerformed(ActionEvent e) {
            nameField.transferFocus();
            nameField.setEnabled(false);
            setPluginName();
        }

        public void focusGained(FocusEvent e) {
        }

        public void focusLost(FocusEvent e) {
            setPluginName();
        }
    }
}
