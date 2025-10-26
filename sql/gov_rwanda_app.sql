CREATE DATABASE IF NOT EXISTS gov_rwanda_app;
USE gov_rwanda_app;

-- Users table
CREATE TABLE IF NOT EXISTS users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    role VARCHAR(20) NOT NULL
);

-- Vehicles table
CREATE TABLE IF NOT EXISTS vehicles (
    vehicle_id INT AUTO_INCREMENT PRIMARY KEY,
    owner_id INT NOT NULL,
    plate_no VARCHAR(20) NOT NULL UNIQUE,
    vehicle_type VARCHAR(50) NOT NULL,
    fuel_type VARCHAR(20) NOT NULL,
    manufacture_year INT NOT NULL,
    mileage INT NOT NULL,
    FOREIGN KEY (owner_id) REFERENCES users(user_id)
);

-- Exchange offers table
CREATE TABLE IF NOT EXISTS exchange_offers (
    offer_id INT AUTO_INCREMENT PRIMARY KEY,
    vehicle_id INT NOT NULL,
    exchange_value DOUBLE NOT NULL,
    subsidy_percent DOUBLE NOT NULL,
    status VARCHAR(20) DEFAULT 'pending',
    FOREIGN KEY (vehicle_id) REFERENCES vehicles(vehicle_id)
);
