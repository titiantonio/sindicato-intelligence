import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, provideRouter } from '@angular/router';
import { of } from 'rxjs';

import { EventDetail } from '../../core/models/event.models';
import { ContentService } from '../../core/services/content.service';
import { EventService } from '../../core/services/event.service';
import { EventDetailPageComponent } from './event-detail-page.component';

describe('EventDetailPageComponent', () => {
  let fixture: ComponentFixture<EventDetailPageComponent>;
  let contentService: jasmine.SpyObj<ContentService>;
  let eventService: jasmine.SpyObj<EventService>;

  beforeEach(async () => {
    contentService = jasmine.createSpyObj<ContentService>('ContentService', ['generateContent']);
    eventService = jasmine.createSpyObj<EventService>('EventService', ['getEvent']);
    eventService.getEvent.and.returnValue(of(eventDetail()));
    contentService.generateContent.and.returnValue(of({
      id: 9,
      eventId: 7,
      createdBy: 1,
      channel: 'Telegram',
      tone: 'INFORMATIVO',
      title: 'Contenido',
      content: 'Texto',
      status: 'PENDING_REVIEW',
      generatedAt: '2026-06-16T10:00:00Z',
      approvedAt: null
    }));

    await TestBed.configureTestingModule({
      imports: [EventDetailPageComponent],
      providers: [
        provideRouter([]),
        { provide: ContentService, useValue: contentService },
        { provide: EventService, useValue: eventService },
        {
          provide: ActivatedRoute,
          useValue: {
            snapshot: {
              paramMap: {
                get: () => '7'
              }
            }
          }
        }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(EventDetailPageComponent);
    fixture.detectChanges();
  });

  it('generates content for the selected event analysis', () => {
    (fixture.componentInstance as any).generateContent(eventDetail());

    expect(contentService.generateContent).toHaveBeenCalledWith({
      eventId: 7,
      analysisId: 3,
      channel: 'Telegram',
      tone: 'INFORMATIVO',
      length: 'MEDIUM'
    });
  });

  function eventDetail(): EventDetail {
    return {
      id: 7,
      title: 'Evento SIPRI',
      description: 'Descripcion',
      category: 'SIPRI',
      importance: 'HIGH',
      status: 'OPEN',
      newsCount: 1,
      firstDetectedAt: '2026-06-16T09:00:00Z',
      lastUpdatedAt: '2026-06-16T09:00:00Z',
      updatedAt: '2026-06-16T09:00:00Z',
      createdAt: '2026-06-16T09:00:00Z',
      news: [],
      analyses: [
        {
          id: 3,
          eventId: 7,
          executiveSummary: 'Resumen',
          unionSummary: 'Resumen sindical',
          keyPoints: [],
          risks: [],
          opportunities: [],
          modelUsed: 'deterministic',
          generatedAt: '2026-06-16T09:30:00Z'
        }
      ],
      contents: []
    };
  }
});
