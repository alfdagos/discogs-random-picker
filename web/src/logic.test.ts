import { describe, expect, it } from 'vitest';
import { applyFilter, facets, pickRandom } from './filter';
import { parseRelease } from './discogs';
import { computeStats } from './stats';
import { toCSV } from './export';
import { emptyFilter, type Album, type HistoryEntry } from './types';

const mk = (id: number, over: Partial<Album> = {}): Album => ({
  id, title: `T${id}`, artists: ['Pink Floyd'], year: 1973, formats: ['Vinyl'], genres: ['Rock'], styles: ['Prog Rock'], cover: '', ...over,
});

describe('filter', () => {
  const all = [mk(1), mk(2, { year: 1985, genres: ['Pop'], styles: [], artists: ['Prince'], formats: ['CD'] }), mk(3, { year: 0 })];
  it('filters by genre/style, format, artist, years', () => {
    const f = emptyFilter();
    expect(applyFilter(all, { ...f, genre: 'Prog Rock' }, new Set()).map((a) => a.id)).toEqual([1, 3]);
    expect(applyFilter(all, { ...f, format: 'CD' }, new Set()).map((a) => a.id)).toEqual([2]);
    expect(applyFilter(all, { ...f, artist: 'prin' }, new Set()).map((a) => a.id)).toEqual([2]);
    expect(applyFilter(all, { ...f, minYear: 1980, maxYear: 1989 }, new Set()).map((a) => a.id)).toEqual([2]);
  });
  it('excludes listened', () => {
    expect(applyFilter(all, { ...emptyFilter(), excludeListened: true }, new Set([1, 2])).map((a) => a.id)).toEqual([3]);
  });
  it('pickRandom avoids last pick and handles empty', () => {
    expect(pickRandom([])).toBeNull();
    expect(pickRandom([mk(1), mk(2)], 1, () => 0)!.id).toBe(2);
    expect(pickRandom([mk(1)], 1)!.id).toBe(1);
  });
  it('facets', () => expect(facets(all).formats).toEqual(['CD', 'Vinyl']));
});

describe('parseRelease', () => {
  it('maps Discogs payload and strips disambiguation', () => {
    const a = parseRelease({ id: 9, basic_information: { id: 9, title: 'X', year: 1999, artists: [{ name: 'Nirvana (2)' }],
      formats: [{ name: 'Vinyl' }, { name: 'Vinyl' }], genres: ['Rock'], thumb: 't.jpg' } });
    expect(a).toMatchObject({ id: 9, artists: ['Nirvana'], formats: ['Vinyl'], cover: 't.jpg', year: 1999 });
  });
});

describe('stats & export', () => {
  const h: HistoryEntry[] = [
    { uid: 'a', album: mk(1), listenedAt: new Date().toISOString() },
    { uid: 'b', album: mk(1), listenedAt: '2000-01-01T00:00:00Z' },
  ];
  it('computes stats', () => {
    const s = computeStats(h, [mk(1), mk(2)]);
    expect(s).toMatchObject({ totalListens: 2, uniqueAlbums: 1, coverage: 50, last7Days: 1 });
    expect(s.decades[0].label).toBe('1970s');
  });
  it('escapes csv quotes', () => {
    const csv = toCSV([{ uid: 'a', album: mk(1, { title: 'Say "Hi"' }), listenedAt: new Date().toISOString() }]);
    expect(csv).toContain('"Say ""Hi"""');
  });
});
