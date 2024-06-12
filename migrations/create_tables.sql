-- create_tables.sql
CREATE TABLE users (
  id SERIAL PRIMARY KEY,
  email VARCHAR(255) NOT NULL UNIQUE,
  password_hash VARCHAR(255) NOT NULL,
  role VARCHAR(255) NOT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE permissions (
  id SERIAL PRIMARY KEY,
  permission VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE roles (
  id SERIAL PRIMARY KEY,
  role VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE role_permissions (
  role VARCHAR(255) NOT NULL,
  permission VARCHAR(255) NOT NULL,
  PRIMARY KEY (role, permission),
  FOREIGN KEY (role) REFERENCES roles(role),
  FOREIGN KEY (permission) REFERENCES permissions(permission)
);

CREATE TABLE books (
  id SERIAL PRIMARY KEY,
  title VARCHAR(255) NOT NULL,
  author VARCHAR(255) NOT NULL,
  description TEXT,
  price FLOAT NOT NULL,
  category VARCHAR(255)
);

CREATE TABLE inventory (
  id SERIAL PRIMARY KEY,
  book_id INTEGER NOT NULL,
  stock INTEGER NOT NULL,
  FOREIGN KEY (book_id) REFERENCES books(id)
);

CREATE TABLE orders (
  id SERIAL PRIMARY KEY,
  user_id INTEGER NOT NULL,
  status VARCHAR(255) NOT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  total_price FLOAT NOT NULL,
  FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE order_items (
  order_id INTEGER NOT NULL,
  book_id INTEGER NOT NULL,
  amount INTEGER NOT NULL,
  PRIMARY KEY (order_id, book_id),
  FOREIGN KEY (order_id) REFERENCES orders(id),
  FOREIGN KEY (book_id) REFERENCES books(id)
);
