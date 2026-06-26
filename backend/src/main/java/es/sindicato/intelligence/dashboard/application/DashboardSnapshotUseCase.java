package es.sindicato.intelligence.dashboard.application;

import es.sindicato.intelligence.event.application.EventEditorialStatus;
import es.sindicato.intelligence.event.domain.EventCategory;
import es.sindicato.intelligence.event.domain.EventStatus;
import es.sindicato.intelligence.event.domain.Importance;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;

@Service
public class DashboardSnapshotUseCase {

    private final DashboardSnapshotQueryRepository dashboardSnapshotQueryRepository;

    public DashboardSnapshotUseCase(DashboardSnapshotQueryRepository dashboardSnapshotQueryRepository) {
        this.dashboardSnapshotQueryRepository = dashboardSnapshotQueryRepository;
    }

    @Transactional(readOnly = true)
    public DashboardSnapshot execute() {
        return dashboardSnapshotQueryRepository.loadSnapshot();
    }

    public record DashboardSnapshot(
            DashboardMetric capturedNews,
            DashboardMetric detectedEvents,
            DashboardMetric pendingContents,
            DashboardMetric publishedPublications,
            DashboardTotals totals,
            DashboardLastUpdated lastUpdated,
            List<PriorityEventView> priorityEvents
    ) {
    }

    public record DashboardMetric(
            String key,
            long todayValue,
            long yesterdayValue,
            long difference
    ) {
    }

    public record DashboardTotals(
            long totalNews,
            long criticalEvents,
            long pendingContents,
            long generatedContents,
            long approvedContents,
            long scheduledPublications,
            long failedPublications
    ) {
    }

    public record DashboardLastUpdated(
            OffsetDateTime news,
            OffsetDateTime events,
            OffsetDateTime contents,
            OffsetDateTime publications
    ) {
    }

    public record PriorityEventView(
            Long id,
            String title,
            EventCategory category,
            Importance importance,
            int relatedNews,
            OffsetDateTime updatedAt,
            EventStatus status,
            EventEditorialStatus editorialStatus
    ) {
    }
}
