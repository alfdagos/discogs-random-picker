import { createHash } from 'node:crypto';
import { readdirSync, readFileSync, writeFileSync } from 'node:fs';
import { join } from 'node:path';
import { defineConfig, type Plugin } from 'vitest/config';

// Fills the service worker's precache list with the built, hashed assets so the
// whole app shell works offline after the first visit.
function pwaPrecache(): Plugin {
  let outDir = 'dist';
  return {
    name: 'pwa-precache',
    apply: 'build',
    configResolved(c) { outDir = c.build.outDir; },
    closeBundle() {
      const assets = readdirSync(join(outDir, 'assets')).map((f) => `./assets/${f}`);
      const statics = ['./index.html', './manifest.webmanifest', './icon.svg', './icon-192.png', './icon-512.png', './apple-touch-icon.png'];
      const urls = [...statics, ...assets];
      const hash = createHash('sha1').update(assets.join('|')).update(readFileSync(join(outDir, 'index.html'))).digest('hex').slice(0, 10);
      const swPath = join(outDir, 'sw.js');
      const sw = readFileSync(swPath, 'utf8')
        .replace("'__PRECACHE__'", JSON.stringify(urls))
        .replace("'__VERSION__'", JSON.stringify(hash));
      writeFileSync(swPath, sw);
    },
  };
}

// base './' keeps asset URLs relative, so the build works under
// https://<user>.github.io/<repo>/ without extra configuration.
export default defineConfig({
  base: './',
  plugins: [pwaPrecache()],
  build: { target: 'es2020', sourcemap: false },
  test: { environment: 'node' },
});
