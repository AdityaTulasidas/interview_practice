-- Create onesource_database_type table if it doesn't exist
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM information_schema.tables
        WHERE table_name = 'onesource_database_type'
    ) THEN
        CREATE TABLE onesource_database_type (
            db_type VARCHAR NOT NULL,
            jdbc_driver VARCHAR NOT NULL,
            default_port INT,
            PRIMARY KEY (db_type)
        );
    END IF;
END $$;
