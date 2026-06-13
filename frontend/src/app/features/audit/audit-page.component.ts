import { Component, OnInit, inject, signal } from '@angular/core';

import { EditorialAuditLogItem, UserAuditLogItem } from '../../core/models/audit.models';
import { AuditService } from '../../core/services/audit.service';

type AuditTab = 'users' | 'editorial';

@Component({
  selector: 'app-audit-page',
  templateUrl: './audit-page.component.html',
  styleUrl: './audit-page.component.scss'
})
export class AuditPageComponent implements OnInit {
  private readonly auditService = inject(AuditService);

  protected readonly activeTab = signal<AuditTab>('users');
  protected readonly userAudit = signal<UserAuditLogItem[]>([]);
  protected readonly editorialAudit = signal<EditorialAuditLogItem[]>([]);
  protected readonly isLoading = signal(false);
  protected readonly errorMessage = signal<string | null>(null);

  ngOnInit(): void {
    this.loadAudit();
  }

  protected setTab(tab: AuditTab): void {
    this.activeTab.set(tab);
  }

  protected loadAudit(): void {
    this.isLoading.set(true);
    this.errorMessage.set(null);

    this.auditService.listUserAudit().subscribe({
      next: (userAudit) => {
        this.userAudit.set(userAudit);
        this.auditService.listEditorialAudit().subscribe({
          next: (editorialAudit) => {
            this.editorialAudit.set(editorialAudit);
            this.isLoading.set(false);
          },
          error: (error: { error?: { error?: string } }) => {
            this.errorMessage.set(error.error?.error ?? 'No se pudo cargar la auditoria editorial.');
            this.isLoading.set(false);
          }
        });
      },
      error: (error: { error?: { error?: string } }) => {
        this.errorMessage.set(error.error?.error ?? 'No se pudo cargar la auditoria de usuarios.');
        this.isLoading.set(false);
      }
    });
  }

  protected formatDate(value: string): string {
    return new Intl.DateTimeFormat('es-ES', {
      dateStyle: 'short',
      timeStyle: 'short'
    }).format(new Date(value));
  }

  protected formatValue(value: string | null): string {
    return value?.trim() ? value : '-';
  }
}
