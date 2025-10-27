

DO $$
DECLARE
    constraint_name text;
BEGIN


    -- Drop foreign key constraint if it exists
    IF EXISTS (
        SELECT 1
        FROM information_schema.table_constraints tc
        JOIN information_schema.key_column_usage kcu
        ON tc.constraint_name = kcu.constraint_name
        WHERE tc.table_name = 'meta_object_attribute'
        AND kcu.column_name = 'meta_object_sys_name'
        AND tc.constraint_type = 'FOREIGN KEY'
    ) THEN
        ALTER TABLE meta_object_attribute DROP CONSTRAINT meta_object_attribute_meta_object_fkey;
    END IF;


    FOR constraint_name IN
        SELECT tc.constraint_name
        FROM information_schema.table_constraints tc
        WHERE tc.table_name = 'meta_object'
        AND tc.constraint_type = 'UNIQUE'
    LOOP
        EXECUTE format('ALTER TABLE meta_object DROP CONSTRAINT %I', constraint_name);
    END LOOP;

    -- Drop all unique constraints from meta_object_attribute table
    FOR constraint_name IN
        SELECT tc.constraint_name
        FROM information_schema.table_constraints tc
        WHERE tc.table_name = 'meta_object_attribute'
        AND tc.constraint_type = 'UNIQUE'
    LOOP
        EXECUTE format('ALTER TABLE meta_object_attribute DROP CONSTRAINT %I', constraint_name);
    END LOOP;
END $$;

-- Update `meta_object` table
DO $$

BEGIN


    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'meta_object' AND column_name = 'domain_object') THEN
        ALTER TABLE meta_object ADD COLUMN domain_object VARCHAR(255);
    END IF;

    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'meta_object' AND column_name = 'schema') THEN
            ALTER TABLE meta_object ADD COLUMN schema VARCHAR(255);
        END IF;

  IF EXISTS (
      SELECT 1
      FROM information_schema.columns
      WHERE table_name = 'meta_object'
      AND column_name = 'system_name'
  )
  AND EXISTS (
      SELECT 1
      FROM information_schema.columns
      WHERE table_name = 'meta_object'
      AND column_name = 'name'
  ) THEN
      ALTER TABLE meta_object DROP COLUMN system_name;
  END IF;


    IF EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'meta_object' AND column_name = 'name') THEN
        ALTER TABLE meta_object RENAME COLUMN name TO system_name;
    END IF;

    IF EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'meta_object' AND column_name = 'table_name') THEN
        ALTER TABLE meta_object RENAME COLUMN table_name TO db_table;
    END IF;

	IF NOT EXISTS (
            SELECT 1
            FROM information_schema.table_constraints
            WHERE table_name = 'meta_object'
            AND constraint_type = 'UNIQUE'
            AND constraint_name = 'unique_system_name'
        ) THEN
            ALTER TABLE meta_object ADD CONSTRAINT unique_system_name UNIQUE (system_name);
        END IF;
END $$;

-- Update `meta_attribute` table
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'meta_object_attribute' AND column_name = 'meta_object_sys_name') THEN
        ALTER TABLE meta_object_attribute ADD COLUMN meta_object_sys_name VARCHAR(255);
    END IF;

    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'meta_object_attribute' AND column_name = 'logical_key') THEN
        ALTER TABLE meta_object_attribute ADD COLUMN logical_key VARCHAR(255);
    END IF;

    IF EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'meta_object_attribute' AND column_name = 'name') THEN
        ALTER TABLE meta_object_attribute RENAME COLUMN name TO system_name;
    END IF;

    IF EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'meta_object_attribute' AND column_name = 'db_column_name') THEN
        ALTER TABLE meta_object_attribute RENAME COLUMN db_column_name TO db_column;
    END IF;

    IF EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'meta_object_attribute' AND column_name = 'is_system_gen') THEN
        ALTER TABLE meta_object_attribute RENAME COLUMN is_system_gen TO is_sys_attribute;
    END IF;

	IF EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'meta_object_attribute' AND column_name = 'json_tag') THEN
                ALTER TABLE meta_object_attribute DROP COLUMN json_tag;
            END IF;

    IF EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'meta_object_attribute' AND column_name = 'meta_object_attribute_id') THEN
                    ALTER TABLE meta_object_attribute DROP COLUMN meta_object_attribute_id;
                END IF;

    -- Drop foreign key constraint if it exists
    IF EXISTS (
        SELECT 1
        FROM information_schema.table_constraints tc
        JOIN information_schema.key_column_usage kcu
        ON tc.constraint_name = kcu.constraint_name
        WHERE tc.table_name = 'meta_object_attribute'
        AND kcu.column_name = 'meta_object_id'
        AND tc.constraint_type = 'FOREIGN KEY'
    ) THEN
        ALTER TABLE meta_object_attribute DROP CONSTRAINT meta_object_attribute_meta_object_fkey;
    END IF;


        IF EXISTS (
            SELECT 1
            FROM information_schema.table_constraints
            WHERE table_name = 'meta_object_attribute'
            AND constraint_type = 'UNIQUE'
            AND constraint_name = 'meta_object_attribute_meta_object_attribute_id_key'
        ) THEN
            ALTER TABLE meta_object_attribute DROP CONSTRAINT meta_object_attribute_meta_object_attribute_id_key;
        END IF;


    -- Drop the column if it exists
    IF EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'meta_object_attribute' AND column_name = 'meta_object_id') THEN
        ALTER TABLE meta_object_attribute DROP COLUMN meta_object_id;
    END IF;
END $$;

-- Update `meta_object_relation` table
DO $$
BEGIN
    IF EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'meta_object_relation' AND column_name = 'meta_object_relation_id') THEN
        ALTER TABLE meta_object_relation RENAME COLUMN meta_object_relation_id TO system_name;
    END IF;
END $$;

-- Update foreign key in `meta_attribute` table
DO $$
BEGIN


    ALTER TABLE meta_object_attribute
    ADD CONSTRAINT meta_object_attribute_meta_object_fkey
    FOREIGN KEY (meta_object_sys_name)
    REFERENCES meta_object (system_name)
    ON DELETE CASCADE;
END $$;

END;