-- ==============================================================================
-- Flyway Migration V1: Initial Schema
-- Construction Management System
-- ==============================================================================

-- ------------------------------------------------------------------------------
-- 1. Global Tables: users, sites, units, categories, vendors
-- ------------------------------------------------------------------------------

CREATE TABLE IF NOT EXISTS users (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    phone VARCHAR(20),
    role VARCHAR(50) DEFAULT 'USER' NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS sites (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    address TEXT,
    city VARCHAR(100),
    state VARCHAR(100),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS units (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    code VARCHAR(20) NOT NULL UNIQUE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS categories (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    description TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL
);

-- Seed Categories
INSERT INTO categories (name, description) VALUES
    ('Plumber', 'Plumbing installation, repair, and sanitary fittings'),
    ('Electrician', 'Electrical wiring, paneling, and electrical fixtures'),
    ('Painter', 'Interior/exterior wall painting, priming, and finishing'),
    ('POP', 'Plaster of Paris, false ceiling, and decorative plaster work'),
    ('Construction Worker', 'General masonry, structural, and construction labor')
ON CONFLICT (name) DO NOTHING;

CREATE TABLE IF NOT EXISTS vendors (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    contact_person VARCHAR(255),
    phone VARCHAR(20),
    email VARCHAR(255),
    address TEXT,
    gst_no VARCHAR(50),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL
);

-- ------------------------------------------------------------------------------
-- 2. Business Tables (Site-Scoped): materials, work_items, workers, attendance, payments
-- ------------------------------------------------------------------------------

CREATE TABLE IF NOT EXISTS materials (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    site_id BIGINT NOT NULL REFERENCES sites(id) ON DELETE CASCADE,
    material_name VARCHAR(255) NOT NULL,
    quantity NUMERIC(14, 3) NOT NULL,
    unit_id BIGINT NOT NULL REFERENCES units(id),
    price NUMERIC(14, 2) NOT NULL,
    vendor_id BIGINT NOT NULL REFERENCES vendors(id),
    transport_vehicle_no VARCHAR(50),
    payment_status VARCHAR(20) NOT NULL, -- PENDING, DONE
    payment_mode VARCHAR(20) NOT NULL,   -- CASH, ONLINE
    entry_date DATE NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS work_items (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    site_id BIGINT NOT NULL REFERENCES sites(id) ON DELETE CASCADE,
    item_name VARCHAR(255) NOT NULL,
    quantity NUMERIC(14, 3) NOT NULL,
    price NUMERIC(14, 2) NOT NULL,
    transport_vehicle_no VARCHAR(50),
    vendor_id BIGINT NOT NULL REFERENCES vendors(id),
    entry_date DATE NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS workers (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    site_id BIGINT NOT NULL REFERENCES sites(id) ON DELETE CASCADE,
    full_name VARCHAR(255) NOT NULL,
    age INTEGER NOT NULL,
    address TEXT,
    phone VARCHAR(20),
    city VARCHAR(100),
    state VARCHAR(100),
    category_id BIGINT NOT NULL REFERENCES categories(id),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS attendance (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    site_id BIGINT NOT NULL REFERENCES sites(id) ON DELETE CASCADE,
    worker_id BIGINT NOT NULL REFERENCES workers(id) ON DELETE CASCADE,
    date DATE NOT NULL,
    status VARCHAR(20) NOT NULL,   -- PRESENT, ABSENT
    day_type VARCHAR(20) NOT NULL, -- FULL, HALF
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT uq_site_worker_date UNIQUE (site_id, worker_id, date)
);

CREATE TABLE IF NOT EXISTS payments (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    site_id BIGINT NOT NULL REFERENCES sites(id) ON DELETE CASCADE,
    category_id BIGINT NOT NULL REFERENCES categories(id),
    worker_id BIGINT NOT NULL REFERENCES workers(id) ON DELETE CASCADE,
    amount NUMERIC(14, 2) NOT NULL,
    status VARCHAR(20) NOT NULL, -- PENDING, DONE
    entry_date DATE NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL
);

-- ------------------------------------------------------------------------------
-- 3. Indexes for Foreign Keys & Frequent Queries
-- ------------------------------------------------------------------------------

CREATE INDEX IF NOT EXISTS idx_materials_site_id ON materials(site_id);
CREATE INDEX IF NOT EXISTS idx_materials_vendor_id ON materials(vendor_id);
CREATE INDEX IF NOT EXISTS idx_materials_unit_id ON materials(unit_id);

CREATE INDEX IF NOT EXISTS idx_work_items_site_id ON work_items(site_id);
CREATE INDEX IF NOT EXISTS idx_work_items_vendor_id ON work_items(vendor_id);

CREATE INDEX IF NOT EXISTS idx_workers_site_id ON workers(site_id);
CREATE INDEX IF NOT EXISTS idx_workers_category_id ON workers(category_id);

CREATE INDEX IF NOT EXISTS idx_attendance_site_id ON attendance(site_id);
CREATE INDEX IF NOT EXISTS idx_attendance_worker_id ON attendance(worker_id);
CREATE INDEX IF NOT EXISTS idx_attendance_date ON attendance(date);

CREATE INDEX IF NOT EXISTS idx_payments_site_id ON payments(site_id);
CREATE INDEX IF NOT EXISTS idx_payments_worker_id ON payments(worker_id);
CREATE INDEX IF NOT EXISTS idx_payments_category_id ON payments(category_id);
