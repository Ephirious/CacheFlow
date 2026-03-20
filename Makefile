ALEMBIC = uv run alembic -c backend/alembic.ini

revision:
	$(ALEMBIC) revision --autogenerate -m "$(m)"

upgrade:
	$(ALEMBIC) upgrade head

run:
	uv run python -m backend.src.main