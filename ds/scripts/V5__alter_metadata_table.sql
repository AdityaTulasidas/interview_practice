DO $$
    BEGIN

        ALTER TABLE meta_object
        ADD COLUMN IF NOT EXISTS usage_count INT NOT NULL DEFAULT 0;

END $$;