import './style.css';
import { fetchCollection, DiscogsError } from './discogs';
import { applyFilter, artistLine, facets, pickRandom } from './filter';
import { computeStats, type Ranked } from './stats';
import { download, esc, toCSV, toHTML, toJSON, toMarkdown } from './export';
import { store } from './store';
import { config } from './config';
import { emptyFilter, type Album, type Filter } from './types';

type Tab = 'pick' | 'history' | 'stats' | 'settings';

const state = {
  tab: 'pick' as Tab,
  user: config.user,
  collection: [] as Album[],
  fetchedAt: '',
  loading: null as null | { loaded: number; total: number },
  error: '',
  current: null as Album | null,
  filter: store.getFilter(),
  history: store.getHistory(),
  query: '',
};

const $view = document.getElementById('view')!;
const $badge = document.getElementById('user-badge')!;
let abort: AbortController | null = null;

/* ---------- helpers ---------- */
const listenedIds = () => new Set(state.history.map((h) => h.album.id));
const pool = () => applyFilter(state.collection, state.filter, listenedIds());
const fmtDate = (iso: string) =>
  new Date(iso).toLocaleDateString('it-IT', { day: 'numeric', month: 'short', year: 'numeric' });

function toast(msg: string) {
  const t = document.getElementById('toast')!;
  t.textContent = msg;
  t.classList.add('show');
  setTimeout(() => t.classList.remove('show'), 2200);
}

const img = (src: string, cls = '') =>
  src ? `<img class="${cls}" src="${esc(src)}" alt="" loading="lazy" referrerpolicy="no-referrer" onerror="this.replaceWith(Object.assign(document.createElement('div'),{className:'ph'}))">` : '<div class="ph"></div>';

/* ---------- data loading ---------- */
async function loadCollection(force = false) {
  if (!state.user) return;
  const cached = !force && store.getCollection(state.user);
  if (cached) {
    state.collection = cached.albums;
    state.fetchedAt = cached.fetchedAt;
    render();
    return;
  }
  abort?.abort();
  abort = new AbortController();
  state.loading = { loaded: 0, total: 0 };
  state.error = '';
  render();
  try {
    const albums = await fetchCollection(
      state.user,
      config.token,
      (loaded, total) => {
        state.loading = { loaded, total };
        const bar = document.getElementById('progress');
        if (bar) {
          bar.style.width = `${total ? Math.round((loaded / total) * 100) : 0}%`;
          document.getElementById('progress-label')!.textContent = `${loaded} / ${total} dischi`;
        }
      },
      abort.signal,
    );
    state.collection = albums;
    state.fetchedAt = new Date().toISOString();
    if (!store.setCollection({ user: state.user, fetchedAt: state.fetchedAt, albums })) {
      toast('Collezione troppo grande per la cache offline');
    }
  } catch (e) {
    if ((e as Error).name === 'AbortError') return;
    state.error = e instanceof DiscogsError ? e.message : 'Errore imprevisto durante il caricamento.';
  } finally {
    state.loading = null;
    render();
  }
}

/* ---------- views ---------- */
function loaderView(): string {
  const l = state.loading!;
  const pct = l.total ? Math.round((l.loaded / l.total) * 100) : 0;
  return `<div class="card loader"><div class="disc"></div>
    <h2>Carico la tua collezione…</h2>
    <p class="muted" id="progress-label">${l.loaded} / ${l.total} dischi</p>
    <div class="bar"><i id="progress" style="width:${pct}%"></i></div>
    <p class="muted" style="margin-top:12px">Solo al primo avvio: poi la collezione resta in cache.</p>
    <button class="btn small" data-action="cancel">Annulla</button></div>`;
}

