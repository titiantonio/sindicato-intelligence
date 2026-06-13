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
      { id: 1, userId: 7, actorEmail: 'admin@sindicato.es', action: 'USER_CREATED', details: 'role=EDITOR', createdAt: '2026-06-13T10:00:00Z' }
    ]));
    auditService.listEditorialAudit.and.returnValue(of([
      { id: 2, userId: 1, action: 'EVENT_MERGED', entityType: 'EVENT', entityId: 9, oldValues: null, newValues: '{}', createdAt: '2026-06-13T10:05:00Z' }
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

  it('switches tabs', () => {
    (component as any).setTab('editorial');

    expect((component as any).activeTab()).toBe('editorial');
  });

  it('shows load errors', () => {
    auditService.listUserAudit.and.returnValue(throwError(() => ({ error: { error: 'No autorizado' } })));

    (component as any).loadAudit();

    expect((component as any).errorMessage()).toBe('No autorizado');
  });
});
