-- ==============================================================================
-- Flyway Migration V1: Baseline Schema
-- Construction Management System
-- ==============================================================================

CREATE TABLE IF NOT EXISTS system_metadata (
    id VARCHAR(50) PRIMARY KEY,
    value VARCHAR(255) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO system_metadata (id, value)
VALUES ('schema_version', '1.0.0')
ON CONFLICT (id) DO NOTHING;
