import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { of } from 'rxjs';

import { NewsListItem } from '../../core/models/news.models';
import { NewsService } from '../../core/services/news.service';
import { NewsPageComponent } from './news-page.component';

describe('NewsPageComponent', () => {
  let fixture: ComponentFixture<NewsPageComponent>;
  let component: NewsPageComponent;
  let newsService: jasmine.SpyObj<NewsService>;

  const news: NewsListItem[] = [
    newsItem(1, 'Bolsas SIPRI abiertas', 2, 'CLASSIFIED', 20, 'SIPRI', '2026-06-13T10:00:00Z', '2026-06-13T11:00:00Z'),
    newsItem(2, 'Formacion profesorado', 3, 'CAPTURED', null, null, null, '2026-06-12T09:00:00Z'),
    newsItem(3, 'Oposiciones Andalucia', 2, 'EVENT_MATCHED', 10, 'OPOSICIONES', '2026-06-14T08:00:00Z', '2026-06-14T09:00:00Z')
  ];

  beforeEach(async () => {
    newsService = jasmine.createSpyObj<NewsService>('NewsService', ['listNews', 'listNewsPage', 'getNews']);
    newsService.listNewsPage.and.returnValue(of({ items: news, page: 1, pageSize: 10, totalItems: 30, totalPages: 3 }));

    await TestBed.configureTestingModule({
      imports: [NewsPageComponent],
      providers: [provideRouter([]), { provide: NewsService, useValue: newsService }]
    }).compileComponents();

    fixture = TestBed.createComponent(NewsPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('loads news on init', () => {
    expect(newsService.listNewsPage).toHaveBeenCalledWith(jasmine.objectContaining({
      page: 1,
      pageSize: 10,
      sortColumn: 'capturedAt',
      sortDirection: 'desc'
    }));
    expect((component as any).news()).toEqual(news);
    expect((component as any).totalItems()).toBe(30);
    expect((component as any).totalPages()).toBe(3);
  });

  it('reloads from backend when global filter changes', () => {
    (component as any).setGlobalFilter('oposiciones');

    expect(newsService.listNewsPage).toHaveBeenCalledWith(jasmine.objectContaining({
      page: 1,
      global: 'oposiciones'
    }));
  });

  it('reloads from backend when filters are combined', () => {
    (component as any).setStatusFilter('CLASSIFIED');
    (component as any).setCategoryFilter('SIPRI');
    (component as any).setSourceFilter('Fuente #2');

    expect(newsService.listNewsPage).toHaveBeenCalledWith(jasmine.objectContaining({
      page: 1,
      status: 'CLASSIFIED',
      category: 'SIPRI',
      source: 'Fuente #2'
    }));
  });

  it('reloads from backend when sorting changes', () => {
    (component as any).changeSort('processingStatus');

    expect(newsService.listNewsPage).toHaveBeenCalledWith(jasmine.objectContaining({
      page: 1,
      sortColumn: 'processingStatus',
      sortDirection: 'asc'
    }));

    (component as any).changeSort('processingStatus');

    expect(newsService.listNewsPage).toHaveBeenCalledWith(jasmine.objectContaining({
      page: 1,
      sortColumn: 'processingStatus',
      sortDirection: 'desc'
    }));
  });

  it('reloads from backend when page size changes', () => {
    (component as any).setPageSize('1');

    expect(newsService.listNewsPage).toHaveBeenCalledWith(jasmine.objectContaining({
      page: 1,
      pageSize: 1
    }));
  });

  it('loads selected pages directly', () => {
    (component as any).goToNextPage();

    expect(newsService.listNewsPage).toHaveBeenCalledWith(jasmine.objectContaining({ page: 2 }));

    (component as any).setPageInput('3');
    (component as any).goToPage();

    expect(newsService.listNewsPage).toHaveBeenCalledWith(jasmine.objectContaining({ page: 3 }));
  });

  it('renders links to news detail and associated event', () => {
    const nativeElement: HTMLElement = fixture.nativeElement;

    expect(nativeElement.querySelector('a[href="/news/3"]')?.textContent?.trim()).toBe('Ver');
    expect(nativeElement.querySelector('a[href="/events/10"]')?.textContent?.trim()).toBe('#10');
  });

  function newsItem(
    id: number,
    title: string,
    sourceId: number,
    processingStatus: string,
    eventId: number | null,
    category: string | null,
    publishedAt: string | null,
    capturedAt: string
  ): NewsListItem {
    return {
      id,
      sourceId,
      title,
      processingStatus,
      eventId,
      category,
      publishedAt,
      capturedAt
    };
  }
});
