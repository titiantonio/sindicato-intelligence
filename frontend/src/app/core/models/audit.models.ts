export interface UserAuditLogItem {
  id: number;
  userId: number | null;
  userDisplayName: string | null;
  actorEmail: string | null;
  action: string;
  details: string | null;
  createdAt: string;
}

export interface EditorialAuditLogItem {
  id: number;
  userId: number | null;
  userDisplayName: string | null;
  action: string;
  entityType: string;
  entityId: number | null;
  oldValues: string | null;
  newValues: string | null;
  createdAt: string;
}
