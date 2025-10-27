-- Rename existing columns
DO $$ BEGIN
    IF EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'job_execution_log' AND column_name = 'when_accepted') THEN
        ALTER TABLE job_execution_log RENAME COLUMN when_accepted TO accepted_at;
    END IF;

    IF EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'job_execution_log' AND column_name = 'when_started') THEN
        ALTER TABLE job_execution_log RENAME COLUMN when_started TO started_at;
    END IF;

    IF EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'job_execution_log' AND column_name = 'when_completed') THEN
        ALTER TABLE job_execution_log RENAME COLUMN when_completed TO completed_at;
    END IF;

    IF EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'job_execution_log' AND column_name = 'customer_id') THEN
        ALTER TABLE job_execution_log RENAME COLUMN customer_id TO customer_tenant_id;
    END IF;

    IF EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'job_execution_log' AND column_name = 'job_id') THEN
        ALTER TABLE job_execution_log RENAME COLUMN job_id TO regional_job_id;
    END IF;

    IF EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'job_execution_log' AND column_name = 'onesource_job_name') THEN
        ALTER TABLE job_execution_log RENAME COLUMN onesource_job_name TO regional_job_sys_name;
    END IF;
END $$;

-- Drop unnecessary columns
DO $$ BEGIN
    IF EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'job_execution_log' AND column_name = 'target_1_ended') THEN
        ALTER TABLE job_execution_log DROP COLUMN target_1_ended;
    END IF;

    IF EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'job_execution_log' AND column_name = 'target_2_ended') THEN
        ALTER TABLE job_execution_log DROP COLUMN target_2_ended;
    END IF;

    IF EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'job_execution_log' AND column_name = 'target_1_tenant_counter') THEN
        ALTER TABLE job_execution_log DROP COLUMN target_1_tenant_counter;
    END IF;

    IF EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'job_execution_log' AND column_name = 'target_2_tenant_counter') THEN
        ALTER TABLE job_execution_log DROP COLUMN target_2_tenant_counter;
    END IF;

    IF EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'job_execution_log' AND column_name = 'customer_tenant') THEN
        ALTER TABLE job_execution_log DROP COLUMN customer_tenant;
    END IF;
END $$;

-- Add new columns
DO $$ BEGIN
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'job_execution_log' AND column_name = 'region') THEN
        ALTER TABLE job_execution_log ADD COLUMN region VARCHAR(50);
    END IF;

    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'job_execution_log' AND column_name = 'regional_tenant_id') THEN
        ALTER TABLE job_execution_log ADD COLUMN regional_tenant_id VARCHAR(100);
    END IF;

    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'job_execution_log' AND column_name = 'type') THEN
        ALTER TABLE job_execution_log ADD COLUMN type VARCHAR(50);
    END IF;

    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'job_execution_log' AND column_name = 'job_execution_id') THEN
        ALTER TABLE job_execution_log ADD COLUMN job_execution_id UUID;
    END IF;
END $$;
