import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { Router, RouterLink, provideRouter } from '@angular/router';
import { of } from 'rxjs';

import { DashboardService } from '../../core/services/dashboard.service';
import { DashboardPageComponent } from './dashboard-page.component';

describe('DashboardPageComponent', () => {
  let fixture: ComponentFixture<DashboardPageComponent>;
  let dashboardService: jasmine.SpyObj<DashboardService>;
  let router: Router;

  beforeEach(async () => {
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
          status: 'OPEN'
        },
        {
          id: 8,
          title: 'Convocatoria urgente reciente',
          category: 'SIPRI',
          importance: 'CRITICAL',
          relatedNews: 1,
          updatedAt: '2026-06-13T10:00:00Z',
          status: 'MONITORING'
        },
        {
          id: 9,
          title: 'Mesa sectorial con muchas noticias',
          category: 'SINDICAL',
          importance: 'HIGH',
          relatedNews: 7,
          updatedAt: '2026-06-13T11:00:00Z',
          status: 'OPEN'
        }
      ]
    }));

    await TestBed.configureTestingModule({
      imports: [DashboardPageComponent],
      providers: [provideRouter([]), { provide: DashboardService, useValue: dashboardService }]
    }).compileComponents();

    fixture = TestBed.createComponent(DashboardPageComponent);
    router = TestBed.inject(Router);
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
    const impactSelect = fixture.debugElement.query(By.css('select[aria-label="Filtrar eventos prioritarios por impacto"]')).nativeElement as HTMLSelectElement;

    expect(impactSelect.textContent).toContain('CRITICAL');
    expect(impactSelect.textContent).toContain('HIGH');
    expect(rows.length).toBe(1);
    expect(rows[0].nativeElement.textContent).toContain('Mesa sectorial con muchas noticias');
  });

  it('filters priority events by status with select options', () => {
    (fixture.componentInstance as any).setStatusFilter('MONITORING');
    fixture.detectChanges();

    const rows = fixture.debugElement.queryAll(By.css('tr.event-row'));
    const statusSelect = fixture.debugElement.query(By.css('select[aria-label="Filtrar eventos prioritarios por estado"]')).nativeElement as HTMLSelectElement;

    expect(statusSelect.textContent).toContain('OPEN');
    expect(statusSelect.textContent).toContain('MONITORING');
    expect(rows.length).toBe(1);
    expect(rows[0].nativeElement.textContent).toContain('Convocatoria urgente reciente');
  });
});
