package es.sindicato.intelligence.dashboard.api;

public record MetricItemResponse(
        String label,
        long value,
        String tone,
        String icon,
        boolean signed
) {
}
