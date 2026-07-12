package es.sindicato.intelligence.automation.domain;

import es.sindicato.intelligence.automation.application.AutomationRunResult;

import java.time.OffsetDateTime;
import java.util.Objects;

public class AutomationWorkflowSetting {

    private final AutomationWorkflowCode workflowCode;
    private boolean enabled;
    private int intervalSeconds;
    private int batchSize;
    private boolean running;
    private OffsetDateTime lastRunAt;
    private OffsetDateTime lastSuccessAt;
    private OffsetDateTime lastFailureAt;
    private OffsetDateTime nextRunAt;
    private int lastProcessedCount;
    private int lastSuccessCount;
    private int lastFailedCount;
    private int lastSkippedCount;
    private String lastError;
    private final OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    public AutomationWorkflowSetting(
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
        this.workflowCode = Objects.requireNonNull(workflowCode, "workflowCode is required");
        this.enabled = enabled;
        this.intervalSeconds = validateInterval(intervalSeconds);
        this.batchSize = validateBatchSize(batchSize);
        this.running = running;
        this.lastRunAt = lastRunAt;
        this.lastSuccessAt = lastSuccessAt;
        this.lastFailureAt = lastFailureAt;
        this.nextRunAt = Objects.requireNonNull(nextRunAt, "nextRunAt is required");
        this.lastProcessedCount = Math.max(0, lastProcessedCount);
        this.lastSuccessCount = Math.max(0, lastSuccessCount);
        this.lastFailedCount = Math.max(0, lastFailedCount);
        this.lastSkippedCount = Math.max(0, lastSkippedCount);
        this.lastError = lastError;
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt is required");
        this.updatedAt = Objects.requireNonNull(updatedAt, "updatedAt is required");
    }

    public void update(boolean enabled, int intervalSeconds, int batchSize, OffsetDateTime now) {
        this.enabled = enabled;
        this.intervalSeconds = validateInterval(intervalSeconds);
        this.batchSize = validateBatchSize(batchSize);
        if (!running && this.nextRunAt.isBefore(now)) {
            this.nextRunAt = now.plusSeconds(this.intervalSeconds);
        }
        touch(now);
    }

    public boolean isDue(OffsetDateTime now) {
        return enabled && !running && !nextRunAt.isAfter(now);
    }

    public void markRunning(OffsetDateTime now) {
        this.running = true;
        this.lastRunAt = now;
        touch(now);
    }

    public void markCompleted(AutomationRunResult result, OffsetDateTime now) {
        this.running = false;
        this.lastProcessedCount = result.processedCount();
        this.lastSuccessCount = result.successCount();
        this.lastFailedCount = result.failedCount();
        this.lastSkippedCount = result.skippedCount();
        this.lastError = result.errors().isEmpty() ? null : result.errors().getFirst().message();
        if (result.failedCount() > 0) {
            this.lastFailureAt = now;
        }
        if (result.successCount() > 0 || result.failedCount() == 0) {
            this.lastSuccessAt = now;
        }
        this.nextRunAt = now.plusSeconds(intervalSeconds);
        touch(now);
    }

    public boolean requestImmediateRun(OffsetDateTime now) {
        Objects.requireNonNull(now, "now is required");
        if (!enabled || running || !nextRunAt.isAfter(now)) {
            return false;
        }

        this.nextRunAt = now;
        touch(now);
        return true;
    }

    public void markFailed(String error, OffsetDateTime now) {
        this.running = false;
        this.lastProcessedCount = 0;
        this.lastSuccessCount = 0;
        this.lastFailedCount = 1;
        this.lastSkippedCount = 0;
        this.lastFailureAt = now;
        this.lastError = truncate(error);
        this.nextRunAt = now.plusSeconds(intervalSeconds);
        touch(now);
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

    private void touch(OffsetDateTime now) {
        this.updatedAt = Objects.requireNonNull(now, "now is required");
    }

    private int validateInterval(int value) {
        if (value < 60) {
            throw new IllegalArgumentException("intervalSeconds must be at least 60");
        }
        return value;
    }

    private int validateBatchSize(int value) {
        if (value < 1) {
            throw new IllegalArgumentException("batchSize must be at least 1");
        }
        return value;
    }

    private String truncate(String value) {
        if (value == null || value.isBlank()) {
            return "automation failed";
        }
        return value.length() <= 500 ? value : value.substring(0, 500);
    }
}
