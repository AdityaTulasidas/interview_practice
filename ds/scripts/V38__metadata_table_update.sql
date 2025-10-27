-- Check and alter the column type for logical_key
DO $$
BEGIN
    IF EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'meta_object_attribute' AND column_name = 'logical_key') THEN
        ALTER TABLE meta_object_attribute ALTER COLUMN logical_key TYPE INTEGER USING logical_key::INTEGER;
    END IF;
END $$;

-- Check and add the unique constraint for meta_object_sys_name and system_name
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM information_schema.table_constraints
        WHERE table_name = 'meta_object_attribute'
        AND constraint_type = 'UNIQUE'
        AND constraint_name = 'unique_meta_object_sys_name_system_name'
    ) THEN
        ALTER TABLE meta_object_attribute ADD CONSTRAINT unique_meta_object_sys_name_system_name UNIQUE (meta_object_sys_name, system_name);
    END IF;
END $$;