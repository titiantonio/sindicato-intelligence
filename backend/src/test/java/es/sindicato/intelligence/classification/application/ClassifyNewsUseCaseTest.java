package es.sindicato.intelligence.classification.application;

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
        ClassifyNewsUseCase useCase = new ClassifyNewsUseCase(newsRepository, classificationRepository, promptBuilder, aiProvider, metricsRecorder);
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
        ClassifyNewsUseCase useCase = new ClassifyNewsUseCase(newsRepository, classificationRepository, promptBuilder, aiProvider, metricsRecorder);
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
        assertEquals(NewsStatus.DISCARDED, newsArticle.getProcessingStatus());
        assertEquals("DISCARDED", detailsCaptor.getValue().get("finalNewsStatus"));
        assertEquals("FUERA_DE_AMBITO", detailsCaptor.getValue().get("discardReason"));
    }

    @Test
    void marksInsufficientInformationNewsAsDiscardedAfterPersistingClassification() {
        NewsRepository newsRepository = mock(NewsRepository.class);
        NewsClassificationRepository classificationRepository = mock(NewsClassificationRepository.class);
        ClassifyNewsPromptBuilder promptBuilder = new ClassifyNewsPromptBuilder();
        AIProvider aiProvider = mock(AIProvider.class);
        AiOperationMetricsRecorder metricsRecorder = mock(AiOperationMetricsRecorder.class);
        ClassifyNewsUseCase useCase = new ClassifyNewsUseCase(newsRepository, classificationRepository, promptBuilder, aiProvider, metricsRecorder);
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
    void rejectsUnknownNews() {
        NewsRepository newsRepository = mock(NewsRepository.class);
        NewsClassificationRepository classificationRepository = mock(NewsClassificationRepository.class);
        ClassifyNewsUseCase useCase = new ClassifyNewsUseCase(newsRepository, classificationRepository, new ClassifyNewsPromptBuilder(), mock(AIProvider.class), mock(AiOperationMetricsRecorder.class));

        when(newsRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> useCase.execute(new ClassifyNewsCommand(1L)));

        verify(classificationRepository, never()).save(any(NewsClassification.class));
    }

    @Test
    void rejectsAlreadyClassifiedNews() {
        NewsRepository newsRepository = mock(NewsRepository.class);
        NewsClassificationRepository classificationRepository = mock(NewsClassificationRepository.class);
        ClassifyNewsUseCase useCase = new ClassifyNewsUseCase(newsRepository, classificationRepository, new ClassifyNewsPromptBuilder(), mock(AIProvider.class), mock(AiOperationMetricsRecorder.class));
        NewsArticle newsArticle = newsArticle();

        when(newsRepository.findById(newsArticle.getId())).thenReturn(Optional.of(newsArticle));
        when(classificationRepository.existsByNewsId(newsArticle.getId())).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> useCase.execute(new ClassifyNewsCommand(newsArticle.getId())));

        verify(classificationRepository, never()).save(any(NewsClassification.class));
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
                List.of("SIPRI"),
                List.of("Junta de Andalucia"),
                "Resumen IA"
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
        OffsetDateTime now = OffsetDateTime.parse("2026-06-06T10:00:00Z");

        return new NewsArticle(
                2L,
                1L,
                "SIPRI publica adjudicaciones",
                "https://test.example/news/sipri",
                "Resumen",
                "Contenido",
                "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa",
                now.minusHours(1),
                now,
                NewsStatus.CAPTURED,
                now,
                now
        );
    }
}
