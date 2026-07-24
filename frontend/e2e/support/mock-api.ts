import { Page, Route } from '@playwright/test';

type MockRole = 'ADMIN' | 'EDITOR';

interface MockApiState {
  contentItems: MockContentItem[];
  publications: MockPublicationItem[];
  nextContentId: number;
  nextPublicationId: number;
}

interface MockContentItem {
  id: number;
  eventId: number;
  analysisId: number | null;
  createdBy: number;
  channel: string;
  tone: string;
  contentType: string;
  length: string;
  title: string;
  content: string;
  status: string;
  generatedAt: string;
  approvedAt: string | null;
  generationMetadata: Record<string, unknown>;
}

interface MockPublicationItem {
  id: number;
  contentId: number | null;
  channel: string;
  publicationType: 'GENERATED_CONTENT' | 'MANUAL_MESSAGE';
  titleSnapshot: string | null;
  messageSnapshot: string | null;
  requestedBy: number | null;
  requestedByName: string | null;
  requestedByEmail: string | null;
  externalId: string | null;
  status: string;
  publishedAt: string | null;
  responsePayload: string | null;
  scheduledAt: string | null;
  targets: unknown[];
  attachments: unknown[];
}

const now = '2026-07-22T10:00:00Z';

export function createMockApiState(): MockApiState {
  return {
    contentItems: [initialContentItem()],
    publications: [initialPublicationItem()],
    nextContentId: 202,
    nextPublicationId: 402
  };
}

export async function mockApi(page: Page, state = createMockApiState()): Promise<MockApiState> {
  await page.route('**/api/v1/**', async (route) => handleApiRoute(route, state));
  return state;
}

export async function loginWithMockRole(page: Page, role: MockRole): Promise<void> {
  await page.goto('/login');
  await page.getByLabel('Email').fill(role === 'ADMIN' ? 'admin.e2e@sindicato.test' : 'editor.e2e@sindicato.test');
  await page.getByLabel('Password').fill('PasswordE2E!123');
  await page.getByRole('button', { name: 'Entrar' }).click();
}

