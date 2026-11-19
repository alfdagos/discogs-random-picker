import { emptyFilter, type Album, type Filter, type HistoryEntry } from './types';

const K = { coll: 'drp:collection', hist: 'drp:history', filter: 'drp:filter' } as const;

function read<T>(key: string, fallback: T): T {
  try {
    const v = localStorage.getItem(key);
    return v ? (JSON.parse(v) as T) : fallback;
  } catch {
    return fallback;
  }
}
function write(key: string, value: unknown): boolean {
  try {
    localStorage.setItem(key, JSON.stringify(value));
    return true;
  } catch {
    return false;
  }
}

export interface CollectionCache { user: string; fetchedAt: string; albums: Album[] }

export const store = {
  getCollection: (user: string): CollectionCache | null => {
    const c = read<CollectionCache | null>(K.coll, null);
    return c && c.user.toLowerCase() === user.toLowerCase() ? c : null;
  },
  setCollection: (c: CollectionCache) => write(K.coll, c),
  clearCollection: () => { try { localStorage.removeItem(K.coll); } catch { /* ignore */ } },
  getHistory: () => read<HistoryEntry[]>(K.hist, []),
  setHistory: (h: HistoryEntry[]) => write(K.hist, h),
  getFilter: (): Filter => ({ ...emptyFilter(), ...read<Partial<Filter>>(K.filter, {}) }),
  setFilter: (f: Filter) => write(K.filter, f),
};
