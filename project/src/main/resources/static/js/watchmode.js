/* ============================================================
   watchmode.js — Movie / show search via Watchmode API proxy
   ============================================================ */

// Source type labels for the streaming sources section
const SOURCE_LABELS = {
  sub: 'Subscription',
  free: 'Free',
  rent: 'Rent',
  buy: 'Buy',
  tve: 'TV Everywhere',
};

// ── DOM refs ──────────────────────────────────────────────
const form      = document.getElementById('wm-search-form');
const input     = document.getElementById('wm-input');
const btn       = document.getElementById('wm-btn');
const status    = document.getElementById('wm-status');
const results   = document.getElementById('wm-results');
const detail    = document.getElementById('wm-detail');
const detailInner = document.getElementById('wm-detail-inner');

// ── Helpers ───────────────────────────────────────────────
function setStatus(msg) {
  status.textContent = msg;
  status.hidden = !msg;
}

function setLoading(yes) {
  btn.disabled = yes;
  btn.textContent = yes ? '⏳ Searching…' : '🔍 Search';
}

function showResults() {
  results.hidden = false;
  detail.hidden  = true;
}

function closeDetail() {
  detail.hidden  = true;
  results.hidden = false;
}

// ── Search ────────────────────────────────────────────────
async function doSearch() {
  const q = input.value.trim();
  if (!q) { input.focus(); return; }

  setLoading(true);
  setStatus('Searching…');
  results.innerHTML = '';
  showResults();

  try {
    // Calls our Spring proxy — key never leaves the server
    const res  = await fetch(`/api/watchmode/search?q=${encodeURIComponent(q)}`);
    const data = await res.json();

    if (!res.ok) {
      setStatus(data.error || 'Search failed.');
      setLoading(false);
      return;
    }

    const titles = data.title_results || [];
    if (!titles.length) {
      setStatus(`No results found for "${q}".`);
      setLoading(false);
      return;
    }

    setStatus(`${titles.length} result${titles.length > 1 ? 's' : ''} for "${q}"`);
    renderResults(titles);
  } catch (err) {
    setStatus('Error: ' + err.message);
  } finally {
    setLoading(false);
  }
}

// ── Render result cards ───────────────────────────────────
function renderResults(titles) {
  results.innerHTML = titles.map(t => {
    const year      = t.year          ? ` (${t.year})`        : '';
    const type      = t.type          ? capitalise(t.type)     : 'Title';
    const imgSrc    = t.image_url     || '';
    const releaseDate = t.release_date || '';

    return `
      <article class="wm-card" onclick="loadDetail('${t.id}', '${esc(t.name)}')">
        <div class="wm-card-poster">
          ${imgSrc
            ? `<img src="${imgSrc}" alt="${esc(t.name)}" loading="lazy">`
            : `<div class="wm-card-fallback">🎬</div>`}
          <span class="wm-card-type">${type}</span>
        </div>
        <div class="wm-card-meta">
          <h3 class="wm-card-title">${esc(t.name)}${year}</h3>
          ${releaseDate ? `<p class="wm-card-sub">Released: ${releaseDate}</p>` : ''}
          <button class="btn btn-ghost btn-sm mt-2" onclick="event.stopPropagation();loadDetail('${t.id}','${esc(t.name)}')">
            Details &amp; Streaming →
          </button>
        </div>
      </article>`;
  }).join('');
}

// ── Load individual title detail + sources ────────────────
async function loadDetail(id, name) {
  results.hidden = true;
  detail.hidden  = false;
  detailInner.innerHTML = `<p style="font-family:var(--mono);font-size:.85rem;color:var(--text-muted)">Loading "${name}"…</p>`;

  try {
    const [detailRes, sourcesRes] = await Promise.all([
      fetch(`/api/watchmode/title/${id}`),
      fetch(`/api/watchmode/title/${id}/sources`),
    ]);

    const titleData   = await detailRes.json();
    const sourcesData = await sourcesRes.json();

    if (!detailRes.ok) {
      detailInner.innerHTML = `<p style="color:var(--red)">${titleData.error || 'Could not load details.'}</p>`;
      return;
    }

    renderDetail(titleData, Array.isArray(sourcesData) ? sourcesData : []);
  } catch (err) {
    detailInner.innerHTML = `<p style="color:var(--red)">Error: ${err.message}</p>`;
  }
}

