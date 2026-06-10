import { Injectable } from '@angular/core';

import { ContentListItem } from '../models/content.models';
import { MetricCard, PriorityEvent } from '../models/dashboard.models';
import { EventListItem } from '../models/event.models';
import { PublicationListItem } from '../models/publication.models';

@Injectable({
  providedIn: 'root'
})
export class MockDashboardService {
  getMetricCards(): MetricCard[] {
    return [
      { label: 'Noticias capturadas hoy', value: '58', trend: '+12 vs ayer', tone: 'primary' },
      { label: 'Eventos activos', value: '14', trend: '3 criticos', tone: 'warning' },
      { label: 'Contenidos pendientes', value: '6', trend: '2 urgentes', tone: 'success' },
      { label: 'Publicaciones realizadas', value: '12', trend: '1 fallo recuperable', tone: 'danger' }
    ];
  }

  getPriorityEvents(): PriorityEvent[] {
    return [
      {
        id: 108,
        title: 'Convocatoria extraordinaria de SIPRI con cambios de baremacion',
        category: 'SIPRI',
        importance: 'CRITICAL',
        relatedNews: 5,
        updatedAt: 'Hoy 09:42',
        status: 'OPEN'
      },
      {
        id: 104,
        title: 'Mesa sectorial sobre plantillas docentes para el proximo curso',
        category: 'PLANTILLAS',
        importance: 'HIGH',
        relatedNews: 3,
        updatedAt: 'Hoy 08:15',
        status: 'MONITORING'
      },
      {
        id: 99,
        title: 'Novedades de oposiciones 2026 para secundaria en Andalucia',
        category: 'OPOSICIONES',
        importance: 'HIGH',
        relatedNews: 7,
        updatedAt: 'Ayer 18:20',
        status: 'OPEN'
      }
    ];
  }

  getEvents(): EventListItem[] {
    return [
      {
        id: 108,
        title: 'Convocatoria extraordinaria de SIPRI con cambios de baremacion',
        category: 'SIPRI',
        importance: 'CRITICAL',
        newsCount: 5,
        status: 'OPEN',
        updatedAt: '2026-06-10 09:42'
      },
      {
        id: 104,
        title: 'Mesa sectorial sobre plantillas docentes para el proximo curso',
        category: 'PLANTILLAS',
        importance: 'HIGH',
        newsCount: 3,
        status: 'MONITORING',
        updatedAt: '2026-06-10 08:15'
      },
      {
        id: 99,
        title: 'Novedades de oposiciones 2026 para secundaria en Andalucia',
        category: 'OPOSICIONES',
        importance: 'HIGH',
        newsCount: 7,
        status: 'OPEN',
        updatedAt: '2026-06-09 18:20'
      },
      {
        id: 87,
        title: 'Ajustes retributivos del profesorado interino en estudio',
        category: 'RETRIBUCIONES',
        importance: 'MEDIUM',
        newsCount: 2,
        status: 'MONITORING',
        updatedAt: '2026-06-09 16:10'
      }
    ];
  }

  getContent(): ContentListItem[] {
    return [
      {
        id: 301,
        channel: 'Telegram',
        title: 'SIPRI: claves sindicales ante la nueva convocatoria extraordinaria',
        status: 'PENDING_REVIEW',
        createdAt: '2026-06-10 09:55',
        approvedAt: null
      },
      {
        id: 298,
        channel: 'Telegram',
        title: 'Plantillas 2026: lectura sindical de la mesa sectorial',
        status: 'APPROVED',
        createdAt: '2026-06-10 08:40',
        approvedAt: '2026-06-10 09:05'
      },
      {
        id: 284,
        channel: 'Telegram',
        title: 'Oposiciones 2026: que cambia y que debe vigilar el profesorado',
        status: 'PUBLISHED',
        createdAt: '2026-06-09 17:15',
        approvedAt: '2026-06-09 17:40'
      }
    ];
  }

  getPublications(): PublicationListItem[] {
    return [
      {
        id: 801,
        channel: 'Telegram',
        publishedAt: '2026-06-10 09:20',
        status: 'PUBLISHED',
        result: 'Publicado correctamente en canal principal.'
      },
      {
        id: 799,
        channel: 'Telegram',
        publishedAt: '2026-06-10 08:00',
        status: 'FAILED',
        result: 'Timeout temporal de la Bot API. Reintento pendiente.'
      },
      {
        id: 792,
        channel: 'Telegram',
        publishedAt: '2026-06-09 17:45',
        status: 'PUBLISHED',
        result: 'Mensaje entregado y fijado por automatizacion.'
      }
    ];
  }
}
