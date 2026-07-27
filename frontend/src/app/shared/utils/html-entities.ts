export function decodeHtmlEntities(documentRef: Document, value: string): string {
  if (!value.includes('&')) {
    return value;
  }

  const decoder = documentRef.createElement('textarea');
  decoder.innerHTML = value;
  return decoder.value;
}
