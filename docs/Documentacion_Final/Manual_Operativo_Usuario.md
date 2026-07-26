# Manual operativo de usuario

**Aplicación:** Sindicato Intelligence
**Versión documental:** 2.0
**Actualizado:** 25/07/2026
**Ámbito:** MVP técnico ejecutable y backoffice actual

## 1. Propósito

Sindicato Intelligence centraliza el seguimiento de actualidad educativa andaluza. El sistema captura noticias, las clasifica, agrupa duplicados en eventos, genera análisis y prepara contenido para revisión y publicación.

La decisión editorial se toma sobre `Event`, no sobre noticias aisladas:

```text
News -> Event -> Analysis -> Content -> Publication
```

La IA propone y resume. Una persona revisa, edita, aprueba y decide la publicación.

## 2. Roles

### ADMIN

Puede:

- consultar noticias, eventos, contenido y publicaciones;
- ejecutar automatizaciones manualmente;
- generar, editar, aprobar, rechazar y publicar contenido;
- gestionar fuentes;
- gestionar usuarios;
- consultar auditoría;
- administrar métricas, prompts visibles, automatizaciones y Telegram en `/settings`.

### EDITOR

Puede:

- consultar noticias y eventos;
- ejecutar automatizaciones autorizadas;
- generar, editar, aprobar o rechazar contenido;
- publicar contenido aprobado;
- consultar el histórico de publicaciones.

No puede gestionar usuarios, fuentes administrativas, auditoría global ni configuración.

La autorización efectiva se aplica en el backend; ocultar una opción en la interfaz no sustituye los controles de seguridad.

## 3. Acceso y sesión

### Inicio de sesión

1. Abre la URL del backoffice.
2. Introduce email y contraseña.
3. Pulsa `Entrar`.

El backend devuelve un access token y un refresh token. Si la sesión expira, el frontend intenta renovarla mediante el refresh token.

### Contraseña temporal

Los usuarios creados por un administrador reciben una contraseña temporal por email. En el primer acceso deben establecer una contraseña definitiva antes de usar el resto de la aplicación.

### Recuperación

1. Pulsa `Olvidé mi password`.
2. Introduce el email.
3. Abre el mensaje recibido en MailHog o en el servicio de correo configurado.
4. Usa el enlace de recuperación antes de su caducidad.

### Cierre de sesión

Pulsa `Salir` en la cabecera. La sesión local se elimina y se registra la acción.

## 4. Navegación

Rutas operativas:

| Ruta | Función | Acceso |
| --- | --- | --- |
| `/login` | autenticación | pública |
| `/dashboard` | resumen operativo | ADMIN, EDITOR |
| `/news` | noticias capturadas | ADMIN, EDITOR |
| `/events` | eventos consolidados | ADMIN, EDITOR |
| `/content` | bandeja editorial | ADMIN, EDITOR |
| `/publications` | publicaciones y mensajes | ADMIN, EDITOR |
| `/sources` | fuentes RSS | ADMIN |
| `/users` | usuarios y estados | ADMIN |
| `/audit` | auditoría de usuario y editorial | ADMIN |
| `/settings` | configuración y observabilidad | ADMIN |

La interfaz dispone de modos claro y oscuro, menú lateral adaptable y navegación por teclado.

## 5. Dashboard

El dashboard presenta:

- noticias capturadas;
- eventos detectados y críticos;
- análisis pendientes;
- contenido pendiente de revisión;
- avisos editoriales;
- eventos prioritarios;
- accesos a ejecuciones manuales de clasificación, detección y análisis.

Utiliza este orden de lectura:

1. revisa el aviso editorial;
2. comprueba las métricas del día;
3. prioriza eventos críticos o de alto impacto;
4. ejecuta automatizaciones solo si es necesario;
5. abre el evento que necesita decisión.

## 6. Noticias

La pantalla `Noticias` muestra cada artículo capturado con:

- fuente;
- título y URL;
- fecha de publicación y captura;
- estado de procesamiento;
- clasificación;
- evento relacionado.

Estados:

