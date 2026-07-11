package es.sindicato.intelligence.ai.application;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

@Service
public class AiModelExecutionCoordinator {

    private static final Logger log = LoggerFactory.getLogger(AiModelExecutionCoordinator.class);

    private final AiWorkflowRuntimeSettingsResolver settingsResolver;
    private final Clock clock;
    private final Object monitor = new Object();
    private final Map<String, ExecutionState> activeExecutions = new HashMap<>();
    private final Map<String, OffsetDateTime> cooldownUntil = new HashMap<>();

    public AiModelExecutionCoordinator(AiWorkflowRuntimeSettingsResolver settingsResolver, Clock clock) {
        this.settingsResolver = settingsResolver;
        this.clock = clock;
    }

    public <T> T execute(String workflowCode, Supplier<T> action) {
        Objects.requireNonNull(action, "action is required");
        AiWorkflowRuntimeSettings settings = settingsResolver.resolve(workflowCode);
        String modelName = normalizeModel(settings.modelName());
        int cooldownSeconds = Math.max(0, settings.cooldownSeconds());
        acquire(workflowCode, modelName);
        try {
            return action.get();
        } finally {
            release(workflowCode, modelName, cooldownSeconds);
        }
    }

    private void acquire(String workflowCode, String modelName) {
        synchronized (monitor) {
            Thread currentThread = Thread.currentThread();
            while (true) {
                ExecutionState active = activeExecutions.get(modelName);
                if (active != null && active.owner == currentThread) {
                    active.incrementDepth();
                    return;
                }
                if (active == null && !isCoolingDown(modelName)) {
                    activeExecutions.put(modelName, new ExecutionState(currentThread));
                    log.info("ai model execution acquired: workflowCode={}, modelName={}", workflowCode, modelName);
                    return;
                }
                waitUntilAvailable(workflowCode, modelName, active);
            }
        }
    }

    private boolean isCoolingDown(String modelName) {
        OffsetDateTime until = cooldownUntil.get(modelName);
        if (until == null) {
            return false;
        }
        if (!now().isBefore(until)) {
            cooldownUntil.remove(modelName);
            return false;
        }
        return true;
    }

    private void waitUntilAvailable(String workflowCode, String modelName, ExecutionState active) {
        try {
            long waitMillis = waitMillis(modelName, active);
            log.info("ai model execution waiting: workflowCode={}, modelName={}, waitMillis={}", workflowCode, modelName, waitMillis);
            monitor.wait(waitMillis);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("ai model execution interrupted while waiting for model: " + modelName, exception);
        }
    }

    private long waitMillis(String modelName, ExecutionState active) {
        if (active != null) {
            return 1000L;
        }
        OffsetDateTime until = cooldownUntil.get(modelName);
        if (until == null) {
            return 1000L;
        }
        long millis = until.toInstant().toEpochMilli() - now().toInstant().toEpochMilli();
        return Math.max(1L, Math.min(millis, 1000L));
    }

    private void release(String workflowCode, String modelName, int cooldownSeconds) {
        synchronized (monitor) {
            ExecutionState active = activeExecutions.get(modelName);
            if (active == null || active.owner != Thread.currentThread()) {
                return;
            }
            if (active.decrementDepth() > 0) {
                return;
            }
            activeExecutions.remove(modelName);
            if (cooldownSeconds > 0) {
                OffsetDateTime until = now().plusSeconds(cooldownSeconds);
                cooldownUntil.put(modelName, until);
                log.info("ai model execution released with cooldown: workflowCode={}, modelName={}, cooldownSeconds={}", workflowCode, modelName, cooldownSeconds);
            } else {
                cooldownUntil.remove(modelName);
                log.info("ai model execution released: workflowCode={}, modelName={}", workflowCode, modelName);
            }
            monitor.notifyAll();
        }
    }

    private OffsetDateTime now() {
        return OffsetDateTime.now(clock);
    }

    private String normalizeModel(String modelName) {
        if (modelName == null || modelName.isBlank()) {
            throw new IllegalArgumentException("ai modelName is required for execution coordination");
        }
        return modelName.trim().toLowerCase();
    }

    private static final class ExecutionState {
        private final Thread owner;
        private int depth = 1;

        private ExecutionState(Thread owner) {
            this.owner = owner;
        }

        private void incrementDepth() {
            depth++;
        }

        private int decrementDepth() {
            depth--;
            return depth;
        }
    }
}
