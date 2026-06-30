import { Component, input } from '@angular/core';

import { MetricCard, MetricCardItem } from '../../../core/models/dashboard.models';

@Component({
  selector: 'app-metric-card',
  templateUrl: './metric-card.component.html',
  styleUrl: './metric-card.component.scss'
})
export class MetricCardComponent {
  readonly card = input.required<MetricCard>();

  private readonly numberFormatter = new Intl.NumberFormat('es-ES');
  private readonly timeFormatter = new Intl.DateTimeFormat('es-ES', {
    dateStyle: 'short',
    timeStyle: 'short'
  });

  protected formattedValue(item: MetricCardItem): string {
    const value = this.numberFormatter.format(Math.abs(item.value));
    if (!item.signed) {
      return this.numberFormatter.format(item.value);
    }

    if (item.value > 0) {
      return `+${value}`;
    }

    if (item.value < 0) {
      return `-${value}`;
    }

    return '0';
  }

  protected valueTone(item: MetricCardItem): string {
    if (item.signed && item.value === 0) {
      return 'neutral';
    }

    return item.tone;
  }

  protected formattedLastUpdatedAt(): string {
    return this.timeFormatter.format(new Date(this.card().lastUpdatedAt));
  }

  protected iconClass(icon: string): string {
    switch (icon) {
      case 'news':
        return 'pi pi-file';
      case 'target':
        return 'pi pi-bullseye';
      case 'content':
      case 'file':
        return 'pi pi-pen-to-square';
      case 'send':
        return 'pi pi-send';
      case 'trend':
        return 'pi pi-arrow-up-right';
      case 'alert':
        return 'pi pi-exclamation-triangle';
      case 'clock':
        return 'pi pi-clock';
      case 'search':
        return 'pi pi-search';
      case 'check':
        return 'pi pi-check';
      case 'calendar':
        return 'pi pi-calendar';
      case 'x':
        return 'pi pi-times';
      case 'total':
        return 'pi pi-list';
      default:
        return 'pi pi-circle';
    }
  }
}
