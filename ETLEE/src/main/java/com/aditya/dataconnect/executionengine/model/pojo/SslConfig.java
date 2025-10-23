package com.aditya.dataconnect.executionengine.model.pojo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SslConfig {
    private boolean enabled;
    private String algorithm;
}