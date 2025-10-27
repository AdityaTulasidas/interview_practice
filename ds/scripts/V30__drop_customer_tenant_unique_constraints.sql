-- Drop unique constraint involving customer_tenant_id on onesource_datasync_job
-- Idempotent: only drops if the constraint currently exists

DO $$
BEGIN
    -- Drop unique constraint on onesource_datasync_job
    IF EXISTS (
        SELECT 1
        FROM information_schema.table_constraints
        WHERE table_schema = 'public'
          AND table_name = 'onesource_datasync_job'
          AND constraint_type = 'UNIQUE'
          AND constraint_name = 'meta_object_customer_tenant_domain_unique'
    ) THEN
        ALTER TABLE public.onesource_datasync_job
            DROP CONSTRAINT meta_object_customer_tenant_domain_unique;
    END IF;
END $$;
