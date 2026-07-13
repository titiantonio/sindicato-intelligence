package es.sindicato.intelligence.event.api;

import es.sindicato.intelligence.analysis.domain.EventAIAnalysis;
import es.sindicato.intelligence.classification.domain.NewsClassification;
import es.sindicato.intelligence.content.api.GeneratedContentResponse;
import es.sindicato.intelligence.content.domain.GeneratedContent;
import es.sindicato.intelligence.event.application.EventEditorialStatusResolver;
import es.sindicato.intelligence.event.application.GetEventDetailUseCase.EventDetail;
import es.sindicato.intelligence.event.application.GetEventDetailUseCase.EventNewsDetail;
import es.sindicato.intelligence.event.domain.Event;
import es.sindicato.intelligence.news.domain.NewsArticle;
import org.springframework.stereotype.Component;

@Component
public class EventResponseMapper {

    private final EventEditorialStatusResolver editorialStatusResolver;

    public EventResponseMapper(EventEditorialStatusResolver editorialStatusResolver) {
        this.editorialStatusResolver = editorialStatusResolver;
    }

    public EventDetailResponse toDetailResponse(EventDetail detail) {
        Event event = detail.event();
        return new EventDetailResponse(
                event.getId(),
                event.getTitle(),
                event.getDescription(),
                event.getCategory(),
                event.getImportance(),
                event.getStatus(),
                editorialStatusResolver.resolve(event),
                event.getNewsIds().size(),
                event.getFirstDetectedAt(),
                event.getLastUpdatedAt(),
                event.getCreatedAt(),
                event.getUpdatedAt(),
                detail.news().stream().map(this::toNewsResponse).toList(),
                detail.analyses().stream().map(analysis -> toAnalysisResponse(event, analysis)).toList(),
                detail.contents().stream().map(this::toContentResponse).toList()
        );
    }

    public EventNewsResponse toNewsResponse(EventNewsDetail detail) {
        NewsArticle newsArticle = detail.newsArticle();
        return new EventNewsResponse(
                newsArticle.getId(),
                newsArticle.getSourceId(),
                newsArticle.getTitle(),
                newsArticle.getUrl(),
                newsArticle.getSummary(),
                newsArticle.getProcessingStatus(),
                newsArticle.getPublishedAt(),
                newsArticle.getCapturedAt(),
                detail.classification() == null ? null : toClassificationResponse(detail.classification())
        );
    }

    public EventNewsClassificationResponse toClassificationResponse(NewsClassification classification) {
        return new EventNewsClassificationResponse(
                classification.getId(),
                classification.getNewsId(),
                classification.getCategory(),
                classification.getSubcategory(),
                classification.getRelevanceScore(),
                classification.getImpactLevel(),
                classification.getUrgencyLevel(),
                classification.getKeywords(),
                classification.getEntities(),
                classification.getClassifiedAt()
        );
    }

    public EventAnalysisResponse toAnalysisResponse(Event event, EventAIAnalysis analysis) {
        return new EventAnalysisResponse(
                analysis.getId(),
                analysis.getEventId(),
                analysis.getExecutiveSummary(),
                analysis.getUnionSummary(),
                analysis.getKeyPoints(),
                analysis.getRisks(),
                analysis.getOpportunities(),
                analysis.getAffectedGroups(),
                analysis.getRecommendedMonitoring(),
                analysis.getAnalysisType().name(),
                analysis.getGenerationTrigger().name(),
                analysis.getEventUpdatedAtSnapshot(),
                analysis.getContextNewsCount(),
                analysis.isContextTruncated(),
                analysis.isOutdatedFor(event.getUpdatedAt()),
                analysis.getModelUsed(),
                analysis.getGeneratedAt()
        );
    }

    public GeneratedContentResponse toContentResponse(GeneratedContent content) {
        return new GeneratedContentResponse(
                content.getId(),
                content.getEventId(),
                content.getAnalysisId(),
                content.getCreatedBy(),
                content.getChannel(),
                content.getTone(),
                content.getTitle(),
                content.getContent(),
                content.getStatus(),
                content.getGeneratedAt(),
                content.getApprovedAt()
        );
    }
}
