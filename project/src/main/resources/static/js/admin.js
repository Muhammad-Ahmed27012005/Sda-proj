async function loadAdmin() {
  requireAuth();
  const dashboard = await apiFetch('/api/admin/dashboard');
  Object.entries(dashboard).forEach(([key, value]) => {
    const target = document.querySelector(`[data-stat="${key}"]`);
    if (target) target.textContent = value;
  });
  const usersTable = document.getElementById('users-table');
  if (usersTable) {
    const users = await apiFetch('/api/admin/users');
    usersTable.innerHTML = users.map(user => `
      <tr>
        <td>${user.fullName}</td>
        <td>${user.email}</td>
        <td>${user.role}</td>
        <td><button class="btn btn-sm btn-outline-warning" data-block="${user.userId}">Block</button></td>
      </tr>
    `).join('');
    usersTable.querySelectorAll('[data-block]').forEach(button => {
      button.addEventListener('click', async () => {
        await apiFetch(`/api/admin/users/${button.dataset.block}/block`, { method: 'POST' });
        loadAdmin();
      });
    });
  }
}

document.addEventListener('DOMContentLoaded', () => {
  if (document.querySelector('[data-admin-page]')) {
    loadAdmin().catch(error => alert(error.message));
  }
});
