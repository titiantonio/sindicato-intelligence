import { TestBed } from '@angular/core/testing';
import { App } from './app';
import { ThemeService } from './core/services/theme.service';

describe('App', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [App],
    }).compileComponents();
  });

  it('should create the app', () => {
    const fixture = TestBed.createComponent(App);
    const app = fixture.componentInstance;
    expect(app).toBeTruthy();
  });

  it('initializes the global theme service at bootstrap', () => {
    const themeService = TestBed.inject(ThemeService);

    TestBed.createComponent(App);

    expect(document.documentElement.dataset['theme']).toBe(themeService.theme());
  });
});
