import { Component, OnInit, inject, signal } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';

import { NewsDetail } from '../../core/models/news.models';
import { NewsService } from '../../core/services/news.service';
import { StatusBadgeComponent } from '../../shared/components/status-badge/status-badge.component';

@Component({
  selector: 'app-news-detail-page',
  imports: [RouterLink, StatusBadgeComponent],
  templateUrl: './news-detail-page.component.html',
  styleUrl: './news-detail-page.component.scss'
})
export class NewsDetailPageComponent implements OnInit {
  private readonly route = inject(ActivatedRoute);
  private readonly newsService = inject(NewsService);

  protected readonly news = signal<NewsDetail | null>(null);
  protected readonly isLoading = signal(false);
  protected readonly errorMessage = signal<string | null>(null);

  ngOnInit(): void {
    const newsId = Number(this.route.snapshot.paramMap.get('id'));
    if (!Number.isFinite(newsId)) {
      this.errorMessage.set('Identificador de noticia invalido.');
      return;
    }

    this.loadNews(newsId);
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

  private loadNews(newsId: number): void {
    this.isLoading.set(true);
    this.errorMessage.set(null);

    this.newsService.getNews(newsId).subscribe({
      next: (news) => {
        this.news.set(news);
        this.isLoading.set(false);
      },
      error: (error: { error?: { error?: string } }) => {
        this.errorMessage.set(error.error?.error ?? 'No se pudo cargar el detalle de noticia.');
        this.isLoading.set(false);
      }
    });
  }
}
