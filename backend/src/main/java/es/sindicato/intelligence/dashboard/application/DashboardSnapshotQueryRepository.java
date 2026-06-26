package es.sindicato.intelligence.dashboard.application;

public interface DashboardSnapshotQueryRepository {

    DashboardSnapshotUseCase.DashboardSnapshot loadSnapshot();
}
