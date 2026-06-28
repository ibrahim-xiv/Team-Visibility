/**
 * app.js – Visibility gemeinsames Skript
 * 1. Darkmode
 * 2. Auth-Guard (kein Zugang zu geschützten Seiten ohne Login)
 * 3. Settings-Toggle Sync
 */

(function () {

  /* ── 1. DARKMODE INIT ─────────────────────────────────────────────────── */
  function applyTheme(isDark) {
    if (isDark) document.body.classList.add('dark-mode');
    else document.body.classList.remove('dark-mode');
    const t = document.getElementById('darkModeToggle');
    if (t) t.checked = isDark;
  }
  applyTheme(localStorage.getItem('theme') === 'dark');

  /* ── 2. AUTH-GUARD ─────────────────────────────────────────────────────── */
  // Pages that don't require login
  const PUBLIC_PAGES = ['login.html', 'register.html', 'index.html', ''];
  document.addEventListener('DOMContentLoaded', function () {
    const path = window.location.pathname.split('/').pop() || '';
    const isPublic = PUBLIC_PAGES.some(p => path === p || path === '');
    if (!isPublic) {
      const user = JSON.parse(localStorage.getItem('tv-user') || 'null');
      if (!user) {
        window.location.href = 'login.html';
        return;
      }
    }

    /* ── 3. SETTINGS-TOGGLE SYNC ──────────────────────────────────────────── */
    const settingsToggle = document.getElementById('darkModeToggle');
    if (settingsToggle) {
      settingsToggle.checked = localStorage.getItem('theme') === 'dark';
      settingsToggle.addEventListener('change', () => {
        applyTheme(settingsToggle.checked);
        localStorage.setItem('theme', settingsToggle.checked ? 'dark' : 'light');
      });
    }
  });

})();
