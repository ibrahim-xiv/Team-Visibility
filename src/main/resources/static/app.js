/**
 * app.js – Visibility gemeinsames Skript
 * Übernimmt:
 *  1. Darkmode-Initialisierung (sofort, vor dem Rendern)
 *  2. Settings-Toggle Synchronisierung (nur auf settings.html)
 */

(function () {

  /* ── 1. DARKMODE INIT ─────────────────────────────────────────────────── */
  function applyTheme(isDark) {
    if (isDark) {
      document.body.classList.add('dark-mode');
    } else {
      document.body.classList.remove('dark-mode');
    }
    const settingsToggle = document.getElementById('darkModeToggle');
    if (settingsToggle) settingsToggle.checked = isDark;
  }

  const storedTheme = localStorage.getItem('theme');
  applyTheme(storedTheme === 'dark');

  /* ── 2. SETTINGS-TOGGLE SYNC ────────────────────────────────────────────── */
  document.addEventListener('DOMContentLoaded', function () {
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
