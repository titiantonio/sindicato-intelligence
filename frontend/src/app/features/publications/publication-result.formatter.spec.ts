import { PublicationListItem } from '../../core/models/publication.models';
import { formatPublicationResult } from './publication-result.formatter';

describe('formatPublicationResult', () => {
  it('formats compact Telegram success payloads with messageId', () => {
    expect(formatPublicationResult(publication({ responsePayload: '{"ok":true,"messageId":"459"}' })))
      .toBe('Telegram confirmo la publicacion. Mensaje #459.');
  });

  it('formats Telegram API nested message_id payloads', () => {
    expect(formatPublicationResult(publication({ responsePayload: '{"ok":true,"result":{"message_id":460}}' })))
      .toBe('Telegram confirmo la publicacion. Mensaje #460.');
  });

  it('formats rejected Telegram payloads with description', () => {
    expect(formatPublicationResult(publication({ responsePayload: '{"ok":false,"description":"chat not found"}' })))
      .toBe('Telegram rechazo la publicacion: chat not found.');
  });

  it('falls back to external id when there is no payload', () => {
    expect(formatPublicationResult(publication({ responsePayload: null, externalId: '461' })))
      .toBe('Publicacion registrada en Telegram con mensaje #461.');
  });

  function publication(overrides: Partial<PublicationListItem>): PublicationListItem {
    return {
      id: 1,
      contentId: 2,
      channel: 'TELEGRAM',
      externalId: null,
      status: 'PUBLISHED',
      publishedAt: '2026-06-27T10:00:00Z',
      responsePayload: null,
      scheduledAt: null,
      ...overrides
    };
  }
});
