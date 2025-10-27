-- Enable the uuid-ossp extension
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Create table: onesource_datasync_job
DO $$
BEGIN
   IF NOT EXISTS (SELECT 1 FROM information_schema.tables WHERE table_schema = 'public' AND table_name = 'onesource_datasync_job') THEN
       CREATE TABLE onesource_datasync_job (
           id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
           job_name VARCHAR(255),
           description VARCHAR(255),
           meta_object_id uuid NOT NULL,
           job_type VARCHAR(255) NOT NULL,
           source_region VARCHAR(255) NOT NULL,
           target_regions VARCHAR(255) NOT NULL,
           customer_id VARCHAR(255),
           customer_tenant_id VARCHAR(255),
           client_id VARCHAR(255),
           onesource_domain VARCHAR(255) NOT NULL,
           exec_type VARCHAR(255) NOT NULL,
           created_at TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
           created_by VARCHAR(100),
           updated_at TIMESTAMP(6) WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
           updated_by VARCHAR(100),
           CONSTRAINT meta_object_customer_tenant_domain_unique UNIQUE (meta_object_id, customer_tenant_id, onesource_domain)
       );
   END IF;
END $$;

-- Create regional_datasync_job table if not exists
DO $$
BEGIN
   IF NOT EXISTS (SELECT 1 FROM information_schema.tables WHERE table_schema = 'public' AND table_name = 'regional_datasync_job') THEN
       CREATE TABLE regional_datasync_job (
           id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
           job_name VARCHAR(255),
           job_type VARCHAR(255) NOT NULL,
           region_type VARCHAR(255) NOT NULL,
           exec_region VARCHAR(255) NOT NULL,
           datasync_job_id UUID NOT NULL,
           is_active BOOLEAN,
           in_adaptor_id VARCHAR(255),
           out_adaptor_id VARCHAR(255),
           region_tenant_id VARCHAR(255),
           client_id VARCHAR(255),
           created_by VARCHAR(100),
           created_at TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
           updated_by VARCHAR(100),
           updated_at TIMESTAMP(6) WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
           CONSTRAINT regional_datasync_job_onesource_datasync_job_fkey FOREIGN KEY (datasync_job_id) REFERENCES onesource_datasync_job (id)
       );
   END IF;
END $$;

-- Create in_adaptor_exec_context table if not exists
DO $$
BEGIN
   IF NOT EXISTS (SELECT 1 FROM information_schema.tables WHERE table_schema = 'public' AND table_name = 'in_adaptor_exec_context') THEN
       CREATE TABLE in_adaptor_exec_context (
                  id UUID NOT NULL,
                  key VARCHAR(255),
                  value VARCHAR(255),
                  PRIMARY KEY (id, key),
                  CONSTRAINT in_adaptor_exec_context_fkey FOREIGN KEY (id) REFERENCES regional_datasync_job (id)
              );
   END IF;
END $$;

-- Create out_adaptor_exec_context table if not exists
DO $$
BEGIN
   IF NOT EXISTS (SELECT 1 FROM information_schema.tables WHERE table_schema = 'public' AND table_name = 'out_adaptor_exec_context') THEN
       CREATE TABLE out_adaptor_exec_context (
                  id UUID NOT NULL,
                  key VARCHAR(255),
                  value VARCHAR(255),
                  PRIMARY KEY (id, key),
                  CONSTRAINT out_adaptor_exec_context_fkey FOREIGN KEY (id) REFERENCES regional_datasync_job (id)
              );
   END IF;
END $$;

-- Create job_execution_log table if not exists
DO $$
BEGIN
   IF NOT EXISTS (SELECT 1 FROM information_schema.tables WHERE table_schema = 'public' AND table_name = 'job_execution_log') THEN
       CREATE TABLE job_execution_log (
           id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
           when_accepted TIMESTAMP(6) WITHOUT TIME ZONE,
           when_started TIMESTAMP(6) WITHOUT TIME ZONE,
           when_completed TIMESTAMP(6) WITHOUT TIME ZONE,
           status VARCHAR(50),
           records_read INTEGER,
           records_failed INTEGER,
           records_written INTEGER,
           customer_id VARCHAR(100),
           job_id UUID NOT NULL,
           created_by VARCHAR(100) NOT NULL,
           created_at TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
           updated_by VARCHAR(100),
           updated_at TIMESTAMP(6) WITHOUT TIME ZONE
       );
   END IF;
END $$;
