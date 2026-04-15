# Smart Logistics Management System (SMLOG-JAVA)

Smart Logistics Management System is a Java-based logistics tracking project that combines:

- Java servlet-backed shipment CRUD APIs
- MySQL persistence using JDBC
- A browser dashboard built with HTML, CSS, and JavaScript
- A JavaFX desktop client
- Python-based demand prediction and anomaly detection

## Team

- Sujal Purbey (`25MCA1035`) — frontend, web dashboard, and JavaFX interface
- Sudhanshu Ranjan (`25MCA1053`) — machine learning logic and analytics integration
- Prachyat Misra (`25MCA1063`) — backend, overall integration, submission packaging

## Project Structure

- `backend/` — servlet runtime, DAO layer, model classes, servlet mappings, and SQL resources
- `web/` — browser dashboard
- `javafx-frontend/` — desktop client
- `ml/` — Python analytics module
- `report-assets/` — screenshots and report media

## Key Features

- Shipment create, read, update, and delete operations
- Live shipment monitoring through a browser dashboard
- Desktop access through JavaFX
- Demand prediction for upcoming shipment volume
- Delayed-shipment anomaly detection
- Academic report generation with screenshots and source-code appendix

## Running the Project

### Backend and Dashboard

```bash
./start-project.sh
```

This starts:

- Backend API on `http://localhost:8080`
- Dashboard on `http://localhost:8000`

### Stop Services

```bash
./stop-project.sh
```

## Main Endpoints

- `GET /api/shipments`
- `GET /api/shipments/{id}`
- `POST /api/shipments`
- `PUT /api/shipments/{id}`
- `DELETE /api/shipments/{id}`
- `GET /api/ml/predict`
- `GET /api/ml/detect-anomalies`

## Submission Files

- `SMART_LOGISTICS_MANAGEMENT_SYSTEM_FINAL_SUBMISSION.pdf`
- `SMART_LOGISTICS_MANAGEMENT_SYSTEM_FINAL_SUBMISSION.docx`
- `generate_final_submission_report.py`
