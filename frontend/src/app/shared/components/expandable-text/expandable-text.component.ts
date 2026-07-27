import { DOCUMENT } from '@angular/common';
import {
  AfterViewInit,
  Component,
  ElementRef,
  OnChanges,
  OnDestroy,
  SimpleChanges,
  ViewChild,
  inject,
  input,
  signal
} from '@angular/core';

@Component({
  selector: 'app-expandable-text',
  templateUrl: './expandable-text.component.html',
  styleUrl: './expandable-text.component.scss'
})
export class ExpandableTextComponent implements AfterViewInit, OnChanges, OnDestroy {
  private static nextId = 0;

  private readonly document = inject(DOCUMENT);

  @ViewChild('contentElement')
  private contentElement?: ElementRef<HTMLElement>;

  readonly text = input.required<string>();
  readonly maxLines = input(3);
  readonly label = input('contenido');

  protected readonly expanded = signal(false);
  protected readonly hasOverflow = signal(false);
  protected readonly contentId = `expandable-text-${++ExpandableTextComponent.nextId}`;

  private measurementFrame: number | null = null;
  private resizeObserver?: ResizeObserver;

  ngAfterViewInit(): void {
    const ResizeObserverType = this.document.defaultView?.ResizeObserver;

    if (ResizeObserverType && this.contentElement) {
      this.resizeObserver = new ResizeObserverType(() => this.scheduleMeasurement());
      this.resizeObserver.observe(this.contentElement.nativeElement);
    }

    this.scheduleMeasurement();
  }

  ngOnChanges(_changes: SimpleChanges): void {
    this.expanded.set(false);
    this.scheduleMeasurement();
  }

  ngOnDestroy(): void {
    this.resizeObserver?.disconnect();

    if (this.measurementFrame !== null) {
      this.document.defaultView?.cancelAnimationFrame(this.measurementFrame);
    }
  }

  protected toggle(): void {
    this.expanded.update((expanded) => !expanded);

    if (!this.expanded()) {
      this.scheduleMeasurement();
    }
  }

  private scheduleMeasurement(): void {
    const view = this.document.defaultView;

    if (!view || !this.contentElement || this.expanded()) {
      return;
    }

    if (this.measurementFrame !== null) {
      view.cancelAnimationFrame(this.measurementFrame);
    }

    this.measurementFrame = view.requestAnimationFrame(() => {
      this.measurementFrame = null;
      this.measureOverflow();
    });
  }

  private measureOverflow(): void {
    const element = this.contentElement?.nativeElement;

    if (!element || this.expanded()) {
      return;
    }

    this.hasOverflow.set(element.scrollHeight > element.clientHeight + 1);
  }
}
