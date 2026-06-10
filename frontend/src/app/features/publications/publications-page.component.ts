import { Component, computed, inject } from '@angular/core';

import { MockDashboardService } from '../../core/services/mock-dashboard.service';
import { StatusBadgeComponent } from '../../shared/components/status-badge/status-badge.component';

@Component({
  selector: 'app-publications-page',
  imports: [StatusBadgeComponent],
  templateUrl: './publications-page.component.html',
  styleUrl: './publications-page.component.scss'
})
export class PublicationsPageComponent {
  private readonly mockDashboardService = inject(MockDashboardService);

  protected readonly publications = computed(() => this.mockDashboardService.getPublications());
}
