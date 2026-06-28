/**
 * app.js – Visibility gemeinsames Skript
 * Wird auf jeder Seite eingebunden und übernimmt:
 *  1. Darkmode-Initialisierung (sofort, vor dem Rendern)
 *  2. Dev-Nav HTML-Injection
 *  3. Dev-Nav Collapse-Logik
 *  4. Dev-Nav Theme-Toggle
 *  5. Settings-Toggle Synchronisierung (nur auf settings.html)
 */

(function () {

  /* ── 1. DARKMODE INIT ─────────────────────────────────────────────────── */
  // Früh im Skript, damit kein Flash of unstyled content entsteht.
  function applyTheme(isDark) {
    if (isDark) {
      document.body.classList.add('dark-mode');
    } else {
      document.body.classList.remove('dark-mode');
    }
    // Wenn der Settings-Toggle existiert: synchronisieren
    const settingsToggle = document.getElementById('darkModeToggle');
    if (settingsToggle) settingsToggle.checked = isDark;
  }

  const storedTheme = localStorage.getItem('theme');
  applyTheme(storedTheme === 'dark');

  /* ── 2. DEV-NAV HTML-INJECTION ────────────────────────────────────────── */
  const DEV_NAV_HTML = `
<nav class="dev-nav" id="devNavEl">
  <div class="dev-nav-header">DEV-Navigation</div>
  <div><a href="login.html" class="dev-btn">🔑 Login</a></div>
  <div><a href="register.html" class="dev-btn">📝 Registrieren</a></div>
  <div><a href="home.html" class="dev-btn">🏠 Home</a></div>
  <div><a href="meeting-form.html" class="dev-btn">➕ Erstellen</a></div>
  <div><a href="profile.html" class="dev-btn">👤 Eigenes Profil</a></div>
  <div><a href="profile-view.html" class="dev-btn">👁️ Fremdes Profil</a></div>
  <div><a href="profile-edit.html" class="dev-btn">✏️ Profil bearbeiten</a></div>
  <div><a href="settings.html" class="dev-btn">⚙️ Einstellungen</a></div>
  <div><a href="map.html" class="dev-btn">🗺️ Kartenansicht</a></div>
  <div><a href="event-details.html" class="dev-btn">📝 Event Details</a></div>
  <div><a href="saved.html" class="dev-btn">📅 Events</a></div>
  <div><a href="chat.html" class="dev-btn">💬 Chat</a></div>
  <div style="margin-top:5px;padding-top:10px;border-top:1px solid #363434;">
    <button id="devThemeToggle" class="dev-btn-action">🌗 Modus wechseln</button>
  </div>
</nav>`;

  // Dev-Nav ans Ende des body hängen (falls noch keine vorhanden)
  document.addEventListener('DOMContentLoaded', function () {
    if (!document.getElementById('devNavEl')) {
      document.body.insertAdjacentHTML('beforeend', DEV_NAV_HTML);
    }

    const devNav    = document.getElementById('devNavEl');
    const devHeader = devNav ? devNav.querySelector('.dev-nav-header') : null;
    const devBtn    = document.getElementById('devThemeToggle');

    /* ── 3. COLLAPSE LOGIK ──────────────────────────────────────────────── */
    if (devNav && devHeader) {
      if (localStorage.getItem('devNavCollapsed') === 'true') {
        devNav.classList.add('collapsed');
      }
      devHeader.addEventListener('click', () => {
        const isNowCollapsed = devNav.classList.toggle('collapsed');
        localStorage.setItem('devNavCollapsed', String(isNowCollapsed));
      });
    }

    /* ── 4. DEV THEME-TOGGLE ────────────────────────────────────────────── */
    if (devBtn) {
      devBtn.addEventListener('click', () => {
        const isDark = document.body.classList.toggle('dark-mode');
        localStorage.setItem('theme', isDark ? 'dark' : 'light');
        const settingsToggle = document.getElementById('darkModeToggle');
        if (settingsToggle) settingsToggle.checked = isDark;
        // Karte neu laden damit dunkle Tiles geladen werden
        if (window.location.pathname.includes('map.html')) window.location.reload();
      });
    }

    /* ── 5. SETTINGS-TOGGLE SYNC ────────────────────────────────────────── */
    // Nur auf settings.html vorhanden
    const settingsToggle = document.getElementById('darkModeToggle');
    if (settingsToggle) {
      // Checkbox-Status mit gespeichertem Theme synchronisieren
      settingsToggle.checked = localStorage.getItem('theme') === 'dark';
      settingsToggle.addEventListener('change', () => {
        applyTheme(settingsToggle.checked);
        localStorage.setItem('theme', settingsToggle.checked ? 'dark' : 'light');
      });
    }
  });

})();
