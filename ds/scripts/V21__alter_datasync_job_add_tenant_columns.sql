-- Flyway migration: Add regional_tenant_id to onesource_datasync_job
-- and customer_tenant_id to regional_datasync_job (idempotent)

DO $$
BEGIN
    -- Add regional_tenant_id to onesource_datasync_job if it does not exist
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_name = 'onesource_datasync_job'
          AND column_name = 'regional_tenant_id'
    ) THEN
        ALTER TABLE public.onesource_datasync_job
            ADD COLUMN regional_tenant_id VARCHAR(255);
    END IF;

    -- Add customer_tenant_id to regional_datasync_job if it does not exist
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_name = 'regional_datasync_job'
          AND column_name = 'customer_tenant_id'
    ) THEN
        ALTER TABLE public.regional_datasync_job
            ADD COLUMN customer_tenant_id VARCHAR(255);
    END IF;
END $$;
