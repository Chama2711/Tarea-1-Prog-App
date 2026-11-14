#!/bin/sh
set -eu

cd "$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)"
"${MVN:-./mvnw}" --batch-mode --no-transfer-progress clean verify "$@"

mkdir -p target/distribucion
cp estacion-trabajo/target/servidor.jar target/distribucion/servidor.jar
cp servidor-web/target/web.war target/distribucion/web.war
cp -R configuracion-ejemplo target/distribucion/
cp -R migraciones target/distribucion/
cp README.md target/distribucion/LEEME.md
printf '\nListo: target/distribucion/servidor.jar y target/distribucion/web.war\n'
