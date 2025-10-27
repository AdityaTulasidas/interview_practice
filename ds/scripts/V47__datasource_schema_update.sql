
-- Drop 'name' column if it exists
DO $$
BEGIN
    IF EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_name = 'onesource_data_source' AND column_name = 'name'
    ) THEN
        ALTER TABLE public.onesource_data_source DROP COLUMN name;
    END IF;
END $$;

-- Add 'system_name' column if it does not exist
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_name = 'onesource_data_source' AND column_name = 'system_name'
    ) THEN
        ALTER TABLE public.onesource_data_source ADD COLUMN system_name VARCHAR(255);
    END IF;
END $$;

-- Add 'display_name' column if it does not exist
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_name = 'onesource_data_source' AND column_name = 'display_name'
    ) THEN
        ALTER TABLE public.onesource_data_source ADD COLUMN display_name VARCHAR(255);
    END IF;
END $$;



 -- Check if 'onesource_domain' exists and 'domain' does not
 DO $$
 BEGIN
    IF EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_name = 'onesource_data_source' AND column_name = 'onesource_domain'
    ) AND NOT EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_name = 'onesource_data_source' AND column_name = 'domain'
    ) THEN
        -- Rename the column
        ALTER TABLE public.onesource_data_source RENAME COLUMN onesource_domain TO domain;
    END IF;
END $$;


-- Drop 'meta_object_sys_name' column if it exists
DO $$
BEGIN
    IF EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_name = 'onesource_data_source' AND column_name = 'meta_object_sys_name'
    ) THEN
        ALTER TABLE public.onesource_data_source DROP COLUMN meta_object_sys_name;
    END IF;
END $$;

-- Add 'domain_object_sys_name' column if it does not exist
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_name = 'onesource_data_source' AND column_name = 'domain_object_sys_name'
    ) THEN
        ALTER TABLE public.onesource_data_source ADD COLUMN domain_object_sys_name VARCHAR(255);
    END IF;
END $$;


DO $$
BEGIN
    -- Drop the existing unique constraint if it exists
    IF EXISTS (
        SELECT 1
        FROM information_schema.table_constraints
        WHERE constraint_name = 'regional_tenant_domain_region_meta_unique'
          AND table_name = 'onesource_data_source'
          AND constraint_type = 'UNIQUE'
    ) THEN
        ALTER TABLE public.onesource_data_source
        DROP CONSTRAINT regional_tenant_domain_region_meta_unique;
    END IF;

    -- Add the new unique constraint with updated column names
    ALTER TABLE public.onesource_data_source
    ADD CONSTRAINT regional_tenant_domain_region_meta_unique UNIQUE (
        regional_tenant_id,
        domain,
        onesource_region,
        domain_object_sys_name
    );
END $$;