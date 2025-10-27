DO $$ BEGIN
    -- Change id column type to UUID
    IF EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_name = 'activity_log' AND column_name = 'id' AND data_type <> 'uuid'
    ) THEN
        ALTER TABLE activity_log
        ALTER COLUMN id TYPE UUID USING id::uuid;
    END IF;

    -- Rename execution_id to job_execution_id and change type to UUID
    IF EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_name = 'activity_log' AND column_name = 'execution_id'
    ) THEN
        ALTER TABLE activity_log RENAME COLUMN execution_id TO job_execution_id;
    END IF;
    IF EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_name = 'activity_log' AND column_name = 'job_execution_id' AND data_type <> 'uuid'
    ) THEN
        ALTER TABLE activity_log
        ALTER COLUMN job_execution_id TYPE UUID USING job_execution_id::uuid;
    END IF;

    -- Drop columns
    IF EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'activity_log' AND column_name = 'region') THEN
        ALTER TABLE activity_log DROP COLUMN region;
    END IF;
    IF EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'activity_log' AND column_name = 'data_sync_task') THEN
        ALTER TABLE activity_log DROP COLUMN data_sync_task;
    END IF;
    IF EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'activity_log' AND column_name = 'timestamp') THEN
        ALTER TABLE activity_log DROP COLUMN "timestamp";
    END IF;
    IF EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'activity_log' AND column_name = 'correlation_id') THEN
        ALTER TABLE activity_log DROP COLUMN correlation_id;
    END IF;
    IF EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'activity_log' AND column_name = 'customer_tenant_name') THEN
        ALTER TABLE activity_log DROP COLUMN customer_tenant_name;
    END IF;
    IF EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'activity_log' AND column_name = 'log_type') THEN
        ALTER TABLE activity_log DROP COLUMN log_type;
    END IF;
    IF EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'activity_log' AND column_name = 'tenant_count') THEN
        ALTER TABLE activity_log DROP COLUMN tenant_count;
    END IF;
    IF EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'activity_log' AND column_name = 'target_name') THEN
        ALTER TABLE activity_log DROP COLUMN target_name;
    END IF;
    IF EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'activity_log' AND column_name = 'job_id') THEN
        ALTER TABLE activity_log DROP COLUMN job_id;
    END IF;

    -- Add new columns
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'activity_log' AND column_name = 'activity_id') THEN
        ALTER TABLE activity_log ADD COLUMN activity_id VARCHAR(100);
    END IF;
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'activity_log' AND column_name = 'created_by') THEN
        ALTER TABLE activity_log ADD COLUMN created_by VARCHAR(100);
    END IF;
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'activity_log' AND column_name = 'created_at') THEN
        ALTER TABLE activity_log ADD COLUMN created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP;
    END IF;
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'activity_log' AND column_name = 'updated_by') THEN
        ALTER TABLE activity_log ADD COLUMN updated_by VARCHAR(100);
    END IF;
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'activity_log' AND column_name = 'updated_at') THEN
        ALTER TABLE activity_log ADD COLUMN updated_at TIMESTAMP WITHOUT TIME ZONE;
    END IF;
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'activity_log' AND column_name = 'region_job_execution_id') THEN
        ALTER TABLE activity_log ADD COLUMN region_job_execution_id UUID;
    END IF;
END $$;
