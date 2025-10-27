DO $$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM information_schema.tables
        WHERE table_schema = 'public'
          AND table_name = 'meta_object_attributes'
    ) THEN
        DROP TABLE public.meta_object_attributes;
    END IF;
END
$$;
