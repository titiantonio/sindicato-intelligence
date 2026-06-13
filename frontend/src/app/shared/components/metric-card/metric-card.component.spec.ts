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

  it('renders the redesigned metric card with signed difference and icon svg', () => {
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
    expect(compiled.querySelectorAll('svg').length).toBeGreaterThan(0);
  });
});