async function handleApiRoute(route: Route, state: MockApiState): Promise<void> {
  const request = route.request();
  const url = new URL(request.url());
  const method = request.method();
  const path = url.pathname;

  if (method === 'POST' && path === '/api/v1/auth/login') {
    const body = request.postDataJSON() as { email?: string } | null;
    const role: MockRole = body?.email?.startsWith('editor') ? 'EDITOR' : 'ADMIN';
    await json(route, loginResponse(role));
    return;
  }

  if (method === 'GET' && path === '/api/v1/dashboard') {
    await json(route, dashboardResponse());
    return;
  }

  if (method === 'GET' && path === '/api/v1/events') {
    await json(route, eventsResponse(state));
    return;
  }

  const eventDetailMatch = path.match(/^\/api\/v1\/events\/(\d+)$/);
  if (method === 'GET' && eventDetailMatch) {
    await json(route, eventDetailResponse(Number(eventDetailMatch[1]), state));
    return;
  }

  if (method === 'GET' && path === '/api/v1/news/page') {
    await json(route, newsPageResponse());
    return;
  }

  const newsDetailMatch = path.match(/^\/api\/v1\/news\/(\d+)$/);
  if (method === 'GET' && newsDetailMatch) {
    await json(route, newsDetailResponse(Number(newsDetailMatch[1])));
    return;
  }

  if (method === 'POST' && path === '/api/v1/content/generate') {
    const body = request.postDataJSON() as { eventId: number; analysisId: number | null; tone: string; contentType: string; length: string };
    const item = generatedContentItem(state.nextContentId++, body);
    state.contentItems.push(item);
    await json(route, item);
    return;
  }

  if (method === 'GET' && path === '/api/v1/content') {
    await json(route, contentResponse(state));
    return;
  }

  const contentDetailMatch = path.match(/^\/api\/v1\/content\/(\d+)\/detail$/);
  if (method === 'GET' && contentDetailMatch) {
    const content = findContent(state, Number(contentDetailMatch[1]));
    await json(route, { content, event: eventDetailResponse(content.eventId, state) });
    return;
  }

  const contentApproveMatch = path.match(/^\/api\/v1\/content\/(\d+)\/approve$/);
  if (method === 'POST' && contentApproveMatch) {
    const content = findContent(state, Number(contentApproveMatch[1]));
    content.status = 'APPROVED';
    content.approvedAt = now;
    await json(route, content);
    return;
  }

  const contentRejectMatch = path.match(/^\/api\/v1\/content\/(\d+)\/reject$/);
  if (method === 'POST' && contentRejectMatch) {
    const content = findContent(state, Number(contentRejectMatch[1]));
    content.status = 'REJECTED';
    await json(route, content);
    return;
  }

  const contentUpdateMatch = path.match(/^\/api\/v1\/content\/(\d+)$/);
  if (method === 'PUT' && contentUpdateMatch) {
    const body = request.postDataJSON() as { title: string; content: string; tone: string };
    const content = findContent(state, Number(contentUpdateMatch[1]));
    content.title = body.title;
    content.content = body.content;
    content.tone = body.tone;
    content.status = 'PENDING_REVIEW';
    content.approvedAt = null;
    await json(route, content);
    return;
  }

  if (method === 'GET' && path === '/api/v1/publications') {
    await json(route, publicationsResponse(state));
    return;
  }

  if (method === 'GET' && path === '/api/v1/publications/telegram-destinations') {
    await json(route, [{ id: 1, name: 'Canal principal E2E', defaultSelected: true }]);
    return;
  }

  const publicationDetailMatch = path.match(/^\/api\/v1\/publications\/(\d+)\/detail$/);
  if (method === 'GET' && publicationDetailMatch) {
    const publication = state.publications.find((item) => item.id === Number(publicationDetailMatch[1]));
    if (!publication) {
      await json(route, { error: 'Publicacion mock no encontrada' }, 404);
      return;
    }
    const content = publication.contentId === null ? null : findContent(state, publication.contentId);
    await json(route, {
      publication,
      content,
      event: content ? eventDetailResponse(content.eventId, state) : null
    });
    return;
  }

  const publicationScheduleMatch = path.match(/^\/api\/v1\/publications\/(\d+)\/schedule$/);
  if (method === 'POST' && publicationScheduleMatch) {
    const body = request.postDataJSON() as { scheduledAt: string };
    const publication = scheduledPublicationItem(state.nextPublicationId++, findContent(state, Number(publicationScheduleMatch[1])), body.scheduledAt);
    state.publications.unshift(publication);
    await json(route, publication);
    return;
  }

  const publicationPublishMatch = path.match(/^\/api\/v1\/publications\/(\d+)\/publish$/);
  if (method === 'POST' && publicationPublishMatch) {
    const content = findContent(state, Number(publicationPublishMatch[1]));
    content.status = 'PUBLISHED';
    const publication = publishedPublicationItem(state.nextPublicationId++, content);
    state.publications.unshift(publication);
    await json(route, publication);
    return;
  }

  if (method === 'GET' && path === '/api/v1/sources') {
    await json(route, sourcesResponse());
    return;
  }

  if (method === 'GET' && path === '/api/v1/users') {
    await json(route, usersResponse());
    return;
  }

  if (method === 'GET' && path === '/api/v1/audit/users') {
    await json(route, userAuditResponse());
    return;
  }

  if (method === 'GET' && path === '/api/v1/audit/editorial') {
    await json(route, editorialAuditResponse());
    return;
  }

  if (method === 'GET' && path === '/api/v1/ai/metrics') {
    await json(route, aiMetricsResponse());
    return;
  }

  if (method === 'GET' && path === '/api/v1/automation/operations') {
    await json(route, workflowOperationsResponse());
    return;
  }

  await json(route, { error: `Mock API sin respuesta para ${method} ${path}` }, 404);
}

