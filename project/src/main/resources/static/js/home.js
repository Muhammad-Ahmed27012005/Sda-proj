/* ============================================================
   home.js — StreamFlixTv home page logic
   ============================================================ */

// ── Genre icon map ─────────────────────────────────────────
const GENRE_ICONS = {
  action:'🔥', adventure:'🗺️', animation:'🎨', anime:'🎌',
  comedy:'😂', crime:'🕵️', documentary:'📽️', drama:'🎭',
  fantasy:'✨', horror:'👻', music:'🎵', mystery:'🔮',
  romance:'❤️', 'sci-fi':'🚀', 'science fiction':'🚀',
  thriller:'😨', western:'🤠', sport:'⚽', history:'📜',
  family:'👨‍👩‍👧', biography:'📖', movie:'🎬', default:'🎬'
};
function genreIcon(name) {
  return GENRE_ICONS[(name || '').toLowerCase()] || GENRE_ICONS.default;
}

// ── Famous movies catalogue — TMDB poster CDN (stable, no API key needed) ──
// All images served from image.tmdb.org/t/p/w500 — permanent public CDN
const FAMOUS_MOVIES = [
  // Action
  {
    title: 'Deadpool & Wolverine', year: 2024, genre: 'action', rating: '7.8',
    poster: 'https://image.tmdb.org/t/p/w500/8cdWjvZQUExUUTzyp4t6EDMubfO.jpg',
    desc: 'Two anti-heroes team up in the wildest Marvel crossover yet.'
  },
  {
    title: 'Top Gun: Maverick', year: 2022, genre: 'action', rating: '8.3',
    poster: 'https://image.tmdb.org/t/p/w500/62HCnUTziyWcpDaBO2i1DX17ljH.jpg',
    desc: 'Maverick returns to train the next generation of Top Gun pilots.'
  },
  {
    title: 'John Wick: Chapter 4', year: 2023, genre: 'action', rating: '7.8',
    poster: 'https://image.tmdb.org/t/p/w500/vZloFAK7NmvMGKE7VkF5UHaz0I.jpg',
    desc: 'John Wick discovers a path to defeating the High Table.'
  },
  {
    title: 'Fast X', year: 2023, genre: 'action', rating: '5.9',
    poster: 'https://image.tmdb.org/t/p/w500/fiVW06jE7z9YnO4trhaMEdclSiC.jpg',
    desc: 'Dom Toretto faces his most lethal adversary yet in the Fast saga.'
  },
  {
    title: 'Black Panther: Wakanda Forever', year: 2022, genre: 'action', rating: '7.3',
    poster: 'https://image.tmdb.org/t/p/w500/sv1xJUazXeYqALzczSZ3O6nkH75.jpg',
    desc: 'The people of Wakanda fight to protect their home and legacy.'
  },
  {
    title: 'Mission: Impossible – Dead Reckoning', year: 2023, genre: 'action', rating: '7.7',
    poster: 'https://image.tmdb.org/t/p/w500/NNxYkU70HPurnNCSiCjYAmacwm.jpg',
    desc: 'Ethan Hunt races to prevent a deadly AI from falling into the wrong hands.'
  },
  // Drama
  {
    title: 'Oppenheimer', year: 2023, genre: 'drama', rating: '8.5',
    poster: 'https://image.tmdb.org/t/p/w500/8Gxv8gSFCU0XGDykEGv7zR1n2ua.jpg',
    desc: 'The story of the man who developed the first atomic bomb.'
  },
  {
    title: 'Barbie', year: 2023, genre: 'drama', rating: '6.9',
    poster: 'https://image.tmdb.org/t/p/w500/iuFNMS8vlbauVCV7Fo2t2SjDGSJ.jpg',
    desc: 'Barbie and Ken embark on a journey of self-discovery in the real world.'
  },
  {
    title: 'The Holdovers', year: 2023, genre: 'drama', rating: '7.9',
    poster: 'https://image.tmdb.org/t/p/w500/VHSzNBTwxV8vh7wylo7O5L1UGRQ.jpg',
    desc: 'A curmudgeonly teacher is forced to babysit a student over the holidays.'
  },
  {
    title: 'Poor Things', year: 2023, genre: 'drama', rating: '8.0',
    poster: 'https://image.tmdb.org/t/p/w500/kCGlIMHnOm8JPXIf4NXoIBDLI62.jpg',
    desc: 'A young woman brought back to life embarks on an extraordinary self-discovery.'
  },
  {
    title: 'The Shawshank Redemption', year: 1994, genre: 'drama', rating: '9.3',
    poster: 'https://image.tmdb.org/t/p/w500/lyQBXzOQSuE59IsHyhrp0qIiPAz.jpg',
    desc: 'Two imprisoned men bond over years, finding solace and eventual redemption.'
  },
  // Sci-Fi
  {
    title: 'Dune: Part Two', year: 2024, genre: 'scifi', rating: '8.5',
    poster: 'https://image.tmdb.org/t/p/w500/1pdfLvkbY9ohJlCjQH2CZjjYVvJ.jpg',
    desc: 'Paul Atreides unites with the Fremen to avenge his family.'
  },
  {
    title: 'Interstellar', year: 2014, genre: 'scifi', rating: '8.7',
    poster: 'https://image.tmdb.org/t/p/w500/gEU2QniE6E77NI6lCU6MxlNBvIx.jpg',
    desc: 'A team travels through a wormhole in search of a new home for humanity.'
  },
  {
    title: 'Avengers: Endgame', year: 2019, genre: 'scifi', rating: '8.4',
    poster: 'https://image.tmdb.org/t/p/w500/or06FN3Dka5tukK1e9sl16pB3iy.jpg',
    desc: 'The Avengers assemble one last time to reverse Thanos\'s devastating snap.'
  },
  {
    title: 'Guardians of the Galaxy Vol. 3', year: 2023, genre: 'scifi', rating: '8.0',
    poster: 'https://image.tmdb.org/t/p/w500/r2J02Z2OpNTctfOSN1Ydgii51I3.jpg',
    desc: 'The Guardians embark on a mission to protect one of their own.'
  },
  {
    title: 'The Creator', year: 2023, genre: 'scifi', rating: '6.8',
    poster: 'https://image.tmdb.org/t/p/w500/vBZ0qvaRxqEhZwl6LWmruJqWE8Z.jpg',
    desc: 'A former soldier hunts down an AI weapon that could end the war.'
  },
  {
    title: 'Inception', year: 2010, genre: 'scifi', rating: '8.8',
    poster: 'https://image.tmdb.org/t/p/w500/edv5CZvWj09upOsy2Y6IwDhK8bt.jpg',
    desc: 'A thief enters the dreams of others to plant an idea in their mind.'
  },
  // Animation
  {
    title: 'Spider-Man: Across the Spider-Verse', year: 2023, genre: 'animation', rating: '8.7',
    poster: 'https://image.tmdb.org/t/p/w500/8Vt6mWEReuy4Of61Lnj5Xj704m8.jpg',
    desc: 'Miles Morales journeys across the Multiverse to meet fellow Spider-People.'
  },
  {
    title: 'The Lion King', year: 1994, genre: 'animation', rating: '8.5',
    poster: 'https://image.tmdb.org/t/p/w500/sKCr78MXSLixwmZ8DyJLrpMsd15.jpg',
    desc: 'A young lion prince must return home to claim his rightful place as king.'
  },
  {
    title: 'Elemental', year: 2023, genre: 'animation', rating: '6.8',
    poster: 'https://image.tmdb.org/t/p/w500/6oH378KUfgdm58Z1bGkSJRIHKRg.jpg',
    desc: 'A fire and water element discover they have more in common than expected.'
  },
  {
    title: 'Toy Story 4', year: 2019, genre: 'animation', rating: '7.8',
    poster: 'https://image.tmdb.org/t/p/w500/w9kR8qbmQ01HwnvK4alvnQ2ca0L.jpg',
    desc: 'Woody and the gang go on an unexpected road trip with surprising new friends.'
  },
  // Horror
  {
    title: 'Talk to Me', year: 2023, genre: 'horror', rating: '7.1',
    poster: 'https://image.tmdb.org/t/p/w500/kdPMUlRdZk4Ef3pVa5TZskFBfCb.jpg',
    desc: 'Teens discover how to conjure spirits — with terrifying consequences.'
  },
  {
    title: 'M3GAN', year: 2022, genre: 'horror', rating: '6.4',
    poster: 'https://image.tmdb.org/t/p/w500/d9nBoowhjiiYc4FBNtQkPY7c11H.jpg',
    desc: 'A lifelike AI doll develops a twisted sense of protection for her companion.'
  },
  {
    title: 'The Nun II', year: 2023, genre: 'horror', rating: '6.2',
    poster: 'https://image.tmdb.org/t/p/w500/5gzzkR7y3hnY8AD1wXjCnVlHba5.jpg',
    desc: 'The sinister demon Valak continues to haunt a young novitiate in France.'
  },
  {
    title: 'A Quiet Place: Day One', year: 2024, genre: 'horror', rating: '7.1',
    poster: 'https://image.tmdb.org/t/p/w500/yrpPYKijwdMHyTGIOd1iK1h0Wno.jpg',
    desc: 'Experience the terrifying first day the world went silent.'
  },
];

