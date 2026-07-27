import { DOCUMENT } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { ButtonModule } from 'primeng/button';
import { DialogModule } from 'primeng/dialog';
import { InputTextModule } from 'primeng/inputtext';
import { MessageModule } from 'primeng/message';
import { SelectModule } from 'primeng/select';

import { EventListItem } from '../../core/models/event.models';
import { EventService } from '../../core/services/event.service';
import { ExpandableTextComponent } from '../../shared/components/expandable-text/expandable-text.component';
import { StandardTableComponent } from '../../shared/components/standard-table/standard-table.component';
import { StatusBadgeComponent } from '../../shared/components/status-badge/status-badge.component';

type EventSortColumn = 'id' | 'title' | 'category' | 'importance' | 'newsCount' | 'status' | 'editorialStatus' | 'updatedAt';
type SortDirection = 'asc' | 'desc';
type PendingConfirmation =
  | { type: 'merge'; title: string; message: string; confirmLabel: string }
  | { type: 'discard'; event: EventListItem; title: string; message: string; confirmLabel: string }
  | { type: 'restore'; event: EventListItem; title: string; message: string; confirmLabel: string };

@Component({
  selector: 'app-events-page',
  imports: [ButtonModule, DialogModule, ExpandableTextComponent, FormsModule, InputTextModule, MessageModule, RouterLink, SelectModule, StandardTableComponent, StatusBadgeComponent],
  templateUrl: './events-page.component.html',
  styleUrls: [
    './events-page.component.scss',
    './events-workspace.component.scss',
    './events-merge.component.scss'
  ]
})
export class EventsPageComponent implements OnInit {
  private readonly document = inject(DOCUMENT);
  private readonly eventService = inject(EventService);
  private confirmationTrigger: HTMLElement | null = null;

  protected readonly events = signal<EventListItem[]>([]);
  protected readonly isLoading = signal(false);
  protected readonly errorMessage = signal<string | null>(null);
  protected readonly successMessage = signal<string | null>(null);
  protected readonly targetEventId = signal<number | null>(null);
  protected readonly sourceEventIds = signal<number[]>([]);
  protected readonly activeEvents = computed(() => this.events().filter((event) => this.canUseInMerge(event)));
  protected readonly criticalEventCount = computed(() =>
    this.events().filter((event) => event.importance === 'CRITICAL' && this.canUseInMerge(event)).length
  );
  protected readonly linkedNewsCount = computed(() =>
    this.events().reduce((total, event) => total + event.newsCount, 0)
  );
  protected readonly targetEvent = computed(() => this.activeEvents().find((event) => event.id === this.targetEventId()) ?? null);
  protected readonly selectedSourceEvents = computed(() => this.activeEvents().filter((event) => this.sourceEventIds().includes(event.id)));
  protected readonly selectedSourceNewsCount = computed(() => this.selectedSourceEvents().reduce((total, event) => total + event.newsCount, 0));
  protected readonly canRequestMerge = computed(() =>
    this.targetEventId() !== null && this.sourceEventIds().length > 0
  );
  protected readonly pendingConfirmation = signal<PendingConfirmation | null>(null);
  protected readonly globalFilter = signal('');
  protected readonly idFilter = signal('');
  protected readonly titleFilter = signal('');
  protected readonly categoryFilter = signal('');
  protected readonly importanceFilter = signal('');
  protected readonly newsCountFilter = signal('');
  protected readonly statusFilter = signal('');
  protected readonly editorialStatusFilter = signal('');
  protected readonly updatedAtFilter = signal('');
  protected readonly sortColumn = signal<EventSortColumn>('importance');
  protected readonly sortDirection = signal<SortDirection>('asc');
  protected readonly pageSize = signal(10);
  protected readonly currentPage = signal(1);
  protected readonly pageSizeOptions = [5, 10, 25, 50];
  protected readonly categoryOptions = computed(() => this.uniqueOptions((event) => event.category));
  protected readonly importanceOptions = computed(() => this.uniqueOptions((event) => event.importance));
  protected readonly statusOptions = computed(() => this.uniqueOptions((event) => event.status));
  protected readonly editorialStatusOptions = computed(() => this.uniqueOptions((event) => event.editorialStatus));
  protected readonly hasActiveFilters = computed(() => [
    this.globalFilter(),
    this.idFilter(),
    this.titleFilter(),
    this.categoryFilter(),
    this.importanceFilter(),
    this.newsCountFilter(),
    this.statusFilter(),
    this.editorialStatusFilter(),
    this.updatedAtFilter()
  ].some((value) => value.trim().length > 0));
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

