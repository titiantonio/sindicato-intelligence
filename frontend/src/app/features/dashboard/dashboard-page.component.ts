import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { ButtonModule } from 'primeng/button';
import { InputTextModule } from 'primeng/inputtext';
import { MessageModule } from 'primeng/message';
import { SelectModule } from 'primeng/select';

import { MetricCardComponent } from '../../shared/components/metric-card/metric-card.component';
import { StandardTableComponent } from '../../shared/components/standard-table/standard-table.component';
import { StatusBadgeComponent } from '../../shared/components/status-badge/status-badge.component';
import { AutomationService } from '../../core/services/automation.service';
import { DashboardService } from '../../core/services/dashboard.service';
import { AutomationRunResult } from '../../core/models/automation.models';
import { MetricCard, PriorityEvent } from '../../core/models/dashboard.models';

type PriorityEventSortColumn = 'title' | 'category' | 'importance' | 'relatedNews' | 'updatedAt' | 'status';
type SortDirection = 'asc' | 'desc';
type AutomationAction = 'classifications' | 'events' | 'analysis' | `analysis-${number}`;

@Component({
  selector: 'app-dashboard-page',
  imports: [
    ButtonModule,
    FormsModule,
    InputTextModule,
    MessageModule,
    MetricCardComponent,
    RouterLink,
    SelectModule,
    StandardTableComponent,
    StatusBadgeComponent
  ],
  templateUrl: './dashboard-page.component.html',
  styleUrl: './dashboard-page.component.scss'
})
export class DashboardPageComponent implements OnInit {
  private readonly automationService = inject(AutomationService);
  private readonly dashboardService = inject(DashboardService);

