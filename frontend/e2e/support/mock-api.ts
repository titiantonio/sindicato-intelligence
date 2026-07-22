import { Page, Route } from '@playwright/test';

type MockRole = 'ADMIN' | 'EDITOR';

const now = '2026-07-22T10:00:00Z';

export async function mockApi(page: Page): Promise<void> {
  await page.route('**/api/v1/**', async (route) => handleApiRoute(route));
}

export async function loginWithMockRole(page: Page, role: MockRole): Promise<void> {
  await page.goto('/login');
  await page.getByLabel('Email').fill(role === 'ADMIN' ? 'admin.e2e@sindicato.test' : 'editor.e2e@sindicato.test');
  await page.getByLabel('Password').fill('PasswordE2E!123');
  await page.getByRole('button', { name: 'Entrar' }).click();
}

async function handleApiRoute(route: Route): Promise<void> {
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
    await json(route, eventsResponse());
    return;
  }

  if (method === 'GET' && path === '/api/v1/content') {
    await json(route, contentResponse());
    return;
  }

  if (method === 'GET' && path === '/api/v1/publications') {
    await json(route, publicationsResponse());
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

function eventsResponse() {
  return [
    {
      id: 101,
      title: 'Convocatoria extraordinaria de oposiciones',
      description: 'Evento consolidado desde varias noticias educativas.',
      category: 'OPOSICIONES',
      importance: 'CRITICAL',
      status: 'OPEN',
      editorialStatus: 'PENDING_CONTENT',
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

function contentResponse() {
  return [
    {
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
    }
  ];
}

function publicationsResponse() {
  return [
    {
      id: 401,
      contentId: 201,
      channel: 'TELEGRAM',
      publicationType: 'GENERATED_CONTENT',
      titleSnapshot: 'Resumen sindical de oposiciones',
      messageSnapshot: 'Mensaje publicado de prueba.',
      requestedBy: 1,
      requestedByName: 'Admin E2E',
      requestedByEmail: 'admin.e2e@sindicato.test',
      externalId: 'e2e-message-1',
      status: 'PUBLISHED',
      publishedAt: now,
      responsePayload: '{"ok":true,"messageId":"e2e"}',
      scheduledAt: null,
      targets: [],
      attachments: []
    }
  ];
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
