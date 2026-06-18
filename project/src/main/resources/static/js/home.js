/* ============================================================
   home.js — StreamFlixTv home page logic (TMDB edition)
   ============================================================ */

// ── Genre icon map ─────────────────────────────────────────
const GENRE_ICONS = {
  action:'🔥', adventure:'🗺️', animation:'🎨', anime:'🎌',
  comedy:'😂', crime:'🕵️', documentary:'📽️', drama:'🎭',
  fantasy:'✨', horror:'👻', music:'🎵', mystery:'🔮',
  romance:'❤️', 'sci-fi':'🚀', 'science fiction':'🚀',
  thriller:'😨', western:'🤠', sport:'⚽', history:'📜',
  family:'👨‍👩‍👧', biography:'📖', movie:'🎬', default:'🎬',
  'action/adventure':'🗺️', adventure:'🗺️'
};
function genreIcon(name) {
  return GENRE_ICONS[(name || '').toLowerCase().trim()] || GENRE_ICONS.default;
}

// ── Famous movies loaded from TMDB API ─────────────────────
let famousMoviesCache = [];

async function loadHome() {
  const data = await apiFetch('/api/videos/tmdb/home-data');
  window._homeData = data || {};

  renderVideos('trending-row', data.trending || []);
  renderVideos('toprated-row', data.topRated || []);
  renderVideos('recent-row',   data.recent   || []);
  renderVideos('featured-slider', data.featured || data.trending || []);

  setupTrailerTabs();
  await loadFamousMovies('all');
}

async function loadFamousMovies(genre) {
  famousMoviesCache = await apiFetch(`/api/videos/tmdb/famous-movies?genre=${encodeURIComponent(genre)}`) || [];
  renderFamousMovies(famousMoviesCache, genre);
}

function renderFamousMovies(movies, genre) {
  const grid = document.getElementById('trailers-grid');
  if (!grid) return;
  grid.innerHTML = movies.map(m => movieCardTmdb(m, genre)).join('');
}

// ── TMDB-aware video card for content rows ──────────────────
function renderVideos(id, videos) {
  const row = document.getElementById(id);
  if (!row) return;
  if (videos.length) {
    row.innerHTML = videos.map(v => videoCard(v)).join('');
    enableDragScroll(row);
  } else {
    row.innerHTML = `<div style="font-family:var(--mono);font-size:.85rem;color:var(--text-muted);padding:2rem 0">
      No titles found. Try again later.
    </div>`;
  }
}

function isTmdbVideo(video) {
  return video && (String(video.videoId || '').startsWith('tmdb-') || video.mediaType === 'movie');
}

function videoCard(video) {
  if (isTmdbVideo(video)) return movieCardTmdb(video);
  return localVideoCard(video);
}

function localVideoCard(video) {
  const image   = video.thumbnailUrl ? `/uploads/${video.thumbnailUrl}` : '';
  const title   = video.title        || 'Untitled';
  const genre   = video.genre        || 'Movie';
  const year    = video.releaseYear  ? ' · ' + video.releaseYear        : '';
  const rating  = video.rating       ? `⭐ ${parseFloat(video.rating).toFixed(1)}` : '';
  const dur     = video.duration     ? fmtDuration(video.duration)      : '';

  let posterHtml;
  if (image) {
    posterHtml = `<img src="${image}" alt="${title}" loading="lazy">`;
  } else {
    posterHtml = `<div class="video-poster-fallback"><span>${title}</span></div>`;
  }

  return `
    <article class="video-card">
      <a class="video-poster" href="/watch/${video.videoId}" aria-label="Play ${title}">
        ${posterHtml}
        <div class="card-hover-overlay">
          <div class="card-hover-meta">
            ${rating ? `<span class="card-badge">${rating}</span>` : ''}
            ${genre  ? `<span class="card-badge">${genre}</span>`  : ''}
            ${dur    ? `<span class="card-badge">⏱ ${dur}</span>`  : ''}
          </div>
          <span class="card-play-btn">▶ Watch Now</span>
        </div>
      </a>
      <div class="video-meta">
        <p class="video-meta-tag">${genre}${year}${rating ? ' · ' + rating : ''}</p>
        <h3>${title}</h3>
        <div class="video-actions">
          <a class="btn btn-stream btn-sm" href="/watch/${video.videoId}">▶ Play</a>
          <a class="btn btn-ghost btn-sm"  href="/videos/${video.videoId}">Details</a>
        </div>
      </div>
    </article>`;
}

