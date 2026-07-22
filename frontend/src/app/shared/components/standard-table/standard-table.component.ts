import { NgTemplateOutlet } from '@angular/common';
import { Component, TemplateRef, computed, contentChild, input, output } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ButtonModule } from 'primeng/button';
import { SelectModule } from 'primeng/select';
import { TableModule } from 'primeng/table';

@Component({
  selector: 'app-standard-table',
  imports: [ButtonModule, FormsModule, NgTemplateOutlet, SelectModule, TableModule],
  templateUrl: './standard-table.component.html',
  styleUrl: './standard-table.component.scss',
  host: {
    '[class.standard-table-host--pilot]': "appearance() === 'pilot'"
  }
})
export class StandardTableComponent<T> {
  readonly rows = input.required<T[]>();
  readonly columnCount = input.required<number>();
  readonly totalItems = input.required<number>();
  readonly totalLabel = input.required<string>();
  readonly pageSize = input.required<number>();
  readonly currentPage = input.required<number>();
  readonly totalPages = input.required<number>();
  readonly pageSizeOptions = input<number[]>([5, 10, 25, 50]);
  readonly loading = input(false);
  readonly loadingRows = input(5);
  readonly minWidth = input('64rem');
  readonly appearance = input<'default' | 'pilot'>('default');

  readonly pageSizeChange = output<string>();
  readonly previousPage = output<void>();
  readonly nextPage = output<void>();

  protected readonly headerTemplate = contentChild<TemplateRef<unknown>>('tableHeader');
  protected readonly filtersTemplate = contentChild<TemplateRef<unknown>>('tableFilters');
  protected readonly rowTemplate = contentChild<TemplateRef<{ $implicit: T }>>('tableRow');
  protected readonly emptyTemplate = contentChild<TemplateRef<unknown>>('tableEmpty');
  protected readonly loadingTemplate = contentChild<TemplateRef<unknown>>('tableLoading');

  protected readonly skeletonRows = computed(() => Array.from({ length: this.loadingRows() }));
  protected readonly skeletonCells = computed(() => Array.from({ length: this.columnCount() }));

  protected setPageSize(value: string | number): void {
    this.pageSizeChange.emit(String(value));
  }
}
