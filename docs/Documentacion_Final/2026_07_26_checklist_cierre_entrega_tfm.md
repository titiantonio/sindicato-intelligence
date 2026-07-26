# Checklist de cierre de la entrega TFM

**Proyecto:** Sindicato Intelligence
**Fecha:** 26/07/2026
**Uso:** completar en orden y no enviar el formulario mientras quede un punto
obligatorio sin verificar.

## 1. Registro de enlaces definitivos

Rellena esta tabla cuando cada URL responda desde una ventana privada:

| Entregable | URL definitiva | Verificado sin sesión |
| --- | --- | --- |
| Repositorio | <https://github.com/titiantonio/sindicato-intelligence> | [x] |
| Slides, PDF o HTML | `PENDIENTE` | [ ] |
| Vídeo | `PENDIENTE` | [ ] |
| Aplicación desplegada, si se ofrece | `NO APLICA` | [ ] |

## 2. Fecha límite

La fecha límite efectiva confirmada por el autor es el **24/08/2026**.

- [x] Fecha real corregida en la documentación.
- [ ] Completar y verificar todos los entregables antes del 24/08/2026.
- [ ] Reservar margen para comprobar enlaces desde una ventana privada.

## 3. Preparar el estado definitivo de Git

- [ ] Revisar todos los cambios con `git status --short`.
- [ ] Revisar el contenido con `git diff` y comprobar los archivos nuevos.
- [ ] Ejecutar `git diff --check`.
- [ ] Confirmar que no se incluyen `.env`, logs, `node_modules`, `target`,
  `dist`, adjuntos, dumps ni datos personales.
- [ ] Confirmar que las credenciales visibles son únicamente las cuentas de
  demostración exigidas para la evaluación.
- [ ] Crear un commit de entrega.
- [ ] Subir `main` al remoto.

Mensaje de commit propuesto:

```text
docs: preparar entrega final del TFM
```

No uses `git add` ni publiques los cambios hasta haber revisado visualmente el
estado completo.

## 4. Repositorio público

Estado verificado el 26/07/2026 mediante GitHub y acceso HTTP sin sesión.

- [x] Repositorio con visibilidad pública.
- [x] URL accesible sin iniciar sesión:
  `https://github.com/titiantonio/sindicato-intelligence`.
- [ ] Publicar el commit con todos los cambios locales de entrega.
- [ ] Activar el secret scanning disponible para el repositorio.
- [ ] Confirmar que README, código y slides son visibles.

## 5. Obtener una URL pública para las slides

El requisito admite una URL pública o el documento adjunto junto al código.
La opción mínima, una vez publicado el commit, es verificar el PDF versionado:

```text
https://github.com/titiantonio/sindicato-intelligence/blob/main/slides/sindicato_intelligence_tfm.pdf
```

- [ ] Publicar el commit que contiene `slides/sindicato_intelligence_tfm.pdf`.
- [ ] Abrir la URL anterior en una ventana privada.
- [ ] Comprobar que GitHub muestra o permite descargar las 10 diapositivas.
- [ ] Copiar esa URL a la tabla de la sección 1.

### Opción adicional: presentación HTML con GitHub Pages

- [ ] Abrir `Settings > Pages`.
- [ ] Seleccionar `Deploy from a branch`.
- [ ] Elegir la rama `main` y la carpeta `/ (root)`.
- [ ] Guardar y esperar a que termine la publicación.
- [ ] Abrir en una ventana privada:

```text
https://titiantonio.github.io/sindicato-intelligence/slides/tfm_presentacion.html
```

- [ ] Avanzar por las 10 slides.
- [ ] Comprobar capturas, textos, teclado y pantalla completa.
- [ ] Si se prefiere esta versión, sustituir la URL del PDF de la tabla por la
  URL HTML verificada.

GitHub Pages no es obligatorio si el PDF ya está públicamente accesible desde
el repositorio.

## 6. Preparar y grabar el vídeo

### Antes de grabar

- [ ] Usar el entorno habitual de desarrollo, que ya contiene datos; no es
  necesario grabar con la base limpia del stack TFM.
