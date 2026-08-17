-- E-Fashion Website Database Schema
CREATE DATABASE IF NOT EXISTS efashion;
USE efashion;

-- 1. Categories Table
CREATE TABLE IF NOT EXISTS categories (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
);

-- 2. Products Table
CREATE TABLE IF NOT EXISTS products (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    price DECIMAL(10, 2) NOT NULL,
    stock_quantity INT NOT NULL,
    category_id INT,
    image_url VARCHAR(255),
    FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE SET NULL
);

-- 3. Users Table
CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) DEFAULT 'USER'
);

-- 4. Cart Items Table
CREATE TABLE IF NOT EXISTS cart_items (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    product_id INT NOT NULL,
    quantity INT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE
);

-- 5. Orders Table
CREATE TABLE IF NOT EXISTS orders (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    order_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    total_amount DECIMAL(10, 2) NOT NULL,
    status VARCHAR(50) DEFAULT 'PENDING',
    shipping_address TEXT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- 6. Order Items Table
CREATE TABLE IF NOT EXISTS order_items (
    id INT AUTO_INCREMENT PRIMARY KEY,
    order_id INT NOT NULL,
    product_id INT NOT NULL,
    quantity INT NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE
);

-- Seed Categories
INSERT INTO categories (id, name) VALUES 
(1, 'T-Shirts'),
(2, 'Jeans'),
(3, 'Shirts'),
(4, 'Dresses'),
(5, 'Jackets'),
(6, 'Shoes'),
(7, 'Accessories')
ON DUPLICATE KEY UPDATE name=name;

-- Seed Products
INSERT INTO products (name, description, price, stock_quantity, category_id, image_url) VALUES
-- T-Shirts
('Classic White Tee', '100% premium cotton crew neck t-shirt. Breathable and perfect for daily wear.', 19.99, 50, 1, 'https://images.unsplash.com/photo-1521572267360-ee0c2909d518?w=500&auto=format&fit=crop&q=60'),
('Vintage Graphic Tee', 'Retro style graphic t-shirt made with washed-out soft fabric.', 24.99, 30, 1, 'https://images.unsplash.com/photo-1503342217505-b0a15ec3261c?w=500&auto=format&fit=crop&q=60'),

-- Jeans
('Slim Fit Blue Jeans', 'Classic blue denim with light fading and comfortable stretch material.', 49.99, 40, 2, 'https://images.unsplash.com/photo-1542272604-787c3835535d?w=500&auto=format&fit=crop&q=60'),
('Black High-Waist Jeans', 'Sleek black high-waisted denim jeans. Figure-hugging and stylish.', 54.99, 35, 2, 'https://images.unsplash.com/photo-1541099649105-f69ad21f3246?w=500&auto=format&fit=crop&q=60'),

-- Shirts
('Casual Linen Shirt', 'Lightweight, breathable linen button-down shirt. Ideal for warm weather.', 39.99, 25, 3, 'https://images.unsplash.com/photo-1596755094514-f87e34085b2c?w=500&auto=format&fit=crop&q=60'),
('Classic Oxford Shirt', 'Smart casual long-sleeve oxford cotton shirt in light blue.', 44.99, 30, 3, 'https://images.unsplash.com/photo-1603252109303-2751441dd157?w=500&auto=format&fit=crop&q=60'),

-- Dresses
('Floral Summer Dress', 'Flowy floral printed dress with an adjustable waist tie.', 59.99, 20, 4, 'https://images.unsplash.com/photo-1595777457583-95e059d581b8?w=500&auto=format&fit=crop&q=60'),
('Elegant Evening Gown', 'A gorgeous sleek satin evening dress in deep emerald green.', 89.99, 15, 4, 'https://images.unsplash.com/photo-1566174053879-31528523f8ae?w=500&auto=format&fit=crop&q=60'),

-- Jackets
('Classic Denim Jacket', 'Rugged blue denim jacket with metallic button closures.', 69.99, 25, 5, 'https://images.unsplash.com/photo-1576995853123-5a10305d93c0?w=500&auto=format&fit=crop&q=60'),
('Leather Biker Jacket', 'Premium faux-leather jacket featuring asymmetrical zippers.', 79.99, 20, 5, 'https://images.unsplash.com/photo-1551028719-00167b16eac5?w=500&auto=format&fit=crop&q=60'),

-- Shoes
('White Canvas Sneakers', 'Minimalist white canvas sneakers with comfortable vulcanized rubber soles.', 45.00, 30, 6, 'https://images.unsplash.com/photo-1549298916-b41d501d3772?w=500&auto=format&fit=crop&q=60'),
('Classic Leather Loafers', 'Sophisticated slip-on leather loafers in dark brown.', 85.00, 15, 6, 'https://images.unsplash.com/photo-1533867617858-e7b97e060509?w=500&auto=format&fit=crop&q=60'),

-- Accessories
('Polarized Sunglasses', 'Classic aviator style polarized sunglasses with UV400 protection.', 15.99, 50, 7, 'https://images.unsplash.com/photo-1572635196237-14b3f281503f?w=500&auto=format&fit=crop&q=60'),
('Minimalist Leather Watch', 'Elegant watch with a brown leather strap and simple white dial.', 95.00, 20, 7, 'https://images.unsplash.com/photo-1524592094714-0f0654e20314?w=500&auto=format&fit=crop&q=60');

-- Seed Admin and Test User (Password: admin123, user123)
-- These BCrypt hashes are pre-generated.
-- admin123 -> $2a$10$tZ/n0Q4pMv7zU93k7C5bSuGk/n5iPZ7i1z28v7lA4pQy1h.n/rTOW
-- user123  -> $2a$10$wU057h58ZqS72F6fLzN6duF7h3lR3/p2D.lH4n57tXh8GvQzK1H.C
INSERT INTO users (id, name, email, password, role) VALUES
(1, 'E-Fashion Admin', 'admin@efashion.com', '$2a$10$tZ/n0Q4pMv7zU93k7C5bSuGk/n5iPZ7i1z28v7lA4pQy1h.n/rTOW', 'ADMIN'),
(2, 'John Doe', 'john@gmail.com', '$2a$10$wU057h58ZqS72F6fLzN6duF7h3lR3/p2D.lH4n57tXh8GvQzK1H.C', 'USER')
ON DUPLICATE KEY UPDATE email=email;