async function json(route: Route, body: unknown, status = 200): Promise<void> {
  await route.fulfill({
    status,
    contentType: 'application/json',
    body: JSON.stringify(body)
  });
}

function loginResponse(role: MockRole) {
  return {
    accessToken: `e2e-access-token-${role.toLowerCase()}`,
    refreshToken: `e2e-refresh-token-${role.toLowerCase()}`,
    user: {
      id: role === 'ADMIN' ? 1 : 2,
      name: role === 'ADMIN' ? 'Admin E2E' : 'Editor E2E',
      role,
      mustChangePassword: false
    }
  };
}

function dashboardResponse() {
  return {
    metricCards: [
      metricCard('Eventos abiertos', '12', '4 criticos', 'primary'),
      metricCard('Contenidos pendientes', '3', 'revision humana', 'warning')
    ],
    priorityEvents: [
      {
        id: 101,
        title: 'Convocatoria extraordinaria de oposiciones',
        category: 'OPOSICIONES',
        importance: 'CRITICAL',
        relatedNews: 5,
        updatedAt: now,
        status: 'OPEN',
        editorialStatus: 'PENDING_CONTENT'
      }
    ]
  };
}

function metricCard(label: string, value: string, subtitle: string, tone: string) {
  return {
    label,
    value,
    trend: '+1',
    tone,
    todayValue: Number.parseInt(value, 10),
    yesterdayValue: Number.parseInt(value, 10) - 1,
    difference: 1,
    title: label,
    subtitle,
    icon: 'pi pi-chart-line',
    badgeLabel: 'Hoy',
    lastUpdatedAt: now,
    items: [
      {
        label: 'Hoy',
        value: Number.parseInt(value, 10),
        tone: 'primary',
        icon: 'pi pi-calendar',
        signed: false
      }
    ]
  };
}

function eventsResponse(state: MockApiState) {
  const hasPublishedContent = state.contentItems.some((content) => content.eventId === 101 && content.status === 'PUBLISHED');
  return [
    {
      id: 101,
      title: 'Convocatoria extraordinaria de oposiciones',
      description: 'Evento consolidado desde varias noticias educativas.',
      category: 'OPOSICIONES',
      importance: 'CRITICAL',
      status: 'OPEN',
      editorialStatus: hasPublishedContent ? 'PUBLISHED' : 'PENDING_CONTENT',
      newsCount: 5,
      firstDetectedAt: now,
      lastUpdatedAt: now,
      updatedAt: now
    },
    {
      id: 102,
      title: 'Actualizacion de bolsas docentes',
      description: 'Seguimiento de SIPRI e interinos.',
      category: 'INTERINOS',
      importance: 'HIGH',
      status: 'MONITORING',
      editorialStatus: 'ANALYZED',
      newsCount: 3,
      firstDetectedAt: now,
      lastUpdatedAt: now,
      updatedAt: now
    }
  ];
}

function newsPageResponse() {
  const item = newsDetailResponse(1001);
  return {
    items: [{
      id: item.id,
      sourceId: item.sourceId,
      sourceName: item.sourceName,
      title: item.title,
      url: item.url,
      processingStatus: item.processingStatus,
      eventId: item.eventId,
      category: item.classification?.category ?? null,
      publishedAt: item.publishedAt,
      capturedAt: item.capturedAt
    }],
    page: 1,
    pageSize: 10,
    totalItems: 1,
    totalPages: 1
  };
}

function newsDetailResponse(newsId: number) {
  return {
    id: newsId,
    sourceId: 501,
    sourceName: 'BOJA Educacion',
    title: 'Convocatoria extraordinaria de oposiciones docentes',
    url: 'https://example.test/noticias/oposiciones',
    summary: 'Informacion consolidada sobre la convocatoria docente.',
    content: 'Contenido completo simulado para la prueba visual.',
    hash: 'mock-news-hash',
    publishedAt: now,
    capturedAt: now,
    processingStatus: 'EVENT_MATCHED',
    createdAt: now,
    updatedAt: now,
    eventId: 101,
    classification: {
      id: 901,
      newsId,
      category: 'OPOSICIONES',
      subcategory: null,
      relevanceScore: 0.91,
      urgencyLevel: 'HIGH',
      sentiment: 'NEUTRAL',
      geographicScope: 'ANDALUCIA',
      keyEntities: ['Consejeria de Desarrollo Educativo'],
      summary: 'Convocatoria docente relevante.',
      classifiedAt: now
    }
  };
}

