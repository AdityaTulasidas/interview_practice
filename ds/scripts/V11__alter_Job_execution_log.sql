DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_name='job_execution_log' AND column_name='target_1_ended'
    ) THEN
        ALTER TABLE public.job_execution_log ADD COLUMN target_1_ended boolean NOT NULL DEFAULT false;
    END IF;

    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_name='job_execution_log' AND column_name='target_2_ended'
    ) THEN
        ALTER TABLE public.job_execution_log ADD COLUMN target_2_ended boolean NOT NULL DEFAULT false;
    END IF;

    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_name='job_execution_log' AND column_name='target_1_tenant_counter'
    ) THEN
        ALTER TABLE public.job_execution_log ADD COLUMN target_1_tenant_counter integer DEFAULT 0;
    END IF;

    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_name='job_execution_log' AND column_name='target_2_tenant_counter'
    ) THEN
        ALTER TABLE public.job_execution_log ADD COLUMN target_2_tenant_counter integer DEFAULT 0;
    END IF;
END
$$;
