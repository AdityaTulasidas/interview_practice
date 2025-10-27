-- Alter target_tenant_id column to allow NULL values
ALTER TABLE IF EXISTS public.target_regions
    ALTER COLUMN target_tenant_id DROP NOT NULL;

