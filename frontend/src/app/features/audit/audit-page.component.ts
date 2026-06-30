import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ButtonModule } from 'primeng/button';
import { DialogModule } from 'primeng/dialog';
import { InputTextModule } from 'primeng/inputtext';
import { MessageModule } from 'primeng/message';

import { EditorialAuditLogItem, UserAuditLogItem } from '../../core/models/audit.models';
import { AuditService } from '../../core/services/audit.service';
import { StandardTableComponent } from '../../shared/components/standard-table/standard-table.component';

type AuditTab = 'users' | 'editorial';
type SortDirection = 'asc' | 'desc';
type UserAuditSortColumn = 'createdAt' | 'action' | 'userId' | 'actorEmail' | 'details';
type EditorialAuditSortColumn = 'createdAt' | 'action' | 'entityType' | 'entityId' | 'userId' | 'newValues';
type AuditDetailSelection = {
  title: string;
  category: string;
  action: string;
  user: string;
  createdAt: string;
  entity: string;
  detail: string;
  error: boolean;
};

@Component({
  selector: 'app-audit-page',
  imports: [ButtonModule, DialogModule, FormsModule, InputTextModule, MessageModule, StandardTableComponent],
  templateUrl: './audit-page.component.html',
  styleUrl: './audit-page.component.scss'
})
export class AuditPageComponent implements OnInit {
  private readonly auditService = inject(AuditService);

  protected readonly activeTab = signal<AuditTab>('users');
  protected readonly auditDate = signal(this.todayInputValue());
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
  protected readonly selectedAuditDetail = signal<AuditDetailSelection | null>(null);
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

