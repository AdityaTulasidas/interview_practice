DO $$
BEGIN

    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_name='regional_datasync_job' AND column_name='system_name'
    ) THEN
        ALTER TABLE public.regional_datasync_job ADD COLUMN system_name VARCHAR(255);
    END IF;

    IF NOT EXISTS (
            SELECT 1 FROM information_schema.columns
            WHERE table_name='regional_datasync_job' AND column_name='datasync_job_sys_name'
        ) THEN
            ALTER TABLE public.regional_datasync_job ADD COLUMN datasync_job_sys_name VARCHAR(255);
        END IF;

    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_name='regional_datasync_job' AND column_name='source_region'
    ) THEN
        ALTER TABLE public.regional_datasync_job ADD COLUMN source_region VARCHAR(255);
    END IF;

    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_name='regional_datasync_job' AND column_name='target_region'
    ) THEN
        ALTER TABLE public.regional_datasync_job ADD COLUMN target_region VARCHAR(255);
    END IF;

    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_name='regional_datasync_job' AND column_name='onesource_domain'
    ) THEN
        ALTER TABLE public.regional_datasync_job ADD COLUMN onesource_domain VARCHAR(255);
    END IF;

    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_name='regional_datasync_job' AND column_name='meta_object_id'
    ) THEN
        ALTER TABLE public.regional_datasync_job ADD COLUMN meta_object_id UUID;
    END IF;

    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_name='regional_datasync_job' AND column_name='meta_object_sys_name'
    ) THEN
        ALTER TABLE public.regional_datasync_job ADD COLUMN meta_object_sys_name VARCHAR(255);
    END IF;

    -- Add system_name to onesource_datasync_job
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_name='onesource_datasync_job' AND column_name='system_name'
    ) THEN
        ALTER TABLE public.onesource_datasync_job ADD COLUMN system_name VARCHAR(255);
    END IF;

    -- Add meta_object_sys_name to onesource_data_source
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_name='onesource_data_source' AND column_name='meta_object_sys_name'
    ) THEN
        ALTER TABLE public.onesource_data_source ADD COLUMN meta_object_sys_name VARCHAR(255);
    END IF;

END
$$;

DO $$ BEGIN
    IF NOT EXISTS (
    SELECT 1 FROM information_schema.columns
    WHERE table_name = 'job_execution_log' AND column_name = 'client_id'
    ) THEN
        ALTER TABLE job_execution_log ADD COLUMN client_id VARCHAR(100);
    END IF;
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns
    WHERE table_name = 'job_execution_log' AND column_name = 'regional_job_sys_name'
    ) THEN
        ALTER TABLE job_execution_log ADD COLUMN regional_job_sys_name VARCHAR(255);
    END IF;
END $$;
