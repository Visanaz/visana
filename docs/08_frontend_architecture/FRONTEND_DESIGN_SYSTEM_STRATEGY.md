# Frontend Design-System and Asset Strategy

## Inventario AS-IS y disposición

| Elemento | Evidencia legado | Disposición |
|---|---|---|
| Logos, iconos, banners, imágenes producto | `img/`, `uploads/`, fuentes | `REUSE` sólo tras validación de licencia, propiedad, calidad y contenido. |
| Tipografía y colores | fuentes/CSS compilado disponibles | `PRESERVE_BRAND` como referencia; extraer tokens después de aprobación visual. |
| Botones, formularios, tablas, cards, alerts | CSS/JS heterogéneo, Bootstrap/jQuery/DataTables | `MODERNIZE`; definir componentes accesibles, no portar plugins. |
| Tailwind compilado | variables `--tw-*`, sin fuente/config | `DISCARD_AS_IMPLEMENTATION_DEPENDENCY`; evaluar tecnología de estilo separadamente. |
| HTML inyectado/plantillas JS | `innerHTML` legado | `DISCARD`; usar templates seguros y datos tipados. |
| Recursos de terceros/cargas remotas | CSS/worker/librerías | `VERIFY_LICENSE` y revisar CSP/ownership antes de incorporar. |

No se adoptan Angular Material, CDK, Tailwind ni otra librería UI en esta fase. La elección depende de accesibilidad, necesidades de tabla/formulario, branding, mantenibilidad y preferencia del equipo; FR-DG-03 permanece abierto.

## Tokens y patrones futuros

Un design system futuro debe definir tokens de color, tipografía, espaciado, elevación, breakpoints, foco y estados semánticos. Los componentes mínimos son botón, input/select, mensajes/alertas, modal, card, tabla responsive, paginación y empty/error state. No se crean CSS ni componentes aún.

## Responsive y accesibilidad

Los journeys priorizan desktop, tablet y móvil web responsive; no hay evidencia de app nativa. Baseline objetivo WCAG-aligned: HTML semántico, labels, navegación teclado, foco visible/gestionado, contraste validado, anuncios de error/estado y alternativas a visualizaciones complejas. Esto no declara certificación.
