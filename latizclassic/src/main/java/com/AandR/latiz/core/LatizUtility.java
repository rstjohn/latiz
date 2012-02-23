package com.AandR.latiz.core;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.HashMap;

/**
 * @author Dr. Richard St. John
 * @version $Revision$, $Date$
 */
public class LatizUtility {

    private static final HashMap<Class, Class> converterMap = convertPrimativeClasses();

    public static Object cloneObject(Object o) {
        Object copy;

        // Serialize to a byte array
        ObjectOutput out;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        // Deserialize from a byte array
        try {
            out = new ObjectOutputStream(bos);
            out.writeObject(o);
            out.close();
            ObjectInput in = new ObjectInputStream(new ByteArrayInputStream(bos.toByteArray()));
            copy = in.readObject();
            in.close();
            return copy;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (OutOfMemoryError e) {
            System.out.println("Out of Memory Error. Set a larger heap space with: -Xmx512m");
            e.printStackTrace();
            return null;
        }
        return null;
    }

    public static Double[] convertDoublePrimativeToObject(double[] input) {
        Double[] output = new Double[input.length];
        for (int i = 0; i < input.length; i++) {
            output[i] = new Double(input[i]);
        }
        return output;
    }

    public static Double[][] convertDoubleObjectsToPrimative(double[][] input) {
        Double[][] output = new Double[input.length][input[0].length];
        for (int i = 0; i < input.length; i++) {
            for (int j = 0; j < input[0].length; j++) {
                output[i][j] = new Double(input[i][j]);
            }
        }
        return output;
    }

    public static HashMap<Class, Class> convertPrimativeClasses() {
        HashMap<Class, Class> map = new HashMap<Class, Class>();
        map.put(double.class, Double.class);
        map.put(int.class, Integer.class);
        map.put(float.class, Float.class);
        map.put(byte.class, Byte.class);
        map.put(short.class, Short.class);
        map.put(long.class, Long.class);
        map.put(boolean.class, Boolean.class);
        map.put(char.class, Character.class);
        return map;
    }

    @SuppressWarnings("unchecked")
    public static boolean checkClassAssignAbility(Class assignFromClass, Class assignToClass) {
        assignFromClass = converterMap.get(assignFromClass) == null ? assignFromClass : converterMap.get(assignFromClass);
        assignToClass = converterMap.get(assignToClass) == null ? assignToClass : converterMap.get(assignToClass);

        if (assignToClass.isAssignableFrom(assignFromClass)) {
            return true;
        } else {
            return false;
        }

    }
}
