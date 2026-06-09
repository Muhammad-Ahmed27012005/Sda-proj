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
  const image = video.thumbnailUrl ? `/uploads/${video.thumbnailUrl}` : 'https://images.unsplash.com/photo-1440404653325-ab127d49abc1?auto=format&fit=crop&w=600&q=80';
  return `
    <article class="card video-card text-white">
      <img src="${image}" class="card-img-top" alt="${video.title}">
      <div class="card-body">
        <h3 class="h6 mb-1">${video.title}</h3>
        <p class="small text-muted-light mb-2">${video.genre || 'Movie'} ${video.releaseYear ? ' - ' + video.releaseYear : ''}</p>
        <div class="d-flex gap-2">
          <a class="btn btn-sm btn-light" href="/videos/${video.videoId}">Info</a>
          <a class="btn btn-sm btn-stream" href="/watch/${video.videoId}">Play</a>
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
});
