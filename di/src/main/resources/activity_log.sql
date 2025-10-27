CREATE TABLE activity_log (
    id VARCHAR(128) PRIMARY KEY,
    region VARCHAR(255),
    data_sync_task VARCHAR(255),
    execution_id VARCHAR(255),
    component VARCHAR(255),
    timestamp VARCHAR(255),
    thread_id VARCHAR(255),
    correlation_id VARCHAR(255),
    description VARCHAR(1024),
    customer_tenant_name VARCHAR(255),
    log_type VARCHAR(255)
);
