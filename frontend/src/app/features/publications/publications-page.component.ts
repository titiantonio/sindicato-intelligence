import { Component, ElementRef, OnInit, ViewChild, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { ButtonModule } from 'primeng/button';
import { DialogModule } from 'primeng/dialog';
import { InputTextModule } from 'primeng/inputtext';
import { MessageModule } from 'primeng/message';
import { TooltipModule } from 'primeng/tooltip';

import { OperationalTelegramDestination, PublicationListItem } from '../../core/models/publication.models';
import { PublicationService } from '../../core/services/publication.service';
import { StatusBadgeComponent } from '../../shared/components/status-badge/status-badge.component';
import { DialogFocusReturnDirective } from '../../shared/directives/dialog-focus-return.directive';
import { formatPublicationResult } from './publication-result.formatter';

type EditorPanel = 'link' | 'telegram' | 'blocks' | 'emotes';
type TelegramInlineTag = 'code' | 'tg-spoiler';
type TelegramBlockTag = 'pre' | 'blockquote' | 'expandable-blockquote';

@Component({
  selector: 'app-publications-page',
  imports: [ButtonModule, DialogFocusReturnDirective, DialogModule, FormsModule, InputTextModule, MessageModule, RouterLink, StatusBadgeComponent, TooltipModule],
  templateUrl: './publications-page.component.html',
  styleUrl: './publications-page.component.scss'
})
export class PublicationsPageComponent implements OnInit {
  private readonly publicationService = inject(PublicationService);

  @ViewChild('manualTitleEditor')
  private manualTitleEditor?: ElementRef<HTMLDivElement>;

  @ViewChild('manualEditor')
  private manualEditor?: ElementRef<HTMLDivElement>;

  @ViewChild('manualFileInput')
  private manualFileInput?: ElementRef<HTMLInputElement>;

  protected readonly publications = signal<PublicationListItem[]>([]);
  protected readonly destinations = signal<OperationalTelegramDestination[]>([]);
  protected readonly manualDialogVisible = signal(false);
  protected readonly manualTitle = signal('');
  protected readonly manualMessage = signal('');
  protected readonly selectedDestinationIds = signal<number[]>([]);
  protected readonly selectedFiles = signal<File[]>([]);
  protected readonly isLoading = signal(false);
  protected readonly isSendingManual = signal(false);
  protected readonly errorMessage = signal<string | null>(null);
  protected readonly successMessage = signal<string | null>(null);
  protected readonly manualErrorMessage = signal<string | null>(null);
  protected readonly attachmentErrorMessage = signal<string | null>(null);
  protected readonly activeEditorPanel = signal<EditorPanel | null>(null);
  protected readonly linkUrl = signal('');
  protected readonly linkText = signal('');
  protected readonly mentionUserId = signal('');
  protected readonly mentionText = signal('');
  protected readonly customEmojiId = signal('');
  protected readonly customEmojiAlt = signal('🙂');
  protected readonly timeUnix = signal('');
  protected readonly timeLabel = signal('');
  protected readonly timeFormat = signal('wDT');
  protected readonly codeLanguage = signal('');
  protected readonly activeEditorTarget = signal<'title' | 'message'>('message');
  protected readonly emoteGroups = [
    { label: 'Frecuentes', items: ['✅', '☑️', '✔️', '❌', '📌', 'ℹ️', '⚠️', '🚨', '📣', '📢', '🔔', '📨', '📩', '📝', '🗓️', '📅', '📍', '🔎', '⭐', '💡', '👉', '➡️', '⬇️', '🔗'] },
    { label: 'Tono', items: ['👋', '👍', '👎', '🙏', '💪', '🤝', '🙂', '😊', '😐', '😮', '😟', '😡', '👏', '🙌', '👌', '💬', '🗣️', '👀', '🚫', '❗', '❓', '❤️', '💙', '🫶'] },
    { label: 'Educacion', items: ['🎓', '🏫', '👩‍🏫', '👨‍🏫', '📚', '📖', '✏️', '🖊️', '📋', '📄', '📑', '🗂️', '📊', '📈', '📉', '🧪', '🔬', '💻', '🖥️', '🧑‍💻', '🧮', '🧠', '🏛️', '🧑‍🎓'] },
    { label: 'Tiempo y lugar', items: ['⏰', '⏱️', '⏳', '⌛', '📆', '🗓️', '🌅', '🌇', '🌙', '📍', '🧭', '🚌', '🚆', '🚗', '🚶', '🏛️', '🏢', '🏫', '🟢', '🟡', '🟠', '🔴', '🔵', '🟣'] },
    { label: 'Prioridad', items: ['🔥', '⚡', '🎯', '📎', '🔒', '🔓', '🧾', '💼', '🧩', '🛠️', '📡', '🧭', '🏷️', '🔖', '📥', '📤', '📦', '📁', '🧷', '🔐', '🛑', '✅', '❌', '⚠️'] },
    { label: 'Publicacion', items: ['📰', '📲', '💬', '📷', '🎥', '🎙️', '📻', '🔗', '🌐', '📡', '🧵', '✉️', '📫', '📭', '🗞️', '🖼️', '🎧', '📺', '🧠', '✍️', '🗒️', '📣', '📢', '🔔'] },
    { label: 'Numeros', items: ['0️⃣', '1️⃣', '2️⃣', '3️⃣', '4️⃣', '5️⃣', '6️⃣', '7️⃣', '8️⃣', '9️⃣', '🔟', '#️⃣', '*️⃣', '➕', '➖', '✖️', '➗', '💯', '🔢', '🆕', '🆗', '🆘', '🆙', '🆒'] }
  ];

  ngOnInit(): void {
    this.loadPublications();
  }

  protected loadPublications(): void {
    this.isLoading.set(true);
    this.errorMessage.set(null);

    this.publicationService.listPublications().subscribe({
      next: (publications) => {
        this.publications.set(publications);
        this.isLoading.set(false);
      },
      error: (error: { error?: { error?: string } }) => {
        this.errorMessage.set(error.error?.error ?? 'No se pudo cargar el historico de publicaciones.');
        this.isLoading.set(false);
      }
    });
  }

  protected formatDate(value: string | null): string {
    if (value === null) {
      return 'Pendiente';
    }

    return new Intl.DateTimeFormat('es-ES', {
      dateStyle: 'short',
      timeStyle: 'short'
    }).format(new Date(value));
  }

  protected publicationResult(publication: PublicationListItem): string {
    return formatPublicationResult(publication);
  }

  protected publicationDateLabel(publication: PublicationListItem): string {
    if (publication.status === 'SCHEDULED') {
      return `Programada: ${this.formatDate(publication.scheduledAt)}`;
    }

    return `Publicada: ${this.formatDate(publication.publishedAt)}`;
  }

  protected publicationOrigin(publication: PublicationListItem): string {
    return publication.publicationType === 'MANUAL_MESSAGE' ? 'Manual' : `Contenido #${publication.contentId}`;
  }

  protected publicationAuthor(publication: PublicationListItem): string {
    return publication.requestedByName
      ? `Autor: ${publication.requestedByName}`
      : publication.requestedBy
        ? `Autor #${publication.requestedBy}`
        : 'Autor no registrado';
  }

  protected openManualDialog(): void {
    this.manualDialogVisible.set(true);
    this.errorMessage.set(null);
    this.successMessage.set(null);
    this.manualErrorMessage.set(null);
    this.attachmentErrorMessage.set(null);
    this.publicationService.listTelegramDestinations().subscribe({
      next: (destinations) => {
        this.destinations.set(destinations);
        this.selectedDestinationIds.set(destinations.filter((destination) => destination.defaultSelected).map((destination) => destination.id));
      },
      error: (error: { error?: { error?: string } }) => {
        this.manualErrorMessage.set(error.error?.error ?? 'No se pudieron cargar los destinos Telegram.');
      }
    });
  }

  protected setManualDialogVisible(visible: boolean): void {
    this.manualDialogVisible.set(visible);
    if (!visible) {
      this.resetManualForm();
    }
  }

  protected updateDestinationSelection(destinationId: number, checked: boolean): void {
    const selected = new Set(this.selectedDestinationIds());
    if (checked) {
      selected.add(destinationId);
    } else {
      selected.delete(destinationId);
    }
    this.selectedDestinationIds.set([...selected]);
  }

  protected updateSelectedFiles(event: Event): void {
    const input = event.target as HTMLInputElement;
    this.selectedFiles.set(input.files ? Array.from(input.files) : []);
    this.attachmentErrorMessage.set(null);
    this.manualErrorMessage.set(null);
  }

  protected updateManualTitleFromEditor(event: Event): void {
    const editor = event.target as HTMLDivElement;
    this.activeEditorTarget.set('title');
    this.manualTitle.set(this.normalizeEditorHtml(editor.innerHTML));
  }

  protected updateManualMessageFromEditor(event: Event): void {
    const editor = event.target as HTMLDivElement;
    this.activeEditorTarget.set('message');
    this.manualMessage.set(this.normalizeEditorHtml(editor.innerHTML));
  }

  protected setActiveEditorTarget(target: 'title' | 'message'): void {
    this.activeEditorTarget.set(target);
  }

  protected titleCharacterCount(): number {
    return this.textCharacterCount(this.manualTitle());
  }

  protected messageCharacterCount(): number {
    return this.textCharacterCount(this.manualMessage());
  }

  protected applyFormat(command: 'bold' | 'italic' | 'underline' | 'strikeThrough'): void {
    this.focusEditor();
    document.execCommand(command, false);
    this.syncActiveEditor();
  }

  protected applyInlineTag(tag: TelegramInlineTag): void {
    const fallback = tag === 'code' ? 'codigo' : 'spoiler';
    this.insertWrappedHtml(`<${tag}>`, `</${tag}>`, fallback);
  }

  protected insertBlock(tag: TelegramBlockTag): void {
    if (tag === 'pre') {
      this.insertCodeBlock();
      return;
    }
    const expandable = tag === 'expandable-blockquote' ? ' expandable' : '';
    this.insertHtml(`<blockquote${expandable}>${this.escapeHtml(this.selectedTextOr('Cita'))}</blockquote>`);
  }

  protected toggleEditorPanel(panel: EditorPanel): void {
    this.activeEditorPanel.update((active) => active === panel ? null : panel);
  }

  protected applyLink(): void {
    const url = this.linkUrl().trim();
    if (!/^(https?:\/\/|mailto:|tel:)/i.test(url)) {
      return;
    }
    const text = this.linkText().trim() || this.selectedTextOr(url);
    this.insertHtml(`<a href="${this.escapeAttribute(url)}">${this.escapeHtml(text)}</a>`);
    this.activeEditorPanel.set(null);
    this.linkUrl.set('');
    this.linkText.set('');
  }

  protected applyMention(): void {
    const userId = this.mentionUserId().trim();
    if (!/^\d+$/.test(userId)) {
      return;
    }
    const text = this.mentionText().trim() || this.selectedTextOr('Usuario');
    this.insertHtml(`<a href="tg://user?id=${this.escapeAttribute(userId)}">${this.escapeHtml(text)}</a>`);
    this.mentionUserId.set('');
    this.mentionText.set('');
    this.activeEditorPanel.set(null);
  }

  protected insertCustomEmoji(): void {
    const emojiId = this.customEmojiId().trim();
    const alt = this.customEmojiAlt().trim();
    if (!/^\d+$/.test(emojiId) || !alt) {
      return;
    }
    this.insertHtml(`<tg-emoji emoji-id="${this.escapeAttribute(emojiId)}">${this.escapeHtml(alt)}</tg-emoji>`);
    this.customEmojiId.set('');
    this.customEmojiAlt.set('🙂');
    this.activeEditorPanel.set(null);
  }

  protected insertTime(): void {
    const unix = this.timeUnix().trim();
    const format = this.timeFormat().trim();
    if (!/^\d+$/.test(unix) || !/^(r|w?[dD]?[tT]?)$/.test(format)) {
      return;
    }
    const label = this.timeLabel().trim() || 'Fecha';
    const formatAttribute = format ? ` format="${this.escapeAttribute(format)}"` : '';
    this.insertHtml(`<tg-time unix="${this.escapeAttribute(unix)}"${formatAttribute}>${this.escapeHtml(label)}</tg-time>`);
    this.timeUnix.set('');
    this.timeLabel.set('');
    this.activeEditorPanel.set(null);
  }

  protected insertCodeBlock(): void {
    const language = this.codeLanguage().trim().replace(/[^a-z0-9_+#-]/gi, '');
    const text = this.selectedTextOr('bloque de codigo');
    const codeOpen = language ? `<code class="language-${this.escapeAttribute(language)}">` : '<code>';
    this.insertHtml(`<pre>${codeOpen}${this.escapeHtml(text)}</code></pre>`);
    this.codeLanguage.set('');
    this.activeEditorPanel.set(null);
  }

  protected insertPlainTemplate(template: string): void {
    this.insertHtml(this.escapeHtml(template));
  }

  protected insertSeparator(): void {
    this.insertPlainTemplate('\n━━━━━━━━━━━━\n');
  }

  protected isPanelVisible(panel: EditorPanel): boolean {
    return this.activeEditorPanel() === panel;
  }

  protected insertEmote(emote: string): void {
    this.insertPlainTemplate(emote);
  }

  protected sendManualPublication(): void {
    this.isSendingManual.set(true);
    this.errorMessage.set(null);
    this.successMessage.set(null);
    this.manualErrorMessage.set(null);
    this.attachmentErrorMessage.set(null);
    this.publicationService.publishManual({
      channel: 'TELEGRAM',
      title: this.manualTitle(),
      message: this.manualMessage(),
      destinationIds: this.selectedDestinationIds(),
      files: this.selectedFiles()
    }).subscribe({
      next: () => {
        this.successMessage.set('Mensaje manual enviado.');
        this.isSendingManual.set(false);
        this.setManualDialogVisible(false);
        this.loadPublications();
      },
      error: (error: { error?: { error?: string } }) => {
        const message = error.error?.error ?? 'No se pudo enviar el mensaje manual.';
        this.manualErrorMessage.set(message);
        if (this.isAttachmentError(message)) {
          this.attachmentErrorMessage.set(message);
        }
        this.isSendingManual.set(false);
      }
    });
  }

  protected fileSummary(file: File): string {
    return `${file.name} (${Math.ceil(file.size / 1024)} KB)`;
  }

  private resetManualForm(): void {
    this.manualTitle.set('');
    this.manualMessage.set('');
    this.selectedDestinationIds.set([]);
    this.selectedFiles.set([]);
    this.destinations.set([]);
    this.manualErrorMessage.set(null);
    this.attachmentErrorMessage.set(null);
    this.activeEditorPanel.set(null);
    this.linkUrl.set('');
    this.linkText.set('');
    this.mentionUserId.set('');
    this.mentionText.set('');
    this.customEmojiId.set('');
    this.customEmojiAlt.set('🙂');
    this.timeUnix.set('');
    this.timeLabel.set('');
    this.timeFormat.set('wDT');
    this.codeLanguage.set('');
    this.activeEditorTarget.set('message');
    if (this.manualTitleEditor?.nativeElement) {
      this.manualTitleEditor.nativeElement.innerHTML = '';
    }
    if (this.manualEditor?.nativeElement) {
      this.manualEditor.nativeElement.innerHTML = '';
    }
    if (this.manualFileInput?.nativeElement) {
      this.manualFileInput.nativeElement.value = '';
    }
  }

  private isAttachmentError(message: string): boolean {
    const normalized = message.toLowerCase();
    return normalized.includes('adjunto') || normalized.includes('archivo') || normalized.includes('bytes');
  }

  private focusEditor(): void {
    this.currentEditor()?.focus();
  }

  private syncActiveEditor(): void {
    const editor = this.currentEditor();
    if (!editor) {
      return;
    }
    const value = this.normalizeEditorHtml(editor.innerHTML);
    if (this.activeEditorTarget() === 'title') {
      this.manualTitle.set(value);
    } else {
      this.manualMessage.set(value);
    }
  }

  private currentEditor(): HTMLDivElement | undefined {
    return this.activeEditorTarget() === 'title'
      ? this.manualTitleEditor?.nativeElement
      : this.manualEditor?.nativeElement;
  }

  private insertWrappedHtml(openTag: string, closeTag: string, fallback: string): void {
    this.insertHtml(`${openTag}${this.escapeHtml(this.selectedTextOr(fallback))}${closeTag}`);
  }

  private insertHtml(html: string): void {
    this.focusEditor();
    document.execCommand('insertHTML', false, html);
    this.syncActiveEditor();
  }

  private selectedTextOr(fallback: string): string {
    const selection = window.getSelection();
    const text = selection?.toString().trim();
    return text || fallback;
  }

  private textCharacterCount(value: string): number {
    return value
      .replace(/<[^>]*>/g, '')
      .replace(/&nbsp;/gi, ' ')
      .replace(/&lt;/gi, '<')
      .replace(/&gt;/gi, '>')
      .replace(/&amp;/gi, '&')
      .trim()
      .length;
  }

  private escapeHtml(value: string): string {
    return value
      .replace(/&/g, '&amp;')
      .replace(/</g, '&lt;')
      .replace(/>/g, '&gt;')
      .replace(/"/g, '&quot;');
  }

  private escapeAttribute(value: string): string {
    return this.escapeHtml(value).replace(/'/g, '&#39;');
  }

  private normalizeEditorHtml(value: string): string {
    return value
      .replace(/<div><br><\/div>/gi, '\n')
      .replace(/<div>/gi, '\n')
      .replace(/<\/div>/gi, '')
      .replace(/<br\s*\/?>/gi, '\n')
      .replace(/\sstyle="[^"]*"/gi, '')
      .replace(/\sdata-[a-z0-9-]+="[^"]*"/gi, '')
      .replace(/&nbsp;/gi, ' ')
      .trim();
  }
}
