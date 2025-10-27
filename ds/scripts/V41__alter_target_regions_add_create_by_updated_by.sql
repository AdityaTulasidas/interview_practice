-- Add create_by and updated_by columns to target_regions table if not exists
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_name = 'target_regions'
        AND column_name = 'created_by'
    ) THEN
        ALTER TABLE public.target_regions ADD COLUMN created_by varchar(100);
    END IF;

    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_name = 'target_regions'
        AND column_name = 'updated_by'
    ) THEN
        ALTER TABLE public.target_regions ADD COLUMN updated_by varchar(100);
    END IF;
END $$;