  protected setEditorialStatusFilter(value: string): void {
    this.editorialStatusFilter.set(value);
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

  protected sortIcon(column: EventSortColumn): string {
    if (this.sortColumn() !== column) {
      return 'pi pi-sort-alt';
    }

    return this.sortDirection() === 'asc' ? 'pi pi-sort-amount-up-alt' : 'pi pi-sort-amount-down';
  }

  protected sortAriaValue(column: EventSortColumn): 'ascending' | 'descending' | 'none' {
    if (this.sortColumn() !== column) {
      return 'none';
    }

    return this.sortDirection() === 'asc' ? 'ascending' : 'descending';
  }

  protected sortAriaLabel(label: string, column: EventSortColumn): string {
    if (this.sortColumn() !== column) {
      return `Ordenar por ${label} de forma ascendente`;
    }

    const nextDirection = this.sortDirection() === 'asc' ? 'descendente' : 'ascendente';
    return `Ordenar por ${label} de forma ${nextDirection}`;
  }

  protected clearFilters(): void {
    this.globalFilter.set('');
    this.idFilter.set('');
    this.titleFilter.set('');
    this.categoryFilter.set('');
    this.importanceFilter.set('');
    this.newsCountFilter.set('');
    this.statusFilter.set('');
    this.editorialStatusFilter.set('');
    this.updatedAtFilter.set('');
    this.resetPagination();
  }

  protected setTargetEventId(value: string | number): void {
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

  protected requestMergeEvents(): void {
    const targetId = this.targetEventId();
    const sourceIds = this.sourceEventIds();

    if (targetId === null || sourceIds.length === 0) {
      this.errorMessage.set('Selecciona un evento destino y al menos un evento origen.');
      return;
    }

    this.rememberConfirmationTrigger();
    this.pendingConfirmation.set({
      type: 'merge',
      title: 'Fusionar eventos',
      message: `Se archivaran ${sourceIds.length} eventos origen y se moveran ${this.selectedSourceNewsCount()} noticias al evento destino #${targetId}.`,
      confirmLabel: 'Fusionar eventos'
    });
  }

  protected discardEvent(event: EventListItem): void {
    this.rememberConfirmationTrigger();
    this.pendingConfirmation.set({
      type: 'discard',
      event,
      title: 'Descartar evento',
      message: `El evento #${event.id} se archivara y dejara de aparecer en la operativa visible. Conserva ${event.newsCount} noticias asociadas como trazabilidad.`,
      confirmLabel: 'Descartar evento'
    });
  }

  protected restoreEvent(event: EventListItem): void {
    this.rememberConfirmationTrigger();
    this.pendingConfirmation.set({
      type: 'restore',
      event,
      title: 'Deshacer descarte',
      message: `El evento #${event.id} volvera a la operativa editorial y podra analizarse o generar contenido si procede.`,
      confirmLabel: 'Deshacer descarte'
    });
  }

  protected closeConfirmation(): void {
    this.pendingConfirmation.set(null);
  }

  protected restoreConfirmationFocus(): void {
    const trigger = this.confirmationTrigger;
    this.confirmationTrigger = null;

    if (trigger?.isConnected) {
      trigger.focus();
    }
  }

  protected confirmPendingAction(): void {
    const confirmation = this.pendingConfirmation();
    if (!confirmation) {
      return;
    }

    this.pendingConfirmation.set(null);
    if (confirmation.type === 'merge') {
      this.mergeEvents();
      return;
    }

    if (confirmation.type === 'discard') {
      this.executeDiscardEvent(confirmation.event);
      return;
    }

    this.executeRestoreEvent(confirmation.event);
  }

  private mergeEvents(): void {
    const targetId = this.targetEventId();
    const sourceIds = this.sourceEventIds();

    if (targetId === null || sourceIds.length === 0) {
      this.errorMessage.set('Selecciona un evento destino y al menos un evento origen.');
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

  private executeDiscardEvent(event: EventListItem): void {
    this.errorMessage.set(null);
    this.successMessage.set(null);
    this.eventService.discardEvent(event.id).subscribe({
      next: () => {
        this.successMessage.set(`Evento #${event.id} descartado correctamente.`);
        this.sourceEventIds.update((ids) => ids.filter((id) => id !== event.id));
        if (this.targetEventId() === event.id) {
          this.targetEventId.set(null);
        }
        this.loadEvents();
      },
      error: (error: { error?: { error?: string } }) => {
        this.errorMessage.set(error.error?.error ?? 'No se pudo descartar el evento.');
      }
    });
  }

  private executeRestoreEvent(event: EventListItem): void {
    this.errorMessage.set(null);
    this.successMessage.set(null);
    this.eventService.restoreEvent(event.id).subscribe({
      next: () => {
        this.successMessage.set(`Descarte del evento #${event.id} deshecho correctamente.`);
        this.loadEvents();
      },
      error: (error: { error?: { error?: string } }) => {
        this.errorMessage.set(error.error?.error ?? 'No se pudo deshacer el descarte del evento.');
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
      .filter((event) => this.matchesSelect(event.editorialStatus, this.editorialStatusFilter()))
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
      event.description ?? '',
      event.category,
      event.importance,
      event.newsCount.toString(),
      event.status,
      event.editorialStatus,
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

    if (column === 'importance') {
      const importanceComparison = this.importanceScore(left.importance) - this.importanceScore(right.importance);
      return importanceComparison !== 0 ? importanceComparison : right.newsCount - left.newsCount;
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

  private importanceScore(importance: string): number {
    const scores: Record<string, number> = {
      CRITICAL: 0,
      HIGH: 1,
      MEDIUM: 2,
      LOW: 3
    };
    return scores[importance] ?? 4;
  }

  protected canDiscard(event: EventListItem): boolean {
    return this.canUseInMerge(event);
  }

  protected canRestore(event: EventListItem): boolean {
    return event.editorialStatus === 'DISCARDED';
  }

  private canUseInMerge(event: EventListItem): boolean {
    return (event.status === 'OPEN' || event.status === 'MONITORING')
      && event.editorialStatus !== 'DISCARDED'
      && event.editorialStatus !== 'PUBLISHED';
  }

  private normalize(value: string): string {
    return value.trim().toLocaleLowerCase('es');
  }

  private rememberConfirmationTrigger(): void {
    const activeElement = this.document.activeElement;
    this.confirmationTrigger = activeElement instanceof HTMLElement && activeElement !== this.document.body
      ? activeElement
      : null;
  }

  private resetPagination(): void {
    this.currentPage.set(1);
  }
}
