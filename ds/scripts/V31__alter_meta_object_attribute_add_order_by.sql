-- Add order_by column to meta_object_attribute table if not exists
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_name = 'meta_object_attribute'
        AND column_name = 'order_by'
    ) THEN
        ALTER TABLE public.meta_object_attribute ADD COLUMN order_by integer;
    END IF;
END $$;