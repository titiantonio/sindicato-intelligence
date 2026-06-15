import { FormsModule } from '@angular/forms';
import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';

import { EventListItem } from '../../core/models/event.models';
import { EventService } from '../../core/services/event.service';
import { StatusBadgeComponent } from '../../shared/components/status-badge/status-badge.component';

type EventSortColumn = 'id' | 'title' | 'category' | 'importance' | 'newsCount' | 'status' | 'updatedAt';
type SortDirection = 'asc' | 'desc';

@Component({
  selector: 'app-events-page',
  imports: [FormsModule, RouterLink, StatusBadgeComponent],
  templateUrl: './events-page.component.html',
  styleUrl: './events-page.component.scss'
})
export class EventsPageComponent implements OnInit {
  private readonly eventService = inject(EventService);

  protected readonly events = signal<EventListItem[]>([]);
  protected readonly isLoading = signal(false);
  protected readonly errorMessage = signal<string | null>(null);
  protected readonly successMessage = signal<string | null>(null);
  protected readonly targetEventId = signal<number | null>(null);
  protected readonly sourceEventIds = signal<number[]>([]);
  protected readonly activeEvents = computed(() => this.events().filter((event) => event.status === 'OPEN' || event.status === 'MONITORING'));
  protected readonly globalFilter = signal('');
  protected readonly idFilter = signal('');
  protected readonly titleFilter = signal('');
  protected readonly categoryFilter = signal('');
  protected readonly importanceFilter = signal('');
  protected readonly newsCountFilter = signal('');
  protected readonly statusFilter = signal('');
  protected readonly updatedAtFilter = signal('');
  protected readonly sortColumn = signal<EventSortColumn>('updatedAt');
  protected readonly sortDirection = signal<SortDirection>('desc');
  protected readonly pageSize = signal(10);
  protected readonly currentPage = signal(1);
  protected readonly pageSizeOptions = [5, 10, 25, 50];
  protected readonly categoryOptions = computed(() => this.uniqueOptions((event) => event.category));
  protected readonly importanceOptions = computed(() => this.uniqueOptions((event) => event.importance));
  protected readonly statusOptions = computed(() => this.uniqueOptions((event) => event.status));
  protected readonly displayedEvents = computed(() => this.sortEvents(this.filterEvents(this.events())));
  protected readonly totalPages = computed(() => Math.max(1, Math.ceil(this.displayedEvents().length / this.pageSize())));
  protected readonly paginatedEvents = computed(() => {
    const page = Math.min(this.currentPage(), this.totalPages());
    const start = (page - 1) * this.pageSize();
    return this.displayedEvents().slice(start, start + this.pageSize());
  });

  ngOnInit(): void {
    this.loadEvents();
  }

