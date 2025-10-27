-- Add created_by, created_at, updated_by, updated_at columns to datasync_activity table if not exists
DO $$
BEGIN
    -- Add created_by column if not exists
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_name = 'datasync_activity'
        AND column_name = 'created_by'
    ) THEN
        ALTER TABLE public.datasync_activity ADD COLUMN created_by varchar(100);
    END IF;

    -- Add created_at column if not exists
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_name = 'datasync_activity'
        AND column_name = 'created_at'
    ) THEN
        ALTER TABLE public.datasync_activity ADD COLUMN created_at timestamp DEFAULT CURRENT_TIMESTAMP;
    END IF;

    -- Add updated_by column if not exists
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_name = 'datasync_activity'
        AND column_name = 'updated_by'
    ) THEN
        ALTER TABLE public.datasync_activity ADD COLUMN updated_by varchar(100);
    END IF;

    -- Add updated_at column if not exists
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_name = 'datasync_activity'
        AND column_name = 'updated_at'
    ) THEN
        ALTER TABLE public.datasync_activity ADD COLUMN updated_at timestamp DEFAULT CURRENT_TIMESTAMP;
    END IF;
END $$;
