package com.interview.practice;

import java.util.Map;
import java.util.HashMap;


public class Main {
    public static void main(String[] args) {
        int[] ints = {3,5};
        ExecutionContextBase ctx = new GlobalContext();
        ctx.setValue("key1", 3);
        ctx.setValue("key2", ints);
        
        String[] cities = {"NY", "CA", "DC"};
        Region region = new Region(1, "AMER", cities);
        ctx.setValue(GlobalContext.HOST_REGION, region);
        
        
        
        int key1 = ctx.getValue("key1");
        System.out.println("DEBUG: key1=" + key1);
        int[] key2 = ctx.getValue("key2");
        System.out.println("DEBUG: key2=" + (key2 == null ? "null" : key2.length));
        Region hostRegion = ctx.getValue(GlobalContext.HOST_REGION);
        System.out.println("DEBUG: hostRegion=" + (hostRegion == null ? "null" : hostRegion.name));

        if (key2 == null) {
            System.err.println("ERROR: key2 is null");
            return;
        }
        if (hostRegion == null) {
            System.err.println("ERROR: hostRegion is null");
            return;
        }
        System.out.println("int val: " + key1);
        System.out.println("Array val: " + key2[1]);
        System.out.println("Class val: " + hostRegion.name);
    }
}

 class ExecutionContextBase {
    private Map<String, Object> attributes;

    public ExecutionContextBase() {
        this.attributes = new HashMap<>();
    }
    public <T> void setValue(String name, T value ) {
        this.attributes.put(name, value);
    }

    public <T> T getValue(String attribName){

        if(!attribName.isEmpty()) {
            return (T)attributes.get(attribName);        
            
        }
        return null;
    }
}


class GlobalContext extends ExecutionContextBase {

    public static final String HOST_REGION = "region";//AMER,EMEA,APAC;
}

class Region {
    public int code;
    public String name;
    public String[] cities;
    
    public Region (int code, String name, String[] cities) {
        this.code = code;
        this.name = name;
        this.cities = cities;
    }
}
