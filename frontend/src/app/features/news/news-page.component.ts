import { FormsModule } from '@angular/forms';
import { Component, OnInit, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { ButtonModule } from 'primeng/button';
import { InputTextModule } from 'primeng/inputtext';
import { MessageModule } from 'primeng/message';
import { SelectModule } from 'primeng/select';

import { NewsListItem } from '../../core/models/news.models';
import { NewsService } from '../../core/services/news.service';
import { StandardTableComponent } from '../../shared/components/standard-table/standard-table.component';
import { StatusBadgeComponent } from '../../shared/components/status-badge/status-badge.component';

type NewsSortColumn = 'id' | 'title' | 'sourceId' | 'processingStatus' | 'eventId' | 'category' | 'publishedAt' | 'capturedAt';
type SortDirection = 'asc' | 'desc';

@Component({
  selector: 'app-news-page',
  imports: [ButtonModule, FormsModule, InputTextModule, MessageModule, RouterLink, SelectModule, StandardTableComponent, StatusBadgeComponent],
  templateUrl: './news-page.component.html',
  styleUrl: './news-page.component.scss'
})
export class NewsPageComponent implements OnInit {
  private readonly newsService = inject(NewsService);

  protected readonly news = signal<NewsListItem[]>([]);
  protected readonly isLoading = signal(false);
  protected readonly errorMessage = signal<string | null>(null);
  protected readonly totalItems = signal(0);
  protected readonly totalPages = signal(1);
  protected readonly pageInput = signal(1);
  protected readonly globalFilter = signal('');
  protected readonly idFilter = signal('');
  protected readonly titleFilter = signal('');
  protected readonly sourceFilter = signal('');
  protected readonly statusFilter = signal('');
  protected readonly eventFilter = signal('');
  protected readonly categoryFilter = signal('');
  protected readonly publishedAtFilter = signal('');
  protected readonly capturedAtFilter = signal('');
  protected readonly sortColumn = signal<NewsSortColumn>('capturedAt');
  protected readonly sortDirection = signal<SortDirection>('desc');
  protected readonly pageSize = signal(10);
  protected readonly currentPage = signal(1);
  protected readonly pageSizeOptions = [5, 10, 25, 50];
  protected readonly statusOptions = ['CAPTURED', 'CLASSIFIED', 'DISCARDED', 'EVENT_MATCHED', 'ARCHIVED'];
  protected readonly categoryOptions = [
    'Sin clasificar',
    'OPOSICIONES',
    'INTERINOS',
    'SIPRI',
    'PLANTILLAS',
    'RETRIBUCIONES',
    'FORMACION',
    'INSPECCION',
    'LEGISLACION',
    'CURRICULO',
    'UNIVERSIDAD',
    'FP',
    'DIGITALIZACION',
    'INCLUSION',
    'INFRAESTRUCTURAS',
    'CONFLICTO_LABORAL',
    'SINDICAL',
    'OTROS'
  ];

  ngOnInit(): void {
    this.loadNews();
  }

  protected loadNews(): void {
    this.loadNewsPage(this.currentPage());
  }

  protected loadNewsPage(page: number): void {
    this.isLoading.set(true);
    this.errorMessage.set(null);

    this.newsService.listNewsPage({
      page,
      pageSize: this.pageSize(),
      global: this.globalFilter(),
      id: this.idFilter(),
      title: this.titleFilter(),
      source: this.sourceFilter(),
      status: this.statusFilter(),
      event: this.eventFilter(),
      category: this.categoryFilter(),
      publishedAt: this.publishedAtFilter(),
      capturedAt: this.capturedAtFilter(),
      sortColumn: this.sortColumn(),
      sortDirection: this.sortDirection()
    }).subscribe({
      next: (response) => {
        this.news.set(response.items);
        this.currentPage.set(response.page);
        this.pageInput.set(response.page);
        this.pageSize.set(response.pageSize);
        this.totalItems.set(response.totalItems);
        this.totalPages.set(response.totalPages);
        this.isLoading.set(false);
      },
      error: (error: { error?: { error?: string } }) => {
        this.errorMessage.set(error.error?.error ?? 'No se pudo cargar el listado de noticias.');
        this.isLoading.set(false);
      }
    });
  }

  protected formatDate(value: string | null): string {
    if (!value) {
      return '-';
    }

    return new Intl.DateTimeFormat('es-ES', {
      dateStyle: 'short',
      timeStyle: 'short'
    }).format(new Date(value));
  }

  protected sourceLabel(item: NewsListItem): string {
    return item.sourceName?.trim() || `Fuente #${item.sourceId}`;
  }

  protected eventLabel(eventId: number | null): string {
    return eventId === null ? 'Sin evento' : `#${eventId}`;
  }

  protected categoryLabel(item: NewsListItem): string {
    return item.category ?? 'Sin clasificar';
  }

  protected setGlobalFilter(value: string): void {
    this.globalFilter.set(value);
    this.resetPaginationAndLoad();
  }

  protected setIdFilter(value: string): void {
    this.idFilter.set(value);
    this.resetPaginationAndLoad();
  }

  protected setTitleFilter(value: string): void {
    this.titleFilter.set(value);
    this.resetPaginationAndLoad();
  }

  protected setSourceFilter(value: string): void {
    this.sourceFilter.set(value);
    this.resetPaginationAndLoad();
  }

  protected setStatusFilter(value: string): void {
    this.statusFilter.set(value);
    this.resetPaginationAndLoad();
  }

  protected setEventFilter(value: string): void {
    this.eventFilter.set(value);
    this.resetPaginationAndLoad();
  }

  protected setCategoryFilter(value: string): void {
    this.categoryFilter.set(value);
    this.resetPaginationAndLoad();
  }

  protected setPublishedAtFilter(value: string): void {
    this.publishedAtFilter.set(value);
    this.resetPaginationAndLoad();
  }

  protected setCapturedAtFilter(value: string): void {
    this.capturedAtFilter.set(value);
    this.resetPaginationAndLoad();
  }

  protected changeSort(column: NewsSortColumn): void {
    if (this.sortColumn() === column) {
      this.sortDirection.update((direction) => direction === 'asc' ? 'desc' : 'asc');
      this.loadNewsPage(1);
      return;
    }

    this.sortColumn.set(column);
    this.sortDirection.set(column === 'publishedAt' || column === 'capturedAt' ? 'desc' : 'asc');
    this.loadNewsPage(1);
  }

  protected sortLabel(column: NewsSortColumn): string {
    if (this.sortColumn() !== column) {
      return '';
    }

    return this.sortDirection() === 'asc' ? 'ASC' : 'DESC';
  }

  protected setPageSize(value: string): void {
    this.pageSize.set(Number(value));
    this.loadNewsPage(1);
  }

  protected goToPreviousPage(): void {
    this.loadNewsPage(Math.max(1, this.currentPage() - 1));
  }

  protected goToNextPage(): void {
    this.loadNewsPage(Math.min(this.totalPages(), this.currentPage() + 1));
  }

  protected setPageInput(value: string): void {
    const requestedPage = Number(value);
    this.pageInput.set(Number.isFinite(requestedPage) ? requestedPage : this.currentPage());
  }

  protected goToPage(): void {
    const targetPage = Math.min(this.totalPages(), Math.max(1, Math.trunc(this.pageInput())));
    this.loadNewsPage(targetPage);
  }

  private resetPaginationAndLoad(): void {
    this.currentPage.set(1);
    this.pageInput.set(1);
    this.loadNewsPage(1);
  }
}
