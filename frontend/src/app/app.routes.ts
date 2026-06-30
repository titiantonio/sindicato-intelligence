import { Routes } from '@angular/router';

import { authGuard } from './core/guards/auth.guard';
import { passwordChangeGuard } from './core/guards/password-change.guard';
import { roleGuard } from './core/guards/role.guard';

export const routes: Routes = [
  {
    path: 'login',
    loadComponent: () =>
      import('./features/auth/login/login-page.component').then((module) => module.LoginPageComponent)
  },
  {
    path: 'forgot-password',
    loadComponent: () =>
      import('./features/auth/forgot-password/forgot-password-page.component').then(
        (module) => module.ForgotPasswordPageComponent
      )
  },
  {
    path: 'reset-password',
    loadComponent: () =>
      import('./features/auth/reset-password/reset-password-page.component').then(
        (module) => module.ResetPasswordPageComponent
      )
  },
  {
    path: 'change-password',
    loadComponent: () =>
      import('./features/auth/change-password/change-password-page.component').then(
        (module) => module.ChangePasswordPageComponent
      ),
    canActivate: [authGuard]
  },
  {
    path: '',
    loadComponent: () =>
      import('./layout/shell/shell.component').then((module) => module.ShellComponent),
    canActivate: [authGuard, passwordChangeGuard],
    children: [
      {
        path: '',
        pathMatch: 'full',
        redirectTo: 'dashboard'
      },
      {
        path: 'dashboard',
        loadComponent: () =>
          import('./features/dashboard/dashboard-page.component').then(
            (module) => module.DashboardPageComponent
          )
      },
      {
        path: 'events/:id',
        loadComponent: () =>
          import('./features/events/event-detail-page.component').then(
            (module) => module.EventDetailPageComponent
          )
      },
      {
        path: 'events',
        loadComponent: () =>
          import('./features/events/events-page.component').then((module) => module.EventsPageComponent)
      },
      {
        path: 'content',
        loadComponent: () =>
          import('./features/content/content-page.component').then((module) => module.ContentPageComponent)
      },
      {
        path: 'content/:id',
        loadComponent: () =>
          import('./features/content/content-detail-page.component').then(
            (module) => module.ContentDetailPageComponent
          )
      },
      {
        path: 'publications',
        loadComponent: () =>
          import('./features/publications/publications-page.component').then(
            (module) => module.PublicationsPageComponent
          )
      },
      {
        path: 'publications/:id',
        loadComponent: () =>
          import('./features/publications/publication-detail-page.component').then(
            (module) => module.PublicationDetailPageComponent
          )
      },
      {
        path: 'news/:id',
        loadComponent: () =>
          import('./features/news/news-detail-page.component').then(
            (module) => module.NewsDetailPageComponent
          )
      },
      {
        path: 'news',
        loadComponent: () =>
          import('./features/news/news-page.component').then((module) => module.NewsPageComponent)
      },
      {
        path: 'sources',
        loadComponent: () =>
          import('./features/sources/sources-page.component').then(
            (module) => module.SourcesPageComponent
          ),
        canActivate: [roleGuard],
        data: {
          roles: ['ADMIN']
        }
      },
      {
        path: 'users',
        loadComponent: () =>
          import('./features/users/users-page.component').then((module) => module.UsersPageComponent),
        canActivate: [roleGuard],
        data: {
          roles: ['ADMIN']
        }
      },
      {
        path: 'audit',
        loadComponent: () =>
          import('./features/audit/audit-page.component').then((module) => module.AuditPageComponent),
        canActivate: [roleGuard],
        data: {
          roles: ['ADMIN']
        }
      },
      {
        path: 'automation-settings',
        redirectTo: 'settings',
        pathMatch: 'full'
      },
      {
        path: 'settings',
        loadComponent: () =>
          import('./features/settings/settings-page.component').then(
            (module) => module.SettingsPageComponent
          ),
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