// ── Render detail view ────────────────────────────────────
function renderDetail(t, sources) {
  const poster  = t.poster || '';
  const runtime = t.runtime_minutes ? `${t.runtime_minutes} min` : '';
  const rating  = t.user_rating     ? `⭐ ${parseFloat(t.user_rating).toFixed(1)}` : '';
  const genres  = Array.isArray(t.genres_list) ? t.genres_list.join(', ') : '';
  const networks = Array.isArray(t.network_names) ? t.network_names.join(', ') : '';

  // Group sources by type
  const grouped = {};
  for (const s of sources) {
    const label = SOURCE_LABELS[s.type] || capitalise(s.type || 'other');
    if (!grouped[label]) grouped[label] = [];
    grouped[label].push(s);
  }

  const sourcesHtml = Object.keys(grouped).length
    ? Object.entries(grouped).map(([label, items]) => `
        <div class="wm-source-group">
          <p class="eyebrow mb-2">${label}</p>
          <div class="d-flex flex-wrap gap-2">
            ${items.map(s => `
              <a class="wm-source-chip" href="${s.web_url || '#'}" target="_blank" rel="noopener">
                ${s.name}
                ${s.price ? `<span class="wm-source-price">$${s.price}</span>` : ''}
              </a>`).join('')}
          </div>
        </div>`).join('')
    : `<p style="font-family:var(--mono);font-size:.82rem;color:var(--text-muted)">No streaming sources found.</p>`;

  detailInner.innerHTML = `
    <div class="wm-detail-grid">
      <!-- Poster -->
      <div class="wm-detail-poster">
        ${poster
          ? `<img src="${poster}" alt="${esc(t.title)}" style="width:100%;border-radius:.5rem">`
          : `<div class="wm-card-fallback" style="min-height:300px;font-size:4rem">🎬</div>`}
      </div>

      <!-- Info -->
      <div class="wm-detail-info">
        <h2 style="font-family:var(--display);font-size:clamp(1.6rem,3vw,2.8rem);margin-bottom:.5rem">
          ${esc(t.title)}
          ${t.year ? `<span style="opacity:.5;font-size:1.2rem"> (${t.year})</span>` : ''}
        </h2>

        <div class="d-flex flex-wrap gap-2 mb-3">
          ${rating  ? `<span class="card-badge">${rating}</span>`        : ''}
          ${runtime ? `<span class="card-badge">⏱ ${runtime}</span>`     : ''}
          ${genres  ? `<span class="card-badge">🎭 ${genres}</span>`     : ''}
          ${networks? `<span class="card-badge">📺 ${networks}</span>`   : ''}
          ${t.type  ? `<span class="card-badge">${capitalise(t.type)}</span>` : ''}
        </div>

        ${t.plot_overview
          ? `<p style="color:var(--text-muted);line-height:1.75;margin-bottom:1.5rem">${esc(t.plot_overview)}</p>`
          : ''}

        <h3 class="eyebrow mb-3">Where to Watch</h3>
        ${sourcesHtml}
      </div>
    </div>`;
}

// ── Utilities ─────────────────────────────────────────────
function capitalise(s) {
  return s ? s.charAt(0).toUpperCase() + s.slice(1) : '';
}

/** HTML-escape a string to prevent XSS in innerHTML */
function esc(str) {
  if (!str) return '';
  return str
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
    .replace(/"/g, '&quot;')
    .replace(/'/g, '&#39;');
}

// ── Boot ──────────────────────────────────────────────────
document.addEventListener('DOMContentLoaded', () => {
  form.addEventListener('submit', doSearch);

  // Pre-fill from URL param: /movie-search?q=Matrix
  const urlQ = new URLSearchParams(window.location.search).get('q');
  if (urlQ) {
    input.value = urlQ;
    doSearch();
  } else {
    input.focus();
  }
});
