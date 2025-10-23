package com.aditya.dataconnect.executionengine.context;

public class RegionalJobContext extends ExecutionContextBase {
    public static final String JOB_ID = "job_id";
    public static final String JOB_NAME = "job_name";
    public static final String EXEC_ID = "exec_id";
    public static final String REGIONAL_JOB_EXEC_ID = "rj_exec_id";
    public static final String REGIONAL_JOB_EXEC_STATUS = "rj_exec_status";
    public static final String REGIONAL_JOB_NAME = "rj_name";
    public static final String EXEC_LEG = "exec_leg";
    public static final String REGIONAL_TENANT_ID = "reg_tenant_id";
    public static final String CUSTOMER_TENANT_ID = "customer_tenant_id";
    public static final String SOURCE_REGION = "source_region";
    public static final String TARGET_REGIONS = "target_regions";
    public static final String CLIENT_ID = "client_id";
    public static final String ONESOURCE_DOMAIN = "onesource_domain";
    public static final String META_OBJECT_ID = "meta_object_id";
    public static final String META_OBJECT_SYS_NAME = "meta_object_sys_name";
    public static final String LAST_COMPLETED_AT = "last_completed_at";
    public static final String READ_COUNT = "read_count";
    public static final String WRITE_COUNT = "write_count";
    public static final String FAILED_COUNT = "failed_count";

    public RegionalJobContext() {
    }
}