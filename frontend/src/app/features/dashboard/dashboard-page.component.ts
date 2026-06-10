import { Component, computed, inject } from '@angular/core';

import { MetricCardComponent } from '../../shared/components/metric-card/metric-card.component';
import { StatusBadgeComponent } from '../../shared/components/status-badge/status-badge.component';
import { MockDashboardService } from '../../core/services/mock-dashboard.service';

@Component({
  selector: 'app-dashboard-page',
  imports: [MetricCardComponent, StatusBadgeComponent],
  templateUrl: './dashboard-page.component.html',
  styleUrl: './dashboard-page.component.scss'
})
export class DashboardPageComponent {
  private readonly mockDashboardService = inject(MockDashboardService);

  protected readonly metricCards = computed(() => this.mockDashboardService.getMetricCards());
  protected readonly priorityEvents = computed(() => this.mockDashboardService.getPriorityEvents());
}
