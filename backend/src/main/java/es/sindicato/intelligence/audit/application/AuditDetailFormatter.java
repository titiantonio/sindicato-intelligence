package es.sindicato.intelligence.audit.application;

import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;

public final class AuditDetailFormatter {

    private static final int ERROR_LIMIT = 500;

    private AuditDetailFormatter() {
    }

    public static String eventMerged(Long targetEventId, Collection<Long> sourceEventIds, int newsCount) {
        return "Evento #" + targetEventId
                + " fusionado con eventos origen " + formatIds(sourceEventIds)
                + ". Noticias asociadas tras la fusion: " + newsCount + ".";
    }

    public static String eventDiscarded(Long eventId, String title, Object importance, int newsCount) {
        return "Evento " + idText(eventId)
                + " descartado manualmente. Titulo: \"" + safe(title)
                + "\". Impacto: " + importance
                + ". Noticias asociadas: " + newsCount + ".";
    }

    public static String eventRestored(Long eventId, String title, Object importance, int newsCount) {
        return "Evento " + idText(eventId)
                + " restaurado tras descarte manual. Titulo: \"" + safe(title)
                + "\". Impacto: " + importance
                + ". Noticias asociadas: " + newsCount + ".";
    }

    public static String sourceCreated(Long sourceId, String name, String type, int priority, boolean active) {
        return "Fuente " + idText(sourceId)
                + " creada. Nombre: \"" + safe(name)
                + "\". Tipo: " + safe(type)
                + ". Prioridad: " + priority
                + ". Activa: " + active + ".";
    }

    public static String sourceUpdated(Long sourceId, String name, String type, int priority, boolean active) {
        return "Fuente " + idText(sourceId)
                + " actualizada. Nombre: \"" + safe(name)
                + "\". Tipo: " + safe(type)
                + ". Prioridad: " + priority
                + ". Activa: " + active + ".";
    }

    public static String contentGenerated(Long contentId, Long eventId, Long analysisId, String channel, String tone, String status) {
        return "Contenido " + idText(contentId)
                + " generado para evento " + idText(eventId)
                + " usando analisis " + idText(analysisId)
                + ". Canal: " + safe(channel)
                + ". Tono: " + safe(tone)
                + ". Estado: " + safe(status) + ".";
    }

    public static String analysisGenerated(Long analysisId, Long eventId, int keyPoints, int risks, int opportunities, String modelUsed) {
        return "Analisis " + idText(analysisId)
                + " generado para evento " + idText(eventId)
                + ". Puntos clave: " + keyPoints
                + ". Riesgos: " + risks
                + ". Oportunidades: " + opportunities
                + ". Modelo: " + safe(modelUsed) + ".";
    }

    public static String contentApproved(Long contentId, Long eventId, OffsetDateTime approvedAt, String status) {
        return "Contenido " + idText(contentId)
                + " aprobado para evento " + idText(eventId)
                + ". Fecha de aprobacion: " + approvedAt
                + ". Estado: " + safe(status) + ".";
    }

    public static String contentRejected(Long contentId, Long eventId, String status) {
        return "Contenido " + idText(contentId)
                + " rechazado para evento " + idText(eventId)
                + ". Estado: " + safe(status) + ".";
    }

    public static String contentEditedBefore(Long contentId, Long eventId, String title, String tone, String status) {
        return "Contenido " + idText(contentId)
                + " del evento " + idText(eventId)
                + " antes de editar. Titulo: \"" + safe(title)
                + "\". Tono: " + safe(tone)
                + ". Estado: " + safe(status) + ".";
    }

    public static String contentEditedAfter(Long contentId, Long eventId, String title, String tone, String status) {
        return "Contenido " + idText(contentId)
                + " del evento " + idText(eventId)
                + " editado correctamente. Titulo: \"" + safe(title)
                + "\". Tono: " + safe(tone)
                + ". Estado resultante: " + safe(status) + ".";
    }

    public static String publicationScheduled(Long publicationId, Long contentId, Long eventId, String channel, OffsetDateTime scheduledAt, String status) {
        return "Publicacion " + idText(publicationId)
                + " programada para contenido " + idText(contentId)
                + " del evento " + idText(eventId)
                + " en " + safe(channel)
                + ". Fecha programada: " + scheduledAt
                + ". Estado: " + safe(status) + ".";
    }

    public static String publicationPublished(Long publicationId, Long contentId, Long eventId, String channel, String status, String externalId, boolean scheduled) {
        String mode = scheduled ? "programada" : "directa";
        return "Publicacion " + mode + " " + idText(publicationId)
                + " completada correctamente para contenido " + idText(contentId)
                + " del evento " + idText(eventId)
                + " en " + safe(channel)
                + ". Estado: " + safe(status)
                + ". Referencia externa: " + safe(externalId) + ".";
    }

