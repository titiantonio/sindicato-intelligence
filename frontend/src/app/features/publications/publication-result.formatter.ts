import { PublicationListItem } from '../../core/models/publication.models';

export function formatPublicationResult(publication: PublicationListItem | null | undefined): string {
  if (!publication) {
    return 'Sin respuesta registrada.';
  }

  const payload = publication.responsePayload?.trim();
  if (payload) {
    const formattedPayload = formatPayload(payload);
    if (formattedPayload) {
      return formattedPayload;
    }

    return payload;
  }

  if (publication.externalId) {
    return `Publicacion registrada en Telegram con mensaje #${publication.externalId}.`;
  }

  return publication.status === 'SCHEDULED'
    ? 'Publicacion programada pendiente de envio.'
    : 'Sin respuesta registrada.';
}

function formatPayload(payload: string): string | null {
  try {
    const parsed = JSON.parse(payload) as unknown;
    if (!isRecord(parsed)) {
      return null;
    }

    const messageId = textValue(parsed['messageId'])
      ?? textValue(parsed['message_id'])
      ?? nestedMessageId(parsed);
    const description = textValue(parsed['description'])
      ?? textValue(parsed['error'])
      ?? textValue(parsed['message']);
    const ok = parsed['ok'];

    if (ok === true && messageId) {
      return `Telegram confirmo la publicacion. Mensaje #${messageId}.`;
    }

    const messageIds = Array.isArray(parsed['messageIds']) ? parsed['messageIds'].map((item) => textValue(item)).filter(Boolean) : [];
    if (ok === true && messageIds.length) {
      return `Telegram confirmo la publicacion. Mensajes #${messageIds.join(', #')}.`;
    }

    if (ok === true) {
      return 'Telegram confirmo la publicacion correctamente.';
    }

    if (ok === false && description) {
      return `Telegram rechazo la publicacion: ${description}.`;
    }

    if (description) {
      return description;
    }

    return null;
  } catch {
    return null;
  }
}

function nestedMessageId(value: Record<string, unknown>): string | null {
  const result = value['result'];
  if (!isRecord(result)) {
    return null;
  }

  return textValue(result['message_id']) ?? textValue(result['messageId']);
}

function textValue(value: unknown): string | null {
  if (typeof value === 'string' && value.trim()) {
    return value.trim();
  }

  if (typeof value === 'number' && Number.isFinite(value)) {
    return value.toString();
  }

  return null;
}

function isRecord(value: unknown): value is Record<string, unknown> {
  return typeof value === 'object' && value !== null && !Array.isArray(value);
}
