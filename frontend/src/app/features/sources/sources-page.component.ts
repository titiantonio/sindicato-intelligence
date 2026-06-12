import { CommonModule } from '@angular/common';
import { Component, OnInit, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';

import { SourceResponse } from '../../core/models/source.models';
import { SourceService } from '../../core/services/source.service';

@Component({
  selector: 'app-sources-page',
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './sources-page.component.html',
  styleUrl: './sources-page.component.scss'
})
export class SourcesPageComponent implements OnInit {
  private readonly formBuilder = inject(FormBuilder);
  private readonly sourceService = inject(SourceService);

  protected readonly sources = signal<SourceResponse[]>([]);
  protected readonly isLoading = signal(false);
  protected readonly isSubmitting = signal(false);
  protected readonly errorMessage = signal<string | null>(null);
  protected readonly successMessage = signal<string | null>(null);
  protected readonly editingSourceId = signal<number | null>(null);

  protected readonly sourceForm = this.formBuilder.nonNullable.group({
    name: ['', [Validators.required, Validators.minLength(3)]],
    url: ['', [Validators.required]],
    type: ['RSS', [Validators.required]],
    priority: [10, [Validators.required, Validators.min(0)]],
    active: [true]
  });

  ngOnInit(): void {
    this.loadSources();
  }

  protected loadSources(): void {
    this.isLoading.set(true);
    this.errorMessage.set(null);

    this.sourceService.listSources().subscribe({
      next: (sources) => {
        this.sources.set(sources);
        this.isLoading.set(false);
      },
      error: (error: { error?: { error?: string } }) => {
        this.errorMessage.set(error.error?.error ?? 'No se pudieron cargar las fuentes.');
        this.isLoading.set(false);
      }
    });
  }

  protected startCreate(): void {
    this.editingSourceId.set(null);
    this.successMessage.set(null);
    this.errorMessage.set(null);
    this.sourceForm.reset({ name: '', url: '', type: 'RSS', priority: 10, active: true });
  }

  protected startEdit(source: SourceResponse): void {
    this.editingSourceId.set(source.id);
    this.successMessage.set(null);
    this.errorMessage.set(null);
    this.sourceForm.reset({
      name: source.name,
      url: source.url,
      type: source.type,
      priority: source.priority,
      active: source.active
    });
  }

  protected submit(): void {
    if (this.sourceForm.invalid) {
      this.sourceForm.markAllAsTouched();
      return;
    }

    this.isSubmitting.set(true);
    this.successMessage.set(null);
    this.errorMessage.set(null);

    const payload = this.sourceForm.getRawValue();
    const editingSourceId = this.editingSourceId();
    const request = editingSourceId === null
      ? this.sourceService.createSource(payload)
      : this.sourceService.updateSource(editingSourceId, payload);

    request.subscribe({
      next: () => {
        this.isSubmitting.set(false);
        this.successMessage.set(editingSourceId === null ? 'Fuente creada correctamente.' : 'Fuente actualizada correctamente.');
        this.startCreateKeepingMessage();
        this.loadSources();
      },
      error: (error: { error?: { error?: string } }) => {
        this.isSubmitting.set(false);
        this.errorMessage.set(error.error?.error ?? 'No se pudo guardar la fuente.');
      }
    });
  }

  protected formatDate(value: string): string {
    return new Intl.DateTimeFormat('es-ES', {
      dateStyle: 'short',
      timeStyle: 'short'
    }).format(new Date(value));
  }

  private startCreateKeepingMessage(): void {
    this.editingSourceId.set(null);
    this.sourceForm.reset({ name: '', url: '', type: 'RSS', priority: 10, active: true });
  }
}