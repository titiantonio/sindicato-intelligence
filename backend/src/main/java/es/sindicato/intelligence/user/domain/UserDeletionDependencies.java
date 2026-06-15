package es.sindicato.intelligence.user.domain;

public record UserDeletionDependencies(
        long generatedContentCount,
        long auditLogCount
) {

    public boolean hasFunctionalDependencies() {
        return generatedContentCount > 0 || auditLogCount > 0;
    }

    public String describeFunctionalDependencies() {
        StringBuilder details = new StringBuilder();
        if (generatedContentCount > 0) {
            details.append("generated_content.created_by=").append(generatedContentCount);
        }
        if (auditLogCount > 0) {
            if (!details.isEmpty()) {
                details.append(", ");
            }
            details.append("audit_log.user_id=").append(auditLogCount);
        }
        return details.toString();
    }
}
