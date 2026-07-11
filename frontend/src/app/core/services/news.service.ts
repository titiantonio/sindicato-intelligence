import { HttpClient } from '@angular/common/http';
import { HttpParams } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';

import { NewsDetail, NewsListItem, NewsPageParams, NewsPageResponse } from '../models/news.models';

@Injectable({
  providedIn: 'root'
})
export class NewsService {
  private readonly httpClient = inject(HttpClient);

  listNews() {
    return this.httpClient.get<NewsListItem[]>('/api/v1/news');
  }

  listNewsPage(params: NewsPageParams) {
    return this.httpClient.get<NewsPageResponse>('/api/v1/news/page', {
      params: this.toHttpParams(params)
    });
  }

  getNews(newsId: number) {
    return this.httpClient.get<NewsDetail>(`/api/v1/news/${newsId}`);
  }

  discardNews(newsId: number) {
    return this.httpClient.post<NewsDetail>(`/api/v1/news/${newsId}/discard`, {});
  }

  restoreNews(newsId: number) {
    return this.httpClient.post<NewsDetail>(`/api/v1/news/${newsId}/restore`, {});
  }

  private toHttpParams(params: NewsPageParams): HttpParams {
    let httpParams = new HttpParams()
      .set('page', params.page)
      .set('pageSize', params.pageSize)
      .set('sortColumn', params.sortColumn)
      .set('sortDirection', params.sortDirection);

    for (const key of ['global', 'id', 'title', 'source', 'status', 'event', 'category', 'publishedAt', 'capturedAt'] as const) {
      const value = params[key];
      if (value !== undefined && value.trim() !== '') {
        httpParams = httpParams.set(key, value);
      }
    }

    return httpParams;
  }
}
