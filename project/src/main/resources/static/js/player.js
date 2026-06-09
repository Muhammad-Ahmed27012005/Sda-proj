class StreamFlixPlayer {
  constructor(videoElement, videoId, userId) {
    this.video = videoElement;
    this.videoId = videoId;
    this.userId = userId;
    this.saveInterval = null;
  }

  init() {
    this.video.src = `/api/videos/stream/${this.videoId}`;
    this.loadLastPosition();
    this.bindControls();
    this.startPositionSaving();
  }

  async loadLastPosition() {
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
  requireAuth();
  const videoId = location.pathname.split('/').pop();
  const user = currentUser();
  const info = await apiFetch(`/api/videos/stream-info/${videoId}`);
  document.getElementById('quality-label').textContent = `${info.qualityLabel} - ${info.bitrate}`;
  new StreamFlixPlayer(element, videoId, user?.userId).init();
});
