import { defineConfig } from 'vitest/config';

// base './' keeps asset URLs relative, so the build works under
// https://<user>.github.io/<repo>/ without extra configuration.
export default defineConfig({
  base: './',
  build: { target: 'es2020', sourcemap: false },
  test: { environment: 'node' },
});
