.EXPORT_ALL_VARIABLES:
include .env

repl:
		clj -M:repl

up:
		docker-compose up -d

down:
		docker-compose down

psql:
		psql -h $(POSTGRES_HOST) -p 5433 -U $(POSTGRES_USER) -d $(POSTGRES_DB)

run:
		clj -M:run
