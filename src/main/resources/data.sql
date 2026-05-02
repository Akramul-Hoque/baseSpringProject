-- Insert default roles
INSERT INTO roles (name, description, created_at, updated_at, created_by, updated_by) VALUES 
('ADMIN', 'Administrator with full access', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system'),
('USER', 'Regular user with limited access', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system'),
('MANAGER', 'Manager with intermediate access', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system');

-- Insert default permissions
INSERT INTO permissions (name, description, created_at, updated_at, created_by, updated_by) VALUES 
('READ_USERS', 'Read user information', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system'),
('WRITE_USERS', 'Create and update users', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system'),
('DELETE_USERS', 'Delete users', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system'),
('READ_ROLES', 'Read role information', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system'),
('WRITE_ROLES', 'Create and update roles', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system'),
('DELETE_ROLES', 'Delete roles', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system'),
('ADMIN_PANEL', 'Access admin panel', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system');

-- Insert sample admin user (password: admin123)
INSERT INTO users (username, email, password, account_non_expired, account_non_locked, credentials_non_expired, enabled, created_at, updated_at) VALUES 
('admin', 'admin@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', true, true, true, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Assign admin role to admin user
INSERT INTO user_roles (user_id, role_id) VALUES 
(1, 1);
