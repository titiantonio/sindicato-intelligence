import { Component, OnInit, inject, signal } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { MessageModule } from 'primeng/message';

import { ContentDetail } from '../../core/models/content.models';
import { ContentService } from '../../core/services/content.service';
import { StatusBadgeComponent } from '../../shared/components/status-badge/status-badge.component';

@Component({
  selector: 'app-content-detail-page',
  imports: [MessageModule, RouterLink, StatusBadgeComponent],
  templateUrl: './content-detail-page.component.html',
  styleUrl: './content-detail-page.component.scss'
})
export class ContentDetailPageComponent implements OnInit {
  private readonly route = inject(ActivatedRoute);
  private readonly contentService = inject(ContentService);

  protected readonly detail = signal<ContentDetail | null>(null);
  protected readonly isLoading = signal(false);
  protected readonly errorMessage = signal<string | null>(null);

  ngOnInit(): void {
    const contentId = Number(this.route.snapshot.paramMap.get('id'));
    if (!Number.isFinite(contentId)) {
      this.errorMessage.set('Identificador de contenido invalido.');
      return;
    }

    this.loadContent(contentId);
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

  private loadContent(contentId: number): void {
    this.isLoading.set(true);
    this.errorMessage.set(null);

    this.contentService.getContentDetail(contentId).subscribe({
      next: (detail) => {
        this.detail.set(detail);
        this.isLoading.set(false);
      },
      error: (error: { error?: { error?: string } }) => {
        this.errorMessage.set(error.error?.error ?? 'No se pudo cargar el detalle de contenido.');
        this.isLoading.set(false);
      }
    });
  }
}
