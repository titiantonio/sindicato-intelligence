package es.sindicato.intelligence.dashboard.api;

import es.sindicato.intelligence.dashboard.application.DashboardSnapshotUseCase;
import es.sindicato.intelligence.dashboard.application.DashboardSnapshotUseCase.DashboardMetric;
import es.sindicato.intelligence.dashboard.application.DashboardSnapshotUseCase.DashboardSnapshot;
import es.sindicato.intelligence.event.application.EventEditorialStatusResolver;
import es.sindicato.intelligence.event.domain.Event;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/dashboard")
public class DashboardController {

    private final DashboardSnapshotUseCase dashboardSnapshotUseCase;
    private final EventEditorialStatusResolver editorialStatusResolver;

    public DashboardController(
            DashboardSnapshotUseCase dashboardSnapshotUseCase,
            EventEditorialStatusResolver editorialStatusResolver
    ) {
        this.dashboardSnapshotUseCase = dashboardSnapshotUseCase;
        this.editorialStatusResolver = editorialStatusResolver;
    }

    @GetMapping
    public DashboardResponse getDashboard() {
        DashboardSnapshot snapshot = dashboardSnapshotUseCase.execute();
        return new DashboardResponse(
                List.of(
                        newsCard(snapshot),
                        eventsCard(snapshot),
                        contentsCard(snapshot),
                        publicationsCard(snapshot)
                ),
                snapshot.priorityEvents().stream().map(this::toPriorityEvent).toList()
        );
    }

    private MetricCardResponse newsCard(DashboardSnapshot snapshot) {
        DashboardMetric metric = snapshot.capturedNews();
        return toMetricCard(
                "Noticias",
                "Ultima captura",
                "news",
                "primary",
                metric,
                snapshot.lastUpdated().news(),
                List.of(
                        item("Capturadas hoy", metric.todayValue(), "primary", "news", false),
                        item("Diferencia vs anterior", metric.difference(), "success", "trend", true),
                        item("Total acumulado", snapshot.totals().totalNews(), "neutral", "total", false)
                )
        );
    }

    private MetricCardResponse eventsCard(DashboardSnapshot snapshot) {
        DashboardMetric metric = snapshot.detectedEvents();
        return toMetricCard(
                "Eventos",
                "Detectados hoy",
                "target",
                "warning",
                metric,
                snapshot.lastUpdated().events(),
                List.of(
                        item("Detectados hoy", metric.todayValue(), "warning", "trend", false),
                        item("Eventos criticos", snapshot.totals().criticalEvents(), "danger", "alert", false),
                        item("Pendientes de revision", snapshot.totals().pendingContents(), "warning", "clock", false)
                )
        );
    }

    private MetricCardResponse contentsCard(DashboardSnapshot snapshot) {
        DashboardMetric metric = snapshot.pendingContents();
        return toMetricCard(
                "Contenidos",
                "Estado actual",
                "content",
                "success",
                metric,
                snapshot.lastUpdated().contents(),
                List.of(
                        item("Pendientes", snapshot.totals().pendingContents(), "warning", "file", false),
                        item("Generados", snapshot.totals().generatedContents(), "primary", "search", false),
                        item("Aprobados", snapshot.totals().approvedContents(), "success", "check", false)
                )
        );
    }

    private MetricCardResponse publicationsCard(DashboardSnapshot snapshot) {
        DashboardMetric metric = snapshot.publishedPublications();
        return toMetricCard(
                "Publicaciones",
                "Resumen del estado",
                "send",
                "danger",
                metric,
                snapshot.lastUpdated().publications(),
                List.of(
                        item("Publicadas hoy", metric.todayValue(), "success", "check", false),
                        item("Programadas", snapshot.totals().scheduledPublications(), "purple", "calendar", false),
                        item("Fallidas", snapshot.totals().failedPublications(), "danger", "x", false)
                )
        );
    }

    private MetricCardResponse toMetricCard(
            String title,
            String subtitle,
            String icon,
            String tone,
            DashboardMetric metric,
            java.time.OffsetDateTime lastUpdatedAt,
            List<MetricItemResponse> items
    ) {
        return new MetricCardResponse(
                title,
                Long.toString(metric.todayValue()),
                formatDifference(metric.difference()),
                tone,
                metric.todayValue(),
                metric.yesterdayValue(),
                metric.difference(),
                title,
                subtitle,
                icon,
                "Hoy",
                lastUpdatedAt,
                items
        );
    }

    private MetricItemResponse item(String label, long value, String tone, String icon, boolean signed) {
        return new MetricItemResponse(label, value, tone, icon, signed);
    }

    private String formatDifference(long difference) {
        return difference > 0 ? "+" + difference : Long.toString(difference);
    }

    private PriorityEventResponse toPriorityEvent(Event event) {
        return new PriorityEventResponse(
                event.getId(),
                event.getTitle(),
                event.getCategory(),
                event.getImportance(),
                event.getNewsIds().size(),
                event.getLastUpdatedAt(),
                event.getStatus(),
                editorialStatusResolver.resolve(event)
        );
    }
}
