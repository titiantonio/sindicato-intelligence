import { DOCUMENT } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { map } from 'rxjs';

import { EventDetail, EventListItem } from '../models/event.models';
import { decodeHtmlEntities } from '../../shared/utils/html-entities';

@Injectable({
  providedIn: 'root'
})
export class EventService {
  private readonly document = inject(DOCUMENT);
  private readonly httpClient = inject(HttpClient);

  listEvents() {
    return this.httpClient.get<EventListItem[]>('/api/v1/events').pipe(
      map((events) => events.map((event) => this.normalizeEventListItem(event)))
    );
  }

  getEvent(eventId: number) {
    return this.httpClient.get<EventDetail>(`/api/v1/events/${eventId}`).pipe(
      map((event) => this.normalizeEventDetail(event))
    );
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

  restoreEvent(eventId: number) {
    return this.httpClient.post<EventListItem>(`/api/v1/events/${eventId}/restore`, {});
  }

  private normalizeEventListItem(event: EventListItem): EventListItem {
    return {
      ...event,
      title: this.decode(event.title),
      description: event.description ? this.decode(event.description) : event.description
    };
  }

  private normalizeEventDetail(event: EventDetail): EventDetail {
    return {
      ...event,
      ...this.normalizeEventListItem(event),
      news: event.news.map((news) => ({
        ...news,
        title: this.decode(news.title),
        summary: news.summary ? this.decode(news.summary) : news.summary,
        classification: news.classification
          ? {
              ...news.classification,
              subcategory: news.classification.subcategory
                ? this.decode(news.classification.subcategory)
                : news.classification.subcategory,
              keywords: news.classification.keywords.map((keyword) => this.decode(keyword)),
              entities: news.classification.entities.map((entity) => this.decode(entity))
            }
          : null
      })),
      analyses: event.analyses.map((analysis) => ({
        ...analysis,
        executiveSummary: this.decode(analysis.executiveSummary),
        unionSummary: this.decode(analysis.unionSummary),
        keyPoints: analysis.keyPoints.map((point) => this.decode(point)),
        risks: analysis.risks.map((risk) => this.decode(risk)),
        opportunities: analysis.opportunities.map((opportunity) => this.decode(opportunity)),
        affectedGroups: analysis.affectedGroups.map((group) => this.decode(group)),
        recommendedMonitoring: analysis.recommendedMonitoring.map((item) => this.decode(item))
      })),
      contents: event.contents.map((content) => ({
        ...content,
        title: this.decode(content.title),
        content: this.decode(content.content)
      }))
    };
  }

  private decode(value: string): string {
    return decodeHtmlEntities(this.document, value);
  }
}
