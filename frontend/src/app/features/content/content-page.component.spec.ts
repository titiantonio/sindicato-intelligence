import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { of } from 'rxjs';

import { ContentListItem } from '../../core/models/content.models';
import { ContentService } from '../../core/services/content.service';
import { PublicationService } from '../../core/services/publication.service';
import { ContentPageComponent } from './content-page.component';

describe('ContentPageComponent', () => {
  let fixture: ComponentFixture<ContentPageComponent>;
  let component: ContentPageComponent;
  let contentService: jasmine.SpyObj<ContentService>;
  let publicationService: jasmine.SpyObj<PublicationService>;

  const contents: ContentListItem[] = [
    contentItem(1, 'Telegram', 'Pendiente SIPRI', 'PENDING_REVIEW', '2026-06-15T10:00:00Z', null),
    contentItem(2, 'Telegram', 'Aprobado Oposiciones', 'APPROVED', '2026-06-15T09:00:00Z', '2026-06-15T09:30:00Z'),
    contentItem(3, 'Telegram', 'Rechazado Plantillas', 'REJECTED', '2026-06-14T10:00:00Z', null),
    contentItem(4, 'Telegram', 'Publicado Retribuciones', 'PUBLISHED', '2026-06-13T10:00:00Z', '2026-06-13T11:00:00Z')
  ];

  beforeEach(async () => {
    contentService = jasmine.createSpyObj<ContentService>('ContentService', ['listContent', 'approveContent', 'rejectContent', 'updateContent']);
    publicationService = jasmine.createSpyObj<PublicationService>('PublicationService', ['schedulePublication']);
    contentService.listContent.and.returnValue(of(contents));
    contentService.updateContent.and.returnValue(of({ ...contents[0], title: 'Actualizado' }));
    publicationService.schedulePublication.and.returnValue(of({
      id: 9,
      contentId: 2,
      channel: 'Telegram',
      externalId: null,
      status: 'SCHEDULED',
      scheduledAt: '2026-06-16T10:00:00Z',
      publishedAt: null,
      responsePayload: null
    }));

    await TestBed.configureTestingModule({
      imports: [ContentPageComponent],
      providers: [
        { provide: ContentService, useValue: contentService },
        { provide: PublicationService, useValue: publicationService }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(ContentPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('opens the editorial modal in read mode when a row is clicked', () => {
    const firstRow = fixture.debugElement.query(By.css('tbody tr.content-row'));

    firstRow.triggerEventHandler('click');
    fixture.detectChanges();

    const modal = fixture.nativeElement.querySelector('.editor-modal') as HTMLElement;
    expect(modal.textContent).toContain('Contenido en lectura');
    expect(modal.textContent).toContain('Pendiente SIPRI');
    expect(modal.querySelector('textarea')).toBeNull();
  });

  it('opens the editorial modal in edit mode from the edit button', () => {
    const editButton = editButtons()[0];

    editButton.click();
    fixture.detectChanges();

    const modal = fixture.nativeElement.querySelector('.editor-modal') as HTMLElement;
    expect(modal.textContent).toContain('Contenido editable');
    expect(modal.querySelector('textarea')).not.toBeNull();
  });

  it('enables edit only for pending review and approved content', () => {
    const buttons = editButtons();

    expect(buttons[0].disabled).toBeFalse();
    expect(buttons[1].disabled).toBeFalse();
    expect(buttons[2].disabled).toBeTrue();
    expect(buttons[3].disabled).toBeTrue();
  });

  it('saves editable content changes', () => {
    editButtons()[0].click();
    fixture.detectChanges();

    (component as any).editTitle.set('Nuevo titulo');
    (component as any).editTone.set('Informativo');
    (component as any).editContent.set('Nuevo contenido');
    fixture.debugElement.query(By.css('.editor-form')).triggerEventHandler('submit', { preventDefault: () => undefined });

    expect(contentService.updateContent).toHaveBeenCalledWith(1, {
      title: 'Nuevo titulo',
      tone: 'Informativo',
      content: 'Nuevo contenido'
    });
  });

  it('allows scheduling only for approved content', () => {
    editButtons()[1].click();
    fixture.detectChanges();

    const scheduleInput = fixture.nativeElement.querySelector('input[name="scheduleAt"]') as HTMLInputElement;
    const scheduleButton = fixture.nativeElement.querySelector('.schedule-form button') as HTMLButtonElement;

    expect(scheduleInput.disabled).toBeFalse();
    expect(scheduleButton.disabled).toBeFalse();

    (component as any).scheduleAt.set('2099-01-01T10:00');
    fixture.debugElement.query(By.css('.schedule-form')).triggerEventHandler('submit', { preventDefault: () => undefined });

    expect(publicationService.schedulePublication).toHaveBeenCalledWith(2, new Date('2099-01-01T10:00').toISOString());
  });

  it('keeps local filtering, sorting and pagination available', () => {
    (component as any).setStatusFilter('APPROVED');
    expect((component as any).displayedItems().map((item: ContentListItem) => item.id)).toEqual([2]);

    (component as any).setStatusFilter('');
    (component as any).changeSort('title');
    expect((component as any).displayedItems().map((item: ContentListItem) => item.id)).toEqual([2, 1, 4, 3]);

    (component as any).setPageSize('2');
    expect((component as any).paginatedItems().map((item: ContentListItem) => item.id)).toEqual([2, 1]);
  });

  function editButtons(): HTMLButtonElement[] {
    return Array.from(fixture.nativeElement.querySelectorAll('.content-actions button:first-child'));
  }

  function contentItem(
    id: number,
    channel: string,
    title: string,
    status: string,
    generatedAt: string,
    approvedAt: string | null
  ): ContentListItem {
    return {
      id,
      eventId: id + 10,
      createdBy: 1,
      channel,
      tone: 'INFORMATIVO',
      title,
      content: `Contenido ${title}`,
      status,
      generatedAt,
      approvedAt
    };
  }
});
