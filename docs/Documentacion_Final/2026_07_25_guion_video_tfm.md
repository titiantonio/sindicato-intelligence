# Guion del vídeo de presentación del TFM

**Duración objetivo:** 7 minutos 30 segundos  
**Formato:** voz propia + captura de pantalla  
**Proyecto:** Sindicato Intelligence  
**Actualizado:** 26/07/2026

## 1. Preparación antes de grabar

### Entorno

- Usa el entorno habitual de desarrollo, que ya contiene datos útiles para la
  demostración. No es necesario grabar con la base limpia del stack TFM.
- Ejecuta `.\dev-start.ps1` y arranca backend y frontend localmente como indica
  el propio script.
- Comprueba `http://localhost:8080/api/v1/health` y
  `http://localhost:4200`.
- No ejecutes `.\tfm-reset.ps1` ni elimines el volumen de desarrollo antes de
  grabar.
- Abre el backoffice en `http://localhost:4200`.
- Usa una ventana de navegador a 1440 × 900 o similar.
- Cierra notificaciones, terminales y pestañas no necesarias.
- Amplía el puntero si el grabador lo permite.
- Mantén el zoom del navegador al 100 %.

### Datos

- Accede como `admin@sindicato.es`.
- Verifica que los datos de desarrollo elegidos no contienen datos personales
  ni secretos.
- Elige antes un evento estable con varias noticias y un análisis vigente.
- Elige un contenido ya preparado para demostrar revisión sin depender de una
  llamada externa durante la grabación.
- Mantén Telegram deshabilitado.
- No ejecutes IA externa. Si necesitas generar algo durante el ensayo, usa el
  proveedor determinista local; durante la toma definitiva es preferible
  reutilizar el contenido preparado.

### Privacidad

No muestres:

- `.env`;
- tokens;
- claves;
- contraseñas escritas en texto visible;
- configuración completa de Telegram;
- datos personales reales;
- consola con secretos.

En el login, la contraseña puede introducirse antes de iniciar la grabación o escribirse con el campo enmascarado.

## 2. Estructura visual

Puedes alternar entre:

- slides para problema, arquitectura y calidad;
- aplicación para la demostración;
- una última slide para conclusiones.

No leas las slides. Úsalas como apoyo mientras explicas.

## 3. Guion minuto a minuto

### 0:00–0:35 — Apertura

**Muestra:** slide de portada.

**Di:**

> Hola. Voy a presentar Sindicato Intelligence, una plataforma interna de inteligencia informativa para un sindicato docente de Andalucía. El proyecto busca reducir el trabajo manual de seguimiento de noticias educativas, agrupar información repetida y preparar contenido fiable para revisión y publicación.

> La idea principal es sencilla: la noticia no es la unidad de trabajo final. Varias noticias pueden hablar del mismo hecho y deben convertirse en un único evento.

### 0:35–1:10 — Problema y propuesta

**Muestra:** slide de problema y solución.

**Di:**

> En el proceso tradicional hay que revisar muchas fuentes RSS, detectar duplicados, clasificar cada noticia, relacionarla con asuntos ya abiertos y redactar después un contenido sindical. Esto consume tiempo y puede provocar duplicidades.

> Sindicato Intelligence automatiza la parte repetitiva, pero conserva la decisión humana. La IA clasifica, agrupa, analiza y propone; un editor revisa, corrige, aprueba y decide si se publica.

### 1:10–1:45 — Arquitectura

**Muestra:** slide de arquitectura y flujo.

**Di:**

> La solución utiliza Angular para el backoffice, Spring Boot para toda la lógica de negocio y PostgreSQL para persistencia. La arquitectura del backend sigue DDD, Clean Architecture y un monolito modular.

> n8n tiene una responsabilidad limitada: el workflow WF-01 captura RSS o XML y envía las noticias al backend. La clasificación, la detección de eventos, el análisis, el contenido y Telegram se ejecutan en Spring Boot.

