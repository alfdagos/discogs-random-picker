# Discogs Random Picker — Web

Versione web mobile-first della CLI Java: pesca un disco a caso dalla tua collezione Discogs, filtra, registra gli ascolti, guarda le statistiche ed esporta lo storico. **Nessuna autenticazione**: serve solo lo username e la collezione deve essere pubblica.

## Stack
Vite + TypeScript vanilla (zero dipendenze runtime, ~7 KB gzip di JS). Hugo è stato scartato perché genera siti di contenuto statico, mentre qui serve un'app interattiva lato client.

- Collezione scaricata dall'API pubblica di Discogs (senza token) e messa in cache in `localStorage`.
- Storico, filtri e username salvati solo nel browser.
- PWA: installabile, funziona offline sui dati già in cache.
- Export storico: CSV, Markdown, HTML, JSON.

## Sviluppo
```bash
cd web
npm install
npm run dev      # http://localhost:5173
npm test
npm run build    # output in web/dist
```

## Deploy su GitHub Pages
1. Su GitHub: **Settings → Pages → Source: GitHub Actions**.
2. Fai merge su `main`: il workflow `.github/workflows/pages.yml` esegue test, build e pubblica.
3. L'app sarà su `https://<utente>.github.io/discogs-random-picker/` (i percorsi relativi funzionano con qualsiasi nome repo).

## Note
- L'accesso anonimo a Discogs è limitato a ~25 richieste/minuto: il primo caricamento di collezioni grandi (100 dischi per richiesta) può richiedere qualche secondo; l'app gestisce i 429 con retry automatico.
- Se le copertine non compaiono, è Discogs che non le espone ai client anonimi: viene mostrato un disco segnaposto.
