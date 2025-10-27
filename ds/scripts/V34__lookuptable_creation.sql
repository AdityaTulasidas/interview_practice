DO $$
BEGIN


   IF NOT EXISTS (SELECT 1 FROM information_schema.tables WHERE table_schema = 'public' AND table_name = 'onesource_region') THEN
      CREATE TABLE onesource_region (
         id SERIAL PRIMARY KEY,
         system_name VARCHAR(255) NOT NULL,
         display_name VARCHAR(255) NOT NULL,
         description TEXT,
         created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
         created_by VARCHAR(100) NOT NULL,
         updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
         updated_by VARCHAR(100) NOT NULL
      );
   END IF;

   IF NOT EXISTS (SELECT 1 FROM information_schema.tables WHERE table_schema = 'public' AND table_name = 'domain') THEN
         CREATE TABLE domain (
            id SERIAL PRIMARY KEY,
            name VARCHAR(255) NOT NULL,
            type_id INT NOT NULL,
            system_name VARCHAR(255) NOT NULL,
            is_system BOOLEAN DEFAULT FALSE,
            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
            created_by VARCHAR(100) NOT NULL,
            updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
            updated_by VARCHAR(100) NOT NULL
         );
      END IF;

      IF NOT EXISTS (SELECT 1 FROM information_schema.tables WHERE table_schema = 'public' AND table_name = 'domain_type') THEN
            CREATE TABLE domain_type (
               id SERIAL PRIMARY KEY,
               name VARCHAR(255) NOT NULL,
               system_name VARCHAR(255) NOT NULL,
               created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
               created_by VARCHAR(100) NOT NULL,
               updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
               updated_by VARCHAR(100) NOT NULL
            );
         END IF;

         IF NOT EXISTS (SELECT 1 FROM information_schema.tables WHERE table_schema = 'public' AND table_name = 'domain_object') THEN
                     CREATE TABLE domain_object (
                        id SERIAL PRIMARY KEY,
                        system_name VARCHAR(255) NOT NULL,
                        object_name VARCHAR(255) NOT NULL,
                        domain_sys_name VARCHAR(255) NOT NULL,
                        description TEXT,
                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                        created_by VARCHAR(100) NOT NULL,
                        updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                        updated_by VARCHAR(100) NOT NULL
                     );
                  END IF;
END $$;