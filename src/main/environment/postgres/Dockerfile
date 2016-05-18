FROM postgres
ENV POSTGRES_USER=redmine
# Dumped from live postgres under redmine
# pg_dump redmine > redmine.sql
ADD redmine.sql /docker-entrypoint-initdb.d