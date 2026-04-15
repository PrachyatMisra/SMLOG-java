#!/bin/bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "$0")" && pwd)"
RUN_DIR="$ROOT_DIR/run"
LOG_DIR="$ROOT_DIR/logs"

mkdir -p "$RUN_DIR" "$LOG_DIR"

start_backend() {
    if [[ -f "$RUN_DIR/backend.pid" ]] && kill -0 "$(cat "$RUN_DIR/backend.pid")" 2>/dev/null; then
        echo "Backend is already running on PID $(cat "$RUN_DIR/backend.pid")"
        return
    fi

    echo "Building backend..."
    (cd "$ROOT_DIR/backend" && mvn -q -DskipTests package)

    echo "Starting backend on http://localhost:8080 ..."
    nohup bash -lc "cd '$ROOT_DIR/backend' && exec java -jar target/SmartLogistics.jar" \
        > "$LOG_DIR/backend.log" 2>&1 &
    echo $! > "$RUN_DIR/backend.pid"

    for _ in {1..20}; do
        if curl -sf http://localhost:8080/api/shipments >/dev/null 2>&1; then
            echo "Backend is ready."
            return
        fi
        if ! kill -0 "$(cat "$RUN_DIR/backend.pid")" 2>/dev/null; then
            echo "Backend process exited unexpectedly. Check $LOG_DIR/backend.log"
            exit 1
        fi
        sleep 1
    done

    echo "Backend did not become ready. Check $LOG_DIR/backend.log"
    exit 1
}

start_web() {
    if [[ -f "$RUN_DIR/web.pid" ]] && kill -0 "$(cat "$RUN_DIR/web.pid")" 2>/dev/null; then
        echo "Web server is already running on PID $(cat "$RUN_DIR/web.pid")"
        return
    fi

    echo "Starting dashboard on http://localhost:8000 ..."
    nohup bash -lc "cd '$ROOT_DIR/web' && exec python3 -m http.server 8000" \
        > "$LOG_DIR/web.log" 2>&1 &
    echo $! > "$RUN_DIR/web.pid"
}

start_backend
start_web

echo
echo "Smart Logistics is running:"
echo "  Dashboard: http://localhost:8000"
echo "  Servlet API: http://localhost:8080/api/shipments"
echo "  Forecast API: http://localhost:8080/api/ml/predict"
echo "  Anomaly API: http://localhost:8080/api/ml/detect-anomalies"
