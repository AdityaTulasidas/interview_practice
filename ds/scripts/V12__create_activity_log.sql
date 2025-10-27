DO $$
BEGIN
    -- Create table for activity_log
    IF NOT EXISTS (SELECT 1 FROM information_schema.tables WHERE table_schema = 'public' AND table_name = 'activity_log') THEN
        CREATE TABLE activity_log (
            id VARCHAR(50) PRIMARY KEY,
            region VARCHAR(10),
            data_sync_task VARCHAR(100),
            execution_id VARCHAR(50),
            component VARCHAR(50),
            timestamp VARCHAR(50),
            thread_id VARCHAR(100),
            correlation_id VARCHAR(50),
            description VARCHAR(255),
            customer_tenant_name VARCHAR(50),
            log_type VARCHAR(50),
            tenant_count INT,
            target_name VARCHAR(10),
            status VARCHAR(20),
            job_id VARCHAR(50)
        );
    END IF;
END $$;