function eventDetailResponse(eventId: number, state: MockApiState) {
  const baseEvent = eventsResponse(state).find((event) => event.id === eventId) ?? eventsResponse(state)[0];
  return {
    ...baseEvent,
    createdAt: now,
    news: eventNewsResponse(eventId),
    analyses: eventAnalysesResponse(eventId),
    contents: state.contentItems.filter((content) => content.eventId === eventId)
  };
}

function eventNewsResponse(eventId: number) {
  return [
    {
      id: eventId === 101 ? 1001 : 1002,
      sourceId: 501,
      title: eventId === 101 ? 'La Junta anuncia convocatoria extraordinaria de oposiciones' : 'Actualizacion de bolsas docentes SIPRI',
      url: 'https://example.test/noticia-e2e',
      summary: 'Noticia educativa de prueba para el flujo editorial.',
      processingStatus: 'EVENT_MATCHED',
      publishedAt: now,
      capturedAt: now,
      classification: {
        id: 901,
        newsId: eventId === 101 ? 1001 : 1002,
        category: eventId === 101 ? 'OPOSICIONES' : 'INTERINOS',
        subcategory: null,
        relevanceScore: 0.91,
        impactLevel: 'HIGH',
        urgencyLevel: 'HIGH',
        keywords: ['docentes', 'Andalucia'],
        entities: ['Junta de Andalucia'],
        classifiedAt: now
      }
    }
  ];
}

function eventAnalysesResponse(eventId: number) {
  return [
    {
      id: eventId === 101 ? 301 : 302,
      eventId,
      executiveSummary: eventId === 101 ? 'Impacto relevante en oposiciones docentes.' : 'Seguimiento relevante para bolsas docentes.',
      unionSummary: 'Resumen sindical mockeado sin llamada a IA real.',
      keyPoints: ['Punto clave de seguimiento'],
      risks: ['Riesgo operativo controlado'],
      opportunities: ['Oportunidad informativa'],
      affectedGroups: ['Docentes'],
      recommendedMonitoring: ['Revisar nuevas comunicaciones oficiales'],
      analysisType: 'STANDARD',
      generationTrigger: 'MANUAL',
      eventUpdatedAtSnapshot: now,
      contextNewsCount: 1,
      contextTruncated: false,
      outdated: false,
      modelUsed: 'mock-model',
      generatedAt: now
    }
  ];
}

function contentResponse(state: MockApiState) {
  return state.contentItems;
}

function publicationsResponse(state: MockApiState) {
  return state.publications;
}

function initialContentItem(): MockContentItem {
  return {
    id: 201,
    eventId: 101,
    analysisId: 301,
    createdBy: 1,
    channel: 'TELEGRAM',
    tone: 'INFORMATIVO',
    contentType: 'TELEGRAM_POST',
    length: 'SHORT',
    title: 'Resumen sindical de oposiciones',
    content: 'Contenido editorial de prueba para revision humana.',
    status: 'PENDING_REVIEW',
    generatedAt: now,
    approvedAt: null,
    generationMetadata: {}
  };
}

function initialPublicationItem(): MockPublicationItem {
  return publishedPublicationItem(401, initialContentItem());
}

function generatedContentItem(id: number, payload: { eventId: number; analysisId: number | null; tone: string; contentType: string; length: string }): MockContentItem {
  return {
    id,
    eventId: payload.eventId,
    analysisId: payload.analysisId,
    createdBy: 2,
    channel: 'TELEGRAM',
    tone: payload.tone,
    contentType: payload.contentType,
    length: payload.length,
    title: payload.contentType === 'TELEGRAM_SHORT' ? 'Resumen breve E2E de oposiciones' : 'Contenido editorial E2E de oposiciones',
    content: 'Borrador editorial generado de forma mockeada para revision humana.',
    status: 'PENDING_REVIEW',
    generatedAt: now,
    approvedAt: null,
    generationMetadata: { e2e: true }
  };
}

