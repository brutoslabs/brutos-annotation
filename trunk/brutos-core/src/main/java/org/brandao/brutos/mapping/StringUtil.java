package org.brandao.brutos.mapping;

import java.util.ArrayList;
import java.util.List;


public class StringUtil {
    
    public static String toVariableFormat(String value){
        if(isEmpty(value))
            return null;
        
        value = adjust(value);
        
        char first = value.charAt(0);
        first = Character.toLowerCase(first);
        value = first + value.substring(1);
        return value;
    }
    
    public static boolean isEmpty(String value){
        return value == null || value.trim().length() == 0;
    }
    
    public static String trimLeft(String value){
        return value == null? null : value.replaceAll("^\\s+", "");
    }
    
    public static String trimRight(String value){
        return value == null? null : value.replaceAll("\\s+$", "");
    }
    
    public static String adjust(String value){
        if(value != null){
            String tmp = trimLeft(trimRight(value));
            return isEmpty(tmp)? null : tmp;
        }
        else
            return null;
    }
    
    public static List getList(String value, String separator){
        
        if(value == null)
            return null;
        
        List result = new ArrayList();
        String[] split = value.split(separator);
        
        for(int i=0;i<split.length;i++){
            String tmp = split[i];
            tmp = adjust(tmp);
            result.add(tmp);
        }
        
        return result;
    }
    
    public static String[] getArray(String value, String separator){
        
        if(value == null)
            return null;
        
        String[] split = value.split(separator);
        
        for(int i=0;i<split.length;i++){
            split[i] = adjust(split[i]);
        }
        
        return split;
    }

    public static String toCamelCase(String value){

        if(isEmpty(value))
            throw new RuntimeException("expected valid string");

        if(value.length() > 1)
            return Character.toUpperCase(value.charAt(0)) + value.substring(1).toLowerCase();
        else
            return String.valueOf(Character.toUpperCase(value.charAt(0)));

    }
    
}
