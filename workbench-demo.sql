USE smart_logistics;
SELECT id, origin, destination, cargo, weight, status, eta, carrier, created_at
FROM shipments
WHERE id IN ('SHP-LIVE-2001', 'SHP-LIVE-2002')
ORDER BY id;
