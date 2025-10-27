-- Flyway migration: Add target_tenant_id column to regional_datasync_job (idempotent)

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_name = 'regional_datasync_job'
          AND column_name = 'target_tenant_id'
    ) THEN
        ALTER TABLE public.regional_datasync_job
            ADD COLUMN target_tenant_id VARCHAR(255);
    END IF;
END $$;