function errorView(): string {
  const msg = state.user
    ? state.error
    : 'Configurazione mancante: imposta VITE_DISCOGS_USER e VITE_DISCOGS_TOKEN (vedi web/.env.example).';
  return `<div class="card stack"><h2>Qualcosa non va</h2>
    <p style="color:var(--danger)">${esc(msg)}</p>
    ${state.user ? '<button class="btn primary" data-action="reload">Riprova</button>' : ''}</div>`;
}

function filterPanel(): string {
  const f = state.filter;
  const { genres, formats } = facets(state.collection);
  const opts = (list: string[], sel: string, all: string) =>
    `<option value="">${all}</option>` + list.map((o) => `<option ${o === sel ? 'selected' : ''}>${esc(o)}</option>`).join('');
  const active = JSON.stringify(f) !== JSON.stringify(emptyFilter());
  return `<details class="card" ${active ? 'open' : ''}>
    <summary>Filtri ${active ? '· attivi' : ''}</summary>
    <div class="stack">
      <div class="grid2">
        <label>Genere / stile<select data-filter="genre">${opts(genres, f.genre, 'Tutti')}</select></label>
        <label>Formato<select data-filter="format">${opts(formats, f.format, 'Tutti')}</select></label>
      </div>
      <label>Artista<input type="text" data-filter="artist" value="${esc(f.artist)}" placeholder="Es. Pink Floyd" autocomplete="off"></label>
      <div class="grid2">
        <label>Anno da<input type="number" inputmode="numeric" data-filter="minYear" value="${f.minYear ?? ''}" placeholder="1970"></label>
        <label>Anno a<input type="number" inputmode="numeric" data-filter="maxYear" value="${f.maxYear ?? ''}" placeholder="1979"></label>
      </div>
      <label class="check"><input type="checkbox" data-filter="excludeListened" ${f.excludeListened ? 'checked' : ''}> Escludi dischi già ascoltati</label>
      <button class="btn small" data-action="reset-filter">Azzera filtri</button>
    </div></details>`;
}

function resultView(a: Album): string {
  const heard = state.history.filter((h) => h.album.id === a.id).length;
  const chips = [...a.formats, ...a.genres, ...a.styles].map((c) => `<span class="chip">${esc(c)}</span>`).join('');
  return `<article class="card result">
    <div class="cover-wrap">${a.cover ? img(a.cover, 'cover') : '<div class="disc"></div>'}</div>
    <div class="result-body">
      <div><h2>${esc(a.title)}</h2><div class="artist">${esc(artistLine(a))}</div>
        <p class="muted">${a.year || 'Anno sconosciuto'}</p></div>
      <div class="chips">${heard ? `<span class="chip warn">Già ascoltato ${heard}×</span>` : ''}${chips}</div>
      <button class="btn primary" data-action="listened">✓ Segna come ascoltato</button>
      <div class="row">
        <button class="btn" data-action="pick">↻ Un altro</button>
        <a class="btn" href="https://www.discogs.com/release/${a.id}" target="_blank" rel="noopener">Discogs ↗</a>
      </div>
    </div></article>`;
}

function pickView(): string {
  if (state.loading) return loaderView();
  if (!state.user || state.error) return errorView();
  if (!state.collection.length)
    return `<div class="card empty"><div class="disc"></div><p>La collezione è vuota.</p><button class="btn" data-action="reload">Ricarica</button></div>`;
  const n = pool().length;
  return `<div class="stack">
    <button class="btn primary big" data-action="pick" ${n ? '' : 'disabled'}>🎲 Pesca un disco</button>
    <p class="muted" style="text-align:center;margin:0"><span id="pool-count">${n}</span> dischi disponibili su ${state.collection.length}</p>
    ${state.current ? resultView(state.current) : ''}
    ${filterPanel()}</div>`;
}

