import { UserRole } from './auth.models';

export type UserStatus = 'PENDING_ACTIVATION' | 'ACTIVE' | 'INACTIVE' | 'LOCKED';

export interface UserAdminResponse {
  id: number;
  email: string;
  name: string;
  role: UserRole;
  active: boolean;
  mustChangePassword: boolean;
  status: UserStatus;
  temporaryPasswordExpiresAt: string | null;
  lastLoginAt: string | null;
  lastPasswordChangeAt: string | null;
}

export interface CreateUserAdminRequest {
  email: string;
  name: string;
  role: UserRole;
}

export interface UpdateUserAdminRequest {
  name: string;
  role: UserRole;
}
