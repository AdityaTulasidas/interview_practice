-- Add last_run_date, customer_tenant_sys_name columns to regional_datasync_job table
-- Rename region_type to exec_leg, region_tenant_id to source_tenant_id
-- Drop job_name, exec_region, meta_object_id, customer_tenant_id columns from regional_datasync_job table
DO $$
BEGIN
    -- Add last_run_date column if not exists
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_name = 'regional_datasync_job'
        AND column_name = 'last_run_date'
    ) THEN
        ALTER TABLE public.regional_datasync_job ADD COLUMN last_run_date timestamp;
    END IF;

    -- Add customer_tenant_sys_name column if not exists
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_name = 'regional_datasync_job'
        AND column_name = 'customer_tenant_sys_name'
    ) THEN
        ALTER TABLE public.regional_datasync_job ADD COLUMN customer_tenant_sys_name varchar(255);
    END IF;

    -- Rename region_type to exec_leg if exists and not already renamed
    IF EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_name = 'regional_datasync_job'
        AND column_name = 'region_type'
    ) AND NOT EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_name = 'regional_datasync_job'
        AND column_name = 'exec_leg'
    ) THEN
        ALTER TABLE public.regional_datasync_job RENAME COLUMN region_type TO exec_leg;
    END IF;

    -- Rename region_tenant_id to source_tenant_id if exists and not already renamed
    IF EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_name = 'regional_datasync_job'
        AND column_name = 'region_tenant_id'
    ) AND NOT EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_name = 'regional_datasync_job'
        AND column_name = 'source_tenant_id'
    ) THEN
        ALTER TABLE public.regional_datasync_job RENAME COLUMN region_tenant_id TO source_tenant_id;
    END IF;

    -- Drop job_name column if exists
    IF EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_name = 'regional_datasync_job'
        AND column_name = 'job_name'
    ) THEN
        ALTER TABLE public.regional_datasync_job DROP COLUMN job_name;
    END IF;

    -- Drop exec_region column if exists
    IF EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_name = 'regional_datasync_job'
        AND column_name = 'exec_region'
    ) THEN
        ALTER TABLE public.regional_datasync_job DROP COLUMN exec_region;
    END IF;

    -- Drop meta_object_id column if exists
    IF EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_name = 'regional_datasync_job'
        AND column_name = 'meta_object_id'
    ) THEN
        ALTER TABLE public.regional_datasync_job DROP COLUMN meta_object_id;
    END IF;

    -- Drop customer_tenant_id column if exists
    IF EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_name = 'regional_datasync_job'
        AND column_name = 'customer_tenant_id'
    ) THEN
        ALTER TABLE public.regional_datasync_job DROP COLUMN customer_tenant_id;
    END IF;

    -- Drop system_name column if exists
    IF EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_name = 'regional_datasync_job'
        AND column_name = 'system_name'
    ) THEN
        ALTER TABLE public.regional_datasync_job DROP COLUMN system_name;
    END IF;

END $$;
