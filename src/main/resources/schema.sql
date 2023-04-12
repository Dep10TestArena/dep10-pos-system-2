DROP TABLE IF EXISTS User;

CREATE TABLE IF NOT EXISTS User(
       username VARCHAR(50) PRIMARY KEY,
       full_name VARCHAR(100) NOT NULL,
       password VARCHAR(300) NOT NULL,
       role ENUM ('ADMIN','USER') NOT NULL
);

