import { Routes } from '@angular/router';

import { authGuard } from './core/guards/auth.guard';
import { roleGuard } from './core/guards/role.guard';
import { ContentPageComponent } from './features/content/content-page.component';
import { DashboardPageComponent } from './features/dashboard/dashboard-page.component';
import { EventsPageComponent } from './features/events/events-page.component';
import { LoginPageComponent } from './features/auth/login/login-page.component';
import { PublicationsPageComponent } from './features/publications/publications-page.component';
import { SourcesPageComponent } from './features/sources/sources-page.component';
import { ShellComponent } from './layout/shell/shell.component';

export const routes: Routes = [
  {
    path: 'login',
    component: LoginPageComponent
  },
  {
    path: '',
    component: ShellComponent,
    canActivate: [authGuard],
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
      }
    ]
  },
  {
    path: '**',
    redirectTo: ''
  }
];