> El flujo de dominio es noticia, evento, análisis, contenido y publicación. Event es el aggregate root principal.

### 1:45–2:20 — Login y dashboard

**Muestra:** aplicación. Si no has iniciado sesión, entra con el campo de contraseña enmascarado. Abre `/dashboard`.

**Di:**

> El acceso está protegido con JWT y existen dos roles: ADMIN y EDITOR. También se implementan refresh token, recuperación, contraseña temporal y cambio obligatorio.

> En el dashboard se resume el trabajo del día: noticias, eventos, análisis, contenido pendiente y avisos editoriales. Los eventos prioritarios permiten empezar por aquello que necesita una decisión.

**Haz:** desplaza lentamente para mostrar métricas y eventos prioritarios. No pulses todavía automatizaciones.

### 2:20–3:05 — Noticias y evento central

**Muestra:** `/news`, luego `/events`.

**Di:**

> Las noticias llegan desde las fuentes y pasan por estados de captura, clasificación y asociación a evento. La URL es única, por lo que el backend evita duplicados.

> La pantalla principal es Eventos. Aquí puedo buscar, filtrar por categoría o impacto y comprobar cuántas noticias respaldan cada evento.

**Haz:** abre un evento previamente elegido.

**Di:**

> En el detalle se reúne toda la información del hecho: sus noticias, la clasificación, el análisis consolidado y los contenidos generados. Si cinco medios informan sobre la misma convocatoria, el objetivo es mantener un solo evento y no cinco publicaciones.

### 3:05–3:50 — IA y análisis

**Muestra:** análisis dentro del evento.

**Di:**

> La IA trabaja con prompts versionados y solo con el contexto facilitado. La respuesta se valida antes de guardar nada. El análisis incluye resumen ejecutivo, puntos clave, riesgos, oportunidades y colectivos afectados.

> También se registra observabilidad: proveedor, modelo, latencia, resultado, entidad y error resumido. Si el evento recibe noticias nuevas, el análisis puede marcarse como desactualizado.

> Para esta demostración utilizo un proveedor determinista local. Así se recorre el flujo sin depender de una API de pago.

### 3:50–4:45 — Generación y revisión editorial

**Muestra:** generación desde el evento o un contenido existente en `/content`.

**Di:**

> Desde el evento se genera un borrador editorial. La generación no equivale a una aprobación. El contenido queda pendiente de revisión.

**Haz:** abre un contenido.

> El editor puede modificar título y cuerpo, rechazar el borrador o aprobarlo. Si se edita un contenido aprobado, vuelve al estado de revisión. Esta regla evita publicar cambios que nadie haya validado.

**Haz:** muestra los botones de editar, aprobar y rechazar. Si los datos son desechables, edita una palabra y guarda; si no, solo señala el flujo.

### 4:45–5:30 — Publicación Telegram

**Muestra:** `/publications`.

**Di:**

> Solo el contenido aprobado puede publicarse. La publicación puede ser inmediata o programada y conserva estados pendiente, programada, publicada y fallida.

> El histórico guarda snapshots del mensaje, usuario solicitante, destinos, adjuntos y respuesta técnica resumida.

**Haz:** abre `Mensaje manual`.

> También existe un editor para mensajes manuales, con formato, destinos y adjuntos. Telegram está deshabilitado en este entorno de demostración, por lo que no se realiza un envío real durante el vídeo.

**Haz:** cierra el diálogo sin enviar.

### 5:30–6:25 — Administración y observabilidad

**Muestra:** `/settings`.

**Di:**

> La configuración administrativa reúne cuatro áreas. En Métricas IA se consulta el volumen, éxito, errores y latencia. En Prompts se ven las versiones técnicas, pero el texto oficial no se edita desde la interfaz.

**Haz:** pulsa `Automatizaciones`.

> Las automatizaciones WF-02, WF-03 y WF-04 se activan y configuran desde PostgreSQL. Se puede ajustar intervalo y lote o lanzar una ejecución manual. WF-01 sigue siendo externo en n8n.

