package com.smartlogistics.servlet;

import com.smartlogistics.dao.ShipmentDAO;
import com.smartlogistics.model.Shipment;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@WebServlet(name = "ShipmentServlet", urlPatterns = {"/api/shipments/*"})
public class ShipmentServlet extends BaseApiServlet {

    private static final DateTimeFormatter CREATED_AT_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            String shipmentId = extractShipmentId(request);
            if (shipmentId == null) {
                writeJson(response, HttpServletResponse.SC_OK, ShipmentDAO.getAllShipments());
                return;
            }

            Shipment shipment = ShipmentDAO.getShipmentById(shipmentId);
            if (shipment == null) {
                writeError(response, HttpServletResponse.SC_NOT_FOUND, "Shipment not found: " + shipmentId);
                return;
            }

            writeJson(response, HttpServletResponse.SC_OK, shipment);
        } catch (SQLException e) {
            writeError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Unable to fetch shipment data: " + e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            Shipment shipment = GSON.fromJson(readRequestBody(request), Shipment.class);
            if (!isValidShipmentForCreate(shipment)) {
                writeError(response, HttpServletResponse.SC_BAD_REQUEST, "Shipment payload is incomplete");
                return;
            }

            if (shipment.getCreatedAt() == null || shipment.getCreatedAt().isBlank()) {
                shipment.setCreatedAt(LocalDateTime.now().format(CREATED_AT_FORMATTER));
            }

            ShipmentDAO.insertShipment(shipment);
            writeJson(response, HttpServletResponse.SC_CREATED, shipment);
        } catch (SQLIntegrityConstraintViolationException e) {
            writeError(response, HttpServletResponse.SC_CONFLICT, "Shipment ID already exists");
        } catch (SQLException e) {
            writeError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Unable to create shipment: " + e.getMessage());
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String shipmentId = extractShipmentId(request);
        if (shipmentId == null) {
            writeError(response, HttpServletResponse.SC_BAD_REQUEST, "Shipment ID is required for update");
            return;
        }

        try {
            Shipment shipment = GSON.fromJson(readRequestBody(request), Shipment.class);
            if (!isValidShipmentForUpdate(shipment)) {
                writeError(response, HttpServletResponse.SC_BAD_REQUEST, "Shipment payload is incomplete");
                return;
            }

            shipment.setId(shipmentId);
            boolean updated = ShipmentDAO.updateShipment(shipmentId, shipment);
            if (!updated) {
                writeError(response, HttpServletResponse.SC_NOT_FOUND, "Shipment not found: " + shipmentId);
                return;
            }

            Shipment refreshed = ShipmentDAO.getShipmentById(shipmentId);
            writeJson(response, HttpServletResponse.SC_OK, refreshed);
        } catch (SQLException e) {
            writeError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Unable to update shipment: " + e.getMessage());
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String shipmentId = extractShipmentId(request);
        if (shipmentId == null) {
            writeError(response, HttpServletResponse.SC_BAD_REQUEST, "Shipment ID is required for delete");
            return;
        }

        try {
            boolean deleted = ShipmentDAO.deleteShipment(shipmentId);
            if (!deleted) {
                writeError(response, HttpServletResponse.SC_NOT_FOUND, "Shipment not found: " + shipmentId);
                return;
            }

            writeJson(response, HttpServletResponse.SC_OK, java.util.Map.of(
                "message", "Shipment deleted successfully",
                "id", shipmentId
            ));
        } catch (SQLException e) {
            writeError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Unable to delete shipment: " + e.getMessage());
        }
    }

    private String extractShipmentId(HttpServletRequest request) {
        String pathInfo = request.getPathInfo();
        if (pathInfo == null || pathInfo.isBlank() || "/".equals(pathInfo)) {
            return null;
        }

        String normalized = pathInfo.startsWith("/") ? pathInfo.substring(1) : pathInfo;
        return normalized.isBlank() ? null : normalized;
    }

    private boolean isValidShipmentForCreate(Shipment shipment) {
        return shipment != null
            && isPresent(shipment.getId())
            && isPresent(shipment.getOrigin())
            && isPresent(shipment.getDestination())
            && isPresent(shipment.getCargo())
            && shipment.getWeight() > 0
            && isPresent(shipment.getStatus());
    }

    private boolean isValidShipmentForUpdate(Shipment shipment) {
        return shipment != null
            && isPresent(shipment.getOrigin())
            && isPresent(shipment.getDestination())
            && isPresent(shipment.getCargo())
            && shipment.getWeight() > 0
            && isPresent(shipment.getStatus());
    }

    private boolean isPresent(String value) {
        return value != null && !value.isBlank();
    }
}
