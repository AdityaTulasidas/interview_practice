package com.aditya.dataconnect.executionengine.transformation;

import com.aditya.dataconnect.executionengine.model.entity.enums.TransformType;

public class TransformationFunctionFactory {

      /* 
        public static final String JAVA_ENV_TYPE = "built_in";
        public static final String XSLT_ENV_TYPE = "xslt";
        public static final String SCALA_ENV_TYPE = "custom_java";
    */
    private static final String REPLACE_VALUE = "cd.replace_value";
    private static final String REPLACE_REGIONAL_TENANT = "cd.mast.replace_regional_tenant";
    private static final String REPLACE_CLIENT_ID = "cd.replace_client_id";
    private static final String TRANSFORM_SIGN = "cd.fin.transform_sign";
    private static final String EXEC_XSLT_MAPPER = "cd.execute_xslt_mapper";
    private static final String EXEC_CUSTOM_JAVA = "cd.execute_custom_java";

    public static TransformationFunction getFunction(TransformType type, String name) {

        TransformationFunction retVal = null;

        if(name.equalsIgnoreCase(TransformationFunctionFactory.REPLACE_VALUE)) {
            retVal = new ReplaceValue();
        }
        else if (name.equalsIgnoreCase(TransformationFunctionFactory.REPLACE_REGIONAL_TENANT)) {
            retVal = new ReplaceRegionalTenant();
        }

        else {
            // throw DataSyncExecutionException  name function is not supported.
        }
        
        return retVal;
    }
} 