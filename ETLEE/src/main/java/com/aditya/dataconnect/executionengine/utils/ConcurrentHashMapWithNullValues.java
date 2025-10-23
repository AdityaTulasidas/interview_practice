package com.aditya.dataconnect.executionengine.utils;

import java.util.concurrent.ConcurrentHashMap;

public class ConcurrentHashMapWithNullValues {
    private static final Object NULL_PLACEHOLDER = new Object();
    private final ConcurrentHashMap<String, Object> map = new ConcurrentHashMap<>();

    public void put(String key, Object value) {
        map.put(key, value == null ? NULL_PLACEHOLDER : value);
    }

    public Object get(String key) {
        Object value = map.get(key);
        return value == NULL_PLACEHOLDER ? null : value;
    }

    public void remove(String key) {
        map.remove(key);
    }

    public boolean containsKey(String key) {
        return map.containsKey(key);
    }

    public ConcurrentHashMap<String, Object> getMap() {
        return map;
    }
}