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

  it('gets news detail', () => {
    service.getNews(12).subscribe();

    const request = httpTestingController.expectOne('/api/v1/news/12');
    expect(request.request.method).toBe('GET');
    request.flush({ id: 12 });
  });
});
