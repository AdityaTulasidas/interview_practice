DO $$
    BEGIN

        ALTER TABLE onesource_database_type
        ADD COLUMN IF NOT EXISTS jdbc_template VARCHAR(255);

END $$;
