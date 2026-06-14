function getJwt() {
  return localStorage.getItem('streamflixToken') || '';
}

function currentUser() {
  const raw = localStorage.getItem('streamflixUser');
  if (!raw) {
    return null;
  }
  try {
    return JSON.parse(raw);
  } catch {
    localStorage.removeItem('streamflixUser');
    return null;
  }
}

function authHeaders(extra = {}) {
  const token = getJwt();
  return {
    ...extra,
    ...(token ? { Authorization: `Bearer ${token}` } : {})
  };
}

async function apiFetch(url, options = {}) {
  const headers = authHeaders(options.headers || {});
  const response = await fetch(url, { ...options, headers });
  const contentType = response.headers.get('content-type') || '';
  const responseText = await response.text();
  let body = responseText;
  if (contentType.includes('application/json')) {
    try {
      body = responseText ? JSON.parse(responseText) : {};
    } catch {
      throw new Error('The server returned an invalid response. Please try again.');
    }
  }
  if (!response.ok || body.success === false) {
    // Handle both ApiResponse errors ({success:false, message:...})
    // and GlobalExceptionHandler errors ({error:..., message:...})
    throw new Error(body.message || body.error || response.statusText);
  }
  // Unwrap ApiResponse wrapper if present, otherwise return body as-is
  return 'data' in body ? body.data : body;
}

function requireAuth() {
  if (!getJwt()) {
    window.location.href = '/login';
  }
}

function logout() {
  localStorage.removeItem('streamflixToken');
  localStorage.removeItem('streamflixUser');
  fetch('/logout', { method: 'POST' }).finally(() => window.location.href = '/');
}

function videoCard(video) {
  const image   = video.thumbnailUrl ? `/uploads/${video.thumbnailUrl}` : '';
  const videoSrc = video.videoUrl    ? `/uploads/${video.videoUrl}`     : '';
  const title   = video.title        || 'Untitled';
  const genre   = video.genre        || 'Movie';
  const year    = video.releaseYear  ? ' · ' + video.releaseYear        : '';
  const rating  = video.rating       ? `⭐ ${parseFloat(video.rating).toFixed(1)}` : '';
  const dur     = video.duration     ? fmtDuration(video.duration)      : '';

  let posterHtml;
  if (image) {
    posterHtml = `<img src="${image}" alt="${title}" loading="lazy">`;
  } else if (videoSrc) {
    posterHtml = `<video src="${videoSrc}" muted preload="metadata"
                    style="width:100%;height:100%;object-fit:cover;pointer-events:none"
                    onloadedmetadata="this.currentTime=2"></video>`;
  } else {
    posterHtml = `<div class="video-poster-fallback"><span>${title}</span></div>`;
  }

  return `
    <article class="video-card">
      <a class="video-poster" href="/watch/${video.videoId}" aria-label="Play ${title}">
        ${posterHtml}
        <!-- hover overlay -->
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
          <button class="btn btn-ghost btn-sm wl-btn" title="Add to Watchlist"
                  onclick="handleWatchlistAdd(event,${video.videoId},this)">❤️</button>
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

async function handleWatchlistAdd(e, videoId, btn) {
  e.preventDefault();
  if (!getJwt()) { window.location.href = '/login'; return; }
  try {
    await apiFetch('/api/watchlist/add', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ videoId })
    });
    btn.textContent = '✅';
    btn.disabled = true;
    btn.title = 'Added to Watchlist';
  } catch (err) {
    btn.textContent = '❌';
    setTimeout(() => { btn.textContent = '❤️'; btn.disabled = false; }, 1500);
  }
}

/** Show an inline error message inside a form */
function showFormError(formEl, message) {
  let el = formEl.querySelector('.form-error-msg');
  if (!el) {
    el = document.createElement('div');
    el.className = 'alert-error form-error-msg mb-3';
    formEl.prepend(el);
  }
  el.textContent = message;
  el.hidden = false;
}

/** Clear inline error message */
function clearFormError(formEl) {
  const el = formEl.querySelector('.form-error-msg');
  if (el) el.hidden = true;
}

document.addEventListener('DOMContentLoaded', () => {
  const user = currentUser();
  document.querySelectorAll('[data-user-name]').forEach(el => {
    el.textContent = user ? user.fullName : 'Guest';
  });
  document.querySelectorAll('[data-admin-only]').forEach(el => {
    el.hidden = user?.role !== 'ADMIN';
  });
  document.querySelectorAll('[data-guest-only]').forEach(el => {
    el.hidden = !!user;
  });
  document.querySelectorAll('[data-logout]').forEach(el => el.addEventListener('click', logout));

  // Load plan badge in navbar
  const badge = document.getElementById('nav-plan-badge');
  if (badge && user) {
    loadNavPlanBadge(badge);
  }

  const nav = document.querySelector('.sf-nav');
  const updateNav = () => nav?.classList.toggle('is-scrolled', window.scrollY > 24);
  updateNav();
  window.addEventListener('scroll', updateNav, { passive: true });
});

async function loadNavPlanBadge(badge) {
  try {
    const data = await apiFetch('/api/subscription/status');
    if (data.active && data.subscription && data.subscription !== 'none') {
      const planName = data.subscription.planName;
      badge.textContent = planName + ' Plan';
      badge.hidden = false;
    }
  } catch (_) {
    // No subscription or not logged in — leave badge hidden
  }
}
