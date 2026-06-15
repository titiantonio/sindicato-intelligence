import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, RouterLink } from '@angular/router';

import { EventDetail, EventNewsItem } from '../../core/models/event.models';
import { EventService } from '../../core/services/event.service';
import { StatusBadgeComponent } from '../../shared/components/status-badge/status-badge.component';

type EventNewsSortColumn = 'id' | 'title' | 'processingStatus' | 'category' | 'urgencyLevel' | 'capturedAt';
type SortDirection = 'asc' | 'desc';

@Component({
  selector: 'app-event-detail-page',
  imports: [FormsModule, RouterLink, StatusBadgeComponent],
  templateUrl: './event-detail-page.component.html',
  styleUrl: './event-detail-page.component.scss'
})
export class EventDetailPageComponent implements OnInit {
  private readonly route = inject(ActivatedRoute);
  private readonly eventService = inject(EventService);

  protected readonly event = signal<EventDetail | null>(null);
  protected readonly isLoading = signal(false);
  protected readonly errorMessage = signal<string | null>(null);
  protected readonly newsIdFilter = signal('');
  protected readonly newsTitleFilter = signal('');
  protected readonly newsStatusFilter = signal('');
  protected readonly newsCategoryFilter = signal('');
  protected readonly newsUrgencyFilter = signal('');
  protected readonly newsCapturedAtFilter = signal('');
  protected readonly newsSortColumn = signal<EventNewsSortColumn>('capturedAt');
  protected readonly newsSortDirection = signal<SortDirection>('desc');
  protected readonly newsPageSize = signal(10);
  protected readonly newsCurrentPage = signal(1);
  protected readonly pageSizeOptions = [5, 10, 25, 50];
  protected readonly displayedNews = computed(() => this.sortNews(this.filterNews(this.event()?.news ?? [])));
  protected readonly newsTotalPages = computed(() => Math.max(1, Math.ceil(this.displayedNews().length / this.newsPageSize())));
  protected readonly paginatedNews = computed(() => {
    const page = Math.min(this.newsCurrentPage(), this.newsTotalPages());
    const start = (page - 1) * this.newsPageSize();
    return this.displayedNews().slice(start, start + this.newsPageSize());
  });

  ngOnInit(): void {
    const eventId = Number(this.route.snapshot.paramMap.get('id'));

    if (!Number.isFinite(eventId)) {
      this.errorMessage.set('Identificador de evento invalido.');
      return;
    }

    this.loadEvent(eventId);
  }

  protected loadEvent(eventId: number): void {
    this.isLoading.set(true);
    this.errorMessage.set(null);

    this.eventService.getEvent(eventId).subscribe({
      next: (event) => {
        this.event.set(event);
        this.newsCurrentPage.set(Math.min(this.newsCurrentPage(), this.newsTotalPages()));
        this.isLoading.set(false);
      },
      error: (error: { error?: { error?: string } }) => {
        this.errorMessage.set(error.error?.error ?? 'No se pudo cargar el detalle del evento.');
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

  protected setNewsIdFilter(value: string): void { this.newsIdFilter.set(value); this.newsCurrentPage.set(1); }
  protected setNewsTitleFilter(value: string): void { this.newsTitleFilter.set(value); this.newsCurrentPage.set(1); }
  protected setNewsStatusFilter(value: string): void { this.newsStatusFilter.set(value); this.newsCurrentPage.set(1); }
  protected setNewsCategoryFilter(value: string): void { this.newsCategoryFilter.set(value); this.newsCurrentPage.set(1); }
  protected setNewsUrgencyFilter(value: string): void { this.newsUrgencyFilter.set(value); this.newsCurrentPage.set(1); }
  protected setNewsCapturedAtFilter(value: string): void { this.newsCapturedAtFilter.set(value); this.newsCurrentPage.set(1); }

  protected changeNewsSort(column: EventNewsSortColumn): void {
    if (this.newsSortColumn() === column) {
      this.newsSortDirection.update((direction) => direction === 'asc' ? 'desc' : 'asc');
      return;
    }
    this.newsSortColumn.set(column);
    this.newsSortDirection.set(column === 'capturedAt' ? 'desc' : 'asc');
  }

  protected newsSortLabel(column: EventNewsSortColumn): string {
    return this.newsSortColumn() === column ? this.newsSortDirection().toUpperCase() : '';
  }

  protected setNewsPageSize(value: string): void {
    this.newsPageSize.set(Number(value));
    this.newsCurrentPage.set(1);
  }

  protected previousNewsPage(): void {
    this.newsCurrentPage.update((page) => Math.max(1, page - 1));
  }

  protected nextNewsPage(): void {
    this.newsCurrentPage.update((page) => Math.min(this.newsTotalPages(), page + 1));
  }

  private filterNews(newsItems: EventNewsItem[]): EventNewsItem[] {
    return newsItems
      .filter((news) => this.matchesText(news.id.toString(), this.newsIdFilter()))
      .filter((news) => this.matchesText(news.title, this.newsTitleFilter()))
      .filter((news) => this.matchesText(news.processingStatus, this.newsStatusFilter()))
      .filter((news) => this.matchesText(news.classification?.category ?? '-', this.newsCategoryFilter()))
      .filter((news) => this.matchesText(news.classification?.urgencyLevel ?? '-', this.newsUrgencyFilter()))
      .filter((news) => this.matchesText(this.formatDate(news.capturedAt), this.newsCapturedAtFilter()));
  }

  private sortNews(newsItems: EventNewsItem[]): EventNewsItem[] {
    const direction = this.newsSortDirection() === 'asc' ? 1 : -1;
    const column = this.newsSortColumn();
    return [...newsItems].sort((left, right) => direction * this.compareNews(left, right, column));
  }

  private compareNews(left: EventNewsItem, right: EventNewsItem, column: EventNewsSortColumn): number {
    if (column === 'id') {
      return left.id - right.id;
    }
    if (column === 'capturedAt') {
      return new Date(left.capturedAt).getTime() - new Date(right.capturedAt).getTime();
    }
    return this.newsValue(left, column).localeCompare(this.newsValue(right, column), 'es', { sensitivity: 'base' });
  }

  private newsValue(news: EventNewsItem, column: Exclude<EventNewsSortColumn, 'id' | 'capturedAt'>): string {
    const values = {
      title: news.title,
      processingStatus: news.processingStatus,
      category: news.classification?.category ?? '-',
      urgencyLevel: news.classification?.urgencyLevel ?? '-'
    };
    return values[column];
  }

  private matchesText(value: string, filter: string): boolean {
    const normalizedFilter = filter.trim().toLocaleLowerCase('es');
    return !normalizedFilter || value.trim().toLocaleLowerCase('es').includes(normalizedFilter);
  }
}
