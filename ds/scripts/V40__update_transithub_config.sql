DO $$
    BEGIN

        ALTER TABLE  transithub_config
        ADD COLUMN IF NOT EXISTS schema_id VARCHAR(255) NOT NULL ;

END $$;
