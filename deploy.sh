#!/usr/bin/env bash

set -euo pipefail

APP_DIR="/opt/myapp/CacheFlow"
COMPOSE_FILE="docker-compose-prod.yml"
ENV_FILE="backend/.env"
BACKUP_DIR="$APP_DIR/backups"

cd "$APP_DIR"

echo "==> Current directory:"
pwd

echo "==> Fetch latest code from main"
git fetch origin main
git reset --hard origin/main

echo "==> Create backup directory"
mkdir -p "$BACKUP_DIR"

echo "==> Backup PostgreSQL database"
BACKUP_FILE="$BACKUP_DIR/postgres_$(date +'%Y-%m-%d_%H-%M-%S').sql.gz"

if docker compose --env-file "$ENV_FILE" -f "$COMPOSE_FILE" ps db | grep -q "Up"; then
  docker compose --env-file "$ENV_FILE" -f "$COMPOSE_FILE" exec -T db \
    sh -c 'pg_dump -U "$POSTGRES_USER" "$POSTGRES_DB"' | gzip > "$BACKUP_FILE"

  echo "==> Backup saved to:"
  echo "$BACKUP_FILE"
else
  echo "==> DB container is not running, skipping backup"
fi

echo "==> Build new images"
make build

echo "==> Run migrations"
docker compose --env-file "$ENV_FILE" -f "$COMPOSE_FILE" run --rm migrations || {
  echo "==> Migrations failed"
  exit 1
}

echo "==> Start updated containers"
docker compose --env-file "$ENV_FILE" -f "$COMPOSE_FILE" up -d --remove-orphans

echo "==> Remove unused Docker images"
docker image prune -f

echo "==> Remove old backups, keep last 10"
ls -1t "$BACKUP_DIR"/postgres_*.sql.gz 2>/dev/null | tail -n +11 | xargs -r rm -f

echo "==> Deployment finished"
docker compose --env-file "$ENV_FILE" -f "$COMPOSE_FILE" ps
