-- Railway Booking System Database Schema

DROP DATABASE IF EXISTS railway_booking;
CREATE DATABASE railway_booking;
USE railway_booking;

-- Admin Users Table
CREATE TABLE admin_users (
    user_id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(50) NOT NULL,
    full_name VARCHAR(100),
    email VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Trains Table
CREATE TABLE trains (
    train_id INT PRIMARY KEY AUTO_INCREMENT,
    train_number VARCHAR(10) UNIQUE NOT NULL,
    train_name VARCHAR(100) NOT NULL,
    source_station VARCHAR(50) NOT NULL,
    destination_station VARCHAR(50) NOT NULL,
    departure_time TIME NOT NULL,
    arrival_time TIME NOT NULL,
    total_seats INT DEFAULT 200,
    available_seats INT DEFAULT 200,
    price DECIMAL(8,2) NOT NULL,
    status ENUM('active', 'inactive') DEFAULT 'active',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Passengers Table
CREATE TABLE passengers (
    passenger_id INT PRIMARY KEY AUTO_INCREMENT,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    age INT NOT NULL,
    gender ENUM('Male', 'Female', 'Other') NOT NULL,
    phone VARCHAR(15),
    email VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Bookings Table
CREATE TABLE bookings (
    booking_id INT PRIMARY KEY AUTO_INCREMENT,
    pnr VARCHAR(20) UNIQUE NOT NULL,
    train_id INT,
    passenger_id INT,
    booking_date DATE NOT NULL,
    journey_date DATE NOT NULL,
    fare DECIMAL(8,2) NOT NULL,
    status ENUM('confirmed', 'cancelled', 'waiting') DEFAULT 'confirmed',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (train_id) REFERENCES trains(train_id),
    FOREIGN KEY (passenger_id) REFERENCES passengers(passenger_id)
);

-- Insert Sample Admin Users
INSERT INTO admin_users (username, password, full_name, email) VALUES
('admin', 'admin', 'System Administrator', 'admin@railway.com'),
('user', 'user', 'Railway User', 'user@railway.com'),
('manager', 'manager123', 'Station Manager', 'manager@railway.com');

-- Insert Sample Trains
INSERT INTO trains (train_number, train_name, source_station, destination_station, departure_time, arrival_time, total_seats, available_seats, price, status) VALUES
-- Delhi Routes
('12001', 'Shatabdi Express', 'New Delhi', 'Mumbai Central', '06:00:00', '20:15:00', 300, 280, 2850.00, 'active'),
('12002', 'Mumbai Rajdhani', 'Mumbai Central', 'New Delhi', '16:55:00', '08:35:00', 350, 320, 3200.00, 'active'),
('12301', 'Howrah Rajdhani', 'New Delhi', 'Howrah', '16:55:00', '10:05:00', 400, 375, 2950.00, 'active'),
('12302', 'Howrah Rajdhani', 'Howrah', 'New Delhi', '14:15:00', '07:25:00', 400, 380, 2950.00, 'active'),
('12621', 'Tamil Nadu Express', 'New Delhi', 'Chennai Central', '22:30:00', '04:35:00', 350, 320, 1850.00, 'active'),
('12622', 'Tamil Nadu Express', 'Chennai Central', 'New Delhi', '22:45:00', '05:40:00', 350, 335, 1850.00, 'active'),

-- Mumbai Routes
('12137', 'Punjab Mail', 'Mumbai Central', 'Firozpur', '19:15:00', '19:20:00', 250, 230, 1650.00, 'active'),
('12138', 'Punjab Mail', 'Firozpur', 'Mumbai Central', '04:45:00', '04:50:00', 250, 245, 1650.00, 'active'),
('12262', 'Duronto Express', 'Mumbai Central', 'Howrah', '14:15:00', '18:30:00', 400, 385, 2150.00, 'active'),
('12261', 'Duronto Express', 'Howrah', 'Mumbai Central', '08:45:00', '13:00:00', 400, 390, 2150.00, 'active'),

-- Chennai Routes
('12601', 'Chennai Mail', 'Chennai Central', 'New Delhi', '14:05:00', '18:40:00', 300, 285, 1950.00, 'active'),
('12602', 'Chennai Mail', 'New Delhi', 'Chennai Central', '15:55:00', '20:30:00', 300, 275, 1950.00, 'active'),
('12840', 'Howrah Mail', 'Chennai Central', 'Howrah', '23:05:00', '22:45:00', 280, 265, 1450.00, 'active'),
('12841', 'Howrah Mail', 'Howrah', 'Chennai Central', '14:20:00', '14:00:00', 280, 270, 1450.00, 'active'),

-- Bangalore Routes
('12627', 'Karnataka Express', 'New Delhi', 'Bangalore City', '20:10:00', '04:20:00', 350, 325, 2250.00, 'active'),
('12628', 'Karnataka Express', 'Bangalore City', 'New Delhi', '21:40:00', '05:50:00', 350, 340, 2250.00, 'active'),
('12578', 'Bagmati Express', 'Mysore', 'Darbhanga', '22:00:00', '11:30:00', 200, 185, 1850.00, 'active'),
('12577', 'Bagmati Express', 'Darbhanga', 'Mysore', '05:50:00', '19:20:00', 200, 195, 1850.00, 'active'),

-- Hyderabad Routes
('12723', 'Telangana Express', 'Hyderabad', 'New Delhi', '17:40:00', '06:05:00', 300, 280, 2150.00, 'active'),
('12724', 'Telangana Express', 'New Delhi', 'Hyderabad', '21:25:00', '09:50:00', 300, 290, 2150.00, 'active'),
('17201', 'Golconda Express', 'Hyderabad', 'Mumbai Central', '15:20:00', '03:15:00', 250, 235, 1550.00, 'active'),
('17202', 'Golconda Express', 'Mumbai Central', 'Hyderabad', '11:40:00', '23:35:00', 250, 240, 1550.00, 'active'),

-- Kolkata Routes
('13006', 'Amritsar Mail', 'Howrah', 'Amritsar', '23:05:00', '06:50:00', 280, 265, 1750.00, 'active'),
('13005', 'Amritsar Mail', 'Amritsar', 'Howrah', '11:15:00', '19:00:00', 280, 270, 1750.00, 'active'),
('12274', 'Duronto Express', 'Howrah', 'New Delhi', '06:50:00', '17:55:00', 400, 375, 2850.00, 'active'),
('12273', 'Duronto Express', 'New Delhi', 'Howrah', '16:40:00', '03:45:00', 400, 385, 2850.00, 'active'),

-- Pune Routes
('12124', 'Deccan Queen', 'Mumbai CST', 'Pune', '17:10:00', '20:25:00', 150, 135, 450.00, 'active'),
('12123', 'Deccan Queen', 'Pune', 'Mumbai CST', '07:15:00', '10:30:00', 150, 140, 450.00, 'active'),
('11302', 'Udyan Express', 'Mumbai CST', 'Bangalore City', '08:05:00', '22:00:00', 200, 185, 1250.00, 'active'),
('11301', 'Udyan Express', 'Bangalore City', 'Mumbai CST', '20:30:00', '10:25:00', 200, 190, 1250.00, 'active'),

-- Ahmedabad Routes
('12917', 'Gujarat Sampark Kranti', 'Ahmedabad', 'New Delhi', '13:25:00', '05:15:00', 300, 285, 1950.00, 'active'),
('12918', 'Gujarat Sampark Kranti', 'New Delhi', 'Ahmedabad', '19:15:00', '11:05:00', 300, 290, 1950.00, 'active'),

-- Jaipur Routes
('12413', 'Poorna Express', 'Jaipur', 'Howrah', '11:30:00', '19:50:00', 250, 230, 1650.00, 'active'),
('12414', 'Poorna Express', 'Howrah', 'Jaipur', '06:00:00', '14:20:00', 250, 240, 1650.00, 'active');

-- Insert Sample Passengers
INSERT INTO passengers (first_name, last_name, age, gender, phone, email) VALUES
('Rajesh', 'Kumar', 35, 'Male', '9876543210', 'rajesh.kumar@email.com'),
('Priya', 'Sharma', 28, 'Female', '9876543211', 'priya.sharma@email.com'),
('Amit', 'Singh', 42, 'Male', '9876543212', 'amit.singh@email.com'),
('Sunita', 'Patel', 31, 'Female', '9876543213', 'sunita.patel@email.com'),
('Rahul', 'Gupta', 26, 'Male', '9876543214', 'rahul.gupta@email.com');

-- Insert Sample Bookings
INSERT INTO bookings (pnr, train_id, passenger_id, booking_date, journey_date, fare, status) VALUES
('PNR001', 1, 1, '2024-01-10', '2024-01-15', 2850.00, 'confirmed'),
('PNR002', 3, 2, '2024-01-11', '2024-01-16', 2950.00, 'confirmed'),
('PNR003', 5, 3, '2024-01-12', '2024-01-17', 1850.00, 'confirmed'),
('PNR004', 7, 4, '2024-01-13', '2024-01-18', 1650.00, 'confirmed'),
('PNR005', 9, 5, '2024-01-14', '2024-01-19', 2150.00, 'confirmed');

-- Create Indexes for better performance
CREATE INDEX idx_train_route ON trains(source_station, destination_station);
CREATE INDEX idx_booking_date ON bookings(booking_date);
CREATE INDEX idx_journey_date ON bookings(journey_date);
CREATE INDEX idx_pnr ON bookings(pnr);

COMMIT;