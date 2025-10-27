-- Enable the uuid-ossp extension
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";


   -- Create table: meta_object
   DO $$
   BEGIN
       IF NOT EXISTS (SELECT 1 FROM information_schema.tables WHERE table_name = 'meta_object' AND table_schema = 'public') THEN
           CREATE TABLE meta_object (
               id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
               description VARCHAR(255) NOT NULL,
               table_name VARCHAR(255) UNIQUE NOT NULL,
               onesource_domain VARCHAR(50) NOT NULL,
               display_name VARCHAR(255),
               is_autogen_id BOOLEAN NOT NULL,
               name VARCHAR(255) NOT NULL,
               created_by VARCHAR(100) NOT NULL,
               created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
               updated_by VARCHAR(100),
               updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
           );
       END IF;
   END $$;

   -- Create table: meta_object_attribute
   DO $$
   BEGIN
       IF NOT EXISTS (SELECT 1 FROM information_schema.tables WHERE table_name = 'meta_object_attribute' AND table_schema = 'public') THEN
           CREATE TABLE meta_object_attribute (
               id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
               meta_object_attribute_id VARCHAR(255) UNIQUE NOT NULL,
               data_type VARCHAR(50) NOT NULL,
               db_column_name VARCHAR(50) NOT NULL,
               meta_object_id UUID NOT NULL,
               description VARCHAR(255) NOT NULL,
               display_name VARCHAR(255) NOT NULL,
               is_mandatory BOOLEAN NOT NULL,
               is_primary BOOLEAN NOT NULL,
               is_system_gen BOOLEAN NOT NULL,
               seq_num INT NOT NULL,
               json_tag VARCHAR(50) NOT NULL,
               created_by VARCHAR(100) NOT NULL,
               created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
               updated_by VARCHAR(100),
               updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
               CONSTRAINT meta_object_attribute_meta_object_fkey FOREIGN KEY (meta_object_id) REFERENCES meta_object (id)
           );
       END IF;
   END $$;

   -- Create table: meta_object_relation
   DO $$
   BEGIN
       IF NOT EXISTS (SELECT 1 FROM information_schema.tables WHERE table_name = 'meta_object_relation' AND table_schema = 'public') THEN
           CREATE TABLE meta_object_relation (
               id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
               description VARCHAR(255) NOT NULL,
               meta_object_relation_id VARCHAR(255) UNIQUE NOT NULL,
               parent_object_id UUID NOT NULL,
               parent_obj_rel_col VARCHAR(50),
               child_object_id UUID NOT NULL,
               child_obj_rel_col VARCHAR(50),
               relation_type VARCHAR(50) NOT NULL,
               created_by VARCHAR(100) NOT NULL,
               created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
               updated_by VARCHAR(100),
               updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
               CONSTRAINT meta_object_relation_parent_object_fkey FOREIGN KEY (parent_object_id) REFERENCES meta_object (id),
               CONSTRAINT meta_object_relation_child_object_fkey FOREIGN KEY (child_object_id) REFERENCES meta_object (id)
           );
       END IF;
   END $$;



