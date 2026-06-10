async function loadWatchlist() {
  requireAuth();
  const videos = await apiFetch('/api/watchlist');
  document.getElementById('watchlist-grid').innerHTML = videos.length ? videos.map(video => `
    ${videoCard(video)}
    <button class="btn btn-sm btn-outline-light mt-2" data-remove="${video.videoId}">Remove</button>
  `).join('') : '<p class="text-muted-light">Your watchlist is empty.</p>';
  document.querySelectorAll('[data-remove]').forEach(button => {
    button.addEventListener('click', async () => {
      await apiFetch(`/api/watchlist/remove/${button.dataset.remove}`, { method: 'DELETE' });
      loadWatchlist();
    });
  });
}

async function addToWatchlist(videoId) {
  await apiFetch('/api/watchlist/add', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ videoId })
  });
}

document.addEventListener('DOMContentLoaded', () => {
  if (document.getElementById('watchlist-grid')) {
    loadWatchlist().catch(error => alert(error.message));
  }
});
