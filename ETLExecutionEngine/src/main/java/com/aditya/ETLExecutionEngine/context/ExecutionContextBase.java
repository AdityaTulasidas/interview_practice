package com.aditya.ETLExecutionEngine.context;

import java.util.HashMap;
import java.util.Map;

public abstract class ExecutionContextBase {
    private Map<String, Object> attributes = new HashMap();

    public ExecutionContextBase() {
    }

    public <T> void setValue(String name, T value) {
        this.attributes.put(name, value);
    }

    public <T> T getValue(String attribName) {
        return (T)(!attribName.isEmpty() ? this.attributes.get(attribName) : null);
    }

    public void setAttributes(final Map<String, Object> attributes) {
        this.attributes = attributes;
    }
}