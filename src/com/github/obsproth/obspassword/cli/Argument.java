package com.github.obsproth.obspassword.cli;
import java.lang.RuntimeException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.HashMap;



public class Argument {
    Map<String, Object> options;
    public Argument() {
        this.options = new HashMap<String, Object>(); 
    }

    public Object get(String name) {
        if (this.options.containsKey(name)) {
            return this.options.get(name);
        }
        return null;
    }

    public String getString(String name) {
        Object obj = this.get(name);
        if(obj.getClass().equals(String.class)) {
            String str = (String)obj;
            return str;
        }
        return obj.toString();
    }

    public int getInteger(String name) {
        return getInteger(name, 10);
    }

    public int getInteger(String name, int base) {
        Object obj = this.get(name);
        if(obj.getClass().equals(String.class)) {
            String str = (String)obj;
            return Integer.parseInt(str);
        } else if (obj.getClass().equals(int.class)) {
            int val = (int)obj;
            return val;
        } else if (obj.getClass().equals(Integer.class)) {
            Integer val = (Integer)obj;
            return val.intValue();
        }
        throw new RuntimeException(String.format("%s object could not convert to integer", obj.getClass().getName()));
    }
    
    public boolean containsKey(String name) {
        return this.options.containsKey(name);
    }
}