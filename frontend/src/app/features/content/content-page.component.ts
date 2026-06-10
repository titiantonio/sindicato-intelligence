import { Component, computed, inject } from '@angular/core';

import { MockDashboardService } from '../../core/services/mock-dashboard.service';
import { StatusBadgeComponent } from '../../shared/components/status-badge/status-badge.component';

@Component({
  selector: 'app-content-page',
  imports: [StatusBadgeComponent],
  templateUrl: './content-page.component.html',
  styleUrl: './content-page.component.scss'
})
export class ContentPageComponent {
  private readonly mockDashboardService = inject(MockDashboardService);

  protected readonly items = computed(() => this.mockDashboardService.getContent());
}
