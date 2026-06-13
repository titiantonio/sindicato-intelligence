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
  });

  it('toggles and persists the selected theme', () => {
    const initialTheme = service.theme();

    service.toggleTheme();

    expect(service.theme()).not.toBe(initialTheme);
    expect(document.documentElement.dataset['theme']).toBe(service.theme());
    expect(localStorage.getItem('sindicato-theme')).toBe(JSON.stringify(service.theme()));
  });
});
