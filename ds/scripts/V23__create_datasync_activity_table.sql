DO $$
BEGIN
   IF NOT EXISTS (SELECT 1 FROM information_schema.tables WHERE table_schema = 'public' AND table_name = 'datasync_activity') THEN

        CREATE TABLE datasync_activity (
            id SERIAL PRIMARY KEY,
            activity_sys_name VARCHAR NOT NULL,
            activity_id INT NOT NULL,
            datasync_job_sys_name VARCHAR NOT NULL,
            exec_type VARCHAR NOT NULL,
            exec_seq INT NOT NULL,
            event_type VARCHAR NOT NULL,
            activity_type VARCHAR NOT NULL
        );
   END IF;


    CREATE TABLE IF NOT EXISTS transithub_config (
    id SERIAL PRIMARY KEY,
    domain_object VARCHAR(255) NOT NULL,
    meta_object_sys_name VARCHAR(255) NOT NULL,
    publisher_id VARCHAR(255) NOT NULL,
    subscription_key VARCHAR(255) NOT NULL,
    private_key TEXT NOT NULL,
    issuer VARCHAR(255) NOT NULL
);

    CREATE TABLE IF NOT EXISTS target_regions (
    id UUID PRIMARY KEY,
    target_region VARCHAR(255) NOT NULL,
    target_tenant_id VARCHAR(255) NOT NULL,
    datasync_job_sys_name VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

END $$;