function historyView(): string {
  const q = state.query.trim().toLowerCase();
  const items = state.history
    .filter((h) => !q || `${artistLine(h.album)} ${h.album.title}`.toLowerCase().includes(q))
    .sort((a, b) => b.listenedAt.localeCompare(a.listenedAt));
  if (!state.history.length)
    return `<div class="card empty"><div class="disc"></div><p>Nessun ascolto registrato.<br>Pesca un disco e segnalo come ascoltato.</p></div>`;
  return `<div class="stack">
    <input type="text" data-action="search" placeholder="Cerca artista o titolo…" value="${esc(state.query)}" aria-label="Cerca nello storico">
    <ul class="list">${items.map((h) => `<li class="item">${img(h.album.cover)}
      <div><b>${esc(h.album.title)}</b><small>${esc(artistLine(h.album))}</small><small>${fmtDate(h.listenedAt)}</small></div>
      <button class="icon-btn" data-action="remove" data-uid="${h.uid}" aria-label="Elimina">✕</button></li>`).join('') || '<li class="empty">Nessun risultato</li>'}</ul>
    <div class="card"><h3>Esporta</h3><div class="row">
      <button class="btn small" data-export="csv">CSV</button><button class="btn small" data-export="md">Markdown</button>
      <button class="btn small" data-export="html">HTML</button><button class="btn small" data-export="json">JSON</button></div></div>
    <button class="btn danger" data-action="clear-history">Cancella tutto lo storico</button></div>`;
}

function rankView(title: string, list: Ranked[]): string {
  if (!list.length) return '';
  const max = Math.max(...list.map((r) => r.count));
  return `<div class="card"><h3>${title}</h3><div class="rank">${list.map((r) =>
    `<div><span>${esc(r.label)}</span><b>${r.count}</b><i style="width:${Math.max(6, (r.count / max) * 100)}%"></i></div>`).join('')}</div></div>`;
}

function statsView(): string {
  if (!state.history.length)
    return `<div class="card empty"><div class="disc"></div><p>Le statistiche appariranno dopo il primo ascolto.</p></div>`;
  const s = computeStats(state.history, state.collection);
  return `<div class="stack">
    <div class="kpis">
      <div class="kpi"><b>${s.totalListens}</b><span>ascolti</span></div>
      <div class="kpi"><b>${s.uniqueAlbums}</b><span>dischi unici</span></div>
      <div class="kpi"><b>${s.coverage}%</b><span>collezione</span></div></div>
    <p class="muted" style="text-align:center;margin:0">${s.last7Days} ascolti negli ultimi 7 giorni</p>
    ${rankView('Artisti più ascoltati', s.topArtists)}${rankView('Generi', s.genres)}
    ${rankView('Decenni', s.decades)}${rankView('Formati', s.formats)}</div>`;
}

function settingsView(): string {
  return `<div class="stack">
    <div class="card stack"><h3>Collezione di ${esc(state.user)}</h3>
      <p class="muted">${state.collection.length} dischi in cache${state.fetchedAt ? ` · aggiornata il ${fmtDate(state.fetchedAt)}` : ''}</p>
      <button class="btn" data-action="reload" ${state.user ? '' : 'disabled'}>↻ Aggiorna da Discogs</button></div>
    <div class="card"><h3>Dati</h3><p class="muted">Collezione e storico sono salvati solo in questo browser (localStorage).</p></div>
    <button class="btn danger" data-action="logout">Cancella storico e cache locale</button></div>`;
}

/* ---------- render ---------- */
function render() {
  $badge.hidden = !state.user;
  $badge.textContent = state.user;
  document.querySelectorAll<HTMLButtonElement>('.tabbar button').forEach((b) => {
    if (b.dataset.tab === state.tab) b.setAttribute('aria-current', 'page');
    else b.removeAttribute('aria-current');
  });
  const views: Record<Tab, () => string> = { pick: pickView, history: historyView, stats: statsView, settings: settingsView };
  $view.innerHTML = views[state.tab]();
}

function setTab(t: Tab) {
  state.tab = t;
  render();
  window.scrollTo({ top: 0 });
}

