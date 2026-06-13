import { DOCUMENT } from '@angular/common';
import { Injectable, inject, signal } from '@angular/core';

import { StorageService } from './storage.service';

export type ThemeMode = 'light' | 'dark';

const THEME_STORAGE_KEY = 'sindicato-theme';

@Injectable({
  providedIn: 'root'
})
export class ThemeService {
  private readonly document = inject(DOCUMENT);
  private readonly storageService = inject(StorageService);

  readonly theme = signal<ThemeMode>(this.loadInitialTheme());

  constructor() {
    this.applyTheme(this.theme());
  }

  toggleTheme(): void {
    const nextTheme: ThemeMode = this.theme() === 'dark' ? 'light' : 'dark';

    this.theme.set(nextTheme);
    this.storageService.setItem(THEME_STORAGE_KEY, nextTheme);
    this.applyTheme(nextTheme);
  }

  private loadInitialTheme(): ThemeMode {
    const storedTheme = this.storageService.getItem<ThemeMode>(THEME_STORAGE_KEY);

    if (storedTheme === 'dark' || storedTheme === 'light') {
      return storedTheme;
    }

    return this.document.defaultView?.matchMedia?.('(prefers-color-scheme: dark)').matches
      ? 'dark'
      : 'light';
  }

  private applyTheme(theme: ThemeMode): void {
    this.document.documentElement.dataset['theme'] = theme;
    this.document.documentElement.style.colorScheme = theme;
  }
}
