document.addEventListener('DOMContentLoaded', () => {
  requireAuth();
  document.querySelectorAll('[data-plan]').forEach(button => {
    button.addEventListener('click', async () => {
      const planName = button.dataset.plan;
      const paymentMethod = document.querySelector('input[name="paymentMethod"]:checked')?.value || 'DEMO';
      button.disabled = true;
      button.textContent = 'Processing...';
      try {
        const result = await apiFetch('/api/subscription/subscribe', {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({ planName, paymentMethod })
        });
        document.getElementById('subscription-result').textContent = `Success: ${result.payment.transactionId}`;
      } catch (error) {
        alert(error.message);
      } finally {
        button.disabled = false;
        button.textContent = 'Subscribe';
      }
    });
  });
});
