import { CommonModule } from '@angular/common';
import { Component, computed, input } from '@angular/core';
import { TagModule } from 'primeng/tag';

@Component({
  selector: 'app-status-badge',
  imports: [CommonModule, TagModule],
  templateUrl: './status-badge.component.html',
  styleUrl: './status-badge.component.scss'
})
export class StatusBadgeComponent {
  readonly value = input.required<string>();

  readonly tone = computed(() => {
    switch (this.value()) {
      case 'CRITICAL':
      case 'FAILED':
      case 'REJECTED':
        return 'danger';
      case 'HIGH':
      case 'PENDING_REVIEW':
      case 'OPEN':
        return 'warning';
      case 'APPROVED':
      case 'PUBLISHED':
        return 'success';
      default:
        return 'neutral';
    }
  });

  readonly severity = computed(() => {
    switch (this.tone()) {
      case 'danger':
        return 'danger';
      case 'warning':
        return 'warn';
      case 'success':
        return 'success';
      default:
        return 'secondary';
    }
  });
}
