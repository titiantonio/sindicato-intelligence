import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of, throwError } from 'rxjs';

import { AuditService } from '../../core/services/audit.service';
import { AuditPageComponent } from './audit-page.component';

describe('AuditPageComponent', () => {
  let fixture: ComponentFixture<AuditPageComponent>;
  let component: AuditPageComponent;
  let auditService: jasmine.SpyObj<AuditService>;

  beforeEach(async () => {
    auditService = jasmine.createSpyObj<AuditService>('AuditService', ['listUserAudit', 'listEditorialAudit']);
    auditService.listUserAudit.and.returnValue(of([
      { id: 1, userId: 7, userDisplayName: 'Editor Prueba <editor@sindicato.es>', actorEmail: 'admin@sindicato.es', action: 'USER_CREATED', details: 'role=EDITOR', createdAt: '2026-06-13T10:00:00Z' }
    ]));
    auditService.listEditorialAudit.and.returnValue(of([
      { id: 2, userId: 1, userDisplayName: 'Admin <admin@sindicato.es>', action: 'PUBLICATION_FAILED', entityType: 'PUBLICATION', entityId: 9, oldValues: null, newValues: '{"publicationId":9,"contentId":333,"status":"FAILED","error":"provider unavailable","scheduledAt":"2026-06-18T22:36Z"}', createdAt: '2026-06-13T10:05:00Z' }
    ]));

    await TestBed.configureTestingModule({
      imports: [AuditPageComponent],
      providers: [{ provide: AuditService, useValue: auditService }]
    }).compileComponents();

    fixture = TestBed.createComponent(AuditPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('loads user and editorial audit on init', () => {
    expect(auditService.listUserAudit).toHaveBeenCalled();
    expect(auditService.listEditorialAudit).toHaveBeenCalled();
    expect((component as any).userAudit().length).toBe(1);
    expect((component as any).editorialAudit().length).toBe(1);
  });

  it('reloads audit when date changes', () => {
    (component as any).setAuditDate('2026-06-18');

    expect(auditService.listUserAudit).toHaveBeenCalledWith(100, '2026-06-18');
    expect(auditService.listEditorialAudit).toHaveBeenCalledWith(100, '2026-06-18');
  });

  it('switches tabs', () => {
    (component as any).setTab('editorial');

    expect((component as any).activeTab()).toBe('editorial');
  });

  it('formats legacy audit details', () => {
    expect((component as any).formatUserAuditDetail((component as any).userAudit()[0])).toContain('Usuario creado con rol EDITOR');

    (component as any).setTab('editorial');
    fixture.detectChanges();

    expect(fixture.nativeElement.textContent).toContain('Admin <admin@sindicato.es>');
    expect(fixture.nativeElement.textContent).toContain('Detalle');
  });

  it('opens detail modal and marks error rows', () => {
    (component as any).setTab('editorial');
    fixture.detectChanges();

    const errorRow = fixture.nativeElement.querySelector('tbody tr');
    expect(errorRow.classList).toContain('audit-row--error');

    const button: HTMLButtonElement = fixture.nativeElement.querySelector('.inline-action');
    button.click();
    fixture.detectChanges();

    expect(fixture.nativeElement.textContent).toContain('Detalle del error');
    expect(fixture.nativeElement.textContent).toContain('provider unavailable');
    expect(fixture.nativeElement.textContent).toContain('19/6/26');
  });

  it('shows load errors', () => {
    auditService.listUserAudit.and.returnValue(throwError(() => ({ error: { error: 'No autorizado' } })));

    (component as any).loadAudit();

    expect((component as any).errorMessage()).toBe('No autorizado');
  });
});
