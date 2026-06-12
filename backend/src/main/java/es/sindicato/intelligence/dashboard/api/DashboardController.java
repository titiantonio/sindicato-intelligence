package es.sindicato.intelligence.dashboard.api;

import es.sindicato.intelligence.dashboard.application.DashboardSnapshotUseCase;
import es.sindicato.intelligence.dashboard.application.DashboardSnapshotUseCase.DashboardSnapshot;
import es.sindicato.intelligence.event.domain.Event;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/dashboard")
public class DashboardController {

    private final DashboardSnapshotUseCase dashboardSnapshotUseCase;

    public DashboardController(DashboardSnapshotUseCase dashboardSnapshotUseCase) {
        this.dashboardSnapshotUseCase = dashboardSnapshotUseCase;
    }

    @GetMapping
    public DashboardResponse getDashboard() {
        DashboardSnapshot snapshot = dashboardSnapshotUseCase.execute();
        return new DashboardResponse(
                List.of(
                        new MetricCardResponse("Noticias capturadas", Long.toString(snapshot.capturedNews()), "Total historico", "primary"),
                        new MetricCardResponse("Eventos activos", Long.toString(snapshot.activeEvents()), "OPEN + MONITORING", "warning"),
                        new MetricCardResponse("Contenidos pendientes", Long.toString(snapshot.pendingContents()), "PENDING_REVIEW", "success"),
                        new MetricCardResponse("Publicaciones registradas", Long.toString(snapshot.publications()), "Historico", "danger")
                ),
                snapshot.priorityEvents().stream().map(this::toPriorityEvent).toList()
        );
    }

    private PriorityEventResponse toPriorityEvent(Event event) {
        return new PriorityEventResponse(
                event.getId(),
                event.getTitle(),
                event.getCategory(),
                event.getImportance(),
                event.getNewsIds().size(),
                event.getLastUpdatedAt(),
                event.getStatus()
        );
    }
}