- [ ] Ejecutar `.\dev-start.ps1` y arrancar backend y frontend localmente.
- [ ] Comprobar `http://localhost:8080/api/v1/health` y
  `http://localhost:4200`.
- [ ] No ejecutar `.\tfm-reset.ps1` ni eliminar el volumen de desarrollo.
- [ ] Localizar un evento estable con varias noticias, análisis vigente y
  contenido preparado.
- [ ] Confirmar que los registros mostrados no contienen datos personales ni
  secretos.
- [ ] Mantener Telegram real deshabilitado.
- [ ] No ejecutar IA externa durante la toma; si fuera necesario ensayar una
  generación, usar el proveedor determinista local.
- [ ] Cerrar notificaciones, correo y pestañas personales.
- [ ] Revisar que no sean visibles tokens, claves ni valores de `.env`.
- [ ] Tener abierto el
  `2026_07_25_guion_video_tfm.md`.

### Durante la grabación

- [ ] Usar voz propia.
- [ ] Mantener una duración entre 5 y 10 minutos.
- [ ] Mostrar portada, problema, arquitectura y flujo.
- [ ] Mostrar dashboard y un evento con varias noticias.
- [ ] Mostrar análisis, generación o revisión de contenido.
- [ ] Explicar la aprobación humana antes de publicar.
- [ ] Mostrar settings, automatizaciones y métricas sin exponer secretos.
- [ ] Cerrar con pruebas, seguridad y conclusiones.

### Después de grabar

- [ ] Comprobar audio, resolución y legibilidad.
- [ ] Ver el vídeo completo antes de subirlo.
- [ ] Subirlo como público u oculto accesible mediante enlace; no como privado.
- [ ] Abrir la URL sin sesión y comprobar la reproducción.
- [ ] Copiar la URL verificada a la tabla de la sección 1.
- [ ] Detener el entorno con `.\tfm-stop.ps1`.

Título propuesto:

```text
TFM — Sindicato Intelligence | Plataforma de inteligencia informativa
```

Descripción propuesta:

```text
Presentación del TFM Sindicato Intelligence.

La plataforma captura noticias educativas, las clasifica con IA, agrupa
duplicados en eventos, genera análisis consolidados y prepara contenido para
revisión humana y publicación en Telegram.

Repositorio: https://github.com/titiantonio/sindicato-intelligence
Slides: [URL]
```

## 7. Actualizar el README

- [x] Reflejar que el repositorio ya es público.
- [ ] Añadir la URL pública de las slides.
- [ ] Añadir la URL pública del vídeo.
- [ ] Añadir la URL de despliegue solo si está realmente operativa.
- [ ] Eliminar la frase que indica que slides y vídeo siguen pendientes.
- [ ] Comprobar que las credenciales de demostración siguen visibles y
  funcionan.

No añadas URL previstas o privadas: solo enlaces ya comprobados sin sesión.

## 8. Verificación final antes del formulario

- [x] Repositorio accesible por el evaluador.
- [ ] README legible y con todos los enlaces.
- [ ] Slides accesibles y completas.
- [ ] Vídeo reproducible y con voz.
- [ ] Credenciales de demostración correctas.
- [ ] Guía Docker reproducible.
- [ ] Sin secretos, datos personales ni archivos locales.
- [ ] Entregables verificados antes del 24/08/2026.
- [ ] Nombre completo del alumno introducido.
- [ ] Email usado en la inscripción del máster introducido.
- [ ] URL del repositorio:
  `https://github.com/titiantonio/sindicato-intelligence`.
- [ ] URL de despliegue introducida solo si existe.
- [ ] URL de slides copiada desde la sección 1.
- [ ] URL del vídeo copiada desde la sección 1.
- [ ] Usuario y contraseña de prueba copiados desde el README.
- [ ] Captura o justificante del envío guardado.

## 9. Recomendaciones no bloqueantes

- [ ] Decidir si procede una licencia académica o de código abierto.
- [ ] Crear un tag o release final después de publicar los enlaces.
- [ ] Añadir CI/CD en una evolución posterior.
- [ ] Mantener un despliegue temporal si facilita la evaluación.

El despliegue público es recomendable según los requisitos recibidos, pero no
sustituye al repositorio, las slides ni el vídeo obligatorio.
