import { Component, OnInit, inject, signal } from '@angular/core';

import { MetricCardComponent } from '../../shared/components/metric-card/metric-card.component';
import { StatusBadgeComponent } from '../../shared/components/status-badge/status-badge.component';
import { DashboardService } from '../../core/services/dashboard.service';
import { MetricCard, PriorityEvent } from '../../core/models/dashboard.models';

@Component({
  selector: 'app-dashboard-page',
  imports: [MetricCardComponent, StatusBadgeComponent],
  templateUrl: './dashboard-page.component.html',
  styleUrl: './dashboard-page.component.scss'
})
export class DashboardPageComponent implements OnInit {
  private readonly dashboardService = inject(DashboardService);

  protected readonly metricCards = signal<MetricCard[]>([]);
  protected readonly priorityEvents = signal<PriorityEvent[]>([]);
  protected readonly isLoading = signal(false);
  protected readonly errorMessage = signal<string | null>(null);

  ngOnInit(): void {
    this.loadDashboard();
  }

  protected loadDashboard(): void {
    this.isLoading.set(true);
    this.errorMessage.set(null);

    this.dashboardService.getDashboard().subscribe({
      next: (dashboard) => {
        this.metricCards.set(dashboard.metricCards);
        this.priorityEvents.set(dashboard.priorityEvents);
        this.isLoading.set(false);
      },
      error: (error: { error?: { error?: string } }) => {
        this.errorMessage.set(error.error?.error ?? 'No se pudo cargar el dashboard.');
        this.isLoading.set(false);
      }
    });
  }

  protected formatDate(value: string): string {
    return new Intl.DateTimeFormat('es-ES', {
      dateStyle: 'short',
      timeStyle: 'short'
    }).format(new Date(value));
  }
}