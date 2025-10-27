DO $$
BEGIN
    IF EXISTS (
        SELECT 1 FROM information_schema.table_constraints
        WHERE table_name = 'onesource_data_source'
          AND constraint_name = 'customer_tenant_domain_region_unique'
    ) THEN
        ALTER TABLE public.onesource_data_source
        DROP CONSTRAINT customer_tenant_domain_region_unique;
    END IF;

    IF NOT EXISTS (
        SELECT 1 FROM information_schema.table_constraints
        WHERE table_name = 'onesource_data_source'
          AND constraint_type = 'UNIQUE'
          AND constraint_name = 'regional_tenant_domain_region_meta_unique'
    ) THEN
        ALTER TABLE public.onesource_data_source
        ADD CONSTRAINT regional_tenant_domain_region_meta_unique
        UNIQUE (regional_tenant_id, onesource_domain, onesource_region, meta_object_sys_name);
    END IF;
END;
$$;


DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_name='meta_object' AND column_name='system_name'
    ) THEN
        ALTER TABLE public.meta_object ADD COLUMN system_name VARCHAR(100);
    END IF;
END;
$$;
