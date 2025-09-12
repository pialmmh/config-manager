-- Create stellar_test database
CREATE DATABASE IF NOT EXISTS stellar_test;
USE stellar_test;

-- Drop existing tables if they exist
DROP TABLE IF EXISTS invoice;
DROP TABLE IF EXISTS orders;
DROP TABLE IF EXISTS person;

-- Create person table
CREATE TABLE person (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create orders table
CREATE TABLE orders (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    idPerson BIGINT NOT NULL,
    total DECIMAL(10, 2),
    status VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (idPerson) REFERENCES person(id) ON DELETE CASCADE
);

-- Create invoice table
CREATE TABLE invoice (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    idOrder BIGINT NOT NULL,
    status VARCHAR(50),
    amount DECIMAL(10, 2),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (idOrder) REFERENCES orders(id) ON DELETE CASCADE
);

-- Insert sample persons
INSERT INTO person (name, email) VALUES 
    ('Ali Ahmed', 'ali.ahmed@example.com'),
    ('Ali Hassan', 'ali.hassan@example.com'),
    ('Sara Ali', 'sara.ali@example.com'),
    ('John Doe', 'john.doe@example.com'),
    ('Jane Smith', 'jane.smith@example.com'),
    ('Bob Johnson', 'bob.johnson@example.com'),
    ('Alice Brown', 'alice.brown@example.com'),
    ('Charlie Wilson', 'charlie.wilson@example.com'),
    ('Diana Prince', 'diana.prince@example.com'),
    ('Eve Davis', 'eve.davis@example.com');

-- Insert sample orders
INSERT INTO orders (idPerson, total, status) VALUES 
    (1, 150.00, 'completed'),
    (1, 250.50, 'completed'),
    (1, 75.25, 'pending'),
    (2, 320.00, 'completed'),
    (2, 180.75, 'completed'),
    (3, 90.00, 'completed'),
    (3, 410.25, 'processing'),
    (4, 225.50, 'completed'),
    (4, 150.00, 'completed'),
    (5, 500.00, 'completed'),
    (5, 125.75, 'pending'),
    (6, 300.00, 'completed'),
    (7, 175.25, 'completed'),
    (8, 220.00, 'processing'),
    (9, 180.50, 'completed'),
    (10, 95.75, 'completed');

-- Insert sample invoices
INSERT INTO invoice (idOrder, status, amount) VALUES 
    (1, 'paid', 150.00),
    (2, 'paid', 250.50),
    (2, 'paid', 0.00),  -- Additional invoice for same order
    (3, 'pending', 75.25),
    (4, 'paid', 320.00),
    (5, 'paid', 180.75),
    (6, 'paid', 90.00),
    (7, 'pending', 410.25),
    (8, 'paid', 225.50),
    (8, 'paid', 0.00),  -- Additional invoice
    (9, 'paid', 150.00),
    (10, 'paid', 500.00),
    (10, 'refunded', -50.00),  -- Refund invoice
    (11, 'pending', 125.75),
    (12, 'paid', 300.00),
    (13, 'paid', 175.25),
    (14, 'processing', 220.00),
    (15, 'paid', 180.50),
    (16, 'paid', 95.75);

-- Create indexes for better performance
CREATE INDEX idx_orders_person ON orders(idPerson);
CREATE INDEX idx_invoice_order ON invoice(idOrder);
CREATE INDEX idx_invoice_status ON invoice(status);
CREATE INDEX idx_person_name ON person(name);

-- Show summary
SELECT 'Database setup complete!' AS message;
SELECT COUNT(*) as person_count FROM person;
SELECT COUNT(*) as order_count FROM orders;
SELECT COUNT(*) as invoice_count FROM invoice;