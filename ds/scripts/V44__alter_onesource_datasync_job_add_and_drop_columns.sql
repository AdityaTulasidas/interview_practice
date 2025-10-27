-- Add meta_object_sys_name, customer_tenant_sys_name columns
-- Drop job_name, target_regions, customer_id, meta_object_id, customer_tenant_id columns from onesource_datasync_job table
-- Rename regional_tenant_id to source_tenant_id
DO $$
BEGIN
    -- Add meta_object_sys_name column if not exists
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_name = 'onesource_datasync_job'
        AND column_name = 'meta_object_sys_name'
    ) THEN
        ALTER TABLE public.onesource_datasync_job ADD COLUMN meta_object_sys_name varchar(255);
    END IF;

    -- Add customer_tenant_sys_name column if not exists
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_name = 'onesource_datasync_job'
        AND column_name = 'customer_tenant_sys_name'
    ) THEN
        ALTER TABLE public.onesource_datasync_job ADD COLUMN customer_tenant_sys_name varchar(255);
    END IF;

    -- Drop job_name column if exists
    IF EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_name = 'onesource_datasync_job'
        AND column_name = 'job_name'
    ) THEN
        ALTER TABLE public.onesource_datasync_job DROP COLUMN job_name;
    END IF;

    -- Drop target_regions column if exists
    IF EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_name = 'onesource_datasync_job'
        AND column_name = 'target_regions'
    ) THEN
        ALTER TABLE public.onesource_datasync_job DROP COLUMN target_regions;
    END IF;

    -- Drop customer_id column if exists
    IF EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_name = 'onesource_datasync_job'
        AND column_name = 'customer_id'
    ) THEN
        ALTER TABLE public.onesource_datasync_job DROP COLUMN customer_id;
    END IF;

    -- Drop meta_object_id column if exists
    IF EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_name = 'onesource_datasync_job'
        AND column_name = 'meta_object_id'
    ) THEN
        ALTER TABLE public.onesource_datasync_job DROP COLUMN meta_object_id;
    END IF;

    -- Drop customer_tenant_id column if exists
    IF EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_name = 'onesource_datasync_job'
        AND column_name = 'customer_tenant_id'
    ) THEN
        ALTER TABLE public.onesource_datasync_job DROP COLUMN customer_tenant_id;
    END IF;

    -- Rename regional_tenant_id to source_tenant_id if exists and not already renamed
    IF EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_name = 'onesource_datasync_job'
        AND column_name = 'regional_tenant_id'
    ) AND NOT EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_name = 'onesource_datasync_job'
        AND column_name = 'source_tenant_id'
    ) THEN
        ALTER TABLE public.onesource_datasync_job RENAME COLUMN regional_tenant_id TO source_tenant_id;
    END IF;
END $$;
