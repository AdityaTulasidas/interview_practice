-- Add datasync_job_sys_name, region, transform_func_sys_name columns
-- Drop datasync_job_id from datasync_transformation table
-- Rename transform_context to function_params, exec_type to exec_leg
DO $$
BEGIN
    -- Add datasync_job_sys_name column if not exists
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_name = 'datasync_transformation'
        AND column_name = 'datasync_job_sys_name'
    ) THEN
        ALTER TABLE public.datasync_transformation ADD COLUMN datasync_job_sys_name varchar(255);
    END IF;

    -- Add region column if not exists
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_name = 'datasync_transformation'
        AND column_name = 'region'
    ) THEN
        ALTER TABLE public.datasync_transformation ADD COLUMN region varchar(50);
    END IF;

    -- Add transform_func_sys_name column if not exists
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_name = 'datasync_transformation'
        AND column_name = 'transform_func_sys_name'
    ) THEN
        ALTER TABLE public.datasync_transformation ADD COLUMN transform_func_sys_name varchar(255);
    END IF;

    -- Drop datasync_job_id column if exists
    IF EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_name = 'datasync_transformation'
        AND column_name = 'datasync_job_id'
    ) THEN
        ALTER TABLE public.datasync_transformation DROP COLUMN datasync_job_id;
    END IF;

    -- Rename transform_context to function_params if exists and not already renamed
    IF EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_name = 'datasync_transformation'
        AND column_name = 'transform_context'
    ) AND NOT EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_name = 'datasync_transformation'
        AND column_name = 'function_params'
    ) THEN
        ALTER TABLE public.datasync_transformation RENAME COLUMN transform_context TO function_params;
    END IF;

    -- Rename exec_type to exec_leg if exists and not already renamed
    IF EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_name = 'datasync_transformation'
        AND column_name = 'exec_type'
    ) AND NOT EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_name = 'datasync_transformation'
        AND column_name = 'exec_leg'
    ) THEN
        ALTER TABLE public.datasync_transformation RENAME COLUMN exec_type TO exec_leg;
    END IF;
END $$;
