import { artistLine } from './filter';
import type { HistoryEntry } from './types';

export const esc = (s: string) =>
  s.replace(/[&<>"']/g, (c) => ({ '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;' })[c]!);
const csvCell = (s: string) => `"${s.replace(/"/g, '""')}"`;
const fmtDate = (iso: string) => new Date(iso).toLocaleString('it-IT');

export function toCSV(h: HistoryEntry[]): string {
  const rows = h.map((e) =>
    [fmtDate(e.listenedAt), artistLine(e.album), e.album.title, e.album.year || '', e.album.formats.join('; '), e.album.genres.join('; ')]
      .map((c) => csvCell(String(c))).join(','));
  return ['Data,Artista,Titolo,Anno,Formato,Generi', ...rows].join('\n');
}

export function toMarkdown(h: HistoryEntry[]): string {
  const rows = h.map((e) =>
    `| ${fmtDate(e.listenedAt)} | ${artistLine(e.album)} | ${e.album.title} | ${e.album.year || ''} | ${e.album.formats.join(', ')} |`);
  return ['# Storico ascolti', '', '| Data | Artista | Titolo | Anno | Formato |', '|---|---|---|---|---|', ...rows].join('\n');
}

export function toHTML(h: HistoryEntry[]): string {
  const rows = h.map((e) =>
    `<tr><td>${esc(fmtDate(e.listenedAt))}</td><td>${esc(artistLine(e.album))}</td><td>${esc(e.album.title)}</td><td>${e.album.year || ''}</td><td>${esc(e.album.formats.join(', '))}</td></tr>`).join('');
  return `<!doctype html><html lang="it"><meta charset="utf-8"><title>Storico ascolti</title>
<style>body{font-family:system-ui,sans-serif;margin:2rem}table{border-collapse:collapse;width:100%}th,td{padding:.5rem;border-bottom:1px solid #ddd;text-align:left}th{background:#f4f4f4}</style>
<h1>Storico ascolti</h1><table><tr><th>Data</th><th>Artista</th><th>Titolo</th><th>Anno</th><th>Formato</th></tr>${rows}</table></html>`;
}

export function toJSON(h: HistoryEntry[]): string {
  return JSON.stringify(h, null, 2);
}

export function download(filename: string, content: string, mime: string): void {
  const url = URL.createObjectURL(new Blob([content], { type: `${mime};charset=utf-8` }));
  const a = Object.assign(document.createElement('a'), { href: url, download: filename });
  document.body.append(a);
  a.click();
  a.remove();
  setTimeout(() => URL.revokeObjectURL(url), 1000);
}
