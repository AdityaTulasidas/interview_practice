-- Insert into onesource_database_type table if it doesn't exist
INSERT INTO onesource_database_type (db_type, jdbc_driver, default_port)
VALUES ('POSTGRESQL', 'postgresql', 5432)
ON CONFLICT (db_type) DO NOTHING;