function movieCardTmdb(video, inFamousSection = false) {
  const title   = video.title || 'Untitled';
  const poster  = video.poster || video.thumbnailUrl || '';
  const genre   = video.genre || 'Movie';
  const year    = video.year || video.releaseYear ? ' · ' + (video.year || video.releaseYear) : '';
  const rating  = video.rating ? `⭐ ${parseFloat(video.rating).toFixed(1)}` : '';
  const desc    = video.desc || video.description || '';
  const tmdbId  = video.tmdbId || (String(video.videoId || '').replace('tmdb-', ''));
  const href    = `/movie/${tmdbId}`;
  const playHref = `https://vidlink.pro/movie/${tmdbId}`;

  let posterHtml;
  if (poster) {
    posterHtml = `<img src="${poster}" alt="${title}" loading="lazy"
                  onerror="this.parentElement.classList.add('video-poster-fallback');this.style.display='none'">`;
  } else {
    posterHtml = `<div class="video-poster-fallback"><span>${genreIcon(genre)}</span></div>`;
  }

  return `
    <article class="video-card">
      <a class="video-poster" href="${href}" aria-label="${title}" target="_self">
        ${posterHtml}
        <div class="card-hover-overlay">
          <div class="card-hover-meta">
            ${rating ? `<span class="card-badge">${rating}</span>` : ''}
            ${genre  ? `<span class="card-badge">${genreIcon(genre)} ${genre}</span>`  : ''}
          </div>
          <span class="card-play-btn">▶ Play Now</span>
        </div>
      </a>
      <div class="video-meta">
        <p class="video-meta-tag">${genre}${year}${rating ? ' · ' + rating : ''}</p>
        <h3>${title}</h3>
        <p class="video-meta-tag" style="margin-top:.35rem;color:var(--text-dim);font-size:.8rem;line-height:1.5">
          ${desc.length > 110 ? desc.substring(0,110) + '…' : desc}
        </p>
        <div class="video-actions">
          <a class="btn btn-stream btn-sm" href="${playHref}" target="_blank">▶ Stream</a>
          <a class="btn btn-ghost btn-sm"  href="${href}">📋 Details</a>
        </div>
      </div>
    </article>`;
}

function fmtDuration(mins) {
  if (!mins) return '';
  const h = Math.floor(mins / 60);
  const m = mins % 60;
  return h ? `${h}h ${m}m` : `${m}m`;
}

// ── Trailer genre tab filter ───────────────────────────────
function setupTrailerTabs() {
  const container = document.getElementById('trailer-genre-tabs');
  if (!container) return;
  container.addEventListener('click', async e => {
    const btn = e.target.closest('.tab-btn[data-genre]');
    if (!btn) return;
    container.querySelectorAll('.tab-btn').forEach(b => b.classList.remove('active'));
    btn.classList.add('active');
    await loadFamousMovies(btn.dataset.genre);
  });
}

// ── Continue watching ──────────────────────────────────────
async function loadContinueWatching() {
  try {
    const items = await apiFetch('/api/history/continue');
    if (!items.length) return;
    document.getElementById('continue-section').hidden = false;
    const row = document.getElementById('continue-row');
    row.innerHTML = items.map(continueCard).join('');
    enableDragScroll(row);
  } catch (_) {}
}

function continueCard(item) {
  const pct   = Math.min(100, Math.round(item.watchPercentage || 0));
  const title = item.title || 'Untitled';
  return `
    <article class="video-card">
      <a class="video-poster" href="/watch/${item.videoId}" aria-label="Continue ${title}">
        <div class="video-poster-fallback"><span>${title}</span></div>
        <div class="video-poster-play"><span>▶</span></div>
        <div style="position:absolute;bottom:0;left:0;right:0;height:4px;background:rgba(255,255,255,.15)">
          <div style="width:${pct}%;height:100%;background:var(--acid-green)"></div>
        </div>
      </a>
      <div class="video-meta">
        <p class="video-meta-tag">${pct}% watched</p>
        <h3>${title}</h3>
        <div class="video-actions">
          <a class="btn btn-stream btn-sm" href="/watch/${item.videoId}">▶ Resume</a>
        </div>
      </div>
    </article>`;
}

