import { CommonModule } from '@angular/common';
import { Component, computed, inject, signal } from '@angular/core';
import { RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';

import { UserRole } from '../../core/models/auth.models';
import { AuthService } from '../../core/services/auth.service';
import { ThemeService } from '../../core/services/theme.service';

interface NavigationItem {
  label: string;
  route: string;
  icon: string;
  roles: UserRole[];
}

@Component({
  selector: 'app-shell',
  imports: [CommonModule, RouterLink, RouterLinkActive, RouterOutlet],
  templateUrl: './shell.component.html',
  styleUrl: './shell.component.scss'
})
export class ShellComponent {
  private readonly authService = inject(AuthService);
  protected readonly themeService = inject(ThemeService);

  protected readonly sidebarOpen = signal(false);
  protected readonly sidebarCollapsed = signal(false);
  protected readonly currentUser = this.authService.currentUser;
  protected readonly navigationItems = computed(() => {
    const items: NavigationItem[] = [
      { label: 'Dashboard', route: '/dashboard', icon: 'pi pi-chart-line', roles: ['ADMIN', 'EDITOR'] },
      { label: 'Noticias', route: '/news', icon: 'pi pi-file', roles: ['ADMIN', 'EDITOR'] },
      { label: 'Eventos', route: '/events', icon: 'pi pi-sitemap', roles: ['ADMIN', 'EDITOR'] },
      { label: 'Contenido', route: '/content', icon: 'pi pi-pen-to-square', roles: ['ADMIN', 'EDITOR'] },
      { label: 'Publicaciones', route: '/publications', icon: 'pi pi-send', roles: ['ADMIN', 'EDITOR'] },
      { label: 'Configuracion', route: '/settings', icon: 'pi pi-cog', roles: ['ADMIN'] },
      { label: 'Fuentes', route: '/sources', icon: 'pi pi-rss', roles: ['ADMIN'] },
      { label: 'Usuarios', route: '/users', icon: 'pi pi-users', roles: ['ADMIN'] },
      { label: 'Auditoria', route: '/audit', icon: 'pi pi-shield', roles: ['ADMIN'] }
    ];

    return items.filter((item) => this.authService.hasRole(item.roles));
  });

  protected toggleSidebar(): void {
    this.sidebarOpen.update((value) => !value);
  }

  protected closeSidebar(): void {
    this.sidebarOpen.set(false);
  }

  protected toggleSidebarCollapsed(): void {
    this.sidebarCollapsed.update((value) => !value);
  }

  protected logout(): void {
    this.authService.logout();
  }

  protected toggleTheme(): void {
    this.themeService.toggleTheme();
  }
}
