#!/bin/bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"

echo "Starting Web Dashboard Server..."
echo "Open your browser: http://localhost:8000"
echo

cd "$SCRIPT_DIR"
python3 -m http.server 8000
