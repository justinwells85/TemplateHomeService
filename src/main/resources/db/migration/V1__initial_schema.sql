-- Initial schema creation for template-home-service

CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    first_name VARCHAR(100),
    last_name VARCHAR(100),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    version BIGINT NOT NULL DEFAULT 0
);

CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_email ON users(email);

-- Add a comment to the table
COMMENT ON TABLE users IS 'Sample users table for the template service';
COMMENT ON COLUMN users.id IS 'Primary key';
COMMENT ON COLUMN users.username IS 'Unique username';
COMMENT ON COLUMN users.email IS 'User email address';
COMMENT ON COLUMN users.version IS 'Optimistic locking version';
