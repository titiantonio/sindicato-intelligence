import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivateFn, Router, UrlTree } from '@angular/router';

import { UserRole } from '../models/auth.models';
import { AuthService } from '../services/auth.service';

export const roleGuard: CanActivateFn = (route: ActivatedRouteSnapshot): boolean | UrlTree => {
  const authService = inject(AuthService);
  const router = inject(Router);
  const roles = (route.data['roles'] as UserRole[] | undefined) ?? [];

  if (roles.length === 0 || authService.hasRole(roles)) {
    return true;
  }

  return router.createUrlTree(['/dashboard']);
};
