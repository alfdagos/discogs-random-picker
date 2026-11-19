import type { Album, Filter } from './types';

export const artistLine = (a: Album) => a.artists.join(', ') || 'Artista sconosciuto';

export function matches(album: Album, f: Filter): boolean {
  if (f.genre && !album.genres.includes(f.genre) && !album.styles.includes(f.genre)) return false;
  if (f.format && !album.formats.includes(f.format)) return false;
  if (f.artist && !artistLine(album).toLowerCase().includes(f.artist.trim().toLowerCase())) return false;
  if (f.minYear !== null && (album.year === 0 || album.year < f.minYear)) return false;
  if (f.maxYear !== null && (album.year === 0 || album.year > f.maxYear)) return false;
  return true;
}

export function applyFilter(albums: Album[], f: Filter, listenedIds: Set<number>): Album[] {
  return albums.filter((a) => matches(a, f) && !(f.excludeListened && listenedIds.has(a.id)));
}

export function pickRandom(pool: Album[], avoidId?: number, rnd: () => number = Math.random): Album | null {
  if (pool.length === 0) return null;
  const candidates = pool.length > 1 && avoidId !== undefined ? pool.filter((a) => a.id !== avoidId) : pool;
  return candidates[Math.floor(rnd() * candidates.length)];
}

export function facets(albums: Album[]) {
  const genres = new Set<string>();
  const formats = new Set<string>();
  for (const a of albums) {
    a.genres.forEach((g) => genres.add(g));
    a.styles.forEach((s) => genres.add(s));
    a.formats.forEach((x) => formats.add(x));
  }
  const sort = (s: Set<string>) => [...s].sort((a, b) => a.localeCompare(b));
  return { genres: sort(genres), formats: sort(formats) };
}
