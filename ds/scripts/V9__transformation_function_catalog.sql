DO $$
BEGIN

    -- Create table for transformation_function
    IF NOT EXISTS (SELECT 1 FROM information_schema.tables WHERE table_schema = 'public' AND table_name = 'transformation_function') THEN
        CREATE TABLE transformation_function (
            transform_func_id VARCHAR(100) PRIMARY KEY,
            function_description VARCHAR(100) NOT NULL,
            implementation_class VARCHAR(250) NOT NULL,
            exec_environ VARCHAR(50) NOT NULL,
            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
            updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
            created_by VARCHAR(100) NOT NULL,
            updated_by VARCHAR(100) NOT NULL
        );
    END IF;

    -- Create table for transformation_function_param
    IF NOT EXISTS (SELECT 1 FROM information_schema.tables WHERE table_schema = 'public' AND table_name = 'transformation_function_param') THEN
        CREATE TABLE transformation_function_param (
            transform_func_param_id VARCHAR(100) PRIMARY KEY,
            transform_func_id VARCHAR(100),
            param_name VARCHAR(100) NOT NULL,
            display_name VARCHAR(250) NOT NULL,
            data_type VARCHAR(50) NOT NULL,
            is_mandatory BOOLEAN NOT NULL DEFAULT FALSE,
            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
            updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
            created_by VARCHAR(100) NOT NULL,
            updated_by VARCHAR(100) NOT NULL,
            FOREIGN KEY (transform_func_id) REFERENCES transformation_function (transform_func_id) ON DELETE CASCADE
        );
    END IF;

END $$;