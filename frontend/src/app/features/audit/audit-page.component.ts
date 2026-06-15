import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';

import { EditorialAuditLogItem, UserAuditLogItem } from '../../core/models/audit.models';
import { AuditService } from '../../core/services/audit.service';

type AuditTab = 'users' | 'editorial';
type SortDirection = 'asc' | 'desc';
type UserAuditSortColumn = 'createdAt' | 'action' | 'userId' | 'actorEmail' | 'details';
type EditorialAuditSortColumn = 'createdAt' | 'action' | 'entityType' | 'entityId' | 'userId' | 'newValues';

@Component({
  selector: 'app-audit-page',
  imports: [FormsModule],
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
  protected readonly pageSizeOptions = [5, 10, 25, 50];
  protected readonly userPageSize = signal(10);
  protected readonly userCurrentPage = signal(1);
  protected readonly userSortColumn = signal<UserAuditSortColumn>('createdAt');
  protected readonly userSortDirection = signal<SortDirection>('desc');
  protected readonly userDateFilter = signal('');
  protected readonly userActionFilter = signal('');
  protected readonly userIdFilter = signal('');
  protected readonly userActorFilter = signal('');
  protected readonly userDetailsFilter = signal('');
  protected readonly editorialPageSize = signal(10);
  protected readonly editorialCurrentPage = signal(1);
  protected readonly editorialSortColumn = signal<EditorialAuditSortColumn>('createdAt');
  protected readonly editorialSortDirection = signal<SortDirection>('desc');
  protected readonly editorialDateFilter = signal('');
  protected readonly editorialActionFilter = signal('');
  protected readonly editorialEntityTypeFilter = signal('');
  protected readonly editorialEntityIdFilter = signal('');
  protected readonly editorialUserIdFilter = signal('');
  protected readonly editorialChangesFilter = signal('');
  protected readonly displayedUserAudit = computed(() => this.sortUserAudit(this.filterUserAudit(this.userAudit())));
  protected readonly displayedEditorialAudit = computed(() => this.sortEditorialAudit(this.filterEditorialAudit(this.editorialAudit())));
  protected readonly userTotalPages = computed(() => Math.max(1, Math.ceil(this.displayedUserAudit().length / this.userPageSize())));
  protected readonly editorialTotalPages = computed(() => Math.max(1, Math.ceil(this.displayedEditorialAudit().length / this.editorialPageSize())));
  protected readonly paginatedUserAudit = computed(() => {
    const page = Math.min(this.userCurrentPage(), this.userTotalPages());
    const start = (page - 1) * this.userPageSize();
    return this.displayedUserAudit().slice(start, start + this.userPageSize());
  });
  protected readonly paginatedEditorialAudit = computed(() => {
    const page = Math.min(this.editorialCurrentPage(), this.editorialTotalPages());
    const start = (page - 1) * this.editorialPageSize();
    return this.displayedEditorialAudit().slice(start, start + this.editorialPageSize());
  });

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
        this.userCurrentPage.set(Math.min(this.userCurrentPage(), this.userTotalPages()));
        this.auditService.listEditorialAudit().subscribe({
          next: (editorialAudit) => {
            this.editorialAudit.set(editorialAudit);
            this.editorialCurrentPage.set(Math.min(this.editorialCurrentPage(), this.editorialTotalPages()));
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

  protected setUserSort(column: UserAuditSortColumn): void {
    if (this.userSortColumn() === column) {
      this.userSortDirection.update((direction) => direction === 'asc' ? 'desc' : 'asc');
      return;
    }
    this.userSortColumn.set(column);
    this.userSortDirection.set(column === 'createdAt' ? 'desc' : 'asc');
  }

  protected setEditorialSort(column: EditorialAuditSortColumn): void {
    if (this.editorialSortColumn() === column) {
      this.editorialSortDirection.update((direction) => direction === 'asc' ? 'desc' : 'asc');
      return;
    }
    this.editorialSortColumn.set(column);
    this.editorialSortDirection.set(column === 'createdAt' ? 'desc' : 'asc');
  }

  protected userSortLabel(column: UserAuditSortColumn): string {
    return this.userSortColumn() === column ? this.userSortDirection().toUpperCase() : '';
  }

  protected editorialSortLabel(column: EditorialAuditSortColumn): string {
    return this.editorialSortColumn() === column ? this.editorialSortDirection().toUpperCase() : '';
  }

  protected setUserDateFilter(value: string): void { this.userDateFilter.set(value); this.userCurrentPage.set(1); }
  protected setUserActionFilter(value: string): void { this.userActionFilter.set(value); this.userCurrentPage.set(1); }
  protected setUserIdFilter(value: string): void { this.userIdFilter.set(value); this.userCurrentPage.set(1); }
  protected setUserActorFilter(value: string): void { this.userActorFilter.set(value); this.userCurrentPage.set(1); }
  protected setUserDetailsFilter(value: string): void { this.userDetailsFilter.set(value); this.userCurrentPage.set(1); }
  protected setEditorialDateFilter(value: string): void { this.editorialDateFilter.set(value); this.editorialCurrentPage.set(1); }
  protected setEditorialActionFilter(value: string): void { this.editorialActionFilter.set(value); this.editorialCurrentPage.set(1); }
  protected setEditorialEntityTypeFilter(value: string): void { this.editorialEntityTypeFilter.set(value); this.editorialCurrentPage.set(1); }
  protected setEditorialEntityIdFilter(value: string): void { this.editorialEntityIdFilter.set(value); this.editorialCurrentPage.set(1); }
  protected setEditorialUserIdFilter(value: string): void { this.editorialUserIdFilter.set(value); this.editorialCurrentPage.set(1); }
  protected setEditorialChangesFilter(value: string): void { this.editorialChangesFilter.set(value); this.editorialCurrentPage.set(1); }

  protected setUserPageSize(value: string): void { this.userPageSize.set(Number(value)); this.userCurrentPage.set(1); }
  protected previousUserPage(): void { this.userCurrentPage.update((page) => Math.max(1, page - 1)); }
  protected nextUserPage(): void { this.userCurrentPage.update((page) => Math.min(this.userTotalPages(), page + 1)); }
  protected setEditorialPageSize(value: string): void { this.editorialPageSize.set(Number(value)); this.editorialCurrentPage.set(1); }
  protected previousEditorialPage(): void { this.editorialCurrentPage.update((page) => Math.max(1, page - 1)); }
  protected nextEditorialPage(): void { this.editorialCurrentPage.update((page) => Math.min(this.editorialTotalPages(), page + 1)); }

  private filterUserAudit(entries: UserAuditLogItem[]): UserAuditLogItem[] {
    return entries
      .filter((entry) => this.matchesText(this.formatDate(entry.createdAt), this.userDateFilter()))
      .filter((entry) => this.matchesText(entry.action, this.userActionFilter()))
      .filter((entry) => this.matchesText(this.formatNullableNumber(entry.userId), this.userIdFilter()))
      .filter((entry) => this.matchesText(entry.actorEmail ?? '-', this.userActorFilter()))
      .filter((entry) => this.matchesText(this.formatValue(entry.details), this.userDetailsFilter()));
  }

  private filterEditorialAudit(entries: EditorialAuditLogItem[]): EditorialAuditLogItem[] {
    return entries
      .filter((entry) => this.matchesText(this.formatDate(entry.createdAt), this.editorialDateFilter()))
      .filter((entry) => this.matchesText(entry.action, this.editorialActionFilter()))
      .filter((entry) => this.matchesText(entry.entityType, this.editorialEntityTypeFilter()))
      .filter((entry) => this.matchesText(this.formatNullableNumber(entry.entityId), this.editorialEntityIdFilter()))
      .filter((entry) => this.matchesText(this.formatNullableNumber(entry.userId), this.editorialUserIdFilter()))
      .filter((entry) => this.matchesText(this.formatValue(entry.newValues), this.editorialChangesFilter()));
  }

  private sortUserAudit(entries: UserAuditLogItem[]): UserAuditLogItem[] {
    const direction = this.userSortDirection() === 'asc' ? 1 : -1;
    const column = this.userSortColumn();
    return [...entries].sort((left, right) => direction * this.compareUserAudit(left, right, column));
  }

  private sortEditorialAudit(entries: EditorialAuditLogItem[]): EditorialAuditLogItem[] {
    const direction = this.editorialSortDirection() === 'asc' ? 1 : -1;
    const column = this.editorialSortColumn();
    return [...entries].sort((left, right) => direction * this.compareEditorialAudit(left, right, column));
  }

  private compareUserAudit(left: UserAuditLogItem, right: UserAuditLogItem, column: UserAuditSortColumn): number {
    if (column === 'createdAt') {
      return new Date(left.createdAt).getTime() - new Date(right.createdAt).getTime();
    }
    if (column === 'userId') {
      return (left.userId ?? 0) - (right.userId ?? 0);
    }
    return this.userAuditValue(left, column).localeCompare(this.userAuditValue(right, column), 'es', { sensitivity: 'base' });
  }

  private compareEditorialAudit(left: EditorialAuditLogItem, right: EditorialAuditLogItem, column: EditorialAuditSortColumn): number {
    if (column === 'createdAt') {
      return new Date(left.createdAt).getTime() - new Date(right.createdAt).getTime();
    }
    if (column === 'entityId' || column === 'userId') {
      return (left[column] ?? 0) - (right[column] ?? 0);
    }
    return this.editorialAuditValue(left, column).localeCompare(this.editorialAuditValue(right, column), 'es', { sensitivity: 'base' });
  }

  private userAuditValue(entry: UserAuditLogItem, column: Exclude<UserAuditSortColumn, 'createdAt' | 'userId'>): string {
    const values = {
      action: entry.action,
      actorEmail: entry.actorEmail ?? '-',
      details: this.formatValue(entry.details)
    };
    return values[column];
  }

  private editorialAuditValue(entry: EditorialAuditLogItem, column: Exclude<EditorialAuditSortColumn, 'createdAt' | 'entityId' | 'userId'>): string {
    const values = {
      action: entry.action,
      entityType: entry.entityType,
      newValues: this.formatValue(entry.newValues)
    };
    return values[column];
  }

  private formatNullableNumber(value: number | null): string {
    return value === null ? '-' : value.toString();
  }

  private matchesText(value: string, filter: string): boolean {
    const normalizedFilter = filter.trim().toLocaleLowerCase('es');
    return !normalizedFilter || value.trim().toLocaleLowerCase('es').includes(normalizedFilter);
  }
}
