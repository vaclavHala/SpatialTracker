FROM postgres
ENV POSTGRES_USER=redmine
# Dumped from live postgres under redmine
# pg_dump --clean --create --column-inserts -Uredmine  redmine > redmine.sql
# docker cp pg-red:/redmine.sql redmine.sql
ADD redmine.sql /docker-entrypoint-initdb.d