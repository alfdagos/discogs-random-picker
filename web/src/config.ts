// Personal app: credentials are injected at build time (see web/.env.example).
export const config = {
  user: (import.meta.env.VITE_DISCOGS_USER as string | undefined)?.trim() ?? '',
  token: (import.meta.env.VITE_DISCOGS_TOKEN as string | undefined)?.trim() ?? '',
};
