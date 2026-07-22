# Accesibilidad del frontend

Fecha de actualización: 2026-07-22

## Objetivo y estado de conformidad

El objetivo obligatorio del backoffice es cumplir **WCAG 2.2 nivel AA** en todas las páginas, estados, formularios, tablas y diálogos, tanto en tema claro como oscuro y en sus variantes responsive.

Esta declaración es un **objetivo de conformidad**, no una certificación del frontend completo. WCAG exige evaluar páginas completas y todas sus variantes responsive. La conformidad global solo podrá declararse cuando cada ruta haya superado la definición de terminado de este documento.

Referencia normativa:

- [Web Content Accessibility Guidelines (WCAG) 2.2](https://www.w3.org/TR/WCAG22/).
- [Novedades de WCAG 2.2](https://www.w3.org/WAI/standards-guidelines/wcag/new-in-22/).

## Alcance

El alcance incluye:

- Rutas públicas de autenticación.
- Shell, cabecera, navegación lateral y selector de tema.
- Dashboard y todas las rutas editoriales y ADMIN.
- Tablas, filtros, paginadores, formularios, editores y diálogos.
- Estados de carga, vacío, éxito, advertencia y error.
- Tema claro y oscuro.
- Viewports móvil, tablet, portátil, escritorio y ultrawide.
- Flujos completos operables con teclado y tecnologías de asistencia.

## Criterios de aceptación WCAG 2.2 AA

### Perceptible

- La estructura usa HTML semántico, landmarks, encabezados jerárquicos, listas, tablas, `fieldset` y `legend` cuando corresponda (`1.3.1`).
- El color nunca es el único medio para comunicar estado, prioridad, selección o error (`1.4.1`).
- El texto normal mantiene contraste mínimo `4.5:1`; texto grande, `3:1` (`1.4.3`).
- Componentes, bordes necesarios, estados y foco mantienen contraste no textual mínimo `3:1` (`1.4.11`).
- El contenido admite zoom de texto al `200 %` sin perder información ni funcionalidad (`1.4.4`).
- A `320 CSS px` o zoom equivalente al `400 %` no aparece scroll horizontal global. Las tablas de datos complejas pueden tener scroll local contenido y con acceso por teclado (`1.4.10`).
- Los ajustes de espaciado de texto no recortan ni solapan contenido (`1.4.12`).
- El contenido mostrado por hover o foco puede mantenerse, descartarse y alcanzarse cuando aplique (`1.4.13`).

### Operable

- Toda funcionalidad está disponible con teclado, sin trampas de foco (`2.1.1`, `2.1.2`).
- Existe un enlace para saltar al contenido principal (`2.4.1`).
- El título del documento identifica la aplicación o la ruta activa (`2.4.2`).
- El orden de foco conserva el orden visual y funcional (`2.4.3`).
- Enlaces y botones tienen un propósito comprensible en contexto; las acciones repetidas incluyen la entidad afectada en su nombre accesible (`2.4.4`).
- Encabezados y etiquetas describen su propósito (`2.4.6`).
- El foco es visible en todos los controles y no queda completamente oculto por cabeceras, overlays o contenido fijo (`2.4.7`, `2.4.11`).
- No se introduce funcionalidad basada solo en arrastre; siempre existe una alternativa de clic o teclado (`2.5.7`).
- Los objetivos de puntero miden al menos `24 × 24 CSS px` o cumplen una excepción normativa. Como criterio de diseño se priorizan `40 × 40 CSS px` en acciones (`2.5.8`).
- El texto visible de un control forma parte de su nombre accesible (`2.5.3`).

### Comprensible

- El idioma principal del documento es español (`3.1.1`).
- Foco y cambio de valor no provocan cambios de contexto inesperados (`3.2.1`, `3.2.2`).
- Navegación, iconografía y orden de acciones son consistentes entre páginas (`3.2.3`, `3.2.4`).
- Todos los campos tienen etiqueta visible o asociación equivalente; no se depende solo del placeholder (`3.3.2`).
- Los errores identifican el campo o la operación, explican el problema y, cuando es posible, sugieren cómo resolverlo (`3.3.1`, `3.3.3`).
- Los flujos no solicitan de nuevo información ya proporcionada en el mismo proceso salvo excepción justificada (`3.3.7`).
- La autenticación permite gestores de contraseñas, pegado y mecanismos sin pruebas cognitivas innecesarias (`3.3.8`).

### Robusto

- Los controles exponen nombre, rol, valor y estado correctos (`4.1.2`).
- Los mensajes de carga, éxito y error relevantes se anuncian sin mover el foco de forma inesperada (`4.1.3`).
- Se prioriza HTML semántico. ARIA solo complementa aquello que el elemento nativo o PrimeNG no comunica.
- Los componentes PrimeNG se verifican en el DOM renderizado; usar una librería accesible no demuestra por sí solo la conformidad de la página.

## Reglas por tipo de componente

### Navegación y shell

- Mantener `Saltar al contenido principal` y `main#main-content` enfocable.
- La navegación principal debe tener nombre accesible y estado de ruta actual perceptible.
- Ninguna cabecera sticky puede ocultar por completo el elemento enfocado.
- Los controles solo con icono requieren nombre accesible y objetivo táctil suficiente.

### Formularios y filtros

- Usar `label` asociado mediante `for/id` o la API accesible equivalente del componente PrimeNG.
- Usar `fieldset/legend` para grupos de radio o checkbox relacionados.
- Asociar ayuda y errores con `aria-describedby` cuando proceda.
- No deshabilitar el zoom ni el pegado.
- Mantener un orden de tabulación natural; no usar `tabindex` positivo.

### Tablas

- Usar `app-standard-table` para mantener scroll local, carga, vacío y paginación coherentes.
- Las cabeceras de datos usan `th` y `scope="col"`.
- La columna activa de ordenación expone `aria-sort`; el botón anuncia la siguiente dirección.
- Los filtros de columna tienen nombre accesible específico.
- Las acciones repetidas incluyen el identificador o título del registro en el nombre accesible.
- El paginador es una región de navegación con nombre y anuncia el cambio de página.

### Diálogos

- El diálogo tiene nombre accesible, cierre etiquetado y descripción comprensible.
- Al abrirse, el foco entra en una acción segura y queda contenido.
- `Escape` cierra el diálogo cuando no existe una razón funcional para impedirlo.
- Al cerrarse, el foco vuelve al control que lo abrió.
- Las acciones destructivas requieren confirmación explícita y no se distinguen solo por color.

### Estados y mensajes

- Carga y actualización usan `role="status"` o región viva equivalente.
- Los errores bloqueantes usan semántica de alerta.
- Skeletons e iconos decorativos se ocultan del árbol accesible.
- Los estados vacío y error ofrecen una explicación y una acción de recuperación cuando exista.

## Método de verificación

Cada página se valida en cuatro capas:

1. **Revisión de código:** semántica, labels, orden DOM, nombres de controles, estados y ausencia de ARIA redundante.
2. **Tests Angular:** comportamiento y atributos accesibles que formen parte del contrato del componente.
3. **Playwright mockeado:** flujo por roles y locators accesibles, teclado, diálogos, reflow y ausencia de overflow global.
4. **Revisión manual:** recorrido completo con teclado, zoom `200 %` y `400 %`, temas claro/oscuro, contraste y lector de pantalla antes de declarar conformidad.

Viewports mínimos de revisión:

```text
320 × 800
390 × 844
768 × 1024
1366 × 768
1440 × 900
1920 × 1080
```

La automatización reduce regresiones, pero no sustituye las comprobaciones manuales de contraste, foco, significado, orden de lectura y lector de pantalla.

## Definición de terminado de una página

Una página solo se considera conforme al objetivo WCAG 2.2 AA cuando:

- [ ] Cumple todos los criterios A y AA aplicables, no solo los nuevos de WCAG 2.2.
- [ ] Funciona íntegramente con teclado y no presenta trampas de foco.
- [ ] Supera contraste de texto, componentes, estados y foco en ambos temas.
- [ ] Conserva contenido y funcionalidad con zoom y espaciado de texto.
- [ ] No tiene overflow horizontal global en los viewports definidos.
- [ ] Sus tablas y diálogos cumplen las reglas anteriores.
- [ ] Sus estados de carga, error, éxito y vacío son perceptibles.
- [ ] Supera tests Angular, Playwright mockeado y build de producción.
- [ ] Se ha realizado revisión manual documentada.
- [ ] No contiene mojibake ni texto truncado que impida comprender una acción.

## Estado del piloto `/events`

La ruta `/events` es el piloto del nuevo sistema visual. Su alcance cubre cabecera, métricas, búsqueda global, filtros, tabla, ordenación, paginación, estados, herramienta de fusión y diálogo de confirmación.

Mejoras incorporadas en el piloto:

- Jerarquía semántica y visual centrada en eventos.
- Nombre accesible visible para búsqueda y filtros.
- `aria-sort` y dirección siguiente anunciable en todas las columnas ordenables.
- Acciones de fila con el identificador del evento en su nombre accesible.
- Grupos de fusión estructurados con `fieldset/legend`.
- Resumen de selección anunciado y acción bloqueada hasta completar los datos.
- Diálogo etiquetado, con foco contenido, cierre accesible y acción segura priorizada.
- Objetivos táctiles ampliados y scroll horizontal limitado a la tabla.
- Eliminación del mojibake y de estilos de modal heredados en la página.
- Respeto de `prefers-reduced-motion` en animaciones no esenciales.

Evidencia técnica cerrada el 2026-07-22:

- `npm run build`: compilación de producción correcta, sin advertencias de presupuesto.
- Suite Angular completa: `158 SUCCESS`.
- Suite Playwright mockeada completa: `10 passed`.
- Suite Playwright específica del piloto tras el refuerzo de accesibilidad: `4 passed`.
- Reflow sin overflow horizontal global verificado a `390` y `320 CSS px`; la tabla mantiene su scroll local.
- Flujo de fusión verificado con teclado, foco inicial seguro, contención del foco y cierre con `Escape`.
- Retorno del foco al control disparador verificado al cerrar la confirmación.
- Objetivos visibles del contenido principal verificados con tamaño mínimo de `24 CSS px`.
- Idioma español y título específico de la ruta verificados en el documento renderizado.
- Espaciado de texto WCAG `1.4.12` verificado sin recorte de controles textuales ni overflow horizontal global.
- Contrastes calculados en los tokens principales: texto del hero `8.71:1` o superior; texto normal `16.27:1` o superior; texto secundario `6.33:1` o superior; acento `7.58:1` o superior; borde de controles `3.73:1` o superior.
- Evidencia visual revisada en escritorio claro, escritorio oscuro y móvil.

La conformidad del resto de rutas continúa pendiente hasta extender y verificar el sistema visual aprobado. También queda pendiente la validación humana de esta dirección visual y, para el cierre global, el recorrido manual con lector de pantalla y zoom del frontend completo.

## Estado transversal conocido

Ya existen en el frontend:

- Enlace global para saltar al contenido.
- `main#main-content` enfocable.
- Navegación principal con nombre accesible.
- Selector de tema con estado y nombre contextual.
- Foco visible global.
- Componentes compartidos PrimeNG para mensajes, botones, selects, tablas, tags y diálogos.

Pendientes globales:

- Auditar y modernizar todas las rutas restantes con esta definición de terminado.
- Completar revisión manual con lector de pantalla.
- Verificar de forma sistemática contraste real de ambos temas.
- Incorporar una herramienta automática especializada solo si se aprueba como dependencia del proyecto.
