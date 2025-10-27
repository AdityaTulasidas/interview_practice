DO $$
BEGIN

    -- Create table for integration_adaptor
    IF NOT EXISTS (SELECT 1 FROM information_schema.tables WHERE table_schema = 'public' AND table_name = 'integration_adaptor') THEN
        CREATE TABLE integration_adaptor (
            integration_adaptor_id VARCHAR(100) PRIMARY KEY,
            adaptor_description VARCHAR(1000) NOT NULL,
            implementation_class VARCHAR(250) NOT NULL,
            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
            updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
            created_by VARCHAR(100) NOT NULL,
            updated_by VARCHAR(100) NOT NULL
        );
    END IF;

    -- Create table for integration_adaptor_param
    IF NOT EXISTS (SELECT 1 FROM information_schema.tables WHERE table_schema = 'public' AND table_name = 'integration_adaptor_param') THEN
        CREATE TABLE integration_adaptor_param (
            adaptor_param_id VARCHAR(100) PRIMARY KEY,
            integration_adaptor_id VARCHAR(100) NOT NULL,
            param_name VARCHAR(100) NOT NULL,
            display_name VARCHAR(250) NOT NULL,
            data_type VARCHAR(50) NOT NULL,
            is_mandatory BOOLEAN NOT NULL DEFAULT FALSE,
            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
            updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
            created_by VARCHAR(100) NOT NULL,
            updated_by VARCHAR(100) NOT NULL,
            FOREIGN KEY (integration_adaptor_id) REFERENCES integration_adaptor (integration_adaptor_id) ON DELETE CASCADE
        );
    END IF;

END $$;