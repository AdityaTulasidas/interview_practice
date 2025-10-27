DO $$
BEGIN
    -- Check if the column is already NOT NULL
    IF EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_name = 'onesource_data_source'
          AND column_name = 'system_name'
          AND is_nullable = 'YES'
    ) THEN
        -- Alter the column to set NOT NULL
        ALTER TABLE onesource_data_source
        ALTER COLUMN system_name SET NOT NULL;
    END IF;
END $$;