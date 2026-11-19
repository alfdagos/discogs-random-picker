# Discogs Random Picker — Web

Versione web mobile-first della CLI Java: pesca un disco a caso dalla tua collezione Discogs, filtra, registra gli ascolti, guarda le statistiche ed esporta lo storico. **Nessun login né form**: username e token Discogs sono incorporati nel build tramite variabili d'ambiente (app personale).

## Stack
Vite + TypeScript vanilla (zero dipendenze runtime, ~7 KB gzip di JS). Hugo è stato scartato perché genera siti di contenuto statico, mentre qui serve un'app interattiva lato client.

- Collezione scaricata dall'API Discogs con il tuo token e messa in cache in `localStorage`.
- Storico e filtri salvati solo nel browser.
- PWA completa: installabile (Android/desktop dal pulsante in Opzioni, iOS da Condividi → Aggiungi a Home), icone PNG e maskable, shell precaricata dal service worker per l'avvio offline, copertine in cache (stale-while-revalidate), aggiornamento automatico alla nuova versione.
- Export storico: CSV, Markdown, HTML, JSON.

## Configurazione
Copia `.env.example` in `.env.local` (ignorato da git) e imposta `VITE_DISCOGS_USER` e `VITE_DISCOGS_TOKEN`. Per GitHub Pages crea i secret di repository `DISCOGS_USER` e `DISCOGS_TOKEN`. Attenzione: il token finisce nel JavaScript pubblicato, quindi è leggibile da chiunque apra il sito.

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
- Con token Discogs consente ~60 richieste/minuto; il primo caricamento scarica 100 dischi per richiesta e gestisce i 429 con retry automatico.
- Se le copertine non compaiono, Discogs non ne ha per quel disco: viene mostrato un disco segnaposto.
