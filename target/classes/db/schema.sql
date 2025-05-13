CREATE TABLE IF NOT EXISTS books (
    id IDENTITY PRIMARY KEY,
    title VARCHAR(255),
    author VARCHAR(255),
    isbn VARCHAR(100) UNIQUE,
    reserved BOOLEAN
);

CREATE TABLE IF NOT EXISTS reservations (
    id IDENTITY PRIMARY KEY,
    book_id BIGINT,
    customer_name VARCHAR(255),
    due_date DATE,
    FOREIGN KEY (book_id) REFERENCES books(id)
);