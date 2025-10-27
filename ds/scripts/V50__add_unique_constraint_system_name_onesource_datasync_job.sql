DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.table_constraints
        WHERE constraint_type = 'UNIQUE'
          AND table_name = 'onesource_datasync_job'
          AND constraint_name = 'onesource_datasync_job_system_name_key'
    ) THEN
        ALTER TABLE public.onesource_datasync_job
        ADD CONSTRAINT onesource_datasync_job_system_name_key UNIQUE (system_name);
    END IF;
END $$;

