CREATE EXTENSION IF NOT EXISTS "uuid-ossp";



-- Check if the table onesource_data_store already exists before creating it
DO $$
BEGIN
   IF NOT EXISTS (SELECT 1 FROM information_schema.tables WHERE table_name = 'onesource_data_source') THEN
       CREATE TABLE onesource_data_source (
           id UUID PRIMARY KEY,
           name VARCHAR(255) NOT NULL,
           description VARCHAR(255) NOT NULL,
           db_type VARCHAR(255) NOT NULL,
           onesource_domain VARCHAR(50) NOT NULL,
           regional_tenant_id VARCHAR(255) NOT NULL,
           customer_tenant_id VARCHAR(255) NOT NULL,
           onesource_region VARCHAR(255)  NOT NULL,
           product_name VARCHAR(255) NOT NULL,
           jdbc_connect_url VARCHAR(255) NOT NULL,
           user_name VARCHAR(255) NOT NULL,
           password VARCHAR(255) NOT NULL,
           created_by VARCHAR(255),
           updated_by VARCHAR(255),
           created_at TIMESTAMP,
           updated_at TIMESTAMP,
           CONSTRAINT customer_tenant_domain_region_unique UNIQUE (customer_tenant_id, onesource_domain, onesource_region)
       );
   END IF;
END $$;