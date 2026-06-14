document.addEventListener('DOMContentLoaded', () => {
  requireAuth();

  // Highlight the user's current plan if already subscribed
  const user = currentUser();
  if (user) {
    loadCurrentPlan();
  }

  // Open payment modal when a plan button is clicked
  document.querySelectorAll('[data-plan]').forEach(button => {
    button.addEventListener('click', () => {
      const plan  = button.dataset.plan;
      const price = button.dataset.price;
      document.getElementById('modal-plan-name').textContent = plan;
      document.getElementById('modal-plan-price').textContent = price + ' / month';
      document.getElementById('modal-error').hidden = true;
      document.getElementById('confirm-subscribe-btn').dataset.plan = plan;
      document.getElementById('confirm-subscribe-btn').disabled = false;
      document.getElementById('confirm-subscribe-btn').textContent = 'Pay & Subscribe';
      const modal = new bootstrap.Modal(document.getElementById('paymentModal'));
      modal.show();
    });
  });

  // Confirm button inside modal
  document.getElementById('confirm-subscribe-btn').addEventListener('click', async (e) => {
    const btn = e.currentTarget;
    const planName     = btn.dataset.plan;
    const paymentMethod = document.querySelector('input[name="paymentMethod"]:checked')?.value || 'PAYPAL';
    const errorEl      = document.getElementById('modal-error');

    errorEl.hidden = true;
    btn.disabled = true;
    btn.textContent = 'Processing…';

    try {
      const result = await apiFetch('/api/subscription/subscribe', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ planName, paymentMethod })
      });

      // Update stored user plan so navbar badge refreshes immediately
      const stored = currentUser();
      if (stored) {
        stored.plan = planName;
        localStorage.setItem('streamflixUser', JSON.stringify(stored));
      }

      // Pass result to success page via sessionStorage
      sessionStorage.setItem('subscribeResult', JSON.stringify({
        planName,
        paymentMethod,
        transactionId: result.payment?.transactionId,
        amount:        result.payment?.amount,
        startDate:     result.subscription?.startDate,
        endDate:       result.subscription?.endDate,
        status:        result.subscription?.status
      }));

      bootstrap.Modal.getInstance(document.getElementById('paymentModal')).hide();
      window.location.href = '/subscribe/success';

    } catch (err) {
      errorEl.textContent = err.message || 'Payment failed. Please try again.';
      errorEl.hidden = false;
      btn.disabled = false;
      btn.textContent = 'Pay & Subscribe';
    }
  });

  async function loadCurrentPlan() {
    try {
      const data = await apiFetch('/api/subscription/status');
      if (data.active && data.subscription && data.subscription !== 'none') {
        const sub = data.subscription;
        const planName = sub.planName;

        // Show active plan banner
        const banner = document.getElementById('active-plan-banner');
        document.getElementById('active-plan-name').textContent    = planName + ' Plan';
        document.getElementById('active-plan-renews').textContent  = 'Renews ' + fmt(sub.endDate);
        banner.style.display = 'flex';
        banner.hidden = false;

        // Mark current plan card
        const card = document.getElementById('plan-' + planName);
        if (card) {
          const badge = document.createElement('span');
          badge.textContent = 'Current';
          badge.style.cssText = 'background:var(--acid-green);color:var(--off-black);font-family:var(--mono);' +
            'font-size:.65rem;padding:.18rem .55rem;border-radius:999px;text-transform:uppercase;font-weight:700;margin-left:.5rem';
          card.querySelector('.eyebrow').appendChild(badge);
          const btn = card.querySelector('[data-plan]');
          if (btn) { btn.textContent = 'Renew Plan'; }
        }
      }
    } catch (_) {}
  }
});

function downloadSlip() {
  const token = localStorage.getItem('streamflixToken');
  const btn = document.getElementById('download-slip-btn');
  btn.disabled = true;
  btn.textContent = 'Generating…';
  fetch('/api/subscription/slip', {
    headers: token ? { Authorization: 'Bearer ' + token } : {}
  })
  .then(res => {
    if (!res.ok) throw new Error('Could not generate slip');
    return res.blob();
  })
  .then(blob => {
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = 'streamflixtv-subscription-slip.pdf';
    a.click();
    URL.revokeObjectURL(url);
  })
  .catch(err => alert(err.message))
  .finally(() => { btn.disabled = false; btn.textContent = '⬇ Download Slip'; });
}

function fmt(d) {
  if (!d) return '—';
  if (Array.isArray(d)) return `${d[0]}-${String(d[1]).padStart(2,'0')}-${String(d[2]).padStart(2,'0')}`;
  return d;
}
