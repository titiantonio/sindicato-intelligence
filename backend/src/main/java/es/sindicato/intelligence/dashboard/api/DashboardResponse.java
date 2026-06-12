package es.sindicato.intelligence.dashboard.api;

import java.util.List;

public record DashboardResponse(
        List<MetricCardResponse> metricCards,
        List<PriorityEventResponse> priorityEvents
) {
}