import { FormsModule } from '@angular/forms';
import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';

import { EventListItem } from '../../core/models/event.models';
import { EventService } from '../../core/services/event.service';
import { StatusBadgeComponent } from '../../shared/components/status-badge/status-badge.component';

@Component({
  selector: 'app-events-page',
  imports: [FormsModule, RouterLink, StatusBadgeComponent],
  templateUrl: './events-page.component.html',
  styleUrl: './events-page.component.scss'
})
export class EventsPageComponent implements OnInit {
  private readonly eventService = inject(EventService);

  protected readonly events = signal<EventListItem[]>([]);
  protected readonly isLoading = signal(false);
  protected readonly errorMessage = signal<string | null>(null);
  protected readonly successMessage = signal<string | null>(null);
  protected readonly targetEventId = signal<number | null>(null);
  protected readonly sourceEventIds = signal<number[]>([]);
  protected readonly activeEvents = computed(() => this.events().filter((event) => event.status === 'OPEN' || event.status === 'MONITORING'));

  ngOnInit(): void {
    this.loadEvents();
  }

  protected loadEvents(): void {
    this.isLoading.set(true);
    this.errorMessage.set(null);

    this.eventService.listEvents().subscribe({
      next: (events) => {
        this.events.set(events);
        if (this.targetEventId() === null && this.activeEvents().length > 0) {
          this.targetEventId.set(this.activeEvents()[0].id);
        }
        this.isLoading.set(false);
      },
      error: (error: { error?: { error?: string } }) => {
        this.errorMessage.set(error.error?.error ?? 'No se pudo cargar el listado de eventos.');
        this.isLoading.set(false);
      }
    });
  }

  protected formatDate(value: string): string {
    return new Intl.DateTimeFormat('es-ES', {
      dateStyle: 'short',
      timeStyle: 'short'
    }).format(new Date(value));
  }

  protected setTargetEventId(value: string): void {
    const targetId = Number(value);
    this.targetEventId.set(Number.isNaN(targetId) ? null : targetId);
    this.sourceEventIds.update((ids) => ids.filter((id) => id !== targetId));
  }

  protected toggleSourceEvent(eventId: number, checked: boolean): void {
    this.sourceEventIds.update((ids) => {
      const currentIds = ids.filter((id) => id !== eventId);
      return checked ? [...currentIds, eventId] : currentIds;
    });
  }

  protected isSourceSelected(eventId: number): boolean {
    return this.sourceEventIds().includes(eventId);
  }

  protected mergeEvents(): void {
    const targetId = this.targetEventId();
    const sourceIds = this.sourceEventIds();

    if (targetId === null || sourceIds.length === 0) {
      this.errorMessage.set('Selecciona un evento destino y al menos un evento origen.');
      return;
    }

    if (!confirm('La fusion archivara los eventos origen y movera sus noticias al evento destino.')) {
      return;
    }

    this.errorMessage.set(null);
    this.successMessage.set(null);
    this.eventService.mergeEvents(targetId, sourceIds).subscribe({
      next: (mergedEvent) => {
        this.successMessage.set(`Eventos fusionados correctamente en #${mergedEvent.id}.`);
        this.sourceEventIds.set([]);
        this.targetEventId.set(mergedEvent.id);
        this.loadEvents();
      },
      error: (error: { error?: { error?: string } }) => {
        this.errorMessage.set(error.error?.error ?? 'No se pudo fusionar los eventos.');
      }
    });
  }
}
