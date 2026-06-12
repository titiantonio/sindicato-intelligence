import { Component, OnInit, computed, inject, signal } from '@angular/core';

import { ContentListItem } from '../../core/models/content.models';
import { ContentService } from '../../core/services/content.service';
import { StatusBadgeComponent } from '../../shared/components/status-badge/status-badge.component';

@Component({
  selector: 'app-content-page',
  imports: [StatusBadgeComponent],
  templateUrl: './content-page.component.html',
  styleUrl: './content-page.component.scss'
})
export class ContentPageComponent implements OnInit {
  private readonly contentService = inject(ContentService);

  protected readonly items = signal<ContentListItem[]>([]);
  protected readonly isLoading = signal(false);
  protected readonly errorMessage = signal<string | null>(null);
  protected readonly selectedItem = computed(() => this.items()[0] ?? null);

  ngOnInit(): void {
    this.loadContent();
  }

  protected loadContent(): void {
    this.isLoading.set(true);
    this.errorMessage.set(null);

    this.contentService.listContent().subscribe({
      next: (items) => {
        this.items.set(items);
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
      next: () => this.loadContent(),
      error: (error: { error?: { error?: string } }) => this.errorMessage.set(error.error?.error ?? 'No se pudo aprobar el contenido.')
    });
  }

  protected reject(item: ContentListItem): void {
    this.contentService.rejectContent(item.id).subscribe({
      next: () => this.loadContent(),
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
}