// ── Categories (kept for layout; no TMDB category fetch yet) ──
async function loadCategories() {
  const target = document.getElementById('category-row');
  if (!target) return;
  target.innerHTML = `
    <button class="category-chip-icon" data-category="Action">
      <span class="cat-icon">🔥</span><span class="cat-name">Action</span>
    </button>
    <button class="category-chip-icon" data-category="Drama">
      <span class="cat-icon">🎭</span><span class="cat-name">Drama</span>
    </button>
    <button class="category-chip-icon" data-category="Sci-Fi">
      <span class="cat-icon">🚀</span><span class="cat-name">Sci-Fi</span>
    </button>
    <button class="category-chip-icon" data-category="Comedy">
      <span class="cat-icon">😂</span><span class="cat-name">Comedy</span>
    </button>
    <button class="category-chip-icon" data-category="Horror">
      <span class="cat-icon">👻</span><span class="cat-name">Horror</span>
    </button>
    <button class="category-chip-icon" data-category="Animation">
      <span class="cat-icon">🎨</span><span class="cat-name">Animation</span>
    </button>`;
  target.querySelectorAll('[data-category]').forEach(btn =>
    btn.addEventListener('click', () => triggerSearch(btn.dataset.category)));
}

// ── Drag-to-scroll for content rows ────────────────────────
function enableDragScroll(el) {
  if (!el || el.dataset.dragEnabled) return;
  el.dataset.dragEnabled = 'true';

  let isDown = false, startX = 0, scrollLeft = 0;

  el.addEventListener('mousedown', e => {
    if (e.target.closest('a,button')) return;
    isDown    = true;
    startX    = e.pageX - el.offsetLeft;
    scrollLeft = el.scrollLeft;
    el.style.cursor = 'grabbing';
    el.style.userSelect = 'none';
  });

  el.addEventListener('mouseleave', () => { isDown = false; el.style.cursor = ''; });
  el.addEventListener('mouseup',    () => { isDown = false; el.style.cursor = ''; el.style.userSelect = ''; });

  el.addEventListener('mousemove', e => {
    if (!isDown) return;
    e.preventDefault();
    const x    = e.pageX - el.offsetLeft;
    const walk = (x - startX) * 1.4;
    el.scrollLeft = scrollLeft - walk;
  });

  let touchStartX = 0, touchScrollLeft = 0;
  el.addEventListener('touchstart', e => {
    touchStartX    = e.touches[0].pageX;
    touchScrollLeft = el.scrollLeft;
  }, { passive: true });
  el.addEventListener('touchmove', e => {
    const dx = touchStartX - e.touches[0].pageX;
    el.scrollLeft = touchScrollLeft + dx;
  }, { passive: true });
}

// ── Section tab switcher ────────────────────────────────────
document.addEventListener('click', e => {
  const btn = e.target.closest('.tab-btn[data-tab]');
  if (!btn) return;
  const section = btn.closest('section');
  if (!section) return;
  section.querySelectorAll('.tab-btn[data-tab]').forEach(b => b.classList.remove('active'));
  section.querySelectorAll('.tab-panel').forEach(p => { p.hidden = true; });
  btn.classList.add('active');
  const panel = document.getElementById(btn.dataset.tab);
  if (panel) panel.hidden = false;
});

// ── Full-section search ────────────────────────────────────
async function triggerSearch(query) {
  const section = document.getElementById('search-results-section');
  if (!section) return;
  document.getElementById('search-query-label').textContent = `"${query}"`;
  section.hidden = false;
  section.scrollIntoView({ behavior: 'smooth', block: 'start' });

  const row = document.getElementById('search-row');
  row.innerHTML = `<p style="font-family:var(--mono);font-size:.82rem;color:var(--text-muted);padding:1rem 0">Searching TMDB…</p>`;

  try {
    const videos = await apiFetch(`/api/tmdb/search/multi?query=${encodeURIComponent(query)}`);
    const results = videos.results || [];
    if (results.length) {
      row.innerHTML = results.map(v => movieCardTmdb({
        title: v.title || v.name,
        poster: v.poster_path ? 'https://image.tmdb.org/t/p/w500' + v.poster_path : null,
        genre: (v.media_type || 'movie').toUpperCase(),
        year: v.release_date ? v.release_date.substring(0,4) : (v.first_air_date ? v.first_air_date.substring(0,4) : ''),
        rating: v.vote_average,
        desc: v.overview,
        tmdbId: v.id,
        mediaType: v.media_type
      })).join('');
      enableDragScroll(row);
    } else {
      row.innerHTML = `<p style="font-family:var(--mono);font-size:.82rem;color:var(--text-muted);padding:1rem 0">No results for "${query}".</p>`;
    }
  } catch (err) {
    row.innerHTML = `<p style="color:var(--red);font-family:var(--mono);font-size:.82rem;padding:1rem 0">${err.message}</p>`;
  }
}

