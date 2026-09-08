#!/usr/bin/env bash
# Starts the backend (Spring Boot, :8080) and frontend (Vite, :5173) together.
# Ctrl+C stops both.
set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
BACKEND_DIR="$SCRIPT_DIR/backend"
FRONTEND_DIR="$SCRIPT_DIR/frontend/poll-project"

if [ ! -d "$FRONTEND_DIR" ]; then
  echo "Frontend directory not found: $FRONTEND_DIR" >&2
  exit 1
fi

free_port() {
  local port="$1"
  local port_pids
  port_pids="$(lsof -tiTCP:"$port" -sTCP:LISTEN 2>/dev/null || true)"
  if [ -n "$port_pids" ]; then
    echo "Port $port is in use, stopping existing process(es): $port_pids"
    kill $port_pids 2>/dev/null || true
    sleep 1
    port_pids="$(lsof -tiTCP:"$port" -sTCP:LISTEN 2>/dev/null || true)"
    [ -n "$port_pids" ] && kill -9 $port_pids 2>/dev/null || true
  fi
}

free_port 8080
free_port 5173

pids=()
cleanup() {
  echo
  echo "Stopping..."
  for pid in "${pids[@]}"; do
    kill "$pid" 2>/dev/null || true
  done
  wait 2>/dev/null || true
}
trap cleanup EXIT INT TERM

echo "Starting backend on :8080..."
(cd "$BACKEND_DIR" && ./mvnw -q spring-boot:run) &
pids+=($!)

echo "Starting frontend on :5173..."
(cd "$FRONTEND_DIR" && npm run dev) &
pids+=($!)

wait
