FROM postgres:9.6.3
MAINTAINER Adrien Cheype <adrien.cheype@ird.fr>

COPY ./docker-entrypoint-initdb.d/ /docker-entrypoint-initdb.d/
RUN chmod 0755 /docker-entrypoint-initdb.d/init-cantharella-db.sh