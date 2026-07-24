import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { ButtonModule } from 'primeng/button';
import { DialogModule } from 'primeng/dialog';
import { InputTextModule } from 'primeng/inputtext';
import { MessageModule } from 'primeng/message';

import { ContentListItem } from '../../core/models/content.models';
import { ContentService } from '../../core/services/content.service';
import { PublicationService } from '../../core/services/publication.service';
import { StandardTableComponent } from '../../shared/components/standard-table/standard-table.component';
import { StatusBadgeComponent } from '../../shared/components/status-badge/status-badge.component';
import { DialogFocusReturnDirective } from '../../shared/directives/dialog-focus-return.directive';

type ContentSortColumn = 'channel' | 'title' | 'status' | 'generatedAt' | 'approvedAt';
type SortDirection = 'asc' | 'desc';
type ModalMode = 'view' | 'edit';

@Component({
  selector: 'app-content-page',
  imports: [ButtonModule, DialogFocusReturnDirective, DialogModule, FormsModule, InputTextModule, MessageModule, RouterLink, StandardTableComponent, StatusBadgeComponent],
  templateUrl: './content-page.component.html',
  styleUrl: './content-page.component.scss'
})
export class ContentPageComponent implements OnInit {
  private readonly contentService = inject(ContentService);
  private readonly publicationService = inject(PublicationService);

  protected readonly items = signal<ContentListItem[]>([]);
  protected readonly isLoading = signal(false);
  protected readonly errorMessage = signal<string | null>(null);
  protected readonly successMessage = signal<string | null>(null);
  protected readonly selectedItemId = signal<number | null>(null);
  protected readonly modalMode = signal<ModalMode | null>(null);
  protected readonly editTitle = signal('');
  protected readonly editContent = signal('');
  protected readonly editTone = signal('');
  protected readonly scheduleAt = signal('');
  protected readonly channelFilter = signal('');
  protected readonly titleFilter = signal('');
  protected readonly statusFilter = signal('');
  protected readonly generatedAtFilter = signal('');
  protected readonly approvedAtFilter = signal('');
  protected readonly sortColumn = signal<ContentSortColumn>('generatedAt');
  protected readonly sortDirection = signal<SortDirection>('desc');
  protected readonly pageSize = signal(10);
  protected readonly currentPage = signal(1);
  protected readonly pageSizeOptions = [5, 10, 25, 50];
  protected readonly displayedItems = computed(() => this.sortItems(this.filterItems(this.items())));
  protected readonly totalPages = computed(() => Math.max(1, Math.ceil(this.displayedItems().length / this.pageSize())));
  protected readonly paginatedItems = computed(() => {
    const page = Math.min(this.currentPage(), this.totalPages());
    const start = (page - 1) * this.pageSize();
    return this.displayedItems().slice(start, start + this.pageSize());
  });
  protected readonly selectedItem = computed(() => {
    const selectedId = this.selectedItemId();
    return this.items().find((item) => item.id === selectedId) ?? null;
  });
  protected readonly modalItem = computed(() => this.modalMode() ? this.selectedItem() : null);

  ngOnInit(): void {
    this.loadContent();
  }

  protected loadContent(): void {
    this.isLoading.set(true);
    this.errorMessage.set(null);

    this.contentService.listContent().subscribe({
      next: (items) => {
        this.items.set(items);
        this.currentPage.set(Math.min(this.currentPage(), this.totalPages()));
        const selected = this.selectedItem();
        if (selected) {
          this.prepareEditor(selected);
        }
        this.isLoading.set(false);
      },
      error: (error: { error?: { error?: string } }) => {
        this.errorMessage.set(error.error?.error ?? 'No se pudo cargar la bandeja editorial.');
        this.isLoading.set(false);
      }
    });
  }

  protected approve(item: ContentListItem): void {
    this.contentService.approveContent(item.id).subscribe({
      next: () => {
        this.successMessage.set('Contenido aprobado correctamente.');
        this.loadContent();
      },
      error: (error: { error?: { error?: string } }) => this.errorMessage.set(error.error?.error ?? 'No se pudo aprobar el contenido.')
    });
  }

  protected reject(item: ContentListItem): void {
    this.contentService.rejectContent(item.id).subscribe({
      next: () => {
        this.successMessage.set('Contenido rechazado correctamente.');
        this.loadContent();
      },
      error: (error: { error?: { error?: string } }) => this.errorMessage.set(error.error?.error ?? 'No se pudo rechazar el contenido.')
    });
  }

  protected formatDate(value: string | null): string {
    if (value === null) {
      return 'Pendiente';
    }

    return new Intl.DateTimeFormat('es-ES', {
      dateStyle: 'short',
      timeStyle: 'short'
    }).format(new Date(value));
  }

  protected openView(item: ContentListItem): void {
    this.prepareEditor(item);
    this.modalMode.set('view');
  }

  protected openEdit(item: ContentListItem, event?: Event): void {
    event?.stopPropagation();
    if (!this.canEdit(item)) {
      return;
    }

    this.prepareEditor(item);
    this.modalMode.set('edit');
  }

  protected closeModal(): void {
    this.modalMode.set(null);
    this.selectedItemId.set(null);
    this.scheduleAt.set('');
  }

  protected canEdit(item: ContentListItem): boolean {
    return item.status === 'PENDING_REVIEW' || item.status === 'APPROVED';
  }

  protected canReview(item: ContentListItem): boolean {
    return item.status === 'PENDING_REVIEW';
  }

