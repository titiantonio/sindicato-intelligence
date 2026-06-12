export type UserRole = 'ADMIN' | 'EDITOR';

export interface AuthUser {
  id: number;
  name: string;
  role: UserRole;
  mustChangePassword: boolean;
}

export interface LoginRequest {
  email: string;
  password: string;
}

export interface ForgotPasswordRequest {
  email: string;
}

export interface RequestTemporaryPasswordRequest {
  email: string;
}

export interface ResetPasswordRequest {
  token: string;
  newPassword: string;
}

export interface ChangePasswordRequest {
  currentPassword: string;
  newPassword: string;
}

export interface MessageResponse {
  message: string;
}

export interface LoginResponse {
  accessToken: string;
  refreshToken: string;
  user: AuthUser;
}

export interface AuthSession {
  accessToken: string;
  refreshToken: string;
  user: AuthUser;
}
