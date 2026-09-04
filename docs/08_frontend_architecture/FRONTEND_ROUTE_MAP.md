# Frontend Route Map — conceptual

Las rutas son propuestas de información y lazy loading; no demuestran URLs legado ni se crean en código.

| Zona | Ruta propuesta | Feature | Guard / estado |
|---|---|---|---|
| Pública | `/` | contenido/marketing aprobado | pública; puede residir fuera de la SPA según FR-DG-02 |
| Pública | `/legal/:document` | contenido legal | pública sólo para documentos aprobados |
| Pública | `/login` | Auth | redirige si ya existe sesión válida |
| Técnica | `/auth/callback` | Auth | procesa Authorization Code + PKCE mediante adaptador |
| Técnica | `/unauthorized`, `/forbidden`, `/unlinked` | Auth | estados explícitos, sin exponer claims |
| Aplicación | `/app` | shell/dashboard | authenticated + linked identity; dashboard limitado a contenido aprobado |
| Aplicación | `/app/catalog` y `/app/catalog/:productId` | Catalog | authenticated + linked identity por contrato actual |
| Aplicación | `/app/orders`, `/app/orders/:orderId` | Orders | authenticated + linked identity; backend vuelve a verificar ownership |
| Aplicación | `/app/checkout` | Checkout placeholder | authenticated; bloquea confirmación productiva hasta DG-15 |
| Aplicación | `/app/network` | Network | authenticated + linked identity; datos mínimos |
| Aplicación | `/app/qualification`, `/app/rewards`, `/app/finance` | features futuras | feature-disabled / business gate, no simulación de reglas |
| Aplicación | `/app/profile` | Profile | authenticated; UI de perfiles condicionada a DG-17 |
| Administración | `/admin/...` | Admin | authenticated + capability aprobada; no se definen rutas hijas hasta role matrix |

## Guards

- **Authenticated:** delega en el adaptador OIDC; no inspecciona manualmente campos de token para decidir negocio.
- **Linked identity:** consulta estado de `/api/v1/me`; `UNLINKED_IDENTITY` dirige a `/unlinked`, sin crear enlace automático.
- **Required capability:** sólo controla navegación/visibilidad a partir de capacidades acordadas; una llamada denegada por backend prevalece y se presenta como forbidden.

Cada feature route se carga de forma lazy. Redirecciones de URLs legado y estrategia de coexistencia requieren inventario/decisión posterior; no se deducen desde el front controller ausente.
