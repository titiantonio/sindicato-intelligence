import { Component, OnInit, inject, signal } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';

import { EventDetail } from '../../core/models/event.models';
import { EventService } from '../../core/services/event.service';
import { StatusBadgeComponent } from '../../shared/components/status-badge/status-badge.component';

@Component({
  selector: 'app-event-detail-page',
  imports: [RouterLink, StatusBadgeComponent],
  templateUrl: './event-detail-page.component.html',
  styleUrl: './event-detail-page.component.scss'
})
export class EventDetailPageComponent implements OnInit {
  private readonly route = inject(ActivatedRoute);
  private readonly eventService = inject(EventService);

  protected readonly event = signal<EventDetail | null>(null);
  protected readonly isLoading = signal(false);
  protected readonly errorMessage = signal<string | null>(null);

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
}