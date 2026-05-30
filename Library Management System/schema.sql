-- Create Database
CREATE DATABASE IF NOT EXISTS library_db;
USE library_db;

-- Table: authors
CREATE TABLE IF NOT EXISTS authors (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE
);

-- Table: books
CREATE TABLE IF NOT EXISTS books (
    id INT AUTO_INCREMENT PRIMARY KEY,
    isbn VARCHAR(20) UNIQUE NOT NULL,
    title VARCHAR(255) NOT NULL,
    author_id INT NOT NULL,
    published_year INT,
    FOREIGN KEY (author_id) REFERENCES authors(id)
);

-- Table: users
CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    join_date DATE NOT NULL
);

-- Table: borrowed_books
CREATE TABLE IF NOT EXISTS borrowed_books (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    book_id INT NOT NULL,
    borrow_date DATE NOT NULL,
    due_date DATE NOT NULL,
    return_date DATE,
    fine_amount DECIMAL(10, 2) DEFAULT 0.00,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (book_id) REFERENCES books(id)
);

-- Insert sample data
INSERT IGNORE INTO authors (name) VALUES ('J.K. Rowling'), ('George R.R. Martin'), ('J.R.R. Tolkien');
INSERT IGNORE INTO books (isbn, title, author_id, published_year) VALUES 
('9780747532699', 'Harry Potter and the Philosopher''s Stone', 1, 1997),
('9780553103540', 'A Game of Thrones', 2, 1996),
('9780618640157', 'The Lord of the Rings', 3, 1954);

INSERT IGNORE INTO users (name, email, join_date) VALUES 
('Alice Smith', 'alice@example.com', CURDATE()),
('Bob Jones', 'bob@example.com', CURDATE());