// ── Render famous movies (poster cards, no iframes) ────────
function renderTrailers(genre = 'all') {
  const list = genre === 'all'
    ? FAMOUS_MOVIES
    : FAMOUS_MOVIES.filter(m => m.genre === genre);

  const grid = document.getElementById('trailers-grid');
  if (!grid) return;

  grid.innerHTML = list.map(m => `
    <div class="trailer-card" data-genre="${m.genre}">
      <div class="trailer-poster">
        <img src="${m.poster}" alt="${m.title}" loading="lazy"
             onerror="this.parentElement.classList.add('trailer-poster--fallback');this.style.display='none'">
        <div class="trailer-poster-overlay">
          <div class="d-flex gap-2 flex-wrap">
            <span class="card-badge">${genreIcon(m.genre)} ${m.genre.toUpperCase()}</span>
            <span class="card-badge">⭐ ${m.rating}</span>
          </div>
        </div>
      </div>
      <div class="trailer-meta">
        <h3 class="trailer-title">${m.title}</h3>
        <p class="trailer-year">${m.year}</p>
        <p class="trailer-desc">${m.desc}</p>
      </div>
    </div>`).join('');
}

// ── Trailer genre tab filter ───────────────────────────────
function setupTrailerTabs() {
  const container = document.getElementById('trailer-genre-tabs');
  if (!container) return;
  container.addEventListener('click', e => {
    const btn = e.target.closest('.tab-btn[data-genre]');
    if (!btn) return;
    container.querySelectorAll('.tab-btn').forEach(b => b.classList.remove('active'));
    btn.classList.add('active');
    renderTrailers(btn.dataset.genre);
  });
}

