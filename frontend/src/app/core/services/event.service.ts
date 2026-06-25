import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';

import { EventDetail, EventListItem } from '../models/event.models';

@Injectable({
  providedIn: 'root'
})
export class EventService {
  private readonly httpClient = inject(HttpClient);

  listEvents() {
    return this.httpClient.get<EventListItem[]>('/api/v1/events');
  }

  getEvent(eventId: number) {
    return this.httpClient.get<EventDetail>(`/api/v1/events/${eventId}`);
  }

  mergeEvents(targetEventId: number, sourceEventIds: number[]) {
    return this.httpClient.post<EventDetail>('/api/v1/events/merge', {
      targetEventId,
      sourceEventIds
    });
  }

  discardEvent(eventId: number) {
    return this.httpClient.post<EventListItem>(`/api/v1/events/${eventId}/discard`, {});
  }
}
