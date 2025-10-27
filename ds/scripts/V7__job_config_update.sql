DO $$
BEGIN
   IF EXISTS (
       SELECT 1
       FROM information_schema.tables
       WHERE table_schema = 'public'
         AND table_name = 'onesource_datasync_job'
   ) THEN
       -- Drop the existing constraint if it exists
       IF EXISTS (
           SELECT 1
           FROM information_schema.table_constraints
           WHERE constraint_name = 'meta_object_customer_tenant_domain_unique'
             AND table_name = 'onesource_datasync_job'
       ) THEN
           ALTER TABLE onesource_datasync_job
           DROP CONSTRAINT meta_object_customer_tenant_domain_unique;
       END IF;

       -- Add the new constraint including exec_type
       ALTER TABLE onesource_datasync_job
       ADD CONSTRAINT meta_object_customer_tenant_domain_unique
       UNIQUE (meta_object_id, customer_tenant_id, onesource_domain, exec_type);
   END IF;
END $$;