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
    newsService = jasmine.createSpyObj<NewsService>('NewsService', ['listNews', 'getNews']);
    newsService.listNews.and.returnValue(of(news));

    await TestBed.configureTestingModule({
      imports: [NewsPageComponent],
      providers: [provideRouter([]), { provide: NewsService, useValue: newsService }]
    }).compileComponents();

    fixture = TestBed.createComponent(NewsPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('loads news on init', () => {
    expect(newsService.listNews).toHaveBeenCalled();
    expect((component as any).news()).toEqual(news);
  });

  it('filters news by global search across visible fields', () => {
    (component as any).setGlobalFilter('oposiciones');

    expect((component as any).displayedNews().map((item: NewsListItem) => item.id)).toEqual([3]);
  });

  it('combines status, category and source filters', () => {
    (component as any).setStatusFilter('CLASSIFIED');
    (component as any).setCategoryFilter('SIPRI');
    (component as any).setSourceFilter('Fuente #2');

    expect((component as any).displayedNews().map((item: NewsListItem) => item.id)).toEqual([1]);
  });

  it('sorts by date, status, title and id', () => {
    expect((component as any).displayedNews().map((item: NewsListItem) => item.id)).toEqual([3, 1, 2]);

    (component as any).changeSort('processingStatus');
    expect((component as any).displayedNews().map((item: NewsListItem) => item.id)).toEqual([2, 1, 3]);

    (component as any).changeSort('title');
    expect((component as any).displayedNews().map((item: NewsListItem) => item.id)).toEqual([1, 2, 3]);

    (component as any).changeSort('id');
    (component as any).changeSort('id');
    expect((component as any).displayedNews().map((item: NewsListItem) => item.id)).toEqual([3, 2, 1]);
  });

  it('paginates filtered news locally', () => {
    (component as any).setPageSize('1');

    expect((component as any).paginatedNews().map((item: NewsListItem) => item.id)).toEqual([3]);

    (component as any).goToNextPage();

    expect((component as any).paginatedNews().map((item: NewsListItem) => item.id)).toEqual([1]);
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
      url: `https://example.test/news/${id}`,
      summary: null,
      content: null,
      hash: `hash-${id}`,
      publishedAt,
      capturedAt,
      processingStatus,
      createdAt: capturedAt,
      updatedAt: capturedAt,
      eventId,
      classification: category === null
        ? null
        : {
          id,
          newsId: id,
          category,
          subcategory: null,
          impactLevel: 'MEDIUM',
          urgencyLevel: 'LOW',
          relevanceScore: 60,
          keywords: [],
          entities: [],
          classifiedAt: capturedAt
        }
    };
  }
});
