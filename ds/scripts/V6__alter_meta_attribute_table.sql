DO $$
    BEGIN

        ALTER TABLE meta_object_attribute
        RENAME COLUMN meta_object_attribute_id TO name;

END $$;