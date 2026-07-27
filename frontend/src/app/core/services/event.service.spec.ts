import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { EventDetail, EventListItem } from '../models/event.models';
import { EventService } from './event.service';

describe('EventService', () => {
  let service: EventService;
  let httpTestingController: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()]
    });

    service = TestBed.inject(EventService);
    httpTestingController = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpTestingController.verify();
  });

  it('decodes HTML entities in event list text', () => {
    let result: EventListItem[] | undefined;

    service.listEvents().subscribe((events) => {
      result = events;
    });

    const request = httpTestingController.expectOne('/api/v1/events');
    request.flush([eventListItem()]);

    expect(result?.[0].title).toBe('Educación pública');
    expect(result?.[0].description).toBe('Movilización en Andalucía');
  });

  it('decodes HTML entities in event detail and generated content', () => {
    let result: EventDetail | undefined;

    service.getEvent(166).subscribe((event) => {
      result = event;
    });

    const request = httpTestingController.expectOne('/api/v1/events/166');
    request.flush(eventDetail());

    expect(result?.description).toBe('Educación pública en Andalucía');
    expect(result?.news[0].title).toBe('Próxima movilización');
    expect(result?.analyses[0].unionSummary).toBe('Defensa de la educación');
    expect(result?.contents[0].content).toBe('Más inversión pública');
  });

  it('merges events', () => {
    service.mergeEvents(10, [11, 12]).subscribe();

    const request = httpTestingController.expectOne('/api/v1/events/merge');
    expect(request.request.method).toBe('POST');
    expect(request.request.body).toEqual({ targetEventId: 10, sourceEventIds: [11, 12] });
    request.flush({ id: 10 });
  });

  it('discards events', () => {
    service.discardEvent(10).subscribe();

    const request = httpTestingController.expectOne('/api/v1/events/10/discard');
    expect(request.request.method).toBe('POST');
    expect(request.request.body).toEqual({});
    request.flush({ id: 10 });
  });

  it('restores manually discarded events', () => {
    service.restoreEvent(10).subscribe();

    const request = httpTestingController.expectOne('/api/v1/events/10/restore');
    expect(request.request.method).toBe('POST');
    expect(request.request.body).toEqual({});
    request.flush({ id: 10 });
  });

  function eventListItem(): EventListItem {
    return {
      id: 166,
      title: 'Educaci&oacute;n p&uacute;blica',
      description: 'Movilizaci&oacute;n en Andaluc&iacute;a',
      category: 'CONFLICTO_LABORAL',
      importance: 'CRITICAL',
      status: 'OPEN',
      editorialStatus: 'PENDING_ANALYSIS',
      newsCount: 1,
      firstDetectedAt: '2026-07-27T08:00:00Z',
      lastUpdatedAt: '2026-07-27T09:00:00Z',
      updatedAt: '2026-07-27T09:00:00Z'
    };
  }

  function eventDetail(): EventDetail {
    const base = eventListItem();

    return {
      ...base,
      description: 'Educaci&oacute;n p&uacute;blica en Andaluc&iacute;a',
      createdAt: '2026-07-27T08:00:00Z',
      news: [{
        id: 1,
        sourceId: 1,
        title: 'Pr&oacute;xima movilizaci&oacute;n',
        url: 'https://example.com/noticia',
        summary: null,
        processingStatus: 'EVENT_MATCHED',
        publishedAt: '2026-07-27T07:00:00Z',
        capturedAt: '2026-07-27T08:00:00Z',
        classification: null
      }],
      analyses: [{
        id: 1,
        eventId: 166,
        executiveSummary: 'An&aacute;lisis ejecutivo',
        unionSummary: 'Defensa de la educaci&oacute;n',
        keyPoints: [],
        risks: [],
        opportunities: [],
        affectedGroups: [],
        recommendedMonitoring: [],
        analysisType: 'PRIORITY',
        generationTrigger: 'MANUAL',
        eventUpdatedAtSnapshot: '2026-07-27T09:00:00Z',
        contextNewsCount: 1,
        contextTruncated: false,
        outdated: false,
        modelUsed: 'deterministic',
        generatedAt: '2026-07-27T09:30:00Z'
      }],
      contents: [{
        id: 1,
        eventId: 166,
        analysisId: 1,
        createdBy: 1,
        channel: 'Telegram',
        tone: 'INFORMATIVO',
        contentType: 'TELEGRAM_POST',
        length: 'STANDARD',
        title: 'Educaci&oacute;n',
        content: 'M&aacute;s inversi&oacute;n p&uacute;blica',
        status: 'PENDING_REVIEW',
        generatedAt: '2026-07-27T10:00:00Z',
        approvedAt: null,
        generationMetadata: {}
      }]
    };
  }
});
