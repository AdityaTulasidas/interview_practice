DO $$
BEGIN
    -- Update column transform_context to TEXT
    IF EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'regional_datasync_job' AND column_name = 'transform_context') THEN
        ALTER TABLE regional_datasync_job ALTER COLUMN transform_context TYPE TEXT;
    END IF;
END $$;