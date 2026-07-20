import { TestBed } from '@angular/core/testing';

import { ThemeService } from './theme.service';

describe('ThemeService', () => {
  let service: ThemeService;

  beforeEach(() => {
    localStorage.clear();
    document.documentElement.removeAttribute('data-theme');

    TestBed.configureTestingModule({});
    service = TestBed.inject(ThemeService);
  });

  afterEach(() => {
    localStorage.clear();
    document.documentElement.removeAttribute('data-theme');
  });

  it('applies the initial theme to the document', () => {
    expect(['light', 'dark']).toContain(document.documentElement.dataset['theme'] ?? '');
    expect(document.body.dataset['theme']).toBe(document.documentElement.dataset['theme']);
    expect(document.documentElement.classList.contains(`theme-${service.theme()}`)).toBeTrue();
    expect(document.body.classList.contains(`theme-${service.theme()}`)).toBeTrue();
  });

  it('toggles and persists the selected theme', () => {
    const initialTheme = service.theme();

    service.toggleTheme();

    expect(service.theme()).not.toBe(initialTheme);
    expect(document.documentElement.dataset['theme']).toBe(service.theme());
    expect(document.body.dataset['theme']).toBe(service.theme());
    expect(document.documentElement.classList.contains(`theme-${service.theme()}`)).toBeTrue();
    expect(document.body.classList.contains(`theme-${service.theme()}`)).toBeTrue();
    expect(localStorage.getItem('sindicato-theme')).toBe(JSON.stringify(service.theme()));
  });
});
