DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.table_constraints
        WHERE constraint_type = 'FOREIGN KEY'
          AND table_name = 'datasync_transformation'
          AND constraint_name = 'datasync_transformation_job_sys_name_fkey'
    ) THEN
        ALTER TABLE public.datasync_transformation
        ADD CONSTRAINT datasync_transformation_job_sys_name_fkey
        FOREIGN KEY (datasync_job_sys_name)
        REFERENCES public.onesource_datasync_job(system_name)
        ON DELETE CASCADE;
    END IF;
END $$;

