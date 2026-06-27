import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';

import { NewsDetail, NewsListItem } from '../models/news.models';

@Injectable({
  providedIn: 'root'
})
export class NewsService {
  private readonly httpClient = inject(HttpClient);

  listNews() {
    return this.httpClient.get<NewsListItem[]>('/api/v1/news');
  }

  getNews(newsId: number) {
    return this.httpClient.get<NewsDetail>(`/api/v1/news/${newsId}`);
  }
}
