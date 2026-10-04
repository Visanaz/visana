# VISANA-SEC-EXC-KC-PCRE2-20261001

| Campo | Registro |
|---|---|
| Estado | `APPROVED_TEMPORARY_DEV_ONLY` |
| Responsable y aprobador | Cristian; declaración directa del responsable, 2026-10-01 |
| Sistema y alcance | Keycloak DEV únicamente, versión 26.8.0 |
| Base inmutable | `quay.io/keycloak/keycloak:26.8.0@sha256:b0f60d489d51c5d113390bdf5461d4c06e6051be026c05549f2e1e10ec352bcc` |
| Paquetes afectados | `pcre2` / `pcre2-syntax` 10.40-6.el9 |
| Vigencia máxima | 2026-10-15 16:33 America/Bogota; termina antes si se cumple una condición de revocación |
| Producción | `NOT_APPROVED` |

## CVE y evidencia

- CVE-2026-103111
- CVE-2026-86145
- CVE-2026-89161

[KC-00H, run 36921804827](https://github.com/Visanaz/visana/actions/runs/36921804827): build 26.8.0, 6 HIGH, 0 CRITICAL, 0 secretos y sin CVE nuevas respecto de 26.7.5; CVE-2025-59250 quedó fuera. Compatibilidad aislada con PostgreSQL 18.3 y contrato backend PASS.

[KC-00I, run 36927032965](https://github.com/Visanaz/visana/actions/runs/36927032965), artefacto `kc00i-pcre2-reachability.json` SHA-256 `45665d819abdaa00964740df89301894fabe0dc883a3700f092eb23c15d75a4f`: PCRE2 no apareció cargada en los procesos observados; Java y sus bibliotecas nativas cargadas no dependían de PCRE2; no se identificaron consumidores de las cuatro APIs objetivo ni ruta VISANA de regex PCRE2 nativa controlada por entrada no confiable. `RUNTIME_REACHABILITY=NOT_DEMONSTRATED` bajo los flujos ejercitados. Prueba aislada con PostgreSQL 18.3 y contrato backend: 1 test, 0 fallos, 0 errores, 0 omitidos.

En KC-00I, Red Hat clasificó los tres CVE como `Affected` para RHEL 9 sin paquete corregido ni advisory acreditado. `NOT_DEMONSTRATED` no significa `NOT_AFFECTED`, imposibilidad de explotación ni ausencia de toda vía futura. La decisión es una excepción de alcance DEV, no una supresión de hallazgos ni una autorización de producción.

## Controles durante la vigencia

- Fijar la imagen por digest inmutable; repetir el scan antes de publicación o promoción y conservar los hallazgos de Trivy.
- No agregar extensiones ni providers nativos no revisados, ni admitir regex PCRE2 nativa procedente de entrada no confiable.
- No exponer shell ni SSH al público; mantener el modelo runtime DEV evaluado.
- No introducir `.trivyignore`, allowlist Gitleaks, waiver genérico ni cambios manuales de paquetes o dependencias para eludir el scan.
- Adoptar de inmediato el fix del proveedor mediante una operación posterior autorizada.

`EXCEPTION_STATUS=INVALIDATED` si Red Hat publica un paquete RHEL9/UBI corregido; Keycloak publica imagen oficial con el fix; surge evidencia nueva de explotabilidad o alcanzabilidad; se agrega extensión/provider nativo; aparece una ruta regex PCRE2 no confiable; cambia el modelo runtime relevante; o la imagen deja de conservar 0 CRITICAL y 0 secretos. También expira automáticamente en la fecha máxima indicada.

La imagen Keycloak todavía **no está publicada** en Artifact Registry ni desplegada en Cloud Run. La publicación requiere KC-00R2 separado; esta excepción no autoriza KC-01 ni siguientes.

## Seguimiento separado de costos

Keycloak 26.8.0 persiste fallos de login en base de datos por defecto. `COST_IMPACT=NOT_QUANTIFIED` y `PRODUCTION_FOLLOWUP_REQUIRED=YES`. Esta excepción no cambia ese comportamiento ni cuantifica el costo cloud.
