package es.sindicato.intelligence.dashboard.application;

import es.sindicato.intelligence.content.domain.ContentStatus;
import es.sindicato.intelligence.content.domain.GeneratedContent;
import es.sindicato.intelligence.content.domain.GeneratedContentRepository;
import es.sindicato.intelligence.event.domain.Event;
import es.sindicato.intelligence.event.domain.EventRepository;
import es.sindicato.intelligence.event.domain.EventStatus;
import es.sindicato.intelligence.news.domain.NewsRepository;
import es.sindicato.intelligence.publication.domain.Publication;
import es.sindicato.intelligence.publication.domain.PublicationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
public class DashboardSnapshotUseCase {

    private final NewsRepository newsRepository;
    private final EventRepository eventRepository;
    private final GeneratedContentRepository contentRepository;
    private final PublicationRepository publicationRepository;

    public DashboardSnapshotUseCase(
            NewsRepository newsRepository,
            EventRepository eventRepository,
            GeneratedContentRepository contentRepository,
            PublicationRepository publicationRepository
    ) {
        this.newsRepository = newsRepository;
        this.eventRepository = eventRepository;
        this.contentRepository = contentRepository;
        this.publicationRepository = publicationRepository;
    }

    @Transactional(readOnly = true)
    public DashboardSnapshot execute() {
        List<Event> events = eventRepository.findAll();
        List<GeneratedContent> contents = contentRepository.findAll();
        List<Publication> publications = publicationRepository.findAll();

        long activeEvents = events.stream().filter(Event::isActive).count();
        long pendingContents = contents.stream()
                .filter(content -> content.getStatus() == ContentStatus.PENDING_REVIEW)
                .count();

        List<Event> priorityEvents = events.stream()
                .filter(Event::isActive)
                .sorted(Comparator.comparing(Event::getLastUpdatedAt).reversed())
                .limit(5)
                .toList();

        return new DashboardSnapshot(
                newsRepository.findAll().size(),
                activeEvents,
                pendingContents,
                publications.size(),
                priorityEvents
        );
    }

    public record DashboardSnapshot(
            long capturedNews,
            long activeEvents,
            long pendingContents,
            long publications,
            List<Event> priorityEvents
    ) {
    }
}