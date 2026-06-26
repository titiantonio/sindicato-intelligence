package es.sindicato.intelligence.event.application;

import java.util.List;

public interface EventSummaryQueryRepository {

    List<EventSummaryView> findVisibleSummaries();
}
