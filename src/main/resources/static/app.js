/*
 * Team-Visibility - tiny REST client used by the static frontend.
 *
 * Pages still use localStorage as their local cache so the original UX (saved
 * events list, dark-mode preference, etc.) keeps working. The functions below
 * ALSO push relevant actions to the backend so CRUD is real.
 */
window.TV = (function () {
  const json = (r) => r.json().catch(() => ({}));

  async function post(path, body) {
    const res = await fetch(path, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(body || {})
    });
    const data = await json(res);
    if (!res.ok) throw Object.assign(new Error(data.error || res.statusText), { status: res.status });
    return data;
  }

  async function del(path) {
    const res = await fetch(path, { method: 'DELETE' });
    if (!res.ok && res.status !== 204) {
      throw new Error(res.statusText);
    }
  }

  async function get(path) {
    const res = await fetch(path);
    const data = await json(res);
    if (!res.ok) throw Object.assign(new Error(data.error || res.statusText), { status: res.status });
    return data;
  }

  function currentUserId() {
    const raw = localStorage.getItem('tv-user');
    if (!raw) return null;
    try { return JSON.parse(raw).id; } catch { return null; }
  }

  function setCurrentUser(u) {
    if (u) localStorage.setItem('tv-user', JSON.stringify(u));
    else localStorage.removeItem('tv-user');
  }

  return {
    // Auth
    register: (payload) => post('/api/users/register', payload),
    login:    (payload) => post('/api/users/login', payload),

    // Meetings
    createMeeting: (m)        => post('/api/meetings', m),
    listMeetings:  ()         => get('/api/meetings'),
    getMeeting:    (id)       => get('/api/meetings/' + id),
    deleteMeeting: (id)       => del('/api/meetings/' + id),

    // Favorites
    addFavorite:    (userId, meetingId) => post('/api/favorites', { userId, meetingId }),
    removeFavorite: (userId, meetingId) => del(`/api/favorites/user/${userId}/meeting/${meetingId}`),
    listFavorites:  (userId)            => get('/api/favorites/user/' + userId),

    // Session helpers
    currentUserId,
    setCurrentUser
  };
})();
