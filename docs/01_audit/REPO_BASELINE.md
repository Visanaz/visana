# Repository Baseline - Plan 3

## Identidad Git previa a esta documentacion

| Campo | Valor observado |
|---|---|
| Repositorio | `C:\visana_auditoria\visana` |
| HEAD | `5f241fe0d1a47cfc783f866408219da38e085ab2` |
| Commit resumido | `5f241fe feat: add application configuration for database, JPA, and Keycloak integration` |
| Rama | `main` |
| Remoto | `origin` -> `https://github.com/eduarxdogar/visana.git` (fetch/push) |
| Ramas remotas observadas | `origin/main`, `origin/HEAD -> origin/main` |
| Archivos versionados | 298 |
| Estado previo | Sin cambios trackeados reportados; un no trackeado: `VISANA SAS - Plan de Compensacion-3.pdf` |

No se ejecutaron `reset`, `clean`, `checkout`, `restore`, `pull`, `rebase`, `commit` ni `push`.

## Estructura relevante observada

```text
src/main/java/com/visana/erp/     Aplicacion Java por dominios
src/main/resources/              Configuracion y migracion Flyway
src/test/java/                   19 clases de prueba
src/test/resources/              Schema y propiedades de prueba
target/                          Artefactos y reportes versionados
pom.xml                          Maven / dependencias
docker-compose.yml               MySQL de desarrollo declarado
visanaco_produc.sql              Export SQL legado
visana-realm-export.json         Export de realm Keycloak
VISANA SAS - Plan...pdf          Plan documental no trackeado
```

No se localizo un arbol de frontend fuente (por ejemplo `package.json`, `src` web separado o recursos frontend propios) en este clon. Esto es un HECHO de inventario; no demuestra que no exista frontend en otro repositorio o ambiente.

## Historial visible

Los commits recientes muestran creacion incremental de dominios commerce, compensation, network, ledger, qualification, migracion, seguridad, migraciones y reportes Surefire. El historial es evidencia de cambios versionados; no es evidencia de despliegue ni de aprobacion funcional.

## Integridad de baseline

`git diff --check` y `git diff --numstat` no devolvieron diferencias trackeadas antes de crear los documentos autorizados. Los seis documentos bajo `docs/` constituyen el unico cambio intencional de esta entrega.

## Divergencia fuente - artefacto versionado

La inspeccion posterior localizo en `target/classes` artefactos compilados para `network/application/port/out/NetworkNodeRepository.class` y `network/infrastructure/adapter/out/persistence/NetworkNodePersistenceAdapter.class`. No existe el archivo fuente de la interfaz ni el adaptador correspondiente en el arbol fuente de `HEAD`; `git ls-tree -r HEAD` confirma los puertos de compensacion y el controller de red, pero no esos fuentes.

Esto es un HECHO de consistencia del repositorio. [CONTRADICCION]: el binario versionado conserva tipos que el fuente actual no entrega. No se ejecuto el JAR ni Maven, por lo que no se afirma cual artefacto arrancaria ni si se recompilaria correctamente. Los reportes Surefire y `target/` deben considerarse evidencia historica de artefactos, no validacion del fuente vigente.

## Comparacion estructural limitada con SRC-002

El registro historico de la auditoria anterior describe un sistema PHP/CodeIgniter con frontend tradicional y export MariaDB. SRC-001 contiene, en cambio, un backend Maven/Spring Boot Java 21 y no contiene aquel arbol PHP/CodeIgniter. Esta es una diferencia de estructura de fuentes, no una conclusion sobre version, calidad, produccion o equivalencia funcional.
