USE smart_logistics;
SELECT id, origin, destination, cargo, weight, status, eta, carrier, created_at
FROM shipments
ORDER BY id;
