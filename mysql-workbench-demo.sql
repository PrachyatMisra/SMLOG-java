-- Smart Logistics MySQL Workbench Demo
-- First run this whole file, or run one statement at a time.

USE smart_logistics;

SELECT DATABASE() AS current_database;

SHOW TABLES;

DESCRIBE shipments;

SELECT COUNT(*) AS shipment_count
FROM smart_logistics.shipments;

SELECT id, origin, destination, cargo, weight, status, eta, carrier, created_at
FROM smart_logistics.shipments
ORDER BY id
LIMIT 1000;

SELECT id, origin, destination, cargo, weight, status, eta, carrier, created_at
FROM smart_logistics.shipments
WHERE id = 'SHP-1001';

SELECT status, COUNT(*) AS total
FROM smart_logistics.shipments
GROUP BY status
ORDER BY total DESC;