    public static String publicationFailed(Long publicationId, Long contentId, Long eventId, String channel, String status, String error, boolean scheduled) {
        String mode = scheduled ? "programada" : "directa";
        return "Publicacion " + mode + " " + idText(publicationId)
                + " fallida para contenido " + idText(contentId)
                + " del evento " + idText(eventId)
                + " en " + safe(channel)
                + ". Estado: " + safe(status)
                + ". Motivo: " + truncate(error) + ".";
    }

    public static String manualPublicationPublished(Long publicationId, Long requestedBy, String destinations, int attachmentCount, long totalBytes, String status) {
        return "Publicacion manual " + idText(publicationId)
                + " enviada correctamente por usuario " + idText(requestedBy)
                + ". Destinos: " + safe(destinations)
                + ". Adjuntos: " + attachmentCount
                + ". Tamano total: " + totalBytes + " bytes"
                + ". Estado: " + safe(status) + ".";
    }

    public static String manualPublicationFailed(Long publicationId, Long requestedBy, String destinations, int attachmentCount, long totalBytes, String status, String error) {
        return "Publicacion manual " + idText(publicationId)
                + " fallida por usuario " + idText(requestedBy)
                + ". Destinos: " + safe(destinations)
                + ". Adjuntos: " + attachmentCount
                + ". Tamano total: " + totalBytes + " bytes"
                + ". Estado: " + safe(status)
                + ". Motivo: " + truncate(error) + ".";
    }

    public static String automationSettingUpdated(String workflowCode, boolean enabled, int intervalSeconds, int batchSize) {
        return "Configuracion de automatizacion " + safe(workflowCode)
                + " actualizada. Activa: " + enabled
                + ". Intervalo: " + intervalSeconds + " segundos"
                + ". Lote: " + batchSize + ".";
    }

    public static String automationRunCompleted(String workflowCode, int processed, int success, int failed, int skipped) {
        return "Ejecucion de automatizacion " + safe(workflowCode)
                + " completada. Procesadas: " + processed
                + ". Correctas: " + success
                + ". Fallidas: " + failed
                + ". Omitidas: " + skipped + ".";
    }

    public static String automationRunFailed(String workflowCode, String error) {
        return "Ejecucion de automatizacion " + safe(workflowCode)
                + " fallida. Motivo: " + truncate(error) + ".";
    }

    public static String telegramSettingsUpdated(boolean enabled, String baseUrl, boolean disableWebPagePreview, int destinations, int maxAttachmentCount) {
        return "Configuracion Telegram actualizada. Activa: " + enabled
                + ". Base URL: " + safe(baseUrl)
                + ". Vista previa web deshabilitada: " + disableWebPagePreview
                + ". Destinos configurados: " + destinations
                + ". Maximo adjuntos: " + maxAttachmentCount + ".";
    }

    public static String userCreated(Object role) {
        return "Usuario creado con rol " + role + " y pendiente de activacion.";
    }

    public static String userDeleted(Long userId, String email, Object role) {
        return "Usuario " + idText(userId)
                + " eliminado. Email: " + safe(email)
                + ". Rol: " + role + ".";
    }

    public static String userStatusChanged(Object previousStatus, Object newStatus) {
        return "Estado de usuario actualizado de " + previousStatus + " a " + newStatus + ".";
    }

    public static String userRoleChanged(Object previousRole, Object newRole) {
        return "Rol de usuario actualizado de " + previousRole + " a " + newRole + ".";
    }

    public static String temporaryPasswordReset(OffsetDateTime expiresAt) {
        return "Password temporal regenerada. Caduca el " + expiresAt + ".";
    }

    public static String passwordChanged(OffsetDateTime changedAt) {
        return "Password actualizada correctamente el " + changedAt + ".";
    }

    public static String login(OffsetDateTime loginAt) {
        return "Login completado correctamente el " + loginAt + ".";
    }

    private static String formatIds(Collection<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return "[]";
        }
        return ids.stream()
                .filter(Objects::nonNull)
                .map(id -> "#" + id)
                .collect(Collectors.joining(", ", "[", "]"));
    }

    private static String truncate(String value) {
        String safeValue = safe(value);
        if (safeValue.length() <= ERROR_LIMIT) {
            return safeValue;
        }
        return safeValue.substring(0, ERROR_LIMIT);
    }

    private static String idText(Long id) {
        return id == null ? "#-" : "#" + id;
    }

    private static String safe(String value) {
        if (value == null || value.isBlank()) {
            return "-";
        }
        return value.replace("\r", " ").replace("\n", " ").trim();
    }
}
