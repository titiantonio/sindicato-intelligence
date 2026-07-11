package es.sindicato.intelligence.ai.application;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Clock;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AiModelExecutionCoordinatorTest {

    @Test
    void waitsWhenAnotherWorkflowUsesSameModel() throws Exception {
        AiWorkflowRuntimeSettingsResolver resolver = mock(AiWorkflowRuntimeSettingsResolver.class);
        when(resolver.resolve("WF04_ANALYSIS")).thenReturn(settings("WF04_ANALYSIS", "shared-model", 0));
        when(resolver.resolve("WF05_CONTENT")).thenReturn(settings("WF05_CONTENT", "shared-model", 0));
        AiModelExecutionCoordinator coordinator = new AiModelExecutionCoordinator(resolver, Clock.systemUTC());
        CountDownLatch firstStarted = new CountDownLatch(1);
        CountDownLatch releaseFirst = new CountDownLatch(1);
        AtomicBoolean secondEntered = new AtomicBoolean(false);

        Thread first = new Thread(() -> coordinator.execute("WF04_ANALYSIS", () -> {
            firstStarted.countDown();
            await(releaseFirst);
            return null;
        }));
        Thread second = new Thread(() -> coordinator.execute("WF05_CONTENT", () -> {
            secondEntered.set(true);
            return null;
        }));

        first.start();
        assertTrue(firstStarted.await(1, TimeUnit.SECONDS));
        second.start();
        Thread.sleep(100);

        assertFalse(secondEntered.get());

        releaseFirst.countDown();
        first.join(1000);
        second.join(1000);
        assertTrue(secondEntered.get());
    }

    @Test
    void allowsConcurrentExecutionWithDifferentModels() throws Exception {
        AiWorkflowRuntimeSettingsResolver resolver = mock(AiWorkflowRuntimeSettingsResolver.class);
        when(resolver.resolve("WF04_ANALYSIS")).thenReturn(settings("WF04_ANALYSIS", "analysis-model", 0));
        when(resolver.resolve("WF05_CONTENT")).thenReturn(settings("WF05_CONTENT", "content-model", 0));
        AiModelExecutionCoordinator coordinator = new AiModelExecutionCoordinator(resolver, Clock.systemUTC());
        CountDownLatch firstStarted = new CountDownLatch(1);
        CountDownLatch secondEntered = new CountDownLatch(1);
        CountDownLatch releaseFirst = new CountDownLatch(1);

        Thread first = new Thread(() -> coordinator.execute("WF04_ANALYSIS", () -> {
            firstStarted.countDown();
            await(releaseFirst);
            return null;
        }));
        Thread second = new Thread(() -> coordinator.execute("WF05_CONTENT", () -> {
            secondEntered.countDown();
            return null;
        }));

        first.start();
        assertTrue(firstStarted.await(1, TimeUnit.SECONDS));
        second.start();

        assertTrue(secondEntered.await(1, TimeUnit.SECONDS));
        releaseFirst.countDown();
        first.join(1000);
        second.join(1000);
    }

    @Test
    void appliesCooldownAfterExecutionCompletes() throws Exception {
        AiWorkflowRuntimeSettingsResolver resolver = mock(AiWorkflowRuntimeSettingsResolver.class);
        when(resolver.resolve("WF04_ANALYSIS")).thenReturn(settings("WF04_ANALYSIS", "shared-model", 1));
        when(resolver.resolve("WF05_CONTENT")).thenReturn(settings("WF05_CONTENT", "shared-model", 0));
        AiModelExecutionCoordinator coordinator = new AiModelExecutionCoordinator(resolver, Clock.systemUTC());

        coordinator.execute("WF04_ANALYSIS", () -> null);

        AtomicLong elapsedMillis = new AtomicLong();
        long started = System.nanoTime();
        Thread second = new Thread(() -> coordinator.execute("WF05_CONTENT", () -> {
            elapsedMillis.set(TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - started));
            return null;
        }));

        second.start();
        second.join(2000);

        assertTrue(elapsedMillis.get() >= 900);
    }

    private AiWorkflowRuntimeSettings settings(String workflowCode, String modelName, int cooldownSeconds) {
        return new AiWorkflowRuntimeSettings(workflowCode, "gemini", modelName, BigDecimal.valueOf(0.2), 1024, cooldownSeconds, "api-key");
    }

    private void await(CountDownLatch latch) {
        try {
            latch.await(1, TimeUnit.SECONDS);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException(exception);
        }
    }
}