    this.auditService.listUserAudit(100, this.auditDate()).subscribe({
      next: (userAudit) => {
        this.userAudit.set(userAudit);
        this.userCurrentPage.set(Math.min(this.userCurrentPage(), this.userTotalPages()));
        this.auditService.listEditorialAudit(100, this.auditDate()).subscribe({
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

  protected formatUserAuditDetail(entry: UserAuditLogItem): string {
    return this.formatDetailDates(this.formatAuditDetail(entry.action, entry.details));
  }

  protected formatEditorialAuditDetail(entry: EditorialAuditLogItem): string {
    return this.formatDetailDates(this.formatAuditDetail(entry.action, entry.newValues));
  }

  protected userLabel(entry: UserAuditLogItem | EditorialAuditLogItem): string {
    return entry.userDisplayName ?? this.formatNullableNumber(entry.userId);
  }

  protected openUserAuditDetail(event: Event, entry: UserAuditLogItem): void {
    event.stopPropagation();
    this.selectedAuditDetail.set({
      title: 'Detalle de auditoria de usuario',
      category: 'Auditoria de usuario',
      action: entry.action,
      user: this.userLabel(entry),
      createdAt: this.formatDate(entry.createdAt),
      entity: 'Usuario ' + this.userLabel(entry),
      detail: this.formatUserAuditDetail(entry),
      error: this.isUserAuditError(entry)
    });
  }

  protected openEditorialAuditDetail(event: Event, entry: EditorialAuditLogItem): void {
    event.stopPropagation();
    this.selectedAuditDetail.set({
      title: this.isEditorialAuditError(entry) ? 'Detalle del error' : 'Detalle de auditoria editorial',
      category: 'Auditoria editorial',
      action: entry.action,
      user: this.userLabel(entry),
      createdAt: this.formatDate(entry.createdAt),
      entity: `${entry.entityType} #${entry.entityId ?? '-'}`,
      detail: this.formatEditorialAuditDetail(entry),
      error: this.isEditorialAuditError(entry)
    });
  }

  protected closeAuditDetail(): void {
    this.selectedAuditDetail.set(null);
  }

  protected setAuditDate(value: string): void {
    this.auditDate.set(value || this.todayInputValue());
    this.userCurrentPage.set(1);
    this.editorialCurrentPage.set(1);
    this.loadAudit();
  }

  protected isUserAuditError(entry: UserAuditLogItem): boolean {
    return this.isErrorAction(entry.action) || this.hasErrorText(this.formatUserAuditDetail(entry));
  }

  protected isEditorialAuditError(entry: EditorialAuditLogItem): boolean {
    return this.isErrorAction(entry.action) || this.hasErrorText(this.formatEditorialAuditDetail(entry));
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
      .filter((entry) => this.matchesText(this.userLabel(entry), this.userIdFilter()))
      .filter((entry) => this.matchesText(entry.actorEmail ?? '-', this.userActorFilter()))
      .filter((entry) => this.matchesText(this.formatUserAuditDetail(entry), this.userDetailsFilter()));
  }

  private filterEditorialAudit(entries: EditorialAuditLogItem[]): EditorialAuditLogItem[] {
    return entries
      .filter((entry) => this.matchesText(this.formatDate(entry.createdAt), this.editorialDateFilter()))
      .filter((entry) => this.matchesText(entry.action, this.editorialActionFilter()))
      .filter((entry) => this.matchesText(entry.entityType, this.editorialEntityTypeFilter()))
      .filter((entry) => this.matchesText(this.formatNullableNumber(entry.entityId), this.editorialEntityIdFilter()))
      .filter((entry) => this.matchesText(this.userLabel(entry), this.editorialUserIdFilter()))
      .filter((entry) => this.matchesText(this.formatEditorialAuditDetail(entry), this.editorialChangesFilter()));
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
      return this.userLabel(left).localeCompare(this.userLabel(right), 'es', { sensitivity: 'base' });
    }
    return this.userAuditValue(left, column).localeCompare(this.userAuditValue(right, column), 'es', { sensitivity: 'base' });
  }

  private compareEditorialAudit(left: EditorialAuditLogItem, right: EditorialAuditLogItem, column: EditorialAuditSortColumn): number {
    if (column === 'createdAt') {
      return new Date(left.createdAt).getTime() - new Date(right.createdAt).getTime();
    }
    if (column === 'entityId' || column === 'userId') {
      if (column === 'userId') {
        return this.userLabel(left).localeCompare(this.userLabel(right), 'es', { sensitivity: 'base' });
      }
      return (left[column] ?? 0) - (right[column] ?? 0);
    }
    return this.editorialAuditValue(left, column).localeCompare(this.editorialAuditValue(right, column), 'es', { sensitivity: 'base' });
  }

  private userAuditValue(entry: UserAuditLogItem, column: Exclude<UserAuditSortColumn, 'createdAt' | 'userId'>): string {
    const values = {
      action: entry.action,
      actorEmail: entry.actorEmail ?? '-',
      details: this.formatUserAuditDetail(entry)
    };
    return values[column];
  }

  private editorialAuditValue(entry: EditorialAuditLogItem, column: Exclude<EditorialAuditSortColumn, 'createdAt' | 'entityId' | 'userId'>): string {
    const values = {
      action: entry.action,
      entityType: entry.entityType,
      newValues: this.formatEditorialAuditDetail(entry)
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

  private formatAuditDetail(action: string, value: string | null): string {
    const text = this.formatValue(value);
    if (text === '-') {
      return '-';
    }

    if (this.looksLikeJson(text)) {
      return this.formatJsonAuditDetail(action, text);
    }

    if (this.looksLikeKeyValues(text)) {
      return this.formatKeyValueAuditDetail(action, text);
    }

    return text;
  }

  private formatJsonAuditDetail(action: string, value: string): string {
    try {
      const parsed = JSON.parse(value) as Record<string, unknown>;
      if (action === 'PUBLICATION_SCHEDULED') {
        return `Publicacion programada para contenido #${this.valueOrDash(parsed['contentId'])}. Fecha programada: ${this.valueOrDash(parsed['scheduledAt'])}.`;
      }
      if (action === 'EVENT_MERGED') {
        return `Evento #${this.valueOrDash(parsed['targetEventId'])} fusionado. Noticias asociadas tras la fusion: ${this.valueOrDash(parsed['newsCount'])}.`;
      }
      if (action === 'CONTENT_EDITED') {
        return `Contenido editado. Titulo: "${this.valueOrDash(parsed['title'])}". Tono: ${this.valueOrDash(parsed['tone'])}. Estado: ${this.valueOrDash(parsed['status'])}.`;
      }
      if (action === 'PUBLICATION_PUBLISHED') {
        return `Publicacion #${this.valueOrDash(parsed['publicationId'])} completada correctamente para contenido #${this.valueOrDash(parsed['contentId'])}. Estado: ${this.valueOrDash(parsed['status'])}.`;
      }
      if (action === 'PUBLICATION_FAILED') {
        const scheduledAt = parsed['scheduledAt'] ? ` Fecha programada: ${this.valueOrDash(parsed['scheduledAt'])}.` : '';
        return `Publicacion #${this.valueOrDash(parsed['publicationId'])} fallida para contenido #${this.valueOrDash(parsed['contentId'])}.${scheduledAt} Motivo: ${this.valueOrDash(parsed['error'] ?? parsed['description'])}.`;
      }
    } catch {
      return value;
    }

    return value;
  }

  private formatKeyValueAuditDetail(action: string, value: string): string {
    const keyValues = this.parseKeyValues(value);
    if (action === 'USER_CREATED' && keyValues['role']) {
      return `Usuario creado con rol ${keyValues['role']} y pendiente de activacion.`;
    }
    if (['USER_ACTIVATED', 'USER_DEACTIVATED', 'USER_LOCKED', 'USER_UNLOCKED'].includes(action) && keyValues['status']) {
      return `Estado de usuario actualizado a ${keyValues['status']}.`;
    }
    if (action === 'USER_ROLE_CHANGED' && keyValues['from'] && keyValues['to']) {
      return `Rol de usuario actualizado de ${keyValues['from']} a ${keyValues['to']}.`;
    }
    if (action === 'TEMPORARY_PASSWORD_RESET' && keyValues['temporaryPasswordExpiresAt']) {
      return `Password temporal regenerada. Caduca el ${keyValues['temporaryPasswordExpiresAt']}.`;
    }
    if (action === 'PASSWORD_CHANGED' && keyValues['passwordChangedAt']) {
      return `Password actualizada correctamente el ${keyValues['passwordChangedAt']}.`;
    }
    if (action === 'LOGIN' && keyValues['loginAt']) {
      return `Login completado correctamente el ${keyValues['loginAt']}.`;
    }

    return value;
  }

  private looksLikeJson(value: string): boolean {
    const trimmed = value.trim();
    return trimmed.startsWith('{') && trimmed.endsWith('}');
  }

  private looksLikeKeyValues(value: string): boolean {
    return /^[A-Za-z][A-Za-z0-9]*=/.test(value.trim());
  }

  private parseKeyValues(value: string): Record<string, string> {
    return value.split(',').reduce<Record<string, string>>((result, part) => {
      const [key, ...rawValue] = part.split('=');
      if (key?.trim() && rawValue.length > 0) {
        result[key.trim()] = rawValue.join('=').trim();
      }
      return result;
    }, {});
  }

  private valueOrDash(value: unknown): string {
    if (value === null || value === undefined || value === '') {
      return '-';
    }
    return String(value);
  }

  private formatDetailDates(value: string): string {
    return value.replace(/\d{4}-\d{2}-\d{2}T\d{2}:\d{2}(?::\d{2}(?:\.\d+)?)?(?:Z|[+-]\d{2}:\d{2})?/g, (match) => this.formatDate(match));
  }

  private isErrorAction(action: string): boolean {
    return action.toLocaleUpperCase('es').includes('FAILED') || action.toLocaleUpperCase('es').includes('ERROR');
  }

  private hasErrorText(value: string): boolean {
    const normalized = value.toLocaleLowerCase('es');
    return normalized.includes('fallida') || normalized.includes('fallido') || normalized.includes('fallo') || normalized.includes('error');
  }

  private todayInputValue(): string {
    const now = new Date();
    const offset = now.getTimezoneOffset() * 60000;
    return new Date(now.getTime() - offset).toISOString().slice(0, 10);
  }
}
