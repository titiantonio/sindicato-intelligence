import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MetricCard } from '../../../core/models/dashboard.models';
import { MetricCardComponent } from './metric-card.component';

describe('MetricCardComponent', () => {
  let fixture: ComponentFixture<MetricCardComponent>;
  const card: MetricCard = {
    label: 'Noticias',
    value: '350',
    trend: '+350',
    tone: 'primary',
    todayValue: 350,
    yesterdayValue: 0,
    difference: 350,
    title: 'Noticias',
    subtitle: 'Ultima captura',
    icon: 'news',
    badgeLabel: 'Hoy',
    lastUpdatedAt: '2026-06-13T16:35:00Z',
    items: [
      { label: 'Capturadas hoy', value: 350, tone: 'primary', icon: 'news', signed: false },
      { label: 'Diferencia vs anterior', value: 350, tone: 'success', icon: 'trend', signed: true },
      { label: 'Total acumulado', value: 32585, tone: 'neutral', icon: 'total', signed: false }
    ]
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MetricCardComponent]
    }).compileComponents();

    fixture = TestBed.createComponent(MetricCardComponent);
    fixture.componentRef.setInput('card', card);
    fixture.detectChanges();
  });

  it('renders the redesigned metric card with signed difference and PrimeIcons', () => {
    const compiled = fixture.nativeElement as HTMLElement;

    expect(compiled.textContent).toContain('Noticias');
    expect(compiled.textContent).toContain('Ultima captura');
    expect(compiled.textContent).toContain('Hoy');
    expect(compiled.textContent).toContain('Capturadas hoy');
    expect(compiled.textContent).toContain('Diferencia vs anterior');
    expect(compiled.textContent).toContain('Total acumulado');
    expect(compiled.textContent).toContain('Ultima actualizacion:');
    expect(compiled.textContent).toContain('+350');
    expect(compiled.textContent).toContain('32.585');
    expect(compiled.querySelector('.metric-card__header > .metric-card__badge')?.textContent?.trim()).toBe('Hoy');
    expect(compiled.querySelector('.metric-card__summary')?.textContent).toContain('350');
    expect(compiled.querySelector('.metric-card__trend')?.textContent).toContain('+350');
    expect(compiled.querySelectorAll('.metric-card__item').length).toBe(3);
    expect(compiled.querySelectorAll('.pi').length).toBeGreaterThan(0);
  });

  it('keeps long values inside the metric card structure', () => {
    fixture.componentRef.setInput('card', {
      ...card,
      value: '1.234.567.890 ms',
      trend: '+987.654',
      items: [
        { label: 'Modelo conservador con fallback', value: 1234567890, tone: 'warning', icon: 'trend', signed: false },
        { label: 'Diferencia media acumulada', value: 987654, tone: 'danger', icon: 'alert', signed: true }
      ]
    });
    fixture.detectChanges();

    const compiled = fixture.nativeElement as HTMLElement;

    expect(compiled.querySelector('.metric-card__summary')?.textContent).toContain('1.234.567.890 ms');
    expect(compiled.querySelector('.metric-card__item-value')?.textContent).toContain('1.234.567.890');
    expect(compiled.querySelector('.metric-card__item-label')?.textContent).toContain('Modelo conservador con fallback');
  });
});
