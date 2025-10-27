DO $$
    BEGIN
        -- Remove NOT NULL constraint from description, display_name, and is_autogen_id in meta_object table
        ALTER TABLE meta_object
            ALTER COLUMN description DROP NOT NULL,
            ALTER COLUMN display_name DROP NOT NULL,
            ALTER COLUMN is_autogen_id DROP NOT NULL;

        -- Remove NOT NULL constraint from description and display_name in meta_object_attribute table
        ALTER TABLE meta_object_attribute
            ALTER COLUMN description DROP NOT NULL,
            ALTER COLUMN is_mandatory DROP NOT NULL,
            ALTER COLUMN is_system_gen DROP NOT NULL,
            ALTER COLUMN json_tag DROP NOT NULL,
            ALTER COLUMN seq_num DROP DEFAULT,
            ALTER COLUMN seq_num SET NOT NULL;
            -- Remove NOT NULL constraint from description in meta_object_relation table
        ALTER TABLE meta_object_relation
            ALTER COLUMN description DROP NOT NULL,
            ALTER COLUMN parent_obj_rel_col SET NOT NULL,
            ALTER COLUMN child_obj_rel_col SET NOT NULL;

        -- Remove NOT NULL constraint from description,product_name in onesource_data_source table
        ALTER TABLE onesource_data_source
            ALTER COLUMN description DROP NOT NULL,
            ALTER COLUMN product_name DROP NOT NULL,
            ALTER COLUMN customer_tenant_id DROP NOT NULL;

        -- Create sequence for user_code_seq
            IF NOT EXISTS (SELECT 1 FROM pg_class WHERE relkind = 'S' AND relname = 'user_code_seq') THEN
                CREATE SEQUENCE user_code_seq
                    START WITH 1
                    INCREMENT BY 1;
            END IF;

    END $$;