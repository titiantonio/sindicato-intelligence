package es.sindicato.intelligence.automation.application;

import es.sindicato.intelligence.ai.domain.AiMetricStatus;
import es.sindicato.intelligence.ai.domain.AiOperationMetric;
import es.sindicato.intelligence.ai.domain.AiOperationMetricRepository;
import es.sindicato.intelligence.audit.domain.AuditLogEntry;
import es.sindicato.intelligence.audit.domain.AuditLogQuery;
import es.sindicato.intelligence.audit.domain.AuditLogRepository;
import es.sindicato.intelligence.content.domain.ContentStatus;
import es.sindicato.intelligence.content.domain.GeneratedContent;
import es.sindicato.intelligence.content.domain.GeneratedContentRepository;
import es.sindicato.intelligence.publication.domain.Publication;
import es.sindicato.intelligence.publication.domain.PublicationRepository;
import es.sindicato.intelligence.publication.domain.PublicationStatus;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ListWorkflowOperationsUseCaseTest {

    @Test
    void combinesAiMetricsAndTelegramPublicationOperations() {
        AiOperationMetricRepository metricRepository = mock(AiOperationMetricRepository.class);
        PublicationRepository publicationRepository = mock(PublicationRepository.class);
        GeneratedContentRepository contentRepository = mock(GeneratedContentRepository.class);
        AuditLogRepository auditLogRepository = mock(AuditLogRepository.class);
        ListWorkflowOperationsUseCase useCase = new ListWorkflowOperationsUseCase(
                metricRepository,
                publicationRepository,
                contentRepository,
                auditLogRepository
        );
        OffsetDateTime metricTime = OffsetDateTime.parse("2026-06-18T10:00:00+02:00");
        OffsetDateTime auditTime = OffsetDateTime.parse("2026-06-18T11:00:00+02:00");

        when(metricRepository.findByCreatedAtBetween(any(), any())).thenReturn(List.of(new AiOperationMetric(
                1L,
                "CLASSIFICATION",
                "WF02_CLASSIFICATION",
                "GeminiAIProvider",
                "gemini-1.5-flash",
                AiMetricStatus.SUCCESS,
                "NEWS",
                7L,
                120,
                null,
                Map.of("workflowCode", "WF02_CLASSIFICATION", "finalNewsStatus", "DISCARDED"),
                metricTime
        )));
        when(auditLogRepository.findEditorial(any(AuditLogQuery.class))).thenReturn(List.of(new AuditLogEntry(
                10L,
                1L,
                "PUBLICATION_FAILED",
                "PUBLICATION",
                20L,
                null,
                "Publicacion fallida",
                auditTime
        )));
        when(publicationRepository.findById(20L)).thenReturn(Optional.of(new Publication(
                20L,
                30L,
                "TELEGRAM",
                null,
                PublicationStatus.FAILED,
                null,
                "{\"ok\":false,\"description\":\"Telegram publication failed\"}"
        )));
        when(contentRepository.findById(30L)).thenReturn(Optional.of(new GeneratedContent(
                30L,
                40L,
                1L,
                "TELEGRAM",
                "INFORMATIVO",
                "Titulo",
                "Contenido",
                ContentStatus.APPROVED,
                metricTime,
                metricTime
        )));
        when(publicationRepository.findScheduledBetween(any(), any())).thenReturn(List.of());

        List<WorkflowOperationView> operations = useCase.execute(LocalDate.parse("2026-06-18"));

        assertEquals(2, operations.size());
        assertEquals("WF06_PUBLICATION_TELEGRAM", operations.getFirst().workflowCode());
        assertEquals("FAILED", operations.getFirst().status());
        assertEquals(40L, operations.getFirst().details().get("eventId"));
        assertEquals("WF02_CLASSIFICATION", operations.get(1).workflowCode());
        assertEquals("DISCARDED", operations.get(1).details().get("finalNewsStatus"));
    }
}
