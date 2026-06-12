import { Component, OnInit, inject, signal } from '@angular/core';

import { PublicationListItem } from '../../core/models/publication.models';
import { PublicationService } from '../../core/services/publication.service';
import { StatusBadgeComponent } from '../../shared/components/status-badge/status-badge.component';

@Component({
  selector: 'app-publications-page',
  imports: [StatusBadgeComponent],
  templateUrl: './publications-page.component.html',
  styleUrl: './publications-page.component.scss'
})
export class PublicationsPageComponent implements OnInit {
  private readonly publicationService = inject(PublicationService);

  protected readonly publications = signal<PublicationListItem[]>([]);
  protected readonly isLoading = signal(false);
  protected readonly errorMessage = signal<string | null>(null);

  ngOnInit(): void {
    this.loadPublications();
  }

  protected loadPublications(): void {
    this.isLoading.set(true);
    this.errorMessage.set(null);

    this.publicationService.listPublications().subscribe({
      next: (publications) => {
        this.publications.set(publications);
        this.isLoading.set(false);
      },
      error: (error: { error?: { error?: string } }) => {
        this.errorMessage.set(error.error?.error ?? 'No se pudo cargar el historico de publicaciones.');
        this.isLoading.set(false);
      }
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

  protected publicationResult(publication: PublicationListItem): string {
    return publication.responsePayload ?? publication.externalId ?? 'Sin respuesta registrada.';
  }
}