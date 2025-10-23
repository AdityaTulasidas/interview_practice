package com.aditya.dataconnect.executionengine.utils;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

public class ConcurrentHashMapSerializer extends StdSerializer<ConcurrentHashMap<?, ?>> {



    public ConcurrentHashMapSerializer() {
        super((Class<ConcurrentHashMap<?, ?>>)(Class<?>) ConcurrentHashMap.class);
        //super(ConcurrentHashMap.class);
    }




    @Override
    public void serialize(ConcurrentHashMap<?, ?> value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();
        for (Object key : value.keySet()) {
            Object mapValue = value.get(key);
            if (mapValue != null) {
                gen.writeObjectField(key.toString(), mapValue);
            } else {
                gen.writeNullField(key.toString());
            }
        }
        gen.writeEndObject();
    }
    }
