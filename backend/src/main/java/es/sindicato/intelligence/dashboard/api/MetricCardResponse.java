package es.sindicato.intelligence.dashboard.api;

import java.time.OffsetDateTime;
import java.util.List;

public record MetricCardResponse(
        String label,
        String value,
        String trend,
        String tone,
        long todayValue,
        long yesterdayValue,
        long difference,
        String title,
        String subtitle,
        String icon,
        String badgeLabel,
        OffsetDateTime lastUpdatedAt,
        List<MetricItemResponse> items
) {
}
