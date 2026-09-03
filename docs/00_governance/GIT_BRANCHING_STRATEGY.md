# Estrategia de ramas Git — VISANA

## Roles de rama

| Rama | Propósito | Regla de cambio |
|---|---|---|
| `main` | Fuente de verdad de producción | Sólo promoción mediante PR desde `qa` |
| `qa` | Preproducción, QA y UAT | Sólo promoción mediante PR desde `dev` |
| `dev` | Integración de desarrollo | Sólo integra PR de ramas de trabajo |
| `feat/*`, `fix/*`, `refactor/*`, `chore/*`, `test/*`, `docs/*` | Trabajo aislado | Nacen de `dev` y regresan por PR a `dev` |

No se desarrolla directamente sobre `main`, `qa` ni `dev`. Una feature nunca se promueve directamente a `qa` o `main`, y `dev` no puede saltarse `qa` para llegar a `main`.

## Flujo normal

```text
feat/fix/refactor/chore/test/docs
            -> PR -> dev
            -> PR -> qa
            -> PR -> main
```

Cada promoción requiere CI verde. La promoción a `qa` requiere validación QA/UAT. La promoción a `main` requiere aprobación de producción y, cuando corresponda, un tag semántico. Ningún despliegue de producción proviene de `dev` o de una rama de trabajo.

## Hotfix excepcional

Un `hotfix/*` nace de `main` únicamente ante un incidente crítico y autorización expresa del PM. Su cambio se promueve por PR a `main` y luego se reconcilia obligatoriamente hacia `dev`. Esta excepción no autoriza el bypass de controles de CI.

## Protección operativa

Las ramas protegidas deben impedir force-push y borrado, exigir PR y CI, y exigir revisión cuando el plan de GitHub lo permita. Si la plataforma no ofrece una protección, la regla sigue siendo obligatoria de forma manual y debe registrarse como `BRANCH_PROTECTION_NOT_AVAILABLE`.

