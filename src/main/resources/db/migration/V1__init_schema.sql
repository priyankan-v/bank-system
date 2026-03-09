CREATE DATABASE IF NOT EXISTS bank_system;
USE bank_system;

CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    account_number VARCHAR(8) UNIQUE NOT NULL,
    pin_hash VARCHAR(255) NOT NULL,
    active BOOLEAN DEFAULT TRUE,
    balance DECIMAL(15,2) NOT NULL DEFAULT 0.00,
    failed_attempts INT DEFAULT 0,
    locked BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE admins (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    username VARCHAR(6) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    active BOOLEAN DEFAULT TRUE,
    role ENUM('ADMIN', 'SUPER_ADMIN') NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE transactions (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    account_number VARCHAR(8) NOT NULL,
    type ENUM('DEPOSIT', 'WITHDRAW', 'ADMIN_DEPOSIT', 'ADMIN_WITHDRAW') NOT NULL,
    amount DECIMAL(15,2),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (account_number) REFERENCES users(account_number)
);

CREATE TABLE audit_logs (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    account_number VARCHAR(8),
    admin_id VARCHAR(6),
    event VARCHAR(50) NOT NULL,
    description VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (account_number) REFERENCES users(account_number),
    FOREIGN KEY (admin_id) REFERENCES admins(username)
);

CREATE TABLE admin_logs (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    performedId VARCHAR(6),
    targetId VARCHAR(6),
    event VARCHAR(50) NOT NULL,
    description VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (targetId) REFERENCES admins(username),
    FOREIGN KEY (performedId) REFERENCES admins(username)
);