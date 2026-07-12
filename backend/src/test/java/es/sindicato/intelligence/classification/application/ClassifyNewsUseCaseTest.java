package es.sindicato.intelligence.classification.application;

import es.sindicato.intelligence.ai.application.AiModelExecutionCoordinator;
import es.sindicato.intelligence.ai.application.AiOperationMetricsRecorder;
import es.sindicato.intelligence.classification.domain.ClassificationCategory;
import es.sindicato.intelligence.classification.domain.ImpactLevel;
import es.sindicato.intelligence.classification.domain.NewsClassification;
import es.sindicato.intelligence.classification.domain.NewsClassificationRepository;
import es.sindicato.intelligence.classification.domain.UrgencyLevel;
import es.sindicato.intelligence.news.domain.NewsArticle;
import es.sindicato.intelligence.news.domain.NewsRepository;
import es.sindicato.intelligence.news.domain.NewsStatus;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ClassifyNewsUseCaseTest {

    @Test
    void classifiesAndPersistsNewsClassification() {
        NewsRepository newsRepository = mock(NewsRepository.class);
        NewsClassificationRepository classificationRepository = mock(NewsClassificationRepository.class);
        ClassifyNewsPromptBuilder promptBuilder = new ClassifyNewsPromptBuilder();
        AIProvider aiProvider = mock(AIProvider.class);
        AiOperationMetricsRecorder metricsRecorder = mock(AiOperationMetricsRecorder.class);
        NewsContentEnrichmentPort enrichmentPort = mock(NewsContentEnrichmentPort.class);
        ClassifiedNewsFollowUpPort followUpPort = mock(ClassifiedNewsFollowUpPort.class);
        ClassifyNewsUseCase useCase = new ClassifyNewsUseCase(newsRepository, classificationRepository, promptBuilder, aiProvider, metricsRecorder, coordinator(), enrichmentPort, followUpPort);
        NewsArticle newsArticle = newsArticle();
        NewsClassification savedClassification = classification(10L, newsArticle.getId());

        when(newsRepository.findById(newsArticle.getId())).thenReturn(Optional.of(newsArticle));
        when(classificationRepository.existsByNewsId(newsArticle.getId())).thenReturn(false);
        when(aiProvider.classify(any(ClassificationAIRequest.class))).thenReturn(aiResponse());
        when(aiProvider.providerName()).thenReturn("test-provider");
        when(aiProvider.modelName()).thenReturn("test-model");
        when(classificationRepository.save(any(NewsClassification.class))).thenReturn(savedClassification);

        NewsClassification result = useCase.execute(new ClassifyNewsCommand(newsArticle.getId()));

        ArgumentCaptor<NewsClassification> captor = ArgumentCaptor.forClass(NewsClassification.class);
        verify(classificationRepository).save(captor.capture());
        verify(newsRepository).save(newsArticle);
        NewsClassification classificationToSave = captor.getValue();

        assertEquals(savedClassification, result);
        assertEquals(newsArticle.getId(), classificationToSave.getNewsId());
        assertEquals(ClassificationCategory.SIPRI, classificationToSave.getCategory());
        assertEquals(BigDecimal.valueOf(95), classificationToSave.getRelevanceScore());
        assertEquals(NewsStatus.CLASSIFIED, newsArticle.getProcessingStatus());
        assertNotNull(classificationToSave.getClassifiedAt());
        verify(followUpPort).requestEventDetection(newsArticle.getId());
        verify(metricsRecorder).recordSuccess(
                eq("CLASSIFICATION"),
                eq("WF02_CLASSIFICATION"),
                anyString(),
                eq("test-model"),
                eq("NEWS"),
                eq(newsArticle.getId()),
                isNull(),
                org.mockito.ArgumentMatchers.<Map<String, Object>>any()
        );
    }

    @Test
    void marksOutOfScopeNewsAsDiscardedAfterPersistingClassification() {
        NewsRepository newsRepository = mock(NewsRepository.class);
        NewsClassificationRepository classificationRepository = mock(NewsClassificationRepository.class);
        ClassifyNewsPromptBuilder promptBuilder = new ClassifyNewsPromptBuilder();
        AIProvider aiProvider = mock(AIProvider.class);
        AiOperationMetricsRecorder metricsRecorder = mock(AiOperationMetricsRecorder.class);
        NewsContentEnrichmentPort enrichmentPort = mock(NewsContentEnrichmentPort.class);
        ClassifiedNewsFollowUpPort followUpPort = mock(ClassifiedNewsFollowUpPort.class);
        ClassifyNewsUseCase useCase = new ClassifyNewsUseCase(newsRepository, classificationRepository, promptBuilder, aiProvider, metricsRecorder, coordinator(), enrichmentPort, followUpPort);
        NewsArticle newsArticle = newsArticle();
        ClassificationAIResponse response = aiResponse(ClassificationCategory.OTROS, "FUERA_DE_AMBITO", BigDecimal.ZERO, ImpactLevel.LOW, UrgencyLevel.LOW);

        when(newsRepository.findById(newsArticle.getId())).thenReturn(Optional.of(newsArticle));
        when(classificationRepository.existsByNewsId(newsArticle.getId())).thenReturn(false);
        when(aiProvider.classify(any(ClassificationAIRequest.class))).thenReturn(response);
        when(aiProvider.providerName()).thenReturn("test-provider");
        when(aiProvider.modelName()).thenReturn("test-model");
        when(classificationRepository.save(any(NewsClassification.class))).thenAnswer(invocation -> invocation.getArgument(0));

        useCase.execute(new ClassifyNewsCommand(newsArticle.getId()));

        ArgumentCaptor<NewsClassification> captor = ArgumentCaptor.forClass(NewsClassification.class);
        ArgumentCaptor<Map<String, Object>> detailsCaptor = ArgumentCaptor.forClass(Map.class);
        verify(classificationRepository).save(captor.capture());
        verify(newsRepository).save(newsArticle);
        verify(metricsRecorder).recordSuccess(
                eq("CLASSIFICATION"),
                eq("WF02_CLASSIFICATION"),
                anyString(),
                eq("test-model"),
                eq("NEWS"),
                eq(newsArticle.getId()),
                isNull(),
                detailsCaptor.capture()
        );
        assertEquals(ClassificationCategory.OTROS, captor.getValue().getCategory());
        assertEquals("FUERA_DE_AMBITO", captor.getValue().getSubcategory());
        assertEquals(List.of(), captor.getValue().getKeywords());
        assertEquals(List.of(), captor.getValue().getEntities());
        assertEquals(NewsStatus.DISCARDED, newsArticle.getProcessingStatus());
        assertEquals("DISCARDED", detailsCaptor.getValue().get("finalNewsStatus"));
        assertEquals("FUERA_DE_AMBITO", detailsCaptor.getValue().get("discardReason"));
        assertFalse(detailsCaptor.getValue().containsKey("keywords"));
        assertFalse(detailsCaptor.getValue().containsKey("entities"));
        assertFalse(detailsCaptor.getValue().containsKey("aiSummary"));
        verify(followUpPort, never()).requestEventDetection(newsArticle.getId());
    }

    @Test
    void marksInsufficientInformationNewsAsDiscardedAfterPersistingClassification() {
        NewsRepository newsRepository = mock(NewsRepository.class);
        NewsClassificationRepository classificationRepository = mock(NewsClassificationRepository.class);
        ClassifyNewsPromptBuilder promptBuilder = new ClassifyNewsPromptBuilder();
        AIProvider aiProvider = mock(AIProvider.class);
        AiOperationMetricsRecorder metricsRecorder = mock(AiOperationMetricsRecorder.class);
        NewsContentEnrichmentPort enrichmentPort = mock(NewsContentEnrichmentPort.class);
        ClassifyNewsUseCase useCase = new ClassifyNewsUseCase(newsRepository, classificationRepository, promptBuilder, aiProvider, metricsRecorder, coordinator(), enrichmentPort, mock(ClassifiedNewsFollowUpPort.class));
        NewsArticle newsArticle = newsArticle();

        when(newsRepository.findById(newsArticle.getId())).thenReturn(Optional.of(newsArticle));
        when(classificationRepository.existsByNewsId(newsArticle.getId())).thenReturn(false);
        when(aiProvider.classify(any(ClassificationAIRequest.class))).thenReturn(aiResponse(ClassificationCategory.OTROS, "INFORMACION_INSUFICIENTE", BigDecimal.ZERO, ImpactLevel.LOW, UrgencyLevel.LOW));
        when(aiProvider.providerName()).thenReturn("test-provider");
        when(aiProvider.modelName()).thenReturn("test-model");
        when(classificationRepository.save(any(NewsClassification.class))).thenAnswer(invocation -> invocation.getArgument(0));

        useCase.execute(new ClassifyNewsCommand(newsArticle.getId()));

        verify(newsRepository).save(newsArticle);
        assertEquals(NewsStatus.DISCARDED, newsArticle.getProcessingStatus());
    }

    @Test
    void fallsBackToOutOfScopeDiscardWhenGeminiReturnsNoTextForNewsWithoutEducationSignals() {
        NewsRepository newsRepository = mock(NewsRepository.class);
        NewsClassificationRepository classificationRepository = mock(NewsClassificationRepository.class);
        AIProvider aiProvider = mock(AIProvider.class);
        AiOperationMetricsRecorder metricsRecorder = mock(AiOperationMetricsRecorder.class);
        NewsContentEnrichmentPort enrichmentPort = mock(NewsContentEnrichmentPort.class);
        ClassifyNewsUseCase useCase = new ClassifyNewsUseCase(newsRepository, classificationRepository, new ClassifyNewsPromptBuilder(), aiProvider, metricsRecorder, coordinator(), enrichmentPort, mock(ClassifiedNewsFollowUpPort.class));
        NewsArticle newsArticle = newsArticle("Noticia de sucesos", "La Audiencia dicta sentencia penal", "Contenido judicial de ambito penal");

        when(newsRepository.findById(newsArticle.getId())).thenReturn(Optional.of(newsArticle));
        when(classificationRepository.existsByNewsId(newsArticle.getId())).thenReturn(false);
        when(aiProvider.classify(any(ClassificationAIRequest.class))).thenThrow(new AIProviderException("Gemini response does not contain candidates[0].content.parts[0].text"));
        when(aiProvider.providerName()).thenReturn("gemini");
        when(aiProvider.modelName()).thenReturn("models/gemma-4-31b-it");
        when(classificationRepository.save(any(NewsClassification.class))).thenAnswer(invocation -> invocation.getArgument(0));

        useCase.execute(new ClassifyNewsCommand(newsArticle.getId()));

        ArgumentCaptor<NewsClassification> captor = ArgumentCaptor.forClass(NewsClassification.class);
        verify(classificationRepository).save(captor.capture());
        verify(newsRepository).save(newsArticle);
        verify(metricsRecorder, never()).recordFailure(anyString(), anyString(), anyString(), anyString(), anyString(), any(), any(), any());
        assertEquals(ClassificationCategory.OTROS, captor.getValue().getCategory());
        assertEquals("FUERA_DE_AMBITO", captor.getValue().getSubcategory());
        assertEquals(BigDecimal.ZERO, captor.getValue().getRelevanceScore());
        assertEquals(NewsStatus.DISCARDED, newsArticle.getProcessingStatus());
    }

    @Test
    void ignoresUrlEnrichmentNoiseWhenApplyingOutOfScopeFallback() {
        NewsRepository newsRepository = mock(NewsRepository.class);
        NewsClassificationRepository classificationRepository = mock(NewsClassificationRepository.class);
        AIProvider aiProvider = mock(AIProvider.class);
        AiOperationMetricsRecorder metricsRecorder = mock(AiOperationMetricsRecorder.class);
        NewsContentEnrichmentPort enrichmentPort = mock(NewsContentEnrichmentPort.class);
        ClassifyNewsUseCase useCase = new ClassifyNewsUseCase(newsRepository, classificationRepository, new ClassifyNewsPromptBuilder(), aiProvider, metricsRecorder, coordinator(), enrichmentPort, mock(ClassifiedNewsFollowUpPort.class));
        NewsArticle newsArticle = newsArticle("Noticia de sucesos", "La Audiencia dicta sentencia penal", "Contenido judicial de ambito penal");

        when(newsRepository.findById(newsArticle.getId())).thenReturn(Optional.of(newsArticle));
        when(classificationRepository.existsByNewsId(newsArticle.getId())).thenReturn(false);
        when(enrichmentPort.enrich(newsArticle.getUrl())).thenReturn(Optional.of("Menu: Educacion Universidad FP. Cuerpo: contenido de sociedad."));
        when(aiProvider.classify(any(ClassificationAIRequest.class))).thenThrow(new AIProviderException("Gemini response does not contain candidates[0].content.parts[0].text"));
        when(aiProvider.providerName()).thenReturn("gemini");
        when(aiProvider.modelName()).thenReturn("models/gemma-4-31b-it");
        when(classificationRepository.save(any(NewsClassification.class))).thenAnswer(invocation -> invocation.getArgument(0));

        useCase.execute(new ClassifyNewsCommand(newsArticle.getId()));

        ArgumentCaptor<NewsClassification> captor = ArgumentCaptor.forClass(NewsClassification.class);
        verify(classificationRepository).save(captor.capture());
        assertEquals(ClassificationCategory.OTROS, captor.getValue().getCategory());
        assertEquals("FUERA_DE_AMBITO", captor.getValue().getSubcategory());
        assertEquals(NewsStatus.DISCARDED, newsArticle.getProcessingStatus());
    }

    @Test
    void keepsFailureWhenGeminiReturnsNoTextForNewsWithEducationSignals() {
        NewsRepository newsRepository = mock(NewsRepository.class);
        NewsClassificationRepository classificationRepository = mock(NewsClassificationRepository.class);
        AIProvider aiProvider = mock(AIProvider.class);
        AiOperationMetricsRecorder metricsRecorder = mock(AiOperationMetricsRecorder.class);
        NewsContentEnrichmentPort enrichmentPort = mock(NewsContentEnrichmentPort.class);
        ClassifyNewsUseCase useCase = new ClassifyNewsUseCase(newsRepository, classificationRepository, new ClassifyNewsPromptBuilder(), aiProvider, metricsRecorder, coordinator(), enrichmentPort, mock(ClassifiedNewsFollowUpPort.class));
        NewsArticle newsArticle = newsArticle("Conflicto en centros educativos", "Docentes reclaman medidas", "Profesorado de Andalucia afectado");

        when(newsRepository.findById(newsArticle.getId())).thenReturn(Optional.of(newsArticle));
        when(classificationRepository.existsByNewsId(newsArticle.getId())).thenReturn(false);
        when(aiProvider.classify(any(ClassificationAIRequest.class))).thenThrow(new AIProviderException("Gemini response does not contain candidates[0].content.parts[0].text"));
        when(aiProvider.providerName()).thenReturn("gemini");
        when(aiProvider.modelName()).thenReturn("models/gemma-4-31b-it");

        assertThrows(AIProviderException.class, () -> useCase.execute(new ClassifyNewsCommand(newsArticle.getId())));

        verify(classificationRepository, never()).save(any(NewsClassification.class));
        verify(newsRepository, never()).save(newsArticle);
        verify(metricsRecorder).recordFailure(eq("CLASSIFICATION"), eq("WF02_CLASSIFICATION"), eq("gemini"), eq("models/gemma-4-31b-it"), eq("NEWS"), eq(newsArticle.getId()), isNull(), any(AIProviderException.class));
    }

    @Test
    void rejectsUnknownNews() {
        NewsRepository newsRepository = mock(NewsRepository.class);
        NewsClassificationRepository classificationRepository = mock(NewsClassificationRepository.class);
        ClassifyNewsUseCase useCase = new ClassifyNewsUseCase(newsRepository, classificationRepository, new ClassifyNewsPromptBuilder(), mock(AIProvider.class), mock(AiOperationMetricsRecorder.class), coordinator(), mock(NewsContentEnrichmentPort.class), mock(ClassifiedNewsFollowUpPort.class));

        when(newsRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> useCase.execute(new ClassifyNewsCommand(1L)));

        verify(classificationRepository, never()).save(any(NewsClassification.class));
    }

    @Test
    void rejectsAlreadyClassifiedNews() {
        NewsRepository newsRepository = mock(NewsRepository.class);
        NewsClassificationRepository classificationRepository = mock(NewsClassificationRepository.class);
        ClassifyNewsUseCase useCase = new ClassifyNewsUseCase(newsRepository, classificationRepository, new ClassifyNewsPromptBuilder(), mock(AIProvider.class), mock(AiOperationMetricsRecorder.class), coordinator(), mock(NewsContentEnrichmentPort.class), mock(ClassifiedNewsFollowUpPort.class));
        NewsArticle newsArticle = newsArticle();

        when(newsRepository.findById(newsArticle.getId())).thenReturn(Optional.of(newsArticle));
        when(classificationRepository.existsByNewsId(newsArticle.getId())).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> useCase.execute(new ClassifyNewsCommand(newsArticle.getId())));

        verify(classificationRepository, never()).save(any(NewsClassification.class));
    }

    @Test
    void enrichesFromUrlWhenLocalContextIsInsufficient() {
        NewsRepository newsRepository = mock(NewsRepository.class);
        NewsClassificationRepository classificationRepository = mock(NewsClassificationRepository.class);
        AIProvider aiProvider = mock(AIProvider.class);
        AiOperationMetricsRecorder metricsRecorder = mock(AiOperationMetricsRecorder.class);
        NewsContentEnrichmentPort enrichmentPort = mock(NewsContentEnrichmentPort.class);
        ClassifyNewsUseCase useCase = new ClassifyNewsUseCase(newsRepository, classificationRepository, new ClassifyNewsPromptBuilder(), aiProvider, metricsRecorder, coordinator(), enrichmentPort, mock(ClassifiedNewsFollowUpPort.class));
        NewsArticle newsArticle = newsArticle();

        when(newsRepository.findById(newsArticle.getId())).thenReturn(Optional.of(newsArticle));
        when(classificationRepository.existsByNewsId(newsArticle.getId())).thenReturn(false);
        when(enrichmentPort.enrich(newsArticle.getUrl())).thenReturn(Optional.of("SIPRI publica adjudicaciones provisionales con plazo de alegaciones."));
        when(aiProvider.classify(any(ClassificationAIRequest.class))).thenReturn(aiResponse());
        when(aiProvider.providerName()).thenReturn("test-provider");
        when(aiProvider.modelName()).thenReturn("test-model");
        when(classificationRepository.save(any(NewsClassification.class))).thenAnswer(invocation -> invocation.getArgument(0));

        useCase.execute(new ClassifyNewsCommand(newsArticle.getId()));

        ArgumentCaptor<ClassificationAIRequest> requestCaptor = ArgumentCaptor.forClass(ClassificationAIRequest.class);
        verify(enrichmentPort).enrich(newsArticle.getUrl());
        verify(aiProvider).classify(requestCaptor.capture());
        assertEquals(newsArticle.getUrl(), requestCaptor.getValue().url());
        org.junit.jupiter.api.Assertions.assertTrue(requestCaptor.getValue().content().contains("Contexto enriquecido desde la URL"));
        org.junit.jupiter.api.Assertions.assertTrue(requestCaptor.getValue().content().contains("plazo de alegaciones"));
    }

    @Test
    void keepsOriginalContextWhenUrlEnrichmentFails() {
        NewsRepository newsRepository = mock(NewsRepository.class);
        NewsClassificationRepository classificationRepository = mock(NewsClassificationRepository.class);
        AIProvider aiProvider = mock(AIProvider.class);
        AiOperationMetricsRecorder metricsRecorder = mock(AiOperationMetricsRecorder.class);
        NewsContentEnrichmentPort enrichmentPort = mock(NewsContentEnrichmentPort.class);
        ClassifyNewsUseCase useCase = new ClassifyNewsUseCase(newsRepository, classificationRepository, new ClassifyNewsPromptBuilder(), aiProvider, metricsRecorder, coordinator(), enrichmentPort, mock(ClassifiedNewsFollowUpPort.class));
        NewsArticle newsArticle = newsArticle();

        when(newsRepository.findById(newsArticle.getId())).thenReturn(Optional.of(newsArticle));
        when(classificationRepository.existsByNewsId(newsArticle.getId())).thenReturn(false);
        when(enrichmentPort.enrich(newsArticle.getUrl())).thenThrow(new IllegalStateException("fetch failed"));
        when(aiProvider.classify(any(ClassificationAIRequest.class))).thenReturn(aiResponse(ClassificationCategory.OTROS, "INFORMACION_INSUFICIENTE", BigDecimal.ZERO, ImpactLevel.LOW, UrgencyLevel.LOW));
        when(aiProvider.providerName()).thenReturn("test-provider");
        when(aiProvider.modelName()).thenReturn("test-model");
        when(classificationRepository.save(any(NewsClassification.class))).thenAnswer(invocation -> invocation.getArgument(0));

        useCase.execute(new ClassifyNewsCommand(newsArticle.getId()));

        ArgumentCaptor<ClassificationAIRequest> requestCaptor = ArgumentCaptor.forClass(ClassificationAIRequest.class);
        verify(aiProvider).classify(requestCaptor.capture());
        assertEquals("Contenido", requestCaptor.getValue().content());
    }

    private ClassificationAIResponse aiResponse() {
        return aiResponse(ClassificationCategory.SIPRI, "Adjudicaciones", BigDecimal.valueOf(95), ImpactLevel.HIGH, UrgencyLevel.HIGH);
    }

    private ClassificationAIResponse aiResponse(
            ClassificationCategory category,
            String subcategory,
            BigDecimal relevance,
            ImpactLevel impact,
            UrgencyLevel urgency
    ) {
        return new ClassificationAIResponse(
                category,
                subcategory,
                relevance,
                impact,
                urgency,
                relevance.compareTo(BigDecimal.ZERO) == 0 ? null : List.of("SIPRI"),
                relevance.compareTo(BigDecimal.ZERO) == 0 ? null : List.of("Junta de Andalucia"),
                relevance.compareTo(BigDecimal.ZERO) == 0 ? null : "Resumen IA"
        );
    }

    private NewsClassification classification(Long id, Long newsId) {
        return new NewsClassification(
                id,
                newsId,
                ClassificationCategory.SIPRI,
                "Adjudicaciones",
                BigDecimal.valueOf(95),
                ImpactLevel.HIGH,
                UrgencyLevel.HIGH,
                List.of("SIPRI"),
                List.of("Junta de Andalucia"),
                OffsetDateTime.parse("2026-06-06T10:00:00Z")
        );
    }

    private NewsArticle newsArticle() {
        return newsArticle("SIPRI publica adjudicaciones", "Resumen", "Contenido");
    }

    private NewsArticle newsArticle(String title, String summary, String content) {
        OffsetDateTime now = OffsetDateTime.parse("2026-06-06T10:00:00Z");

        return new NewsArticle(
                2L,
                1L,
                title,
                "https://test.example/news/item",
                summary,
                content,
                "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa",
                now.minusHours(1),
                now,
                NewsStatus.CAPTURED,
                now,
                now
        );
    }

    private AiModelExecutionCoordinator coordinator() {
        AiModelExecutionCoordinator coordinator = mock(AiModelExecutionCoordinator.class);
        org.mockito.Mockito.when(coordinator.execute(org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any()))
                .thenAnswer(invocation -> invocation.<java.util.function.Supplier<?>>getArgument(1).get());
        return coordinator;
    }
}
