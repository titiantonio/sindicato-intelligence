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
    if (item.signed && item.value < 0) {
      return 'danger';
    }

    if (item.signed && item.value === 0) {
      return 'neutral';
    }

    return item.tone;
  }

  protected formattedLastUpdatedAt(): string {
    return this.timeFormatter.format(new Date(this.card().lastUpdatedAt));
  }

  protected iconPath(icon: string): string {
    switch (icon) {
      case 'news':
        return 'M4 5h16v14H4V5Zm3 3h5v5H7V8Zm8 0h3M15 12h3M7 16h11';
      case 'target':
        return 'M12 21a9 9 0 1 0 0-18 9 9 0 0 0 0 18Zm0-4a5 5 0 1 0 0-10 5 5 0 0 0 0 10Zm0-3a2 2 0 1 0 0-4 2 2 0 0 0 0 4Z';
      case 'content':
      case 'file':
        return 'M7 3h7l5 5v13H7V3Zm7 0v6h5M10 13h6M10 17h6';
      case 'send':
        return 'M21 3 10 14M21 3l-7 18-4-7-7-4 18-7Z';
      case 'trend':
        return 'M4 17l6-6 4 4 6-8M20 7v6h-6';
      case 'alert':
        return 'M12 3 2 21h20L12 3Zm0 6v5m0 4h.01';
      case 'clock':
        return 'M12 21a9 9 0 1 0 0-18 9 9 0 0 0 0 18Zm0-13v5l3 2';
      case 'search':
        return 'M10.5 18a7.5 7.5 0 1 1 5.3-12.8 7.5 7.5 0 0 1 0 10.6L21 21';
      case 'check':
        return 'M20 6 9 17l-5-5';
      case 'calendar':
        return 'M7 3v4M17 3v4M4 9h16M5 5h14v16H5V5Z';
      case 'x':
        return 'M18 6 6 18M6 6l12 12';
      case 'total':
        return 'M4 6h16M4 12h16M4 18h16';
      default:
        return 'M12 21a9 9 0 1 0 0-18 9 9 0 0 0 0 18Z';
    }
  }
}
