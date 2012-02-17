/**
 * 
 */
package com.AandR.latiz.core;

import java.util.HashMap;

import com.AandR.latiz.dev.AbstractPlugin;

/**
 * @author Aaron Masino
 * @version Oct 25, 2007 3:42:32 PM <br>
 * Connection <br>
 * isSingleton: <br>
 * isAbstract: <br>
 * Comments: This hmap has the input to output key relation between the inputReceivingProcessor and the outputSendingProcessor
 * 
 */
public class Connection extends HashMap<String, String> {

    private AbstractPlugin inputReceivingProcessor; //The AbstractProcessor whose inputs are going to be set
    private AbstractPlugin outputSendingProcessor; //The AbstractProcessor providing the outputs

    @Override
    public String put(String inputProcessorKey, String outputProcessorKey) {
        super.put(inputProcessorKey, outputProcessorKey);
        return outputProcessorKey;
    }

    public AbstractPlugin getInputReceivingProcessor() {
        return inputReceivingProcessor;
    }

    public void setInputReceivingProcessor(AbstractPlugin inputProcessor) {
        this.inputReceivingProcessor = inputProcessor;
    }

    public AbstractPlugin getOutputSendingProcessor() {
        return outputSendingProcessor;
    }

    public void setOutputSendingProcessor(AbstractPlugin outputProcessor) {
        this.outputSendingProcessor = outputProcessor;
    }
}
