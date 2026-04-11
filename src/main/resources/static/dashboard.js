// dashboard.js

let todosLosFichajes = [];

async function cargarDashboard() {
  try {
    todosLosFichajes = await apiFetch(`${API}/fichajes`);
    renderFichajes(todosLosFichajes);
  } catch (e) {
    showToast('Error al cargar los datos', 'error');
  }
}

function renderFichajes(data) {
  const tbody = document.querySelector('#fichajesTable tbody');
  if (!data.length) {
    tbody.innerHTML = `<tr><td colspan="5" style="text-align:center;padding:40px;color:var(--text-label)">No hay fichajes registrados</td></tr>`;
    return;
  }
  tbody.innerHTML = data.slice(0, 10).map(f => {
    const nombre     = f.nombreUsuario || '—';
    const color      = avatarColor(nombre);
    const ini        = initials(nombre);
    const tipoBadge  = f.tipo === 'entrada'
      ? '<span class="badge badge-green">Entrada</span>'
      : '<span class="badge badge-red">Salida</span>';
    const gpsBadge   = f.dentroDeRadio
      ? '<span class="badge badge-green">✓ Dentro</span>'
      : '<span class="badge badge-red">✗ Fuera</span>';
    const hora       = f.timestampFicha ? formatTime(f.timestampFicha) : '—';

    return `<tr>
      <td><div class="emp-cell">
        <div class="emp-avatar" style="background:${color}">${ini}</div>
        ${nombre}
      </div></td>
      <td>${tipoBadge}</td>
      <td><code style="font-family:var(--mono);font-size:12px">${hora}</code></td>
      <td>${f.nombreSede || '—'}</td>
      <td>${gpsBadge}</td>
    </tr>`;
  }).join('');
}

document.getElementById('searchInput').addEventListener('input', function () {
  const q = this.value.toLowerCase();
  const filtered = todosLosFichajes.filter(f =>
    (f.nombreUsuario || '').toLowerCase().includes(q) ||
    (f.nombreSede    || '').toLowerCase().includes(q)
  );
  renderFichajes(filtered);
});

cargarDashboard();
