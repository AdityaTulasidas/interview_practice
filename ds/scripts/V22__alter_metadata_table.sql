DO $$
    BEGIN

        ALTER TABLE meta_object
        ADD COLUMN IF NOT EXISTS is_event_enabled BOOLEAN NOT NULL DEFAULT FALSE;

        ALTER TABLE meta_object_attribute
        ADD COLUMN IF NOT EXISTS is_event_enabled BOOLEAN NOT NULL DEFAULT FALSE;

END $$;
