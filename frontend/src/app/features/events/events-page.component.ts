import { Component, OnInit, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';

import { EventListItem } from '../../core/models/event.models';
import { EventService } from '../../core/services/event.service';
import { StatusBadgeComponent } from '../../shared/components/status-badge/status-badge.component';

@Component({
  selector: 'app-events-page',
  imports: [RouterLink, StatusBadgeComponent],
  templateUrl: './events-page.component.html',
  styleUrl: './events-page.component.scss'
})
export class EventsPageComponent implements OnInit {
  private readonly eventService = inject(EventService);

  protected readonly events = signal<EventListItem[]>([]);
  protected readonly isLoading = signal(false);
  protected readonly errorMessage = signal<string | null>(null);

  ngOnInit(): void {
    this.loadEvents();
  }

  protected loadEvents(): void {
    this.isLoading.set(true);
    this.errorMessage.set(null);

    this.eventService.listEvents().subscribe({
      next: (events) => {
        this.events.set(events);
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
}