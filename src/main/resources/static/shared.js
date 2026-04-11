
const API = '/api';

(function () {
  const token = sessionStorage.getItem('token');
  const esLogin = window.location.pathname.endsWith('login.html')
               || window.location.pathname === '/'
               || window.location.pathname.endsWith('index.html');
  if (!token && !esLogin) {
    window.location.href = 'login.html';
  }
})();

(function () {
  const page = document.body.dataset.page;
  if (!page) return;
  document.querySelectorAll('.nav-item[data-page]').forEach(el => {
    if (el.dataset.page === page) el.classList.add('active');
  });

  const nombre = sessionStorage.getItem('nombre');
  const rol    = sessionStorage.getItem('rol');
  const nameEl = document.querySelector('.user-name');
  const roleEl = document.querySelector('.user-role');
  if (nameEl && nombre) nameEl.textContent = nombre;
  if (roleEl && rol)    roleEl.textContent  = rol;
})();

function goTo(path) { window.location.href = path; }

function doLogout() {
  sessionStorage.clear();
  window.location.href = 'login.html';
}

function openModal(id)  { document.getElementById(id)?.classList.add('open'); }
function closeModal(id) { document.getElementById(id)?.classList.remove('open'); }

document.addEventListener('click', e => {
  if (e.target.classList.contains('modal-overlay'))
    e.target.classList.remove('open');
});
document.addEventListener('keydown', e => {
  if (e.key === 'Escape')
    document.querySelectorAll('.modal-overlay.open')
      .forEach(m => m.classList.remove('open'));
});

function activateTab(clickedTab, groupSelector) {
  const bar = clickedTab.closest(groupSelector || '.tabs-bar');
  if (!bar) return;
  bar.querySelectorAll('.tab').forEach(t => t.classList.remove('active'));
  clickedTab.classList.add('active');
}

function showToast(msg, type = 'success') {
  const colors = { success: '#0F9A5A', error: '#E84855', info: '#1A6FD4' };
  const t = document.createElement('div');
  t.style.cssText = `
    position:fixed; bottom:24px; right:24px; z-index:9999;
    background:${colors[type] || colors.info}; color:white;
    padding:12px 20px; border-radius:8px;
    font-family:'DM Sans',sans-serif; font-size:13px; font-weight:600;
    box-shadow:0 4px 20px rgba(0,0,0,.2); transition:opacity .3s;
  `;
  t.textContent = msg;
  document.body.appendChild(t);
  setTimeout(() => { t.style.opacity = '0'; }, 2500);
  setTimeout(() => t.remove(), 2900);
}

function confirmDelete(message, onConfirm) {
  if (window.confirm(message || '¿Eliminar este elemento?'))
    onConfirm?.();
}

function toggleDay(btn) {
  const on = btn.classList.contains('day-on');
  btn.classList.toggle('day-on',  !on);
  btn.classList.toggle('day-off',  on);
}
function getSelectedDays() {
  return [...document.querySelectorAll('.day-btn.day-on')]
    .map(b => b.dataset.day).join(',');
}

function formatDate(iso) {
  if (!iso) return '—';
  return new Date(iso).toLocaleDateString('es-ES', {
    day: '2-digit', month: '2-digit', year: 'numeric'
  });
}
function formatTime(iso) {
  if (!iso) return '—';
  return new Date(iso).toLocaleTimeString('es-ES', {
    hour: '2-digit', minute: '2-digit'
  });
}
function formatDateTime(iso) {
  if (!iso) return '—';
  return `${formatDate(iso)} ${formatTime(iso)}`;
}

function initials(name) {
  if (!name) return '??';
  return name.split(' ').slice(0, 2).map(w => w[0]).join('').toUpperCase();
}

const AVATAR_COLORS = [
  '#3D5AFE', '#E84855', '#0F9A5A', '#F59E0B',
  '#8B5CF6', '#06B6D4', '#EC4899', '#64748B'
];
function avatarColor(name) {
  if (!name) return AVATAR_COLORS[0];
  let h = 0;
  for (let i = 0; i < name.length; i++)
    h = (h * 31 + name.charCodeAt(i)) % AVATAR_COLORS.length;
  return AVATAR_COLORS[h];
}

async function apiFetch(url, options = {}) {
  const token = sessionStorage.getItem('token');

  const res = await fetch(url, {
    headers: {
      'Content-Type': 'application/json',
      ...(token ? { 'Authorization': `Bearer ${token}` } : {}),
      ...options.headers,
    },
    ...options,
  });

  if (res.status === 401 || res.status === 403) {
    sessionStorage.clear();
    window.location.href = 'login.html';
    return;
  }

  if (!res.ok) {
    const err = await res.json().catch(() => ({}));
    throw new Error(err.mensaje || `Error ${res.status}`);
  }

  if (res.status === 204) return null;
  return res.json();
}