/* ---------- actions ---------- */
function doPick() {
  const a = pickRandom(pool(), state.current?.id);
  if (!a) return toast('Nessun disco corrisponde ai filtri');
  state.current = a;
  render();
  $view.querySelector('.result')?.scrollIntoView({ behavior: 'smooth', block: 'nearest' });
}

function markListened() {
  if (!state.current) return;
  state.history.push({ uid: crypto.randomUUID(), album: state.current, listenedAt: new Date().toISOString() });
  store.setHistory(state.history);
  toast('Aggiunto allo storico ✓');
  render();
}

function exportHistory(kind: string) {
  const h = [...state.history].sort((a, b) => b.listenedAt.localeCompare(a.listenedAt));
  const map: Record<string, [string, string, string]> = {
    csv: [toCSV(h), 'text/csv', 'csv'],
    md: [toMarkdown(h), 'text/markdown', 'md'],
    html: [toHTML(h), 'text/html', 'html'],
    json: [toJSON(h), 'application/json', 'json'],
  };
  const [content, mime, ext] = map[kind];
  download(`storico-ascolti.${ext}`, content, mime);
}

$view.addEventListener('click', (e) => {
  const el = (e.target as HTMLElement).closest<HTMLElement>('[data-action],[data-export]');
  if (!el) return;
  if (el.dataset.export) return exportHistory(el.dataset.export);
  switch (el.dataset.action) {
    case 'pick': return doPick();
    case 'listened': return markListened();
    case 'reload': return void loadCollection(true);
    case 'cancel': abort?.abort(); state.loading = null; return render();
    case 'reset-filter':
      state.filter = emptyFilter();
      store.setFilter(state.filter);
      return render();
    case 'remove':
      state.history = state.history.filter((h) => h.uid !== el.dataset.uid);
      store.setHistory(state.history);
      return render();
    case 'clear-history':
      if (confirm('Cancellare tutto lo storico ascolti?')) {
        state.history = [];
        store.setHistory([]);
        render();
      }
      return;
    case 'logout':
      if (confirm('Cancellare storico e cache della collezione da questo dispositivo?')) {
        ['drp:collection', 'drp:history', 'drp:filter'].forEach((k) => { try { localStorage.removeItem(k); } catch { /* ignore */ } });
        Object.assign(state, { collection: [], history: [], current: null, filter: emptyFilter(), tab: 'pick', error: '' });
        void loadCollection(true);
      }
  }
});

$view.addEventListener('input', (e) => {
  const t = e.target as HTMLInputElement;
  if (t.dataset.action === 'search') {
    state.query = t.value;
    const pos = t.selectionStart;
    render();
    const again = $view.querySelector<HTMLInputElement>('[data-action=search]')!;
    again.focus();
    again.setSelectionRange(pos, pos);
    return;
  }
  const key = t.dataset.filter as keyof Filter | undefined;
  if (!key) return;
  const f = state.filter as unknown as Record<string, unknown>;
  if (key === 'minYear' || key === 'maxYear') f[key] = t.value === '' ? null : Number(t.value);
  else if (key === 'excludeListened') f[key] = t.checked;
  else f[key] = t.value;
  store.setFilter(state.filter);
  const c = document.getElementById('pool-count');
  if (c) c.textContent = String(pool().length);
  const btn = $view.querySelector<HTMLButtonElement>('button.big[data-action=pick]');
  if (btn) btn.disabled = pool().length === 0;
});

document.querySelector('.tabbar')!.addEventListener('click', (e) => {
  const b = (e.target as HTMLElement).closest<HTMLElement>('[data-tab]');
  if (b) setTab(b.dataset.tab as Tab);
});

/* ---------- boot ---------- */
render();
void loadCollection();

if ('serviceWorker' in navigator && import.meta.env.PROD) {
  navigator.serviceWorker.register(`${import.meta.env.BASE_URL}sw.js`).catch(() => undefined);
}
