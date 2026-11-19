export interface Album {
  id: number;
  title: string;
  artists: string[];
  year: number; // 0 = unknown
  formats: string[];
  genres: string[];
  styles: string[];
  cover: string;
}

export interface HistoryEntry {
  uid: string;
  album: Album;
  listenedAt: string; // ISO
}

export interface Filter {
  genre: string;
  format: string;
  artist: string;
  minYear: number | null;
  maxYear: number | null;
  excludeListened: boolean;
}

export const emptyFilter = (): Filter => ({
  genre: '',
  format: '',
  artist: '',
  minYear: null,
  maxYear: null,
  excludeListened: false,
});
