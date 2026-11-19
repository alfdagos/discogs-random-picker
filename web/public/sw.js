// Service worker. `__PRECACHE__` and `__VERSION__` are filled in at build time
// by the pwaPrecache plugin in vite.config.ts.
const VERSION = '__VERSION__';
const SHELL = `drp-shell-${VERSION}`;
const IMAGES = 'drp-images-v1';
const PRECACHE = '__PRECACHE__'; // replaced with a JSON array of relative URLs
const MAX_IMAGES = 400;

self.addEventListener('install', (e) => {
  const urls = Array.isArray(PRECACHE) ? PRECACHE : [];
  e.waitUntil(caches.open(SHELL).then((c) => c.addAll(['./', ...urls])).then(() => self.skipWaiting()));
});

self.addEventListener('activate', (e) => {
  e.waitUntil(
    caches.keys()
      .then((keys) => Promise.all(keys.filter((k) => k !== SHELL && k !== IMAGES).map((k) => caches.delete(k))))
      .then(() => self.clients.claim()),
  );
});

async function trimImages() {
  const c = await caches.open(IMAGES);
  const keys = await c.keys();
  await Promise.all(keys.slice(0, Math.max(0, keys.length - MAX_IMAGES)).map((k) => c.delete(k)));
}

// Cover art: show the cached copy immediately, refresh it in the background.
async function coverImage(req) {
  const cache = await caches.open(IMAGES);
  const hit = await cache.match(req);
  const refresh = fetch(req)
    .then((res) => {
      if (res.ok || res.type === 'opaque') cache.put(req, res.clone()).then(trimImages);
      return res;
    })
    .catch(() => hit);
  return hit || refresh;
}

self.addEventListener('fetch', (e) => {
  const req = e.request;
  if (req.method !== 'GET') return;
  const url = new URL(req.url);

  if (url.hostname.endsWith('discogs.com') && req.destination === 'image') {
    e.respondWith(coverImage(req));
    return;
  }
  if (url.origin !== self.location.origin) return; // API calls are never cached here

  if (req.mode === 'navigate') {
    // App shell: fresh when online, cached when offline.
    e.respondWith(fetch(req).catch(() => caches.match('./')));
    return;
  }
  // Same-origin assets are hashed by Vite, so cache-first is safe.
  e.respondWith(
    caches.match(req).then((hit) => hit || fetch(req).then((res) => {
      const copy = res.clone();
      if (res.ok) caches.open(SHELL).then((c) => c.put(req, copy));
      return res;
    })),
  );
});