function clearSearch() {
  const section = document.getElementById('search-results-section');
  if (section) section.hidden = true;
  const row = document.getElementById('search-row');
  if (row) row.innerHTML = '';
  const input = document.getElementById('nav-search-input');
  if (input) input.value = '';
  hideSuggestions();
}

// ── Live search suggestions ────────────────────────────────
let suggestDebounce  = null;
let searchDebounce   = null;

async function showSuggestions(query) {
  const box = document.getElementById('search-suggestions');
  if (!box) return;
  const q = query.toLowerCase().trim();
  if (!q) { hideSuggestions(); return; }

  box.innerHTML = `<div class="suggest-empty">Searching…</div>`;
  box.hidden = false;

  try {
    const res = await apiFetch(`/api/tmdb/search/multi?query=${encodeURIComponent(q)}`);
    const matches = (res.results || []).slice(0, 7);
    if (!matches.length) {
      box.innerHTML = `<div class="suggest-empty">No results for "<strong>${query}</strong>"</div>`;
      return;
    }
    const rx = new RegExp(`(${q.replace(/[.*+?^${}()|[\]\\]/g, '\\$&')})`, 'gi');
    box.innerHTML = matches.map(v => {
      const img    = v.poster_path ? 'https://image.tmdb.org/t/p/w500' + v.poster_path : '';
      const title  = v.title || v.name || 'Untitled';
      const genre  = (v.media_type || 'movie').toUpperCase();
      const year   = v.release_date ? ' · ' + v.release_date.substring(0,4) : (v.first_air_date ? ' · ' + v.first_air_date.substring(0,4) : '');
      const rating = v.vote_average ? `⭐ ${parseFloat(v.vote_average).toFixed(1)}` : '';
      const hl     = title.replace(rx, '<mark>$1</mark>');
      const href   = `/movie/${v.id}`;
      return `
        <a class="suggest-item" href="${href}">
          <div class="suggest-thumb">
            ${img ? `<img src="${img}" alt="${title}" loading="lazy">` : `<div class="suggest-thumb-fallback">${genreIcon(genre)}</div>`}
          </div>
          <div class="suggest-info">
            <span class="suggest-title">${hl}</span>
            <span class="suggest-meta">${genre}${year}${rating ? ' · ' + rating : ''}</span>
          </div>
          <span class="suggest-play">▶</span>
        </a>`;
    }).join('');
    const safeQuery = query.replace(/\\/g, '\\\\').replace(/'/g, "\\'");
    box.innerHTML += `
      <div class="suggest-footer"
           onclick="triggerSearch('${safeQuery}');hideSuggestions()">
        🔍 See all results for "<strong>${query}</strong>"
      </div>`;
  } catch (_) {
    box.innerHTML = `<div class="suggest-empty">Search failed. Try again.</div>`;
  }
}

function hideSuggestions() {
  const box = document.getElementById('search-suggestions');
  if (box) box.hidden = true;
}

function setupSearch() {
  const input = document.getElementById('nav-search-input');
  if (!input) return;

  input.addEventListener('focus', () => {
    if (input.value.trim()) showSuggestions(input.value.trim());
  });

  input.addEventListener('input', () => {
    clearTimeout(suggestDebounce);
    clearTimeout(searchDebounce);
    const q = input.value.trim();
    if (!q) { hideSuggestions(); clearSearch(); return; }
    suggestDebounce = setTimeout(() => showSuggestions(q), 150);
    searchDebounce  = setTimeout(() => triggerSearch(q),   400);
  });

  input.addEventListener('keydown', e => {
    if (e.key === 'Enter') {
      e.preventDefault();
      const q = input.value.trim();
      if (q) { hideSuggestions(); triggerSearch(q); }
    }
    if (e.key === 'Escape') { hideSuggestions(); input.blur(); }
  });

  document.addEventListener('click', e => {
    if (!e.target.closest('.search-wrapper')) hideSuggestions();
  });
}

// ── Boot ────────────────────────────────────────────────────
document.addEventListener('DOMContentLoaded', () => {
  loadHome().catch(err => console.error('loadHome:', err));
  setupSearch();
});
