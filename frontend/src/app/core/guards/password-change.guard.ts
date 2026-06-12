import { inject } from '@angular/core';
import { CanActivateFn, Router, UrlTree } from '@angular/router';

import { AuthService } from '../services/auth.service';

export const passwordChangeGuard: CanActivateFn = (): boolean | UrlTree => {
  const authService = inject(AuthService);
  const router = inject(Router);
  const user = authService.currentUser();

  if (user?.mustChangePassword === true) {
    return router.createUrlTree(['/change-password']);
  }

  return true;
};
