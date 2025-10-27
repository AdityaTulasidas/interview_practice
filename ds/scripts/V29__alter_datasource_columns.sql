-- Make fields optional
DO $$
BEGIN
    IF EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_name = 'onesource_data_source'
        AND column_name = 'name'
        AND is_nullable = 'NO'
    ) THEN
        ALTER TABLE public.onesource_data_source ALTER COLUMN name DROP NOT NULL;
    END IF;

    IF EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_name = 'onesource_data_source'
        AND column_name = 'regional_tenant_id'
        AND is_nullable = 'NO'
    ) THEN
        ALTER TABLE public.onesource_data_source ALTER COLUMN regional_tenant_id DROP NOT NULL;
    END IF;
END $$;

-- Make fields mandatory
DO $$
BEGIN
    IF EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_name = 'onesource_data_source'
        AND column_name = 'db'
        AND is_nullable = 'YES'
    ) THEN
        ALTER TABLE public.onesource_data_source ALTER COLUMN db SET NOT NULL;
    END IF;

    IF EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_name = 'onesource_data_source'
        AND column_name = 'host'
        AND is_nullable = 'YES'
    ) THEN
        ALTER TABLE public.onesource_data_source ALTER COLUMN host SET NOT NULL;
    END IF;

    IF EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_name = 'onesource_data_source'
        AND column_name = 'meta_object_sys_name'
        AND is_nullable = 'YES'
    ) THEN
        ALTER TABLE public.onesource_data_source ALTER COLUMN meta_object_sys_name SET NOT NULL;
    END IF;
END $$;
