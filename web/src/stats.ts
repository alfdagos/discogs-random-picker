import { artistLine } from './filter';
import type { Album, HistoryEntry } from './types';

export interface Ranked { label: string; count: number }

export interface Stats {
  totalListens: number;
  uniqueAlbums: number;
  coverage: number; // 0..100
  topArtists: Ranked[];
  genres: Ranked[];
  decades: Ranked[];
  formats: Ranked[];
  last7Days: number;
}

function rank(values: string[], limit: number): Ranked[] {
  const m = new Map<string, number>();
  for (const v of values) m.set(v, (m.get(v) ?? 0) + 1);
  return [...m.entries()]
    .map(([label, count]) => ({ label, count }))
    .sort((a, b) => b.count - a.count || a.label.localeCompare(b.label))
    .slice(0, limit);
}

export function computeStats(history: HistoryEntry[], collection: Album[], now = Date.now()): Stats {
  const unique = new Set(history.map((h) => h.album.id));
  const week = now - 7 * 24 * 3600 * 1000;
  return {
    totalListens: history.length,
    uniqueAlbums: unique.size,
    coverage: collection.length ? Math.round((unique.size / collection.length) * 100) : 0,
    topArtists: rank(history.flatMap((h) => (h.album.artists.length ? h.album.artists : [artistLine(h.album)])), 5),
    genres: rank(history.flatMap((h) => h.album.genres), 6),
    decades: rank(history.filter((h) => h.album.year > 0).map((h) => `${Math.floor(h.album.year / 10) * 10}s`), 8)
      .sort((a, b) => a.label.localeCompare(b.label)),
    formats: rank(history.flatMap((h) => h.album.formats), 5),
    last7Days: history.filter((h) => Date.parse(h.listenedAt) >= week).length,
  };
}
