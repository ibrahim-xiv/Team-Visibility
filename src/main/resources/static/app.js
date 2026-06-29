/**
 * app.js – Visibility gemeinsames Skript
 * 1. Darkmode-Initialisierung
 * 2. Auth-Guard (redirect wenn nicht eingeloggt)
 * 3. Settings-Toggle Sync
 */
(function () {
  // Darkmode
  function applyTheme(isDark) {
    if (isDark) document.body.classList.add('dark-mode');
    else document.body.classList.remove('dark-mode');
    const t = document.getElementById('darkModeToggle');
    if (t) t.checked = isDark;
  }
  applyTheme(localStorage.getItem('theme') === 'dark');

  // Auth guard - pages that DON'T require login
  const publicPages = ['login.html', 'register.html', 'forgot-password.html'];
  const currentPage = window.location.pathname.split('/').pop() || 'login.html';
  if (!publicPages.includes(currentPage)) {
    const user = JSON.parse(localStorage.getItem('tv-user') || 'null');
    if (!user) { window.location.href = 'login.html'; return; }
  }

  document.addEventListener('DOMContentLoaded', function () {
    // Settings toggle sync
    const settingsToggle = document.getElementById('darkModeToggle');
    if (settingsToggle) {
      settingsToggle.addEventListener('change', () => {
        const isDark = settingsToggle.checked;
        localStorage.setItem('theme', isDark ? 'dark' : 'light');
        applyTheme(isDark);
      });
    }
  });
})();