import { Component, OnInit, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { ButtonModule } from 'primeng/button';
import { DialogModule } from 'primeng/dialog';
import { InputTextModule } from 'primeng/inputtext';
import { MessageModule } from 'primeng/message';

import { TelegramPublicationDestination } from '../../core/models/application-settings.models';
import { PublicationListItem } from '../../core/models/publication.models';
import { ApplicationSettingsService } from '../../core/services/application-settings.service';
import { PublicationService } from '../../core/services/publication.service';
import { StatusBadgeComponent } from '../../shared/components/status-badge/status-badge.component';
import { formatPublicationResult } from './publication-result.formatter';

@Component({
  selector: 'app-publications-page',
  imports: [ButtonModule, DialogModule, FormsModule, InputTextModule, MessageModule, RouterLink, StatusBadgeComponent],
  templateUrl: './publications-page.component.html',
  styleUrl: './publications-page.component.scss'
})
export class PublicationsPageComponent implements OnInit {
  private readonly publicationService = inject(PublicationService);
  private readonly settingsService = inject(ApplicationSettingsService);

  protected readonly publications = signal<PublicationListItem[]>([]);
  protected readonly destinations = signal<TelegramPublicationDestination[]>([]);
  protected readonly manualDialogVisible = signal(false);
  protected readonly manualTitle = signal('');
  protected readonly manualMessage = signal('');
  protected readonly selectedDestinationIds = signal<number[]>([]);
  protected readonly selectedFiles = signal<File[]>([]);
  protected readonly isLoading = signal(false);
  protected readonly isSendingManual = signal(false);
  protected readonly errorMessage = signal<string | null>(null);
  protected readonly successMessage = signal<string | null>(null);

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
    return formatPublicationResult(publication);
  }

  protected publicationDateLabel(publication: PublicationListItem): string {
    if (publication.status === 'SCHEDULED') {
      return `Programada: ${this.formatDate(publication.scheduledAt)}`;
    }

    return `Publicada: ${this.formatDate(publication.publishedAt)}`;
  }

  protected publicationOrigin(publication: PublicationListItem): string {
    return publication.publicationType === 'MANUAL_MESSAGE' ? 'Manual' : `Contenido #${publication.contentId}`;
  }

  protected openManualDialog(): void {
    this.manualDialogVisible.set(true);
    this.errorMessage.set(null);
    this.successMessage.set(null);
    this.settingsService.getTelegramSettings().subscribe({
      next: (settings) => {
        const activeDestinations = (settings.destinations ?? []).filter((destination) => destination.active && destination.id !== null);
        this.destinations.set(activeDestinations);
        this.selectedDestinationIds.set(activeDestinations.filter((destination) => destination.defaultSelected).map((destination) => destination.id as number));
      },
      error: (error: { error?: { error?: string } }) => {
        this.errorMessage.set(error.error?.error ?? 'No se pudieron cargar los destinos Telegram.');
      }
    });
  }

  protected updateDestinationSelection(destinationId: number, checked: boolean): void {
    const selected = new Set(this.selectedDestinationIds());
    if (checked) {
      selected.add(destinationId);
    } else {
      selected.delete(destinationId);
    }
    this.selectedDestinationIds.set([...selected]);
  }

  protected updateSelectedFiles(event: Event): void {
    const input = event.target as HTMLInputElement;
    this.selectedFiles.set(input.files ? Array.from(input.files) : []);
  }

  protected sendManualPublication(): void {
    this.isSendingManual.set(true);
    this.errorMessage.set(null);
    this.successMessage.set(null);
    this.publicationService.publishManual({
      channel: 'TELEGRAM',
      title: this.manualTitle(),
      message: this.manualMessage(),
      destinationIds: this.selectedDestinationIds(),
      files: this.selectedFiles()
    }).subscribe({
      next: () => {
        this.successMessage.set('Mensaje manual enviado.');
        this.isSendingManual.set(false);
        this.manualDialogVisible.set(false);
        this.manualTitle.set('');
        this.manualMessage.set('');
        this.selectedFiles.set([]);
        this.loadPublications();
      },
      error: (error: { error?: { error?: string } }) => {
        this.errorMessage.set(error.error?.error ?? 'No se pudo enviar el mensaje manual.');
        this.isSendingManual.set(false);
      }
    });
  }

  protected fileSummary(file: File): string {
    return `${file.name} (${Math.ceil(file.size / 1024)} KB)`;
  }
}