function publishedPublicationItem(id: number, content: MockContentItem): MockPublicationItem {
  return {
    id,
    contentId: content.id,
    channel: 'TELEGRAM',
    publicationType: 'GENERATED_CONTENT',
    titleSnapshot: content.title,
    messageSnapshot: content.content,
    requestedBy: 1,
    requestedByName: 'Admin E2E',
    requestedByEmail: 'admin.e2e@sindicato.test',
    externalId: `e2e-message-${id}`,
    status: 'PUBLISHED',
    publishedAt: now,
    responsePayload: '{"ok":true,"messageId":"e2e"}',
    scheduledAt: null,
    targets: [],
    attachments: []
  };
}

function scheduledPublicationItem(id: number, content: MockContentItem, scheduledAt: string): MockPublicationItem {
  return {
    ...publishedPublicationItem(id, content),
    externalId: null,
    status: 'SCHEDULED',
    publishedAt: null,
    responsePayload: null,
    scheduledAt
  };
}

function findContent(state: MockApiState, contentId: number): MockContentItem {
  const content = state.contentItems.find((item) => item.id === contentId);
  if (!content) {
    throw new Error(`Contenido mock no encontrado: ${contentId}`);
  }
  return content;
}

function sourcesResponse() {
  return [
    {
      id: 501,
      name: 'BOJA Educacion',
      url: 'https://example.test/rss/boja-educacion.xml',
      type: 'RSS',
      priority: 1,
      active: true,
      createdAt: now,
      updatedAt: now
    }
  ];
}

function usersResponse() {
  return [
    {
      id: 1,
      email: 'admin.e2e@sindicato.test',
      name: 'Admin E2E',
      role: 'ADMIN',
      active: true,
      mustChangePassword: false,
      status: 'ACTIVE',
      temporaryPasswordExpiresAt: null,
      lastLoginAt: now,
      lastPasswordChangeAt: now
    }
  ];
}

function userAuditResponse() {
  return [
    {
      id: 601,
      userId: 1,
      userDisplayName: 'Admin E2E',
      actorEmail: 'admin.e2e@sindicato.test',
      action: 'LOGIN_SUCCESS',
      details: 'Acceso simulado para E2E',
      createdAt: now
    }
  ];
}

function editorialAuditResponse() {
  return [
    {
      id: 701,
      userId: 1,
      userDisplayName: 'Admin E2E',
      action: 'CONTENT_APPROVED',
      entityType: 'CONTENT',
      entityId: 201,
      oldValues: null,
      newValues: '{"status":"APPROVED"}',
      createdAt: now
    }
  ];
}

function aiMetricsResponse() {
  return {
    totalOperations: 4,
    successCount: 3,
    failedCount: 1,
    averageLatencyMs: 1200,
    p95LatencyMs: 1800,
    successRate: 75,
    failureRate: 25,
    previousTotalOperations: 2,
    previousSuccessCount: 2,
    previousFailedCount: 0,
    previousAverageLatencyMs: 900,
    totalDifference: 2,
    successRateDifference: -25,
    failureRateDifference: 25,
    averageLatencyDifference: 300,
    recentMetrics: []
  };
}

function workflowOperationsResponse() {
  return [
    {
      id: 'op-e2e-1',
      workflowCode: 'WF02_CLASSIFICATION',
      operationType: 'CLASSIFY_NEWS',
      status: 'SUCCESS',
      relatedEntityType: 'NEWS',
      relatedEntityId: 1001,
      createdAt: now,
      latencyMs: 1200,
      promptKey: 'news_classification',
      provider: 'mock-ai',
      model: 'mock-model',
      errorMessage: null,
      details: {}
    }
  ];
}
