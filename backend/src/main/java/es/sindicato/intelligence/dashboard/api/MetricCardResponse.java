package es.sindicato.intelligence.dashboard.api;

public record MetricCardResponse(
        String label,
        String value,
        String trend,
        String tone
) {
}