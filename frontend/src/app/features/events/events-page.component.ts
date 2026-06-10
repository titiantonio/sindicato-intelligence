import { Component, computed, inject } from '@angular/core';

import { MockDashboardService } from '../../core/services/mock-dashboard.service';
import { StatusBadgeComponent } from '../../shared/components/status-badge/status-badge.component';

@Component({
  selector: 'app-events-page',
  imports: [StatusBadgeComponent],
  templateUrl: './events-page.component.html',
  styleUrl: './events-page.component.scss'
})
export class EventsPageComponent {
  private readonly mockDashboardService = inject(MockDashboardService);

  protected readonly events = computed(() => this.mockDashboardService.getEvents());
}
