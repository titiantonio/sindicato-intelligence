import { Routes } from '@angular/router';

import { authGuard } from './core/guards/auth.guard';
import { passwordChangeGuard } from './core/guards/password-change.guard';
import { roleGuard } from './core/guards/role.guard';

export const routes: Routes = [
  {
    path: 'login',
    title: 'Acceso | Sindicato Intelligence',
    loadComponent: () =>
      import('./features/auth/login/login-page.component').then((module) => module.LoginPageComponent)
  },
  {
    path: 'forgot-password',
    title: 'Recuperar contraseña | Sindicato Intelligence',
    loadComponent: () =>
      import('./features/auth/forgot-password/forgot-password-page.component').then(
        (module) => module.ForgotPasswordPageComponent
      )
  },
  {
    path: 'reset-password',
    title: 'Restablecer contraseña | Sindicato Intelligence',
    loadComponent: () =>
      import('./features/auth/reset-password/reset-password-page.component').then(
        (module) => module.ResetPasswordPageComponent
      )
  },
  {
    path: 'change-password',
    title: 'Cambiar contraseña | Sindicato Intelligence',
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
        title: 'Dashboard | Sindicato Intelligence',
        loadComponent: () =>
          import('./features/dashboard/dashboard-page.component').then(
            (module) => module.DashboardPageComponent
          )
      },
      {
        path: 'events/:id',
        title: 'Detalle de evento | Sindicato Intelligence',
        loadComponent: () =>
          import('./features/events/event-detail-page.component').then(
            (module) => module.EventDetailPageComponent
          )
      },
      {
        path: 'events',
        title: 'Eventos | Sindicato Intelligence',
        loadComponent: () =>
          import('./features/events/events-page.component').then((module) => module.EventsPageComponent)
      },
      {
        path: 'content',
        title: 'Contenido | Sindicato Intelligence',
        loadComponent: () =>
          import('./features/content/content-page.component').then((module) => module.ContentPageComponent)
      },
      {
        path: 'content/:id',
        title: 'Detalle de contenido | Sindicato Intelligence',
        loadComponent: () =>
          import('./features/content/content-detail-page.component').then(
            (module) => module.ContentDetailPageComponent
          )
      },
      {
        path: 'publications',
        title: 'Publicaciones | Sindicato Intelligence',
        loadComponent: () =>
          import('./features/publications/publications-page.component').then(
            (module) => module.PublicationsPageComponent
          )
      },
      {
        path: 'publications/:id',
        title: 'Detalle de publicación | Sindicato Intelligence',
        loadComponent: () =>
          import('./features/publications/publication-detail-page.component').then(
            (module) => module.PublicationDetailPageComponent
          )
      },
      {
        path: 'news/:id',
        title: 'Detalle de noticia | Sindicato Intelligence',
        loadComponent: () =>
          import('./features/news/news-detail-page.component').then(
            (module) => module.NewsDetailPageComponent
          )
      },
      {
        path: 'news',
        title: 'Noticias | Sindicato Intelligence',
        loadComponent: () =>
          import('./features/news/news-page.component').then((module) => module.NewsPageComponent)
      },
      {
        path: 'sources',
        title: 'Fuentes | Sindicato Intelligence',
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
        title: 'Usuarios | Sindicato Intelligence',
        loadComponent: () =>
          import('./features/users/users-page.component').then((module) => module.UsersPageComponent),
        canActivate: [roleGuard],
        data: {
          roles: ['ADMIN']
        }
      },
      {
        path: 'audit',
        title: 'Auditoría | Sindicato Intelligence',
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
        title: 'Configuración | Sindicato Intelligence',
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
