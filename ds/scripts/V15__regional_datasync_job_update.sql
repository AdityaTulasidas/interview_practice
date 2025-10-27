DO $$
BEGIN

-- Add new column transform_context
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'regional_datasync_job' AND column_name = 'transform_context') THEN
    ALTER TABLE regional_datasync_job ADD COLUMN transform_context VARCHAR(1000);
    END IF;
 -- Drop column transform_exec_context if it exists
    IF EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'regional_datasync_job' AND column_name = 'transform_exec_context') THEN
        ALTER TABLE regional_datasync_job DROP COLUMN transform_exec_context;
    END IF;
END $$;