**Haz:** pulsa `Publicación`, sin abrir ni mostrar secretos.

> La configuración de Telegram se gestiona de forma administrativa y los valores sensibles se enmascaran.

**Muestra brevemente:** `/audit`.

> Las acciones de usuarios y editoriales quedan auditadas para mantener trazabilidad.

### 6:25–7:05 — Calidad y seguridad

**Muestra:** slide de calidad.

**Di:**

> El backend dispone de 347 pruebas y el frontend de 163 pruebas unitarias. Playwright cubre login, roles, navegación, flujo editorial, responsive y accesibilidad con una API simulada. El build de producción está verificado y las dependencias enviadas al navegador no presentan vulnerabilidades conocidas en la auditoría realizada.

> En seguridad se aplican JWT, roles en backend, rate limiting de autenticación, cambio obligatorio de contraseña, cifrado o enmascarado de settings sensibles y logs sin secretos.

### 7:05–7:35 — Cierre

**Muestra:** slide final.

**Di:**

> Como resultado, Sindicato Intelligence convierte un conjunto disperso de noticias en un flujo editorial trazable centrado en eventos. Automatiza clasificación, agrupación y análisis, pero mantiene la revisión humana antes de publicar.

> El MVP es reproducible con Docker, integra el backoffice completo y deja preparado el camino para un despliegue productivo con Nginx, Proxmox, secretos gestionados y CI/CD.

> Gracias por la atención.

## 4. Versión corta de 5 minutos

Para reducir el vídeo:

- apertura y problema: 40 segundos;
- arquitectura: 35 segundos;
- dashboard: 30 segundos;
- eventos y análisis: 1 minuto;
- contenido y publicación: 1 minuto 20 segundos;
- settings, calidad y cierre: 55 segundos.

Elimina la visita a `/news`, `/audit` y el detalle de infraestructura. No elimines:

- voz propia;
- captura de pantalla;
- evento como entidad central;
- revisión humana;
- calidad y seguridad;
- conclusión.

## 5. Versión ampliada de 9 minutos

Añade:

- una noticia detallada y su clasificación;
- demostración de filtros en Eventos;
- edición real de contenido;
- explicación breve de estados;
- Swagger con un endpoint, sin mostrar tokens;
- n8n con `WF-01` inactivo o controlado;
- MailHog para enseñar la contraseña temporal.

No hagas una llamada real a IA ni una publicación real solo para llenar tiempo.

## 6. Checklist de grabación

- [ ] Entorno de desarrollo levantado y comprobado.
- [ ] Base de desarrollo conservada; no se ha ejecutado `tfm-reset.ps1`.
- [ ] Evento estable con varias noticias y análisis vigente localizado.
- [ ] Contenido de demostración localizado y revisado.
- [ ] Telegram deshabilitado.
- [ ] No se ejecutará IA externa durante la toma.
- [ ] Escritorio limpio.
- [ ] Micrófono probado.
- [ ] Resolución legible.
- [ ] Cursor visible.
- [ ] Sin secretos en pantalla.
- [ ] Audio sin ruido.
- [ ] Duración entre 5 y 10 minutos.
- [ ] Vídeo revisado completo.
- [ ] Enlace accesible sin iniciar sesión.
- [ ] URL añadida al README.

## 7. Nombre y descripción sugeridos

**Título:**

```text
Sindicato Intelligence | Presentación TFM
```

**Descripción:**

```text
Presentación de Sindicato Intelligence, plataforma interna de inteligencia
informativa para seguimiento educativo andaluz. Incluye arquitectura,
demostración del flujo News -> Event -> Analysis -> Content -> Publication,
seguridad, pruebas y conclusiones.

Repositorio: https://github.com/titiantonio/sindicato-intelligence
Slides: https://titiantonio.github.io/sindicato-intelligence/slides/tfm_presentacion.html
```

Publica el vídeo como público u oculto accesible mediante enlace, según permita la entrega. Comprueba el enlace desde una ventana privada.