// ── Main home load ─────────────────────────────────────────
async function loadHome() {
  const [trending, recent, categories] = await Promise.all([
    apiFetch('/api/videos/trending'),
    apiFetch('/api/videos/recent'),
    apiFetch('/api/categories')
  ]);

  // Store for tab switching
  window._homeData = { trending, recent };

  renderVideos('trending-row', trending);
  renderVideos('recent-row',   recent);

  // Top rated: all videos sorted by rating desc
  const allForRating = [...trending, ...recent]
    .filter((v, i, arr) => arr.findIndex(x => x.videoId === v.videoId) === i)
    .sort((a, b) => (b.rating || 0) - (a.rating || 0));
  renderVideos('toprated-row', allForRating);

  renderVideos('featured-slider', recent.length ? recent : trending);
  renderCategories(categories);

  if (getJwt()) loadContinueWatching();

  renderTrailers('all');
  setupTrailerTabs();
}

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

// ── Rendering helpers ──────────────────────────────────────
function renderVideos(id, videos) {
  const row = document.getElementById(id);
  if (!row) return;
  if (videos.length) {
    row.innerHTML = videos.map(videoCard).join('');
    enableDragScroll(row);
  } else {
    row.innerHTML = `<div style="font-family:var(--mono);font-size:.85rem;color:var(--text-muted);padding:2rem 0">
      No titles yet — drop an MP4 into upload/movies to get started.
    </div>`;
  }
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

function renderCategories(root) {
  const target = document.getElementById('category-row');
  const children = root.children || [];
  if (!children.length) {
    target.innerHTML = `<p style="font-family:var(--mono);font-size:.82rem;color:var(--text-muted)">No categories yet.</p>`;
    return;
  }
  target.innerHTML = children.map(cat => `
    <button class="category-chip-icon" data-category="${cat.name}">
      <span class="cat-icon">${genreIcon(cat.name)}</span>
      <span class="cat-name">${cat.name}</span>
    </button>`).join('');
  target.querySelectorAll('[data-category]').forEach(btn =>
    btn.addEventListener('click', () => triggerSearch(btn.dataset.category)));
}

// ── Drag-to-scroll for content rows ───────────────────────
function enableDragScroll(el) {
  if (!el || el.dataset.dragEnabled) return;
  el.dataset.dragEnabled = 'true';

  let isDown = false, startX = 0, scrollLeft = 0;

  el.addEventListener('mousedown', e => {
    // Don't hijack clicks on buttons/links
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

  // Touch drag (mobile)
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

// ── Section tab switcher — scoped to the discover section ──
document.addEventListener('click', e => {
  const btn = e.target.closest('.tab-btn[data-tab]');
  if (!btn) return;

  const section = btn.closest('section');
  if (!section) return;

  // Deactivate only sibling tab buttons in this section
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
  row.innerHTML = `<p style="font-family:var(--mono);font-size:.82rem;color:var(--text-muted);padding:1rem 0">Searching…</p>`;

  try {
    const videos = await apiFetch(`/api/videos?search=${encodeURIComponent(query)}`);
    if (videos.length) {
      row.innerHTML = videos.map(videoCard).join('');
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
let allVideos        = [];

async function ensureVideoCache() {
  if (allVideos.length) return;
  try { allVideos = await apiFetch('/api/videos'); } catch (_) {}
}

function showSuggestions(query) {
  const box = document.getElementById('search-suggestions');
  if (!box) return;
  const q = query.toLowerCase().trim();
  if (!q) { hideSuggestions(); return; }

  const matches = allVideos
    .filter(v => (v.title || '').toLowerCase().includes(q) ||
                 (v.genre || '').toLowerCase().includes(q))
    .slice(0, 7);

  const rx = new RegExp(`(${q.replace(/[.*+?^${}()|[\]\\]/g, '\\$&')})`, 'gi');

  if (!matches.length) {
    box.innerHTML = `<div class="suggest-empty">No results for "<strong>${query}</strong>"</div>`;
  } else {
    box.innerHTML = matches.map(v => {
      const img    = v.thumbnailUrl ? `/uploads/${v.thumbnailUrl}` : '';
      const title  = v.title  || 'Untitled';
      const genre  = v.genre  || 'Movie';
      const year   = v.releaseYear ? ` · ${v.releaseYear}` : '';
      const rating = v.rating ? `⭐ ${parseFloat(v.rating).toFixed(1)}` : '';
      const hl     = title.replace(rx, '<mark>$1</mark>');

      return `
        <a class="suggest-item" href="/videos/${v.videoId}">
          <div class="suggest-thumb">
            ${img
              ? `<img src="${img}" alt="${title}" loading="lazy">`
              : `<div class="suggest-thumb-fallback">${genreIcon(genre)}</div>`}
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
  }

  box.hidden = false;
}

function hideSuggestions() {
  const box = document.getElementById('search-suggestions');
  if (box) box.hidden = true;
}

function setupSearch() {
  const input = document.getElementById('nav-search-input');
  if (!input) return;

  ensureVideoCache();

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

// ── Boot ───────────────────────────────────────────────────
document.addEventListener('DOMContentLoaded', () => {
  loadHome().catch(err => console.error('loadHome:', err));
  setupSearch();
});
