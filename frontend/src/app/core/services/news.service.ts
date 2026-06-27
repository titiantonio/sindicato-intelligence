import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';

import { NewsDetail } from '../models/news.models';

@Injectable({
  providedIn: 'root'
})
export class NewsService {
  private readonly httpClient = inject(HttpClient);

  getNews(newsId: number) {
    return this.httpClient.get<NewsDetail>(`/api/v1/news/${newsId}`);
  }
}
