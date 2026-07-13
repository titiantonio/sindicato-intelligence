import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { ButtonModule } from 'primeng/button';
import { InputTextModule } from 'primeng/inputtext';
import { MessageModule } from 'primeng/message';

import { EventAnalysisItem, EventDetail, EventNewsItem } from '../../core/models/event.models';
import { AnalysisService } from '../../core/services/analysis.service';
import { ContentService } from '../../core/services/content.service';
import { EventService } from '../../core/services/event.service';
import { StandardTableComponent } from '../../shared/components/standard-table/standard-table.component';
import { StatusBadgeComponent } from '../../shared/components/status-badge/status-badge.component';

type EventNewsSortColumn = 'id' | 'title' | 'processingStatus' | 'category' | 'urgencyLevel' | 'capturedAt';
type SortDirection = 'asc' | 'desc';

@Component({
  selector: 'app-event-detail-page',
  imports: [ButtonModule, FormsModule, InputTextModule, MessageModule, RouterLink, StandardTableComponent, StatusBadgeComponent],
  templateUrl: './event-detail-page.component.html',
  styleUrl: './event-detail-page.component.scss'
})
export class EventDetailPageComponent implements OnInit {
  private readonly route = inject(ActivatedRoute);
  private readonly eventService = inject(EventService);
  private readonly analysisService = inject(AnalysisService);
  private readonly contentService = inject(ContentService);

  protected readonly event = signal<EventDetail | null>(null);
  protected readonly isLoading = signal(false);
  protected readonly errorMessage = signal<string | null>(null);
  protected readonly successMessage = signal<string | null>(null);
  protected readonly isGeneratingAnalysis = signal(false);
  protected readonly isGeneratingContent = signal(false);
  protected readonly selectedAnalysisId = signal<number | null>(null);
  protected readonly contentTone = signal('INFORMATIVO');
  protected readonly contentType = signal('TELEGRAM_POST');
  protected readonly contentLength = signal('STANDARD');
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
  protected readonly selectedAnalysis = computed(() => {
    const selectedId = this.selectedAnalysisId();
    return this.event()?.analyses.find((analysis) => analysis.id === selectedId) ?? null;
  });
  protected readonly hasActiveDuplicateContent = computed(() => {
    const selected = this.selectedAnalysis();
    const event = this.event();
    if (!selected || !event) {
      return false;
    }
    return event.contents.some((content) =>
      content.analysisId === selected.id &&
      content.channel.toUpperCase() === 'TELEGRAM' &&
      content.contentType === this.contentType() &&
      ['PENDING_REVIEW', 'APPROVED'].includes(content.status)
    );
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
        this.selectedAnalysisId.set(this.defaultAnalysisId(event.analyses));
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

  protected setSelectedAnalysis(value: string | number): void {
    const analysisId = Number(value);
    this.selectedAnalysisId.set(Number.isFinite(analysisId) ? analysisId : null);
  }

  protected generateAnalysis(item: EventDetail): void {
    this.isGeneratingAnalysis.set(true);
    this.errorMessage.set(null);
    this.successMessage.set(null);

    this.analysisService.generateAnalysis(item.id).subscribe({
      next: () => {
        this.successMessage.set('Analisis generado correctamente.');
        this.isGeneratingAnalysis.set(false);
        this.loadEvent(item.id);
      },
      error: (error: { error?: { error?: string } }) => {
        this.errorMessage.set(error.error?.error ?? 'No se pudo generar el analisis.');
        this.isGeneratingAnalysis.set(false);
      }
    });
  }

  protected generateContent(item: EventDetail): void {
    const selected = this.selectedAnalysis();

    if (!selected) {
      this.errorMessage.set('Selecciona un analisis para generar contenido.');
      return;
    }
    if (selected.outdated) {
      this.errorMessage.set('El analisis seleccionado esta obsoleto. Regenera el analisis antes de crear contenido.');
      return;
    }
    if (this.hasActiveDuplicateContent()) {
      this.errorMessage.set('Ya existe contenido pendiente o aprobado para este analisis y tipo editorial.');
      return;
    }

    this.isGeneratingContent.set(true);
    this.errorMessage.set(null);
    this.successMessage.set(null);

    this.contentService.generateContent({
      eventId: item.id,
      analysisId: selected.id,
      channel: 'Telegram',
      tone: this.contentTone(),
      contentType: this.contentType(),
      length: this.contentLength()
    }).subscribe({
      next: () => {
        this.successMessage.set('Contenido generado correctamente.');
        this.isGeneratingContent.set(false);
        this.loadEvent(item.id);
      },
      error: (error: { error?: { error?: string } }) => {
        this.errorMessage.set(error.error?.error ?? 'No se pudo generar el contenido.');
        this.isGeneratingContent.set(false);
      }
    });
  }

  protected setContentType(value: string): void {
    this.contentType.set(value);
    if (value === 'TELEGRAM_SHORT') {
      this.contentLength.set('SHORT');
      return;
    }
    if (value === 'UNION_STATEMENT') {
      this.contentLength.set('LONG');
      return;
    }
    this.contentLength.set('STANDARD');
  }

  private defaultAnalysisId(analyses: EventAnalysisItem[]): number | null {
    return analyses.find((analysis) => !analysis.outdated)?.id ?? analyses[0]?.id ?? null;
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
