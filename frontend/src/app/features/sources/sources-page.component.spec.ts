import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of, throwError } from 'rxjs';

import { SourceResponse } from '../../core/models/source.models';
import { SourceService } from '../../core/services/source.service';
import { SourcesPageComponent } from './sources-page.component';

describe('SourcesPageComponent', () => {
  let fixture: ComponentFixture<SourcesPageComponent>;
  let component: SourcesPageComponent;
  let sourceService: jasmine.SpyObj<SourceService>;

  const sources: SourceResponse[] = [
    {
      id: 1,
      name: 'BOJA',
      url: 'https://www.juntadeandalucia.es/boja/rss.xml',
      type: 'RSS',
      priority: 5,
      active: true,
      createdAt: '2026-06-10T10:00:00Z',
      updatedAt: '2026-06-12T10:00:00Z'
    },
    {
      id: 2,
      name: 'Consejeria',
      url: 'https://www.juntadeandalucia.es/educacion/atom.xml',
      type: 'ATOM',
      priority: 1,
      active: false,
      createdAt: '2026-06-11T10:00:00Z',
      updatedAt: '2026-06-13T10:00:00Z'
    }
  ];

  beforeEach(async () => {
    sourceService = jasmine.createSpyObj<SourceService>('SourceService', [
      'listSources',
      'createSource',
      'updateSource'
    ]);
    sourceService.listSources.and.returnValue(of(sources));

    await TestBed.configureTestingModule({
      imports: [SourcesPageComponent],
      providers: [{ provide: SourceService, useValue: sourceService }]
    }).compileComponents();

    fixture = TestBed.createComponent(SourcesPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('loads sources on init', () => {
    expect(sourceService.listSources).toHaveBeenCalled();
    expect((component as any).sources()).toEqual(sources);
    expect((component as any).displayedSources().length).toBe(2);
  });

  it('opens creation modal and submits new sources', () => {
    sourceService.createSource.and.returnValue(of(sources[0]));

    (component as any).startCreate();
    (component as any).sourceForm.setValue({
      name: 'Nueva fuente',
      url: 'https://example.org/rss',
      type: 'RSS',
      priority: 10,
      active: true
    });
    (component as any).submit();

    expect((component as any).isModalOpen()).toBeFalse();
    expect(sourceService.createSource).toHaveBeenCalledWith({
      name: 'Nueva fuente',
      url: 'https://example.org/rss',
      type: 'RSS',
      priority: 10,
      active: true
    });
    expect((component as any).successMessage()).toBe('Fuente creada correctamente.');
  });

  it('opens edit modal and submits source updates', () => {
    sourceService.updateSource.and.returnValue(of({ ...sources[0], priority: 7 }));

    (component as any).startEdit(sources[0]);

    expect((component as any).isModalOpen()).toBeTrue();
    expect((component as any).formMode()).toBe('edit');
    expect((component as any).editingSourceId()).toBe(1);

    (component as any).sourceForm.patchValue({ priority: 7 });
    (component as any).submit();

    expect(sourceService.updateSource).toHaveBeenCalledWith(1, {
      name: 'BOJA',
      url: 'https://www.juntadeandalucia.es/boja/rss.xml',
      type: 'RSS',
      priority: 7,
      active: true
    });
    expect((component as any).successMessage()).toBe('Fuente actualizada correctamente.');
  });

  it('filters sources by any visible field', () => {
    (component as any).setGlobalFilter('atom');

    expect((component as any).displayedSources()).toEqual([sources[1]]);

    (component as any).setGlobalFilter('');
    (component as any).setActiveFilter('Activa');

    expect((component as any).displayedSources()).toEqual([sources[0]]);
  });

  it('sorts sources by selected column', () => {
    (component as any).changeSort('priority');

    expect((component as any).displayedSources().map((source: SourceResponse) => source.id)).toEqual([2, 1]);

    (component as any).changeSort('priority');

    expect((component as any).displayedSources().map((source: SourceResponse) => source.id)).toEqual([1, 2]);
  });

  it('does not submit invalid forms', () => {
    (component as any).startCreate();
    (component as any).sourceForm.setValue({
      name: 'AB',
      url: '',
      type: 'RSS',
      priority: 10,
      active: true
    });

    (component as any).submit();

    expect(sourceService.createSource).not.toHaveBeenCalled();
    expect((component as any).sourceForm.touched).toBeTrue();
  });

  it('shows service errors', () => {
    sourceService.createSource.and.returnValue(
      throwError(() => ({ error: { error: 'URL duplicada' } }))
    );
    (component as any).startCreate();
    (component as any).sourceForm.setValue({
      name: 'BOJA duplicada',
      url: 'https://www.juntadeandalucia.es/boja/rss.xml',
      type: 'RSS',
      priority: 10,
      active: true
    });

    (component as any).submit();

    expect((component as any).errorMessage()).toBe('URL duplicada');
    expect((component as any).isSubmitting()).toBeFalse();
  });
});
