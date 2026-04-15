package com.smartlogistics.servlet;

import com.smartlogistics.dao.ShipmentDAO;
import com.smartlogistics.model.Shipment;
import com.smartlogistics.ml.MLService;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@WebServlet(name = "ForecastServlet", urlPatterns = {"/api/ml/predict"})
public class ForecastServlet extends BaseApiServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            List<Shipment> shipments = ShipmentDAO.getAllShipments();
            Map<String, Object> prediction = MLService.predictDemand(shipments.size());
            writeJson(response, HttpServletResponse.SC_OK, prediction);
        } catch (SQLException e) {
            writeError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Unable to generate forecast: " + e.getMessage());
        }
    }
}
