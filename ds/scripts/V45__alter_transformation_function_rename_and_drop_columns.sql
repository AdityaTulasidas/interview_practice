-- Rename transform_type to type
-- Drop val_eval column from transformation_function table
DO $$
BEGIN
    -- Rename transform_type to type if exists and not already renamed
    IF EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_name = 'transformation_function'
        AND column_name = 'transform_type'
    ) AND NOT EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_name = 'transformation_function'
        AND column_name = 'type'
    ) THEN
        ALTER TABLE public.transformation_function RENAME COLUMN transform_type TO type;
    END IF;

    -- Drop val_eval column if exists
    IF EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_name = 'transformation_function'
        AND column_name = 'val_eval'
    ) THEN
        ALTER TABLE public.transformation_function DROP COLUMN val_eval;
    END IF;
END $$;

