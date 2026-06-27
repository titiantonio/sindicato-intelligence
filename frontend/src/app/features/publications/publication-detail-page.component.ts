import { Component, OnInit, inject, signal } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';

import { PublicationDetail } from '../../core/models/publication.models';
import { PublicationService } from '../../core/services/publication.service';
import { StatusBadgeComponent } from '../../shared/components/status-badge/status-badge.component';
import { formatPublicationResult } from './publication-result.formatter';

@Component({
  selector: 'app-publication-detail-page',
  imports: [RouterLink, StatusBadgeComponent],
  templateUrl: './publication-detail-page.component.html',
  styleUrl: './publication-detail-page.component.scss'
})
export class PublicationDetailPageComponent implements OnInit {
  private readonly route = inject(ActivatedRoute);
  private readonly publicationService = inject(PublicationService);

  protected readonly detail = signal<PublicationDetail | null>(null);
  protected readonly isLoading = signal(false);
  protected readonly errorMessage = signal<string | null>(null);

  ngOnInit(): void {
    const publicationId = Number(this.route.snapshot.paramMap.get('id'));
    if (!Number.isFinite(publicationId)) {
      this.errorMessage.set('Identificador de publicacion invalido.');
      return;
    }

    this.loadPublication(publicationId);
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

  protected publicationResult(): string {
    return formatPublicationResult(this.detail()?.publication);
  }

  private loadPublication(publicationId: number): void {
    this.isLoading.set(true);
    this.errorMessage.set(null);

    this.publicationService.getPublicationDetail(publicationId).subscribe({
      next: (detail) => {
        this.detail.set(detail);
        this.isLoading.set(false);
      },
      error: (error: { error?: { error?: string } }) => {
        this.errorMessage.set(error.error?.error ?? 'No se pudo cargar el detalle de publicacion.');
        this.isLoading.set(false);
      }
    });
  }
}