  protected loadEvents(): void {
    this.isLoading.set(true);
    this.errorMessage.set(null);

    this.eventService.listEvents().subscribe({
      next: (events) => {
        this.events.set(events);
        if (this.targetEventId() === null && this.activeEvents().length > 0) {
          this.targetEventId.set(this.activeEvents()[0].id);
        }
        this.currentPage.set(Math.min(this.currentPage(), this.totalPages()));
        this.isLoading.set(false);
      },
      error: (error: { error?: { error?: string } }) => {
        this.errorMessage.set(error.error?.error ?? 'No se pudo cargar el listado de eventos.');
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

  protected setGlobalFilter(value: string): void {
    this.globalFilter.set(value);
    this.resetPagination();
  }

  protected setIdFilter(value: string): void {
    this.idFilter.set(value);
    this.resetPagination();
  }

  protected setTitleFilter(value: string): void {
    this.titleFilter.set(value);
    this.resetPagination();
  }

  protected setCategoryFilter(value: string): void {
    this.categoryFilter.set(value);
    this.resetPagination();
  }

  protected setImportanceFilter(value: string): void {
    this.importanceFilter.set(value);
    this.resetPagination();
  }

  protected setNewsCountFilter(value: string): void {
    this.newsCountFilter.set(value);
    this.resetPagination();
  }

  protected setStatusFilter(value: string): void {
    this.statusFilter.set(value);
    this.resetPagination();
  }

  protected setUpdatedAtFilter(value: string): void {
    this.updatedAtFilter.set(value);
    this.resetPagination();
  }

  protected changeSort(column: EventSortColumn): void {
    if (this.sortColumn() === column) {
      this.sortDirection.update((direction) => direction === 'asc' ? 'desc' : 'asc');
      return;
    }

    this.sortColumn.set(column);
    this.sortDirection.set(column === 'updatedAt' ? 'desc' : 'asc');
  }

  protected sortLabel(column: EventSortColumn): string {
    if (this.sortColumn() !== column) {
      return '';
    }

    return this.sortDirection() === 'asc' ? '↑' : '↓';
  }

  protected setTargetEventId(value: string): void {
    const targetId = Number(value);
    this.targetEventId.set(Number.isNaN(targetId) ? null : targetId);
    this.sourceEventIds.update((ids) => ids.filter((id) => id !== targetId));
  }

  protected toggleSourceEvent(eventId: number, checked: boolean): void {
    this.sourceEventIds.update((ids) => {
      const currentIds = ids.filter((id) => id !== eventId);
      return checked ? [...currentIds, eventId] : currentIds;
    });
  }

  protected isSourceSelected(eventId: number): boolean {
    return this.sourceEventIds().includes(eventId);
  }

  protected mergeEvents(): void {
    const targetId = this.targetEventId();
    const sourceIds = this.sourceEventIds();

    if (targetId === null || sourceIds.length === 0) {
      this.errorMessage.set('Selecciona un evento destino y al menos un evento origen.');
      return;
    }

    if (!confirm('La fusion archivara los eventos origen y movera sus noticias al evento destino.')) {
      return;
    }

    this.errorMessage.set(null);
    this.successMessage.set(null);
    this.eventService.mergeEvents(targetId, sourceIds).subscribe({
      next: (mergedEvent) => {
        this.successMessage.set(`Eventos fusionados correctamente en #${mergedEvent.id}.`);
        this.sourceEventIds.set([]);
        this.targetEventId.set(mergedEvent.id);
        this.loadEvents();
      },
      error: (error: { error?: { error?: string } }) => {
        this.errorMessage.set(error.error?.error ?? 'No se pudo fusionar los eventos.');
      }
    });
  }

  protected setPageSize(value: string): void {
    this.pageSize.set(Number(value));
    this.resetPagination();
  }

  protected goToPreviousPage(): void {
    this.currentPage.update((page) => Math.max(1, page - 1));
  }

  protected goToNextPage(): void {
    this.currentPage.update((page) => Math.min(this.totalPages(), page + 1));
  }

  private filterEvents(events: EventListItem[]): EventListItem[] {
    return events.filter((event) => this.matchesGlobalFilter(event))
      .filter((event) => this.matchesText(event.id.toString(), this.idFilter()))
      .filter((event) => this.matchesText(event.title, this.titleFilter()))
      .filter((event) => this.matchesSelect(event.category, this.categoryFilter()))
      .filter((event) => this.matchesSelect(event.importance, this.importanceFilter()))
      .filter((event) => this.matchesText(event.newsCount.toString(), this.newsCountFilter()))
      .filter((event) => this.matchesSelect(event.status, this.statusFilter()))
      .filter((event) => this.matchesText(this.formatDate(event.updatedAt), this.updatedAtFilter()));
  }

  private matchesGlobalFilter(event: EventListItem): boolean {
    const filter = this.normalize(this.globalFilter());
    if (!filter) {
      return true;
    }

    return [
      event.id.toString(),
      `#${event.id}`,
      event.title,
      event.category,
      event.importance,
      event.newsCount.toString(),
      event.status,
      event.updatedAt,
      this.formatDate(event.updatedAt)
    ].some((value) => this.normalize(value).includes(filter));
  }

  private sortEvents(events: EventListItem[]): EventListItem[] {
    const direction = this.sortDirection() === 'asc' ? 1 : -1;
    const column = this.sortColumn();

    return [...events].sort((left, right) => direction * this.compareEvents(left, right, column));
  }

  private compareEvents(left: EventListItem, right: EventListItem, column: EventSortColumn): number {
    if (column === 'id' || column === 'newsCount') {
      return left[column] - right[column];
    }

    if (column === 'updatedAt') {
      return new Date(left.updatedAt).getTime() - new Date(right.updatedAt).getTime();
    }

    return left[column].localeCompare(right[column], 'es', { sensitivity: 'base' });
  }

  private matchesText(value: string, filter: string): boolean {
    const normalizedFilter = this.normalize(filter);
    return !normalizedFilter || this.normalize(value).includes(normalizedFilter);
  }

  private matchesSelect(value: string, filter: string): boolean {
    return !filter || value === filter;
  }

  private uniqueOptions(selector: (event: EventListItem) => string): string[] {
    return [...new Set(this.events().map(selector))].sort((left, right) => left.localeCompare(right, 'es'));
  }

  private normalize(value: string): string {
    return value.trim().toLocaleLowerCase('es');
  }

  private resetPagination(): void {
    this.currentPage.set(1);
  }
}
