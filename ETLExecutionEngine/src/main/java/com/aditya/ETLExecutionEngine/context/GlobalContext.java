package com.aditya.ETLExecutionEngine.context;

import java.util.HashMap;

public class GlobalContext extends ExecutionContextBase {
    public static final String HOST_REGION = "region";
    public static final String CLOUD_PROVIDER = "cloud";
    public static final String MESSAGING_CONTEXT = "msg";
    private static GlobalContext globalContext;
    public static final String TRANSITHUB_CONTEXT = "transithub";

    private GlobalContext() {
    }

    public static GlobalContext getInstance() {
        synchronized (GlobalContext.class) {
            if (globalContext == null) {
                globalContext = new GlobalContext();
                globalContext.setAttributes(new HashMap<>());
            }
        }

        return globalContext;
    }
}