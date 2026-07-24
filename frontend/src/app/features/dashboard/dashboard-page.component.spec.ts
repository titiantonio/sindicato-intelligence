import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { Router, RouterLink, provideRouter } from '@angular/router';
import { of } from 'rxjs';

import { AutomationService } from '../../core/services/automation.service';
import { ContentService } from '../../core/services/content.service';
import { DashboardService } from '../../core/services/dashboard.service';
import { DashboardPageComponent } from './dashboard-page.component';

describe('DashboardPageComponent', () => {
  let fixture: ComponentFixture<DashboardPageComponent>;
  let automationService: jasmine.SpyObj<AutomationService>;
  let contentService: jasmine.SpyObj<ContentService>;
  let dashboardService: jasmine.SpyObj<DashboardService>;
  let router: Router;

  beforeEach(async () => {
    automationService = jasmine.createSpyObj<AutomationService>('AutomationService', ['runClassifications', 'runEventDetection', 'runAnalysis']);
    automationService.runClassifications.and.returnValue(of({ processedCount: 2, successCount: 2, failedCount: 0, skippedCount: 0, errors: [] }));
    automationService.runEventDetection.and.returnValue(of({ processedCount: 1, successCount: 1, failedCount: 0, skippedCount: 0, errors: [] }));
    automationService.runAnalysis.and.returnValue(of({ processedCount: 1, successCount: 1, failedCount: 0, skippedCount: 0, errors: [] }));
    contentService = jasmine.createSpyObj<ContentService>('ContentService', ['generateContent']);
    contentService.generateContent.and.returnValue(of({
      id: 30,
      eventId: 8,
      analysisId: 15,
      createdBy: 1,
      channel: 'TELEGRAM',
      tone: 'INFORMATIVO',
      contentType: 'TELEGRAM_POST',
      length: 'STANDARD',
      title: 'Contenido generado',
      content: 'Mensaje',
      status: 'PENDING_REVIEW',
      generatedAt: '2026-06-13T10:05:00Z',
      approvedAt: null,
      generationMetadata: {}
    }));
    dashboardService = jasmine.createSpyObj<DashboardService>('DashboardService', ['getDashboard']);
    dashboardService.getDashboard.and.returnValue(of({
      metricCards: [
        {
          label: 'Noticias capturadas',
          value: '12',
          trend: '+5',
          tone: 'primary',
          todayValue: 12,
          yesterdayValue: 7,
          difference: 5,
          title: 'Noticias',
          subtitle: 'Ultima captura',
          icon: 'news',
          badgeLabel: 'Hoy',
          lastUpdatedAt: '2026-06-13T10:00:00Z',
          items: [
            { label: 'Capturadas hoy', value: 12, tone: 'primary', icon: 'news', signed: false },
            { label: 'Diferencia vs anterior', value: 5, tone: 'success', icon: 'trend', signed: true },
            { label: 'Total acumulado', value: 35, tone: 'neutral', icon: 'total', signed: false }
          ]
        }
      ],
      priorityEvents: [
        {
          id: 7,
          title: 'Convocatoria urgente con varias noticias',
          category: 'SIPRI',
          importance: 'CRITICAL',
          relatedNews: 4,
          updatedAt: '2026-06-13T08:00:00Z',
          status: 'OPEN',
          editorialStatus: 'PENDING_ANALYSIS'
        },
        {
          id: 8,
          title: 'Convocatoria urgente reciente',
          category: 'SIPRI',
          importance: 'CRITICAL',
          relatedNews: 1,
          updatedAt: '2026-06-13T10:00:00Z',
          status: 'MONITORING',
          editorialStatus: 'ANALYZED_PENDING_CONTENT'
        },
        {
          id: 9,
          title: 'Mesa sectorial con muchas noticias',
          category: 'SINDICAL',
          importance: 'HIGH',
          relatedNews: 7,
          updatedAt: '2026-06-13T11:00:00Z',
          status: 'OPEN',
          editorialStatus: 'PENDING_ANALYSIS'
        }
      ]
    }));

    await TestBed.configureTestingModule({
      imports: [DashboardPageComponent],
      providers: [
        provideRouter([]),
        { provide: AutomationService, useValue: automationService },
        { provide: ContentService, useValue: contentService },
        { provide: DashboardService, useValue: dashboardService }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(DashboardPageComponent);
    router = TestBed.inject(Router);
    spyOn(router, 'navigate').and.resolveTo(true);
    fixture.detectChanges();
  });

  it('links priority event rows to event detail', () => {
    const row = fixture.debugElement.query(By.css('tr.event-row'));
    const link = row.injector.get(RouterLink);

    expect(link.urlTree).not.toBeNull();
    expect(router.serializeUrl(link.urlTree!)).toBe('/events/7');
  });

  it('renders redesigned metric cards', () => {
    const compiled = fixture.nativeElement as HTMLElement;

    expect(compiled.textContent).toContain('Noticias');
    expect(compiled.textContent).toContain('Capturadas hoy');
    expect(compiled.textContent).toContain('Diferencia vs anterior');
    expect(compiled.textContent).toContain('Total acumulado');
  });

  it('notifies analyzed priority events pending content generation', () => {
    const compiled = fixture.nativeElement as HTMLElement;

    expect(compiled.textContent).toContain('Aviso editorial');
    expect(compiled.textContent).toContain('1 eventos importantes analizados pendientes de contenido');
    expect(compiled.textContent).toContain('Generar primer contenido');
  });

  it('orders priority events by impact and related news by default', () => {
    const rows = fixture.debugElement.queryAll(By.css('tr.event-row'));

    expect(rows[0].nativeElement.textContent).toContain('Convocatoria urgente con varias noticias');
    expect(rows[1].nativeElement.textContent).toContain('Convocatoria urgente reciente');
    expect(rows[2].nativeElement.textContent).toContain('Mesa sectorial con muchas noticias');
  });

  it('filters priority events by impact with select options', () => {
    (fixture.componentInstance as any).setImportanceFilter('HIGH');
    fixture.detectChanges();

    const rows = fixture.debugElement.queryAll(By.css('tr.event-row'));
    const impactOptions = (fixture.componentInstance as any).importanceOptions;

    expect(impactOptions).toContain('CRITICAL');
    expect(impactOptions).toContain('HIGH');
    expect(rows.length).toBe(1);
    expect(rows[0].nativeElement.textContent).toContain('Mesa sectorial con muchas noticias');
  });

  it('filters priority events by status with select options', () => {
    (fixture.componentInstance as any).setStatusFilter('MONITORING');
    fixture.detectChanges();

    const rows = fixture.debugElement.queryAll(By.css('tr.event-row'));
    const statusOptions = (fixture.componentInstance as any).statusOptions();

    expect(statusOptions).toContain('OPEN');
    expect(statusOptions).toContain('MONITORING');
    expect(rows.length).toBe(1);
    expect(rows[0].nativeElement.textContent).toContain('Convocatoria urgente reciente');
  });

  it('runs backend automations from dashboard actions', () => {
    const buttons = fixture.debugElement.queryAll(By.css('.automation-actions button'));

    buttons[0].triggerEventHandler('click');
    buttons[1].triggerEventHandler('click');
    buttons[2].triggerEventHandler('click');
    fixture.detectChanges();

    expect(automationService.runClassifications).toHaveBeenCalled();
    expect(automationService.runEventDetection).toHaveBeenCalled();
    expect(automationService.runAnalysis).toHaveBeenCalledWith();
    expect((fixture.nativeElement as HTMLElement).textContent).toContain('Procesados 1. Correctos 1. Fallidos 0. Omitidos 0.');
  });

  it('runs event analysis from a priority event row action', () => {
    const action = fixture.debugElement.query(By.css('.row-action'));

    action.triggerEventHandler('click', { stopPropagation: () => undefined });
    fixture.detectChanges();

    expect(automationService.runAnalysis).toHaveBeenCalledWith(7);
  });

  it('generates content from an analyzed priority event action', () => {
    const rows = fixture.debugElement.queryAll(By.css('tr.event-row'));
    const action = rows[1].query(By.css('.row-action'));

    action.triggerEventHandler('click', { stopPropagation: () => undefined });
    fixture.detectChanges();

    expect(contentService.generateContent).toHaveBeenCalledWith({
      eventId: 8,
      analysisId: null,
      channel: 'TELEGRAM',
      tone: 'INFORMATIVO',
      contentType: 'TELEGRAM_POST',
      length: 'STANDARD'
    });
    expect(router.navigate).toHaveBeenCalledWith(['/content', 30]);
  });
});
