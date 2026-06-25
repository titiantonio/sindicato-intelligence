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

    public static String userCreated(Object role) {
        return "Usuario creado con rol " + role + " y pendiente de activacion.";
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
