import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';

import { ExpandableTextComponent } from './expandable-text.component';

describe('ExpandableTextComponent', () => {
  let fixture: ComponentFixture<ExpandableTextComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ExpandableTextComponent]
    }).compileComponents();

    fixture = TestBed.createComponent(ExpandableTextComponent);
    fixture.componentRef.setInput('text', 'Contenido largo del evento.');
    fixture.componentRef.setInput('maxLines', 3);
    fixture.componentRef.setInput('label', 'descripción del evento #166');
    fixture.detectChanges();
  });

  it('expands and collapses overflowing content accessibly', fakeAsync(() => {
    const paragraph = fixture.nativeElement.querySelector('.expandable-text__content') as HTMLElement;
    Object.defineProperty(paragraph, 'clientHeight', { configurable: true, value: 60 });
    Object.defineProperty(paragraph, 'scrollHeight', { configurable: true, value: 180 });

    (fixture.componentInstance as any).measureOverflow();
    fixture.detectChanges();

    const button = fixture.nativeElement.querySelector('.expandable-text__toggle') as HTMLButtonElement;
    expect(button.textContent).toContain('Mostrar más');
    expect(button.getAttribute('aria-expanded')).toBe('false');
    expect(button.getAttribute('aria-label')).toContain('descripción del evento #166');

    button.click();
    fixture.detectChanges();

    expect(paragraph.classList).not.toContain('expandable-text__content--collapsed');
    expect(button.textContent).toContain('Mostrar menos');
    expect(button.getAttribute('aria-expanded')).toBe('true');

    button.click();
    tick(20);
    fixture.detectChanges();

    expect(paragraph.classList).toContain('expandable-text__content--collapsed');
    expect(button.textContent).toContain('Mostrar más');
  }));

  it('does not render the toggle when the content fits', () => {
    const paragraph = fixture.nativeElement.querySelector('.expandable-text__content') as HTMLElement;
    Object.defineProperty(paragraph, 'clientHeight', { configurable: true, value: 60 });
    Object.defineProperty(paragraph, 'scrollHeight', { configurable: true, value: 60 });

    (fixture.componentInstance as any).measureOverflow();
    fixture.detectChanges();

    expect(fixture.nativeElement.querySelector('.expandable-text__toggle')).toBeNull();
  });
});
