# Smart Logistics Demo Guide

## Quick Start

Run the complete project:

```bash
./start-project.sh
```

Stop the project:

```bash
./stop-project.sh
```

## URLs

- Dashboard: `http://localhost:8000`
- Shipments API: `http://localhost:8080/api/shipments`
- Forecast API: `http://localhost:8080/api/ml/predict`
- Anomaly API: `http://localhost:8080/api/ml/detect-anomalies`

## What Is Running

Backend:
- Spring Boot embedded Tomcat
- Java Servlets
- MySQL JDBC integration
- Python ML bridge

Frontend:
- HTML, CSS, Vanilla JavaScript dashboard
- JavaFX desktop client

## Servlet Files

- `backend/src/main/java/com/smartlogistics/servlet/BaseApiServlet.java`
- `backend/src/main/java/com/smartlogistics/servlet/ShipmentServlet.java`
- `backend/src/main/java/com/smartlogistics/servlet/ForecastServlet.java`
- `backend/src/main/java/com/smartlogistics/servlet/AnomalyServlet.java`
- `backend/src/main/webapp/WEB-INF/web.xml`

## Demo Flow

1. Open `http://localhost:8000`
2. Show the dashboard header and system information
3. Show the shipment list loaded from the servlet backend
4. Use the Create Shipment form and save a new shipment
5. Edit any shipment and update it
6. Delete a shipment to show the full CRUD cycle
7. Click `Run Forecast` to show the prediction output
8. Click `Run Anomaly Scan` to show the anomaly output
9. Open `http://localhost:8080/api/shipments/SHP-1001` to show direct servlet JSON
10. Open `http://localhost:8080/api/ml/predict` and `http://localhost:8080/api/ml/detect-anomalies` to show the analytics endpoints

## JavaFX Run Command

```bash
cd javafx-frontend
mvn javafx:run
```

## Verification Commands

```bash
curl http://localhost:8080/api/shipments
curl http://localhost:8080/api/shipments/SHP-1001
curl http://localhost:8080/api/ml/predict
curl http://localhost:8080/api/ml/detect-anomalies
```

## Screenshot Assets

Generated screenshots are stored in `report-assets/`:

- `dashboard-full.png`
- `servlet-single-shipment.png`
- `servlet-forecast-endpoint.png`
- `servlet-anomalies-endpoint.png`
