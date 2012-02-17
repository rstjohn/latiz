/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jmatlink;

/**
 *
 * @author Aaron Masino
 */
public class MatlabUtilities {

    /**
     * returns 1 if the variable contains any NaN values
     * returns 0 if the the variable does not contain any NaNs
     * @param jMatlink
     * @param engineHandle
     * @param variable
     * @return
     */
    public static boolean anyIsNans(JMatLink jMatlink, long engineHandle, String variable) throws VariableNotFoundException{
        if(!variableExists(jMatlink, engineHandle, variable))throw new VariableNotFoundException(variable);
        jMatlink.engEvalString(engineHandle, "ans=any(isnan("+variable+"))");
        return jMatlink.engGetScalar(engineHandle, "ans")==0 ? false : true;
    }

    /**
     * returns true if field is a memeber of the structure variable
     * returns false otherwise
     * @param jMatlink
     * @param engineHandle
     * @param variable
     * @param field
     * @return
     */
    public static boolean isField(JMatLink jMatlink, long engineHandle, String variable, String field) throws VariableNotFoundException{
        String[] flds = variable.split("\\.");
        if(!variableExists(jMatlink,engineHandle,flds[0]))throw new VariableNotFoundException(flds[0]);
        jMatlink.engEvalString(engineHandle, "ans=isfield("+variable+",'"+field+"');");
        return jMatlink.engGetScalar(engineHandle, "ans")==0 ? false : true;
    }
    
    /**
     * returns true if the variable currently exists in the Matlab engine. The variable may be a
     * structrue element
     * returns false otherwise
     * @param jMatLink
     * @param engineHandle
     * @param variable
     * @return
     */
    public static boolean variableExists(JMatLink jMatLink, long engHandle, String variable){
        jMatLink.engOutputBuffer(engHandle);
        jMatLink.engEvalString(engHandle, "whos");
        String buff = jMatLink.engGetOutputBuffer(engHandle);
        if(buff==null || buff.length()<1)return false;
        String[] lines = buff.split("\n");
        if(lines.length<3)return false;
        String[] flds = variable.split("\\.");
        String thisVar;
        boolean exists;
        for (int i = 2; i < lines.length; i++) {
            if(lines[i].trim().length()<1)continue;
            thisVar=lines[i].trim();
            thisVar = thisVar.substring(0, thisVar.indexOf(" "));
            if(thisVar.equals(variable))return true;
            if (flds.length>0 && thisVar.equals(flds[0])) {
                exists = true;
                for(int j = 1; j < flds.length; j++){
                    try {
                        exists = isField(jMatLink, engHandle, thisVar, flds[j]);
                        thisVar += "." + flds[j];
                    } catch (VariableNotFoundException ex) {
                        ex.printStackTrace();
                    }
                }
                return exists;
            }
        }
        return false;
    }

}
