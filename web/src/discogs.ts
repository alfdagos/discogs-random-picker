import type { Album } from './types';

const API = 'https://api.discogs.com';
const PER_PAGE = 100;
const MAX_RETRIES = 5;

export class DiscogsError extends Error {}

const sleep = (ms: number) => new Promise((r) => setTimeout(r, ms));
const stripDisambiguation = (n: string) => n.replace(/\s\(\d+\)$/, '');

/* eslint-disable @typescript-eslint/no-explicit-any */
export function parseRelease(r: any): Album {
  const b = r.basic_information ?? r;
  return {
    id: Number(b.id ?? r.id ?? 0),
    title: String(b.title ?? 'Senza titolo'),
    artists: (b.artists ?? []).map((a: any) => stripDisambiguation(String(a.name))).filter(Boolean),
    year: Number(b.year) || 0,
    formats: [...new Set<string>((b.formats ?? []).map((f: any) => String(f.name)))],
    genres: b.genres ?? [],
    styles: b.styles ?? [],
    cover: b.cover_image || b.thumb || '',
  };
}

async function getJson(url: string, signal?: AbortSignal): Promise<any> {
  for (let attempt = 0; ; attempt++) {
    let res: Response;
    try {
      res = await fetch(url, { signal });
    } catch (e) {
      if ((e as Error).name === 'AbortError') throw e;
      throw new DiscogsError('Errore di rete: controlla la connessione.');
    }
    if (res.status === 429 && attempt < MAX_RETRIES) {
      // Authenticated access is limited to ~60 requests/minute.
      const wait = Number(res.headers.get('Retry-After')) || 8;
      await sleep(wait * 1000);
      continue;
    }
    if (res.status === 404) throw new DiscogsError('Utente non trovato. Controlla lo username.');
    if (res.status === 401 || res.status === 403)
      throw new DiscogsError('Token Discogs non valido o scaduto.');
    if (res.status === 429) throw new DiscogsError('Troppe richieste a Discogs. Riprova tra un minuto.');
    if (!res.ok) throw new DiscogsError(`Discogs ha risposto con errore ${res.status}.`);
    return res.json();
  }
}

/** Downloads the whole public collection (folder 0 = "All"), de-duplicated by release id. */
export async function fetchCollection(
  username: string,
  token: string,
  onProgress: (loaded: number, total: number) => void,
  signal?: AbortSignal,
): Promise<Album[]> {
  const byId = new Map<number, Album>();
  let page = 1;
  let pages = 1;
  do {
    const url = `${API}/users/${encodeURIComponent(username)}/collection/folders/0/releases?page=${page}&per_page=${PER_PAGE}${token ? `&token=${encodeURIComponent(token)}` : ''}`;
    const data = await getJson(url, signal);
    pages = data.pagination?.pages ?? 1;
    for (const r of data.releases ?? []) {
      const a = parseRelease(r);
      if (a.id) byId.set(a.id, a);
    }
    onProgress(byId.size, data.pagination?.items ?? byId.size);
    page++;
  } while (page <= pages);
  return [...byId.values()];
}
