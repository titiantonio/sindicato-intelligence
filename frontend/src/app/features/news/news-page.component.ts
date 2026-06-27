import { FormsModule } from '@angular/forms';
import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';

import { NewsListItem } from '../../core/models/news.models';
import { NewsService } from '../../core/services/news.service';
import { StatusBadgeComponent } from '../../shared/components/status-badge/status-badge.component';

type NewsSortColumn = 'id' | 'title' | 'sourceId' | 'processingStatus' | 'eventId' | 'category' | 'publishedAt' | 'capturedAt';
type SortDirection = 'asc' | 'desc';

@Component({
  selector: 'app-news-page',
  imports: [FormsModule, RouterLink, StatusBadgeComponent],
  templateUrl: './news-page.component.html',
  styleUrl: './news-page.component.scss'
})
export class NewsPageComponent implements OnInit {
  private readonly newsService = inject(NewsService);

  protected readonly news = signal<NewsListItem[]>([]);
  protected readonly isLoading = signal(false);
  protected readonly errorMessage = signal<string | null>(null);
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
  protected readonly statusOptions = computed(() => this.uniqueOptions((item) => item.processingStatus));
  protected readonly categoryOptions = computed(() => this.uniqueOptions((item) => this.categoryLabel(item)));
  protected readonly displayedNews = computed(() => this.sortNews(this.filterNews(this.news())));
  protected readonly totalPages = computed(() => Math.max(1, Math.ceil(this.displayedNews().length / this.pageSize())));
  protected readonly paginatedNews = computed(() => {
    const page = Math.min(this.currentPage(), this.totalPages());
    const start = (page - 1) * this.pageSize();
    return this.displayedNews().slice(start, start + this.pageSize());
  });

  ngOnInit(): void {
    this.loadNews();
  }

  protected loadNews(): void {
    this.isLoading.set(true);
    this.errorMessage.set(null);

    this.newsService.listNews().subscribe({
      next: (news) => {
        this.news.set(news);
        this.currentPage.set(Math.min(this.currentPage(), this.totalPages()));
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

  protected sourceLabel(sourceId: number): string {
    return `Fuente #${sourceId}`;
  }

  protected eventLabel(eventId: number | null): string {
    return eventId === null ? 'Sin evento' : `#${eventId}`;
  }

  protected categoryLabel(item: NewsListItem): string {
    return item.classification?.category ?? 'Sin clasificar';
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

  protected setSourceFilter(value: string): void {
    this.sourceFilter.set(value);
    this.resetPagination();
  }

  protected setStatusFilter(value: string): void {
    this.statusFilter.set(value);
    this.resetPagination();
  }

  protected setEventFilter(value: string): void {
    this.eventFilter.set(value);
    this.resetPagination();
  }

  protected setCategoryFilter(value: string): void {
    this.categoryFilter.set(value);
    this.resetPagination();
  }

  protected setPublishedAtFilter(value: string): void {
    this.publishedAtFilter.set(value);
    this.resetPagination();
  }

  protected setCapturedAtFilter(value: string): void {
    this.capturedAtFilter.set(value);
    this.resetPagination();
  }

  protected changeSort(column: NewsSortColumn): void {
    if (this.sortColumn() === column) {
      this.sortDirection.update((direction) => direction === 'asc' ? 'desc' : 'asc');
      return;
    }

    this.sortColumn.set(column);
    this.sortDirection.set(column === 'publishedAt' || column === 'capturedAt' ? 'desc' : 'asc');
  }

  protected sortLabel(column: NewsSortColumn): string {
    if (this.sortColumn() !== column) {
      return '';
    }

    return this.sortDirection() === 'asc' ? 'ASC' : 'DESC';
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

  private filterNews(news: NewsListItem[]): NewsListItem[] {
    return news.filter((item) => this.matchesGlobalFilter(item))
      .filter((item) => this.matchesText(item.id.toString(), this.idFilter()))
      .filter((item) => this.matchesText(item.title, this.titleFilter()))
      .filter((item) => this.matchesText(this.sourceLabel(item.sourceId), this.sourceFilter()))
      .filter((item) => this.matchesSelect(item.processingStatus, this.statusFilter()))
      .filter((item) => this.matchesText(this.eventLabel(item.eventId), this.eventFilter()))
      .filter((item) => this.matchesSelect(this.categoryLabel(item), this.categoryFilter()))
      .filter((item) => this.matchesText(this.formatDate(item.publishedAt), this.publishedAtFilter()))
      .filter((item) => this.matchesText(this.formatDate(item.capturedAt), this.capturedAtFilter()));
  }

  private matchesGlobalFilter(item: NewsListItem): boolean {
    const filter = this.normalize(this.globalFilter());
    if (!filter) {
      return true;
    }

    return [
      item.id.toString(),
      `#${item.id}`,
      item.title,
      this.sourceLabel(item.sourceId),
      item.processingStatus,
      this.eventLabel(item.eventId),
      this.categoryLabel(item),
      item.publishedAt ?? '',
      item.capturedAt,
      this.formatDate(item.publishedAt),
      this.formatDate(item.capturedAt)
    ].some((value) => this.normalize(value).includes(filter));
  }

  private sortNews(news: NewsListItem[]): NewsListItem[] {
    const direction = this.sortDirection() === 'asc' ? 1 : -1;
    const column = this.sortColumn();

    return [...news].sort((left, right) => direction * this.compareNews(left, right, column));
  }

  private compareNews(left: NewsListItem, right: NewsListItem, column: NewsSortColumn): number {
    if (column === 'id' || column === 'sourceId') {
      return left[column] - right[column];
    }

    if (column === 'eventId') {
      return (left.eventId ?? Number.MAX_SAFE_INTEGER) - (right.eventId ?? Number.MAX_SAFE_INTEGER);
    }

    if (column === 'publishedAt' || column === 'capturedAt') {
      return this.dateValue(left[column]) - this.dateValue(right[column]);
    }

    if (column === 'category') {
      return this.categoryLabel(left).localeCompare(this.categoryLabel(right), 'es', { sensitivity: 'base' });
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

  private uniqueOptions(selector: (item: NewsListItem) => string): string[] {
    return [...new Set(this.news().map(selector))].sort((left, right) => left.localeCompare(right, 'es'));
  }

  private dateValue(value: string | null): number {
    return value ? new Date(value).getTime() : 0;
  }

  private normalize(value: string): string {
    return value.trim().toLocaleLowerCase('es');
  }

  private resetPagination(): void {
    this.currentPage.set(1);
  }
}
