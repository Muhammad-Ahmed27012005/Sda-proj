class StreamFlixPlayer {
  constructor(videoElement, videoId, userId) {
    this.video = videoElement;
    this.videoId = videoId;
    this.userId = userId;
    this.saveInterval = null;
  }

  init() {
    this.video.src = `/api/videos/stream/${this.videoId}`;
    this.loadLastPosition().catch(() => {});
    this.bindControls();
    this.startPositionSaving();
  }

  async loadLastPosition() {
    if (!getJwt()) return;
    const items = await apiFetch('/api/history/continue');
    const match = items.find(item => Number(item.videoId) === Number(this.videoId));
    if (match?.lastPosition) {
      this.video.addEventListener('loadedmetadata', () => {
        this.video.currentTime = match.lastPosition;
      }, { once: true });
    }
  }

  bindControls() {
    document.getElementById('play-toggle')?.addEventListener('click', () => this.video.paused ? this.play() : this.pause());
    document.getElementById('stop')?.addEventListener('click', () => this.stop());
    document.getElementById('mute')?.addEventListener('click', () => this.toggleMute());
    document.getElementById('fullscreen')?.addEventListener('click', () => this.toggleFullscreen());
    document.getElementById('skip-back')?.addEventListener('click', () => this.skip(-10));
    document.getElementById('skip-forward')?.addEventListener('click', () => this.skip(10));
    document.getElementById('volume')?.addEventListener('input', event => this.setVolume(event.target.value));
    document.getElementById('speed')?.addEventListener('change', event => this.setPlaybackSpeed(event.target.value));
    document.getElementById('progress')?.addEventListener('click', event => this.seek(event));
    this.video.addEventListener('timeupdate', () => this.updateProgressBar());
    this.video.addEventListener('pause', () => this.savePosition());
  }

  startPositionSaving() {
    this.saveInterval = setInterval(() => this.savePosition(), 10000);
  }

  async savePosition() {
    if (!this.video.duration || Number.isNaN(this.video.duration)) return;
    const pct = (this.video.currentTime / this.video.duration) * 100;
    if (!getJwt()) return;
    await apiFetch('/api/history/save', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ videoId: this.videoId, lastPosition: Math.floor(this.video.currentTime), watchPercentage: pct })
    });
  }

  play() { this.video.play(); }
  pause() { this.video.pause(); }
  stop() { this.video.pause(); this.video.currentTime = 0; this.savePosition(); }
  toggleMute() { this.video.muted = !this.video.muted; }
  setVolume(value) { this.video.volume = Number(value); }
  skip(seconds) { this.video.currentTime = Math.max(0, this.video.currentTime + seconds); }
  setPlaybackSpeed(rate) { this.video.playbackRate = Number(rate); }
  toggleFullscreen() { this.video.parentElement.requestFullscreen?.(); }
  setQuality(qualityLabel) { document.getElementById('quality-label').textContent = qualityLabel; }

  seek(event) {
    const rect = event.currentTarget.getBoundingClientRect();
    const pct = (event.clientX - rect.left) / rect.width;
    this.video.currentTime = pct * this.video.duration;
  }

  updateProgressBar() {
    const pct = this.video.duration ? (this.video.currentTime / this.video.duration) * 100 : 0;
    document.getElementById('progress-fill').style.width = `${pct}%`;
    document.getElementById('timecode').textContent = `${this.formatTime(this.video.currentTime)} / ${this.formatTime(this.video.duration)}`;
  }

  formatTime(seconds) {
    if (!seconds || Number.isNaN(seconds)) return '0:00';
    const mins = Math.floor(seconds / 60);
    const secs = Math.floor(seconds % 60).toString().padStart(2, '0');
    return `${mins}:${secs}`;
  }
}

document.addEventListener('DOMContentLoaded', async () => {
  const element = document.getElementById('stream-video');
  if (!element) return;

  const videoId = location.pathname.split('/').pop();
  const user    = currentUser();

  // Load video metadata for the sidebar
  try {
    const video = await apiFetch(`/api/videos/${videoId}`);
    document.getElementById('player-title').textContent = video.title || 'Untitled';

    const meta = [video.genre, video.releaseYear].filter(Boolean).join(' · ');
    const metaEl = document.getElementById('player-meta');
    if (metaEl) metaEl.textContent = meta;

    if (video.description) {
      const descEl = document.getElementById('player-desc');
      if (descEl) descEl.textContent = video.description;
    }

    if (video.rating) {
      const ratingEl = document.getElementById('player-rating');
      if (ratingEl) { ratingEl.textContent = `⭐ ${parseFloat(video.rating).toFixed(1)}`; ratingEl.hidden = false; }
    }

    const detailsLink = document.getElementById('details-link');
    if (detailsLink) detailsLink.href = `/videos/${videoId}`;

    const wlBtn = document.getElementById('player-watchlist-btn');
    if (wlBtn) {
      wlBtn.addEventListener('click', async () => {
        if (!getJwt()) { window.location.href = '/login'; return; }
        try {
          await apiFetch('/api/watchlist/add', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ videoId: Number(videoId) })
          });
          wlBtn.textContent = '✅ Added';
          wlBtn.disabled = true;
        } catch (err) {
          wlBtn.textContent = '❌ Error';
          setTimeout(() => { wlBtn.textContent = '❤️ Add to Watchlist'; wlBtn.disabled = false; }, 1500);
        }
      });
    }
  } catch (err) {
    document.getElementById('player-title').textContent = 'Video';
  }

  // Load quality info — safe: we only display it when available
  try {
    const info = await apiFetch(`/api/videos/stream-info/${videoId}`);
    const labelEl = document.getElementById('quality-label');
    if (labelEl && info.qualityLabel) {
      labelEl.textContent = info.qualityLabel + (info.bitrate ? ' — ' + info.bitrate : '');
      labelEl.style.display = '';
    }
  } catch (_) {
    // Guest or no subscription — quality info is optional, player still works
  }

  // Always start the player — streaming endpoint allows public access
  new StreamFlixPlayer(element, videoId, user?.userId).init();
});
