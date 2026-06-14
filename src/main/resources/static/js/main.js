function getJwt() {
  return localStorage.getItem('streamflixToken') || '';
}

function currentUser() {
  const raw = localStorage.getItem('streamflixUser');
  return raw ? JSON.parse(raw) : null;
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
  const body = contentType.includes('application/json') ? await response.json() : await response.text();
  if (!response.ok || body.success === false) {
    throw new Error(body.message || response.statusText);
  }
  return body.data ?? body;
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
  const image = video.thumbnailUrl ? `/uploads/${video.thumbnailUrl}` : '';
  const title = video.title || 'Untitled';
  return `
    <article class="video-card">
      <a class="video-poster" href="/watch/${video.videoId}" aria-label="Play ${title}">
        ${image ? `<img src="${image}" alt="${title}">` : `<span>${title}</span>`}
      </a>
      <div class="video-meta">
        <p>${video.genre || 'Movie'}${video.releaseYear ? ' / ' + video.releaseYear : ''}</p>
        <h3>${title}</h3>
        <div class="video-actions">
          <a class="btn btn-sm btn-stream" href="/watch/${video.videoId}">Start Watching</a>
          <a class="btn btn-sm btn-outline-light" href="/videos/${video.videoId}">Details</a>
        </div>
      </div>
    </article>`;
}

document.addEventListener('DOMContentLoaded', () => {
  const user = currentUser();
  document.querySelectorAll('[data-user-name]').forEach(el => {
    el.textContent = user ? user.fullName : 'Guest';
  });
  document.querySelectorAll('[data-logout]').forEach(el => el.addEventListener('click', logout));
  const nav = document.querySelector('.sf-nav');
  const updateNav = () => nav?.classList.toggle('is-scrolled', window.scrollY > 24);
  updateNav();
  window.addEventListener('scroll', updateNav, { passive: true });
});
