-- Smart Logistics Database Setup
-- Run: mysql -u root -p < database.sql

CREATE DATABASE IF NOT EXISTS smart_logistics;
USE smart_logistics;

CREATE TABLE IF NOT EXISTS shipments (
    id          VARCHAR(20)    PRIMARY KEY,
    origin      VARCHAR(100)   NOT NULL,
    destination VARCHAR(100)   NOT NULL,
    cargo       VARCHAR(100)   NOT NULL,
    weight      DECIMAL(10,2)  NOT NULL,
    status      ENUM('Pending','In Transit','Delivered','Delayed') DEFAULT 'Pending',
    eta         VARCHAR(20),
    carrier     VARCHAR(100),
    created_at  VARCHAR(50)
);

-- Sample data
INSERT INTO shipments VALUES
  ('SHP-1001','Mumbai','Delhi','Electronics',420.00,'In Transit','2025-06-12','BlueDart','2025-06-08 09:14'),
  ('SHP-1002','Chennai','Bangalore','Pharmaceuticals',180.00,'Delivered','2025-06-09','DTDC','2025-06-07 14:32'),
  ('SHP-1003','Kolkata','Hyderabad','Textiles',650.00,'Pending','2025-06-15','Delhivery','2025-06-08 11:05'),
  ('SHP-1004','Pune','Ahmedabad','Auto Parts',900.00,'Delayed','2025-06-13','Gati KWE','2025-06-06 08:00'),
  ('SHP-1005','Jaipur','Surat','FMCG Goods',300.00,'In Transit','2025-06-11','Ecom Express','2025-06-09 07:45');
