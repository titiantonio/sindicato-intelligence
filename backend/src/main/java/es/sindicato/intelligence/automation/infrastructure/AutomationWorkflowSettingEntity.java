package es.sindicato.intelligence.automation.infrastructure;

import es.sindicato.intelligence.automation.domain.AutomationWorkflowCode;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.OffsetDateTime;

@Entity
@Table(name = "automation_workflow_settings")
public class AutomationWorkflowSettingEntity {

    @Id
    @Enumerated(EnumType.STRING)
    @Column(name = "workflow_code", nullable = false, length = 50)
    private AutomationWorkflowCode workflowCode;

    @Column(name = "enabled", nullable = false)
    private boolean enabled;

    @Column(name = "interval_seconds", nullable = false)
    private int intervalSeconds;

    @Column(name = "batch_size", nullable = false)
    private int batchSize;

    @Column(name = "running", nullable = false)
    private boolean running;

    @Column(name = "last_run_at")
    private OffsetDateTime lastRunAt;

    @Column(name = "last_success_at")
    private OffsetDateTime lastSuccessAt;

    @Column(name = "last_failure_at")
    private OffsetDateTime lastFailureAt;

    @Column(name = "next_run_at", nullable = false)
    private OffsetDateTime nextRunAt;

    @Column(name = "last_processed_count", nullable = false)
    private int lastProcessedCount;

    @Column(name = "last_success_count", nullable = false)
    private int lastSuccessCount;

    @Column(name = "last_failed_count", nullable = false)
    private int lastFailedCount;

    @Column(name = "last_skipped_count", nullable = false)
    private int lastSkippedCount;

    @Column(name = "last_error")
    private String lastError;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    protected AutomationWorkflowSettingEntity() {
    }

    public AutomationWorkflowSettingEntity(
            AutomationWorkflowCode workflowCode,
            boolean enabled,
            int intervalSeconds,
            int batchSize,
            boolean running,
            OffsetDateTime lastRunAt,
            OffsetDateTime lastSuccessAt,
            OffsetDateTime lastFailureAt,
            OffsetDateTime nextRunAt,
            int lastProcessedCount,
            int lastSuccessCount,
            int lastFailedCount,
            int lastSkippedCount,
            String lastError,
            OffsetDateTime createdAt,
            OffsetDateTime updatedAt
    ) {
        this.workflowCode = workflowCode;
        this.enabled = enabled;
        this.intervalSeconds = intervalSeconds;
        this.batchSize = batchSize;
        this.running = running;
        this.lastRunAt = lastRunAt;
        this.lastSuccessAt = lastSuccessAt;
        this.lastFailureAt = lastFailureAt;
        this.nextRunAt = nextRunAt;
        this.lastProcessedCount = lastProcessedCount;
        this.lastSuccessCount = lastSuccessCount;
        this.lastFailedCount = lastFailedCount;
        this.lastSkippedCount = lastSkippedCount;
        this.lastError = lastError;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public AutomationWorkflowCode getWorkflowCode() {
        return workflowCode;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public int getIntervalSeconds() {
        return intervalSeconds;
    }

    public int getBatchSize() {
        return batchSize;
    }

    public boolean isRunning() {
        return running;
    }

    public OffsetDateTime getLastRunAt() {
        return lastRunAt;
    }

    public OffsetDateTime getLastSuccessAt() {
        return lastSuccessAt;
    }

    public OffsetDateTime getLastFailureAt() {
        return lastFailureAt;
    }

    public OffsetDateTime getNextRunAt() {
        return nextRunAt;
    }

    public int getLastProcessedCount() {
        return lastProcessedCount;
    }

    public int getLastSuccessCount() {
        return lastSuccessCount;
    }

    public int getLastFailedCount() {
        return lastFailedCount;
    }

    public int getLastSkippedCount() {
        return lastSkippedCount;
    }

    public String getLastError() {
        return lastError;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }
}
