import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { of } from 'rxjs';

import { EventListItem } from '../../core/models/event.models';
import { EventService } from '../../core/services/event.service';
import { EventsPageComponent } from './events-page.component';

describe('EventsPageComponent', () => {
  let fixture: ComponentFixture<EventsPageComponent>;
  let component: EventsPageComponent;
  let eventService: jasmine.SpyObj<EventService>;

  const events: EventListItem[] = [
    eventItem(1, 'Bolsas SIPRI', 'SIPRI', 'HIGH', 3, 'OPEN', '2026-06-13T10:00:00Z'),
    eventItem(2, 'Formacion profesorado', 'FORMACION', 'MEDIUM', 1, 'CLOSED', '2026-06-12T09:00:00Z'),
    eventItem(3, 'Oposiciones Andalucia', 'OPOSICIONES', 'CRITICAL', 8, 'MONITORING', '2026-06-13T12:00:00Z')
  ];

  beforeEach(async () => {
    eventService = jasmine.createSpyObj<EventService>('EventService', ['listEvents', 'mergeEvents', 'discardEvent']);
    eventService.listEvents.and.returnValue(of(events));
    eventService.discardEvent.and.returnValue(of(events[0]));

    await TestBed.configureTestingModule({
      imports: [EventsPageComponent],
      providers: [provideRouter([]), { provide: EventService, useValue: eventService }]
    }).compileComponents();

    fixture = TestBed.createComponent(EventsPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('filters events by global search across visible fields', () => {
    (component as any).setGlobalFilter('oposiciones');

    expect((component as any).displayedEvents().map((event: EventListItem) => event.id)).toEqual([3]);
  });

  it('combines column filters', () => {
    (component as any).setCategoryFilter('SIPRI');
    (component as any).setStatusFilter('OPEN');
    (component as any).setImportanceFilter('HIGH');

    expect((component as any).displayedEvents().map((event: EventListItem) => event.id)).toEqual([1]);
  });

  it('sorts by any visible column', () => {
    expect((component as any).displayedEvents().map((event: EventListItem) => event.id)).toEqual([3, 1, 2]);

    (component as any).changeSort('newsCount');

    expect((component as any).displayedEvents().map((event: EventListItem) => event.id)).toEqual([2, 1, 3]);

    (component as any).changeSort('newsCount');

    expect((component as any).displayedEvents().map((event: EventListItem) => event.id)).toEqual([3, 1, 2]);
  });

  it('paginates filtered events locally', () => {
    (component as any).setPageSize('1');

    expect((component as any).paginatedEvents().map((event: EventListItem) => event.id)).toEqual([3]);

    (component as any).goToNextPage();

    expect((component as any).paginatedEvents().map((event: EventListItem) => event.id)).toEqual([1]);
  });

  it('discards active events and reloads the list', () => {
    const confirmSpy = spyOn(window, 'confirm');

    (component as any).discardEvent(events[0]);
    expect((component as any).pendingConfirmation()?.title).toBe('Descartar evento');
    expect(confirmSpy).not.toHaveBeenCalled();

    (component as any).confirmPendingAction();

    expect(eventService.discardEvent).toHaveBeenCalledWith(1);
    expect(eventService.listEvents).toHaveBeenCalledTimes(2);
    expect((component as any).successMessage()).toContain('#1');
  });

  it('opens app confirmation modal before merging events', () => {
    eventService.mergeEvents.and.returnValue(of({ ...events[0], createdAt: events[0].updatedAt, news: [], analyses: [], contents: [] }));
    const confirmSpy = spyOn(window, 'confirm');

    (component as any).setTargetEventId(1);
    (component as any).toggleSourceEvent(3, true);
    (component as any).requestMergeEvents();

    expect((component as any).pendingConfirmation()?.title).toBe('Fusionar eventos');
    expect(confirmSpy).not.toHaveBeenCalled();

    (component as any).confirmPendingAction();

    expect(eventService.mergeEvents).toHaveBeenCalledWith(1, [3]);
  });

  function eventItem(
    id: number,
    title: string,
    category: string,
    importance: string,
    newsCount: number,
    status: string,
    updatedAt: string
  ): EventListItem {
    return {
      id,
      title,
      description: null,
      category,
      importance,
      status,
      newsCount,
      firstDetectedAt: updatedAt,
      lastUpdatedAt: updatedAt,
      updatedAt
    };
  }
});
