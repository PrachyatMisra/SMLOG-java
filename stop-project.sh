#!/bin/bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "$0")" && pwd)"
RUN_DIR="$ROOT_DIR/run"

stop_pid_file() {
    local name="$1"
    local pid_file="$2"

    if [[ -f "$pid_file" ]]; then
        local pid
        pid="$(cat "$pid_file")"
        if kill -0 "$pid" 2>/dev/null; then
            echo "Stopping $name (PID $pid)..."
            kill "$pid"
        else
            echo "$name is not running."
        fi
        rm -f "$pid_file"
    else
        echo "No PID file found for $name."
    fi
}

stop_pid_file "backend" "$RUN_DIR/backend.pid"
stop_pid_file "web server" "$RUN_DIR/web.pid"