  private prepareEditor(item: ContentListItem): void {
    this.selectedItemId.set(item.id);
    this.editTitle.set(item.title);
    this.editContent.set(item.content);
    this.editTone.set(item.tone);
    this.scheduleAt.set('');
  }

  protected saveEdit(item: ContentListItem): void {
    if (!this.canEdit(item)) {
      return;
    }

    this.errorMessage.set(null);
    this.successMessage.set(null);
    this.contentService.updateContent(item.id, {
      title: this.editTitle(),
      content: this.editContent(),
      tone: this.editTone()
    }).subscribe({
      next: (updatedItem) => {
        this.successMessage.set('Contenido actualizado y devuelto a revision.');
        this.selectedItemId.set(updatedItem.id);
        this.modalMode.set('view');
        this.loadContent();
      },
      error: (error: { error?: { error?: string } }) => this.errorMessage.set(error.error?.error ?? 'No se pudo actualizar el contenido.')
    });
  }

  protected schedulePublication(item: ContentListItem): void {
    if (item.status !== 'APPROVED') {
      this.errorMessage.set('Solo se puede programar contenido aprobado.');
      return;
    }

    const scheduleValue = this.scheduleAt();
    const scheduledDate = scheduleValue ? new Date(scheduleValue) : null;
    if (scheduledDate === null || Number.isNaN(scheduledDate.getTime()) || scheduledDate <= new Date()) {
      this.errorMessage.set('Selecciona una fecha futura para programar la publicacion.');
      return;
    }

    this.errorMessage.set(null);
    this.successMessage.set(null);
    this.publicationService.schedulePublication(item.id, scheduledDate.toISOString()).subscribe({
      next: () => {
        this.successMessage.set('Publicacion programada correctamente.');
        this.scheduleAt.set('');
        this.loadContent();
      },
      error: (error: { error?: { error?: string } }) => this.errorMessage.set(error.error?.error ?? 'No se pudo programar la publicacion.')
    });
  }

  protected publishNow(item: ContentListItem): void {
    if (item.status !== 'APPROVED') {
      this.errorMessage.set('Solo se puede publicar contenido aprobado.');
      return;
    }

    this.errorMessage.set(null);
    this.successMessage.set(null);
    this.publicationService.publishContent(item.id).subscribe({
      next: () => {
        this.successMessage.set('Contenido publicado correctamente.');
        this.loadContent();
      },
      error: (error: { error?: { error?: string } }) => this.errorMessage.set(error.error?.error ?? 'No se pudo publicar el contenido.')
    });
  }

  protected canPublish(item: ContentListItem): boolean {
    return item.status === 'APPROVED';
  }

  protected setChannelFilter(value: string): void { this.channelFilter.set(value); this.currentPage.set(1); }
  protected setTitleFilter(value: string): void { this.titleFilter.set(value); this.currentPage.set(1); }
  protected setStatusFilter(value: string): void { this.statusFilter.set(value); this.currentPage.set(1); }
  protected setGeneratedAtFilter(value: string): void { this.generatedAtFilter.set(value); this.currentPage.set(1); }
  protected setApprovedAtFilter(value: string): void { this.approvedAtFilter.set(value); this.currentPage.set(1); }

  protected changeSort(column: ContentSortColumn): void {
    if (this.sortColumn() === column) {
      this.sortDirection.update((direction) => direction === 'asc' ? 'desc' : 'asc');
      return;
    }
    this.sortColumn.set(column);
    this.sortDirection.set(column === 'generatedAt' || column === 'approvedAt' ? 'desc' : 'asc');
  }

  protected sortLabel(column: ContentSortColumn): string {
    return this.sortColumn() === column ? this.sortDirection().toUpperCase() : '';
  }

  protected sortAriaValue(column: ContentSortColumn): 'ascending' | 'descending' | 'none' {
    return this.sortColumn() === column
      ? (this.sortDirection() === 'asc' ? 'ascending' : 'descending')
      : 'none';
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

  private filterItems(items: ContentListItem[]): ContentListItem[] {
    return items
      .filter((item) => this.matchesText(item.channel, this.channelFilter()))
      .filter((item) => this.matchesText(item.title, this.titleFilter()))
      .filter((item) => this.matchesText(item.status, this.statusFilter()))
      .filter((item) => this.matchesText(this.formatDate(item.generatedAt), this.generatedAtFilter()))
      .filter((item) => this.matchesText(this.formatDate(item.approvedAt), this.approvedAtFilter()));
  }

  private sortItems(items: ContentListItem[]): ContentListItem[] {
    const direction = this.sortDirection() === 'asc' ? 1 : -1;
    const column = this.sortColumn();
    return [...items].sort((left, right) => direction * this.compareItems(left, right, column));
  }

  private compareItems(left: ContentListItem, right: ContentListItem, column: ContentSortColumn): number {
    if (column === 'generatedAt' || column === 'approvedAt') {
      return this.dateValue(left[column]) - this.dateValue(right[column]);
    }
    return left[column].localeCompare(right[column], 'es', { sensitivity: 'base' });
  }

  private matchesText(value: string, filter: string): boolean {
    const normalizedFilter = filter.trim().toLocaleLowerCase('es');
    return !normalizedFilter || value.trim().toLocaleLowerCase('es').includes(normalizedFilter);
  }

  private dateValue(value: string | null): number {
    return value === null ? 0 : new Date(value).getTime();
  }
}
