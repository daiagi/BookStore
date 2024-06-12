-- populate_data.sql
-- Insert roles
INSERT INTO roles (role) VALUES ('admin'), ('user');

-- Insert permissions
INSERT INTO permissions (permission) VALUES 
('books.read'), 
('books.write'), 
('books.delete'), 
('books.*'), 
('users.read'), 
('users.write'), 
('users.delete'),
('users.*'),
('role.read'),
('role.write'),
('role.delete'),
('role.*'),
('permission.read'),
('permission.write'),
('permission.delete'),
('permission.*'),
('role_permission.read'),
('role_permission.write'),
('role_permission.delete'),
('role_permission.*'),
('inventory.read'),
('inventory.write'),
('inventory.delete'),
('inventory.*'),
('orders.read'),
('orders.write'),
('orders.delete'),
('orders.*'),
('order_items.read'),
('order_items.write'),
('order_items.delete'),
('order_items.*'),
('*');

-- Assign permissions to roles
INSERT INTO role_permissions (role, permission) VALUES 
('admin', '*'),
('user', 'books.read'),
('user', 'orders.*'),
('user', 'order_items.*');

-- -- Create an admin user
-- INSERT INTO users (username, password, role, created_at) VALUES 
-- ('admin', 'admin_password_hash', 'admin', CURRENT_TIMESTAMP);
