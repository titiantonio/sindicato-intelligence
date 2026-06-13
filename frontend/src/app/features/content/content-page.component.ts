import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';

import { ContentListItem } from '../../core/models/content.models';
import { ContentService } from '../../core/services/content.service';
import { PublicationService } from '../../core/services/publication.service';
import { StatusBadgeComponent } from '../../shared/components/status-badge/status-badge.component';

@Component({
  selector: 'app-content-page',
  imports: [FormsModule, StatusBadgeComponent],
  templateUrl: './content-page.component.html',
  styleUrl: './content-page.component.scss'
})
export class ContentPageComponent implements OnInit {
  private readonly contentService = inject(ContentService);
  private readonly publicationService = inject(PublicationService);

  protected readonly items = signal<ContentListItem[]>([]);
  protected readonly isLoading = signal(false);
  protected readonly errorMessage = signal<string | null>(null);
  protected readonly successMessage = signal<string | null>(null);
  protected readonly selectedItemId = signal<number | null>(null);
  protected readonly editTitle = signal('');
  protected readonly editContent = signal('');
  protected readonly editTone = signal('');
  protected readonly scheduleAt = signal('');
  protected readonly selectedItem = computed(() => {
    const selectedId = this.selectedItemId();
    return this.items().find((item) => item.id === selectedId) ?? this.items()[0] ?? null;
  });

  ngOnInit(): void {
    this.loadContent();
  }

  protected loadContent(): void {
    this.isLoading.set(true);
    this.errorMessage.set(null);

    this.contentService.listContent().subscribe({
      next: (items) => {
        this.items.set(items);
        const selected = this.selectedItem();
        if (selected) {
          this.selectItem(selected);
        }
        this.isLoading.set(false);
      },
      error: (error: { error?: { error?: string } }) => {
        this.errorMessage.set(error.error?.error ?? 'No se pudo cargar la bandeja editorial.');
        this.isLoading.set(false);
      }
    });
  }

  protected approve(item: ContentListItem): void {
    this.contentService.approveContent(item.id).subscribe({
      next: () => {
        this.successMessage.set('Contenido aprobado correctamente.');
        this.loadContent();
      },
      error: (error: { error?: { error?: string } }) => this.errorMessage.set(error.error?.error ?? 'No se pudo aprobar el contenido.')
    });
  }

  protected reject(item: ContentListItem): void {
    this.contentService.rejectContent(item.id).subscribe({
      next: () => {
        this.successMessage.set('Contenido rechazado correctamente.');
        this.loadContent();
      },
      error: (error: { error?: { error?: string } }) => this.errorMessage.set(error.error?.error ?? 'No se pudo rechazar el contenido.')
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

  protected selectItem(item: ContentListItem): void {
    this.selectedItemId.set(item.id);
    this.editTitle.set(item.title);
    this.editContent.set(item.content);
    this.editTone.set(item.tone);
    this.scheduleAt.set('');
  }

  protected saveEdit(item: ContentListItem): void {
    if (item.status === 'PUBLISHED') {
      return;
    }

    this.errorMessage.set(null);
    this.successMessage.set(null);
    this.contentService.updateContent(item.id, {
      title: this.editTitle(),
      content: this.editContent(),
      tone: this.editTone()
    }).subscribe({
      next: (updatedItem) => {
        this.successMessage.set('Contenido actualizado y devuelto a revision.');
        this.selectedItemId.set(updatedItem.id);
        this.loadContent();
      },
      error: (error: { error?: { error?: string } }) => this.errorMessage.set(error.error?.error ?? 'No se pudo actualizar el contenido.')
    });
  }

  protected schedulePublication(item: ContentListItem): void {
    if (item.status !== 'APPROVED') {
      this.errorMessage.set('Solo se puede programar contenido aprobado.');
      return;
    }

    const scheduleValue = this.scheduleAt();
    const scheduledDate = scheduleValue ? new Date(scheduleValue) : null;
    if (scheduledDate === null || Number.isNaN(scheduledDate.getTime()) || scheduledDate <= new Date()) {
      this.errorMessage.set('Selecciona una fecha futura para programar la publicacion.');
      return;
    }

    this.errorMessage.set(null);
    this.successMessage.set(null);
    this.publicationService.schedulePublication(item.id, scheduledDate.toISOString()).subscribe({
      next: () => {
        this.successMessage.set('Publicacion programada correctamente.');
        this.scheduleAt.set('');
        this.loadContent();
      },
      error: (error: { error?: { error?: string } }) => this.errorMessage.set(error.error?.error ?? 'No se pudo programar la publicacion.')
    });
  }
}
