

DO $$
BEGIN
    IF EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'meta_object' AND column_name = 'display_name') THEN
        ALTER TABLE meta_object ALTER COLUMN display_name SET NOT NULL;
    END IF;
END $$;