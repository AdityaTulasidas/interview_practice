DO $$
BEGIN

    -- Create table for datasync_regional_transform
    IF NOT EXISTS (SELECT 1 FROM information_schema.tables WHERE table_schema = 'public' AND table_name = 'datasync_regional_transform') THEN
        CREATE TABLE datasync_regional_transform (
            id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
            regional_job_id UUID NOT NULL,
            execution_seq INT NOT NULL,
            transform_func_id VARCHAR(100) NOT NULL,
            func_exec_context VARCHAR(500) NOT NULL,
            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
            updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
            created_by VARCHAR(100) NOT NULL,
            updated_by VARCHAR(100) NOT NULL,
            FOREIGN KEY (regional_job_id) REFERENCES regional_datasync_job (id) ON DELETE CASCADE,
            FOREIGN KEY (transform_func_id) REFERENCES transformation_function (transform_func_id) ON DELETE CASCADE
        );
    END IF;

END $$;