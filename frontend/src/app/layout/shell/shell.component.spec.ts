import { ComponentFixture, TestBed } from '@angular/core/testing';
import { signal } from '@angular/core';
import { provideRouter } from '@angular/router';

import { AuthService } from '../../core/services/auth.service';
import { ThemeService } from '../../core/services/theme.service';
import { ShellComponent } from './shell.component';

describe('ShellComponent', () => {
  let fixture: ComponentFixture<ShellComponent>;
  let authService: jasmine.SpyObj<AuthService>;

  beforeEach(async () => {
    authService = jasmine.createSpyObj<AuthService>('AuthService', ['hasRole', 'logout'], {
      currentUser: signal({
        id: 1,
        name: 'Admin',
        role: 'ADMIN',
        mustChangePassword: false
      })
    });
    authService.hasRole.and.returnValue(true);

    const themeService = {
      theme: signal<'light' | 'dark'>('light'),
      toggleTheme: jasmine.createSpy('toggleTheme')
    };

    await TestBed.configureTestingModule({
      imports: [ShellComponent],
      providers: [
        provideRouter([]),
        { provide: AuthService, useValue: authService },
        { provide: ThemeService, useValue: themeService }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(ShellComponent);
    fixture.detectChanges();
  });

  it('collapses and expands the desktop sidebar', () => {
    const compiled = fixture.nativeElement as HTMLElement;
    const collapseButton = compiled.querySelector<HTMLButtonElement>('.shell__collapse-button');

    expect(compiled.querySelector('.shell--collapsed')).toBeNull();
    expect(compiled.textContent).toContain('Dashboard');

    collapseButton?.click();
    fixture.detectChanges();

    expect(compiled.querySelector('.shell--collapsed')).not.toBeNull();
    expect(compiled.querySelector('.shell__sidebar--collapsed')).not.toBeNull();

    collapseButton?.click();
    fixture.detectChanges();

    expect(compiled.querySelector('.shell--collapsed')).toBeNull();
  });

  it('keeps the mobile drawer toggle available', () => {
    const compiled = fixture.nativeElement as HTMLElement;
    const menuButton = compiled.querySelector<HTMLButtonElement>('.shell__menu-button');

    expect(compiled.querySelector('.shell__sidebar--open')).toBeNull();

    menuButton?.click();
    fixture.detectChanges();

    expect(compiled.querySelector('.shell__sidebar--open')).not.toBeNull();
  });

  it('shows automation settings navigation for admin users', () => {
    const compiled = fixture.nativeElement as HTMLElement;

    expect(compiled.textContent).toContain('Automatizaciones');
  });
});
