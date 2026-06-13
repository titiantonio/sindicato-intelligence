import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { ContentService } from './content.service';

describe('ContentService', () => {
  let service: ContentService;
  let httpTestingController: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()]
    });

    service = TestBed.inject(ContentService);
    httpTestingController = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpTestingController.verify();
  });

  it('updates generated content', () => {
    const payload = { title: 'Titulo', content: 'Contenido', tone: 'URGENTE' };

    service.updateContent(7, payload).subscribe();

    const request = httpTestingController.expectOne('/api/v1/content/7');
    expect(request.request.method).toBe('PUT');
    expect(request.request.body).toEqual(payload);
    request.flush({ id: 7, ...payload });
  });
});