- `CAPTURED`: pendiente de clasificación;
- `CLASSIFIED`: clasificada, pendiente de evento;
- `EVENT_MATCHED`: vinculada a un evento;
- `ARCHIVED`: fuera del flujo activo.

Una URL solo puede almacenarse una vez. Los duplicados se descartan sin crear una segunda noticia.

## 7. Eventos

`Eventos` es la pantalla central del trabajo editorial.

La tabla permite:

- buscar en todos los campos;
- filtrar por categoría, impacto, estado y estado editorial;
- ordenar resultados;
- abrir el detalle de un evento;
- identificar cuántas noticias lo sustentan.

Estados de dominio:

- `OPEN`;
- `MONITORING`;
- `CLOSED`;
- `ARCHIVED`.

Impacto:

- `LOW`;
- `MEDIUM`;
- `HIGH`;
- `CRITICAL`.

### Detalle del evento

El detalle reúne:

- título, descripción, categoría, importancia y estado;
- noticias vinculadas;
- análisis generados;
- contenido editorial;
- información de vigencia del análisis.

Un evento debe conservar al menos una noticia. Si distintas noticias hablan del mismo hecho, deben quedar agrupadas bajo el mismo evento.

### Fusión

La fusión mueve las noticias y el contexto al evento principal y evita publicar dos veces el mismo asunto. Debe realizarse con criterio editorial y queda auditada.

## 8. Análisis

El análisis se genera desde la información consolidada del evento. Puede incluir:

- resumen ejecutivo;
- síntesis sindical;
- puntos clave;
- riesgos y oportunidades;
- colectivos afectados;
- seguimiento recomendado.

Si el evento recibe nueva información después de generar el análisis, la interfaz puede marcarlo como desactualizado.

La IA no puede introducir hechos ausentes en las noticias suministradas. Las respuestas se validan antes de persistirse.

## 9. Contenido editorial

### Generar

1. Abre un evento analizado.
2. Pulsa la acción de generación.
3. Selecciona los parámetros editoriales disponibles.
4. Revisa el borrador generado.

El contenido nace con estado de revisión. Nunca se publica automáticamente por haber sido generado.

### Editar

Modifica título, cuerpo o tono cuando sea necesario. Una edición devuelve el contenido al estado de revisión para evitar publicar cambios no aprobados.

### Aprobar

La aprobación confirma que el contenido es correcto, oportuno y publicable. Solo el contenido aprobado puede enviarse a Telegram.

### Rechazar

El rechazo retira el borrador del flujo de publicación. Puede generarse otro contenido desde el mismo evento si procede.

Estados:

- `GENERATED`;
- `PENDING_REVIEW`;
- `APPROVED`;
- `REJECTED`;
- `PUBLISHED`.

## 10. Publicaciones

### Publicación de contenido aprobado

El contenido puede:

- publicarse inmediatamente;
- programarse para una fecha y hora;
- quedar pendiente;
- registrar un fallo para revisión.

Estados:

- `PENDING`;
- `SCHEDULED`;
- `PUBLISHED`;
- `FAILED`.

El detalle conserva snapshots de título y mensaje, usuario solicitante, destino, adjuntos y respuesta técnica resumida.

### Mensaje manual de Telegram

Desde `Publicaciones > Mensaje manual` se puede:

- redactar título y mensaje;
- aplicar formato compatible;
- seleccionar destinos;
- añadir adjuntos;
- enviar una comunicación no vinculada a contenido generado.

La acción está protegida y auditada. No se deben usar bots o canales reales durante pruebas no controladas.

## 11. Fuentes

Disponible para `ADMIN`.

Permite:

- consultar fuentes RSS;
- crear una fuente;
- modificar nombre, URL, tipo, prioridad y activación;
- desactivar fuentes sin eliminar su histórico.

Antes de añadir una fuente:

1. comprueba que la URL responde;
2. confirma que devuelve RSS/XML válido;
3. evita crear otra entrada para el mismo medio;
4. asigna una prioridad coherente.

## 12. Usuarios

Disponible para `ADMIN`.

Operaciones:

