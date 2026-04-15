package com.smartlogistics.dao;

import com.smartlogistics.model.Shipment;
import java.sql.*;
import java.util.*;

/**
 * Shipment Data Access Object (DAO)
 * Raw JDBC implementation for CRUD operations
 */
public class ShipmentDAO {

    /**
     * Get all shipments
     */
    public static List<Shipment> getAllShipments() throws SQLException {
        List<Shipment> shipments = new ArrayList<>();
        String query = "SELECT * FROM shipments";
        
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
                Shipment ship = new Shipment(
                    rs.getString("id"),
                    rs.getString("origin"),
                    rs.getString("destination"),
                    rs.getString("cargo"),
                    rs.getDouble("weight"),
                    rs.getString("status"),
                    rs.getString("eta"),
                    rs.getString("carrier"),
                    rs.getString("created_at")
                );
                shipments.add(ship);
            }
        }
        return shipments;
    }

    /**
     * Get shipment by ID
     */
    public static Shipment getShipmentById(String id) throws SQLException {
        String query = "SELECT * FROM shipments WHERE id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Shipment(
                        rs.getString("id"),
                        rs.getString("origin"),
                        rs.getString("destination"),
                        rs.getString("cargo"),
                        rs.getDouble("weight"),
                        rs.getString("status"),
                        rs.getString("eta"),
                        rs.getString("carrier"),
                        rs.getString("created_at")
                    );
                }
            }
        }
        return null;
    }

    /**
     * Insert new shipment (CREATE)
     */
    public static boolean insertShipment(Shipment shipment) throws SQLException {
        String query = "INSERT INTO shipments (id, origin, destination, cargo, weight, status, eta, carrier, created_at) " +
                       "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, shipment.getId());
            stmt.setString(2, shipment.getOrigin());
            stmt.setString(3, shipment.getDestination());
            stmt.setString(4, shipment.getCargo());
            stmt.setDouble(5, shipment.getWeight());
            stmt.setString(6, shipment.getStatus());
            stmt.setString(7, shipment.getEta());
            stmt.setString(8, shipment.getCarrier());
            stmt.setString(9, shipment.getCreatedAt());
            
            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Update shipment by ID (UPDATE)
     */
    public static boolean updateShipment(String id, Shipment shipment) throws SQLException {
        String query = "UPDATE shipments SET origin=?, destination=?, cargo=?, weight=?, status=?, eta=?, carrier=? WHERE id=?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, shipment.getOrigin());
            stmt.setString(2, shipment.getDestination());
            stmt.setString(3, shipment.getCargo());
            stmt.setDouble(4, shipment.getWeight());
            stmt.setString(5, shipment.getStatus());
            stmt.setString(6, shipment.getEta());
            stmt.setString(7, shipment.getCarrier());
            stmt.setString(8, id);
            
            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Delete shipment by ID (DELETE)
     */
    public static boolean deleteShipment(String id) throws SQLException {
        String query = "DELETE FROM shipments WHERE id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, id);
            return stmt.executeUpdate() > 0;
        }
    }
}
