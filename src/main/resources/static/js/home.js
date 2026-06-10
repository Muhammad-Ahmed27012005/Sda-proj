async function loadHome() {
  const [trending, recent, categories] = await Promise.all([
    apiFetch('/api/videos/trending'),
    apiFetch('/api/videos/recent'),
    apiFetch('/api/categories')
  ]);
  renderVideos('trending-row', trending);
  renderVideos('recent-row', recent);
  renderVideos('featured-slider', recent.length ? recent : trending);
  renderVideos('continue-row', recent.slice(0, 6));
  renderCategories(categories);
}

function renderVideos(id, videos) {
  const row = document.getElementById(id);
  row.innerHTML = videos.length ? videos.map(videoCard).join('') : '<p class="text-muted-light">No videos uploaded yet.</p>';
}

function renderCategories(root) {
  const target = document.getElementById('category-row');
  const children = root.children || [];
  target.innerHTML = children.map(category => `
    <button class="category-chip" data-category="${category.name}">${category.name}</button>
  `).join('');
  target.querySelectorAll('[data-category]').forEach(button => {
    button.addEventListener('click', () => searchVideos(button.dataset.category));
  });
}

async function searchVideos(search) {
  const videos = await apiFetch(`/api/videos?search=${encodeURIComponent(search)}`);
  renderVideos('search-row', videos);
}

document.addEventListener('DOMContentLoaded', () => {
  loadHome().catch(error => console.error(error));
  const form = document.getElementById('search-form');
  form?.addEventListener('submit', event => {
    event.preventDefault();
    searchVideos(new FormData(form).get('search')).catch(error => alert(error.message));
  });
});
