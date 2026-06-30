import { CommonModule } from '@angular/common';
import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { FormBuilder, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { ButtonModule } from 'primeng/button';
import { DialogModule } from 'primeng/dialog';
import { InputTextModule } from 'primeng/inputtext';
import { MessageModule } from 'primeng/message';
import { SelectModule } from 'primeng/select';

import { SourceResponse } from '../../core/models/source.models';
import { SourceService } from '../../core/services/source.service';
import { StandardTableComponent } from '../../shared/components/standard-table/standard-table.component';

type SourceSortColumn = 'id' | 'name' | 'url' | 'type' | 'priority' | 'active' | 'createdAt' | 'updatedAt';
type SortDirection = 'asc' | 'desc';
type SourceFormMode = 'create' | 'edit';

@Component({
  selector: 'app-sources-page',
  imports: [ButtonModule, CommonModule, DialogModule, FormsModule, InputTextModule, MessageModule, ReactiveFormsModule, SelectModule, StandardTableComponent],
  templateUrl: './sources-page.component.html',
  styleUrl: './sources-page.component.scss'
})
export class SourcesPageComponent implements OnInit {
  private readonly formBuilder = inject(FormBuilder);
  private readonly sourceService = inject(SourceService);

  protected readonly sources = signal<SourceResponse[]>([]);
  protected readonly isLoading = signal(false);
  protected readonly isSubmitting = signal(false);
  protected readonly errorMessage = signal<string | null>(null);
  protected readonly successMessage = signal<string | null>(null);
  protected readonly editingSourceId = signal<number | null>(null);
  protected readonly isModalOpen = signal(false);
  protected readonly formMode = signal<SourceFormMode>('create');
  protected readonly globalFilter = signal('');
  protected readonly idFilter = signal('');
  protected readonly nameFilter = signal('');
  protected readonly urlFilter = signal('');
  protected readonly typeFilter = signal('');
  protected readonly priorityFilter = signal('');
  protected readonly activeFilter = signal('');
  protected readonly createdAtFilter = signal('');
  protected readonly updatedAtFilter = signal('');
  protected readonly sortColumn = signal<SourceSortColumn>('updatedAt');
  protected readonly sortDirection = signal<SortDirection>('desc');
  protected readonly pageSize = signal(10);
  protected readonly currentPage = signal(1);
  protected readonly pageSizeOptions = [5, 10, 25, 50];
  protected readonly typeOptions = computed(() => this.uniqueOptions((source) => source.type));
  protected readonly displayedSources = computed(() => this.sortSources(this.filterSources(this.sources())));
  protected readonly totalPages = computed(() => Math.max(1, Math.ceil(this.displayedSources().length / this.pageSize())));
  protected readonly paginatedSources = computed(() => {
    const page = Math.min(this.currentPage(), this.totalPages());
    const start = (page - 1) * this.pageSize();
    return this.displayedSources().slice(start, start + this.pageSize());
  });

  protected readonly sourceForm = this.formBuilder.nonNullable.group({
    name: ['', [Validators.required, Validators.minLength(3)]],
    url: ['', [Validators.required]],
    type: ['RSS', [Validators.required]],
    priority: [10, [Validators.required, Validators.min(0)]],
    active: [true]
  });

  ngOnInit(): void {
    this.loadSources();
  }

  protected loadSources(): void {
    this.isLoading.set(true);
    this.errorMessage.set(null);

    this.sourceService.listSources().subscribe({
      next: (sources) => {
        this.sources.set(sources);
        this.currentPage.set(Math.min(this.currentPage(), this.totalPages()));
        this.isLoading.set(false);
      },
      error: (error: { error?: { error?: string } }) => {
        this.errorMessage.set(error.error?.error ?? 'No se pudieron cargar las fuentes.');
        this.isLoading.set(false);
      }
    });
  }

  protected startCreate(): void {
    this.editingSourceId.set(null);
    this.formMode.set('create');
    this.isModalOpen.set(true);
    this.successMessage.set(null);
    this.errorMessage.set(null);
    this.sourceForm.reset({ name: '', url: '', type: 'RSS', priority: 10, active: true });
  }

  protected startEdit(source: SourceResponse): void {
    this.editingSourceId.set(source.id);
    this.formMode.set('edit');
    this.isModalOpen.set(true);
    this.successMessage.set(null);
    this.errorMessage.set(null);
    this.sourceForm.reset({
      name: source.name,
      url: source.url,
      type: source.type,
      priority: source.priority,
      active: source.active
    });
  }

  protected closeModal(): void {
    if (this.isSubmitting()) {
      return;
    }

    this.isModalOpen.set(false);
    this.editingSourceId.set(null);
    this.sourceForm.reset({ name: '', url: '', type: 'RSS', priority: 10, active: true });
  }

  protected submit(): void {
    if (this.sourceForm.invalid) {
      this.sourceForm.markAllAsTouched();
      return;
    }

    this.isSubmitting.set(true);
    this.successMessage.set(null);
    this.errorMessage.set(null);

    const payload = this.sourceForm.getRawValue();
    const editingSourceId = this.editingSourceId();
    const request = editingSourceId === null
      ? this.sourceService.createSource(payload)
      : this.sourceService.updateSource(editingSourceId, payload);

    request.subscribe({
      next: () => {
        this.isSubmitting.set(false);
        this.successMessage.set(editingSourceId === null ? 'Fuente creada correctamente.' : 'Fuente actualizada correctamente.');
        this.closeModalKeepingMessage();
        this.loadSources();
      },
      error: (error: { error?: { error?: string } }) => {
        this.isSubmitting.set(false);
        this.errorMessage.set(error.error?.error ?? 'No se pudo guardar la fuente.');
      }
    });
  }

  protected setGlobalFilter(value: string): void {
    this.globalFilter.set(value);
    this.resetPagination();
  }

  protected setIdFilter(value: string): void {
    this.idFilter.set(value);
    this.resetPagination();
  }

  protected setNameFilter(value: string): void {
    this.nameFilter.set(value);
    this.resetPagination();
  }

  protected setUrlFilter(value: string): void {
    this.urlFilter.set(value);
    this.resetPagination();
  }

  protected setTypeFilter(value: string): void {
    this.typeFilter.set(value);
    this.resetPagination();
  }

  protected setPriorityFilter(value: string): void {
    this.priorityFilter.set(value);
    this.resetPagination();
  }

  protected setActiveFilter(value: string): void {
    this.activeFilter.set(value);
    this.resetPagination();
  }

  protected setCreatedAtFilter(value: string): void {
    this.createdAtFilter.set(value);
    this.resetPagination();
  }

  protected setUpdatedAtFilter(value: string): void {
    this.updatedAtFilter.set(value);
    this.resetPagination();
  }

  protected changeSort(column: SourceSortColumn): void {
    if (this.sortColumn() === column) {
      this.sortDirection.update((direction) => direction === 'asc' ? 'desc' : 'asc');
      return;
    }

    this.sortColumn.set(column);
    this.sortDirection.set(column === 'updatedAt' || column === 'createdAt' ? 'desc' : 'asc');
  }

  protected sortLabel(column: SourceSortColumn): string {
    if (this.sortColumn() !== column) {
      return '';
    }

    return this.sortDirection() === 'asc' ? '↑' : '↓';
  }

  protected formatDate(value: string): string {
    return new Intl.DateTimeFormat('es-ES', {
      dateStyle: 'short',
      timeStyle: 'short'
    }).format(new Date(value));
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

  private closeModalKeepingMessage(): void {
    this.isModalOpen.set(false);
    this.editingSourceId.set(null);
    this.formMode.set('create');
    this.sourceForm.reset({ name: '', url: '', type: 'RSS', priority: 10, active: true });
  }

  private filterSources(sources: SourceResponse[]): SourceResponse[] {
    return sources.filter((source) => this.matchesGlobalFilter(source))
      .filter((source) => this.matchesText(source.id.toString(), this.idFilter()))
      .filter((source) => this.matchesText(source.name, this.nameFilter()))
      .filter((source) => this.matchesText(source.url, this.urlFilter()))
      .filter((source) => this.matchesSelect(source.type, this.typeFilter()))
      .filter((source) => this.matchesText(source.priority.toString(), this.priorityFilter()))
      .filter((source) => this.matchesSelect(this.activeLabel(source.active), this.activeFilter()))
      .filter((source) => this.matchesText(this.formatDate(source.createdAt), this.createdAtFilter()))
      .filter((source) => this.matchesText(this.formatDate(source.updatedAt), this.updatedAtFilter()));
  }

  private matchesGlobalFilter(source: SourceResponse): boolean {
    const filter = this.normalize(this.globalFilter());
    if (!filter) {
      return true;
    }

    return [
      source.id.toString(),
      `#${source.id}`,
      source.name,
      source.url,
      source.type,
      source.priority.toString(),
      this.activeLabel(source.active),
      source.createdAt,
      source.updatedAt,
      this.formatDate(source.createdAt),
      this.formatDate(source.updatedAt)
    ].some((value) => this.normalize(value).includes(filter));
  }

  private sortSources(sources: SourceResponse[]): SourceResponse[] {
    const direction = this.sortDirection() === 'asc' ? 1 : -1;
    const column = this.sortColumn();

    return [...sources].sort((left, right) => direction * this.compareSources(left, right, column));
  }

  private compareSources(left: SourceResponse, right: SourceResponse, column: SourceSortColumn): number {
    if (column === 'id' || column === 'priority') {
      return left[column] - right[column];
    }

    if (column === 'active') {
      return Number(left.active) - Number(right.active);
    }

    if (column === 'createdAt' || column === 'updatedAt') {
      return new Date(left[column]).getTime() - new Date(right[column]).getTime();
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

  private uniqueOptions(selector: (source: SourceResponse) => string): string[] {
    return [...new Set(this.sources().map(selector))].sort((left, right) => left.localeCompare(right, 'es'));
  }

  private activeLabel(active: boolean): string {
    return active ? 'Activa' : 'Inactiva';
  }

  private normalize(value: string): string {
    return value.trim().toLocaleLowerCase('es');
  }

  private resetPagination(): void {
    this.currentPage.set(1);
  }
}
