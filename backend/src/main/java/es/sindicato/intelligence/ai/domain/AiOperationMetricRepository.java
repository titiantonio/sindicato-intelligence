package es.sindicato.intelligence.ai.domain;

import java.util.List;

public interface AiOperationMetricRepository {

    AiOperationMetric save(AiOperationMetric metric);

    List<AiOperationMetric> findRecent(int limit);
}
