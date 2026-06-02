ALEMBIC = uv run alembic -c backend/alembic.ini

revision:
	$(ALEMBIC) revision --autogenerate -m "$(m)"

upgrade:
	$(ALEMBIC) upgrade head

run:
	uv run python -m backend.src.main
	
build:
	docker compose -f docker-compose-prod.yml build

rebuild:
	docker compose -f docker-compose-prod.yml build --no-cache

up:
	docker compose -f docker-compose-prod.yml up
	
down:
	docker compose -f docker-compose-prod.yml down
	
down-v:
	docker compose -f docker-compose-prod.yml down -v
