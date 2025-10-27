-- Remove jdbc_connect_url column if it exists
DO $$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_name = 'onesource_data_source'
          AND column_name = 'jdbc_connect_url'
    ) THEN
        ALTER TABLE onesource_data_source DROP COLUMN jdbc_connect_url;
    END IF;
END $$;

-- Remove product_name column if it exists
DO $$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_name = 'onesource_data_source'
          AND column_name = 'product_name'
    ) THEN
        ALTER TABLE onesource_data_source DROP COLUMN product_name;
    END IF;
END $$;

-- Add host column if it doesn't exist
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_name = 'onesource_data_source'
          AND column_name = 'host'
    ) THEN
        ALTER TABLE onesource_data_source ADD COLUMN host VARCHAR;
    END IF;
END $$;

-- Add db column if it doesn't exist
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_name = 'onesource_data_source'
          AND column_name = 'db'
    ) THEN
        ALTER TABLE onesource_data_source ADD COLUMN db VARCHAR;
    END IF;
END $$;

-- Add port column if it doesn't exist
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_name = 'onesource_data_source'
          AND column_name = 'port'
    ) THEN
        ALTER TABLE onesource_data_source ADD COLUMN port VARCHAR;
    END IF;
END $$;
