# Smart Logistics Management System (SMLOG-JAVA)

> Multi-Tier Enterprise Logistics Platform combining Java Servlets, JDBC Persistence, JavaFX Desktop Interface, Web Dashboard, and Python ML Analytics.

---

## 📦 Overview

**SMLOG-JAVA** is an end-to-end logistics tracking and shipment lifecycle management system. Designed to bridge enterprise backend architectures with predictive analytics, the platform coordinates shipment CRUD lifecycles, warehouse routing, automated transit anomaly detection, and predictive demand forecasting.

---

## 👥 Engineering Team

- **Prachyat Misra** (`25MCA1063`) — Backend Architecture, JDBC Data Layer, System Integration, Packaging
- **Sujal Purbey** (`25MCA1035`) — Frontend Web Dashboard, JavaFX Desktop Interface
- **Sudhanshu Ranjan** (`25MCA1053`) — Machine Learning Logic & Demand Forecasting Analytics

---

## 🌟 Architecture & Subsystems

```mermaid
flowchart TD
    subgraph Clients ["Presentation Layer"]
        A1[Web Browser Dashboard (Port 8000)]
        A2[JavaFX Desktop Application]
    end

    subgraph Backend ["Application Layer (Port 8080)"]
        B1[Servlet Controller Layer]
        B2[DAO & Business Logic Layer]
    end

    subgraph ML ["Analytics Layer (Python)"]
        C1[Demand Forecasting (Scikit-Learn)]
        C2[Delayed Shipment Anomaly Detection]
    end

    subgraph Storage ["Persistence Layer"]
        D[(MySQL Database / JDBC)]
    end

    A1 <-->|REST / JSON| B1
    A2 <-->|REST / JDBC| B1
    B1 <--> B2
    B2 <--> D
    B1 <-->|Subprocess / API| ML
```

---

## 📁 Repository Structure

```
SMLOG-java/
├── backend/                       # Java Servlets, Models, DAOs, and SQL scripts
├── web/                           # HTML5, CSS3, JavaScript browser dashboard
├── javafx-frontend/               # JavaFX desktop client application
├── ml/                            # Python predictive modeling and anomaly detection
├── report-assets/                 # Documentation media and interface screenshots
├── start-project.sh               # One-click startup script for backend & web
├── stop-project.sh                # Graceful service shutdown script
├── mysql-workbench-demo.sql       # Database schema initialization script
└── README.md
```

---

## 📋 Prerequisites

- **Java**: JDK 17 or higher
- **MySQL Server**: 8.0+
- **Python**: 3.9+ (with `pandas`, `scikit-learn`, `numpy`)
- **Maven**: 3.8+ (for backend compilation)

---

## 🚀 Quick Start

### 1. Database Initialization

Execute the schema script in your MySQL instance:

```bash
mysql -u root -p < mysql-workbench-demo.sql
```

### 2. Configure Environment

Copy `.env.example` to `.env` and verify database credentials:

```bash
cp .env.example .env
```

### 3. Launch Services

Start both the Backend API and Web Dashboard simultaneously:

```bash
chmod +x start-project.sh stop-project.sh
./start-project.sh
```

- **Backend API**: `http://localhost:8080`
- **Web Dashboard**: `http://localhost:8000`

To terminate running background services:

```bash
./stop-project.sh
```

---

## 🔌 API Endpoints

| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `GET` | `/api/shipments` | List all active shipments |
| `GET` | `/api/shipments/{id}` | Retrieve shipment details by tracking ID |
| `POST` | `/api/shipments` | Create and register a new shipment |
| `PUT` | `/api/shipments/{id}` | Update transit status, coordinates, or delivery date |
| `DELETE` | `/api/shipments/{id}` | Archive / delete shipment record |
| `GET` | `/api/ml/predict` | Generate 7-day shipment volume demand forecast |
| `GET` | `/api/ml/detect-anomalies` | Identify delayed transit routes and flagged carriers |

---

## 🔒 Security Practices

- **Parameterized Queries**: All database operations use JDBC `PreparedStatement` to prevent SQL injection vulnerabilities.
- **Credential Segregation**: Database connection strings and credentials must be supplied via environment variables.

---

## 📄 License

This project is licensed under the [MIT License](LICENSE).
