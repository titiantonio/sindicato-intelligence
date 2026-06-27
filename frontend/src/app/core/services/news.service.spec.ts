import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { NewsService } from './news.service';

describe('NewsService', () => {
  let service: NewsService;
  let httpTestingController: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()]
    });

    service = TestBed.inject(NewsService);
    httpTestingController = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpTestingController.verify();
  });

  it('lists news', () => {
    service.listNews().subscribe();

    const request = httpTestingController.expectOne('/api/v1/news');
    expect(request.request.method).toBe('GET');
    request.flush([]);
  });

  it('lists paged news with query params', () => {
    service.listNewsPage({
      page: 2,
      pageSize: 25,
      global: 'oposiciones',
      id: '',
      title: 'Andalucia',
      sortColumn: 'capturedAt',
      sortDirection: 'desc'
    }).subscribe();

    const request = httpTestingController.expectOne((candidate) => candidate.url === '/api/v1/news/page');
    expect(request.request.method).toBe('GET');
    expect(request.request.params.get('page')).toBe('2');
    expect(request.request.params.get('pageSize')).toBe('25');
    expect(request.request.params.get('global')).toBe('oposiciones');
    expect(request.request.params.get('title')).toBe('Andalucia');
    expect(request.request.params.has('id')).toBeFalse();
    expect(request.request.params.get('sortColumn')).toBe('capturedAt');
    expect(request.request.params.get('sortDirection')).toBe('desc');
    request.flush({ items: [], page: 2, pageSize: 25, totalItems: 0, totalPages: 1 });
  });

  it('gets news detail', () => {
    service.getNews(12).subscribe();

    const request = httpTestingController.expectOne('/api/v1/news/12');
    expect(request.request.method).toBe('GET');
    request.flush({ id: 12 });
  });
});