- alta;
- edición;
- activación y desactivación;
- bloqueo y desbloqueo;
- regeneración de contraseña temporal;
- eliminación cuando no existen dependencias que lo impidan.

Estados:

- `PENDING_ACTIVATION`;
- `ACTIVE`;
- `INACTIVE`;
- `LOCKED`.

El alta no solicita una contraseña en claro. El sistema genera una contraseña temporal y fuerza su cambio.

## 13. Auditoría

Disponible para `ADMIN`.

La pestaña `Usuarios` incluye acciones como:

- acceso correcto o fallido;
- cierre de sesión;
- cambio y recuperación de contraseña;
- alta y cambios de estado.

La pestaña `Editorial` incluye:

- cambios de eventos;
- generación, edición, aprobación y rechazo;
- publicación;
- ejecuciones y errores operativos.

Los filtros permiten localizar acciones por fecha, actor, entidad y detalle. Los errores se resaltan para facilitar el diagnóstico.

## 14. Configuración

`/settings` es el centro administrativo.

### Métricas IA

Muestra por día:

- operaciones;
- aciertos y fallos;
- tasas de éxito y error;
- latencia media y percentil 95;
- proveedor, modelo, prompt y entidad relacionada;
- error resumido cuando existe.

### Prompts IA

Permite consultar las versiones técnicas registradas. El texto oficial se mantiene en código y en el catálogo de prompts; la UI no lo edita.

### Automatizaciones

Configura `WF-02`, `WF-03` y `WF-04`:

- activación;
- intervalo;
- tamaño de lote;
- ejecución manual;
- estado de la última ejecución.

`WF-01` se muestra como integración externa de n8n. `WF-05` y `WF-06` se activan por acciones editoriales o programación en Spring Boot.

### Publicación

Administra:

- activación de Telegram;
- datos no sensibles del canal;
- destinos;
- configuración operativa;
- estado de la integración.

Los secretos se enmascaran y deben proporcionarse mediante configuración segura.

## 15. Automatizaciones

| Código | Responsabilidad | Motor |
| --- | --- | --- |
| `WF-01` | captura RSS/XML | n8n |
| `WF-02` | clasificación de noticias | Spring Boot |
| `WF-03` | detección y agrupación de eventos | Spring Boot |
| `WF-04` | análisis de eventos | Spring Boot |
| `WF-05` | generación de contenido | Spring Boot bajo demanda |
| `WF-06` | publicación inmediata o programada | Spring Boot |

Una ejecución manual no modifica la arquitectura: solo adelanta el scheduler correspondiente.

## 16. Buenas prácticas

- Trabaja desde los eventos prioritarios.
- Verifica las noticias fuente antes de aprobar.
- Comprueba si el análisis está vigente.
- Corrige lenguaje ambiguo o no neutral.
- No publiques contenido no aprobado.
- No copies secretos en campos editoriales.
- Revisa la auditoría después de un fallo.
- Mantén deshabilitadas integraciones reales durante demostraciones.

## 17. Incidencias frecuentes

### No aparecen datos

- comprueba que la fecha seleccionada es correcta;
- actualiza la pantalla;
- revisa el estado del backend;
- confirma que el rol tiene acceso.

### Una automatización falla

- abre `/settings`;
- revisa el último resultado;
- consulta las métricas IA y la auditoría;
- comprueba la configuración del proveedor;
- vuelve a ejecutar solo después de resolver la causa.

### Una publicación falla

- revisa el estado `FAILED`;
- abre el detalle;
- comprueba Telegram, destino y adjuntos;
- no repitas la acción sin validar que no se publicó externamente.

### El usuario no accede

- revisa su estado;
- desbloquea o activa si procede;
- genera otra contraseña temporal;
- usa MailHog en el entorno local para consultar el mensaje.

## 18. Límites del MVP

- El canal social operativo es Telegram.
- n8n solo captura RSS/XML.
- La edición de prompts desde UI no forma parte del MVP.
- El despliegue Proxmox/Nginx está documentado como objetivo de producción, pero no existe una URL pública verificada.
- Las credenciales de demostración y el proveedor determinista son exclusivos del entorno local.