  protected readonly metricCards = signal<MetricCard[]>([]);
  protected readonly priorityEvents = signal<PriorityEvent[]>([]);
  protected readonly isLoading = signal(false);
  protected readonly errorMessage = signal<string | null>(null);
  protected readonly automationMessage = signal<string | null>(null);
  protected readonly automationError = signal<string | null>(null);
  protected readonly runningAutomation = signal<AutomationAction | null>(null);
  protected readonly titleFilter = signal('');
  protected readonly categoryFilter = signal('');
  protected readonly importanceFilter = signal('');
  protected readonly relatedNewsFilter = signal('');
  protected readonly updatedAtFilter = signal('');
  protected readonly statusFilter = signal('');
  protected readonly sortColumn = signal<PriorityEventSortColumn>('importance');
  protected readonly sortDirection = signal<SortDirection>('desc');
  protected readonly pageSize = signal(10);
  protected readonly currentPage = signal(1);
  protected readonly pageSizeOptions = [5, 10, 25, 50];
  protected readonly importanceOptions = ['CRITICAL', 'HIGH'];
  protected readonly statusOptions = computed(() => this.uniqueOptions((event) => event.status));
  protected readonly displayedPriorityEvents = computed(() => this.sortEvents(this.filterEvents(this.priorityEvents())));
  protected readonly totalPages = computed(() => Math.max(1, Math.ceil(this.displayedPriorityEvents().length / this.pageSize())));
  protected readonly paginatedPriorityEvents = computed(() => {
    const page = Math.min(this.currentPage(), this.totalPages());
    const start = (page - 1) * this.pageSize();
    return this.displayedPriorityEvents().slice(start, start + this.pageSize());
  });

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
        this.currentPage.set(Math.min(this.currentPage(), this.totalPages()));
        this.isLoading.set(false);
      },
      error: (error: { error?: { error?: string } }) => {
        this.errorMessage.set(error.error?.error ?? 'No se pudo cargar el dashboard.');
        this.isLoading.set(false);
      }
    });
  }

  protected runClassifications(): void {
    this.runAutomation('classifications', this.automationService.runClassifications());
  }

  protected runEventDetection(): void {
    this.runAutomation('events', this.automationService.runEventDetection());
  }

  protected runPendingAnalysis(): void {
    this.runAutomation('analysis', this.automationService.runAnalysis());
  }

  protected runEventAnalysis(event: Event, eventId: number): void {
    event.stopPropagation();
    this.runAutomation(`analysis-${eventId}`, this.automationService.runAnalysis(eventId));
  }

  protected isRunningAutomation(action: AutomationAction): boolean {
    return this.runningAutomation() === action;
  }

  protected isRunningEventAnalysis(eventId: number): boolean {
    return this.runningAutomation() === `analysis-${eventId}`;
  }

  protected formatDate(value: string): string {
    return new Intl.DateTimeFormat('es-ES', {
      dateStyle: 'short',
      timeStyle: 'short'
    }).format(new Date(value));
  }

  protected setTitleFilter(value: string): void { this.titleFilter.set(value); this.currentPage.set(1); }
  protected setCategoryFilter(value: string): void { this.categoryFilter.set(value); this.currentPage.set(1); }
  protected setImportanceFilter(value: string): void { this.importanceFilter.set(value); this.currentPage.set(1); }
  protected setRelatedNewsFilter(value: string): void { this.relatedNewsFilter.set(value); this.currentPage.set(1); }
  protected setUpdatedAtFilter(value: string): void { this.updatedAtFilter.set(value); this.currentPage.set(1); }
  protected setStatusFilter(value: string): void { this.statusFilter.set(value); this.currentPage.set(1); }

  protected changeSort(column: PriorityEventSortColumn): void {
    if (this.sortColumn() === column) {
      this.sortDirection.update((direction) => direction === 'asc' ? 'desc' : 'asc');
      return;
    }
    this.sortColumn.set(column);
    this.sortDirection.set(column === 'updatedAt' ? 'desc' : 'asc');
  }

  protected sortLabel(column: PriorityEventSortColumn): string {
    return this.sortColumn() === column ? this.sortDirection().toUpperCase() : '';
  }

  protected setPageSize(value: string): void {
    this.pageSize.set(Number(value));
    this.currentPage.set(1);
  }

  protected goToPreviousPage(): void {
    this.currentPage.update((page) => Math.max(1, page - 1));
  }

  protected goToNextPage(): void {
    this.currentPage.update((page) => Math.min(this.totalPages(), page + 1));
  }

  private filterEvents(events: PriorityEvent[]): PriorityEvent[] {
    return events
      .filter((event) => this.matchesText(event.title, this.titleFilter()))
      .filter((event) => this.matchesText(event.category, this.categoryFilter()))
      .filter((event) => this.matchesSelect(event.importance, this.importanceFilter()))
      .filter((event) => this.matchesText(event.relatedNews.toString(), this.relatedNewsFilter()))
      .filter((event) => this.matchesText(this.formatDate(event.updatedAt), this.updatedAtFilter()))
      .filter((event) => this.matchesSelect(event.status, this.statusFilter()));
  }

  private sortEvents(events: PriorityEvent[]): PriorityEvent[] {
    const direction = this.sortDirection() === 'asc' ? 1 : -1;
    const column = this.sortColumn();
    return [...events].sort((left, right) => direction * this.compareEvents(left, right, column));
  }

  private compareEvents(left: PriorityEvent, right: PriorityEvent, column: PriorityEventSortColumn): number {
    if (column === 'relatedNews') {
      return left.relatedNews - right.relatedNews;
    }
    if (column === 'updatedAt') {
      return new Date(left.updatedAt).getTime() - new Date(right.updatedAt).getTime();
    }
    if (column === 'importance') {
      const importanceComparison = this.importanceScore(left.importance) - this.importanceScore(right.importance);
      return importanceComparison !== 0 ? importanceComparison : left.relatedNews - right.relatedNews;
    }
    return left[column].localeCompare(right[column], 'es', { sensitivity: 'base' });
  }

  private matchesText(value: string, filter: string): boolean {
    const normalizedFilter = filter.trim().toLocaleLowerCase('es');
    return !normalizedFilter || value.trim().toLocaleLowerCase('es').includes(normalizedFilter);
  }

  private matchesSelect(value: string, filter: string): boolean {
    return !filter || value === filter;
  }

  private uniqueOptions(selector: (event: PriorityEvent) => string): string[] {
    return [...new Set(this.priorityEvents().map(selector))].sort((left, right) => left.localeCompare(right, 'es'));
  }

  private importanceScore(importance: string): number {
    const scores: Record<string, number> = {
      LOW: 1,
      MEDIUM: 2,
      HIGH: 3,
      CRITICAL: 4
    };
    return scores[importance] ?? 0;
  }

  private runAutomation(action: AutomationAction, request: ReturnType<AutomationService['runClassifications']>): void {
    this.runningAutomation.set(action);
    this.automationMessage.set(null);
    this.automationError.set(null);

    request.subscribe({
      next: (result) => {
        this.automationMessage.set(this.formatAutomationResult(result));
        this.runningAutomation.set(null);
        this.loadDashboard();
      },
      error: (error: { error?: { error?: string } }) => {
        this.automationError.set(error.error?.error ?? 'No se pudo ejecutar la automatizacion.');
        this.runningAutomation.set(null);
      }
    });
  }

  private formatAutomationResult(result: AutomationRunResult): string {
    return `Procesados ${result.processedCount}. Correctos ${result.successCount}. Fallidos ${result.failedCount}. Omitidos ${result.skippedCount}.`;
  }
}
