import { Routes } from '@angular/router';

import { authGuard } from './core/guards/auth.guard';
import { passwordChangeGuard } from './core/guards/password-change.guard';
import { roleGuard } from './core/guards/role.guard';
import { ChangePasswordPageComponent } from './features/auth/change-password/change-password-page.component';
import { ContentPageComponent } from './features/content/content-page.component';
import { DashboardPageComponent } from './features/dashboard/dashboard-page.component';
import { EventsPageComponent } from './features/events/events-page.component';
import { ForgotPasswordPageComponent } from './features/auth/forgot-password/forgot-password-page.component';
import { LoginPageComponent } from './features/auth/login/login-page.component';
import { PublicationsPageComponent } from './features/publications/publications-page.component';
import { ResetPasswordPageComponent } from './features/auth/reset-password/reset-password-page.component';
import { SourcesPageComponent } from './features/sources/sources-page.component';
import { UsersPageComponent } from './features/users/users-page.component';
import { ShellComponent } from './layout/shell/shell.component';

export const routes: Routes = [
  {
    path: 'login',
    component: LoginPageComponent
  },
  {
    path: 'forgot-password',
    component: ForgotPasswordPageComponent
  },
  {
    path: 'reset-password',
    component: ResetPasswordPageComponent
  },
  {
    path: 'change-password',
    component: ChangePasswordPageComponent,
    canActivate: [authGuard]
  },
  {
    path: '',
    component: ShellComponent,
    canActivate: [authGuard, passwordChangeGuard],
    children: [
      {
        path: '',
        pathMatch: 'full',
        redirectTo: 'dashboard'
      },
      {
        path: 'dashboard',
        component: DashboardPageComponent
      },
      {
        path: 'events',
        component: EventsPageComponent
      },
      {
        path: 'content',
        component: ContentPageComponent
      },
      {
        path: 'publications',
        component: PublicationsPageComponent
      },
      {
        path: 'sources',
        component: SourcesPageComponent,
        canActivate: [roleGuard],
        data: {
          roles: ['ADMIN']
        }
      },
      {
        path: 'users',
        component: UsersPageComponent,
        canActivate: [roleGuard],
        data: {
          roles: ['ADMIN']
        }
      }
    ]
  },
  {
    path: '**',
    redirectTo: ''
  }
];
