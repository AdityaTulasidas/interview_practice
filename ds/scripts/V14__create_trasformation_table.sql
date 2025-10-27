DO $$
BEGIN

    IF EXISTS (SELECT 1 FROM information_schema.tables WHERE table_schema = 'public' AND table_name = 'datasync_regional_transform') THEN
        -- Drop table transform_type if it exists
        DROP TABLE datasync_regional_transform CASCADE;
    END IF;
    -- Drop table transformation_function_param if it exists
        IF EXISTS (SELECT 1 FROM information_schema.tables WHERE table_schema = 'public' AND table_name = 'transformation_function_param') THEN
            DROP TABLE transformation_function_param CASCADE;
        END IF;

        -- Drop table transformation_function if it exists
        IF EXISTS (SELECT 1 FROM information_schema.tables WHERE table_schema = 'public' AND table_name = 'transformation_function') THEN
            DROP TABLE transformation_function CASCADE;
        END IF;


    -- Create table for transformation_function
    IF NOT EXISTS (SELECT 1 FROM information_schema.tables WHERE table_schema = 'public' AND table_name = 'transformation_function') THEN
        CREATE TABLE transformation_function (
            id INT PRIMARY KEY,
            system_name VARCHAR(100) NOT NULL UNIQUE,
            display_name VARCHAR(250) NOT NULL,
            description VARCHAR(250) NOT NULL,
            transform_type VARCHAR(50) NOT NULL,
            val_eval Boolean NOT NULL,
            onesource_domain VARCHAR(50) NOT NULL,
            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
            updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
            created_by VARCHAR(100) NOT NULL,
            updated_by VARCHAR(100) NOT NULL
        );
    END IF;

    -- Create table for transformation_function_param
    IF NOT EXISTS (SELECT 1 FROM information_schema.tables WHERE table_schema = 'public' AND table_name = 'transformation_function_param') THEN
        CREATE TABLE transformation_function_param (
            id INT PRIMARY KEY,
            transform_func_id VARCHAR(100),
            system_name VARCHAR(100) NOT NULL,
            display_name VARCHAR(250) NOT NULL,
            description VARCHAR(500) NOT NULL,
            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
            updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
            created_by VARCHAR(100) NOT NULL,
            updated_by VARCHAR(100) NOT NULL,
            FOREIGN KEY (transform_func_id) REFERENCES transformation_function (system_name) ON DELETE CASCADE
        );
    END IF;


INSERT INTO transformation_function (id, system_name, display_name, description, transform_type, val_eval, onesource_domain, created_at, updated_at, created_by, updated_by)
VALUES
    (1, 'cd.replace_value', 'Replace Value', 'General function that replace value of any field of domain object', 'built_in', false, 'cd', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system'),
    (2, 'cd.mast.replace_regional_tenant', 'Replace Regional Tenant', 'Transform regional tenant of a customer from source region to target region', 'built_in', true, 'cd.mast', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system'),
    (3, 'cd.replace_client_id', 'Client Id', 'Transform Client Id of a customer from source region to target region', 'built_in', true, 'cd.mast', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system'),
    (4, 'cd.fin.transform_sign', 'Transform Sign', 'Transfrom representation of negative and positive numbers in finanical data', 'built_in', false, 'cd.fin', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system'),
    (5, 'cd.execute_xslt_mapper', 'XSLT based Transformation', 'Support XSLT data mapping from source ONESOURCE domain object to target one', 'xslt', false, 'cd', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system'),
    (6, 'cd.execute_java', 'Custom Transformation via Java', 'Enable adding custom java code to convert source ONESOURCE object into target one', 'custom_java', false, 'cd', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system');
INSERT INTO transformation_function_param (id, system_name, display_name, description, transform_func_id, created_at, updated_at, created_by, updated_by)
VALUES
    (1, 'field_name', 'Field Name', 'Field Name of the ONESOURCE domain object', 'cd.replace_value', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system'),
    (2, 'source_value', 'Source Value', 'value of field (Field Name) in source region', 'cd.replace_value', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system'),
    (3, 'target_value', 'Target Value', 'value of field (Field Name) in target region', 'cd.replace_value', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system'),
    (4, 'data_type', 'Data Type', 'data type in java of the field', 'cd.replace_value', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system'),
    (5, 'xslt_file', 'XSLT file', 'File that contains xslt transformation scripts', 'cd.execute_xslt_mapper', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system'),
    (6, 'target_meta_obj', 'Target ONESOURCE Domain Object', 'Meta information of ONESOURCE domain object xslt transform to', 'cd.execute_xslt_mapper', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system'),
    (7, 'class_name', 'Java Class', 'Java class that has implemented the custom transformation logic', 'cd.execute_java', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system');


    IF NOT EXISTS (SELECT 1 FROM information_schema.tables WHERE table_schema = 'public' AND table_name = 'datasync_transformation') THEN
            CREATE TABLE datasync_transformation (
                id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                datasync_job_id UUID NOT NULL,
                execution_seq INT NOT NULL,
                exec_type VARCHAR(50) NOT NULL,
                type VARCHAR(50) NOT NULL,
                transform_context VARCHAR(500) NOT NULL,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                created_by VARCHAR(100) NOT NULL,
                updated_by VARCHAR(100) NOT NULL,
                FOREIGN KEY (datasync_job_id) REFERENCES onesource_datasync_job (id) ON DELETE CASCADE);
        END IF;



      -- Create table for transformation_function
          IF NOT EXISTS (SELECT 1 FROM information_schema.tables WHERE table_schema = 'public' AND table_name = 'transform_type') THEN
              CREATE TABLE transform_type (
                  id INT PRIMARY KEY,
                  system_name VARCHAR(100) NOT NULL,
                  display_name VARCHAR(250) NOT NULL,
                  description VARCHAR(250) NOT NULL,
                  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                  created_by VARCHAR(100) NOT NULL,
                  updated_by VARCHAR(100) NOT NULL
              );
          END IF;


       INSERT INTO transform_type (id, system_name, display_name, description, created_at, updated_at, created_by, updated_by)
       VALUES
           (1, 'built-in', 'Built-In Transformation', 'Provides inbuilt value transformation of known data patterns', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system'),
           (2, 'xslt', 'Data Mapper', 'Provides content structure transformation via data mapping pattern using XSLT', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system'),
           (3, 'custom_java', 'Custom Java', 'custom Java', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system');


END $$;