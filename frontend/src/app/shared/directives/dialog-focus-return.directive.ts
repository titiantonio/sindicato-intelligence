import { DOCUMENT } from '@angular/common';
import { Directive, Input, OnChanges, OnDestroy, SimpleChanges, inject } from '@angular/core';

/**
 * Conserva el elemento que abrió un diálogo y le devuelve el foco al cerrarlo.
 * PrimeNG gestiona el foco inicial y su contención dentro del diálogo.
 */
@Directive({
  selector: 'p-dialog[appDialogFocusReturn]'
})
export class DialogFocusReturnDirective implements OnChanges, OnDestroy {
  private readonly document = inject(DOCUMENT);
  private trigger: HTMLElement | null = null;

  @Input()
  appDialogFocusReturn = false;

  ngOnChanges(changes: SimpleChanges): void {
    const visibilityChange = changes['appDialogFocusReturn'];
    if (!visibilityChange) {
      return;
    }

    if (visibilityChange.currentValue && !visibilityChange.previousValue) {
      const activeElement = this.document.activeElement;
      this.trigger = activeElement instanceof HTMLElement && activeElement !== this.document.body
        ? activeElement
        : null;
      return;
    }

    if (!visibilityChange.currentValue && visibilityChange.previousValue) {
      this.restoreFocus();
    }
  }

  ngOnDestroy(): void {
    this.restoreFocus();
  }

  private restoreFocus(): void {
    const trigger = this.trigger;
    this.trigger = null;
    this.document.defaultView?.setTimeout(() => {
      if (trigger?.isConnected) {
        trigger.focus();
      }
    });
  }
}
